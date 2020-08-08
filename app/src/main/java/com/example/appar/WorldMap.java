package com.example.appar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appar.database.Sensor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldMap extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibnllcmNhIiwiYSI6ImNrYW1jY2R2azA1ZHUyc3Bmb2JqYmRjN2EifQ.E4YLUOB7CH5VGbqs5Tj4vg");
        setContentView(R.layout.world_map);

        findViewById(R.id.backButton).setOnClickListener(v -> startActivity(new Intent(this,Homepage.class)));

        Spinner dynamicSpinner = findViewById(R.id.dynamicSpinner);

        Map<String, String> city_map= new HashMap<>();
        List<String> list_str = new ArrayList<>();
        GlobalVariable.getDatabase_reference().child("parks/").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                dataSnapshot.getChildren().forEach(el -> {
                    String city = el.child("city").getValue(String.class);
                    if(!city_map.keySet().contains("city")) {
                        GlobalVariable.getDatabase_reference().child("city/" + city).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                String lat_long = dataSnapshot2.getValue(String.class);
                                city_map.put(city, lat_long);
                                list_str.add(city);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
                 });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap_el -> {
            mapboxMap = mapboxMap_el;
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setLogoEnabled(false);

            GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){

                    List<Sensor> list = new ArrayList<>();

                    dataSnapshot.child("parks").getChildren().forEach(el -> {
                        String position = el.child("position").getValue(String.class); //This is a1
                        String name = el.child("name").getValue(String.class); //This is a1
                        list.add(new Sensor(el.getKey(), position, name, true, ""));
                    });
                    mapboxMap.setStyle(Style.OUTDOORS, style -> {

                        list.forEach(el -> {
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(el.getLat(), el.getLon()))
                                    .title(el.getId() + ""));
                        });


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(WorldMap.this, android.R.layout.simple_spinner_dropdown_item, list_str);
                        dynamicSpinner.setAdapter(adapter);
                        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                //Toast.makeText(parent.getContext(), city_map.get(list_str.get(position)), Toast.LENGTH_SHORT).show();

                                Double lat = Double.parseDouble(city_map.get(list_str.get(position)).split(";")[0]);
                                Double lon = Double.parseDouble(city_map.get(list_str.get(position)).split(";")[1]);

                                CameraPosition pos = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(14).build();
                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 7000);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });





                        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                // Show a toast with the title of the selected marker
                                //Toast.makeText(WorldMap.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                                Intent foo = new Intent(WorldMap.this, GameMap.class);
                                foo.putExtra("parkid", marker.getTitle());
                                foo.putExtra("position", marker.getPosition().getLatitude()+ ";" + marker.getPosition().getLongitude());
                                startActivity(foo);
                                return true;
                            }
                        });
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
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
}
