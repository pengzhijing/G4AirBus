package com.shima.smartbushome.selflayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MusicAlbumAdapter;
import com.shima.smartbushome.assist.Adapter.MusicFolderAdapter;
import com.shima.smartbushome.assist.Adapter.MusicOptionAdapter;
import com.shima.smartbushome.assist.Adapter.MusicSongAdapter;
import com.shima.smartbushome.assist.AtoZlist.AtoZAdapter;
import com.shima.smartbushome.assist.AtoZlist.CharacterParser;
import com.shima.smartbushome.assist.AtoZlist.PinyinComparator;
import com.shima.smartbushome.assist.AtoZlist.SideBar;
import com.shima.smartbushome.assist.AtoZlist.SortModel;
import com.shima.smartbushome.assist.MusicNotification;
import com.shima.smartbushome.assist.MusicNotifyReceiver;
import com.shima.smartbushome.assist.rollingTextview;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.database.Savesong;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_view.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class MusicLayout2 extends RelativeLayout implements View.OnClickListener {

    /*------------view-------------*/
    View view;
    SeekBar sb_setvoice;
    //TextView tv_voicevalue;
    rollingTextview tv_songname;
    Button bt_back,bt_next,bt_voice,bt_playmode,bt_play;
    CheckBox cb_like;
    private ProgressDialog progress;
    GridView menugridview;
    ListView songlistview;
    SideBar sideBar;
    TextView dialog;
    String songmaxtime;
    LayoutInflater musicinflater;
    Context rootcontext;
    int foroldvoice=0;
    /*-----------------------*/

    musiccontrol mc;//sent command
    int recalbumno=0,recsongno=0;//get selectsong when reflash
    int adapternum=0,albumno=1,songpackno=1,updatestep=0
            ,bigpackagesmax=0,currentbigpackages=1;//item when updating songlist int udp zaudio
    boolean doing=true,setlikestate=false,setvoicestate=false;//,ifonlyrec=false;//doing for recived data;like&voice for not curse
    //data change in listener;ifonlyrec for resent command in refalsh ui
    boolean listcontrol=false;
    int playmodecount=0,playstate=0;
    /*------------handler-------------*/
    Handler getdatahandler=new Handler();
    Handler writehandler=new Handler();
    Handler receivehandler=new Handler();
    Handler reflashhandler=new Handler();
    /*-----------command-----------*/
    public int Music_Source=1,PlayModeChange=2,AlbumorRadioControl=3,
            PlayControl=4,VoluneControl=5,ControlSpecSong=6;

    /*----------update song list in udp value---------*/
    Savemusic thismusic=new Savemusic();
    Savesong selectedsong=new Savesong();
    List<Savesong> data=new ArrayList<Savesong>();
    //List<String> menulist= new ArrayList<String>(){{add("Album");add("All Songs");add("FAVORITE");add("Theme");add("Update List");add("File Managerment");}};
    List<String> menulist= new ArrayList<String>(){{add("Album");add("All Songs");add("FAVORITE");add("Update List");add("File Managerment");}};
    List<String> albumlist= new ArrayList<String>();//save data in updating
    List<Savesong> savesongdata=new ArrayList<Savesong>();//for write to database
    List<HashMap<String,Integer>> songnumhash=new ArrayList<HashMap<String,Integer>>();//save data in updating
    List<HashMap<Integer,String>> songdetailhash=new ArrayList<HashMap<Integer,String>>();//sace data in updating

    /*------------adapters---------*/
    MusicOptionAdapter menuadapter;
    MusicSongAdapter likesongadapter;
    MusicFolderAdapter filefolderadapter,foldersongadapter;
    MusicSongAdapter currentSongadapter;
    List<MusicSongAdapter> songadapter=new ArrayList<MusicSongAdapter>();
    AtoZAdapter allsongadapter;

    /*----------a to z adapter--------*/
    // 汉字转换成拼音的类
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    //根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;

    /*------------reflash ui变量----------*/
    boolean reflashuienable=false;
    /*-------------------------------------------------------*/
    public MusicLayout2(Context context) {
        super(context);
        init(context);
    }

    public MusicLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicLayout2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
         view = View.inflate(context, R.layout.music_layout, this);
        musicinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootcontext=context;
        mc=new musiccontrol();
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sb_setvoice=(SeekBar)view.findViewById(R.id.seekBar2);
        dialog=(TextView)view.findViewById(R.id.dialog);
        sideBar= (SideBar)view. findViewById(R.id.sidrbar);
        tv_songname=(rollingTextview)view.findViewById(R.id.name);

        sideBar.setTextView(dialog);
       // tv_voicevalue=(TextView)view.findViewById(R.id.voicevalue);
        bt_back=(Button)view.findViewById(R.id.back);
        bt_next=(Button)view.findViewById(R.id.next);
        bt_voice=(Button)view.findViewById(R.id.voice);
        bt_playmode=(Button)view.findViewById(R.id.playmode);
        bt_play=(Button)view.findViewById(R.id.play);
        cb_like=(CheckBox)view.findViewById(R.id.like);
        menugridview=(GridView) view.findViewById(R.id.gridView);
        songlistview=(ListView) view.findViewById(R.id.listView);
        bt_back.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_voice.setOnClickListener(this);
        bt_playmode.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        cb_like.setOnCheckedChangeListener(likeclick);

        sb_setvoice.setOnSeekBarChangeListener(voicechange);
        menugridview.setOnItemClickListener(menuclick);
        songlistview.setOnItemClickListener(songclick);
        songlistview.setOnItemLongClickListener(folderlongclick);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(righttouch);
        menuadapter = new MusicOptionAdapter(context,menulist);

        menugridview.setAdapter(menuadapter);
        getdatahandler.postDelayed(getdatarun,70);
    }

    public void setcontent(Savemusic sm){
        thismusic=sm;
    }
    public void setsong(Savesong song){
        tv_songname.setText(song.song_name);
        if(song.like==1){
            setlikestate=true;
            cb_like.setChecked(true);
            setlikestate=false;
        }else{
            setlikestate=true;
            cb_like.setChecked(false);
            setlikestate=false;
        }
    }

    public void setVoiceValue(int value){
        setvoicestate=true;
        sb_setvoice.setProgress(value);
       // tv_voicevalue.setText(value + "%");
        setvoicestate=false;
    }

    public void SwitchtoMusicSD(){
        mc.SwitchtoMusicSD((byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    }

    boolean setabp=false,readabd=false,readsongbp=false,readsongdata=false,writesongdata=false;
    public void setAlbumBigPackages(int qty){
        if(!setabp){
            if(qty>0){
                bigpackagesmax=qty;
                if(progress!=null){
                    progress.setProgress(10);
                }
                currentbigpackages=1;
                updatestep=2;
                setabp=true;
                //getdatahandler.postDelayed(updatarun, 70);
            }else{
                updatestep=0;
                progress.setProgress(100);
                progress.dismiss();
            }
        }
    }

    public void readalbumdata(byte[] data){
        if(!readabd){
            if(currentbigpackages==(data[28]&0xff)){
                if(currentbigpackages<=bigpackagesmax){
                    int thisalbumqty=(data[29]&0xff);
                    int olddata=0;
                    for(int i=0;i<thisalbumqty;i++){
                        byte[] name=new byte[data[31+olddata]];
                        for(int t=0;t<data[31+olddata];t++){
                            name[t]=data[32+olddata+t];
                        }
                        albumlist.add(bytetostring(name,"Unicode").replace(".PLS",""));
                        olddata+=name.length+2;
                    }
                    if(currentbigpackages==bigpackagesmax){
                        for(int i=0;i<albumlist.size();i++){
                            if(albumlist.get(i).equals("SPECIAL")){
                                albumlist.remove(i);
                            }
                        }
                        currentbigpackages++;
                        if(progress!=null){
                            progress.setProgress(30);
                        }
                        updatestep=3;
                        albumno=1;
                        readabd=true;
                        //getdatahandler.postDelayed(updatarun,10);
                    }else{
                        updatestep=2;
                        currentbigpackages++;
                        //getdatahandler.postDelayed(updatarun, 70);
                    }
                }
            }
        }

    }

    public void readSongBigPackages(byte[] data){
        if(!readsongbp){
            if((data[26]&0xff)==albumno) {
                HashMap<String, Integer> songnum = new HashMap<String, Integer>();
                songnum.put("albumno", (int) (data[26] & 0xff));
                songnum.put("songbigpackage", (int) (data[27] & 0xff));
                songnumhash.add(songnum);
                if (albumno < albumlist.size()) {
                    albumno++;
                    //getdatahandler.postDelayed(updatarun, 80);
                } else {
                    progress.setProgress(50);

                    for(int i=0;i<albumlist.size();i++){
                        if(songnumhash.get(i).get("songbigpackage")!=0){
                            albumno=i+1;
                            break;
                        }
                    }
                    updatestep=4;
                    songpackno = 1;
                    readsongbp=true;
                    //getdatahandler.postDelayed(updatarun,20);
                }
            }
        }
    }

    public void readSongdata(byte[] data){
        if(!readsongdata){
            if(albumno==(data[28]&0xff)) {
                if(songpackno==(data[29]&0xff)) {

                    int thissongqty = (data[30]&0xff);//Total QTY of songs for current package
                    int olddata = 0;
                    for (int i = 0; i < thissongqty; i++) {
                        byte[] name = new byte[data[33 + olddata]];
                        HashMap<Integer, String> songname = new HashMap<Integer, String>();
                        for (int t = 0; t < name.length; t++) {
                            name[t] = data[34 + olddata + t];
                        }
                        int songnum=(data[31+olddata]<<8)+(data[32+olddata]&0xff);
                        songname.put(1,String.valueOf(albumno));
                        songname.put(2,String.valueOf(songnum));
                        songname.put(3, bytetostring(name, "Unicode"));
                        songdetailhash.add(songname);
                        olddata += name.length + 3;
                    }

                    //完成一个songpackage,如果多于一个package则转下一个package
                    if ((albumno-1)>=songnumhash.size()){
                     return;
                    }
                    if(songpackno==songnumhash.get(albumno-1).get("songbigpackage")){
                        //最后一个album完成,否则切换到下一个album
                        if(albumno==songnumhash.size()){
                            updatestep=5;
                            progress.setProgress(70);
                            readsongdata=true;
                            //getdatahandler.postDelayed(updatarun, 10);
                        }else
                        {
                            albumno++;
                            for(int i=0;i<albumlist.size();i++){
                                if(songnumhash.get(albumno-1).get("songbigpackage")!=0){
                                    songpackno=1;
                                    updatestep=4;
                                    progress.setProgress(50 + ((albumno * 20) / albumlist.size()));
                                   // getdatahandler.postDelayed(updatarun, 100);
                                    break;
                                }else{
                                    albumno++;
                                    if(albumno>=albumlist.size()){
                                        updatestep=5;
                                        progress.setProgress(70);
                                        readsongdata=true;
                                        //getdatahandler.postDelayed(updatarun, 10);
                                        break;
                                    }
                                }
                            }
                        }
                    }else{
                        songpackno++;
                        updatestep=4;
                        //getdatahandler.postDelayed(updatarun, 140);
                    }
                }
            }

        }

    }

    public void getmusicstate(byte[] datavalue){
        int a=(int)((datavalue[25]<<8))+(int)(datavalue[26]&0xff);
        int b=(int)(datavalue[36]&0xff);
        if(a==0x235a){
            if(!getreflashvoice){
                getreflashvoice=true;
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
                setVoiceValue(value);
            }

        }else if(a==0x2353){
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
                        if(getreflashvoice&&getreflashablum){
                            if(!getreflashsong){
                                getreflashsong=true;
                                try{
                                    byte[] song = getpieceofbyte(datavalue,1);
                                    String str2 = bytetostring(song, "Unicode");
                                    int value2=Integer.parseInt(str2);
                                    recsongno=value2;
                                    //reflashstep=true;
                                    receivehandler.postDelayed(getselectmusic,20);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
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
        int length=0;
        try{
            length=endbyte-startbyte;
        }catch (Exception e){
            e.printStackTrace();
        }
            byte[] result=new byte[length];
        try{
            for(int i=0;i<length;i++){
                result[i]=data[startbyte+i];
            }
        }catch (Exception e){
            e.printStackTrace();
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

    public void setplaybuttonstate(String str){
        switch (str){
            case "play":
                bt_play.setBackgroundResource(R.drawable.pause);
                playstate=1;
                mc.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
            case "pause":
                bt_play.setBackgroundResource(R.drawable.play);
                playstate=0;
                mc.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                break;
        }
    }
    private  SideBar.OnTouchingLetterChangedListener righttouch=new SideBar.OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            // 该字母首次出现的位置
            int position = allsongadapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                songlistview.setSelection(position);
            }
        }
    };


    private CheckBox.OnCheckedChangeListener likeclick=new CheckBox.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            if(tv_songname.getText().toString().equals("song name")){
                Toast.makeText(getContext(), "you need to select one song", Toast.LENGTH_SHORT).show();
            }else{
                if(!setlikestate){
                    if (isChecked) {
                        selectedsong.like=1;
                        MainActivity.mgr.updatesong(selectedsong);
                        Toast.makeText(getContext(), "ADDED", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedsong.like=0;
                        MainActivity.mgr.updatesong(selectedsong);
                        Toast.makeText(getContext(), "DELETED", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    //adapternum:0:menu;  1:album menu;   2:album-song;    3:all song;    4:like song
    public void onClick(View v){
        switch(v.getId()){
            case R.id.back:
                   switch (adapternum){
                       case 0:
                       case 1:
                           mc.MusicControl((byte)PlayControl,(byte)1,(byte)0,(byte)0,(byte)thismusic.subnetID,(byte)thismusic.deviceID,MainActivity.mydupsocket);
                           reflashui();
                           break;
                       case 2:
                           currentSongadapter.setNextorBack("back");
                           songlistview.setSelection(currentSongadapter.getSelectItem());
                           currentSongadapter.notifyDataSetInvalidated();
                           String name=currentSongadapter.getselectSongname();
                           for(int i=0;i<data.size();i++){
                               if((data.get(i).song_name).equals(name)){
                                   selectedsong=data.get(i);
                                   setsong(selectedsong);
                                   break;
                               }
                           }
                           mc.MusicControl((byte)PlayControl,(byte)1,(byte)0,(byte)0,(byte)thismusic.subnetID,(byte)thismusic.deviceID,MainActivity.mydupsocket);
                           break;
                       case 3:
                           allsongadapter.setNextorBack("back");
                           songlistview.setSelection(allsongadapter.getSelectItem());
                           allsongadapter.notifyDataSetInvalidated();
                           String name3=allsongadapter.getselectSongname();
                           for(int i=0;i<data.size();i++){
                               if((data.get(i).song_name).equals(name3)){
                                   selectedsong=data.get(i);
                                   setsong(selectedsong);
                                   break;
                               }
                           }
                           palyselectsong();
                           break;
                       case 4:
                           currentSongadapter.setNextorBack("back");
                           songlistview.setSelection(currentSongadapter.getSelectItem());
                           currentSongadapter.notifyDataSetInvalidated();
                           String name4=currentSongadapter.getselectSongname();
                           for(int i=0;i<data.size();i++){
                               if((data.get(i).song_name).equals(name4)){
                                   selectedsong=data.get(i);
                                   setsong(selectedsong);
                                   break;
                               }
                           }
                           palyselectsong();
                           break;
                           default:break;
                   }
                break;
            case R.id.next:
                switch (adapternum){
                    case 0:
                    case 1:
                        mc.MusicControl((byte)PlayControl,(byte)2,(byte)0,(byte)0,(byte)thismusic.subnetID,(byte)thismusic.deviceID,MainActivity.mydupsocket);
                        reflashui();
                        break;
                    case 2:
                        currentSongadapter.setNextorBack("next");
                        songlistview.setSelection(currentSongadapter.getSelectItem());
                        currentSongadapter.notifyDataSetInvalidated();
                        String name=currentSongadapter.getselectSongname();
                        for(int i=0;i<data.size();i++){
                            if((data.get(i).song_name).equals(name)){
                                selectedsong=data.get(i);
                                setsong(selectedsong);
                                break;
                            }
                        }
                        mc.MusicControl((byte)PlayControl,(byte)2,(byte)0,(byte)0,(byte)thismusic.subnetID,(byte)thismusic.deviceID,MainActivity.mydupsocket);
                        break;
                    case 3:
                        allsongadapter.setNextorBack("next");
                        songlistview.setSelection(allsongadapter.getSelectItem());
                        allsongadapter.notifyDataSetInvalidated();
                        String name3=allsongadapter.getselectSongname();
                        for(int i=0;i<data.size();i++){
                            if((data.get(i).song_name).equals(name3)){
                                selectedsong=data.get(i);
                                setsong(selectedsong);
                                break;
                            }
                        }
                        palyselectsong();
                        break;
                    case 4:
                        currentSongadapter.setNextorBack("next");
                        songlistview.setSelection(currentSongadapter.getSelectItem());
                        currentSongadapter.notifyDataSetInvalidated();
                        String name4=currentSongadapter.getselectSongname();
                        for(int i=0;i<data.size();i++){
                            if((data.get(i).song_name).equals(name4)){
                                selectedsong=data.get(i);
                                setsong(selectedsong);
                                break;
                            }
                        }
                        palyselectsong();
                        break;
                    default:break;
                }
                break;
            case R.id.voice:
                if(sb_setvoice.getProgress()>0&&sb_setvoice.getProgress()<=100){
                    foroldvoice=sb_setvoice.getProgress();
                    sb_setvoice.setProgress(0);
                    mc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * 0) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    bt_voice.setBackgroundResource(R.drawable.btn_voice_none);
                }else if(sb_setvoice.getProgress()==0){
                    if(foroldvoice==0){
                        sb_setvoice.setProgress(50);
                        mc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * 50) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    }else{
                        sb_setvoice.setProgress(foroldvoice);
                        mc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * foroldvoice) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    }
                    bt_voice.setBackgroundResource(R.drawable.btn_voice_normal);
                }
                break;
            case R.id.playmode:
                switch (playmodecount){
                    case 0:
                        bt_playmode.setBackgroundResource(R.drawable.songplaymode2);
                        mc.MusicControl((byte) PlayModeChange, (byte) 2, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID, MainActivity.mydupsocket);
                        playmodecount=1;
                        Toast.makeText(getContext(), "repeat 1 song", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        bt_playmode.setBackgroundResource(R.drawable.songplaymode3);
                        mc.MusicControl((byte) PlayModeChange, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID, MainActivity.mydupsocket);
                        playmodecount=2;
                        Toast.makeText(getContext(), "repeat 1 album", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        bt_playmode.setBackgroundResource(R.drawable.songplaymode4);
                        mc.MusicControl((byte) PlayModeChange, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID, MainActivity.mydupsocket);
                        playmodecount=3;
                        Toast.makeText(getContext(), "repeat all album", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        bt_playmode.setBackgroundResource(R.drawable.songplaymode1);
                        mc.MusicControl((byte) PlayModeChange, (byte) 1, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID, MainActivity.mydupsocket);
                        playmodecount=0;
                        Toast.makeText(getContext(), "not repeat", Toast.LENGTH_SHORT).show();
                        break;
                }

                break;
            case R.id.play:
                switch (playstate){
                    case 0:
                        setplaybuttonstate("play");
                        break;
                    case 1:
                        setplaybuttonstate("pause");
                        break;
                }
                break;
                default:break;
        }
    }
    AlertView iconalter;
    int bg_position=0;
    private AdapterView.OnItemClickListener menuclick=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(adapternum==0) {
                switch (position) {
                    case 0:
                       // 进入album页面
                        MusicAlbumAdapter albumadapter = new MusicAlbumAdapter(getContext(),albumlist);
                        menugridview.setAdapter(albumadapter);
                        adapternum=1;
                        listcontrol=false;
                        break;
                    case 1://进入allsonglist页面
                        adapternum=3;
                        menugridview.setVisibility(GONE);
                        songlistview.setVisibility(VISIBLE);
                        sideBar.setVisibility(VISIBLE);
                        songlistview.setAdapter(allsongadapter);
                        listcontrol=true;
                        break;
                    case 2://进入likesonglist页面
                        adapternum=4;
                        List<String> likesonglist = new ArrayList<String>();
                        for(int i=0;i<data.size();i++){
                            if(data.get(i).like==1){
                                likesonglist.add(data.get(i).song_name);
                            }
                        }
                        likesongadapter=new MusicSongAdapter(getContext(),likesonglist);
                        menugridview.setVisibility(GONE);
                        songlistview.setVisibility(VISIBLE);
                        songlistview.setAdapter(likesongadapter);
                        currentSongadapter=likesongadapter;
                        listcontrol=true;
                        break;
                 /*   case 3://主题设置
                        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        iconalter = new AlertView("BG Selection", null, "CANCEL", new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                                selectbg);
                        View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                        GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                        icongrid.setAdapter(new MusicBGAdapter(rootcontext));
                        icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                bg_position=position+1;
                                Toast.makeText(getContext(), "select BG "+(bg_position), Toast.LENGTH_SHORT).show();
                                //iconalter.dismiss();
                            }
                        });
                        iconalter.addExtView(selfviewx);
                        iconalter.show();
                        break;*/
                    case 3://进入setting页面
                        showPopupMenu(view);
                        listcontrol=true;
                        break;
                    case 4://todo 进入文件管理，可以删除歌曲
                        filefolderadapter = new MusicFolderAdapter(getContext(),albumlist,1);
                        menugridview.setVisibility(GONE);
                        songlistview.setVisibility(VISIBLE);
                        songlistview.setAdapter(filefolderadapter);
                        adapternum=5;
                        listcontrol=false;
                       // Toast.makeText(rootcontext, "still developing", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }else if(adapternum==1){ //选择album，进入album-song页面
                menugridview.setVisibility(GONE);
                songlistview.setVisibility(VISIBLE);
                adapternum=2;
                songlistview.setAdapter(songadapter.get(position));
                currentSongadapter=songadapter.get(position);
                listcontrol=true;
               // mc.MusicControl((byte)AlbumorRadioControl,(byte)3,(byte)(selectedAlbum),(byte)0,(byte)thismusic.subnetID,(byte)thismusic.deviceID);
            }
        }
    };


    public com.bigkoo.alertview.OnItemClickListener selectbg=new com.bigkoo.alertview.OnItemClickListener() {
        public void onItemClick(Object o, int position) {
            switch (position){
                case 0:
                    String bgname="music_bg"+bg_position;
                    savecolorInfo(getContext(),bgname);
                    break;
            }
        }
    };
    public void savecolorInfo(Context context,String bgname){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("pagesbgcolor", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("musicbg", bgname);
        //提交
        editor.commit();
    }
    //adapternum:0:menu;  1:album menu;   2:album-song;    3:all song;    4:like song  5:folder 6:folder-song
    private AdapterView.OnItemClickListener songclick=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (adapternum){
                case 0:
                case 1:
                case 2:
                case 4:
                    currentSongadapter.setSelectItem(position);
                    currentSongadapter.notifyDataSetInvalidated();
                    for (int i = 0; i < data.size(); i++) {
                        // int dot=data.get(i).song_name.length()-4;
                        if ((currentSongadapter.getselectSongname()).equals(data.get(i).song_name)) {
                            selectedsong = data.get(i);
                        }
                    }
                    byte[] songbyte = new byte[2];
                    songbyte[0] = (byte) ((selectedsong.song_num &0xff00)>>8);
                    songbyte[1] = (byte) ((selectedsong.song_num ) - (selectedsong.song_num&0xff00));
                    mc.MusicControl((byte) ControlSpecSong, (byte) (selectedsong.album_num), songbyte[0], songbyte[1], (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    setsong(selectedsong);
                    setplaybuttonstate("play");
                    break;
                case 3:
                    allsongadapter.setSelectItem(position);
                    allsongadapter.notifyDataSetInvalidated();
                    //Toast.makeText(getContext(), allsongadapter.getselectSongname(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < data.size(); i++) {
                        // int dot=data.get(i).song_name.length()-4;
                        if ((allsongadapter.getselectSongname()).equals(data.get(i).song_name)) {
                            selectedsong = data.get(i);
                        }
                    }
                    byte[] songbyte3 = new byte[2];
                    songbyte3[0] = (byte) ((selectedsong.song_num &0xff00)>>8);
                    songbyte3[1] = (byte) ((selectedsong.song_num ) - (selectedsong.song_num&0xff00));
                    mc.MusicControl((byte) ControlSpecSong, (byte) (selectedsong.album_num), songbyte3[0], songbyte3[1], (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    setsong(selectedsong);
                    setplaybuttonstate("play");
                    break;
                case 5:
                    foldersongadapter = new MusicFolderAdapter(getContext(),songadapter.get(position).getsonglist(),2);
                    songlistview.setAdapter(foldersongadapter);
                    listcontrol=true;
                    adapternum=6;
                    break;
                case 6:
                    break;
            }

        }
    };

    private AdapterView.OnItemLongClickListener folderlongclick=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            switch (adapternum){
                case 5:
                    showPopupfolderMenu(view);
                    break;
                case 6:
                    showPopupmp3Menu(view);
                    break;
            }
            return true;
        }
    };
    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }
    public void musicbackpress(){
        if(adapternum==0){
           // ((Activity)getContext()).finish();
            Music.finishmusic();
            listcontrol=false;
        }else if(adapternum==1){//menu-album
            adapternum=0;
            menugridview.setAdapter(menuadapter);
            listcontrol=false;
        }else if(adapternum==2){//menu-ablum-song
            adapternum=1;
            menugridview.setVisibility(VISIBLE);
            songlistview.setVisibility(GONE);
            MusicAlbumAdapter albumadapter = new MusicAlbumAdapter(getContext(),albumlist);
            menugridview.setAdapter(albumadapter);
            listcontrol=false;
        }else if(adapternum==3){//menu-allsong&like
            adapternum=0;
            menugridview.setVisibility(VISIBLE);
            songlistview.setVisibility(GONE);
            sideBar.setVisibility(GONE);
            menugridview.setAdapter(menuadapter);
            listcontrol=false;
        }else if(adapternum==4){
            adapternum=0;
            menugridview.setVisibility(VISIBLE);
            songlistview.setVisibility(GONE);
            menugridview.setAdapter(menuadapter);
            listcontrol=false;
        }else if(adapternum==5){//folder
            adapternum=0;
            menugridview.setVisibility(VISIBLE);
            songlistview.setVisibility(GONE);
            menugridview.setAdapter(menuadapter);
            listcontrol=false;
        }else if(adapternum==6){//folder-song
            MusicFolderAdapter folderadapter = new MusicFolderAdapter(getContext(),albumlist,1);
            songlistview.setAdapter(folderadapter);
            adapternum=5;
            listcontrol=false;
        }
    }
    /**********音量控制**********/
    private SeekBar.OnSeekBarChangeListener voicechange = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            listcontrol=true;
            // tv_voicevalue.setText(seekBar.getProgress() + "%");
            mc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79-((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG,"onStartTrackingTouch");

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(progress==0){
                bt_voice.setBackgroundResource(R.drawable.btn_voice_none);
            } else {
                bt_voice.setBackgroundResource(R.drawable.btn_voice_normal);
            }
            if(!setvoicestate){
               // tv_voicevalue.setText(progress + "%");
              //  mc.MusicControl((byte) VoluneControl, (byte) 1, (byte) 3, (byte) (79 - ((79 * seekBar.getProgress()) / 100)), (byte) thismusic.subnetID, (byte) thismusic.deviceID);
            }
        }
    };
    public com.bigkoo.alertview.OnItemClickListener updateclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }else if(position==0){
                if (albumlist.size() != 0) {
                    albumlist.clear();
                }
                if (songnumhash.size() != 0) {
                    songnumhash.clear();
                }
                if (songdetailhash.size() != 0) {
                    songdetailhash.clear();
                }
                albumno = 1;
                songpackno = 1;
                updatestep = 0;
                bigpackagesmax = 0;
                currentbigpackages = 1;
                setabp = false;
                readabd = false;
                readsongbp = false;
                readsongdata = false;
                writesongdata = false;
                progress = new ProgressDialog(getContext());
                progress.setCancelable(true);
                progress.setCanceledOnTouchOutside(false);
                progress.setMessage("Getting Music...");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.show();
                if (data.size() != 0) {
                    MainActivity.mgr.deletesong("song", FounctionActivity.roomidfc);
                }
                updatestep = 1;
                getdatahandler.postDelayed(updatarun, 10);
            }
        }
    };
    public void music_setting(){
        AlertView mAlertView = new AlertView("Update music list", "Are you sure to update? It will take a few minutes to finish",
                "CANCEL", new String[]{"YES"}, null, rootcontext, AlertView.Style.Alert, updateclick);
        mAlertView .setCancelable(false);
        mAlertView .show();
    }
    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.music_update_setting, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.music_updatelist:
                        music_setting();
                        break;
                    case R.id.music_updatezaudiolist:
                        mc.updateZaudiolist((byte) thismusic.subnetID, (byte) thismusic.deviceID, MainActivity.mydupsocket);
                        Toast.makeText(rootcontext, "updating,please wait for a few sec", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;

            }
        });

        popupMenu.show();
    }
    //todo popup
    private void showPopupfolderMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.music_folder_setting, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.music_folder_delete:

                        break;
                }
                return false;

            }
        });

        popupMenu.show();
    }
    private void showPopupmp3Menu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.music_mp3file_setting, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                   /* case R.id.music_mp3_add:
                        FolderFilePicker picker = new FolderFilePicker(rootcontext,
                                new FolderFilePicker.PickPathEvent() {
                                    @Override
                                    public void onPickEvent(String resultPath) {
                                        String mPath = resultPath;
                                        Toast.makeText(rootcontext, mPath,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }, ".MP3",".mp3");
                        picker.show();
                        break;*/
                    case R.id.music_mp3_delete:

                        break;
                }
                return false;

            }
        });

        popupMenu.show();
    }
    /*************************获取数据库数据********************/
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            List<Savesong> alldata=MainActivity.mgr.querysong();
            if(data.size()>0){data.clear();}
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id==FounctionActivity.roomidfc){
                    data.add(alldata.get(i));//get all song data in this room
                }
            }
            if(data.size()>0) {
                if (songadapter.size() > 0) {
                    songadapter.clear();
                }
                if (albumlist.size() > 0) {
                    albumlist.clear();
                }

                int albumsize = data.get(data.size() - 1).album_num;//最后一首歌的album num
                int alno = 1;
                for (int i = 0; i < albumsize; i++) {
                    List<String> songlist = new ArrayList<String>();
                    for (int t = 0; t < data.size(); t++) {
                        if (data.get(t).album_num == i + 1) {
                            songlist.add(data.get(t).song_name);
                        }
                        if (data.get(t).album_num == alno) {
                            albumlist.add(data.get(t).album_name);
                            alno++;
                        }
                    }
                    MusicSongAdapter thisadapter = new MusicSongAdapter(getContext(), songlist);
                    songadapter.add(thisadapter);
                    //songlist.clear();
                }
            }
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
                /*********************get all song adapter***************************/
                String[] allsong=new String[data.size()];
                for(int i=0;i<data.size();i++){
                    allsong[i]=data.get(i).song_name;
                }
                SourceDateList = filledData(allsong);
                // 根据a-z进行排序源数据
                Collections.sort(SourceDateList, pinyinComparator);
                allsongadapter = new AtoZAdapter(getContext(), SourceDateList);
                /****************************************************************/

                reflashui();
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
            initrollingtext();
        }
    };

    public void initrollingtext(){

    }
    public void palyselectsong(){
        byte[] songbytevalue = new byte[2];
        songbytevalue[0] = (byte) (selectedsong.song_num >> 8);
        songbytevalue[1] = (byte) ((selectedsong.song_num & 0xff) - (selectedsong.song_num >> 8));
        mc.MusicControl((byte) ControlSpecSong, (byte) (selectedsong.album_num), songbytevalue[0], songbytevalue[1], (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
    }

    Runnable getselectmusic=new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < data.size(); i++) {
                if ((data.get(i).album_num==recalbumno)&&(data.get(i).song_num==recsongno)) {
                    selectedsong = data.get(i);
                    setsong(selectedsong);
                    MusicNotifyReceiver.subnetID=thismusic.subnetID;
                    MusicNotifyReceiver.deviceID=thismusic.deviceID;
                   MusicNotification.sendResidentNoticeType0(MainActivity.maincontext, selectedsong.song_name);
                    reflashstep=true;
                    break;
                }
            }
            receivehandler.removeCallbacks(getselectmusic);
        }
    };
    boolean reflashstep=false,getreflashablum=false,getreflashsong=false,getreflashvoice=false;
    int reflashcount=0;
    Runnable reflashrun=new Runnable() {
        @Override
        public void run() {
            if(reflashstep){
                reflashuienable=false;
                reflashcount=0;
                //Toast.makeText(rootcontext, "out", Toast.LENGTH_SHORT).show();
                reflashhandler.removeCallbacks(reflashrun);
            }else{
                mc.GetMusicState((byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
               // Toast.makeText(rootcontext, "get music", Toast.LENGTH_SHORT).show();
                reflashhandler.postDelayed(reflashrun,300);
                reflashcount++;
                if(reflashcount>=1){
                    reflashstep=true;
                }
            }

        }
    };
    public void reflashui(){
        reflashuienable=true;
        reflashstep=false;
        getreflashablum=false;
        getreflashvoice=false;
        getreflashsong=false;
        //Toast.makeText(rootcontext, "in", Toast.LENGTH_SHORT).show();
        reflashhandler.postDelayed(reflashrun,80);
    }
   /******************************************从zaudio重新获取所有数据**********************************************/
   int waitfeedback=0;
    boolean feedback=false;
    Runnable updatarun = new Runnable() {
        @Override
        public void run() {
            waitfeedback++;
            if(waitfeedback>10){
                if(!feedback){
                    albumlist.clear();
                    songnumhash.clear();
                    songdetailhash.clear();
                    savesongdata.clear();
                    albumno=1;
                    songpackno=1;
                    updatestep=0;
                    bigpackagesmax=0;
                    currentbigpackages=1;
                    feedback=false;
                    waitfeedback=0;
                    progress.dismiss();
                    Toast.makeText(getContext(), "update fail", Toast.LENGTH_SHORT).show();
                    getdatahandler.removeCallbacks(updatarun);
                }
            }

            switch(updatestep){
                case 1:
                    mc.MusicReadAlbumQTY((byte) 1, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    getdatahandler.postDelayed(updatarun,500);
                    break;
                case 2:
                    feedback=true;
                    mc.MusicReadAlbum((byte) 1, (byte) currentbigpackages, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    getdatahandler.postDelayed(updatarun, 500);
                    break;
                case 3:
                    mc.MusicReadSongQTY((byte) 1, (byte) albumno, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    getdatahandler.postDelayed(updatarun, 500);
                    break;
                case 4:
                    mc.MusicReadSong((byte) 1, (byte) albumno, (byte) songpackno, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    getdatahandler.postDelayed(updatarun, 500);
                    break;
                case 5:
                    if(!writesongdata){
                        writesongdata=true;
                        progress.setProgress(80);
                        for (int a = 0; a < songdetailhash.size(); a++) {
                            Savesong singlesongdata = new Savesong();
                            singlesongdata.room_id = FounctionActivity.roomidfc;
                            singlesongdata.album_num =Integer.parseInt(songdetailhash.get(a).get(1)) ;
                            singlesongdata.album_name = albumlist.get(Integer.parseInt(songdetailhash.get(a).get(1))-1);
                            singlesongdata.song_num = Integer.parseInt(songdetailhash.get(a).get(2));
                            singlesongdata.song_name = songdetailhash.get(a).get(3);
                            singlesongdata.like=0;
                            singlesongdata.music_id=thismusic.music_id;
                            savesongdata.add(singlesongdata);
                        }
                        writenum = 0;
                        writehandler.postDelayed(writedata, 50);
                        getdatahandler.removeCallbacks(updatarun);
                    }

                    break;
            }
        }
    };
    int writenum=0;
    Runnable writedata =new Runnable() {
        @Override
        public void run() {
            if(writenum<savesongdata.size()){
            MainActivity.mgr.addsong(savesongdata.get(writenum));
            writenum++;
            progress.setProgress(80+(((writenum*20)/savesongdata.size())));
            writehandler.postDelayed(writedata, 100);
            }else{
                albumlist.clear();
                songnumhash.clear();
                songdetailhash.clear();
                savesongdata.clear();
                albumno=1;
                songpackno=1;
                updatestep=0;
                bigpackagesmax=0;
                currentbigpackages=1;
                feedback=false;
                waitfeedback=0;
                getdatahandler.postDelayed(getdatarun, 100);
                progress.dismiss();
                Toast.makeText(getContext(), "update succeed", Toast.LENGTH_SHORT).show();
                writehandler.removeCallbacks(writedata);
            }
        }
    };

    public void removetimer(){
        reflashhandler.removeCallbacks(reflashrun);
    }
    /***************************************接收到数据**********************************************/
    public void receiveddata(byte[] data){
        if(doing){
            doing=false;
            int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);
            if((data[17]==(byte)thismusic.subnetID)&&(data[18]==(byte)thismusic.deviceID)) {
                switch (x) {
                    case 0x2e1:
                        setAlbumBigPackages(data[26]&0xff);
                        break;
                    case 0x2e3:
                        readalbumdata(data);
                        break;
                    case 0x2e5:
                        readSongBigPackages(data);
                        break;
                    case 0x2e7:
                        readSongdata(data);
                        break;
                    case 0x192f:
                        if(listcontrol){
                            listcontrol=false;
                        }else{
                            if(reflashuienable){
                                try{
                                    getmusicstate(data);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                               /* reflashuienable=true;
                                reflashstep=false;
                                getreflashablum=false;
                                getreflashvoice=false;
                                getreflashsong=false;
                                try{
                                    getmusicstate(data);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }*/
                                //reflashui();
                                setCurrSongName(data);
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
            doing=true;
        }
    }
    /**********************************************************************************************/

    public void setCurrSongName(byte[] datavalue){
        int a=(int)((datavalue[25]<<8))+(int)(datavalue[26]&0xff);
        int b=(int)(datavalue[36]&0xff);
        if(a==0x2353){

                switch (b){
                    case 0x31:

                            byte[] album = getpieceofbyte(datavalue, 0);
                            String str = bytetostring(album, "Unicode");
                            int value = Integer.parseInt(str);
                            recalbumno = value;

                        break;
                    case 0x33:

                                try{
                                    byte[] song = getpieceofbyte(datavalue,1);
                                    String str2 = bytetostring(song, "Unicode");
                                    int value2=Integer.parseInt(str2);
                                    recsongno=value2;
                                    //reflashstep=true;
                                    receivehandler.postDelayed(getselectmusic,20);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }



                        break;
                    default:break;
                }

        }
    }

  }
