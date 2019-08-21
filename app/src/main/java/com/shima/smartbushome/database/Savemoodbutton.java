package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/8/31.
 */
public class Savemoodbutton {
    public int _id;
    public int room_id,mood_id;
    public String mood_name,mood_icon;

    public Savemoodbutton() {
    }

    public Savemoodbutton(int room_id, int mood_id,String mood_name,String mood_icon) {
        this.room_id = room_id;
        this.mood_id=mood_id;
        this.mood_name=mood_name;
        this.mood_icon=mood_icon;
    }
}
