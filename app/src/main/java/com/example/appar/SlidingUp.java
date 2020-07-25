package com.example.appar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class SlidingUp extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected ListView listView;

    public enum Distance {
        ONE(10),
        TWO(20),
        THREE(30),
        FAR(31);

        private int distance;
        private Distance(int dist) {
            this.distance = dist;
        }

        public int getDistance() {
            return distance;
        }
        public static Distance getStep(double value) {
            if(value < 10) return ONE;
            else if(value < 20) return TWO;
            else if(value < 30) return THREE;
            else return FAR;
        }
        public String toString() {
            return this.distance + "";
        }
    }
    public TextView createTextView(String text) {
        String attributes =
                "<attribute xmlns:android=\"http://schemas.android.com/apk/res/android\" " +
                        "android:layout_width=\"match_parent\" android:layout_height=\"900dp\" android:padding=\"10dp\" " + "android:clickable=\"true\" " +
                        "android:clickable=\"true\" " +
                        "android:text=\"" + text +  "\" android:gravity = \"left\" " + "\"/>";

        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(attributes));
            parser.next();
            AttributeSet attrs = Xml.asAttributeSet(parser);
            return new TextView(this, attrs);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TextView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_panel);

        RelativeLayout root = (RelativeLayout) findViewById(R.id.main_layout);

        LinearLayout top = new LinearLayout(this);
        top.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));





        DistanceAnimalView distance_animal_view = new DistanceAnimalView(this, null, true, "bat");
        distance_animal_view.setDistance(3);
        distance_animal_view.setImagePath("http://www.pngall.com/wp-content/uploads/2016/06/Bat-Download-PNG.png");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(300, 100,0,0);
        distance_animal_view.setLayoutParams(params);
        //Button button = new Button(this);
        //button.setText("Button 1");
        //button.setHeight(700);
        //button.setWidth(700);
        //top.addView(button);

        //top.addView(DistanceAnimalView.createView(this, 0, 1));
        top.addView(distance_animal_view);
        //root.addView(DistanceAnimalView.createView(this, 0,1));
        //root.addView(DistanceAnimalView.createView(this, 100,2));


        listView = findViewById(R.id.listView);
        /*
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new String[] {"T1", "Paste"}));

         */
        Button button = new Button(this);
        button.setText("Button 1");
        button.setHeight(700);
        button.setWidth(700);

        Button button2 = new Button(this);
        button.setText("Button 1");
        button.setHeight(700);
        button.setWidth(700);
        listView.setAdapter(new ArrayAdapter<Button>(this,
                android.R.layout.simple_list_item_1, new Button[] {button, button2}));

        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);






        Button btn = findViewById(R.id.btnOpen);
        btn.setOnClickListener(view ->{
            layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        });





        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            requestPermissions(
                    new String[]{Manifest.permission
                            .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    100);

        }

        //Toast.makeText(SlidingUp.this, "" + getMeasure(44.1306184,12.2272485,44.1306348,12.2273569),Toast.LENGTH_LONG).show();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        Toast.makeText(Position.this, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(),
                Toast.LENGTH_LONG).show();
         */
        Double returnDistance = getMeasure(location.getLatitude(), location.getLongitude(), 44.13086749, 12.22772357);
        Distance step = Distance.getStep(returnDistance);

        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new String[] {"" + step}));
    }

    private double getDistance(double lat1, double long1, double lat2, double long2) {
        return Math.sqrt(Math.pow(long2 - long1, 2) + Math.pow(lat2 - lat1, 2));
    }

    private double getMeasure(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        Toast.makeText(SlidingUp.this, "Lat: " + lat1 + " long: " + lon1 + " D: " + (d*1000),Toast.LENGTH_LONG).show();
        return d * 1000; // meters
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

}