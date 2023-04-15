package com.example.safeshift;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


public class Home extends Fragment
{
    CardView b1,b2;

    TextView userName, userRole;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    ImageView profileImageView;

    String firstName, lastName, role;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        b1=(CardView) view.findViewById(R.id.eb1);
        b2=(CardView) view.findViewById(R.id.eb2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("uploads/users/" + user.getUid() + "/profile_picture/profile_image.jpg");

        profileImageView = view.findViewById(R.id.profilePicture);
        userRole = view.findViewById(R.id.userRole);
        userName = view.findViewById(R.id.userName);

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
                profileImageView.setImageResource(R.drawable.avatar);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(view.getContext(), Booking.class);
                i.putExtra("Value1", "Android By Javatpoint");
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(view.getContext(), UserOrderHistory.class);
                i.putExtra("Value2", "Android By Javatpoint");
                startActivity(i);
            }
        });

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
                            role = documentSnapshot.getString("role");

                            userName.setText(firstName + " " + lastName);
                            userRole.setText(role);
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
                                                role = documentSnapshot.getString("role");

                                                userName.setText(firstName + " " + lastName);
                                                userRole.setText(role);
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

        return view;
    }

}