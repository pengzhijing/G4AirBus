package com.shima.smartbushome.centercontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.WheelView;
import com.shima.smartbushome.assist.holocolorpicker.ColorPicker;
import com.shima.smartbushome.assist.holocolorpicker.SVBar;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.database.Saveroom;
import com.shima.smartbushome.database.Savesong;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.List;

public class MarcoAddDetailActivity extends AppCompatActivity {
    WheelView roomwva,devicewva,actionwva;
    TextView room,device,action,value;
    Button bt_room,bt_device,bt_action,bt_value;
    AlertView addroomalter,adddevicealter,addactionalter,addvaluealter,addvaluealter2,addvaluealter3
            ,addvaluealter4;
    String selectedroom="",selecttype="",selecteddevice="",selectedaction="",selecedtvalue=""
            ,selecedtvalue2="",selecedtvalue3="",selecedtvalue4="";
    List<Saveroom> roomlist=new ArrayList<>();
    List<Savelight> lightlist=new ArrayList<>();
    List<Savehvac> hvaclist=new ArrayList<>();
    List<Savemusic> musiclist=new ArrayList<>();
    List<Savecurtain> curtainlist=new ArrayList<>();
    List<Saveother> otherlist=new ArrayList<>();
    List<Savefan> fanlist=new ArrayList<>();
    List<Savemedia> medialist=new ArrayList<>();
    List<Savemediabutton> mediabuttonlist=new ArrayList<>();
    int marcoID=0,sentorder=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marco_add_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.marco_add_detail_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Add Mission");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {

