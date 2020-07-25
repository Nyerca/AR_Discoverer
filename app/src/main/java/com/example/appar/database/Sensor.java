package com.example.appar.database;

public class Sensor {

    private int id;
    private double lat;
    private double lon;
    private String animal;
    private Boolean seen;
    private String imagepath;

    public Sensor(String id, String lat_long, String animal, Boolean seen, String imagepath) {
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat_long.split(";")[0]);
        this.lon = Double.parseDouble(lat_long.split(";")[1]);
        this.animal = animal;
        this.seen = seen;
        this.imagepath = imagepath;
    }

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
