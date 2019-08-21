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
public class OnoffTypeAdapter extends BaseAdapter{
    private Context context;
    private List<String> stringarray=new ArrayList<String>(){
        {
            add("other1");add("other2");add("other3");add("other4");add("other5");
        }
    };
    private List<String> namearray=new ArrayList<String>(){
        {
            add("Single Type");add("Interlocking Type");add("LogicModule Type");add("Scene Type");add("Sequence Type");
        }
    };
    private int size;
    private LayoutInflater inflater=null;

    public OnoffTypeAdapter(final Context context){
        this.context = context;
        size=5;
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
