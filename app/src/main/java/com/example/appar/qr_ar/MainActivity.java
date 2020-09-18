package com.example.appar.qr_ar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appar.AlertQuestionary;
import com.example.appar.GameMap;
import com.example.appar.GlobalVariable;
import com.example.appar.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements AnimationClass {

    private ArFragment fragment;
    private boolean isTracking;
    private boolean isHitting;
    private boolean clickable = true;
    private String qrcode;
    boolean created = false;
    private int rotation_intent;

    RelativeLayout view;
    View imageViewContainer;
    ImageView imageView;

    public void setAnimationAchievement(String title, String img_path) {

        imageViewContainer.setOnClickListener(v -> {
            imageViewContainer.setVisibility(View.INVISIBLE);
        });

        TextView badge_title = findViewById(R.id.badge_title);
        badge_title.setText(title);

        ImageView imageView = findViewById(R.id.badge);
        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier(img_path, "drawable", context.getPackageName());
        imageView.setImageDrawable(ContextCompat.getDrawable(this, id));

        imageViewContainer.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.righttoleft);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                Animation animation2 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate);
                findViewById(R.id.badge).startAnimation(animation2);
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        findViewById(R.id.achievement_panel).startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Intent back = new Intent(this, GameMap.class);
        findViewById(R.id.backButton).setOnClickListener(v -> {
            if(getIntent().getStringExtra("position") != null) {
                String position = getIntent().getStringExtra("position");
                String parkid = getIntent().getStringExtra("parkid");
                back.putExtra("position", position);
                back.putExtra("parkid", parkid);
            }
            startActivity(back);
        });


        view = findViewById(R.id.achievement);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageViewContainer = inflater.inflate(R.layout.achievement, null);
        view.addView(imageViewContainer, 0);
        imageViewContainer.setVisibility(View.INVISIBLE);


        //qrcode = getIntent().getStringExtra("Qr_code");
        //qrcode = "bat.sfb";
        //qrcode = "andy_dance.sfb";
        //qrcode = "ab2.sfb";
        //qrcode = "scene.sfb";
        //qrcode = "bees.sfb";
        //qrcode = "girasole.jpg";
        //qrcode = "https://raw.githubusercontent.com/Nyerca/ar_images/master/bat.sfb";

        qrcode = "";

        imageView = findViewById(R.id.image);

        if(getIntent().getStringExtra("Qr_code") != null) {
            qrcode = getIntent().getStringExtra("Qr_code");
            rotation_intent = Integer.parseInt(getIntent().getStringExtra("rotation"));
            System.out.println("ROTATION_INTENT: " + rotation_intent);
            imageView.setVisibility(View.GONE);
        } else {
            int id = this.getResources().getIdentifier(getIntent().getStringExtra("2d_image"), "drawable", this.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(v -> AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid")));
        }

        Button capture =  findViewById(R.id.capture);
        capture.setOnClickListener(v -> {
            DatabaseReference usersRef = GlobalVariable.getDatabase_reference().child("user_captured_animals/" + GlobalVariable.getInstance().getUsername() + "/"+getIntent().getStringExtra("2d_image"));
            usersRef.setValue(getIntent().getStringExtra("animal"));

            FrameLayout pnlFlash = findViewById(R.id.pnlFlash);

            AlphaAnimation fade = new AlphaAnimation(1, 0);
            fade.setDuration(450);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation anim) {
                    pnlFlash.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationRepeat(Animation anim) {
                }
                @Override
                public void onAnimationEnd(Animation anim) {
                    pnlFlash.setVisibility(View.GONE);
                }
            });
            pnlFlash.startAnimation(fade);

            capture.setVisibility(View.GONE);
        });

        if(getIntent().getStringExtra("image_owned") != null) {
            System.out.println("THE IMAGE IS OWNED 2");
            capture.setVisibility(View.GONE);
        }

        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());
        fragment.getPlaneDiscoveryController().hide();
        fragment.getPlaneDiscoveryController().setInstructionView(null);
        if(qrcode.length()>0) {
            fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                fragment.onUpdate(frameTime);
                onUpdate();
            });
        }

    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();


        if (isTracking && !created) {

            Session session = fragment.getArSceneView().getSession();
            float[] pos = { 0,0,-1 };
            //float[] rotation = {0,90,0,1};
            float[] rotation = {0,rotation_intent,0,1};
            Anchor anchor =  session.createAnchor(new Pose(pos, rotation));
            Frame frame = fragment.getArSceneView().getArFrame();

            ModelRenderable.builder()
                    .setSource(this, Uri.parse(qrcode))
                    .build()
                    .handle((renderable, throwable) -> {
                        MainActivity activity =this;
                        if (throwable != null) {
                            activity.onException(throwable);
                        } else {
                            activity.addNodeToScene(anchor, renderable);
                        }
                        return null;
                    });

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

    public void setClickable(boolean clickable){
        this.clickable = clickable;
    }


    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {
        System.out.println("FIGLI_SU_ARCORE: " + fragment.getArSceneView().getScene().getChildren().size());

        Optional<Node> tobeRemoved = Optional.empty();
        for (Node el : fragment.getArSceneView().getScene().getChildren()) {
            //method to avoid double nodes appearing
            if(el instanceof AnchorNode) {
                tobeRemoved = Optional.of(el);
            }
        }
        if(tobeRemoved.isPresent()) fragment.getArSceneView().getScene().removeChild(tobeRemoved.get());
        if(fragment.getArSceneView().getScene().getChildren().size() <= 3) {
            AnchorNode anchorNode = new AnchorNode(anchor);
            TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
            node.setRenderable(renderable);
            node.setParent(anchorNode);
            node.setOnTapListener((v, event) -> {
                if (clickable) {
                    clickable = false;
                    AlertQuestionary.randomQuestionary(MainActivity.this, GlobalVariable.getInstance().getUsername(),
                            getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid"));
                }
            });
            fragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();
            startAnimation(node, renderable);
        }

    }

    public void onException(Throwable throwable){
        System.out.println("ECCEZIONE_GENERATA");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(throwable.getMessage()).setTitle("Codelab error!");
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }

    public void startAnimation(TransformableNode node, ModelRenderable renderable){
        if(renderable==null || renderable.getAnimationDataCount() == 0) {
            return;
        }
        ModelAnimator animator = new ModelAnimator(renderable.getAnimationData(0), renderable);
        animator.setRepeatCount(10000);
        animator.start();
    }

    private class CustomVisualizer implements SelectionVisualizer {
        @Override
        public void applySelectionVisual(BaseTransformableNode node) {}
        @Override
        public void removeSelectionVisual(BaseTransformableNode node) {}
    }
}

