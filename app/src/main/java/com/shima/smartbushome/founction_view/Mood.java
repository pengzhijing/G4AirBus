package com.shima.smartbushome.founction_view;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MoodIconAdapter;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.database.Savemood;
import com.shima.smartbushome.database.Savemoodbutton;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.audio_incontrol;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.founction_command.moodcontrol;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_command.radiocontrol;
import com.shima.smartbushome.selflayout.MoodLayout;
import com.shima.smartbushome.udp.udp_socket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Mood extends Fragment {
    View view;
    LinearLayout moodcontainer;
    List<Savemoodbutton> roommoodbutton=new ArrayList<Savemoodbutton>();
    List<MoodLayout> moodlist=new ArrayList<MoodLayout>();
    MenuItem add,delete;
    Handler moodhandler=new Handler();
    Handler moodselecthandler=new Handler();
    Handler lighthandler=new Handler();
    Handler hvachandler=new Handler();
    Handler zaudiohandler=new Handler();
    Handler curtainhandler=new Handler();
    List<Savelight> lightdata=new ArrayList<Savelight>();
    List<Savehvac> hvacdata=new ArrayList<Savehvac>();
    List<Savemusic> musicdata=new ArrayList<Savemusic>();
    List<Savecurtain> curtaindata=new ArrayList<Savecurtain>();
    moodcontrol mmc;
    lightcontrol mlc;
    ACcontrol macc;
    musiccontrol mmuc;
    radiocontrol mrc;
    audio_incontrol maic;
    curtaincontrol mctc;

    boolean deletemood=false;
    ProgressDialog moodsaving;
    String iconrout="mood_icon1";//save icon 路径 when add mood
    String savecurtainstate="",savezaudiostate="";
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("mood_icon1");add("mood_icon2");add("mood_icon3");add("mood_icon4");
            add("mood_icon5");add("mood_icon6");add("mood_icon7");add("mood_icon8");
            add("mood_icon9");add("mood_icon10");
        }
    };

    //ac mode
    private static final byte const_mode_cool=0;
    private static final byte const_mode_heat=1;
    private static final byte const_mode_fan=2;
    private static final byte const_mode_auto=3;
    
    public Mood() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_mood, container, false);
        getActivity().setTitle("Mood");
        setHasOptionsMenu(true);
        initView();
