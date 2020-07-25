package com.example.appar.qr_ar;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.appar.AlertQuestionary;
import com.example.appar.GameMap;
import com.example.appar.GlobalVariable;
import com.example.appar.Homepage;
import com.example.appar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private ArFragment fragment;
    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    private ModelLoader modelLoader;
    private String qrcode;
    boolean created = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Intent back = new Intent(this, GameMap.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getIntent().getStringExtra("position") != null) {
                    String position = getIntent().getStringExtra("position");
                    String parkid = getIntent().getStringExtra("parkid");
                    back.putExtra("position", position);
                    back.putExtra("parkid", parkid);
                }
                startActivity(back);
            }
        });


        fragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });
        modelLoader = new ModelLoader(new WeakReference<>(this));

        qrcode = getIntent().getStringExtra("Qr_code");

    }

    private void onUpdate() {
        /*
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }

         */


        //addObject(Uri.parse("andy_dance.sfb"));




        boolean trackingChanged = updateTracking();


        if (isTracking && !created) {

            Session session = fragment.getArSceneView().getSession();
            float[] pos = { 0,0,-1 };
            float[] rotation = {0,0,0,1};
            Anchor anchor =  session.createAnchor(new Pose(pos, rotation));
            Frame frame = fragment.getArSceneView().getArFrame();

            modelLoader.loadModel(anchor, Uri.parse(qrcode));
            //modelLoader.loadModel(anchor, Uri.parse("andy_dance.sfb"));
            Toast.makeText(MainActivity.this, "ONUPDATE " + created, Toast.LENGTH_LONG).show();

            created = true;
        }
        if(!isTracking) {
            created = false;
        }




    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth()/2, vw.getHeight()/2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void addObject(Uri model) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    modelLoader.loadModel(hit.createAnchor(), model);
                    break;

                }
            }
        }
    }

    /*
    private void nodeClick() {
        AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid"));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Toast.makeText(MainActivity.this, "START", Toast.LENGTH_LONG).show();
        myRef.child("sensor_sounds/"+getIntent().getStringExtra("parkid")).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.getChildren().forEach(el2 -> {
                    dataSnapshot.getRef().child(getIntent().getStringExtra("sensorid")).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2){

                            Toast.makeText(MainActivity.this, "OPEN ONE ALERT SENSID " + getIntent().getStringExtra("sensorid") +  " park " + getIntent().getStringExtra("parkid"), Toast.LENGTH_LONG).show();

                            for(com.google.firebase.database.DataSnapshot el3 : dataSnapshot2.getChildren()) {

                                String value = el3.child("sound").getValue(String.class); //This is a1
                                AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid"));


                            }

                                /*
                                dataSnapshot2.getChildren().forEach(el3 -> {
                                    String value = el3.child("sound").getValue(String.class); //This is a1
                                    //Toast.makeText(MainActivity.this, "Path: " + value + " animal: " + getIntent().getStringExtra("animal"), Toast.LENGTH_LONG).show();
                                    //AlertQuestionary cdd=new AlertQuestionary(MainActivity.this, value, getIntent().getStringExtra("animal"), true);

                                    //cdd.requestWindowFeature(Window.FEATURE_NO_TITLE);


                                    //Toast.makeText(MainActivity.this, "OPEN ONE ALERT", Toast.LENGTH_LONG).show();

                                    //Toast.makeText(MainActivity.this, "Username: " + GlobalVariable.getInstance().getUsername() + " parkid: " + getIntent().getStringExtra("parkid") + " sensorid: " + getIntent().getStringExtra("sensorid"), Toast.LENGTH_LONG).show();
                                    AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid"));




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
    }
*/
    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        node.setOnTapListener((v, event) -> {
            AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid"));

            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("zzz")
                    .setTitle("Codelab aaaaaaaaaerror!");
            AlertDialog dialog = builder.create();
            dialog.show();*/
            //node.setOnTapListener((v2, event2) -> {});








        });
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();

        startAnimation(node, renderable);
    }

    public void onException(Throwable throwable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(throwable.getMessage())
                .setTitle("Codelab error!");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }

    public void startAnimation(TransformableNode node, ModelRenderable renderable){
        if(renderable==null || renderable.getAnimationDataCount() == 0) {
            return;
        }
        for(int i = 0;i < renderable.getAnimationDataCount();i++){
            AnimationData animationData = renderable.getAnimationData(i);
        }
        ModelAnimator animator = new ModelAnimator(renderable.getAnimationData(0), renderable);
        animator.start();
    }


}
