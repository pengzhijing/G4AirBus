package com.pzj.ipcdemo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseUtil {

	private static final String TAG = "DatabaseUtil";

	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "p2p_camera_database";

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "cameralist";
    private static final String DATABASW_VIDEOPICTURE_TABLE="cameravidpic";
    private static final String DATABASE_ALARMLOG_TABLE="alarmlog";
    private static final String DATABASE_FIRSTPIC_TABLE="firstpic";
    private static final String DATABASE_PRESENT_TABLE="present";
	/**
	 * Table columns
	 */
	public static final String KEY_ID = "id" ;
	public static final String KEY_NAME = "name";
	public static final String KEY_USER = "user" ;
	public static final String KEY_PWD = "pwd" ;
	public static final String KEY_DID = "did";
    
	public static final String KEY_FILEPATH="filepath";
    public static final String KEY_CREATETIME="createtime";
    public static final String KEY_TYPE="type";
    public static final String KEY_POSITION="position";
    
    
    public static final String KEY_ALARMLOG_CONTENT="content";
    
    /**
     * save video or picture to video_picture_table type
     * */
    public static final String TYPE_VIDEO="video";
    public static final String TYPE_PICTURE="picture";
    
    /**
     * create firstpic_table sql statement
     * 
     * **/
    
    private static final String CREATE_FIRSTPIC_TABLE=
    		"create table " +DATABASE_FIRSTPIC_TABLE+"("
    		+KEY_ID+" integer primary key autoincrement,"
    		+KEY_DID+" text not null, "
    		+KEY_FILEPATH+" text not null)";
    
    /**
     * create firstpic_table sql statement
     * 
     * **/
    
    private static final String CREATE_PRESENT_TABLE=
    		"create table " +DATABASE_PRESENT_TABLE+"("
    	    		+KEY_ID+" integer primary key autoincrement,"
    	    		+KEY_DID+" text not null, "
    	    		+KEY_POSITION+" text not null, "
    	    		+KEY_FILEPATH+" text not null)";
    
    
    /**
     * create alarmlog_table sql statement
     * 
     * **/
    private static final String CREATE_ALARMLOG_TABLE=
    		"create table "+DATABASE_ALARMLOG_TABLE+"("
    		+KEY_ID+" integer primary key autoincrement, "
    		+KEY_DID+" text not null, "
    		+KEY_ALARMLOG_CONTENT+" text not null, "
    		+KEY_CREATETIME+" text not null);"
    		;
    /**
     * create video_picture_table sql statement
     * **/
    private static final String CREATE_VIDEO_PICTURE_TABLE=
    		"create table "+DATABASW_VIDEOPICTURE_TABLE+"("
    		+KEY_ID+" integer primary key autoincrement, "
    		+KEY_DID+" text not null, "
    		+KEY_FILEPATH+" text not null, "
    		+KEY_CREATETIME+" text not null, "
    		+KEY_TYPE+" text not null);"
    		; 
    
    
	/**
	 * Database creation sql statement
	 */
	private static final String CREATE_STUDENT_TABLE =
		"create table " + DATABASE_TABLE 
		+ " (" + KEY_ID + " integer primary key autoincrement, "
		+ KEY_NAME +" text not null, " 
		+ KEY_DID + " text not null, "
		+ KEY_USER + " text not null,"
		+ KEY_PWD + " text);";

	/**
	 * Context
	 */
	private final Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Inner private class. Database Helper class for creating and updating database.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		/**
		 * onCreate method is called for the 1st time when database doesn't exists.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating student_table: " + CREATE_STUDENT_TABLE);
			Log.i(TAG,"Creating Video_Picture_Table: "+CREATE_VIDEO_PICTURE_TABLE);
			Log.i(TAG,"Creating alarmlog_table: "+CREATE_ALARMLOG_TABLE);
			Log.i(TAG,"Creating Firstpic_table"+CREATE_FIRSTPIC_TABLE);
			db.execSQL(CREATE_STUDENT_TABLE);
			db.execSQL(CREATE_VIDEO_PICTURE_TABLE);
			db.execSQL(CREATE_ALARMLOG_TABLE);
			db.execSQL(CREATE_FIRSTPIC_TABLE);
			db.execSQL(CREATE_PRESENT_TABLE);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>!!!!!!!!!!!!!!!!1");
		}
		
		/**
		 * onUpgrade method is called when database version changes.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx the Context within which to work
	 */
	public DatabaseUtil(Context ctx) {
		this.mCtx = ctx;
	}
	/**
	 * This method is used for creating/opening connection
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public DatabaseUtil open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	/**
	 * This method is used for closing the connection.
	 */
	public void close() {
		mDbHelper.close();
	}


	/**
	 * This method is used to create/insert new record record.
	 * @param name
	 * @param did
	 * @param user
	 * @param pwd
	 * @return
	 */
	public long createCamera(String name, String did, String user, String pwd) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PWD, pwd);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	
	
	/**
	 * This method will delete record.
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteCamera(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}
	/**
	 * 
	 * */
	public boolean deleteCamera(String did) {
		//
		int count1=mDb.delete(DATABASE_FIRSTPIC_TABLE, KEY_DID+"='"+did+"'", null);
		//
		int count2 = mDb.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID+"='"+did+"'", null);
		//
		int delete = mDb.delete(DATABASE_ALARMLOG_TABLE, KEY_DID+"='"+did+"'",null);
		//
		int delete2 = mDb.delete(DATABASE_PRESENT_TABLE, KEY_DID+"='"+did+"'", null);
		return mDb.delete(DATABASE_TABLE, KEY_DID + "='" + did + "'", null) > 0;
	}

	/**
	 * This method will return Cursor holding all the records.
	 * @return Cursor
	 */
	public Cursor fetchAllCameras() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_DID, KEY_USER, KEY_PWD}, null, null, null, null, null);
	}

	/**
	 * This method will return Cursor holding the specific record.
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	
	public Cursor fetchCamera(long id) throws SQLException {
		Cursor mCursor =
		mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID,
					KEY_NAME, KEY_DID, KEY_USER, KEY_PWD}, KEY_ID + "=" + id, null,
					null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}	
	
	/**
	 * 
	 * @param oldaddr
	 * @param oldport
	 * @param name
	 * @param addr
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean updateCamera(String oldDID, String name, String did, String user, String pwd) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_DID, did);
		args.put(KEY_USER, user);
		args.put(KEY_PWD, pwd);
		return mDb.update(DATABASE_TABLE, args, KEY_DID + "='" + oldDID + "'", null) > 0;
	}
	
	public boolean updateCameraUser(String did, String username, String pwd){
		ContentValues values=new ContentValues();
		values.put(KEY_USER, username);
		values.put(KEY_PWD, pwd);
		return  mDb.update(DATABASE_TABLE, values, KEY_DID+"='"+did+"'", null)>0;
		
	}
	/**
	 * This Method is used to create/insert new record
	 * **/
	public long createPresent(String did, String position, String filepath){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_POSITION, position);
		initialValues.put(KEY_FILEPATH, filepath);
		return mDb.insert(DATABASE_PRESENT_TABLE, null, initialValues);
	}
	/**
	 * This Method is used to quert all present
	 */
	public Cursor queryAllPresent(String did, String position){
		String sql = "select "+KEY_FILEPATH+" from "+DATABASE_PRESENT_TABLE+ " where  "+KEY_POSITION+"='"+position+"' and "+KEY_DID+"='"+did+"'";
		return mDb.rawQuery(sql, null);
	}
	/**
	 * This Method is used to update one present 
	 */
	public boolean updatePresent(String did, String position, String path){
		ContentValues values=new ContentValues();
		values.put(KEY_DID, did);
		values.put(KEY_POSITION, position);
		values.put(KEY_FILEPATH, path);
		return mDb.update(DATABASE_PRESENT_TABLE, values, KEY_POSITION+"='"+position+"' and "+KEY_DID+"='"+did+"'", null)>0;
	}
	public boolean deleteOnePresent(String did, String position){
		return mDb.delete(DATABASE_PRESENT_TABLE, KEY_DID + "=? and "+KEY_POSITION+"=?", new String[]{did,position})>0;
	}
	/**
	 * This Method is used to delete all present 
	 */
	public boolean deletePresent(String did){
		return mDb.delete(DATABASE_PRESENT_TABLE, KEY_DID+"=?",new String[]{did})>0;
	}
	/**
	 * This Method is used to create/insert new record
	 * **/
	public long createVideoOrPic(String did, String filepath, String type, String createtime){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_FILEPATH, filepath);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_CREATETIME, createtime);
		return mDb.insert(DATABASW_VIDEOPICTURE_TABLE, null, initialValues);
	}
	/**
	 * This Method is used to query all video record from video_picture_table
	 * **/
	public Cursor queryAllVideo(String did){
		String sql="select * from "+DATABASW_VIDEOPICTURE_TABLE+ " where  "+KEY_TYPE+"='"+TYPE_VIDEO+"' and "+KEY_DID+"='"+did+"' order by "+KEY_FILEPATH+" desc";
		return mDb.rawQuery(sql,null);
	}
	
	/**
	 * This Method is used to query all video record from video_picture_table
	 * **/
	public Cursor queryAllPicture(String did){
		String sql="select * from "+DATABASW_VIDEOPICTURE_TABLE+ " where  "+KEY_TYPE+"='"+TYPE_PICTURE+"' and "+KEY_DID+"='"+did+"'";
		Log.d("tag","queryAllPictrue sql:"+sql);
		return mDb.rawQuery(sql,null);
	}
	/**
	 * This Method is used to query video/picture in createtime from video_picture_table
	 * **/
	public Cursor queryVideoOrPictureByDate(String did, String date, String type){
		String sql="select * from "+DATABASW_VIDEOPICTURE_TABLE+ " where  "+KEY_TYPE+"='"+TYPE_PICTURE+"' and "+KEY_DID+"='"+did+"' and "+KEY_CREATETIME+"='"+date+"'";
		return mDb.rawQuery(sql, null);
	}
	/***
	 * This Method is used to delete specific video/picture record from video_picture_table
	 * */
	public boolean deleteVideoOrPicture(String did, String filePath, String type){
		return mDb.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID + "=? and "+KEY_FILEPATH+"=? and "+KEY_TYPE+"=?", new String[]{did,filePath,type}) > 0;
	}
	/**
	 * This Method is used to delete all video/picture record from video_picture_table
	 * **/
	public boolean deleteAllVideoOrPicture(String did, String type){
		return mDb.delete(DATABASW_VIDEOPICTURE_TABLE,KEY_DID+"=? and "+KEY_TYPE+"=?", new String[]{did,type}) > 0;
	}
	/**
	 * This Method is used to delete all record from video_picture_table
	 * **/
	public boolean deldteAllVideoPicture(String did){
		return mDb.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID+"=?", new String[]{did})>0;
	}
	/**
	 * This Method is used to add alarm log to alarmlog_table
	 * **/
	public long insertAlarmLogToDB(String did, String content, String createTime){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_ALARMLOG_CONTENT, content);
		initialValues.put(KEY_CREATETIME, createTime);
		return mDb.insertOrThrow(DATABASE_ALARMLOG_TABLE, null, initialValues);
	}
	/**
	 * This Method is used to query all alarmlog from the specified  did
	 * **/
	public Cursor queryAllAlarmLog(String did){
		String sql="select * from "+DATABASE_ALARMLOG_TABLE+" where "+KEY_DID+"='"+did+"' order by "+KEY_CREATETIME+" desc";
		return mDb.rawQuery(sql, null);
	}
	/**
	 * This Method is used to delete one alarmlog with specified time from did 
	 * **/
	public boolean delAlarmLog(String did, String createtime){
		return mDb.delete(DATABASE_ALARMLOG_TABLE, KEY_DID+"=? and "+KEY_CREATETIME+"=?", new String[]{did,createtime})>0;
	}
	public boolean addFirstpic(String did, String filepath){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_FILEPATH, filepath);
		return mDb.insert(DATABASE_FIRSTPIC_TABLE, null, initialValues)>0;
	}
    public Cursor queryFirstpic(String did){
    	String sql="select *  from "+DATABASE_FIRSTPIC_TABLE+" where "+KEY_DID+"='"+did+"'";
    	return mDb.rawQuery(sql, null);
    }
    public boolean delFirstpic(String did, String filePath){
    	return mDb.delete(DATABASE_FIRSTPIC_TABLE, KEY_DID + "=? and "+KEY_FILEPATH+"=? ", new String[]{did,filePath})>0;
    }
}