package com.pzj.ipcdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.pzj.ipcdemo.adapter.PushVideoTimingAdapter;
import com.pzj.ipcdemo.bean.AlermBean;
import com.pzj.ipcdemo.bean.SwitchBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;
import com.pzj.ipcdemo.view.MyListView;

import java.util.HashMap;
import java.util.Map;

import vstc2.nativecaller.NativeCaller;


public class MoveNotificationActivity extends BaseActivity implements OnClickListener,BridgeService.PushTimingInterface,BridgeService.CallBack_AlarmParamsInterface {
	
	private static String strDID = null;// camera id
	private static String strPWD = null;// camera pwd
	private Button btn_infoback; //返回
	/**
	 * 移动侦测报警通知
	 */
	private MyListView lv_info_plan; //列表
	private RelativeLayout rl_add_infoplan; //添加
	private PushVideoTimingAdapter pushAdapter = null; //adapter
	private static Map<Integer, Integer> pushplan;
	private static String pushmark = "147258369";
	private static SwitchBean switchBean; //
	private static AlermBean alermBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.move_message);
		switchBean = new SwitchBean();
		alermBean = new AlermBean();
		findView();
		setLister();
		BridgeService.setPushTimingInterface(this);
		BridgeService.setCallBack_AlarmParamsInterface(this);
		waitHandler.sendEmptyMessageDelayed(1, 100);
		waitHandler.sendEmptyMessageDelayed(2, 100);

	}
	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		strPWD = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);

	}
	public void setLister(){
		
		btn_infoback.setOnClickListener(this);
		rl_add_infoplan.setOnClickListener(this);
	}
	
	//初始化控件
	public void findView(){
		btn_infoback = (Button) findViewById(R.id.btn_infoback);
		lv_info_plan = (MyListView) findViewById(R.id.lv_info_plan);
		rl_add_infoplan = (RelativeLayout) findViewById(R.id.rl_add_infoplan);
		// 移动侦测推送
				pushplan = new HashMap<Integer, Integer>();
				pushAdapter = new PushVideoTimingAdapter(MoveNotificationActivity.this);
				lv_info_plan.setAdapter(pushAdapter);
				lv_info_plan.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v, int position,
                                            long id) {
						// TODO Auto-generated method stub
						Map<Integer, Integer> item = pushAdapter.movetiming.get(position);
						int itemplan = item.entrySet().iterator().next().getValue();
						int itemplanKey = item.entrySet().iterator().next().getKey();
						Intent it = new Intent(MoveNotificationActivity.this,
								SCameraSetPushVideoTiming.class);
						it.putExtra("type", 1);
						it.putExtra("value", itemplan);
						it.putExtra("key", itemplanKey);
						startActivityForResult(it, 1);
					}

				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.btn_infoback) {
			finish();

		} else if (i == R.id.rl_add_infoplan) {
			Intent intent1 = new Intent(MoveNotificationActivity.this,
					SCameraSetPushVideoTiming.class);
			intent1.putExtra("type", 0);
			startActivityForResult(intent1, 0);

		} else {
		}
	}
	private static Handler waitHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				NativeCaller.TransferMessage(strDID,
						"trans_cmd_string.cgi?cmd=2017&command=11&mark="
								+ pushmark + "&type=2&loginuse=" + "admin"
								+ "&loginpas=" + strPWD, 1);
				break;
			case 2:
				NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 2011) { // 移动侦测推送添加
			int time = data.getIntExtra("jnitime", 1);
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = time;
			upHandler.sendMessage(msg);
		}
		if (resultCode == 2012) { // 移动侦测推送编辑
			int time = data.getIntExtra("jnitime", 1);
			int key = data.getIntExtra("key", -1);
			if (key == -1)
				return;
			Message msg = new Message();
			msg.what = 2;
			msg.arg1 = time;
			msg.arg2 = key;
			upHandler.sendMessage(msg);
		}
		if (resultCode == 2013) { // 移动侦测推送删除
			int key = data.getIntExtra("key", -1);
			if (key == -1)
				return;
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = key;
			deleteHandler.sendMessage(msg);
		}

	}
	private Handler upHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			int time = msg.arg1;
			switch (what) {
			case 1:
				for (int i = 1; i < 22; i++) {
					int value = pushplan.get(i);
					if (value == -1 || value == 0) {
						pushplan.put(i, time);
						pushAdapter.addPlan(i, time);
						break;
					}
				}
				pushAdapter.notifyDataSetChanged();
				setAlarmHandler.sendEmptyMessage(1);
				break;
			case 2:
				int pushkey = msg.arg2;
				pushplan.put(pushkey, time);
				pushAdapter.notify(pushkey, time);
				pushAdapter.notifyDataSetChanged();
				setAlarmHandler.sendEmptyMessage(1);
				break;
			default:
				break;
			}

		};
	};
	/**
	 * 删除定制计划
	 */
	private Handler deleteHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int delpushkey = msg.arg1;
				pushAdapter.removePlan(delpushkey);
				pushplan.put(delpushkey, -1);
				pushAdapter.notifyDataSetChanged();
				setAlarmHandler.sendEmptyMessage(1);
				break;
			default:
				break;
			}
		};
	};
	private Handler setAlarmHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setPushTiming(strDID, strPWD);
				break;
			default:
				break;
			}
		};
	};
	private Handler callbackHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				for (int i = 1; i < 22; i++) {
					int plan = pushplan.get(i);
					if (plan != 0 && plan != -1) {
						pushAdapter.addPlan(i, plan);
						pushAdapter.notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
			}

		};
	};
	/**
	 * 设置移动侦测推送
	 *
	 * @param udid
	 * @param pwd
	 */

	private static void setPushTiming(String udid, String pwd) {
		NativeCaller.TransferMessage(udid,
				"trans_cmd_string.cgi?cmd=2017&command=2&mark=" + pushmark
						+ "&motion_push_plan1=" + pushplan.get(1)
						+ "&motion_push_plan2=" + pushplan.get(2)
						+ "&motion_push_plan3=" + pushplan.get(3)
						+ "&motion_push_plan4=" + pushplan.get(4)
						+ "&motion_push_plan5=" + pushplan.get(5)
						+ "&motion_push_plan6=" + pushplan.get(6)
						+ "&motion_push_plan7=" + pushplan.get(7)
						+ "&motion_push_plan8=" + pushplan.get(8)
						+ "&motion_push_plan9=" + pushplan.get(9)
						+ "&motion_push_plan10=" + pushplan.get(10)
						+ "&motion_push_plan11=" + pushplan.get(11)
						+ "&motion_push_plan12=" + pushplan.get(12)
						+ "&motion_push_plan13=" + pushplan.get(13)
						+ "&motion_push_plan14=" + pushplan.get(14)
						+ "&motion_push_plan15=" + pushplan.get(15)
						+ "&motion_push_plan16=" + pushplan.get(16)
						+ "&motion_push_plan17=" + pushplan.get(17)
						+ "&motion_push_plan18=" + pushplan.get(18)
						+ "&motion_push_plan19=" + pushplan.get(19)
						+ "&motion_push_plan20=" + pushplan.get(20)
						+ "&motion_push_plan21=" + pushplan.get(21)
						+ "&motion_push_plan_enable=" + switchBean.getMotion_push_plan_enable()
						+ "&loginuse=" + "admin" + "&loginpas=" + pwd, -1);
	}

	@Override
	public void PushTimingCallback(String did, String command, String mask,
                                   String motion_push_plan1, String motion_push_plan2,
                                   String motion_push_plan3, String motion_push_plan4,
                                   String motion_push_plan5, String motion_push_plan6,
                                   String motion_push_plan7, String motion_push_plan8,
                                   String motion_push_plan9, String motion_push_plan10,
                                   String motion_push_plan11, String motion_push_plan12,
                                   String motion_push_plan13, String motion_push_plan14,
                                   String motion_push_plan15, String motion_push_plan16,
                                   String motion_push_plan17, String motion_push_plan18,
                                   String motion_push_plan19, String motion_push_plan20,
                                   String motion_push_plan21, String motion_push_enable) {
		// TODO Auto-generated method stub
		if (did.contains(did)) {
			if (mask.contains(pushmark)) {

				pushplan.put(1, Integer.valueOf(motion_push_plan1));
				pushplan.put(2, Integer.valueOf(motion_push_plan2));
				pushplan.put(3, Integer.valueOf(motion_push_plan3));
				pushplan.put(4, Integer.valueOf(motion_push_plan4));
				pushplan.put(5, Integer.valueOf(motion_push_plan5));
				pushplan.put(6, Integer.valueOf(motion_push_plan6));
				pushplan.put(7, Integer.valueOf(motion_push_plan7));
				pushplan.put(8, Integer.valueOf(motion_push_plan8));
				pushplan.put(9, Integer.valueOf(motion_push_plan9));
				pushplan.put(10, Integer.valueOf(motion_push_plan10));
				pushplan.put(11, Integer.valueOf(motion_push_plan11));
				pushplan.put(12, Integer.valueOf(motion_push_plan12));
				pushplan.put(13, Integer.valueOf(motion_push_plan13));
				pushplan.put(14, Integer.valueOf(motion_push_plan14));
				pushplan.put(15, Integer.valueOf(motion_push_plan15));
				pushplan.put(16, Integer.valueOf(motion_push_plan16));
				pushplan.put(17, Integer.valueOf(motion_push_plan17));
				pushplan.put(18, Integer.valueOf(motion_push_plan18));
				pushplan.put(19, Integer.valueOf(motion_push_plan19));
				pushplan.put(20, Integer.valueOf(motion_push_plan20));
				pushplan.put(21, Integer.valueOf(motion_push_plan21));
				switchBean.setMotion_push_plan_enable(motion_push_enable);
				callbackHandler.sendEmptyMessage(1);
			}
			}
	}


	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case 0:// 设置失败
				Toast.makeText(MoveNotificationActivity.this, R.string.alerm_set_failed, Toast.LENGTH_LONG)
				.show();
				break;
			case 1:// 设置成功
					// showToast(R.string.alerm_set)
				//finish();
				break;
			case 2:// 回调成功
				Log.e("1111alermBean.getMotion_armed()", "***"+alermBean.getMotion_armed());
				if (alermBean.getMotion_armed() == 0) {// 移动侦测布防0-不布防，1-布防
					Log.e("1111111", "11111");
						setSDLing();
					} 
			
				break;
			case 4:
				// editUploadPicInterval.setText("");
				break;

			default:
				break;
			}
		}
	};
	
	//打开移动侦测布防
	private static void setSDLing() {
		NativeCaller.PPPPAlarmSetting(strDID, alermBean.getAlarm_audio(),
				1,
				alermBean.getMotion_sensitivity(),
				alermBean.getInput_armed(), alermBean.getIoin_level(),
				alermBean.getIoout_level(), alermBean.getIolinkage(),
				alermBean.getAlermpresetsit(), alermBean.getMail(),
				alermBean.getSnapshot(),1,
				alermBean.getUpload_interval(),
				alermBean.getSchedule_enable(),
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				
				alermBean.getDefense_plan1(),
				alermBean.getDefense_plan2(),
				alermBean.getDefense_plan3(),
				alermBean.getDefense_plan4(),
				alermBean.getDefense_plan5(),
				alermBean.getDefense_plan6(),
				alermBean.getDefense_plan7(),
				alermBean.getDefense_plan8(),
				alermBean.getDefense_plan9(),
				alermBean.getDefense_plan10(),
				alermBean.getDefense_plan11(),
				alermBean.getDefense_plan12(),
				alermBean.getDefense_plan13(),
				alermBean.getDefense_plan14(),
				alermBean.getDefense_plan15(),
				alermBean.getDefense_plan16(),
				alermBean.getDefense_plan17(),
				alermBean.getDefense_plan18(),
				alermBean.getDefense_plan19(),
				alermBean.getDefense_plan20(),
				alermBean.getDefense_plan21(),-1
				);


}
	@Override
	public void CallBack_AlarmParams(String did, int alarm_audio,
                                     int motion_armed, int motion_sensitivity, int input_armed,
                                     int ioin_level, int iolinkage, int ioout_level, int alarmpresetsit,
                                     int mail, int snapshot, int record, int upload_interval,
                                     int schedule_enable, int schedule_sun_0, int schedule_sun_1,
                                     int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
                                     int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
                                     int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
                                     int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
                                     int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
                                     int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
                                     int schedule_sat_2, int defense_plan1, int defense_plan2,
                                     int defense_plan3, int defense_plan4, int defense_plan5,
                                     int defense_plan6, int defense_plan7, int defense_plan8,
                                     int defense_plan9, int defense_plan10, int defense_plan11,
                                     int defense_plan12, int defense_plan13, int defense_plan14,
                                     int defense_plan15, int defense_plan16, int defense_plan17,
                                     int defense_plan18, int defense_plan19, int defense_plan20,
                                     int defense_plan21) {
		// TODO Auto-generated method stub
		if (strDID.equals(did)) {
			alermBean.setDid(did);
			alermBean.setAlarm_audio(alarm_audio);
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
			alermBean.setSchedule_enable(1);

			alermBean.setSchedule_sun_0(schedule_sun_0);
			alermBean.setSchedule_sun_1(schedule_sun_1);
			alermBean.setSchedule_sun_2(schedule_sun_2);
			alermBean.setSchedule_mon_0(schedule_mon_0);
			alermBean.setSchedule_mon_1(schedule_mon_1);
			alermBean.setSchedule_mon_2(schedule_mon_2);
			alermBean.setSchedule_tue_0(schedule_tue_0);
			alermBean.setSchedule_tue_1(schedule_tue_1);
			alermBean.setSchedule_tue_2(schedule_tue_2);
			alermBean.setSchedule_wed_0(schedule_wed_0);
			alermBean.setSchedule_wed_1(schedule_wed_1);
			alermBean.setSchedule_wed_2(schedule_wed_2);
			alermBean.setSchedule_thu_0(schedule_thu_0);
			alermBean.setSchedule_thu_1(schedule_thu_1);
			alermBean.setSchedule_thu_2(schedule_thu_2);
			alermBean.setSchedule_fri_0(schedule_fri_0);
			alermBean.setSchedule_fri_1(schedule_fri_1);
			alermBean.setSchedule_fri_2(schedule_fri_2);
			alermBean.setSchedule_sat_0(schedule_sat_0);
			alermBean.setSchedule_sat_1(schedule_sat_1);
			alermBean.setSchedule_sat_2(schedule_sat_2);

			alermBean.setDefense_plan1(defense_plan1);
			alermBean.setDefense_plan2(defense_plan2);
			alermBean.setDefense_plan3(defense_plan3);
			alermBean.setDefense_plan4(defense_plan4);
			alermBean.setDefense_plan5(defense_plan5);
			alermBean.setDefense_plan6(defense_plan6);
			alermBean.setDefense_plan7(defense_plan7);
			alermBean.setDefense_plan8(defense_plan8);
			alermBean.setDefense_plan9(defense_plan9);
			alermBean.setDefense_plan10(defense_plan10);
			alermBean.setDefense_plan11(defense_plan11);
			alermBean.setDefense_plan12(defense_plan12);
			alermBean.setDefense_plan13(defense_plan13);
			alermBean.setDefense_plan14(defense_plan14);
			alermBean.setDefense_plan15(defense_plan15);
			alermBean.setDefense_plan16(defense_plan16);
			alermBean.setDefense_plan17(defense_plan17);
			alermBean.setDefense_plan18(defense_plan18);
			alermBean.setDefense_plan19(defense_plan19);
			alermBean.setDefense_plan20(defense_plan20);
			alermBean.setDefense_plan21(defense_plan21);

			mHandler.sendEmptyMessage(2);
		}
	}
}
