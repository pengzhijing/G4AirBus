package com.shima.smartbushome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.assist.Adapter.FounctionAdapter;
import com.shima.smartbushome.assist.DraggableGridViewType2;
import com.shima.smartbushome.database.Saveroom;
import com.shima.smartbushome.util.SystemUIUtil;

public class RoomActivity extends AppCompatActivity {

    private DraggableGridViewType2 mDraggableGridViewPager;
    public String[] founctionlist={""};
    private FounctionAdapter mAdapter;
    int roomid;
    private int mGridCount;
    public boolean deletefc=false;
    android.os.Handler handlefc =new android.os.Handler();
    String[] founction_count={"0","0","0","0","0","0","0","0","0","0"};//light,ac,fan,curtain,music,mood,other,media,nio,fh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.room_toolbar);
        Intent intent = getIntent();
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Room:" + intent.getStringExtra("roomname"));
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

        SharedPreferences sharedcolorPre = getSharedPreferences("pagesbgcolor", MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("roombgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)findViewById(R.id.roomacbg);
        roomacbg.setBackgroundColor(backgroudcolor);


        mDraggableGridViewPager = (DraggableGridViewType2) findViewById(R.id.draggable_grid_view_pager);
        roomid=intent.getIntExtra("roomid",0);
        mAdapter = new FounctionAdapter(this);
        try{
            founctionlist=MainActivity.mgr.queryroomfounction(roomid);
        }catch (Exception e){
            finish();
        }
        for(int i=0;i<founctionlist.length;i++){
            if(founctionlist[i].equals("1")){
                switch(i){
                    case 0:mAdapter.add("Light");founction_count[0]="1";break;
                    case 1:mAdapter.add("HVAC");founction_count[1]="1";break;
                    case 2:mAdapter.add("Fan");founction_count[2]="1";break;
                    case 3:mAdapter.add("Curtain");founction_count[3]="1";break;
                    case 4:mAdapter.add("Music");founction_count[4]="1";break;
                    case 5:mAdapter.add("Mood");founction_count[5]="1";break;
                    case 6:mAdapter.add("Other");founction_count[6]="1";break;
                    case 7:mAdapter.add("Media");founction_count[7]="1";break;
                    case 8:mAdapter.add("9 in 1");founction_count[8]="1";break;
                    case 9:mAdapter.add("Floor Heat");founction_count[9]="1";break;
                        default:break;
                }
            }
        }
        mDraggableGridViewPager.setAdapter(mAdapter);
        mDraggableGridViewPager.setOnPageChangeListener(new DraggableGridViewType2.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /* Log.v(TAG, "onPageScrolled position=" + position + ", positionOffset=" + positionOffset
                        + ", positionOffsetPixels=" + positionOffsetPixels);*/
            }

            @Override
            public void onPageSelected(int position) {
                // Log.i(TAG, "onPageSelected position=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.d(TAG, "onPageScrollStateChanged state=" + state);
            }
        });
        mDraggableGridViewPager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (deletefc) {
                    //String x = ((TextView) view).getText().toString().trim();
                    String x=((TextView)view.findViewById(R.id.dragtype2tv)).getText().toString().trim();
                    Saveroom roominfo = new Saveroom();
                    roominfo.room_id = roomid;
                    if (x.equals("Light")) {
                        roominfo.light = 0;
                        founctionlist[0]="0";//light
                        MainActivity.mgr.deletefc("light", roomid);
                    } else if (x.equals("HVAC")) {
                        roominfo.hvac = 0;
                        founctionlist[1]="0";//ac
                        MainActivity.mgr.deletefc("hvac", roomid);
                    } else if (x.equals("Mood")) {
                        roominfo.mood = 0;
                        founctionlist[5]="0";//mood
                        MainActivity.mgr.deletefc("mood", roomid);
                        MainActivity.mgr.deletefc("moodbutton", roomid);
                    } else if (x.equals("Fan")) {
                        roominfo.fan = 0;
                        founctionlist[2]="0";//fan
                        MainActivity.mgr.deletefc("fan", roomid);
                    } else if (x.equals("Curtain")) {
                        roominfo.curtain = 0;
                        founctionlist[3]="0";//curtain
                        MainActivity.mgr.deletefc("curtain", roomid);
                    } else if (x.equals("Music")) {
                        roominfo.music = 0;
                        founctionlist[4]="0";//music
                        MainActivity.mgr.deletefc("music", roomid);
                        MainActivity.mgr.deletefc("song", roomid);
                        MainActivity.mgr.deletefc("radio", roomid);
                    }else if(x.equals("Other")){
                        roominfo.other=0;
                        founctionlist[6]="0";//other
                        MainActivity.mgr.deletefc("other", roomid);
                    }else if(x.equals("Media")){
                        roominfo.media=0;
                        founctionlist[7]="0";//media
                        MainActivity.mgr.deletefc("media", roomid);
                        MainActivity.mgr.deletefc("mediabutton", roomid);
                    }else if(x.equals("9 in 1")){
                        roominfo.nio=0;
                        founctionlist[8]="0";//nio
                        MainActivity.mgr.deletefc("nio", roomid);
                    }else if(x.equals("Floor Heat")){
                        roominfo.fh=0;
                        founctionlist[9]="0";//fh
                        MainActivity.mgr.deletefc("floorheat", roomid);
                    }
                    MainActivity.mgr.updateroom(roominfo, x.toLowerCase());
                    handlefc.postDelayed(getfc, 20);
                    deletefc = false;
                } else {
                    Intent gotofounction = new Intent();
                    gotofounction.putExtra("roomid", roomid);
                    gotofounction.putExtra("founction_type", ((TextView)view.findViewById(R.id.dragtype2tv)).getText().toString().trim());
                    gotofounction.setClass(RoomActivity.this, FounctionActivity.class);
                    Bundle b=new Bundle();
                    b.putStringArray("founction_count", founction_count);
                    gotofounction.putExtras(b);
                    startActivity(gotofounction);
                }

