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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;


import com.pzj.ipcdemo.bean.DateBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import java.util.Calendar;
import java.util.TimeZone;

import vstc2.nativecaller.NativeCaller;

/**
 * 
 * */
public class SettingDateActivity extends BaseActivity implements
        OnClickListener, OnTouchListener, OnCheckedChangeListener,
		BridgeService.DateTimeInterface {
	private String strDID;
//	private String cameraName;
	private final int FAIL = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 3;
	private final int TIMEOUT = 3000;
	private boolean successFlag;
	private ProgressDialog progressDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAIL:
				showToast(R.string.date_setting_failed);
				break;
			case SUCCESS:
				showToast(R.string.date_setting_success);
				finish();
				break;
			case PARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
					if (dateBean.getNtp_enable() == 1) {
						cbxCheck.setChecked(true);
						ntpView.setVisibility(View.VISIBLE);
					} else {
						cbxCheck.setChecked(false);
						ntpView.setVisibility(View.GONE);
					}
					editNtpServer.setText(dateBean.getNtp_ser());
					tvDeviceTime.setText("longtime:" + dateBean.getNow());
					setTimeZone();
				}
				break;

			default:
				break;
			}
		}

		private void setTimeZone() {
			int utc = dateBean.getNow();
			Long lon = new Long(utc);
			switch (dateBean.getTz()) {
			case 39600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-11:00"));
				editTimeZone.setText(R.string.date_middle_island);
				break;
			case 36000:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-10:00"));
				editTimeZone.setText(R.string.date_hawaii);
				break;
			case 32400:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-09:00"));
				editTimeZone.setText(R.string.date_alaska);
				break;
			case 28800:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-08:00"));
				editTimeZone.setText(R.string.date_pacific_time);
				break;
			case 25200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-07:00"));
				editTimeZone.setText(R.string.date_mountain_time);
				break;
			case 21600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-06:00"));
				editTimeZone.setText(R.string.date_middle_part_time);
				break;
			case 18000:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-05:00"));
				editTimeZone.setText(R.string.date_eastern_time);
				break;
			case 14400:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-04:00"));
				editTimeZone.setText(R.string.date_ocean_time);
				break;
			case 12600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-03:30"));
				editTimeZone.setText(R.string.date_newfoundland);
				break;
			case 10800:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-03:00"));
				editTimeZone.setText(R.string.date_brasilia);
				break;
			case 7200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-02:00"));
				editTimeZone.setText(R.string.date_center_ocean);
				break;
			case 3600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT-01:00"));
				editTimeZone.setText(R.string.date_cape_verde_island);
				break;
			case 0:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT"));
				editTimeZone.setText(R.string.date_greenwich);
				break;
			case -3600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+01:00"));
				editTimeZone.setText(R.string.date_brussels);
				break;
			case -7200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+02:00"));
				editTimeZone.setText(R.string.date_athens);
				break;
			case -10800:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+03:00"));
				editTimeZone.setText(R.string.date_nairobi);
				break;
			case -12600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+03:30"));
				editTimeZone.setText(R.string.date_teheran);
				break;
			case -14400:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+04:00"));
				editTimeZone.setText(R.string.date_baku);
				break;
			case -16200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+04:30"));
				editTimeZone.setText(R.string.date_kebuer);
				break;
			case -18000:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+05:00"));
				editTimeZone.setText(R.string.date_islamabad);
				break;
			case -19800:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+05:30"));
				editTimeZone.setText(R.string.date_calcutta);
				break;

			case -21600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+06:00"));
				editTimeZone.setText(R.string.date_alamotu);
				break;
			case -25200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+07:00"));
				editTimeZone.setText(R.string.date_bangkok);
				break;
			case -28800:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+08:00"));
				editTimeZone.setText(R.string.date_beijing);
				break;
			case -32400:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+09:00"));
				editTimeZone.setText(R.string.date_seoul);
				break;
			case -34200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+09:30"));
				editTimeZone.setText(R.string.date_darwin);
				break;
			case -36000:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+10:00"));
				editTimeZone.setText(R.string.date_guam);
				break;
			case -39600:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+11:00"));
				editTimeZone.setText(R.string.date_suolumen);
				break;
			case -43200:
				tvDeviceTime.setText(setDeviceTime(lon * 1000, "GMT+12:00"));
				editTimeZone.setText(R.string.date_auckland);
				break;
			default:
				break;
			}
		}
	};

	private Button tvDeviceTime = null;
	private EditText editTimeZone = null;
	private EditText editNtpServer = null;
	private CheckBox cbxCheck = null;
	private ImageButton imgTimeZoneDown = null;
	private ImageButton imgNtpServerDown = null;
	private View ntpView = null;
	private PopupWindow timeZonePopWindow = null;
	private PopupWindow ntpServerPopWindow = null;
	private ScrollView scrollView = null;
	private DateBean dateBean = null;
	private Button btnOk = null;
	private Button btnCancel = null;
	private Button btnCheckOut = null;
