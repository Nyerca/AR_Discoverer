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

                dataSnapshot.child("user_captured_animals/"+GlobalVariable.getInstance().getUsername()).getChildren().forEach(el ->  {
                    if(el.getValue(String.class).equals(animal)) {
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

        Intent animal_profile = new Intent(this,AnimalProfile.class);

        findViewById(R.id.batcard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Bat");
            animal_profile.putExtra("image", "ic_bat");
            //animal_profile.putExtra("sound", "bat.wav");
            //animal_profile.putExtra("sound", "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            animal_profile.putExtra("sound", "android.resource://" + this.getPackageName() + "/raw/bat");
            prepareIntent("bat", animal_profile);
        });

        findViewById(R.id.beecard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Bee");
            animal_profile.putExtra("image", "ic_bee");
            animal_profile.putExtra("sound", "android.resource://" + this.getPackageName() + "/raw/bee");
            prepareIntent("bee", animal_profile);
        });

        findViewById(R.id.butterflycard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Butterfly");
            animal_profile.putExtra("image", "ic_butterfly");
            animal_profile.putExtra("sound", "android.resource://" + this.getPackageName() + "/raw/butterfly");
            prepareIntent("butterfly", animal_profile);
        });

        findViewById(R.id.mosquitocard).setOnClickListener(v -> {
            animal_profile.putExtra("animal", "Mosquito");
            animal_profile.putExtra("image", "ic_mosquito");
            animal_profile.putExtra("sound", "android.resource://" + this.getPackageName() + "/raw/mosquito");
            prepareIntent("mosquito", animal_profile);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> startActivity(new Intent(Collection.this,Homepage.class)));




    }

}