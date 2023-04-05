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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.FirebaseException;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button login;
    TextView registerTextView;
    TextView forgotButton;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registerTextView = findViewById(R.id.loginNow);
        forgotButton = findViewById(R.id.forgotPassword);
        toggleButton = findViewById(R.id.toggleButton);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Enter your email");
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Enter your password");
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();

                                                if (document.exists()) {
                                                    // user is a regular user
                                                    // check if profile exists
                                                    boolean isProfileCompleted = document.getBoolean("profileCompleted");

                                                    Intent intent;
                                                    if (isProfileCompleted) {
                                                        intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    } else {
                                                        intent = new Intent(getApplicationContext(), AdditionalDetailsActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    finish();

                                                } else {
                                                    // User is an employee
                                                    db.collection("employees")
                                                            .document(user.getUid())
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            // check if profile exists
                                                                            boolean isProfileCompleted = document.getBoolean("profileCompleted");

                                                                            Intent intent;
                                                                            if (isProfileCompleted) {
                                                                                intent = new Intent(getApplicationContext(), AdminPanelActivity.class);
                                                                            } else {
                                                                                intent = new Intent(getApplicationContext(), AdditionalDetailsActivity.class);
                                                                            }
                                                                            startActivity(intent);
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(LoginActivity.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } else {
                                                                        Log.d("EmployeeDocError", "Error getting employee document", task.getException());
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.e("FireStoreDocumentError", "Error getting user document from Firestore", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "Invalid user account", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                Log.d("EmployeeDocError", "Error getting employee document", task.getException());
                            }
                        }
                    }
                });

            }
        });
    }
}