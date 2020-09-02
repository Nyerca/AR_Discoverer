package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appar.qr_ar.MainActivity;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import java.io.IOException;

public class AnimalProfile extends AppCompatActivity {
    private TableRow row1, row2, row3;

    public TableRow getRow(int index) {
        if(index / 3 == 0) return row1;
        else if(index / 3 == 1) return row2;
        return row3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal_profile);

        row1 = findViewById(R.id.tableRow1);
        row2 = findViewById(R.id.tableRow2);
        row3 = findViewById(R.id.tableRow3);

        if( getIntent().getStringExtra("images") != null) {
            String[] images = getIntent().getStringExtra("images").split("---");

            for (int i = 0; i < images.length; i++) {
                ImageView image;

                IconFactory mIconFactory = IconFactory.getInstance(AnimalProfile.this);
                Resources res = getResources();
                String packagename = getPackageName();
                int id = res.getIdentifier(images[i], "drawable", packagename);

                image = new ImageView(this);

                image.setImageDrawable(res.getDrawable(id));
                image.setLayoutParams(new TableRow.LayoutParams(150, 150));

                getRow(i).addView(image);

            }
        } else {
            TableLayout tl = findViewById(R.id.tl);
            tl.setVisibility(View.GONE);
        }

        TextView title = findViewById(R.id.textView);
        title.setText(getIntent().getStringExtra("animal"));

        ImageView imageView = findViewById(R.id.imageView);
        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier(getIntent().getStringExtra("image"), "drawable", context.getPackageName());
        imageView.setImageDrawable(ContextCompat.getDrawable(this, id));

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.righttoleft);
        imageView.startAnimation(animation);

        LinearLayout linear_layout = findViewById(R.id.facts);
        String[] parts = getIntent().getStringExtra("funfacts").split("---");
        for (int i = 0; i < parts.length; i++) {
            linear_layout.addView(createText(parts[i]));
        }

        MediaPlayer mediaPlayer;
        Uri uri = Uri.parse(getIntent().getStringExtra("sound"));
        //String filename = "android.resource://" + this.getPackageName() + "/raw/bat";
        //Uri uri =Uri.parse(filename);
        mediaPlayer = MediaPlayer.create(this, uri);

        findViewById(R.id.imageButton2).setOnClickListener(v -> {
            mediaPlayer.reset();
            try {
                //mediaPlayer.setDataSource(uri.getPath());
                mediaPlayer.setDataSource(this, uri);
                //mediaPlayer.setDataSource(getIntent().getStringExtra("sound"));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
        });

        findViewById(R.id.imageButton).setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            startActivity(new Intent(AnimalProfile.this,Collection.class));
        } );

    }


    private TextView createText(String text) {
        TextView tv = new TextView(AnimalProfile.this);
        tv.setText(text);
        tv.setTextSize(18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 24,0,0);
        tv.setLayoutParams(params);
        tv.setTextColor(Color.BLACK);
        return tv;

    }

}