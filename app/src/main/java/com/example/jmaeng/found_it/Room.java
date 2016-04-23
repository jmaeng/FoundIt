package com.example.jmaeng.found_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Jmaeng on 4/22/2016.
 */
public class Room {
    private String name;
    private byte[] image;

    public Room(String name, byte[] image) {
        this.name = name;
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public Bitmap getBitmap(){
        Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
        return b;
    }
}
