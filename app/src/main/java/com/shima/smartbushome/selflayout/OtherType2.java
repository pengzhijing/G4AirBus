package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
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
 * Created by Administrator on 2016/9/18.
 */
public class OtherType2 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    private TextView tv_remark,tv_state;
    private ImageView im_image;
    private Button bt_open,bt_close,bt_stop;
    private CheckBox deleteview;
    LinearLayout delete;
    Saveother othercontent;
    boolean deletemode=false,receiveChange=false;
    othercontrol cc;
    private String remark;
    private String state;
    public DBManager mgr;
    Context rootcontext;
    View view,selfview;
    LayoutInflater inflater;
    LinearLayout othertype2linear;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("other_icon1");add("other_icon2");add("other_icon3");add("other_icon4");
            add("other_icon5");add("other_icon6");add("other_icon7");add("other_icon8");
        }
    };
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    String iconstring="other_icon1_off";

    public OtherType2(Context context) {
        super(context);
        initview(context);
    }
    public OtherType2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public void initview(Context context){
        view = View.inflate(context, R.layout.other_type2, this);
        rootcontext=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_remark=(TextView)view.findViewById(R.id.othertype2rmark);
        othertype2linear=(LinearLayout)view.findViewById(R.id.othertype2linear);
        tv_state=(TextView)view.findViewById(R.id.othertype2state);
        bt_open=(Button)view.findViewById(R.id.othertype2on);
        bt_stop=(Button)view.findViewById(R.id.othertype2stop);
        bt_close=(Button)view.findViewById(R.id.othertype2off);
        deleteview=(CheckBox)view.findViewById(R.id.othertype2delete);
        im_image=(ImageView)view.findViewById(R.id.othertype2imageView);
        delete=(LinearLayout)view.findViewById(R.id.othertype2_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.othertype2swipe));
        bt_open.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_close.setOnClickListener(this);
        delete.setOnClickListener(this);
        tv_remark.setOnLongClickListener(this);
        cc=new othercontrol();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.othertype2on:
                cc.CurtainControl((byte)othercontent.subnetID,(byte)othercontent.deviceID,othercontent.channel_1,
                        othercontent.channel_2,"open",MainActivity.mydupsocket);
                setstate("open");
                setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_on"));
                break;
            case R.id.othertype2off:
                cc.CurtainControl((byte)othercontent.subnetID,(byte)othercontent.deviceID,othercontent.channel_1,
                        othercontent.channel_2,"close",MainActivity.mydupsocket);
                setstate("close");
                setimage(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off"));
                break;
            case R.id.othertype2stop:
                cc.CurtainControl((byte)othercontent.subnetID,(byte)othercontent.deviceID,othercontent.channel_1,
                        othercontent.channel_2,"stop",MainActivity.mydupsocket);
                setstate("stop");
                break;
            case R.id.othertype2_delete:
                MainActivity.mgr.deleteother("other", othercontent.other_id, othercontent.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETEOTHER);
                Toast.makeText(rootcontext, "delete succeed", Toast.LENGTH_SHORT).show();
                break;
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
                        View selfview= inflater.inflate(R.layout.setting_otherinfo, null);
                        sub = (EditText) selfview.findViewById(R.id.othersubed);
                        dev = (EditText) selfview.findViewById(R.id.otherdeved);
                        cha1 = (EditText) selfview.findViewById(R.id.otherchan1ed);
                        cha2 = (EditText) selfview.findViewById(R.id.otherchan2ed);
                        name = (EditText) selfview.findViewById(R.id.othernameed);
                        final ImageView othericon=(ImageView)selfview.findViewById(R.id.imageView11);
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
                                otherinfo.other_statement = othercontent.other_statement;
                                otherinfo.channel_1 = othercontent.channel_1;
                                otherinfo.channel_2 = othercontent.channel_2;
                                otherinfo.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                otherinfo.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                otherinfo.other_icon = othercontent.other_icon;
                                MainActivity.mgr.updateother(otherinfo);
                                Toast.makeText(rootcontext, "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                                othercontent.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                othercontent.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
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

    public void setremark(String remark){
        tv_remark.setText(remark);
    }
    public void setstate(String state){
        tv_state.setText(state);
    }
    public void setimage(int background) {
        im_image.setImageResource(background);
    }
    public void setcontan(Saveother lg){
        othercontent=lg;
        tv_remark.setText(lg.other_statement);
        //othertype2linear.setBackgroundColor(ToColor(colorarray[(othercontent.other_id % 7)]));
        othertype2linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        im_image.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(rootcontext, othercontent.other_icon + "_off")));
    }
    public int getType2otherid(){
        return othercontent.other_id;
    }

    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }
    public int getsubid(){
        return othercontent.subnetID;
    }
    public int getdevid(){
        return othercontent.deviceID;
    }
    public int[] getchannel(){
        int[] result=new int[2];
        result[0]=othercontent.channel_1;
        result[1]=othercontent.channel_2;
        return result;
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
}
