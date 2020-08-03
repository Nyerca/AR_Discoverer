package com.example.appar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Card extends LinearLayout {

    private String titleText, descriptionText, imageText;
    private ImageView image;

    public Card(@NonNull Context context) {
        super(context);
    }

    public Card(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Card, 0, 0);
        titleText = a.getString(R.styleable.Card_title);
        descriptionText = a.getString(R.styleable.Card_description);
        imageText = a.getString(R.styleable.Card_image);


        inflate(context, R.layout.card_view, this);

        TextView title = findViewById(R.id.title);
        title.setText(titleText);

        TextView description = findViewById(R.id.description);
        description.setText(descriptionText);
        if(descriptionText == null) {
            description.setVisibility(GONE);
            findViewById(R.id.separation).setVisibility(GONE);
        }

        image = findViewById(R.id.image);
        image.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_bg));
        //image.setBackgroundResource(R.drawable.ic_profile);
        int id = getResources().getIdentifier(imageText, "drawable", context.getPackageName());
        image.setImageDrawable(ContextCompat.getDrawable(context, id));

        a.recycle();

    }

    public Card(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}