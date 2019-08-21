package com.shima.smartbushome.centercontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.statusTypeAdapter;
import com.shima.smartbushome.database.Savestatus;
import com.shima.smartbushome.founction_command.statuscontrol;
import com.shima.smartbushome.selflayout.StatusLayout;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SystemUIUtil;

import java.util.ArrayList;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

    LinearLayout statuscontent;
    RelativeLayout nodeviceinfo;
    ScrollView scrollView9;
    Handler getstatushandler=new Handler();
    Handler getdatahandler=new Handler();
    List<Savestatus> statuslist=new ArrayList<>();
    List<StatusLayout> viewchildlist=new ArrayList<>();
    boolean intodeletemode=false;
    MenuItem  add,delete;
    statuscontrol sc;
    public final static String ACTION_REFLASH = "com.example.status.REFLASH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.status_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Status");
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
        initUI();
        sc=new statuscontrol();
        getstatushandler.postDelayed(getstatusrun, 20);
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
        //unregeisterReceiver();
        //MainActivity.mydupsocket.StopAllThread();
    }
    public void initUI(){
        statuscontent=(LinearLayout)findViewById(R.id.statuslinear);
        nodeviceinfo=(RelativeLayout)findViewById(R.id.relativeLayout18);
        scrollView9=(ScrollView)findViewById(R.id.scrollView9);
    }

    Runnable getstatusrun=new Runnable() {
        @Override
        public void run() {
            if(statuslist.size()>0){statuslist.clear();}
            if(viewchildlist.size()>0){viewchildlist.clear();}
            statuscontent.removeAllViews();
            statuslist= MainActivity.mgr.querystatus();
            if(statuslist.size()<=0){
                nodeviceinfo.setVisibility(View.VISIBLE);
                statuscontent.setVisibility(View.GONE);
                scrollView9.setVisibility(View.GONE);
            }else{
                nodeviceinfo.setVisibility(View.GONE);
                scrollView9.setVisibility(View.VISIBLE);
                statuscontent.setVisibility(View.VISIBLE);
                for(int i=0;i<statuslist.size();i++){
                    addspecView(statuslist.get(i));
                }
                getstatus();
            }
        }
    };
    private void addspecView(Savestatus lg) {
        StatusLayout sl=new StatusLayout(statuscontent.getContext());
        sl.setcontan(lg);
        sl.setId(lg.status_id);
        statuscontent.addView(sl);
        viewchildlist.add(sl);
    }

    public void getstatus(){
        readingfinish=false;
        readinglist=new int[viewchildlist.size()];
        for(int i=0;i<viewchildlist.size();i++){
            readinglist[i]=0;
        }
        getdatahandler.postDelayed(getdatarun,20);
    }
    public void addstatusbutton(View v){
        addstatus();
    }
    int sentcount=0,oldsentcount=0,samecommandcount=0;
    boolean readingfinish=true;
    int[] readinglist;
    Savestatus sent;
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            if(sentcount>=statuslist.size()){
                sentcount=0;oldsentcount=0;samecommandcount=0;
                getdatahandler.removeCallbacks(getdatarun);
                readingfinish=true;
            }else{
                sent=statuslist.get(sentcount);
                if(sentcount==oldsentcount){
                    samecommandcount++;
                    if(samecommandcount>10){
                        sentcount++;//如果发送超过10次指令没有收到回应则发送下一条
                        samecommandcount=0;
                    }
                }else{
                    samecommandcount=0;
                }

                switch (sent.type){
                    case 1:
                        sc.getlightstate((byte)sent.subnetID,(byte)sent.deviceID,MainActivity.mydupsocket);
                        break;
                    case 2:
                        sc.getlightstate((byte)sent.subnetID,(byte)sent.deviceID,MainActivity.mydupsocket);
                        break;
                    case 3:
                        sc.ReadTemp((byte) sent.subnetID, (byte) sent.deviceID,(byte)sent.unit,MainActivity.mydupsocket);
                        break;
                    case 4:
                        sc.ACReadCFFlag((byte) sent.subnetID, (byte) sent.deviceID,MainActivity.mydupsocket);
                        break;
                    case 5:
                        sc.getlightstate((byte)sent.subnetID,(byte)sent.deviceID,MainActivity.mydupsocket);
                        break;
                    case 6:
                        sc.ReadTemp((byte)sent.subnetID,(byte)sent.deviceID,(byte)sent.unit,MainActivity.mydupsocket);
                        break;
                }

                oldsentcount=sentcount;
                getdatahandler.postDelayed(getdatarun,250);
            }

        }
    };
    public void addstatus(){
        if(intodeletemode){
            add.setTitle("ADD");
            delete.setTitle("DELETE");
            intodeletemode=false;
            for(int i=0;i<viewchildlist.size();i++){
                viewchildlist.get(i).setdeletevisable(false);
            }
            getstatushandler.postDelayed(getstatusrun, 20);
        }else{
            final AlertView mAlertViewExt = new AlertView(null, null, "CANCEL", null, null, this, AlertView.Style.Alert,
                    null);
            //ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.listdialog,null);
            final ListView typelist=new ListView(this);
            typelist.setAdapter(new statusTypeAdapter(this));
            typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int status_id;
                    if (statuslist.size() == 0) {
                        status_id = 1;
                    } else {
                        status_id = statuslist.get(statuslist.size() - 1).status_id + 1;
                    }
                    switch (position) {
                        case 0:
                            Savestatus type1 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 1, 0, "light",0);
                            MainActivity.mgr.addstatus(type1);
                            break;
                        case 1:
                            Savestatus type2 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 2, 0, "light_type3_on",0);
                            MainActivity.mgr.addstatus(type2);
                            break;
                        case 2:
                            Savestatus type3 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 3, 0, "nio",0);
                            MainActivity.mgr.addstatus(type3);
                            break;
                        case 3:
                            Savestatus type4 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 4, 0, "hvacitem_icon",0);
                            MainActivity.mgr.addstatus(type4);
                            break;
                        case 4:
                            Savestatus type5 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 5, 0, "other",0);
                            MainActivity.mgr.addstatus(type5);
                            break;
                        case 5:
                            Savestatus type6 = new Savestatus(status_id,
                                    "device"+status_id, 0, 0, 6, 0, "hvac",0);
                            MainActivity.mgr.addstatus(type6);
                            break;
                        default:
                            break;
                    }
                    getstatushandler.postDelayed(getstatusrun,20);
                    mAlertViewExt.dismiss();
                }
            });
            mAlertViewExt.addExtView(typelist);
            mAlertViewExt.show();
        }
    }
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
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                finish();

            }else if(ACTION_REFLASH.equals(action)){
                getstatushandler.postDelayed(getstatusrun,20);
            }
        }
    };
    boolean doing=false;
    String acunit="",fanstate="",modestate="",actemp="";
    private byte[] fanArray,modeArray;
    boolean readCF=false,readCountfanAndMode=false,readcstate=false;
    public byte CurrentMode=3,CoolTemp=20,HeatTemp=20,AutoTemp=20,CurrentFanMode=2;
    public void RunReceiveData(byte[] data){
        int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);//get op code
        if(doing){

        }else {
            doing = true;
            if(!readingfinish) {//读取状态
                if (sentcount < viewchildlist.size()) {
                    if (readinglist[sentcount] != 1) {
                        if (((data[17] & 0xff) == statuslist.get(sentcount).subnetID) && ((data[18] & 0xff) == statuslist.get(sentcount).deviceID)) {
                            switch (x) {
                                case 0x0034:
                                    switch (statuslist.get(sentcount).type) {
                                        case 1:
                                            String state = data[viewchildlist.get(sentcount).getchannel() + 25] + "%";
                                            viewchildlist.get(sentcount).setstatus(state);
                                            break;
                                        case 2:
                                            byte[] rgbdata={data[26],data[27],data[28],data[29]};
                                            viewchildlist.get(sentcount).setstatuscolor(ToColor(rgbdata));

                                            break;
                                        case 5:
                                            String state_other="";
                                            switch (data[viewchildlist.get(sentcount).getchannel() + 25]&0xff){
                                                case 0:state_other="OFF";break;
                                                case 100:state_other="ON";break;
                                            }
                                            viewchildlist.get(sentcount).setstatus(state_other);
                                            break;
                                    }
                                    readinglist[sentcount] = 1;
                                    sentcount++;
                                    break;
                                case 0xe3e8:
                                    switch (statuslist.get(sentcount).type){
                                        case 3:
                                            String state="",sunit="";
                                            switch (data[25]%0xff){
                                                case 0:sunit=" ℉";break;
                                                case 1:sunit=" ℃";break;
                                            }
                                            if(data.length>35){
                                                switch (data[34]&0xff){
                                                    case 0:state=""+(data[26]&0xff)+sunit;break;
                                                    case 1:state="-"+(data[26]&0xff)+sunit;break;
                                                }
                                            }else{
                                                state=""+(data[26]&0xff)+sunit;
                                            }
                                            viewchildlist.get(sentcount).setstatus(state);
                                            break;
                                        case 6:
                                            String state4t="",sunit4t="";
                                            int chan=viewchildlist.get(sentcount).getchannel();
                                            switch (data[25]%0xff){
                                                case 0:sunit4t=" ℉";break;
                                                case 1:sunit4t=" ℃";break;
                                            }
                                            switch (data[33+chan]&0xff){
                                                case 0:state4t=""+(data[25+chan]&0xff)+sunit4t;break;
                                                case 1:state4t="-"+(data[25+chan]&0xff)+sunit4t;break;
                                            }
                                            viewchildlist.get(sentcount).setstatus(state4t);
                                            break;
                                    }
                                    readinglist[sentcount] = 1;
                                    sentcount++;
                                    break;
                                case 0xe121:
                                    if(!readCF){
                                        switch (data[25]%0xff){
                                            case 1:acunit=" ℉";break;
                                            case 0:acunit=" ℃";break;
                                        }
                                        readCF=true;
                                    }
                                    sc.ACReadCountFanAndMode((byte) sent.subnetID, (byte) sent.deviceID,MainActivity.mydupsocket);
                                    break;
                                case 0xe125:
                                    if(!readCountfanAndMode){
                                        readCountfanAndMode=true;
                                        fanArray=new byte[data[25]];
                                        for(int i=0;i<fanArray.length;i++){
                                            fanArray[i]=data[26+i];
                                        }
                                        int startmode=26+fanArray.length;
                                        for(int i=0;i<10;i++){
                                            if((data[startmode]>(byte)0x20)||(data[startmode]==(byte)0x00)){
                                                startmode++;
                                            }else if(data[startmode]<(byte)0x06){
                                                break;
                                            }
                                        }
                                        modeArray=new byte[data[startmode]];
                                        for(int i=0;i<modeArray.length;i++){
                                            modeArray[i]=data[startmode+1+i];
                                        }
                                    }
                                    sc.ACReadCState((byte) sent.subnetID, (byte) sent.deviceID,MainActivity.mydupsocket);
                                    break;
                                case 0xe0ed:
                                    String state_ac="";
                                    if(!readcstate){
                                        readcstate=true;
                                        if(data[25]==0){
                                            state_ac="OFF";
                                        }else{
                                            state_ac="ON";
                                        }
                                        CoolTemp=data[26];
                                        HeatTemp=data[30];
                                        AutoTemp=data[32];
                                        if((data[27]&0x0f)==fanArray.length){
                                            CurrentFanMode=fanArray[(data[27]&0x0f)-1];
                                        }else{
                                            CurrentFanMode=fanArray[(data[27]&0x0f)];
                                        }

                                        switch(CurrentFanMode){
                                            case 0://const_fan_speed_anto:
                                                fanstate="Auto speed";
                                                break;
                                            case 1://const_fan_speed_high:
                                                fanstate="High speed";
                                                break;
                                            case 2://const_fan_speed_medium:
                                                fanstate="Medium speed";
                                                break;
                                            case 3://const_fan_speed_low:
                                                fanstate="Low speed";
                                                break;
                                            default:break;
                                        }
                                        if((data[27]>>4)==modeArray.length){
                                            CurrentMode=modeArray[(data[27]>>4)-1];
                                        }else{
                                            CurrentMode=modeArray[(data[27]>>4)];
                                        }

                                        switch(CurrentMode){
                                            case 3://const_mode_auto:
                                                actemp=(String.valueOf(AutoTemp&0xff));
                                                modestate=("AUTO");
                                                break;
                                            case 0://const_mode_cool:
                                                actemp=(String.valueOf(CoolTemp&0xff));
                                                modestate=("COOL");
                                                break;
                                            case 2://const_mode_fan:
                                                modestate=("FAN");
                                                break;
                                            case 1://const_mode_heat:
                                                actemp=(String.valueOf(HeatTemp&0xff));
                                                modestate=("HEAT");
                                                break;
                                            default:break;
                                        }
                                    }

                                    if ((data[25]&0xff)==0){
                                        state_ac="OFF";
                                    }else{
                                        state_ac="ON:/"+fanstate+"/"+modestate+"/"+actemp+acunit;
                                    }
                                    viewchildlist.get(sentcount).setstatus(state_ac);
                                    readinglist[sentcount] = 1;
                                    sentcount++;

                                    acunit="";fanstate="";modestate="";actemp="";
                                    readCF=false;readCountfanAndMode=false;readcstate=false;
                                    CurrentMode=3;CoolTemp=20;HeatTemp=20;AutoTemp=20;CurrentFanMode=2;
                                    break;
                            }
                        }
                    } else {

                    }
                }
            }else{//有人进行操作，反馈状态
                int find=0;
                for(int i=0;i<statuslist.size();i++){
                    if(((data[17] & 0xff) == statuslist.get(i).subnetID) && ((data[18] & 0xff) == statuslist.get(i).deviceID))
                    {
                        switch (statuslist.get(i).type){
                            case 1:
                                if(((data[25] & 0xff)==viewchildlist.get(i).getchannel())&&(x==0x0032)){
                                    String state = (data[27]&0xff) + "%";
                                    viewchildlist.get(i).setstatus(state);
                                    find=1;
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                if((x==0xe3e8)){
                                    String statetemp="",sunittemp="";
                                    switch (data[25]%0xff){
                                        case 0:sunittemp=" ℉";break;
                                        case 1:sunittemp=" ℃";break;
                                    }
                                    if(data.length>35){
                                        switch (data[34]&0xff){
                                            case 0:statetemp=""+(data[26]&0xff)+sunittemp;break;
                                            case 1:statetemp="-"+(data[26]&0xff)+sunittemp;break;
                                        }
                                    }else{
                                        statetemp=""+(data[26]&0xff)+sunittemp;
                                    }
                                    viewchildlist.get(i).setstatus(statetemp);
                                    find=1;
                                }

                                break;
                            case 4:

                                break;
                            case 5:
                                if(((data[25] & 0xff)==viewchildlist.get(i).getchannel())&&(x==0x0032)){
                                    String state ="";
                                    switch (data[27]&0xff){
                                        case 0:state="OFF";break;
                                        case 100:state="ON";break;
                                    }
                                    viewchildlist.get(i).setstatus(state);
                                    find=1;
                                }
                                break;
                            case 6:
                                if((x==0xe3e8)){
                                    String state4t="",sunit4t="";
                                    int chan=viewchildlist.get(i).getchannel();
                                    switch (data[25]%0xff){
                                        case 0:sunit4t=" ℉";break;
                                        case 1:sunit4t=" ℃";break;
                                    }
                                    switch (data[33+chan]&0xff){
                                        case 0:state4t=""+(data[25+chan]&0xff)+sunit4t;break;
                                        case 1:state4t="-"+(data[25+chan]&0xff)+sunit4t;break;
                                    }
                                    viewchildlist.get(i).setstatus(state4t);
                                    find=1;
                                }

                                break;
                        }
                        if(find==1){
                            break;
                        }
                    }
                }
            }

            doing = false;
        }
    }
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(ACTION_REFLASH);
        return intentFilter;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.light_setting_menu, menu);
        add= menu.findItem(R.id.light_add);
        delete= menu.findItem(R.id.light_remove);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.light_add:
                addstatus();
                break;
            case R.id.light_remove:
                intodeletemode=!intodeletemode;
                if(intodeletemode){
                    add.setTitle("CANCLE DELETE");
                    delete.setTitle("DELETE");
                    if(viewchildlist.size()>0){viewchildlist.clear();}
                    for(int i=0;i<statuslist.size();i++){
                        StatusLayout x=(StatusLayout)statuscontent.findViewById(statuslist.get(i).status_id);
                        viewchildlist.add(x);
                        x.setdeletevisable(true);
                    }
                }else{
                    for(int i=0;i<viewchildlist.size();i++){
                        if(viewchildlist.get(i).getIfneedtoDelete()){
                            MainActivity.mgr.deletestatus("status", viewchildlist.get(i).getstatusid());
                        }
                    }
                    getstatushandler.postDelayed(getstatusrun,20);
                    add.setTitle("ADD");
                    delete.setTitle("DELETE");
                    intodeletemode=false;
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        color= Color.argb(255, Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
        return color;
    }
}
