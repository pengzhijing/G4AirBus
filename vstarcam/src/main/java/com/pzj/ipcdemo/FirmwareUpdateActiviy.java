package com.pzj.ipcdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import vstc2.nativecaller.NativeCaller;

public class FirmwareUpdateActiviy extends Activity implements OnClickListener,BridgeService.Firmware {

	private String did=null;
	private TextView tvsysver,tv_service_sysver;
	private Button button_back;
	private ProgressDialog progressDialog = null;
	private String LocalSysver = "noinfo";
	private String language;
	private boolean isGetSysData = false;
	private String download_server;
	private String filePath_sys;
    private String oemID;
    
    private boolean sys_isnew = false;
    
    private RelativeLayout service_sysver;
	
	//
	private Handler hander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				isGetSysData = true;
				tvsysver.setText(LocalSysver);
				getFirmware();
				break;
			}
		}
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!isGetSysData)
			{
				isGetSysData = false;
				if (progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
			}
		}
	};
	
	private Handler sysVerhander = new Handler() {
		public void handleMessage(Message msg) {
			String ver = (String) msg.obj;
			Log.e("info", "sys:" + ver);
			tv_service_sysver.setText(ver);
		};
	};
	
	private Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case 1:
				Log.i("info", "did:" + did + "download_server:"
						+ download_server + "filePath_sys:" + filePath_sys);
				new AlertDialog.Builder(FirmwareUpdateActiviy.this)
				.setTitle("New system firmware was detected. Is it updated?")
				.setCancelable(false)
				.setNegativeButton("Not updated",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

					}
				}).setPositiveButton("Update now",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
						sys_isnew = false;
						NativeCaller.UpgradeFirmware(did,download_server, filePath_sys,0);
						Toast.makeText(getApplicationContext(),"The system firmware is being updated and the camera will be restarted later...",Toast.LENGTH_LONG).show();
					}
				}).show();

				break;
			default:
				break;

			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_firmware_view);
		language = Locale.getDefault().getCountry();
		getDate();
		findView();
		showDiglog();
		hander.postDelayed(runnable,5000);
		
		NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_STATUS);
		BridgeService.setFirmware(this);
	}
	
	private void getDate()
	{
		Intent intent=getIntent();
		did = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
	}
	
	private void findView()
	{
		button_back=(Button) findViewById(R.id.back);
		button_back.setOnClickListener(this);
		tvsysver = (TextView) findViewById(R.id.sysver);
		
		tv_service_sysver = (TextView) findViewById(R.id.service_sysver_text);
		tv_service_sysver.setOnClickListener(this);
		
		service_sysver = (RelativeLayout) findViewById(R.id.service_sysver);
		service_sysver.setOnClickListener(this);
	}
	
	private void showDiglog()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在获取版本信息...");
		progressDialog.show();
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.back) {
			finish();

		} else if (i == R.id.service_sysver) {
			if (download_server != null && filePath_sys != null) {
				if (download_server.length() == 0 || filePath_sys.length() == 0) {
					Toast.makeText(this, "信息不完整，无法升级", Toast.LENGTH_LONG).show();
					return;
				}
				if (LocalSysver.equals(serverVer)) {
					Toast.makeText(this, "版本信息一样，无需升级", Toast.LENGTH_LONG).show();
					return;
				}
				updateHandler.sendEmptyMessage(1);
			} else {
				Toast.makeText(this, "", Toast.LENGTH_LONG).show();
			}

		} else {
		}
	}
	
	//获取版本
	private void getFirmware()
	{
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		getFirmwareData getdata = new getFirmwareData();
		new Thread(getdata).start();
	}

	private String serverVer = null;
/*
 * 获取固件版本线程
 */
	class getFirmwareData implements Runnable
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] params = { "firmware",LocalSysver,language};
			String result =sendHttpFirmwareMessge("firmware",params);
			
			if (result == null || result.equals("")) {
				return;
			}

			try {
				JSONObject obj = new JSONObject(result);
				String ssver=obj.optString("name");
				if (ssver == null ) {
					ssver = "";
				}
				String filepath=obj.optString("download_file");
				if (filepath == null ) {
					filepath = "";
				}
				String downloadServer=obj.optString("download_server");
				if (downloadServer == null ) {
					downloadServer = "";
				}
				if(ssver.trim().length()==0||filepath.trim().length()==0||downloadServer.trim().length()==0)
				{
					return;
				}else{
					Message msg = new Message();
					msg.obj = ssver;
					serverVer=ssver;
					sysVerhander.sendMessage(msg);
					download_server=downloadServer;
					filePath_sys=filepath;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	/*
	 * http获取版当前版本本方法
	 */
	public static String sendHttpFirmwareMessge(String MethodName, String... Parameters)
	{
		int len = Parameters.length;
		if (len == 0)
			return null;
		if (MethodName.length() == 0)
			return null;
		String uriString ="http://api4.eye4.cn:808";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++)
		{
			sb.append("/" + Parameters[i]);
		}
		uriString = uriString + sb.toString();
		Log.e("url", uriString );
		try {
			URL url = new URL(uriString);
			URI uri = new URI(url.getProtocol(), url.getHost() + ":808",
					url.getPath(), url.getQuery(), null);

			HttpGet httpRequest = new HttpGet(uri);
			// 取得HttpClient 对象
			HttpClient httpclient = new DefaultHttpClient();
			// 请求httpClient ，取得HttpRestponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				/* 取出响应字符串 */
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				if (strResult == null) {
					return null;
				}else{
					JSONObject obj = new JSONObject(strResult);
					int ret = obj.optInt("ret");
					int errcode = obj.optInt("errcode");
					if (errcode == 333) 
					{
						return null;
					}else{
						return strResult;
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 回调
	 */
	@Override
	public void CallBack_UpdateFirmware(String uid, String sysver, String appver, String oemid) {
		// TODO Auto-generated method stub
		Log.i("info", "callback" + sysver + "==appver" + appver + "oemid"+ oemid);
		LocalSysver = sysver;
		oemID = oemid;
		if (oemID == null || oemID.equals(""))
		{
			oemID = "OEM";
		}
		if(did.equalsIgnoreCase(uid))
		{		
			hander.sendEmptyMessage(1);
		}

	}
	
}
