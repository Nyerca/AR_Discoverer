package com.example.appar.database;

import java.util.Optional;

public class AnimalFigure implements Figure {
    private String name;
    private Double distance;
    private Integer id;
    private Boolean seen;
    private String imagepath;
    private Optional<String> arImage;
    private Optional<Integer> rotation;

    public AnimalFigure(String name, Double distance, Integer id, Boolean seen, String imagepath, Optional<String> arImage, Optional<Integer> rotation) {
        this.name = name;
        this.distance = distance;
        this.id = id;
        this.seen = seen;
        this.imagepath = imagepath;
        this.arImage = arImage;
        this.rotation = rotation;
    }

    public Optional<Integer> getRotation() {return this.rotation;}

    public Optional<String> getArImage() {return this.arImage;}

    public String getName() {
        return this.name;
    }

    public Double getDistance() {
        return this.distance;
    }

    public Integer getId() { return this.id; }

    public Boolean getSeen() { return this.seen; }

    public String getImagepath() {return this.imagepath;}
}
