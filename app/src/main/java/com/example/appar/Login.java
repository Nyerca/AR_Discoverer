package com.example.appar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appar.database.AESCrypt;
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

        TextView error = findViewById(R.id.error);

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

                        if(dataSnapshot.child("users/" + username).exists()) {

                            try {
                                String decrypted = AESCrypt.decrypt(dataSnapshot.child("users/" + username).child("password").getValue(String.class));
                                if(decrypted.equals(password)) {
                                    GlobalVariable.setInstance(username);
                                    startActivity(login_intent);
                                } else {
                                    error.setText("The password is wrong.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            error.setText("The username doesn't exist.");
                        }

                        /*
                        dataSnapshot.child("users").getChildren().forEach(el -> {

                            if(el.getKey().equals(username)) {
                                try {
                                    String decrypted = AESCrypt.decrypt(el.child("password").getValue(String.class));
                                    if(decrypted.equals(password)) {
                                        GlobalVariable.setInstance(username);
                                        startActivity(login_intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                         */


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
            }
        });

    }

}