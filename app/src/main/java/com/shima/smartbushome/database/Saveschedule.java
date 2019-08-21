package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Saveschedule {
    public int schedule_id,marco_ID,repeat_option1,repeat_option2,last_status;
    public String schedule_name,schedule_icon,alarm_time;
    public Saveschedule(){

    }

    public Saveschedule(int schedule_id, String schedule_name, String schedule_icon
            , int marco_ID, String alarm_time, int repeat_option1, int repeat_option2, int last_status){
        this.schedule_id=schedule_id;
        this.schedule_name=schedule_name;
        this.schedule_icon=schedule_icon;
        this.marco_ID=marco_ID;
        this.alarm_time=alarm_time;
        this.repeat_option1=repeat_option1;
        this.repeat_option2=repeat_option2;
        this.last_status=last_status;
    }
}
