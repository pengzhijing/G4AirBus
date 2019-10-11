package com.pzj.ipcdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.CircularProgressBar;
import com.pzj.ipcdemo.utils.ContentCommon;
import com.pzj.ipcdemo.utils.MyRender;
import com.pzj.ipcdemo.utils.Tools;

import java.nio.ByteBuffer;
import java.util.Date;

import vstc2.nativecaller.NativeCaller;

/**
 * 远程录像回放
 */

public class PlayBackActivity extends Activity implements BridgeService.PlayBackInterface, BridgeService.DateTimeInterface, View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener
{
    public static PlayBackActivity self;

    private PlayCommonManager playCommonManager;

	private ImageView playImg;
	private String strDID;
    private String strName = null;
	private String strFilePath;
	private int strFilesize;
//    private float downloadedFileSize = 0;
//    private int downloadedPercent = 0;
	private byte[] videodata = null;
	private int videoDataLen = 0;
	private int nVideoWidth = 0;
	private int nVideoHeight = 0;
	private LinearLayout layoutConnPrompt;
	private LinearLayout playSeekBarLayout;
	private SeekBar playSeekBar;
	private CircularProgressBar circularProgress;
	private GLSurfaceView myGlSurfaceView;
	private MyRender myRender;
    private TextView textback = null;
    private TextView textDownloadPercent = null;
	private TextView textTimeStamp;
	private String tzStr = "GMT+08:00";
	private long time;
	private long currentTimeStamp;
	private String timeShow = " ";
//    private long startTimestamp;
//	private long endTimestamp;
//	private long totalDiff;
	private float pos;
	private float cachePos;

    private boolean isOneShow = true;
    private boolean isTakepic = false;
    private Bitmap mBmp;

    private boolean isPlaying;
    private boolean isSliding;
    private boolean isDownloading;
    private boolean isDownloaded;

    long slidingTimeMillis;
    int ptz_take_photos;
    private ImageButton ptzTake_photos;

    int ptz_play_pause;
    private ImageButton ptzPlay_pause;
    private ImageButton ptzDownloadVideo;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1 || msg.what == 2) {
				if (isOneShow) {
					layoutConnPrompt.setVisibility(View.GONE);
					isOneShow = false;
				}
			}
			switch (msg.what) {
             case 0:
                 isSliding = false;
                 break;
			case 1: {// h264
//				Log.d("timeShow", "timeShow: " + timeShow);
				textTimeStamp.setText(timeShow);
				if (!isSliding) {
//                    playSeekBar.setProgress(downloadedPercent);
//                    if (System.currentTimeMillis() - slidingTimeMillis > 500) {

                        playSeekBar.setProgress((int) (10000 * pos));

//                    }
                    playSeekBar.setSecondaryProgress((int) (10000 * cachePos));
                    Log.d("PlayBackActivity", "#pos = " + pos + " cachePos = " + cachePos);
                }
                if (isDownloading) {
//                    textDownloadPercent.setText(cachePos + "%");
                    circularProgress.setVisibility(View.VISIBLE);
                    circularProgress.setProgress((int) (10000 * cachePos) /100);
                }

                if (pos == 1){
                    togglePlayPauseButton(true);
                }

                if (cachePos == 1){

                    if (isDownloading && !isDownloaded){
                        isDownloaded = true;
                        showToast(("messageDownloadVideoSuccess"));
                        playCommonManager.onDownloadVideoFinished();
                    }
                }

				myRender.writeSample(videodata, nVideoWidth, nVideoHeight);
				playImg.setVisibility(View.GONE);
				int width = getWindowManager().getDefaultDisplay().getWidth();

				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				myGlSurfaceView.setLayoutParams(lp);
			}
				break;
			case 2: {// jpeg
				textTimeStamp.setText(timeShow);
				Bitmap bmp = BitmapFactory.decodeByteArray(videodata, 0,videoDataLen);
				if (bmp == null) {
					return;
				}
				Bitmap bitmap = null;
				int width = getWindowManager().getDefaultDisplay().getWidth();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				playImg.setLayoutParams(lp);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					bitmap = Bitmap.createScaledBitmap(bmp, width,
							width * 3 / 4, true);
				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					bitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
				}
				playImg.setVisibility(View.VISIBLE);
				playImg.setImageBitmap(bitmap);
			}
				break;
			default:
				break;
			}
		}
	};

    private void showToast(String i)
    {
        Toast.makeText(this, i, Toast.LENGTH_LONG).show();
    }

