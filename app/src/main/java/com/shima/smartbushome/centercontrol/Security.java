package com.shima.smartbushome.centercontrol;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.WheelView;
import com.shima.smartbushome.database.Savesecurity;
import com.shima.smartbushome.database.SavesecurityArea;
import com.shima.smartbushome.founction_command.securitycontrol;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

public class Security extends AppCompatActivity {

    List<SavesecurityArea> securityarealist=new ArrayList<>();
    Savesecurity thisecurity=new Savesecurity();
    WheelView wva;
    int selectArea=1;
    Handler getareahandler=new Handler();
    securitycontrol secc=new securitycontrol();
    List<String> areanamelist=new ArrayList<>();
    EditText sub,dev,password;
    AlertView setIDdialog;
    boolean updateareaname=false;
    ProgressDialog renewdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secerity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.security_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Security");
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
        password=(EditText)findViewById(R.id.security_password);
        /*if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        renewdialog = new ProgressDialog(this);
        renewdialog.setCancelable(true);
        renewdialog.setCanceledOnTouchOutside(false);
        renewdialog.setMessage("Getting Area Name...");
        renewdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        wva=(WheelView)findViewById(R.id.security_area);
        wva.setOffset(1);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                //allcurtainselectroom.setText("Select Room: " + item);
                selectArea=selectedIndex;
            }
        });
        List<Savesecurity> securitylist= MainActivity.mgr.querysecurity();
        if(securitylist.size()<=0){
            //MainActivity.mgr.addsecurity(new Savesecurity(0,0,0,0,888888));
            //thisecurity=MainActivity.mgr.querysecurity().get(0);
            setIDdialog=new AlertView("Setting", null, "CANCEL",  new String[]{"SAVE"}, null, this, AlertView.Style.Alert,
                    itemclick);
            View selfview= getLayoutInflater().inflate(R.layout.setting_musicinfo, null);
            sub = (EditText) selfview.findViewById(R.id.music_subedit);
            dev = (EditText) selfview.findViewById(R.id.music_devedit);
            sub.setText(String.valueOf(thisecurity.subnetID));
            dev.setText(String.valueOf(thisecurity.deviceID));
            setIDdialog.addExtView(selfview);
            setIDdialog.setCancelable(false);
            setIDdialog.show();
        }else{
            thisecurity=MainActivity.mgr.querysecurity().get(0);
            securityarealist=MainActivity.mgr.querysecurityarea();
            if(securityarealist.size()!=8){
                MainActivity.mgr.deletesecurityarea("securityarea");
                renewdialog.show();
                getareahandler.postDelayed(getarearun, 20);
            }else {
                for(int i=0;i<securityarealist.size();i++){
                    areanamelist.add("Area"+(i+1)+": "+securityarealist.get(i).areaName);
                }
                wva.setItems(areanamelist);
            }
        }
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
    public void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }
    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //MainActivity.mydupsocket.StopAllThread();
        getareahandler.removeCallbacks(getarearun);
    }
    int alarmid=0;
    public void away(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=2;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,2,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    public void night(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=3;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,3,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    public void nightwithguest(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=4;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,4,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    public void day(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=5;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,5,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    public void vacation(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=1;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,1,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    public void disarm(View v){
        String passstr=password.getText().toString().trim();
        try{
            if(Integer.parseInt(passstr)==thisecurity.password){
                alarmid=6;
                secc.setAlarm((byte)thisecurity.subnetID,(byte)thisecurity.deviceID,selectArea,6,MainActivity.mydupsocket);
            }else{
                Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
        }
    }
    int areanum=1;
    int count=0;
    Runnable getarearun=new Runnable() {
        @Override
        public void run() {
            if(areanum>8){
                renewdialog.dismiss();
                renewdialog.setProgress(0);
                areanum=1;
                getareahandler.removeCallbacks(getarearun);
                securityarealist=MainActivity.mgr.querysecurityarea();
                if(areanamelist.size()>0){areanamelist.clear();}
                for(int i=0;i<securityarealist.size();i++){
                    areanamelist.add("Area"+(i+1)+": "+securityarealist.get(i).areaName);
                }
                wva.setItems(areanamelist);
                updateareaname=false;
            }else{
                count++;
                secc.ReadAreaName((byte) thisecurity.subnetID, (byte) thisecurity.deviceID, areanum,MainActivity.mydupsocket);
                if(count>=50){
                    count=0;
                    areanum=1;
                    getareahandler.removeCallbacks(getarearun);
                }else{
                    getareahandler.postDelayed(getarearun, 500);
                }
            }

        }
    };
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==setIDdialog){
                switch (position){
                    case -1:finish();break;
                    case 0:
                        Savesecurity savedata=new Savesecurity();
                        savedata.room_id=0;
                        savedata.security_id=0;
                        savedata.subnetID=Integer.parseInt(sub.getText().toString().trim());
                        savedata.deviceID=Integer.parseInt(dev.getText().toString().trim());
                        savedata.password=888888;
                        MainActivity.mgr.addsecurity(savedata);
                        thisecurity=MainActivity.mgr.querysecurity().get(0);
                        securityarealist=MainActivity.mgr.querysecurityarea();
                        if(securityarealist.size()<=0){
                            renewdialog.show();
                            getareahandler.postDelayed(getarearun,20);
                        }
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));

                if(rev.length>25){
                    RunReceiveData(rev);
                }
                rev=null;
            }
        }
    };
    boolean doing=false;
    public void RunReceiveData(byte[] data){
        int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
        if(doing){

        }else {
            doing = true;
            if (((data[17]&0xff) == thisecurity.subnetID) && ((data[18]&0xff) == thisecurity.deviceID)){
                switch(x){
                    case 0x0249:
                        if(data[25]==(byte)areanum){
                            if(updateareaname){
                                byte[] namebyte=new byte[20];
                                for(int i=0;i<20;i++){
                                    if(data[27+i]==(byte)0xff){
                                        namebyte[i]=0x20;
                                    }else{
                                        namebyte[i]=data[27+i];
                                    }
                                }

                                String name=bytetostring(namebyte,"Unicode");
                                name=name.replaceAll("†","");
                                StringBuffer   sb   =   new   StringBuffer(name);
                                name=sb.reverse().toString();
                                SavesecurityArea revarea=new SavesecurityArea();
                                revarea.areaNO=areanum;
                                revarea.security_id=0;
                                revarea.areaName=name;
                                MainActivity.mgr.updatesecurityarea(revarea);
                                renewdialog.setProgress((areanum*100)/8);
                                areanum++;
                            }else{
                                byte[] namebyte=new byte[20];
                                for(int i=0;i<20;i++){
                                    if(data[27+i]==(byte)0xff){
                                        namebyte[i]=0x20;
                                    }else{
                                        namebyte[i]=data[27+i];
                                    }
                                }
                                String name=bytetostring(namebyte,"ascii");
                                name=name.replaceAll(" ","");
                                SavesecurityArea revarea=new SavesecurityArea();
                                revarea.areaNO=areanum;
                                revarea.security_id=0;
                                revarea.areaName=name;
                                MainActivity.mgr.addsecurityarea(revarea);
                                renewdialog.setProgress((areanum*100)/8);
                                areanum++;
                            }
                        }
                        break;
                    case 0x0105:
                        if((data[26]&0xff)==alarmid){
                            Toast.makeText(Security.this, "suecceed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            doing=false;
        }
    }
    public String bytetostring(byte[] name,String type){
        String s="";
        try {
            s= new String(name,type);// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public String orderDesc(String str){
        if(str==null||str.length()<0){
            return "ERROR";
        }
        byte[] bytes=str.getBytes();
        for(int i=0;i<bytes.length/2;i++){
            Byte b=bytes[i];
            bytes[i]=bytes[bytes.length-1-i];
            bytes[bytes.length-1-i]=b;
        }
        return new String(bytes);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.security_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.security_setting:
                String passstr=password.getText().toString();
                if(passstr==null){
                    Toast.makeText(Security.this, "please put password", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        if(Integer.parseInt(passstr)==thisecurity.password){
                            startActivity(new Intent(this,SecuritySettingActivity.class));
                        }else{
                            Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(Security.this, "wrong password", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.security_reflashname:
              //  wva.removeAllViews();
                renewdialog.show();
                updateareaname=true;
                getareahandler.postDelayed(getarearun,20);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        return intentFilter;
    }
}
