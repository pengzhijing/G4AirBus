package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/9/14.
 */
public class SavesecurityArea {
    public int _id;
    public int security_id;
    public int areaNO;
    public String areaName;
    public SavesecurityArea() {
    }

    public SavesecurityArea(int security_id, int areaNO, String areaName) {
        this.security_id = security_id;
        this.areaNO = areaNO;
        this.areaName = areaName;

    }
}
