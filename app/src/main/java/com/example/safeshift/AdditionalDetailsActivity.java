package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AdditionalDetailsActivity extends AppCompatActivity {

    Button confirmDetails, signOutButton;
    EditText firstName, lastName, contactNumber;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private ImageButton profileImage;
    private Uri imageUri;
    private Uri croppedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

        progressBar = findViewById(R.id.additionalDetailProgressBar);
        confirmDetails = findViewById(R.id.additionalDetailsButton);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        contactNumber = findViewById(R.id.mobile);
//        signOutButton = findViewById(R.id.signOutButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the CircleImageView
        profileImage = findViewById(R.id.profile_picture);

        // Get a reference to the Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // Set an onClickListener to select an image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        confirmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String fName, lName, contact;

                fName = firstName.getText().toString();
                lName = lastName.getText().toString();
                contact = contactNumber.getText().toString();

                if (TextUtils.isEmpty(fName)) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lName)) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Please enter your contact number", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("profileCompleted", true);
                userMap.put("firstName", fName);
                userMap.put("lastName", lName);
                userMap.put("contactNumber", contact);

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
                                                        // Upload the image to Firebase Storage
                                                        uploadFile();
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

    }


    // Open the file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    // Handle the result of selecting an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Load the selected image into the CircleImageView
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .centerCrop()
                    .into(profileImage);
        }
    }

    // Upload the selected file to Firebase Storage
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile_picture/"
                    + "profile_image" + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("uploadedImage", "Image uploaded successfully");
                            Toast.makeText(AdditionalDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("upload failed", "failed to upload image");
                            Toast.makeText(AdditionalDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Get the file extension of the selected image
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}