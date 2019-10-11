package com.pzj.ipcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


;
import com.pzj.ipcdemo.adapter.BindSensorListAdapter;
import com.pzj.ipcdemo.adapter.SensorListAdapter;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import vstc2.nativecaller.NativeCaller;

public class SensorListActivty extends Activity implements OnClickListener,BridgeService.SensorSetCodeInterface,
		BridgeService.SensorListActivityAllDataInterface,OnItemClickListener {

	private String strDid,pwd;
	private TextView tv_back;
	public ListView sensorList = null;
	public ListView sensortypelist = null;
	private ArrayList<Map<String, String>> typelistItems;
	public ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	private SensorListAdapter typeadapter;
	public BindSensorListAdapter adapter;
	private LinearLayout existssensor_layout;
	private ImageView singleline_top;
	
	Handler Msghandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				existssensor_layout.setVisibility(View.VISIBLE);
				singleline_top.setVisibility(View.VISIBLE);
				Bundle it = msg.getData();
				int id1 = it.getInt("sensorid1", -1);
				int id2 = it.getInt("sensorid2", -1);
				int id3 = it.getInt("sensorid3", -1);
				int id = it.getInt("id", -1);
				int sensortype = it.getInt("sensortype", -1);
				int isnew = it.getInt("isnew", -1);
				String sensorname = it.getString("sensorname");
				adapter.addSensorToList(id1, id2, id3, sensortype, sensorname,isnew, id);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				Bundle b = msg.getData();
				int pos = b.getInt("pos");
				String name = b.getString("name");
				adapter.changeSensorName(name, pos);
				adapter.notifyDataSetChanged();
				break;
			case 3:
				Bundle b1 = msg.getData();
				int pos1 = b1.getInt("pos");
				adapter.removeMylist(pos1);
				Log.i("info", "remove:" + pos1);
				adapter.notifyDataSetChanged();
				break;
			case 4:
				existssensor_layout.setVisibility(View.GONE);
				singleline_top.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsensorlist);
		BridgeService.setSensorListInterface(this);
		getDate();
		findView();
		adapter = new BindSensorListAdapter(this);
		sensorList.setAdapter(adapter);
	}

	private void getDate()
	{
		Intent intent=getIntent();
		strDid=intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		pwd=intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
		//获取传感器列表
		String msg = "get_sensorlist.cgi?loginuse=admin&loginpas=" + pwd + "&user=admin&pwd=" + pwd;
		NativeCaller.TransferMessage(strDid, msg, 1);
	}


	
	private void findView()
	{
		existssensor_layout = (LinearLayout) findViewById(R.id.layout1);
		singleline_top = (ImageView) findViewById(R.id.singleline_top);
		
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);

		sensorList = (ListView) findViewById(R.id.camera_list);
		sensorList.setCacheColorHint(0);
		sensorList.setOnItemClickListener(this);
	    sensortypelist = (ListView) findViewById(R.id.sensortype_list);
		sensortypelist.setCacheColorHint(0);
		
		typelistItems = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("sensortype", "1");
		typelistItems.add(map);
		
		map = new HashMap<String, String>();
		map.put("sensortype", "7");
		typelistItems.add(map);
		
		map = new HashMap<String, String>();
		map.put("sensortype", "3");
		typelistItems.add(map);
		
		map = new HashMap<String, String>();
		map.put("sensortype", "4");
		typelistItems.add(map);
		
		map=new HashMap<String, String>();
		map.put("sensortype", "2");
		typelistItems.add(map);
		
		map=new HashMap<String, String>();
		map.put("sensortype", "8");
		typelistItems.add(map);
		
		typeadapter = new SensorListAdapter(getApplicationContext(),typelistItems);
		sensortypelist.setAdapter(typeadapter);
		sensortypelist.setOnItemClickListener(new sensortypeListAdapter());
	}
	
	class sensortypeListAdapter implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
			// TODO Auto-generated method stub
			Map<String, String> map = typelistItems.get(position);
			String type = map.get("sensortype");
			Intent it = new Intent(SensorListActivty.this,SensorStartCodeActivity.class);
			it.putExtra("type", type);
			it.putExtra("did", strDid);
			it.putExtra("pwd", pwd);
			startActivityForResult(it, 2014);

		}

	}
	
	/*
	 * 设置返回空名称的传感器名称
	 */
	private String getNullSensorName(int sensorid1, int sensorid2, int sensorid3, int snesortype)
	{
		String sensortype = null;
		if (snesortype == 1)
		{
			sensortype = getString(R.string.sensor_type_door);
		}
		else if (snesortype == 2)
		{
			sensortype = getString(R.string.sensor_type_infrared);
		}
		else if (snesortype == 3)
		{
			sensortype = getString(R.string.sensor_type_smoke);
		}
		else if (snesortype == 4)
		{
			sensortype = getString(R.string.sensor_type_gas);
		}
		else if (snesortype == 7)
		{
			sensortype = getString(R.string.sensor_type_remote);
		}
		else if (snesortype == 7)
		{
			sensortype = getString(R.string.sensor_type_remote);
		}
		else if (snesortype == 10)
		{
			sensortype = getString(R.string.sensor_type_camera);
		}
		else if (snesortype == 11)
		{
			sensortype = getString(R.string.sensor_type_curtain);
		}
		String list = adapter.changeTo16(sensorid1) + adapter.changeTo16(sensorid2) + adapter.changeTo16(sensorid3);
		return sensortype + "-" + list;
	}

	private void setSensorName(String name, int id)
	{
		NativeCaller.TransferMessage(strDid, "set_sensorname.cgi?" + "&sensorid="
				+ id + "&sensorname=" + name + "&loginuse=admin&loginpas="
				+ pwd, 1);
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.back) {
			finish();

		} else if (i == R.id.tv_back) {
			finish();

		} else {
		}
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Map<String, Object> map = listItems.get(position);
		String sensorid = BuildID(position);
		String sensortag=SensorTag(position);//id123合并
		int sensortype = (Integer) map.get("sensortype");
		String sensorname = (String) map.get("sensorname");
		int presetid = (Integer) map.get("presetid");
		int id = (Integer) map.get("id");
		Intent it = new Intent(SensorListActivty.this,EditSensorActivity.class);
		it.putExtra("sensorid1_list", sensorid);
		it.putExtra("sensortype", sensortype);
		it.putExtra("sensorname", sensorname);
		it.putExtra("presetid", presetid);
		it.putExtra("did", strDid);
		it.putExtra("pwd", pwd);
		it.putExtra("id", id);
		it.putExtra("position", position + "");
		it.putExtra("sensortag", sensortag);
		startActivityForResult(it, 500);
		Message msg = new Message();
		msg.arg1 = position;
		newHandler.sendMessage(msg);
	}
	
	public Handler newHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int position = msg.arg1;
			adapter.changeSensorNew(position);
			adapter.notifyDataSetChanged();
		}

	};
	
	private String SensorTag(int position)
	{
		Map<String, Object> map = listItems.get(position);
		int sensorid1 = (Integer) map.get("sensorid1");
		int sensorid2 = (Integer) map.get("sensorid2");
		int sensorid3 = (Integer) map.get("sensorid3");
		return sensorid1+""+sensorid2+sensorid3;
	}
	
	private String BuildID(int position)
	{
		Map<String, Object> map = listItems.get(position);
		int sensorid1 = (Integer) map.get("sensorid1");
		int sensorid2 = (Integer) map.get("sensorid2");
		int sensorid3 = (Integer) map.get("sensorid3");
		Log.i("info", "id1:" + sensorid1 + "--id2:" + sensorid2 + "--id3;"+ sensorid3);
		String list = adapter.changeTo16(sensorid1) + adapter.changeTo16(sensorid2)+ adapter.changeTo16(sensorid3);
		return list;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 501) {
			Log.i("info", "501");
			String name = data.getStringExtra("name");
			String position = data.getStringExtra("position");

			int pos = Integer.parseInt(position);
			Message msg = new Message();
			msg.what = 2;
			Bundle b = new Bundle();
			b.putString("name", name);
			b.putInt("pos", pos);
			msg.setData(b);
			Msghandler.sendMessage(msg);
			Map<String, Object> map = listItems.get(pos);
			map.put("sensorname", name);
		}
		if (resultCode == 502) {
			Log.i("info", "502");
			String position = data.getStringExtra("position");
			int pos = Integer.parseInt(position);
			Message msg = new Message();
			msg.what = 3;
			Bundle b = new Bundle();
			b.putInt("pos", pos);
			msg.setData(b);
			Msghandler.sendMessage(msg);
			listItems.remove(pos);
			if (listItems.size() == 0) {
				Msghandler.sendEmptyMessage(4);
			}
		}

		if (resultCode == 2013) {
			int sensorid1 = data.getIntExtra("sensorid1", -1);
			int sensorid2 = data.getIntExtra("sensorid2", -1);
			int sensorid3 = data.getIntExtra("sensorid3", -1);
			
			if(CheckHaveSame(sensorid1, sensorid2, sensorid3))
			{
				return;
			}
			
			int sensortype = data.getIntExtra("sensortype", -1);
			int linkpreset = data.getIntExtra("linkpreset", -1);
			String name = data.getStringExtra("sensorname");
			int channel = data.getIntExtra("id", -1);

			Log.i("tag", "对码界面返回" + sensorid1 + "," + sensorid2 + ","
					+ sensorid3 + ",sensortype" + sensortype + ",sensorname"
					+ name);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("presetid", linkpreset);
			map.put("sensortype", sensortype);
			map.put("sensorid3", sensorid3);
			map.put("sensorid2", sensorid2);
			map.put("sensorid1", sensorid1);
			map.put("sensorname", name);
			map.put("id", channel);//
			
			listItems.add(map);

			Message msg = new Message();
			Bundle datas = new Bundle();
			datas.putInt("sensorid1", sensorid1);
			datas.putInt("sensorid2", sensorid2);
			datas.putInt("sensorid3", sensorid3);
			datas.putInt("sensortype", sensortype);
			datas.putString("sensorname", name);
			Log.i("info", "3----new name:" + name);
			datas.putInt("id", channel);
			msg.what = 1;
			msg.setData(datas);
			Msghandler.sendMessage(msg);
			setSensorName(name, channel);
			
			if (sensortype != 1) {
				Log.i("info", "不是门磁 无需处理");
				return;
			}
//			
//			Log.i("info", " add new sensor door ");
//			String sensoridTag=sensorid1+""+sensorid2+sensorid3;
//			DoorBean bean=new DoorBean();
//			bean.setName(name);
//			bean.setSensoridTag(sensoridTag);
//			bean.setStatus(1);
//			SensorDoorData.AddSensor(did, bean);
			
		}
	}
	
	public boolean CheckHaveSame(int id1,int id2,int id3){
		int size=listItems.size();
		for(int i=0;i<size;i++){
			Map<String, Object> map =listItems.get(i);
			int mapid1=(Integer) map.get("sensorid1");
			if(mapid1==id1){
				int mapid2=(Integer) map.get("sensorid2");
				if(mapid2==id2){
					int mapid3=(Integer) map.get("sensorid3");
					if(mapid3==id3){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/** Timer对象 **/
	private Timer timer = null;
	/** TimerTask对象 **/
	private TimerTask timerTask = null;
	
	private void startTimer()
	{
		if (timerTask == null) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					System.out.println("=---------------");
					closeTimer();
				}
			};
			timer = new Timer();
			// schedule(TimerTask task, long delay, long period)
			// 安排指定的任务从指定的延迟后开始进行重复的固定延迟执行。
			// task - 所要安排的任务。
			// delay - 执行任务前的延迟时间，单位是毫秒。
			// period - 执行各后续任务之间的时间间隔，单位是毫秒。
			System.out.println("对码中、、、");
			timer.schedule(timerTask, 30000);
		}
	}
	private void reStart() 
	{
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask = null;
		}
	}
	private void closeTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask = null;
		}

		System.out.println("对码完成，退出对码、、、");
		NativeCaller.TransferMessage(strDid,
				"set_sensorstatus.cgi?cmd=3&loginuse=admin&loginpas=" + pwd
						+ "&user=admin&pwd=" + pwd, 1);

	}

	@Override
	public void CallBackReCodeMessage(String did, String name, int headcmd,
                                      int selfcmd, int linkpreset, int sensortype, int sensoraction,
                                      int channel, int sensorid1, int sensorid2, int sensorid3) {
		// TODO Auto-generated method stub
		Log.e("重新对码返回", name+"--"+headcmd+"--"+selfcmd+"--"+linkpreset+"--"+sensortype+"--"+sensoraction
				+"--"+channel+"--"+sensorid1+"--"+sensorid2+"--"+sensorid3);
		if (sensorid1 == 0 && sensorid2 == 0 && sensorid3 == 0 || sensorid1 == 255 && sensorid2 == 255 && sensorid3 == 255)
		{
			return;
		}
		 
		reStart();
		startTimer();

		if (name.trim().length() == 0 || name.trim().equals(""))
		{
			name = getNullSensorName(sensorid1, sensorid2, sensorid3,sensortype);
			setSensorName(name, channel);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("presetid", linkpreset);
		map.put("sensortype", sensortype);
		map.put("sensorid3", sensorid3);
		map.put("sensorid2", sensorid2);
		map.put("sensorid1", sensorid1);
		map.put("sensorname", name);
		map.put("id", channel);//
		listItems.add(map);

		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("sensorid1", sensorid1);
		data.putInt("sensorid2", sensorid2);
		data.putInt("sensorid3", sensorid3);
		data.putInt("sensortype", sensortype);
		data.putString("sensorname", name);
		Log.i("info", "3----new name:" + name);
		data.putInt("id", channel);
		data.putInt("isnew", 1);
		msg.what = 1;
		msg.setData(data);
		Msghandler.sendMessage(msg);
	}

	@Override
	public void CallBackMessage(String did, String resultPbuf, int cmd,
                                int sensorid1, int sensorid2, int sensorid3, int sensortype,
                                int sensorstatus, int presetid, int index) {
		// TODO Auto-generated method stub
		Log.e("添加的传感器", resultPbuf+"--"+cmd+"--"+sensorid1+"--"+sensorid2+"--"+sensorid3+"--"+
		sensorstatus+"--"+presetid+"--"+index);
		if (sensorid1 == 0 && sensorid2 == 0 && sensorid3 == 0 || sensorid1 == 255 && sensorid2 == 255 && sensorid3 == 255)
		{
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("presetid", presetid);
		map.put("sensorstatus", sensorstatus);
		map.put("sensortype", sensortype);
		map.put("sensorid3", sensorid3);
		map.put("sensorid2", sensorid2);
		map.put("sensorid1", sensorid1);
		map.put("sensorname", resultPbuf);
		map.put("id",index);
		listItems.add(map);

		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("sensorid1", sensorid1);
		data.putInt("sensorid2", sensorid2);
		data.putInt("sensorid3", sensorid3);
		data.putInt("sensortype", sensortype);
		data.putString("sensorname", resultPbuf);
		data.putInt("id", index);
		msg.what = 1;
		msg.setData(data);
		Msghandler.sendMessage(msg);
	}

}
