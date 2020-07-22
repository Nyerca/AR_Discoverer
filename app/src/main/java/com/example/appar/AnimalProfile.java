package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.io.IOException;

public class AnimalProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal_profile);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Intent back = new Intent(this,Collection.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
            }
        });

        TextView title = (TextView) findViewById(R.id.textView);

        Toast.makeText(this, "Scanned: " + getIntent().getStringExtra("animal"), Toast.LENGTH_LONG).show();
        title.setText(getIntent().getStringExtra("animal"));

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier(getIntent().getStringExtra("image"), "drawable", context.getPackageName());
        imageView.setImageDrawable(ContextCompat.getDrawable(this, id));

        LinearLayout linear_layout = (LinearLayout) findViewById(R.id.facts);
        String[] parts = getIntent().getStringExtra("funfacts").split("---");
        for (int i = 0; i < parts.length; i++) {
            linear_layout.addView(createText(parts[i]));
        }

        MediaPlayer mediaPlayer;
        Uri uri = Uri.parse(getIntent().getStringExtra("sound"));
        mediaPlayer = MediaPlayer.create(this, uri);

        ImageButton playbtn = (ImageButton) findViewById(R.id.imageButton2);
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(getIntent().getStringExtra("sound"));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });

        ImageButton stopbtn = (ImageButton) findViewById(R.id.imageButton);
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

            }
        });

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

        //tv.setTextColor(ContextCompat.getColor(AnimalProfile.this, R.color.black_overlay));
        tv.setTextColor(Color.BLACK);

        return tv;

    }

}