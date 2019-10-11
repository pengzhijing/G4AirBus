package com.pzj.ipcdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.pzj.ipcdemo.utils.AudioPlayer;
import com.pzj.ipcdemo.utils.CustomBuffer;
import com.pzj.ipcdemo.utils.CustomBufferData;
import com.pzj.ipcdemo.utils.CustomBufferHead;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import vstc2.nativecaller.NativeCaller;

public class PlayCommonManager implements MediaScannerConnection.MediaScannerConnectionClient{

    public static final int PLAY_MODE_LIVE = 0;
    public static final int PLAY_MODE_RECORDED_SDCARD = 1;

    private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;

    private Activity activity;
    private ImageView videoView = null;

    private int playMode;
    private String strDID;

    private MediaScannerConnection mediaScannerConnection;
    private File snapshotFile;
    private File videoFile;

    private CustomBuffer AudioBuffer = null;
    private AudioPlayer audioPlayer = null;

    private String snapshotSaveFolderName = "";
    private String snapshotSaveFileName = "";

    private boolean isPictSave = false;
    private boolean isBackPressed = false;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    ////////////////////////////////////
    private BitmapDrawable drawable = null;
    private RelativeLayout bottomView;
    private RelativeLayout topbg;
    private Animation showTopAnim;
    private Animation dismissTopAnim;
    private Animation showAnim;
    private Animation dismissAnim;

    private boolean isUpDownPressed = false;
    private boolean isShowtoping = false;

    private boolean isDown = false;
    private boolean isSecondDown = false;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;
    private int mode = NONE;

