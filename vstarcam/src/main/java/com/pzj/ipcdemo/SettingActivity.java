package com.pzj.ipcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.pzj.ipcdemo.utils.ContentCommon;


/**
 * 
 * @author
 * 设备系统设置
 */
public class SettingActivity extends Activity implements OnClickListener
{

	private String strDID;
	private String cameraName;
	private String cameraPwd;
	//控件声明
	private RelativeLayout wifi_Relat,pwd_Relat,alarm_Relat,time_Relat,sd_Relat,tf_Relat,update,sensor,rl_move_inform;
	private Button back_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		getDataFromOther();
		initView();		
	}

	//获取activity传过来的数据
	private void getDataFromOther()
	{
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		cameraPwd=intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
	}
	//初始化控件
	private void initView()
	{
		wifi_Relat=(RelativeLayout) findViewById(R.id.wifi_setting);
		pwd_Relat=(RelativeLayout) findViewById(R.id.pwd_setting);
		alarm_Relat=(RelativeLayout) findViewById(R.id.alarm_setting);
		time_Relat=(RelativeLayout) findViewById(R.id.time_setting);
		sd_Relat=(RelativeLayout) findViewById(R.id.sd_setting);
		tf_Relat=(RelativeLayout) findViewById(R.id.tf_setting);
		update=(RelativeLayout) findViewById(R.id.update_firmware);
		back_btn=(Button) findViewById(R.id.back);
		sensor=(RelativeLayout) findViewById(R.id.setting_sensor);
		rl_move_inform = (RelativeLayout) findViewById(R.id.rl_move_inform);
		
		wifi_Relat.setOnClickListener(this);
		pwd_Relat.setOnClickListener(this);
		alarm_Relat.setOnClickListener(this);
		time_Relat.setOnClickListener(this);
		sd_Relat.setOnClickListener(this);
		tf_Relat.setOnClickListener(this);
		update.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		sensor.setOnClickListener(this);
		rl_move_inform.setOnClickListener(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.wifi_setting) {
			Intent intent1 = new Intent(SettingActivity.this, SettingWifiActivity.class);
			intent1.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent1.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intent1.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.pwd_setting) {
			Intent intent2 = new Intent(SettingActivity.this, SettingUserActivity.class);
			intent2.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent2.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intent2.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.alarm_setting) {
			Intent intent3 = new Intent(SettingActivity.this, SettingAlarmActivity.class);
			intent3.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent3.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intent3.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			startActivity(intent3);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.time_setting) {
			Intent intent4 = new Intent(SettingActivity.this, SettingDateActivity.class);
			intent4.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent4.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intent4.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			startActivity(intent4);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.sd_setting) {
			Intent intent5 = new Intent(SettingActivity.this, SettingSDCardActivity.class);
			intent5.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent5.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intent5.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			startActivity(intent5);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		} else if (i == R.id.tf_setting) {
			Intent intentVid = new Intent(SettingActivity.this, PlayBackTFActivity.class);
			intentVid.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentVid.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intentVid.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			intentVid.putExtra(ContentCommon.STR_CAMERA_USER, "admin");
			startActivity(intentVid);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.update_firmware) {
			Intent intentup = new Intent(SettingActivity.this, FirmwareUpdateActiviy.class);
			intentup.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentup.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			startActivity(intentup);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.setting_sensor) {
			Intent intentsen = new Intent(SettingActivity.this, SensorListActivty.class);
			intentsen.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			intentsen.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			intentsen.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			startActivity(intentsen);

		} else if (i == R.id.rl_move_inform) {
			Intent intentalam = new Intent(SettingActivity.this, MoveNotificationActivity.class);
			intentalam.putExtra(ContentCommon.STR_CAMERA_PWD, cameraPwd);
			intentalam.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			startActivity(intentalam);

		} else if (i == R.id.back) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);


		} else {
		}
	}
	
}