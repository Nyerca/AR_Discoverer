package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AnimalProfile extends AppCompatActivity {
    TableRow row1, row2, row3;


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
            System.out.println("NOT NULLLLLLLLLLLLL");
            String[] images = getIntent().getStringExtra("images").split("---");
            for (int i = 0; i < images.length; i++) {
                System.out.println("IMMAGINI_CIAO: " + images[i]);
            }

            for (int i = 0; i < images.length; i++) {
                System.out.println("IMMGG: " + images[i]);

                ImageView image;

                IconFactory mIconFactory = IconFactory.getInstance(AnimalProfile.this);
                Resources res = getResources();
                String packagename = getPackageName();
                int id = res.getIdentifier(images[i], "drawable", packagename);

                image = new ImageView(this);

                image.setImageDrawable(res.getDrawable(id));
                image.setLayoutParams(new TableRow.LayoutParams(150, 150));

                row1.addView(image);

            }
        } else {
            TableLayout tl = findViewById(R.id.tl);
            tl.setVisibility(View.GONE);
        }



        /*
        for(int i=0; i<9 && i< animals.size(); i++) {
            if(i == 0 && animals.get(i).getDistance() < 20) {
                neareastSensor = animals.get(i);
                scan.setEnabled(true);
                //Toast.makeText(this, "ENABLED", Toast.LENGTH_LONG).show();
            }
            if (i<3 && DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance() <= 3) {
                root.addView(DistanceAnimalView.createView(this, 0, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen(), animals.get(i).getImagepath()));
            }
            //slidedview.addView(DistanceAnimalView.createView(this, i * 100, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance()));
            getRow(i).addView(DistanceAnimalView.createView(this, 20, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen(), animals.get(i).getImagepath()));
        }

         */

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