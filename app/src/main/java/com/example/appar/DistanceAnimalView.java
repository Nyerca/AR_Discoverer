package com.example.appar;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;

public class DistanceAnimalView extends View {

    private int shapeColor;
    private boolean displayShapeName;
    private float distance;
    private String imagePath;

    private int shapeWidth = 100;
    private int shapeHeight = 100;
    private int textXOffset = 0;
    private int textYOffset = 30;
    private Paint paintShape;

    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DistanceAnimalView, 0, 0);
        // Extract custom attributes into member variables
        try {
            shapeColor = a.getColor(R.styleable.DistanceAnimalView_shapeColor, Color.BLACK);
            displayShapeName = a.getBoolean(R.styleable.DistanceAnimalView_displayShapeName, false);
            distance = a.getFloat(R.styleable.DistanceAnimalView_distance, 0);
            imagePath = a.getString(R.styleable.DistanceAnimalView_imagePath);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
        invalidate();
        requestLayout();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float dist) {
        this.distance = dist;
        invalidate();
        requestLayout();
    }

    public boolean isDisplayingShapeName() {
        return displayShapeName;
    }

    public void setDisplayingShapeName(boolean state) {
        this.displayShapeName = state;
        invalidate();
        requestLayout();
    }


    public int getShapeColor() {
        return shapeColor;
    }

    public void setShapeColor(int color) {
        this.shapeColor = color;
        invalidate();
        requestLayout();
    }

    public DistanceAnimalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawRect(0, 0, shapeWidth, shapeHeight, paintShape);
        if (displayShapeName) {
            //canvas.drawText("Square", shapeWidth + textXOffset, shapeHeight + textXOffset, paintShape);
        }
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.droid_thumb);

        byte [] encodeByte = Base64.decode(imagePath,Base64.DEFAULT);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        Bitmap foot = BitmapFactory.decodeResource(res, R.drawable.paw_one);

        canvas.drawBitmap(bitmap, 10, 0, paintShape);
        if(distance == 1) {
            canvas.drawBitmap(foot, 80, 150, paintShape);
        } else if(distance == 2) {
            canvas.drawBitmap(foot, 40, 150, paintShape);

            canvas.drawBitmap(foot, 120, 150, paintShape);
        } else if(distance == 3) {
            canvas.drawBitmap(foot, 20, 150, paintShape);
            canvas.drawBitmap(foot, 80, 150, paintShape);
            canvas.drawBitmap(foot, 140, 150, paintShape);
        }



        //canvas.drawText("" + distance, shapeWidth + textXOffset, shapeHeight + textXOffset, paintShape);

    }

    private void setupPaint() {
        paintShape = new Paint();
        paintShape.setStyle(Style.FILL);
        paintShape.setColor(shapeColor);
        paintShape.setTextSize(30);
    }

}

