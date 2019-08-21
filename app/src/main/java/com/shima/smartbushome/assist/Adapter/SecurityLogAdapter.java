package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.seclogdata;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16-6-15.
 */
public class SecurityLogAdapter extends BaseAdapter {
    private Context context;
    private List<seclogdata> logarray;
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private int  selectItem=-1;
    AlertView deletealter;
    int delete_item_position=0;
    public static final int select   = 0x62424242;
    public SecurityLogAdapter(Context context, List<seclogdata> logarray){
        this.context = context;
        this.logarray=logarray;
        size=logarray.size();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return logarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(Savemarco arg0) {//删除指定位置的item
        logarray.remove(arg0);
        this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.adapter_securitylogitem, null);
            TextView date = (TextView)view.findViewById(R.id.logitem_date);
            TextView time = (TextView)view.findViewById(R.id.logitem_time);
            TextView add = (TextView)view.findViewById(R.id.logitem_add);
            TextView chan=(TextView)view.findViewById(R.id.logitem_chan);
            TextView type=(TextView)view.findViewById(R.id.logitem_type);
            date.setText(logarray.get(position).Date);
            time.setText(logarray.get(position).Time);
            add.setText(logarray.get(position).Address);
            chan.setText(logarray.get(position).Channel);
            type.setText(logarray.get(position).Type);
            mView.put(position, view);
        }


        return view;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int getSelectItem(){
        return selectItem;
    }


}
