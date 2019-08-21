package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-21.
 */
public class Savehvac {
    public int _id;
    public int room_id;
    public int hvac_id;
    public String hvac_remark;
    public int subnetID,deviceID;

    public Savehvac() {
    }

    public Savehvac(int room_id, int hvac_id,int subnetID,int deviceID,String hvac_remark) {
        this.room_id = room_id;
        this.hvac_id=hvac_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.hvac_remark=hvac_remark;
    }
}
