package com.pzj.ipcdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;


import com.pzj.ipcdemo.bean.AlermBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

public class SettingAlarmActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener, OnGestureListener,
        OnTouchListener, BridgeService.AlarmInterface {
	// private String TAG = "SettingAlermActivity";
	private String strDID = null;
	private boolean successFlag = false;
	private final int TIMEOUT = 3000;
	private final int ALERMPARAMS = 3;
	private final int UPLOADTIMETOOLONG = 4;
	private int cameraType = 0;
	private GestureDetector gt = new GestureDetector(this);
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				showToast(R.string.alerm_set_failed);
				break;
			case 1:
				showToast(R.string.setting_aler_sucess);
				finish();
				break;
			case ALERMPARAMS:
				successFlag = true;
				progressDialog.cancel();
				if (0 == alermBean.getMotion_armed()) {
					motionAlermView.setVisibility(View.GONE);

				} else {
					cbxMotionAlerm.setChecked(true);
					motionAlermView.setVisibility(View.VISIBLE);
				}
				tvSensitivity.setText(String.valueOf(alermBean
						.getMotion_sensitivity()));

				if (0 == alermBean.getInput_armed()) {

					ioAlermView.setVisibility(View.GONE);
				} else {
					cbxIOAlerm.setChecked(true);
					ioAlermView.setVisibility(View.VISIBLE);
				}

				if (0 == alermBean.getIoin_level()) {
					tvTriggerLevel.setText(getResources().getString(
							R.string.alerm_ioin_levellow));
				} else {
					tvTriggerLevel.setText(getResources().getString(
							R.string.alerm_ioin_levelhight));
				}

				if (0 == alermBean.getAlarm_audio()) {
					audioAlermView.setVisibility(View.GONE);
					audioSensitivity.setText(getResources().getString(
							R.string.alerm_audio_levelforbid));
				} else {
					cbxAudioAlerm.setChecked(true);
					audioAlermView.setVisibility(View.VISIBLE);
					if (1 == alermBean.getAlarm_audio()) {
						audioSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levelhigh));
					} else if (2 == alermBean.getAlarm_audio()) {
						audioSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levelmiddle));
					} else if (3 == alermBean.getAlarm_audio()) {
						audioSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levellow));
					}
				}

				if (0 == alermBean.getAlarm_temp()) {
					tempAlermView.setVisibility(View.GONE);
					tempSensitivity.setText(getResources().getString(
							R.string.alerm_audio_levelforbid));
				} else {
					cbxTempAlerm.setChecked(true);
					tempAlermView.setVisibility(View.VISIBLE);
					if (1 == alermBean.getAlarm_temp()) {
						tempSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levelhigh));
					} else if (2 == alermBean.getAlarm_temp()) {
						tempSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levelmiddle));
					} else if (3 == alermBean.getAlarm_temp()) {
						tempSensitivity.setText(getResources().getString(
								R.string.alerm_audio_levellow));
					}
				}

				if (0 == alermBean.getIolinkage()) {
					ioMotionView.setVisibility(View.GONE);
				} else {
					cbxIOMotion.setChecked(true);
					ioMotionView.setVisibility(View.VISIBLE);
				}

				if (0 == alermBean.getIoout_level()) {
					tvIoOutLevel.setText(getResources().getString(
							R.string.alerm_ioin_levellow));
				} else {
					tvIoOutLevel.setText(getResources().getString(
							R.string.alerm_ioin_levelhight));
				}

				if (alermBean.getAlermpresetsit() == 0) {
					tvPreset.setText(getResources().getString(
							R.string.alerm_preset_no));
				} else {
					tvPreset.setText(String.valueOf(alermBean
							.getAlermpresetsit()));
				}

				if (1 == alermBean.getMotion_armed()
						|| 1 == alermBean.getInput_armed()
						|| alermBean.getAlarm_audio() != 0) {
					eventView.setVisibility(View.VISIBLE);
				} else {
					eventView.setVisibility(View.GONE);
				}

				break;
			default:
				break;
			}
		}
	};

	private Button btnOk = null;
	private Button btnCancel = null;
	private View motionAlermView = null;
	private View ioAlermView = null;
	private View audioAlermView = null;
	private View tempAlermView = null;
	private View ioMotionView = null;
	private View eventView = null;
	private LinearLayout alarm3518eOptionll = null;
	private ImageView imgTriggerLevelDrop = null;
	private ImageView audioImgDrop = null;
	private ImageView tempImgDrop = null;
	private ImageView imgSensitiveDrop = null;
	private ImageView imgPresetDrop = null;
	private ImageView imgIoOutLevelDrop = null;
	private TextView tvIoOutLevel = null;
	private TextView tvPreset = null;
	private TextView tvTriggerLevel = null;
	private TextView tvSensitivity = null;
	private TextView audioSensitivity = null;
	private TextView tempSensitivity = null;
	private CheckBox cbxIOMotion = null;
	private CheckBox cbxIOAlerm = null;
	private CheckBox cbxAudioAlerm = null;
	private CheckBox cbxTempAlerm = null;
	private CheckBox cbxMotionAlerm = null;
	private AlermBean alermBean = null;

	private PopupWindow sensitivePopWindow = null;
	private PopupWindow triggerLevelPopWindow = null;
	private PopupWindow ioOutLevelPopWindow = null;
	private PopupWindow presteMovePopWindow = null;
	private PopupWindow audioPopWindow = null;

	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		getDataFromOther();
		setContentView(R.layout.settingalarm);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.alerm_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		alermBean = new AlermBean();
		findView();
		setListener();
		BridgeService.setAlarmInterface(this);

		initPopupWindow();
	}

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		super.onPause();
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
			}
		}
	};

	private void setListener() {
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgIoOutLevelDrop.setOnClickListener(this);
		imgPresetDrop.setOnClickListener(this);
		imgSensitiveDrop.setOnClickListener(this);
		imgTriggerLevelDrop.setOnClickListener(this);
		audioImgDrop.setOnClickListener(this);
		tempImgDrop.setOnClickListener(this);
		cbxMotionAlerm.setOnCheckedChangeListener(this);
		cbxIOAlerm.setOnCheckedChangeListener(this);
		cbxAudioAlerm.setOnCheckedChangeListener(this);
		cbxTempAlerm.setOnCheckedChangeListener(this);
		cbxIOMotion.setOnCheckedChangeListener(this);
		eventView.setOnTouchListener(this);
		ioMotionView.setOnTouchListener(this);
		scrollView.setOnTouchListener(this);
		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}

		});
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();//
//
//
//
//
		if (i == R.id.alerm_ok) {
			setAlerm();

		} else if (i == R.id.alerm_cancel) {
			finish();

		} else if (i == R.id.alerm_img_ioout_level_drop) {
			dismissPopupWindow();

			ioOutLevelPopWindow.showAsDropDown(imgIoOutLevelDrop, -140, 0);

		} else if (i == R.id.ioout_hight) {
			tvIoOutLevel.setText(getResources().getString(
					R.string.alerm_ioin_levelhight));
			alermBean.setIoout_level(1);

		} else if (i == R.id.ioout_low) {
			tvIoOutLevel.setText(getResources().getString(
					R.string.alerm_ioin_levellow));
			alermBean.setIoout_level(0);

		} else if (i == R.id.alerm_img_preset_drop) {
			dismissPopupWindow();

			presteMovePopWindow.showAsDropDown(imgPresetDrop, -160, 0);


		} else if (i == R.id.alerm_img_sensitive_drop) {
			dismissPopupWindow();

			sensitivePopWindow.showAsDropDown(imgSensitiveDrop, -120, 10);

		} else if (i == R.id.alerm_img_leveldrop) {
			dismissPopupWindow();

			triggerLevelPopWindow.showAsDropDown(imgTriggerLevelDrop, -140, 0);

		} else if (i == R.id.alerm_audio_leveldrop) {
			isTempAlarm = false;
			dismissPopupWindow();

			audioPopWindow.showAsDropDown(audioImgDrop, -140, 0);


		} else if (i == R.id.alerm_temp_leveldrop) {
			isTempAlarm = true;
			dismissPopupWindow();

			audioPopWindow.showAsDropDown(tempImgDrop, -140, 0);

		} else if (i == R.id.trigger_audio_levelhigh) {
			if (isTempAlarm) {
				alermBean.setAlarm_temp(1);
				tempSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelhigh));
			} else {
				audioSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelhigh));
				alermBean.setAlarm_audio(1);
			}
			audioPopWindow.dismiss();

		} else if (i == R.id.trigger_audio_levelmiddle) {
			if (isTempAlarm) {
				alermBean.setAlarm_temp(2);
				tempSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelmiddle));
			} else {
				audioSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelmiddle));
				alermBean.setAlarm_audio(2);
			}
			audioPopWindow.dismiss();

		} else if (i == R.id.trigger_audio_levellow) {
			if (isTempAlarm) {
				alermBean.setAlarm_temp(3);
				tempSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levellow));
			} else {

				audioSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levellow));
				alermBean.setAlarm_audio(3);
			}
			audioPopWindow.dismiss();

		} else if (i == R.id.trigger_audio_levelforbid) {
			if (isTempAlarm) {
				alermBean.setAlarm_temp(0);
				tempSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelforbid));
			} else {
				audioSensitivity.setText(getResources().getString(
						R.string.alerm_audio_levelforbid));
				alermBean.setAlarm_audio(0);
			}
			audioPopWindow.dismiss();

		} else if (i == R.id.trigger_hight) {
			tvTriggerLevel.setText(getResources().getString(
					R.string.alerm_ioin_levelhight));
			triggerLevelPopWindow.dismiss();
			alermBean.setIoin_level(1);

		} else if (i == R.id.trigger_low) {
			tvTriggerLevel.setText(getResources().getString(
					R.string.alerm_ioin_levellow));
			triggerLevelPopWindow.dismiss();
			alermBean.setIoin_level(0);

		} else if (i == R.id.sensitive_10) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(10);
			tvSensitivity.setText(String.valueOf(10));

		} else if (i == R.id.sensitive_9) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(9);
			tvSensitivity.setText(String.valueOf(9));

		} else if (i == R.id.sensitive_8) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(8);
			tvSensitivity.setText(String.valueOf(8));

		} else if (i == R.id.sensitive_7) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(7);
			tvSensitivity.setText(String.valueOf(7));

		} else if (i == R.id.sensitive_6) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(6);
			tvSensitivity.setText(String.valueOf(6));

		} else if (i == R.id.sensitive_5) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(5);
			tvSensitivity.setText(String.valueOf(5));

		} else if (i == R.id.sensitive_4) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(4);
			tvSensitivity.setText(String.valueOf(4));

		} else if (i == R.id.sensitive_3) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(3);
			tvSensitivity.setText(String.valueOf(3));

		} else if (i == R.id.sensitive_2) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(2);
			tvSensitivity.setText(String.valueOf(2));

		} else if (i == R.id.sensitive_1) {
			sensitivePopWindow.dismiss();
			alermBean.setMotion_sensitivity(1);
			tvSensitivity.setText(String.valueOf(1));

		} else if (i == R.id.preset_no) {
			alermBean.setAlermpresetsit(0);
			tvPreset.setText(getResources().getString(R.string.alerm_preset_no));
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_1) {
			alermBean.setAlermpresetsit(1);
			tvPreset.setText("1");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_2) {
			alermBean.setAlermpresetsit(2);
			tvPreset.setText("2");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_3) {
			alermBean.setAlermpresetsit(3);
			tvPreset.setText("3");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_4) {
			alermBean.setAlermpresetsit(4);
			tvPreset.setText("4");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_5) {
			alermBean.setAlermpresetsit(5);
			tvPreset.setText("5");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_6) {
			alermBean.setAlermpresetsit(6);
			tvPreset.setText("6");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_7) {
			alermBean.setAlermpresetsit(7);
			tvPreset.setText("7");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_8) {
			alermBean.setAlermpresetsit(8);
			tvPreset.setText("8");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_9) {
			alermBean.setAlermpresetsit(9);
			tvPreset.setText("9");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_10) {
			alermBean.setAlermpresetsit(10);
			tvPreset.setText("10");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_11) {
			alermBean.setAlermpresetsit(11);
			tvPreset.setText("11");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_12) {
			alermBean.setAlermpresetsit(12);
			tvPreset.setText("12");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_13) {
			alermBean.setAlermpresetsit(13);
			tvPreset.setText("13");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_14) {
			alermBean.setAlermpresetsit(14);
			tvPreset.setText("14");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_15) {
			alermBean.setAlermpresetsit(15);
			tvPreset.setText("15");
			presteMovePopWindow.dismiss();

		} else if (i == R.id.preset_16) {
			alermBean.setAlermpresetsit(16);
			tvPreset.setText("16");
			presteMovePopWindow.dismiss();

		} else {
		}
	}

	private void setAlerm() {
		if (successFlag) {
			Log.e("setAlerm", "setAlermTemp: " + alermBean.getAlarm_temp());
			NativeCaller.PPPPAlarmSetting(strDID, alermBean.getAlarm_audio(),
					alermBean.getMotion_armed(),
					alermBean.getMotion_sensitivity(),
					alermBean.getInput_armed(), alermBean.getIoin_level(),
					alermBean.getIoout_level(), alermBean.getIolinkage(),
					alermBean.getAlermpresetsit(), alermBean.getMail(),
					alermBean.getSnapshot(), alermBean.getRecord(),
					alermBean.getUpload_interval(),
					alermBean.getSchedule_enable(),
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
					0xFFFFFFFF,0xFFFFFFFF,-1);
		} else {
			showToast(R.string.alerm_set_failed);
		}
	}

	private ScrollView scrollView;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gt.onTouchEvent(event);
	}

	private void findView() {

		cbxMotionAlerm = (CheckBox) findViewById(R.id.alerm_cbx_move_layout);
		cbxIOAlerm = (CheckBox) findViewById(R.id.alerm_cbx_i0_layout);
		cbxAudioAlerm = (CheckBox) findViewById(R.id.alerm_cbx_audio_layout);
		cbxTempAlerm = (CheckBox) findViewById(R.id.alerm_cbx_temp_layout);
		cbxIOMotion = (CheckBox) findViewById(R.id.alerm_cbx_io_move);

		tvSensitivity = (TextView) findViewById(R.id.alerm_tv_sensitivity);
		audioSensitivity = (TextView) findViewById(R.id.alerm_audio_triggerlevel);
		tempSensitivity = (TextView) findViewById(R.id.alerm_temp_triggerlevel);
		tvTriggerLevel = (TextView) findViewById(R.id.alerm_tv_triggerlevel);
		tvPreset = (TextView) findViewById(R.id.alerm_tv_preset);
		tvIoOutLevel = (TextView) findViewById(R.id.alerm_tv_ioout_level_value);

		imgIoOutLevelDrop = (ImageView) findViewById(R.id.alerm_img_ioout_level_drop);
		imgPresetDrop = (ImageView) findViewById(R.id.alerm_img_preset_drop);
		imgSensitiveDrop = (ImageView) findViewById(R.id.alerm_img_sensitive_drop);
		imgTriggerLevelDrop = (ImageView) findViewById(R.id.alerm_img_leveldrop);
		audioImgDrop = (ImageView) findViewById(R.id.alerm_audio_leveldrop);
		tempImgDrop = (ImageView) findViewById(R.id.alerm_temp_leveldrop);

		alarm3518eOptionll = (LinearLayout) findViewById(R.id.alarm_3518e_option_view);
		if (cameraType == 1) {
			alarm3518eOptionll.setVisibility(View.VISIBLE);
		}

		ioMotionView = findViewById(R.id.alerm_io_move_view);
		ioAlermView = findViewById(R.id.alerm_ioview);
		audioAlermView = findViewById(R.id.alerm_audio_level);
		tempAlermView = findViewById(R.id.alerm_temp_level);
		motionAlermView = findViewById(R.id.alerm_moveview);
		eventView = findViewById(R.id.alerm_eventview);

		btnOk = (Button) findViewById(R.id.alerm_ok);
		btnCancel = (Button) findViewById(R.id.alerm_cancel);

		scrollView = (ScrollView) findViewById(R.id.scrollView1);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int i = buttonView.getId();
		if (i == R.id.alerm_cbx_move_layout) {
			if (isChecked) {
				alermBean.setMotion_armed(1);
				motionAlermView.setVisibility(View.VISIBLE);
			} else {
				alermBean.setMotion_armed(0);
				motionAlermView.setVisibility(View.GONE);
			}


		} else if (i == R.id.alerm_cbx_i0_layout) {
			if (isChecked) {
				alermBean.setInput_armed(1);
				ioAlermView.setVisibility(View.VISIBLE);
			} else {
				alermBean.setInput_armed(0);
				ioAlermView.setVisibility(View.GONE);
			}

		} else if (i == R.id.alerm_cbx_audio_layout) {
			if (isChecked) {
				alermBean.setAudioArmedCheck(1);
				audioAlermView.setVisibility(View.VISIBLE);
			} else {
				alermBean.setAudioArmedCheck(0);
				audioAlermView.setVisibility(View.GONE);
			}

		} else if (i == R.id.alerm_cbx_temp_layout) {
			if (isChecked) {
				alermBean.setAlarmTempChecked(1);
				tempAlermView.setVisibility(View.VISIBLE);
			} else {
				alermBean.setAlarmTempChecked(0);
				tempAlermView.setVisibility(View.GONE);
			}

		} else if (i == R.id.alerm_cbx_io_move) {
			if (isChecked) {
				alermBean.setIolinkage(1);
				ioMotionView.setVisibility(View.VISIBLE);
			} else {
				alermBean.setIolinkage(0);
				ioMotionView.setVisibility(View.GONE);
			}

		}
		if (1 == alermBean.getMotion_armed() || 1 == alermBean.getInput_armed()
				|| alermBean.getAudioArmedCheck() == 1
				|| alermBean.getAlarmTempChecked() == 1) {
			eventView.setVisibility(View.VISIBLE);
		} else {
			eventView.setVisibility(View.GONE);
		}
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraType = intent.getIntExtra(ContentCommon.STR_CAMERA_TYPE, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		dismissPopupWindow();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
		return false;
	}

	@Override
	public void callBackAlarmParams(String did, int alarm_audio, int motion_armed,
                                    int motion_sensitivity, int input_armed, int ioin_level,
                                    int iolinkage, int ioout_level, int alarmpresetsit, int mail,
                                    int snapshot, int record, int upload_interval,
                                    int schedule_enable, int schedule_sun_0, int schedule_sun_1,
                                    int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
                                    int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
                                    int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
                                    int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
                                    int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
                                    int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
                                    int schedule_sat_2) {

		alermBean.setDid(did);
		alermBean.setMotion_armed(motion_armed);
		alermBean.setMotion_sensitivity(motion_sensitivity);
		alermBean.setInput_armed(input_armed);
		alermBean.setIoin_level(ioin_level);
		alermBean.setIolinkage(iolinkage);
		alermBean.setIoout_level(ioout_level);
		alermBean.setAlermpresetsit(alarmpresetsit);
		alermBean.setMail(mail);
		alermBean.setSnapshot(snapshot);
		alermBean.setRecord(record);
		alermBean.setUpload_interval(upload_interval);
		alermBean.setAlarm_audio(alarm_audio);
		alermBean.setAlarm_temp(input_armed);
		alermBean.setSchedule_enable(schedule_enable);
		mHandler.sendEmptyMessage(ALERMPARAMS);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
                                              int result) {
		mHandler.sendEmptyMessage(result);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		dismissPopupWindow();
		return false;
	}

	private void dismissPopupWindow() {
		if (presteMovePopWindow != null && presteMovePopWindow.isShowing()) {
			presteMovePopWindow.dismiss();
		}
		if (sensitivePopWindow != null && sensitivePopWindow.isShowing()) {
			sensitivePopWindow.dismiss();
		}
		if (triggerLevelPopWindow != null && triggerLevelPopWindow.isShowing()) {
			triggerLevelPopWindow.dismiss();
		}
		if (ioOutLevelPopWindow != null && ioOutLevelPopWindow.isShowing()) {
			ioOutLevelPopWindow.dismiss();
		}
		if (audioPopWindow != null && audioPopWindow.isShowing()) {
			audioPopWindow.dismiss();
		}
	}

	private boolean isTempAlarm = false;

	private void initPopupWindow() {
		// TODO Auto-generated method stub
		initAudioPopupWindow();
		initMovePopupWindow();
		initInputPopupWindow();
		initIOlinkMovePopupWindow();
		initPresetPopupWindow();
	}

	private void initPresetPopupWindow() {
		LinearLayout preLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.alermpresetmovepopwindow, null);
		TextView presetNo = (TextView) preLayout.findViewById(R.id.preset_no);
		TextView preset1 = (TextView) preLayout.findViewById(R.id.preset_1);
		TextView preset2 = (TextView) preLayout.findViewById(R.id.preset_2);
		TextView preset3 = (TextView) preLayout.findViewById(R.id.preset_3);
		TextView preset4 = (TextView) preLayout.findViewById(R.id.preset_4);
		TextView preset5 = (TextView) preLayout.findViewById(R.id.preset_5);
		TextView preset6 = (TextView) preLayout.findViewById(R.id.preset_6);
		TextView preset7 = (TextView) preLayout.findViewById(R.id.preset_7);
		TextView preset8 = (TextView) preLayout.findViewById(R.id.preset_8);
		TextView preset9 = (TextView) preLayout.findViewById(R.id.preset_9);
		TextView preset10 = (TextView) preLayout.findViewById(R.id.preset_10);
		TextView preset11 = (TextView) preLayout.findViewById(R.id.preset_11);
		TextView preset12 = (TextView) preLayout.findViewById(R.id.preset_12);
		TextView preset13 = (TextView) preLayout.findViewById(R.id.preset_13);
		TextView preset14 = (TextView) preLayout.findViewById(R.id.preset_14);
		TextView preset15 = (TextView) preLayout.findViewById(R.id.preset_15);
		TextView preset16 = (TextView) preLayout.findViewById(R.id.preset_16);
		presetNo.setOnClickListener(this);
		preset1.setOnClickListener(this);
		preset2.setOnClickListener(this);
		preset3.setOnClickListener(this);
		preset4.setOnClickListener(this);
		preset5.setOnClickListener(this);
		preset6.setOnClickListener(this);
		preset7.setOnClickListener(this);
		preset8.setOnClickListener(this);
		preset9.setOnClickListener(this);
		preset10.setOnClickListener(this);
		preset11.setOnClickListener(this);
		preset12.setOnClickListener(this);
		preset13.setOnClickListener(this);
		preset14.setOnClickListener(this);
		preset15.setOnClickListener(this);
		preset16.setOnClickListener(this);
		presteMovePopWindow = new PopupWindow(preLayout, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initIOlinkMovePopupWindow() {
		LinearLayout outLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.alermiooutpopwindow, null);
		TextView outHight = (TextView) outLayout.findViewById(R.id.ioout_hight);
		TextView outLow = (TextView) outLayout.findViewById(R.id.ioout_low);
		outHight.setOnClickListener(this);
		outLow.setOnClickListener(this);
		ioOutLevelPopWindow = new PopupWindow(outLayout, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initMovePopupWindow() {

		LinearLayout layout1 = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.alermsensitivepopwindow, null);
		TextView sensitive10 = (TextView) layout1
				.findViewById(R.id.sensitive_10);
		TextView sensitive9 = (TextView) layout1.findViewById(R.id.sensitive_9);
		TextView sensitive8 = (TextView) layout1.findViewById(R.id.sensitive_8);
		TextView sensitive7 = (TextView) layout1.findViewById(R.id.sensitive_7);
		TextView sensitive6 = (TextView) layout1.findViewById(R.id.sensitive_6);
		TextView sensitive5 = (TextView) layout1.findViewById(R.id.sensitive_5);
		TextView sensitive4 = (TextView) layout1.findViewById(R.id.sensitive_4);
		TextView sensitive3 = (TextView) layout1.findViewById(R.id.sensitive_3);
		TextView sensitive2 = (TextView) layout1.findViewById(R.id.sensitive_2);
		TextView sensitive1 = (TextView) layout1.findViewById(R.id.sensitive_1);
		sensitive10.setOnClickListener(this);
		sensitive9.setOnClickListener(this);
		sensitive8.setOnClickListener(this);
		sensitive7.setOnClickListener(this);
		sensitive6.setOnClickListener(this);
		sensitive5.setOnClickListener(this);
		sensitive4.setOnClickListener(this);
		sensitive3.setOnClickListener(this);
		sensitive2.setOnClickListener(this);
		sensitive1.setOnClickListener(this);
		sensitivePopWindow = new PopupWindow(layout1, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initInputPopupWindow() {
		LinearLayout triggerLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.alermtriggerpopwindow, null);
		TextView tvHight = (TextView) triggerLayout
				.findViewById(R.id.trigger_hight);
		TextView tvLow = (TextView) triggerLayout
				.findViewById(R.id.trigger_low);
		tvLow.setOnClickListener(this);
		tvHight.setOnClickListener(this);
		triggerLevelPopWindow = new PopupWindow(triggerLayout, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private void initAudioPopupWindow() {
		// TODO Auto-generated method stub
		LinearLayout audiotriggerLayout = (LinearLayout) LayoutInflater.from(
				this).inflate(R.layout.alermaudiopopwindow, null);
		TextView senHight = (TextView) audiotriggerLayout
				.findViewById(R.id.trigger_audio_levelhigh);
		TextView senMiddle = (TextView) audiotriggerLayout
				.findViewById(R.id.trigger_audio_levelmiddle);
		TextView senLow = (TextView) audiotriggerLayout
				.findViewById(R.id.trigger_audio_levellow);
		TextView senForbid = (TextView) audiotriggerLayout
				.findViewById(R.id.trigger_audio_levelforbid);
		senHight.setOnClickListener(this);
		senLow.setOnClickListener(this);
		senMiddle.setOnClickListener(this);
		senForbid.setOnClickListener(this);
		audioPopWindow = new PopupWindow(audiotriggerLayout, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}

}