/*
        SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("moodbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.moodout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        moodhandler.postDelayed(getmoodbuttonlist, 30);
        //背景图上移
        ImageView iv_layout= (ImageView) view.findViewById(R.id.iv_layout);
        setMargins(iv_layout,0,(-FounctionActivity.topHeight),0,0);


        return view;
    }



    //设置view的外边距
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


    public void initView(){
        moodcontainer=(LinearLayout)view.findViewById(R.id.moodview);
        moodsaving=new ProgressDialog(getActivity());
        moodsaving.setCancelable(true);
        moodsaving.setCanceledOnTouchOutside(false);
        moodsaving.setMessage("Saving Mood info...");
        moodsaving.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mmc=new moodcontrol();
        mlc=new lightcontrol();
        macc=new ACcontrol();
        mmuc=new musiccontrol();
        mrc=new radiocontrol();
        maic=new audio_incontrol();
        mctc=new curtaincontrol();
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }
    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        lighthandler.removeCallbacks(getlightstaterun);
        hvachandler.removeCallbacks(gethvacstaterun);
        zaudiohandler.removeCallbacks(getzaudiostaterun);
        curtainhandler.removeCallbacks(getcurtainstaterun);
        moodhandler.removeCallbacks(getmoodbuttonlist);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mood_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        add= menu.findItem(R.id.mood_add);
        delete= menu.findItem(R.id.mood_remove);
    }

    boolean getallstate=false;
    AlertView addingalter,iconalter,curtainalter,musicalter;
    ListView test;
    EditText moodename;
    TextView tv_cur_statue,tv_music_status;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mood_add:
                if(deletemood){
                    deletemood=false;
                    for(int i=0;i<moodlist.size();i++){
                        moodlist.get(i).setdeleteable(false);
                        moodlist.get(i).setDeletemode(false);
                    }
                    add.setTitle("ADD MOOD");
                    delete.setTitle("DELETE MOOD");
                    if(moodlist.size()>0){moodlist.clear();}
                }else{
                    addingalter = new AlertView("Add Mood", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfview= getActivity().getLayoutInflater().inflate(R.layout.mood_add_layout, null);
                    tv_cur_statue=(TextView)selfview.findViewById(R.id.tv_cur_status);
                    tv_music_status=(TextView)selfview.findViewById(R.id.tv_music_status);
                    test=(ListView)selfview.findViewById(R.id.listView5);
                    moodename=(EditText)selfview.findViewById(R.id.moodnamedeit);
                    final ImageView icon=(ImageView)selfview.findViewById(R.id.imageView5);
                    List<String> stringarray=new ArrayList<String>(){
                        {
                            add("Light");add("HVAC");add("Z-Audio");add("Curtain");
                        }
                    };
                    final ArrayAdapter<String> savelist=new ArrayAdapter<String>(getActivity(),
                            R.layout.simple_list_item_choice, stringarray);
                    test.setAdapter(savelist);
                    test.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 2) {
                                if (test.isItemChecked(2)) {
                                    final String[] mItems = {"ZAudio On", "ZAudio Off"};
                                    musicalter = new AlertView(null, null, "CANCEL", null, null, getActivity(), AlertView.Style.ActionSheet,
                                            itemclick);
                                    final ListView typelist = new ListView(getActivity());
                                    typelist.setAdapter(new ArrayAdapter(getActivity(),
                                            R.layout.simplelistitem, mItems));
                                    typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if (position == 0) {
                                                savezaudiostate = "open";
                                                tv_music_status.setText("ZAudio On");
                                            } else {
                                                savezaudiostate = "close";
                                                tv_music_status.setText("ZAudio Off");
                                            }
                                            musicalter.dismiss();
                                        }
                                    });
                                    musicalter.addExtView(typelist);
                                    musicalter.show();
                                }else{
                                    tv_music_status.setText("");
                                }
                            }
                            if (position == 3) {
                                if (test.isItemChecked(3)) {
                                    final String[] mItems = {"Curtain Open", "Curtain Close"};
                                    curtainalter = new AlertView(null, null, "CANCEL", null, null, getActivity(), AlertView.Style.ActionSheet,
                                            itemclick);
                                    final ListView typelist = new ListView(getActivity());
                                    typelist.setAdapter(new ArrayAdapter(getActivity(),
                                            R.layout.simplelistitem, mItems));
                                    typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if (position == 0) {
                                                savecurtainstate = "open";
                                                tv_cur_statue.setText("Curtain Open");
                                            } else {
                                                savecurtainstate = "close";
                                                tv_cur_statue.setText("Curtain Close");
                                            }
                                            curtainalter.dismiss();
                                        }
                                    });
                                    curtainalter.addExtView(typelist);
                                    curtainalter.show();
                                }else{
                                    tv_cur_statue.setText("");
                                }

                            }
                        }
                    });
                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, getActivity(), AlertView.Style.Alert,
                                    itemclick);
                            View selfviewx = getActivity().getLayoutInflater().inflate(R.layout.mood_icon_select, null);
                            GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                            icongrid.setAdapter(new MoodIconAdapter(getActivity()));
                            icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    iconrout = iconarray.get(position);
                                    icon.setImageResource(getResourdIdByResourdName(getActivity(), iconarray.get(position)));
                                    iconalter.dismiss();
                                }
                            });
                            iconalter.addExtView(selfviewx);
                            iconalter.show();
                        }
                    });
                    addingalter.addExtView(selfview);
                    addingalter.setShoulddismiss(false);
                    addingalter.show();

                }
                break;
            case R.id.mood_remove:
                deletemood=!deletemood;
                if(deletemood){
                    if(moodlist.size()>0){moodlist.clear();}
                    for(int i=0;i<roommoodbutton.size();i++){
                        MoodLayout x=(MoodLayout)moodcontainer.findViewById(roommoodbutton.get(i).mood_id);
                        moodlist.add(x);
                        x.setdeleteable(true);
                        x.setDeletemode(true);
                        x.setcheck(false);
                    }
                    add.setTitle("CANCEL DELETE");
                    delete.setTitle("DELETE");
                }else{
                    for(int i=0;i<moodlist.size();i++){
                        if(moodlist.get(i).getchoosestate()){
                            MainActivity.mgr.deletemood("mood",moodlist.get(i).getMoodID(),moodlist.get(i).getRoomid());
                            MainActivity.mgr.deletemoodbutton("moodbutton",moodlist.get(i).getMoodID(),moodlist.get(i).getRoomid());
                        }
                    }
                    moodhandler.postDelayed(getmoodbuttonlist,30);
                    add.setTitle("ADD MOOD");
                    delete.setTitle("DELETE MOOD");
                    if(moodlist.size()>0){moodlist.clear();}
                }
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==addingalter){
                if(position==0){
                    SparseBooleanArray checked = test.getCheckedItemPositions();
                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int positioncount = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i))
                            selectedItems[positioncount]=1;
                    }
                    if (moodename.getText().toString().trim().length() < 1) {
                        Toast.makeText(getActivity(), "please enter the mood name", Toast.LENGTH_SHORT).show();
                    } else {
                        Savemoodbutton moodinfo = new Savemoodbutton();
                        moodinfo.room_id = FounctionActivity.roomidfc;
                        if (roommoodbutton.size() == 0) {
                            moodinfo.mood_id = 1;
                        } else {
                            moodinfo.mood_id = roommoodbutton.get(roommoodbutton.size() - 1).mood_id + 1;
                        }
                        moodinfo.mood_name = moodename.getText().toString().trim();
                        moodinfo.mood_icon = iconrout;
                        MainActivity.mgr.addmoodbutton(moodinfo);
                        moodhandler.postDelayed(getmoodbuttonlist, 30);
                        getallstate=true;
                        addingalter.dismiss();
                    }
                }
            }

        }
    };

    public void addmood(Savemoodbutton sm){
        MoodLayout ml=new MoodLayout(getActivity());
        ml.seticon(getResources().getDrawable(getResourdIdByResourdName(getActivity(),sm.mood_icon)));
        ml.setitile(sm.mood_name);
        ml.setId(sm.mood_id);
        ml.setcontent(sm);
        moodcontainer.addView(ml);
    }
/******************************************各种定时器************************************************/
    Runnable getmoodbuttonlist=new Runnable() {
        @Override
        public void run() {
            moodcontainer.removeAllViews();
            roommoodbutton.clear();
            List<Savemoodbutton> alldata=MainActivity.mgr.querymoodbutton();
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id== FounctionActivity.roomidfc){
                    roommoodbutton.add(alldata.get(i));
                }
            }

            for(int i=0;i<roommoodbutton.size();i++){
                addmood(roommoodbutton.get(i));
            }
            if(getallstate){
                getallstate=false;
                List<Savelight> lightalldata=MainActivity.mgr.querylight();
                for(int i=0;i<lightalldata.size();i++){
                    if(lightalldata.get(i).room_id== FounctionActivity.roomidfc){
                        lightdata.add(lightalldata.get(i));
                    }
                }
                List<Savehvac> hvacalldata=MainActivity.mgr.queryhvac();
                for(int i=0;i<hvacalldata.size();i++){
                    if(hvacalldata.get(i).room_id== FounctionActivity.roomidfc){
                        hvacdata.add(hvacalldata.get(i));
                    }
                }
                List<Savemusic> musicx=MainActivity.mgr.querymusic();
                for(int i=0;i<musicx.size();i++){
                    if(musicx.get(i).room_id==FounctionActivity.roomidfc&&musicx.get(i).music_id==1){
                        musicdata.add(musicx.get(i));
                    }
                }

                List<Savecurtain> curtainalldata=MainActivity.mgr.querycurtain();
                for(int i=0;i<curtainalldata.size();i++){
                    if(curtainalldata.get(i).room_id== FounctionActivity.roomidfc){
                        curtaindata.add(curtainalldata.get(i));
                    }
                }
                moodsaving.show();
                getmoodsavestaterun();
            }
        }
    };
    byte lightsub=0,lightdev=0;
    int lightcount=0,state1=0;
    boolean lightsent=false;
    int lightovertime=0;
    Runnable getlightstaterun=new Runnable() {
        @Override
        public void run() {
            if(lightsent){
                lightovertime++;
                if(lightovertime>20){
                    lightsent=false;
                    lightcount++;
                }else{
                    mlc.getlightstate(lightsub, lightdev,MainActivity.mydupsocket);
                }
                lighthandler.postDelayed(getlightstaterun, 300);
            }else if(lightcount<lightdata.size()) {
                lightovertime=0;
                if (lightdata.get(lightcount).subnetID != (lightsub & 0xff)) {
                    lightsub = (byte) lightdata.get(lightcount).subnetID;
                    lightdev = (byte) lightdata.get(lightcount).deviceID;
                    lightsent=true;
                    state1=lightcount;
                   // mlc.getlightstate(lightsub, lightdev);
                } else if (lightdata.get(lightcount).deviceID != (lightdev & 0xff)) {
                    lightsub = (byte) lightdata.get(lightcount).subnetID;
                    lightdev = (byte) lightdata.get(lightcount).deviceID;
                    lightsent=true;
                    state1=lightcount;
                    //mlc.getlightstate(lightsub, lightdev);
                }else{
                    lightcount++;
                }
                //lightcount++;
                lighthandler.postDelayed(getlightstaterun,300);
            }else{
                lighthandler.removeCallbacks(getlightstaterun);
                selectedcount++;
                getmoodsavestaterun();
                lightsub=0;
                lightdev=0;
                lightcount=0;
                lightovertime=0;
            }
        }
    };
    int acStateCount=0,acdeviceCount=0;
    boolean readCountfanAndMode=false,readcstate=false;
    Runnable gethvacstaterun=new Runnable() {
        @Override
        public void run() {
                switch(acStateCount){
                    case 0:
                        macc.ACReadCountFanAndMode((byte)hvacdata.get(acdeviceCount).subnetID,(byte)hvacdata.get(acdeviceCount).deviceID,MainActivity.mydupsocket);
                        hvachandler.postDelayed(gethvacstaterun, 300);
                        break;
                    case 1:
                        macc.ACReadCState((byte)hvacdata.get(acdeviceCount).subnetID,(byte)hvacdata.get(acdeviceCount).deviceID,MainActivity.mydupsocket);
                        hvachandler.postDelayed(gethvacstaterun,300);
                        break;
                    case 2:
                        acdeviceCount++;
                        if(acdeviceCount>=hvacdata.size()){
                            hvachandler.removeCallbacks(gethvacstaterun);
                            selectedcount++;
                            getmoodsavestaterun();
                            acdeviceCount=0;
                            acStateCount=0;
                            readCountfanAndMode=false;
                            readcstate=false;
                        }else{
                            acStateCount=0;
                            readCountfanAndMode=false;
                            readcstate=false;
                            hvachandler.postDelayed(gethvacstaterun,300);
                        }
                        break;
                }
        }
    };
    boolean musicstate=false;
    int musicovertime=0;
    Runnable getzaudiostaterun=new Runnable() {
        @Override
        public void run() {
                if(!musicstate){
                    musicovertime++;
                    if(musicovertime>30){
                        musicstate=true;
                    }else{
                        mmuc.GetMusicState((byte)musicdata.get(0).subnetID,(byte)musicdata.get(0).deviceID ,MainActivity.mydupsocket);
                    }
                    zaudiohandler.postDelayed(getzaudiostaterun, 300);
                }else{
                    musicovertime=0;
                    musicstate=false;
                    voicestate=false;
                    albumstate=false;
                    songOrchannelstate=false;
                    currentfounction=0;
                    ablumno=0;
                    songno=0;
                    channelno=0;
                    selectedcount++;
                    getmoodsavestaterun();
                    zaudiohandler.removeCallbacks(getzaudiostaterun);
                }
        }
    };
    byte curtainsub=0,curtaindev=0;
    int curtaincount=0,state2=0;
    boolean curtainsent=false;
    Runnable getcurtainstaterun=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<curtaindata.size();i++){
                Savemood curtaininfo1=new Savemood();
                curtaininfo1.room_id=FounctionActivity.roomidfc;
                curtaininfo1.subnetID=((int) (curtaindata.get(i).subnetID));
                curtaininfo1.deviceID=((int) (curtaindata.get(i).deviceID));
                curtaininfo1.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                curtaininfo1.control_type=4;
                if(savecurtainstate.equals("open")){
                    curtaininfo1.value_1=curtaindata.get(i).channel_1;//channel
                    curtaininfo1.value_2=100;//brightness
                }else if(savecurtainstate.equals("close")){
                    curtaininfo1.value_1=curtaindata.get(i).channel_2;//channel
                    curtaininfo1.value_2=100;//brightness
                }
                curtaininfo1.value_3=0;
                curtaininfo1.value_4=0;
                curtaininfo1.value_5=0;
                curtaininfo1.value_6=0;
                MainActivity.mgr.addmood(curtaininfo1);

            }
            selectedcount++;
            getmoodsavestaterun();
        }
    };
    int[] selectedItems=new int[4];//{light,hvac,zaudio,curtain}
    int selectedcount=0;
    public void getmoodsavestaterun(){
        if(selectedcount==4){
            for(int i=0;i<4;i++){
                selectedItems[i]=0;
            }
            selectedcount=0;
            lighthandler.removeCallbacks(getlightstaterun);
            hvachandler.removeCallbacks(gethvacstaterun);
            zaudiohandler.removeCallbacks(getzaudiostaterun);
            curtainhandler.removeCallbacks(getcurtainstaterun);
            moodsaving.dismiss();
            Toast.makeText(getActivity(), "succeed", Toast.LENGTH_SHORT).show();
        }else if(selectedItems[selectedcount]==1){
            moodsaving.setProgress((selectedcount+1)*25);
            switch (selectedcount){
                case 0:
                    lighthandler.postDelayed(getlightstaterun,10);
                    break;
                case 1:
                    hvachandler.postDelayed(gethvacstaterun,10);
                    break;
                case 2:
                    zaudiohandler.postDelayed(getzaudiostaterun,10);
                    break;
                case 3:
                    curtainhandler.postDelayed(getcurtainstaterun,10);
                    break;
            }
        }else if(selectedItems[selectedcount]==0){
            moodsaving.setProgress((selectedcount+1)*25);
            selectedcount++;
            getmoodsavestaterun();
        }

    }
