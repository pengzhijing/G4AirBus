package com.pzj.ipcdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.pzj.ipcdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BindSensorListAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private ArrayList<Map<String, Object>> mylist;
	private Context context;

	public BindSensorListAdapter(Context c)
	{
		context=c;
		this.mInflater = LayoutInflater.from(c);
		mylist =new ArrayList<Map<String,Object>>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mylist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		sensorlist sensor;
		if (convertView == null) {
			sensor = new sensorlist();
			convertView = mInflater.inflate(R.layout.sensoradapterlayout,null);
			sensor.sensorname = (TextView) convertView.findViewById(R.id.sensorname);
			sensor.sensortypeimageview = (ImageView) convertView.findViewById(R.id.sensor_type);

			sensor.getnext = (ImageView) convertView.findViewById(R.id.imageView1);
			sensor.sensornew = (ImageView) convertView.findViewById(R.id.sensor_new);
			convertView.setTag(sensor);
		} else {
			sensor = (sensorlist) convertView.getTag();
		}
		Map<String, Object> map = mylist.get(position);
		String name = (String) map.get("sensorname").toString().trim();
		if (name.length() != 0 && !name.equals("")) {
			sensor.sensorname.setText(name);
			Log.i("info", "传感器名称不为空。");
		}
		int id = (Integer) map.get("id");// 修改传感器名称的ID 为listview的position
		int list1 = (Integer) map.get("sensorid1");
		int list2 = (Integer) map.get("sensorid2");
		int list3 = (Integer) map.get("sensorid3");
		int type = (Integer) map.get("sensortype");
		int isNew = (Integer) map.get("sensornew");
		if (isNew == 1) {
			sensor.sensornew.setVisibility(View.VISIBLE);
		} else {
			sensor.sensornew.setVisibility(View.INVISIBLE);
		}
		String list = changeTo16(list1) + changeTo16(list2)
				+ changeTo16(list3);
		String sensortype = null;
		if (type == 1)
		{
			sensortype = context.getString(R.string.sensor_type_door);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensorlist_item_magnetic);
		}
		else if (type == 2) 
		{
			sensortype = context.getString(R.string.sensor_type_infrared);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensorlist_item_infrared);
		}
		else if (type == 3) 
		{
			sensortype = context.getString(R.string.sensor_type_smoke);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensorlist_item_smoke);
		}
		else if (type == 4)
		{
			sensortype = context.getString(R.string.sensor_type_gas);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensorlist_item_gas);
		}
		else if (type == 7)
		{
			sensortype = context.getString(R.string.sensor_type_remote);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensorlist_item_control);
		}
		else if (type == 8)
		{
			sensortype = context.getString(R.string.sensor_type_siren);
			sensor.sensortypeimageview.setBackgroundResource(R.drawable.sensor_siren_icon);
		}
		else if (type == 10)
		{
			sensortype = context.getString(R.string.sensor_type_camera);
		}
		else if (type == 11) 
		{
			sensortype = context.getString(R.string.sensor_type_curtain);
		}


		return convertView;
	}

	public void addSensorToList(int sensorid1, int sensorid2, int sensorid3, int sensortype, String resultPbuf, int newadd, int id)
	{
		Log.i("info", "add sensor");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sensorid1", sensorid1);
		map.put("sensorid2", sensorid2);
		map.put("sensorid3", sensorid3);
		map.put("sensortype", sensortype);
		map.put("sensorname", resultPbuf);
		map.put("sensornew", newadd);
		map.put("id", id);

		synchronized (this) {
			mylist.add(map);
		}
	}

	public void changeSensorName(String name, int pos) {
		Map<String, Object> map = mylist.get(pos);
		map.put("sensorname", name);
		Log.i("info", "changeSensorName:" + name);
	}

	public void changeSensorNew(int pos) {
		Map<String, Object> map = mylist.get(pos);
		map.put("sensornew", -1);
	}

	public void cleanItem() {
		mylist.clear();
	}

	public void removeMylist(int pos) {
		mylist.remove(pos);
	}
	
	public String changeTo16(int id)
	{
		String b = Integer.toHexString(id).toUpperCase();

		return id < 10 ? "0" + b : b;
	}

	class sensorlist 
	{
		public TextView sensorname;
		// public TextView sensorid;
		public ImageView getnext;
		public ImageView sensornew;
		public ImageView sensortypeimageview;
	}
}
