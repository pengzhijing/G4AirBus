package com.shima.smartbushome.founction_view;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.HvacAdapter;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.selflayout.HVACLayout;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HAVC extends Fragment implements View.OnClickListener{
    Savehvac roomhvacdata=new Savehvac();
    View view;
    //TextView hvaclog;
    Handler hvachandler=new Handler();
    Handler reflashhandler=new Handler();
    List<Savehvac> roomhvac=new ArrayList<Savehvac>();
    HVACLayout hl;
    ACcontrol accommand;
    Dialog aupairdialog;
    public int POWER_ON=0,POWER_OFF=1,FANSPEED_AUTO=2,
    FANSPEED_HIGH=3,FANSPEED_MID=4,FANSPEED_LOW=5,MODE_AUTO=6,MODE_COOL=7,
    MODE_HEAT=8,MODE_FAN=9,TEMP=10;

    ScrollView hvacview;
    RelativeLayout nohvacinfo;
    GridView hvacgrid;
    Button addhvacbutton;
    HvacAdapter adapter;
    public HAVC() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("HVAC");
        view = inflater.inflate(R.layout.fragment_havc, container, false);
      /*  hvaclog=(TextView)view.findViewById(R.id.textView2);
        hvaclog.setMovementMethod(new ScrollingMovementMethod());*/
        hl=(HVACLayout)view.findViewById(R.id.hvackongjian);
        hvacgrid=(GridView)view.findViewById(R.id.hvacgrid);
        nohvacinfo=(RelativeLayout)view.findViewById(R.id.nohvacinfo);
        hvacview=(ScrollView)view.findViewById(R.id.hvacview);
        addhvacbutton=(Button)view.findViewById(R.id.button46);
        addhvacbutton.setOnClickListener(this);
        setHasOptionsMenu(true);

      /*  SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("hvacbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.fhvac);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        accommand=new ACcontrol();
        aupairdialog=new Dialog(getActivity());
        hvachandler.postDelayed(gethvactag, 30);

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
        hl.removetimer();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hvac_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.button46:
                addalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"},null , getActivity(), AlertView.Style.Alert,
                        itemclick);
                View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                sub = (EditText) selfview.findViewById(R.id.acsubedit);
                dev = (EditText) selfview.findViewById(R.id.acdevedit);
                remark = (EditText) selfview.findViewById(R.id.acremark);
                sub.setText(String.valueOf(0));
                dev.setText(String.valueOf(0));
                if(roomhvac.size()==0){
                    remark.setText("HV"+1);
                }else{
                    remark.setText("HV"+(roomhvac.get(roomhvac.size()-1).hvac_id+1));
                }
                addalter.addExtView(selfview);
                addalter.setCancelable(false);
                addalter.setShoulddismiss(false);
                addalter.show();
                break;
        }
    }
    AlertView settingalter,switchalter,pairalter,deletealter,addalter;
    EditText sub,dev,remark;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] mItems=new String[roomhvac.size()] ;
        for(int i=0;i<roomhvac.size();i++){
            mItems[i]=roomhvac.get(i).hvac_remark;
        }
        switch (item.getItemId()) {
            case R.id.hvacsetting:
                if(!MainActivity.islockchangeid){
                    settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfview.findViewById(R.id.acsubedit);
                    dev = (EditText) selfview.findViewById(R.id.acdevedit);
                    remark = (EditText) selfview.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(roomhvacdata.subnetID));
                    dev.setText(String.valueOf(roomhvacdata.deviceID));
                    remark.setText(roomhvacdata.hvac_remark);
                    settingalter.addExtView(selfview);
                    settingalter.show();
                }

                break;

            case R.id.hvac_add:
                if(!MainActivity.islockchangeid){
                    addalter = new AlertView("Settings", null, "CANCEL", new String[]{"SAVE"},  null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewadd= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfviewadd.findViewById(R.id.acsubedit);
                    dev = (EditText) selfviewadd.findViewById(R.id.acdevedit);
                    remark = (EditText) selfviewadd.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(0));
                    dev.setText(String.valueOf(0));
                    if(roomhvac.size()==0){
                        remark.setText("HV"+1);
                    }else{
                        remark.setText("HV"+(roomhvac.get(roomhvac.size()-1).hvac_id+1));
                    }
                    addalter.addExtView(selfviewadd);
                    addalter.setCancelable(false);
                    addalter.setShoulddismiss(false);
                    addalter.show();
                }

                break;
            case R.id.hvac_delete:
                if(!MainActivity.islockchangeid){
                    deletealter = new AlertView("Select AC to Delete", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    ViewGroup extView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.listdialog, null);
                    ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
                    extView.setLayoutParams(lp);
                    deletelist.setAdapter(new ArrayAdapter(getActivity(),
                            R.layout.simplelistitem, mItems));
                    deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MainActivity.mgr.deletehvac("hvac", roomhvac.get(position).hvac_id, FounctionActivity.roomidfc);
                            deleteSharedPreferences(roomhvac.get(position));
                            Toast.makeText(getActivity(), "Delete Succeed", Toast.LENGTH_SHORT).show();
                            hvachandler.postDelayed(gethvactag, 30);
                            deletealter.dismiss();
                        }
                    });
                    deletealter.addExtView(extView);
                    deletealter.show();
                }

                break;
            case R.id.hvacpair:
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
                            Savehvac data = new Savehvac();
                            data.room_id = FounctionActivity.roomidfc;
                            data.hvac_id = roomhvacdata.hvac_id;
                            data.hvac_remark = roomhvacdata.hvac_remark;
                            data.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            data.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            MainActivity.mgr.updatehvac(data);
                            renewdata();
                            roomhvacdata.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            roomhvacdata.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            hl.setcontant(roomhvacdata);
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

    //删除HVAC页面当前温度相关数据
    public void deleteSharedPreferences(Savehvac thishvac){
       String CurTemperType="CurTemperType"+thishvac._id;
        String ZoneBeastSubID="ZoneBeastSubID"+thishvac._id;
        String ZoneBeastDevID="ZoneBeastDevID"+thishvac._id;
        String SensorSubID="SensorSubID"+thishvac._id;
        String  SensorDevID="SensorDevID"+thishvac._id;
        String FTSubID="FTSubID"+thishvac._id;
        String  FTDevID="FTDevID"+thishvac._id;

        SharedPreferencesHelper.init(getContext());
        SharedPreferencesHelper.getInstance().deleteData(CurTemperType);
        SharedPreferencesHelper.getInstance().deleteData(ZoneBeastSubID);
        SharedPreferencesHelper.getInstance().deleteData(ZoneBeastDevID);
        SharedPreferencesHelper.getInstance().deleteData(SensorSubID);
        SharedPreferencesHelper.getInstance().deleteData(SensorDevID);
        SharedPreferencesHelper.getInstance().deleteData(FTSubID);
        SharedPreferencesHelper.getInstance().deleteData(FTDevID);
    }

    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==settingalter){
                if(position==0){
                    Savehvac data=new Savehvac();
                    data.room_id=FounctionActivity.roomidfc;
                    data.hvac_id=roomhvacdata.hvac_id;
                    data.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    data.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    data.hvac_remark=remark.getText().toString().trim();
                    MainActivity.mgr.updatehvac(data);
                    renewdata();
                    roomhvacdata.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    roomhvacdata.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    roomhvacdata.hvac_remark=remark.getText().toString().trim();
                    hl.setcontant(roomhvacdata);
                }
            }else if(o==switchalter){

            }else if(o==deletealter){

            }else if(o==pairalter){

            }else if(o==addalter){
                if(position==0){
                    if(remark.getText().toString().trim().length()<1){
                        Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                    }else{
                        int hvacid=0;
                        if(roomhvac.size()==0){
                            hvacid=1;
                        }else{
                            hvacid=roomhvac.get(roomhvac.size()-1).hvac_id+1;
                        }
                        ArrayList<Savehvac> tips = new ArrayList<Savehvac>();
                        Savehvac thisroom=new Savehvac(FounctionActivity.roomidfc,hvacid,
                                Integer.parseInt(sub.getText().toString().trim()),Integer.parseInt(dev.getText().toString().trim()),
                                remark.getText().toString().trim());
                        tips.add(thisroom);
                        MainActivity.mgr.addhvac(tips);
                        Toast.makeText(getActivity(), "Add Succeed", Toast.LENGTH_SHORT).show();
                        hvachandler.postDelayed(gethvactag, 30);
                        addalter.dismiss();
                    }

                }
            }

        }
    };
/***************reflash the hvac data*********************/
int step=0;
public void renewdata(){
    /*****renew data*****/
    if(roomhvac.size()!=0){roomhvac.clear();}
    List<Savehvac> alldata=MainActivity.mgr.queryhvac();
    for(int i=0;i<alldata.size();i++){
        if(alldata.get(i).room_id== FounctionActivity.roomidfc){
            roomhvac.add(alldata.get(i));
        }
    }
    adapter=new HvacAdapter(getContext(),roomhvac);
    hvacgrid.setAdapter(adapter);
    hvacgrid.setOnItemClickListener(hvacitemclick);
}
    public AdapterView.OnItemClickListener hvacitemclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            roomhvacdata = roomhvac.get(position);
            hl.setcontant(roomhvacdata);
            hvacview.setVisibility(View.VISIBLE);
            hvacgrid.setVisibility(View.GONE);
            nohvacinfo.setVisibility(View.GONE);
            step=1;
        }
    };
    Runnable gethvactag=new Runnable() {
        @Override
        public void run() {
            renewdata();
            if(roomhvac.size()==1){
                roomhvacdata = roomhvac.get(0);
                hl.setcontant(roomhvacdata);
                hvacview.setVisibility(View.VISIBLE);
                hvacgrid.setVisibility(View.GONE);
                nohvacinfo.setVisibility(View.GONE);
            }else if(roomhvac.size()>1){
                hvacview.setVisibility(View.GONE);
                hvacgrid.setVisibility(View.VISIBLE);
                nohvacinfo.setVisibility(View.GONE);
            }else{
                hvacview.setVisibility(View.GONE);
                hvacgrid.setVisibility(View.GONE);
                nohvacinfo.setVisibility(View.VISIBLE);
            }
            //reflashhandler.postDelayed(HVACReflashRun,20000);
        }
    };
