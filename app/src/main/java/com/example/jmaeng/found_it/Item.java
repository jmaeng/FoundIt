package com.example.jmaeng.found_it;

import android.graphics.Bitmap;

/**
 * Created by Jmaeng on 5/8/2016.
 */
public class Item {

    private Bitmap image;

    public Item(Bitmap image) { //TODO fix the sizing of image
        float aspectRatio = image.getWidth() / image.getHeight();
        this.image = Bitmap.createScaledBitmap(image, 480, Math.round((480 / aspectRatio)), false);
    }

    public Bitmap getBitmap() {
        return image;
    }

}
