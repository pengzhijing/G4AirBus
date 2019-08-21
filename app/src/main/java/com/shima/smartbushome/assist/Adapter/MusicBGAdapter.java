package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shima.smartbushome.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MusicBGAdapter extends BaseAdapter {

    private Context context;
    private List<String> stringarray=new ArrayList<String>(){
        {
            add("music_bg1");add("music_bg2");add("music_bg3");add("music_bg4");
            add("music_bg5");add("music_bg6");add("music_bg7");add("music_bg8");
            add("music_bg9");add("music_bg10");
        }
    };
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;

    public MusicBGAdapter(final Context context){
        this.context = context;
        size=10;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();

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
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.mood_icon_select_item, null);
            ImageView ceb = (ImageView)view.findViewById(R.id.imageView6);
            ceb.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(stringarray.get(position))));
            mView.put(position, view);
        }
        return view;
    }

    public int getResourdIdByResourdName(String ResName){
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
