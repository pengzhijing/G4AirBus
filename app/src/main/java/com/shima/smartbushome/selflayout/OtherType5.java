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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.OtherIconAdapter;
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.founction_command.othercontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/14.
 */
public class OtherType5 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    private TextView tv_remark;
    private TextView tv_state;
    private ImageView im_image;
    //private Switch sw_onoff;
    private CheckBox deleteview;
    LinearLayout delete;
    Saveother othercontent;
    Button on,off;
    boolean deletemode=false,receiveChange=false;
    othercontrol oc;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("other_icon1");add("other_icon2");add("other_icon3");add("other_icon4");
            add("other_icon5");add("other_icon6");add("other_icon7");add("other_icon8");
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
    Context rootcontext;
    View view,selfview;
    LayoutInflater inflater;
    LinearLayout type1linear;
    String iconstring="other_icon1_off";
    public OtherType5(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public OtherType5(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.other_type1, this);
        rootcontext=context;
        type1linear=(LinearLayout)view.findViewById(R.id.othertype1linear);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_remark = (TextView) view.findViewById(R.id.othertype1remark);
        tv_state = (TextView) view.findViewById(R.id.othertype1state);
        im_image = (ImageView) view.findViewById(R.id.othertype1imageView);
        //sw_onoff = (Switch) view.findViewById(R.id.switch2);
        deleteview=(CheckBox)view.findViewById(R.id.othertype1checkBox);
        delete=(LinearLayout)view.findViewById(R.id.othertype1_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.othertype1swipe));
        on=(Button)view.findViewById(R.id.othertype1on);
        off=(Button)view.findViewById(R.id.othertype1off);
        on.setOnClickListener(this);
        off.setOnClickListener(this);
        delete.setOnClickListener(this);
        // sw_onoff.setOnClickListener(this);
        oc=new othercontrol();
        im_image.setOnClickListener(this);
        tv_remark.setOnLongClickListener(this);
        // deleteview.setOnClickListener(this);
        // 初始化到子控件
        tv_state.setText("unknown");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.othertype1imageView:
                if(((tv_state.getText().toString().trim()).equals("unknown"))||(
                        (tv_state.getText().toString().trim()).equals("OFF")
                        )){
                    HandleLightOnoff(othercontent.channel_1,othercontent.channel_2);
                    setstate("ON");
                    setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_on"));
                }else{
                    HandleLightOnoff(othercontent.channel_1,0);
                    setstate("OFF");
                    setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off"));
                }
                break;
            case R.id.othertype1on:
                HandleLightOnoff(othercontent.channel_1,othercontent.channel_2);
                setstate("ON");
                setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_on"));
                break;
            case R.id.othertype1off:
                HandleLightOnoff(othercontent.channel_1,0);
                setstate("OFF");
                setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off"));
                break;
            case R.id.othertype1_delete:
                MainActivity.mgr.deleteother("other", othercontent.other_id, othercontent.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETEOTHER);
                Toast.makeText(rootcontext, "delete succeed", Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
    }
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }
    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            showPopupMenu(tv_remark);
        }


        return true;
    }
    AlertView settingalter,iconalter;
    EditText sub,dev,cha1,cha2,name;
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
                        View selfview= inflater.inflate(R.layout.setting_otherinfotype5, null);
                        sub = (EditText) selfview.findViewById(R.id.setting_othertype3_sub);
                        dev = (EditText) selfview.findViewById(R.id.setting_othertype3_dev);
                        cha1 = (EditText) selfview.findViewById(R.id.setting_othertype3_lgon);
                        cha2 = (EditText) selfview.findViewById(R.id.setting_othertype3_lgoff);
                        name = (EditText) selfview.findViewById(R.id.setting_othertype3_name);
                        final ImageView othericon=(ImageView)selfview.findViewById(R.id.setting_othertype3_img);
                        sub.setText(String.valueOf(othercontent.subnetID));
                        dev.setText(String.valueOf(othercontent.deviceID));
                        cha1.setText(String.valueOf(othercontent.channel_1));
                        cha2.setText(String.valueOf(othercontent.channel_2));
                        name.setText(othercontent.other_statement);
                        iconstring=othercontent.other_icon;
                        othericon.setImageResource(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_on"));
                        othericon.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                        settingclick);
                                View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                                GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                                icongrid.setAdapter(new OtherIconAdapter(rootcontext));
                                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        iconstring = iconarray.get(position);
                                        othericon.setImageResource(getResourdIdByResourdName(rootcontext, iconarray.get(position) + "_on"));
                                        iconalter.dismiss();
                                    }
                                });
                                iconalter.addExtView(selfviewx);
                                iconalter.show();
                            }
                        });
                        settingalter.addExtView(selfview);
                        settingalter.show();
                        break;
                    case R.id.action_pair:
                        final AlertView mAlertViewExt = new AlertView("Select Device", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                settingclick);
                        View selfviewt= inflater.inflate(R.layout.auto_pair_dialog, null);
                        ListView test=(ListView)selfviewt.findViewById(R.id.listView4);
                        DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(rootcontext,MainActivity.netdeviceList);
                        test.setAdapter(mLeDeviceListAdapter);
                        test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Saveother otherinfo = new Saveother();
                                otherinfo.room_id = othercontent.room_id;
                                otherinfo.other_id = othercontent.other_id;
                                otherinfo.other_statement=othercontent.other_statement;
                                otherinfo.channel_1=othercontent.channel_1;
                                otherinfo.channel_2=othercontent.channel_2;
                                otherinfo.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                otherinfo.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                otherinfo.other_icon=othercontent.other_icon;
                                MainActivity.mgr.updateother(otherinfo);
                                Toast.makeText(rootcontext, "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                                othercontent.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                othercontent.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                mAlertViewExt.dismiss();
                            }
                        });
                        mAlertViewExt.addExtView(selfviewt);
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
            if(o==settingalter){
                if(position==0){
                    Saveother otherinfo = new Saveother();
                    otherinfo.room_id=othercontent.room_id;
                    otherinfo.other_id=othercontent.other_id;
                    otherinfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    otherinfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    otherinfo.channel_1 = Integer.parseInt(cha1.getText().toString().trim());
                    otherinfo.channel_2 = Integer.parseInt(cha2.getText().toString().trim());
                    otherinfo.other_statement=name.getText().toString().trim();
                    otherinfo.other_icon=iconstring;
                    MainActivity.mgr.updateother(otherinfo);
                    othercontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    othercontent.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    othercontent.channel_1 = Integer.parseInt(cha1.getText().toString().trim());
                    othercontent.channel_2 = Integer.parseInt(cha2.getText().toString().trim());
                    othercontent.other_statement=name.getText().toString().trim();
                    othercontent.other_icon=iconstring;
                    setremark(othercontent.other_statement);
                    setimage(getResourdIdByResourdName(rootcontext, iconstring + "_off"));

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
    public void setcontan(Saveother lg){
        othercontent=lg;
        tv_remark.setText(lg.other_statement);
       // type1linear.setBackgroundColor(ToColor(colorarray[(othercontent.other_id % 7)]));
        type1linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        im_image.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off")));
    }
    public void setReceiveChange(byte value){
        if(value==(byte)0){
            setstate("OFF");
            setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off"));
        }else if(value==(byte)100){
            setstate("ON");
            setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_on"));
        }
    }

    public int getType4otherid(){
        return othercontent.other_id;
    }

    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }

    public String getstate(){
        return tv_state.getText().toString().trim();
    }

    public int getsubid(){
        return othercontent.subnetID;
    }
    public int getdevid(){
        return othercontent.deviceID;
    }
    public int getchannel(){
        return othercontent.channel_1;
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
    /***************************************************************************************/
// 如何处理灯的开关，需要参数，1.灯位置 2.灯类型，是什么样的灯，如果是开灯，就关灯，如果是关灯就开灯，
    public void HandleLightOnoff(int areaNo,int sequenceNo ) {
        
        try {
            byte byteSubnetID, byteDeviceID;// 子网ID，设备ID

            int bytSuccess = 0;
            byteSubnetID = (byte) othercontent.subnetID;
            byteDeviceID = (byte) othercontent.deviceID;
            // 执行开关灯
            if (oc.SequenceControl(byteSubnetID, byteDeviceID,
                    areaNo,sequenceNo,MainActivity.mydupsocket) == true) {
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
