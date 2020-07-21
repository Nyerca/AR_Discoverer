package com.example.appar.qr_ar;

import android.app.Activity;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appar.AlertQuestionary;
import com.example.appar.GlobalVariable;
import com.example.appar.Homepage;
import com.example.appar.database.User;
import com.example.appar.database.UserCredibility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;

public class QuestionAnswer {
    public static boolean intToBoolean(int number) {
        if(number == 1) return true;
        return false;
    }

    public static Pair<String, String> valueAnswer(boolean given, boolean real, Optional<Double> credibility, String username) {
        String title;
        String body;
        int answer_correct = 0;
        if (given == real) {
            title = "CORRETTO";
            answer_correct = 1;
        } else {
            title = "ERRATO";
        }
        if(credibility.isPresent()) {
            body = "Hai risposto: " + given + " \n e secondo gli utenti la risposta era: " + real + " con una confidenza del: " + Math.abs(credibility.get());
        } else {
            body = "Hai risposto: " + given + " \n e la risposta era: " + real;
        }
        QuestionAnswer.changeUserCredibility(username, answer_correct);
        return Pair.create(title, body);
    }

    public static void changeUserCredibility(String username, int answer_correct) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("users/" + username).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int correct = dataSnapshot.child("correct").getValue(Integer.class);
                int total = dataSnapshot.child("total").getValue(Integer.class);
                String password = dataSnapshot.child("password").getValue(String.class);
                User user = new User(password, total + 1, correct + answer_correct);
                DatabaseReference usersRef = myRef.child("users/"+username);
                usersRef.setValue(user);

                //Toast.makeText(act, "cor: " + correct + " tot: " + total, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void addUserQuestionAnswer(String username, String animal, int questionid, int answer) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference usersRef = myRef.child("questions/" + animal + "/" + questionid + "/users/" + username);
        usersRef.setValue(answer);
    }

    public static void addUserSoundAnswer(String username, int parkid, int sensorid, int soundid, int answer) {
        //TODO get credibility
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int correct = dataSnapshot.child("users/admin").child("correct").getValue(Integer.class);
                int total = dataSnapshot.child("users/admin").child("total").getValue(Integer.class);

                double credibility = 1.0;
                if(total > 0) credibility = (double) correct / (double) total;

                DatabaseReference usersRef = myRef.child("sensor_sounds/" + parkid + "/" + sensorid + "/" + soundid + "/users/" + username);
                usersRef.setValue(new UserCredibility(answer, credibility));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
