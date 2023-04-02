package com.example.safeshift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminPanelActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


        BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        bottomNavigationView = findViewById(R.id.bn2);

        //gv=(GridView)findViewById(R.id.gv);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.ehome);
    }
    employeehome eh=new employeehome();
    ContactUs c = new ContactUs();
    Profile p = new Profile();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ehome:
                getSupportFragmentManager().beginTransaction().replace(R.id.ifragemnt2, eh).commit();
                return true;

            case R.id.econtact:
                getSupportFragmentManager().beginTransaction().replace(R.id.ifragemnt2, c).commit();
                return true;

            case R.id.eprofile:
                getSupportFragmentManager().beginTransaction().replace(R.id.ifragemnt2, p).commit();
                return true;

        }
        return false;
    }


}
