package com.example.jmaeng.found_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
 * Also, this is a singleton class because I want all activities to share the same DB
 *
 */

//TODO INCOMPLETE CLASS
    
public class MainDB {

    private static final String DB_NAME = "mainTestDB.db"; //TODO change this name when the camera and image saving is complete
    private static final int DB_VERSION = 1;
    private static final String ROOMS_TABLE = "rooms_table";
    private static final String ROOM_FACES_TABLE = "room_faces_table";
    private static final String ENTRY_ID = "_id";

    private static final String ENTRY_NAME = "name";
    private static final String ROOM_FACE = "room_face";
    private static final String VIEW_COUNT ="view_count";
    private static final String DATE_CREATED ="date_created";
    private static final String LAST_ACCESSED = "last_accessed";
    private static final String ENTRY_IMAGE = "entry_image";

    //Using SQL to create tables
     /*
    ROOMS TABLE -- will keep track of all the rooms individually -- each name and key should be unique
    name -- name of the room
    entry_image -- thumbnail image of the room -- will actually be image of a face we just chose at
    random to represent the room
     */
    private static final String CREATE_ROOMS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ROOMS_TABLE + "(" + ENTRY_ID + " INTEGER PRIMARY KEY, "
                    + ENTRY_NAME + " TEXT, " + ENTRY_IMAGE + " BLOB" + ") ;";

    /*
    ROOM_FACES_TABLE -- will keep track of all the walls in the room
    name -- name of the room the face/wall belongs to
    room_face -- numerical value of the room wall
    entry_image -- actual image of the room wall
     */
    private static final String CREATE_ROOM_FACES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ROOM_FACES_TABLE + "(" + ENTRY_ID + " INTEGER PRIMARY KEY, "
                    + ENTRY_NAME + " TEXT, " + ROOM_FACE + " INTEGER, " + ENTRY_IMAGE + " BLOB"
                    + ") ;";
    /*
    ITEM TABLE
    Name
    Description
    Last Accessed
    Created
    View Count
    InRoom -- what room it lives in -- room Face
    RoomX - X coord on room face
    RoomY - Y coord
    Picture
     */
    //TODO need to create ITEMS table

    private static final String DROP_ROOMS_TABLE = "DROP TABLE IF EXISTS " + ROOMS_TABLE;
    private static final String DROP_ROOM_FACES_TABLE = "DROP TABLE IF EXISTS " + ROOM_FACES_TABLE;

    /*Inner class that obtains references to database and performs long-running operations of
    * creating and updating the database only when needed and NOT during app startup
    *
        Helper Class that contains all the methods to perform database operations like
        opening a connection, closing connection, insert, update, read, delete, etc.

    */
    private static class DBHelper extends SQLiteOpenHelper {

        private Context mContext;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        /* Called when database is created for the first time and if database doesn't already exist */
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ROOMS_TABLE);
            db.execSQL(CREATE_ROOM_FACES_TABLE);
        }

        @Override
        /* Called when database needs to be updgraded */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_ROOMS_TABLE); // will only drop the table if it already exists
            db.execSQL(DROP_ROOM_FACES_TABLE);
            onCreate(db); //will create new updated table
        }
    }

    private DBHelper dbHelper;
    private SQLiteDatabase sqlDB;

    // Singleton object of this class
    private static MainDB mainDBInstance;

    private MainDB(Context context) {
        dbHelper = new DBHelper(context);
        sqlDB = null;
    }

    public static synchronized MainDB getInstance(Context context) {
        if (mainDBInstance == null) {
            mainDBInstance = new MainDB(context);
        }
        return mainDBInstance;
    }
    // sets up the sqlDB as a readable DB
    protected void openReadableDB() {
        sqlDB = dbHelper.getReadableDatabase();
    }

    // sets up the sqlDB as a writable DB
    protected void openWritableDB() {
        sqlDB = dbHelper.getWritableDatabase();
    }

    //TODO writes to DB through helper when needed
    public void writeToDB(){
        openWritableDB();
        //TODO
        closeDB();
    }

    protected void closeDB() {
        if (sqlDB != null)
            sqlDB.close();
    }

    //Gets all images from the rooms_table, meaning every room paired with one image
    public ArrayList<byte[]> getAllImagesWithName() {
        openReadableDB();
        String[] cols = {ENTRY_NAME, ENTRY_IMAGE};
        Cursor c = sqlDB.query(ROOMS_TABLE, cols, null, null, null, null, null, null);
        ArrayList<byte[]> imageArray = new ArrayList<byte[]>();
        int COL_NAME_INDEX = c.getColumnIndex(ENTRY_NAME);
        int COL_IMAGE_INDEX = c.getColumnIndex(ENTRY_IMAGE);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (!c.isNull(COL_IMAGE_INDEX)) {
                String name = c.getString(COL_NAME_INDEX); //TODO need to add this somewhere
                byte[] bytes = c.getBlob(COL_IMAGE_INDEX);
                //TODO this is not working need to figure out how to save pictures of high quality
                //by saving the picture into internal storage and saving the path of the picture in the DB -- if need be.
                imageArray.add(bytes);
            }
            c.moveToNext();
        }
        c.close();
        closeDB();
        return imageArray;
    }

    public ArrayList<Room> getAllRooms(){
        openReadableDB();
        String[] cols = {ENTRY_NAME, ENTRY_IMAGE};
        Cursor c = sqlDB.query(ROOMS_TABLE, cols, null, null, null, null, null, null);
        ArrayList<Room> roomArray = new ArrayList<Room>();
        int COL_NAME_INDEX = c.getColumnIndex(ENTRY_NAME);
        int COL_IMAGE_INDEX = c.getColumnIndex(ENTRY_IMAGE);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (!c.isNull(COL_IMAGE_INDEX)) {
                String name = c.getString(COL_NAME_INDEX); //TODO need to add this somewhere
                byte[] image = c.getBlob(COL_IMAGE_INDEX);
                //TODO this is not working need to figure out how to save pictures of high quality
                //by saving the picture into internal storage and saving the path of the picture in the DB -- if need be.
                Room room = new Room(name, image);
                roomArray.add(room);
            }
            c.moveToNext();
        }
        c.close();
        closeDB();
        return roomArray;
    }

    public ArrayList<RoomFace> getAllRoomFaces(String roomName) {
        openReadableDB();
        String[] cols = {ROOM_FACE, ENTRY_IMAGE};
        ArrayList<RoomFace> roomFaceArray = new ArrayList<RoomFace>();
        Cursor c = sqlDB.query(ROOM_FACES_TABLE, cols, ENTRY_NAME + " = " + "\'" + roomName + "\'", null, null, null, ROOM_FACE, null);
        int COL_FACE_INDEX = c.getColumnIndex(ROOM_FACE);
        int COL_IMAGE_INDEX = c.getColumnIndex(ENTRY_IMAGE);
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            if (!c.isNull(COL_IMAGE_INDEX)){
                int roomFaceNum = c.getInt(COL_FACE_INDEX);
                byte[] image = c.getBlob(COL_IMAGE_INDEX);
                RoomFace roomFace = new RoomFace(roomFaceNum, image);
                roomFaceArray.add(roomFace);
            }
            c.moveToNext();
        }
        c.close();
        closeDB();
        return roomFaceArray;
    }
}
