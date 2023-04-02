package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdditionalDetailsActivity extends AppCompatActivity {

    Button confirmDetails, signOutButton;
    EditText firstName, lastName;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

        progressBar = findViewById(R.id.additionalDetailProgressBar);
        confirmDetails = findViewById(R.id.additionalDetailsButton);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        signOutButton = findViewById(R.id.signOutButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        confirmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String fName, lName;

                fName = firstName.getText().toString();
                lName = lastName.getText().toString();

                if (TextUtils.isEmpty(fName)) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lName)) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("profileCompleted", true);
                userMap.put("firstName", fName);
                userMap.put("lastName", lName);

                // Check if the user is a normal user or an employee
                db.collection("users")
                        .document(userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // User is a normal user

                                        db.collection("users")
                                                .document(userId)
                                                .update(userMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.d("Success", "Additional details added in Firestore.");
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.w("Failure", "Error adding data in firestore", e);
                                                    }
                                                });
                                    } else {
                                        // User is an employee
                                        db.collection("employees")
                                                .document(userId)
                                                .update(userMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.d("Success", "Additional details added in Firestore.");
                                                        Intent intent = new Intent(getApplicationContext(), AdminPanelActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.w("Failure", "Error adding data in firestore", e);
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("UserDocError", "Error getting user document", task.getException());
                                }
                            }
                        });
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}