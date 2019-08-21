package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.founction_command.audio_incontrol;
import com.shima.smartbushome.founction_view.Music;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public class AudioInLayout extends RelativeLayout {
    Context rootcontext;
    ImageView audioin;
    View view;
    TextView tv_voicevalue;
    SeekBar sb_voice;
    Button play;
    boolean initstart=false;
    Handler getdatahandler=new Handler();
    audio_incontrol ai;
    Savemusic thismusic=new Savemusic();
    int playstate=0;
    public int Music_Source=1,PlayModeChange=2,AlbumorRadioControl=3,
            PlayControl=4,VoluneControl=5,ControlSpecSong=6;

    public AudioInLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        //initview(context);
    }
    public AudioInLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
       // initview(context);
    }
    public boolean ifinitstarted(){
        return initstart;
    }
    public void initview(Context context){
        rootcontext=context;
        view = View.inflate(context, R.layout.music_audioin_layout, this);
        initstart=true;
        audioin=(ImageView)view.findViewById(R.id.imageView4);
        tv_voicevalue=(TextView)view.findViewById(R.id.textView4);
        sb_voice=(SeekBar)view.findViewById(R.id.seekBar3);
        play=(Button)view.findViewById(R.id.checkBox4);
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (playstate) {
                    case 0:
                        setplaybuttonstate("play");
                        break;
                    case 1:
                        setplaybuttonstate("pause");
                        break;
                }
            }
        });
        sb_voice.setOnSeekBarChangeListener(voicechange);
        ai=new audio_incontrol();
        if(Build.VERSION.SDK_INT >= 16){
           // setdrawable(audioin);
            audioin.setBackground(getResources().getDrawable(R.drawable.music_audioin));
        }
        getdatahandler.postDelayed(getdatarun, 50);
    }
    /**********音量控制**********/
    private SeekBar.OnSeekBarChangeListener voicechange = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            tv_voicevalue.setText(seekBar.getProgress() + "%");
            ai.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG,"onStartTrackingTouch");
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(setvolume){
                tv_voicevalue.setText(progress + "%");
            }else{
                tv_voicevalue.setText(progress + "%");
               // ai.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID);

            }
         }
    };

    public void setplaybuttonstate(String str){
        switch (str){
            case "play":
                play.setBackgroundResource(R.drawable.pause);
                playstate=1;
                ai.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
            case "pause":
                play.setBackgroundResource(R.drawable.play);
                playstate=0;
                ai.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
        }
    }

    public void setcontent(Savemusic sm){
        thismusic=sm;
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            /*******************获取sub&dev id******************/
            List<Savemusic> musicx=MainActivity.mgr.querymusic();
            List<Savemusic> musicx2=new ArrayList<Savemusic>();
            for(int i=0;i<musicx.size();i++){
                if(musicx.get(i).room_id==FounctionActivity.roomidfc&&musicx.get(i).music_id==1){
                    musicx2.add(musicx.get(i));
                }
            }
            if (musicx2.size() > 0) {
                thismusic = musicx2.get(0);
                getvoicevalue();
            }else{
               /* List<Savemusic> k=new ArrayList<Savemusic>();
                Savemusic kk=new Savemusic();
                kk.room_id= FounctionActivity.roomidfc;
                kk.music_id = 1;
                kk.deviceID = 0;
                kk.subnetID=0;
                k.add(kk);
                MainActivity.mgr.addmusic(k);
                thismusic=kk;*/
            }

        }
    };
    boolean setvolume=false;
    public void setvoicevalue(byte[] datavalue){
        int a=(int)((datavalue[25]<<8))+(int)(datavalue[26]&0xff);
        int b=(int)(datavalue[36]&0xff);
        if(a==0x235a){
            byte[] voicevalue=new byte[2];
            if(((datavalue[37])==((byte)0x56))&&((datavalue[38])==((byte)0x4f))&&((datavalue[39])==((byte)0x4c))){
                if(datavalue[41]==(byte)0x0d){
                    voicevalue[0]=0x30;
                    voicevalue[1]=datavalue[40];
                }else if(datavalue[42]==(byte)0x0d){
                    voicevalue[0]=datavalue[40];
                    voicevalue[1]=datavalue[41];
                }
            }
            String str=bytetostring(voicevalue,"ascii");
            int value=Integer.parseInt(str);
            value=100-(100*value)/79;
            setvolume=true;
            sb_voice.setProgress(value);

            setvolume=false;
        }
    }
    public void getvoicevalue(){
        ai.GetMusicState( (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
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
    public void SwitchtoAudioIn(){
        ai.SwitchtoAudioIn((byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    }
    public void receiveddata(byte[] data){
        int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);
        if((data[17]==(byte)thismusic.subnetID)&&(data[18]==(byte)thismusic.deviceID)) {
            switch (x) {
                case 0x192f:
                    setvoicevalue(data);
                default:
                    break;
            }
        }
    }
    public void radiobackpress(){
        Music.finishmusic();
    }

    public void setdrawable(View v){
        Drawable backgroundDrawable = getContext().getResources().getDrawable(R.drawable.music_audioin);
        StateListDrawable sld = (StateListDrawable) backgroundDrawable;// 通过向下转型，转回原型，selector对应的Java类为：StateListDrawable

        Drawable.ConstantState cs = sld.getConstantState();

        try {
            Method method = cs.getClass().getMethod("getChildren", new Class[ 0 ]);// 通过反射调用getChildren方法获取xml文件中写的drawable数组
            method.setAccessible(true);
            Object obj = method.invoke(cs, new  Object[ 0 ]);
            Drawable[] drawables = (Drawable[]) obj;

            for (int i = 0; i < drawables.length; i++) {
                // 接下来我们要通过遍历的方式对每个drawable对象进行修改颜色值
                GradientDrawable gd = (GradientDrawable) drawables[i];
                if (gd == null) {
                    break;
                }
                if (i == 0) {
                    // 我们对按下的状态做浅色处理
                    gd.setColor(ToColor("40edbaff"));
                } else {
                    // 对默认状态做深色处理
                    gd.setColor(ToColor("40edbaff"));
                }
            }
            // 最后总结一下，为了实现这个效果，刚开始并没有看到setColor的方法，而是通过反射获取GradientDrawable对象的属性GradientState，
            // 再通过反射调用GradientState对象的setSolidColor方法去实现，效果不太理想。
            // 最后在仔仔细细一一看GradientDrawable对象的属性，发现属性Paint
            // mFillPaint，从名字就可以看出这个对象是用来绘制drawable的背景的，
            // 于是顺着往下找，发现setColor方法，于是bingo，这个过程也是挺曲折的。
            v.setBackground(backgroundDrawable);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public int ToColor(String data){
        int color=0;
        int rin,gin,bin,ain;
        ain=Integer.parseInt(data.substring(0,2),16);
        rin=Integer.parseInt(data.substring(2,4),16);
        gin=Integer.parseInt(data.substring(4,6),16);
        bin=Integer.parseInt(data.substring(6,8),16);
        color= Color.argb(ain, rin, gin, bin);
        return color;
    }
}
