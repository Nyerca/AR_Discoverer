package com.example.appar.qr_ar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appar.GlobalVariable;
import com.example.appar.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class PlantActivity extends AppCompatActivity {

    private Button btnScan;
    private TextView tv_qr_readTxt;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        btnScan = findViewById(R.id.btnScan);
        tv_qr_readTxt = findViewById(R.id.tv_qr_readTxt);
        imageView = findViewById(R.id.imageView);
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
                //AlertDialog dialog = builder.create();

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

        /*

        Set<String> hash_Set= new HashSet<String>();
        hash_Set.add("ciao");
        hash_Set.add("miao");
        hash_Set.add("ciao");

        hash_Set.forEach(el -> {
            System.out.println(el);
        });

         */
        //AchievementChecker.check("bat", 1, this);

/*

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
        */

        Resources res = getResources();
        Bitmap foot = BitmapFactory.decodeResource(res, R.drawable.girasole);
        InputImage image = InputImage.fromBitmap(foot, 0);

        /*
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            int index = label.getIndex();

                            System.out.println("RISULTATO: " + text + " conf: " + confidence + " index: " + index);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

         */








        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int REQUEST_IMAGE_CAPTURE = 1;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });



    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);

            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("lite-model_aiy_vision_classifier_plants_V1_3.tflite")
                            // or .setAbsoluteFilePath(absolute file path to tflite model)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(5)
                            .build();

            ImageLabeler imageLabeler =
                    ImageLabeling.getClient(customImageLabelerOptions);

            imageLabeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            System.out.println("ENTER_ON_SC " + labels.size());
                            for (ImageLabel label : labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                int index = label.getIndex();

                                tv_qr_readTxt.setText(text);

                                System.out.println("RESULT_result: " + text + " conf: " + confidence + " index: " + index);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("FAIL_ON_SC " + e);
                            // Task failed with an exception
                            // ...
                        }
                    });

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}