//	private TextView tvCameraName = null;

	private String setDeviceTime(long millisutc, String tz) {

		TimeZone timeZone = TimeZone.getTimeZone(tz);
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(millisutc);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int weekNum = calendar.get(Calendar.DAY_OF_WEEK);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		String strWeek = "";
		
		switch (weekNum) {
		case 1:
			strWeek = "Sun";
			break;
		case 2:
			strWeek = "Mon";
			break;
		case 3:
			strWeek = "Tues";
			break;
		case 4:
			strWeek = "Wed";
			break;
		case 5:
			strWeek = "Thur";
			break;
		case 6:
			strWeek = "Fri";
			break;
		case 7:
			strWeek = "Sat";
			break;
		}
		String strMonth = "";
		switch (month) {
		case 1:
			strMonth = "Jan";
			break;
		case 2:
			strMonth = "Feb";
			break;
		case 3:
			strMonth = "Mar";
			break;
		case 4:
			strMonth = "Apr";
			break;
		case 5:
			strMonth = "May";
			break;
		case 6:
			strMonth = "Jun";
			break;
		case 7:
			strMonth = "Jul";
			break;
		case 8:
			strMonth = "Aug";
			break;
		case 9:
			strMonth = "Sept";
			break;
		case 10:
			strMonth = "Oct";
			break;
		case 11:
			strMonth = "Nov";
			break;
		case 12:
			strMonth = "Dec";
			break;
		}
		String strHour = "";
		if (hour < 10) {
			strHour = "0" + hour;
		} else {
			strHour = String.valueOf(hour);
		}
		String strMinute = "";
		if (minute < 10) {
			strMinute = "0" + minute;
		} else {
			strMinute = String.valueOf(minute);
		}
		String strSecond = "";
		if (second < 10) {
			strSecond = "0" + second;
		} else {
			strSecond = String.valueOf(second);
		}
		return strWeek + "," + day + " " + strMonth + year + " " + strHour
				+ ":" + strMinute + ":" + strSecond + "    UTC";
	}

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		setContentView(R.layout.settingdate);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.date_get_params));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		findView();
		setListener();
		dateBean = new DateBean();

		BridgeService.setDateTimeInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				// showToast(R.string.wifi_getparams_failed);
			}
		}
	};

	private void setListener() {
		imgTimeZoneDown.setOnClickListener(this);
		imgNtpServerDown.setOnClickListener(this);
		scrollView.setOnTouchListener(this);
		editTimeZone.setOnClickListener(this);
		editNtpServer.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		cbxCheck.setOnCheckedChangeListener(this);
		btnCheckOut.setOnClickListener(this);
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

	private void findView() {
		btnOk = (Button) findViewById(R.id.date_ok);
		btnCancel = (Button) findViewById(R.id.date_cancel);
		btnCheckOut = (Button) findViewById(R.id.date_btn_checkout);

		tvDeviceTime = (Button) findViewById(R.id.date_tv_device_time);
		editTimeZone = (EditText) findViewById(R.id.date_edit_timezone);
		editNtpServer = (EditText) findViewById(R.id.date_edit_ntp_server);
		cbxCheck = (CheckBox) findViewById(R.id.date_cbx_check);

		imgTimeZoneDown = (ImageButton) findViewById(R.id.date_img_timezone_down);
		imgNtpServerDown = (ImageButton) findViewById(R.id.date_img_ntp_server_down);

		ntpView = findViewById(R.id.date_ntp_view);

		scrollView = (ScrollView) findViewById(R.id.scrollView1);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.date_ok) {
			setDate();

		} else if (i == R.id.date_cancel) {
			finish();

		} else if (i == R.id.date_btn_checkout) {
			checkDeviceAsPhoneTime();

		} else if (i == R.id.date_edit_timezone || i == R.id.date_img_timezone_down) {
			if (ntpServerPopWindow != null && ntpServerPopWindow.isShowing()) {
				ntpServerPopWindow.dismiss();
				ntpServerPopWindow = null;
			}
			showTimeZonePopWindow();

		} else if (i == R.id.date_edit_ntp_server || i == R.id.date_img_ntp_server_down) {
			if (timeZonePopWindow != null && timeZonePopWindow.isShowing()) {
				timeZonePopWindow.dismiss();
				timeZonePopWindow = null;
			}
			showNtpServerPopWindow();

			// /ntpServer
		} else if (i == R.id.date_ntpserver_kriss) {
			ntpServerPopWindow.dismiss();
			dateBean.setNtp_ser(getResources().getString(
					R.string.date_ntp_server_time_kriss_re_kr));
			editNtpServer.setText(R.string.date_ntp_server_time_kriss_re_kr);

		} else if (i == R.id.date_ntpserver_nist) {
			ntpServerPopWindow.dismiss();
			dateBean.setNtp_ser(getResources().getString(
					R.string.date_ntp_server_time_nist_gov));
			editNtpServer.setText(R.string.date_ntp_server_time_nist_gov);

		} else if (i == R.id.date_ntpserver_nuri) {
			ntpServerPopWindow.dismiss();
			dateBean.setNtp_ser(getResources().getString(
					R.string.date_ntp_server_time_nuri_net));
			editNtpServer.setText(R.string.date_ntp_server_time_nuri_net);

		} else if (i == R.id.date_ntpserver_windows) {
			ntpServerPopWindow.dismiss();
			dateBean.setNtp_ser(getResources().getString(
					R.string.date_ntp_server_time_windows_com));
			editNtpServer.setText(R.string.date_ntp_server_time_windows_com);


			// timezone
		} else if (i == R.id.date_zone_middle_island) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(39600);
			editTimeZone.setText(R.string.date_middle_island);

		} else if (i == R.id.date_zone_hawaii) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(36000);
			editTimeZone.setText(R.string.date_hawaii);

		} else if (i == R.id.date_zone_alaska) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(32400);
			// Long nowAlaska=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowAlaska*1000, "GMT-09:00"));
			editTimeZone.setText(R.string.date_alaska);

		} else if (i == R.id.date_zone_pacific_time) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(28800);
			// Long nowPacific=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowPacific*1000,
			// "GMT-08:00"));
			editTimeZone.setText(R.string.date_pacific_time);

		} else if (i == R.id.date_zone_mountain_time) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(25200);
			// Long nowMountain=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowMountain*1000,
			// "GMT-07:00"));
			editTimeZone.setText(R.string.date_mountain_time);

		} else if (i == R.id.date_zone_middle_part_time) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(21600);
			// Long nowMiddlePart=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowMiddlePart*1000,
			// "GMT-06:00"));
			editTimeZone.setText(R.string.date_middle_part_time);

		} else if (i == R.id.date_zone_eastern_time) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(18000);
			// Long nowEastern=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowEastern*1000,
			// "GMT-05:00"));
			editTimeZone.setText(R.string.date_eastern_time);

		} else if (i == R.id.date_zone_ocean_time) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(14400);
			// Long nowOcean=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowOcean*1000, "GMT-04:00"));
			editTimeZone.setText(R.string.date_ocean_time);

		} else if (i == R.id.date_zone_newfoundland) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(12600);
			// Long nowNewfoundland=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowNewfoundland*1000,
			// "GMT-03:30"));
			editTimeZone.setText(R.string.date_newfoundland);

		} else if (i == R.id.date_zone_brasilia) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(10800);
			// Long nowBrasilia=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowBrasilia*1000,
			// "GMT-03:00"));
			editTimeZone.setText(R.string.date_brasilia);

		} else if (i == R.id.date_zone_center_ocean) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(7200);
			// Long nowCenterOcean=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowCenterOcean*1000,
			// "GMT-02:00"));
			editTimeZone.setText(R.string.date_center_ocean);

		} else if (i == R.id.date_zone_cap_verde_island) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(3600);
			// Long nowCapeVerde=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowCapeVerde*1000,
			// "GMT-01:00"));
			editTimeZone.setText(R.string.date_cape_verde_island);

		} else if (i == R.id.date_zone_greenwich) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(0);
			// Long nowGreenwich=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowGreenwich*1000, "GMT"));
			editTimeZone.setText(R.string.date_greenwich);

		} else if (i == R.id.date_zone_brussels) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-3600);
			// Long nowBrussels=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowBrussels*1000,
			// "GMT+01:00"));
			editTimeZone.setText(R.string.date_brussels);

		} else if (i == R.id.date_zone_athens) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-7200);
			// Long nowAthens=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowAthens*1000, "GMT+02:00"));
			editTimeZone.setText(R.string.date_athens);

		} else if (i == R.id.date_zone_nairobi) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-10800);
			// Long nowNairobi=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowNairobi*1000,
			// "GMT+03:00"));
			editTimeZone.setText(R.string.date_nairobi);

		} else if (i == R.id.date_zone_teheran) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-12600);
			// Long nowTeheran=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowTeheran*1000,
			// "GMT+03:30"));
			editTimeZone.setText(R.string.date_teheran);

		} else if (i == R.id.date_zone_baku) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-14400);
			// Long nowBaku=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowBaku*1000, "GMT+04:00"));
			editTimeZone.setText(R.string.date_baku);

		} else if (i == R.id.date_zone_kebuer) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-16200);
			// Long nowKebuer=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowKebuer*1000, "GMT+04:30"));
			editTimeZone.setText(R.string.date_kebuer);

		} else if (i == R.id.date_zone_islamabad) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-18000);
			// Long nowIslamabad=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowIslamabad*1000,
			// "GMT+05:00"));
			editTimeZone.setText(R.string.date_islamabad);

		} else if (i == R.id.date_zone_calcutta) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-19800);
			// Long nowCalcutta=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowCalcutta*1000,
			// "GMT+05:30"));
			editTimeZone.setText(R.string.date_calcutta);

		} else if (i == R.id.date_zone_alamotu) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-21600);
			// Long nowAlamotu=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowAlamotu*1000,
			// "GMT+06:00"));
			editTimeZone.setText(R.string.date_alamotu);

		} else if (i == R.id.date_zone_bangkok) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-25200);
			// Long nowBangkok=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowBangkok*1000,
			// "GMT+07:00"));
			editTimeZone.setText(R.string.date_bangkok);

		} else if (i == R.id.date_zone_beijing) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-28800);
			// Long nowBeijing=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowBeijing*1000,
			// "GMT+08:00"));
			editTimeZone.setText(R.string.date_beijing);

		} else if (i == R.id.date_zone_seoul) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-32400);
			// Long nowSeoul=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowSeoul*1000, "GMT+09:00"));
			editTimeZone.setText(R.string.date_seoul);

		} else if (i == R.id.date_zone_darwin) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-34200);
			// Long nowDarwin=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowDarwin*1000, "GMT+09:30"));
			editTimeZone.setText(R.string.date_darwin);

		} else if (i == R.id.date_zone_guam) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-36000);
			// Long nowGuam=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowGuam*1000, "GMT+10:00"));
			editTimeZone.setText(R.string.date_guam);

		} else if (i == R.id.date_zone_soulumen) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-39600);
			// Long nowSoulmen=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowSoulmen*1000,
			// "GMT+11:00"));
			editTimeZone.setText(R.string.date_suolumen);

		} else if (i == R.id.date_zone_auckland) {
			timeZonePopWindow.dismiss();
			dateBean.setTz(-43200);
			// Long nowAuckland=new Long(dateBean.getNow());
			// tvDeviceTime.setText(setDeviceTime(nowAuckland*1000,
			// "GMT+12:00"));
			editTimeZone.setText(R.string.date_auckland);

		} else {
		}
	}

	private void setDate() {
		NativeCaller.PPPPDatetimeSetting(strDID, 0, dateBean.getTz(),
				dateBean.getNtp_enable(), dateBean.getNtp_ser());
	}

	private void checkDeviceAsPhoneTime() {
		TimeZone timeZone = TimeZone.getDefault();
		int tz = -timeZone.getRawOffset() / 1000;
		Calendar calendar = Calendar.getInstance();
		int now = (int) (calendar.getTimeInMillis() / 1000);
		NativeCaller.PPPPDatetimeSetting(strDID, now, tz,
				dateBean.getNtp_enable(), dateBean.getNtp_ser());
	}

	private void showNtpServerPopWindow() {
		if (ntpServerPopWindow != null && ntpServerPopWindow.isShowing()) {
			return;
		}
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.settingdate_ntpserver_popwindow, null);
		Button ntpServerKriss = (Button) layout
				.findViewById(R.id.date_ntpserver_kriss);
		Button ntpServerNist = (Button) layout
				.findViewById(R.id.date_ntpserver_nist);
		Button ntpServerNuri = (Button) layout
				.findViewById(R.id.date_ntpserver_nuri);
		Button ntpServerWindows = (Button) layout
				.findViewById(R.id.date_ntpserver_windows);
		ntpServerKriss.setOnClickListener(this);
		ntpServerNist.setOnClickListener(this);
		ntpServerNuri.setOnClickListener(this);
		ntpServerWindows.setOnClickListener(this);
		ntpServerPopWindow = new PopupWindow(layout, 200,
				WindowManager.LayoutParams.WRAP_CONTENT);
		ntpServerPopWindow.showAsDropDown(imgNtpServerDown, -200, 0);
	}

	private void showTimeZonePopWindow() {
		if (timeZonePopWindow != null && timeZonePopWindow.isShowing()) {
			return;
		}
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.settingdate_timezone_popwindow, null);
		Button zoneMiddleIsland = (Button) layout
				.findViewById(R.id.date_zone_middle_island);
		Button zoneHawaii = (Button) layout.findViewById(R.id.date_zone_hawaii);
		Button zoneAlaska = (Button) layout.findViewById(R.id.date_zone_alaska);
		Button zonePacificTime = (Button) layout
				.findViewById(R.id.date_zone_pacific_time);
		Button zoneMountainTime = (Button) layout
				.findViewById(R.id.date_zone_mountain_time);
		Button zoneMiddlePartTime = (Button) layout
				.findViewById(R.id.date_zone_middle_part_time);
		Button zoneEasternTime = (Button) layout
				.findViewById(R.id.date_zone_eastern_time);
		Button zoneOceanTime = (Button) layout
				.findViewById(R.id.date_zone_ocean_time);
		Button zoneNewfoundland = (Button) layout
				.findViewById(R.id.date_zone_newfoundland);
		Button zoneBrasilia = (Button) layout
				.findViewById(R.id.date_zone_brasilia);
		Button zoneCenterOcean = (Button) layout
				.findViewById(R.id.date_zone_center_ocean);
		Button zoneCapeVerdeIsland = (Button) layout
				.findViewById(R.id.date_zone_cap_verde_island);
		Button zoneGreenWich = (Button) layout
				.findViewById(R.id.date_zone_greenwich);
		Button zoneBrussels = (Button) layout
				.findViewById(R.id.date_zone_brussels);
		Button zoneAthens = (Button) layout.findViewById(R.id.date_zone_athens);
		Button zoneNairobi = (Button) layout
				.findViewById(R.id.date_zone_nairobi);
		Button zoneTeheran = (Button) layout
				.findViewById(R.id.date_zone_teheran);
		Button zoneBaku = (Button) layout.findViewById(R.id.date_zone_baku);
		Button zoneKebuer = (Button) layout.findViewById(R.id.date_zone_kebuer);
		Button zoneIslamambad = (Button) layout
				.findViewById(R.id.date_zone_islamabad);
		Button zoneIslamabad = (Button) layout
				.findViewById(R.id.date_zone_calcutta);
		Button zoneAlamotu = (Button) layout
				.findViewById(R.id.date_zone_alamotu);
		Button zoneBangkok = (Button) layout
				.findViewById(R.id.date_zone_bangkok);
		Button zoneBeijing = (Button) layout
				.findViewById(R.id.date_zone_beijing);
		Button zoneSeoul = (Button) layout.findViewById(R.id.date_zone_seoul);
		Button zoneDarwin = (Button) layout.findViewById(R.id.date_zone_darwin);
		Button zoneGuam = (Button) layout.findViewById(R.id.date_zone_guam);
		Button zoneSoulumen = (Button) layout
				.findViewById(R.id.date_zone_soulumen);
		Button zoneAuckland = (Button) layout
				.findViewById(R.id.date_zone_auckland);

		zoneMiddleIsland.setOnClickListener(this);
		zoneHawaii.setOnClickListener(this);
		zoneAlaska.setOnClickListener(this);
		zonePacificTime.setOnClickListener(this);
		zoneMountainTime.setOnClickListener(this);
		zoneMiddlePartTime.setOnClickListener(this);
		zoneEasternTime.setOnClickListener(this);
		zoneOceanTime.setOnClickListener(this);
		zoneNewfoundland.setOnClickListener(this);
		zoneBrasilia.setOnClickListener(this);
		zoneCenterOcean.setOnClickListener(this);
		zoneCapeVerdeIsland.setOnClickListener(this);
		zoneGreenWich.setOnClickListener(this);
		zoneBrussels.setOnClickListener(this);
		zoneAthens.setOnClickListener(this);
		zoneNairobi.setOnClickListener(this);
		zoneTeheran.setOnClickListener(this);
		zoneBaku.setOnClickListener(this);
		zoneKebuer.setOnClickListener(this);
		zoneIslamambad.setOnClickListener(this);
		zoneIslamabad.setOnClickListener(this);
		zoneAlamotu.setOnClickListener(this);
		zoneBangkok.setOnClickListener(this);
		zoneBeijing.setOnClickListener(this);
		zoneSeoul.setOnClickListener(this);
		zoneDarwin.setOnClickListener(this);
		zoneGuam.setOnClickListener(this);
		zoneSoulumen.setOnClickListener(this);
		zoneAuckland.setOnClickListener(this);

		timeZonePopWindow = new PopupWindow(layout, 300, 500);
		timeZonePopWindow.showAsDropDown(imgTimeZoneDown, -310, 0);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
