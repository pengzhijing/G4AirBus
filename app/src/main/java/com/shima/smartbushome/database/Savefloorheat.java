package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-21.
 */
public class Savefloorheat {
    public int _id;
    public int room_id;
    public int floorheat_id;
    public String floorheat_remark;
    public int subnetID,deviceID,channel;

    public Savefloorheat() {
    }

    public Savefloorheat(int room_id, int floorheat_id, int subnetID, int deviceID, int channel, String floorheat_remark) {
        this.room_id = room_id;
        this.floorheat_id=floorheat_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.channel=channel;
        this.floorheat_remark=floorheat_remark;
    }
}
