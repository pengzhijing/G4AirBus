package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shima.smartbushome.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public class statusTypeAdapter extends BaseAdapter{
    private Context context;
    private List<String> stringarray=new ArrayList<String>(){
        {
            add("status1");add("light_type3_on");add("status2");add("status3");
            add("status4");add("status5");
        }
    };
    private List<String> namearray=new ArrayList<String>(){
        {
            add("Light");add("RGB LED");add("Temperature(9in1 or other)");
            add("HVAC");add("Other");add("4T device");
        }
    };
    private int size;
    private LayoutInflater inflater=null;

    public statusTypeAdapter(final Context context){
        this.context = context;
        size=6;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return stringarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.adapter_light_type_item, null);
            TextView tv=(TextView)convertView.findViewById(R.id.textView13);
            ImageView ceb = (ImageView)convertView.findViewById(R.id.imageView9);
            tv.setText(namearray.get(position));
            ceb.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context, stringarray.get(position))));
        }
        return convertView;
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
}
