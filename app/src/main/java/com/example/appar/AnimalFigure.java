package com.example.appar;

public class AnimalFigure {
    private String animal;
    private Double distance;
    private Integer id;
    private Boolean seen;
    private String imagepath;

    public AnimalFigure(String animal, Double distance, Integer id, Boolean seen, String imagepath) {
        this.animal = animal;
        this.distance = distance;
        this.id = id;
        this.seen = seen;
        this.imagepath = imagepath;
    }

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
