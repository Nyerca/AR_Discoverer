package com.example.appar;

import java.util.Optional;

public class AnimalFigure {
    private String animal;
    private Double distance;
    private Integer id;
    private Boolean seen;
    private String imagepath;
    private Optional<String> arImage;
    private Optional<Integer> rotation;

    public AnimalFigure(String animal, Double distance, Integer id, Boolean seen, String imagepath, Optional<String> arImage, Optional<Integer> rotation) {
        this.animal = animal;
        this.distance = distance;
        this.id = id;
        this.seen = seen;
        this.imagepath = imagepath;
        this.arImage = arImage;
        this.rotation = rotation;
    }

    public Optional<Integer> getRotation() {return this.rotation;}

    public Optional<String> getArImage() {return this.arImage;}

    public String getAnimal() {
        return this.animal;
    }

    public Double getDistance() {
        return this.distance;
    }

    public Integer getId() { return this.id; }

    public Boolean getSeen() { return this.seen; }

    public String getImagepath() {return this.imagepath;}
}
