package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.LightIconAdapter;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.founction_command.lightcontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16-5-31.
 */
public class LightType4 extends LinearLayout implements View.OnLongClickListener{
    private TextView tv_remark;
    private TextView tv_state;
    private ImageView im_image;
    private Button bt_press;
    private CheckBox deleteview;
    LinearLayout delete;
    Savelight lightcontent;
    boolean deletemode=false,receiveChange=false;
    lightcontrol lc;
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
    public DBManager mgr;
    AlertView settingalter,iconalter;
    Context rootcontext;
    View view,selfview;
    LayoutInflater inflater;
    String iconstring="light_icon1";
    LinearLayout type4linear;
    public LightType4(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public LightType4(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.light_type4, this);
        rootcontext=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        type4linear=(LinearLayout)view.findViewById(R.id.lighttype4linear);
        tv_remark = (TextView) view.findViewById(R.id.ltype4_remark);
        tv_state = (TextView) view.findViewById(R.id.ltype4_state);
        im_image = (ImageView) view.findViewById(R.id.ltype4_icon);
        bt_press=(Button)view.findViewById(R.id.ltype4button);
        deleteview=(CheckBox)view.findViewById(R.id.ltype4_checkbox);

       bt_press.setOnTouchListener(new OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_UP) {
                   setstate("OFF");
                   setimage(getResourdIdByResourdName(rootcontext, lightcontent.light_icon+"_off"));
                   HandleLightOnoff(0);
                   Log.v("test", "放开操作");
               }
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   setstate("ON");
                   setimage(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_on"));
                   HandleLightOnoff(100);
                   Log.v("test", "按下操作");
               }
               return false;
           }
       });
        tv_remark.setOnLongClickListener(this);
        tv_state.setText("unknown");
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }

    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            showPopupMenu(tv_remark);
        }

        return true;
    }
    EditText sub,dev,cha,name;
    RadioButton type1,type2,type3,type4;
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
                        type4.setChecked(true);
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
                    if(lightinfo.lightType!=1){
                        broadcastUpdate(FounctionActivity.ACTION_DELETELIGHT);
                    }
                }
            }

        }
    };


    /**
     * 设置图片
     *
     *
     */
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
    public void setremark(String remark){
        tv_remark.setText(remark);
    }
    public void setstate(String state){
        tv_state.setText(state);
    }
    public void setcontant(Savelight lg){
        lightcontent=lg;
       // type4linear.setBackgroundColor(ToColor(colorarray[(lightcontent.light_id%7)]));
        type4linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        tv_remark.setText(lg.light_statement);
        im_image.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_off")));
    }
    public void setReceiveChange(byte value){
        receiveChange=true;
        if(value==(byte)0){
            setstate("OFF");
            setimage(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_off"));
            receiveChange=false;
        }else if(value==(byte)100){
            setstate("ON");
            setimage(getResourdIdByResourdName(rootcontext, lightcontent.light_icon + "_on"));
            receiveChange=false;
        }
    }


    public int getType4lightid(){
        return lightcontent.light_id;
    }

    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }

    public String getstate(){
        return tv_state.getText().toString().trim();
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
    public void setDialogdismiss(boolean result,DialogInterface dialog){
        try
        {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing");
            field.setAccessible( true );
// 将mShowing变量设为false，表示对话框已关闭
            field.set(dialog, result );
            dialog.dismiss();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
// 如何处理灯的开关，需要参数，1.灯位置 2.灯类型，是什么样的灯，如果是开灯，就关灯，如果是关灯就开灯，
    public void HandleLightOnoff(int waitSendValue) {
        lc=new lightcontrol();
        try {
            byte byteSubnetID, byteDeviceID;// 子网ID，设备ID
            int intChns, intTemp;// 是哪一路，灯亮度，
            int bytSuccess = 0;
            byteSubnetID = (byte) lightcontent.subnetID;
            byteDeviceID = (byte) lightcontent.deviceID;
            intChns = lightcontent.channel;
            // 执行开关灯
            if (lc.SingleChannelControl(byteSubnetID, byteDeviceID,
                    intChns, waitSendValue,MainActivity.mydupsocket) == true) {
                bytSuccess = 1;

            } else {
                bytSuccess = 0;
                Log.i("btn", "bytSuccess = 0");
            }
            if (bytSuccess == 1) {
                // 在这里发送数据给Handler，要求刷新数据
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
