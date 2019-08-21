package com.shima.smartbushome.centercontrol;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.WheelView;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.database.Saveroom;
import com.shima.smartbushome.founction_command.othercontrol;

import java.util.ArrayList;
import java.util.List;

public class AllOtherActivity extends AppCompatActivity {
    TextView allotherselectroom;
    WheelView wva;
    othercontrol oc=new othercontrol();
    List<Saveroom> roomlist=new ArrayList<Saveroom>();
    List<String> roomnamelist=new ArrayList<String>();
    List<Saveother> allotherlist=new ArrayList<Saveother>();
    Handler getdatahandler=new Handler();
    Handler senthandler=new Handler();
    String selectroom="";
    int value=0;
    boolean senting=false,allother=false;
    int roomid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_other);
        Toolbar toolbar = (Toolbar) findViewById(R.id.allother_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("All Other");
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

        SharedPreferences sharedcolorPre = getSharedPreferences("xxx", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("xxx", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allotherout);
        roomacbg.setBackgroundColor(backgroudcolor);

        wva = (WheelView) findViewById(R.id.otherroomchoose);
        allotherselectroom=(TextView)findViewById(R.id.allotherselectroom);
        wva.setOffset(1);

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                allotherselectroom.setText("Select Room: " + item);
                selectroom = item;
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
    public void onDestroy(){
        super.onDestroy();
        //unregeisterReceiver();
       // MainActivity.mydupsocket.StopAllThread();
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            roomlist= MainActivity.mgr.queryroom();
            for(int i=0;i<roomlist.size();i++){
                roomnamelist.add(roomlist.get(i).room_name);
            }
            roomnamelist.add("All Other");
            wva.setItems(roomnamelist);
            selectroom=roomnamelist.get(0);
            allotherselectroom.setText("Select Room: " + selectroom);
            allotherlist=MainActivity.mgr.queryother();
        }
    };

    int count=0;
    Runnable senrun=new Runnable() {
        @Override
        public void run() {
            if(count>=allotherlist.size()){
                count=0;
                senting=false;
                allother=false;
                Toast.makeText(AllOtherActivity.this, "finished", Toast.LENGTH_SHORT).show();
                senthandler.removeCallbacks(senrun);
            }else{
                if(allother){
                    if(value==2){
                        switch (allotherlist.get(count).other_type){
                            case 1:oc.SingleChannelControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                    allotherlist.get(count).channel_1,100,MainActivity.mydupsocket);break;
                            case 2:oc.CurtainControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                    allotherlist.get(count).channel_1, allotherlist.get(count).channel_2,"open",MainActivity.mydupsocket);break;
                        }
                    }else if(value==3){
                        switch (allotherlist.get(count).other_type){
                            case 1:oc.SingleChannelControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                    allotherlist.get(count).channel_1,0,MainActivity.mydupsocket);break;
                            case 2:oc.CurtainControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                    allotherlist.get(count).channel_1, allotherlist.get(count).channel_2,"close",MainActivity.mydupsocket);break;
                        }
                    }
                }else{
                    if(allotherlist.get(count).room_id==roomid){
                        if(value==2){
                            switch (allotherlist.get(count).other_type){
                                case 1:oc.SingleChannelControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                        allotherlist.get(count).channel_1,100,MainActivity.mydupsocket);break;
                                case 2:oc.CurtainControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                        allotherlist.get(count).channel_1, allotherlist.get(count).channel_2,"open",MainActivity.mydupsocket);break;
                            }
                        }else if(value==3){
                            switch (allotherlist.get(count).other_type){
                                case 1:oc.SingleChannelControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                        allotherlist.get(count).channel_1,0,MainActivity.mydupsocket);break;
                                case 2:oc.CurtainControl((byte)allotherlist.get(count).subnetID,(byte)allotherlist.get(count).deviceID,
                                        allotherlist.get(count).channel_1, allotherlist.get(count).channel_2,"close",MainActivity.mydupsocket);break;
                            }
                        }
                    }
                }
                count++;
                senthandler.postDelayed(senrun,70);
            }

        }
    };
    public void allotheron(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Other")){
            allother=true;
        }
        count=0;
        senting=true;
        value=2;//open
        senthandler.postDelayed(senrun,30);
    }

    public void allotheroff(View v){
        for(int i=0;i<roomlist.size();i++){
            if(roomlist.get(i).room_name.equals(selectroom)){
                roomid=roomlist.get(i).room_id;
                break;
            }
        }
        if(selectroom.equals("All Other")){
            allother=true;
        }
        count=0;
        senting=true;
        value=3;//close
        senthandler.postDelayed(senrun,30);
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
