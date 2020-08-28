package com.example.appar.database;

public class UserCredibility {

    private int answer;
    private double credibility;

    public UserCredibility(int answer, double credibility) {
        this.answer = answer;
        this.credibility = credibility;
    }

    public Integer getAnswer() {return this.answer;}
    public Double getCredibility() {return this.credibility;}
}