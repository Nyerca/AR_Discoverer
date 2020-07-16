package com.example.appar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appar.database.Sensor;
import com.example.appar.qr_ar.ArActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button login = (Button) findViewById(R.id.login);
        Intent login_intent = new Intent(this,Login.class);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(login_intent);
            }
        });

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String rep_password = ((EditText) findViewById(R.id.rep_password)).getText().toString();
                if(username.length() > 4 && password.length() > 4 && password.equals(rep_password)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    myRef.addListenerForSingleValueEvent(new ValueEventListener(){

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot){

                            List<Sensor> list = new ArrayList<Sensor>();


                            dataSnapshot.child("users").getChildren().forEach(el -> {

                                if(!el.getKey().equals(username)) {
                                    Toast.makeText(Register.this, "id: " + el.getKey(),   Toast.LENGTH_LONG).show();
                                    DatabaseReference usersRef = myRef.child("users/"+username);
                                    User user = new User(password);
                                    usersRef.setValue(user);
                                    startActivity(login_intent);
                                }
                            });




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });

                }
            }
        });

    }

    public class User {

        public String password;

        public User(String password) {
            this.password = password;
        }

    }

}

