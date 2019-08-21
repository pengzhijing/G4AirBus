package com.shima.smartbushome.database;

//区域实体类
public class SaveArea {
    private int id;
    private String area_name;
    private String area_icon;
    private String area_bg;
    private String area_remark;

    public SaveArea(){}

    public SaveArea(String area_name, String area_icon, String area_bg, String area_remark) {
        this.area_name = area_name;
        this.area_icon = area_icon;
        this.area_bg = area_bg;
        this.area_remark = area_remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getArea_icon() {
        return area_icon;
    }

    public void setArea_icon(String area_icon) {
        this.area_icon = area_icon;
    }

    public String getArea_bg() {
        return area_bg;
    }

    public void setArea_bg(String area_bg) {
        this.area_bg = area_bg;
    }

    public String getArea_remark() {
        return area_remark;
    }

    public void setArea_remark(String area_remark) {
        this.area_remark = area_remark;
    }
}
