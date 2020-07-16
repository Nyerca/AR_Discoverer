package com.example.appar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appar.database.Sensor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button login = (Button) findViewById(R.id.login);
        Intent login_intent = new Intent(this,Homepage.class);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){

                        String username = ((EditText) findViewById(R.id.username)).getText().toString();
                        String password = ((EditText) findViewById(R.id.password)).getText().toString();

                        dataSnapshot.child("users").getChildren().forEach(el -> {

                            if(el.getKey().equals(username)) {
                                if(el.child("password").getValue(String.class).equals(password)) {
                                    GlobalVariable.setInstance(username);
                                    startActivity(login_intent);
                                }
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
            }
        });

    }

}