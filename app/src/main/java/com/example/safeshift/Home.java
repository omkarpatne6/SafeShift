package com.example.safeshift;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Home extends Fragment
{


    TextView tv;

    CardView b1,b2,b3,b4;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //gv = view.findViewById(id.gv);
       // tv= view.findViewById(R.id.idTVCourse);

        b1=(CardView) view.findViewById(R.id.eb1);
        b2=(CardView) view.findViewById(R.id.eb2);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(view.getContext(), Booking.class);
                i.putExtra("Value1", "Android By Javatpoint");
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(view.getContext(), Preview.class);
                i.putExtra("Value2", "Android By Javatpoint");
                startActivity(i);
            }
        });

        return view;
    }

}