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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.example.appar.database.AnimalFigure;
import com.example.appar.database.Figure;
import com.example.appar.database.Park;
import com.example.appar.database.Sensor;
import com.example.appar.qr_ar.CaptureActivityPortrait;
import com.example.appar.qr_ar.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import com.mapbox.mapboxsdk.maps.Style;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameMap extends AppCompatActivity implements PermissionsListener{

    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Button scan;
    private Figure neareastSensor;
    private int parkid;
    private TableRow root;
    private TableRow row1, row2, row3;

    private static int PLANT_PHOTO_CODE = 1333;

    public TableRow getRow(int index) {
        if(index / 3 == 0) return row1;
        else if(index / 3 == 1) return row2;
        return row3;
    }


    public void setDistance(List<Figure> elements) {
        row1.removeAllViews();
        row2.removeAllViews();
        row3.removeAllViews();
        root.removeAllViewsInLayout();
        for(int i=0; i<9 && i< elements.size(); i++) {
            if(i == 0 && elements.get(i).getDistance() < 15) {
                neareastSensor = elements.get(i);
                scan.setEnabled(true);
            }
            if (i<3 && DistanceListener.Distance.getStep(elements.get(i).getDistance()).getDistance() <= 3) {
                root.addView(DistanceAnimalView.createView(this, 0, DistanceListener.Distance.getStep(elements.get(i).getDistance()).getDistance(), elements.get(i).getSeen(), elements.get(i).getImagepath()));
            }
            getRow(i).addView(DistanceAnimalView.createView(this, 20, DistanceListener.Distance.getStep(elements.get(i).getDistance()).getDistance(), elements.get(i).getSeen(), elements.get(i).getImagepath()));
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

        ImageButton backButton =findViewById(R.id.backButton);
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
            if (neareastSensor instanceof AnimalFigure) {
                IntentIntegrator integrator = new IntentIntegrator(GameMap.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, PLANT_PHOTO_CODE);
                }
            }


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

                    mapboxMap.setStyle(Style.OUTDOORS, style -> {
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
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, dl);

                            dataSnapshot.child("park_plants/" + parkid).getChildren().forEach(el -> {
                                String position = el.child("position").getValue(String.class); //This is a1
                                String plant = el.child("plant").getValue(String.class); //This is a1
                                String imagepath = "leaf";

                                Sensor sensor = new Sensor(el.getKey(), position, plant, true, imagepath, true);
                                list.add(sensor);
                            });

                        } else {
                            scan.setVisibility(View.GONE);
                            parkid =Integer.parseInt(getIntent().getStringExtra("parkid"));
                            Double latitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[0]);
                            Double longitude = Double.parseDouble(getIntent().getStringExtra("position").split(";")[1]);

                            CameraPosition pos = new CameraPosition.Builder()
                                    .target(new LatLng(latitude, longitude))
                                    .zoom(17)
                                    .build();
                            mapboxMap.setCameraPosition(pos);

                            mapboxMap.addOnMapClickListener(point -> {

                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(point.getLatitude(), point.getLongitude()))
                                        .zoom(17)
                                        .build();

                                mapboxMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(position), 7000);

                                DistanceListener dl = new DistanceListener(GameMap.this, list);
                                dl.getWorldSensorDistance(point.getLatitude(), point.getLongitude());
                                return true;
                            });

                        }



                        dataSnapshot.child("park_sensors/" + parkid).getChildren().forEach(el -> {
                            String position = el.child("position").getValue(String.class); //This is a1
                            String animal = el.child("animal").getValue(String.class); //This is a1
                            String imagepath = el.child("image").getValue(String.class); //This is a1
                            Sensor sensor = new Sensor(el.getKey(), position, animal, el.child("users/" + GlobalVariable.getInstance().getUsername()).exists(), imagepath, false);
                            if(el.child("ar_image").exists()) {
                                sensor.setArImage(Optional.of(el.child("ar_image").getValue(String.class)));
                                sensor.setRotation(el.child("rotation").getValue(Integer.class));
                            }
                            list.add(sensor);
                        });



                        list.forEach(el -> {
                            IconFactory mIconFactory = IconFactory.getInstance(GameMap.this);
                            Resources res = getResources();
                            String packagename = getPackageName();
                            //int id = res.getIdentifier(el.getImagepath(), "drawable", packagename);
                            int id;
                            if(el.getIsPlant()) id = res.getIdentifier("plant_map", "drawable", packagename);
                            else id = res.getIdentifier("sensor_map", "drawable", packagename);
                            Icon icon = mIconFactory.fromResource(id);
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(el.getLat(), el.getLon())).icon(icon)
                                    .title(el.getId() + ";" + el.getAnimal() + ";" + el.getImagepath() + (el.getArImage().isPresent()? ";" + el.getArImage().get() + ";" + el.getRotation().get() : ";")    ));
                            //.title(el.getId() + ";" + el.getAnimal() + ";" + (el.getArImage().isPresent()? "true;" + el.getArImage().get() + ";" + el.getRotation().get() : "false;" + el.getImagepath()) ));
                        });




                        if(getIntent().getStringExtra("parkid") != null) {
                            mapboxMap.setOnMarkerClickListener(marker -> {
                                String sensorid = marker.getTitle().split(";")[0];
                                String animal = marker.getTitle().split(";")[1];

                                String imgpath = marker.getTitle().split(";")[2];

                                Intent intent = new Intent(GameMap.this, MainActivity.class);

                                intent.putExtra("animal",animal);
                                intent.putExtra("sensorid", sensorid);
                                intent.putExtra("parkid", parkid + "");
                                String position = getIntent().getStringExtra("position");
                                intent.putExtra("position", position);

                                if(marker.getTitle().split(";").length > 3) {
                                    String ar = marker.getTitle().split(";")[3];
                                    intent.putExtra("Qr_code", ar);
                                    intent.putExtra("rotation", marker.getTitle().split(";")[4]);
                                }
                                intent.putExtra("2d_image", imgpath);

                                GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot1){
                                        if(dataSnapshot1.child("user_captured_animals/"+ GlobalVariable.getInstance().getUsername()+"/"+imgpath).exists()) {
                                            intent.putExtra("image_owned", "1");
                                            System.out.println("THE IMAGE IS OWNED");
                                        }
                                        startActivity(intent);

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                return true;
                                //intent.putExtra("Qr_code", "andy_dance.sfb");

                            });
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
        if (requestCode == PLANT_PHOTO_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);

            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("lite-model_aiy_vision_classifier_plants_V1_3.tflite")
                            // or .setAbsoluteFilePath(absolute file path to tflite model)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(5)
                            .build();

            ImageLabeler imageLabeler =
                    ImageLabeling.getClient(customImageLabelerOptions);

            imageLabeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            System.out.println("ENTER_ON_SC " + labels.size());
                            if(labels.size() == 0 || (labels.size() > 0 && labels.get(0).getText().equals("None"))) new MaterialAlertDialogBuilder(GameMap.this).setMessage("Nessuna pianta trovata!").show();
                            else {
                                for (ImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();

                                    System.out.println("RESULT_result: " + text + " conf: " + confidence + " index: " + index);
                                    new MaterialAlertDialogBuilder(GameMap.this).setTitle("La pianta scannerizzata è: ").setMessage(text).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("FAIL_ON_SC " + e);
                            new MaterialAlertDialogBuilder(GameMap.this).setMessage("Nessuna pianta trovata!").show();
                            // Task failed with an exception
                            // ...
                        }
                    });

        } else  if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");
                if(result.getContents().contains("PreSense.com/")) {
                    //Toast.makeText(this, "Scanned: " + result.getContents().split("PreSense.com/")[1], Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, MainActivity.class);

                    AnimalFigure nearestAnimalSensor = (AnimalFigure)neareastSensor;

                    intent.putExtra("animal", nearestAnimalSensor.getName());
                    intent.putExtra("sensorid", nearestAnimalSensor.getId() + "");
                    intent.putExtra("parkid", parkid + "");
                    if(getIntent().getStringExtra("position") != null) {
                        String position = getIntent().getStringExtra("position");
                        intent.putExtra("position", position);
                    }
                    //intent.putExtra("Qr_code", "andy_dance.sfb");
                    if(nearestAnimalSensor.getArImage().isPresent()) {
                        intent.putExtra("Qr_code", nearestAnimalSensor.getArImage().get());
                        intent.putExtra("rotation", ""+nearestAnimalSensor.getRotation().get());
                    }
                    intent.putExtra("2d_image", nearestAnimalSensor.getImagepath());
                    GlobalVariable.getDatabase_reference().addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot){
                            if(dataSnapshot.child("user_captured_animals/"+ GlobalVariable.getInstance().getUsername()+"/"+nearestAnimalSensor.getImagepath()).exists()) {
                                intent.putExtra("image_owned", "1");
                            }
                            startActivity(intent);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
