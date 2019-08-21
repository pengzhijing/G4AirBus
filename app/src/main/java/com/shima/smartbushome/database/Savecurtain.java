package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/8/29.
 */
public class Savecurtain {
    public int _id;
    public int room_id;
    public int subnetID,deviceID,curtain_id,curtain_type,channel_1,channel_2;
    public String curtain_remark,current_state;

    public Savecurtain() {
    }

    public Savecurtain(int room_id, int subnetID,int deviceID,int curtain_id,
                     int curtain_type,int channel_1,int channel_2,String curtain_remark,String current_state) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID=deviceID;
        this.curtain_id=curtain_id;
        this.curtain_type=curtain_type;
        this.channel_1=channel_1;
        this.channel_2=channel_2;
        this.curtain_remark=curtain_remark;
        this.current_state=current_state;
    }
}
