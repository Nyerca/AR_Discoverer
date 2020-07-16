package com.example.appar;

public class GlobalVariable {
    private static GlobalVariable globalInstance= null;

    private String username;

    protected GlobalVariable(String username){this.username = username;}

    public String getUsername() {return this.username;}

    public static synchronized GlobalVariable getInstance() {
        return globalInstance;
    }

    public static synchronized void setInstance(String username) {
        globalInstance = new GlobalVariable(username);
    }
}