package com.example.appar;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.appar.database.Sensor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collection extends AppCompatActivity {

    public void prepareIntent(String animal, Intent intent) {
        List<String> funfacts = new ArrayList<String>();
        Set<String> gallery_images= new HashSet<String>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.child("funfacts/" + animal).getChildren().forEach(el -> {
                    if(el.child("users/" + GlobalVariable.getInstance().getUsername()).exists())
                    funfacts.add(el.child("title").getValue(String.class));
                });

                dataSnapshot.child("park_sensors/").getChildren().forEach(el -> {
                    el.getChildren().forEach(el2 -> {
                        if(el2.child("animal").getValue(String.class).equals(animal) && el2.child("users/" + GlobalVariable.getInstance().getUsername()).exists()) {
                            System.out.println("prova_animal: " + el2.child("animal").getValue(String.class));
                            System.out.println("prova_animal2: " + animal);
                            gallery_images.add(el2.child("image").getValue(String.class));
                        }

                    });
                });
                /*


                dataSnapshot.child("user_funfacts/"+ GlobalVariable.getInstance().getUsername() + "/" + animal).getChildren().forEach(el -> {
                    funfacts.add(el.getValue(String.class));
                });
                */
                String ff = "";
                for (String element : funfacts) {
                    ff += element + "---";
                }

                String images = "";
                for (String element : gallery_images) {
                    System.out.println("GALLERY_IMG: " + element);
                    images += element + "---";
                }

                intent.putExtra("funfacts", ff);
                intent.putExtra("images", images);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        GlobalVariable.setInstance("prova3");


        CardView batcard = (CardView) findViewById(R.id.batcard);
        Intent bat_intent = new Intent(this,AnimalProfile.class);
        batcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bat_intent.putExtra("animal", "Bat");
                bat_intent.putExtra("image", "ic_bat");
                bat_intent.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
                prepareIntent("bat", bat_intent);
            }
        });

        CardView beecard = (CardView) findViewById(R.id.beecard);
        Intent bee_intent = new Intent(this,AnimalProfile.class);
        beecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bee_intent.putExtra("animal", "Bee");
                bee_intent.putExtra("image", "ic_bee");
                bee_intent.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
                prepareIntent("bee", bee_intent);
            }
        });

        CardView butterflycard = (CardView) findViewById(R.id.butterflycard);
        Intent butterfly_intent = new Intent(this,AnimalProfile.class);
        butterflycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butterfly_intent.putExtra("animal", "Butterfly");
                butterfly_intent.putExtra("image", "ic_butterfly");
                butterfly_intent.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
                prepareIntent("butterfly", butterfly_intent);
            }
        });

        CardView mosquitocard = (CardView) findViewById(R.id.mosquitocard);
        Intent mosquito_intent = new Intent(this,AnimalProfile.class);
        mosquitocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mosquito_intent.putExtra("animal", "Mosquito");
                mosquito_intent.putExtra("image", "ic_mosquito");
                mosquito_intent.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
                prepareIntent("mosquito", mosquito_intent);
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Intent back = new Intent(this,Homepage.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
            }
        });




    }

}