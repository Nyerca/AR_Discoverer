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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appar.database.AESCrypt;
import com.example.appar.database.Sensor;
import com.example.appar.database.User;
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

        TextView error = findViewById(R.id.error);

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String rep_password = ((EditText) findViewById(R.id.rep_password)).getText().toString();
                if(username.length() > 4 && password.length() > 4) {
                    if(password.equals(rep_password)) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        myRef.addListenerForSingleValueEvent(new ValueEventListener(){

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot){

                                List<Sensor> list = new ArrayList<Sensor>();

                                if(!dataSnapshot.child("users/" + username).exists()) {
                                    DatabaseReference usersRef = myRef.child("users/"+username);

                                    try {
                                        String crypted = AESCrypt.encrypt(password);
                                        User user = new User(crypted, 0, 0);
                                        usersRef.setValue(user);
                                        startActivity(login_intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    error.setText("The username is already taken.");
                                }

                                /*
                                dataSnapshot.child("users").getChildren().forEach(el -> {

                                    if(!el.getKey().equals(username)) {
                                        Toast.makeText(Register.this, "id: " + el.getKey(),   Toast.LENGTH_LONG).show();
                                        DatabaseReference usersRef = myRef.child("users/"+username);

                                        try {
                                            String crypted = AESCrypt.encrypt(password);
                                            User user = new User(crypted, 0, 0);
                                            usersRef.setValue(user);
                                            startActivity(login_intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        error.setText("The username is already taken.");
                                    }
                                });
*/



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}

                        });
                    } else {
                        error.setText("Password and repeated password dont match.");
                    }
                }
                else {
                    error.setText("Both username and password has to be atleast 5 characters long.");
                }
            }
        });

    }

}


