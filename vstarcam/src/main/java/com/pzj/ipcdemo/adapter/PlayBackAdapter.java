package com.pzj.ipcdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.pzj.ipcdemo.PlayBackTFActivity;
import com.pzj.ipcdemo.R;
import com.pzj.ipcdemo.bean.PlayBackBean;

import java.util.ArrayList;

public class PlayBackAdapter extends BaseAdapter {
    public ArrayList<PlayBackBean> arrayList;
    private Context context;
	private LayoutInflater inflater;
	PlayBackTFActivity playback;
	
	
    public PlayBackAdapter(PlayBackTFActivity playback, Context context){
    	this.context=context;
    	this.playback = playback;
    	arrayList=new ArrayList<PlayBackBean>();
    	inflater = LayoutInflater.from(context);
    }
	@Override
	public int getCount() {
		return arrayList.size();
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
		 ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView=inflater.inflate(R.layout.playbacktf_listitem,null);
			holder.tvName=(TextView)convertView.findViewById(R.id.tv_name);
			holder.imgtip = (ImageView) convertView.findViewById(R.id.img_tip);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		PlayBackBean bean = arrayList.get(position);
		String path = bean.getPath();
		holder.tvName.setText(getTime(path));
		String tip = getTip(path);
		System.out.println(tip+"#########");
		if(getModel(tip).equals("a")){
			holder.imgtip.setBackgroundResource(R.drawable.icon_gpio);
		}else if (getModel(tip).equals("b")){
			holder.imgtip.setBackgroundResource(R.drawable.icon_motion);
		}else if(getModel(tip).equals("c")){
			holder.imgtip.setBackgroundResource(R.drawable.icon_rec);
		}
//		if(position == arrayList.size()){
//			playback.loadMoreView.setVisibility(View.VISIBLE);
//		}
		if(position>5000){
			playback.loadMoreView.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
    public String getTip(String tip){
    	String t = tip.substring(tip.length() - 8, tip.length() - 5);
    	return t;
    }
    public String getModel(String mess){
    	System.out.println(mess+">>>>>>>>>>>>>>>>>>>>>>>>>!!!!!!!!!!!!!!!!!!!");
    	String m = mess.substring(mess.length()-1, mess.length());
    	String n = mess.substring(1, 2);
    	if(m.equals("1")){
    		
    		return "a";
    	}else if(n.equals("1")){
    		return "b";
    	}else{
    		return "c";
    	}
    }
	public String getTime(String time) {
		String mess = time.substring(0, 14);
		String me = time.substring(15, time.length());
		String ddd = mess.substring(0, 4);
		String dd = mess.substring(4, 6);
		String d = mess.substring(6, 8);
		String hour = mess.substring(8, 10);
		String min = mess.substring(10, 12);
		String sec = mess.substring(12, 14);

		return ddd + "-" + dd + "-" + d + " " + hour + ":" + min + ":" + sec
				;
	}
    private class ViewHolder{
    	ImageView imgtip;
    	TextView tvName;
    }
    
    public void addPlayBean(PlayBackBean bean){
    	if(!arrayList.contains(bean.getPath())){
    		arrayList.add(bean);
    	}
    	
    }
    public PlayBackBean getPlayBean(int position){
    	return arrayList.get(position);
    }
}
