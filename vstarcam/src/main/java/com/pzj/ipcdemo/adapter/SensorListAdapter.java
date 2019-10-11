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
import java.util.Map;

public class SensorListAdapter extends BaseAdapter {
	private LayoutInflater inflater=null;
	private Context context;
	public ArrayList<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
	
	public SensorListAdapter(Context context, ArrayList<Map<String, String>> lists)
	{
		this.context=context;
		listItems=lists;
		inflater=LayoutInflater.from(context);
	
	}
	
	class sensorlist
	{
		public TextView sensorname;
		public TextView sensordis;
		public ImageView sensortype;
		public TextView sensorid;
		public ImageView getnext;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
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
	public View getView(int position, View convertView, ViewGroup v) {
		// TODO Auto-generated method stub
		sensorlist sensor;
		if (convertView == null) {
			sensor = new sensorlist();
			convertView = inflater.inflate(R.layout.sensorlist_item_sensortype_item, null);
			sensor.sensorname = (TextView) convertView
					.findViewById(R.id.sensor_name);
			sensor.sensortype = (ImageView) convertView
					.findViewById(R.id.sensor_type);
			sensor.sensordis = (TextView) convertView
					.findViewById(R.id.sensor_dic);
			sensor.getnext = (ImageView) convertView
					.findViewById(R.id.imageView1);
			convertView.setTag(sensor);
		} else {
			sensor = (sensorlist) convertView.getTag();
		}

		Map<String, String> map = listItems.get(position);
		String type = map.get("sensortype");
		int a = Integer.parseInt(type);
		Log.i("info", "aaa传感器类型aaaaaaaa" + a);
		if (a == 1) {
			sensor.sensorname.setText(context.getString(R.string.sensor_type_door));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_door_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensorlist_item_magnetic);
		}
		else if (a == 2) {
//			sensor.sensorid.setText("(" + list + "/" + "红外" + ")");
			sensor.sensorname.setText(context.getString(R.string.sensor_type_infrared));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_infrerad_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensorlist_item_infrared);
		} 
		else if (a == 3) {
//			sensor.sensorid.setText("(" + list + "/" + "烟感" + ")");
			sensor.sensorname.setText(context.getString(R.string.sensor_type_smoke));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_smoke_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensorlist_item_smoke);
		}
		else if (a == 4) {
//			sensor.sensorid.setText("(" + list + "/" + "煤气" + ")");
			sensor.sensorname.setText(context.getString(R.string.sensor_type_gas));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_gas_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensorlist_item_gas);
		} 
		else if (a == 7) {
			sensor.sensorname.setText(context.getString(R.string.sensor_type_remote));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_control_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensorlist_item_control);
		}
		else if (a == 8){
			sensor.sensorname.setText(context.getString(R.string.sensor_type_siren));
			sensor.sensordis.setText(context.getString(R.string.add_sensor_siren_desc));
			sensor.sensortype.setBackgroundResource(R.drawable.sensor_siren_icon);
		}

		return convertView;
	}
}
