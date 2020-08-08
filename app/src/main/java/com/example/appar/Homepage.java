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
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import java.util.List;

public class Homepage extends AppCompatActivity implements PermissionsListener {

    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

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
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (!granted) {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}