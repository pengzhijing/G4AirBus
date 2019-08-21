package com.shima.smartbushome.assist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savesong;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_view.Music;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class MusicNotifyReceiver extends BroadcastReceiver {
    musiccontrol mc=new musiccontrol();
    public int Music_Source=1,PlayModeChange=2,AlbumorRadioControl=3,
            PlayControl=4,VoluneControl=5,ControlSpecSong=6;
    public static int subnetID=0,deviceID=0;
    int playcount=0;
    boolean count=false;
    Handler playhandler=new Handler();
    Handler receivehandler=new Handler();
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (MusicNotification.ACTION_Button.equals(action)) {
            int type=intent.getIntExtra("button_type",0);
            switch (type){
                case 0:mc.MusicControl((byte)PlayControl,(byte)1,(byte)0,(byte)0,(byte)subnetID,(byte)deviceID,MainActivity.mydupsocket);
                    break;
                case 1: mc.MusicControl((byte)PlayControl,(byte)2,(byte)0,(byte)0,(byte)subnetID,(byte)deviceID,MainActivity.mydupsocket);
                    break;
                case 2:
                    if(MusicNotification.playcheck){
                        mc.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte)subnetID, (byte)deviceID,MainActivity.mydupsocket);
                        MusicNotification.remoteViews.setImageViewResource(R.id.music_notify_play, R.drawable.notify_pause);
                        MusicNotification.manager.notify(MusicNotification.NOTICE_ID_TYPE_0, MusicNotification.notification);
                    }else{
                        mc.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte)subnetID, (byte)deviceID,MainActivity.mydupsocket);
                        MusicNotification.remoteViews.setImageViewResource(R.id.music_notify_play, R.drawable.notify_play);
                        MusicNotification.manager.notify(MusicNotification.NOTICE_ID_TYPE_0, MusicNotification.notification);}
                        playhandler.postDelayed(playrun,300);
                    break;
            }
        }
        else if(MusicNotification.ACTION_notify_close.equals(action)){
            int noticeId = intent.getIntExtra(MusicNotification.NOTICE_ID_KEY, -1);
            if(noticeId != -1){
                MusicNotification.clearNotification(context, noticeId);
            }
        } if (udp_socket.ACTION_DATA_IN.equals(action)) {
            if(Music.musicchoosestate==0){
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                if(rev.length>35){
                    try{
                        receiveddata(rev);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                rev=null;
            }

        }
    }
    boolean doing=true;boolean reflashuienable=false;
    Handler reflashhandler=new Handler();
    public void receiveddata(byte[] data){
        if(doing){
            doing=false;
            int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);
            if((data[17]==(byte)subnetID)&&(data[18]==(byte)deviceID)) {
                switch (x) {
                    case 0x192f:
                            if(reflashuienable){
                                getmusicstate(data);
                            }else{
                                reflashstep=false;
                                getreflashablum=false;
                                reflashui();
                            }
                        break;
                    default:
                        break;
                }
            }
            doing=true;
        }
    }
    public void reflashui(){
        reflashuienable=true;
        reflashhandler.postDelayed(reflashrun,70);
    }
    boolean reflashstep=false,getreflashablum=false;
    Runnable reflashrun=new Runnable() {
        @Override
        public void run() {
            if(reflashstep){
                reflashuienable=false;
                reflashhandler.removeCallbacks(reflashrun);
            }else{
                mc.GetMusicState((byte)subnetID,(byte)deviceID,MainActivity.mydupsocket);
                reflashhandler.postDelayed(reflashrun,300);
            }

        }
    };
    int recalbumno=0,recsongno=0;//get selectsong when reflash
    public void getmusicstate(byte[] datavalue){
        int a=(int)((datavalue[25]<<8))+(int)(datavalue[26]&0xff);
        int b=(int)(datavalue[36]&0xff);
         if(a==0x2353){
            if(!reflashstep){
                switch (b){
                    case 0x31:
                        if(!getreflashablum) {
                            getreflashablum=true;
                            byte[] album = getpieceofbyte(datavalue, 0);
                            String str = bytetostring(album, "Unicode");
                            int value = Integer.parseInt(str);
                            recalbumno = value;
                        }
                        break;
                    case 0x33:
                        byte[] song = getpieceofbyte(datavalue,1);
                        String str2 = bytetostring(song, "Unicode");
                        int value2=Integer.parseInt(str2);
                        recsongno=value2;
                        reflashstep=true;
                        receivehandler.postDelayed(getselectmusic,10);
                        break;
                    default:break;
                }
            }
        }
    }
    public byte[] getpieceofbyte(byte[] data,int type){
        int endbyte=0,startbyte=0;
        if(type==0){
            startbyte=49;
        }else if(type==1){
            startbyte=51;
        }
        for(int i=startbyte;i<data.length;i++){
            if((data[i]==(byte)0x00)&&(data[i+1]==(byte)0x2f)){
                endbyte=i;
                break;
            }
        }
        byte[] result=new byte[endbyte-startbyte];
        for(int i=0;i<endbyte-startbyte;i++){
            result[i]=data[startbyte+i];
        }
        return result;
    }
    public String bytetostring(byte[] name,String type){
        String s="";
        try {
            s= new String(name,type);// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    Savesong selectedsong=new Savesong();
    List<Savesong> data=new ArrayList<Savesong>();
    Runnable getselectmusic=new Runnable() {
        @Override
        public void run() {

            if(data.size()>0){}
            else{
                List<Savesong> alldata=MainActivity.mgr.querysong();
                for(int i=0;i<alldata.size();i++){
                    if(alldata.get(i).room_id== FounctionActivity.roomidfc){
                        data.add(alldata.get(i));//get all song data in this room
                    }
                }
            }
            for (int i = 0; i < data.size(); i++) {
                if ((data.get(i).album_num==recalbumno)&&(data.get(i).song_num==recsongno)) {
                    selectedsong = data.get(i);
                    MusicNotification.sendResidentNoticeType0(MainActivity.maincontext, selectedsong.song_name);
                    reflashstep=true;
                    break;
                }
            }
            receivehandler.removeCallbacks(getselectmusic);
        }
    };
    Runnable playrun=new Runnable() {
        @Override
        public void run() {
            if(playcount==0){
                playcount++;
                playhandler.removeCallbacks(playrun);
                if(MusicNotification.playcheck){
                    MusicNotification.playcheck=false;
                }else{
                    MusicNotification.playcheck=true;
                }
                playcount=0;
            }
        }
    };
}
