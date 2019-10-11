package com.pzj.ipcdemo;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;
import com.pzj.ipcdemo.utils.SensorDoorData;

import vstc2.nativecaller.NativeCaller;

public class EditSensorActivity extends Activity implements OnClickListener,
		BridgeService.EditSensorListActivityInterface, OnItemClickListener {
	private String sensorid, sensorname, did, pwd, position,sensortag;
	private int sensortype, presetid;
	private TextView tv_sensorid, tv_sensortype, tv_sensorname,btnOK;
	private EditText et_sensorname;
	private Button btnDelete;
	private ImageView btnBack;
	
	private ProgressDialog progressDialog;
	private int id;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String id = msg.getData().getString("id");
			int type = msg.getData().getInt("type");
			String name = msg.getData().getString("name");
			int setid = msg.getData().getInt("setid");
			int inttype = type;
			if (inttype == 1) {
				tv_sensortype.setText(getString(R.string.sensor_type_door));
			} else if (inttype == 2) {
				tv_sensortype.setText(getString(R.string.sensor_type_infrared));
			} else if (inttype == 3) {
				tv_sensortype.setText(getString(R.string.sensor_type_smoke));
			} else if (inttype == 4) {
				tv_sensortype.setText(getString(R.string.sensor_type_gas));
			} else if (inttype == 7) {
				tv_sensortype.setText(getString(R.string.sensor_type_remote));
			}  else if (inttype == 8) {
				tv_sensortype.setText(getString(R.string.sensor_type_siren));
			} else if (inttype == 10) {
				tv_sensortype.setText(getString(R.string.sensor_type_camera));
			} else if (inttype == 11) {
				tv_sensortype.setText(getString(R.string.sensor_type_curtain));
			}
			tv_sensorid.setText(id);
			et_sensorname.setText(name);
			et_sensorname.setSelectAllOnFocus(true);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_sensor);
		findview();
		getData();
		BridgeService.setSensornameInterface(this);

	}

	private TextView tv_back;

	private void findview() {
		// TODO Auto-generated method stub
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
		tv_sensorid = (TextView) findViewById(R.id.sensorid);
		tv_sensortype = (TextView) findViewById(R.id.sensortype);
		et_sensorname = (EditText) findViewById(R.id.sensorname);
		btnOK = (TextView) findViewById(R.id.edit_ok);
		btnOK.setOnClickListener(this);
		btnBack = (ImageView) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnDelete = (Button) findViewById(R.id.btn_delete);
		btnDelete.setOnClickListener(this);

//		RelativeLayout topRelativeLayout = (RelativeLayout) findViewById(R.id.top_relativeLayout);
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.top_bg);
//		BitmapDrawable drawable = new BitmapDrawable(bitmap);
//		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//		drawable.setDither(true);
//		topRelativeLayout.setBackgroundDrawable(drawable);
	}

	private void getData() {
		// TODO Auto-generated method stub
		Intent it = getIntent();
		id = it.getIntExtra("id", -1);
		sensorid = it.getStringExtra("sensorid1_list");
		sensortype = it.getIntExtra("sensortype", -200);
		sensorname = it.getStringExtra("sensorname");
		presetid = it.getIntExtra("presetid", -200);
		position = it.getStringExtra("position");
		did = it.getStringExtra("did");
		pwd = it.getStringExtra("pwd");
		sensortag=it.getStringExtra("sensortag");
		
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("id", sensorid);
		b.putInt("type", sensortype);
		b.putString("name", sensorname);
		b.putInt("setid", presetid);
		
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	private void setSensorName(String name) {
		NativeCaller.TransferMessage(did, "set_sensorname.cgi?" + "&sensorid="
				+ id + "&sensorname=" + name + "&loginuse=admin&loginpas="
				+ pwd, 1);
		
		showDia();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.edit_ok) {
			String name = et_sensorname.getText().toString().trim();
			if (!name.equals(sensorname)) {
				setSensorName(name);
			} else {
				finish();
			}


		} else if (i == R.id.back) {
			finish();

		} else if (i == R.id.tv_back) {
			finish();

		} else if (i == R.id.btn_delete) {
			Log.i("info", "delete sensor");
			NativeCaller.TransferMessage(did, "del_sensor.cgi?" + "&sensorid="
					+ id + "&loginuse=admin&loginpas=" + pwd
					+ "&user=admin&pwd=" + pwd, 1);
			showDia();

		} else {
		}
	}

