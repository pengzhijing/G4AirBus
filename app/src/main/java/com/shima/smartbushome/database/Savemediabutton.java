package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/11/1.
 */
public class Savemediabutton {
    public int _id;
    public int room_id;
    public int media_id;
    public int button_num,media_swno,media_controltype,media_type,ifIRmarco;

    public Savemediabutton() {
    }

    public Savemediabutton(int room_id, int media_id, int button_num, int media_swno,
                           int media_controltype, int media_type, int ifIRmarco) {
        this.room_id = room_id;
        this.media_id=media_id;
        this.button_num=button_num;
        this.media_swno=media_swno;
        this.media_controltype=media_controltype;
        this.media_type=media_type;
        this.ifIRmarco=ifIRmarco;
    }
}
