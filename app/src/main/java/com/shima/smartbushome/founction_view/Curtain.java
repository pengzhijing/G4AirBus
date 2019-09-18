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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.selflayout.CurtainType1;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Curtain extends Fragment {
    List<CurtainType1> type1list=new ArrayList<CurtainType1>();
    List<Savecurtain> roomcurtain= new ArrayList<Savecurtain>();
    MenuItem  add,delete;
    boolean intodeletemode=false;
    View view;
    LinearLayout curtaincontrollayout;
    Handler curtainhandler=new Handler();
    public Curtain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Curtain");
        view=inflater.inflate(R.layout.fragment_curtain, container, false);
        curtaincontrollayout=(LinearLayout)view.findViewById(R.id.curtainlayout);
        setHasOptionsMenu(true);

      /*  SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("curtainbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.curtainout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        curtainhandler.postDelayed(getcurtainlist, 30);

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
        type1list.clear();
        intodeletemode=false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.curtain_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        add= menu.findItem(R.id.add_curtain);
        delete= menu.findItem(R.id.delete_curtain);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_curtain:
                if(!MainActivity.islockchangeid){
                    if(intodeletemode){
                        add.setTitle("ADD");
                        delete.setTitle("DELETE");
                        intodeletemode=false;
                        for(int i=0;i<type1list.size();i++){
                            type1list.get(i).setdeletevisable(false);
                        }

                    }else{
                        int curtain_id;
                        if(roomcurtain.size()==0){
                            curtain_id=1;
                        }else{
                            curtain_id=roomcurtain.get(roomcurtain.size()-1).curtain_id+1;
                        }
                        ArrayList<Savecurtain> tips = new ArrayList<Savecurtain>();
                        Savecurtain type1 = new Savecurtain(FounctionActivity.roomidfc,
                                0,0,curtain_id,1,0,1,"curtain"+curtain_id,"close");
                        tips.add(type1);
                        MainActivity.mgr.addcurtain(tips);
                        Toast.makeText(getActivity(), "add succeed", Toast.LENGTH_SHORT).show();
                        curtainhandler.postDelayed(getcurtainlist,30);
                    }
                }

                break;
            case R.id.delete_curtain:
                if(!MainActivity.islockchangeid){
                    intodeletemode=!intodeletemode;
                    if(intodeletemode){
                        add.setTitle("CANCLE DELETE");
                        delete.setTitle("DELETE");
                        if(type1list.size()>0){type1list.clear();}
                        for(int i=0;i<roomcurtain.size();i++){
                            switch(roomcurtain.get(i).curtain_type){
                                case 1:
                                    CurtainType1 x=(CurtainType1)curtaincontrollayout.findViewById(roomcurtain.get(i).curtain_id);
                                    type1list.add(x);
                                    x.setdeletevisable(true);
                                    break;
                                default:break;
                            }
                        }
                    }else{
                        for(int i=0;i<type1list.size();i++){
                            if(type1list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deletecurtain("curtain", type1list.get(i).getType1curtainid(), FounctionActivity.roomidfc);
                            }
                        }
                        curtainhandler.postDelayed(getcurtainlist,30);
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
    /*****************reflash the list*****************/
    Runnable getcurtainlist=new Runnable() {
        @Override
        public void run() {
            if(roomcurtain.size()>0){
                roomcurtain.clear();
            }
            curtaincontrollayout.removeAllViews();
            List<Savecurtain> alldata=MainActivity.mgr.querycurtain();
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id== FounctionActivity.roomidfc){
                    roomcurtain.add(alldata.get(i));
                }
            }

            for(int i=0;i<roomcurtain.size();i++){
                addspecView(roomcurtain.get(i));
            }

            for(int i=0;i<roomcurtain.size();i++){
                switch(roomcurtain.get(i).curtain_type){
                    case 1:
                        CurtainType1 x=(CurtainType1)curtaincontrollayout.findViewById(roomcurtain.get(i).curtain_id);
                        type1list.add(x);
                        break;
                    default:break;
                }
            }
            curtainhandler.removeCallbacks(getcurtainlist);
        }
    };

    private void addspecView(Savecurtain lg) {
        switch(lg.curtain_type){
            case 1:
                CurtainType1 ct=new CurtainType1(curtaincontrollayout.getContext());
                ct.setcontan(lg);
                ct.setId(lg.curtain_id);
                curtaincontrollayout.addView(ct);
                break;
            default:break;
        }
    }
    curtaincontrol ccomand=new curtaincontrol();
    int shakecount=0;
    Handler shakehandler=new Handler();
    String shakecurtainvalue="";
    Runnable shakerun=new Runnable() {
        @Override
        public void run() {
            if(shakecount>=roomcurtain.size()){
                shakecurtainvalue="";
                shakecount=0;
                shakehandler.removeCallbacks(shakerun);
            }else{
                ccomand.CurtainControl((byte) roomcurtain.get(shakecount).subnetID, (byte) roomcurtain.get(shakecount).deviceID,
                        roomcurtain.get(shakecount).channel_1, roomcurtain.get(shakecount).channel_2, shakecurtainvalue,MainActivity.mydupsocket);
                shakecount++;
                shakehandler.postDelayed(shakerun, 80);
            }

        }
    };
    public void shakeperform(int shaketype){
        switch (shaketype){
            case 1:
                shakecurtainvalue="close";
                shakehandler.postDelayed(shakerun,30);
                break;
            case 2:
                shakecurtainvalue="open";
                shakehandler.postDelayed(shakerun, 30);
                break;
            case 6:
                shakecurtainvalue="stop";
                shakehandler.postDelayed(shakerun, 30);
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
                if(rev.length>25){
                    RunReceiveData(rev);
                }

                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                getActivity().finish();
            }else if(FounctionActivity.ACTION_DELETECURTAIN.equals(action)){
                curtainhandler.postDelayed(getcurtainlist,30);
            }else if(FounctionActivity.ACTION_SHAKE.equals(action)){
                if(!MainActivity.islockshake){
                    int shaketype=(intent.getIntExtra("shake_type",0));
                    shakeperform(shaketype);
                }

            }
        }
    };

    public void RunReceiveData(byte[] cdata){
        int x=(int)(((cdata[21]&0xff)<<8))+(int)(cdata[22]&0xff);//get op code
        switch (x) {
            case 0x0032:
                if (cdata[26] == (byte) 0xf8) {
                    int subid = ((int) (cdata[17]) & 0xff);
                    int devid = ((int) (cdata[18]) & 0xff);
                    int chan = ((int) (cdata[25]) & 0xff);
                    CurtainType1 a0 = null;

                    for (int i = 0; i < type1list.size(); i++) {
                        if ((type1list.get(i).getsubid() == subid) && (type1list.get(i).getdevid() == devid)
                                && (type1list.get(i).getchannel()[0] == chan)) {
                            if(cdata[27]==(byte)0x64){
                                a0 = type1list.get(i);
                                a0.setReceiveChange(0);
                            }
                        }else if((type1list.get(i).getsubid() == subid) && (type1list.get(i).getdevid() == devid)
                                && (type1list.get(i).getchannel()[1] == chan)){
                            if(cdata[27]==(byte)0x64){
                                a0 = type1list.get(i);
                                a0.setReceiveChange(1);
                            }
                        }
                    }


                }
                break;
        }
    }
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_DELETECURTAIN);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }
}
