package com.shima.smartbushome.assist.nfc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.marcoCompare;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Savemarco;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.database.Savenfc;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.founction_command.fancontrol;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.founction_command.mediacontrol;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_command.othercontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */
public class NFCRun {

    private NotificationManager notificationManager;
    private Notification notification;
    private String nofitycontent="";
    private Context rootcontext;
    List<Savemarco> clickmarco=new ArrayList<>();
    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;

    public NFCRun(Context context){
        rootcontext=context;
    }
    public void run(List<Savenfc> nfclist){
        for(int i=0;i<nfclist.size();i++){
            RunnfcSystem(nfclist.get(i));
        }
    }
    public void RunnfcSystem(Savenfc data){
        switch (data.action_type){
            case 1:
                nofitycontent="Marco:"+data.marco_name;
                break;
            case 2:
                nofitycontent="Call:"+data.call_num;
                break;
            case 3:
                break;
        }
        Notification.Builder builder = new Notification.Builder(rootcontext);
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(rootcontext, 0, mIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(rootcontext.getResources(), R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setContentTitle(nofitycontent);
        notificationManager = (NotificationManager) rootcontext.getSystemService(rootcontext.NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            notification=builder.build();
        }else{
            notification = new Notification(R.mipmap.ic_launcher, nofitycontent, System.currentTimeMillis());
        }
        if(notificationManager!=null&&notification!=null){
            notificationManager.notify(0, notification);
        }

        switch (data.action_type){
            case 1:
                if(data.state==0){
                    if(data.nfc_id!=99999999){
                        if(clickmarco.size()>0){clickmarco.clear();}
                        int macid=data.marco_ID;
                        List<Savemarco> allmarco=MainActivity.mgr.querymarco();
                        clickmarco=listorder(macid,allmarco);
                        senthandler.postDelayed(sent,20);
                    }else{

                    }
                }
                break;
            case 2:
                if(data.state==0){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri dataurl = Uri.parse("tel:" + data.call_num);
                    intent.setData(dataurl);
                    rootcontext.startActivity(intent);
                }
                break;
            case 3:
                break;
        }
    }

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
                    Toast.makeText(rootcontext, "finished", Toast.LENGTH_SHORT).show();
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