//		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timeZonePopWindow != null && timeZonePopWindow.isShowing()) {
			timeZonePopWindow.dismiss();
			timeZonePopWindow = null;
		}
		if (ntpServerPopWindow != null && ntpServerPopWindow.isShowing()) {
			ntpServerPopWindow.dismiss();
			ntpServerPopWindow = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (timeZonePopWindow != null && timeZonePopWindow.isShowing()) {
			timeZonePopWindow.dismiss();
			timeZonePopWindow = null;
		}
		if (ntpServerPopWindow != null && ntpServerPopWindow.isShowing()) {
			ntpServerPopWindow.dismiss();
			ntpServerPopWindow = null;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (timeZonePopWindow != null && timeZonePopWindow.isShowing()) {
			timeZonePopWindow.dismiss();
			timeZonePopWindow = null;
		}
		if (ntpServerPopWindow != null && ntpServerPopWindow.isShowing()) {
			ntpServerPopWindow.dismiss();
			ntpServerPopWindow = null;
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		if (isChecked) {
			dateBean.setNtp_enable(1);
			ntpView.setVisibility(View.VISIBLE);
		} else {
			dateBean.setNtp_enable(0);
			ntpView.setVisibility(View.GONE);
		}
	}

	/**
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackDatetimeParams(String did, int now, int tz,
                                       int ntp_enable, String ntp_svr) {
		dateBean.setNow(now);
		dateBean.setTz(tz);
		dateBean.setNtp_enable(ntp_enable);
		dateBean.setNtp_ser(ntp_svr);
		mHandler.sendEmptyMessage(PARAMS);
	}

	/**
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
                                              int result) {
		mHandler.sendEmptyMessage(result);
	}
}
