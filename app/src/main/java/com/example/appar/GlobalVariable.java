package com.example.appar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GlobalVariable {
    private static GlobalVariable globalInstance= null;
    private static DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();

    private String username;
    private DatabaseReference database_reference;
    private Boolean arsupport = false;

    protected GlobalVariable(String username, Boolean arsupport){
        this.username = username;
        this.arsupport = arsupport;
        database_reference = FirebaseDatabase.getInstance().getReference();
    }

    public String getUsername() {return this.username;}

    public Boolean getArsupport() {return this.arsupport;}

    public static synchronized GlobalVariable getInstance() {
        return globalInstance;
    }

    public static synchronized DatabaseReference getDatabase_reference() {
        return db_ref;
    }

    public static synchronized void setInstance(String username, Boolean arsupport) {
        globalInstance = new GlobalVariable(username, arsupport);
    }
}