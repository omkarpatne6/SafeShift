package com.example.safeshift;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class employeehome extends Fragment {

    Button eb1, eb2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_employeehome, container, false);


        eb1 = (Button) view.findViewById(R.id.eb1);
        eb2 = (Button) view.findViewById(R.id.eb2);


        eb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), EmployeeMyOrder.class);
                i.putExtra("Value11", "Android By Javatpoint");
                startActivity(i);
            }
        });

        eb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(view.getContext(), EmployeeOrderHistory.class);
                i2.putExtra("Value22", "Android By Javatpoint");
                startActivity(i2);
            }
        });
        return view;
    }
}