package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-21.
 */
public class Savelight {
    public int _id;
    public int room_id;
    public int subnetID,deviceID,light_id,channel,brightvalue,lightType;
    public String light_statement,light_icon;

    public Savelight() {
    }

    public Savelight(int room_id, int subnetID,int deviceID,int light_id,
                     int channel,int brightvalue,int lightType,String light_statement,String light_icon) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.light_id=light_id;
        this.channel=channel;
        this.brightvalue=brightvalue;
        this.lightType=lightType;
        this.light_statement=light_statement;
        this.light_icon=light_icon;
    }
}
