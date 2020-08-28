package com.example.appar.database;

public class User {

    private String password;
    private int total;
    private int correct;

    public User(String password, int total, int correct) {
        this.password = password;
        this.total = total;
        this.correct = correct;
    }

    public String getPassword() {return this.password;}
    public Integer getTotal() {return this.total;}
    public Integer getCorrect() {return this.correct;}
}
