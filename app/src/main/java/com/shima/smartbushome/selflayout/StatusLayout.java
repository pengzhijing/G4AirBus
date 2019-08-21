package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.StatusIconAdapter;
import com.shima.smartbushome.centercontrol.StatusActivity;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Savestatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StatusLayout extends LinearLayout implements View.OnLongClickListener {
    private TextView tv_name;
    private TextView tv_status;
    private CheckBox deleteview;
    private ImageView statusicon;
    Savestatus statuscontent;
    LinearLayout statuslinear;
    boolean deletemode=false,receiveChange=false;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("room_type1");add("room_type2");add("room_type3");add("room_type4");
            add("room_type5");add("room_type6");add("room_type7");add("room_type8");
            add("room_type9");add("room_type10");add("room_type11");add("room_type12");
            add("room_type13");add("room_type14");add("room_type15");
            add("light_icon1_on");add("light_icon2_on");add("light_icon3_on");add("light_icon4_on");
            add("light_icon5_on");add("light_type3_on");
            add("hvacitem_icon");add("hvac");add("light");add("music");
            add("curtain");add("other");add("nio");
            add("marco_icon1");add("marco_icon2");add("marco_icon3");add("marco_icon4");
            add("marco_icon5");add("marco_icon6");add("marco_icon7");add("marco_icon8");
            add("marco_icon9");
            add("other_icon1_on");add("other_icon2_on");add("other_icon3_on");add("other_icon4_on");
            add("other_icon5_on");add("other_icon6_on");add("other_icon7_on");add("other_icon8_on");
            add("mood_icon1");add("mood_icon2");add("mood_icon3");add("mood_icon4");
            add("mood_icon5");add("mood_icon6");add("mood_icon7");add("mood_icon8");
            add("mood_icon9");add("mood_icon10");
        }
    };
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public  DBManager mgr;
    AlertView settingalter,iconalter;
    Context rootcontext;
    View view;
    LayoutInflater inflater;
    String iconstring="room_type1";
    public StatusLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public StatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.view_statuslayout, this);
        rootcontext=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_name=(TextView)view.findViewById(R.id.status_name);
        tv_status=(TextView)view.findViewById(R.id.status_detail);
        tv_name.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_status.setMovementMethod(ScrollingMovementMethod.getInstance());
        deleteview=(CheckBox)view.findViewById(R.id.status_delete);
        statuslinear=(LinearLayout)view.findViewById(R.id.statuslinear);
        statusicon=(ImageView)view.findViewById(R.id.status_icon);
        tv_name.setOnLongClickListener(this);
        tv_name.setText("unknown");
    }


    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            showPopupMenu(tv_name);
        }

        return true;
    }

    EditText sub,dev,cha,name;
    RadioButton Celsius,Fahrenheit;
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
                        ViewGroup setextView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.setting_statusinfo, null);
                        sub = (EditText) setextView.findViewById(R.id.et_statussub);
                        dev = (EditText) setextView.findViewById(R.id.et_statusdev);
                        cha = (EditText) setextView.findViewById(R.id.et_statusch);
                        name = (EditText) setextView.findViewById(R.id.et_statusname);
                        Celsius=(RadioButton)setextView.findViewById(R.id.rb_statusc);
                        Fahrenheit=(RadioButton)setextView.findViewById(R.id.rb_statusf);
                        switch (statuscontent.type){
                            case 1://light
                            case 5://other
                                Celsius.setChecked(false);
                                Fahrenheit.setChecked(false);
                                Celsius.setEnabled(false);
                                Fahrenheit.setEnabled(false);
                                cha.setText(String.valueOf(statuscontent.channel));
                                break;
                            case 2://rgb
                            case 4://ac
                                Celsius.setChecked(false);
                                Fahrenheit.setChecked(false);
                                Celsius.setEnabled(false);
                                Fahrenheit.setEnabled(false);
                                cha.setText("no need to set channel");
                                cha.setEnabled(false);
                                break;
                            case 3://temp
                            case 6://4t
                                switch (statuscontent.unit)
                                {
                                    case 1:Celsius.setChecked(true);break;
                                    case 2:Fahrenheit.setChecked(true);break;
                                }
                                cha.setText(String.valueOf(statuscontent.channel));
                                break;
                        }

                        final ImageView statusicon=(ImageView)setextView.findViewById(R.id.im_statusicon);
                        sub.setText(String.valueOf(statuscontent.subnetID));
                        dev.setText(String.valueOf(statuscontent.deviceID));

                        name.setText(statuscontent.name);
                        statusicon.setImageResource(getResourdIdByResourdName(rootcontext, statuscontent.status_icon));
                        iconstring=statuscontent.status_icon;
                        statusicon.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                        settingclick);
                                View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                                GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                                icongrid.setAdapter(new StatusIconAdapter(rootcontext));
                                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        iconstring = iconarray.get(position);
                                        statusicon.setImageResource(getResourdIdByResourdName(rootcontext, iconarray.get(position)));
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
                                Savestatus statusinfo = new Savestatus();
                                statusinfo.status_id = statuscontent.status_id;
                                statusinfo.name = statuscontent.name;
                                statusinfo.type=statuscontent.type;
                                statusinfo.unit=statuscontent.unit;
                                statusinfo.status_icon=statuscontent.status_icon;
                                statusinfo.channel = statuscontent.channel;
                                statusinfo.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                statusinfo.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                MainActivity.mgr.updatestatus(statusinfo);
                                Toast.makeText(rootcontext, "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                                statuscontent.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                statuscontent.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
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
                    Savestatus statusinfo = new Savestatus();
                    statusinfo.status_id=statuscontent.status_id;
                    statusinfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    statusinfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    statusinfo.name=name.getText().toString().trim();
                     switch (statuscontent.type){
                         case 1://light
                         case 5://other
                             statusinfo.unit=statuscontent.unit;
                             statusinfo.channel = Integer.parseInt(cha.getText().toString().trim());
                             statuscontent.channel = Integer.parseInt(cha.getText().toString().trim());
                             break;
                         case 2://rgb
                         case 4://ac
                             statusinfo.unit=statuscontent.unit;
                             statusinfo.channel = statuscontent.channel;
                             statuscontent.channel =statuscontent.channel;
                             break;
                         case 3://temp
                         case 6://4t
                             if(Celsius.isChecked()){
                                 statusinfo.unit=1;
                             }else if(Fahrenheit.isChecked()){
                                 statusinfo.unit=2;
                             }
                             statusinfo.channel = Integer.parseInt(cha.getText().toString().trim());
                             statuscontent.channel = Integer.parseInt(cha.getText().toString().trim());
                             break;
                     }
                    statusinfo.status_icon=iconstring;

                    MainActivity.mgr.updatestatus(statusinfo);
                    statuscontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                     statuscontent.deviceID=Integer.parseInt(dev.getText().toString().trim());

                     statuscontent.name=name.getText().toString().trim();
                     statuscontent.status_icon=iconstring;
                    setname(statuscontent.name);
                     seticon(getResourdIdByResourdName(rootcontext, iconstring));
                     broadcastUpdate(StatusActivity.ACTION_REFLASH);
                }
            }

        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        rootcontext.sendBroadcast(intent);
    }
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

    public void setname(String remark){
        tv_name.setText(remark);
    }
    public void setstatus(String detial){tv_status.setText(detial);}
    public void seticon(int icon){
        statusicon.setImageResource(icon);
    }
    public void setstate(String state){
        tv_status.setText(state);
    }
    public void setcontan(Savestatus lg){
        statuscontent=lg;
       // statuslinear.setBackgroundColor(ToColor(colorarray[(statuscontent.status_id%7)]));
        statuslinear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        seticon(getResourdIdByResourdName(rootcontext, statuscontent.status_icon));
        setname(lg.name);
    }
    public void setstatuscolor(int color){
        tv_status.setText("LED Color");
        tv_status.setTextColor(0xFFB0B0B0);
        tv_status.setBackgroundColor(color);
    }

    public int getstatusid(){
        return statuscontent.status_id;
    }

    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }

    public int getsubid(){
        return statuscontent.subnetID;
    }
    public int getdevid(){
        return statuscontent.deviceID;
    }
    public int getchannel(){
        return statuscontent.channel;
    }

    public int getResourdIdByResourdName(Context context, String ResName){
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

}
