package com.example.appar.database;

import java.util.Optional;

public class Sensor {

    private int id;
    private double lat;
    private double lon;
    private String animal;
    private Boolean seen;
    private String imagepath;
    private Boolean isPlant;
    private Optional<String> arImage;
    private Optional<Integer> rotation;

    public Sensor(String id, String lat_long, String animal, Boolean seen, String imagepath, Boolean isPlant) {
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat_long.split(";")[0]);
        this.lon = Double.parseDouble(lat_long.split(";")[1]);
        this.animal = animal;
        this.seen = seen;
        this.imagepath = imagepath;
        this.isPlant = isPlant;
        arImage = Optional.empty();
        rotation = Optional.empty();
    }

    public Boolean getIsPlant() {return this.isPlant;}

    public void setRotation(int rotation) {
        this.rotation = Optional.of(rotation);
    }

    public Optional<Integer> getRotation() {return this.rotation;}

    public void setArImage(Optional<String> arImage) {
        this.arImage = arImage;
    }

    public Optional<String> getArImage() {return this.arImage;}

    public int getId() {
        return this.id;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public String getAnimal() {
        return this.animal;
    }

    public String toString() {
        return "Id: " + id + " (" + lat + ", " + lon +")";
    }

    public Boolean getSeen() { return this.seen; }

    public String getImagepath() {
        return this.imagepath;
    }
}
