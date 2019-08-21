package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shima.smartbushome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/25.
 */
public class MusicRadioChannelAdapter extends BaseAdapter {
    private List<HashMap<String,String>> mLeDevices = new ArrayList<HashMap<String, String>>();
    private LayoutInflater mInflator;
    private int  selectItem=-1;
    public static final int select   = 0x7d02A3E9;
    private String[] colorarray={"5f000000", "5f000000"};
    public MusicRadioChannelAdapter(Context context,List<HashMap<String,String>> data) {
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
        radioViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.music_radio_items, null);
            viewHolder = new radioViewHolder();
            viewHolder.channelName = (TextView) view.findViewById(R.id.tvradioname);
            viewHolder.channelvalue= (TextView) view.findViewById(R.id.radiovalue);
            view.setTag(viewHolder);
        } else {
            viewHolder = (radioViewHolder) view.getTag();
        }
        if (i == selectItem) {
            view.setBackgroundColor(select);
        }
        else {
            //view.setBackgroundColor(Color.TRANSPARENT);
            view.setBackgroundColor(ToColor(colorarray[(i%2)]));
        }
        HashMap<String,String> device = mLeDevices.get(i);
        String deviceName = device.get("channelname");
        String devicevalue=device.get("channelvalue");
        devicevalue=devicevalue.substring(0,devicevalue.length()-1)+"."+devicevalue.substring(devicevalue.length()-1,devicevalue.length())+" MHZ";
        // if (deviceName != null && deviceName.length() > 0){
        viewHolder.channelName.setText(deviceName);
        viewHolder.channelvalue.setText(devicevalue);
        // }


        return view;
    }
    public int ToColor(String data){
        int color=0;
        int rin,gin,bin,ain;
        ain=Integer.parseInt(data.substring(0,2),16);
        rin=Integer.parseInt(data.substring(2,4),16);
        gin=Integer.parseInt(data.substring(4,6),16);
        bin=Integer.parseInt(data.substring(6,8),16);
        color= Color.argb(ain, rin, gin, bin);
        return color;
    }
    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int getSelectItem(){
        return selectItem;
    }

}
class radioViewHolder {
    TextView channelName;
    TextView channelvalue;
}