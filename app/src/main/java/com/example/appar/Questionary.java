package com.example.appar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Questionary extends AppCompatActivity {

    Button btnScan;
    TextView tv_qr_readTxt;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionary);

        Button btn = findViewById(R.id.btnPlay);
        btn.setOnClickListener(view ->{
            if(mediaPlayer.isPlaying())  {
                mediaPlayer.pause();
                btn.setText(">");
            } else {
                mediaPlayer.start();
                btn.setText("||");
                changeSeekbar();
            }
        });

        handler = new Handler();
        seekbar = findViewById(R.id.seekbar);
       mediaPlayer = MediaPlayer.create(this, R.raw.cat_annoyed);
        //Uri uri = Uri.parse("https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
       // mediaPlayer = MediaPlayer.create(this, uri);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekbar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                //changeSeekbar();
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("sensor_sounds").orderByKey().equalTo("1").addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.getChildren().forEach(el2 -> {
                    Toast.makeText(Questionary.this, "id1: " + el2, Toast.LENGTH_LONG).show();
                    dataSnapshot.getRef().child("1").addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2){

                            dataSnapshot2.getChildren().forEach(el3 -> {
                                String value = el3.child("sound").getValue(String.class); //This is a1
                                //String labelled = el2.child("labelled").getValue(String.class);
                                Toast.makeText(Questionary.this, "id: " + value, Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




/*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("sensor_sounds").equalTo("1").orderByChild("labelled").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot2){

                dataSnapshot2.child("1").getChildren().forEach(el2 -> {
                    String value = el2.child("sound").getValue(String.class); //This is a1
                    String labelled = el2.child("labelled").getValue(String.class);
                    Toast.makeText(Questionary.this, "id: " + value + " labelled: " + labelled, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });

*/
    }


    private void changeSeekbar() {
        seekbar.setProgress(mediaPlayer.getCurrentPosition());
        if(mediaPlayer.isPlaying()) {
            runnable= new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }

}