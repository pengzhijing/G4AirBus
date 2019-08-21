package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.shima.smartbushome.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16-6-15.
 */
public class MusicSongAdapter extends BaseAdapter {


    private Context context;
    private List<String> namearray;
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private int  selectItem=-1;
    public static final int select   = 0x7d02A3E9;
    private String[] colorarray={"5f000000", "5f000000"};
    public MusicSongAdapter(Context context,List<String> songdata){
        this.context = context;
        this.namearray=songdata;
        size=namearray.size();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return namearray.get(position);
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
            view = inflater.inflate(R.layout.music_songitem, null);
            TextView name = (TextView)view.findViewById(R.id.item_songname);
            name.setText(" "+(position+1)+": "+namearray.get(position));
            mView.put(position, view);
        }

        if (position == selectItem) {
            view.setBackgroundColor(select);
        }
        else {
            //view.setBackgroundColor(Color.TRANSPARENT);
            view.setBackgroundColor(ToColor(colorarray[(position%2)]));
        }
        return view;
    }
    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int getSelectItem(){
        return selectItem;
    }
    public void setNextorBack(String str){
        if(str.equals("next")){
            this.selectItem++;
            if(selectItem>size-1){
                selectItem=0;
            }
        }else if(str.equals("back")){
            this.selectItem--;
            if(selectItem<0){
                selectItem=size-1;
            }
        }
    }

    public List<String> getsonglist(){
        return namearray;
    }
    public String getselectSongname(){
        return namearray.get(selectItem);
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
