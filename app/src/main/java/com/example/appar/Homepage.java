package com.example.appar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.ar.core.ArCoreApk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Homepage extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(Homepage.this, PERMISSIONS, 100);

        findViewById(R.id.logout).setOnClickListener(v -> startActivity(new Intent(this,Register.class)));

        findViewById(R.id.worldcard).setOnClickListener(v -> startActivity(new Intent(this,WorldMap.class)));

        findViewById(R.id.mapcard).setOnClickListener(v -> startActivity(new Intent(this,GameMap.class)));

        findViewById(R.id.collectioncard).setOnClickListener(v -> startActivity(new Intent(this,Collection.class)));

        Intent profile_intent = new Intent(this,UserProfile.class);
        findViewById(R.id.profilecard).setOnClickListener(v -> GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int count_parks = 0;
                int count_animals = 0;
                for (DataSnapshot el: dataSnapshot.child("user_sensors/" + GlobalVariable.getInstance().getUsername()).getChildren()) {
                    count_parks += 1;
                    for (DataSnapshot el2: el.getChildren()) {
                        count_animals += el2.getValue(Integer.class);
                    }

                }
                int correct = dataSnapshot.child("users/" + GlobalVariable.getInstance().getUsername()).child("correct").getValue(Integer.class);
                int total = dataSnapshot.child("users/"  + GlobalVariable.getInstance().getUsername()).child("total").getValue(Integer.class);
                double credibility = 1.0;
                if(total > 0) credibility = (double) correct / (double) total;
                profile_intent.putExtra("count_parks", "" + count_parks);
                profile_intent.putExtra("count_animals", "" + count_animals);
                profile_intent.putExtra("credibility", "" + credibility);
                startActivity(profile_intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        }));

        findViewById(R.id.tutorialcard).setOnClickListener(v -> startActivity(new Intent(this,WalkthroughActivity.class)));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("GRANT_RESULTS length: " + grantResults.length + "PERMISSION_GRANTED: " + (grantResults[0] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[1] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[2] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[3] == PackageManager.PERMISSION_GRANTED)  + ", " + (grantResults[4] == PackageManager.PERMISSION_GRANTED));
                } else {
                    System.out.println("GRANT_RESULTS length: " + grantResults.length + "PERMISSION_GRANTED: " + (grantResults[0] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[1] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[2] == PackageManager.PERMISSION_GRANTED) + ", " + (grantResults[3] == PackageManager.PERMISSION_GRANTED)  + ", " + (grantResults[4] == PackageManager.PERMISSION_GRANTED));
                    Intent back = new Intent(this,Login.class);
                    startActivity(back);
                }
                return;
            }
        }
    }

}