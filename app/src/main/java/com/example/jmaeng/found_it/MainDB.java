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
 * Android stores your database in private disk space that's associated with the application.
 * Your data is secure, because by default this area is not accessible to other applications
 *
 * This class will server as the outer protective class for the database, so nothing interacts with
 * the database directly.
 *
 * Also, this is a singleton class because I want all activities to share the same DB
 *
 */

public class MainDB {

    private static final String TAG = MainDB.class.getSimpleName();
    private static final int CAROUSEL_LIMIT = 8;

    // Database
    private static final String DB_NAME = "mainTestDB.db"; //TODO change this name when the camera and image saving is complete
    private static final int DB_VERSION = 1;

    // Table Names
    private static final String ROOMS_TABLE = "rooms_table";
    private static final String FACES_TABLE = "faces_table";
    private static final String ITEMS_TABLE = "items_table";

    // Room Column Names
    private static final String ROOM_NAME = "room_name";
    private static final String ROOM_IMG = "room_img";

    // Face Column Names
    private static final String FACE_NAME = "face_name";
    private static final String FACE_ROOM = "face_room";
    private static final String FACE_IMG = "face_img";

    // Item Table Column Names
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_DESC = "item_desc";
    private static final String ITEM_LAST_ACCESS = "item_last_access";
    private static final String ITEM_CREATED = "item_created";
    private static final String ITEM_VIEW_CNT = "item_view_cnt";
    private static final String ITEM_LOCATION = "item_location";
    private static final String ITEM_X = "item_x";
    private static final String ITEM_Y = "item_y";
    private static final String ITEM_IMG = "item_img";


    //Using SQL to create tables
     /**
    ROOMS_TABLE  : Stores all the rooms individually
    room_name    : Name of the room (Primary Key)
    room_img     : Thumbnail image of the room (not the room face/wall)
     */
    private static final String CREATE_ROOMS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ROOMS_TABLE + "("
                    + ROOM_NAME + " TEXT PRIMARY KEY, "
                    + ROOM_IMG + " BLOB"
                    + ") ;";


    /**
    FACES_TABLE  : Stores all room face information for specific rooms
    face_name    : Face/Wall for the associated image. Example: Kitchen_1, Kitchen_2, etc (Primary Key)
    face_room    : Room name associated with the face. Example: Kitchen for Kitchen_1
    face_img     : Image for the room face
     */
    private static final String CREATE_ROOM_FACES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + FACES_TABLE + "("
                    + FACE_NAME + " TEXT PRIMARY KEY, "
                    + FACE_ROOM + " TEXT, "
                    + FACE_IMG + " BLOB"
                    + ") ;";


    /**
    ITEMS_TABLE          : Stores information for all items stored in the database
    item_name           : Name of the stored item (primary key)
    item_desc           : Description of the item
    item_last_accessed  : When the item was last acceseds
    item_created       : When the item was added to the database FORMAT: SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    item_view_count     : Number of times the item was searched
    item_location       : The room where the item is stored in
    item_x              : X-coordinate of where the item is on the image
    item_y              : Y-coordinate of where the item is on the image
    item_img            : Image of the item
     */
    private static final String CREATE_ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ITEMS_TABLE + "("
                    + ITEM_NAME + " TEXT PRIMARY KEY, "
                    + ITEM_DESC + " TEXT, "
                    + ITEM_LAST_ACCESS + " TEXT, "
                    + ITEM_CREATED + " TEXT, "
                    + ITEM_VIEW_CNT + " INTEGER, "
                    + ITEM_LOCATION + " TEXT, "
                    + ITEM_X + " INTEGER, "
                    + ITEM_Y + " INTEGER, "
                    + ITEM_IMG + " BLOB"
                    + ") ;";


    // Drop Table Strings
    private static final String DROP_ROOMS_TABLE = "DROP TABLE IF EXISTS " + ROOMS_TABLE;
    private static final String DROP_ROOM_FACES_TABLE = "DROP TABLE IF EXISTS " + FACES_TABLE;
    private static final String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS " + ITEMS_TABLE;


