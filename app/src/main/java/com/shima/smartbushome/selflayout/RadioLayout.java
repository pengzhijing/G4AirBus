package com.shima.smartbushome.selflayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MusicRadioChannelAdapter;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.database.Saveradio;
import com.shima.smartbushome.founction_command.radiocontrol;
import com.shima.smartbushome.founction_view.Music;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public class RadioLayout extends RelativeLayout {
    Context rootcontext;
    View view;
    ListView radiochannellist;
    TextView tv_voicevalue;
    Button play;
    SeekBar sb_voice;
    List<HashMap<String,String>> channellist=new ArrayList<HashMap<String,String>>();
    Handler getdatahandler=new Handler();
    Handler getchannel=new Handler();
    radiocontrol rc;
    Savemusic thismusic=new Savemusic();
    List<Saveradio> alldata= new ArrayList<Saveradio>();;
    List<Saveradio> thisroomradio= new ArrayList<Saveradio>();;
    MusicRadioChannelAdapter radioadapter;
    LayoutInflater inflater;
    ProgressDialog progress;
    int playstate=0;
    boolean initstart=false,setvolume=false;
    public int Music_Source=1,PlayModeChange=2,AlbumorRadioControl=3,
            PlayControl=4,VoluneControl=5,ControlSpecSong=6;
    public RadioLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
       // initview(context);
    }
    public RadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
       // initview(context);
    }

    public boolean ifinitstarted(){
        return initstart;
    }
    public void initview(Context context){
        rootcontext=context;
        initstart=true;
        view = View.inflate(context, R.layout.music_radio_layout, this);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        radiochannellist=(ListView)view.findViewById(R.id.listView2);
        tv_voicevalue=(TextView)view.findViewById(R.id.textView5);
        radiochannellist.setOnItemClickListener(channelclick);
        radiochannellist.setOnItemLongClickListener(channellongclick);
        play=(Button)view.findViewById(R.id.checkBox3);
        sb_voice=(SeekBar)view.findViewById(R.id.seekBar4);
        sb_voice.setOnSeekBarChangeListener(voicechange);
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (playstate){
                    case 0:
                        setplaybuttonstate("play");
                        break;
                    case 1:
                        setplaybuttonstate("pause");
                        break;
                }
            }
        });
        rc=new radiocontrol();
        progress = new ProgressDialog(rootcontext);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Getting Radio Channel info...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        getdatahandler.postDelayed(getdatarun, 50);
    }
    public void setcontent(Savemusic sm){
        thismusic=sm;
    }

    /**********音量控制**********/
    private SeekBar.OnSeekBarChangeListener voicechange = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            tv_voicevalue.setText(seekBar.getProgress() + "%");
            rc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79-((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
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
               // rc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID);
            }
          }
    };
    public void setplaybuttonstate(String str){
        switch (str){
            case "play":
                play.setBackgroundResource(R.drawable.pause);
                playstate=1;
                rc.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
            case "pause":
                play.setBackgroundResource(R.drawable.play);
                playstate=0;
                rc.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
        }
    }
    private AdapterView.OnItemClickListener channelclick=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            radioadapter.setSelectItem(position);
            radioadapter.notifyDataSetInvalidated();
            rc.MusicControl((byte) AlbumorRadioControl, (byte) 6, (byte) (position + 1), (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    };};

    final String[] mItems = {"setting","update all channel"};
    private AdapterView.OnItemLongClickListener channellongclick=new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertView("Settings", null, "CANCEL", null,
                    mItems, getContext(), AlertView.Style.Alert, itemlick).show();
            return false;
        };};
    EditText channelname,channelvalue;
    AlertView settingalter;
    Handler showsettinghandler=new Handler();
    public com.bigkoo.alertview.OnItemClickListener itemlick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else{
                if(mItems[position].equals("setting")){
                    settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                            settingclick);
                    ViewGroup setextView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.setting_radioinfo, null);
                    channelname = (EditText) setextView.findViewById(R.id.etradioname);
                    channelvalue = (EditText) setextView.findViewById(R.id.etradiovalue);
                    channelvalue.setHint("87.5MHZ to 108.0MHZ");
                    channelname.setText(alldata.get(radioadapter.getSelectItem()).channel_name.trim());
                    channelvalue.setText(channelvaluetoedit(alldata.get(radioadapter.getSelectItem()).channel_value));
                    settingalter.addExtView(setextView);
                    showsettinghandler.postDelayed(showsettingalertrun,500);
                }else if(mItems[position].equals("update all channel")){
                    MainActivity.mgr.deleteradio("radio", FounctionActivity.roomidfc);
                    channellist.clear();
                    getChannelValue();
                }
            }
        }
    };

    Runnable showsettingalertrun=new Runnable() {
        @Override
        public void run() {
            settingalter.show();
            showsettinghandler.removeCallbacks(showsettingalertrun);
        }
    };
    public com.bigkoo.alertview.OnItemClickListener settingclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }
            if(position==0){
                if (channelvalue.getText().toString().trim().length() > 3) {
                    int value = Integer.parseInt(edittochannelvalue(channelvalue.getText().toString().trim()));
                    if (value >= 875 && value <= 1080) {
                        Saveradio x = new Saveradio();
                        x.room_id = FounctionActivity.roomidfc;
                        x.channel_num = position + 1;
                        x.channel_name = channelname.getText().toString().trim();
                        x.channel_value = edittochannelvalue(channelvalue.getText().toString().trim());
                        MainActivity.mgr.updateradio(x);
                        byte[] writename = new byte[40];
                        try {
                            byte[] name = (x.channel_name.trim()).getBytes("UnicodeLittleUnmarked");
                            for (int r = 0; r < name.length; r++) {
                                writename[r] = name[r];
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rc.WriteChannelValue(x.channel_num, Integer.parseInt(x.channel_value), writename, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                        alldata.clear();
                        channellist.clear();
                        getdatahandler.postDelayed(getdatarun, 50);
                    } else {
                        Toast.makeText(rootcontext, "channel value out of range", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(rootcontext, "you need to add ‘.’ in the edittext", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };


    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            if(thisroomradio.size()>0){thisroomradio.clear();}
            /*******************获取sub&dev id******************/
            List<Savemusic> musicx= MainActivity.mgr.querymusic();
            List<Savemusic> musicx2=new ArrayList<Savemusic>();
            for(int i=0;i<musicx.size();i++){
                if(musicx.get(i).room_id== FounctionActivity.roomidfc&&musicx.get(i).music_id==1){
                    musicx2.add(musicx.get(i));
                }
            }
            if (musicx2.size() > 0) {
                thismusic = musicx2.get(0);
                alldata=MainActivity.mgr.queryradio();
                for(int t=0;t<alldata.size();t++){
                    if(FounctionActivity.roomidfc==alldata.get(t).room_id){
                        thisroomradio.add(alldata.get(t));
                    }
                }
                if(thisroomradio.size()>0){
                    for(int i=0;i<thisroomradio.size();i++){
                        HashMap<String,String> chan=new HashMap<String,String>();
                        chan.put("channel",String.valueOf(alldata.get(i).channel_num));
                        chan.put("channelvalue",alldata.get(i).channel_value);
                        //chan.put("channelname",alldata.get(i).channel_name);
                        chan.put("channelname", "Channel " + (alldata.get(i).channel_num) + ": " + alldata.get(i).channel_name);
                        channellist.add(chan);
                    }
                    radioadapter=new MusicRadioChannelAdapter(rootcontext,channellist);
                    radiochannellist.setAdapter(radioadapter);
                }else{
                    radioadapter=new MusicRadioChannelAdapter(rootcontext,channellist);
                    radiochannellist.setAdapter(radioadapter);
                    getChannelValue();
                }
                getVoiceValue();
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

    int channel=1;
    Runnable getchannelrun=new Runnable() {
        @Override
        public void run() {
            rc.GetChannelValue(channel, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
            if(channel==26){
                channel=0;
                getchannel.removeCallbacks(getchannelrun);
                progress.dismiss();
                getdatahandler.postDelayed(getdatarun, 50);
                Toast.makeText(rootcontext, "update succeed", Toast.LENGTH_SHORT).show();
            }else{
                getchannel.postDelayed(getchannelrun,500);
            }
        }
    };

    public void getVoiceValue(){
        rc.GetMusicState((byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    }
    public void getChannelValue(){

        progress.show();
        getchannel.postDelayed(getchannelrun, 100);
    }
    public void SwitchtoRadio(){
        rc.SwitchtoRadio((byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    }
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
    public void setDialogdismiss(boolean result,DialogInterface dialog){
        try
        {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing" );
            field.setAccessible( true );
// 将mShowing变量设为false，表示对话框已关闭
            field.set(dialog, result );
            dialog.dismiss();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public String channelvalue(byte st,byte sd){
        String result="";
        int ff=((st&0xff)<<8)+(sd&0xff);
        result=String.valueOf(ff);
        return result;
    }
    public String edittochannelvalue(String value){
        String result="";
        String get1=value.substring(0,value.lastIndexOf("."));
        String get2=value.substring(value.lastIndexOf(".")+1,value.length());
        result=get1+get2;
        return result;
    }
    public String channelvaluetoedit(String value){
        String result="";
        String get1=value.substring(0,value.length()-1);
        String get2=value.substring(value.length()-1,value.length());
        result=get1+"."+get2;
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

    public void receiveddata(byte[] data){
        int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);
        if((data[17]==(byte)thismusic.subnetID)&&(data[18]==(byte)thismusic.deviceID)) {
            switch (x) {
                case 0xe141:
                        if((data[26] & 0xff)==channel) {
                            HashMap<String, String> chan = new HashMap<String, String>();
                            chan.put("channel", String.valueOf(data[26] & 0xff));
                            chan.put("channelvalue", channelvalue(data[27], data[28]));
                            byte[] name = new byte[40];
                            for (int i = 0; i < 40; i++) {
                                name[i] = data[29 + i];
                            }
                            chan.put("channelname", "Channel " + (data[26] & 0xff) + ": " + bytetostring(name, "UnicodeLittleUnmarked"));
                            if (!channellist.contains(chan)) {
                                channellist.add(chan);
                                radioadapter.addDevice(chan);
                                radioadapter.notifyDataSetChanged();
                                Saveradio db=new Saveradio();
                                db.room_id=FounctionActivity.roomidfc;
                                db.channel_num=data[26] & 0xff;
                                db.channel_value=channelvalue(data[27],data[28]);
                                db.channel_name=bytetostring(name, "UnicodeLittleUnmarked");
                                MainActivity.mgr.addradio(db);
                                channel++;
                                progress.setProgress(4*channel);
                            }
                        }
                    break;
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
}
