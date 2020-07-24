package com.example.appar;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.appar.database.Park;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

public class GameMap extends AppCompatActivity implements PermissionsListener{

    private MapView mapView;
    PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Button scan;
    private AnimalFigure neareastSensor;
    int parkid;
    /*
    public void setDistance(List<Double> positions) {
        //Toast.makeText(this, "DISTANZA: " + positions.get(0) + " DISTANZA2: " + positions.get(1), Toast.LENGTH_LONG).show();
        Log.d("DISTANZE","DISTANZA: " + positions.get(0) + " DISTANZA2: " + positions.get(1));
    }*/

    TableRow root;
    LinearLayout slidedview;
    TableRow row1, row2, row3;

    public TableRow getRow(int index) {
        if(index / 3 == 0) return row1;
        else if(index / 3 == 1) return row2;
        return row3;
    }


    public void setDistance(List<AnimalFigure> animals) {
        //Toast.makeText(this, "DISTANZA: " + animals.get(0).getDistance() + " DISTANZA2: " + animals.get(1).getDistance()+ " DISTANZA3: " + animals.get(2).getDistance(), Toast.LENGTH_LONG).show();

        row1.removeAllViews();
        row2.removeAllViews();
        row3.removeAllViews();
        //slidedview.removeAllViews();
        root.removeAllViewsInLayout();
        for(int i=0; i<9 && i< animals.size(); i++) {
            if(i == 0 && animals.get(i).getDistance() < 20) {
                neareastSensor = animals.get(i);
                scan.setEnabled(true);
                //Toast.makeText(this, "ENABLED", Toast.LENGTH_LONG).show();
            }
            if (i<3 && DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance() <= 3) {
                root.addView(DistanceAnimalView.createView(this, 0, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen()));
            }
            //slidedview.addView(DistanceAnimalView.createView(this, i * 100, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance()));
            getRow(i).addView(DistanceAnimalView.createView(this, 20, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibnllcmNhIiwiYSI6ImNrYW1jY2R2azA1ZHUyc3Bmb2JqYmRjN2EifQ.E4YLUOB7CH5VGbqs5Tj4vg");
        setContentView(R.layout.game_map2);
        root = findViewById(R.id.top3row);
        //slidedview = (LinearLayout) findViewById(R.id.dragview);

        row1 = findViewById(R.id.tableRow1);
        row2 = findViewById(R.id.tableRow2);
        row3 = findViewById(R.id.tableRow3);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Intent back = new Intent(this,Homepage.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
            }
        });




        scan = findViewById(R.id.btnScan);
        scan.setEnabled(false);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(GameMap.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();

            }
        });

