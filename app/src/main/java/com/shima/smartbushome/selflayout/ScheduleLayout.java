package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.scheduleutil.DateUtils;
import com.shima.smartbushome.centercontrol.ScheduleAddActivity;
import com.shima.smartbushome.database.Saveschedule;
import com.kyleduo.switchbutton.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ScheduleLayout extends LinearLayout implements View.OnLongClickListener {
    View view;
    Context rootcontext;
    LayoutInflater inflater;
    Saveschedule schedulecontent;
    TextView tv_actiontime,tv_repeat,tv_name;
    CheckBox cb_delete;
    SwitchButton schedule_switch;
    RelativeLayout schedule_selflayout;
    boolean deletemode=false,init=false;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public ScheduleLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public void initview(Context context) {
        view = View.inflate(context, R.layout.schedule_layout, this);
        rootcontext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initUI();
    }

    public void initUI(){
        tv_actiontime=(TextView)view.findViewById(R.id.schedule_time);
        tv_repeat=(TextView)view.findViewById(R.id.schedule_state);
        tv_name=(TextView)view.findViewById(R.id.schedule_name);
        cb_delete=(CheckBox)view.findViewById(R.id.schedule_delete);
        schedule_switch=(SwitchButton)view.findViewById(R.id.schedule_switch);
        schedule_selflayout=(RelativeLayout)view.findViewById(R.id.schedule_selflayout);
        tv_actiontime.setOnLongClickListener(this);
        schedule_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (init) {
                    if (isChecked) {
                        schedulecontent.last_status = 0;
                        MainActivity.mgr.updateschedule_status(schedulecontent);
                        updatealarmtime(schedulecontent);
                        //.makeText(rootcontext, "check", Toast.LENGTH_SHORT).show();
                    } else {
                        schedulecontent.last_status = 1;
                        MainActivity.mgr.updateschedule_status(schedulecontent);
                        // Toast.makeText(rootcontext, "uncheck", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }

    public boolean onLongClick(View v) {
        if(v.getId()==R.id.schedule_time){
            String[] weeks = getop2();
            Intent intent=new Intent(getContext(),ScheduleAddActivity.class);
            intent.putExtra("updatedata",true);
            intent.putExtra("scheduleID",schedulecontent.schedule_id);
            intent.putExtra("length",weeks.length);
            intent.putExtra("name", schedulecontent.schedule_name);
            intent.putExtra("time",schedulecontent.alarm_time);
            intent.putExtra("repeat",tv_repeat.getText().toString());
            intent.putExtra("op1",schedulecontent.repeat_option1);
            intent.putExtra("marcoid",schedulecontent.marco_ID);
            rootcontext.startActivity(intent);
        }
        return true;
    }


    private String[] getop2(){
        String[] result;
        int i=0;
        int op2=schedulecontent.repeat_option2;
        String[] weeks = tv_repeat.getText().toString().split(",");
        result=new String[weeks.length];
        if((op2&0x02)==0x02){
            result[i]="1";
            i++;
        }
        if((op2&0x04)==0x04){
            result[i]="2";
            i++;
        }
        if((op2&0x08)==0x08){
            result[i]="3";
            i++;
        }
        if((op2&0x10)==0x10){
            result[i]="4";
            i++;
        }
        if((op2&0x20)==0x20){
            result[i]="5";
            i++;
        }
        if((op2&0x40)==0x40){
            result[i]="6";
            i++;
        }
        if((op2&0x80)==0x80){
            result[i]="7";
            i++;
        }

         return result;
    }
    public void setcontan(Saveschedule sg) {
        schedulecontent=sg;
        setname(sg.schedule_name);
        setTime(sg.alarm_time);
        setrepeat(sg.repeat_option1, sg.repeat_option2);
        if(sg.last_status==0){
            schedule_switch.setChecked(true);
        }else if(sg.last_status==1){
            schedule_switch.setChecked(false);
        }
        //schedule_selflayout.setBackgroundColor(ToColor(colorarray[(schedulecontent.schedule_id%7)]));
        schedule_selflayout.setBackground(getResources().getDrawable(R.drawable.control_back_10));

        init=true;
    }

    public void setTime(String str){
        str=str.split(":")[3];
        str=str.substring(0,2)+":"+str.substring(2,4);
        tv_actiontime.setText(str);
    }
    public void setrepeat(int op1,int op2){
        switch (op1){
            case 1:
                tv_repeat.setText("Only One Time");
                break;
            case 2:
                tv_repeat.setText("EveryDay");
                break;
            case 3:
                if((op2&0x80)==0x80){
                    tv_repeat.append("Sun,");
                }
                if((op2&0x40)==0x40){
                    tv_repeat.append("Sat,");
                }
                if((op2&0x20)==0x20){
                    tv_repeat.append("Fri,");
                }
                if((op2&0x10)==0x10){
                    tv_repeat.append("Thu,");
                }

                if((op2&0x08)==0x08){
                    tv_repeat.append("Wed,");
                }
                if((op2&0x04)==0x04){
                    tv_repeat.append("Tue,");
                }
                if((op2&0x02)==0x02){
                    tv_repeat.append("Mon");
                }
                break;
        }
        
        
    }
    public void setname(String str){
        tv_name.setText(str);
    }

    public void setdeletevisable(boolean result){
        if(result){
            cb_delete.setVisibility(VISIBLE);
            deletemode=true;
            cb_delete.setChecked(false);
        }else{
            cb_delete.setVisibility(INVISIBLE);
            deletemode=false;
        }
    }
    public boolean getIfneedtoDelete(){
        return cb_delete.isChecked();
    }
    public int getscheduleid(){
        return schedulecontent.schedule_id;
    }
    public int ToColor(String data) {
        int color = 0;
        int rin, gin, bin, ain;
        ain = Integer.parseInt(data.substring(0, 2), 16);
        rin = Integer.parseInt(data.substring(2, 4), 16);
        gin = Integer.parseInt(data.substring(4, 6), 16);
        bin = Integer.parseInt(data.substring(6, 8), 16);
        color = Color.argb(ain, rin, gin, bin);
        return color;
    }
    /*********************************update alarm time************************************************/

    private void updatealarmtime(Saveschedule schedule){
        Saveschedule addschedule=new Saveschedule();
        addschedule.schedule_id=schedule.schedule_id;
        addschedule.schedule_icon="";
        addschedule.schedule_name=schedule.schedule_name;
        tv_repeat.setText("");
        setrepeat(schedule.repeat_option1, schedule.repeat_option2);
        String time=schedule.alarm_time.split(":")[3];
        addschedule.alarm_time=getTargettime(schedule.repeat_option1, schedule.repeat_option2, time);
        addschedule.last_status=0;
        addschedule.marco_ID=schedule.marco_ID;
        addschedule.repeat_option1=schedule.repeat_option1;
        addschedule.repeat_option2=schedule.repeat_option2;
        MainActivity.mgr.updateschedule(addschedule);
    }
    private String getTargettime(int op1,int op2,String time){
        String result="";
        String target_time=time;
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy:MM:dd:HHmm");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy:MM:dd");
        Date curDate =  new Date(System.currentTimeMillis());
        String   currenttime   =   formatter.format(curDate);
        switch (op1){
            case 1://only one time
            case 2://everyday
                if(Integer.parseInt(currenttime.split(":")[3])<Integer.parseInt(target_time)){
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
                String[] weekstr=tv_repeat.getText().toString().split(",");
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
                    if(Integer.parseInt(currenttime.split(":")[3])<Integer.parseInt(target_time)){
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
}
