package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appar.database.AESCrypt;
import com.example.appar.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        findViewById(R.id.login).setOnClickListener(v -> startActivity(new Intent(this,Login.class)));
        TextView error = findViewById(R.id.error);

        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.username)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            String rep_password = ((EditText) findViewById(R.id.rep_password)).getText().toString();
            if(username.length() > 4 && password.length() > 4) {
                if(password.equals(rep_password)) {
                    GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot){
                            if(!dataSnapshot.child("users/" + username).exists()) {
                                DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("users/"+username);

                                try {
                                    String crypted = AESCrypt.encrypt(password);
                                    User user = new User(crypted, 0, 0);
                                    usersRef.setValue(user);
                                    startActivity(new Intent(Register.this,Login.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                error.setText("Lo username è già in uso.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });
                } else {
                    error.setText("La password non fa match.");
                }
            }
            else {
                error.setText("Sia username che password devono contenere almeno 5 caratteri.");
            }
        });

    }

}


