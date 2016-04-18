package com.example.jmaeng.found_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jmaeng on 4/16/2016.
 *
 * Class of main database to hold all our items and rooms
 * Android stroes your database in private disk space that's associated with the application.
 * Your data is secure, because by default this area is not accessible to other applications
 *
 * This class will server as the outter protective class for the database, so nothing interacts with
 * the database directly.
 *
 */

//TODO INCOMPLETE CLASS
    
public class MainDB {

    private static final String DB_NAME = "main.db";
    private static final int DB_VERSION = 1;
    private static final String ROOMS_TABLE = "rooms_table";
    private static final String ENTRY_ID = "_id";

    private static final String ENTRY_NAME = "name";
    private static final String ENTRY_IMAGE = "image";

    //TODO need to add a items table here and connect the items table to the room table

    //Using SQL to create table
    private static final String CREATE_ROOMS_TABLE =
            "CREATE TABLE" + ROOMS_TABLE + "(" + ENTRY_ID + " INTEGER PRIMARY KEY,"
                    + ENTRY_NAME + " TEXT, " + ENTRY_IMAGE + " BLOB" + ") ;";

    private static final String DROP_ROOMS_TABLE = "DROP TABLE IF EXISTS " + ROOMS_TABLE;

    /*Inner class that obtains references to database and performs long-running operations of
    * creating and updating the database only when needed and NOT during app startup
    */
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        /* Called when database is created for the first time */
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ROOMS_TABLE);
        }

        @Override
        /* Called when database needs to be updgraded */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_ROOMS_TABLE);
            onCreate(db); //TODO is this what we want? To drop the table onUpgrade?
        }
    }


    private DBHelper dbHelper;
    private SQLiteDatabase mainDB;

    public MainDB(Context context) {
        dbHelper = new DBHelper(context);
        mainDB = null;
    }

    // sets up the main db as a readable db
    protected void openReadableDB() {
        mainDB = dbHelper.getReadableDatabase();
    }

    // sets up the main db as a writable db
    protected void openWritableDB() {
        mainDB = dbHelper.getWritableDatabase();
    }

    protected void closeDB() {
        if (mainDB != null)
            mainDB.close();
    }
}
