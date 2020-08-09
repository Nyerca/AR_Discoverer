package com.example.appar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appar.database.AESCrypt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Login  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView error = findViewById(R.id.error);

        Button login = findViewById(R.id.login);
        Intent login_intent = new Intent(this,Homepage.class);
        login.setOnClickListener(v -> GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if(dataSnapshot.child("users/" + username).exists()) {

                    try {
                        String decrypted = AESCrypt.decrypt(dataSnapshot.child("users/" + username).child("password").getValue(String.class));
                        if(decrypted.equals(password)) {
                            GlobalVariable.setInstance(username);
                            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        }));

    }

}