package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-3.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shima.smartbushome.MainActivity;

//http://blog.csdn.net/liuhe688/article/details/6715983/
public class DBManager {

    private static String TAG="DBManager";
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        DatabaseContext dbContext = new DatabaseContext(context);
        //初始化创建数据库
        dbContext.getDatabasePath("");
        helper = new DBHelper(dbContext);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        //----------------------------数据库字段的增删改查---------------------------
        //检查song表字段中是否存在music_id
       boolean isSongHaveMusicId= checkColumnExists(db,"song","music_id");
       //如果song表字段中不存在music_id
       if (!isSongHaveMusicId){
           //添加字段
           String addSongMusicIdSql="alter table song add column music_id integer default 1";
           db.execSQL(addSongMusicIdSql);
       }

        //检查music表字段中是否存在music_remark
        boolean isMusicHaveRemark= checkColumnExists(db,"music","music_remark");
        //如果music表字段中不存在music_remark
        if (!isMusicHaveRemark){
            //添加字段
            String addMusicRemarkSql="alter table music add column music_remark text default ' '";
            db.execSQL(addMusicRemarkSql);
        }


        //检查room表字段中是否存在area_id
        boolean isRoomHaveAreaId= checkColumnExists(db,"room","area_id");
        //如果room表字段中不存在area_id
        if (!isRoomHaveAreaId){
            //添加字段 作用是标记房间属于哪个区域
            String addMusicRemarkSql="alter table room add column area_id integer default 0";
            db.execSQL(addMusicRemarkSql);
        }

        //检查room表字段中是否存在is_area
//        boolean isRoomHaveArea= checkColumnExists(db,"room","is_area");
//        //如果room表字段中不存在is_area
//        if (!isRoomHaveAreaId){
//            //添加字段  作用是标记是否为区域
//            String addMusicRemarkSql="alter table room add column is_area integer default 0";
//            db.execSQL(addMusicRemarkSql);
//        }

