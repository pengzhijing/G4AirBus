package com.shima.smartbushome.centercontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MarcoAdapter;
import com.shima.smartbushome.assist.marcoCompare;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savemarcobutton;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.founction_command.fancontrol;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.founction_command.mediacontrol;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_command.othercontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarcoActivity extends AppCompatActivity {

    ScrollView nonemarcoview;
    RelativeLayout marcon;
    GridView marcobuttonview;
    Handler getdata=new Handler();
    List<Savemarcobutton> allmarcobutton=new ArrayList<>();
    List<Savemarco> clickmarco=new ArrayList<>();
    MarcoAdapter adapter;
    public static int SAVEMARCO=22;
    boolean deletemode=false;
    AlertView deleteview;

    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;

    //fan speed
    private static final byte const_fan_speed_anto=0;
    private static final byte const_fan_speed_high=1;
    private static final byte const_fan_speed_medium=2;
    private static final byte const_fan_speed_low=3;

    //ac mode
    private static final byte const_mode_cool=0;
    private static final byte const_mode_heat=1;
    private static final byte const_mode_fan=2;
    private static final byte const_mode_auto=3;


    public  DBManager mgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marco);
        Toolbar toolbar = (Toolbar) findViewById(R.id.marco_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Macro");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mgr = new DBManager(this);

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
        /*if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        initView();
        getdata.postDelayed(run,10);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregeisterReceiver();
        //MainActivity.mydupsocket.StopAllThread();
    }
    public void initView(){
        nonemarcoview=(ScrollView)findViewById(R.id.marcoinsscroll);
        marcon=(RelativeLayout)findViewById(R.id.marcon);
        marcobuttonview=(GridView)findViewById(R.id.gridView3);
    }

    public void addmarco(View v){
        Intent add=new Intent(this,MarcoAddActivity.class);
        int passmarcoid=0;
        if(allmarcobutton.size()>0){
            passmarcoid=allmarcobutton.get(allmarcobutton.size()-1).marco_id+1;
        }else{
            passmarcoid=0;
        }
        add.putExtra("marcoID",passmarcoid);
        add.putExtra("editmarco",0);
        startActivityForResult(add, SAVEMARCO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // String change01 = data.getStringExtra("change01");
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case 22:
                getdata.postDelayed(run, 30);
                break;
            default:
                break;
        }
    }
    Runnable run=new Runnable() {
        @Override
        public void run() {
            if(allmarcobutton.size()>0){allmarcobutton.clear();}
            allmarcobutton= mgr.querymarcobutton();
            if(allmarcobutton.size()>0){
                marcon.setVisibility(View.VISIBLE);
                nonemarcoview.setVisibility(View.GONE);
                adapter=new MarcoAdapter(MarcoActivity.this, allmarcobutton);
                marcobuttonview.setAdapter(adapter);
                marcobuttonview.setOnItemClickListener(marcoitemclick);
                marcobuttonview.setOnItemLongClickListener(marcoitemlongclick);
            }else{
                nonemarcoview.setVisibility(View.VISIBLE);
                marcon.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.marco_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.marco_add:
                Intent add=new Intent(this,MarcoAddActivity.class);
                int passmarcoid=0;
                if(allmarcobutton.size()>0){
                    passmarcoid=allmarcobutton.get(allmarcobutton.size()-1).marco_id+1;
                }else{
                    passmarcoid=0;
                }
                add.putExtra("marcoID",passmarcoid);
                add.putExtra("editmarco",0);
                startActivityForResult(add, SAVEMARCO);
                break;
            case R.id.marco_delete:
                deletemode=true;
                Toast.makeText(MarcoActivity.this, "Please click one button to delete", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public AdapterView.OnItemClickListener marcoitemclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(deletemode){
                String str=adapter.getselected(position).marco_remark;
                final int itemselect=position;
                deleteview = new AlertView("Warning", "Are you sure to delete "+str+" ?", "CANCEL",
                        new String[]{"YES"}, null, MarcoActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener(){
                    public void onItemClick(Object o,int position) {
                        if(position==-1){
                            deletemode=false;
                        }else if(position==0){
                            int marcoidx=adapter.getselected(itemselect).marco_id;
                            mgr.deletemarcobutton("marco",marcoidx);
                            mgr.deletemarcobutton("marcobutton", marcoidx);
                            deletemode=false;
                            getdata.postDelayed(run, 30);
                        }
                    }
                });
                deleteview .setCancelable(false);
                deleteview .show();
            }else{
                if(clickmarco.size()>0){clickmarco.clear();}
                int macid=adapter.getselected(position).marco_id;
                List<Savemarco> allmarco=mgr.querymarco();
                clickmarco=listorder(macid,allmarco);
                senthandler.postDelayed(sent,20);

            }

        }
    };

    public AdapterView.OnItemLongClickListener marcoitemlongclick=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Intent add=new Intent(MarcoActivity.this,MarcoAddActivity.class);
            int passmarcoid=0;
            passmarcoid=allmarcobutton.get(position).marco_id;
            add.putExtra("marcoID",passmarcoid);
            add.putExtra("editmarco",1);
            startActivityForResult(add, SAVEMARCO);
            return false;
        }
    };
    Handler senthandler=new Handler();
    int sentcount=0;
    boolean senting=false;
    Runnable sent=new Runnable() {
        @Override
        public void run() {
            if(senting){
                sentcount++;
                senting=false;
                if(sentcount>=clickmarco.size()){
                    senthandler.removeCallbacks(sent);
                    sentcount=0;
                    Toast.makeText(MarcoActivity.this, "finished", Toast.LENGTH_SHORT).show();
                }else{
                    senthandler.postDelayed(sent,75);
                }
            }else{
                if(clickmarco.size()>0){
                    Savemarco sentmarco=clickmarco.get(sentcount);
                    sentmarco(sentmarco);
                }
                senthandler.postDelayed(sent,250);//450
                senting=true;
            }
        }
    };

    Savemarco senttemp=new Savemarco();

    public class senttemp implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                switch (senttemp.value3){
                    case 0:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_cold_tmp, senttemp.value2,MainActivity.mydupsocket);
                        break;
                    case 1:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_heat_tmp, senttemp.value2,MainActivity.mydupsocket);
                        break;
                    case 3:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_auto_tmp,senttemp.value2,MainActivity.mydupsocket);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    lightcontrol lc=new lightcontrol();
    ACcontrol ac=new ACcontrol();
    curtaincontrol cc=new curtaincontrol();
    musiccontrol mc=new musiccontrol();
    othercontrol oc=new othercontrol();
    fancontrol fc=new fancontrol();
    mediacontrol mec=new mediacontrol();
    public void sentmarco(Savemarco sentmarco){
        switch (sentmarco.control_type){
            case 1:
                Savelight thislight=new Savelight();
                List<Savelight> alllight=mgr.querylight();
                for(int i=0;i<alllight.size();i++){
                    if(alllight.get(i).room_id==sentmarco.room_id&&alllight.get(i).light_statement.equals(sentmarco.device)){
                        thislight=alllight.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        switch (sentmarco.value2){
                            case 0:lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,0,MainActivity.mydupsocket);break;
                            case 1:lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,100,MainActivity.mydupsocket);break;
                        }
                        break;
                    case 1:
                        lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                    case 2:
                        lc.ARGBlightcontrol((byte)thislight.subnetID,(byte)thislight.deviceID,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                }
                break;
            case 2:
                Savehvac thisac=new Savehvac();
                List<Savehvac> allac=mgr.queryhvac();
                for(int i=0;i<allac.size();i++){
                    if(allac.get(i).room_id==sentmarco.room_id&&allac.get(i).hvac_remark.equals(sentmarco.device)){
                        thisac=allac.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_onoff,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                    case 1:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_set_mode,sentmarco.value3,MainActivity.mydupsocket);
                        senttemp=sentmarco;
                        senttemp.subnetID=thisac.subnetID;
                        senttemp.deviceID=thisac.deviceID;
                        new Thread(new senttemp()).start();
                        break;
                    case 2:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_set_fan,sentmarco.value2,MainActivity.mydupsocket);
                        break;

                }
                break;
            case 3:
                Savecurtain thisct=new Savecurtain();
                List<Savecurtain> allct=mgr.querycurtain();
                for(int i=0;i<allct.size();i++){
                    if(allct.get(i).room_id==sentmarco.room_id&&allct.get(i).curtain_remark.equals(sentmarco.device)){
                        thisct=allct.get(i);
                        break;
                    }
                }
                switch (sentmarco.value2){
                    case 0:cc.CurtainControl((byte)thisct.subnetID,(byte)thisct.deviceID,thisct.channel_1,thisct.channel_2,"close",MainActivity.mydupsocket);break;
                    case 1:cc.CurtainControl((byte)thisct.subnetID,(byte)thisct.deviceID,thisct.channel_1,thisct.channel_2,"open",MainActivity.mydupsocket);break;
                }
                break;
            case 4:
                switch (sentmarco.value1){
                    case 1:
                        mc.MusicControl((byte)1,(byte)sentmarco.value2,(byte)0,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 3:
                        mc.MusicControl((byte)3,(byte)6,(byte)sentmarco.value3,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 4:
                        mc.MusicControl((byte)4,(byte)sentmarco.value2,(byte)0,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 5:
                        mc.MusicControl((byte)5,(byte)1,(byte)3,(byte)(79-((79 * sentmarco.value2) / 100)),(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 6:
                        byte[] songbyte = new byte[2];
                        songbyte[0] = (byte) ((sentmarco.value3 &0xff00)>>8);
                        songbyte[1] = (byte) ((sentmarco.value3 ) - (sentmarco.value3&0xff00));
                        mc.MusicControl((byte) 6, (byte) (sentmarco.value2), songbyte[0], songbyte[1], (byte) sentmarco.subnetID, (byte) sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                }
                break;
            case 5:
                Saveother thisot=new Saveother();
                List<Saveother> allot=mgr.queryother();
                for(int i=0;i<allot.size();i++){
                    if(allot.get(i).room_id==sentmarco.room_id&&allot.get(i).other_statement.equals(sentmarco.device)){
                        thisot=allot.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        switch (sentmarco.value2){
                            case 0:oc.SingleChannelControl((byte)thisot.subnetID,(byte)thisot.deviceID,thisot.channel_1,0,MainActivity.mydupsocket);break;
                            case 1:oc.SingleChannelControl((byte)thisot.subnetID,(byte)thisot.deviceID,thisot.channel_1,100,MainActivity.mydupsocket);break;
                        }
                        break;
                    case 1:
                        switch (sentmarco.value2){
                            case 0:oc.CurtainControl((byte) thisot.subnetID, (byte) thisot.deviceID, thisot.channel_1,
                                    thisot.channel_2,"close",MainActivity.mydupsocket);break;
                            case 1:oc.CurtainControl((byte) thisot.subnetID, (byte) thisot.deviceID, thisot.channel_1,
                                    thisot.channel_2,"open",MainActivity.mydupsocket);break;
                        }
                        break;
                }
                break;
            case 6:
                Savefan thisft=new Savefan();
                List<Savefan> allft=mgr.queryfan();
                for(int i=0;i<allft.size();i++){
                    if(allft.get(i).room_id==sentmarco.room_id&&allft.get(i).fan_statement.equals(sentmarco.device)){
                        thisft=allft.get(i);
                        break;
                    }
                }
                fc.FanChannelControl((byte)thisft.subnetID,(byte)thisft.deviceID,thisft.channel,sentmarco.value2,MainActivity.mydupsocket);
                break;
            case 7:
                Savemedia thismedia=new Savemedia();
                Savemediabutton thismediabutton=new Savemediabutton();
                List<Savemedia> allmedia=mgr.querymedia();
                List<Savemediabutton> allmediabutton=mgr.querymediabutton();
                for(int i=0;i<allmedia.size();i++){
                    if(allmedia.get(i).room_id==sentmarco.room_id&&allmedia.get(i).media_statement.equals(sentmarco.device)){
                        thismedia=allmedia.get(i);
                        break;
                    }
                }
                for(int i=0;i<allmediabutton.size();i++){
                    if(allmediabutton.get(i).room_id==sentmarco.room_id&&
                            allmediabutton.get(i).media_id==thismedia.media_id&& allmediabutton.get(i).button_num==sentmarco.value2){
                        thismediabutton=allmediabutton.get(i);
                        break;
                    }
                }
                if(thismediabutton.ifIRmarco==1){
                    mec.IRMarcoControl((byte) thismedia.subnetID, (byte) thismedia.deviceID, thismediabutton.media_swno, thismediabutton.media_controltype,MainActivity.mydupsocket);
                }else{
                    mec.IRControl((byte)thismedia.subnetID,(byte)thismedia.deviceID,thismediabutton.media_swno,thismediabutton.media_controltype,MainActivity.mydupsocket);
                }
                break;
        }
    }

    public List<Savemarco> listorder(int marcoid,List<Savemarco> allmarco){
        List<Savemarco> thisfoun=new ArrayList<>();
        List<Savemarco> result=new ArrayList<>();
        for(int i=0;i<allmarco.size();i++){
            if(marcoid==allmarco.get(i).marco_id){
                thisfoun.add(allmarco.get(i));
            }
        }
        Collections.sort(thisfoun,new marcoCompare());
        return thisfoun;
    }

}