//	private MainSettingDialog1 dialog3;
//
//	private void showSettingContextMenu() {
//		int width = getWindowManager().getDefaultDisplay().getWidth();
//		int height = getWindowManager().getDefaultDisplay().getHeight();
//
//		dialog3 = new MainSettingDialog1(EditSensorActivity.this, width, height);
//		dialog3.mRoot.setOnClickListener(this);
//		Message msg;
//		dialog3.setOnClickListener(new OnListener() {
//
//			@Override
//			public void onItemClick(int itemposition, int count) {
//				Log.i("info", "itemposition" + itemposition);
//				switch (itemposition) {
//				case 1:
//					setPreSetMessage(0);
//					Message msg = new Message();
//					msg.arg1 = 0;
//					preHandler.sendMessage(msg);
//					break;
//				case 2:
//					setPreSetMessage(1);
//					Message msg1 = new Message();
//					msg1.arg1 = 1;
//					preHandler.sendMessage(msg1);
//					break;
//				case 3:
//					setPreSetMessage(2);
//					Message msg2 = new Message();
//					msg2.arg1 = 2;
//					preHandler.sendMessage(msg2);
//					break;
//				case 4:
//					setPreSetMessage(3);
//					Message msg3 = new Message();
//					msg3.arg1 = 3;
//					preHandler.sendMessage(msg3);
//					break;
//				case 5:
//					setPreSetMessage(4);
//					Message msg4 = new Message();
//					msg4.arg1 = 4;
//					preHandler.sendMessage(msg4);
//					break;
//				case 6:
//					setPreSetMessage(5);
//					Message msg5 = new Message();
//					msg5.arg1 = 5;
//					preHandler.sendMessage(msg5);
//					break;
//				default:
//					break;
//				}
//
//			}
//		});
//		dialog3.show();
//	}


	private void setPreSetMessage(int pos) 
	{
		NativeCaller.TransferMessage(did, "set_sensor_preset.cgi?sensorid="
				+ id + "&presetid=" + pos + "&sensorid=" + sensorid
				+ "&loginuse=admin&loginpas=" + pwd, 1);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	public void showDia()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在操作，请稍后...");
		progressDialog.show();
	}

	@Override
	public void CallBackMessages(String did, String resultPbuf, int cmd) {
		// TODO Auto-generated method stub
		Log.e("info", "EditSensorActivity callback" + cmd);
		if (cmd == ContentCommon.CGI_SET_SENSOR_NAME) {
			Log.i("info", "CGI_SET_SENSOR_NAME" + resultPbuf);
			String isSucess = resultPbuf.substring(resultPbuf.indexOf("=") + 1,
					resultPbuf.indexOf(";"));
			Log.e("info", "EditSensorActivity isSucess:" + isSucess);
			if (isSucess.contains("0")) {
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				
				Intent data = new Intent();
				String sensorname=et_sensorname.getText().toString().trim();
				data.putExtra("name", sensorname);
				data.putExtra("position", position);
				SensorDoorData.ChangeDoorName(did, sensortag, sensorname);
				setResult(501, data);
				// Toast.makeText(getApplicationContext(), "修改成功!",
				// Toast.LENGTH_LONG).show();
				finish();
			} else {
				Toast.makeText(EditSensorActivity.this,"修改失败!",Toast.LENGTH_LONG).show();
			}

		}
		if (cmd == ContentCommon.CGI_DEL_SENSOR) {
			Log.i("info", "CGI_DEL_SENSOR" + resultPbuf);
			String isSucess = resultPbuf.substring(resultPbuf.indexOf("=") + 1,
					resultPbuf.indexOf(";"));
			Log.i("info", "EditSensorActivity isSucess:" + isSucess);
			if (isSucess.contains("0")) {
				Log.i("info", "delete----------0");
				// Toast.makeText(getApplicationContext(), "删除成功!",
				// Toast.LENGTH_LONG).show();
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				Intent data = new Intent();
				data.putExtra("position", position);
				setResult(502, data);
				SensorDoorData.removeSnesorDoor(did, sensortag);
				
				finish();
			} else {
				Toast.makeText(EditSensorActivity.this,"删除失败!",Toast.LENGTH_LONG).show();
			}
		}
		if (cmd == ContentCommon.CGI_SET_SENSOR_PRESET) {
			Log.i("info", "CGI_SET_SENSOR_PRESET" + resultPbuf);

		}

	}

//	class GroupAdapter extends BaseAdapter {
//
//		private Context mContext;
//		private ArrayList<String> groups;
//		private LayoutInflater mLayoutInflater;
//
//		public GroupAdapter(Context context, ArrayList<String> groups) {
//			this.mContext = context;
//			this.groups = groups;
//			mLayoutInflater = LayoutInflater.from(mContext);
//		}
//
//		@Override
//		public int getCount() {
//			return groups.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return groups.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder viewHolder;
//			if (convertView == null) {
//				viewHolder = new ViewHolder();
//				convertView = mLayoutInflater.inflate(
//						R.layout.sensor_edit_prelist, null);
//				convertView.setTag(viewHolder);
//				viewHolder.groupItemTextView = (TextView) convertView
//						.findViewById(R.id.listitem);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//
//			viewHolder.groupItemTextView.setText(groups.get(position));
//			return convertView;
//		}
//
//		class ViewHolder {
//			TextView groupItemTextView;
//		}
//
//	}

}
