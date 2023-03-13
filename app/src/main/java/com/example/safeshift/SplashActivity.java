package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    db.collection("users")
                            .document(currentUser.getUid())
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
                                                    .document(currentUser.getUid())
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
                                                                    Toast.makeText(SplashActivity.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                Log.d("EmployeeDocError", "Error getting employee document", task.getException());
                                                            }
                                                        }
                                                    });                                        }
                                    } else {
                                        Log.e("FireStoreDocumentError", "Error getting user document from Firestore", task.getException());
                                    }
                                }
                            });
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);
    }
}