    /**Inner class that obtains references to database and performs long-running operations of
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
            db.execSQL(CREATE_ITEMS_TABLE);
        }

        @Override
        /* Called when database needs to be upgraded */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_ROOMS_TABLE); // will only drop the table if it already exists
            db.execSQL(DROP_ROOM_FACES_TABLE);
            db.execSQL(DROP_ITEMS_TABLE);
            onCreate(db); //will create new updated table
        }
    }


    // Singleton object of this class
    private static MainDB mainDBInstance;
    // DBHelper variables
    private DBHelper dbHelper;
    private SQLiteDatabase sqlDB;


    /**
     * Called by getInstance() when creating a new instance of MainDB
     */
    private MainDB(Context context) {
        dbHelper = new DBHelper(context);
        sqlDB = null;
    }

    /**
     * Called by anything that needs access to the database.
     * If MainDB is being accessed for the first time, mainDBInstance singleton is created
     * Otherwise, returns the already created singleton MainDB
     */
    public static synchronized MainDB getInstance(Context context) {
        if (mainDBInstance == null) {
            mainDBInstance = new MainDB(context.getApplicationContext());
        }
        return mainDBInstance;
    }


    /**
     * Uses DBHelper to create access to read the database.
     */
    protected void openReadableDB() {
        sqlDB = dbHelper.getReadableDatabase();
    }


    /**
     * Uses DBHelper to create access to write to the database
     */
    protected void openWritableDB() {
        sqlDB = dbHelper.getWritableDatabase();
    }


    /**
     * Closes the database access
     */
    protected void closeDB() {
        if (sqlDB != null)
            sqlDB.close();
    }



    /**
     * Add the passed room to the rooms table.
     * @param room Room to add.
     * @return True if successful, otherwise false.
     */
    public boolean addNewRoomToDB(Room room) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to table
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room.getName());
        cv.put(ROOM_IMG, room.getImage());

        // INSERT INTO rooms_table
        // VALUES (val1, val2, ...)
        long ret = sqlDB.insert(ROOMS_TABLE, null, cv);
        closeDB();
        return ret > 0;
    }

    /**
     * Delete the passed room from the rooms table and room faces table.
     * @param room Room to delete. Using the room name as the primary key to delete with
     * @return True if successful, otherwise false.
     */
    public boolean deleteRoomFromDB(Room room) {
        // Open writable access to database
        openWritableDB();

        // Delete room
        String whereClause = ROOM_NAME + "=?";
        String whereArgs[] = new String[]{(room.getName())};

        // DELETE FROM rooms_table
        // WHERE room_name='room'
        long ret = sqlDB.delete(ROOMS_TABLE, whereClause, whereArgs);

        // Delete failed
        if(ret <= 0)
            return false;

        // Delete all room face entries in faces table; no reason to store faces if room is deleted
        // DELETE FROM faces_table
        // WHERE face_room='room'
        whereClause = FACE_ROOM + "=?";
        ret = sqlDB.delete(FACES_TABLE, whereClause, whereArgs);

        closeDB();
        return ret > 0;
    }

    /**
     * Update rooms table with new image for passed room.
     * Matches with the room name stored in database.
     * However, a name cannot be changed.
     *
     * @param room Room to change.
     * @return True if successful, otherwise false.
     */
    public boolean updateRoomInDB(Room room) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to the database
        ContentValues cv = new ContentValues();
        cv.put(ROOM_IMG, room.getImage());

        // Update item in database
        String whereClause = ROOM_NAME + "=?";
        String whereArgs[] = new String[]{room.getName()};

        // UPDATE rooms_table
        // SET col1=val1, col2=val2, ...
        // WHERE room_name='room'
        long ret = sqlDB.update(ROOMS_TABLE, cv, whereClause, whereArgs);
        closeDB();
        return ret > 0;
    }

    /**
     * Retrieve a single room and the thumbnail image
     * @param room_name The room's name to search for
     * @return Room object that stores all the information or null if room doesn't exist
     */
    public Room getRoomFromDB(String room_name) {  //This should return null if room doesn't exist -- remember to do null checks then -JM
        openReadableDB();
        Room room;

        String query = "SELECT *" +
                " FROM " + ROOMS_TABLE +
                " WHERE " + ROOM_NAME + "=\'" + room_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);

        if(c == null || c.getCount() == 0) {
            closeDB();
            return null;
        }

        int ROOM_NAME_INDEX = c.getColumnIndex(ROOM_NAME);
        int ROOM_IMG_INDEX = c.getColumnIndex(ROOM_IMG);
        // Move to start of query result
        c.moveToFirst();
        room = new Room(c.getString(ROOM_NAME_INDEX), // Kitchen
                c.getBlob(ROOM_IMG_INDEX));           // <image>

        c.close();
        closeDB();
        return room;
    }

    /**
     * Checks to see if a room exists in database.
     * @param room_name Room to search for.
     * @return True if item exists, otherwise false
     */
    public boolean checkRoomInDB(String room_name) {
        boolean roomExists = false;
        openReadableDB();
        String query = "SELECT " + ROOM_NAME +
                " FROM " + ROOMS_TABLE +
                " WHERE " + ROOM_NAME + "=\'" + room_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);
        if (c != null) {
            roomExists = c.getCount() > 0;
            c.close();
        }
        closeDB();
        return roomExists;

    }


    /**
     * Add the passed room face to the faces table.
     * @param face Room face to add.
     * @return True if successful, otherwise false.
     */
    public boolean addNewFaceToDB(RoomFace face) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to table
        ContentValues cv = new ContentValues();
        cv.put(FACE_NAME, face.getRoomFace());
        cv.put(FACE_ROOM, face.getRoomName());
        cv.put(FACE_IMG, face.getImage());

        // INSERT INTO faces_table
        // VALUES (val1, val2, ...)
        long ret = sqlDB.insert(FACES_TABLE, null, cv);
        closeDB();
        return ret > 0;
    }

    /**
     * Delete the passed room face from the faces table.
     * @param face Room face to delete. Using the face name as the primary key to delete with
     * @return True if successful, otherwise false.
     */
    public boolean deleteFaceFromDB(RoomFace face) {
        // Open writable access to database
        openWritableDB();

        // Delete room face
        String whereClause = FACE_NAME + "=?";
        String whereArgs[] = new String[]{(face.getRoomFace())};

        // DELETE FROM faces_table
        // WHERE face_name='face'
        long ret = sqlDB.delete(FACES_TABLE, whereClause, whereArgs);
        closeDB();
        return ret > 0;
    }

    /**
     * Update faces table with new values for passed room.
     * Matches with the face name stored in database.
     * However, a name cannot be changed.
     *
     * @param face Room face to change.
     * @return True if successful, otherwise false.
     */
    public boolean updateFaceInDB(RoomFace face) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to the database
        ContentValues cv = new ContentValues();
        cv.put(FACE_ROOM, face.getRoomFace());
        cv.put(FACE_IMG, face.getImage());

        // Update face in database
        String whereClause = FACE_NAME + "=?";
        String whereArgs[] = new String[]{face.getRoomFace()};

        // UPDATE faces_table
        // SET col1=val1, col2=val2, ...
        // WHERE face_name='face'
        long ret = sqlDB.update(FACES_TABLE, cv, whereClause, whereArgs);
        closeDB();
        return ret > 0;
    }

    /**
     * Retrieve a single face from the table
     * @param face_name The room face's name to search for
     * @return RoomFace object that stores all the information or null if roomface doesn't exist
     */
    public RoomFace getFaceFromDB(String face_name) { //This method is right and should return null -JM
        openReadableDB();
        RoomFace face;

        String query = "SELECT *" +
                " FROM " + FACES_TABLE +
                " WHERE " + FACE_NAME + "=\'" + face_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);
        if(c == null || c.getCount() == 0) {
            closeDB();
            return null;
        }

        int FACE_NAME_INDEX = c.getColumnIndex(FACE_NAME);
        int FACE_ROOM_INDEX = c.getColumnIndex(FACE_ROOM);
        int FACE_IMG_INDEX = c.getColumnIndex(FACE_IMG);
        // Move to start of query result
        c.moveToFirst();
        face = new RoomFace(c.getString(FACE_NAME_INDEX), // kitchen_1
                c.getString(FACE_ROOM_INDEX),             // Kitchen
                c.getBlob(FACE_IMG_INDEX));               // <image>
        c.close();
        closeDB();
        return face;
    }

    /**
     * Checks to see if a room face exists in database.
     * @param face_name Room face to search for.
     * @return True if item exists, otherwise false
     */
    public boolean checkFaceInDB(String face_name) {
        boolean faceExists = false;
        openReadableDB();
        String query = "SELECT " + FACE_NAME +
                " FROM " + FACES_TABLE +
                " WHERE " + FACE_NAME + "=\'" + face_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);
        if (c != null) {
            faceExists = (c.getCount() > 0);
            c.close();
        }

        closeDB();
        return faceExists;
    }


    /**
     * Adds a new item to the Item Table.
     * Item contents is to be filled prior to adding to the DB.
     *
     * @param item Item to add
     * @return True if successful, otherwise false.
     */
    public boolean addNewItemToDB(Item item) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to the database
        ContentValues cv = new ContentValues();
        cv.put(ITEM_NAME, item.get_ITEM_NAME());
        cv.put(ITEM_DESC, item.get_ITEM_DESC());
        cv.put(ITEM_LAST_ACCESS, item.get_ITEM_ACCESS());
        cv.put(ITEM_CREATED, item.get_ITEM_CREATED());
        cv.put(ITEM_VIEW_CNT, item.get_ITEM_VIEW_CNT());
        cv.put(ITEM_LOCATION, item.get_ITEM_LOCATION());
        cv.put(ITEM_X, item.get_ITEM_X());
        cv.put(ITEM_Y, item.get_ITEM_Y());
        cv.put(ITEM_IMG, item.get_ITEM_IMG());

        // INSERT INTO items_table
        // VALUES (val1, val2, ...)
        long ret = sqlDB.insert(ITEMS_TABLE, null, cv);
        closeDB();
        return ret > 0;
    }

    /**
     * Delete the passed item from the database
     * @param item Item to delete. Using the item name as the primary key to delete with
     * @return True if successful, otherwise false.
     */
    public boolean deleteItemFromDB(Item item) {
        // Open writable access to database
        openWritableDB();

        String whereClause = ITEM_NAME + "=?";
        String whereArgs[] = new String[]{item.get_ITEM_NAME()};

        // DELETE FROM items_table
        // WHERE item_name='item'
        long ret = sqlDB.delete(ITEMS_TABLE, whereClause, whereArgs);
        closeDB();
        return ret > 0;
    }

    /**
     * Update items table with new values for passed item.
     * Matches with the item name stored in database.
     * One or more values can be altered at a time. However, a name cannot be changed.
     *
     * @param item Item to change.
     * @return True if successful, otherwise false.
     */
    public boolean updateItemInDB(Item item) {
        // Open writable access to database
        openWritableDB();

        // Create an entity to add to the database
        ContentValues cv = new ContentValues();
        cv.put(ITEM_DESC, item.get_ITEM_DESC());
        cv.put(ITEM_LAST_ACCESS, item.get_ITEM_ACCESS());
        cv.put(ITEM_CREATED, item.get_ITEM_CREATED());
        cv.put(ITEM_VIEW_CNT, item.get_ITEM_VIEW_CNT());
        cv.put(ITEM_LOCATION, item.get_ITEM_LOCATION());
        cv.put(ITEM_X, item.get_ITEM_X());
        cv.put(ITEM_Y, item.get_ITEM_Y());
        cv.put(ITEM_IMG, item.get_ITEM_IMG());

        // Update item in database
        String whereClause = ITEM_NAME + "=?";
        String whereArgs[] = new String[]{item.get_ITEM_NAME()};

        // UPDATE items_table
        // SET col1=val1, col2=val2, etc
        // WHERE item_name='item'
        long ret = sqlDB.update(ITEMS_TABLE, cv, whereClause, whereArgs);
        closeDB();
        return ret > 0;
    }

    /**
     * Retrieve a single item and all of the associated data by matching the item_name
     * @param item_name The item's name to search for
     * @return Item object that stores all the information, or null if item doesn't exist
     */
    public Item getItemFromDB(String item_name) { //This method is right and should return null -JM
        openReadableDB();
        Item item;

        String query = "SELECT *" +
                " FROM " + ITEMS_TABLE +
                " WHERE " + ITEM_NAME + "=\'" + item_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);
        if(c == null || c.getCount() == 0) {
            closeDB();
            return null;
        }

        int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
        int ITEM_DESC_INDEX = c.getColumnIndex(ITEM_DESC);
        int ITEM_LAST_ACCESS_INDEX = c.getColumnIndex(ITEM_LAST_ACCESS);
        int ITEM_CREATED_INDEX = c.getColumnIndex(ITEM_CREATED);
        int ITEM_VIEW_CNT_INDEX = c.getColumnIndex(ITEM_VIEW_CNT);
        int ITEM_LOCATION_INDEX = c.getColumnIndex(ITEM_LOCATION);
        int ITEM_X_INDEX = c.getColumnIndex(ITEM_X);
        int ITEM_Y_INDEX = c.getColumnIndex(ITEM_Y);
        int ITEM_IMG_INDEX = c.getColumnIndex(ITEM_IMG);

        // Move to start of query result
        c.moveToFirst();
        item = new Item(c.getString(ITEM_NAME_INDEX), // Laptop
                c.getString(ITEM_DESC_INDEX),         // Office
                c.getString(ITEM_LAST_ACCESS_INDEX),  // 5/4/2016, 12:00 PM
                c.getString(ITEM_CREATED_INDEX),      // 2/3/2016, 8:00 PM
                c.getInt(ITEM_VIEW_CNT_INDEX),        // 10
                c.getString(ITEM_LOCATION_INDEX),     // Office_2
                c.getInt(ITEM_X_INDEX),               // 10
                c.getInt(ITEM_Y_INDEX),               // 40
                c.getBlob(ITEM_IMG_INDEX));           // <image>

        c.close();
        closeDB();
        return item;
    }

    /**
     * Checks to see if item exists in database.
     * @param item_name Item to search for.
     * @return True if item exists, otherwise false
     */
    public boolean checkItemInDB(String item_name) {
        boolean itemExists = false;
        openReadableDB();
        String query = "SELECT " + ITEM_NAME +
                " FROM " + ITEMS_TABLE +
                " WHERE " + ITEM_NAME + "=\'" + item_name + "\'";
        Cursor c = sqlDB.rawQuery(query, null);
        if (c != null) {
            itemExists = (c.getCount() > 0);
            c.close();
        }

        closeDB();
        return itemExists;
    }



    /**
     * Get all room names in alphabetical order as an ArrayList
     * @return ArrayList of all room names, if query return nothing, then will return empty arraylist
     */
    public ArrayList<String> getAllRoomNames() {
        openReadableDB();
        ArrayList<String> nameArray = new ArrayList<String>();

        // SQL Query Construction
        String query = "SELECT " + ROOM_NAME +
                " FROM " + ROOMS_TABLE +
                " ORDER BY " + ROOM_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        // Query is not empty
        if(c != null) {
            int ROOM_NAME_INDEX = c.getColumnIndex(ROOM_NAME);
            // Gather all rooms
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                if (!c.isNull(ROOM_NAME_INDEX)) {
                    nameArray.add(c.getString(ROOM_NAME_INDEX));
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return nameArray;
    }

    /**
     * Get all stored rooms as an ArrayList of Room objects.
     * @return ArrayList of all rooms, or empty arraylist of rooms if query return nothing
     */
    public ArrayList<Room> getAllRoomImages() {  //DO NOT MODIFY -JM
        openReadableDB();
        ArrayList<Room> roomArray = new ArrayList<Room>();

        // SQL Query Construction
        String query = "SELECT " + ROOM_NAME + ", " + ROOM_IMG +
                " FROM " + ROOMS_TABLE +
                " ORDER BY " + ROOM_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        // Query is not empty
        if(c != null) {
            int ROOM_IMG_INDEX = c.getColumnIndex(ROOM_IMG);
            int ROOM_NAME_INDEX = c.getColumnIndex(ROOM_NAME);
            // Gather all rooms
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                if (!c.isNull(ROOM_IMG_INDEX)) {
                    String name = c.getString(ROOM_NAME_INDEX);
                    byte[] bytes = c.getBlob(ROOM_IMG_INDEX);
                    roomArray.add(new Room(name, bytes));
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return roomArray;
    }

    /**
     * Get all faces of the provided room as an ArrayList of RoomFace objects
     * If query is empty, will return empty arraylist of RoomFaces
     * @param roomName Room to get faces for
     * @return ArrayList of all RoomFace objects related to roomName
     */
    public ArrayList<RoomFace> getRoomFaceImages(String roomName) { //DO NOT MODIFY -JM
        openReadableDB();
        ArrayList<RoomFace> roomFaces = new ArrayList<RoomFace>();

        // SQL Query Construction
        String query = "SELECT " + FACE_NAME + ", " + FACE_IMG +
                " FROM " + FACES_TABLE +
                " WHERE " + FACE_ROOM + "=\'" + roomName + "\' " +
                " ORDER BY " + FACE_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        // Query returned is not empty
        if(c != null) {
            int FACE_IMG_INDEX = c.getColumnIndex(FACE_IMG);
            int FACE_NAME_INDEX = c.getColumnIndex(FACE_NAME);
            // Gather all room faces
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                if (!c.isNull(FACE_IMG_INDEX)) {
                    String roomFaceNum = c.getString(FACE_NAME_INDEX);
                    byte[] image = c.getBlob(FACE_IMG_INDEX);
                    RoomFace roomFace = new RoomFace(roomFaceNum, image);
                    roomFaces.add(roomFace);
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return roomFaces;
    }

    /**
     * Get all item names in alphabetical order as an ArrayList
     * @return ArrayList of all item names
     */
    public ArrayList<String> getAllItemNames() {
        openReadableDB();
        ArrayList<String> nameArray = new ArrayList<String>();

        // SQL Query Construction
        String query = "SELECT " + ITEM_NAME +
                " FROM " + ITEMS_TABLE +
                " ORDER BY " + ITEM_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        // Query was empty
        if(c == null || c.getCount() == 0) {
            closeDB();
            return null;
        }

        int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
        // Gather all rooms
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (!c.isNull(ITEM_NAME_INDEX)) {
                nameArray.add(c.getString(ITEM_NAME_INDEX));
            }
            c.moveToNext();
        }

        c.close();
        closeDB();
        return nameArray;
    }

    /**
     * Get all items with only the item name and image.
     * If database does not have any items, this method will just return an empty array list of items
     * @return ArrayList of Item with all item names and images stored, all other values are null.
     */
    public ArrayList<Item> getAllItemImages(){  //DO NOT MODIFY THIS METHOD -JM
        openReadableDB();
        ArrayList<Item> itemArray = new ArrayList<Item>();

        // SQL Query Construction
        String query = "SELECT " + ITEM_NAME + ", " + ITEM_IMG +
                " FROM " + ITEMS_TABLE +
                " ORDER BY " + ITEM_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        //Query returned results
        if(c != null) {
            // Gather all item images
            Item toAdd;
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                toAdd = new Item();
                int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
                int ITEM_IMAGE_INDEX = c.getColumnIndex(ITEM_IMG);
                if (!c.isNull(ITEM_IMAGE_INDEX)) {
                    String name = c.getString(ITEM_NAME_INDEX);
                    byte[] image = c.getBlob(ITEM_IMAGE_INDEX);
                    toAdd.set_ITEM_NAME(name);
                    toAdd.set_ITEM_IMG(image);
                    itemArray.add(toAdd);
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return itemArray;
    }

    public ArrayList<Item> getMostPopularItemImages() { //DO NOT MODIFY -JM
        openReadableDB();
        ArrayList<Item> mostPopItemArray = new ArrayList<Item>();

        // SQL Query Construction
        String query = "SELECT " + ITEM_NAME + ", " + ITEM_VIEW_CNT + ", " + ITEM_IMG +
                " FROM " + ITEMS_TABLE +
                " ORDER BY " + ITEM_VIEW_CNT + " COLLATE NOCASE DESC";
        Cursor c = sqlDB.rawQuery(query, null);

        //Query returned results
        if(c != null) {
            // Gather the top 8 most popular items
            Item toAdd;
            c.moveToFirst();
            for (int i = 0; i < c.getCount() && i < CAROUSEL_LIMIT; i++) {
                toAdd = new Item();
                int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
                int ITEM_CNT_INDEX = c.getColumnIndex(ITEM_VIEW_CNT);
                int ITEM_IMAGE_INDEX = c.getColumnIndex(ITEM_IMG);
                if (!c.isNull(ITEM_IMAGE_INDEX)) {
                    String name = c.getString(ITEM_NAME_INDEX);
                    byte[] image = c.getBlob(ITEM_IMAGE_INDEX);
                    int viewCount = Integer.parseInt(c.getString(ITEM_CNT_INDEX));
                    toAdd.set_ITEM_NAME(name);
                    toAdd.set_ITEM_IMG(image);
                    toAdd.set_ITEM_VIEW_CNT(viewCount);
                    mostPopItemArray.add(toAdd);
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return mostPopItemArray;
    }

    /**
     * This will return either the arraylist of items of recently added or arraylist of items
     * last viewed depending on which recyclerViewID is given.
     * @return
     */
    public ArrayList<Item> getRecentlyAddedOrLastViewedItemImages(int recyclerViewID) {
        openReadableDB();
        ArrayList<Item> recentlyAddedOrLastViewedItemArray = new ArrayList<Item>();
        String targetField;

        if (recyclerViewID == R.id.recently_added_recycler_carousel_view) {
            targetField = ITEM_CREATED;
        } else {
            //last viewed
            targetField = ITEM_LAST_ACCESS;
        }
        // SQL Query Construction
        String query = "SELECT " + ITEM_NAME + ", " + ITEM_CREATED + ", " + ITEM_IMG +
                " FROM " + ITEMS_TABLE +
                " ORDER BY " + "datetime(" + targetField + ")" + " COLLATE NOCASE DESC";
        Cursor c = sqlDB.rawQuery(query, null);

        //Query returned results
        if(c != null) {
            // Gather the top 8 most recently added items
            Item toAdd;
            c.moveToFirst();
            for (int i = 0; i < c.getCount() && i < CAROUSEL_LIMIT; i++) {
                toAdd = new Item();
                int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
                int ITEM_IMAGE_INDEX = c.getColumnIndex(ITEM_IMG);
                if (!c.isNull(ITEM_IMAGE_INDEX)) {
                    String name = c.getString(ITEM_NAME_INDEX);
                    byte[] image = c.getBlob(ITEM_IMAGE_INDEX);
                    //Log.d(TAG, "***** " + name + " ::: " + itemCreatedDateTime);
                    toAdd.set_ITEM_NAME(name);
                    toAdd.set_ITEM_IMG(image);
                    recentlyAddedOrLastViewedItemArray.add(toAdd);
                }
                c.moveToNext();
            }
            c.close();
        }
        closeDB();
        return recentlyAddedOrLastViewedItemArray;
    }


    /**
     * Get all items with x- and y-coordinates for the item pin.
     * @return ArrayList of Item with all item names, item pin coordinates and images stored,
     * all other values are null.
     */
    public ArrayList<Item> getRoomFaceItemPins(String roomFace){
        openReadableDB();
        ArrayList<Item> itemArray = new ArrayList<Item>();

        // SQL Query Construction
        String query = "SELECT " + ITEM_NAME + ", " + ITEM_X +", " + ITEM_Y +", " + ITEM_IMG +
                " FROM " + ITEMS_TABLE +
                " ORDER BY " + ROOM_NAME + " COLLATE NOCASE ASC";
        Cursor c = sqlDB.rawQuery(query, null);

        // Query returned empty
        if(c == null || c.getCount() == 0) {
            closeDB();
            return null;
        }

        int ITEM_IMG_INDEX = c.getColumnIndex(ITEM_IMG);
        int ITEM_NAME_INDEX = c.getColumnIndex(ITEM_NAME);
        int ITEM_X_INDEX = c.getColumnIndex(ITEM_X);
        int ITEM_Y_INDEX = c.getColumnIndex(ITEM_Y);
        // Gather all room faces
        Item toAdd;
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            toAdd = new Item();
            if (!c.isNull(ITEM_IMG_INDEX)) {
                String name = c.getString(ITEM_NAME_INDEX);
                int x = c.getInt(ITEM_X_INDEX);
                int y = c.getInt(ITEM_Y_INDEX);
                byte[] image = c.getBlob(ITEM_IMG_INDEX);
                toAdd.set_ITEM_NAME(name);
                toAdd.set_ITEM_X(x);
                toAdd.set_ITEM_Y(y);
                toAdd.set_ITEM_IMG(image);
                itemArray.add(toAdd);
            }
            c.moveToNext();
        }
        c.close();
        closeDB();
        return itemArray;
    }
}