package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.shima.smartbushome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16-6-15.
 */
public class MusicOptionAdapter extends BaseAdapter {


    private Context context;
    private List<String> stringarray;
    private List<Drawable> drawarray;
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public MusicOptionAdapter(final Context context,List<String> listdata){
        this.context = context;
        this.stringarray=listdata;
        this.drawarray=new ArrayList<Drawable>(){
            {add(context.getResources().getDrawable(R.drawable.musicmenu_album));
                add(context.getResources().getDrawable(R.drawable.musicmenu_allsong));
                add(context.getResources().getDrawable(R.drawable.musicmenu_like));
              //  add(context.getResources().getDrawable(R.drawable.musicmenu_theme));
                add(context.getResources().getDrawable(R.drawable.musicmenu_more));
                add(context.getResources().getDrawable(R.drawable.musicmenu_folder));
            }
        };;
            size=5;
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
            view = inflater.inflate(R.layout.music_folderitem, null);
            TextView txt = (TextView)view.findViewById(R.id.txtName);
            ImageView ceb = (ImageView)view.findViewById(R.id.ad_musicop_img);
            txt.setText(stringarray.get(position));
            ceb.setImageDrawable(drawarray.get(position));
            mView.put(position, view);
        }

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
}