        //root.addView(DistanceAnimalView.createView(this, 0));
        //root.addView(DistanceAnimalView.createView(this, 100));
        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        root.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("Latitude","disaaaaaaaaaaaaaaaaaaaaable");
                                        layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    }
                                });




        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap_el) {
                mapboxMap = mapboxMap_el;
                mapboxMap.getUiSettings().setZoomGesturesEnabled(false);
                mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference("t1/t2");
                //myRef.setValue("Hello, World!");
                DatabaseReference myRef = database.getReference();


                myRef.addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){

                        List<Sensor> list = new ArrayList<Sensor>();

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                        }

                        List<String> providers = locationManager.getProviders(true);
                        Location bestLocation = null;
                        for (String provider : providers) {
                            Location l = locationManager.getLastKnownLocation(provider);
                            if (l == null) {
                                continue;
                            }
                            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                                // Found best last known location: %s", l);
                                bestLocation = l;
                            }
                        }

                        final double latitude= bestLocation.getLatitude();
                        final double longitude= bestLocation.getLongitude();

                        //Toast.makeText(GameMap.this, "Lat: " + latitude + " Long:" + longitude, Toast.LENGTH_LONG).show();



                        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add data or make other map adjustments.

                                String tmp = getIntent().getStringExtra("park");


                                if(getIntent().getStringExtra("parkid") == null) {

                                    //Toast.makeText(GameMap.this, getIntent().getStringExtra("parkid"), Toast.LENGTH_LONG).show();
                                    enableLocationComponent(style);

                                    List<Park> parklist = new ArrayList<Park>();

                                    dataSnapshot.child("parks").getChildren().forEach(el -> {
                                        String position = el.child("position").getValue(String.class); //This is a1
                                        parklist.add(new Park(el.getKey(), position));
                                    });


                                    Park park = DistanceListener.getNearest(parklist, latitude, longitude);
                                    parkid = park.getId();
                                    //Toast.makeText(GameMap.this, park.toString(), Toast.LENGTH_LONG).show();

                                    DistanceListener dl = new DistanceListener(GameMap.this, list);
                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                    }
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, dl);

                                } else {
                                    parkid =Integer.parseInt(getIntent().getStringExtra("parkid"));
                                    Double latitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[0]);
                                    Double longitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[1]);

                                    CameraPosition pos = new CameraPosition.Builder()
                                            .target(new LatLng(latitude, longitude)) // Sets the new camera position
                                            .zoom(17) // Sets the zoom
                                            .build(); // Creates a CameraPosition from the builder
                                    mapboxMap.setCameraPosition(pos);

                                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                                        @Override
                                        public boolean onMapClick(@NonNull LatLng point) {

                                            //Toast.makeText(GameMap.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();

                                            //mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(point.getLatitude(), point.getLongitude())).build());

                                            CameraPosition position = new CameraPosition.Builder()
                                                    .target(new LatLng(point.getLatitude(), point.getLongitude())) // Sets the new camera position
                                                    .zoom(17) // Sets the zoom
                                                    .build(); // Creates a CameraPosition from the builder

                                            mapboxMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(position), 7000);

                                            DistanceListener dl = new DistanceListener(GameMap.this, list);
                                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                            }
                                            dl.getWorldSensorDistance(point.getLatitude(), point.getLongitude());
                                            return true;
                                        }
                                    });
                                }

                                //Toast.makeText(GameMap.this, "id: " + parkid, Toast.LENGTH_LONG).show();


                                dataSnapshot.child("park_sensors/" + parkid).getChildren().forEach(el -> {
                                    String position = el.child("position").getValue(String.class); //This is a1
                                    String animal = el.child("animal").getValue(String.class); //This is a1
                                    //Toast.makeText(GameMap.this, "id: " + el.getKey() + " position: " + position,Toast.LENGTH_LONG).show();

                                    list.add(new Sensor(el.getKey(), position, animal, el.child("users/" + GlobalVariable.getInstance().getUsername()).exists()));
                                });

                                //IconFactory mIconFactory = IconFactory.getInstance(GameMap.this);
                                //Drawable mIconDrawable = ContextCompat.getDrawable(GameMap.this, R.drawable.ic_bee);
                                //drawableToIcon(GameMap.this, R.drawable.ic_bee, 0);
                                //Icon icon = mIconFactory.fromResource(R.drawable.ic_bee);



                                //Icon icon2 = drawableToIcon(GameMap.this, R.drawable.ic_bee, 0);





                                //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.your_drawable_here);
                                list.forEach(el -> {
                                    //Bitmap icon3 = BitmapFactory.decodeResource(GameMap.this.getResources(),R.drawable.ic_bee);
                                    //Resources res = getResources();
                                   // Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.droid_thumb2);


                                    IconFactory mIconFactory = IconFactory.getInstance(GameMap.this);
                                    Icon icon = mIconFactory.fromResource(R.drawable.droid_thumb2);
                                    mapboxMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(el.getLat(), el.getLon())).icon(icon)
                                            .title(el.getId() + ""));
                                });








                                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(@NonNull Marker marker) {
                                        // Show a toast with the title of the selected marker
                                        Toast.makeText(GameMap.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                                        Intent foo = new Intent(GameMap.this, ArActivity.class);
                                        foo.putExtra("myFirstKey", "myFirstValue");
                                        startActivity(foo);
                                        return true;
                                    }
                                });

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

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.droid_thumb2, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Resources res = context.getResources();
        //Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.droid_thumb2);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
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
        public void onExplanationNeeded(List<String> permissionsToExplain) {

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @Override
        public void onPermissionResult(boolean granted) {
            if (granted) {
                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        //enableLocationComponent(style);
                    }
                });
            } else {
                Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
                finish();
            }
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
                intent.putExtra("sensorid", neareastSensor.getId() + "");
                intent.putExtra("parkid", parkid + "");
                intent.putExtra("Qr_code", "https://raw.githubusercontent.com/Nyerca/ar_images/master/bat.sfb");
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
