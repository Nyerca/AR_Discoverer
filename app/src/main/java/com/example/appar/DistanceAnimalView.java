package com.example.appar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

public class DistanceAnimalView extends View {

    private float distance;
    private String imagePath;
    private Paint paintShape;
    private String image;
    private String packagename;

    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DistanceAnimalView, 0, 0);
        try {
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

    public DistanceAnimalView(Context context, AttributeSet attrs, Boolean seen, String image) {
        super(context, attrs);
        this.image = image;
        setupAttributes(attrs);
        setupPaint(seen);
        this.packagename =context.getPackageName();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Resources res = getResources();

        int id = this.getResources().getIdentifier(image, "drawable",packagename);

        Bitmap bitmap = BitmapFactory.decodeResource(res, id);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        //Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bat);

        byte [] encodeByte = Base64.decode(imagePath,Base64.DEFAULT);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        Bitmap foot = BitmapFactory.decodeResource(res, R.drawable.paw_one2);

        canvas.drawBitmap(resized, 10, 0, paintShape);
        if(distance == 1) {
            canvas.drawBitmap(foot, 40, 80, paintShape);
        } else if(distance == 2) {
            canvas.drawBitmap(foot, 20, 80, paintShape);

            canvas.drawBitmap(foot, 60, 80, paintShape);
        } else if(distance == 3) {
            canvas.drawBitmap(foot, 0, 80, paintShape);
            canvas.drawBitmap(foot, 40, 80, paintShape);
            canvas.drawBitmap(foot, 80, 80, paintShape);
        }
    }

    private void setupPaint(Boolean seen) {
        paintShape = new Paint();

        if(!seen) {
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);

            paintShape.setColorFilter(new ColorMatrixColorFilter(ma));
        }


        paintShape.setStyle(Style.FILL);
        paintShape.setTextSize(10);
    }

    public static DistanceAnimalView createView(Context context, int left, int distance, Boolean seen, String image) {
        DistanceAnimalView distance_animal_view = new DistanceAnimalView(context, null, seen, image);
        distance_animal_view.setDistance(distance);
        distance_animal_view.setImagePath("http://www.pngall.com/wp-content/uploads/2016/06/Bat-Download-PNG.png");
        TableRow.LayoutParams params = new TableRow.LayoutParams(pxFromDp(context, 50), pxFromDp(context, 50));
        Log.d("Latitude","" + pxFromDp(context, left));
        params.setMargins(pxFromDp(context, left), 0,0,0);
        distance_animal_view.setLayoutParams(params);
        return distance_animal_view;
    }

    public static int dpFromPx(final Context context, final int px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(final Context context, final float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

}

