package com.example.appar;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserProfile  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        findViewById(R.id.backButton).setOnClickListener(v -> startActivity(new Intent(this,Homepage.class)));

        TextView username = findViewById(R.id.username);
        username.setText(GlobalVariable.getInstance().getUsername());

        TextView count_parks = findViewById(R.id.count_parks);
        count_parks.setText(getIntent().getStringExtra("count_parks"));

        TextView count_animals = findViewById(R.id.count_animals);
        count_animals.setText(getIntent().getStringExtra("count_animals"));

        TextView credibility = findViewById(R.id.credibility);

        double d = Double.parseDouble(getIntent().getStringExtra("credibility")) * 100;
        int value = (int)d;
        credibility.setText(value + "%");

        LinearLayout medals = findViewById(R.id.medals);

        GlobalVariable.getDatabase_reference().child("achievements/"+ GlobalVariable.getInstance().getUsername()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.getChildren().forEach(el -> {
                    String title = el.getKey();
                    String date = el.child("date").getValue(String.class);
                    String path = el.child("path").getValue(String.class);
                    medals.addView(createCard(title, date, path));
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
    }


    private CardView createCard(String title, String date, String resource_image) {
        CardView cardview = new CardView(UserProfile.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DistanceAnimalView.pxFromDp(UserProfile.this, 100));
        int margin = DistanceAnimalView.pxFromDp(UserProfile.this, 10);
        params.setMargins(margin, margin,margin,margin);
        cardview.setLayoutParams(params);
        cardview.setClickable(true);

        TypedValue outValue = new TypedValue();
        UserProfile.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        cardview.setForeground(getDrawable(outValue.resourceId));

        LinearLayout inner_linearlayout = new LinearLayout(UserProfile.this);
        inner_linearlayout.setGravity(Gravity.CENTER);
        inner_linearlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout inner2_linearlayout = new LinearLayout(UserProfile.this);
        LinearLayout.LayoutParams inner2_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int inner2_margin = DistanceAnimalView.pxFromDp(UserProfile.this, 30);
        inner2_params.setMargins(inner2_margin,0,0,0);
        inner2_linearlayout.setLayoutParams(inner2_params);

        ImageView image = new ImageView(UserProfile.this);
        int length = DistanceAnimalView.pxFromDp(UserProfile.this, 64);
        LinearLayout.LayoutParams image_params = new LinearLayout.LayoutParams(length,length);
        Resources res = getResources();
        //Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bronze);

        int id = this.getResources().getIdentifier(resource_image, "drawable", this.getPackageName());

        Bitmap bitmap = BitmapFactory.decodeResource(res, id);

        image.setImageBitmap(bitmap);
        image.setBackground(ContextCompat.getDrawable(UserProfile.this, R.drawable.circle_bg));
        image.setPadding(margin,margin,margin,margin);
        image.setLayoutParams(image_params);

        View separator = new View(UserProfile.this);
        LinearLayout.LayoutParams separator_params = new LinearLayout.LayoutParams(1,LinearLayout.LayoutParams.MATCH_PARENT);
        separator_params.setMargins(margin, margin,margin,margin);
        separator.setLayoutParams(separator_params);
        separator.setBackground(ContextCompat.getDrawable(UserProfile.this, R.color.lightgray));


        TextView textview = new TextView(UserProfile.this);
        LinearLayout.LayoutParams textview_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textview_params.setMargins(0, margin,0,0);
        textview.setText(title);
        textview.setTypeface(Typeface.DEFAULT_BOLD);
        textview.setLayoutParams(textview_params);

        LinearLayout inner3_linearlayout = new LinearLayout(UserProfile.this);
        LinearLayout.LayoutParams inner3_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        inner3_linearlayout.setGravity(Gravity.RIGHT);
        inner3_linearlayout.setLayoutParams(inner3_params);

        LinearLayout inner4_linearlayout = new LinearLayout(UserProfile.this);
        LinearLayout.LayoutParams inner4_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        inner4_linearlayout.setGravity(Gravity.BOTTOM);
        inner4_linearlayout.setLayoutParams(inner4_params);

        TextView time = new TextView(UserProfile.this);
        time.setGravity(Gravity.CENTER);
        int time_margin = DistanceAnimalView.pxFromDp(UserProfile.this, 5);
        LinearLayout.LayoutParams time_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        time_params.setMargins(0, 0,time_margin,0);
        time.setText(date);
        time.setLayoutParams(time_params);
        time.setPadding(0,time_margin,0,0);

        inner4_linearlayout.addView(time);


        inner3_linearlayout.addView(inner4_linearlayout);

        inner2_linearlayout.addView(image);
        inner2_linearlayout.addView(separator);
        inner2_linearlayout.addView(textview);
        inner2_linearlayout.addView(inner3_linearlayout);

        inner_linearlayout.addView(inner2_linearlayout);

        cardview.addView(inner_linearlayout);
        return cardview;
    }

}