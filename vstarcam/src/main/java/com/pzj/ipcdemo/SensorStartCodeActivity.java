package com.pzj.ipcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pzj.ipcdemo.service.BridgeService;

import java.util.Timer;
import java.util.TimerTask;

import vstc2.nativecaller.NativeCaller;

public class SensorStartCodeActivity extends Activity implements OnClickListener,BridgeService.SensorSetCodeInterface
{
	private ImageView sucess_code_imageview,code_imageview_type;
	private Button cancleCode;
	private TextView tv_note;
	private RelativeLayout cancle_layout;
	private LinearLayout code_sucess_layout;
	private EditText sensor_name_edit;
	private Button add_sensor_ok;

	private String sensortype, did, pwd;
	private Intent backdata = null;
	private int id1, id2, id3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.startcode_layout);
		getData();
		findview();
		backdata = new Intent();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	private void getData() 
	{
		// TODO Auto-generated method stub
		Intent it = getIntent();
		sensortype = it.getStringExtra("type");
		did = it.getStringExtra("did");
		pwd = it.getStringExtra("pwd");
		NativeCaller.TransferMessage(did,"set_sensorstatus.cgi?cmd=2&loginuse=admin&loginpas=" + pwd + "&user=admin&pwd=" + pwd, 1);
		NativeCaller.TransferMessage(did,
				"trans_cmd_string.cgi?loginuse=admin&loginpas=" + pwd
						+ "&user=admin&pwd=" + pwd + "&cmd=2002&sensor_type="
						+ sensortype, 1);
		startTimer();
		BridgeService.setCodeInterface(this);
	}

	private void findview() {
		// TODO Auto-generated method stub
		code_imageview_type = (ImageView) findViewById(R.id.code_imageview_type);

		cancle_layout = (RelativeLayout) findViewById(R.id.cancle_code_layout);
		code_sucess_layout = (LinearLayout) findViewById(R.id.code_sucess_layout);

		sensor_name_edit = (EditText) findViewById(R.id.sensor_name_edit);
		add_sensor_ok = (Button) findViewById(R.id.add_sensor_ok);
		add_sensor_ok.setOnClickListener(this);

		tv_note = (TextView) findViewById(R.id.note);
		cancleCode = (Button) findViewById(R.id.cancel_code);
		cancleCode.setOnClickListener(this);

		sucess_code_imageview = (ImageView) findViewById(R.id.sucess_code);

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.cancel_code) {
			System.out.println("Complete the alignment, exit the alignment、、、");
			closeTimer(0);

		} else if (i == R.id.add_sensor_ok) {
			goBack();

		} else {
		}
	}

	private void goBack() {
		String sensorname = sensor_name_edit.getText().toString().trim();
		if (sensorname.equals("")) {
//			sensorname = getString(R.string.sensor_list_sensor);
			sensorname=getNullSensorName(id1, id2, id3, type);
		}
		backdata.putExtra("sensorname", sensorname);
		setResult(2013, backdata);
		finish();

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeTimer(0);
			finish();
		}
		return super.onKeyDown(keyCode, event);
		
	}

	/** Timer对象 **/
	private Timer timer = null;
	/** TimerTask对象 **/
	private TimerTask timerTask = null;

	private void startTimer() {
		if (timerTask == null) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					System.out.println("=---------------");
					closeTimer(0);
				}
			};
			timer = new Timer();
			// schedule(TimerTask task, long delay, long period)
			// 安排指定的任务从指定的延迟后开始进行重复的固定延迟执行。
			// task - 所要安排的任务。
			// delay - 执行任务前的延迟时间，单位是毫秒。
			// period - 执行各后续任务之间的时间间隔，单位是毫秒。
			System.out.println("对码中、、、");
			timer.schedule(timerTask, 30*1000);
		}
	}

	private void reStart() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask = null;
		}
	}

	private void closeTimer(int type) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask = null;
		}
		System.out.println("时间到对码完成，退出对码、、、");
		stopCode();
		if (type == 0) {
			finish();
		}

	}

	public void stopCode() {
		NativeCaller.TransferMessage(did,
				"set_sensorstatus.cgi?cmd=3&loginuse=admin&loginpas=" + pwd
						+ "&user=admin&pwd=" + pwd, 1);
	}

	/*
	 * 设置返回空名称的传感器名称
	 */
	private String getNullSensorName(int sensorid1, int sensorid2,
                                     int sensorid3, int snesortype) {
		String sensortype = null;
		if (snesortype == 1) {
			sensortype = getString(R.string.sensor_type_door);
		}
		else if (snesortype == 2) {
			sensortype = getString(R.string.sensor_type_infrared);
		}
		else if (snesortype == 3) {
			sensortype = getString(R.string.sensor_type_smoke);
		}
		else if (snesortype == 4) {
			sensortype = getString(R.string.sensor_type_gas);
		}
		else if (snesortype == 7) {
			sensortype = getString(R.string.sensor_type_remote);
		} 
		else if (snesortype == 10) {
			sensortype = getString(R.string.sensor_type_camera);
		}
		else if (snesortype == 11) {
			sensortype = getString(R.string.sensor_type_curtain);
		}
		else if (snesortype == 8) {
			sensortype = getString(R.string.sensor_type_siren);
		}
		String list = changeTo16(sensorid1) + changeTo16(sensorid2)
				+ changeTo16(sensorid3);
		return sensortype + "-" + list;
	}

	public String changeTo16(int id) {
		String b = Integer.toHexString(id).toUpperCase();

		return id < 10 ? "0" + b : b;
	}

	private void setSensorName(String name, int id) {
		Log.i("info", "list 设置默认名称id:" + id);
		NativeCaller.TransferMessage(did, "set_sensorname.cgi?" + "&sensorid="
				+ id + "&sensorname=" + name + "&loginuse=admin&loginpas="
				+ pwd, 1);
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			String name = (String) msg.obj;
			if (what == 1) {
				code_imageview_type.setBackgroundResource(R.drawable.magnetic);
			}
			else if (what == 3) {
				code_imageview_type.setBackgroundResource(R.drawable.somke);
			} 
			else if (what == 4) {
				code_imageview_type.setBackgroundResource(R.drawable.gas);
			}
			else if (what == 7) {
				code_imageview_type.setBackgroundResource(R.drawable.control);
			} 
			else if (what == 8){
				code_imageview_type.setBackgroundResource(R.drawable.sensor_siren_icon);
			}
			else{
				code_imageview_type.setBackgroundResource(R.drawable.infrared);
			}

			sensor_name_edit.setText(name);
			sucess_code_imageview.setVisibility(View.VISIBLE);
			cancle_layout.setVisibility(View.GONE);
			code_sucess_layout.setVisibility(View.VISIBLE);
		};
	};

	public int type;

	@Override
	public void CallBackReCodeMessage(String did, String name, int headcmd,
                                      int selfcmd, int linkpreset, int sensortype, int sensoraction,
                                      int channel, int sensorid1, int sensorid2, int sensorid3) {
		// TODO Auto-generated method stub
		if (sensorid1 == 0 && sensorid2 == 0 && sensorid3 == 0 || sensorid1 == 255 && sensorid2 == 255 && sensorid3 == 255)
		{
			return;
		}
		Log.i("info", "SensorStartCodeActivity:" + did);
		System.out.println("重新对码返回---------------!" + did + ",id1" + sensorid1);
		closeTimer(1);

		Log.i("info", "对码返回数据都不为空，有有效的ID");

		 if (name.trim().length() == 0 || name.trim().equals("")) {
		 Log.i("info", "对码返回的名称为空值，设置为默认值");
		 name = getNullSensorName(sensorid1, sensorid2, sensorid3,
		 sensortype);
		 Log.i("info", "1---new name:" + name);
		 }
		Message msg = new Message();
		msg.what = sensortype;
		msg.obj = name;
		myHandler.sendMessage(msg);
		id1 = sensorid1;
		id2 = sensorid2;
		id3 = sensorid3;
		type = sensortype;

		backdata.putExtra("linkpreset", linkpreset);
		backdata.putExtra("sensorid1", sensorid1);
		backdata.putExtra("sensorid2", sensorid2);
		backdata.putExtra("sensorid3", sensorid3);
		backdata.putExtra("sensortype", sensortype);
		backdata.putExtra("sensorname", name);
		backdata.putExtra("id", channel);

	}

}
