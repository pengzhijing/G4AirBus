package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Savestatus {
    public int status_id,subnetID,deviceID,type,channel,unit;
    public String name,status_icon;
    public Savestatus(){

    }

    public Savestatus(int status_id, String name, int subnetID
            , int deviceID, int type, int channel,String status_icon,int unit){
        this.status_id=status_id;
        this.name=name;
        this.subnetID=subnetID;
        this.deviceID=deviceID;
        this.type=type;
        this.channel=channel;
        this.status_icon=status_icon;
        this.unit=unit;
    }
}
