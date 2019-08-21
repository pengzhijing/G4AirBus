package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/10/13.
 */
public class Savemarcobutton {
    public int _id;
    public int marco_id;
    public String marco_remark,marco_icon;

    public Savemarcobutton() {
    }

    public Savemarcobutton( int marco_id,String marco_remark,String marco_icon) {
        this.marco_id = marco_id;
        this.marco_remark=marco_remark;
        this.marco_icon=marco_icon;
    }
}
