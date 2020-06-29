package com.example.appar;

public class AnimalFigure {
    private String animal;
    private Double distance;
    private Integer id;

    public AnimalFigure(String animal, Double distance, Integer id) {
        this.animal = animal;
        this.distance = distance;
        this.id = id;
    }

    public String getAnimal() {
        return this.animal;
    }

    public Double getDistance() {
        return this.distance;
    }

    public Integer getId() { return this.id; }
}
