package com.shima.smartbushome.founction_view;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.database.Savemusic;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.selflayout.AudioInLayout;
import com.shima.smartbushome.selflayout.MusicLayout;
import com.shima.smartbushome.selflayout.RadioLayout;
import com.shima.smartbushome.udp.udp_socket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Music2 extends Fragment implements View.OnClickListener {

    View view;
    Button addmusicbutton;
    MusicLayout ml;
    AudioInLayout ail;
    RadioLayout rl;
    RadioGroup rp;
    RadioButton rb_music,rb_fm,rb_audioin;
    //TextView musiclog;
    Savemusic thismusic=new Savemusic();
    Handler getdata=new Handler();
    Dialog aupairdialog;
    public static Activity getact;
    public static int musicchoosestate=0;//0-music;1-radio;2-audioin
    RelativeLayout zlayout;
    private ViewFlipper viewFlipper;
    private GestureDetector detector; //手势检测
    musiccontrol zaudiocontrol=new musiccontrol();
    RelativeLayout havemusic,nomusic;
    public Music2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_music, container, false);



        zlayout=(RelativeLayout)view.findViewById(R.id.zaudiolayout);
        havemusic=(RelativeLayout)view.findViewById(R.id.musiclayoutcontent);
        nomusic=(RelativeLayout)view.findViewById(R.id.nomusic);
        addmusicbutton=(Button)view.findViewById(R.id.button3);
        addmusicbutton.setOnClickListener(this);
        ml=(MusicLayout)view.findViewById(R.id.view4);
        ail=(AudioInLayout)view.findViewById(R.id.view6);
        rl=(RadioLayout)view.findViewById(R.id.view8);
        rp=(RadioGroup)view.findViewById(R.id.radioGroup);
        rp.setOnCheckedChangeListener(selectchange);
        rb_music=(RadioButton)view.findViewById(R.id.music);
        rb_fm=(RadioButton)view.findViewById(R.id.radio);
        rb_audioin=(RadioButton)view.findViewById(R.id.audio);

      /*  SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        String backgroud = sharedcolorPre.getString("musicbg", "");
        if(backgroud.equals("")){
            zlayout.setBackgroundColor(0x000000);
        }else{
            if(Build.VERSION.SDK_INT >= 16){
                //zlayout.setBackground(getResources().getDrawable(getResourdIdByResourdName(backgroud)));
                zlayout.setBackgroundColor(0x000000);
            }else{
               // zlayout.setBackgroundDrawable(getResources().getDrawable(getResourdIdByResourdName(backgroud)));
                zlayout.setBackgroundColor(0x000000);
            }
        }*/

        //musiclog=(TextView)view.findViewById(R.id.textView3);
        aupairdialog=new Dialog(getActivity());
        getact=getActivity();
        getActivity().setTitle("Music");
        setHasOptionsMenu(true);
        getdata.postDelayed(getmusicidrun,20);
        return view;
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
        ml.removetimer();
    }

    EditText add_sub,add_dev;
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button3:
                addmusicalter = new AlertView("Add", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                        itemclick);
                View selfview = getActivity().getLayoutInflater().inflate(R.layout.setting_musicinfo, null);
                add_sub = (EditText) selfview.findViewById(R.id.music_subedit);
                add_dev = (EditText) selfview.findViewById(R.id.music_devedit);
                add_sub.setText("0");
                add_dev.setText("0");
                addmusicalter.addExtView(selfview);
                addmusicalter.show();
                break;
        }
    }

    Runnable getmusicidrun=new Runnable() {
        @Override
        public void run() {
            List<Savemusic> musicx=MainActivity.mgr.querymusic();
            List<Savemusic> musicx2=new ArrayList<Savemusic>();
            for(int i=0;i<musicx.size();i++){
                if(musicx.get(i).room_id==FounctionActivity.roomidfc&&musicx.get(i).music_id==1){
                    musicx2.add(musicx.get(i));
                }
            }
            if (musicx2.size() > 0) {
                havemusic.setVisibility(View.VISIBLE);
                nomusic.setVisibility(View.GONE);
                thismusic = musicx2.get(0);
                ml.setcontent(thismusic);
                rl.setcontent(thismusic);
                ail.setcontent(thismusic);

                if(rb_music.isChecked()){
                    ml.setVisibility(View.VISIBLE);
                    ail.setVisibility(View.GONE);
                    rl.setVisibility(View.GONE);
                    ml.SwitchtoMusicSD();
                }
            }else{
                havemusic.setVisibility(View.GONE);
                nomusic.setVisibility(View.VISIBLE);
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.music_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    AlertView settingalter,pairalter,addmusicalter;
    EditText sub,dev;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.music_setting:
                if(!MainActivity.islockchangeid){
                    settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfview = getActivity().getLayoutInflater().inflate(R.layout.setting_musicinfo, null);
                    sub = (EditText) selfview.findViewById(R.id.music_subedit);
                    dev = (EditText) selfview.findViewById(R.id.music_devedit);
                    sub.setText(String.valueOf(thismusic.subnetID));
                    dev.setText(String.valueOf(thismusic.deviceID));
                    settingalter.addExtView(selfview);
                    settingalter.show();
                }

                break;
            case R.id.music_pair:
                if(!MainActivity.islockchangeid){
                    pairalter = new AlertView("Select Device", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewx= getActivity().getLayoutInflater().inflate(R.layout.auto_pair_dialog, null);
                    ListView test=(ListView)selfviewx.findViewById(R.id.listView4);
                    DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(getActivity(),MainActivity.netdeviceList);
                    test.setAdapter(mLeDeviceListAdapter);
                    test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Savemusic x = new Savemusic();
                            x.room_id = FounctionActivity.roomidfc;
                            x.music_id = 1;
                            x.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            x.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            MainActivity.mgr.updatemusic(x);
                            getdata.postDelayed(getmusicidrun, 20);
                            Toast.makeText(getActivity(), "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                            pairalter.dismiss();
                        }
                    });
                    pairalter.addExtView(selfviewx);
                    pairalter.show();
                }


                break;

            default:break;

        }
        return super.onOptionsItemSelected(item);
    }
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if (o == settingalter) {
                if (position == 0) {
                    Savemusic x = new Savemusic();
                    x.room_id = FounctionActivity.roomidfc;
                    x.music_id = 1;
                    x.subnetID = Integer.parseInt(sub.getText().toString().trim());
                    x.deviceID = Integer.parseInt(dev.getText().toString().trim());
                    MainActivity.mgr.updatemusic(x);
                    getdata.postDelayed(getmusicidrun, 20);
                }
            }else if(o==addmusicalter){
                if (position == 0) {
                    List<Savemusic> k=new ArrayList<Savemusic>();
                    Savemusic kk=new Savemusic();
                    kk.room_id= FounctionActivity.roomidfc;
                    kk.music_id = 1;
                    kk.subnetID = Integer.parseInt(add_sub.getText().toString().trim());
                    kk.deviceID= Integer.parseInt(add_dev.getText().toString().trim());
                    k.add(kk);
                    MainActivity.mgr.addmusic(k);
                    getdata.postDelayed(getmusicidrun, 20);
                }
            }
        }
    };
    public RadioGroup.OnCheckedChangeListener selectchange=new RadioGroup.OnCheckedChangeListener(){
          @Override
          public void onCheckedChanged(RadioGroup arg0, int arg1) {
              switch(arg0.getCheckedRadioButtonId()){
                  case R.id.music:
                      musicchoosestate=0;
                      ml.setVisibility(View.VISIBLE);
                      ail.setVisibility(View.GONE);
                      rl.setVisibility(View.GONE);
                      ml.SwitchtoMusicSD();
                      //ml.reflashui();
                      break;
                  case R.id.radio:
                      musicchoosestate=1;
                      ml.setVisibility(View.GONE);
                      ail.setVisibility(View.GONE);
                      rl.setVisibility(View.VISIBLE);
                      if(!rl.ifinitstarted()){
                          rl.initview(getActivity());
                      }
                      rl.SwitchtoRadio();
                      break;
                  case R.id.audio:
                      musicchoosestate=2;
                      ml.setVisibility(View.GONE);
                      ail.setVisibility(View.VISIBLE);
                      rl.setVisibility(View.GONE);
                      if(!ail.ifinitstarted()){
                          ail.initview(getActivity());
                      }
                      ail.SwitchtoAudioIn();
                      break;
                  default:break;
              }
                 }
    };
    public static void finishmusic(){
        getact.finish();
    }

    int playorpause=0;
    public void shakeperform(int shaketype){
        switch (shaketype){
            case 1:zaudiocontrol.MusicControl((byte) 4, (byte) 1, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
            break;
            case 2:zaudiocontrol.MusicControl((byte) 4, (byte) 2, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
            break;
            case 6:
                if(playorpause==0){
                   // zaudiocontrol.MusicControl((byte) 4, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    playorpause=1;
                    ml.setplaybuttonstate("pause");
                }else{
                   // zaudiocontrol.MusicControl((byte) 4, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    playorpause=0;
                    ml.setplaybuttonstate("play");
                }
                break;
            case 5:
                if(playorpause==0){
                    zaudiocontrol.MusicControl((byte) 4, (byte) 4, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    playorpause=1;
                }else{
                    zaudiocontrol.MusicControl((byte) 4, (byte) 3, (byte) 0, (byte) 0, (byte) thismusic.subnetID, (byte) thismusic.deviceID,MainActivity.mydupsocket);
                    playorpause=0;
                }
                break;
         }
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                /*byte[] data = new byte[udp_socket.packet.getLength()];
                for (int i = 0; i < udp_socket.packet.getLength(); i++) {
                    data[i] = rev[i];
                }*/
               // String str="1";
                //str=byte2hex(rev);
               // musiclog.setText(str);
                if(rev.length>25){
                    RunReceiveData(rev);
                }
                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                onbackpress();
            }else if(FounctionActivity.ACTION_SHAKE.equals(action)){
                if(!MainActivity.islockshake){
                    int shaketype=(intent.getIntExtra("shake_type",0));
                    shakeperform(shaketype);
                }

            }
        }
    };
    public void RunReceiveData(byte[] data){
        if(rb_music.isChecked()){
            ml.receiveddata(data);
        }else if(rb_audioin.isChecked()){
            ail.receiveddata(data);
        }else if(rb_fm.isChecked()){
            rl.receiveddata(data);
        }
    }


    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }

    public void onbackpress(){
        if(rb_music.isChecked()){
            ml.musicbackpress();
        }else if(rb_fm.isChecked()){
            rl.radiobackpress();
        }else if(rb_audioin.isChecked()){
            ail.radiobackpress();
        }
    }
    public int getResourdIdByResourdName(String ResName){
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
}
