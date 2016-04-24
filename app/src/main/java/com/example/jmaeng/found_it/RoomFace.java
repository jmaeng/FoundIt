package com.example.jmaeng.found_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Jmaeng on 4/23/2016.
 */
public class RoomFace {

    private String roomName;
    private int roomFace;
    private byte[] image;

    public RoomFace(int room_face, byte[] image) {
        this.roomFace = room_face;
        this.image = image;
    }

    public int getRoomFace(){
        return roomFace;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public Bitmap getBitmap(){
        Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
        return b;
    }
}
