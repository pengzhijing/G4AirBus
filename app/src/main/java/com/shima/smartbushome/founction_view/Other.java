package com.shima.smartbushome.founction_view;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.OnoffTypeAdapter;
import com.shima.smartbushome.database.Saveother;
import com.shima.smartbushome.founction_command.othercontrol;
import com.shima.smartbushome.selflayout.OtherType1;
import com.shima.smartbushome.selflayout.OtherType2;
import com.shima.smartbushome.selflayout.OtherType3;
import com.shima.smartbushome.selflayout.OtherType4;
import com.shima.smartbushome.selflayout.OtherType5;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Other extends Fragment {
    View view;
    //TextView log;
    MenuItem add,delete;
    Handler onoffhandle=new Handler();
    Handler reflashroomonoff=new Handler();
    public List<OtherType1> type1list= new ArrayList<OtherType1>();
    public List<OtherType2> type2list= new ArrayList<OtherType2>();
    public List<OtherType3> type3list= new ArrayList<OtherType3>();
    public List<OtherType4> type4list= new ArrayList<OtherType4>();
    public List<OtherType5> type5list= new ArrayList<OtherType5>();
    List<Saveother> roomonoff= new ArrayList<Saveother>();
    LinearLayout othercontrollayout;
    boolean intodeletemode=false;
    othercontrol reflash;
    Dialog addonoffdialog;
    public static Context lightcontext;
    public Other() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_other, container, false);
        othercontrollayout=(LinearLayout)view.findViewById(R.id.otherlinearLayout);
        getActivity().setTitle("Other");
        setHasOptionsMenu(true);

      /*  SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("otherbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.otherout);
        roomacbg.setBackgroundColor(backgroudcolor);
*/
        reflash=new othercontrol();
        onoffhandle.postDelayed(getonofflist, 30);
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
        reflashroomonoff.removeCallbacks(reflashroomonoffrun);
        onoffhandle.removeCallbacks(getonofflist);
        type1list.clear();
        roomonoff.clear();
        intodeletemode=false;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.light_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        add= menu.findItem(R.id.light_add);
        delete= menu.findItem(R.id.light_remove);
    }

    AlertView addingalter;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.light_add:
                if(!MainActivity.islockchangeid){
                    if(intodeletemode){
                        add.setTitle("ADD");
                        delete.setTitle("DELETE");
                        intodeletemode=false;
                        for(int i=0;i<type1list.size();i++){
                            type1list.get(i).setdeletevisable(false);
                        }
                        for(int i=0;i<type2list.size();i++){
                            type2list.get(i).setdeletevisable(false);
                        }
                        for(int i=0;i<type3list.size();i++){
                            type3list.get(i).setdeletevisable(false);
                        }
                        for(int i=0;i<type4list.size();i++){
                            type4list.get(i).setdeletevisable(false);
                        }
                        for(int i=0;i<type5list.size();i++){
                            type5list.get(i).setdeletevisable(false);
                        }
                    }else{
                        final ListView typelist=new ListView(getActivity());
                        addingalter = new AlertView(null, null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                                itemclick);
                        typelist.setAdapter(new OnoffTypeAdapter(getActivity()));
                        typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                int onoff_id;
                                if (roomonoff.size() == 0) {
                                    onoff_id = 1;
                                } else {
                                    onoff_id = roomonoff.get(roomonoff.size() - 1).other_id + 1;
                                }
                                ArrayList<Saveother> tips = new ArrayList<Saveother>();
                                switch (position) {
                                    case 0:
                                        Saveother type1 = new Saveother(FounctionActivity.roomidfc,
                                                0, 0, onoff_id, 0, 0, "single" + onoff_id, "other_icon1", 1);
                                        tips.add(type1);
                                        MainActivity.mgr.addother(tips);
                                        break;
                                    case 1:
                                        Saveother type2 = new Saveother(FounctionActivity.roomidfc,
                                                0, 0, onoff_id, 0, 0, "interlock" + onoff_id, "other_icon8", 2);
                                        tips.add(type2);
                                        MainActivity.mgr.addother(tips);
                                        break;
                                    case 2:
                                        Saveother type3 = new Saveother(FounctionActivity.roomidfc,
                                                0, 0, onoff_id, 0, 0, "logic" + onoff_id, "other_icon3", 3);
                                        tips.add(type3);
                                        MainActivity.mgr.addother(tips);
                                        break;
                                    case 3:
                                        Saveother type4 = new Saveother(FounctionActivity.roomidfc,
                                                0, 0, onoff_id, 0, 0, "scene" + onoff_id, "other_icon2", 4);
                                        tips.add(type4);
                                        MainActivity.mgr.addother(tips);
                                        break;
                                    case 4:
                                        Saveother type5 = new Saveother(FounctionActivity.roomidfc,
                                                0, 0, onoff_id, 0, 0, "sequence" + onoff_id, "other_icon7", 5);
                                        tips.add(type5);
                                        MainActivity.mgr.addother(tips);
                                        break;
                                    default:
                                        break;
                                }
                                onoffhandle.postDelayed(getonofflist, 30);
                                addingalter.dismiss();
                            }
                        });
                        addingalter.addExtView(typelist);
                        addingalter.show();
                    }
                }

                break;
            case R.id.light_remove:
                if(!MainActivity.islockchangeid){
                    intodeletemode=!intodeletemode;
                    if(intodeletemode){
                        add.setTitle("CANCLE DELETE");
                        delete.setTitle("DELETE");
                        if(type1list.size()>0){type1list.clear();}
                        if(type2list.size()>0){type2list.clear();}
                        if(type3list.size()>0){type3list.clear();}
                        if(type4list.size()>0){type4list.clear();}
                        if(type5list.size()>0){type5list.clear();}
                        for(int i=0;i<roomonoff.size();i++){
                            switch(roomonoff.get(i).other_type){
                                case 1:
                                    OtherType1 x=(OtherType1)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                                    type1list.add(x);
                                    x.setdeletevisable(true);
                                    break;
                                case 2:
                                    OtherType2 x2=(OtherType2)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                                    type2list.add(x2);
                                    x2.setdeletevisable(true);
                                    break;
                                case 3:
                                    OtherType3 x3=(OtherType3)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                                    type3list.add(x3);
                                    x3.setdeletevisable(true);
                                    break;
                                case 4:
                                    OtherType4 x4=(OtherType4)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                                    type4list.add(x4);
                                    x4.setdeletevisable(true);
                                    break;
                                case 5:
                                    OtherType5 x5=(OtherType5)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                                    type5list.add(x5);
                                    x5.setdeletevisable(true);
                                    break;
                                default:break;
                            }

                        }
                    }else{
                        for(int i=0;i<type1list.size();i++){
                            if(type1list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deleteother("other", type1list.get(i).getType1otherid(), FounctionActivity.roomidfc);
                            }
                        }
                        for(int i=0;i<type2list.size();i++){
                            if(type2list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deleteother("other", type2list.get(i).getType2otherid(), FounctionActivity.roomidfc);
                            }
                        }
                        for(int i=0;i<type3list.size();i++){
                            if(type3list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deleteother("other", type3list.get(i).getType3otherid(), FounctionActivity.roomidfc);
                            }
                        }
                        for(int i=0;i<type4list.size();i++){
                            if(type4list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deleteother("other", type4list.get(i).getType4otherid(), FounctionActivity.roomidfc);
                            }
                        }
                        for(int i=0;i<type5list.size();i++){
                            if(type5list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deleteother("other", type5list.get(i).getType4otherid(), FounctionActivity.roomidfc);
                            }
                        }
                        onoffhandle.postDelayed(getonofflist,30);
                        add.setTitle("ADD");
                        delete.setTitle("DELETE");
                        intodeletemode=false;
                    }
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

                }
            }

        }
    };
    /*****************reflash the list*****************/
    Runnable getonofflist=new Runnable() {
        @Override
        public void run() {
            if(roomonoff.size()>0){
                roomonoff.clear();
            }
            othercontrollayout.removeAllViews();
            List<Saveother> alldata=MainActivity.mgr.queryother();
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id== FounctionActivity.roomidfc){
                    roomonoff.add(alldata.get(i));
                }
            }

            for(int i=0;i<roomonoff.size();i++){
                addspecView(roomonoff.get(i));
            }

            for(int i=0;i<roomonoff.size();i++){
                switch(roomonoff.get(i).other_type){
                    case 1:
                        OtherType1 x=(OtherType1)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                        type1list.add(x);
                        break;
                    case 2:
                        OtherType2 x2=(OtherType2)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                        type2list.add(x2);
                        break;
                    case 3:
                        OtherType3 x3=(OtherType3)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                        type3list.add(x3);
                        break;
                    case 4:
                        OtherType4 x4=(OtherType4)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                        type4list.add(x4);
                        break;
                    case 5:
                        OtherType5 x5=(OtherType5)othercontrollayout.findViewById(roomonoff.get(i).other_id);
                        type5list.add(x5);
                        break;
                    default:break;
                }

            }
            reflashroomlight();
            // reflashhandler.postDelayed(reflashuiRun, 20000);
            onoffhandle.removeCallbacks(getonofflist);
        }
    };

    private void addspecView(Saveother lg) {
        switch(lg.other_type){
            case 1:
                OtherType1 lv=new OtherType1(othercontrollayout.getContext());
                lv.setcontan(lg);
                lv.setId(lg.other_id);
                othercontrollayout.addView(lv);
                break;
            case 2:
                OtherType2 lv2=new OtherType2(othercontrollayout.getContext());
                lv2.setcontan(lg);
                lv2.setId(lg.other_id);
                othercontrollayout.addView(lv2);
                break;
            case 3:
                OtherType3 lv3=new OtherType3(othercontrollayout.getContext());
                lv3.setcontan(lg);
                lv3.setId(lg.other_id);
                othercontrollayout.addView(lv3);
                break;
            case 4:
                OtherType4 lv4=new OtherType4(othercontrollayout.getContext());
                lv4.setcontan(lg);
                lv4.setId(lg.other_id);
                othercontrollayout.addView(lv4);
                break;
            case 5:
                OtherType5 lv5=new OtherType5(othercontrollayout.getContext());
                lv5.setcontan(lg);
                lv5.setId(lg.other_id);
                othercontrollayout.addView(lv5);
                break;
            default:break;
        }

    }
    /**************reflash the ui value******************/

    Runnable reflashuiRun=new Runnable() {
        @Override
        public void run() {
            reflashroomlight();
            //reflashhandler.postDelayed(reflashuiRun, 20000);
        }
    };
    byte sub=0,dev=0,count=0;
    Runnable reflashroomonoffrun=new Runnable() {
        @Override
        public void run() {
            if(count<roomonoff.size()){
                if(roomonoff.get(count).subnetID!=(sub&0xff)){
                    sub=(byte)roomonoff.get(count).subnetID;
                    dev=(byte)roomonoff.get(count).deviceID;

                    if (roomonoff.get(count).other_type==1){
                        reflash.getDryContectState(sub, dev,MainActivity.mydupsocket);//读取干接点状态
                    }else{
                        reflash.getonoffstate(sub, dev,MainActivity.mydupsocket);
                    }
                }else if(roomonoff.get(count).deviceID!=(dev&0xff)){
                    sub=(byte)roomonoff.get(count).subnetID;
                    dev=(byte)roomonoff.get(count).deviceID;

                    if (roomonoff.get(count).other_type==1){
                        reflash.getDryContectState(sub, dev,MainActivity.mydupsocket);//读取干接点状态
                    }else{
                        reflash.getonoffstate(sub, dev,MainActivity.mydupsocket);
                    }
                }


                count++;
                reflashroomonoff.postDelayed(reflashroomonoffrun,70);
            }else{
                reflashroomonoff.removeCallbacks(reflashroomonoffrun);
                sub=0;
                dev=0;
                count=0;
            }

        }
    };
    public void reflashroomlight(){
        reflashroomonoff.postDelayed(reflashroomonoffrun,0);
    }
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
    /**********************************监听listener********************************/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if(rev.length>25){
                    RunReceiveData(rev);
                }

                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                getActivity().finish();
            }else if(FounctionActivity.ACTION_DELETEOTHER.equals(action)){
                onoffhandle.postDelayed(getonofflist,30);
            }
        }
    };

    public void RunReceiveData(byte[] data){
        int x=(int)(((data[21]&0xff)<<8))+(int)(data[22]&0xff);//get op code
        String str=Integer.toHexString(x)+"\n";
        //str=byte2hex(data);
       // log.append(str);
        // if (ifthesubanddevmatch(data[17]&0xff,data[18]&0xff,data[25]&0xff)) //get sub&device&channel and pair to light db
        int subid = ((int) (data[17]) & 0xff);
        int devid = ((int) (data[18]) & 0xff);
        switch (x) {
            case 0x0032:
                if (data[26] == (byte) 0xf8) {
                    int chan = ((int) (data[25]) & 0xff);
                    OtherType1 a0 = null;
                    OtherType2 a1 = null;
                    for (int i = 0; i < type1list.size(); i++) {
                        if (type1list.get(i).getsubid() == subid && type1list.get(i).getdevid() == devid
                                && type1list.get(i).getchannel() == chan) {
                            a0 = type1list.get(i);
                        }
                    }
                    /*for (int i = 0; i < type2list.size(); i++) {
                        if (type2list.get(i).getsubid() == subid && type2list.get(i).getdevid() == devid
                                && type2list.get(i).getchannel() == chan) {
                            a1 = type2list.get(i);
                        }
                    }*/
                    if (a0 != null) {
                        a0.setReceiveChange(data[27]);
                    }
                }
                break;
            case 0x0034:
                for(int i=0;i<type1list.size();i++){
                    if(type1list.get(i).getsubid()==subid&&type1list.get(i).getdevid()==devid){
                        type1list.get(i).setReceiveChange(data[type1list.get(i).getchannel()+25]);//get the channel value
                    }
                }
               /* for(int i=0;i<type2list.size();i++){
                    if(type2list.get(i).getsubid()==subid&&type2list.get(i).getdevid()==devid){
                        type2list.get(i).setReceiveChange(data[type2list.get(i).getchannel()+25]);//get the channel value
                    }
                }*/
                break;

            case 0xDC22://干节点状态改变
                for(int i=0;i<type1list.size();i++){
                    if(type1list.get(i).getsubid()==subid&&type1list.get(i).getdevid()==devid){
                        int contectCount = (data[25] & 0xff);//干接点数量
                        //实际干节点数量大于配置的通道号
                        if (contectCount >= type1list.get(i).getchannel()) {
                            int on_off = (data[(25 +contectCount+ type1list.get(i).getchannel())] & 0xff);//开关状态
                            if (on_off == 0) {
                                //关
                                type1list.get(i).setReceiveChange((byte) 0);
                            } else {
                                //开
                                type1list.get(i).setReceiveChange((byte) 100);
                            }


                        }
                    }
                }

                break;
            case 0x012D://读取干节点状态反馈

                for(int i=0;i<type1list.size();i++){
                    if(type1list.get(i).getsubid()==subid&&type1list.get(i).getdevid()==devid){
                        int contectCount2 = (data[26] & 0xff);//干接点数量
                        //实际干节点数量大于配置的通道号
                        if (contectCount2 >= type1list.get(i).getchannel()) {
                            int on_off = (data[(26 +contectCount2+ type1list.get(i).getchannel())] & 0xff);//开关状态
                            if (on_off == 0) {
                                //关
                                type1list.get(i).setReceiveChange((byte) 0);
                            } else {
                                //开
                                type1list.get(i).setReceiveChange((byte) 100);
                            }

                        }
                    }
                }
                break;
            default:
                break;
        }

    }


    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_DELETEOTHER);
        return intentFilter;
    }

}
