package com.example.appar.database;

import android.os.Bundle;

import com.example.appar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Db_usage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("t1/t2");
        //myRef.setValue("Hello, World!");
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                /*
                String value = dataSnapshot.child("message").getValue(String.class); //This is a1
                Toast.makeText(Db_usage.this, "id: " + value,
                        Toast.LENGTH_LONG).show();

                */

                dataSnapshot.child("sensors").getChildren().forEach(el -> {
                    String value = el.child("position").getValue(String.class); //This is a1
                    //Toast.makeText(Db_usage.this, "id: " + el.getKey(),
                         //   Toast.LENGTH_LONG).show();


                    DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference();
                    myRef.child("sensor_sounds").orderByKey().equalTo(el.getKey()).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2){

                            dataSnapshot2.child(el.getKey()).getChildren().forEach(el2 -> {
                                String value = el2.child("sound").getValue(String.class); //This is a1
                                Toast.makeText(Db_usage.this, "id: " + value, Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
