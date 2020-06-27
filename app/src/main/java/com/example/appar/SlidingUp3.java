package com.example.appar;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.appar.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class SlidingUp3 extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_panel3);

        NestedScrollView bottomSheet = findViewById( R.id.bottom_sheet );
        //Button button1 = (Button) findViewById( R.id.button_1 );
        //Button button2 = (Button) findViewById( R.id.button_2 );
        //Button button3 = (Button) findViewById( R.id.button_3 );


        /*
        button1.setOnClickListener(view ->{
            switch( view.getId() ) {
                case R.id.button_1: {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Toast.makeText(this, "TITOLO", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        });
*/
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


}