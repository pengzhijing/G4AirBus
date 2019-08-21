package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shima.smartbushome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private List<HashMap<String,String>> mLeDevices = new ArrayList<HashMap<String, String>>();
    private LayoutInflater mInflator;

    public DeviceListAdapter(Context context,List<HashMap<String,String>> data) {
        super();
        mLeDevices=data;
        mInflator =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDevice(HashMap<String,String> device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public HashMap<String,String> getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.adapter_devicetype, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.devicetype_name);
            viewHolder.subnetid= (TextView) view.findViewById(R.id.sub);
            viewHolder.deviceid= (TextView) view.findViewById(R.id.dev);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        HashMap<String,String> device = mLeDevices.get(i);
        final String deviceName = device.get("devicename");
        // if (deviceName != null && deviceName.length() > 0){
        viewHolder.deviceName.setText(deviceName);
        viewHolder.subnetid.setText("subnetID:"+device.get("subnetID"));
        viewHolder.deviceid.setText("deviceID:" + device.get("deviceID"));
        // }


        return view;
    }
}
 class ViewHolder {
    TextView deviceName;
    TextView subnetid;
    TextView deviceid;
}
