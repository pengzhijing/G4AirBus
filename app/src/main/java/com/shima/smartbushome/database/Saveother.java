package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Saveother {
    public int _id;
    public int room_id;
    public int subnetID,deviceID,other_id,channel_1,channel_2,other_type;
    public String other_statement,other_icon;

    public Saveother() {
    }

    public Saveother(int room_id, int subnetID, int deviceID, int other_id,
                     int channel_1, int channel_2, String other_statement, String other_icon, int other_type) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.other_id=other_id;
        this.channel_1=channel_1;
        this.channel_2=channel_2;
        this.other_statement=other_statement;
        this.other_icon=other_icon;
        this.other_type=other_type;
    }
}
