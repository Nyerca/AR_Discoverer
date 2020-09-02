package com.example.appar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WalkthroughActivity extends AppCompatActivity {

    private static final int MAX_VIEWS = 7;

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
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View imageViewContainer = inflater.inflate(R.layout.walkthrough_single_view, null);
            ImageView imageView = imageViewContainer.findViewById(R.id.image_view);
            bottom_text = imageViewContainer.findViewById(R.id.screen_navigation_text);
            Button close  = imageViewContainer.findViewById(R.id.close);
            close.setVisibility(View.GONE);
            close.setOnClickListener(v -> startActivity(new Intent(WalkthroughActivity.this,Homepage.class)));

            switch(position) {
                case 0:
                    bottom_text.setText("Esplora un parco nel mondo utilizzando l'applicazione");
                    imageView.setImageResource(R.drawable.world_click);
                    break;

                case 1:
                    bottom_text.setText("Dopo la scelta del parco puoi utilizzare un sensore, cliccando sulla mappa per muoverti e poi sul sensore per attivarlo");
                    imageView.setImageResource(R.drawable.world_click2);
                    break;

                case 2:
                    bottom_text.setText("Esplora un parco nella zona per cercare dei QR_CODE e trovare l'animale nascosto!");
                    imageView.setImageResource(R.drawable.map_click);
                    break;

                case 3:
                    bottom_text.setText("Quando trovi l'animale puoi 'catturarlo' per inserirlo nella tua collezione!");
                    imageView.setImageResource(R.drawable.capture_click);
                    break;

                case 4:
                    bottom_text.setText("Talvolta l'applicazione ti indicherà delle piante che potrai fotografare per scoprirne il nome!");
                    imageView.setImageResource(R.drawable.plant_click);
                    break;

                case 5:
                    bottom_text.setText("Leggi le informazioni che hai raccolto su un animale!");
                    imageView.setImageResource(R.drawable.collection_click);
                    break;

                case 6:
                    bottom_text.setText("Visualizza il tuo profilo!");
                    imageView.setImageResource(R.drawable.profile_click);
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