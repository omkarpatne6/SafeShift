package com.example.safeshift;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class EmployeeMyOrder extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String employeeId;
    private String orderId;

    private TextView orderIdTextView;
    private TextView pickupAddressTextView;
    private TextView destinationAddressTextView;
    private TextView pickupDateTextView;
    private TextView statusTextView;
    private Spinner statusSpinner;
    private Button updateButton;

    private CardView myOrderCard;
    private TextView noOrderTextView;

    SwipeRefreshLayout swipeRefreshLayout;

//    private String[] statusArray = {"Pending", "In Progress", "Completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_my_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        employeeId = mAuth.getCurrentUser().getUid();

        // Initialize TextViews
        orderIdTextView = findViewById(R.id.order_id_textview);
        pickupAddressTextView = findViewById(R.id.pickup_address_textview);
        destinationAddressTextView = findViewById(R.id.destination_address_textview);
        pickupDateTextView = findViewById(R.id.pickup_address_textview);
        statusTextView = findViewById(R.id.status_text_view);
        statusSpinner = findViewById(R.id.status_spinner);
        updateButton = findViewById(R.id.update_button);
        myOrderCard = findViewById(R.id.myOrderCard);
        noOrderTextView = findViewById(R.id.noOrderTextView);

        // fetch data from firestore
        fetchData();

        // Handle click on the Update button
        updateButton.setOnClickListener(view -> {
            String newStatus = statusSpinner.getSelectedItem().toString();
            updateOrderStatus(newStatus);
        });

        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // This function for fetching data from firestore
    public void fetchData() {
        // Fetch the order assigned to the logged-in employee
        db.collection("orders")
                .whereEqualTo("employee_id", employeeId)
                .whereIn("status", Arrays.asList("Pending", "In progress"))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("EmployeeOrderActivity", "Error fetching order: " + e.getMessage());
                        Toast.makeText(EmployeeMyOrder.this, "Error fetching order. Please try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (queryDocumentSnapshots.isEmpty()) {
                        // No order assigned to the logged-in employee
                        Toast.makeText(EmployeeMyOrder.this, "No order assigned yet.", Toast.LENGTH_SHORT).show();
                        myOrderCard.setVisibility(View.GONE);
                        noOrderTextView.setVisibility(View.VISIBLE);
                        return;
                    }

                    myOrderCard.setVisibility(View.VISIBLE);
                    noOrderTextView.setVisibility(View.GONE);

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    orderId = documentSnapshot.getId();
                    String pickupAddress = documentSnapshot.getString("pickup_address");
                    String destinationAddress = documentSnapshot.getString("destination_address");
                    String pickupDate = documentSnapshot.getString("pickup_date");
                    String status = documentSnapshot.getString("status");

                    // Update TextViews and Spinner with the order information
                    orderIdTextView.setText(orderId);
                    pickupAddressTextView.setText(pickupAddress);
                    destinationAddressTextView.setText(destinationAddress);
                    pickupDateTextView.setText(pickupDate);
                    statusTextView.setText(status);
                    setSpinnerSelection(status);
                });
    }

    private void setSpinnerSelection(String status) {

        String orderStatus[] = {"Pending", "In progress", "Completed"};

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, orderStatus);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        if (status.equals("Pending")) {
            statusSpinner.setSelection(0);
        } else if (status.equals("In progress")) {
            statusSpinner.setSelection(1);
        } else if (status.equals("Completed")) {
            statusSpinner.setSelection(2);
        } else {
            Log.e("EmployeeMyOrder", "Invalid status: " + status);
        }
    }

    private void updateOrderStatus(String newStatus) {
        // Get the order ID
        String orderId = orderIdTextView.getText().toString();

        if (newStatus.equals("Completed")) {
            new AlertDialog.Builder(this)
                    .setTitle("Order delivered confirmation")
                    .setMessage("Are you sure the ordered is delivered? Press Yes if package is successfully delivered")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateOrderStatusInFireStore(newStatus);
                        }
                    })
                    .setNegativeButton("No", null).show();
        } else {
            updateOrderStatusInFireStore(newStatus);
        }
    }

    public void updateOrderStatusInFireStore(String newStatus) {
        // Update the status of the order in Firestore
        db.collection("orders").document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Update the status TextView on the UI
                    statusTextView.setText(newStatus);
                    Toast.makeText(this, "Status updated successfully.", Toast.LENGTH_SHORT).show();

                    // Clear the TextViews if the status is "completed"
                    if (newStatus.equals("Completed")) {
                        orderIdTextView.setText("");
                        pickupAddressTextView.setText("");
                        destinationAddressTextView.setText("");
                        pickupDateTextView.setText("");
                        statusTextView.setText("");
                    }

                    // Update the employee's availability in Firestore
                    db.collection("employees").document(employeeId)
                            .update("available", true)
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d("EmployeeMyOrder", "Employee availability updated successfully.");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("EmployeeMyOrder", "Error updating employee availability: " + e.getMessage());
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("EmployeeMyOrder", "Error updating status: " + e.getMessage());
                    Toast.makeText(EmployeeMyOrder.this, "Error updating status. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }

}