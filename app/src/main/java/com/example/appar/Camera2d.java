package com.example.appar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.example.appar.qr_ar.AnimationClass;
import com.example.appar.qr_ar.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.journeyapps.barcodescanner.CameraPreview;

import java.io.IOException;

public class Camera2d extends Activity implements AnimationClass {

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

        Animation animation = AnimationUtils.loadAnimation(Camera2d.this,R.anim.righttoleft);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                Animation animation2 = AnimationUtils.loadAnimation(Camera2d.this,R.anim.rotate);
                findViewById(R.id.badge).startAnimation(animation2);
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        findViewById(R.id.achievement_panel).startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera2d);

        FrameLayout frameLayout = findViewById(R.id.framelayout);

        Camera camera = Camera.open(0);

        ShowCamera showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

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


        imageView = findViewById(R.id.image);

        int id = this.getResources().getIdentifier(getIntent().getStringExtra("2d_image"), "drawable", this.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(v -> AlertQuestionary.randomQuestionary(this, GlobalVariable.getInstance().getUsername(), getIntent().getStringExtra("parkid"), getIntent().getStringExtra("animal"), getIntent().getStringExtra("sensorid")));

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




    }

    public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
        Camera camera;
        SurfaceHolder holder;

        public ShowCamera(Context context, Camera camera) {
            super(context);
            this.camera = camera;
            holder = getHolder();
            holder.addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Camera.Parameters params = camera.getParameters();

            if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                params.setRotation(90);
            } else {
                params.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                params.setRotation(0);
            }

            try{
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {

            }
        }
    }

}

