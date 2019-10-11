package com.pzj.ipcdemo;

import android.app.Activity;
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
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pzj.ipcdemo.adapter.WifiScanListAdapter;
import com.pzj.ipcdemo.bean.WifiBean;
import com.pzj.ipcdemo.bean.WifiScanBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import java.util.Timer;
import java.util.TimerTask;

import vstc2.nativecaller.NativeCaller;

/**
 * Wifi
 * */
public class SettingWifiActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener, OnItemClickListener, BridgeService.WifiInterface {
	private String LOG_TAG = "SettingWifiActivity";
	private String strDID;
	private String cameraName;
	private String cameraPwd;
	private boolean changeWifiFlag = false;//
	private boolean successFlag = false;// 
	private static final int INITTIMEOUT = 30000;
	private final int END = 1;// wifi scan end flag
	private int result;
	private final int WIFIPARAMS = 1;// getwifi params
	private final int SCANPARAMS = 2;// scan wifi params
	private final int OVER = 3;// set wifi over
	private final int TIMEOUT = 4;
	private int CAMERAPARAM = 0xffffffff;// ״̬
	// security
	private final int NO = 0;
	private final int WEP = 1;
	private final int WPA_PSK_AES = 2;
	private final int WPA_PSK_TKIP = 3;
	private final int WPA2_PSK_AES = 4;
	private final int WPA2_PSK_TKIP = 5;
	private Timer mTimerTimeOut;
	private boolean isTimerOver = false;
	private ImageView imgDropDown;
	private Button btnOk;
	private Button btnCancel;
	private CheckBox cbxShowPwd;
	private ListView mListView;
	private PopupWindow popupWindow;
	private TextView tvName;
	private TextView tvPrompt;
	private TextView tvSafe;
	private TextView tvSigal;
	private EditText editPwd;
	private Button btnManager;
	private WifiBean wifiBean;
	private WifiScanListAdapter mAdapter;
	private View pwdView;

	private ProgressDialog scanDialog;
	private View signalView;

	/**
	 * wifi getParams and Scaned
	 * 
	 * **/
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WIFIPARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
				if (wifiBean.getEnable() == 1) {
					if (!TextUtils.isEmpty(wifiBean.getSsid())) {
						tvName.setText(wifiBean.getSsid());
						tvPrompt.setText(getResources().getString(
								R.string.connected));
					} else {
						tvName.setText(getResources().getString(
								R.string.wifi_no_safe));
						tvPrompt.setText(getResources().getString(
								R.string.wifi_not_connected));
					}

					switch (wifiBean.getAuthtype()) {
					case NO:
						tvSafe.setText(getResources().getString(
								R.string.wifi_no_safe));
						break;
					case WEP:
						tvSafe.setText("WEP");
						break;
					case WPA_PSK_AES:
						tvSafe.setText("WPA_PSK(AES)");
						break;
					case WPA_PSK_TKIP:
						tvSafe.setText("WPA_PSK(TKIP)");
						break;
					case WPA2_PSK_AES:
						tvSafe.setText("WPA2_PSK(AES)");
						break;
					case WPA2_PSK_TKIP:
						tvSafe.setText("WPA2_PSK(TKIP)");
						break;
					default:
						break;
					}

				}
				break;
			case SCANPARAMS:// wifi scan
				Log.d(LOG_TAG, "handler  scan wifi");
				if (scanDialog.isShowing()) {
					scanDialog.cancel();
					if (!isTimerOver) {
						mTimerTimeOut.cancel();
					}
					mListView.setAdapter(mAdapter);
					setListViewHeight();
					mListView.setVisibility(View.VISIBLE);
					Log.d(LOG_TAG, "handler  scan wifi  2");
				}
				Log.d(LOG_TAG, "handler  scan wifi  3");
				break;
			case OVER:// set over
				successFlag = true;
				if (result == 1) {
					Log.d("tag", "over");
					NativeCaller.PPPPRebootDevice(strDID);
					Toast.makeText(
							SettingWifiActivity.this,
							getResources().getString(R.string.wifi_set_success),
							Toast.LENGTH_LONG).show();
					// Intent intent2 = new Intent(SettingWifiActivity.this,
					// IpcamClientActivity.class);
					// startActivity(intent2);
					Intent intent2 = new Intent("myback");
					sendBroadcast(intent2);
					finish();
				} else if (result == 0) {
					showToast(R.string.wifi_set_failed);
				}
				break;
			case TIMEOUT:
				if (scanDialog.isShowing()) {
					scanDialog.cancel();
				}
				// showToast(R.string.wifi_scan_failed);
				break;