/*****************reflash the ui state*************************/
    Runnable HVACReflashRun=new Runnable() {
    @Override
    public void run() {
        hl.ReflashUI();
        //reflashhandler.postDelayed(HVACReflashRun,20000);
    }
};
    int shakecount=0;
    Handler shakehandler=new Handler();
    int shakehvacvalue=999;
    Runnable shakerun=new Runnable() {
        @Override
        public void run() {
            if(shakecount>=roomhvac.size()){
                shakecount=0;
                shakehvacvalue=999;
                shakehandler.removeCallbacks(shakerun);
            }else{
                accommand.ACControl((byte)roomhvac.get(shakecount).subnetID, (byte)roomhvac.get(shakecount).deviceID, 3, shakehvacvalue,MainActivity.mydupsocket);
                shakecount++;
                shakehandler.postDelayed(shakerun,80);
            }

        }
    };
    public void shakeperform(int shaketype){
        switch (shaketype){
            case 1:
                shakehvacvalue=shaketype-1;
                shakehandler.postDelayed(shakerun,30);
                break;
            case 2:
                shakehvacvalue=shaketype-1;
                shakehandler.postDelayed(shakerun,30);
                break;
            default:break;
        }
    }
    /**********************************监听listener********************************/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));

                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
            // String str="";
            //str=byte2hex(data);
            //hvaclog.append(str);
              //  Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if(rev.length>25){
                    RunReceiveData(rev);
                }
                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                if(step==0){
                    getActivity().finish();
                }else if(step==1){
                    hvacview.setVisibility(View.GONE);
                    hvacgrid.setVisibility(View.VISIBLE);
                    nohvacinfo.setVisibility(View.GONE);
                    step=0;
                }

            }else if(FounctionActivity.ACTION_SHAKE.equals(action)){
                if(!MainActivity.islockshake){
                    int shaketype=(intent.getIntExtra("shake_type",0));
                    shakeperform(shaketype);
                }

            }
        }
    };
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
    boolean doing=false;
    public void RunReceiveData(byte[] data) {
        int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
        if(doing){

        }else{
            doing=true;

            hl.setCurTemperByData(data);

            if (((data[17]&0xff) == hl.getsubnetid()) && ((data[18]&0xff) == hl.getdeviceid())){
                switch(x){
                    case 0xe3d9:
                        switch (data[25]) {
                            case 0x03:
                                if (data[26] == 0) {
                                    hl.setReceiveChange(POWER_OFF, data[26]);
                                }
                                if (data[26] == 1) {
                                    hl.setReceiveChange(POWER_ON, data[26]);
                                }
                                break;
                            case 0x04:
                                hl.setReceiveChange(TEMP, data[26]);
                                break;
                            case 0x05:
                                switch (data[26]) {
                                    case 0:
                                        hl.setReceiveChange(FANSPEED_AUTO, data[26]);
                                        break;
                                    case 1:
                                        hl.setReceiveChange(FANSPEED_HIGH, data[26]);
                                        break;
                                    case 2:
                                        hl.setReceiveChange(FANSPEED_MID, data[26]);
                                        break;
                                    case 3:
                                        hl.setReceiveChange(FANSPEED_LOW, data[26]);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 0x06:
                                switch (data[26]) {
                                    case 0:
                                        hl.setReceiveChange(MODE_COOL, data[26]);
                                        break;
                                    case 1:
                                        hl.setReceiveChange(MODE_HEAT, data[26]);
                                        break;
                                    case 2:
                                        hl.setReceiveChange(MODE_FAN, data[26]);
                                        break;
                                    case 3:
                                        hl.setReceiveChange(MODE_AUTO, data[26]);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 0x07:
                                hl.setReceiveChange(TEMP, data[26]);
                                break;
                            case 0x08:
                                hl.setReceiveChange(TEMP, data[26]);
                                break;
                            default:
                                break;
                        }
                        break;
                    case 0xe121:
                        hl.setCandF(data[25]);
                        break;
                    case 0x1901:
                        byte[] rangevalue={data[25],data[26],data[27],data[28],data[29],data[30]};
                        hl.setACTempRange(rangevalue);
                        break;
                    case 0xe125:
                        hl.setFanSpeedAndModeCount(data);
                        break;
                    case 0xe0ed:
                        try{
                            hl.setACCurrentState(data);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x193b:

                        try{
                            if (data.length==(25+15+2)){
                                // 在增加单个继电器控制空调的功能前,
                                // HVAC中,0X193B的返回长度是13
                                // IR 中 0X193B的返回长度是14 且顺序与HVAC保持一致,后面多出来的参数暂时没用上
                                // 增加单个继电器控制控制空调的0x193B的返回长度有变化是15。

                                // 普通HAVC 不需要执行 if 语句
                                // 继电器控制空调 需要依据最后一个参数的通道号 && 可变参数的长度是15 来判断
                                if (hl.getdeviceid()==(data[39]&0xff)){
                                    hl.setACCurrentStateBy193B(data);
                                }else {
                                    break;
                                }
                            }else {
                                hl.setACCurrentStateBy193B(data);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

            }
            doing=false;
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




}
