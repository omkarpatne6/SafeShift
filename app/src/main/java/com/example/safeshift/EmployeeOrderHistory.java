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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EmployeeOrderHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<PreviousEmployeeOrders> ordersList;

    ProgressDialog progressDialog;

    LinearLayout noPreviousOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_order_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noPreviousOrder = findViewById(R.id.noPreviousOrders);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        recyclerView = findViewById(R.id.orderHistoryRecyclerView);
        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersList = new ArrayList<PreviousEmployeeOrders>();
        myAdapter = new MyAdapter(this, ordersList);

        recyclerView.setAdapter(myAdapter);

        // set click listener on adapter
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // start new activity to display details of selected item
                Toast.makeText(EmployeeOrderHistory.this, "Item clicked at position " + position, Toast.LENGTH_SHORT).show();
                Log.d("clickedEvent", "Item clicked" + position);

                Intent intent = new Intent(EmployeeOrderHistory.this, EmployeeOrderHistoryDetails.class);
                PreviousEmployeeOrders order = ordersList.get(position);
                intent.putExtra("order_name", order.getOrder_name());
                intent.putExtra("pickup_address", order.getPickup_address());
                intent.putExtra("destination_address", order.getDestination_address());
                intent.putExtra("pickup_date", order.getPickup_date());
                intent.putExtra("status", order.getStatus());
                startActivity(intent);
            }
        });

        EventChangeListener();
    }

    private void EventChangeListener() {
        db.collection("orders")
                .whereEqualTo("status", "Completed")
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
                            // show a message to the user here indicating that no orders were found
                            return;
                        }

                        noPreviousOrder.setVisibility(View.GONE);

                        ordersList.clear(); // clear the list to avoid duplicates

                        for (DocumentSnapshot doc: value.getDocuments()) {
                            PreviousEmployeeOrders order = new PreviousEmployeeOrders();
                            order.setOrder_name(doc.getString("id"));
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