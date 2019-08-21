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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MarcoIconAdapter;
import com.shima.smartbushome.assist.Adapter.MarcoItemAdapter;
import com.shima.smartbushome.assist.DragListView.DragView;
import com.shima.smartbushome.assist.marcoCompare;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savemarcobutton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarcoAddActivity extends AppCompatActivity {
    public static int SAVEMISSION=11;
    public static String MISSION_REFLASH="reflash marco mission";
    DragView actionlist;
    EditText name;
    ImageView icon;
    Handler getdata=new Handler();
    int marcoid=0,editmarco=0;
    List<Savemarco> thismarcolist=new ArrayList<>();
    MarcoItemAdapter listadapter;
    AlertView iconalter;
    String iconstring="marco_icon1";
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("marco_icon1");add("marco_icon2");add("marco_icon3");add("marco_icon4");
            add("marco_icon5");add("marco_icon6");add("marco_icon7");add("marco_icon8");
            add("marco_icon9");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marco_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.marco_add_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("");
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

        Intent f=getIntent();
        marcoid=f.getIntExtra("marcoID", 0);
        editmarco=f.getIntExtra("editmarco",0);
        actionlist=(DragView )findViewById(R.id.listView7);

        icon=(ImageView)findViewById(R.id.marco_add_icon);
       // actionlist.setOnItemLongClickListener(actionlistitemclick);

        name=(EditText)findViewById(R.id.addmarco_name);
        getdata.postDelayed(run,30);
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

    public void changeicon(View v){
        iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, this, AlertView.Style.Alert,
                null);
        View selfviewx = getLayoutInflater().inflate(R.layout.mood_icon_select, null);
        GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
        icongrid.setAdapter(new MarcoIconAdapter(this));
        icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iconstring = iconarray.get(position);
                icon.setImageResource(getResourdIdByResourdName(MarcoAddActivity.this, iconarray.get(position) ));
                iconalter.dismiss();
            }
        });
        iconalter.addExtView(selfviewx);
        iconalter.show();
    }

    public void addmission(View v){
        Intent mission=new Intent(this, MarcoAddDetailActivity.class);
        mission.putExtra("marcoID", marcoid);
        int order=0;
        if(thismarcolist.size()>0){
            order=thismarcolist.get(thismarcolist.size()-1).sentorder+1;
        }else{
            order=0;
        }
        mission.putExtra("marcoOrder",order);
        startActivityForResult(mission, SAVEMISSION);
    }
    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // String change01 = data.getStringExtra("change01");
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case 11:
                getdata.postDelayed(run,30);
                Toast.makeText(MarcoAddActivity.this, "add succeed", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    Runnable run=new Runnable() {
        @Override
        public void run() {
            List<Savemarco> allmarco=new ArrayList<>();
            allmarco= MainActivity.mgr.querymarco();
            if(thismarcolist.size()>0){thismarcolist.clear();}
            for(int i=0;i<allmarco.size();i++){
                if(marcoid==allmarco.get(i).marco_id){
                    thismarcolist.add(allmarco.get(i));
                }
            }
            Collections.sort(thismarcolist,new marcoCompare());
            listadapter=new MarcoItemAdapter(MarcoAddActivity.this,thismarcolist);
            actionlist.setAdapter(listadapter);

            List<Savemarcobutton> allmarcobutton=MainActivity.mgr.querymarcobutton();
            for(int t=0;t<allmarcobutton.size();t++){
                if(marcoid==allmarcobutton.get(t).marco_id){
                    name.setText(allmarcobutton.get(t).marco_remark);
                    break;
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.marco_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                if(exitdialogshow){
                    mAlertView.dismiss();
                    exitdialogshow=false;
                }else{
                    mAlertView = new AlertView("Warning", "You still not save this setting, Are you sure to quit?", "CANCEL",
                            new String[]{"YES"}, null, this, AlertView.Style.Alert, exitclick);
                    mAlertView .setCancelable(false);
                    mAlertView .show();
                    exitdialogshow=true;
                }
                break;
            case R.id.marco_add_save:
                if(name.getText().toString().length()>0){
                    if(editmarco==1){
                        Savemarcobutton savebutton=new Savemarcobutton();
                        savebutton.marco_id=marcoid;
                        savebutton.marco_remark=name.getText().toString().trim();
                        savebutton.marco_icon=iconstring;
                        MainActivity.mgr.updatemarcobutton(savebutton);
                        Toast.makeText(MarcoAddActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        this.setResult(MarcoActivity.SAVEMARCO);
                        finish();
                    }else{
                        Savemarcobutton savebutton=new Savemarcobutton();
                        savebutton.marco_id=marcoid;
                        savebutton.marco_remark=name.getText().toString().trim();
                        savebutton.marco_icon=iconstring;
                        MainActivity.mgr.addmarcobutton(savebutton);
                        Toast.makeText(MarcoAddActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        this.setResult(MarcoActivity.SAVEMARCO);
                        finish();
                    }

                }else{
                    Toast.makeText(MarcoAddActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public com.bigkoo.alertview.OnItemClickListener exitclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){
                exitdialogshow=false;
            }else if(position==0){
                finish();
            }
        }
    };

    public static int getResourdIdByResourdName(Context context, String ResName){
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
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MISSION_REFLASH.equals(action)) {
                getdata.postDelayed(run,30);
            }
        }
    };

    boolean exitdialogshow=false;
    AlertView mAlertView;
    @Override
    public void onBackPressed() {
        if(exitdialogshow){
            mAlertView.dismiss();
            exitdialogshow=false;
        }else{
            mAlertView = new AlertView("Warning", "You still not save this setting, Are you sure to quit?", "CANCEL",
                    new String[]{"YES"}, null, this, AlertView.Style.Alert, exitclick);
            mAlertView .setCancelable(false);
            mAlertView .show();
            exitdialogshow=true;
        }

        return;
    }
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(MISSION_REFLASH);
        return intentFilter;
    }
}
