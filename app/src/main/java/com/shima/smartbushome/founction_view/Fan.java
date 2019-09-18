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
import com.shima.smartbushome.database.Savefan;
import com.shima.smartbushome.founction_command.fancontrol;
import com.shima.smartbushome.selflayout.FanType1;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fan extends Fragment {
    MenuItem  add,delete;
    List<FanType1> type1list=new ArrayList<FanType1>();
    List<Savefan> roomfan= new ArrayList<Savefan>();
    boolean intodeletemode=false;
    View view;
    LinearLayout fancontrollayout;
    Handler fanhandler=new Handler();
    public Fan() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Fan");
        view=inflater.inflate(R.layout.fragment_fan, container, false);
        fancontrollayout=(LinearLayout)view.findViewById(R.id.fanlayout);
        setHasOptionsMenu(true);

    /*    SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        int backgroudcolor = sharedcolorPre.getInt("fanbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.fanout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        fanhandler.postDelayed(getfanlist, 30);

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
                        int fan_id;
                        if(roomfan.size()==0){
                            fan_id=1;
                        }else{
                            fan_id=roomfan.get(roomfan.size()-1).fan_id+1;
                        }
                        Savefan type1 = new Savefan(FounctionActivity.roomidfc,
                                0,0,fan_id,"fan"+fan_id,0,1,"fan_icon1");
                        MainActivity.mgr.addfan(type1);
                        Toast.makeText(getActivity(), "add succeed", Toast.LENGTH_SHORT).show();
                        fanhandler.postDelayed(getfanlist,30);
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
                        for(int i=0;i<roomfan.size();i++){
                            switch(roomfan.get(i).fan_Type){
                                case 1:
                                    FanType1 x=(FanType1)fancontrollayout.findViewById(roomfan.get(i).fan_id);
                                    type1list.add(x);
                                    x.setdeletevisable(true);
                                    break;
                                default:break;
                            }
                        }
                    }else{
                        for(int i=0;i<type1list.size();i++){
                            if(type1list.get(i).getIfneedtoDelete()){
                                MainActivity.mgr.deletefan("fan", type1list.get(i).getType1fanid(), FounctionActivity.roomidfc);
                            }
                        }
                        fanhandler.postDelayed(getfanlist,30);
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
    Runnable getfanlist=new Runnable() {
        @Override
        public void run() {
            if(roomfan.size()>0){
                roomfan.clear();
            }
            fancontrollayout.removeAllViews();

            List<Savefan> alldata=MainActivity.mgr.queryfan();
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id== FounctionActivity.roomidfc){
                    roomfan.add(alldata.get(i));
                }
            }

            for(int i=0;i<roomfan.size();i++){
                addspecView(roomfan.get(i));
            }

            for(int i=0;i<roomfan.size();i++){
                switch(roomfan.get(i).fan_Type){
                    case 1:
                        FanType1 x=(FanType1)fancontrollayout.findViewById(roomfan.get(i).fan_id);
                        type1list.add(x);
                        break;
                    default:break;
                }
            }
            fanhandler.removeCallbacks(getfanlist);
        }
    };

    private void addspecView(Savefan sf) {
        switch(sf.fan_Type){
            case 1:
                FanType1 ct=new FanType1(fancontrollayout.getContext());
                ct.setcontant(sf);
                ct.setId(sf.fan_id);
                fancontrollayout.addView(ct);
                break;
            default:break;
        }
    }

    fancontrol fcommand=new fancontrol();
    int shakecount=0;
    Handler shakehandler=new Handler();
    int shakefanvalue=0;
    Runnable shakerun=new Runnable() {
        @Override
        public void run() {
            if(shakecount>=roomfan.size()){
                shakecount=0;
                shakehandler.removeCallbacks(shakerun);
            }else{
                fcommand.FanChannelControl((byte)roomfan.get(shakecount).subnetID,(byte)roomfan.get(shakecount).deviceID,
                        roomfan.get(shakecount).channel,shakefanvalue,MainActivity.mydupsocket);
                shakecount++;
                shakehandler.postDelayed(shakerun, 80);
            }

        }
    };
    public void shakeperform(int shaketype){
        switch (shaketype){
            case 1:
                shakefanvalue--;
                if(shakefanvalue<=0){shakefanvalue=0;}
                shakehandler.postDelayed(shakerun,30);
                break;
            case 2:
                shakefanvalue++;
                if(shakefanvalue>=4){shakefanvalue=4;}
                shakehandler.postDelayed(shakerun, 30);
                break;
            default:break;
        }

    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if(rev.length>25){
                   // RunReceiveData(rev);
                }

                rev=null;
            }else if(FounctionActivity.ACTION_BACKPRESS.equals(action)){
                getActivity().finish();
            }else if(FounctionActivity.ACTION_DELETEFAN.equals(action)){
                fanhandler.postDelayed(getfanlist, 30);
            }else if(FounctionActivity.ACTION_SHAKE.equals(action)){
                if(!MainActivity.islockshake){
                    int shaketype=(intent.getIntExtra("shake_type",0));
                    shakeperform(shaketype);
                }

            }
        }
    };

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        intentFilter.addAction(FounctionActivity.ACTION_DELETEFAN);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }
}