//	private Runnable mVideoTimeOut = new Runnable() {
//		public void run() {
//			if (isOneShow) {//
//				BridgeService.setPlayBackInterface(PlayBackActivity.this);
//				//NativeCaller.StartPlayBack(strDID, strFilePath, 0,0);
//				NativeCaller.StartPlayBack(strDID, strFilePath, 0, strFilesize, playCommonManager.getDiskCacheDir(PlayBackActivity.this), Tools.getPhoneSDKIntForPlayBack(), Tools.getPhoneMemoryForPlayBack());
//				NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);
//				mHandler.postDelayed(mVideoTimeOut, 3000);
//			}
//		}
//	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        self = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.playback);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        strDID = intent.getStringExtra("did");
        strName = "Name";//getIntent().getExtras().getString("cameraName");
        strFilePath = intent.getStringExtra("filepath");
        strFilesize = intent.getIntExtra("filesize", 0);
        Log.d("getDataFromOther", "strDID:" + strDID);
        Log.d("getDataFromOther", "strFilePath:" + strFilePath);

        findView();

        playCommonManager = new PlayCommonManager(this, playImg, PlayCommonManager.PLAY_MODE_RECORDED_SDCARD, strDID);

		BridgeService.setPlayBackInterface(this);

		//h265 播放720P 需要大于3g的内存
		Log.e("videodate",strFilePath +"strFilesize"+strFilesize +"Tools.getPhoneMemoryForPlayBack()"+Tools.getPhoneMemoryForPlayBack());
		Log.e("videodate",playCommonManager.getSysteTotalMemorySize(this)+"");
		Log.e("videodate",PlayCommonManager.getAvailableInternalMemorySize(this)+"");
		Log.e("videodate",PlayCommonManager.getTotalInternalMemorySize(this)+"");

		startPlayBack();

        isPlaying = true;
        isSliding = false;
        isDownloading = false;
        isDownloaded = false;

