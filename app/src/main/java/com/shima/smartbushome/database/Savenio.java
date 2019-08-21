package com.shima.smartbushome.database;

/**
 * Created by Administrator on 2016/11/1.
 */
public class Savenio {
    public int _id;
    public int room_id,subnetID,deviceID,nio_id,value1,value2,value3,value4,value5,value6,value7,value8,value9,value10
            ,value11,value12,value13,value14,value15,value16,value17,value18,value19,value20,value21,value22,value23,
            value24,value25,value26,value27;
    public String nio_remark,name1,name2,name3,name4,name5,name6,name7,name8,name9,name10,
    name11,name12,name13,name14,name15;

   /* room_id Integer,subnetID Integer,deviceID Integer,nio_id Integer,nio_remark TEXT," +
            "name1 TEXT,name2 TEXT,name3 TEXT,name4 TEXT,name5 TEXT,name6 TEXT,name7 TEXT,name8 TEXT," +
            "name9 TEXT,name10 TEXT,name11 TEXT,name12 TEXT,name13 TEXT,name14 TEXT,name15 TEXT,value1 Integer," +
            "value2 Integer,value3 Integer,value4 Integer,value5 Integer,value6 Integer,value7 Integer," +
            "value8 Integer,value9 Integer,value10 Integer,value11 Integer,value12 Integer,value13 Integer," +
            "value14 Integer,value15 Integer,value16 Integer,value17 Integer,value18 Integer,value19 Integer," +
            "value20 Integer,value21 Integer,value22 Integer,value23 Integer,value24 Integer,value25 Integer," +
            "value26 Integer,value27 Integer*/
    public Savenio() {
    }

    public Savenio(int room_id, int subnetID, int deviceID, int nio_id, String nio_remark, String name1,String name2
            , String name3,String name4, String name5,String name6, String name7,String name8,String name9,String name10
            , String name11,String name12, String name13,String name14, String name15,int value1,int value2,int value3
            , int value4,int value5,int value6,int value7,int value8,int value9,int value10,int value11,int value12,int value13
            , int value14,int value15,int value16,int value17,int value18,int value19,int value20,int value21,int value22
            , int value23,int value24,int value25,int value26,int value27) {
        this.room_id = room_id;
        this.subnetID = subnetID;
        this.deviceID = deviceID;
        this.nio_id = nio_id;this.nio_remark = nio_remark;this.name1 = name1;this.name2 = name2;
        this.name3 = name3;this.name4 = name4;this.name5 = name5;this.name6 = name6;
        this.name7 = name7;this.name8 = name8;this.name9 = name9;this.name10 = name10;
        this.name11 = name11;this.name12 = name12;this.name13 = name13;this.name14 = name14;
        this.name15 = name15;this.value1 = value1;this.value2 = value2;this.value3 = value3;
        this.value4 = value4;this.value5 = value5;this.value6 = value6;this.value7 = value7;
        this.value8 = value8;this.value9 = value9;this.value10 = value10;this.value11 = value11;
        this.value12 = value12;this.value13 = value13;this.value14 = value14;this.value15 = value15;
        this.value16 = value16;this.value17 = value17;this.value18 = value18;this.value19 = value19;
        this.value20 = value20;this.value21 = value21;this.value22 = value22;this.value23 = value23;
        this.value24 = value24;this.value25 = value25;this.value26 = value26;this.value27 = value27;
    }
}
