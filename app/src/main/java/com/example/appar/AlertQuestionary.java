package com.example.appar;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appar.qr_ar.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlertQuestionary extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    TextView textdia;

    private String path;
    private String animal;

    public AlertQuestionary(Activity a, String path, String animal) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.path = path;
        this.animal = animal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_questionary);

        textdia = findViewById(R.id.txt_dia);
        textdia.setText("Is this a/an " + animal);

        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);


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

        //Uri uri = Uri.parse("https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
        Uri uri = Uri.parse(path);
        mediaPlayer = MediaPlayer.create(c, uri);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}