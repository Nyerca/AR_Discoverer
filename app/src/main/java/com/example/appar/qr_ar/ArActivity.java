package com.example.appar.qr_ar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appar.AlertQuestionary;
import com.example.appar.GlobalVariable;
import com.example.appar.R;
import com.example.appar.database.AESCrypt;
import com.example.appar.database.AchievementChecker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArActivity extends AppCompatActivity {

    Button btnScan;
    TextView tv_qr_readTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        btnScan = findViewById(R.id.btnScan);
        tv_qr_readTxt = findViewById(R.id.tv_qr_readTxt);

        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("ciao")
                        .setTitle("title")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e("Scan", "Scanned");
                            }
                        });

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();

                //dialog.show();



        //AlertQuestionary cdd=new AlertQuestionary(ArActivity.this, "https://raw.githubusercontent.com/Nyerca/ar_images/master/cat_annoyed.wav", "cat", true);
        //AlertQuestionary cdd=new AlertQuestionary(ArActivity.this, "Can bat fly?", "Bat", false);

        //cdd.requestWindowFeature(Window.FEATURE_NO_TITLE);





        //AlertQuestionary.randomQuestionary(ArActivity.this, "admin", "1", "bat", "1");

        //QuestionAnswer.changeUserCredibility(this, "admin");


        //QuestionAnswer.addUserQuestionAnswer("admin", "bat", 2, 1);

        //QuestionAnswer.addUserSoundAnswer("admin", 1, 2, 1, 1);

/*
        try {
            String cripted = AESCrypt.encrypt("prova");
            String decripted = AESCrypt.decrypt(cripted);
            Toast.makeText(this, "cripted: " + cripted + " decripted: " + decripted, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        GlobalVariable.setInstance("prova3");
        //AchievementChecker.check("bat", 1, this);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Toast.makeText(ArActivity.this, "START", Toast.LENGTH_LONG).show();
        myRef.child("sensor_sounds/1").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.getChildren().forEach(el2 -> {
                    dataSnapshot.getRef().child("2").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2){

                            Toast.makeText(ArActivity.this, "OPEN ONE ALERT SENSID " + getIntent().getStringExtra("sensorid") +  " park " + getIntent().getStringExtra("parkid"), Toast.LENGTH_LONG).show();

                            dataSnapshot2.getChildren().forEach(el3 -> {
                                String value = el3.child("sound").getValue(String.class); //This is a1
                                Toast.makeText(ArActivity.this, "Path: " + value + " animal: " + getIntent().getStringExtra("animal"), Toast.LENGTH_LONG).show();
                                        //cdd.show();
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
        //cdd.show();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(ArActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                tv_qr_readTxt.setText(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Qr_code", "https://raw.githubusercontent.com/Nyerca/ar_images/master/bat.sfb");
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}