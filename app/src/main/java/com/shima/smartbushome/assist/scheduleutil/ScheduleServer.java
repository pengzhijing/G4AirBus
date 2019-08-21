package com.shima.smartbushome.assist.scheduleutil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.shima.smartbushome.StrongService;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Saveschedule;
import com.shima.smartbushome.udp.udp_socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduleServer extends Service {
    private Context rootcontext;
    private DBManager mgr;
    private udp_socket mudup;
    private SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy:MM:dd:HHmm");
    private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy:MM:dd");
    private String currenttime;
    private Handler runschedulehandler=new Handler();
    private int schedule_count=0;
    private List<Saveschedule> alldata=new ArrayList<>();
    private ScheduleAlarm alarm=new ScheduleAlarm();
    public ScheduleServer() {
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    startService2();
                    break;

                default:
                    break;
            }

        };
    };
    /**
     * 使用aidl 启动Service2
     */
    private StrongService startS2 = new StrongService.Stub() {
        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), ScheduleRestartService.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), ScheduleRestartService.class);
            getBaseContext().startService(i);
        }
    };
    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service2
     */
    @Override
    public void onTrimMemory(int level) {
		/*
		 * 启动service2
		 */
        startService2();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return (IBinder) startS2;
       // return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        rootcontext=this;
        //服务启动广播接收器，使得广播接收器可以在程序退出后在后天继续执行，接收系统时间变更广播事件
       /* ScheduleReceiver receiver=new ScheduleReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));*/
        mgr=new DBManager(this);
        mudup=new udp_socket(this);
        updateallScheduleAlarmTime();
        startService2();
		/*
		 * 此线程用监听Service2的状态
		 */
        new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = scheduleUtils.isServiceWork(ScheduleServer.this,
                            "com.dave.smartbushome.assist.scheduleutil.ScheduleRestartService");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(58000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Date curDate = new Date(System.currentTimeMillis());
                    currenttime= formatter.format(curDate);
                    alldata.clear();
                    alldata = mgr.queryschedule();
                    runschedulehandler.postDelayed(runschedule,20);

                }
            };
        }.start();
        Log.i("liujun", "后台进程被创建。。。");

    }

    Runnable runschedule=new Runnable() {
        @Override
        public void run() {
            if(schedule_count<alldata.size()){
                if(currenttime.equals(alldata.get(schedule_count).alarm_time)){
                    if(alldata.get(schedule_count).last_status==0){//0:on 1:off
                        alarm.start(alldata.get(schedule_count),rootcontext,mgr);
                        alarm.setOnStatusChangeListener(new ScheduleAlarm.OnStatusChangeListener() {
                            @Override
                            public void onStatusChange(String status) {
                                if(status.equals("done")){
                                    schedule_count++;
                                    runschedulehandler.postDelayed(runschedule,20);
                                }
                            }
                        });
                    }else{
                        schedule_count++;
                        runschedulehandler.postDelayed(runschedule,20);
                    }

                }else{
                    schedule_count++;
                    runschedulehandler.postDelayed(runschedule,20);
                }
            }else{
                schedule_count=0;
                runschedulehandler.removeCallbacks(runschedule);
            }

        }
    };


    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    private void startService2() {
        boolean isRun = scheduleUtils.isServiceWork(ScheduleServer.this,
                "com.dave.smartbushome.assist.scheduleutil.ScheduleRestartService");
        if (isRun == false) {
            try {
                startS2.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("liujun", "后台进程。。。");
        return START_STICKY;

    }

    @Override
    public void onDestroy() {

        Log.i("liujun", "后台进程被销毁了。。。");
        super.onDestroy();
    }

    private void updateallScheduleAlarmTime(){
        List<Saveschedule> allschedule=mgr.queryschedule();
        for(int i=0;i<allschedule.size();i++){
            if(allschedule.get(i).last_status==0){
                updatethisschedule(allschedule.get(i));
            }
        }
    }
    private String tv_repeat="";
    private void updatethisschedule(Saveschedule schedule){
        Saveschedule addschedule=new Saveschedule();
        addschedule.schedule_id=schedule.schedule_id;
        addschedule.schedule_icon="";
        addschedule.schedule_name=schedule.schedule_name;
        tv_repeat="";
        setrepeat(schedule.repeat_option1, schedule.repeat_option2);
        String time=schedule.alarm_time.split(":")[3];
        addschedule.alarm_time=getTargettime(schedule.repeat_option1, schedule.repeat_option2, time);
        addschedule.last_status=0;
        addschedule.marco_ID=schedule.marco_ID;
        addschedule.repeat_option1=schedule.repeat_option1;
        addschedule.repeat_option2=schedule.repeat_option2;
        mgr.updateschedule(addschedule);
    }
    public void setrepeat(int op1,int op2){
        switch (op1){
            case 1:
                tv_repeat="Only One Time";
                break;
            case 2:
                tv_repeat="EveryDay";
                break;
            case 3:
                if((op2&0x80)==0x80){
                    tv_repeat+="Sun,";
                }
                if((op2&0x40)==0x40){
                    tv_repeat+="Sat,";
                }
                if((op2&0x20)==0x20){
                    tv_repeat+="Fri,";
                }
                if((op2&0x10)==0x10){
                    tv_repeat+="Thu,";
                }
                if((op2&0x08)==0x08){
                    tv_repeat+="Wed,";
                }
                if((op2&0x04)==0x04){
                    tv_repeat+="Tue,";
                }
                if((op2&0x02)==0x02){
                    tv_repeat+="Mon";
                }
                break;
        }


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
                String[] weekstr=tv_repeat.split(",");
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
