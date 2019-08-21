package com.shima.smartbushome.udp;

public interface CONST{
	//app name
	public static final String CONST_APP_NAME= "SMART-BUS Automation";
	
	//lisence
	public static final String CONST_LISENCE_PREFIX= "SMART-BUS-";
	public static final String CONST_SMART_BUS = "SMART_BUS";
	public static final String CONST_SMART_BUS_LISENCE = "SMART_BUS_LISENCE";
	
	public static final String CONST_TITLE_OFFICIAL_REGISTERED_VERSION= "(Official proffessional version,Registered)";
	public static final String CONST_TITLE_REGISTERD= "(Official Registered)";
	public static final String CONST_TITLE_OFFICIAL_VERSION= "(Official Version)";
	public static final String CONST_TITLE_NOT_FOUND_DB= "(Not Found Database)";
	public static final String CONST_BUIlD_DATE="(build on July 25,2011)";
	public static final String CONST_NOT_REGISTERED="(Not registered)";
	
	//position of udp buffer
	public static final byte CONST_START_PST_OF_ADDITIONAL_DATA_IN_FULL_PACKETS=25;
	public static final byte CONST_START_PST_OF_ADDITIONAL_DATA_WITHOUT_AA_PACKETS=9;
	public static final byte CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS=16;
	
	//string define ������
	public static final String CONST_STR_UNKNOWN="Unknow-caro from CONST --Test";
	public static final String CONST_STR_ON="ON";
	public static final String CONST_STR_OFF="OFF";
	public static final String CONST_STR_OF_FAIL_COMMUNICATION="Sorry,Failure!Please try again or check your network connection.";
	public static final String CONST_STR_NOT_FOUND_DB="Not found DB!please check your directory of database.it will show fake data for testing following.";
	public static final String CONST_RESULT_NOT_FOUND_DB="NOFOUNDDB";
	
	//tab index of room
	public static final byte CONST_TAB_INDEX_OF_LIGHT_IN_ROOM=0;
	public static final byte CONST_TAB_INDEX_OF_MOOD_IN_ROOM=1;
	public static final byte CONST_TAB_INDEX_OF_HVAC_IN_ROOM=2;
	public static final byte CONST_TAB_INDEX_OF_MUSIC_IN_ROOM=3;
	
	//music command type define
	public static final byte CONST_MUSIC_CMD_TYPE_POWER_OFF=0;
	public static final byte CONST_MUSIC_CMD_TYPE_POWER_ON=1;
	public static final byte CONST_MUSIC_CMD_TYPE_SELECT_SOURCE=2;
	public static final byte CONST_MUSIC_CMD_TYPE_PREV_SONG=3;
	public static final byte CONST_MUSIC_CMD_TYPE_NEXT_SONG=4;
	public static final byte CONST_MUSIC_CMD_TYPE_PLAY_PAUSE=5;
	public static final byte CONST_MUSIC_CMD_TYPE_VOL_UP=6;
	public static final byte CONST_MUSIC_CMD_TYPE_VOL_DOWN=7;
	public static final byte CONST_MUSIC_CMD_TYPE_READ_ZONE_STATUS=8;
	public static final byte CONST_MUSIC_CMD_TYPE_READ_PLAYING_INFO=9;
	public static final byte CONST_MUSIC_CMD_TYPE_READ_ALBUM_INFO=10;
	
	//thread sleep time
	public static final int CONST_THREAD_SLEEP_MUSIC_READ_ZONE_STATUS=5000;
	public static final int CONST_THREAD_SLEEP_AC_READ_STATUS=5000;
	public static final int CONST_THREAD_SLEEP_MUSIC_READ_SOURCE_INFO=500;
	public static final int CONST_THREAD_SLEEP_LIGHT_READ_STATUS=5000;
	public static final int CONST_MIN_DELAY_MILLISECONDS=100;
	public static final int CONST_BOX_DELAY_FOR_READING_VIDEO_STATUS=2000;
    
	

}
