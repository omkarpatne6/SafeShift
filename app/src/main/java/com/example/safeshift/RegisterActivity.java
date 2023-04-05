package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    Button register;
    TextView loginTextView;
    ProgressDialog progressDialog;
    ToggleButton toggleButton, toggleButtonConfirmPassword;

    // firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        register = (Button) findViewById(R.id.registerBtn);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        loginTextView = (TextView) findViewById(R.id.registerNow);
        editTextConfirmPassword = findViewById(R.id.confirm_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        toggleButton = findViewById(R.id.toggleButton);
        toggleButtonConfirmPassword = findViewById(R.id.toggleButtonConfirmPassword);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleButton.setButtonDrawable(R.drawable.eye_show_small);
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleButton.setButtonDrawable(R.drawable.hidden);
                }
            }
        });
        toggleButtonConfirmPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleButtonConfirmPassword.setButtonDrawable(R.drawable.eye_show_small);
                } else {
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleButtonConfirmPassword.setButtonDrawable(R.drawable.hidden);
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, cpassword;

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                cpassword = editTextConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Enter your email");
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(cpassword)) {
                    Log.d("cpassword", "passwords don't match");
                    Toast.makeText(RegisterActivity.this, "Please match confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
//                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            String userId = user.getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("profileCompleted", false);
                            userMap.put("role", "user");

                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Success", "User record created in Firestore.");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Failure", "Error adding data in firestore", e);
                                        }
                                    });

                            Intent intent = new Intent(getApplicationContext(), AdditionalDetailsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}