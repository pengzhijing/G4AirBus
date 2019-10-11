package com.pzj.ipcdemo;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pzj.ipcdemo.adapter.PlayBackAdapter;
import com.pzj.ipcdemo.bean.PlayBackBean;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import vstc2.nativecaller.NativeCaller;

/**
 * 
 * */
public class PlayBackTFActivity extends Activity implements
        OnItemClickListener, OnClickListener, BridgeService.PlayBackTFInterface {
	private Button btnBack;
	private ListView listView;
	private TextView tvNoVideo;
	private ProgressDialog progressDialog;
	private PlayBackAdapter mAdapter;
	private int TIMEOUT = 2000;
	private final int PARAMS = 1;
	private boolean successFlag = false;
	private String strName;
	private String strDID;
	private TextView tvTitle;
	private EditText editDateBegin;
	private EditText editDateEnd;
	public View loadMoreView;
	private Button loadMoreButton;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				if (mAdapter.getCount() > 0) {
					Log.i("info", "Video");
					listView.setVisibility(View.VISIBLE);
					//loadMoreView.setVisibility(View.VISIBLE);
					tvNoVideo.setVisibility(View.GONE);
				} else {
					Log.i("info", "noVideo");
					tvNoVideo.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);

				}
			}
		}
	};

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		setContentView(R.layout.playbacktf);
		findView();
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.remote_video_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		setListener();
		tvTitle.setText(strName);
		initDate();
		mAdapter = new PlayBackAdapter(PlayBackTFActivity.this,this);

		BridgeService.setPlayBackTFInterface(this);
		NativeCaller.PPPPGetSDCardRecordFileList(strDID, 0, 500);
		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		
	}

	private void initDate() {
		int byear = 0;
		int bmonth = 0;
		int bday = 0;

		Calendar calendar = Calendar.getInstance();
		int eyear = calendar.get(Calendar.YEAR);
		int emonth = calendar.get(Calendar.MONTH);
		int eday = calendar.get(Calendar.DAY_OF_MONTH);
		if (eday == 1) {
			Calendar ca2 = new GregorianCalendar(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) - 1, 1);
			byear = ca2.get(Calendar.YEAR);
			bmonth = ca2.get(Calendar.MONTH);
			bday = ca2.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else {
			byear = eyear;
			bmonth = emonth;
			bday = eday - 1;
		}
		Calendar bca = new GregorianCalendar(byear, bmonth, bday);
		Calendar eca = new GregorianCalendar(eyear, emonth, eday);
		Date bdate = bca.getTime();
		Date edate = eca.getTime();
		bdate.getTime();
		edate.getTime();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String strDateBegin = f.format(bdate);
		String strDateEnd = f.format(edate);

		editDateBegin.setText(strDateBegin);
		editDateEnd.setText(strDateEnd);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		String strPwd = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
		String strUser = intent.getStringExtra(ContentCommon.STR_CAMERA_USER);
		Log.d("info", "PlayBackTFActivity  strName:" + strName + " strDID:"
				+ strDID + " strPwd:" + strPwd + " strUser:" + strUser);
	}

	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		super.onPause();
	}

	private void setListener() {
		listView.setOnItemClickListener(this);
		btnBack.setOnClickListener(this);
		editDateBegin.setOnClickListener(this);
		editDateEnd.setOnClickListener(this);
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
		btnBack = (Button) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.listview);
		tvNoVideo = (TextView) findViewById(R.id.no_video);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		editDateBegin = (EditText) findViewById(R.id.edit_date_begin);
		editDateEnd = (EditText) findViewById(R.id.edit_date_end);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.top_bg);
	     BitmapDrawable drawable = new BitmapDrawable(bitmap);
	     drawable.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );
	     drawable.setDither(true);
	     layout.setBackgroundDrawable(drawable);
		loadMoreView = getLayoutInflater()
				.inflate(R.layout.loadmorecount, null);
		loadMoreButton = (Button) loadMoreView.findViewById(R.id.btn_load);
		loadMoreView.setVisibility(View.GONE);
		System.out.println("!1111111111111111111111111111111");
		listView.addFooterView(loadMoreView);
		loadMoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(fileTFCount - mAdapter.getCount()>0){
					LoadMoreData();
				}else {
					loadMoreView.setVisibility(View.GONE);
				}
				
			}
		});
		

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
         Log.d("playBackTFActivity...", "!!!!!!!!!!"+position);
		if (position < mAdapter.arrayList.size()) {
			PlayBackBean playBean = mAdapter.getPlayBean(position);
			String filepath = playBean.getPath();
			String mess = filepath.substring(0, 14);
			Intent intent = new Intent(this, PlayBackActivity.class);
			intent.putExtra("did", playBean.getDid());
			intent.putExtra("filepath", playBean.getPath());
			intent.putExtra("videotime", mess);
			intent.putExtra("filesize", playBean.getVideofilesize());
			Log.i("info", "filepath:"+filepath+"---mess:"+mess+"---");
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 
		} else {
			if (fileTFCount -mAdapter.getCount() > 0) {
				
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						LoadMoreData();
					}
				}, 2000);
			} else {
				loadMoreView.setVisibility(View.GONE);
			}

		}

	}

	@Override
	public void onClick(View v) {
		int i = v.getId();//
		if (i == R.id.back) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);//

		} else if (i == R.id.edit_date_begin) {
			String strBd = editDateBegin.getText().toString();
			date(strBd, true);//

		} else if (i == R.id.edit_date_end) {
			String strEd = editDateEnd.getText().toString();
			date(strEd, false);

		} else {
		}
	}

	private void date(String d, final boolean flag) {
		final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = f.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		DatePickerDialog dialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int month,
                                          int day) {
						Calendar ca = new GregorianCalendar(year, month, day);
						Date date2 = ca.getTime();

						String strDate = f.format(date2);
						if (flag) {// begin
							String strE = editDateEnd.getText().toString();
							int result = strDate.compareTo(strE);
							Log.d("tag", " result:" + result);
							if (result > 0) {
								showToast(R.string.remote_start_prompt);
							} else {
								date2.getTime();
								editDateBegin.setText(strDate);
							}
						} else {// end
							String strB = editDateBegin.getText().toString();
							int result = strDate.compareTo(strB);
							if (result < 0) {
								showToast(R.string.remote_end_prompt);
							} else {
								date2.getTime();
								editDateEnd.setText(strDate);
							}
						}
					}
				}, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH),
				ca.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int rid) {
		Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG)
				.show();
	}

	private int fileTFCount = 0;
	private int totalSize = 0;
	private int getCurrentPageIndex = 0;
	private int TotalPageSize = 0;

	public void LoadMoreData() {
		int count = mAdapter.getCount();
		if (count + 500 <= fileTFCount
				&& getCurrentPageIndex + 1 <= TotalPageSize) {
			getCurrentPageIndex+=1;
			NativeCaller.PPPPGetSDCardRecordFileList(strDID,
					getCurrentPageIndex, 500);
			mAdapter.notifyDataSetChanged();
			loadMoreButton.setText("获取更多视频文件...");
		} else {
			int filecount = fileTFCount - count;
			NativeCaller.PPPPGetSDCardRecordFileList(strDID,
					getCurrentPageIndex++, filecount);
			mAdapter.notifyDataSetChanged();
			loadMoreButton.setText("已经加载完毕");
			loadMoreButton.setVisibility(View.GONE);
		}
	}

	/**
	 * BridgeService callback
	 * 
	 * **/
   
	
	public void callBackRecordFileSearchResult(String did, String filename,
                                               int size, int recordcount, int pagecount, int pageindex,
                                               int pagesize, int bEnd) {
		Log.d("info", "CallBack_RecordFileSearchResult did: " + did
				+ " filename: " + filename + " size: " + size
				+ " recordcount :" + recordcount + "pagecount: " + pagecount
				+ "pageindex:" + pageindex + "pagesize: " + pagesize + "bEnd:"
				+ bEnd);
		if (strDID.equals(did)) {
			fileTFCount = recordcount;
			getCurrentPageIndex = pageindex;
			totalSize = size;
			TotalPageSize = pagesize;
			PlayBackBean bean = new PlayBackBean();
			bean.setDid(did);
			bean.setPath(filename);
			bean.setVideofilesize(size);
			mAdapter.addPlayBean(bean);
			if (TotalPageSize%500 == 0 ) {
				
			}
			if (bEnd == 1) {
				mHandler.sendEmptyMessage(PARAMS);
			}
		}
	}

	

}



