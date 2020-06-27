package com.example.appar;

public class AnimalFigure {
    private String animal;
    private Double distance;

    public AnimalFigure(String animal, Double distance) {
        this.animal = animal;
        this.distance = distance;
    }

    public String getAnimal() {
        return this.animal;
    }

    public Double getDistance() {
        return this.distance;
    }
}
