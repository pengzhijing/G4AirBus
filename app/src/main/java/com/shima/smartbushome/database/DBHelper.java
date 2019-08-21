package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-3.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sbus.db";
    private static final int DATABASE_VERSION = 3;
    private static  String DATABASE_PATH=DatabaseContext.dbPath;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context,DATABASE_PATH, null, DATABASE_VERSION);
    }
    /*
            * mood:control type:1->light,value1->channel,value2->brightness
            * control type:2->hvac,value1->save_type, 1on/off 2mode 3temp 4fan speed mode
            *                      value2->type_value if 3temp,this means modetype
            *                      value3->temp value
            * control type3->music,value1->type,1->music,2->radio,3->av-in,4->unknown
            * value2->album no; value3->song no;  value4->channel no; value5->volume;value6->save-type,1->controltype;2->volume;3->value;4->play
            * control type:4 ->curtain,value1->channel1,value2->value1,value3->channel2,value4->value2
            * */
    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS room" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,room_name TEXT,light Integer,hvac Integer,mood Integer,fan Integer," +
                "curtain Integer,music Integer,room_icon TEXT,room_icon_bg TEXT,other Integer,media Integer,nio Integer,fh Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS light" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,subnetID Integer,deviceID Integer," +
                "light_id Integer,light_statement TEXT,channel Integer,brightvalue Integer,lightType Integer,light_icon TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS hvac" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,hvac_id Integer,subnetID Integer,deviceID Integer,hvac_statement TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS floorheat" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,floorheat_id Integer,subnetID Integer,deviceID Integer,channel Integer,floorheat_statement TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS mood" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,mood_id Integer,subnetID Integer,deviceID Integer," +
                "control_type Integer,value_1 Integer," +
                "value_2 Integer,value_3 Integer,value_4 Integer,value_5 Integer,value_6 Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS moodbutton" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer," +
                "mood_id Integer,mood_name TEXT,mood_icon TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS song" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer," +
                "album_num Integer,album_name TEXT,song_num Integer,song_name TEXT,like Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS music" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,music_id Integer,subnetID Integer,deviceID Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS radio" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer," +
                "channel_num Integer,channel_name TEXT, channel_value TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS curtain" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,curtain_id Integer,subnetID Integer,deviceID Integer," +
                "curtain_remark TEXT, curtain_type Integer,channel_1 Integer,channel_2 Integer,current_state TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS other" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,subnetID Integer,deviceID Integer," +
                "other_id Integer,other_statement TEXT,channel_1 Integer,channel_2 Integer, other_icon TEXT,other_type Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS marcobutton" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, marco_id Integer,marco_remark TEXT,marco_icon TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS marco" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, marco_id Integer,room_id Integer,room TEXT,device TEXT,subnetID Integer,deviceID Integer," +
                "control_type Integer,value1 Integer,value2 Integer,value3 Integer,sentorder Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS fan" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,subnetID Integer,deviceID Integer," +
                "fan_id Integer,fan_statement TEXT,channel Integer,fan_Type Integer,fan_icon TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS media" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,subnetID Integer,deviceID Integer," +
                "media_id Integer,media_statement TEXT,media_icon TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS mediabutton" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,media_id Integer,button_num Integer," +
                "media_swno Integer,media_controltype Integer,media_type Integer,ifIRmarco Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS security" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,security_id Integer," +
                "subnetID Integer,deviceID Integer,password Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS securityarea" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, security_id Integer," +
                "areaNO Integer,areaName TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS energy (_id INTEGER PRIMARY KEY AUTOINCREMENT, energy_id Integer," +
                "subnetID Integer,deviceID Integer,energyname TEXT,channel1_name TEXT,channel2_name TEXT,channel3_name TEXT," +
                "channel4_name TEXT,channel5_name TEXT,channel6_name TEXT,channel7_name TEXT,channel8_name TEXT," +
                "channel9_name TEXT,channel10_name TEXT,channel11_name TEXT,channel12_name TEXT,channel13_name TEXT," +
                "channel14_name TEXT,channel15_name TEXT,channel16_name TEXT,channel17_name TEXT,channel18_name TEXT," +
                "channel19_name TEXT,channel20_name TEXT,channel21_name TEXT,channel22_name TEXT,channel23_name TEXT," +
                "channel24_name TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS energydata (_id INTEGER PRIMARY KEY AUTOINCREMENT, energy_id Integer," +
                "channel1_value Integer,channel2_value Integer,channel3_value Integer," +
                "channel4_value Integer,channel5_value Integer,channel6_value Integer," +
                "channel7_value Integer,channel8_value Integer,channel9_value Integer," +
                "channel10_value Integer,channel11_value Integer,channel12_value Integer," +
                "channel13_value Integer,channel14_value Integer,channel15_value Integer," +
                "channel16_value Integer,channel17_value Integer,channel18_value Integer," +
                "channel19_value Integer,channel20_value Integer,channel21_value Integer," +
                "channel22_value Integer,channel23_value Integer,channel24_value Integer," +
                "Time TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS nio (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " room_id Integer,subnetID Integer,deviceID Integer,nio_id Integer,nio_remark TEXT," +
                "name1 TEXT,name2 TEXT,name3 TEXT,name4 TEXT,name5 TEXT,name6 TEXT,name7 TEXT,name8 TEXT," +
                "name9 TEXT,name10 TEXT,name11 TEXT,name12 TEXT,name13 TEXT,name14 TEXT,name15 TEXT,value1 Integer," +
                "value2 Integer,value3 Integer,value4 Integer,value5 Integer,value6 Integer,value7 Integer," +
                "value8 Integer,value9 Integer,value10 Integer,value11 Integer,value12 Integer,value13 Integer," +
                "value14 Integer,value15 Integer,value16 Integer,value17 Integer,value18 Integer,value19 Integer," +
                "value20 Integer,value21 Integer,value22 Integer,value23 Integer,value24 Integer,value25 Integer," +
                "value26 Integer,value27 Integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS status (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " status_id Integer,name TEXT,subnetID Integer,deviceID Integer,type Integer,channel Integer,status_icon TEXT," +
                "unit Integer)");
        /*option1: 1:onlyonetime;2:everyday;3:selectweek;  option2:bit:7,6,5,4,3,2,1->day 7,6,5,4,3,2,1;  last_status: 0:on , 1:off*/
        db.execSQL("CREATE TABLE IF NOT EXISTS schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " schedule_id Integer,schedule_name TEXT,schedule_icon TEXT,marco_ID Integer,alarm_time TEXT," +
                "repeat_option1 Integer,repeat_option2 Integer,last_status Integer)");
        /*state: 0:on 1:off*/
        db.execSQL("CREATE TABLE IF NOT EXISTS nfc (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " nfc_id Integer,nfc_name TEXT,nfc_icon TEXT,nfc_content TEXT,state Integer,action_type Integer," +
                "marco_ID Integer,marco_name TEXT,call_num TEXT,message TEXT,resume1 Integer,resume2 TEXT,delaytime Integer)");


    }

    private String CREATE_room = "create table room(_id INTEGER PRIMARY KEY AUTOINCREMENT, room_id Integer,room_name TEXT,light Integer,hvac Integer,mood Integer,fan Integer," +
            "curtain Integer,music Integer,room_icon TEXT,room_icon_bg TEXT,other Integer,media Integer,nio Integer);";
    private String CREATE_TEMP_room = "alter table room rename to _temp_room";
    private String INSERT_DATA = "insert into room select *,'0' from _temp_room";
    private String DROP_room = "drop table _temp_room";
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      /*  switch (newVersion) {
            case 3:
                db.execSQL(CREATE_TEMP_room);
                db.execSQL(CREATE_room);
                db.execSQL(INSERT_DATA);
                db.execSQL(DROP_room);
                break;
        }*/
    }
}