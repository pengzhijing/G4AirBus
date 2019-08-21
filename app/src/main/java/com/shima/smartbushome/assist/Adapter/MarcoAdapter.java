package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savemarcobutton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8.
 */
public class MarcoAdapter extends BaseAdapter{
    private Context context;
    private List<Savemarcobutton> buttonarray=new ArrayList<Savemarcobutton>();
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public MarcoAdapter(final Context context,List<Savemarcobutton> list){
        this.context = context;
        size=list.size();
        buttonarray=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }


    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return buttonarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.marcobutton, null);
            TextView txt = (TextView)view.findViewById(R.id.textView69);
            ImageView ceb = (ImageView)view.findViewById(R.id.imageView18);
            txt.setText(buttonarray.get(position).marco_remark);
            ceb.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context, buttonarray.get(position).marco_icon)));
           // view.setBackgroundColor(ToColor(colorarray[position%7]));
            view.setBackground(context.getResources().getDrawable(R.drawable.control_back_10));
            mView.put(position, view);
        }
        return view;
    }
    public Savemarcobutton getselected(int position){
        return buttonarray.get(position);
    }
    public static int getResourdIdByResourdName(Context context, String ResName){
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
                // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
                // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
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
}
