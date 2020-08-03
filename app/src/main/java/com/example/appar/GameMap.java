package com.example.appar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.TableRow;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.example.appar.database.Park;
import com.example.appar.database.Sensor;
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
import java.util.ArrayList;
import java.util.List;

public class GameMap extends AppCompatActivity implements PermissionsListener{

    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Button scan;
    private AnimalFigure neareastSensor;
    private int parkid;
    private TableRow root;
    private TableRow row1, row2, row3;

    public TableRow getRow(int index) {
        if(index / 3 == 0) return row1;
        else if(index / 3 == 1) return row2;
        return row3;
    }


    public void setDistance(List<AnimalFigure> animals) {
        row1.removeAllViews();
        row2.removeAllViews();
        row3.removeAllViews();
        root.removeAllViewsInLayout();
        for(int i=0; i<9 && i< animals.size(); i++) {
            if(i == 0 && animals.get(i).getDistance() < 20) {
                neareastSensor = animals.get(i);
                scan.setEnabled(true);
            }
            if (i<3 && DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance() <= 3) {
                root.addView(DistanceAnimalView.createView(this, 0, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen(), animals.get(i).getImagepath()));
            }
            getRow(i).addView(DistanceAnimalView.createView(this, 20, DistanceListener.Distance.getStep(animals.get(i).getDistance()).getDistance(), animals.get(i).getSeen(), animals.get(i).getImagepath()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibnllcmNhIiwiYSI6ImNrYW1jY2R2azA1ZHUyc3Bmb2JqYmRjN2EifQ.E4YLUOB7CH5VGbqs5Tj4vg");
        setContentView(R.layout.game_map2);
        root = findViewById(R.id.top3row);

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
        scan.setOnClickListener(view -> {
            IntentIntegrator integrator = new IntentIntegrator(GameMap.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.setOrientationLocked(true);
            integrator.setCaptureActivity(CaptureActivityPortrait.class);
            integrator.initiateScan();

        });

        SlidingUpPanelLayout layout = findViewById(R.id.sliding_layout);
        root.setOnClickListener(view -> layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED));


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap_el -> {
            mapboxMap = mapboxMap_el;
            mapboxMap.getUiSettings().setZoomGesturesEnabled(false);
            mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setLogoEnabled(false);


            GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    List<Sensor> list = new ArrayList<>();
                    //Toast.makeText(GameMap.this, "Lat: " + latitude + " Long:" + longitude, Toast.LENGTH_LONG).show();

                    mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                            if(getIntent().getStringExtra("parkid") == null) {

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

                                enableLocationComponent(style);

                                List<Park> parklist = new ArrayList<>();

                                dataSnapshot.child("parks").getChildren().forEach(el -> {
                                    String position = el.child("position").getValue(String.class); //This is a1
                                    parklist.add(new Park(el.getKey(), position));
                                });


                                Park park = DistanceListener.getNearest(parklist, latitude, longitude);
                                parkid = park.getId();

                                DistanceListener dl = new DistanceListener(GameMap.this, list);
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                }
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, dl);

                            } else {
                                scan.setVisibility(View.GONE);
                                parkid =Integer.parseInt(getIntent().getStringExtra("parkid"));
                                Double latitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[0]);
                                Double longitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[1]);

                                CameraPosition pos = new CameraPosition.Builder()
                                        .target(new LatLng(latitude, longitude)) // Sets the new camera position
                                        .zoom(17) // Sets the zoom
                                        .build(); // Creates a CameraPosition from the builder
                                mapboxMap.setCameraPosition(pos);

                                mapboxMap.addOnMapClickListener(point -> {

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
                                });
                            }



                            dataSnapshot.child("park_sensors/" + parkid).getChildren().forEach(el -> {
                                String position = el.child("position").getValue(String.class); //This is a1
                                String animal = el.child("animal").getValue(String.class); //This is a1
                                String imagepath = el.child("image").getValue(String.class); //This is a1
                                list.add(new Sensor(el.getKey(), position, animal, el.child("users/" + GlobalVariable.getInstance().getUsername()).exists(), imagepath));
                            });

                            list.forEach(el -> {
                                IconFactory mIconFactory = IconFactory.getInstance(GameMap.this);
                                Resources res = getResources();
                                String packagename = getPackageName();
                                //int id = res.getIdentifier(el.getImagepath(), "drawable", packagename);
                                int id = res.getIdentifier("sensor", "drawable", packagename);
                                Icon icon = mIconFactory.fromResource(id);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(el.getLat(), el.getLon())).icon(icon)
                                        .title(el.getId() + ";" + el.getAnimal()));
                            });


                            if(getIntent().getStringExtra("parkid") != null) {
                                mapboxMap.setOnMarkerClickListener(marker -> {
                                    // Show a toast with the title of the selected marker
                                    Toast.makeText(GameMap.this, marker.getTitle(), Toast.LENGTH_LONG).show();

                                    String sensorid = marker.getTitle().split(";")[0];
                                    String animal = marker.getTitle().split(";")[1];

                                    Intent intent = new Intent(GameMap.this, MainActivity.class);

                                    intent.putExtra("animal",animal);
                                    intent.putExtra("sensorid", sensorid);
                                    intent.putExtra("parkid", parkid + "");
                                    String position = getIntent().getStringExtra("position");
                                    intent.putExtra("position", position);
                                    intent.putExtra("Qr_code", "andy_dance.sfb");
                                    startActivity(intent);
                                    return true;
                                });
                            }

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });



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
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.bat, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Resources res = context.getResources();
        //Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bat);
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
                mapboxMap.getStyle(style -> {
                    //enableLocationComponent(style);
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
                if(getIntent().getStringExtra("position") != null) {
                    String position = getIntent().getStringExtra("position");
                    intent.putExtra("position", position);
                }
                intent.putExtra("Qr_code", "andy_dance.sfb");
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