//		mHandler.postDelayed(mVideoTimeOut, 3000);
		BridgeService.setDateTimeInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);

		playCommonManager.StartAudio();//开启声音
	}

	private void startPlayBack(){
        isPlaying = true;
        NativeCaller.StartPlayBack(strDID, strFilePath, 0, strFilesize, playCommonManager.getDiskCacheDir(PlayBackActivity.this), Tools.getPhoneSDKIntForPlayBack(), Tools.getPhoneMemoryForPlayBack());
    }

    public static void stopVideoAndHide(){
        try{
            if (self != null){
                self.finish();
            }
        }
        catch (Exception e){
            System.out.print("");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        playCommonManager.onTouch(v, event);

        return false;
    }

    private void togglePlayPauseButton(boolean isPlaying){
        if (isPlaying){
            //ptzPlay_pause.setImageResource(getResources().getIdentifier("ptz_play", "drawable", getPackageName()));
            ptzPlay_pause.setImageResource(R.drawable.ptz_play);
        }
        else{
            ptzPlay_pause.setImageResource(R.drawable.ptz_pause);
            //ptzPlay_pause.setImageResource(getResources().getIdentifier("ptz_pause", "drawable", getPackageName()));
        }
        this.isPlaying = !isPlaying;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ptz_take_photos){
            isTakepic = playCommonManager.canTakePhoto();
        }
        if (v.getId() == ptz_play_pause){
            if (pos < 1) {
                NativeCaller.PausePlayBack(strDID, isPlaying ? 0 : 1); // 0 for play, 1 for pause
            }
            else if (pos == 1){
                startPlayBack();
            }
            togglePlayPauseButton(isPlaying);
        }
        else if (v.getId() == R.id.ptz_download){
            if (!isDownloading) {
                String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//                if(ProvisionPlugin.hasPermission(permission)) {
                    playCommonManager.downloadVideo();
                    isDownloading = true;
//                }
//                else{
//                    showToast(("messageVideoStoragePermissionError"));
//                }
            }
        }else if(v.getId() == R.id.back)
        {
            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSliding = true;
        Log.d("PlayBackActivity", "isSliding = " + isSliding);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //isSliding = false;
        Log.d("PlayBackActivity", "isSliding = " + isSliding);

        int progress = seekBar.getProgress();
        changePlayBackTime(progress);

        //mHandler.sendEmptyMessageDelayed(0,2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSliding = false;
            }
        },2000);
    }



    private void changePlayBackTime(int progress){
//                float pos = (float)strFilesize * (float) progress / 100f;
//                float pos = (float)strFilesize * (float) progress;
//                float pos = (float)strFilesize;
//                float pos = (float)progress;
        float progressPos = (float) progress / 10000f;

        float newPos = progressPos / cachePos;
//        float newPos = progressPos;u


        Log.d("PlayBackActivity", "##pos = " + newPos);
        long time = NativeCaller.PlayBackMovePos(strDID, newPos);
        Log.d("PlayBackActivity", "##time = " + time);
        int a = NativeCaller.SetPlayBackPos(strDID, time);
        slidingTimeMillis = System.currentTimeMillis();
    }

	private void findView() {
		playImg = (ImageView) findViewById(R.id.vedioview);
		layoutConnPrompt = (LinearLayout) findViewById(R.id.progressLayout);
        playSeekBar = (SeekBar) findViewById(R.id.playback_seekbar);
        playSeekBarLayout = (LinearLayout) findViewById(R.id.playback_seekbar_layout);
        playSeekBarLayout.setVisibility(View.VISIBLE);

        circularProgress = (CircularProgressBar) findViewById(R.id.circularProgress);
        circularProgress.setProgressWidth(10);
        circularProgress.showProgressText(true);
        circularProgress.setTextColor(Color.WHITE);
        circularProgress.useRoundedCorners(true);
        circularProgress.setProgressColor(Color.RED);

        playSeekBar.setOnSeekBarChangeListener(this);
        playSeekBar.setMax(10000);

        textTimeStamp = (TextView) findViewById(R.id.textTimeStamp);
		myGlSurfaceView = (GLSurfaceView) findViewById(R.id.mysurfaceview);
        myGlSurfaceView.setOnTouchListener(this);

		myRender = new MyRender(myGlSurfaceView);
		myGlSurfaceView.setRenderer(myRender);


        ptzTake_photos=(ImageButton) findViewById(R.id.ptz_take_photos);
        ptzTake_photos.setOnClickListener(this);
        ptzTake_photos.setVisibility(View.GONE);

        ptz_play_pause = getResources().getIdentifier("ptz_play_pause", "id", getPackageName());
        ptzPlay_pause=(ImageButton) findViewById(ptz_play_pause);
        ptzPlay_pause.setOnClickListener(this);
        ptzPlay_pause.setVisibility(View.VISIBLE);

        ptzDownloadVideo = (ImageButton) findViewById(R.id.ptz_download);
        ptzDownloadVideo.setOnClickListener(this);
        ptzDownloadVideo.setVisibility(View.VISIBLE);


        textback = (TextView) findViewById(R.id.back);
        textback.setText("返回");
        textback.setVisibility(View.VISIBLE);
        textback.setOnClickListener(this);

        textDownloadPercent = (TextView) findViewById(R.id.text_download_percent);
        textDownloadPercent.setVisibility(View.VISIBLE);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    playCommonManager.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        playCommonManager.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playCommonManager.onPause();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		playCommonManager.onDestroy();
//        exit = false;
	}

	@Override
	public void callBackDatetimeParams(String did, int now, int tz,
                                       int ntp_enable, String ntp_svr) {
	    String tzTemp = playCommonManager.getTimeZone(tz);
	    if (tzTemp != null){
	        tzStr = tzTemp;
        }
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
                                              int result) {
	}



	@Override
	public void callBackPlaybackVideoData(byte[] videobuf, int h264Data, int len, int width, int height,
                                          int time, int frameType, int originFrameLen, float pos, float cachePos) {

		if (h264Data == 1) {
            if (frameType == 1) {
                this.time = time;
                this.pos = pos;
                this.cachePos = cachePos;
                currentTimeStamp = this.time * 1000;

                Date temp = new Date(currentTimeStamp);
                Log.d("PlayBackActivity", "temp = " + temp.toString());
                videodata = videobuf;
                videoDataLen = len;
                nVideoWidth = width;
                nVideoHeight = height;
                timeShow = playCommonManager.getDeviceTime(currentTimeStamp, tzStr);
                if (h264Data == 1) { // H264
                    mHandler.sendEmptyMessage(1);
                } else { // MJPEG
                    mHandler.sendEmptyMessage(2);
                }

                if (isTakepic) {
                    isTakepic = false;
                    byte[] rgb = new byte[nVideoWidth * nVideoHeight * 2];
                    try {
                        NativeCaller.YUV4202RGB565(videobuf, rgb, nVideoWidth, nVideoHeight);
                        ByteBuffer buffer = ByteBuffer.wrap(rgb);
                        mBmp = Bitmap.createBitmap(nVideoWidth, nVideoHeight, Bitmap.Config.RGB_565);
                        mBmp.copyPixelsFromBuffer(buffer);
                        playCommonManager.takePicture(mBmp);
                    } catch (Exception e) {
                        System.out.print("");
                    }
                }
            }
            else if (frameType == 6) { // 音频数据 - Audio data

                if (!playCommonManager.isAudioPlaying()) {
                    return;
                }

                playCommonManager.addAudioData(videobuf, len);

                Message msg = new Message();
                Bundle b = new Bundle();
                b.putInt("oneFramesize", originFrameLen);
                msg.setData(b);
                return;
            }
        }
	}
}