/***************************************************************************************************/
/******************************************各种接收处理函数************************************************/
boolean doing=false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                if(rev.length>27){
                    if(doing){

                    }else{
                        doing=true;
                        switch (selectedcount){
                            case 0:runrevlight(rev);break;
                            case 1:runrevhvac(rev);break;
                            case 2:runrevzaudio(rev);break;
                            case 3:break;
                        }
                        doing=false;
                    }
                }
                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                getActivity().finish();
            }else if(FounctionActivity.ACTION_DELETEMOOD.equals(action)){
                moodhandler.postDelayed(getmoodbuttonlist,30);
            }
        }
    };

    public void runrevlight(byte[] revdata){
        int x=(int)(((revdata[21]&0xff)<<8))+(int)(revdata[22]&0xff);//get op code
        if(x==0x0034){
            if(lightsub==revdata[17]&&lightdev==revdata[18]&&lightcount==state1){
                for(int i=0;i<lightdata.size();i++){
                    if(lightdata.get(i).subnetID==(revdata[17]&0xff)&&lightdata.get(i).deviceID==(revdata[18]&0xff)){
                        if(lightdata.get(i).lightType!=3){
                            try{
                                Savemood lightinfo=new Savemood();
                                lightinfo.room_id=FounctionActivity.roomidfc;
                                lightinfo.subnetID=((int) (revdata[17]) & 0xff);
                                lightinfo.deviceID=((int) (revdata[18]) & 0xff);
                                lightinfo.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                lightinfo.control_type=1;
                                lightinfo.value_1=lightdata.get(i).channel;//channel
                                lightinfo.value_2=revdata[25+lightdata.get(i).channel];//brightness
                                lightinfo.value_3=0;
                                lightinfo.value_4=0;
                                lightinfo.value_5=0;
                                lightinfo.value_6=0;
                                MainActivity.mgr.addmood(lightinfo);
                            }catch (Exception e){
                                Toast.makeText(getActivity(), "Record light status fail", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Savemood lightinfo2=new Savemood();
                            lightinfo2.room_id=FounctionActivity.roomidfc;
                            lightinfo2.subnetID=((int) (revdata[17]) & 0xff);
                            lightinfo2.deviceID=((int) (revdata[18]) & 0xff);
                            lightinfo2.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                            lightinfo2.control_type=1;
                            lightinfo2.value_1=revdata[26];//a
                            lightinfo2.value_2=revdata[27];//brightness
                            lightinfo2.value_3=revdata[28];
                            lightinfo2.value_4=revdata[29];
                            lightinfo2.value_5=0;
                            lightinfo2.value_6=1;
                            MainActivity.mgr.addmood(lightinfo2);
                        }

                    }
                }
                //这里不能操作led彩灯，因为硬件有问题，channel那个位不知道为什么要设定为0x32彩灯才能有反应
                lightsent=false;
                lightcount++;
            }
        }

    }
    byte[] fanArray,modeArray;
    public void runrevhvac(byte[] revdata){
        int x=(int)(((revdata[21]&0xff)<<8))+(int)(revdata[22]&0xff);//get op code
        if (((revdata[17]&0xff) == hvacdata.get(acdeviceCount).subnetID) &&
                ((revdata[18]&0xff) ==hvacdata.get(acdeviceCount).deviceID)){
            switch(x){
                case 0xe125:
                    if(!readCountfanAndMode){
                        fanArray=new byte[revdata[25]];
                        for(int i=0;i<fanArray.length;i++){
                            fanArray[i]=revdata[26+i];
                        }
                        int startmode=26+fanArray.length;
                        for(int i=0;i<10;i++){
                            if((revdata[startmode]>(byte)0x20)||(revdata[startmode]==(byte)0x00)){
                                startmode++;
                            }else if(revdata[startmode]<(byte)0x06){
                                break;
                            }
                        }
                        modeArray=new byte[revdata[startmode]];
                        for(int i=0;i<modeArray.length;i++){
                            modeArray[i]=revdata[startmode+1+i];
                        }
                        readCountfanAndMode=true;
                        acStateCount=1;
                    }
                    break;
                case 0xe0ed:
                    if(!readcstate){
                        if(revdata[25]==0){
                            Savemood acinfo1=new Savemood();
                            acinfo1.room_id=FounctionActivity.roomidfc;
                            acinfo1.subnetID=((int) (revdata[17]) & 0xff);
                            acinfo1.deviceID=((int) (revdata[18]) & 0xff);
                            acinfo1.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                            acinfo1.control_type=2;
                            acinfo1.value_1=1;//on/off
                            acinfo1.value_2=0;//off
                            acinfo1.value_3=0;
                            acinfo1.value_4=0;
                            acinfo1.value_5=0;
                            acinfo1.value_6=0;
                            MainActivity.mgr.addmood(acinfo1);
                        }else{
                            Savemood acinfo1=new Savemood();
                            acinfo1.room_id=FounctionActivity.roomidfc;
                            acinfo1.subnetID=((int) (revdata[17]) & 0xff);
                            acinfo1.deviceID=((int) (revdata[18]) & 0xff);
                            acinfo1.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                            acinfo1.control_type=2;
                            acinfo1.value_1=1;
                            acinfo1.value_2=1;
                            acinfo1.value_3=0;
                            acinfo1.value_4=0;
                            acinfo1.value_5=0;
                            acinfo1.value_6=0;
                            MainActivity.mgr.addmood(acinfo1);
                        }

                        byte CurrentMode=0;
                        if((revdata[27]>>4)==modeArray.length){
                            CurrentMode=modeArray[(revdata[27]>>4)-1];
                        }else{
                            CurrentMode=modeArray[(revdata[27]>>4)];
                        }
                        Savemood acinfo2=new Savemood();
                        acinfo2.room_id=FounctionActivity.roomidfc;
                        acinfo2.subnetID=((int) (revdata[17]) & 0xff);
                        acinfo2.deviceID=((int) (revdata[18]) & 0xff);
                        acinfo2.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                        acinfo2.control_type=2;
                        acinfo2.value_1=2;//mode
                        acinfo2.value_2=(int)CurrentMode;
                        acinfo2.value_3=0;
                        acinfo2.value_4=0;
                        acinfo2.value_5=0;
                        acinfo2.value_6=0;
                        MainActivity.mgr.addmood(acinfo2);
                        
                        int temp=0;
                        switch(CurrentMode){
                            case const_mode_auto:
                                temp=revdata[32];
                                break;
                            case const_mode_cool:
                                temp=revdata[26];
                                break;
                            case const_mode_heat:
                                temp=revdata[30];
                                break;
                        }
                        Savemood acinfo3=new Savemood();
                        acinfo3.room_id=FounctionActivity.roomidfc;
                        acinfo3.subnetID=((int) (revdata[17]) & 0xff);
                        acinfo3.deviceID=((int) (revdata[18]) & 0xff);
                        acinfo3.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                        acinfo3.control_type=2;
                        acinfo3.value_1=3;//temp
                        acinfo3.value_2=(int)CurrentMode;
                        acinfo3.value_3=temp;
                        acinfo3.value_4=0;
                        acinfo3.value_5=0;
                        acinfo3.value_6=0;
                        MainActivity.mgr.addmood(acinfo3);
                        byte CurrentFanMode=0;
                        if((revdata[27]&0x0f)==fanArray.length){
                            CurrentFanMode=fanArray[(revdata[27]&0x0f)-1];
                        }else{
                            CurrentFanMode=fanArray[(revdata[27]&0x0f)];
                        }

                        Savemood acinfo4=new Savemood();
                        acinfo4.room_id=FounctionActivity.roomidfc;
                        acinfo4.subnetID=((int) (revdata[17]) & 0xff);
                        acinfo4.deviceID=((int) (revdata[18]) & 0xff);
                        acinfo4.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                        acinfo4.control_type=2;
                        acinfo4.value_1=4;//temp
                        acinfo4.value_2=(int)CurrentFanMode;
                        acinfo4.value_3=0;
                        acinfo4.value_4=0;
                        acinfo4.value_5=0;
                        acinfo4.value_6=0;
                        MainActivity.mgr.addmood(acinfo4);
                        fanArray=null;
                        modeArray=null;
                        readcstate=true;
                        acStateCount=2;
                    }
                    break;
                default:
                    break;
            }

        }
    }
    int currentfounction=0,ablumno=0,songno=0,channelno=0;
    boolean voicestate=false,albumstate=false,songOrchannelstate=false;
    public void runrevzaudio(byte[] revdata){
        if(!musicstate){
            int a=(int)((revdata[25]<<8))+(int)(revdata[26]&0xff);
            int b=(int)(revdata[36]&0xff);
            if(a==0x235a){
                if(!voicestate){
                    voicestate=true;
                    if(((revdata[32])==((byte)0x53))&&((revdata[33])==((byte)0x52))&&((revdata[34])==((byte)0x43))){
                        byte[] typevalue=new byte[1];
                        typevalue[0]=revdata[35];
                        String str=bytetostring(typevalue, "ascii");
                        currentfounction=Integer.parseInt(str);

                        Savemood zaudioinfo1=new Savemood();
                        zaudioinfo1.room_id=FounctionActivity.roomidfc;
                        zaudioinfo1.subnetID=((int) (revdata[17]) & 0xff);
                        zaudioinfo1.deviceID=((int) (revdata[18]) & 0xff);
                        zaudioinfo1.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                        zaudioinfo1.control_type=3;
                        zaudioinfo1.value_1=currentfounction;//music type
                        zaudioinfo1.value_2=0;//
                        zaudioinfo1.value_3=0;
                        zaudioinfo1.value_4=0;
                        zaudioinfo1.value_5=0;
                        zaudioinfo1.value_6=1;
                        MainActivity.mgr.addmood(zaudioinfo1);
                    }

                    byte[] voicevalue=new byte[2];
                    if(((revdata[37])==((byte)0x56))&&((revdata[38])==((byte)0x4f))&&((revdata[39])==((byte)0x4c))){
                        if(revdata[41]==(byte)0x0d){
                            voicevalue[0]=0x30;
                            voicevalue[1]=revdata[40];
                        }else if(revdata[42]==(byte)0x0d){
                            voicevalue[0]=revdata[40];
                            voicevalue[1]=revdata[41];
                        }
                    }
                    String str=bytetostring(voicevalue, "ascii");
                    int value=Integer.parseInt(str);
                    //value=100-(100*value)/79;

                    Savemood zaudioinfo2=new Savemood();
                    zaudioinfo2.room_id=FounctionActivity.roomidfc;
                    zaudioinfo2.subnetID=((int) (revdata[17]) & 0xff);
                    zaudioinfo2.deviceID=((int) (revdata[18]) & 0xff);
                    zaudioinfo2.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                    zaudioinfo2.control_type=3;
                    zaudioinfo2.value_1=currentfounction;//music type
                    zaudioinfo2.value_2=0;//
                    zaudioinfo2.value_3=0;
                    zaudioinfo2.value_4=0;
                    zaudioinfo2.value_5=value;
                    zaudioinfo2.value_6=2;
                    MainActivity.mgr.addmood(zaudioinfo2);

                    if(currentfounction==6){
                        if(savezaudiostate.equals("open")){
                            Savemood zaudioinfo3=new Savemood();
                            zaudioinfo3.room_id=FounctionActivity.roomidfc;
                            zaudioinfo3.subnetID=((int) (revdata[17]) & 0xff);
                            zaudioinfo3.deviceID=((int) (revdata[18]) & 0xff);
                            zaudioinfo3.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                            zaudioinfo3.control_type=3;
                            zaudioinfo3.value_1=currentfounction;//audio in
                            zaudioinfo3.value_2=0;//
                            zaudioinfo3.value_3=0;
                            zaudioinfo3.value_4=0;
                            zaudioinfo3.value_5=0;
                            zaudioinfo3.value_6=3;
                            MainActivity.mgr.addmood(zaudioinfo3);
                        }else if(savezaudiostate.equals("close")){
                            Savemood zaudioinfo3=new Savemood();
                            zaudioinfo3.room_id=FounctionActivity.roomidfc;
                            zaudioinfo3.subnetID=((int) (revdata[17]) & 0xff);
                            zaudioinfo3.deviceID=((int) (revdata[18]) & 0xff);
                            zaudioinfo3.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                            zaudioinfo3.control_type=3;
                            zaudioinfo3.value_1=currentfounction;//audio in
                            zaudioinfo3.value_2=0;//
                            zaudioinfo3.value_3=0;
                            zaudioinfo3.value_4=0;
                            zaudioinfo3.value_5=0;
                            zaudioinfo3.value_6=99;
                            MainActivity.mgr.addmood(zaudioinfo3);
                        }
                        albumstate=true;
                        songOrchannelstate=true;
                        musicstate=true;
                    }
                }

            }else if(a==0x2353){
                    switch (b){
                        case 0x31:
                            if(!albumstate){
                                albumstate=true;
                                if(currentfounction==1) {
                                    byte[] album = getpieceofbyte(revdata, 0);
                                    String str = bytetostring(album, "Unicode");
                                    int value = Integer.parseInt(str);
                                    ablumno = value;
                                } 
                            }
                            
                            break;
                        case 0x33:
                            if(!songOrchannelstate&albumstate){
                                songOrchannelstate=true;
                                if(currentfounction==1){
                                    byte[] song = getpieceofbyte(revdata,1);
                                    String str2 = bytetostring(song, "Unicode");
                                    int value2=Integer.parseInt(str2);
                                    songno=value2;

                                    Savemood zaudioinfo3=new Savemood();
                                    zaudioinfo3.room_id=FounctionActivity.roomidfc;
                                    zaudioinfo3.subnetID=((int) (revdata[17]) & 0xff);
                                    zaudioinfo3.deviceID=((int) (revdata[18]) & 0xff);
                                    zaudioinfo3.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                    zaudioinfo3.control_type=3;
                                    zaudioinfo3.value_1=1;//audio in
                                    zaudioinfo3.value_2=ablumno;//
                                    zaudioinfo3.value_3=songno;
                                    zaudioinfo3.value_4=0;
                                    zaudioinfo3.value_5=0;
                                    zaudioinfo3.value_6=3;
                                    MainActivity.mgr.addmood(zaudioinfo3);

                                    if(savezaudiostate.equals("close")){
                                        Savemood zaudioinfo4=new Savemood();
                                        zaudioinfo4.room_id=FounctionActivity.roomidfc;
                                        zaudioinfo4.subnetID=((int) (revdata[17]) & 0xff);
                                        zaudioinfo4.deviceID=((int) (revdata[18]) & 0xff);
                                        zaudioinfo4.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                        zaudioinfo4.control_type=3;
                                        zaudioinfo4.value_1=1;//audio in
                                        zaudioinfo4.value_2=0;//
                                        zaudioinfo4.value_3=0;
                                        zaudioinfo4.value_4=0;
                                        zaudioinfo4.value_5=0;
                                        zaudioinfo4.value_6=99;
                                        MainActivity.mgr.addmood(zaudioinfo4);
                                    }

                                }else if(currentfounction==5){
                                    byte[] song = getpieceofbyte(revdata,2);
                                    String str2 = bytetostring(song, "Unicode");
                                    int value2=Integer.parseInt(str2);
                                    channelno=value2;
                                    
                                    Savemood zaudioinfo4=new Savemood();
                                    zaudioinfo4.room_id=FounctionActivity.roomidfc;
                                    zaudioinfo4.subnetID=((int) (revdata[17]) & 0xff);
                                    zaudioinfo4.deviceID=((int) (revdata[18]) & 0xff);
                                    zaudioinfo4.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                    zaudioinfo4.control_type=3;
                                    zaudioinfo4.value_1=currentfounction;//audio in
                                    zaudioinfo4.value_2=0;//
                                    zaudioinfo4.value_3=0;
                                    zaudioinfo4.value_4=channelno;
                                    zaudioinfo4.value_5=0;
                                    zaudioinfo4.value_6=3;
                                    MainActivity.mgr.addmood(zaudioinfo4);

                                    if(savezaudiostate.equals("open")){
                                        Savemood zaudioinfo5=new Savemood();
                                        zaudioinfo5.room_id=FounctionActivity.roomidfc;
                                        zaudioinfo5.subnetID=((int) (revdata[17]) & 0xff);
                                        zaudioinfo5.deviceID=((int) (revdata[18]) & 0xff);
                                        zaudioinfo5.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                        zaudioinfo5.control_type=3;
                                        zaudioinfo5.value_1=currentfounction;//audio in
                                        zaudioinfo5.value_2=0;//
                                        zaudioinfo5.value_3=0;
                                        zaudioinfo5.value_4=0;
                                        zaudioinfo5.value_5=0;
                                        zaudioinfo5.value_6=4;
                                        MainActivity.mgr.addmood(zaudioinfo5);
                                    }else if(savezaudiostate.equals("close")){
                                        Savemood zaudioinfo5=new Savemood();
                                        zaudioinfo5.room_id=FounctionActivity.roomidfc;
                                        zaudioinfo5.subnetID=((int) (revdata[17]) & 0xff);
                                        zaudioinfo5.deviceID=((int) (revdata[18]) & 0xff);
                                        zaudioinfo5.mood_id=roommoodbutton.get(roommoodbutton.size()-1).mood_id;
                                        zaudioinfo5.control_type=3;
                                        zaudioinfo5.value_1=currentfounction;//audio in
                                        zaudioinfo5.value_2=0;//
                                        zaudioinfo5.value_3=0;
                                        zaudioinfo5.value_4=0;
                                        zaudioinfo5.value_5=0;
                                        zaudioinfo5.value_6=99;
                                        MainActivity.mgr.addmood(zaudioinfo5);
                                    }

                                }
                                musicstate=true;
                            }
                            
                            break;
                        default:break;
                    }
            }
        }
    }

/***************************************************************************************************/
/******************************************各种辅助函数************************************************/
    private static String byte2hex(byte [] buffer){
        String h = "";

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + " "+ temp;
        }
        return h;
    }
    public static int getResourdIdByResourdName(Context context, String ResName){
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
               // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
               // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
    }
    public void setDialogdismiss(boolean result,DialogInterface dialog){
        try
        {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing");
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
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_DELETEMOOD);
        return intentFilter;
    }
    public byte[] getpieceofbyte(byte[] data,int type){
        int endbyte=0,startbyte=0;
        if(type==0){
            startbyte=49;
        }else if(type==1){
            startbyte=51;
        }else if(type==2){
            startbyte=55;
        }
        if(type!=2){
            for(int i=startbyte;i<data.length;i++){
                if((data[i]==(byte)0x00)&&(data[i+1]==(byte)0x2f)){
                    endbyte=i;
                    break;
                }
            }
        }else{
            for(int i=startbyte;i<data.length;i++){
                if((data[i]==(byte)0x00)&&(data[i+1]==(byte)0x03)&&(data[i+2]==(byte)0x0d)){
                    endbyte=i;
                    break;
                }
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
/***************************************************************************************************/
}