                //showToast(((TextView) view).getText().toString());
            }
        });

    }
    Runnable getfc=new Runnable() {
        @Override
        public void run() {
            // roomname.clear();
            founctionlist[0]="0";//light
            founctionlist[1]="0";//ac
            founctionlist[2]="0";//fan
            founctionlist[3]="0";//curtain
            founctionlist[4]="0";//music
            founctionlist[5]="0";//mood
            founctionlist[6]="0";//other
            founctionlist[7]="0";//media
            founctionlist[8]="0";//nio
            founctionlist[9]="0";//fh
            mAdapter.clear();
           // mAdapter=new FounctionAdapter(RoomActivity.this);
            founctionlist=MainActivity.mgr.queryroomfounction(roomid);
            for(int i=0;i<founctionlist.length;i++){
                if(founctionlist[i].equals("1")){
                    switch(i){
                        case 0:mAdapter.add("Light");break;
                        case 1:mAdapter.add("HVAC");break;
                        case 2:mAdapter.add("Fan");break;
                        case 3:mAdapter.add("Curtain");break;
                        case 4:mAdapter.add("Music");break;
                        case 5:mAdapter.add("Mood");break;
                        case 6:mAdapter.add("Other");break;
                        case 7:mAdapter.add("Media");break;
                        case 8:mAdapter.add("9 in 1");break;
                        case 9:mAdapter.add("Floor Heat");break;
                        default:break;
                    }
                }
            }
            mDraggableGridViewPager.setAdapter(mAdapter);
            handlefc.removeCallbacks(getfc);
        }
    };

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int statusBarHeight=0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_menu, menu);
        return true;
    }
    final String[] mItems = {"Light", "HVAC","Fan", "Curtain","Music","Mood","Other","Media","9 in 1","Floor Heat"};
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case R.id.founction_add:
                new AlertView(null, null, "CANCEL", null,
                        mItems, this, AlertView.Style.Alert, itemlick).show();
                break;
            case R.id.founction_delete:
                if (mAdapter.getCount() > 0) {
                    deletefc=true;
                    showToast("pls click one item to delete");
                    // mAdapter.remove(mAdapter.getItem(mAdapter.getCount() - 1));
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public com.bigkoo.alertview.OnItemClickListener itemlick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else{
                int t = 0;
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (mAdapter.getItem(i).equals(mItems[position])) {
                        t = 1;
                    }
                }
                if (t == 0) {
                    //mAdapter.add(mItems[which]);
                    // ArrayList<Saveroom> tips = new ArrayList<Saveroom>();
                    Saveroom roominfo = new Saveroom();
                    roominfo.room_id=roomid;
                    if(mItems[position].equals("Light")){
                        roominfo.light=1;
                        founction_count[0]="1";
                    }else if(mItems[position].equals("HVAC")){
                        roominfo.hvac=1;
                        founction_count[1]="1";
                    }else if(mItems[position].equals("Curtain")){
                        roominfo.curtain=1;
                        founction_count[3]="1";
                    }else if(mItems[position].equals("Music")){
                        roominfo.music=1;
                        founction_count[4]="1";
                    }else if(mItems[position].equals("Mood")){
                        roominfo.mood=1;
                        founction_count[5]="1";
                    }else if(mItems[position].equals("Fan")){
                        roominfo.fan=1;
                        founction_count[2]="1";
                    }else if(mItems[position].equals("Other")){
                        roominfo.other=1;
                        founction_count[6]="1";
                    }else if(mItems[position].equals("Media")){
                        roominfo.media=1;
                        founction_count[7]="1";
                    }else if(mItems[position].equals("9 in 1")){
                        roominfo.nio=1;
                        founction_count[8]="1";
                    }else if(mItems[position].equals("Floor Heat")){
                        roominfo.fh=1;
                        founction_count[9]="1";
                    }
                    MainActivity.mgr.updateroom(roominfo, mItems[position].toLowerCase());//toLowerCase() 将大写字母转为小写
                    handlefc.postDelayed(getfc,20);
                } else {
                    Toast.makeText(RoomActivity.this, mItems[position] + " already exist", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
    }
}
