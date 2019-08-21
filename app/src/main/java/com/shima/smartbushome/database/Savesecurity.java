package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Savesecurity {
    public int _id;
    public int room_id,security_id,password;
    public int subnetID,deviceID;

    public Savesecurity() {
    }

    public Savesecurity(int room_id,int security_id, int subnetID, int deviceID,int password) {
        this.room_id = room_id;
        this.security_id=security_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.password=password;
    }
}
