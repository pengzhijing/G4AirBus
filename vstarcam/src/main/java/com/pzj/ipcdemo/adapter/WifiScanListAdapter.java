package com.pzj.ipcdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.pzj.ipcdemo.R;
import com.pzj.ipcdemo.bean.WifiScanBean;

import java.util.ArrayList;

public class WifiScanListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<WifiScanBean> list;
	private ViewHolder holder;

	public WifiScanListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<WifiScanBean>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.settingwifi_list_item, null);
			holder = new ViewHolder();
			// holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.ssid = (TextView) convertView.findViewById(R.id.ssid);
			holder.safe = (TextView) convertView
					.findViewById(R.id.wifi_scan_listitem_tv_safe);
			holder.signal = (TextView) convertView
					.findViewById(R.id.wifi_scan_listitem_tv_signal_strong);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.size() == 0) {
			convertView
					.setBackgroundResource(R.drawable.listitem_one_pressed_selector);
		} else if (position == 0) {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_top_corner_selector);
		} else if (position == list.size() - 1) {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_bottom_corner_selector);
		} else {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_selector);
		}
		WifiScanBean wifiScanBean = list.get(position);
		holder.ssid.setText(wifiScanBean.getSsid());
		holder.signal.setText(wifiScanBean.getDbm0() + "%");
		String safeName = "";
		switch (wifiScanBean.getSecurity()) {
		case 0:
			safeName = context.getResources().getString(R.string.wifi_no_safe);
			break;
		case 1:
			safeName = "WEP";
			break;
		case 2:
			safeName = "WPA_PSK(AES)";
			break;
		case 3:
			safeName = "WPA_PSK(TKIP)";
			break;
		case 4:
			safeName = "WPA2_PSK(AES)";
			break;
		case 5:
			safeName = "WPA2_PSK(TKIP)";
			break;
		default:
			break;
		}
		holder.safe.setText(safeName);
		return convertView;
	}

	public void addWifiScan(WifiScanBean wifiScanBean) {
		list.add(wifiScanBean);
	}

	public void clearWifi() {
		list.clear();
	}

	public WifiScanBean getWifiScan(int position) {
		return list.get(position);
	}

	public ArrayList<WifiScanBean> getWifiScanBeans() {
		return list;
	}

	private class ViewHolder {
		TextView ssid;
		// ImageView img;
		TextView signal;
		TextView safe;
	}
}
