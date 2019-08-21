package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/10/13.
 */
public class Savemarco {
    public int _id;
    public int marco_id,room_id,subnetID,deviceID,control_type,value1,value2,value3,sentorder;
    public String room,device;
    public Savemarco() {
    }

    public Savemarco( int marco_id,int room_id,String room,String device,int subnetID,int deviceID,
                      int control_type,int value1,int value2,int value3,int sentorder) {
        this.marco_id = marco_id;
        this.room_id=room_id;
        this.room=room;
        this.device=device;
        this.subnetID=subnetID;
        this.deviceID=deviceID;
        this.control_type=control_type;
        this.value1=value1;
        this.value2=value2;
        this.value3=value3;
        this.sentorder=sentorder;
    }
}
