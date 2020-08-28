package com.example.appar.database;

public class PlantFigure implements Figure {
    private String name;
    private Double distance;
    private String imagepath;
    private Boolean seen;

    public PlantFigure(String name, Double distance, Boolean seen, String imagepath) {
        this.name = name;
        this.distance = distance;
        this.seen = seen;
        this.imagepath = imagepath;
    }

    public Boolean getSeen() { return this.seen; }

    public String getName() {
        return this.name;
    }

    public Double getDistance() {
        return this.distance;
    }

    public String getImagepath() {return this.imagepath;}

}
