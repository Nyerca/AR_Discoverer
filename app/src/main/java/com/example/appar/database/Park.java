package com.example.appar.database;

public class Park {

    private int id;
    private double lat;
    private double lon;

    public Park(String id, String lat_long) {
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat_long.split(";")[0]);
        this.lon = Double.parseDouble(lat_long.split(";")[1]);
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

    public String toString() {
        return "Id: " + id + " (" + lat + ", " + lon +")";
    }

}
