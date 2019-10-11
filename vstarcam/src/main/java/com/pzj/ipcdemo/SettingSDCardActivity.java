package com.pzj.ipcdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pzj.ipcdemo.adapter.MoveVideoTimingAdapter;
import com.pzj.ipcdemo.adapter.VideoTimingAdapter;
import com.pzj.ipcdemo.bean.AlermBean;
import com.pzj.ipcdemo.bean.SdcardBean;
import com.pzj.ipcdemo.bean.SwitchBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;
import com.pzj.ipcdemo.view.MyListView;


import java.util.HashMap;
import java.util.Map;

import vstc2.nativecaller.NativeCaller;

public class SettingSDCardActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener, BridgeService.SDCardInterface,BridgeService.VideoTimingInterface,BridgeService.TimingInterface,BridgeService.CallBack_AlarmParamsInterface {
	private TextView tvSdTotal = null;
	private TextView tvSdRemain = null;
	private TextView tvSdStatus = null;
	private Button btnFormat = null;
	private CheckBox cbxConverage = null;
	private EditText editRecordLength = null;
	private CheckBox cbxRecordTime = null;
	private Button btnBack = null;
	private Button btnOk = null;
	private final int TIMEOUT = 3000;
	private static String strDID = null;// camera id
	private static String strPWD = null;// camera pwd
	// private String cameraName = null;
	private ProgressDialog progressDialog = null;
	private boolean successFlag = false;// 获取和设置的结果
	private final int FAILED = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 2;
	private CheckBox iv_video_mode; //录像模式
	private RelativeLayout rl_add_plan; //点击添加录像计划
	private RelativeLayout rl_add_move_plan; //点击添加移动侦测录像
	private RelativeLayout rl_plan_all; //录像模式整个布局
	private static String cmark = "147258369"; //APP唯一标示
	private static SwitchBean switchBean;
	private static AlermBean alermBean;
	/**
	 * 添加计划录像
	 */
	private MyListView lv_video_plan; //计划录像列表
	private VideoTimingAdapter adapter = null;
	private Map<Integer, Integer> planmap;
	/**
	 * 移动侦测录像
	 */
	private MyListView lv_move_plan; //移动侦测录像列表
	private MoveVideoTimingAdapter moveAdapter = null;
	private Map<Integer, Integer> moveplanmap;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAILED:
				showToast(R.string.sdcard_set_failed);
				break;
			case SUCCESS:
				showToast(R.string.sdcard_set_success);
				finish();
				break;
			case PARAMS:
				successFlag = true;
				progressDialog.dismiss();
				tvSdTotal.setText(sdcardBean.getSdtotal() + "MB");
				tvSdRemain.setText(sdcardBean.getSdfree() + "MB");

				if (sdcardBean.getRecord_sd_status() == 1)
				{
					tvSdStatus
							.setText(SettingSDCardActivity.this.getResources()
									.getString(R.string.sdcard_inserted));
				}
				else if (sdcardBean.getRecord_sd_status() == 2)
				{
					tvSdStatus.setText(getString(R.string.sdcard_video));
				}
				else if(sdcardBean.getRecord_sd_status() == 3)
				{
					tvSdStatus.setText(getString(R.string.sdcard_file_error));
				}
				else if(sdcardBean.getRecord_sd_status() == 4)
				{
					tvSdStatus.setText(getString(R.string.sdcard_isformatting));
				}
				else {
					tvSdStatus.setText(SettingSDCardActivity.this
							.getResources().getString(
									R.string.sdcard_status_info));
				}
				cbxConverage.setChecked(true);
				if (sdcardBean.getRecord_time_enable() == 1) {
					cbxRecordTime.setChecked(true);
				} else {
					cbxRecordTime.setChecked(false);
				}
				// editRecordLength.setText(sdcardBean.getRecord_timer() + "");
				editRecordLength.setText(15 + "");
				break;
			default:
				break;
			}

		}
	};