    private float oldDist;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float mMaxZoom = 2.0f;
    float mMinZoom = 0.3125f;
    float originalScale;
    float baseValue;
    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];


    public PlayCommonManager(Activity activity, ImageView videoView, int playMode, String strDID){
        this.activity = activity;
        this.playMode = playMode;
        this.strDID = strDID;
        this.videoView = videoView;

        snapshotSaveFolderName = "wer";//activity.getIntent().getExtras().getString("snapshotSaveFolderName");
        snapshotSaveFileName = "wer";//activity.getIntent().getExtras().getString("snapshotSaveFileName");

        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);

        initViews();

        dismissTopAnim = AnimationUtils.loadAnimation(activity,
            activity.getResources().getIdentifier("ptz_top_anim_dismiss", "anim", activity.getPackageName()));
        showTopAnim = AnimationUtils.loadAnimation(activity,
            activity.getResources().getIdentifier("ptz_top_anim_show", "anim", activity.getPackageName()));
        showAnim = AnimationUtils.loadAnimation(activity,
            activity.getResources().getIdentifier("ptz_otherset_anim_show", "anim", activity.getPackageName()));
        dismissAnim = AnimationUtils.loadAnimation(activity,
            activity.getResources().getIdentifier("ptz_otherset_anim_dismiss", "anim", activity.getPackageName()));
    }

    public void initViews(){
        //底部菜单
        bottomView=(RelativeLayout) activity.findViewById(R.id.bottom_view);
        topbg = (RelativeLayout) activity.findViewById(R.id.top_bg);

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),
            activity.getResources().getIdentifier("top_bg", "drawable", activity.getPackageName()));
        drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);

        topbg.setBackgroundDrawable(drawable);
        bottomView.setBackgroundDrawable(drawable);
    }

    //显示顶部菜单
    private void showTop() {
        if (isShowtoping) {
            isShowtoping = false;
            topbg.setVisibility(View.GONE);
            topbg.startAnimation(dismissTopAnim);
        } else {
            isShowtoping = true;
            topbg.setVisibility(View.VISIBLE);
            topbg.startAnimation(showTopAnim);
        }
    }

    private void showBottom() {
        if (isUpDownPressed) {
            isUpDownPressed = false;
            bottomView.startAnimation(dismissAnim);
            bottomView.setVisibility(View.GONE);
        } else {
            isUpDownPressed = true;
            bottomView.startAnimation(showAnim);
            bottomView.setVisibility(View.VISIBLE);
        }
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isBackPressed = true;
        }
    }

    public void onTouch(View v, MotionEvent event) {
        if (!isDown) {
            x1 = event.getX();
            y1 = event.getY();
            isDown = true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                originalScale = getScale();
                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs((x1 - x2)) < 25 && Math.abs((y1 - y2)) < 25)
                {

                    if (playMode == PLAY_MODE_LIVE){
                       // ((PlayBackActivity)activity).dismissPopups();
                    }
                    if (!isSecondDown) {
//                        if (!bProgress) {
                            showTop();
                            showBottom();
//                        }
                    }
                    isSecondDown = false;
                }
                x1 = 0;
                x2 = 0;
                y1 = 0;
                y2 = 0;
                isDown = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isSecondDown = true;
				/*oldDist = spacing(event);
				if (oldDist > 10f)
				{
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}*/
                break;

            case MotionEvent.ACTION_MOVE:


        }
    }

    protected void zoomTo(float scale, float centerX, float centerY)
    {
        Log.d("zoomTo", "zoomTo scale:" + scale);
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        } else if (scale < mMinZoom) {
            scale = mMinZoom;
        }

        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        Log.d("deltaScale", "deltaScale:" + deltaScale);
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        videoView.setScaleType(ImageView.ScaleType.MATRIX);
        videoView.setImageMatrix(getImageViewMatrix());
    }

    protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getScale() {
        return getScale(mSuppMatrix);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

	/*private float (MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} catch (Exception e) {
		}
		return 0;
	}*/

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    protected void onResume() {
//        ProvisionHandler.getInstance().getProvisionListener().onProvisionEvent(ProvisionListener.ProvisionEvent.EVENT_VIDEO_STARTED, null);
    }

    protected void onPause() {
        activity.finish();
    }

    protected void onDestroy() {
        if (playMode == PLAY_MODE_LIVE){
            NativeCaller.StopPPPPLivestream(strDID);
        }
        else if (playMode == PLAY_MODE_RECORDED_SDCARD){
            NativeCaller.StopPlayBack(strDID);
        }

        StopAudio();

        if (!isBackPressed){
//            ProvisionHandler.getInstance().logout("all");
        }
        isBackPressed = false;

//        ProvisionHandler.getInstance().getProvisionListener().onProvisionEvent(ProvisionListener.ProvisionEvent.EVENT_VIDEO_STOPPED, null);
    }

    public void downloadVideo(){

        if (mediaScannerConnection == null){
            mediaScannerConnection = new MediaScannerConnection(activity, this);
        }

        String strDate = getStrDate();
        String filePath = Environment.getExternalStorageDirectory() + "/" + "ipcam" + "/" +  strDID +"_"+ strDate +".mp4";

        File div = new File(Environment.getExternalStorageDirectory(),
            "ipcam");
        if (!div.exists()) {
            div.mkdirs();
        }

        //videoFile = new File(div, "tf" + "_"+ strDate +".mp4");

        NativeCaller.StrarRecordPlayBack(strDID, filePath);
    }

    public void onDownloadVideoFinished(){
        if (!mediaScannerConnection.isConnected()){
            mediaScannerConnection.connect();
        } else {
            if (videoFile != null) {
                mediaScannerConnection.scanFile(videoFile.getAbsolutePath(), null);
                videoFile = null;
            }
        }
    }

    public boolean canTakePhoto(){
        if (existSdcard()) {// 判断sd卡是否存在
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//            if(ProvisionPlugin.hasPermission(permission)) {
                return true;
//            }
//            else{
//                showToast(ProvisionPlugin.getTranslation("messageStoragePermissionError"));
//            }
        } else {
//                    showToast("The device does not have an SDCard, photos are saved");
        }
        return false;
    }

    // 拍照
    public void takePicture(final Bitmap bmp) {
        if (mediaScannerConnection == null){
            mediaScannerConnection = new MediaScannerConnection(activity, this);
        }

        if (!isPictSave) {
            isPictSave = true;
            new Thread() {
                public void run() {
                    savePicToSDcard(bmp);
                }
            }.start();
        } else {
            return;
        }
    }

    /*
	 * 保存到本地
	 * 注意：此处可以做本地数据库sqlit 保存照片，以便于到本地照片观看界面从SQLite取出照片
	 */
    private synchronized void savePicToSDcard(final Bitmap bmp) {
        String strDate = getStrDate();
        //String date = strDate.substring(0, 10);
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(),
                snapshotSaveFolderName);
            if (!div.exists()) {
                div.mkdirs();
            }

            snapshotFile = new File(div, snapshotSaveFileName + "_"+ strDate +".jpg");
            fos = new FileOutputStream(snapshotFile);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos))
            {
                fos.flush();
                Log.d("tag", "takepicture success");
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showToast(("messageTakingSnapshotSuccess"));
                    }
                });
            }
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(("messageVideoParamsResetToDefault"));
                }
            });
            Log.d("tag", "exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            isPictSave = false;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }

        if (!mediaScannerConnection.isConnected()){
            mediaScannerConnection.connect();
        } else {
            if (snapshotFile != null) {
                mediaScannerConnection.scanFile(snapshotFile.getAbsolutePath(), null);
                snapshotFile = null;
            }
        }
    }
    //时间格式
    private String getStrDate() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String strDate = f.format(d);
        return strDate;
    }

    public String getTimeZone(int tz) {
        String tzStr = null;
        switch (tz) {
            case 39600:
                tzStr = "GMT-11:00";
                break;
            case 36000:
                tzStr = "GMT-10:00";
                break;
            case 32400:
                tzStr = "GMT-09:00";
                break;
            case 28800:
                tzStr = "GMT-08:00";
                break;
            case 25200:
                tzStr = "GMT-07:00";
                break;
            case 21600:
                tzStr = "GMT-06:00";
                break;
            case 18000:
                tzStr = "GMT-05:00";
                break;
            case 14400:
                tzStr = "GMT-04:00";
                break;
            case 12600:
                tzStr = "GMT-03:30";
                break;
            case 10800:
                tzStr = "GMT-03:00";
                break;
            case 7200:
                tzStr = "GMT-02:00";
                break;
            case 3600:
                tzStr = "GMT-01:00";
                break;
            case 0:
                tzStr = "GMT";
                break;
            case -3600:
                tzStr = "GMT+01:00";
                break;
            case -7200:
                tzStr = "GMT+02:00";
                break;
            case -10800:
                tzStr = "GMT+03:00";
                break;
            case -12600:
                tzStr = "GMT+03:30";
                break;
            case -14400:
                tzStr = "GMT+04:00";
                break;
            case -16200:
                tzStr = "GMT+04:30";
                break;
            case -18000:
                tzStr = "GMT+05:00";
                break;
            case -19800:
                tzStr = "GMT+05:30";
                break;

            case -21600:
                tzStr = "GMT+06:00";
                break;
            case -25200:
                tzStr = "GMT+07:00";
                break;
            case -28800:
                tzStr = "GMT+08:00";
                break;
            case -32400:
                tzStr = "GMT+09:00";
                break;
            case -34200:
                tzStr = "GMT+09:30";
                break;
            case -36000:
                tzStr = "GMT+10:00";
                break;
            case -39600:
                tzStr = "GMT+11:00";
                break;
            case -43200:
                tzStr = "GMT+12:00";
                break;
            default:
                break;
        }
        return tzStr;
    }

    public String getDeviceTime(long millisutc, String tz) {

        TimeZone timeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(millisutc);

        int second = calendar.get(Calendar.SECOND);


        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity);

        Locale locale = Locale.getDefault();
        boolean use24Hour = android.text.format.DateFormat.is24HourFormat(activity);
