package com.example.appar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GlobalVariable {
    private static GlobalVariable globalInstance= null;
    private static DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();

    private String username;
    private DatabaseReference database_reference;

    protected GlobalVariable(String username){
        this.username = username;
        database_reference = FirebaseDatabase.getInstance().getReference();
    }

    public String getUsername() {return this.username;}

    public static synchronized GlobalVariable getInstance() {
        return globalInstance;
    }

    public static synchronized DatabaseReference getDatabase_reference() {
        return db_ref;
    }

    public static synchronized void setInstance(String username) {
        globalInstance = new GlobalVariable(username);
    }
}