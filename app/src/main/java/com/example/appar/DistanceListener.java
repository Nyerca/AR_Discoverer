package com.example.appar;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import com.example.appar.database.AnimalFigure;
import com.example.appar.database.Figure;
import com.example.appar.database.Park;
import com.example.appar.database.PlantFigure;
import com.example.appar.database.Sensor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DistanceListener implements LocationListener {

    public enum Distance {
        ONE(1),
        TWO(2),
        THREE(3),
        FAR(4);

        private int distance;
        Distance(int dist) {
            this.distance = dist;
        }

        public int getDistance() {
            return distance;
        }
        public static Distance getStep(double value) {
            if(value < 15) return ONE;
            else if(value < 45) return TWO;
            else if(value < 75) return THREE;
            else return FAR;
        }
        public String toString() {
            return this.distance + "";
        }
    }

    private GameMap context;
    private List<Sensor> list;
    private List<Double> positions;
    private List<Figure> map_elements;

    public DistanceListener(GameMap context, List<Sensor> list) {
        this.context = context;
        this.list = list;
    }

    public class CustomComparator implements Comparator<Figure> {
        @Override
        public int compare(Figure element1, Figure element2) {
            return element1.getDistance().compareTo(element2.getDistance());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        positions = new ArrayList<>();
        map_elements = new ArrayList<>();

        list.forEach(el -> {
            Double returnDistance = getMeasure(location.getLatitude(), location.getLongitude(), el.getLat(), el.getLon());
            positions.add(returnDistance);
            if(el.getIsPlant()) map_elements.add(new PlantFigure(el.getAnimal(), returnDistance, el.getSeen(), el.getImagepath()));
            else map_elements.add(new AnimalFigure(el.getAnimal(), returnDistance, el.getId(), el.getSeen(), el.getImagepath(), el.getArImage(), el.getRotation()));
            Distance step = Distance.getStep(returnDistance);
        });
        Collections.sort(positions);
        positions.stream().limit(9).collect(Collectors.toList());

        Collections.sort(map_elements, new CustomComparator());
        map_elements.stream().limit(9).collect(Collectors.toList());

        context.setDistance(map_elements);
    }

    public void getWorldSensorDistance(Double latitude, Double longitude) {
        positions = new ArrayList<>();
        map_elements = new ArrayList<>();

        list.forEach(el -> {
            Double returnDistance = getMeasure(latitude, longitude, el.getLat(), el.getLon());
            positions.add(returnDistance);
            map_elements.add(new AnimalFigure(el.getAnimal(), returnDistance, el.getId(), el.getSeen(), el.getImagepath(), el.getArImage(), el.getRotation()));
            Distance step = Distance.getStep(returnDistance);
        });
        Collections.sort(positions);
        positions.stream().limit(9).collect(Collectors.toList());

        Collections.sort(map_elements, new CustomComparator());
        map_elements.stream().limit(9).collect(Collectors.toList());

        context.setDistance(map_elements);
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