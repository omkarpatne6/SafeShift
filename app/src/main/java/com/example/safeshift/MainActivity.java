package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    Button signOutBtn, placeOrderBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signOutBtn = findViewById(R.id.signOut);
        placeOrderBtn = findViewById(R.id.placeOrder);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Define the data to add to the "orders" collection as a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("id", "order123");
                data.put("user_id", mAuth.getCurrentUser().getUid());
                data.put("pickup_address", "123 Main St");
                data.put("destination_address", "456 Oak Ave");
                data.put("pickup_date", "2023-04-01");
                data.put("status", "pending");

                // Add the data to a new document in the "orders" collection
                db.collection("orders").add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("newOrder", "Document added with ID: " + documentReference.getId());
                                Toast.makeText(MainActivity.this, "Document added with ID:" + documentReference.getId(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error placing order" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}