package com.example.appar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UserProfile  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        TextView username = (TextView) findViewById(R.id.username);
        username.setText(getIntent().getStringExtra("username"));

        TextView count_parks = (TextView) findViewById(R.id.count_parks);
        count_parks.setText(getIntent().getStringExtra("count_parks"));

        TextView count_animals = (TextView) findViewById(R.id.count_animals);
        count_animals.setText(getIntent().getStringExtra("count_animals"));
    }

}