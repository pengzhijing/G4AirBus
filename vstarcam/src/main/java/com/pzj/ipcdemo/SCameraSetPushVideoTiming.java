package com.pzj.ipcdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;


import com.pzj.ipcdemo.bean.DefenseConstant;
import com.pzj.ipcdemo.utils.SensorTimeUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class SCameraSetPushVideoTiming extends BaseActivity implements
        OnClickListener {
	private LinearLayout timing_backlayout; //返回
	private TextView timing_textView1, timing_textView2; //开始时间结束时间
	private TimePicker timing_timePicker1 , timing_timePicker2; //时间选择器
	private CheckBox timing_id1,timing_id2,timing_id3,timing_id4,timing_id5,timing_id6,timing_id7; //选择星期
	private Button timing_start_delete,timing_start_save,timing_save;
	private String startTime = "00:00", endTime = "24:00";
	private int status = 1;
	private TreeSet<Integer> dateset;
	private int type = 0;
	private int value, key;
	private int absValue;
	private LinearLayout timing_edit_layout;  //编辑布局
	private TextView tv_camera_timingaddplan; //标题
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videotape_timing_setting);
		getDate();
		findview();
		
	}
	
	private void getDate() {
		// TODO Auto-generated method stub
		Intent it = getIntent();
		type = it.getIntExtra("type", 0);
		absValue = it.getIntExtra("value", 0);
		value = Math.abs(absValue);
		key = it.getIntExtra("key", 0);
		dateset = new TreeSet<Integer>(new Comparator<Integer>() {

			@Override
			public int compare(Integer c1, Integer c2) {
				// TODO Auto-generated method stub
				if (c1 > c2) {
					return -1;
				} else if (c1 == c2) {
					return 0;
				} else {
					return 1;
				}

			}

		});
	}
	
	//初始化数据
	public void findview(){
		timing_backlayout = (LinearLayout) findViewById(R.id.timing_backlayout);
		timing_textView1 = (TextView) findViewById(R.id.timing_textView1);
		timing_textView2 = (TextView) findViewById(R.id.timing_textView2);
		timing_timePicker1 = (TimePicker) findViewById(R.id.timing_timePicker1);
		timing_timePicker2 = (TimePicker) findViewById(R.id.timing_timePicker2);
		timing_id1 = (CheckBox) findViewById(R.id.timing_id1);
		timing_id2 = (CheckBox) findViewById(R.id.timing_id2);
		timing_id3 = (CheckBox) findViewById(R.id.timing_id3);
		timing_id4 = (CheckBox) findViewById(R.id.timing_id4);
		timing_id5 = (CheckBox) findViewById(R.id.timing_id5);
		timing_id6 = (CheckBox) findViewById(R.id.timing_id6);
		timing_id7 = (CheckBox) findViewById(R.id.timing_id7);
		timing_start_delete = (Button) findViewById(R.id.timing_start_delete);
		timing_start_save = (Button) findViewById(R.id.timing_start_save);
		timing_save = (Button) findViewById(R.id.timing_save);
		timing_edit_layout = (LinearLayout) findViewById(R.id.timing_edit_layout);
		tv_camera_timingaddplan = (TextView) findViewById(R.id.tv_camera_timingaddplan);
		timing_textView1.setText(getResources().getString(
				R.string.camera_defense_starttime)
				+ ":" + startTime);
		timing_textView2.setText(getResources().getString(
				R.string.camera_defense_endtime)
				+ ":" + endTime);
		//开始时间
		timing_timePicker1.setIs24HourView(true);
		timing_timePicker1.setCurrentHour(0);
		timing_timePicker1.setCurrentMinute(0);
		timing_timePicker1.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				String myMinute = "00";
				String myHour = "00";
				if (hourOfDay < 10) {
					myHour = "0" + hourOfDay;
				} else {
					myHour = hourOfDay + "";
				}
				if (minute < 10) {
					myMinute = "0" + minute;
				} else {
					myMinute = "" + minute;
				}

				startTime = myHour + ":" + myMinute;
				timing_textView1.setText(getResources().getString(
						R.string.camera_defense_starttime)
						+ ":" + startTime);
			}
		});
		//结束时间
		timing_timePicker2.setIs24HourView(true);
		timing_timePicker2.setCurrentHour(0);
		timing_timePicker2.setCurrentMinute(0);
		timing_timePicker2.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				String myMinute = "00";
				String myHour = "00";
				if (hourOfDay < 10) {
					myHour = "0" + hourOfDay;
				} else {
					myHour = hourOfDay + "";
				}
				if (minute < 10) {
					myMinute = "0" + minute;
				} else {
					myMinute = "" + minute;
				}

				endTime = myHour + ":" + myMinute;

				if (hourOfDay == 0 && minute == 0) {
					endTime = "24:00";
				}
				timing_textView2.setText(getResources().getString(
						R.string.camera_defense_endtime)
						+ ":" + endTime);
			}
		});
		
		if (type == 0) {
			tv_camera_timingaddplan.setText(getResources().getString(
					R.string.add_period));
		} else {
			timing_edit_layout.setVisibility(View.VISIBLE);
			timing_save.setVisibility(View.GONE);
			tv_camera_timingaddplan.setText(getResources().getString(
					R.string.edit_valid_time));
			if (value != 0) {
				int bStarttime = value & 0x7ff;
				int bEndTime = (value >> 12) & 0x7ff;
				int tp1H = getTimeHour(bStarttime);
				int tp1M = getTimeMinute(bStarttime);
				timing_timePicker1.setCurrentHour(tp1H);
				timing_timePicker1.setCurrentMinute(tp1M);
				int tp2H = getTimeHour(bEndTime);
				int tp2M = getTimeMinute(bEndTime);
				timing_timePicker2.setCurrentHour(tp2H);
				timing_timePicker2.setCurrentMinute(tp2M);
			}
		}
		timing_backlayout.setOnClickListener(this);
		timing_start_delete.setOnClickListener(this);
		timing_start_save.setOnClickListener(this);
		timing_save.setOnClickListener(this);
		CheckBoxListener listener = new CheckBoxListener();
		timing_id1.setOnCheckedChangeListener(listener);
		timing_id2.setOnCheckedChangeListener(listener);
		timing_id3.setOnCheckedChangeListener(listener);
		timing_id4.setOnCheckedChangeListener(listener);
		timing_id5.setOnCheckedChangeListener(listener);
		timing_id6.setOnCheckedChangeListener(listener);
		timing_id7.setOnCheckedChangeListener(listener);
		if (type == 1) {
			getWeekPlan(value);
		}
	}
	
	private void getWeekPlan(int time) {
		for (int i = 24; i < 31; i++) {
			int weeks = (time >> i) & 1;
			switch (i) {
			case 24:
				if (weeks == 1) {
					timing_id7.setChecked(true);
				} else {
					timing_id7.setChecked(false);
				}
				break;
			case 25:
				if (weeks == 1) {
					timing_id1.setChecked(true);
				} else {
					timing_id1.setChecked(false);
				}
				break;
			case 26:
				if (weeks == 1) {
					timing_id2.setChecked(true);
				} else {
					timing_id2.setChecked(false);
				}
				break;
			case 27:
				if (weeks == 1) {
					timing_id3.setChecked(true);
				} else {
					timing_id3.setChecked(false);
				}
				break;
			case 28:
				if (weeks == 1) {
					timing_id4.setChecked(true);
				} else {
					timing_id4.setChecked(false);
				}
				break;
			case 29:
				if (weeks == 1) {
					timing_id5.setChecked(true);
				} else {
					timing_id5.setChecked(false);
				}
				break;
			case 30:
				if (weeks == 1) {
					timing_id6.setChecked(true);
				} else {
					timing_id6.setChecked(false);
				}
				break;
			default:
				break;
			}
		}

	}
	
	private int getTimeMinute(int time) {
		if (time < 60) {
			return time;
		}
		int h = time / 60;
		int m = time - (h * 60);
		return m;

	}

	private int getTimeHour(int time) {
		if (time < 60) {
			return 0;
		}
		int h = time / 60;
		return h;

	}

	/**
	 * 得到timePicker里面的android.widget.NumberPicker组件
	 * （有两个android.widget.NumberPicker组件--hour，minute）
	 * 
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;

		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}

		return npList;
	}
	
	class CheckBoxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			CompoundButton box = (CompoundButton) arg0;
			int i = box.getId();
			if (i == R.id.timing_id1) {
				if (box.isChecked()) {
					timing_id1.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
					dateset.add(1);
				} else {
					timing_id1.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(1)) {
						dateset.remove(1);
					}
				}

			} else if (i == R.id.timing_id2) {
				if (box.isChecked()) {
					timing_id2.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
					dateset.add(2);
				} else {
					timing_id2.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(2)) {
						dateset.remove(2);
					}
				}

			} else if (i == R.id.timing_id3) {
				if (box.isChecked()) {
					dateset.add(3);
					timing_id3.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
				} else {
					timing_id3.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(3)) {
						dateset.remove(3);
					}
				}

			} else if (i == R.id.timing_id4) {
				if (box.isChecked()) {
					dateset.add(4);
					timing_id4.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
				} else {
					timing_id4.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(4)) {
						dateset.remove(4);
					}
				}

			} else if (i == R.id.timing_id5) {
				if (box.isChecked()) {
					dateset.add(5);
					timing_id5.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
				} else {
					timing_id5.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(5)) {
						dateset.remove(5);
					}
				}

			} else if (i == R.id.timing_id6) {
				if (box.isChecked()) {
					dateset.add(6);
					timing_id6.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
				} else {
					timing_id6.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(6)) {
						dateset.remove(6);
					}
				}

			} else if (i == R.id.timing_id7) {
				if (box.isChecked()) {
					dateset.add(0);
					timing_id7.setTextColor(getResources().getColor(
							R.color.color_startcode_bg));
				} else {
					timing_id7.setTextColor(getResources().getColor(
							R.color.color_alarm_textcolor));
					if (dateset.contains(0)) {
						dateset.remove(0);
					}
				}

			} else {
			}
		}

	}
	
	private int passStartTime, passEndTime;
	private void checkTime() {
		// TODO Auto-generated method stub

		Log.e("vst","startTime:" + startTime + ",endTime:" + endTime
				+ "date:" + dateset.toString());
		// if (startTime.equals(endTime) || startTime == endTime) {
		// showToast(getResources().getString(R.string.defense_sametime));
		// return;
		// }
		passStartTime = SensorTimeUtil.getPassMinutes(timing_timePicker1.getCurrentHour(),
				timing_timePicker1.getCurrentMinute());
		passEndTime = 0;
		int endtimeH = timing_timePicker2.getCurrentHour();
		int endtimeM = timing_timePicker2.getCurrentMinute();
		if (endtimeH == 0 && endtimeM == 0) {
			passEndTime = SensorTimeUtil.getPassMinutes(24, 0);
		} else {
			passEndTime = SensorTimeUtil.getPassMinutes(timing_timePicker2.getCurrentHour(),
					timing_timePicker2.getCurrentMinute());
		}
		Log.e("vst","passStartTime"+passStartTime + "*passEndTime*"+passEndTime);
		if (passEndTime <= passStartTime) {
			//判断结束时间大于开始时间
			//showToast(getResources().getString(R.string.defense_timeoutride));
			//return;
		}

		if (dateset.size() == 0) {
			showToast(getResources().getString(R.string.defense_nodate));
			return;
		}

		// if(SensorTimeUtil.checkTime(startTime, endTime)){
		// showToast("该时间内已经有其他计划了");
		// return;
		// }

		// getPlanTime();
		getPlanTimerInt();

	}
	
	private void getPlanTimerInt() {

		String weeks = "0000000";
		if (dateset.size() != 0) {
			Iterator it = dateset.iterator();
			while (it.hasNext()) {
				int weekday = (Integer) it.next();
				weeks = SensorTimeUtil.replaceIndex(6 - weekday, weeks);
			}
		}
		
		Log.e("vst","weeks"+weeks);
		String string23 = SensorTimeUtil.getMinutesString(passEndTime,
				passStartTime);
		String string32 = SensorTimeUtil.get2Strings(0 + "", weeks, string23);
		int int32 = SensorTimeUtil.string32toInt(string32);

		Intent its = new Intent();
		if (type == 1) {
			if (absValue < 0)
				int32 = 0 - int32;
			its.putExtra("jnitime", int32);
			its.putExtra("key", key);
//			setResult(2016, its);
			setResult(2012, its);
			finish();
		} else {
			its.putExtra("jnitime", int32);
//			setResult(2015, its);
			setResult(2011, its);
			finish();
		}

	}
//	private void getPlanTime() {
//
//		ArrayList<Map<String, String>> planlist = new ArrayList<Map<String, String>>();
//		for (int i = 1; i < 8; i++) {
//			if (!dateset.contains(i)) {
//				String key = getMapKey(i);
//				if (key.trim().length() != 0) {
//					Map<String, String> map = new HashMap<String, String>();
//					map.put(key, DefenseConstant.key_allDisAlarm);
//					planlist.add(map);
//				}
//			}
//		}
//
//		if (dateset.size() != 0) {
//			Iterator it = dateset.iterator();
//			while (it.hasNext()) {
//				int weekday = (Integer) it.next();
//				String key = getMapKey(weekday);
//				Map<String, String> map = new HashMap<String, String>();
//				if (startTime.equals("00:00") && endTime.equals("24:00")) {
//					map.put(key, DefenseConstant.key_allAlarm);
//					planlist.add(map);
//				} else {
//					if (!SensorTimeUtil.checkTime(startTime, endTime, key)) {
//						String op2 = SensorTimeUtil.getOneDay96Strings(
//								startTime, endTime);
//						LogTools.LogWe("op2:" + op2);
//						map.put(key, op2);
//						planlist.add(map);
//					} else {
//						showToast(getResources().getString(
//								R.string.camera_defense_plan_clash));
//						return;
//					}
//				}
//			}
//		}
//
//		int plansize = planlist.size();
//		if (plansize != 0) {
//			for (int i = 0; i < plansize; i++) {
//				Map<String, String> map = planlist.get(i);
//				Set entries = map.entrySet();
//				if (entries != null) {
//					Iterator iterator = entries.iterator();
//					while (iterator.hasNext()) {
//						Map.Entry entry = (Entry) iterator.next();
//						String key = (String) entry.getKey();
//						String value = (String) entry.getValue();
//						SensorTimeUtil.addNewDayToMap(key, value);
//					}
//				}
//			}
//		}
//
//		LogTools.LogWe("添加完成:" + SensorTimeUtil.planMap.toString());
//
//		dateset.comparator();
//
//		Intent its = new Intent();
//		its.putExtra("time", startTime + "-" + endTime);
//		its.putExtra("alarm", status);
//		its.putExtra("set", dateset);
//		setResult(2015, its);
//		finish();
//
//	}
	
	private String getMapKey(int day) {
		String key = "";
		switch (day) {
		case 1:
			key = DefenseConstant.key_Monday;
			break;
		case 2:
			key = DefenseConstant.key_Tuesday;
			break;
		case 3:
			key = DefenseConstant.key_Wednesday;
			break;
		case 4:
			key = DefenseConstant.key_Thursday;
			break;
		case 5:
			key = DefenseConstant.key_Friday;
			break;
		case 6:
			key = DefenseConstant.key_Saturday;
			break;
		case 7:
			key = DefenseConstant.key_Sunday;
			break;

		default:
			break;
		}
		return key;
	}
	
	public void showToast(String t) {
		Toast.makeText(SCameraSetPushVideoTiming.this, t, 1000).show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.timing_backlayout) {
			finish();

		} else if (i == R.id.timing_start_delete) {
			showDefaultSetDialog();

		} else if (i == R.id.timing_start_save) {
			checkTime();

		} else if (i == R.id.timing_save) {
			checkTime();
//			Toast.makeText(
//					CameraSetSDTiming.this,
//					CameraSetSDTiming.this.getResources().getString(
//							R.string.camera_function_notsupport), Toast.LENGTH_SHORT)
//					.show();
//			finish();

		} else {
		}
	}

	

	

	

	

	
	
	

	private AlertDialog dialog = null;

	private void showDefaultSetDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(SCameraSetPushVideoTiming.this);
		builder.setMessage(R.string.del_ok);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent its = new Intent();
						its.putExtra("key", key);
//						setResult(2017, its);
						setResult(2013, its);
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						
					}
				});
		dialog = builder.create();
		dialog.show();

	}

}
