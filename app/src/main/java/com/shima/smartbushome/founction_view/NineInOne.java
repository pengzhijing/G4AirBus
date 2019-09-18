package com.shima.smartbushome.founction_view;


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
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.NioAdapter;
import com.shima.smartbushome.database.Savenio;
import com.shima.smartbushome.selflayout.NioLayout;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NineInOne extends Fragment implements View.OnClickListener{
    View view;
    Handler getdatahandler=new Handler();
    List<Savenio> roomniolist=new ArrayList<>();
    Savenio thisniodevice=new Savenio();
    RelativeLayout nonioinfo;
    GridView niogrid;
    NioLayout niocontrolview;
    Button button51;
    NioAdapter adapter;
    int step=0;
    AlertView addalter,deletealter,settingalter,pairalter;
    public NineInOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("9 in 1");
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_nineinone, container, false);
        nonioinfo=(RelativeLayout)view.findViewById(R.id.nonioinfo);
        niogrid=(GridView)view.findViewById(R.id.niogrid);
        niocontrolview=(NioLayout)view.findViewById(R.id.niocontrolview);
        button51=(Button)view.findViewById(R.id.button51);
        button51.setOnClickListener(this);

    /*    SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("niobgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.nioout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        getdatahandler.postDelayed(getdatarun, 20);
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
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nio_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            renewdata();
            if(roomniolist.size()==1){
                thisniodevice=roomniolist.get(0);
                niocontrolview.setcontent(thisniodevice);
                niogrid.setVisibility(View.GONE);
                niocontrolview.setVisibility(View.VISIBLE);
                nonioinfo.setVisibility(View.GONE);
            }else if(roomniolist.size()>1){
                nonioinfo.setVisibility(View.GONE);
                niogrid.setVisibility(View.VISIBLE);
                niocontrolview.setVisibility(View.GONE);
            }else{
                nonioinfo.setVisibility(View.VISIBLE);
                niogrid.setVisibility(View.GONE);
                niocontrolview.setVisibility(View.GONE);
            }
        }
    };
    public void renewdata(){
        /*****renew data*****/
        if(roomniolist.size()!=0){roomniolist.clear();}
        List<Savenio> allnio= MainActivity.mgr.querynio();
        for(int i=0;i<allnio.size();i++){
            if(allnio.get(i).room_id==FounctionActivity.roomidfc){
                roomniolist.add(allnio.get(i));
            }
        }
        adapter=new NioAdapter(getActivity(), roomniolist);
        niogrid.setAdapter(adapter);
        niogrid.setOnItemClickListener(nioitemclick);
    }

    public AdapterView.OnItemClickListener nioitemclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            thisniodevice=roomniolist.get(position);
            niocontrolview.setcontent(thisniodevice);
            nonioinfo.setVisibility(View.GONE);
            niogrid.setVisibility(View.GONE);
            niocontrolview.setVisibility(View.VISIBLE);
            step=1;
        }
    };
    EditText sub,dev,remark;
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button51:
                addalter = new AlertView("Add 9in1", null, "CANCEL",  new String[]{"SAVE"},null , getActivity(), AlertView.Style.Alert,
                        itemclick);
                View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                sub = (EditText) selfview.findViewById(R.id.acsubedit);
                dev = (EditText) selfview.findViewById(R.id.acdevedit);
                remark = (EditText) selfview.findViewById(R.id.acremark);
                sub.setText(String.valueOf(0));
                dev.setText(String.valueOf(0));
                if(roomniolist.size()==0){
                    remark.setText("9in1device"+(1));
                }else{
                    remark.setText("9in1device"+(roomniolist.get(roomniolist.size()-1).nio_id+1));
                }
                addalter.addExtView(selfview);
                addalter.setCancelable(false);
                addalter.setShoulddismiss(false);
                addalter.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_nio:
                if(!MainActivity.islockchangeid){
                    addalter = new AlertView("Add 9in1", null, "CANCEL",  new String[]{"SAVE"},null , getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfview.findViewById(R.id.acsubedit);
                    dev = (EditText) selfview.findViewById(R.id.acdevedit);
                    remark = (EditText) selfview.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(0));
                    dev.setText(String.valueOf(0));
                    if(roomniolist.size()==0){
                        remark.setText("9in1device"+(1));
                    }else{
                        remark.setText("9in1device"+(roomniolist.get(roomniolist.size()-1).nio_id+1));
                    }
                    addalter.addExtView(selfview);
                    addalter.setCancelable(false);
                    addalter.setShoulddismiss(false);
                    addalter.show();
                }

                break;
            case R.id.delete_nio:
                if(!MainActivity.islockchangeid){
                    final String[] mItems=new String[roomniolist.size()] ;
                    for(int i=0;i<roomniolist.size();i++){
                        mItems[i]=roomniolist.get(i).nio_remark;
                    }
                    deletealter = new AlertView("Select 9in1 to Delete", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
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
                            MainActivity.mgr.deletenio("nio", roomniolist.get(position).nio_id, FounctionActivity.roomidfc);
                            Toast.makeText(getActivity(), "Delete Succeed", Toast.LENGTH_SHORT).show();
                            getdatahandler.postDelayed(getdatarun, 30);
                            deletealter.dismiss();
                        }
                    });
                    deletealter.addExtView(extView);
                    deletealter.show();
                }

                break;
            case R.id.setting_nio:
                if(!MainActivity.islockchangeid){
                    settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewx= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfviewx.findViewById(R.id.acsubedit);
                    dev = (EditText) selfviewx.findViewById(R.id.acdevedit);
                    remark = (EditText) selfviewx.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(thisniodevice.subnetID));
                    dev.setText(String.valueOf(thisniodevice.deviceID));
                    remark.setText(thisniodevice.nio_remark);
                    settingalter.addExtView(selfviewx);
                    settingalter.show();
                }

                break;
            case R.id.autopair_nio:
                if(!MainActivity.islockchangeid){
                    pairalter = new AlertView("Select Device", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewx2= getActivity().getLayoutInflater().inflate(R.layout.auto_pair_dialog, null);
                    ListView test=(ListView)selfviewx2.findViewById(R.id.listView4);
                    DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(getActivity(),MainActivity.netdeviceList);
                    test.setAdapter(mLeDeviceListAdapter);
                    test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Savenio data = new Savenio();
                            data.room_id = FounctionActivity.roomidfc;
                            data.nio_id = thisniodevice.nio_id;
                            data.nio_remark = thisniodevice.nio_remark;
                            data.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            data.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            MainActivity.mgr.updateniosetting(data);
                            renewdata();
                            thisniodevice.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            thisniodevice.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            niocontrolview.setcontent(thisniodevice);
                            Toast.makeText(getActivity(), "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                            pairalter.dismiss();
                        }
                    });
                    pairalter.addExtView(selfviewx2);
                    pairalter.show();
                }

                break;
            default:break;

        }
        return super.onOptionsItemSelected(item);
    }

    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==addalter){
                if(position==0){
                    if(remark.getText().toString().trim().length()<1){
                        Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                    }else{
                        int nioid=0;
                        if(roomniolist.size()==0){
                            nioid=1;
                        }else{
                            nioid=roomniolist.get(roomniolist.size()-1).nio_id+1;
                        }
                        Savenio thisroom=new Savenio(FounctionActivity.roomidfc, Integer.parseInt(sub.getText().toString().trim()),
                                Integer.parseInt(dev.getText().toString().trim()), nioid,
                                remark.getText().toString().trim(),"bt1","bt2","bt3","bt4","bt5","bt6","bt7",
                                "bt8","up","down","left","right","ok","on","off",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0);
                        MainActivity.mgr.addnio(thisroom);
                        Toast.makeText(getActivity(), "Add Succeed", Toast.LENGTH_SHORT).show();
                        getdatahandler.postDelayed(getdatarun, 30);
                        addalter.dismiss();
                    }

                }
            }else if(o==settingalter){
                if(position==0){
                    Savenio data=new Savenio();
                    data.room_id=FounctionActivity.roomidfc;
                    data.nio_id=thisniodevice.nio_id;
                    data.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    data.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    data.nio_remark=remark.getText().toString().trim();
                    MainActivity.mgr.updateniosetting(data);
                    renewdata();
                    thisniodevice.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    thisniodevice.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    thisniodevice.nio_remark=remark.getText().toString().trim();
                    niocontrolview.setcontent(thisniodevice);
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
                if(step==0){
                    getActivity().finish();
                }else if(step==1){
                    niogrid.setVisibility(View.VISIBLE);
                    niocontrolview.setVisibility(View.GONE);
                    nonioinfo.setVisibility(View.GONE);
                    step=0;
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
       // int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
        if(doing){

        }else{
            doing=true;

            niocontrolview.receivedata(data);

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
