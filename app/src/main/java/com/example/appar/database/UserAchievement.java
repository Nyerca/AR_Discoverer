package com.example.appar.database;

public class UserAchievement {

    private String path;
    private String date;

    public UserAchievement(String path, String date) {
        this.path = path;
        this.date = date;
    }

    public String getPath() {return this.path;}
    public String getDate() {return this.date;}
}
