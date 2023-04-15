package com.example.safeshift;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserOrderHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyUserAdapter myAdapter;
    ArrayList<PreviousUserOrders> ordersList;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    LinearLayout noPreviousOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        noPreviousOrder = findViewById(R.id.noPreviousOrders);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        recyclerView = findViewById(R.id.orderHistoryRecyclerView);
        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersList = new ArrayList<PreviousUserOrders>();
        myAdapter = new MyUserAdapter(this, ordersList);

        recyclerView.setAdapter(myAdapter);

        // set click listener on adapter
        myAdapter.setOnItemClickListener(new MyUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // start new activity to display details of selected item
                Toast.makeText(UserOrderHistory.this, "Item clicked at position " + position, Toast.LENGTH_SHORT).show();
                Log.d("clickedEvent", "Item clicked" + position);

                Intent intent = new Intent(UserOrderHistory.this, UserOrderHistoryDetails.class);
                PreviousUserOrders order = ordersList.get(position);
                intent.putExtra("order_name", order.getOrder_name());
                intent.putExtra("pickup_address", order.getPickup_address());
                intent.putExtra("destination_address", order.getDestination_address());
                intent.putExtra("pickup_date", order.getPickup_date());
                intent.putExtra("status", order.getStatus());
                intent.putExtra("employee_id", order.getEmployee_id());
                startActivity(intent);
            }
        });

        EventChangeListener();
    }
    private void EventChangeListener() {
        db.collection("orders")
//                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereEqualTo("user_id", currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.e("Firestore error",error.getMessage());
                            return;
                        }

                        if (value.isEmpty()) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            noPreviousOrder.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            // show a message to the user here indicating that no orders were found
                            return;
                        }

                        noPreviousOrder.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        ordersList.clear(); // clear the list to avoid duplicates

                        for (DocumentSnapshot doc: value.getDocuments()) {
                            PreviousUserOrders order = new PreviousUserOrders();

                            String assignedEmployee;

                            if (doc.getString("employee_id").equals("null")) {

                                assignedEmployee = "No employee assigned";

                            } else {
                                assignedEmployee = doc.getString("employee_id");
                            }
                            order.setEmployee_id(assignedEmployee);
                            order.setOrder_name(doc.getString("pickup_address") + " => " + doc.getString("destination_address"));
                            order.setPickup_address(doc.getString("pickup_address"));
                            order.setDestination_address(doc.getString("destination_address"));
                            order.setPickup_date(doc.getString("pickup_date"));
                            order.setStatus(doc.getString("status"));
                            ordersList.add(order);
                        }

                        myAdapter.notifyDataSetChanged(); // notify adapter after adding items

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}