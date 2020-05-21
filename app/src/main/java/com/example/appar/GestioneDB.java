package com.example.appar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GestioneDB {

    static final String KEY_RIGAID = "id";
    static final String KEY_NOME = "nome";
    static final String KEY_INDIRIZZO = "indirizzo";
    static final String TAG = "GestioneDB";
    static final String DATABASE_NOME = "TestDB";
    static final String DATABASE_TABELLA = "clienti";
    static final int DATABASE_VERSIONE = 1;

    static final String DATABASE_CREAZIONE =
            "CREATE TABLE clienti (id integer primary key autoincrement, "
                    + "nome text not null, indirizzo text not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public GestioneDB(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NOME, null, DATABASE_VERSIONE);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREAZIONE);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            Log.w(DatabaseHelper.class.getName(),"Aggiornamento database dalla versione " + oldVersion + " alla "
                    + newVersion + ". I dati esistenti verranno eliminati.");
            db.execSQL("DROP TABLE IF EXISTS clienti");
            onCreate(db);
        }

    }


    public GestioneDB open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }


    public long inserisciCliente(String nome, String indirizzo)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOME, nome);
        initialValues.put(KEY_INDIRIZZO, indirizzo);
        return db.insert(DATABASE_TABELLA, null, initialValues);
    }


    public boolean cancellaCliente(long rigaId)
    {
        return db.delete(DATABASE_TABELLA, KEY_RIGAID + "=" + rigaId, null) > 0;
    }


    public Cursor ottieniTuttiClienti()
    {
        return db.query(DATABASE_TABELLA, new String[] {KEY_RIGAID, KEY_NOME, KEY_INDIRIZZO}, null, null, null, null, null);
    }


    public Cursor ottieniCliente(long rigaId) throws SQLException
    {
        Cursor mCursore = db.query(true, DATABASE_TABELLA, new String[] {KEY_RIGAID, KEY_NOME, KEY_INDIRIZZO}, KEY_RIGAID + "=" + rigaId, null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }


    public boolean aggiornaCliente(long rigaId, String name, String email)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NOME, name);
        args.put(KEY_INDIRIZZO, email);
        return db.update(DATABASE_TABELLA, args, KEY_RIGAID + "=" + rigaId, null) > 0;
    }

}