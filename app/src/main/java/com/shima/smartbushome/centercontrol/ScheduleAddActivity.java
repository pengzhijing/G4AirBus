package com.shima.smartbushome.centercontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.alertview.AlertView;
import com.bigkoo.pickerview.TimePickerView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;

import com.shima.smartbushome.assist.scheduleutil.DateUtils;
import com.shima.smartbushome.assist.scheduleutil.SelectRemindCyclePopup;
import com.shima.smartbushome.database.Savemarcobutton;
import com.shima.smartbushome.database.Saveschedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleAddActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView date_tv;
    private TimePickerView pvTime;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value;
    private LinearLayout allLayout;
    private Button set_btn;
    private String time="";
    private int cycle=9999;
    private int ring=0;
    private EditText ed_echedule_name;
    private boolean updatedata=false;
    int secheduleid=0,shcedulelength=0,op1=0;
    String repeat_update="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scheduleadd_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Schedule Add");
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

        allLayout = (LinearLayout) findViewById(R.id.all_layout);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        date_tv = (TextView) findViewById(R.id.date_tv);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        ring_rl = (RelativeLayout) findViewById(R.id.ring_rl);
        ring_rl.setOnClickListener(this);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        tv_ring_value = (TextView) findViewById(R.id.tv_ring_value);
        ed_echedule_name=(EditText)findViewById(R.id.ed_echedule_name);
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {//Callback
                time = getTime(date);
                date_tv.setText(time);
            }
        })
                .setType(TimePickerView.Type.HOURS_MINS)//default all
                .setCancelText("Cancel")
                .setSubmitText("Comfirn")
                .setOutSideCancelable(false)// default is true
                .isCyclic(false)// default is false
                .setDate(Calendar.getInstance())// default is System time
                .setLabel("year","month","day","hours","mins","seconds")
                .build();

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });

        Intent intent=getIntent();
        if(intent.getBooleanExtra("updatedata",false)){
            updatedata=true;
            secheduleid= intent.getIntExtra("scheduleID", 0);
            shcedulelength=intent.getIntExtra("length", 0);
            ed_echedule_name.setText(intent.getStringExtra("name"));
            time=intent.getStringExtra("time");
            setTime(time);
            set_btn.setText("Update");
            repeat_update=intent.getStringExtra("repeat");
            tv_repeat_value.setText(repeat_update);
            op1=intent.getIntExtra("op1", 0);
            marcoid=intent.getIntExtra("marcoid", 0);
        }else{
            updatedata=false;
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


    public void setTime(String str){
        str=str.split(":")[3];
        str=str.substring(0,2)+":"+str.substring(2,4);
        date_tv.setText(str);
        time=str;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.ring_rl:
                selectRingWay();
                break;
            case R.id.set_btn:
                if(updatedata){
                    updateClock();
                }else{

                    setClock();
                }
                break;
            default:
                break;
        }
    }
    private void updateClock(){
        if(cycle!=9999){
            if (time != null && time.length() > 0) {
                String[] times = time.split(":");
                if (cycle == 0) {//是每天的闹钟
                    repeat_op1=2;
                } else if(cycle == -1){//是只响一次的闹钟
                    repeat_op1=1;
                }else {//多选，周几的闹钟
                    repeat_op1=3;

                    String weeksStr = parseRepeat(cycle, 1);
                    String[] weeks = weeksStr.split(",");
                    repeat_op2=0;
                    for (int i = 0; i < weeks.length; i++) {
                        setop2( Integer.parseInt(weeks[i]));
                    }
                }
            }
        }else{
            if (time != null && time.length() > 0) {
                String[] times = time.split(":");
                if (op1 == 2) {//是每天的闹钟
                    repeat_op1=2;
                } else if(op1 == 1){//是只响一次的闹钟
                    repeat_op1=1;

                }else if(op1==3){//多选，周几的闹钟
                    repeat_op1=3;
                    String[] weeks = repeat_update.split(",");
                    repeat_op2=0;
                    for (int i = 0; i < weeks.length; i++) {
                        setop2( Integer.parseInt(weeks[i]));
                    }
                }
            }
        }

        String name=ed_echedule_name.getText().toString().trim();
        if(name.length()>0){
            if(time.equals("")){
                Toast.makeText(ScheduleAddActivity.this, "please select time", Toast.LENGTH_SHORT).show();
            }else{
                Saveschedule addschedule=new Saveschedule();
                addschedule.schedule_id=secheduleid;
                addschedule.schedule_icon="";
                addschedule.schedule_name=name;
                addschedule.alarm_time=getTargettime(repeat_op1, repeat_op2, time);
                addschedule.last_status=0;
                addschedule.marco_ID=marcoid;
                addschedule.repeat_option1=repeat_op1;
                addschedule.repeat_option2=repeat_op2;
                MainActivity.mgr.updateschedule(addschedule);
                broadcastUpdate(ScheduleActivity.ACTION_UPDATE_Schedule);
                finish();
            }
        }else{
            Toast.makeText(ScheduleAddActivity.this, "please enter a name", Toast.LENGTH_SHORT).show();
        }
    }
    int repeat_op1=0,repeat_op2=0;
    private void setClock() {
        int schedule_id=getscheduleID();
        if (time != null && time.length() > 0) {
            String[] times = time.split(":");
            if (cycle == 0) {//是每天的闹钟
                repeat_op1=2;
            }else if(cycle == -1){//是只响一次的闹钟
                repeat_op1=1;
            }else {//多选，周几的闹钟
                repeat_op1=3;
                String weeksStr = parseRepeat(cycle, 1);
                String[] weeks = weeksStr.split(",");
                repeat_op2=0;
                for (int i = 0; i < weeks.length; i++) {
                    setop2( Integer.parseInt(weeks[i]));
                }
            }
        }
        if(cycle!=9999){
            String name=ed_echedule_name.getText().toString().trim();
            if(name.length()>0){
                Saveschedule addschedule=new Saveschedule();
                addschedule.schedule_id=schedule_id;
                addschedule.schedule_icon="";
                addschedule.schedule_name=name;
                addschedule.alarm_time=getTargettime(repeat_op1, repeat_op2, time);
                addschedule.last_status=0;
                addschedule.marco_ID=marcoid;
                addschedule.repeat_option1=repeat_op1;
                addschedule.repeat_option2=repeat_op2;
                MainActivity.mgr.addschedule(addschedule);
                broadcastUpdate(ScheduleActivity.ACTION_ADD_Schedule);
                finish();
            }else{
                Toast.makeText(ScheduleAddActivity.this, "please enter a name", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ScheduleAddActivity.this, "you need to select repeat", Toast.LENGTH_SHORT).show();
        }
    }
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    public void setop2(int weeks){//设置op2的值，保存到数据库
        switch (weeks){
            case 1:repeat_op2=repeat_op2|0x02;
                break;
            case 2:repeat_op2=repeat_op2|0x04;
                break;
            case 3:repeat_op2=repeat_op2|0x08;
                break;
            case 4:repeat_op2=repeat_op2|0x10;
                break;
            case 5:repeat_op2=repeat_op2|0x20;
                break;
            case 6:repeat_op2=repeat_op2|0x40;
                break;
            case 7:repeat_op2=repeat_op2|0x80;
                break;
        }
    }

    private int getscheduleID(){
        int result=0;
        List<Saveschedule> alldata=MainActivity.mgr.queryschedule();
        if(alldata.size()==0){
            result=1;
        }else{
            result=alldata.get(alldata.size()-1).schedule_id+1;
        }
        return result;
    }
    private String getTargettime(int op1,int op2,String time){
        String result="";
        String target_time=time.split(":")[0]+time.split(":")[1];
        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy:MM:dd:HHmm");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy:MM:dd");
        Date curDate =  new Date(System.currentTimeMillis());
        String   currenttime   =   formatter.format(curDate);
        switch (op1){
            case 1://only one time
            case 2://everyday
                if(Integer.parseInt(currenttime.split(":")[3])<=Integer.parseInt(target_time)){
                    result=currenttime.split(":")[0]+":"+currenttime.split(":")[1]+":"+currenttime.split(":")[2]+":"+target_time;
                }else{
                    Date date=new Date();//取时间
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
                    date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                    String dateString = formatter2.format(date);
                    result=dateString+":"+target_time;
                }
                break;
            case 3://selected week
                int todayweek= DateUtils.getWeek(formatter2.format(curDate));
                String[] weekstr=tv_repeat_value.getText().toString().split(",");
                String todaywrrkstr="";
                switch (todayweek){
                    case 1://sunday
                        todaywrrkstr="Sun";
                        break;
                    case 2://monday
                        todaywrrkstr="Mon";
                        break;
                    case 3://tuesday
                        todaywrrkstr="Tue";
                        break;
                    case 4://wedurday
                        todaywrrkstr="Wed";
                        break;
                    case 5://thursday
                        todaywrrkstr="Thu";
                        break;
                    case 6://friday
                        todaywrrkstr="Fri";
                        break;
                    case 7://saturday
                        todaywrrkstr="Sat";
                        break;
                }
                int hadthisweek=999;
                for(int i=0;i<weekstr.length;i++){
                    if(todaywrrkstr.equals(weekstr[i])){
                        hadthisweek=i;
                    }
                }
                if(hadthisweek!=999){
                    if(Integer.parseInt(currenttime.split(":")[3])<=Integer.parseInt(target_time)){
                        result=currenttime.split(":")[0]+":"+currenttime.split(":")[1]+":"+currenttime.split(":")[2]+":"+target_time;
                    }else{
                        if(weekstr.length==1){
                            Date date=new Date();//取时间
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(calendar.DATE,7);//把日期往后增加7天.整数往后推,负数往前移动
                            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                            String dateString = formatter2.format(date);
                            result=dateString+":"+target_time;
                        }else{
                            String nextday="";
                            if((hadthisweek-1)>=(0)){
                                 nextday=weekstr[hadthisweek-1];
                            }else{
                                 nextday=weekstr[weekstr.length-1];
                            }
                            Date date=new Date();//取时间
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(date);
                            calendar.add(calendar.DATE,getweektime(todaywrrkstr,nextday));//把日期往后增加7天.整数往后推,负数往前移动
                            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                            String dateString = formatter2.format(date);
                            result=dateString+":"+target_time;
                        }
                    }
                }else{
                    int mintime=9;
                    for(int i=0;i<weekstr.length;i++){
                            mintime=Math.min(mintime,getweektime(todaywrrkstr,weekstr[i]));
                    }
                    Date date=new Date();//取时间
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE,mintime);//把日期往后增加7天.整数往后推,负数往前移动
                    date=calendar.getTime(); //这个时间就是日期往后推一天的结果
                    String dateString = formatter2.format(date);
                    result=dateString+":"+target_time;

                }
                break;
        }
        return result;
    }
    private int getweektime(String today,String nextday){
        int result=0;
        int todayint=getweekint(today);
        int nextdayint=getweekint(nextday);
        if(todayint>nextdayint){
            result=7-(todayint-nextdayint);
        }else if(todayint<nextdayint){
            result=nextdayint-todayint;
        }
        return result;
    }
    private int getweekint(String str){
        int result=0;
        switch (str){
            case "Sun"://sunday
                result=7;
                break;
            case "Mon"://monday
                result=1;
                break;
            case "Tue"://tuesday
                result=2;
                break;
            case "Wed"://wedurday
                result=3;
                break;
            case "Thu"://thursday
                result=4;
                break;
            case "Fri"://friday
                result=5;
                break;
            case "Sat"://saturday
                result=6;
                break;
        }
        return result;
    }
    public void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    // 星期一
                    case 0:

                        break;
                    // 星期二
                    case 1:

                        break;
                    // 星期三
                    case 2:

                        break;
                    // 星期四
                    case 3:

                        break;
                    // 星期五
                    case 4:

                        break;
                    // 星期六
                    case 5:

                        break;
                    // 星期日
                    case 6:

                        break;
                    // 确定
                    case 7:
                        int repeat = Integer.valueOf(ret);
                        tv_repeat_value.setText(parseRepeat(repeat, 0));
                        cycle = repeat;
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("EveryDay");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("Only One Time");
                        cycle = -1;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    AlertView marcolistalert;
    List<Savemarcobutton> listmarco=new ArrayList<>();
    List<String> stringlist=new ArrayList<>();
    private int marcoid=99999999;
    public void selectRingWay() {
        listmarco=MainActivity.mgr.querymarcobutton();
        if(stringlist.size()>0){stringlist.clear();}
        for(int i=0;i<listmarco.size();i++){
            stringlist.add(listmarco.get(i).marco_remark);
        }
        marcolistalert = new AlertView("Select Marco to Act", null, "CANCEL",  null, null, this, AlertView.Style.Alert,
                null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.listdialog, null);
        ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
        extView.setLayoutParams(lp);
        deletelist.setAdapter(new ArrayAdapter(this, R.layout.simplelistitem, stringlist));
        deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_ring_value.setText(listmarco.get(position).marco_remark);
                marcoid=listmarco.get(position).marco_id;
                marcolistalert.dismiss();
            }
        });
        marcolistalert.addExtView(extView);
        marcolistalert.show();
    }

    /**
     * @param repeat 解析二进制闹钟周期
     * @param flag   flag=0返回带有汉字的周一，周二cycle等，flag=1,返回weeks(1,2,3)
     * @return
     */
    public static String parseRepeat(int repeat, int flag) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            cycle = "Mon";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "Tue";
                weeks = "2";
            } else {
                cycle = cycle + "," + "Tue";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "Wed";
                weeks = "3";
            } else {
                cycle = cycle + "," + "Wed";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "Thu";
                weeks = "4";
            } else {
                cycle = cycle + "," + "Thu";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "Fri";
                weeks = "5";
            } else {
                cycle = cycle + "," + "Fri";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "Sat";
                weeks = "6";
            } else {
                cycle = cycle + "," + "Sat";
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                cycle = "Sun";
                weeks = "7";
            } else {
                cycle = cycle + "," + "Sun";
                weeks = weeks + "," + "7";
            }
        }

        return flag == 0 ? cycle : weeks;
    }
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
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
