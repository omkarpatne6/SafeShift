package com.example.safeshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class EmployeeOrderHistoryDetails extends AppCompatActivity {

    String orderName, pickupAddress, destinationAddress, pickupDate, status;
    TextView order_name_history, pickup_address_history_textview, destination_address_history_textview, pickup_date_history_text_view, status_history_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_order_history_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        orderName = extras.getString("order_name");
        pickupAddress = extras.getString("pickup_address");
        destinationAddress = extras.getString("destination_address");
        pickupDate = extras.getString("pickup_date");
        status = extras.getString("status");

        order_name_history = findViewById(R.id.order_name_history);
        pickup_address_history_textview = findViewById(R.id.pickup_address_history_textview);
        destination_address_history_textview = findViewById(R.id.destination_address_history_textview);
        pickup_date_history_text_view = findViewById(R.id.pickup_date_history_text_view);
        status_history_text_view = findViewById(R.id.status_history_text_view);

        order_name_history.setText(orderName);
        pickup_address_history_textview.setText(pickupAddress);
        destination_address_history_textview.setText(destinationAddress);
        status_history_text_view.setText(status);
        pickup_date_history_text_view.setText(pickupDate);
    }
}