package com.example.safeshift;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Booking extends AppCompatActivity  {

    Button datePickerButton, timePickerButton, placeOrderBtn;
    TextView dateTextView, timeTextView;

    EditText firstName, lastName, phoneNumber, pickupLocation, destinationLocation;

    String first_name, last_name, phone_number, pickup_location, destination_location, time_string, date_string;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // firestore initialization
        db = FirebaseFirestore.getInstance();
        mAuth = mAuth = FirebaseAuth.getInstance();

        datePickerButton = (Button) findViewById(R.id.date);
        timePickerButton = (Button) findViewById(R.id.time);

        dateTextView = (TextView) findViewById(R.id.dates);
        timeTextView = (TextView) findViewById(R.id.times);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_edittext);
        pickupLocation = findViewById(R.id.pickup_location);
        destinationLocation = findViewById(R.id.destination_location);
        placeOrderBtn = findViewById(R.id.place_order_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mCalendar = Calendar.getInstance();
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;

                mDatePicker=new DatePickerDialog(Booking.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        Calendar mCalendar = Calendar.getInstance();
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
                        dateTextView.setText(selectedDate);
                        date_string = selectedDate;
                    }
                },year, month, dayOfMonth);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(Booking.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        timeTextView.setText(selectedHour + ":" + selectedMinute);
                        time_string = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get values from editText
                first_name = firstName.getText().toString();
                last_name  = lastName.getText().toString();
                phone_number = phoneNumber.getText().toString();
                pickup_location = pickupLocation.getText().toString();
                destination_location = destinationLocation.getText().toString();
//                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();

                Log.d("pickdest", "hello" + pickup_location + destination_location);

                // Define the data to add to the "orders" collection as a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("user_id", mAuth.getCurrentUser().getUid());
                data.put("pickup_address", pickup_location);
                data.put("destination_address", destination_location);
                data.put("pickup_date", date_string);
                data.put("pickup_time", time_string);
                data.put("status", "pending");

                // Add the data to a new document in the "orders" collection
                db.collection("orders").add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Log.d("Success", "added order");

                                Toast.makeText(Booking.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("OrderError", "added order");

                                Toast.makeText(Booking.this, "Order failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }

}