//        final String skeleton = use24Hour ? "Hm" : "hm";
//        final String pattern = android.text.format.DateFormat.getBestDateTimePattern(locale, skeleton);
        DateFormat formatter = new SimpleDateFormat(use24Hour ? "HH:mm:ss" : "hh:mm:ss a", locale);
        return dateFormat.format(calendar.getTime()) + "   " + formatter.format(calendar.getTime()).replace("am", "AM").replace("pm","PM");

    }

    @Override
    public void onMediaScannerConnected() {
        if (snapshotFile != null) {
            mediaScannerConnection.scanFile(snapshotFile.getAbsolutePath(), null);
        }
        if (videoFile != null) {
            mediaScannerConnection.scanFile(videoFile.getAbsolutePath(), null);
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mediaScannerConnection.disconnect();;
    }

    private void showToast(String i)
    {
        Toast.makeText(activity, i, Toast.LENGTH_LONG).show();
    }

    public void addAudioData(byte[] videobuf, int len){
        CustomBufferHead head = new CustomBufferHead();
        CustomBufferData data = new CustomBufferData();
        head.length = len;
        head.startcode = AUDIO_BUFFER_START_CODE;
        data.head = head;
        data.data = videobuf;
        AudioBuffer.addData(data);
    }

    public boolean isAudioPlaying(){
        return audioPlayer.isAudioPlaying();
    }

    //监听
    public void StartAudio() {
        synchronized (this) {
            AudioBuffer.ClearAll();
            audioPlayer.AudioPlayStart();
            if (playMode == PLAY_MODE_LIVE) {
                NativeCaller.PPPPStartAudio(strDID);
            }
        }
    }
    //停止监听
    public void StopAudio() {
        synchronized (this) {
            audioPlayer.AudioPlayStop();
            AudioBuffer.ClearAll();
            if (playMode == PLAY_MODE_LIVE) {
                NativeCaller.PPPPStopAudio(strDID);
            }
        }
    }

    /**
      * 获取系统内存大小
      * @return
      */
    public String getSysteTotalMemorySize(Context context){
        ActivityManager mActivityManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        mActivityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.totalMem ;
        String availMemStr = Formatter.formatFileSize(context,memSize);
        return availMemStr ;
    }

    /**

     * 获取手机内部可用空间大小

     * @return

     */
    public static String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        Log.i("zzz", path.getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    /**
     * 获取手机内部空间大小
     * @return
     */
    public static String getTotalInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();//Gets the Android data directory
        Log.i("zzz", path.getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();      //每个block 占字节数
        long totalBlocks = stat.getBlockCount();   //block总数
        return Formatter.formatFileSize(context, totalBlocks * blockSize);
    }

    public static String getDiskCacheDir(Context mContext){
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cachePath = mContext.getExternalCacheDir().getPath();
        } else {
            cachePath = mContext.getCacheDir().getPath();
        }
        // 防止不存在目录文件，自动创建
        createFile(cachePath);
        // 返回文件存储地址
        return cachePath;
    }

    public static File createFile(String fPath){
        try {
            File file = new File(fPath);
            // 当这个文件夹不存在的时候则创建文件夹
            if(!file.exists()){
                // 允许创建多级目录
                file.mkdirs();
                // 这个无法创建多级目录
                // rootFile.mkdir();
            }
            return file;
        } catch (Exception e) {
        }
        return null;
    }

    //判断sd卡是否存在
    public boolean existSdcard() {
        if (Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
