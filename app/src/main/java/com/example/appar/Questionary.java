package com.example.appar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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