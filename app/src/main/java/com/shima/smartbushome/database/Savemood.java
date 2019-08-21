package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-21.
 */
public class Savemood {
    public int _id;
    public int room_id,mood_id;
    public int subnetID,deviceID,control_type,value_1,value_2,
            value_3,value_4,value_5,value_6;

    public Savemood() {
    }

    public Savemood(int room_id, int mood_id,int subnetID,int deviceID,int control_type,
                    int value_1,int value_2,int value_3,int value_4,int value_5,int value_6) {
        this.room_id = room_id;
        this.mood_id=mood_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.control_type=control_type;
        this.value_1=value_1;
        this.value_2=value_2;
        this.value_3=value_3;
        this.value_4=value_4;
        this.value_5=value_5;
        this.value_6=value_6;
    }
}
