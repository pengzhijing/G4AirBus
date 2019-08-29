package com.shima.smartbushome.centercontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.WheelView;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Saveroom;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.selflayout.LightType1;
import com.shima.smartbushome.selflayout.LightType2;
import com.shima.smartbushome.selflayout.LightType3;
import com.shima.smartbushome.selflayout.LightType4;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

public class AllLightActivity extends AppCompatActivity {
    TextView alllightselectroom;
    WheelView wva;
    lightcontrol lg=new lightcontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Savelight> alllightlist=new ArrayList<Savelight>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    int value=0;
    boolean senting=false,alllight=false;
    int roomid=0;
    Button button8;
    LinearLayout lightcontrollayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.alllight_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All Light");
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

        lightcontrollayout=(LinearLayout)findViewById(R.id.alllighcontent);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.alllightout);
        roomacbg.setBackgroundColor(backgroudcolor);

        wva = (WheelView) findViewById(R.id.lightroomchoose);
        alllightselectroom=(TextView)findViewById(R.id.alllightselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                alllightselectroom.setText("Select Room: " + item);
                selectroom = item;


                //根据选择的房间显示设备列表
                lightcontrollayout.removeAllViews();

                for(int i=0;i<alllightlist.size();i++){
                    if ((selectedIndex-1)==roomlist.size()){
                        addspecView(alllightlist.get(i));
                    }else if ((selectedIndex-1)<roomlist.size()){
                        if (roomlist.get(selectedIndex-1).room_id==alllightlist.get(i).room_id){
                            addspecView(alllightlist.get(i));
                        }
                    }

                }

                reflashroomlight();

            }
        });
       /* if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/

        getdatahandler.postDelayed(getdatarun, 20);
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
    public  List<LightType1> type1list= new ArrayList<LightType1>();
    public  List<LightType2> type2list= new ArrayList<LightType2>();
    public  List<LightType3> type3list= new ArrayList<LightType3>();
    public  List<LightType4> type4list= new ArrayList<LightType4>();
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            roomlist= MainActivity.mgr.queryroom();
            for(int i=0;i<roomlist.size();i++){
                roomnamelist.add(roomlist.get(i).room_name);
            }
            roomnamelist.add("All Light");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            alllightselectroom.setText("Select Room: " + selectroom);
            alllightlist=MainActivity.mgr.querylight();

            /***********************************************/
            lightcontrollayout.removeAllViews();

            for(int i=0;i<alllightlist.size();i++){
                addspecView(alllightlist.get(i));
            }

            reflashroomlight();
            // reflashhandler.postDelayed(reflashuiRun, 20000);
            getdatahandler.removeCallbacks(getdatarun);

        }
    };
    private void addspecView(Savelight lg) {
        switch(lg.lightType){
            case 1:
                LightType1 lv=new LightType1(lightcontrollayout.getContext());
                lv.setcontant(lg);
                lv.setId(lg.light_id);
                lightcontrollayout.addView(lv);
                type1list.add(lv);
                break;
            case 2:
                LightType2 lv2=new LightType2(lightcontrollayout.getContext());
                lv2.setcontant(lg);
                lv2.setId(lg.light_id);
                lightcontrollayout.addView(lv2);
                type2list.add(lv2);
                break;
            case 3:
                LightType3 lv3=new LightType3(lightcontrollayout.getContext());
                lv3.setcontant(lg);
                lv3.setId(lg.light_id);
                lightcontrollayout.addView(lv3);
                type3list.add(lv3);
                break;
            case 4:
                LightType4 lv4=new LightType4(lightcontrollayout.getContext());
                lv4.setcontant(lg);
                lv4.setId(lg.light_id);
                lightcontrollayout.addView(lv4);
                type4list.add(lv4);
                break;
            default:break;
        }
    }
    Handler reflashroomlight=new Handler();
    public void reflashroomlight(){
       // reflashroomlight.postDelayed(reflashroomlightrun,0);
        for (int i=0;i<alllightlist.size();i++){
            sub=(byte)alllightlist.get(i).subnetID;
            dev=(byte)alllightlist.get(i).deviceID;
            lg.getlightstate(sub, dev,MainActivity.mydupsocket);
        }
    }
    byte sub=0,dev=0,getlightcount=0;
    boolean getstatefinish=false;
    int getcount=0,getcount2=0,timeout=0;
    Runnable reflashroomlightrun=new Runnable() {
        @Override
        public void run() {
            if(getlightcount<alllightlist.size()){
                if(alllightlist.get(getlightcount).subnetID!=(sub&0xff)){
                    sub=(byte)alllightlist.get(getlightcount).subnetID;
                    dev=(byte)alllightlist.get(getlightcount).deviceID;
                    lg.getlightstate(sub, dev,MainActivity.mydupsocket);
                    getcount++;
                }else if(alllightlist.get(getlightcount).deviceID!=(dev&0xff)){
                    sub=(byte)alllightlist.get(getlightcount).subnetID;
                    dev=(byte)alllightlist.get(getlightcount).deviceID;
                    lg.getlightstate(sub,dev,MainActivity.mydupsocket);
                    getcount++;
                }
                getlightcount++;
                reflashroomlight.postDelayed(reflashroomlightrun,200);
            }else{
                if(getcount2!=getcount){
                    sub=0;
                    dev=0;
                    getlightcount=0;
                    getcount=0;
                    getcount2=0;
                    timeout++;
                    if(timeout>=10){
                        reflashroomlight.removeCallbacks(reflashroomlightrun);
                        sub=0;
                        dev=0;
                        getlightcount=0;
                        getcount=0;
                        getcount2=0;
                        timeout=0;
                    }else{
                        reflashroomlight();
                    }
                }else{
                    reflashroomlight.removeCallbacks(reflashroomlightrun);
                    sub=0;
                    dev=0;
                    getlightcount=0;
                    getcount=0;
                    getcount2=0;
                    timeout=0;
                }

            }

        }
    };

    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=alllightlist.size()){
                count=0;
                senting=false;
                alllight=false;
                Toast.makeText(AllLightActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(alllight){
                    lg.SingleChannelControl((byte)alllightlist.get(count).subnetID,(byte)alllightlist.get(count).deviceID,
                            alllightlist.get(count).channel,value,MainActivity.mydupsocket);
                }else{
                    if(alllightlist.get(count).room_id==roomid){
                        lg.SingleChannelControl((byte)alllightlist.get(count).subnetID,(byte)alllightlist.get(count).deviceID,
                                alllightlist.get(count).channel,value,MainActivity.mydupsocket);
                    }
                }
                count++;
                senthandler.postDelayed(senrun,200);
            }

        }
    };
    public void alllighton(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Light")){
            alllight=true;
        }
        count=0;
        senting=true;
        value=100;
        senthandler.postDelayed(senrun,30);
    }
    public void alllightoff(View v){
        if(senting){
            Toast.makeText(AllLightActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All Light")){
                alllight=true;
            }
            count=0;
            senting=true;
            value=0;
            senthandler.postDelayed(senrun,30);
        }

    }
    public void alllight25(View v){
        if(senting){
            Toast.makeText(AllLightActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All Light")){
                alllight=true;
            }
            count=0;
            senting=true;
            value=25;
            senthandler.postDelayed(senrun,30);
        }

    }
    public void alllight50(View v){
        if(senting){
            Toast.makeText(AllLightActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All Light")){
                alllight=true;
            }
            count=0;
            senting=true;
            value=50;
            senthandler.postDelayed(senrun,30);
        }

    }
    public void alllight75(View v){
        if(senting){
            Toast.makeText(AllLightActivity.this, "senting command,please wait", Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<roomlist.size();i++){
                if(roomlist.get(i).room_name.equals(selectroom)){
                    roomid=roomlist.get(i).room_id;
                    break;
                }
            }
            if(selectroom.equals("All Light")){
                alllight=true;
            }
            count=0;
            senting=true;
            value=75;
            senthandler.postDelayed(senrun,30);
        }

    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if(rev.length>25){
                    RunReceiveData(rev);
                }
                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                finish();
                reflashroomlight.removeCallbacks(reflashroomlightrun);
            }
        }
    };
    public void RunReceiveData(byte[] data){
        int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);//get op code
        //str=byte2hex(data);
        // if (ifthesubanddevmatch(data[17]&0xff,data[18]&0xff,data[25]&0xff)) //get sub&device&channel and pair to light db
        switch (x) {
            case 0x0032:
                if (data[26] == (byte) 0xf8) {
                    int subid = ((int) (data[17]) & 0xff);
                    int devid = ((int) (data[18]) & 0xff);
                    int chan = ((int) (data[25]) & 0xff);
                    LightType1 a0 = null;
                    LightType2 a1 = null;
                    LightType3 a2 = null;
                    for (int i = 0; i < type1list.size(); i++) {
                        if (type1list.get(i).getsubid() == subid && type1list.get(i).getdevid() == devid
                                && type1list.get(i).getchannel() == chan) {
                            a0 = type1list.get(i);
                        }
                    }
                    for (int i = 0; i < type2list.size(); i++) {
                        if (type2list.get(i).getsubid() == subid && type2list.get(i).getdevid() == devid
                                && type2list.get(i).getchannel() == chan) {
                            a1 = type2list.get(i);
                        }
                    }
                    for (int i = 0; i < type3list.size(); i++) {
                        if (type3list.get(i).getsubid() == subid && type3list.get(i).getdevid() == devid) {
                            a2 = type3list.get(i);
                        }
                    }
                    if (a0 != null) {
                        a0.setReceiveChange(data[27]);
                    } else if (a1 != null) {
                        a1.setReceiveChange(data[27]);
                    }else if(a2!=null){
                        lg.getlightstate((byte)subid, (byte)devid,MainActivity.mydupsocket);
                    }
                }
                break;
            case 0x0034:
                getcount2++;
                int subid = ((int) (data[17]) & 0xff);
                int devid = ((int) (data[18]) & 0xff);
                try{
                    for(int i=0;i<type1list.size();i++){
                        if(type1list.get(i).getsubid()==subid&&type1list.get(i).getdevid()==devid&&type1list.get(i).getchannel()!=0xff){
                            try{
                                type1list.get(i).setReceiveChange(data[type1list.get(i).getchannel()+25]);//get the channel value
                            }catch (Exception e){
                                switch (type1list.get(i).getchannel()){
                                    case 49:
                                        type1list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type1list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type1list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type1list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(this, "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    }

                    for(int i=0;i<type2list.size();i++){
                        if(type2list.get(i).getsubid()==subid&&type2list.get(i).getdevid()==devid&&type2list.get(i).getchannel()!=0xff){
                            try{
                                type2list.get(i).setReceiveChange(data[type2list.get(i).getchannel()+25]);//get the channel value
                            }catch (Exception e){
                                switch (type2list.get(i).getchannel()){
                                    case 49:
                                        type2list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type2list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type2list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type2list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(this, "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        }
                    }

                    for(int i=0;i<type3list.size();i++){
                        if(type3list.get(i).getsubid()==subid&&type3list.get(i).getdevid()==devid){
                            byte[] rgbdata={data[26],data[27],data[28],data[29]};
                            type3list.get(i).setReceiveChange(rgbdata);//get the rgb value
                        }
                    }

                    for(int i=0;i<type4list.size();i++){
                        if(type4list.get(i).getsubid()==subid&&type4list.get(i).getdevid()==devid&&type4list.get(i).getchannel()!=0xff){
                            try{
                                type4list.get(i).setReceiveChange(data[type4list.get(i).getchannel()+25]);//get the channel value
                            }catch (Exception e){
                                switch (type4list.get(i).getchannel()){
                                    case 49:
                                        type4list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type4list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type4list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type4list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(this, "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                break;
            default:
                break;
        }

    }


    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_DELETELIGHT);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.marco_menu, menu);
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
}
