package com.example.safeshift;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Preview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Bundle extras = getIntent().getExtras();
    }
}