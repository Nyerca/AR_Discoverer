package com.example.appar;

import android.app.Activity;
import android.app.Dialog;
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
import com.example.appar.database.AchievementChecker;
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

public class AlertQuestionary extends Dialog implements android.view.View.OnClickListener {

    private Activity c;
    private Dialog d;
    private FloatingActionButton yes, no, ok;
    private SeekBar seekbar;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    private TextView textdia;
    private Boolean isSound;
    private TextView question;
    private Optional<Boolean> answer;
    private Optional<Double> credibility;
    private Button btn;

    private int parkid;
    private int sensorid;
    private int elementid;

    Optional<Pair<Integer, String>> funfactid = Optional.empty();

    private String body ="https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav";
    private String animal = "cat";

    public AlertQuestionary(Activity a, String body, String animal, Boolean isSound, Optional<Boolean> answer, Optional<Double> credibility, int parkid, int sensorid, int elementid) {
        super(a);
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
        this.getFunFact(animal, GlobalVariable.getInstance().getUsername());

        if(funfactid.isPresent()) {
            textdia.setText(funfactid.get().second);
        } else {
            textdia.setText("Questionary");
        }


        FrameLayout frame_ok = findViewById(R.id.frame_ok);
        frame_ok.setVisibility(View.GONE);
        ok = findViewById(R.id.btn_ok);
        ok.setOnClickListener(v1 -> {
            if(funfactid.isPresent()) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                DatabaseReference usersRef = myRef.child("funfacts/"+animal+ "/" + funfactid.get().first + "/users/" + GlobalVariable.getInstance().getUsername());
                usersRef.setValue(1);
            }

            DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("park_sensors/"+parkid+ "/" + sensorid + "/users/" + GlobalVariable.getInstance().getUsername());
            usersRef.setValue(1);

            dismiss();

        });

        yes = findViewById(R.id.btn_yes);
        yes.setOnClickListener(v12 -> {
            if(answer.isPresent()) {
                Pair<String, String> answers = QuestionAnswer.valueAnswer(true, answer.get(), credibility, GlobalVariable.getInstance().getUsername());
                textdia.setText(answers.first);
                question.setText(answers.second);
                //dismiss();
            } else {
                textdia.setText("CONGRATULATION");
                question.setText("You are the first user which contributed to classify this sound!");
            }
            FrameLayout frame_yes = findViewById(R.id.frame_yes);
            frame_yes.setVisibility(View.GONE);
            FrameLayout frame_no = findViewById(R.id.frame_no);
            frame_no.setVisibility(View.GONE);
            seekbar.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);

            answer(isSound, animal, 1, parkid, sensorid, elementid);

            frame_ok.setVisibility(View.VISIBLE);

        });
        no = findViewById(R.id.btn_no);
        no.setOnClickListener(v13 -> {
            if(answer.isPresent()) {
                Pair<String, String> answers = QuestionAnswer.valueAnswer(false, answer.get(), credibility, GlobalVariable.getInstance().getUsername());
                textdia.setText(answers.first);
                question.setText(answers.second);
            } else {
                textdia.setText("CONGRATULATION");
                question.setText("You are the first user which contributed to classify this sound!");
            }

            FrameLayout frame_yes = findViewById(R.id.frame_yes);
            frame_yes.setVisibility(View.GONE);
            FrameLayout frame_no = findViewById(R.id.frame_no);
            frame_no.setVisibility(View.GONE);
            seekbar.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);

            answer(isSound, animal, -1, parkid, sensorid, elementid);

            frame_ok.setVisibility(View.VISIBLE);
        });

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
        AchievementChecker.check(animal, parkid, (MainActivity)c);

    }

    private void changeSeekbar() {
        seekbar.setProgress(mediaPlayer.getCurrentPosition());
        if(mediaPlayer.isPlaying()) {
            runnable= () -> changeSeekbar();
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



    public void getFunFact(String animal, String username) {
        GlobalVariable.getDatabase_reference().child("funfacts/" + animal).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                for(com.google.firebase.database.DataSnapshot el : dataSnapshot.getChildren()) {
                    if(!el.child("users/"+username).exists()) {
                        String title = el.child("title").getValue(String.class);
                        funfactid = Optional.of(new Pair(Integer.parseInt(el.getKey()), title));
                        textdia.setText(funfactid.get().second);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }


    private static void takeSound(Activity activity, String username, String park, String animal, String sensorid, boolean isFirstCall) {
        GlobalVariable.getDatabase_reference().child("sensor_sounds/" +  park + "/" + sensorid).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for(com.google.firebase.database.DataSnapshot el : dataSnapshot.getChildren()) {
                    if(!el.child("users/"+username).exists()) {
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
                            }

                        }
                        return;
                    }
                }
                if(isFirstCall) {
                    takeQuestion(activity, username, park, animal, sensorid, false);
                } else {
                    Toast.makeText(activity, "There aren't new sounds yet!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private static void takeQuestion(Activity activity, String username, String park, String animal, String sensorid, boolean isFirstCall) {
        GlobalVariable.getDatabase_reference().child("questions/" + animal).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for(com.google.firebase.database.DataSnapshot el : dataSnapshot.getChildren()) {
                    if(!el.child("users/"+username).exists()) {
                        String question = el.child("question").getValue(String.class);
                        int answer = el.child("answer").getValue(Integer.class);


                        AlertQuestionary cdd=new AlertQuestionary(activity, question, animal, false, Optional.of(QuestionAnswer.intToBoolean(answer)), Optional.empty(), Integer.parseInt(park), Integer.parseInt(sensorid), Integer.parseInt(el.getKey()));
                        cdd.show();
                        return;
                    }
                }
                if(isFirstCall) {
                    takeSound(activity, username, park, animal, sensorid, false);
                } else {
                    Toast.makeText(activity, "There aren't new sounds yet!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void randomQuestionary(Activity activity, String username, String park, String animal, String sensorid) {
        Random r = new Random();
        if(!r.nextBoolean()) {
            takeQuestion(activity, username, park, animal, sensorid, true);
        } else {
            takeSound(activity, username, park, animal, sensorid, true);
        }
    }
}