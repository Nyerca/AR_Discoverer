package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WalkthroughActivity extends AppCompatActivity {

    private static final int MAX_VIEWS = 5;

    private ViewPager mViewPager;
    private TextView bottom_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough_activity);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(new WalkthroughPagerAdapter());
    }


    class WalkthroughPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_VIEWS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            //Toast.makeText(WalkthroughActivity.this, "instantiateItem: " + position, Toast.LENGTH_LONG).show();
            Log.e("walkthrough", "instantiateItem(" + position + ");");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View imageViewContainer = inflater.inflate(R.layout.walkthrough_single_view, null);
            ImageView imageView = imageViewContainer.findViewById(R.id.image_view);
            bottom_text = imageViewContainer.findViewById(R.id.screen_navigation_text);
            Button close  = imageViewContainer.findViewById(R.id.close);
            close.setVisibility(View.GONE);
            close.setOnClickListener(v -> startActivity(new Intent(WalkthroughActivity.this,Homepage.class)));

            switch(position) {
                case 0:
                    bottom_text.setText("Explore a park in the world by navigating through the app");
                    imageView.setImageResource(R.drawable.world_click);
                    //Toast.makeText(WalkthroughActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
                    break;

                case 1:
                    bottom_text.setText("After choosing a park you can get to the sensor by clicking on the map to reach them and then by clicking on the sensors pin");
                    imageView.setImageResource(R.drawable.world_click2);
                    //Toast.makeText(WalkthroughActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    bottom_text.setText("Explore a nearby park and search for a QR_CODE to find the animal hiding");
                    imageView.setImageResource(R.drawable.map_click);
                    //Toast.makeText(WalkthroughActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
                    break;

                case 3:
                    bottom_text.setText("Explore the informations you gathered about an animal");
                    imageView.setImageResource(R.drawable.collection_click);
                    //Toast.makeText(WalkthroughActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
                    break;

                case 4:
                    bottom_text.setText("View your own profile");
                    imageView.setImageResource(R.drawable.profile_click);
                    //Toast.makeText(WalkthroughActivity.this, "position: " + position, Toast.LENGTH_LONG).show();
                    close.setVisibility(View.VISIBLE);
                    break;
            }

            ((ViewPager) container).addView(imageViewContainer, 0);
            return imageViewContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }


}