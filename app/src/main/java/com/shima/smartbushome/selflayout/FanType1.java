package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.founction_command.fancontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */
public class FanType1 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    View view;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("fan_icon1");
        }
    };
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    private TextView tv_remark;
    private ImageView im_image;
    private SeekBar sb_process;
    boolean deletemode=false,receiveChange=false;
    CheckBox deleteview;
    LinearLayout delete;
    Savefan fancontent;
    fancontrol fc=new fancontrol();
    Context rootcontext;
    LayoutInflater inflater;
    LinearLayout type1linear;
    String iconstring="fan_icon1";
    LinearLayout fanshowpopup;
    public FanType1(Context context) {
        super(context);
        initview(context);
    }
    public FanType1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.fan_type1, this);
        rootcontext=context;
        sb_process = (SeekBar) view.findViewById(R.id.fantype1seekbar);
        sb_process.setOnSeekBarChangeListener(seekbarchange);
        deleteview=(CheckBox)view.findViewById(R.id.fantype1_cb);
        delete=(LinearLayout)view.findViewById(R.id.fantype1_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.fantype1swipe));
        im_image = (ImageView) view.findViewById(R.id.fantype1_img);
        tv_remark = (TextView) view.findViewById(R.id.fantype1_name);
        type1linear=(LinearLayout)view.findViewById(R.id.fantype1linear);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fanshowpopup=(LinearLayout)view.findViewById(R.id.fanshowpopup);
        fanshowpopup.setOnLongClickListener(this);
        delete.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.fantype1_delete:
                MainActivity.mgr.deletefan("fan", fancontent.fan_id, fancontent.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETEFAN);
                Toast.makeText(rootcontext, "delete succeed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }

    private SeekBar.OnSeekBarChangeListener seekbarchange = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Log.i(TAG,"onStopTrackingTouch");
            fc.FanChannelControl((byte)fancontent.subnetID,(byte)fancontent.deviceID,fancontent.channel,sb_process.getProgress(),MainActivity.mydupsocket);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG,"onStartTrackingTouch");
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //Log.i(TAG,"onProgressChanged");
            if(receiveChange){

                receiveChange=false;
            }else{
                fc.FanChannelControl((byte)fancontent.subnetID,(byte)fancontent.deviceID,fancontent.channel,progress,MainActivity.mydupsocket);
            }
        }
    };
    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            showPopupMenu(fanshowpopup);
        }

        return true;
    }

    AlertView settingalter,pairalter;
    EditText sub,dev,cha,name;
    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_open:

                        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                                itemclick);
                        View  selfview= inflater.inflate(R.layout.setting_faninfo, null);
                        sub = (EditText) selfview.findViewById(R.id.setfaninfo_sub);
                        dev = (EditText) selfview.findViewById(R.id.setfaninfo_dev);
                        cha = (EditText) selfview.findViewById(R.id.setfaninfo_chan);
                        name = (EditText) selfview.findViewById(R.id.setfaninfo_name);
                        sub.setText(String.valueOf(fancontent.subnetID));
                        dev.setText(String.valueOf(fancontent.deviceID));
                        cha.setText(String.valueOf(fancontent.channel));
                        name.setText(fancontent.fan_statement);
                        settingalter.addExtView(selfview);
                        settingalter.show();

                        break;
                    case R.id.action_pair:
                        pairalter = new AlertView("Select Device", null, "CANCEL",  null, null, rootcontext, AlertView.Style.Alert,
                                itemclick);
                        View selfviewx= inflater.inflate(R.layout.auto_pair_dialog, null);
                        ListView test=(ListView)selfviewx.findViewById(R.id.listView4);
                        DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(rootcontext,MainActivity.netdeviceList);
                        test.setAdapter(mLeDeviceListAdapter);
                        test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Savefan faninfo = new Savefan();
                                faninfo.room_id=fancontent.room_id;
                                faninfo.fan_id=fancontent.fan_id;
                                faninfo.channel = fancontent.channel;
                                faninfo.fan_statement=fancontent.fan_statement;
                                faninfo.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                faninfo.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                MainActivity.mgr.updatefan(faninfo);
                                Toast.makeText(rootcontext, "apir "+MainActivity.netdeviceList.get(position).get("devicename")+" succeed", Toast.LENGTH_SHORT).show();
                                fancontent.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                fancontent.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                pairalter.dismiss();
                            }
                        });
                        pairalter.addExtView(selfviewx);
                        pairalter.show();
                        break;
                }


                return false;

            }
        });

        popupMenu.show();

    }
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==settingalter){
                if(position==0){
                    Savefan faninfo = new Savefan();
                    faninfo.room_id=fancontent.room_id;
                    faninfo.fan_id=fancontent.fan_id;
                    faninfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    faninfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    faninfo.channel = Integer.parseInt(cha.getText().toString().trim());
                    faninfo.fan_statement=name.getText().toString().trim();
                    MainActivity.mgr.updatefan(faninfo);
                    fancontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    fancontent.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    fancontent.channel = Integer.parseInt(cha.getText().toString().trim());
                    fancontent.fan_statement=name.getText().toString().trim();
                    setremark(fancontent.fan_statement);
                }
            }else if(o==pairalter){

            }

        }
    };

    public void setdeletevisable(boolean vs){
        if(vs){
            deleteview.setVisibility(VISIBLE);
            deletemode=true;
            deleteview.setChecked(false);
        }else{
            deleteview.setVisibility(INVISIBLE);
            deletemode=false;
        }
    }
    public void setimage(int background) {
        im_image.setImageResource(background);
    }

    public void setremark(String remark) {
        tv_remark.setText(remark);
    }
    public void setReceiveChange(byte value){
        receiveChange=true;
        int pg=((int)(value)&0xff);
        if(pg==sb_process.getProgress()){
            receiveChange=false;
        }else{
            setprocess(pg);
        }

    }
    public int getprocess(){
        return sb_process.getProgress();
    }

    public int getType1fanid(){
        return fancontent.fan_id;
    }
    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }
    /**
     * 设置选中的状态
     *
     * @param process
     */
    public void setprocess(int process) {
        sb_process.setProgress(process);
    }
    public int getsubid(){
        return fancontent.subnetID;
    }
    public int getdevid(){
        return fancontent.deviceID;
    }
    public int getchannel(){
        return fancontent.channel;
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

    public void setcontant(Savefan sf){
        fancontent=sf;
        setremark(sf.fan_statement);
       // type1linear.setBackgroundColor(ToColor(colorarray[fancontent.fan_id%7]));
        type1linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        setimage(getResourdIdByResourdName(rootcontext, fancontent.fan_icon));
    }


}
