package com.example.appar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appar.qr_ar.MainActivity;
import com.example.appar.qr_ar.QuestionAnswer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;
import java.util.Random;

public class AlertQuestionary extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public FloatingActionButton yes, no, ok;
    SeekBar seekbar;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    TextView textdia;
    Boolean isSound;
    TextView question;
    Optional<Boolean> answer;
    Optional<Double> credibility;
    Button btn;

    int parkid;
    int sensorid;
    int elementid;

    private String body ="https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav";
    private String animal = "cat";

    public AlertQuestionary(Activity a, String body, String animal, Boolean isSound, Optional<Boolean> answer, Optional<Double> credibility, int parkid, int sensorid, int elementid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.body = body;
        this.animal = animal;
        this.isSound = isSound;
        this.answer = answer;
        this.credibility = credibility;

        this.parkid = parkid;
        this.sensorid = sensorid;
        this.elementid = elementid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_questionary);

        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);

        textdia = findViewById(R.id.funfact);
        textdia.setText("FUN_FACT");

        FrameLayout frame_ok = findViewById(R.id.frame_ok);
        frame_ok.setVisibility(View.GONE);
        ok = (FloatingActionButton) findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();

            }
        });

        yes = (FloatingActionButton) findViewById(R.id.btn_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(answer.isPresent()) {
                    Pair<String, String> answers = QuestionAnswer.valueAnswer(true, answer.get(), credibility, "admin");
                    textdia.setText(answers.first);
                    question.setText(answers.second);
                    //dismiss();
                } else {
                    textdia.setText("CONGRATULAZIONI");
                    question.setText("Sei il primo utente ad aver contribuito alla classificazione di questo verso!");
                }
                FrameLayout frame_yes = findViewById(R.id.frame_yes);
                frame_yes.setVisibility(View.GONE);
                FrameLayout frame_no = findViewById(R.id.frame_no);
                frame_no.setVisibility(View.GONE);
                seekbar.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);

                answer(isSound, animal, 1, parkid, sensorid, elementid);



                frame_ok.setVisibility(View.VISIBLE);

            }
        });
        no = (FloatingActionButton) findViewById(R.id.btn_no);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(answer.isPresent()) {
                    Pair<String, String> answers = QuestionAnswer.valueAnswer(false, answer.get(), credibility, "admin");
                    textdia.setText(answers.first);
                    question.setText(answers.second);
                } else {
                    textdia.setText("CONGRATULAZIONI");
                    question.setText("Sei il primo utente ad aver contribuito alla classificazione di questo verso!");
                }

                FrameLayout frame_yes = findViewById(R.id.frame_yes);
                frame_yes.setVisibility(View.GONE);
                FrameLayout frame_no = findViewById(R.id.frame_no);
                frame_no.setVisibility(View.GONE);
                seekbar.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);

                answer(isSound, animal, -1, parkid, sensorid, elementid);

                frame_ok.setVisibility(View.VISIBLE);
            }
        });

        /*
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
*/

        seekbar = findViewById(R.id.seekbar);
        question = findViewById(R.id.question);
        btn = findViewById(R.id.btnPlay);

        if(isSound) {
            question.setText("Is this a/an " + animal);
            btn.setOnClickListener(view -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn.setText(">");
                } else {
                    mediaPlayer.start();
                    btn.setText("||");
                    changeSeekbar();
                }
            });

            handler = new Handler();


            //Uri uri = Uri.parse("https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav");
            Uri uri = Uri.parse(body);
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
        } else {
            seekbar.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            question.setText(body);
        }





    }

    private void answer(Boolean isSound, String animal, int answer, int parkid, int sensorid, int elementid) {
        if(!isSound) {
            QuestionAnswer.addUserQuestionAnswer(GlobalVariable.getInstance().getUsername(), animal, elementid, answer);
        } else {
            QuestionAnswer.addUserSoundAnswer(GlobalVariable.getInstance().getUsername(), parkid, sensorid, elementid, answer);
        }

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


    public static void randomQuestionary(Activity activity, String username, String park, String animal, String sensorid) {
        Random r = new Random();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        if(true == false) {
            Toast.makeText(activity, "question", Toast.LENGTH_LONG).show();

            myRef.child("questions/" + animal).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    dataSnapshot.getChildren().forEach(el -> {
                        Boolean exist = el.child("users/"+username).exists(); //This is a1
                        if(!exist) {
                            String question = el.child("question").getValue(String.class);
                            int answer = el.child("answer").getValue(Integer.class);


                            AlertQuestionary cdd=new AlertQuestionary(activity, question, animal, false, Optional.of(QuestionAnswer.intToBoolean(answer)), Optional.empty(), Integer.parseInt(park), Integer.parseInt(sensorid), Integer.parseInt(el.getKey()));
                            cdd.show();
                        }
                        //Toast.makeText(context, "" + exist, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });



        } else {
            Toast.makeText(activity, "sound, park: " + park +  " sensid: " + sensorid, Toast.LENGTH_LONG).show();
            myRef.child("sensor_sounds/" +  park + "/" + sensorid).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    dataSnapshot.getChildren().forEach(el -> {
                        Boolean exist = el.child("users/"+username).exists(); //This is a1
                        Toast.makeText(activity, "" + exist, Toast.LENGTH_LONG).show();
                        if(!exist) {
                            String sound = el.child("sound").getValue(String.class);
                            Boolean expert_labelled = el.child("expert").exists();
                            if(expert_labelled) {
                                int answer = el.child("expert").getValue(Integer.class);
                                AlertQuestionary cdd=new AlertQuestionary(activity, sound, animal, true, Optional.of(QuestionAnswer.intToBoolean(answer)), Optional.empty(), Integer.parseInt(park), Integer.parseInt(sensorid), Integer.parseInt(el.getKey()));
                                cdd.show();
                            } else {
                                double total = 0.0;
                                double weights_sum = 0.0;
                                for (com.google.firebase.database.DataSnapshot element : el.child("users/").getChildren()) {
                                    int answer = element.child("answer").getValue(Integer.class);
                                    double weight = element.child("credibility").getValue(Double.class);
                                    total += answer*weight;
                                    weights_sum += weight;

                                }
                                if(weights_sum > 0) {
                                    double final_answer = total / weights_sum;
                                    int answer = final_answer > 0 ? 1 : -1;
                                    AlertQuestionary cdd = new AlertQuestionary(activity, sound, animal, true, Optional.of(QuestionAnswer.intToBoolean(answer)), Optional.of(final_answer), Integer.parseInt(park), Integer.parseInt(sensorid), Integer.parseInt(el.getKey()));
                                    cdd.show();
                                } else {
                                    AlertQuestionary cdd = new AlertQuestionary(activity, sound, animal, true, Optional.empty(), Optional.empty(), Integer.parseInt(park), Integer.parseInt(sensorid), Integer.parseInt(el.getKey()));
                                    cdd.show();
                                    Toast.makeText(activity, "ULTIMO CASO", Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                        //Toast.makeText(context, "" + exist, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}