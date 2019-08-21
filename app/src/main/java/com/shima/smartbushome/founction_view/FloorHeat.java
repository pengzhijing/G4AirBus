package com.shima.smartbushome.founction_view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.FloorHeatAdapter;
import com.shima.smartbushome.database.Savefloorheat;
import com.shima.smartbushome.selflayout.FloorHeatLayout;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;


public class FloorHeat extends Fragment{
    View view;

    ScrollView sv_FloorHeatView;
    RelativeLayout rl_noFloorHeat;
    GridView gv_FloorHeatList;
    Button bt_AddFloorHeat;
    FloorHeatLayout floorHeatLayout;

    AlertView settingAlter;
    AlertView addAlter;
    AlertView deleteAlter;

    List<Savefloorheat> floorHeatList=new ArrayList<Savefloorheat>();
    FloorHeatAdapter floorHeatAdapter;

    int currPosition=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Floor Heat");
        view = inflater.inflate(R.layout.fragment_floorheat, container, false);
        setHasOptionsMenu(true);


        //初始化视图
        initView();
        //获取数据
        refreshDataList();

        return view;
    }


    public void initView(){
        sv_FloorHeatView= (ScrollView) view.findViewById(R.id.floorheatview);
        rl_noFloorHeat= (RelativeLayout) view.findViewById(R.id.nofloorheatinfo);
        gv_FloorHeatList= (GridView) view.findViewById(R.id.floorheatgrid);
        bt_AddFloorHeat= (Button) view.findViewById(R.id.button46);
        floorHeatLayout= (FloorHeatLayout) view.findViewById(R.id.floorheatkongjian);


        gv_FloorHeatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rl_noFloorHeat.setVisibility(View.GONE);
                sv_FloorHeatView.setVisibility(View.VISIBLE);
                gv_FloorHeatList.setVisibility(View.GONE);

                floorHeatLayout.setFH(floorHeatList.get(position));
                currPosition=position;
            }
        });

        bt_AddFloorHeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加设备
                if(!MainActivity.islockchangeid){
                    addAlter = new AlertView("Add FloorHeat", null, "CANCEL", new String[]{"SAVE"},  null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewadd= getActivity().getLayoutInflater().inflate(R.layout.setting_fhinfo, null);
                    et_sub = (EditText) selfviewadd.findViewById(R.id.acsubedit);
                    et_dev = (EditText) selfviewadd.findViewById(R.id.acdevedit);
                    et_ch= (EditText) selfviewadd.findViewById(R.id.et_channel);
                    et_remark = (EditText) selfviewadd.findViewById(R.id.et_fhremark);
                    et_sub.setText(String.valueOf(0));
                    et_dev.setText(String.valueOf(0));
                    et_ch.setText(String.valueOf(0));
                    et_remark.setText("FloorHeat"+(floorHeatList.size()+1));
                    addAlter.addExtView(selfviewadd);
                    addAlter.setCancelable(false);
                    addAlter.setShoulddismiss(false);
                    addAlter.show();


                }
            }
        });

    }

    //刷新数据
    public void refreshDataList(){

        floorHeatList.clear();
        List<Savefloorheat> allData=MainActivity.mgr.queryfloorheat();
        for(int i=0;i<allData.size();i++){
            if(allData.get(i).room_id== FounctionActivity.roomidfc){
                floorHeatList.add(allData.get(i));
            }
        }
        floorHeatAdapter=new FloorHeatAdapter(getContext(),floorHeatList);
        gv_FloorHeatList.setAdapter(floorHeatAdapter);

        if (floorHeatList.size()>0){
            rl_noFloorHeat.setVisibility(View.GONE);
            sv_FloorHeatView.setVisibility(View.GONE);
            gv_FloorHeatList.setVisibility(View.VISIBLE);
        }else{
            rl_noFloorHeat.setVisibility(View.VISIBLE);
            sv_FloorHeatView.setVisibility(View.GONE);
            gv_FloorHeatList.setVisibility(View.GONE);
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
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.floorheat_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    EditText et_sub;
    EditText et_dev;
    EditText et_ch;
    EditText et_remark;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.floorheat_setting://设置
                if(!MainActivity.islockchangeid){
                    settingAlter = new AlertView("Settings", null, "CANCEL", new String[]{"SAVE"},  null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewadd= getActivity().getLayoutInflater().inflate(R.layout.setting_fhinfo, null);
                    et_sub = (EditText) selfviewadd.findViewById(R.id.acsubedit);
                    et_dev = (EditText) selfviewadd.findViewById(R.id.acdevedit);
                    et_ch= (EditText) selfviewadd.findViewById(R.id.et_channel);
                    et_remark = (EditText) selfviewadd.findViewById(R.id.et_fhremark);
                    if (floorHeatList.size()==0){
                        Toast.makeText(getContext(), "Please add a device first", Toast.LENGTH_SHORT).show();
                        break;
                    }else{
                        if (currPosition<0){
                            et_sub.setText(""+floorHeatList.get(0).subnetID);
                            et_dev.setText(""+floorHeatList.get(0).deviceID);
                            et_ch.setText(""+floorHeatList.get(0).channel);
                            et_remark.setText(""+floorHeatList.get(0).floorheat_remark);
                        }else{
                            et_sub.setText(""+floorHeatList.get(currPosition).subnetID);
                            et_dev.setText(""+floorHeatList.get(currPosition).deviceID);
                            et_ch.setText(""+floorHeatList.get(currPosition).channel);
                            et_remark.setText(""+floorHeatList.get(currPosition).floorheat_remark);
                        }
                    }


                    settingAlter.addExtView(selfviewadd);
                    settingAlter.setCancelable(false);
                    settingAlter.setShoulddismiss(false);
                    settingAlter.show();

                }
                break;
            case R.id.floorheat_add://添加
                if(!MainActivity.islockchangeid){
                    addAlter = new AlertView("Add FloorHeat", null, "CANCEL", new String[]{"SAVE"},  null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewadd= getActivity().getLayoutInflater().inflate(R.layout.setting_fhinfo, null);
                    et_sub = (EditText) selfviewadd.findViewById(R.id.acsubedit);
                    et_dev = (EditText) selfviewadd.findViewById(R.id.acdevedit);
                    et_ch= (EditText) selfviewadd.findViewById(R.id.et_channel);
                    et_remark = (EditText) selfviewadd.findViewById(R.id.et_fhremark);
                    et_sub.setText(String.valueOf(0));
                    et_dev.setText(String.valueOf(0));
                    et_ch.setText(String.valueOf(0));
                    et_remark.setText("FloorHeat"+(floorHeatList.size()+1));
                    addAlter.addExtView(selfviewadd);
                    addAlter.setCancelable(false);
                    addAlter.setShoulddismiss(false);
                    addAlter.show();


                }
                break;

            case R.id.floorheat_remove://删除
                if(!MainActivity.islockchangeid){

                    deleteAlter = new AlertView("Select FloorHeat to Delete", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    ViewGroup extView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.listdialog, null);
                    ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
                    extView.setLayoutParams(lp);
                    final String[] mItems=new String[floorHeatList.size()] ;
                    for(int i=0;i<floorHeatList.size();i++){
                        mItems[i]=floorHeatList.get(i).floorheat_remark;
                    }
                    deletelist.setAdapter(new ArrayAdapter(getActivity(),
                            R.layout.simplelistitem, mItems));
                    deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MainActivity.mgr.deletefloorheat("floorheat", floorHeatList.get(position)._id, FounctionActivity.roomidfc);
                            deleteSharedPreferences(floorHeatList.get(position));
                            Toast.makeText(getActivity(), "Delete Succeed", Toast.LENGTH_SHORT).show();
                            deleteAlter.dismiss();
                            //刷新数据
                            refreshDataList();
                        }
                    });
                    deleteAlter.addExtView(extView);
                    deleteAlter.show();

                }

                break;
            default:break;

        }
        return super.onOptionsItemSelected(item);
    }

    //删除FloorHeatC页面当前温度相关数据
    public void deleteSharedPreferences(Savefloorheat floorHeat){
        String  CurTemperType="floorHeatCurTemperType"+floorHeat._id;
        String ZoneBeastSubID="floorHeatZoneBeastSubID"+floorHeat._id;
        String  ZoneBeastDevID="floorHeatZoneBeastDevID"+floorHeat._id;
        String  SensorSubID="floorHeatSensorSubID"+floorHeat._id;
        String SensorDevID="floorHeatSensorDevID"+floorHeat._id;
        String    FTSubID="floorHeatFTSubID"+floorHeat._id;
        String  FTDevID="floorHeatFTDevID"+floorHeat._id;

        SharedPreferencesHelper.init(getContext());
        SharedPreferencesHelper.getInstance().deleteData(CurTemperType);
        SharedPreferencesHelper.getInstance().deleteData(ZoneBeastSubID);
        SharedPreferencesHelper.getInstance().deleteData(ZoneBeastDevID);
        SharedPreferencesHelper.getInstance().deleteData(SensorSubID);
        SharedPreferencesHelper.getInstance().deleteData(SensorDevID);
        SharedPreferencesHelper.getInstance().deleteData(FTSubID);
        SharedPreferencesHelper.getInstance().deleteData(FTDevID);
    }

    //对话框按钮事件
    public com.bigkoo.alertview.OnItemClickListener itemclick= new com.bigkoo.alertview.OnItemClickListener() {
        @Override
        public void onItemClick(Object o, int position) {

            if (o==settingAlter){
                switch (position){
                    case 0://修改设备信息
                        int subnetID=-1;
                        int deviceID=-1;
                        int channelNo=-1;
                        String deviceName="";
                        try{
                            subnetID=Integer.parseInt(et_sub.getText().toString().trim());
                            deviceID=Integer.parseInt(et_dev.getText().toString().trim());
                            channelNo=Integer.parseInt(et_ch.getText().toString().trim());
                            deviceName=et_remark.getText().toString();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (subnetID<0||subnetID>255||deviceID<0||deviceID>255||channelNo<0||channelNo>255){
                            Toast.makeText(getActivity(), "Please enter a valid value", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        Savefloorheat floorheat;
                        if (currPosition<0){
                            floorheat=floorHeatList.get(0);
                        }else{
                            floorheat=floorHeatList.get(currPosition);
                        }

                        floorheat.room_id=FounctionActivity.roomidfc;
                        floorheat.subnetID=subnetID;
                        floorheat.deviceID=deviceID;
                        floorheat.channel=channelNo;
                        floorheat.floorheat_remark=deviceName;

                        MainActivity.mgr.updatefloorheat(floorheat);
                        Toast.makeText(getActivity(), "Update Succeed", Toast.LENGTH_SHORT).show();
                        settingAlter.dismiss();

                        //刷新数据
                        floorHeatList.clear();
                        List<Savefloorheat> allData=MainActivity.mgr.queryfloorheat();
                        for(int i=0;i<allData.size();i++){
                            if(allData.get(i).room_id== FounctionActivity.roomidfc){
                                floorHeatList.add(allData.get(i));
                            }
                        }
                        floorHeatAdapter=new FloorHeatAdapter(getContext(),floorHeatList);
                        gv_FloorHeatList.setAdapter(floorHeatAdapter);

                        //刷新控制页面的数据
                        if (currPosition>=0){
                            floorHeatLayout.setFH(floorHeatList.get(currPosition));
                        }

                        break;
                }
            }


           if (o==addAlter){
               switch (position){
                   case 0://添加设备
                       int subnetID=-1;
                       int deviceID=-1;
                       int channelNo=-1;
                       String deviceName="";
                       try{
                           subnetID=Integer.parseInt(et_sub.getText().toString().trim());
                           deviceID=Integer.parseInt(et_dev.getText().toString().trim());
                           channelNo=Integer.parseInt(et_ch.getText().toString().trim());
                           deviceName=et_remark.getText().toString();
                       }catch (Exception e){
                           e.printStackTrace();
                       }

                       if (subnetID<0||subnetID>255||deviceID<0||deviceID>255||channelNo<0||channelNo>255){
                           Toast.makeText(getActivity(), "Please enter a valid value", Toast.LENGTH_SHORT).show();
                           break;
                       }

                       ArrayList<Savefloorheat> tips = new ArrayList<Savefloorheat>();
                       Savefloorheat floorheat=new Savefloorheat();
                       floorheat.room_id=FounctionActivity.roomidfc;
                       floorheat.subnetID=subnetID;
                       floorheat.deviceID=deviceID;
                       floorheat.channel=channelNo;
                       floorheat.floorheat_remark=deviceName;
                       tips.add(floorheat);
                       MainActivity.mgr.addfloorheat(tips);
                       Toast.makeText(getActivity(), "Add Succeed", Toast.LENGTH_SHORT).show();
                       addAlter.dismiss();
                       //刷新数据
                       refreshDataList();

                       break;
               }
           }
        }
    };



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
                if (sv_FloorHeatView.getVisibility()==View.VISIBLE){
                    rl_noFloorHeat.setVisibility(View.GONE);
                    sv_FloorHeatView.setVisibility(View.GONE);
                    gv_FloorHeatList.setVisibility(View.VISIBLE);

                    currPosition=-1;
                }else{
                    getActivity().finish();
                }

            }
        }
    };

    //解析数据
    public void RunReceiveData(byte[] data) {
        floorHeatLayout.setFeedBackData(data);

        //int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
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