//	@Override
//	protected void onPause() {
//		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);//动画
//		super.onPause();
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingsdcard);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.sdcard_getparams));
		progressDialog.show();
		sdcardBean = new SdcardBean();
		switchBean = new SwitchBean();
		alermBean = new AlermBean();
		handler.postDelayed(runnable, TIMEOUT);
		findView();
		setLister();
		BridgeService.setSDCardInterface(this);
		BridgeService.setVideoTimingInterface(this);
		BridgeService.setTimingInterface(this);
		BridgeService.setCallBack_AlarmParamsInterface(this);
		waitHandler.sendEmptyMessageDelayed(3, 100);
		waitHandler.sendEmptyMessageDelayed(1, 100);
		waitHandler.sendEmptyMessageDelayed(2, 100);
		waitHandler.sendEmptyMessageDelayed(4, 100);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		strPWD = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
		Log.i("info", "did:" + strDID);
		// cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				successFlag = false;
				progressDialog.dismiss();
			}
		}
	};
	private SdcardBean sdcardBean;

	private void setLister() {
		btnBack.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnFormat.setOnClickListener(this);
		rl_add_plan.setOnClickListener(this);
		rl_add_move_plan.setOnClickListener(this);
		cbxConverage.setOnCheckedChangeListener(this);
		cbxRecordTime.setOnCheckedChangeListener(this);
		iv_video_mode.setOnCheckedChangeListener(this);

	}

	private void findView() {
		tvSdTotal = (TextView) findViewById(R.id.tv_sd_total);
		tvSdRemain = (TextView) findViewById(R.id.tv_sd_remain);
		tvSdStatus = (TextView) findViewById(R.id.tv_state);
		btnFormat = (Button) findViewById(R.id.btn_format);
		cbxConverage = (CheckBox) findViewById(R.id.cbx_coverage);
		editRecordLength = (EditText) findViewById(R.id.edit_record_length);
		cbxRecordTime = (CheckBox) findViewById(R.id.cbx_record_time);
		btnBack = (Button) findViewById(R.id.back);
		btnOk = (Button) findViewById(R.id.ok);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		layout.setBackgroundDrawable(drawable);
		iv_video_mode = (CheckBox) findViewById(R.id.iv_video_mode);
		rl_add_plan = (RelativeLayout) findViewById(R.id.rl_add_plan);
		rl_add_move_plan = (RelativeLayout) findViewById(R.id.rl_add_move_plan);
		rl_plan_all = (RelativeLayout) findViewById(R.id.rl_plan_all);

		lv_video_plan = (MyListView) findViewById(R.id.lv_video_plan);
		lv_move_plan = (MyListView) findViewById(R.id.lv_move_plan);
		//计划录像
		adapter = new VideoTimingAdapter(SettingSDCardActivity.this);
		lv_video_plan.setAdapter(adapter);
		planmap = new HashMap<Integer, Integer>();
		lv_video_plan.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
				// TODO Auto-generated method stub
				Map<Integer, Integer> item = adapter.sdtiming.get(position);
				int itemplan = item.entrySet().iterator().next().getValue();
				int itemplanKey = item.entrySet().iterator().next().getKey();
				Intent it = new Intent(SettingSDCardActivity.this,
						SCameraSetSDTiming.class);
				it.putExtra("type", 1);
				it.putExtra("value", itemplan);
				it.putExtra("key", itemplanKey);
				startActivityForResult(it, 1);
			}

		});
		//移动侦测录像
		moveAdapter = new MoveVideoTimingAdapter(SettingSDCardActivity.this);
		lv_move_plan.setAdapter(moveAdapter);
		moveplanmap = new HashMap<Integer, Integer>(); //移动侦测录像
		lv_move_plan.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
				// TODO Auto-generated method stub
				Map<Integer, Integer> moveitem = moveAdapter.movevideotiming.get(position);
				int moveitemplan = moveitem.entrySet().iterator().next().getValue();
				int moveitemplanKey = moveitem.entrySet().iterator().next().getKey();
				Intent itent = new Intent(SettingSDCardActivity.this,
						SCameraSetPlanVideoTiming.class);
				itent.putExtra("type", 1);
				itent.putExtra("value", moveitemplan);
				itent.putExtra("key", moveitemplanKey);
				startActivityForResult(itent, 1);
			}

		});


	}

	private static Handler waitHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:

				NativeCaller.TransferMessage(strDID,
						"trans_cmd_string.cgi?cmd=2017&command=11&mark="+cmark+"&type=3&loginuse="
								+ "admin" + "&loginpas=" + strPWD, 1);

				break;
			case 2:
				NativeCaller.TransferMessage(strDID,
						"trans_cmd_string.cgi?cmd=2017&command=11&mark="+cmark+"&type=1&loginuse="
								+ "admin" + "&loginpas=" + strPWD, 1);
				break;
			case 3:
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_TYPE_GET_RECORD);
				break;
			case 4:
				NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_PARAMS);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		int i = v.getId();//计划录像
