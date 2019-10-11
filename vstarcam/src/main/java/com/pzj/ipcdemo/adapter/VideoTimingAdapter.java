package com.pzj.ipcdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.pzj.ipcdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class VideoTimingAdapter extends BaseAdapter {
	private Context mContext;
	public ArrayList<Map<Integer, Integer>> sdtiming;
	private LayoutInflater inflater;
	private ViewHolder holder;
	
	public VideoTimingAdapter(Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		sdtiming = new ArrayList<Map<Integer, Integer>>();
		inflater = LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sdtiming.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.timing_video_item, null);
			holder = new ViewHolder();
			holder.tv_timing_time = (TextView) convertView.findViewById(R.id.tv_timing_time);
			holder.tv_timing_week = (TextView) convertView.findViewById(R.id.tv_timing_week);
			
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<Integer, Integer> item = sdtiming.get(position);
		int itemplan = item.entrySet().iterator().next().getValue();
		
		
		holder.tv_timing_week.setText(getWeekPlan(itemplan));

		int bStarttime = itemplan & 0x7ff;
		int bEndTime = (itemplan >> 12) & 0x7ff;
		holder.tv_timing_time.setText(getTime(bStarttime) + "-" + getTime(bEndTime));
		
		int plankey = item.entrySet().iterator().next().getKey();
		int plantime = item.entrySet().iterator().next().getValue();
		sdtiming.get(position).put(plankey, plantime);
		//cameraSetSDTiming.editPlanValid(plankey, plantime);
		
		return convertView;
	}
	private class ViewHolder {
		TextView tv_timing_time;
		TextView tv_timing_week;
		
		
	}
	private String getWeekPlan(int time) {
		String weekdays = "";
		for (int i = 24; i < 31; i++) {
			int weeks = (time >> i) & 1;
			if (weeks == 1) {
				switch (i) {
				case 24:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_seven)
							+ " ";
				
					break;
				case 25:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_one)
							+ " ";
					
					break;
				case 26:
				
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_two)
							+ " ";
					
					break;
				case 27:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_three)
							+ " ";
					
					break;
				case 28:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_four)
							+ " ";
					
					break;
				case 29:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_five)
							+ " ";
					
					break;
				case 30:
					
					weekdays = weekdays
							+ mContext.getResources().getString(R.string.plug_six)
							+ " ";
					
					break;
				default:
					break;
				}
			}
		}

		return weekdays;
	}
	
	private String getTime(int time) {
		if (time < 60) {
			if (time < 10)
				return "00:0" + time;
			return "00:" + time;
		}
		int h = time / 60;
		int m = time - (h * 60);
		if (h < 10 && m < 10) {
			return "0" + h + ":0" + m;
		} else if (h > 9 && m < 10) {
			return h + ":0" + m;
		} else if (h < 10 && m > 9) {
			return "0" + h + ":" + m;
		}

		return h + ":" + m;

	}
	
	public void addPlan(int key, int value) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(key, value);
		sdtiming.add(map);
		int size = sdtiming.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = 1; j < size - i; j++) {
				Map<Integer, Integer> maps;
				if (sdtiming
						.get(j - 1)
						.entrySet()
						.iterator()
						.next()
						.getKey()
						.compareTo(
								sdtiming.get(j).entrySet().iterator().next()
										.getKey()) > 0) {
					maps = sdtiming.get(j - 1);
					sdtiming.set(j - 1, sdtiming.get(j));
					sdtiming.set(j, maps);
				}
			}
		}

	}

	public void notify(int key, int value) {
		int size = sdtiming.size();
		for (int i = 0; i < size; i++) {
			Map<Integer, Integer> map = sdtiming.get(i);
			if (map.containsKey(key)) {
				map.put(key, value);
				break;
			}
		}
	}

	public void removePlan(int key) {
		int size = sdtiming.size();
		for (int i = 0; i < size; i++) {
			Map<Integer, Integer> map = sdtiming.get(i);
			if (map.containsKey(key)) {
				sdtiming.remove(i);
				break;
			}
		}
	}

}
