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
import com.shima.smartbushome.assist.Adapter.MediaAdapter;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.selflayout.MediaType1;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Media extends Fragment implements View.OnClickListener{
    View view;
    Handler getdatahandler=new Handler();
    RelativeLayout nomediainfo;
    GridView mediagrid;
    MediaType1 mediacontrolview;
    Button button45;
    int step=0;
    List<Savemedia> roommedia=new ArrayList<>();
    Savemedia roommediadata=new Savemedia();
    MediaAdapter adapter;
    String defaultmediaicon="media_icon1";
    public Media() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_media, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Media");
        nomediainfo=(RelativeLayout)view.findViewById(R.id.nomediainfo);
        mediagrid=(GridView)view.findViewById(R.id.mediagrid);
        mediacontrolview=(MediaType1)view.findViewById(R.id.mediacontrolview);
        button45=(Button)view.findViewById(R.id.button45);
        button45.setOnClickListener(this);

       /* SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("mediabgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.mediaout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        getdatahandler.postDelayed(getdatarun,20);
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
        inflater.inflate(R.menu.media_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button45:
                addalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"},null , getActivity(), AlertView.Style.Alert,
                        itemclick);
                View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                sub = (EditText) selfview.findViewById(R.id.acsubedit);
                dev = (EditText) selfview.findViewById(R.id.acdevedit);
                remark = (EditText) selfview.findViewById(R.id.acremark);
                sub.setText(String.valueOf(0));
                dev.setText(String.valueOf(0));
                if(roommedia.size()==0){
                    remark.setText("Media"+(1));
                }else{
                    remark.setText("Media"+(roommedia.get(roommedia.size()-1).media_id+1));
                }
                addalter.addExtView(selfview);
                addalter.setCancelable(false);
                addalter.setShoulddismiss(false);
                addalter.show();
                break;
        }
    }
    AlertView settingalter,pairalter,addalter,deletealter;
    EditText sub,dev,remark;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mediasetting:
                if(!MainActivity.islockchangeid){
                    settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfview= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfview.findViewById(R.id.acsubedit);
                    dev = (EditText) selfview.findViewById(R.id.acdevedit);
                    remark = (EditText) selfview.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(roommediadata.subnetID));
                    dev.setText(String.valueOf(roommediadata.deviceID));
                    remark.setText(roommediadata.media_statement);
                    settingalter.addExtView(selfview);
                    settingalter.show();
                }

                break;
            case R.id.mediapair:
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
                            Savemedia data = new Savemedia();
                            data.room_id = FounctionActivity.roomidfc;
                            data.media_id = roommediadata.media_id;
                            data.media_statement = roommediadata.media_statement;
                            data.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            data.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            MainActivity.mgr.updatemedia(data);
                            renewdata();
                            roommediadata.subnetID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                            roommediadata.deviceID = Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                            mediacontrolview.setcontent(roommediadata);
                            Toast.makeText(getActivity(), "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                            pairalter.dismiss();
                        }
                    });
                    pairalter.addExtView(selfviewx);
                    pairalter.show();
                }

                break;
            case R.id.media_add:
                if(!MainActivity.islockchangeid){
                    addalter = new AlertView("Settings", null, "CANCEL", new String[]{"SAVE"},  null, getActivity(), AlertView.Style.Alert,
                            itemclick);
                    View selfviewadd= getActivity().getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                    sub = (EditText) selfviewadd.findViewById(R.id.acsubedit);
                    dev = (EditText) selfviewadd.findViewById(R.id.acdevedit);
                    remark = (EditText) selfviewadd.findViewById(R.id.acremark);
                    sub.setText(String.valueOf(0));
                    dev.setText(String.valueOf(0));
                    if(roommedia.size()==0){
                        remark.setText("Media"+(1));
                    }else{
                        remark.setText("Media"+(roommedia.get(roommedia.size()-1).media_id+1));
                    }
                    addalter.addExtView(selfviewadd);
                    addalter.setCancelable(false);
                    addalter.setShoulddismiss(false);
                    addalter.show();
                }

                break;
            case R.id.media_delete:
                if(!MainActivity.islockchangeid){
                    final String[] mItems=new String[roommedia.size()] ;
                    for(int i=0;i<roommedia.size();i++){
                        mItems[i]=roommedia.get(i).media_statement;
                    }
                    deletealter = new AlertView("Select Media to Delete", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
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
                            MainActivity.mgr.deletemedia("media", roommedia.get(position).media_id, FounctionActivity.roomidfc);
                            MainActivity.mgr.deletemediabutton("mediabutton", roommedia.get(position).media_id, FounctionActivity.roomidfc);
                            Toast.makeText(getActivity(), "Delete Succeed", Toast.LENGTH_SHORT).show();
                            getdatahandler.postDelayed(getdatarun, 30);
                            deletealter.dismiss();
                        }
                    });
                    deletealter.addExtView(extView);
                    deletealter.show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void renewdata(){
        /*****renew data*****/
        if(roommedia.size()!=0){roommedia.clear();}
        List<Savemedia> allmedia= MainActivity.mgr.querymedia();
        for(int i=0;i<allmedia.size();i++){
            if(allmedia.get(i).room_id==FounctionActivity.roomidfc){
                roommedia.add(allmedia.get(i));
            }
        }
        adapter=new MediaAdapter(getActivity(), roommedia);
        mediagrid.setAdapter(adapter);
        mediagrid.setOnItemClickListener(mediaitemclick);
    }
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            renewdata();
            if(roommedia.size()==1){
                roommediadata=roommedia.get(0);
                mediacontrolview.setcontent(roommediadata);
                mediagrid.setVisibility(View.GONE);
                mediacontrolview.setVisibility(View.VISIBLE);
                nomediainfo.setVisibility(View.GONE);
            }else if(roommedia.size()>1){
                nomediainfo.setVisibility(View.GONE);
                mediagrid.setVisibility(View.VISIBLE);
                mediacontrolview.setVisibility(View.GONE);
            }else{
                nomediainfo.setVisibility(View.VISIBLE);
                mediagrid.setVisibility(View.GONE);
                mediacontrolview.setVisibility(View.GONE);
            }
        }
    };

    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==settingalter){
                if(position==0){
                    Savemedia data=new Savemedia();
                    data.room_id=FounctionActivity.roomidfc;
                    data.media_id=roommediadata.media_id;
                    data.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    data.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    data.media_statement=remark.getText().toString().trim();
                    data.media_icon=defaultmediaicon;
                    MainActivity.mgr.updatemedia(data);
                    renewdata();
                    roommediadata.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    roommediadata.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    roommediadata.media_statement=remark.getText().toString().trim();
                    mediacontrolview.setcontent(roommediadata);
                }
            }else if(o==addalter){
                if(position==0){
                    if(remark.getText().toString().trim().length()<1){
                        Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                    }else{
                        int mediaid=0;
                        if(roommedia.size()==0){
                            mediaid=1;
                        }else{
                            mediaid=roommedia.get(roommedia.size()-1).media_id+1;
                        }
                        Savemedia thisroom=new Savemedia(FounctionActivity.roomidfc, Integer.parseInt(sub.getText().toString().trim()),
                                Integer.parseInt(dev.getText().toString().trim()), mediaid,
                                remark.getText().toString().trim(),defaultmediaicon);
                        MainActivity.mgr.addmedia(thisroom);

                        List<Savemediabutton> buttondata=new ArrayList<>();
                        for(int i=0;i<30;i++){
                            Savemediabutton buttonn=new Savemediabutton(FounctionActivity.roomidfc,mediaid,i,0,255,1,0);
                            buttondata.add(buttonn);
                        }
                        MainActivity.mgr.addmediabutton(buttondata);
                        Toast.makeText(getActivity(), "Add Succeed", Toast.LENGTH_SHORT).show();
                        getdatahandler.postDelayed(getdatarun, 30);
                        addalter.dismiss();
                    }

                }
            }

        }
    };


    public AdapterView.OnItemClickListener mediaitemclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            roommediadata=roommedia.get(position);
            mediacontrolview.setcontent(roommediadata);
            nomediainfo.setVisibility(View.GONE);
            mediagrid.setVisibility(View.GONE);
            mediacontrolview.setVisibility(View.VISIBLE);
            step=1;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                if(step==0){
                    getActivity().finish();
                }else if(step==1){
                    mediagrid.setVisibility(View.VISIBLE);
                    mediacontrolview.setVisibility(View.GONE);
                    nomediainfo.setVisibility(View.GONE);
                    step=0;
                }
            }else if(FounctionActivity.ACTION_SHAKE.equals(action)){
                int shaketype=(intent.getIntExtra("shake_type",0));

            }
        }
    };
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }
}
