package com.example.appar;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        CardView worldcard = (CardView) findViewById(R.id.worldcard);
        Intent world_intent = new Intent(this,WorldMap.class);
        worldcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(world_intent);
            }
        });

        CardView mapcard = (CardView) findViewById(R.id.mapcard);
        Intent map_intent = new Intent(this,GameMap.class);
        mapcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(map_intent);
            }
        });

        CardView collectioncard = (CardView) findViewById(R.id.collectioncard);
        Intent collection_intent = new Intent(this,Collection.class);
        collectioncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(collection_intent);
            }
        });

        CardView profilecard = (CardView) findViewById(R.id.profilecard);
        Intent profile_intent = new Intent(this,UserProfile.class);
        profilecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_intent.putExtra("username", "admin");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                myRef.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        int count_parks = 0;
                        int count_animals = 0;
                        for (com.google.firebase.database.DataSnapshot el: dataSnapshot.child("user_sensors/admin").getChildren()) {
                            count_parks += 1;
                            for (com.google.firebase.database.DataSnapshot elem: el.getChildren()) {
                                count_animals += elem.child("count").getValue(Integer.class);
                            }

                        }
                        profile_intent.putExtra("count_parks", "" + count_parks);
                        profile_intent.putExtra("count_animals", "" + count_animals);
                        startActivity(profile_intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });

            }
        });


    }

}