            toolbar.setPadding(0,getStatusBarHeight(this),0,0);
        }
        //获取窗口对象
        Window window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Intent x=getIntent();
        marcoID=x.getIntExtra("marcoID",0);
        sentorder=x.getIntExtra("marcoOrder",0);
        roomlist=MainActivity.mgr.queryroom();
        lightlist=MainActivity.mgr.querylight();
        hvaclist=MainActivity.mgr.queryhvac();
        musiclist=MainActivity.mgr.querymusic();
        curtainlist=MainActivity.mgr.querycurtain();
        otherlist=MainActivity.mgr.queryother();
        fanlist=MainActivity.mgr.queryfan();
        medialist=MainActivity.mgr.querymedia();
        mediabuttonlist=MainActivity.mgr.querymediabutton();
        initView();
        initroomalter();
        initdevicealter();
        initactionalter();
        initvaluealter();
        initvaluealter2();
        initvaluealter3();
        initvaluealter4();
    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int statusBarHeight=0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void initView(){
        roomwva=new WheelView(this);
        devicewva=new WheelView(this);
        actionwva=new WheelView(this);
        room=(TextView)findViewById(R.id.marcoadddetailroom);
        device=(TextView)findViewById(R.id.marcoadddetaildevice);
        action=(TextView)findViewById(R.id.marcoadddetailaction);
        value=(TextView)findViewById(R.id.textView60);
        bt_room=(Button)findViewById(R.id.button33);
        bt_device=(Button)findViewById(R.id.button34);
        bt_action=(Button)findViewById(R.id.button35);
        bt_value=(Button)findViewById(R.id.button37);
    }
    public void initroomalter(){
        List<String> stringroomlist=new ArrayList<>();
        for(int i=0;i<roomlist.size();i++){
            stringroomlist.add(roomlist.get(i).room_name);
        }
        addroomalter = new AlertView("Select Room", null, "CANCEL",  new String[]{"SAVE"}, null,this, AlertView.Style.Alert,
                itemclick);
        RelativeLayout holder=new RelativeLayout(this);
        holder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        roomwva.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        holder.addView(roomwva);
        roomwva.setOffset(1);
        roomwva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                room.setText("Select Room: " + item);
                selectedroom = item;
                device.setText("Select Device: ");
                selecteddevice = "";
                action.setText("Select Action: ");
                selecttype = "";
                selectedaction = "";
            }
        });
        roomwva.setItems(stringroomlist);
        addroomalter.addExtView(holder);
        addroomalter.setCancelable(false);
    }

    public void initdevicealter(){

        adddevicealter = new AlertView("Select Device", null, "CANCEL",  new String[]{"SAVE"}, null,this, AlertView.Style.Alert,
                itemclick);
        RelativeLayout holder=new RelativeLayout(this);
        holder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        devicewva.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        holder.addView(devicewva);
        devicewva.setOffset(1);
        devicewva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (item.length() > 0) {
                    device.setText("Select Device: " + item);
                    String[] de = item.split("-");
                    selecttype = devicetypelist.get(selectedIndex-1);//de[0];
                    selecteddevice = de[1];
                    action.setText("Select Action: ");
                    selectedaction = "";
                }


            }
        });
        adddevicealter.addExtView(holder);
        adddevicealter.setCancelable(false);
    }

    public void initactionalter(){

        addactionalter = new AlertView("Select Action", null, "CANCEL",  new String[]{"SAVE"}, null,this, AlertView.Style.Alert,
                itemclick);
        RelativeLayout holder=new RelativeLayout(this);
        holder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        actionwva.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        holder.addView(actionwva);
        actionwva.setOffset(1);
        actionwva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if(item.length()>0){
                    selectedaction=item;
                    action.setText("Select Action: "+item);
                }

            }
        });
        addactionalter.addExtView(holder);
        addactionalter.setCancelable(false);
    }

    ListView valuelist;
    public void initvaluealter(){
        addvaluealter = new AlertView("Select Value", null, "CANCEL",  null, null,this, AlertView.Style.Alert,
                itemclick);
        valuelist=new ListView(this);
        valuelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                value.setText(((TextView)view.findViewById(R.id.simplelistitemid)).getText().toString());
                selecedtvalue=((TextView)view.findViewById(R.id.simplelistitemid)).getText().toString();
                addvaluealter.dismiss();
            }
        });
        addvaluealter.addExtView(valuelist);
        addvaluealter.setCancelable(false);

    }

    SeekBar seekvalue;
    BubbleSeekBar bubbleSeekBar;
    public void initvaluealter2(){
        addvaluealter2 = new AlertView("Select Value", null, "CANCEL",  new String[]{"SAVE"}, null,this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.adapter_seekbar, null);

        bubbleSeekBar=(BubbleSeekBar)selfview.findViewById(R.id.bubbleview);
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                value.setText(progress + "%");
                selecedtvalue2 = String.valueOf(progress);
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });

       /* seekvalue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(progress+"%");
                selecedtvalue2=String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
        addvaluealter2.addExtView(selfview);
        addvaluealter2.setCancelable(false);
    }

    ColorPicker picker;
    public void initvaluealter3(){
        addvaluealter3 = new AlertView("Color Selection", null, "CANCEL",
                null,  new String[]{"SAVE"}, MarcoAddDetailActivity.this, AlertView.Style.Alert, itemclick);
        addvaluealter3.setCancelable(false);
        ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(MarcoAddDetailActivity.this).inflate(R.layout.view_pickcolor, null);
        picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
        SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
        picker.addSVBar(svBar);
        ColorDrawable dr = (ColorDrawable) value.getBackground();
        if(dr!=null){
            int col_int = dr.getColor();
            picker.setColor(col_int);
        }
        addvaluealter3.addExtView(extcolorView);

    }
    SeekBar tempseekvalue;
    String temp_choose_mode="Cool Mode";
    public void initvaluealter4(){
        addvaluealter4 = new AlertView("Select Value", null, "CANCEL",  new String[]{"SAVE"}, null,this, AlertView.Style.Alert,
                itemclick);
        final List<String> stringarray=new ArrayList<String>(){
            {
                add("Cool Mode");add("Heat Mode");add("Auto Mode");add("Fan Mode");
            }
        };
        ViewGroup exttempView = (ViewGroup) LayoutInflater.from(MarcoAddDetailActivity.this).inflate(R.layout.marco_add_actemp, null);
        ListView choose=(ListView)exttempView.findViewById(R.id.listView8);
        tempseekvalue=(SeekBar)exttempView.findViewById(R.id.seekBar5);
        final TextView temp=(TextView)exttempView.findViewById(R.id.textView91);
        if(selecedtvalue4.equals("")){
            temp.setText("0℃");
        }else{
            temp.setText(selecedtvalue4+"℃");
        }

        final ArrayAdapter<String> savelist=new ArrayAdapter<String>(this,
                R.layout.simple_list_item_choice, stringarray);
        choose.setAdapter(savelist);
        choose.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        switch (temp_choose_mode){
            case "Cool Mode":choose.performItemClick(choose.getAdapter().getView(0,null,null),0,0);break;
            case "Heat Mode":choose.performItemClick(choose.getAdapter().getView(1, null, null), 1, 1);break;
            case "Auto Mode":choose.performItemClick(choose.getAdapter().getView(2,null,null),2,2);break;
            case "Fan Mode":choose.performItemClick(choose.getAdapter().getView(2,null,null),2,2);break;
        }
        choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                temp_choose_mode=stringarray.get(position);
                value.setText(selecedtvalue4 + "℃"+" in "+temp_choose_mode);
            }
        });
        tempseekvalue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp.setText(progress+"℃");
                value.setText(progress + "℃"+" in "+temp_choose_mode);
                selecedtvalue4 = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addvaluealter4.addExtView(exttempView);
        addvaluealter4.setCancelable(false);
    }

    public void addroom(View v){
        addroomalter.show();
        bt_device.setEnabled(true);
        bt_device.setTextColor(0xffffffff);
    }

    List<String> stringdevicelist=new ArrayList<>();//显示出来的名字的list
    int roomid=0,oldroomid=0;
    List<String> devicetypelist=new ArrayList<>();//选择功能不同类型的ui

    public void adddevice(View v){
        bt_action.setEnabled(true);
        bt_action.setTextColor(0xffffffff);
        for(int i=0;i<roomlist.size();i++){
            if(selectedroom.equals(roomlist.get(i).room_name)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(roomid!=oldroomid){
            if(stringdevicelist.size()>0){stringdevicelist.clear();}
            if(devicetypelist.size()>0){devicetypelist.clear();}
            for(int i=0;i<lightlist.size();i++){
                if(lightlist.get(i).room_id==roomid){
                    stringdevicelist.add("Light-"+lightlist.get(i).light_statement);
                    devicetypelist.add("Light"+lightlist.get(i).lightType);
                }
            }
            for(int i=0;i<hvaclist.size();i++){
                if(hvaclist.get(i).room_id==roomid){
                    stringdevicelist.add("HVAC-"+hvaclist.get(i).hvac_remark);
                    devicetypelist.add("HVAC");
                }
            }
            for(int i=0;i<musiclist.size();i++){
                if(musiclist.get(i).room_id==roomid){
                    stringdevicelist.add("Music-"+"zaudio");
                    devicetypelist.add("Music");
                }
            }
            for(int i=0;i<curtainlist.size();i++){
                if(curtainlist.get(i).room_id==roomid){
                    stringdevicelist.add("Curtain-"+curtainlist.get(i).curtain_remark);
                    devicetypelist.add("Curtain");
                }
            }
            for(int i=0;i<otherlist.size();i++){
                if(otherlist.get(i).room_id==roomid){
                    stringdevicelist.add("Other-"+otherlist.get(i).other_statement);
                    devicetypelist.add("Other"+otherlist.get(i).other_type);
                }
            }
            for(int i=0;i<fanlist.size();i++){
                if(fanlist.get(i).room_id==roomid){
                    stringdevicelist.add("Fan-"+fanlist.get(i).fan_statement);
                    devicetypelist.add("Fan");
                }
            }
            for(int i=0;i<medialist.size();i++){
                if(medialist.get(i).room_id==roomid){
                    stringdevicelist.add("Media-"+medialist.get(i).media_statement);
                    devicetypelist.add("Media");
                }
            }
            if(stringdevicelist.size()==0){stringdevicelist.add("");}
            devicewva.setItems(stringdevicelist);
            devicewva.setSeletion(0);
        }
        oldroomid=roomid;
        adddevicealter.show();
    }


    public void addaction(View v){
        bt_value.setEnabled(true);
        bt_value.setTextColor(0xffffffff);
        if(selecttype.equals("Light1")){
            actionwva.setItems(lightcommand1);
        }else if(selecttype.equals("Light2")){
            actionwva.setItems(lightcommand2);
        }else if(selecttype.equals("Light3")){
            actionwva.setItems(lightcommand3);
        }else if(selecttype.equals("HVAC")){
            actionwva.setItems(accommand);
        }else if(selecttype.equals("Music")){
            actionwva.setItems(musiccommand);
        }
        else if(selecttype.equals("Curtain")){
            actionwva.setItems(curtaincommand);
        }else if(selecttype.equals("Other1")){
            actionwva.setItems(othercommand1);
        }else if(selecttype.equals("Other2")){
            actionwva.setItems(othercommand2);
        }else if(selecttype.equals("Fan")){
            actionwva.setItems(fancommand);
        } else if(selecttype.equals("Media")){
            actionwva.setItems(mediacommand);
        }else{
            actionwva.setItems(nothingcommand);
        }
        actionwva.setSeletion(0);
        addactionalter.show();
    }

    public void addvalue(View v){
        if(selectedaction.equals("Power")||selectedaction.equals("Other Type1")||selectedaction.equals("Other Type2")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, power));
            addvaluealter.show();
        }else if(selectedaction.equals("Fan Speed")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, fanspeed));
            addvaluealter.show();
        }else if(selectedaction.equals("Source")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, source));
            addvaluealter.show();
        }else if(selectedaction.equals("SD Song")){
            List<Savesong> savesonglist=new ArrayList<>();
            savesonglist=MainActivity.mgr.querysong();
            if(songlist.size()>0){songlist.clear();}
            for(int i=0;i<savesonglist.size();i++){
                if(savesonglist.get(i).room_id==roomid){
                    songlist.add(savesonglist.get(i).song_name);
                }
            }
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, songlist));
            addvaluealter.show();
        }else if(selectedaction.equals("Radio Channel")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, radiolist));
            addvaluealter.show();
        }else if(selectedaction.equals("Play control")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, playcontrol));
            addvaluealter.show();
        }else if(selectedaction.equals("Dimmer")){
            addvaluealter2.show();
        }else if(selectedaction.equals("Volume")){
            addvaluealter2.show();
        }else if(selectedaction.equals("LED")){
            addvaluealter3.show();
        }else if(selectedaction.equals("Temperature")){
            addvaluealter4.show();
        }else if(selectedaction.equals("Fan Power")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, fancontrol));
            addvaluealter.show();
        }else if(selectedaction.equals("Media Control")){
            valuelist.setAdapter(new ArrayAdapter(this,
                    R.layout.simplelistitem, radiobuttonlist));
            addvaluealter.show();
        }
    }
    public void cancel(View v){
        finish();
    }

    public void save(View v){
        if(selecedtvalue!=""||selecedtvalue2!=""||selecedtvalue3!=""||selecedtvalue4!=""){
            int coltrol_type=getControlType();
            int subnetID=0,deviceID=0,value1=0,value2=0,value3=0;
            Savelight getlight=new Savelight();
            Savehvac gethvac=new Savehvac();
            Savemusic getmusic=new Savemusic();
            Savecurtain getcutrain=new Savecurtain();
            Saveother getother=new Saveother();
            Savefan getfan=new Savefan();
            Savemedia getmedia=new Savemedia();
            switch(coltrol_type){
                case 1:
                    getlight=(Savelight)getdevice(coltrol_type);
                    subnetID=getlight.subnetID;
                    deviceID=getlight.deviceID;
                    break;
                case 2:
                    gethvac=(Savehvac)getdevice(coltrol_type);
                    subnetID=gethvac.subnetID;
                    deviceID=gethvac.deviceID;
                    break;
                case 3:
                    getcutrain=(Savecurtain)getdevice(coltrol_type);
                    subnetID=getcutrain.subnetID;
                    deviceID=getcutrain.deviceID;
                    break;
                case 4:
                    getmusic=(Savemusic)getdevice(coltrol_type);
                    subnetID=getmusic.subnetID;
                    deviceID=getmusic.deviceID;
                    break;
                case 5:
                    getother=(Saveother)getdevice(coltrol_type);
                    subnetID=getother.subnetID;
                    deviceID=getother.deviceID;
                    break;
                case 6:
                    getfan=(Savefan)getdevice(coltrol_type);
                    subnetID=getfan.subnetID;
                    deviceID=getfan.deviceID;
                    break;
                case 7:
                    getmedia=(Savemedia)getdevice(coltrol_type);
                    subnetID=getmedia.subnetID;
                    deviceID=getmedia.deviceID;
                    break;
            }
            value1=getvalue1(coltrol_type);
            value2=getvalue2(coltrol_type, value1);
            value3=getvalue3(value1,coltrol_type);

            Savemarco marcosave=new Savemarco();
            marcosave.marco_id=marcoID;
            marcosave.room_id=roomid;
            marcosave.room=selectedroom;
            marcosave.device=selecteddevice;
            marcosave.subnetID=subnetID;
            marcosave.deviceID=deviceID;
            marcosave.control_type=coltrol_type;
            marcosave.value1=value1;
            marcosave.value2=value2;
            marcosave.value3=value3;
            marcosave.sentorder=sentorder;
            MainActivity.mgr.addmarco(marcosave);
            this.setResult(MarcoAddActivity.SAVEMISSION);
            finish();
        }else{
            Toast.makeText(MarcoAddDetailActivity.this, "Please select a action", Toast.LENGTH_SHORT).show();
        }

    }
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==addroomalter){
                if(position==0){
                    device.setText("Select Device: ");
                    selecteddevice = "";
                    action.setText("Select Action: ");
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selecttype="";
                    selectedaction="";
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3 = "";
                }else if(position==-1){
                   /* room.setText("Select Room: ");
                    selectedroom="";
                    roomwva.setSeletion(0);
                    device.setText("Select Device: ");
                    selecteddevice = "";
                    action.setText("Select Action: ");
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selecttype="";
                    selectedaction="";
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3="";*/
                }
            }else if(o==adddevicealter){
                if(position==-1){
                  /*  device.setText("Select Device: " );
                    selecteddevice = "";
                    action.setText("Select Action: ");
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selecttype="";
                    selectedaction="";
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3="";*/
                }else if(position==0){
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selectedaction="";
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3="";
                }
            }else if(o==addactionalter){
                if(position==-1){
                  /*  action.setText("Select Action: ");
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selectedaction="";
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3="";*/
                }else if(position==0){
                    value.setText("");
                    value.setBackgroundColor(0x00000000);
                    selecedtvalue="";
                    selecedtvalue2="";
                    selecedtvalue3="";
                }
            }else if(o==addvaluealter2){
                if(position==0){

                }
            }else if(o==addvaluealter3){
                if(position==0){
                    value.setText("");
                    value.setBackgroundColor(picker.getColor());
                    selecedtvalue3=Integer.toHexString(picker.getColor());
                    addvaluealter3.dismiss();
                }
            }

        }
    };

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.marco_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public int getControlType(){
        int result=0;
        switch (selecttype){
            case "Light1": result=1;break;
            case "Light2": result=1;break;
            case "Light3": result=1;break;
            case "HVAC": result=2;break;
            case "Curtain":result=3;break;
            case "Music":result=4;break;
            case "Other1": result=5;break;
            case "Other2": result=5;break;
            case "Fan": result=6;break;
            case "Media":result=7;break;
        }
        return result;
    }

    public Object getdevice(int type){
        Savelight resultlight=new Savelight();
        Savehvac resulthvac=new Savehvac();
        Savemusic resultmusic=new Savemusic();
        Savecurtain resultcutrain=new Savecurtain();
        Saveother resultother=new Saveother();
        Savefan resultfan=new Savefan();
        Savemedia resultmedia=new Savemedia();
        switch (type){
            case 1:
                for(int i=0;i<lightlist.size();i++){
                    if(lightlist.get(i).room_id==roomid&&lightlist.get(i).light_statement.equals(selecteddevice)){
                        resultlight=lightlist.get(i);
                        return resultlight;
                    }
                }
                break;
            case 2:
                for(int i=0;i<hvaclist.size();i++){
                    if(hvaclist.get(i).room_id==roomid&&hvaclist.get(i).hvac_remark.equals(selecteddevice)){
                        resulthvac=hvaclist.get(i);
                        return resulthvac;
                    }
                }
                break;
            case 3:
                for(int i=0;i<curtainlist.size();i++){
                    if(curtainlist.get(i).room_id==roomid&&curtainlist.get(i).curtain_remark.equals(selecteddevice)){
                        resultcutrain=curtainlist.get(i);
                        return resultcutrain;
                    }
                }
                break;
            case 4:
                for(int i=0;i<musiclist.size();i++){
                    if(musiclist.get(i).room_id==roomid&&"zaudio".equals(selecteddevice)){
                        resultmusic=musiclist.get(i);
                        return resultmusic;
                    }
                }
                break;
            case 5:
                for(int i=0;i<otherlist.size();i++){
                    if(otherlist.get(i).room_id==roomid&&otherlist.get(i).other_statement.equals(selecteddevice)){
                        resultother=otherlist.get(i);
                        return resultother;
                    }
                }
                break;
            case 6:
                for(int i=0;i<fanlist.size();i++){
                    if(fanlist.get(i).room_id==roomid&&fanlist.get(i).fan_statement.equals(selecteddevice)){
                        resultfan=fanlist.get(i);
                        return resultfan;
                    }
                }
                break;
            case 7:
                for(int i=0;i<medialist.size();i++){
                    if(medialist.get(i).room_id==roomid&&medialist.get(i).media_statement.equals(selecteddevice)){
                        resultmedia=medialist.get(i);
                        return resultmedia;
                    }
                }
                break;
                default:break;
        }

        return null;
    }

    public int getvalue1(int controltype){
        int result=0;
        if(selectedaction.equals("Dimmer")||selectedaction.equals("Volume")){
            if(controltype==1){
                result=1;
            }else if(controltype==4){
                result=5;
            }
        }else if(selectedaction.equals("LED")){
            result=2;
        }else if(selectedaction.equals("Temperature")){
            result=1;
        }else{
            switch (controltype){
                case 1:
                    result=0;
                    break;
                case 2:
                    switch (selectedaction){
                        case "Power": result=0;break;
                        case "Fan Speed":result=2;break;
                        case "Mode": result=3;break;
                    }
                    break;
                case 3:
                    if(selectedaction.equals("Power")){
                        result=0;
                    }
                    break;
                case 4:
                    switch (selectedaction){
                        case "Source": result=1;break;
                        case "SD Song":result=6;break;
                        case "Radio Channel": result=3;break;
                        case "Play control":result=4;break;
                    }
                    break;
                case 5:
                    switch (selectedaction){
                        case "Other Type1": result=0;break;
                        case "Other Type2":result=1;break;
                    }
                    break;
                case 6:
                    if(selectedaction.equals("Fan Power")){
                        result=0;
                    }
                    break;
                case 7:
                    if(selectedaction.equals("Media Control")){
                        result=0;
                    }
                    break;
            }
        }
        return result;
    }

    public int getvalue2(int controltype,int value1){
        int result=0;
        switch (controltype){
            case 1:
                switch (value1){
                    case 0:
                        switch (selecedtvalue){
                            case "ON":result=1;break;
                            case "OFF": result=0;break;
                                default:break;
                        }
                        break;
                    case 1:
                        result=Integer.parseInt(selecedtvalue2.split("%")[0]);
                        break;
                    case 2:
                        result=ToColor(selecedtvalue3);
                        default:break;
                }
                break;
            case 2:
                switch (value1){
                    case 0:
                        switch (selecedtvalue){
                            case "ON":result=1;break;
                            case "OFF": result=0;break;
                            default:break;
                        }
                        break;
                    case 1:
                        result=Integer.parseInt(selecedtvalue4.split("%")[0]);
                        break;
                    case 2:
                        switch (selecedtvalue){
                            case "Auto":result=0;break;
                            case "High":result=1;break;
                            case "Medium":result=2;break;
                            case "Low":result=3;break;
                            default:break;
                        }
                        break;
                    case 3:
                        switch (selecedtvalue){
                            case "Cool":result=0;break;
                            case "Heat":result=1;break;
                            case "Fan":result=2;break;
                            case "Auto":result=3;break;
                            default:break;
                        }
                        break;
                }
                break;
            case 3:
                switch (selecedtvalue){
                    case "ON":result=1;break;
                    case "OFF": result=0;break;
                    default:break;
                }
                break;
            case 4:
                switch (value1){
                    case 1:
                        switch (selecedtvalue){
                            case "Music":result=1;break;
                            case "Radio":result=4;break;
                            case "Audio-In":result=2;break;
                        }
                        break;
                    case 3:
                        result=6;
                        break;
                    case 4:
                        switch (selecedtvalue){
                            case "Play":result=3;break;
                            case "Pause":result=4;break;
                            case "Back":result=1;break;
                            case "Next":result=2;break;
                        }
                        break;
                    case 5:
                        result=Integer.parseInt(selecedtvalue2.split("%")[0]);
                        break;
                    case 6:
                        List<Savesong> song=new ArrayList<>();
                        List<Savesong> thissonglist=new ArrayList<>();
                        Savesong thissong=new Savesong();
                        song=MainActivity.mgr.querysong();

                        for(int t=0;t<song.size();t++){
                            if(song.get(t).room_id==roomid){
                                thissonglist.add(song.get(t));
                            }
                        }

                        for(int i=0;i<thissonglist.size();i++){
                            if(thissonglist.get(i).song_name.equals(selecedtvalue)){
                                thissong=thissonglist.get(i);
                                break;
                            }
                        }
                        result=thissong.album_num;
                        break;
                    default:break;
                }
                break;
            case 5:
                switch (selecedtvalue){
                    case "ON":result=1;break;
                    case "OFF": result=0;break;
                    default:break;
                }
                break;
            case 6:
                switch (selecedtvalue){
                    case "Fan off":result=0;break;
                    case "Fan low Speed":result=1;break;
                    case "Fan Middle Speed":result=2;break;
                    case "Fan High Speed":result=3;break;
                    case "Fan Full Speed":result=4;break;
                }
                break;
            case 7:
                for(int i=0;i<radiobuttonlist.length;i++){
                    if(selecedtvalue.equals(radiobuttonlist[i])){
                        result=i+1;
                        break;
                    }
                }
                break;
            default:break;
        }
        return result;
    }

    public int getvalue3(int value1,int control_type){
        int result=0;
        switch (value1){
            case 1:
                if(control_type==2){
                    switch (temp_choose_mode){
                        case "Cool Mode":result=0;break;
                        case "Heat Mode":result=1;break;
                        case "Fan Mode":result=2;break;
                        case "Auto Mode":result=3;break;
                    }
                }
                break;
            case 3:
                if(control_type==4){
                    result=Integer.parseInt(selecedtvalue.split("-")[1]);
                }
               break;
            case 6:
                if(control_type==4){
                    List<Savesong> song=new ArrayList<>();
                    List<Savesong> thissonglist=new ArrayList<>();
                    Savesong thissong=new Savesong();
                    song=MainActivity.mgr.querysong();

                    for(int t=0;t<song.size();t++){
                        if(song.get(t).room_id==roomid){
                            thissonglist.add(song.get(t));
                        }
                    }

                    for(int i=0;i<thissonglist.size();i++){
                        if(thissonglist.get(i).song_name.equals(selecedtvalue)){
                            thissong=thissonglist.get(i);
                            break;
                        }
                    }
                    result=thissong.song_num;
                }

                break;
        }
        return result;
    }
    List<String> lightcommand1=new ArrayList<String>(){{
        add("Power");
    }};
    List<String> lightcommand2=new ArrayList<String>(){{
        add("Dimmer");
    }};
    List<String> lightcommand3=new ArrayList<String>(){{
        add("LED");
    }};
    List<String> accommand=new ArrayList<String>(){{
        add("Power");add("Temperature");add("Fan Speed");
    }};
    List<String> musiccommand=new ArrayList<String>(){{
        add("Source");add("SD Song");add("Volume");add("Radio Channel");add("Play control");
    }};
    List<String> curtaincommand=new ArrayList<String>(){
        {
            add("Power");
        }
    };
    List<String> othercommand1=new ArrayList<String>(){
        {
            add("Other Type1");
        }
    };
    List<String> othercommand2=new ArrayList<String>(){
        {
            add("Other Type2");
        }
    };
    List<String> fancommand=new ArrayList<String>(){
        {
            add("Fan Power");
        }
    };
    List<String> mediacommand=new ArrayList<String>(){
        {
            add("Media Control");
        }
    };
    List<String> nothingcommand=new ArrayList<String>(){
        {
            add("");
        }
    };
    String[] power=new String[]{"ON","OFF"};
    String[] fanspeed=new String[]{"Auto","High","Medium","Low"};
    String[] source=new String[]{"Music","Radio","Audio-In"};
    String[] playcontrol=new String[]{"Play","Pause","Back","Next"};
    String[] fancontrol=new String[]{"Fan off","Fan low Speed","Fan Middle Speed","Fan High Speed","Fan Full Speed"};
    List<String> songlist=new ArrayList<>();
    String[] radiolist=new String[]{"Channel-1","Channel-2","Channel-3","Channel-4","Channel-5",
            "Channel-6","Channel-7","Channel-8","Channel-9","Channel-10","Channel-11","Channel-12",
            "Channel-13","Channel-14","Channel-15","Channel-16","Channel-17","Channel-18","Channel-19"
            ,"Channel-20","Channel-21","Channel-22","Channel-23","Channel-24","Channel-25"};
    String[] radiobuttonlist=new String[]{"ON","OFF","VOLUME -","VOLUME +","VOLUME MUTE",
            "UP","DOWN","LEFT","RIGHT","OK","VIEW1","VIEW2",
            "VIEW3","VIEW4","BACK","HOME","SETTING","NUM 1","NUM 2"
            ,"NUM 3","NUM 4","NUM 5","NUM 6","NUM 7","NUM 8","NUM 9","NUM *","NUM 0","NUM #"};
}
