package com.example.appar;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collection extends AppCompatActivity {

    public void prepareIntent(String animal, Intent intent) {
        List<String> funfacts = new ArrayList<>();
        Set<String> gallery_images= new HashSet<>();
        GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.child("funfacts/" + animal).getChildren().forEach(el -> {
                    if(el.child("users/" + GlobalVariable.getInstance().getUsername()).exists())
                    funfacts.add(el.child("title").getValue(String.class));
                });
/*
                dataSnapshot.child("park_sensors/").getChildren().forEach(el -> el.getChildren().forEach(el2 -> {
                    if(el2.child("animal").getValue(String.class).equals(animal) && el2.child("users/" + GlobalVariable.getInstance().getUsername()).exists()) {
                        System.out.println("prova_animal: " + el2.child("animal").getValue(String.class));
                        System.out.println("prova_animal2: " + animal);
                        gallery_images.add(el2.child("image").getValue(String.class));
                    }
                }));*/

                dataSnapshot.child("user_captured_animals/"+GlobalVariable.getInstance().getUsername()).getChildren().forEach(el ->  {
                    if(el.getValue(String.class).equals(animal)) {
                        //System.out.println("prova_animal: " + el2.child("animal").getValue(String.class));
                        //System.out.println("prova_animal2: " + animal);
                        gallery_images.add(el.getKey());
                    }
                });

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
                if(images.length()>0) intent.putExtra("images", images);
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

        Intent animal_profile = new Intent(this,AnimalProfile.class);

        findViewById(R.id.batcard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Bat");
            animal_profile.putExtra("image", "ic_bat");
            animal_profile.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            prepareIntent("bat", animal_profile);
        });

        findViewById(R.id.beecard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Bee");
            animal_profile.putExtra("image", "ic_bee");
            animal_profile.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            prepareIntent("bee", animal_profile);
        });

        findViewById(R.id.butterflycard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Butterfly");
            animal_profile.putExtra("image", "ic_butterfly");
            animal_profile.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            prepareIntent("butterfly", animal_profile);
        });

        findViewById(R.id.mosquitocard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Mosquito");
            animal_profile.putExtra("image", "ic_mosquito");
            animal_profile.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            prepareIntent("mosquito", animal_profile);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> startActivity(new Intent(Collection.this,Homepage.class)));




    }

}