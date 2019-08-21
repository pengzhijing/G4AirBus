package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.centercontrol.MarcoAddActivity;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savesong;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16-6-15.
 */
public class MarcoItemAdapter extends BaseAdapter {


    private Context context;
    private List<Savemarco> marcoearray;
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private int  selectItem=-1;
    AlertView deletealter;
    int delete_item_position=0;
    public static final int select   = 0x62424242;
    public MarcoItemAdapter(Context context, List<Savemarco> marcoearray){
        this.context = context;
        this.marcoearray=marcoearray;
        size=marcoearray.size();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return marcoearray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(Savemarco arg0) {//删除指定位置的item
        marcoearray.remove(arg0);
        this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源

    }

    public void update(Savemarco item, int arg0) {//在指定位置插入item
        if(item.sentorder>=marcoearray.get(arg0).sentorder){
            int replaceorder=marcoearray.get(arg0).sentorder;//被取代的senorder
            int orilorder=item.sentorder;//被移动的sentorder
            int marid=item.marco_id;
            MainActivity.mgr.updatemarco(item,9999999);//先改变被移动的sentorder,做缓存

            for(int i=marcoearray.size()-1;i>=0; i--){
                if(marcoearray.get(i).sentorder>=replaceorder&&marcoearray.get(i).sentorder<orilorder){
                    MainActivity.mgr.updatemarco(marcoearray.get(i),marcoearray.get(i).sentorder+1);
                }
            }

            item.sentorder=9999999;
            MainActivity.mgr.updatemarco(item,replaceorder);//先改变被移动的sentorder,做缓存
        }else{
            int replaceorder=marcoearray.get(arg0).sentorder;
            int orilorder=item.sentorder;
            MainActivity.mgr.updatemarco(item, 9999999);//先改变被移动的sentorder
            for(int i=0;i<marcoearray.size(); i++){
                if(marcoearray.get(i).sentorder<=replaceorder&&marcoearray.get(i).sentorder>orilorder){
                    MainActivity.mgr.updatemarco(marcoearray.get(i),marcoearray.get(i).sentorder-1);
                }
            }
            item.sentorder=9999999;
            MainActivity.mgr.updatemarco(item, replaceorder);//先改变被移动的sentorder,做缓存
        }
        broadcastUpdate(MarcoAddActivity.MISSION_REFLASH);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.marcoitem, null);
            ImageView typeicon=(ImageView)view.findViewById(R.id.marcoitem_type);
            TextView room = (TextView)view.findViewById(R.id.marcoitem_room);
            TextView action = (TextView)view.findViewById(R.id.marcoitem_action);
            TextView value = (TextView)view.findViewById(R.id.marcoitem_value);
            TextView delete=(TextView)view.findViewById(R.id.marcoitem_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_item_position=position;
                    deletealter = new AlertView("Warning", " Are you sure to delete the Action ? ", "CANCEL",
                            new String[]{"YES"}, null, context, AlertView.Style.Alert, deleteclick);
                    deletealter .setCancelable(false);
                    deletealter .show();

                }
            });

            switch (marcoearray.get(position).control_type){
                case 1:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"light")));
                    break;
                case 2:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"hvac")));
                    break;
                case 3:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"curtain")));
                    break;
                case 4:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"music")));
                    break;
                case 5:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"other")));
                    break;
                case 6:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"fan")));
                    break;
                case 7:
                    typeicon.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context,"media")));
                    break;
            }
            room.setText(marcoearray.get(position).room + "-" + marcoearray.get(position).device);
            //device.setText(marcoearray.get(position).device);
            action.setText(getAction(marcoearray.get(position))+" :");
            if(getvalue(marcoearray.get(position)).equals("LED")){
                value.setText("");
                value.setBackgroundColor(marcoearray.get(position).value2);
            }else{
                value.setText(getvalue(marcoearray.get(position)));
            }
            mView.put(position, view);
        }

       /* if (position == selectItem) {
            view.setBackgroundColor(select);
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);

        }*/
        return view;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
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
    public int getSelectItem(){
        return selectItem;
    }

    public com.bigkoo.alertview.OnItemClickListener deleteclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else if(position==0){
                MainActivity.mgr.deletemarco("marco",marcoearray.get(delete_item_position).marco_id,marcoearray.get(delete_item_position).sentorder);
                Toast.makeText(context, "delete succeed", Toast.LENGTH_SHORT).show();
                broadcastUpdate(MarcoAddActivity.MISSION_REFLASH);
            }
        }
    };

    public String getAction(Savemarco value){
        String action="";
        switch (value.control_type){
            case 1:
                switch (value.value1){
                    case 0:action="Power";break;
                    case 1:action="Dimmer";break;
                    case 2:action="LED Color";break;
                }
                break;
            case 2:
                switch (value.value1){
                    case 0:action="Power";break;
                    case 1:action="Temperature";break;
                    case 2:action="Fan Speed";break;
                    case 3:action="Mode";break;
                }
                break;
            case 3:action="Power";break;
            case 4:
                switch (value.value1){
                    case 1:action="Source";break;
                    case 3:action="Radio Channel";break;
                    case 4:action="Play control";break;
                    case 5:action="Volume";break;
                    case 6:action="SD Song";break;
                }
                break;
            case 5:
                switch (value.value1){
                    case 0:action="Other Type1";break;
                    case 1:action="Other Type2";break;
                }
                break;
            case 6:
                action="Fan Power";
                break;
            case 7:
                action="Media Control";
                break;
        }
        return action;
    }

    public String getvalue(Savemarco value){
        String valueresult="";
        switch (value.control_type){
            case 1:
                switch (value.value1){
                    case 0:
                        switch (value.value2){
                            case 0:valueresult="OFF";break;
                            case 1:valueresult="ON";break;
                        }
                        break;
                    case 1:
                        valueresult=value.value2+"%";
                        break;
                    case 2:
                        valueresult="LED";
                        break;
                }
                break;
            case 2:
                switch (value.value1){
                    case 0:
                        switch (value.value2){
                            case 0:valueresult="OFF";break;
                            case 1:valueresult="ON";break;
                        }
                        break;
                    case 1:
                        switch (value.value3){
                            case 0:valueresult=value.value2+" ℃ in Cool Mode";break;
                            case 1:valueresult=value.value2+" ℃ in Heat Mode";break;
                            case 2:valueresult=value.value2+" ℃ in Fan Mode";break;
                            case 3:valueresult=value.value2+" ℃ in Auto Mode";break;
                        }
                        break;
                    case 2:
                        switch (value.value2){
                            case 0:valueresult="Auto";break;
                            case 1:valueresult="High";break;
                            case 2:valueresult="Medium";break;
                            case 3:valueresult="Low";break;
                        }
                        break;
                }
                break;
            case 3:
                switch (value.value2){
                    case 0:valueresult="OFF";break;
                    case 1:valueresult="ON";break;
                }
                break;
            case 4:
                switch (value.value1){
                    case 1:
                        switch (value.value2){
                            case 1:valueresult="Music";break;
                            case 2:valueresult="Audio-In";break;
                            case 4:valueresult="Radio";break;
                        }
                        break;
                    case 3:
                        valueresult="Channel-"+value.value3;
                        break;
                    case 4:
                        switch (value.value2){
                            case 1:valueresult="Back";break;
                            case 2:valueresult="Next";break;
                            case 3:valueresult="Play";break;
                            case 4:valueresult="Pause";break;
                        }
                        break;
                    case 5:valueresult=value.value2+"%";break;
                    case 6:
                        List<Savesong> song=new ArrayList<>();
                        List<Savesong> thissonglist=new ArrayList<>();
                        song= MainActivity.mgr.querysong();

                        for(int t=0;t<song.size();t++){
                            if(song.get(t).room_id==value.room_id){
                                thissonglist.add(song.get(t));
                            }
                        }

                        for(int i=0;i<thissonglist.size();i++){
                            if(thissonglist.get(i).album_num==value.value2&&thissonglist.get(i).song_num==value.value3){
                                valueresult=thissonglist.get(i).song_name;
                                break;
                            }
                        }
                        break;
                }
                break;
            case 5:
                switch (value.value1){
                    case 0:
                        switch (value.value2){
                            case 0:valueresult="OFF";break;
                            case 1:valueresult="ON";break;
                        }
                        break;
                    case 1:
                        switch (value.value2){
                            case 0:valueresult="OFF";break;
                            case 1:valueresult="ON";break;
                        }
                        break;
                }
                break;
            case 6:
                switch (value.value2){
                    case 0:valueresult="Fan off";break;
                    case 1:valueresult="Fan low Speed";break;
                    case 2:valueresult="Fan Middle Speed";break;
                    case 3:valueresult="Fan High Speed";break;
                    case 4:valueresult="Fan Full Speed";break;
                }
                break;
            case 7:
                valueresult=radiobuttonlist[value.value2-1];
                break;
        }
        return valueresult;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //  intent.putExtra(ACTION_BACKPRESS, 1);
        context.sendBroadcast(intent);
    }
    String[] radiobuttonlist=new String[]{"ON","OFF","VOLUME -","VOLUME +","VOLUME MUTE",
            "UP","DOWN","LEFT","RIGHT","OK","VIEW1","VIEW2",
            "VIEW3","VIEW4","BACK","HOME","SETTING","NUM 1","NUM 2"
            ,"NUM 3","NUM 4","NUM 5","NUM 6","NUM 7","NUM 8","NUM 9","NUM *","NUM 0","NUM #"};

}