        //创建区域表
        String createAreaTableStr="CREATE TABLE IF NOT EXISTS area" + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,area_name TEXT,area_icon TEXT,area_bg TEXT,area_remark TEXT)";
        db.execSQL(createAreaTableStr);



    }


    /**
     * 检查表中某列是否存在
     * @param db
     * @param tableName 表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExists(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
           e.printStackTrace();
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }


    /**
     *
     *
     *
     *
     *
     *
     *
     *
     * 添加数据函数
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    /**
     * add persons
     * @param tips
     */
    public void addroom(List<Saveroom> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Saveroom tip : tips) {
                    db.execSQL("INSERT INTO room VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)", new Object[]{tip.room_id,
                            tip.room_name, tip.light,tip.hvac,tip.mood,tip.fan,tip.curtain,tip.music,tip.room_icon,
                            tip.room_icon_bg,tip.other,tip.media,tip.nio,tip.fh,tip.area_id});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }
    }
    public void addlight(List<Savelight> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Savelight tip : tips) {
                    db.execSQL("INSERT INTO light VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?,?)", new Object[]{tip.room_id, tip.subnetID, tip.deviceID,tip.light_id,tip.light_statement
                            ,tip.channel,tip.brightvalue,tip.lightType,tip.light_icon});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }
    public void addhvac(List<Savehvac> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Savehvac tip : tips) {
                    db.execSQL("INSERT INTO hvac VALUES(null, ?, ?, ?, ?, ?)", new Object[]{tip.room_id,tip.hvac_id, tip.subnetID, tip.deviceID,tip.hvac_remark});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addfloorheat(List<Savefloorheat> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Savefloorheat tip : tips) {
                    db.execSQL("INSERT INTO floorheat VALUES(null, ?, ?, ?,?, ?, ?)", new Object[]{tip.room_id,tip.floorheat_id, tip.subnetID, tip.deviceID, tip.channel,tip.floorheat_remark});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }


    public void addmood(Savemood tip) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO mood VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)", new Object[]{tip.room_id,tip.mood_id,tip.subnetID, tip.deviceID,tip.control_type, tip.value_1,tip.value_2,tip.value_3,tip.value_4,tip.value_5,tip.value_6});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addmoodbutton(Savemoodbutton tip) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO moodbutton VALUES(null, ?, ?, ?, ?)", new Object[]{tip.room_id,tip.mood_id,tip.mood_name,tip.mood_icon});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addmusic(List<Savemusic> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Savemusic tip : tips) {
                    db.execSQL("INSERT INTO music VALUES(null, ?, ?, ?, ?,?)", new Object[]{tip.room_id, tip.music_id,tip.subnetID, tip.deviceID,tip.music_remark});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addsong(Savesong tip) {
        db.beginTransaction();  //开始事务
        try {

                db.execSQL("INSERT INTO song VALUES(null, ?, ?, ?, ?, ?, ?,?)", new Object[]{tip.room_id,tip.album_num,tip.album_name,tip.song_num,tip.song_name,tip.like,tip.music_id});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addradio(Saveradio tip) {
        db.beginTransaction();  //开始事务
        try {

            db.execSQL("INSERT INTO radio VALUES(null, ?, ?, ?, ?)", new Object[]{tip.room_id,tip.channel_num,tip.channel_name,tip.channel_value});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    //添加区域
    public void addArea(SaveArea area) {
        db.beginTransaction();  //开始事务
        try {

            db.execSQL("INSERT INTO area VALUES(null, ?, ?, ?, ?)", new Object[]{area.getArea_name(),area.getArea_icon(),area.getArea_bg(),area.getArea_remark()});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addcurtain(List<Savecurtain> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Savecurtain tip : tips) {
                    db.execSQL("INSERT INTO curtain VALUES(null, ?, ?, ?, ?,?, ?, ?, ?,?)", new Object[]{tip.room_id, tip.curtain_id, tip.subnetID, tip.deviceID
                            , tip.curtain_remark, tip.curtain_type, tip.channel_1, tip.channel_2,tip.current_state});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }
    public void addother(List<Saveother> tips) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                for (Saveother tip : tips) {
                    db.execSQL("INSERT INTO other VALUES(null, ?, ?, ?, ?, ?, ?, ?,?,?)", new Object[]{tip.room_id, tip.subnetID, tip.deviceID,
                            tip.other_id,tip.other_statement,tip.channel_1,tip.channel_2,tip.other_icon,tip.other_type});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addmarco(Savemarco tip) {
        db.beginTransaction();  //开始事务
        try {

            db.execSQL("INSERT INTO marco VALUES(null, ?,?,?,?,?,?,?,?,?,?,?)", new Object[]{tip.marco_id,tip.room_id,tip.room,tip.device,
                    tip.subnetID,tip.deviceID,tip.control_type,tip.value1,tip.value2,tip.value3,tip.sentorder});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addmarcobutton(Savemarcobutton tip) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO marcobutton VALUES(null, ?,?,?)", new Object[]{tip.marco_id,tip.marco_remark,tip.marco_icon});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addfan(Savefan tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO fan VALUES(null, ?,?,?,?,?,?,?,?)", new Object[]{tip.room_id,tip.subnetID,tip.deviceID,
                        tip.fan_id,tip.fan_statement,tip.channel,tip.fan_Type,tip.fan_icon});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addmedia(Savemedia tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO media VALUES(null, ?,?,?,?,?,?)", new Object[]{tip.room_id,tip.subnetID,tip.deviceID,
                        tip.media_id,tip.media_statement,tip.media_icon});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addmediabutton(List<Savemediabutton> tips) {
        db.beginTransaction();  //开始事务
        try {
            for (Savemediabutton tip : tips) {
                db.execSQL("INSERT INTO mediabutton VALUES(null, ?,?,?,?,?,?,?)", new Object[]{tip.room_id,tip.media_id,tip.button_num,
                        tip.media_swno,tip.media_controltype,tip.media_type,tip.ifIRmarco});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addsecurity(Savesecurity tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO security VALUES(null, ?,?,?,?,?)", new Object[]{tip.room_id,
                        tip.security_id,tip.subnetID,tip.deviceID,tip.password});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }
    public void addsecurityarea(SavesecurityArea tip) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO securityarea VALUES(null, ?,?,?)", new Object[]{tip.security_id,tip.areaNO,tip.areaName});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addenergy(Saveenergy tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO energy VALUES(null, ?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{tip.energy_id,tip.subnetID,tip.deviceID,
                        tip.energyname,tip.channel1_name, tip.channel2_name,tip.channel3_name,tip.channel4_name,
                        tip.channel5_name,tip.channel6_name,tip.channel7_name,tip.channel8_name,tip.channel9_name,
                        tip.channel10_name,tip.channel11_name,tip.channel12_name,tip.channel13_name,tip.channel14_name,
                        tip.channel15_name,tip.channel16_name,tip.channel17_name,tip.channel18_name,tip.channel19_name,
                        tip.channel20_name,tip.channel21_name,tip.channel22_name,tip.channel23_name,tip.channel24_name,});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }
    public void addenergydata(Saveenergydata tip) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO energydata VALUES(null,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{tip.energy_id,tip.channel1_value,
                    tip.channel2_value,tip.channel3_value,tip.channel4_value,
                    tip.channel5_value,tip.channel6_value,tip.channel7_value,
                    tip.channel8_value,tip.channel9_value,tip.channel10_value,
                    tip.channel11_value,tip.channel12_value,tip.channel13_value,
                    tip.channel14_value,tip.channel15_value,tip.channel16_value,
                    tip.channel17_value,tip.channel18_value,tip.channel19_value,
                    tip.channel20_value,tip.channel21_value,tip.channel22_value,
                    tip.channel23_value,tip.channel24_value,tip.Time});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addnio(Savenio tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO nio VALUES(null,?,?,?,?,? ," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{tip.room_id,tip.subnetID,
                        tip.deviceID,tip.nio_id,tip.nio_remark,
                        tip.name1,tip.name2,tip.name3, tip.name4,tip.name5,tip.name6,
                        tip.name7,tip.name8,tip.name9, tip.name10,tip.name11,tip.name12,
                        tip.name13,tip.name14,tip.name15, tip.value1,tip.value2,tip.value3,
                        tip.value4,tip.value5,tip.value6,tip.value7,tip.value8,tip.value9,
                        tip.value10,tip.value11,tip.value12,tip.value13,tip.value14,tip.value15,
                        tip.value16,tip.value17,tip.value18,tip.value19,tip.value20,tip.value21,
                        tip.value22,tip.value23,tip.value24,tip.value25,tip.value26,tip.value27});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addstatus(Savestatus tip) {
        if(!MainActivity.islockchangeid){
            db.beginTransaction();  //开始事务
            try {
                db.execSQL("INSERT INTO status VALUES(null, ?,?,?,?,?,?,?,?)", new Object[]{tip.status_id,tip.name,tip.subnetID,tip.deviceID
                        ,tip.type,tip.channel,tip.status_icon,tip.unit});
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }

    }

    public void addschedule(Saveschedule tip){
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO schedule VALUES(null, ?,?,?,?, ?,?,?,?)", new Object[]{tip.schedule_id,tip.schedule_name,
                    tip.schedule_icon,tip.marco_ID,tip.alarm_time,tip.repeat_option1,tip.repeat_option2,tip.last_status});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void addnfc(Savenfc tip){
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO nfc VALUES(null, ?,?,?,?, ?,?,?,? ,?,?,?,? ,?)", new Object[]{tip.nfc_id,
                    tip.nfc_name, tip.nfc_icon,tip.nfc_content,tip.state,tip.action_type,tip.marco_ID,
                    tip.marco_name,tip.call_num,tip.message,tip.resume1,tip.resume2,tip.delaytime});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     * 更新信息函数
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */


    //修改区域
    public void updateArea(SaveArea area) {
        ContentValues cv = new ContentValues();
        cv.put("area_name",area.getArea_name());
        cv.put("area_icon",area.getArea_icon());
        cv.put("area_bg",area.getArea_bg());
        cv.put("area_remark",area.getArea_remark());
        db.update("area", cv, "_id = ?", new String[]{String.valueOf(area.getId())});
    }


    public void updataroominfo(Saveroom tip){
        ContentValues cv = new ContentValues();
        cv.put("room_name",tip.room_name);
        cv.put("room_icon",tip.room_icon);
        cv.put("room_icon_bg",tip.room_icon_bg);
        cv.put("area_id",tip.area_id);
        db.update("room", cv, "room_id = ?", new String[]{String.valueOf(tip.room_id)});
    }

    public void updateroom(Saveroom tips,String colvalue) {
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            if(colvalue.equals("light")){
                cv.put(colvalue, tips.light);
            }else if(colvalue.equals("hvac")){
                cv.put(colvalue, tips.hvac);
            }else if(colvalue.equals("mood")){
                cv.put(colvalue, tips.mood);
            }else if(colvalue.equals("fan")){
                cv.put(colvalue, tips.fan);
            }else if(colvalue.equals("curtain")){
                cv.put(colvalue, tips.curtain);
            }else if(colvalue.equals("music")){
                cv.put(colvalue, tips.music);
            }else if(colvalue.equals("other")){
                cv.put(colvalue,tips.other);
            }else if(colvalue.equals("media")){
                cv.put(colvalue,tips.media);
            }else if(colvalue.equals("9 in 1")){
                cv.put("nio",tips.nio);
            }else if(colvalue.equals("floor heat")){
                cv.put("fh",tips.fh);
            }
            db.update("room", cv, "room_id = ?", new String[]{String.valueOf(tips.room_id)});
        }

    }

    public void updatelight(Savelight tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("channel",tips.channel);
            cv.put("light_statement",tips.light_statement);
            cv.put("light_icon",tips.light_icon);
            cv.put("lightType",tips.lightType);
            db.update("light", cv, "light_id = ? and room_id = ?", new String[]{String.valueOf(tips.light_id),String.valueOf(tips.room_id)});

        }
     }

    public void updatehvac(Savehvac tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("hvac_statement",tips.hvac_remark);
            db.update("hvac", cv, "hvac_id = ? and room_id = ?", new String[]{String.valueOf(tips.hvac_id),String.valueOf(tips.room_id)});

        }
       }

    public void updatefloorheat(Savefloorheat tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("channel",tips.channel);
            cv.put("floorheat_statement",tips.floorheat_remark);
            db.update("floorheat", cv, "_id = ? and room_id = ?", new String[]{String.valueOf(tips._id),String.valueOf(tips.room_id)});

        }
    }

    public void updatemoodbutton(Savemoodbutton tips){
        ContentValues cv = new ContentValues();
        cv.put("mood_name",tips.mood_name);
        cv.put("mood_icon",tips.mood_icon);
        db.update("moodbutton", cv, "mood_id = ? and room_id = ?", new String[]{String.valueOf(tips.mood_id),String.valueOf(tips.room_id)});
    }

    public void updatemusic(Savemusic tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("music_id",tips.music_id);
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("music_remark",tips.music_remark);
            db.update("music", cv, "music_id = ? and room_id = ?", new String[]{String.valueOf(tips.music_id),String.valueOf(tips.room_id)});

        }
      }

    public void updatesong(Savesong tips){
        ContentValues cv = new ContentValues();
        cv.put("like",tips.like);
        db.update("song", cv, "album_num = ? and room_id = ? and song_num= ?", new String[]{String.valueOf(tips.album_num),String.valueOf(tips.room_id),String.valueOf(tips.song_num)});
    }

    public void updateradio(Saveradio tips){
        ContentValues cv = new ContentValues();
        cv.put("channel_value",tips.channel_value);
        cv.put("channel_name",tips.channel_name);
        db.update("radio", cv, "channel_num = ? and room_id = ?", new String[]{String.valueOf(tips.channel_num),String.valueOf(tips.room_id)});
    }

    public void updatecurtain(Savecurtain tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("channel_1",tips.channel_1);
            cv.put("channel_2",tips.channel_2);
            cv.put("curtain_remark",tips.curtain_remark);
            db.update("curtain", cv, "curtain_id = ? and room_id = ?", new String[]{String.valueOf(tips.curtain_id),String.valueOf(tips.room_id)});

        }
        }

    public void updatecurtainstate(Savecurtain tips){
        ContentValues cv = new ContentValues();
        cv.put("current_state",tips.current_state);
        db.update("curtain", cv, "curtain_id = ? and room_id = ?", new String[]{String.valueOf(tips.curtain_id),String.valueOf(tips.room_id)});
    }

    public void updateother(Saveother tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("channel_1",tips.channel_1);
            cv.put("channel_2",tips.channel_2);
            cv.put("other_statement",tips.other_statement);
            cv.put("other_icon",tips.other_icon);
            db.update("other", cv, "other_id = ? and room_id = ?", new String[]{String.valueOf(tips.other_id),String.valueOf(tips.room_id)});

        }
        }

    public void updatemarco(Savemarco tips,int neworder){
        ContentValues cv = new ContentValues();
        cv.put("sentorder",neworder);
        db.update("marco", cv, "marco_id = ? and sentorder = ?", new String[]{String.valueOf(tips.marco_id),String.valueOf(tips.sentorder)});
    }

    public void updatemarcobutton(Savemarcobutton tips){
        ContentValues cv = new ContentValues();
        cv.put("marco_remark",tips.marco_remark);
        cv.put("marco_icon",tips.marco_icon);
        db.update("marcobutton", cv, "marco_id = ? ", new String[]{String.valueOf(tips.marco_id)});
    }

    public void updatefan(Savefan tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("channel",tips.channel);
            cv.put("fan_statement",tips.fan_statement);
            db.update("fan", cv, "fan_id = ? and room_id = ?", new String[]{String.valueOf(tips.fan_id),String.valueOf(tips.room_id)});

        }
        }

    public void updatemedia(Savemedia tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("media_statement",tips.media_statement);
            cv.put("media_icon",tips.media_icon);
            db.update("media", cv, "media_id = ? and room_id = ? ",
                    new String[]{String.valueOf(tips.media_id),String.valueOf(tips.room_id)});

        }
         }

    public void updatemediabutton(Savemediabutton tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("media_swno",tips.media_swno);
            cv.put("media_controltype",tips.media_controltype);
            cv.put("ifIRmarco",tips.ifIRmarco);
            db.update("mediabutton", cv, "media_id = ? and room_id = ? and button_num=?",
                    new String[]{String.valueOf(tips.media_id), String.valueOf(tips.room_id), String.valueOf(tips.button_num)});

        }
        }

    public void updatesecurity(Savesecurity tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("password",tips.password);
            db.update("security", cv, " room_id = ? and security_id=?",
                    new String[]{String.valueOf(0),String.valueOf(0)});

        }
         }
    public void updatesecurityarea(SavesecurityArea tips){
        ContentValues cv = new ContentValues();
        cv.put("areaName",tips.areaName);
        db.update("securityarea", cv, " security_id=? and areaNO=?",
                new String[]{String.valueOf(0),String.valueOf(tips.areaNO)});
    }
    public void updateenergy(Saveenergy tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("energyname",tips.energyname);
            db.update("energy", cv, " energy_id=? ", new String[]{String.valueOf(tips.energy_id)});

        }
         }
    public void updateenergychannelname(int energy_id,String channel,String name){
        ContentValues cv = new ContentValues();
        cv.put(channel,name);
        db.update("energy", cv, " energy_id=? ", new String[]{String.valueOf(energy_id)});
    }
    public void updateniosetting(Savenio tips){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("subnetID",tips.subnetID);
            cv.put("deviceID",tips.deviceID);
            cv.put("nio_remark",tips.nio_remark);
            db.update("nio", cv, "room_id = ? and nio_id=? ",  new String[]{String.valueOf(tips.room_id),String.valueOf(tips.nio_id)});

        }
        }
    public void updateniovalue(int roomid,int nioid,String name,int value,int num){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            switch (num){
                case 1:cv.put("name1",name);cv.put("value1",value);break;
                case 2:cv.put("name2",name);cv.put("value2",value);break;
                case 3:cv.put("name3",name);cv.put("value3",value);break;
                case 4:cv.put("name4",name);cv.put("value4",value);break;
                case 5:cv.put("name5",name);cv.put("value5",value);break;
                case 6:cv.put("name6",name);cv.put("value6",value);break;
                case 7:cv.put("name7",name);cv.put("value7",value);break;
                case 8:cv.put("name8",name);cv.put("value8",value);break;
                case 9:cv.put("name9",name);cv.put("value9",value);break;
                case 10:cv.put("name10",name);cv.put("value10",value);break;
                case 11:cv.put("name11",name);cv.put("value11",value);break;
                case 12:cv.put("name12",name);cv.put("value12",value);break;
                case 13:cv.put("name13",name);cv.put("value13",value);break;
                case 14:cv.put("name14",name);cv.put("value14",value);break;
                case 15:cv.put("name15",name);cv.put("value15",value);break;
                case 16:cv.put("value16",value);break;
                case 17:cv.put("value17",value);break;
                case 18:cv.put("value18",value);break;
                case 19:cv.put("value19",value);break;
                case 20:cv.put("value20",value);break;
                case 21:cv.put("value21",value);break;
                case 22:cv.put("value22",value);break;
                case 23:cv.put("value23",value);break;
                case 24:cv.put("value24",value);break;
                case 25:cv.put("value25",value);break;
                case 26:cv.put("value26",value);break;
                case 27:cv.put("value27",value);break;
            }
            db.update("nio", cv, " room_id = ? and nio_id=?  ", new String[]{String.valueOf(roomid),String.valueOf(nioid)});

        }
        }

    public void updatestatus(Savestatus tip){
        if(!MainActivity.islockchangeid){
            ContentValues cv = new ContentValues();
            cv.put("name",tip.name);
            cv.put("subnetID",tip.subnetID);
            cv.put("deviceID",tip.deviceID);
            cv.put("status_icon",tip.status_icon);
            cv.put("channel",tip.channel);
            cv.put("unit",tip.unit);
            db.update("status", cv, " status_id=? ", new String[]{String.valueOf(tip.status_id)});

        }
         }

    public void updateschedule(Saveschedule tip){
        ContentValues cv = new ContentValues();
        cv.put("schedule_name",tip.schedule_name);
        cv.put("schedule_icon",tip.schedule_icon);
        cv.put("marco_ID",tip.marco_ID);
        cv.put("alarm_time",tip.alarm_time);
        cv.put("last_status",tip.last_status);
        cv.put("repeat_option1",tip.repeat_option1);
        cv.put("repeat_option2",tip.repeat_option2);
        db.update("schedule", cv, " schedule_id=? ", new String[]{String.valueOf(tip.schedule_id)});
    }

    public void updateschedule_status(Saveschedule tip){
        ContentValues cv = new ContentValues();
        cv.put("last_status",tip.last_status);
        db.update("schedule", cv, " schedule_id=? ", new String[]{String.valueOf(tip.schedule_id)});
    }

    public void updatenfc(Savenfc tip){
        ContentValues cv = new ContentValues();
        cv.put("nfc_name",tip.nfc_name);
        cv.put("action_type",tip.action_type);
        cv.put("marco_ID",tip.marco_ID);
        cv.put("marco_name",tip.marco_name);
        cv.put("call_num",tip.call_num);
        cv.put("message",tip.message);
        cv.put("delaytime",tip.delaytime);
        db.update("nfc", cv, " nfc_id=? ", new String[]{String.valueOf(tip.nfc_id)});
    }
    public void updatenfc_status(Savenfc tip){
        ContentValues cv = new ContentValues();
        cv.put("state",tip.state);
        db.update("nfc", cv, " nfc_id=? ", new String[]{String.valueOf(tip.nfc_id)});
    }
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     * 删除函数
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    /**
     * delete old person
     *
     */


    //删除区域
    public void deleteArea(int area_id) {

        if(!MainActivity.islockchangeid){
            db.delete("area", "_id = ?", new String[]{String.valueOf(area_id)});
        }

    }


    public void deletefounction(String table,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "room_id = ?", new String[]{String.valueOf(roomid)});
        }

    }

    public void deletelight(String table,int lightid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "light_id = ? and room_id = ?", new String[]{String.valueOf(lightid),String.valueOf(roomid)});

        }
     }
    public void deletefc(String table,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "room_id = ?", new String[]{String.valueOf(roomid)});

        }
     }

    public void deletehvac(String table,int hvacid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "hvac_id = ? and room_id = ?", new String[]{String.valueOf(hvacid),String.valueOf(roomid)});

        }
    }

    public void deletefloorheat(String table,int _id,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "_id = ? and room_id = ?", new String[]{String.valueOf(_id),String.valueOf(roomid)});

        }
    }


    public void deletemood(String table,int moodid,int roomid){
        db.delete(table, "mood_id = ? and room_id = ?", new String[]{String.valueOf(moodid),String.valueOf(roomid)});
    }

    public void deletemoodbutton(String table,int moodid,int roomid){
        db.delete(table, "mood_id = ? and room_id = ?", new String[]{String.valueOf(moodid),String.valueOf(roomid)});
    }

    public void deletemusic(String table,int musicid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "music_id = ? and room_id = ?", new String[]{String.valueOf(musicid),String.valueOf(roomid)});

        }
    }

    public void deletesong(String table,int roomid){

            db.delete(table, "room_id = ?", new String[]{String.valueOf(roomid)});


      }

    public void deletesong(String table,int roomid,int musicid){

        db.delete(table, "room_id = ? and music_id = ?", new String[]{String.valueOf(roomid),String.valueOf(musicid)});


    }

    public void deleteradio(String table,int roomid){
        db.delete(table, "room_id = ?", new String[]{String.valueOf(roomid)});
    }
    public void deletecurtain(String table,int curtainid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "curtain_id = ? and room_id = ?", new String[]{String.valueOf(curtainid),String.valueOf(roomid)});

        }
    }

    public void deleteother(String table,int onoffid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "other_id = ? and room_id = ?", new String[]{String.valueOf(onoffid),String.valueOf(roomid)});

        }
     }

    public void deletemarco(String table,int marcoid,int sentorder){
        db.delete(table, "marco_id = ? and sentorder = ?", new String[]{String.valueOf(marcoid),String.valueOf(sentorder)});
    }

    public void deletemarcobutton(String table,int marcoid){
        db.delete(table, "marco_id = ? ", new String[]{String.valueOf(marcoid)});
    }
    public void deletefan(String table,int fanid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "fan_id = ? and room_id = ?", new String[]{String.valueOf(fanid),String.valueOf(roomid)});

        }
     }

    public void deletemedia(String table,int mediaid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "media_id = ? and room_id = ?", new String[]{String.valueOf(mediaid),String.valueOf(roomid)});

        }
     }
    public void deletemediabutton(String table,int mediaid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "media_id = ? and room_id = ?", new String[]{String.valueOf(mediaid), String.valueOf(roomid)});

        }
     }
    public void deletesecurityarea(String table){
        db.delete(table, "security_id = ? ", new String[]{String.valueOf(0)});
    }
    public void deleteenergy(String table,Saveenergy tip){
        if(!MainActivity.islockchangeid){
            db.delete(table, "energy_id = ? ", new String[]{String.valueOf(tip.energy_id)});

        }
    }
    public void deleteenergydata(String table,Saveenergydata tip){
        db.delete(table, "energy_id = ? ", new String[]{String.valueOf(tip.energy_id)});
    }
    public void deletenio(String table,int nioid,int roomid){
        if(!MainActivity.islockchangeid){
            db.delete(table, "nio_id = ? and room_id = ?",new String[]{String.valueOf(nioid),String.valueOf(roomid)});

        }
    }
    public void deletestatus(String table,int status_id){
        if(!MainActivity.islockchangeid){
            db.delete(table, "status_id = ? ", new String[]{String.valueOf(status_id)});

        }
    }

    public void deleteschedule(int schedule_id){
        db.delete("schedule", "schedule_id = ? ", new String[]{String.valueOf(schedule_id)});
    }
    public void deletenfc(int nfc_id){
        db.delete("nfc", "nfc_id = ? ", new String[]{String.valueOf(nfc_id)});
    }
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     * 查询函数
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<SaveTips> query() {
        ArrayList<SaveTips> persons = new ArrayList<SaveTips>();
        Cursor c = queryTheCursor("tip");
        while (c.moveToNext()) {
            SaveTips person = new SaveTips();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.title = c.getString(c.getColumnIndex("title"));
            person.date = c.getString(c.getColumnIndex("date"));
            person.detail = c.getString(c.getColumnIndex("detail"));
            person.url = c.getString(c.getColumnIndex("url"));
            persons.add(person);
        }
        c.close();
        return persons;
    }


    //查询区域
    public List<SaveArea> queryArea() {
        ArrayList<SaveArea> persons = new ArrayList<SaveArea>();
        Cursor c = queryTheCursor("area");
        while (c.moveToNext()) {
            SaveArea area = new SaveArea();
            area.setId(c.getInt(c.getColumnIndex("_id")));
            area.setArea_name(c.getString(c.getColumnIndex("area_name")));
            area.setArea_icon(c.getString(c.getColumnIndex("area_icon")));
            area.setArea_bg(c.getString(c.getColumnIndex("area_bg")));
            area.setArea_remark(c.getString(c.getColumnIndex("area_remark")));

            persons.add(area);
        }
        c.close();
        return persons;
    }



    public List<Savelight> querylight() {
        ArrayList<Savelight> persons = new ArrayList<Savelight>();
        Cursor c = queryTheCursor("light");
        while (c.moveToNext()) {
            Savelight person = new Savelight();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.light_id = c.getInt(c.getColumnIndex("light_id"));
            person.channel = c.getInt(c.getColumnIndex("channel"));
            person.brightvalue = c.getInt(c.getColumnIndex("brightvalue"));
            person.lightType = c.getInt(c.getColumnIndex("lightType"));
            person.light_statement = c.getString(c.getColumnIndex("light_statement"));
            person.light_icon=c.getString(c.getColumnIndex("light_icon"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savehvac> queryhvac() {
        ArrayList<Savehvac> persons = new ArrayList<Savehvac>();
        Cursor c = queryTheCursor("hvac");
        while (c.moveToNext()) {
            Savehvac person = new Savehvac();
            person._id= c.getInt(c.getColumnIndex("_id"));
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.hvac_id = c.getInt(c.getColumnIndex("hvac_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.hvac_remark = c.getString(c.getColumnIndex("hvac_statement"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savefloorheat> queryfloorheat() {
        ArrayList<Savefloorheat> persons = new ArrayList<Savefloorheat>();
        Cursor c = queryTheCursor("floorheat");
        while (c.moveToNext()) {
            Savefloorheat person = new Savefloorheat();
            person._id= c.getInt(c.getColumnIndex("_id"));
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.floorheat_id = c.getInt(c.getColumnIndex("floorheat_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.channel = c.getInt(c.getColumnIndex("channel"));
            person.floorheat_remark = c.getString(c.getColumnIndex("floorheat_statement"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemood> querymood() {
        ArrayList<Savemood> persons = new ArrayList<Savemood>();
        Cursor c = queryTheCursor("mood");
        while (c.moveToNext()) {
            Savemood person = new Savemood();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.mood_id=c.getInt(c.getColumnIndex("mood_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.control_type=c.getInt(c.getColumnIndex("control_type"));
            person.value_1=c.getInt(c.getColumnIndex("value_1"));
            person.value_2=c.getInt(c.getColumnIndex("value_2"));
            person.value_3=c.getInt(c.getColumnIndex("value_3"));
            person.value_4=c.getInt(c.getColumnIndex("value_4"));
            person.value_5=c.getInt(c.getColumnIndex("value_5"));
            person.value_6=c.getInt(c.getColumnIndex("value_6"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemoodbutton> querymoodbutton() {
        ArrayList<Savemoodbutton> persons = new ArrayList<Savemoodbutton>();
        Cursor c = queryTheCursor("moodbutton");
        while (c.moveToNext()) {
            Savemoodbutton person = new Savemoodbutton();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.mood_id=c.getInt(c.getColumnIndex("mood_id"));
            person.mood_name=c.getString(c.getColumnIndex("mood_name"));
            person.mood_icon=c.getString(c.getColumnIndex("mood_icon"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemusic> querymusic() {
        ArrayList<Savemusic> persons = new ArrayList<Savemusic>();
        Cursor c = queryTheCursor("music");
        while (c.moveToNext()) {
            Savemusic person = new Savemusic();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.music_id=c.getInt(c.getColumnIndex("music_id"));
            person.music_remark=c.getString(c.getColumnIndex("music_remark"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savesong> querysong() {
        ArrayList<Savesong> persons = new ArrayList<Savesong>();
        Cursor c = queryTheCursor("song");
        while (c.moveToNext()) {
            Savesong person = new Savesong();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.album_num=c.getInt(c.getColumnIndex("album_num"));
            person.album_name = c.getString(c.getColumnIndex("album_name"));
            person.song_num = c.getInt(c.getColumnIndex("song_num"));
            person.song_name = c.getString(c.getColumnIndex("song_name"));
            person.like=c.getInt(c.getColumnIndex("like"));
            person.music_id=c.getInt(c.getColumnIndex("music_id"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Saveradio> queryradio() {
        ArrayList<Saveradio> persons = new ArrayList<Saveradio>();
        Cursor c = queryTheCursor("radio");
        while (c.moveToNext()) {
            Saveradio person = new Saveradio();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.channel_num=c.getInt(c.getColumnIndex("channel_num"));
            person.channel_name = c.getString(c.getColumnIndex("channel_name"));
            person.channel_value = c.getString(c.getColumnIndex("channel_value"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savecurtain> querycurtain() {
        ArrayList<Savecurtain> persons = new ArrayList<Savecurtain>();
        Cursor c = queryTheCursor("curtain");
        while (c.moveToNext()) {
            Savecurtain person = new Savecurtain();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.curtain_id = c.getInt(c.getColumnIndex("curtain_id"));
            person.curtain_remark = c.getString(c.getColumnIndex("curtain_remark"));
            person.curtain_type = c.getInt(c.getColumnIndex("curtain_type"));
            person.channel_1 = c.getInt(c.getColumnIndex("channel_1"));
            person.channel_2 = c.getInt(c.getColumnIndex("channel_2"));
            person.current_state=c.getString(c.getColumnIndex("current_state"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Saveroom> queryroom() {
        ArrayList<Saveroom> persons = new ArrayList<Saveroom>();
        Cursor c = queryTheCursor("room");
        while (c.moveToNext()) {
            Saveroom person = new Saveroom();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.room_name = c.getString(c.getColumnIndex("room_name"));
            person.light = c.getInt(c.getColumnIndex("light"));
            person.hvac = c.getInt(c.getColumnIndex("hvac"));
            person.mood = c.getInt(c.getColumnIndex("mood"));
            person.curtain = c.getInt(c.getColumnIndex("curtain"));
            person.music = c.getInt(c.getColumnIndex("music"));
            person.other=c.getInt(c.getColumnIndex("other"));
            person.media=c.getInt(c.getColumnIndex("media"));
            person.room_icon = c.getString(c.getColumnIndex("room_icon"));
            person.room_icon_bg=c.getString(c.getColumnIndex("room_icon_bg"));
            person.area_id=c.getInt(c.getColumnIndex("area_id"));
            persons.add(person);

        }
        c.close();
        return persons;
    }

    public List<Saveroom> queryRoomByAreaId(int area_id) {
        ArrayList<Saveroom> persons = new ArrayList<Saveroom>();
        Cursor c = queryTheCursor("room");
        while (c.moveToNext()) {
            Saveroom person = new Saveroom();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.room_name = c.getString(c.getColumnIndex("room_name"));
            person.light = c.getInt(c.getColumnIndex("light"));
            person.hvac = c.getInt(c.getColumnIndex("hvac"));
            person.mood = c.getInt(c.getColumnIndex("mood"));
            person.curtain = c.getInt(c.getColumnIndex("curtain"));
            person.music = c.getInt(c.getColumnIndex("music"));
            person.other=c.getInt(c.getColumnIndex("other"));
            person.media=c.getInt(c.getColumnIndex("media"));
            person.room_icon = c.getString(c.getColumnIndex("room_icon"));
            person.room_icon_bg=c.getString(c.getColumnIndex("room_icon_bg"));
            person.area_id=c.getInt(c.getColumnIndex("area_id"));
            if (area_id==person.area_id){
                persons.add(person);
            }
        }
        c.close();
        return persons;
    }

    public List<Saveother> queryother() {
        ArrayList<Saveother> persons = new ArrayList<Saveother>();
        Cursor c = queryTheCursor("other");
        while (c.moveToNext()) {
            Saveother person = new Saveother();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.other_id = c.getInt(c.getColumnIndex("other_id"));
            person.channel_1 = c.getInt(c.getColumnIndex("channel_1"));
            person.channel_2 = c.getInt(c.getColumnIndex("channel_2"));
            person.other_statement = c.getString(c.getColumnIndex("other_statement"));
            person.other_icon=c.getString(c.getColumnIndex("other_icon"));
            person.other_type=c.getInt(c.getColumnIndex("other_type"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemarcobutton> querymarcobutton() {
        ArrayList<Savemarcobutton> persons = new ArrayList<Savemarcobutton>();
        Cursor c = queryTheCursor("marcobutton");
        while (c.moveToNext()) {
            Savemarcobutton person = new Savemarcobutton();
            person.marco_id = c.getInt(c.getColumnIndex("marco_id"));
            person.marco_icon = c.getString(c.getColumnIndex("marco_icon"));
            person.marco_remark = c.getString(c.getColumnIndex("marco_remark"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemarco> querymarco() {
        ArrayList<Savemarco> persons = new ArrayList<Savemarco>();
        Cursor c = queryTheCursor("marco");
        while (c.moveToNext()) {
            Savemarco person = new Savemarco();
            person.marco_id = c.getInt(c.getColumnIndex("marco_id"));
            person.room_id= c.getInt(c.getColumnIndex("room_id"));
            person.room = c.getString(c.getColumnIndex("room"));
            person.device = c.getString(c.getColumnIndex("device"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.control_type = c.getInt(c.getColumnIndex("control_type"));
            person.value1 = c.getInt(c.getColumnIndex("value1"));
            person.value2=c.getInt(c.getColumnIndex("value2"));
            person.value3=c.getInt(c.getColumnIndex("value3"));
            person.sentorder=c.getInt(c.getColumnIndex("sentorder"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savefan> queryfan() {
        ArrayList<Savefan> persons = new ArrayList<Savefan>();
        Cursor c = queryTheCursor("fan");
        while (c.moveToNext()) {
            Savefan person = new Savefan();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.fan_id = c.getInt(c.getColumnIndex("fan_id"));
            person.fan_statement = c.getString(c.getColumnIndex("fan_statement"));
            person.channel = c.getInt(c.getColumnIndex("channel"));
            person.fan_Type = c.getInt(c.getColumnIndex("fan_Type"));
            person.fan_icon = c.getString(c.getColumnIndex("fan_icon"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemedia> querymedia() {
        ArrayList<Savemedia> persons = new ArrayList<Savemedia>();
        Cursor c = queryTheCursor("media");
        while (c.moveToNext()) {
            Savemedia person = new Savemedia();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.media_id = c.getInt(c.getColumnIndex("media_id"));
            person.media_statement = c.getString(c.getColumnIndex("media_statement"));
            person.media_icon = c.getString(c.getColumnIndex("media_icon"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savemediabutton> querymediabutton() {
        ArrayList<Savemediabutton> persons = new ArrayList<Savemediabutton>();
        Cursor c = queryTheCursor("mediabutton");
        while (c.moveToNext()) {
            Savemediabutton person = new Savemediabutton();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.media_id = c.getInt(c.getColumnIndex("media_id"));
            person.button_num = c.getInt(c.getColumnIndex("button_num"));
            person.media_swno = c.getInt(c.getColumnIndex("media_swno"));
            person.media_controltype = c.getInt(c.getColumnIndex("media_controltype"));
            person.media_type = c.getInt(c.getColumnIndex("media_type"));
            person.ifIRmarco = c.getInt(c.getColumnIndex("ifIRmarco"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savesecurity> querysecurity() {
        ArrayList<Savesecurity> persons = new ArrayList<Savesecurity>();
        Cursor c = queryTheCursor("security");
        while (c.moveToNext()) {
            Savesecurity person = new Savesecurity();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.security_id = c.getInt(c.getColumnIndex("security_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.password = c.getInt(c.getColumnIndex("password"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<SavesecurityArea> querysecurityarea() {
        ArrayList<SavesecurityArea> persons = new ArrayList<SavesecurityArea>();
        Cursor c = queryTheCursor("securityarea");
        while (c.moveToNext()) {
            SavesecurityArea person = new SavesecurityArea();
            person.security_id = c.getInt(c.getColumnIndex("security_id"));
            person.areaNO = c.getInt(c.getColumnIndex("areaNO"));
            person.areaName = c.getString(c.getColumnIndex("areaName"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Saveenergy> queryenergy() {
        ArrayList<Saveenergy> persons = new ArrayList<Saveenergy>();
        Cursor c = queryTheCursor("energy");
        while (c.moveToNext()) {
            Saveenergy person = new Saveenergy();
            person.energy_id = c.getInt(c.getColumnIndex("energy_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.energyname = c.getString(c.getColumnIndex("energyname"));
            person.channel1_name = c.getString(c.getColumnIndex("channel1_name"));
            person.channel2_name = c.getString(c.getColumnIndex("channel2_name"));
            person.channel3_name = c.getString(c.getColumnIndex("channel3_name"));
            person.channel4_name = c.getString(c.getColumnIndex("channel4_name"));
            person.channel5_name = c.getString(c.getColumnIndex("channel5_name"));
            person.channel6_name = c.getString(c.getColumnIndex("channel6_name"));
            person.channel7_name = c.getString(c.getColumnIndex("channel7_name"));
            person.channel8_name = c.getString(c.getColumnIndex("channel8_name"));
            person.channel9_name = c.getString(c.getColumnIndex("channel9_name"));
            person.channel10_name = c.getString(c.getColumnIndex("channel10_name"));
            person.channel11_name = c.getString(c.getColumnIndex("channel11_name"));
            person.channel12_name = c.getString(c.getColumnIndex("channel12_name"));
            person.channel13_name = c.getString(c.getColumnIndex("channel13_name"));
            person.channel14_name = c.getString(c.getColumnIndex("channel14_name"));
            person.channel15_name = c.getString(c.getColumnIndex("channel15_name"));
            person.channel16_name = c.getString(c.getColumnIndex("channel16_name"));
            person.channel17_name = c.getString(c.getColumnIndex("channel17_name"));
            person.channel18_name = c.getString(c.getColumnIndex("channel18_name"));
            person.channel19_name = c.getString(c.getColumnIndex("channel19_name"));
            person.channel20_name = c.getString(c.getColumnIndex("channel20_name"));
            person.channel21_name = c.getString(c.getColumnIndex("channel21_name"));
            person.channel22_name = c.getString(c.getColumnIndex("channel22_name"));
            person.channel23_name = c.getString(c.getColumnIndex("channel23_name"));
            person.channel24_name = c.getString(c.getColumnIndex("channel24_name"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Saveenergydata> queryenergydata() {
        ArrayList<Saveenergydata> persons = new ArrayList<Saveenergydata>();
        Cursor c = queryTheCursor("energydata");
        while (c.moveToNext()) {
            Saveenergydata person = new Saveenergydata();
            person.energy_id = c.getInt(c.getColumnIndex("energy_id"));
            person.channel1_value = c.getInt(c.getColumnIndex("channel1_value"));
            person.channel2_value = c.getInt(c.getColumnIndex("channel2_value"));
            person.channel3_value = c.getInt(c.getColumnIndex("channel3_value"));
            person.channel4_value = c.getInt(c.getColumnIndex("channel4_value"));
            person.channel5_value = c.getInt(c.getColumnIndex("channel5_value"));
            person.channel6_value = c.getInt(c.getColumnIndex("channel6_value"));
            person.channel7_value = c.getInt(c.getColumnIndex("channel7_value"));
            person.channel8_value = c.getInt(c.getColumnIndex("channel8_value"));
            person.channel9_value = c.getInt(c.getColumnIndex("channel9_value"));
            person.channel10_value = c.getInt(c.getColumnIndex("channel10_value"));
            person.channel11_value = c.getInt(c.getColumnIndex("channel11_value"));
            person.channel12_value = c.getInt(c.getColumnIndex("channel12_value"));
            person.channel13_value = c.getInt(c.getColumnIndex("channel13_value"));
            person.channel14_value = c.getInt(c.getColumnIndex("channel14_value"));
            person.channel15_value = c.getInt(c.getColumnIndex("channel15_value"));
            person.channel16_value = c.getInt(c.getColumnIndex("channel16_value"));
            person.channel17_value = c.getInt(c.getColumnIndex("channel17_value"));
            person.channel18_value = c.getInt(c.getColumnIndex("channel18_value"));
            person.channel19_value = c.getInt(c.getColumnIndex("channel19_value"));
            person.channel20_value = c.getInt(c.getColumnIndex("channel20_value"));
            person.channel21_value = c.getInt(c.getColumnIndex("channel21_value"));
            person.channel22_value = c.getInt(c.getColumnIndex("channel22_value"));
            person.channel23_value = c.getInt(c.getColumnIndex("channel23_value"));
            person.channel24_value = c.getInt(c.getColumnIndex("channel24_value"));
            person.Time = c.getString(c.getColumnIndex("Time"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savenio> querynio() {
        ArrayList<Savenio> persons = new ArrayList<Savenio>();
        Cursor c = queryTheCursor("nio");
        while (c.moveToNext()) {
            Savenio person = new Savenio();
            person.room_id = c.getInt(c.getColumnIndex("room_id"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.nio_id = c.getInt(c.getColumnIndex("nio_id"));
            person.nio_remark = c.getString(c.getColumnIndex("nio_remark"));
            person.name1 = c.getString(c.getColumnIndex("name1"));
            person.name2 = c.getString(c.getColumnIndex("name2"));
            person.name3 = c.getString(c.getColumnIndex("name3"));
            person.name4 = c.getString(c.getColumnIndex("name4"));
            person.name5 = c.getString(c.getColumnIndex("name5"));
            person.name6 = c.getString(c.getColumnIndex("name6"));
            person.name7 = c.getString(c.getColumnIndex("name7"));
            person.name8 = c.getString(c.getColumnIndex("name8"));
            person.name9 = c.getString(c.getColumnIndex("name9"));
            person.name10 = c.getString(c.getColumnIndex("name10"));
            person.name11 = c.getString(c.getColumnIndex("name11"));
            person.name12 = c.getString(c.getColumnIndex("name12"));
            person.name13 = c.getString(c.getColumnIndex("name13"));
            person.name14 = c.getString(c.getColumnIndex("name14"));
            person.name15 = c.getString(c.getColumnIndex("name15"));
            person.value1 = c.getInt(c.getColumnIndex("value1"));
            person.value2 = c.getInt(c.getColumnIndex("value2"));
            person.value3 = c.getInt(c.getColumnIndex("value3"));
            person.value4 = c.getInt(c.getColumnIndex("value4"));
            person.value5 = c.getInt(c.getColumnIndex("value5"));
            person.value6 = c.getInt(c.getColumnIndex("value6"));
            person.value7 = c.getInt(c.getColumnIndex("value7"));
            person.value8 = c.getInt(c.getColumnIndex("value8"));
            person.value9 = c.getInt(c.getColumnIndex("value9"));
            person.value10 = c.getInt(c.getColumnIndex("value10"));

            person.value11 = c.getInt(c.getColumnIndex("value11"));
            person.value12 = c.getInt(c.getColumnIndex("value12"));
            person.value13 = c.getInt(c.getColumnIndex("value13"));
            person.value14 = c.getInt(c.getColumnIndex("value14"));
            person.value15 = c.getInt(c.getColumnIndex("value15"));
            person.value16 = c.getInt(c.getColumnIndex("value16"));
            person.value17 = c.getInt(c.getColumnIndex("value17"));
            person.value18 = c.getInt(c.getColumnIndex("value18"));
            person.value19 = c.getInt(c.getColumnIndex("value19"));
            person.value20 = c.getInt(c.getColumnIndex("value20"));

            person.value21 = c.getInt(c.getColumnIndex("value21"));
            person.value22 = c.getInt(c.getColumnIndex("value22"));
            person.value23 = c.getInt(c.getColumnIndex("value23"));
            person.value24 = c.getInt(c.getColumnIndex("value24"));
            person.value25 = c.getInt(c.getColumnIndex("value25"));
            person.value26 = c.getInt(c.getColumnIndex("value26"));
            person.value27 = c.getInt(c.getColumnIndex("value27"));

            persons.add(person);
        }
        c.close();
        return persons;
    }


    public List<Savestatus> querystatus() {
        ArrayList<Savestatus> persons = new ArrayList<Savestatus>();
        Cursor c = queryTheCursor("status");
        while (c.moveToNext()) {
            Savestatus person = new Savestatus();
            person.status_id = c.getInt(c.getColumnIndex("status_id"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.subnetID = c.getInt(c.getColumnIndex("subnetID"));
            person.deviceID = c.getInt(c.getColumnIndex("deviceID"));
            person.type = c.getInt(c.getColumnIndex("type"));
            person.channel = c.getInt(c.getColumnIndex("channel"));
            person.status_icon = c.getString(c.getColumnIndex("status_icon"));
            person.unit = c.getInt(c.getColumnIndex("unit"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Saveschedule> queryschedule() {
        ArrayList<Saveschedule> persons = new ArrayList<Saveschedule>();
        Cursor c = queryTheCursor("schedule");
        while (c.moveToNext()) {
            Saveschedule person = new Saveschedule();
            person.schedule_id = c.getInt(c.getColumnIndex("schedule_id"));
            person.schedule_name = c.getString(c.getColumnIndex("schedule_name"));
            person.schedule_icon = c.getString(c.getColumnIndex("schedule_icon"));
            person.marco_ID = c.getInt(c.getColumnIndex("marco_ID"));
            person.alarm_time = c.getString(c.getColumnIndex("alarm_time"));
            person.repeat_option1 = c.getInt(c.getColumnIndex("repeat_option1"));
            person.repeat_option2 = c.getInt(c.getColumnIndex("repeat_option2"));
            person.last_status = c.getInt(c.getColumnIndex("last_status"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public List<Savenfc> querynfc() {
        ArrayList<Savenfc> persons = new ArrayList<Savenfc>();
        Cursor c = queryTheCursor("nfc");
        while (c.moveToNext()) {
            Savenfc person = new Savenfc();
            person.nfc_id = c.getInt(c.getColumnIndex("nfc_id"));
            person.nfc_name = c.getString(c.getColumnIndex("nfc_name"));
            person.nfc_icon = c.getString(c.getColumnIndex("nfc_icon"));
            person.nfc_content = c.getString(c.getColumnIndex("nfc_content"));
            person.state = c.getInt(c.getColumnIndex("state"));
            person.action_type = c.getInt(c.getColumnIndex("action_type"));
            person.marco_ID = c.getInt(c.getColumnIndex("marco_ID"));
            person.marco_name = c.getString(c.getColumnIndex("marco_name"));
            person.call_num = c.getString(c.getColumnIndex("call_num"));
            person.message = c.getString(c.getColumnIndex("message"));
            person.resume1 = c.getInt(c.getColumnIndex("resume1"));
            person.resume2 = c.getString(c.getColumnIndex("resume2"));
            person.delaytime = c.getInt(c.getColumnIndex("delaytime"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * 获取某一列的所有的值的list
     *获取room表的某一列的内容
     */
    public List<String> querylist(String data) {
        ArrayList<String> persons = new ArrayList<String>();
        Cursor c = queryTheCursor("room");
        while (c.moveToNext()) {
            Saveroom person = new Saveroom();
            person._id = c.getInt(c.getColumnIndex("_id"));
            if(data.equals("room_id")){
                person.room_id = c.getInt(c.getColumnIndex("room_id"));
                persons.add(String.valueOf(person.room_id));
            }else if(data.equals("room_name")){
                person.room_name = c.getString(c.getColumnIndex("room_name"));
                persons.add(person.room_name);
            }else if(data.equals("light")){
                person.light = c.getInt(c.getColumnIndex("light"));
                persons.add(String.valueOf(person.light));
            }else if(data.equals("hvac")){
                person.hvac = c.getInt(c.getColumnIndex("hvac"));
                persons.add(String.valueOf(person.hvac));
            }else if(data.equals("mood")){
                person.mood = c.getInt(c.getColumnIndex("mood"));
                persons.add(String.valueOf(person.mood));
            }else if(data.equals("curtain")){
                person.curtain = c.getInt(c.getColumnIndex("curtain"));
                persons.add(String.valueOf(person.curtain));
            }
        }
        c.close();
        return persons;
    }
    public Cursor queryTheCursor(String table) {
        Cursor c = db.rawQuery("SELECT * FROM "+table, null);
        return c;
    }
    /**
     * 获取符合该条件的行数
     *
     */
    public int querydata(SaveTips tips) {
        Cursor c = db.rawQuery("SELECT * FROM tip where date=?",  new String[]{String.valueOf(tips.date)});
        int result=c.getCount();
        c.close();
        return result;
    }
    /**
     * 获取符合该条件的内容，getString(列数)
     *用于roomid获取fc
     */
    public String[] queryroomfounction(int roomid) {
        Cursor c = db.rawQuery("SELECT * FROM room where room_id=?", new String[]{String.valueOf(roomid)});
        String[] result={"","","","","","","","","",""};
        System.out.println(c);
        if(c.moveToNext()) {

                result[0] = c.getString(3);//light

                result[1] = c.getString(4);//hvac

                result[2] = c.getString(6);//fan
                result[3] = c.getString(7);//curtain
                result[4] = c.getString(8);//music
                result[5] = c.getString(5);//mood
                result[6] = c.getString(11);//other
                result[7] = c.getString(12);//media
                result[8] = c.getString(13);//nio
                result[9] = c.getString(14);//fh
        }
        c.close();
        return result;
    }
    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}