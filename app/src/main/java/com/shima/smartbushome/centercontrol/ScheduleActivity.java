package com.shima.smartbushome.centercontrol;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Saveschedule;
import com.shima.smartbushome.selflayout.ScheduleLayout;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    RelativeLayout noschedule;
    ScrollView scheduleview;
    LinearLayout schedulelayout;
    boolean deletemode=false;
    MenuItem  add,delete;
    Handler getdatahandler=new Handler();
    List<ScheduleLayout> layoutlist=new ArrayList<>();
    List<Saveschedule> allschedule=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scheduletoolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Schedule");
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

        /*SharedPreferences sharedcolorPre = getSharedPreferences("pagesbgcolor", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("curtainbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.allcurtainout);
        roomacbg.setBackgroundColor(backgroudcolor);*/
        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        noschedule=(RelativeLayout)findViewById(R.id.noschedule);
        scheduleview=(ScrollView)findViewById(R.id.scheduleview);
        schedulelayout=(LinearLayout)findViewById(R.id.schedulelinearlayout);
        getdatahandler.postDelayed(getdatarun,20);
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
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_Schedule.equals(action)) {
                getdatahandler.postDelayed(getdatarun,20);
                Toast.makeText(ScheduleActivity.this, "Update Alarm Succeed", Toast.LENGTH_SHORT).show();
            }else if(ACTION_ADD_Schedule.equals(action)){
                getdatahandler.postDelayed(getdatarun,20);
                Toast.makeText(ScheduleActivity.this, "Set Alarm Succeed", Toast.LENGTH_SHORT).show();
            }
        }
    };
    int SCHEDULE_REQUEST=1;
    public static String ACTION_UPDATE_Schedule="com.smarthome.updateschedule";
    public static String ACTION_ADD_Schedule="com.smarthome.addschedule";
    public void addschedulebutton(View v){
        startActivity(new Intent(this, ScheduleAddActivity.class));
    }

    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            if(allschedule.size()>0){allschedule.clear();}
            if(layoutlist.size()>0){layoutlist.clear();}
            schedulelayout.removeAllViews();
            allschedule= MainActivity.mgr.queryschedule();
            if(allschedule.size()>0){
                noschedule.setVisibility(View.GONE);
                scheduleview.setVisibility(View.VISIBLE);
                for(int i=0;i<allschedule.size();i++){
                    addspecView(allschedule.get(i));
                }
            }else{
                noschedule.setVisibility(View.VISIBLE);
                scheduleview.setVisibility(View.GONE);
            }
            getdatahandler.removeCallbacks(getdatarun);
        }
    };

    private void addspecView(Saveschedule sg) {
        ScheduleLayout lv=new ScheduleLayout(this);
        lv.setcontan(sg);
        schedulelayout.addView(lv);
        layoutlist.add(lv);
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
                if(deletemode){
                    add.setTitle("ADD");
                    delete.setTitle("DELETE");
                    deletemode=false;
                    for(int i=0;i<layoutlist.size();i++){
                        layoutlist.get(i).setdeletevisable(false);
                    }
                }else{
                    startActivityForResult(new Intent(this,ScheduleAddActivity.class),SCHEDULE_REQUEST);
                }
                break;
            case R.id.light_remove:
                deletemode=!deletemode;
                if(deletemode){
                    add.setTitle("CANCLE DELETE");
                    delete.setTitle("DELETE");
                    for(int i=0;i<layoutlist.size();i++){
                        layoutlist.get(i).setdeletevisable(true);
                    }
                }else{
                    for(int i=0;i<layoutlist.size();i++){
                        if(layoutlist.get(i).getIfneedtoDelete()){
                            MainActivity.mgr.deleteschedule(layoutlist.get(i).getscheduleid());
                        }
                    }
                    getdatahandler.postDelayed(getdatarun,20);
                    add.setTitle("ADD");
                    delete.setTitle("DELETE");
                    deletemode=false;
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(ACTION_UPDATE_Schedule);
        intentFilter.addAction(ACTION_ADD_Schedule);
        return intentFilter;
    }
}
