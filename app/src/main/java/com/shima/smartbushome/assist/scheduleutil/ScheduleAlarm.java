package com.shima.smartbushome.assist.scheduleutil;

import android.content.Context;
import android.os.Handler;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.assist.marcoCompare;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.database.Saveschedule;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.founction_command.fancontrol;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.founction_command.mediacontrol;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_command.othercontrol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */
public class ScheduleAlarm {
    private int marcoID=0;
    private String tv_repeat="";
    private List<Savemarco> clickmarco=new ArrayList<>();
    private Saveschedule thisschedule;
    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;
    private Context rootcontext;
    private OnStatusChangeListener listener;
    private DBManager thismgr;
    public void start(Saveschedule activeschedule,Context context,DBManager mgr){
        rootcontext=context;
        thisschedule=activeschedule;
        thismgr=mgr;
        List<Savemarco> allmarco= thismgr.querymarco();
        clickmarco=listorder(activeschedule.marco_ID,allmarco);
        senthandler.postDelayed(sent,20);
    }
    public void setOnStatusChangeListener(OnStatusChangeListener l) {
        listener = l;
    }
    public interface OnStatusChangeListener {

        void onStatusChange(String status);

    }
/******************************************** update alarm time **********************************************/
    private void updateAlarmtime(Saveschedule schedule){
        Saveschedule addschedule=new Saveschedule();
        addschedule.schedule_id=schedule.schedule_id;
        addschedule.schedule_icon="";
        addschedule.schedule_name=schedule.schedule_name;
        tv_repeat="";
        setrepeat(schedule.repeat_option1, schedule.repeat_option2);
        String time=schedule.alarm_time.split(":")[3];
        addschedule.alarm_time=getTargettime(schedule.repeat_option1, schedule.repeat_option2, time);
        if(schedule.repeat_option1==1){
            addschedule.last_status=1;
        }else{
            addschedule.last_status=0;
        }
        addschedule.marco_ID=schedule.marco_ID;
        addschedule.repeat_option1=schedule.repeat_option1;
        addschedule.repeat_option2=schedule.repeat_option2;
        thismgr.updateschedule(addschedule);
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

    /******************************************** run marco **********************************************/
    Handler senthandler=new Handler();
    int sentcount=0;
    boolean senting=false;
    Runnable sent=new Runnable() {
        @Override
        public void run() {
            if(senting){
                sentcount++;
                senting=false;
                if(sentcount>=clickmarco.size()){
                    senthandler.removeCallbacks(sent);
                    sentcount=0;
                    updateAlarmtime(thisschedule);
                    listener.onStatusChange("done");
                    //Toast.makeText(rootcontext, "finished", Toast.LENGTH_SHORT).show();
                }else{
                    senthandler.postDelayed(sent,75);
                }
            }else{
                if(clickmarco.size()>0){
                    Savemarco sentmarco=clickmarco.get(sentcount);
                    sentmarco(sentmarco);
                }
                senthandler.postDelayed(sent,250);//450
                senting=true;
            }
        }
    };

    Savemarco senttemp=new Savemarco();

    public class senttemp implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                switch (senttemp.value3){
                    case 0:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_cold_tmp, senttemp.value2,MainActivity.mydupsocket);
                        break;
                    case 1:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_heat_tmp, senttemp.value2,MainActivity.mydupsocket);
                        break;
                    case 3:
                        ac.ACControl((byte)senttemp.subnetID,(byte)senttemp.deviceID,const_ac_cmd_type_set_auto_tmp,senttemp.value2,MainActivity.mydupsocket);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    lightcontrol lc=new lightcontrol();
    ACcontrol ac=new ACcontrol();
    curtaincontrol cc=new curtaincontrol();
    musiccontrol mc=new musiccontrol();
    othercontrol oc=new othercontrol();
    fancontrol fc=new fancontrol();
    mediacontrol mec=new mediacontrol();
    public void sentmarco(Savemarco sentmarco){
        switch (sentmarco.control_type){
            case 1:
                Savelight thislight=new Savelight();
                List<Savelight> alllight=MainActivity.mgr.querylight();
                for(int i=0;i<alllight.size();i++){
                    if(alllight.get(i).room_id==sentmarco.room_id&&alllight.get(i).light_statement.equals(sentmarco.device)){
                        thislight=alllight.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        switch (sentmarco.value2){
                            case 0:lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,0,MainActivity.mydupsocket);break;
                            case 1:lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,100,MainActivity.mydupsocket);break;
                        }
                        break;
                    case 1:
                        lc.SingleChannelControl((byte)thislight.subnetID,(byte)thislight.deviceID,thislight.channel,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                    case 2:
                        lc.ARGBlightcontrol((byte)thislight.subnetID,(byte)thislight.deviceID,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                }
                break;
            case 2:
                Savehvac thisac=new Savehvac();
                List<Savehvac> allac=MainActivity.mgr.queryhvac();
                for(int i=0;i<allac.size();i++){
                    if(allac.get(i).room_id==sentmarco.room_id&&allac.get(i).hvac_remark.equals(sentmarco.device)){
                        thisac=allac.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_onoff,sentmarco.value2,MainActivity.mydupsocket);
                        break;
                    case 1:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_set_mode,sentmarco.value3,MainActivity.mydupsocket);
                        senttemp=sentmarco;
                        senttemp.subnetID=thisac.subnetID;
                        senttemp.deviceID=thisac.deviceID;
                        new Thread(new senttemp()).start();
                        break;
                    case 2:
                        ac.ACControl((byte)thisac.subnetID,(byte)thisac.deviceID,const_ac_cmd_type_set_fan,sentmarco.value2,MainActivity.mydupsocket);
                        break;

                }
                break;
            case 3:
                Savecurtain thisct=new Savecurtain();
                List<Savecurtain> allct=MainActivity.mgr.querycurtain();
                for(int i=0;i<allct.size();i++){
                    if(allct.get(i).room_id==sentmarco.room_id&&allct.get(i).curtain_remark.equals(sentmarco.device)){
                        thisct=allct.get(i);
                        break;
                    }
                }
                switch (sentmarco.value2){
                    case 0:cc.CurtainControl((byte)thisct.subnetID,(byte)thisct.deviceID,thisct.channel_1,thisct.channel_2,"close",MainActivity.mydupsocket);break;
                    case 1:cc.CurtainControl((byte)thisct.subnetID,(byte)thisct.deviceID,thisct.channel_1,thisct.channel_2,"open",MainActivity.mydupsocket);break;
                }
                break;
            case 4:
                switch (sentmarco.value1){
                    case 1:
                        mc.MusicControl((byte)1,(byte)sentmarco.value2,(byte)0,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 3:
                        mc.MusicControl((byte)3,(byte)6,(byte)sentmarco.value3,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 4:
                        mc.MusicControl((byte)4,(byte)sentmarco.value2,(byte)0,(byte)0,(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 5:
                        mc.MusicControl((byte)5,(byte)1,(byte)3,(byte)(79-((79 * sentmarco.value2) / 100)),(byte)sentmarco.subnetID,(byte)sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                    case 6:
                        byte[] songbyte = new byte[2];
                        songbyte[0] = (byte) ((sentmarco.value3 &0xff00)>>8);
                        songbyte[1] = (byte) ((sentmarco.value3 ) - (sentmarco.value3&0xff00));
                        mc.MusicControl((byte) 6, (byte) (sentmarco.value2), songbyte[0], songbyte[1], (byte) sentmarco.subnetID, (byte) sentmarco.deviceID,MainActivity.mydupsocket);
                        break;
                }
                break;
            case 5:
                Saveother thisot=new Saveother();
                List<Saveother> allot=MainActivity.mgr.queryother();
                for(int i=0;i<allot.size();i++){
                    if(allot.get(i).room_id==sentmarco.room_id&&allot.get(i).other_statement.equals(sentmarco.device)){
                        thisot=allot.get(i);
                        break;
                    }
                }
                switch (sentmarco.value1){
                    case 0:
                        switch (sentmarco.value2){
                            case 0:oc.SingleChannelControl((byte)thisot.subnetID,(byte)thisot.deviceID,thisot.channel_1,0,MainActivity.mydupsocket);break;
                            case 1:oc.SingleChannelControl((byte)thisot.subnetID,(byte)thisot.deviceID,thisot.channel_1,100,MainActivity.mydupsocket);break;
                        }
                        break;
                    case 1:
                        switch (sentmarco.value2){
                            case 0:oc.CurtainControl((byte) thisot.subnetID, (byte) thisot.deviceID, thisot.channel_1,
                                    thisot.channel_2,"close",MainActivity.mydupsocket);break;
                            case 1:oc.CurtainControl((byte) thisot.subnetID, (byte) thisot.deviceID, thisot.channel_1,
                                    thisot.channel_2,"open",MainActivity.mydupsocket);break;
                        }
                        break;
                }
                break;
            case 6:
                Savefan thisft=new Savefan();
                List<Savefan> allft=MainActivity.mgr.queryfan();
                for(int i=0;i<allft.size();i++){
                    if(allft.get(i).room_id==sentmarco.room_id&&allft.get(i).fan_statement.equals(sentmarco.device)){
                        thisft=allft.get(i);
                        break;
                    }
                }
                fc.FanChannelControl((byte)thisft.subnetID,(byte)thisft.deviceID,thisft.channel,sentmarco.value2,MainActivity.mydupsocket);
                break;
            case 7:
                Savemedia thismedia=new Savemedia();
                Savemediabutton thismediabutton=new Savemediabutton();
                List<Savemedia> allmedia=MainActivity.mgr.querymedia();
                List<Savemediabutton> allmediabutton=MainActivity.mgr.querymediabutton();
                for(int i=0;i<allmedia.size();i++){
                    if(allmedia.get(i).room_id==sentmarco.room_id&&allmedia.get(i).media_statement.equals(sentmarco.device)){
                        thismedia=allmedia.get(i);
                        break;
                    }
                }
                for(int i=0;i<allmediabutton.size();i++){
                    if(allmediabutton.get(i).room_id==sentmarco.room_id&&
                            allmediabutton.get(i).media_id==thismedia.media_id&& allmediabutton.get(i).button_num==sentmarco.value2){
                        thismediabutton=allmediabutton.get(i);
                        break;
                    }
                }
                if(thismediabutton.ifIRmarco==1){
                    mec.IRMarcoControl((byte) thismedia.subnetID, (byte) thismedia.deviceID, thismediabutton.media_swno, thismediabutton.media_controltype,MainActivity.mydupsocket);
                }else{
                    mec.IRControl((byte)thismedia.subnetID,(byte)thismedia.deviceID,thismediabutton.media_swno,thismediabutton.media_controltype,MainActivity.mydupsocket);
                }
                break;
        }
    }

    public List<Savemarco> listorder(int marcoid,List<Savemarco> allmarco){
        List<Savemarco> thisfoun=new ArrayList<>();
        List<Savemarco> result=new ArrayList<>();
        for(int i=0;i<allmarco.size();i++){
            if(marcoid==allmarco.get(i).marco_id){
                thisfoun.add(allmarco.get(i));
            }
        }
        Collections.sort(thisfoun, new marcoCompare());
        return thisfoun;
    }
}