			default:
				break;
			}
		}
	};

	private void showToast(int i) {
		Toast.makeText(SettingWifiActivity.this, getResources().getString(i), 0)
				.show();
	}

	/**
	 * Listitem click
	 * **/
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tvSigal.setText(wifiBean.getDbm0() + "%");

			tvName.setText(wifiBean.getSsid());
			signalView.setVisibility(View.VISIBLE);
			tvPrompt.setText(getResources().getString(
					R.string.wifi_not_connected));
			switch (wifiBean.getAuthtype()) {
			case NO:
				pwdView.setVisibility(View.GONE);
				tvSafe.setText(getResources().getString(R.string.wifi_no_safe));
				break;
			case WEP:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WEP");
				break;
			case WPA_PSK_AES:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA_PSK(AES)");
				break;
			case WPA_PSK_TKIP:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA_PSK(TKIP)");
				break;
			case WPA2_PSK_AES:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA2_PSK(AES)");
				break;
			case WPA2_PSK_TKIP:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA2_PSK(TKIP)");
				break;
			default:
				break;
			}

		}
	};
	private ProgressDialog progressDialog;

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				// showToast(R.string.wifi_getparams_failed);
			}
		}
	};

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
		setContentView(R.layout.settingwifi);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.wifi_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, INITTIMEOUT);
		wifiBean = new WifiBean();
		findView();
		setListener();
		mAdapter = new WifiScanListAdapter(this);
		mListView.setOnItemClickListener(this);

		BridgeService.setWifiInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_RECORD);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		cameraPwd=intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
	}

	private void setListener() {
		imgDropDown.setOnClickListener(this);
		btnManager.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		cbxShowPwd.setOnCheckedChangeListener(this);
		// progressDialog.setOnKeyListener(new OnKeyListener(){
		//
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		//
		// if(keyCode == KeyEvent.KEYCODE_BACK){
		// return true;
		// }
		// return false;
		// }
		//
		// });
	}

	private void findView() {
		imgDropDown = (ImageView) findViewById(R.id.wifi_img_drop);
		btnOk = (Button) findViewById(R.id.wifi_ok);
		btnCancel = (Button) findViewById(R.id.wifi_cancel);
		cbxShowPwd = (CheckBox) findViewById(R.id.wifi_cbox_show_pwd);
		mListView = (ListView) findViewById(R.id.wifi_listview);
		tvName = (TextView) findViewById(R.id.wifi_tv_name);
		tvPrompt = (TextView) findViewById(R.id.wifi_tv_prompt);
		tvSafe = (TextView) findViewById(R.id.wifi_tv_safe);
		tvSigal = (TextView) findViewById(R.id.wifi_tv_sigal);
		editPwd = (EditText) findViewById(R.id.wifi_edit_pwd);
		btnManager = (Button) findViewById(R.id.wifi_btn_manger);
		pwdView = findViewById(R.id.wifi_pwd_view);
		signalView = findViewById(R.id.wifi_sigalview);
		tvCameraName = (TextView) findViewById(R.id.tv_camera_setting);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.wifi_btn_manger) {
			mAdapter.clearWifi();
			mAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.GONE);
			NativeCaller.PPPPGetSystemParams(strDID,
					ContentCommon.MSG_TYPE_WIFI_SCAN);
			scanDialog = new ProgressDialog(this);
			scanDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			scanDialog.setMessage(getResources().getString(
					R.string.wifi_scanning));
			scanDialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
									 KeyEvent event) {

					if (keyCode == KeyEvent.KEYCODE_BACK) {
						return true;
					}
					return false;
				}

			});
			scanDialog.show();
			setTimeOut();

		} else if (i == R.id.wifi_ok) {
			setWifi();

		} else if (i == R.id.wifi_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);


		} else {
		}
	}

	private void setTimeOut() {

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Log.d(LOG_TAG, "isTimeOver");
				isTimerOver = true;
				mHandler.sendEmptyMessage(TIMEOUT);
			}
		};
		mTimerTimeOut = new Timer();
		mTimerTimeOut.schedule(task, INITTIMEOUT);
	}

	private void setWifi() {
		if (changeWifiFlag) {
			String pwd = editPwd.getText().toString();
			if (wifiBean.getAuthtype() == NO) {
				wifiBean.setWpa_psk("");
				wifiBean.setKey1("");
			} else {
				if (!TextUtils.isEmpty(pwd)) {
					if (wifiBean.getAuthtype() == WEP) {
						wifiBean.setKey1(pwd);
					} else {
						wifiBean.setWpa_psk(pwd);
					}
				} else {
					showToast(R.string.pwd_no_empty);
					return;
				}
			}
			try {
				NativeCaller.PPPPWifiSetting(wifiBean.getDid(),
						wifiBean.getEnable(), wifiBean.getSsid(),
						wifiBean.getChannel(), wifiBean.getMode(),
						wifiBean.getAuthtype(), wifiBean.getEncryp(),
						wifiBean.getKeyformat(), wifiBean.getDefkey(),
						wifiBean.getKey1(), wifiBean.getKey2(),
						wifiBean.getKey3(), wifiBean.getKey4(),
						wifiBean.getKey1_bits(), wifiBean.getKey2_bits(),
						wifiBean.getKey3_bits(), wifiBean.getKey4_bits(),
						wifiBean.getWpa_psk());
			} catch (Exception e) {
				showToast(R.string.wifi_scan_failed);
				e.printStackTrace();
			}

		} else {
			showToast(R.string.wifi_notchange);
		}
		// Intent intent3 = new Intent(SettingWifiActivity.this,
		// MainActivity.class);
		// intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent3);
	}

	private void setttingTimeOut() {
		successFlag = false;
		mHandler.postAtTime(settingRunnable, INITTIMEOUT);
	}

	private Runnable settingRunnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				showToast(R.string.wifi_set_failed);
			}
		}
	};
	private TextView tvCameraName;

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			editPwd.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			editPwd.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		changeWifiFlag = true;
		WifiScanBean wifiScan = mAdapter.getWifiScan(position);
		wifiBean.setSsid(wifiScan.getSsid());
		wifiBean.setAuthtype(wifiScan.getSecurity());
		wifiBean.setChannel(wifiScan.getChannel());
		wifiBean.setDbm0(wifiScan.getDbm0());
		handler.sendEmptyMessage(1);

	}

	public void setListViewHeight() {
		ListAdapter adapter = mListView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { 
			View listItem = adapter.getView(i, null, mListView);
			listItem.measure(0, 0); 
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = totalHeight
				+ (mListView.getDividerHeight() * (adapter.getCount() - 1));
		mListView.setLayoutParams(params);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackWifiParams(String did, int enable, String ssid,
                                   int channel, int mode, int authtype, int encryp, int keyformat,
                                   int defkey, String key1, String key2, String key3, String key4,
                                   int key1_bits, int key2_bits, int key3_bits, int key4_bits,
                                   String wpa_psk) {
		Log.d("tag", "did:" + did + " enable:" + enable + " ssid:" + ssid
				+ " channel:" + channel + " authtype:" + authtype + " encryp:"
				+ encryp + " wpa_psk:" + wpa_psk);
		wifiBean.setDid(did);
		wifiBean.setEnable(1);
		wifiBean.setSsid(ssid);
		wifiBean.setChannel(channel);
		wifiBean.setMode(0);// 0
		wifiBean.setAuthtype(authtype);
		wifiBean.setEncryp(0);// 0
		wifiBean.setKeyformat(0);// 0
		wifiBean.setDefkey(0);// 0
		wifiBean.setKey1(key1);// ""wep
		wifiBean.setKey2("");// ""
		wifiBean.setKey3("");// ""
		wifiBean.setKey4("");// ""
		wifiBean.setKey1_bits(0);// 0
		wifiBean.setKey2_bits(0);// 0
		wifiBean.setKey3_bits(0);// 0
		wifiBean.setKey4_bits(0);// 0
		wifiBean.setWpa_psk(wpa_psk);
		Log.d(LOG_TAG, wifiBean.toString());
		mHandler.sendEmptyMessage(WIFIPARAMS);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackWifiScanResult(String did, String ssid, String mac,
                                       int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
		Log.d(LOG_TAG, "ssid:" + ssid + " mac:" + mac + " security:" + security
				+ " dbm0" + dbm0 + " dbm1:" + dbm1 + " mode:" + mode
				+ " channel:" + channel + " bEnd:" + bEnd);
		Log.d(LOG_TAG, "bEnd=" + bEnd);
		if (bEnd != END) {
			Log.d(LOG_TAG, "");
			WifiScanBean bean = new WifiScanBean();
			bean.setDid(did);
			bean.setSsid(ssid);
			bean.setChannel(channel);
			bean.setSecurity(security);
			bean.setDbm0(dbm0);
			bean.setMac(mac);
			bean.setMode(mode);
			bean.setDbm1(dbm1);
			mAdapter.addWifiScan(bean);
		} else {
			Log.d(LOG_TAG, " bEnd=" + bEnd);
			mHandler.sendEmptyMessage(SCANPARAMS);
		}
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackSetSystemParamsResult(String did, int paramType, int result) {
		Log.d("tag", "result:" + result);
		this.result = result;
		mHandler.sendEmptyMessage(OVER);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackPPPPMsgNotifyData(String did, int type, int param) {
		if (strDID.equals(did)) {
			if (ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS == type) {
				CAMERAPARAM = param;
			}
		}
	}
}
