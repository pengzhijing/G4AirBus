package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/11/1.
 */
public class Savemedia {
    public int _id;
    public int room_id;
    public int media_id;
    public String media_statement,media_icon;
    public int subnetID,deviceID;

    public Savemedia() {
    }

    public Savemedia(int room_id,int subnetID,int deviceID,int media_id,String media_statement,String media_icon) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.media_id=media_id;
        this.media_statement=media_statement;
        this.media_icon=media_icon;
    }
}
