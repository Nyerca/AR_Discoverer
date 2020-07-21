package com.example.appar.database;

public class User {

    public String password;
    public int total;
    public int correct;

    public User(String password, int total, int correct) {
        this.password = password;
        this.total = total;
        this.correct = correct;
    }


}
