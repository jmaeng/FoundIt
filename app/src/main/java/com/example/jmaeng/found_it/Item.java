package com.example.jmaeng.found_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Item {
    /*
    Item table database to store all items.
    ITEM TABLE      : The table to store information for that item

    ITEM_NAME       : Name of the item
    ITEM_ACCESS     : When the item was last accessed (stored as string)
    ITEM_CREATED    : When the item was created (stored as string)
    ITEM_VIEW_CNT   : Number of times the item was searched
    ITEM_LOCATION   : Where the item is located
    ITEM_X          : X-coordinate of where item is on room face image
    ITEM_Y          : Y-coordinate of where item is on room face image
    ITEM_IMG        : Image of the item
     */
    private String ITEM_NAME;
    private String ITEM_DESC;
    private String ITEM_ACCESS;
    private String ITEM_CREATED;
    private int ITEM_VIEW_CNT;
    private String ITEM_LOCATION;
    private int ITEM_X;
    private int ITEM_Y;
    private byte[] ITEM_IMG;
    private Bitmap ITEM_BITMAP;


    /**
     * Default constructor
     */
    public Item() {
        this.ITEM_NAME = "";
        this.ITEM_DESC = "";
        this.ITEM_ACCESS = "";
        this.ITEM_CREATED = "";
        this.ITEM_VIEW_CNT = 0;
        this.ITEM_LOCATION = "";
        this.ITEM_X = 0;
        this.ITEM_Y = 0;
        this.ITEM_IMG = null;
    }

    /**
     * Full constructor that sets all attributes of item
     * @param name Name of item
     * @param desc Description of item
     * @param access The last time the item was accessed
     * @param created When the item was created
     * @param view_cnt The current view count of the item
     * @param location The location of the item
     * @param x The x-coordinate of where the item is on room face
     * @param y The y-coordinate of where the item is on room face
     * @param image The image of the item
     */
    public Item(String name, String desc, String access, String created, int view_cnt,
                String location, int x, int y, byte[] image) {
        this.ITEM_NAME = name;
        this.ITEM_DESC = desc;
        this.ITEM_ACCESS = access;
        this.ITEM_CREATED = created;
        this.ITEM_VIEW_CNT = view_cnt;
        this.ITEM_LOCATION = location;
        this.ITEM_X = x;
        this.ITEM_Y = y;
        this.ITEM_IMG = image;
    }

    public void set_ITEM_NAME(String name) {
        this.ITEM_NAME = name;
    }

    public String get_ITEM_NAME() {
        return this.ITEM_NAME;
    }

    public void set_ITEM_DESC(String desc) {
        this.ITEM_DESC = desc;
    }

    public String get_ITEM_DESC() {
        return this.ITEM_DESC;
    }

    public void set_ITEM_ACCESS(String access) {
        this.ITEM_ACCESS = access;
    }

    public String get_ITEM_ACCESS() {
        return this.ITEM_ACCESS;
    }

    public void set_ITEM_CREATED(String created) {
        this.ITEM_CREATED = created;
    }

    public String get_ITEM_CREATED() {
        return this.ITEM_CREATED;
    }

    public void set_ITEM_VIEW_CNT(int cnt) {
        this.ITEM_VIEW_CNT = cnt;
    }

    public int get_ITEM_VIEW_CNT() {
        return this.ITEM_VIEW_CNT;
    }

    public void set_ITEM_LOCATION(String location) {
        this.ITEM_LOCATION = location;
    }

    public String get_ITEM_LOCATION() {
        return this.ITEM_LOCATION;
    }

    public void set_ITEM_X(int x) {
        this.ITEM_X = x;
    }

    public int get_ITEM_X() {
        return this.ITEM_X;
    }

    public void set_ITEM_Y(int y) {
        this.ITEM_Y = y;
    }

    public int get_ITEM_Y() {
        return this.ITEM_Y;
    }

    public void set_ITEM_IMG(byte[] image) {
        this.ITEM_IMG = image;
    }

    public byte[] get_ITEM_IMG() {
        return this.ITEM_IMG;
    }

    public void setBitmap(Bitmap b){
        ITEM_BITMAP = b;
        //TODO This is causing errors, but might need to scale bitmap, so fix later when create room activity is ready
       /* float aspectRatio = b.getWidth() / b.getHeight();
        ITEM_BITMAP = Bitmap.createScaledBitmap(b, 480, Math.round((480 / aspectRatio)), false);*/
    }

    public Bitmap getBitmap() {
        return ITEM_BITMAP;
    }
}
