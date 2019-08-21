package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/8/22.
 */
public class Saveradio {
    public int room_id,channel_num;
    public String channel_name,channel_value;
    public Saveradio(){

    }

    public Saveradio(int room_id,int channel_num,String channel_name
            ,String channel_value){
        this.room_id=room_id;
        this.channel_num=channel_num;
        this.channel_value=channel_value;
        this.channel_name=channel_name;
    }
}
