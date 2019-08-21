package com.shima.smartbushome.selflayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.assist.holocolorpicker.ColorPicker;
import com.shima.smartbushome.assist.holocolorpicker.SVBar;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.founction_command.lightcontrol;

/**
 * Created by Administrator on 16-5-31.
 */
public class LightType3 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    Context rootcontext;
    View view;
    LayoutInflater inflater;
    private TextView tv_remark,tv_state;
    private TextView tv_color;
    private ImageView im_image;
    private Button setcolor;
    LinearLayout delete;
    private CheckBox deleteview;
    private Savelight lightcontent;
    ColorDrawable dr;
    lightcontrol lc;
    AlertView settingalter,iconalter;
    LinearLayout type3linear;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public LightType3(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public LightType3(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        view = View.inflate(context, R.layout.light_type3, this);
        rootcontext=context;
        type3linear=(LinearLayout)view.findViewById(R.id.type3linear);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        delete=(LinearLayout)view.findViewById(R.id.lighttype3_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.lighttype3swipe));
        tv_remark = (TextView) view.findViewById(R.id.ltype3_remark);
        tv_color=(TextView) view.findViewById(R.id.color);
        tv_state=(TextView) view.findViewById(R.id.tv_state);
        im_image = (ImageView) view.findViewById(R.id.ltype3_icon);
        setcolor=(Button)view.findViewById(R.id.setcolor);
        deleteview=(CheckBox)view.findViewById(R.id.ltype_checkbox);
        lc=new lightcontrol();
        im_image.setOnClickListener(this);
        setcolor.setOnClickListener(this);
        delete.setOnClickListener(this);
        tv_remark.setOnLongClickListener(this);
        setimage(R.mipmap.light_type3_off);
    }
    int oldcolor=Color.argb(255,255,255,255);
    public void onClick(View v){
       switch(v.getId()){
           case R.id.ltype3_icon:
               String state=tv_state.getText().toString().trim();
               if(state.equals("ON")){
                   ColorDrawable dr = (ColorDrawable) tv_color.getBackground();
                   int col_int = dr.getColor();
                   oldcolor=col_int;
                   int color=Color.argb(255,0,0,0);
                   setimage(R.mipmap.light_type3_off);
                   tv_color.setBackgroundColor(color);
                   lc.ARGBlightcontrol((byte)lightcontent.subnetID,(byte)lightcontent.deviceID,color,MainActivity.mydupsocket);
                   tv_state.setText("OFF");
               }else if((state.equals("OFF"))||(state.equals("unknown"))){
                  // int color=Color.argb(255,255,255,255);
                   tv_color.setBackgroundColor(oldcolor);
                   setimage(R.mipmap.light_type3_on);
                   lc.ARGBlightcontrol((byte) lightcontent.subnetID, (byte) lightcontent.deviceID, oldcolor,MainActivity.mydupsocket);
                   tv_state.setText("ON");
               }
               break;
           case R.id.setcolor:
               AlertDialog.Builder set_builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
               View colorview= inflater.inflate(R.layout.view_pickcolor, null);
               final ColorPicker picker = (ColorPicker) colorview.findViewById(R.id.view5);
               SVBar svBar = (SVBar)colorview. findViewById(R.id.view7);
               picker.addSVBar(svBar);
               ColorDrawable dr = (ColorDrawable) tv_color.getBackground();
               if(dr!=null){
                   int col_int = dr.getColor();
                   picker.setColor(col_int);
               }
               set_builder
                       .setTitle("Color Selection")
                       .setView(colorview)
                       .setCancelable(false)
                       .setPositiveButton("RUN", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               tv_color.setBackgroundColor(picker.getColor());
                               if(Integer.toHexString(picker.getColor()).equals("ff000000")){
                                   tv_state.setText("OFF");
                                   setimage(R.mipmap.light_type3_off);
                               }else{
                                   tv_state.setText("ON");
                                   setimage(R.mipmap.light_type3_on);
                               }
                               lc.ARGBlightcontrol((byte)lightcontent.subnetID,(byte)lightcontent.deviceID,picker.getColor(),MainActivity.mydupsocket);
                               dialogInterface.dismiss();
                           }
                       })
                       .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss(); // 执行点击取消按钮的业务逻辑
                           }
                       })
                       .show();
               break;
           case R.id.lighttype3_delete:
               MainActivity.mgr.deletelight("light",lightcontent.light_id,lightcontent.room_id);
               broadcastUpdate(FounctionActivity.ACTION_DELETELIGHT);
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
    EditText sub,dev,name;
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
                switch (item.getItemId()){
                    case R.id.action_open:

                        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                                settingclick);
                        View selfview= inflater.inflate(R.layout.setting_lighttypeinfo, null);
                        sub = (EditText) selfview.findViewById(R.id.lededitTextsub);
                        dev = (EditText) selfview.findViewById(R.id.lededitText2dev);
                        name = (EditText) selfview.findViewById(R.id.lededitTextname);
                        type1=(RadioButton)selfview.findViewById(R.id.light3type1);
                        type2=(RadioButton)selfview.findViewById(R.id.light3type2);
                        type3=(RadioButton)selfview.findViewById(R.id.light3type3);
                        type4=(RadioButton)selfview.findViewById(R.id.light3type4);
                        type3.setChecked(true);
                        sub.setText(String.valueOf(lightcontent.subnetID));
                        dev.setText(String.valueOf(lightcontent.deviceID));
                        name.setText(lightcontent.light_statement);
                        settingalter.addExtView(selfview);
                        settingalter.show();

                        break;
                    case R.id.action_pair:
                        final AlertView mAlertViewExt = new AlertView("Select Device", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                null);
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
            if(o==settingalter){
                if(position==-1){

                }else if(position==0){
                    Savelight lightinfo = new Savelight();
                    lightinfo.room_id=lightcontent.room_id;
                    lightinfo.light_id=lightcontent.light_id;
                    lightinfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    lightinfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    lightinfo.channel=lightcontent.channel;
                    lightinfo.light_statement=name.getText().toString().trim();
                    if(type1.isChecked()){
                        lightinfo.lightType=1;
                    }else if(type2.isChecked()){
                        lightinfo.lightType=2;
                    }else if(type3.isChecked()){
                        lightinfo.lightType=3;
                    }else if(type4.isChecked()){
                        lightinfo.lightType=4;
                    }
                    lightinfo.light_icon=lightcontent.light_icon;
                    MainActivity.mgr.updatelight(lightinfo);
                    lightcontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    lightcontent.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    lightcontent.light_statement=name.getText().toString().trim();
                    setremark(lightcontent.light_statement);
                    if(lightinfo.lightType!=3){
                        broadcastUpdate(FounctionActivity.ACTION_DELETELIGHT);
                    }
                }
            }

        }
    };
    public void setcontant(Savelight sl){
        this.lightcontent=sl;
       // type3linear.setBackgroundColor(ToColor(colorarray[lightcontent.light_id % 7]));
        type3linear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        tv_remark.setText(sl.light_statement);
    }
    public void setimage(int background) {
        im_image.setImageResource(background);
    }
    public void setremark(String remark){
        tv_remark.setText(remark);
    }
    public void setdeletevisable(boolean x){
        if(x)
        {
         deleteview.setVisibility(VISIBLE);
         deleteview.setChecked(false);
        }
        else{
            deleteview.setVisibility(INVISIBLE);
        }
    }

    public void setReceiveChange(byte[] data){
        if(data[0]==(byte)0&&data[1]==(byte)0&&data[2]==(byte)0){
            tv_state.setText("OFF");
            int color=Color.argb(255,0,0,0);
            tv_color.setBackgroundColor(color);
            setimage(R.mipmap.light_type3_off);
        }else{
            tv_color.setBackgroundColor(ToColor(data));
            tv_state.setText("ON");
            setimage(R.mipmap.light_type3_on);
        }
    }


    public int ToColor(byte[] data){
        int color=0;
        int rin,gin,bin;
        rin=(255*(data[0]&0xff))/100;
        gin=(255*(data[1]&0xff))/100;
        bin=(255*(data[2]&0xff))/100;
        String r=Integer.toHexString(rin);
        switch (r.length()){
            case 0:r="00";break;
            case 1:r="0"+r;break;
        }
        String g=Integer.toHexString(gin);
        switch (g.length()){
            case 0:g="00";break;
            case 1:g="0"+g;break;
        }
        String b=Integer.toHexString(bin);
        switch (b.length()){
            case 0:b="00";break;
            case 1:b="0"+b;break;
        }
       color=Color.argb(255,Integer.parseInt(r,16),Integer.parseInt(g,16),Integer.parseInt(b,16));
        return color;
    }
    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }

    public int getType3lightid(){
        return lightcontent.light_id;
    }
    public int getsubid(){
        return lightcontent.subnetID;
    }
    public int getdevid(){
        return lightcontent.deviceID;
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
