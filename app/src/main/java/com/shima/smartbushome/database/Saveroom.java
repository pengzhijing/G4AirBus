package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-21.
 */
public class Saveroom {
    public int _id;
    public int room_id;
    public String room_name,room_icon,room_icon_bg;
    public int light;
    public int hvac;
    public int mood;
    public int fan;
    public int curtain;
    public int music,other,media,nio,fh;
    public int area_id;
    public Saveroom() {
    }

    public Saveroom(int room_id, String room_name,int light,int hvac,int mood,int fan,int curtain,int music,
                    String room_icon,String room_icon_bg,int other,int media,int nio,int fh) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.light = light;
        this.hvac=hvac;
        this.mood=mood;
        this.fan=fan;
        this.curtain=curtain;
        this.music=music;
        this.room_icon=room_icon;
        this.room_icon_bg=room_icon_bg;
        this.other=other;
        this.media=media;
        this.nio=nio;
        this.fh=fh;
    }

    public Saveroom(int room_id, String room_name,int light,int hvac,int mood,int fan,int curtain,int music,
                    String room_icon,String room_icon_bg,int other,int media,int nio,int fh,int area_id) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.light = light;
        this.hvac=hvac;
        this.mood=mood;
        this.fan=fan;
        this.curtain=curtain;
        this.music=music;
        this.room_icon=room_icon;
        this.room_icon_bg=room_icon_bg;
        this.other=other;
        this.media=media;
        this.nio=nio;
        this.fh=fh;
        this.area_id=area_id;

    }

}
