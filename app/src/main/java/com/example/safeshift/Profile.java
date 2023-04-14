package com.example.safeshift;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {
    ImageView signOutButton, updateProfileBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    FirebaseStorage storage;

    StorageReference storageRef, imageRef;

    TextView userId;

    EditText userNameEditText, emailEditText, mobileNumberEditText, firstNameEditText, lastNameEditText;
    ImageButton editUserNameBtn, editEmail, editMobileNumber, editFname, editLname;
    String firstName, lastName, email, role, contactNumber;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        signOutButton = view.findViewById(R.id.signOutButton);
        updateProfileBtn = view.findViewById(R.id.updateProfileBtn);

        userNameEditText = view.findViewById(R.id.userName);
        editUserNameBtn = view.findViewById(R.id.editUserName);

        emailEditText = view.findViewById(R.id.email);
        editEmail = view.findViewById(R.id.editEmail);

        mobileNumberEditText = view.findViewById(R.id.mobileNumber);
        editMobileNumber = view.findViewById(R.id.editMobileNumber);

        firstNameEditText = view.findViewById(R.id.fname);
        editFname = view.findViewById(R.id.editFname);

        lastNameEditText = view.findViewById(R.id.lname);
        editLname = view.findViewById(R.id.editLname);

        userId = view.findViewById(R.id.uid);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        ShapeableImageView profileImageView;

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("uploads/users/" + user.getUid() + "/profile_picture/profile_image.jpg");

        profileImageView = view.findViewById(R.id.profileImageView);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load the profile picture into the ImageView using Glide
                Glide.with(getContext())
                        .load(uri)
                        .circleCrop()
                        .into(profileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                profileImageView.setImageResource(R.drawable.profile_avatar);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                new AlertDialog.Builder(getContext())
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked Yes, sign out
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        editUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameEditText.setFocusable(true);
                userNameEditText.setFocusableInTouchMode(true);
                userNameEditText.requestFocus();
            }
        });

        editMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNumberEditText.setFocusable(true);
                mobileNumberEditText.setFocusableInTouchMode(true);
                mobileNumberEditText.requestFocus();
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditText.setFocusable(true);
                emailEditText.setFocusableInTouchMode(true);
                emailEditText.requestFocus();
            }
        });

        editFname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstNameEditText.setFocusable(true);
                firstNameEditText.setFocusableInTouchMode(true);
                firstNameEditText.requestFocus();
            }
        });

        editLname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastNameEditText.setFocusable(true);
                lastNameEditText.setFocusableInTouchMode(true);
                lastNameEditText.requestFocus();
            }
        });

        // fetch current users data from firestore
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            // Document exists, get the data and update the UI
                            firstName = documentSnapshot.getString("firstName");
                            lastName = documentSnapshot.getString("lastName");
                            email = documentSnapshot.getString("email");
                            role = documentSnapshot.getString("role");
                            contactNumber = documentSnapshot.getString("contactNumber");

                            userNameEditText.setText(firstName + " " + lastName);
                            emailEditText.setText(email);
                            userId.setText(role + ": " + user.getUid());
                            mobileNumberEditText.setText(contactNumber);
                            firstNameEditText.setText(firstName);
                            lastNameEditText.setText(lastName);
                        } else {

                            // now we will search in employees collection
                            db.collection("employees")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {

                                                // Document exists, get the data and update the UI
                                                firstName = documentSnapshot.getString("firstName");
                                                lastName = documentSnapshot.getString("lastName");
                                                email = documentSnapshot.getString("email");
                                                role = documentSnapshot.getString("role");
                                                contactNumber = documentSnapshot.getString("contactNumber");

                                                userNameEditText.setText(firstName + " " + lastName);
                                                emailEditText.setText(email);
                                                userId.setText(role + ": " + user.getUid());
                                                mobileNumberEditText.setText(contactNumber);
                                                firstNameEditText.setText(firstName);
                                                lastNameEditText.setText(lastName);
                                            } else {
                                                // Document does not exist
                                                Log.e("no document", "document does not exist");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error", "random error" + e);
                    }
                });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> updates = new HashMap<>();
                updates.put("email", emailEditText.getText().toString());
                updates.put("contactNumber", mobileNumberEditText.getText().toString());
                updates.put("role", "user");
                updates.put("firstName", firstNameEditText.getText().toString());
                updates.put("lastName", lastNameEditText.getText().toString());

                new AlertDialog.Builder(getContext())
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to update profile")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("users")
                                        .document(user.getUid())
                                        .update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("updatedSuccess", "User document updated successfully");
                                                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("updateFailed", "Error updating user document", e);
                                                Toast.makeText(getContext(), "Couldn't update profile" + e, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}