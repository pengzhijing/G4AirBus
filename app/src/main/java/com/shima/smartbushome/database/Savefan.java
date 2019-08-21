package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Savefan {
    public int _id;
    public int room_id;
    public int subnetID,deviceID,fan_id,channel,fan_Type;
    public String fan_statement,fan_icon;

    public Savefan() {
    }

    public Savefan(int room_id, int subnetID,int deviceID,int fan_id,
                   String fan_statement,int channel,int fan_Type,String fan_icon) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.fan_id=fan_id;
        this.channel=channel;
        this.fan_Type=fan_Type;
        this.fan_statement=fan_statement;
        this.fan_icon=fan_icon;
    }
}