//移动侦测计划
		if (i == R.id.back) {
			finish();

		} else if (i == R.id.ok) {
			setSDCardSchedule();

		} else if (i == R.id.btn_format) {
			showFormatDialog();

		} else if (i == R.id.rl_add_plan) {
			Intent it = new Intent(SettingSDCardActivity.this,
					SCameraSetSDTiming.class);
			it.putExtra("type", 0);
			startActivityForResult(it, 0);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else if (i == R.id.rl_add_move_plan) {
			Intent intent = new Intent(SettingSDCardActivity.this, SCameraSetPlanVideoTiming.class);
			intent.putExtra("type", 0);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

		} else {
		}
	}

	void showFormatDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.sdcard_formatsd);
		adb.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.i("info", "格式化");
						NativeCaller.FormatSD(strDID);
						dialog.dismiss();
					}

				})
				.setNegativeButton(R.string.str_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).create().show();
	}

	// 20140226修改测试

	private void setSDCardSchedule() {

		if (sdcardBean.getRecord_time_enable() == 0) {
			sdcardBean.setSun_0(0);
			sdcardBean.setSun_1(0);
			sdcardBean.setSun_2(0);
			sdcardBean.setMon_0(0);
			sdcardBean.setMon_1(0);
			sdcardBean.setMon_2(0);
			sdcardBean.setTue_0(0);
			sdcardBean.setTue_1(0);
			sdcardBean.setTue_2(0);
			sdcardBean.setWed_0(0);
			sdcardBean.setWed_1(0);
			sdcardBean.setWed_2(0);
			sdcardBean.setThu_0(0);
			sdcardBean.setThu_1(0);
			sdcardBean.setThu_2(0);
			sdcardBean.setFri_0(0);
			sdcardBean.setFri_1(0);
			sdcardBean.setFri_2(0);
			sdcardBean.setSat_0(0);
			sdcardBean.setSat_1(0);
			sdcardBean.setSat_2(0);
		} else {
			sdcardBean.setSun_0(-1);
			sdcardBean.setSun_1(-1);
			sdcardBean.setSun_2(-1);
			sdcardBean.setMon_0(-1);
			sdcardBean.setMon_1(-1);
			sdcardBean.setMon_2(-1);
			sdcardBean.setTue_0(-1);
			sdcardBean.setTue_1(-1);
			sdcardBean.setTue_2(-1);
			sdcardBean.setWed_0(-1);
			sdcardBean.setWed_1(-1);
			sdcardBean.setWed_2(-1);
			sdcardBean.setThu_0(-1);
			sdcardBean.setThu_1(-1);
			sdcardBean.setThu_2(-1);
			sdcardBean.setFri_0(-1);
			sdcardBean.setFri_1(-1);
			sdcardBean.setFri_2(-1);
			sdcardBean.setSat_0(-1);
			sdcardBean.setSat_1(-1);
			sdcardBean.setSat_2(-1);
		}

		sdcardBean.setRecord_timer(15);
		NativeCaller.PPPPSDRecordSetting(strDID,
				sdcardBean.getRecord_conver_enable(),
				sdcardBean.getRecord_timer(), sdcardBean.getRecord_size(),sdcardBean.getRecord_chnl(),
				sdcardBean.getRecord_time_enable(), sdcardBean.getSun_0(),
				sdcardBean.getSun_1(), sdcardBean.getSun_2(),
				sdcardBean.getMon_0(), sdcardBean.getMon_1(),
				sdcardBean.getMon_2(), sdcardBean.getTue_0(),
				sdcardBean.getTue_1(), sdcardBean.getTue_2(),
				sdcardBean.getWed_0(), sdcardBean.getWed_1(),
				sdcardBean.getWed_2(), sdcardBean.getThu_0(),
				sdcardBean.getThu_1(), sdcardBean.getThu_2(),
				sdcardBean.getFri_0(), sdcardBean.getFri_1(),
				sdcardBean.getFri_2(), sdcardBean.getSat_0(),
				sdcardBean.getSat_1(), sdcardBean.getSat_2(),sdcardBean.getEnable_audio());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		int i = v.getId();// case R.id.cbx_coverage:
// if (isChecked) {
// sdcardBean.setRecord_conver_enable(1);
// } else {
// sdcardBean.setRecord_conver_enable(0);
// }
// break;
		if (i == R.id.cbx_record_time) {
			if (isChecked) {
				sdcardBean.setRecord_time_enable(1);
			} else {
				sdcardBean.setRecord_time_enable(0);
			}

		} else if (i == R.id.iv_video_mode) {
			if (isChecked) {
				rl_plan_all.setVisibility(View.VISIBLE);
			} else {
				rl_plan_all.setVisibility(View.GONE);
			}

		} else {
		}

	}

	@Override
	public void callBackRecordSchParams(String did, int record_cover_enable,
                                        int record_timer, int record_size, int record_time_enable,
                                        int record_schedule_sun_0, int record_schedule_sun_1,
                                        int record_schedule_sun_2, int record_schedule_mon_0,
                                        int record_schedule_mon_1, int record_schedule_mon_2,
                                        int record_schedule_tue_0, int record_schedule_tue_1,
                                        int record_schedule_tue_2, int record_schedule_wed_0,
                                        int record_schedule_wed_1, int record_schedule_wed_2,
                                        int record_schedule_thu_0, int record_schedule_thu_1,
                                        int record_schedule_thu_2, int record_schedule_fri_0,
                                        int record_schedule_fri_1, int record_schedule_fri_2,
                                        int record_schedule_sat_0, int record_schedule_sat_1,
                                        int record_schedule_sat_2, int record_sd_status, int sdtotal,
                                        int sdfree, int enable_audio) {
		Log.i("info", "---record_cover_enable" + record_cover_enable
				+ "---record_time_enable" + record_time_enable
				+ "---record_timer" + record_timer);
		Log.i("info", "record_schedule_sun_0:" + record_schedule_sun_0
				+ ",record_schedule_sun_1:" + record_schedule_sun_1
				+ ",record_schedule_sun_2:" + record_schedule_sun_2
				+ ",record_schedule_mon_0:" + record_schedule_mon_0
				+ ",record_schedule_mon_1:" + record_schedule_mon_1
				+ ",record_schedule_mon_2:" + record_schedule_mon_2);
		sdcardBean.setDid(did);
		sdcardBean.setRecord_conver_enable(record_cover_enable);
		sdcardBean.setRecord_timer(record_timer);
		sdcardBean.setRecord_size(record_size);
		sdcardBean.setRecord_time_enable(record_time_enable);
		sdcardBean.setRecord_sd_status(record_sd_status);
		sdcardBean.setSdtotal(sdtotal);
		sdcardBean.setSdfree(sdfree);
		sdcardBean.setSun_0(record_schedule_sun_0);
		sdcardBean.setSun_1(record_schedule_sun_1);
		sdcardBean.setSun_2(record_schedule_sun_2);
		sdcardBean.setMon_0(record_schedule_mon_0);
		sdcardBean.setMon_1(record_schedule_mon_1);
		sdcardBean.setMon_2(record_schedule_mon_2);
		sdcardBean.setTue_0(record_schedule_tue_0);
		sdcardBean.setTue_1(record_schedule_tue_1);
		sdcardBean.setTue_2(record_schedule_tue_2);
		sdcardBean.setWed_0(record_schedule_wed_0);
		sdcardBean.setWed_1(record_schedule_wed_1);
		sdcardBean.setWed_2(record_schedule_wed_2);
		sdcardBean.setThu_0(record_schedule_thu_0);
		sdcardBean.setThu_1(record_schedule_thu_1);
		sdcardBean.setThu_2(record_schedule_thu_2);
		sdcardBean.setFri_0(record_schedule_fri_0);
		sdcardBean.setFri_1(record_schedule_fri_1);
		sdcardBean.setFri_2(record_schedule_fri_2);
		sdcardBean.setSat_0(record_schedule_sat_0);
		sdcardBean.setSat_1(record_schedule_sat_1);
		sdcardBean.setSat_2(record_schedule_sat_2);
		sdcardBean.setEnable_audio(enable_audio);
		handler.sendEmptyMessage(PARAMS);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
                                              int result) {
		Log.d("tag", "result:" + result + " paramType:" + paramType);
		if (strDID.equals(did)) {
			handler.sendEmptyMessage(result);
		}
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 2015) {
			int time = data.getIntExtra("jnitime", 1);
			Message msg = new Message();
			msg.what = 0;
			msg.arg1 = time;
			upHandler.sendMessage(msg);
		}
		if (resultCode == 2016) {
			int time = data.getIntExtra("jnitime", 1);
			int key = data.getIntExtra("key", -1);
			if (key == -1)
				return;
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = time;
			msg.arg2 = key;
			upHandler.sendMessage(msg);

		}
		if(resultCode==2017){
			int key = data.getIntExtra("key", -1);
			if(key==-1)
				return;
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = key;
			deleteHandler.sendMessage(msg);
		}
		if (resultCode == 2018) {
			int time = data.getIntExtra("jnitime", 1);
			Message msg = new Message();
			msg.what = 0;
			msg.arg1 = time;
			moveHandler.sendMessage(msg);
		}
		if (resultCode == 2019) {
			int time = data.getIntExtra("jnitime", 1);
			int key = data.getIntExtra("key", -1);
			if (key == -1)
				return;
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = time;
			msg.arg2 = key;
			moveHandler.sendMessage(msg);

		}
		if(resultCode==2020){
			int key = data.getIntExtra("key", -1);
			if(key==-1)
				return;
			Message msg = new Message();
			msg.what = 2;
			msg.arg1 = key;
			deleteHandler.sendMessage(msg);
		}
		}
	//计划录像
			private Handler upHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					int what = msg.what;
					int time = msg.arg1;
					switch (what) {
					case 0:
						for (int i = 1; i < 22; i++) {
							int value = planmap.get(i);

							if (value == -1 || value == 0) {
								planmap.put(i, time);
								adapter.addPlan(i, time);
								break;
							}
						}

						adapter.notifyDataSetChanged();
						setAlarmHandler.sendEmptyMessage(1);
						break;
					case 1:
						int key = msg.arg2;
						planmap.put(key, time);
						adapter.notify(key, time);
						adapter.notifyDataSetChanged();
						setAlarmHandler.sendEmptyMessage(1);
						break;

					default:
						break;
					}

				};
			};
			//移动侦测录像
			private Handler moveHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					int what = msg.what;
					int time = msg.arg1;
					switch (what) {
					case 0:
						for (int i = 1; i < 22; i++) {
							int value = moveplanmap.get(i);

							if (value == -1 || value == 0) {
								moveplanmap.put(i, time);
								moveAdapter.addPlan(i, time);
								break;
							}
						}

						moveAdapter.notifyDataSetChanged();

						setAlarmHandler.sendEmptyMessage(2);
						break;
					case 1:
						int key = msg.arg2;
						moveplanmap.put(key, time);
						moveAdapter.notify(key, time);
						moveAdapter.notifyDataSetChanged();
						setAlarmHandler.sendEmptyMessage(2);
						break;

					default:
						break;
					}

				};
			};
	/**
	 * 删除计划
	 */
	private Handler deleteHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
			int key = msg.arg1;
			adapter.removePlan(key);
			planmap.put(key, -1);
			adapter.notifyDataSetChanged();
			setAlarmHandler.sendEmptyMessage(1);
			break;
			case 2:
				int movekey = msg.arg1;
				moveAdapter.removePlan(movekey);
				moveplanmap.put(movekey, -1);
				moveAdapter.notifyDataSetChanged();
				setAlarmHandler.sendEmptyMessage(2);
				break;
			default:
				break;
			}
		};
	};
	private Handler setAlarmHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setTiming(strDID , strPWD);
				break;
			case 2:
				setMoveTiming(strDID , strPWD);
				break;
			default:
				break;
			}
		};
	};

	//设置计划录像
	private void setTiming(String udid, String pwd ) {
		NativeCaller
		.TransferMessage(udid,
				"trans_cmd_string.cgi?cmd=2017&command=3&mark="+cmark+"&record_plan1="
				+ planmap.get(1) + "&record_plan2="+ planmap.get(2) + "&record_plan3="
				+ planmap.get(3) + "&record_plan4="+ planmap.get(4) + "&record_plan5="
				+ planmap.get(5) + "&record_plan6="+ planmap.get(6) + "&record_plan7="
				+ planmap.get(7) + "&record_plan8="+ planmap.get(8) + "&record_plan9="
				+ planmap.get(9) + "&record_plan10="+ planmap.get(10) + "&record_plan11="
				+ planmap.get(11) + "&record_plan12="+ planmap.get(12) + "&record_plan13="
				+ planmap.get(13) + "&record_plan14="+ planmap.get(14) + "&record_plan15="
				+ planmap.get(15) + "&record_plan16="+ planmap.get(16) + "&record_plan17="
				+ planmap.get(17) + "&record_plan18="+ planmap.get(18) + "&record_plan19="
				+ planmap.get(19) + "&record_plan20="+ planmap.get(20) + "&record_plan21="
				+ planmap.get(21) + "&record_plan_enable="+ switchBean.getRecord_plan_enable()+"&loginuse=" + "admin" + "&loginpas="
				+ pwd, -1);
	}
	//设置移动侦测录像
	private void setMoveTiming(String udid, String pwd ) {
		NativeCaller
		.TransferMessage(udid,
				"trans_cmd_string.cgi?cmd=2017&command=1&mark="+cmark+"&motion_record_plan1="
				+ moveplanmap.get(1) + "&motion_record_plan2="+ moveplanmap.get(2) + "&motion_record_plan3="
				+ moveplanmap.get(3) + "&motion_record_plan4="+ moveplanmap.get(4) + "&motion_record_plan5="
				+ moveplanmap.get(5) + "&motion_record_plan6="+ moveplanmap.get(6) + "&motion_record_plan7="
				+ moveplanmap.get(7) + "&motion_record_plan8="+ moveplanmap.get(8) + "&motion_record_plan9="
				+ moveplanmap.get(9) + "&motion_record_plan10="+ moveplanmap.get(10) + "&motion_record_plan11="
				+ moveplanmap.get(11) + "&motion_record_plan12="+ moveplanmap.get(12) + "&motion_record_plan13="
				+ moveplanmap.get(13) + "&motion_record_plan14="+ moveplanmap.get(14) + "&motion_record_plan15="
				+ moveplanmap.get(15) + "&motion_record_plan16="+ moveplanmap.get(16) + "&motion_record_plan17="
				+ moveplanmap.get(17) + "&motion_record_plan18="+ moveplanmap.get(18) + "&motion_record_plan19="
				+ moveplanmap.get(19) + "&motion_record_plan20="+ moveplanmap.get(20) + "&motion_record_plan21="
				+ moveplanmap.get(21) + "&motion_record_plan_enable="+ switchBean.getMotion_record_plan_enable()+
				"&loginuse=" + "admin" + "&loginpas="
				+ pwd, -1);
	}
	@Override
	public void TimingCallback(String did, String command, String mask,
                               String record_plan1, String record_plan2, String record_plan3,
                               String record_plan4, String record_plan5, String record_plan6,
                               String record_plan7, String record_plan8, String record_plan9,
                               String record_plan10, String record_plan11, String record_plan12,
                               String record_plan13, String record_plan14, String record_plan15,
                               String record_plan16, String record_plan17, String record_plan18,
                               String record_plan19, String record_plan20, String record_plan21,
                               String record_plan_enable) {
		// TODO Auto-generated method stub
		if(did.contains(strDID) ){
			if(mask.contains(cmark)){

			planmap.put(1, Integer.valueOf(record_plan1));
			planmap.put(2, Integer.valueOf(record_plan2));
			planmap.put(3, Integer.valueOf(record_plan3));
			planmap.put(4, Integer.valueOf(record_plan4));
			planmap.put(5, Integer.valueOf(record_plan5));
			planmap.put(6, Integer.valueOf(record_plan6));
			planmap.put(7, Integer.valueOf(record_plan7));
			planmap.put(8, Integer.valueOf(record_plan8));
			planmap.put(9, Integer.valueOf(record_plan9));
			planmap.put(10, Integer.valueOf(record_plan10));
			planmap.put(11, Integer.valueOf(record_plan11));
			planmap.put(12, Integer.valueOf(record_plan12));
			planmap.put(13, Integer.valueOf(record_plan13));
			planmap.put(14, Integer.valueOf(record_plan14));
			planmap.put(15, Integer.valueOf(record_plan15));
			planmap.put(16, Integer.valueOf(record_plan16));
			planmap.put(17, Integer.valueOf(record_plan17));
			planmap.put(18, Integer.valueOf(record_plan18));
			planmap.put(19, Integer.valueOf(record_plan19));
			planmap.put(20, Integer.valueOf(record_plan20));
			planmap.put(21, Integer.valueOf(record_plan21));
			switchBean.setRecord_plan_enable(record_plan_enable);
			callbackHandler.sendEmptyMessage(1);
			}
		}
	}

	@Override
	public void VideoTimingCallback(String did, String command, String mask,
                                    String motion_record_plan1, String motion_record_plan2,
                                    String motion_record_plan3, String motion_record_plan4,
                                    String motion_record_plan5, String motion_record_plan6,
                                    String motion_record_plan7, String motion_record_plan8,
                                    String motion_record_plan9, String motion_record_plan10,
                                    String motion_record_plan11, String motion_record_plan12,
                                    String motion_record_plan13, String motion_record_plan14,
                                    String motion_record_plan15, String motion_record_plan16,
                                    String motion_record_plan17, String motion_record_plan18,
                                    String motion_record_plan19, String motion_record_plan20,
                                    String motion_record_plan21, String motion_record_enable) {
		// TODO Auto-generated method stub
		if(did.contains(strDID) ){
			if(mask.contains(cmark)){
				moveplanmap.put(1, Integer.valueOf(motion_record_plan1));
				moveplanmap.put(2, Integer.valueOf(motion_record_plan2));
				moveplanmap.put(3, Integer.valueOf(motion_record_plan3));
				moveplanmap.put(4, Integer.valueOf(motion_record_plan4));
				moveplanmap.put(5, Integer.valueOf(motion_record_plan5));
				moveplanmap.put(6, Integer.valueOf(motion_record_plan6));
				moveplanmap.put(7, Integer.valueOf(motion_record_plan7));
				moveplanmap.put(8, Integer.valueOf(motion_record_plan8));
				moveplanmap.put(9, Integer.valueOf(motion_record_plan9));
				moveplanmap.put(10, Integer.valueOf(motion_record_plan10));
				moveplanmap.put(11, Integer.valueOf(motion_record_plan11));
				moveplanmap.put(12, Integer.valueOf(motion_record_plan12));
				moveplanmap.put(13, Integer.valueOf(motion_record_plan13));
				moveplanmap.put(14, Integer.valueOf(motion_record_plan14));
				moveplanmap.put(15, Integer.valueOf(motion_record_plan15));
				moveplanmap.put(16, Integer.valueOf(motion_record_plan16));
				moveplanmap.put(17, Integer.valueOf(motion_record_plan17));
				moveplanmap.put(18, Integer.valueOf(motion_record_plan18));
				moveplanmap.put(19, Integer.valueOf(motion_record_plan19));
				moveplanmap.put(20, Integer.valueOf(motion_record_plan20));
				moveplanmap.put(21, Integer.valueOf(motion_record_plan21));
				switchBean.setMotion_record_plan_enable(motion_record_enable);
				callbackHandler.sendEmptyMessage(2);
			}
		}
	}

	private Handler callbackHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
			for (int i = 1; i < 22; i++) {
				int plan = planmap.get(i);
				if (plan != 0 && plan != -1) {
					adapter.addPlan(i, plan);
					adapter.notifyDataSetChanged();
				}
			}
			break;
			case 2:
				for (int i = 1; i < 22; i++) {
					int plan = moveplanmap.get(i);
					if (plan != 0 && plan != -1) {
						moveAdapter.addPlan(i, plan);
						moveAdapter.notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
			}
		};
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



	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case 0:// 设置失败
				Toast.makeText(SettingSDCardActivity.this, R.string.alerm_set_failed, Toast.LENGTH_LONG)
				.show();
				break;
			case 1:// 设置成功
					// showToast(R.string.alerm_set)
				//finish();
				break;
			case 2:// 回调成功
				Log.e("2222alermBean.getMotion_armed()", "************"+alermBean.getMotion_armed());
				if (alermBean.getMotion_armed() == 0) {// 移动侦测布防0-不布防，1-布防				
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
