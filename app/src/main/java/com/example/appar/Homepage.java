package com.example.appar;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        CardView collectioncard = (CardView) findViewById(R.id.collectioncard);
        Intent collection_intent = new Intent(this,Collection.class);
        collectioncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(collection_intent);
            }
        });

    }

}