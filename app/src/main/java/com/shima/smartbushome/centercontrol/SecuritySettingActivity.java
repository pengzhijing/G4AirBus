package com.shima.smartbushome.centercontrol;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.SecurityLogAdapter;
import com.shima.smartbushome.assist.WheelView;
import com.shima.smartbushome.database.Savesecurity;
import com.shima.smartbushome.database.SavesecurityArea;
import com.shima.smartbushome.database.seclogdata;
import com.shima.smartbushome.founction_command.securitycontrol;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SystemUIUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SecuritySettingActivity extends AppCompatActivity {

    AlertView setIDdialog,changepasswordDialog,logDialog,areanameDialog,settingDialog;
    Savesecurity thissecurity=new Savesecurity();
    List<SavesecurityArea> arealist=new ArrayList<>();
    List<seclogdata> logdatalist=new ArrayList<>();
    securitycontrol secc=new securitycontrol();
    int logsumcount=0;
    byte[] logvalue=new byte[2];
    SecurityLogAdapter madapter;
    ProgressDialog readinglog;
    List<SavesecurityArea> securityarealist=new ArrayList<>();
    List<String> areanamelist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.securitysetting_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Setting");
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

        readinglog = new ProgressDialog(this);
        readinglog.setCancelable(true);
        readinglog.setCanceledOnTouchOutside(false);
        readinglog.setMessage("Getting Log...");
        readinglog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        thissecurity= MainActivity.mgr.querysecurity().get(0);
        arealist=MainActivity.mgr.querysecurityarea();
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
        getloghandler.removeCallbacks(getlogrun);
        //getareahandler.removeCallbacks(getarearun);
    }
    EditText sub,dev,oldp,newp1,newp2,newAreaname;
    public void setID(View v){
        setIDdialog=new AlertView("Setting", null, "CANCEL",  new String[]{"SAVE"}, null, this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.setting_musicinfo, null);
        sub = (EditText) selfview.findViewById(R.id.music_subedit);
        dev = (EditText) selfview.findViewById(R.id.music_devedit);
        sub.setText(String.valueOf(thissecurity.subnetID));
        dev.setText(String.valueOf(thissecurity.deviceID));
        setIDdialog.addExtView(selfview);
        setIDdialog.setCancelable(false);
        setIDdialog.show();
    }

    public void changepassword(View v){
        changepasswordDialog=new AlertView("Change Password", null, "CANCEL",  new String[]{"SAVE"}, null, this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.setting_changepassword, null);
        oldp = (EditText) selfview.findViewById(R.id.setting_cpw_oldp);
        newp1 = (EditText) selfview.findViewById(R.id.setting_cpw_newp1);
        newp2 = (EditText) selfview.findViewById(R.id.setting_cpw_newp2);
        changepasswordDialog.addExtView(selfview);
        changepasswordDialog.setCancelable(false);
        changepasswordDialog.setShoulddismiss(false);
        changepasswordDialog.show();
    }

    Button setting_seclog_startdate,setting_seclog_enddate,setting_seclog_reflash,setting_seclog_clean,
            setting_seclog_exit;
    TextView tv_startdate,tv_enddate;
    ListView loglist;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;

    public void history(View v){
        logDialog=new AlertView(null, null, null,  new String[]{"Clear History"}, null, this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.setting_securitylog, null);
        calendar = Calendar.getInstance();
// 获取当前对应的年、月、日的信息
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        setting_seclog_startdate=(Button)selfview.findViewById(R.id.setting_seclog_startdate);
        setting_seclog_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(SecuritySettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_startdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        // setTitle(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day).show();
            }
        });
        setting_seclog_enddate=(Button)selfview.findViewById(R.id.setting_seclog_enddate);
        setting_seclog_enddate .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SecuritySettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_enddate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        // setTitle(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day).show();
            }
        });
        setting_seclog_reflash = (Button) selfview.findViewById(R.id.setting_seclog_reflash);
        setting_seclog_reflash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logdatalist.size() > 0) {
                    logdatalist.clear();
                }
                readinglog.setProgress(0);
                readinglog.show();
                secc.ReadLogSum((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, tv_startdate.getText().toString().trim(),
                        tv_enddate.getText().toString().trim(),MainActivity.mydupsocket);
            }
        });
        setting_seclog_clean = (Button) selfview.findViewById(R.id.setting_seclog_clearlog);
        setting_seclog_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secc.ClearLog((byte) thissecurity.subnetID, (byte) thissecurity.deviceID,MainActivity.mydupsocket);
            }
        });
        setting_seclog_exit= (Button) selfview.findViewById(R.id.setting_seclog_exit);
        setting_seclog_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDialog.dismiss();
            }
        });
        tv_startdate = (TextView) selfview.findViewById(R.id.setting_seclog_tvstartdate);
        tv_enddate = (TextView) selfview.findViewById(R.id.setting_seclog_tvenddate);
        loglist = (ListView) selfview.findViewById(R.id.setting_seclog_content);
        tv_startdate.setText(year + "-" + (month+1) + "-" + day);
        tv_enddate.setText(year + "-" + (month+1) + "-" + day);
        if(logdatalist.size()>0){
            madapter=new SecurityLogAdapter(SecuritySettingActivity.this,logdatalist);
            loglist.setAdapter(madapter);
        }
        logDialog.addExtView(selfview);
        //logDialog.setCancelable(false);
        logDialog.show();
    }


    int selectArea=1;
    Switch bypass,watchdog;
    byte[] indata=new byte[10];
    public void setareaname(View v){
        if(areanamelist.size()>0){areanamelist.clear();}
        if(securityarealist.size()>0){securityarealist.clear();}
        areanameDialog=new AlertView("Set Area Name", null, "CANCEL",  new String[]{"SAVE"}, null, this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.alertview_setareaname, null);
        bypass=(Switch)selfview.findViewById(R.id.setareaname_bypass);
        watchdog=(Switch)selfview.findViewById(R.id.setareaname_watchdog);
        WheelView wva=(WheelView)selfview.findViewById(R.id.setareaname_arealist);
        wva.setOffset(1);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                //allcurtainselectroom.setText("Select Room: " + item);
                selectArea = selectedIndex;
                secc.ReadBypass((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea,MainActivity.mydupsocket);
                new Thread(new Udpsent()).start();
            }
        });
        securityarealist=MainActivity.mgr.querysecurityarea();
        for(int i=0;i<securityarealist.size();i++){
            areanamelist.add("Area"+(i+1)+": "+securityarealist.get(i).areaName);
        }
        wva.setItems(areanamelist);
        newAreaname = (EditText) selfview.findViewById(R.id.setareaname_edname);


        areanameDialog.addExtView(selfview);
        areanameDialog.show();
    }


    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==setIDdialog){
                switch (position){
                    case -1:break;
                    case 0:
                        Savesecurity savedata=new Savesecurity();
                        savedata.subnetID=Integer.parseInt(sub.getText().toString().trim());
                        savedata.deviceID=Integer.parseInt(dev.getText().toString().trim());
                        savedata.password=thissecurity.password;
                        MainActivity.mgr.updatesecurity(savedata);
                        thissecurity.subnetID=savedata.subnetID;
                        thissecurity.deviceID=savedata.deviceID;
                        break;
                }
            }else if(o==changepasswordDialog){
                switch (position){
                    case -1:break;
                    case 0:
                        String old=oldp.getText().toString().trim();
                        String new1=newp1.getText().toString().trim();
                        String new2=newp2.getText().toString().trim();

                        if(old.length()<=0||new1.length()<=0||new2.length()<=0){
                            Toast.makeText(SecuritySettingActivity.this, "should not have blank value", Toast.LENGTH_SHORT).show();
                        }else if(new1.equals(new2)){
                            if(Integer.parseInt(old)==thissecurity.password){
                                if(new1.length()!=6){
                                    Toast.makeText(SecuritySettingActivity.this, "new password should be 6 num", Toast.LENGTH_SHORT).show();
                                }else{
                                    Savesecurity savedata=new Savesecurity();
                                    savedata.subnetID=thissecurity.subnetID;
                                    savedata.deviceID=thissecurity.deviceID;
                                    savedata.password=Integer.parseInt(new1);
                                    MainActivity.mgr.updatesecurity(savedata);
                                    thissecurity.password=savedata.password;
                                    Toast.makeText(SecuritySettingActivity.this, "save", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(SecuritySettingActivity.this, "password fail", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SecuritySettingActivity.this, "2 new password is diferent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }else if(o==areanameDialog){
                switch (position){
                    case -1:break;
                    case 0:
                        if(newAreaname.getText().toString().trim().length()<=0){

                        }else{
                            secc.WriteAreaName((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea, newAreaname.getText().toString().trim(),MainActivity.mydupsocket);
                            SavesecurityArea newarea=new SavesecurityArea();
                            newarea.security_id=0;
                            newarea.areaNO=selectArea;
                            newarea.areaName=newAreaname.getText().toString().trim();
                            MainActivity.mgr.updatesecurityarea(newarea);
                        }
                        new Thread(new bypasssave()).start();

                        break;
                }
            }
        }
    };
    Handler getloghandler=new Handler();
    int sentcount=1;
    Runnable getlogrun=new Runnable() {
        @Override
        public void run() {
            if(sentcount>logsumcount){
                madapter=new SecurityLogAdapter(SecuritySettingActivity.this,logdatalist);
                loglist.setAdapter(madapter);
                sentcount=1;
                logsumcount=0;
                readinglog.dismiss();
                readinglog.setProgress(0);
                getloghandler.removeCallbacks(getlogrun);
            }else{
                secc.ReadLogvalue((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, tv_startdate.getText().toString().trim()
                        , tv_enddate.getText().toString().trim(), logvalue, sentcount,MainActivity.mydupsocket);
                getloghandler.postDelayed(getlogrun,500);
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
            if (((data[17]&0xff) == thissecurity.subnetID) && ((data[18]&0xff) == thissecurity.deviceID)){
                switch(x){
                    case 0x0139:
                        logsumcount=(data[25]<<8)+(data[26]&0xff);
                        logvalue[0]=data[27];
                        logvalue[1]=data[28];
                        if(logsumcount>0){
                            getloghandler.postDelayed(getlogrun,50);
                        }else{
                            Toast.makeText(SecuritySettingActivity.this, "Nothing in the log", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x013b:
                        int lognum=(data[26]<<8)+(data[27]&0xff);
                        if(lognum==sentcount){
                            seclogdata thisdata=new seclogdata();
                            thisdata.lognum=sentcount;
                            thisdata.Date=String.valueOf((data[29]&0xff))+"-"+String.valueOf((data[30]&0xff))+"-"+String.valueOf((data[31]&0xff));
                            thisdata.Time=String.valueOf((data[32]&0xff))+":"+String.valueOf((data[33]&0xff))+":"+String.valueOf((data[34]&0xff));
                            thisdata.Address=String.valueOf((data[35]&0xff))+"-"+String.valueOf((data[36]&0xff));
                            thisdata.Channel=String.valueOf((data[38]&0xff));
                            switch ((data[37&0xff])){
                                case 1:thisdata.Type="vacation";break;
                                case 2:thisdata.Type="Away";break;
                                case 3:thisdata.Type="Night";break;
                                case 4:thisdata.Type="Night with visitor";break;
                                case 5:thisdata.Type="Day";break;
                                case 6:thisdata.Type="Disarm";break;
                            }
                            logdatalist.add(thisdata);
                            readinglog.setProgress((100*sentcount)/logsumcount);
                            sentcount++;
                        }
                        break;
                    case 0x012f:
                        if((data[26]&0xff)==selectArea){
                            switch (data[28]&0xff){
                                case 0:
                                    bypass.setChecked(true);
                                    break;
                                case 1:
                                    bypass.setChecked(false);
                                    break;
                            }
                        }
                        break;
                    case 0x0133:
                        if((data[26]&0xff)==selectArea){
                            switch (data[28]&0xff){
                                case 0:
                                    watchdog.setChecked(true);
                                    break;
                                case 1:
                                    watchdog.setChecked(false);
                                    break;
                            }
                        }
                        indata[0]=data[27];
                        for(int i=0;i<9;i++){
                            indata[1+i]=data[29+i];
                        }
                        break;
                }
            }
            doing=false;
        }
    }
    /* byte[] songbyte = new byte[2];
            songbyte[0] = (byte) ((selectedsong.song_num &0xff00)>>8);
            songbyte[1] = (byte) ((selectedsong.song_num ) - (selectedsong.song_num&0xff00));
            int songnum=(data[31+olddata]<<8)+(data[32+olddata]&0xff);*/
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        return intentFilter;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.security_menu, menu);
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

    public class Udpsent implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                secc.ReadWatchDog((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea,MainActivity.mydupsocket);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public class bypasssave implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                if(bypass.isChecked()){
                    secc.setBypass((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea, 0,MainActivity.mydupsocket);
                }else{
                    secc.setBypass((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea, 1,MainActivity.mydupsocket);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(watchdog.isChecked()){
                secc.WriteWatchDog((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea,0,indata,MainActivity.mydupsocket);
            }else{
                secc.WriteWatchDog((byte) thissecurity.subnetID, (byte) thissecurity.deviceID, selectArea,1,indata,MainActivity.mydupsocket);
            }
        }
    }

}
