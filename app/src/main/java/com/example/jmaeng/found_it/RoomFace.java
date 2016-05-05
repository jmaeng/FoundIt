package com.example.jmaeng.found_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Jmaeng on 4/23/2016.
 */
public class RoomFace {

    private String roomName;
    private String roomFace;
    private byte[] image;

    public RoomFace(String room_face, String roomName, byte[] image) {
        this.roomFace = room_face;
        this.roomName = roomName;
        this.image = image;
    }

    public RoomFace(String room_face, byte[] image) {
        this.roomFace = room_face;
        this.roomName = room_face.substring(0, room_face.indexOf('_'));
        this.image = image;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomFace(){
        return roomFace;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public Bitmap getBitmap(){
        Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
        return b;
    }
}
