package com.example.appar;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appar.database.Sensor;
import com.example.appar.qr_ar.ArActivity;
import com.example.appar.qr_ar.CaptureActivityPortrait;
import com.example.appar.qr_ar.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldMap extends AppCompatActivity {

    private MapView mapView;
    PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private AnimalFigure neareastSensor;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibnllcmNhIiwiYSI6ImNrYW1jY2R2azA1ZHUyc3Bmb2JqYmRjN2EifQ.E4YLUOB7CH5VGbqs5Tj4vg");
        setContentView(R.layout.world_map);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Intent back = new Intent(this,Homepage.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
            }
        });

        Spinner dynamicSpinner = findViewById(R.id.dynamicSpinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"1", "2", "3"};


        Map<String, String> city_map= new HashMap<String, String>();
        List<String> list_str = new ArrayList<String>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("parks/").addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){



                dataSnapshot.getChildren().forEach(el -> {

                    String city = el.child("city").getValue(String.class);
                    //Toast.makeText(WorldMap.this, "city: " + city, Toast.LENGTH_LONG).show();
                    if(!city_map.keySet().contains("city")) {
                        myRef.child("city/" + city).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {

                                String lat_long = dataSnapshot2.getValue(String.class);
                                city_map.put(city, lat_long);
                                list_str.add(city);
                                //Toast.makeText(WorldMap.this, "city: " + city + " ll: " + lat_long, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                 });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap_el) {
                mapboxMap = mapboxMap_el;
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);



                myRef.addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){

                        List<Sensor> list = new ArrayList<Sensor>();

                        dataSnapshot.child("parks").getChildren().forEach(el -> {
                            String position = el.child("position").getValue(String.class); //This is a1
                            String name = el.child("name").getValue(String.class); //This is a1
                            //Toast.makeText(Db_usage.this, "id: " + el.getKey(),
                            //   Toast.LENGTH_LONG).show();
                            list.add(new Sensor(el.getKey(), position, name, true, ""));
                        });
                        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

                                list.forEach(el -> {
                                    mapboxMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(el.getLat(), el.getLon()))
                                            .title(el.getId() + ""));
                                });


                                //Toast.makeText(WorldMap.this, "city: " + list_str.get(0), Toast.LENGTH_LONG).show();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(WorldMap.this, android.R.layout.simple_spinner_dropdown_item, list_str);
                                dynamicSpinner.setAdapter(adapter);
                                dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        Toast.makeText(parent.getContext(), city_map.get(list_str.get(position)), Toast.LENGTH_SHORT).show();

                                        Double lat = Double.parseDouble(city_map.get(list_str.get(position)).split(";")[0]);
                                        Double lon = Double.parseDouble(city_map.get(list_str.get(position)).split(";")[1]);

                                        CameraPosition pos = new CameraPosition.Builder()
                                                .target(new LatLng(lat, lon)) // Sets the new camera position
                                                .zoom(14) // Sets the zoom
                                                .build(); // Creates a CameraPosition from the builder

                                        mapboxMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(pos), 7000);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                        // sometimes you need nothing here
                                    }
                                });





                                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(@NonNull Marker marker) {
                                        // Show a toast with the title of the selected marker
                                        Toast.makeText(WorldMap.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                                        Intent foo = new Intent(WorldMap.this, GameMap.class);
                                        foo.putExtra("parkid", marker.getTitle());
                                        foo.putExtra("position", marker.getPosition().getLatitude()+ ";" + marker.getPosition().getLongitude());
                                        startActivity(foo);
                                        return true;
                                    }
                                });

                                /*
                                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                                    @Override
                                    public boolean onMapClick(@NonNull LatLng point) {

                                        Toast.makeText(WorldMap.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();

                                        //mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(point.getLatitude(), point.getLongitude())).build());

                                        CameraPosition position = new CameraPosition.Builder()
                                                .target(new LatLng(point.getLatitude(), point.getLongitude())) // Sets the new camera position
                                                .zoom(17) // Sets the zoom
                                                .build(); // Creates a CameraPosition from the builder

                                        mapboxMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(position), 7000);
                                        return true;
                                    }
                                });
*/
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });



            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, MainActivity.class);

                intent.putExtra("animal", neareastSensor.getAnimal());
                intent.putExtra("id", neareastSensor.getId() + "");
                intent.putExtra("Qr_code", "https://raw.githubusercontent.com/Nyerca/ar_images/master/bat.sfb");
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
