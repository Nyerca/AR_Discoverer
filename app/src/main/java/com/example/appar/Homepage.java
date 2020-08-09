package com.example.appar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import java.util.List;

public class Homepage extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
/*
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(this, PERMISSIONS, 311);

         */

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(Homepage.this, PERMISSIONS, 100);
/*
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "PERMISSION NOT GRANTED, FINISH ACTIVITY", Toast.LENGTH_LONG).show();
            finish();
        }
*/
        findViewById(R.id.logout).setOnClickListener(v -> startActivity(new Intent(this,Register.class)));

        findViewById(R.id.worldcard).setOnClickListener(v -> startActivity(new Intent(this,WorldMap.class)));

        findViewById(R.id.mapcard).setOnClickListener(v -> startActivity(new Intent(this,GameMap.class)));

        findViewById(R.id.collectioncard).setOnClickListener(v -> startActivity(new Intent(this,Collection.class)));

        Intent profile_intent = new Intent(this,UserProfile.class);
        findViewById(R.id.profilecard).setOnClickListener(v -> GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                //Toast.makeText(Homepage.this, "aaaaaaa", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(Homepage.this, "total: " + total, Toast.LENGTH_LONG).show();
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

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Intent back = new Intent(this,Login.class);
                    startActivity(back);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}