package com.example.appar.qr_ar;

import android.util.Pair;
import androidx.annotation.NonNull;
import com.example.appar.GlobalVariable;
import com.example.appar.database.User;
import com.example.appar.database.UserCredibility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;

public class QuestionAnswer {
    public static boolean intToBoolean(int number) {
        return number == 1;
    }

    public static Pair<String, String> valueAnswer(boolean given, boolean real, Optional<Double> credibility, String username) {
        String title;
        String body;
        int answer_correct = 0;
        if (given == real) {
            title = "CORRECT";
            answer_correct = 1;
        } else {
            title = "WRONG";
        }
        if(credibility.isPresent()) {
            body = "You answered: " + given + " \n eand accordingly to the users the answer was: " + real + " with a confidence of: " + Math.abs(credibility.get());
        } else {
            body = "You answered: " + given + " \n and the answer was: " + real;
        }
        QuestionAnswer.changeUserCredibility(username, answer_correct);
        return Pair.create(title, body);
    }

    public static void changeUserCredibility(String username, int answer_correct) {
        GlobalVariable.getDatabase_reference().child("users/" + username).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int correct = dataSnapshot.child("correct").getValue(Integer.class);
                int total = dataSnapshot.child("total").getValue(Integer.class);
                String password = dataSnapshot.child("password").getValue(String.class);
                User user = new User(password, total + 1, correct + answer_correct);
                DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("users/"+username);
                usersRef.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void addUserQuestionAnswer(String username, String animal, int questionid, int answer) {
        DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("questions/" + animal + "/" + questionid + "/users/" + username);
        usersRef.setValue(answer);
    }

    public static void addUserSoundAnswer(String username, int parkid, int sensorid, int soundid, int answer) {
        GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int correct = dataSnapshot.child("users/" + GlobalVariable.getInstance().getUsername()).child("correct").getValue(Integer.class);
                int total = dataSnapshot.child("users/" + GlobalVariable.getInstance().getUsername()).child("total").getValue(Integer.class);

                double credibility = 1.0;
                if(total > 0) credibility = (double) correct / (double) total;

                DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("sensor_sounds/" + parkid + "/" + sensorid + "/" + soundid + "/users/" + username);
                usersRef.setValue(new UserCredibility(answer, credibility));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
