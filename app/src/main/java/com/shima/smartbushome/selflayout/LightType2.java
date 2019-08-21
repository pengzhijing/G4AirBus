package com.shima.smartbushome.selflayout;

/**
 * Created by Administrator on 16-5-23.
 */
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.LightIconAdapter;
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.founction_command.lightcontrol;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LightType2 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    private TextView tv_remark;
    private TextView tv_state;
    private ImageView im_image;
    private SeekBar sb_process;
    CheckBox deleteview;
    LinearLayout delete;
    boolean deletemode=false,receiveChange=false;
    Savelight lightcontent;
    lightcontrol lg;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("light_icon1");add("light_icon2");add("light_icon3");add("light_icon4");
            add("light_icon5");
        }
    };
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    // 命名空间，在引用这个自定义组件的时候，需要用到
    // private String namespace = http://schemas.android.com/apk/res/com.example.combinationview;
    // 标题
    private String remark;
    // 被选中的描述
    private String state;
    Context rootcontext;
    View view;
    LayoutInflater inflater;
    LinearLayout type2linear;
    AlertView settingalter,iconalter;
    String iconstring="light_icon1";
    public LightType2(Context context) {
        super(context);
        rootcontext=context;
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public LightType2(Context context, AttributeSet attrs) {
        super(context, attrs);
        rootcontext=context;
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.light_type2, this);
        lg=new lightcontrol();
        type2linear=(LinearLayout)view.findViewById(R.id.type2linear);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_remark = (TextView) view.findViewById(R.id.remark2);
        tv_state = (TextView) view.findViewById(R.id.state2);
        im_image = (ImageView) view.findViewById(R.id.imageView2);
        sb_process = (SeekBar) view.findViewById(R.id.seekBar);
        delete=(LinearLayout)view.findViewById(R.id.lighttype2_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.lighttype2swipe));
        deleteview=(CheckBox)view.findViewById(R.id.checkBox2);
        im_image.setOnClickListener(this);
        sb_process.setOnSeekBarChangeListener(seekbarchange);
        tv_remark.setOnLongClickListener(this);
        delete.setOnClickListener(this);
        // 初始化到子控件
        tv_state.setText("unknown");
    }
    /**
     * 设置图片
     *
     *
     */
    public void onClick(View v){
        switch(v.getId()){
            case R.id.imageView2:
                if(getprocess()>0){
                    setprocess(0);
                    HandleLightDimming(0);
                }else{
                    setprocess(100);
                    HandleLightDimming(100);
                }
                break;
            case R.id.lighttype2_delete:
                MainActivity.mgr.deletelight("light",lightcontent.light_id,lightcontent.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETELIGHT);
                Toast.makeText(rootcontext, "delete succeed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }
    private OnSeekBarChangeListener seekbarchange = new OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           // Log.i(TAG,"onStopTrackingTouch");
                HandleLightDimming(seekBar.getProgress());
                setstate(seekBar.getProgress()+"%");
            if(seekBar.getProgress()>0){
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_on"));
            }else{
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_off"));
            }
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
               // HandleLightDimming(progress);
                setstate(progress+"%");
            }


        }
    };
    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            showPopupMenu(tv_remark);
        }

        return true;
    }
    EditText sub,dev,cha,name;
    RadioButton type1,type2,type3,type4;
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_open:

                        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                                settingclick);
                        ViewGroup setextView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.setting_lightinfo, null);
                        sub = (EditText) setextView.findViewById(R.id.subedit);
                        dev = (EditText) setextView.findViewById(R.id.deviceedit);
                        cha = (EditText) setextView.findViewById(R.id.chanedit);
                        name = (EditText) setextView.findViewById(R.id.lightnameedit);
                        type1=(RadioButton)setextView.findViewById(R.id.lighttype1);
                        type2=(RadioButton)setextView.findViewById(R.id.lighttype2);
                        type3=(RadioButton)setextView.findViewById(R.id.lighttype3);
                        type4=(RadioButton)setextView.findViewById(R.id.lighttype4);
                        type2.setChecked(true);
                        final ImageView lighticon=(ImageView)setextView.findViewById(R.id.imageView10);
                        sub.setText(String.valueOf(lightcontent.subnetID));
                        dev.setText(String.valueOf(lightcontent.deviceID));
                        cha.setText(String.valueOf(lightcontent.channel));
                        name.setText(lightcontent.light_statement);
                        lighticon.setImageResource(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_on"));
                        lighticon.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                        settingclick);
                                View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                                GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                                icongrid.setAdapter(new LightIconAdapter(rootcontext));
                                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        iconstring = iconarray.get(position);
                                        lighticon.setImageResource(getResourdIdByResourdName(rootcontext, iconarray.get(position) + "_on"));
                                        iconalter.dismiss();
                                    }
                                });
                                iconalter.addExtView(selfviewx);
                                iconalter.show();

                            }
                        });
                        settingalter.addExtView(setextView);
                        settingalter.show();

                        break;
                    case R.id.action_pair:
                        final AlertView mAlertViewExt = new AlertView("Select Device", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                settingclick);
                        ViewGroup extView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.auto_pair_dialog, null);
                        ListView test=(ListView)extView.findViewById(R.id.listView4);
                        DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(rootcontext,MainActivity.netdeviceList);
                        test.setAdapter(mLeDeviceListAdapter);
                        test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Savelight lightinfo = new Savelight();
                                lightinfo.room_id = lightcontent.room_id;
                                lightinfo.light_id = lightcontent.light_id;
                                lightinfo.light_statement = lightcontent.light_statement;
                                lightinfo.channel = lightcontent.channel;
                                lightinfo.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                lightinfo.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                lightinfo.light_icon = lightcontent.light_icon;
                                lightinfo.lightType=lightcontent.lightType;
                                MainActivity.mgr.updatelight(lightinfo);
                                Toast.makeText(rootcontext, "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                                lightcontent.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                lightcontent.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                mAlertViewExt.dismiss();

                            }
                        });
                        mAlertViewExt.addExtView(extView);
                        mAlertViewExt.show();
                        break;
                }

                return false;
            }
        });
        popupMenu.show();
    }
    public com.bigkoo.alertview.OnItemClickListener settingclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }
            if(o==settingalter){
                if(position==0){
                    Savelight lightinfo = new Savelight();
                    lightinfo.room_id=lightcontent.room_id;
                    lightinfo.light_id=lightcontent.light_id;
                    lightinfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    lightinfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    lightinfo.channel = Integer.parseInt(cha.getText().toString().trim());
                    lightinfo.light_statement=name.getText().toString().trim();
                    lightinfo.light_icon=iconstring;
                    if(type1.isChecked()){
                        lightinfo.lightType=1;
                    }else if(type2.isChecked()){
                        lightinfo.lightType=2;
                    }else if(type3.isChecked()){
                        lightinfo.lightType=3;
                    }else if(type4.isChecked()){
                        lightinfo.lightType=4;
                    }
                    MainActivity.mgr.updatelight(lightinfo);
                    lightcontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    lightcontent.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    lightcontent.channel = Integer.parseInt(cha.getText().toString().trim());
                    lightcontent.light_statement=name.getText().toString().trim();
                    lightcontent.light_icon=iconstring;
                    setremark(lightcontent.light_statement);
                    setimage(getResourdIdByResourdName(rootcontext, iconstring + "_off"));
                    if(lightinfo.lightType!=2){
                        broadcastUpdate(FounctionActivity.ACTION_DELETELIGHT);
                    }
                }
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

    public void setcontant(Savelight lg){
        lightcontent=lg;
        setremark(lg.light_statement);
        //type2linear.setBackgroundColor(ToColor(colorarray[lightcontent.light_id%7]));
        type2linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        setimage(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_off"));
    }

    public void setimage(int background) {
        im_image.setImageResource(background);
    }

    public void setremark(String remark) {
        tv_remark.setText(remark);
    }

    public void setstate(String state){
        tv_state.setText(state);
    }

    public void setReceiveChange(byte value){
        receiveChange=true;
        int pg=((int)(value)&0xff);
        if(pg==sb_process.getProgress()){
            setstate(String.valueOf(pg)+"%");
            if(pg>0){
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_on"));
            }else{
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_off"));
            }
            receiveChange=false;
        }else{
            setstate(String.valueOf(pg)+"%");
            if(pg>0){
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_on"));
            }else{
                setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_off"));
            }
            setprocess(pg);
        }

    }


    //由EFFF指令转过来的处理状态
    public void setReceiveChange2(byte value){
        int pg=((int)(value)&0xff);
        if (pg==100&&sb_process.getProgress()==0){
            setstate(String.valueOf(pg)+"%");
            setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_on"));
            setprocess(pg);
        }
        if(pg==0){
            setstate(String.valueOf(pg)+"%");
            setimage(getResourdIdByResourdName(rootcontext,lightcontent.light_icon+"_off"));
            setprocess(0);
        }
    }


    public int getprocess(){
        return sb_process.getProgress();
    }

    public int getType2lightid(){
        return lightcontent.light_id;
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
        return lightcontent.subnetID;
    }
    public int getdevid(){
        return lightcontent.deviceID;
    }
    public int getchannel(){
        return lightcontent.channel;
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
    /***************************************************************************************/
    // 处理调光,根据seekBar，位置，进度条
    public void HandleLightDimming(int intProgressOfSeekbar) {
        try {
            byte byteSubnetID, byteDeviceID;
            int intChns, intTemp;
            int curLightSta;
            String curLightStatu = "OFF";
            byteSubnetID = (byte) lightcontent.subnetID;
            byteDeviceID = (byte) lightcontent.deviceID;
            intChns = lightcontent.channel;

            // 还是照样通过这个函数拉爱处理灯光问题
            if (lg.SingleChannelControl(byteSubnetID, byteDeviceID,
                    intChns, intProgressOfSeekbar,MainActivity.mydupsocket) == true) {
                // 并且要在参数结构体当中说明当前灯的亮度值和开关状态，判断前提--根据灯亮度值来判断是开还是关
                if (intProgressOfSeekbar > 0) {
                    curLightStatu = "ON";
                    curLightSta = 1;

                } else {
                    curLightStatu = "OFF";
                    curLightSta = 0;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
