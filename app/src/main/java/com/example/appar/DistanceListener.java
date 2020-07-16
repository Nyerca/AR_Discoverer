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

import com.example.appar.database.Park;
import com.example.appar.database.Sensor;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DistanceListener implements LocationListener {

    protected LocationManager locationManager;

    public enum Distance {
        ONE(1),
        TWO(2),
        THREE(3),
        FAR(4);

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

    private GameMap context;
    List<Sensor> list;
    List<Double> positions;
    List<AnimalFigure> animals;

    public DistanceListener(GameMap context, List<Sensor> list) {
        this.context = context;
        this.list = list;
    }

    public class CustomComparator implements Comparator<AnimalFigure> {
        @Override
        public int compare(AnimalFigure animal1, AnimalFigure animal2) {
            return animal1.getDistance().compareTo(animal2.getDistance());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        positions = new ArrayList<Double>();
        animals = new ArrayList<>();

        //Toast.makeText(context, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();
        list.forEach(el -> {
            Double returnDistance = getMeasure(location.getLatitude(), location.getLongitude(), el.getLat(), el.getLon());
            positions.add(returnDistance);
            animals.add(new AnimalFigure(el.getAnimal(), returnDistance, el.getId()));
            Distance step = Distance.getStep(returnDistance);
        });
        Collections.sort(positions);
        positions.stream().limit(9).collect(Collectors.toList());

        Collections.sort(animals, new CustomComparator());
        animals.stream().limit(9).collect(Collectors.toList());

        context.setDistance(animals);

        //Double returnDistance = getMeasure(location.getLatitude(), location.getLongitude(), 44.13086749, 12.22772357);
        //Distance step = Distance.getStep(returnDistance);
    }

    public void getWorldSensorDistance(Double latitude, Double longitude) {
        positions = new ArrayList<Double>();
        animals = new ArrayList<>();

        //Toast.makeText(context, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();
        list.forEach(el -> {
            Double returnDistance = getMeasure(latitude, longitude, el.getLat(), el.getLon());
            positions.add(returnDistance);
            animals.add(new AnimalFigure(el.getAnimal(), returnDistance, el.getId()));
            Distance step = Distance.getStep(returnDistance);
        });
        Collections.sort(positions);
        positions.stream().limit(9).collect(Collectors.toList());

        Collections.sort(animals, new CustomComparator());
        animals.stream().limit(9).collect(Collectors.toList());

        context.setDistance(animals);
    }

    public static Park getNearest(List<Park> parklist, double playerLat, double playerLong) {
        Park nearest_park = null;
        Double nearest_distance = 0.0;
        for (Park park : parklist) {
            Double returnDistance = getMeasure(park.getLat(), park.getLon(), playerLat, playerLong);
            if(nearest_park == null) {
                nearest_park = park;
                nearest_distance = returnDistance;
            }
            else if(returnDistance < nearest_distance) {
                nearest_park = park;
                nearest_distance = returnDistance;
            }

        }
        return nearest_park;
    }

    public static double getMeasure(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
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