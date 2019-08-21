package com.shima.smartbushome.founction_view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.LightTypeAdapter;
import com.shima.smartbushome.assist.BLE.smartBeaconManager;
import com.shima.smartbushome.database.Savelight;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.selflayout.LightType1;
import com.shima.smartbushome.selflayout.LightType2;
import com.shima.smartbushome.selflayout.LightType3;
import com.shima.smartbushome.selflayout.LightType4;
import com.shima.smartbushome.udp.udp_socket;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Light extends Fragment {

    View view;
    TextView log;
    MenuItem add, delete;
    Handler lighthandle = new Handler();
    Handler reflashroomlight = new Handler();
    public List<LightType1> type1list = new ArrayList<LightType1>();
    public List<LightType2> type2list = new ArrayList<LightType2>();
    public List<LightType3> type3list = new ArrayList<LightType3>();
    public List<LightType4> type4list = new ArrayList<LightType4>();
    List<Savelight> roomlight = new ArrayList<Savelight>();
    LinearLayout lightcontrollayout;
    boolean intodeletemode = false;
    lightcontrol reflash;
    public static Context lightcontext;
    smartBeaconManager beaconManager;

    public Light() {
        // Required empty public constructor
    }

    boolean onetime = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_light, container, false);
        log = (TextView) view.findViewById(R.id.textView);
        log.setMovementMethod(new ScrollingMovementMethod());
        lightcontrollayout = (LinearLayout) view.findViewById(R.id.linearLayout2);
        getActivity().setTitle("Light");
        setHasOptionsMenu(true);
        reflash = new lightcontrol();

       /* SharedPreferences sharedcolorPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
       int backgroudcolor = sharedcolorPre.getInt("lightbgcolor", 0xFF000000);
        RelativeLayout roomacbg=(RelativeLayout)view.findViewById(R.id.lightout);
        roomacbg.setBackgroundColor(backgroudcolor);*/

        lighthandle.postDelayed(getlightlist, 30);

        //背景图上移
        ImageView iv_layout = (ImageView) view.findViewById(R.id.iv_layout);
        setMargins(iv_layout, 0, (-FounctionActivity.topHeight), 0, 0);


        return view;
    }


    //设置view的外边距
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //beaconManager.stop();
        reflashroomlight.removeCallbacks(reflashroomlightrun);
        lighthandle.removeCallbacks(getlightlist);
        type1list.clear();
        type2list.clear();
        type3list.clear();
        type4list.clear();
        roomlight.clear();
        intodeletemode = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.light_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        add = menu.findItem(R.id.light_add);
        delete = menu.findItem(R.id.light_remove);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.light_add:
                if (!MainActivity.islockchangeid) {
                    if (intodeletemode) {
                        add.setTitle("ADD");
                        delete.setTitle("DELETE");
                        intodeletemode = false;
                        for (int i = 0; i < type1list.size(); i++) {
                            type1list.get(i).setdeletevisable(false);
                        }

                        for (int i = 0; i < type2list.size(); i++) {
                            type2list.get(i).setdeletevisable(false);
                        }

                        for (int i = 0; i < type3list.size(); i++) {
                            type3list.get(i).setdeletevisable(false);
                        }

                        for (int i = 0; i < type4list.size(); i++) {
                            type4list.get(i).setdeletevisable(false);
                        }
                    } else {
                        final AlertView mAlertViewExt = new AlertView(null, null, "CANCEL", null, null, getActivity(), AlertView.Style.Alert,
                                null);
                        //ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.listdialog,null);
                        final ListView typelist = new ListView(getActivity());
                        typelist.setAdapter(new LightTypeAdapter(getActivity()));
                        typelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                int light_id;
                                if (roomlight.size() == 0) {
                                    light_id = 1;
                                } else {
                                    light_id = roomlight.get(roomlight.size() - 1).light_id + 1;
                                }
                                ArrayList<Savelight> tips = new ArrayList<Savelight>();
                                switch (position) {
                                    case 0:
                                        Savelight type1 = new Savelight(FounctionActivity.roomidfc,
                                                0, 0, light_id, 0, 100, 1, "light" + light_id, "light_icon1");
                                        tips.add(type1);
                                        MainActivity.mgr.addlight(tips);
                                        break;
                                    case 1:
                                        Savelight type2 = new Savelight(FounctionActivity.roomidfc,
                                                0, 0, light_id, 0, 100, 2, "light" + light_id, "light_icon1");
                                        tips.add(type2);
                                        MainActivity.mgr.addlight(tips);
                                        break;
                                    case 2:
                                        Savelight type3 = new Savelight(FounctionActivity.roomidfc,
                                                0, 0, light_id, 0, 100, 3, "light" + light_id, "light_icon1");
                                        tips.add(type3);
                                        MainActivity.mgr.addlight(tips);
                                        break;
                                    case 3:
                                        Savelight type4 = new Savelight(FounctionActivity.roomidfc,
                                                0, 0, light_id, 0, 100, 4, "light" + light_id, "light_icon1");
                                        tips.add(type4);
                                        MainActivity.mgr.addlight(tips);
                                        break;
                                    default:
                                        break;
                                }
                                lighthandle.postDelayed(getlightlist, 30);
                                mAlertViewExt.dismiss();
                            }
                        });
                        mAlertViewExt.addExtView(typelist);
                        mAlertViewExt.show();
                    }
                }

                break;
            case R.id.light_remove:
                if (!MainActivity.islockchangeid) {
                    intodeletemode = !intodeletemode;
                    if (intodeletemode) {
                        add.setTitle("CANCLE DELETE");
                        delete.setTitle("DELETE");
                        if (type1list.size() > 0) {
                            type1list.clear();
                        }
                        if (type2list.size() > 0) {
                            type2list.clear();
                        }
                        if (type3list.size() > 0) {
                            type3list.clear();
                        }
                        if (type4list.size() > 0) {
                            type4list.clear();
                        }
                        for (int i = 0; i < roomlight.size(); i++) {
                            switch (roomlight.get(i).lightType) {
                                case 1:
                                    LightType1 x = (LightType1) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                                    type1list.add(x);
                                    x.setdeletevisable(true);
                                    break;
                                case 2:
                                    LightType2 x2 = (LightType2) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                                    type2list.add(x2);
                                    x2.setdeletevisable(true);
                                    break;
                                case 3:
                                    LightType3 x3 = (LightType3) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                                    type3list.add(x3);
                                    x3.setdeletevisable(true);
                                    break;
                                case 4:
                                    LightType4 x4 = (LightType4) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                                    type4list.add(x4);
                                    x4.setdeletevisable(true);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        for (int i = 0; i < type1list.size(); i++) {
                            if (type1list.get(i).getIfneedtoDelete()) {
                                MainActivity.mgr.deletelight("light", type1list.get(i).getType1lightid(), FounctionActivity.roomidfc);
                            }
                        }

                        for (int i = 0; i < type2list.size(); i++) {
                            if (type2list.get(i).getIfneedtoDelete()) {
                                MainActivity.mgr.deletelight("light", type2list.get(i).getType2lightid(), FounctionActivity.roomidfc);
                            }
                        }

                        for (int i = 0; i < type3list.size(); i++) {
                            if (type3list.get(i).getIfneedtoDelete()) {
                                MainActivity.mgr.deletelight("light", type3list.get(i).getType3lightid(), FounctionActivity.roomidfc);
                            }
                        }
                        for (int i = 0; i < type4list.size(); i++) {
                            if (type4list.get(i).getIfneedtoDelete()) {
                                MainActivity.mgr.deletelight("light", type4list.get(i).getType4lightid(), FounctionActivity.roomidfc);
                            }
                        }
                        lighthandle.postDelayed(getlightlist, 30);
                        add.setTitle("ADD");
                        delete.setTitle("DELETE");
                        intodeletemode = false;
                    }

                }

                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*****************reflash the list*****************/
    Runnable getlightlist = new Runnable() {
        @Override
        public void run() {
            if (roomlight.size() > 0) {
                roomlight.clear();
            }
            lightcontrollayout.removeAllViews();
            List<Savelight> alldata = MainActivity.mgr.querylight();
            for (int i = 0; i < alldata.size(); i++) {
                if (alldata.get(i).room_id == FounctionActivity.roomidfc) {
                    roomlight.add(alldata.get(i));
                }
            }

            for (int i = 0; i < roomlight.size(); i++) {
                addspecView(roomlight.get(i));
            }

            for (int i = 0; i < roomlight.size(); i++) {
                switch (roomlight.get(i).lightType) {
                    case 1:
                        LightType1 x = (LightType1) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                        type1list.add(x);
                        break;
                    case 2:
                        LightType2 x2 = (LightType2) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                        type2list.add(x2);
                        break;
                    case 3:
                        LightType3 x3 = (LightType3) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                        type3list.add(x3);
                        break;
                    case 4:
                        LightType4 x4 = (LightType4) lightcontrollayout.findViewById(roomlight.get(i).light_id);
                        type4list.add(x4);
                        break;
                    default:
                        break;
                }
            }
            reflashroomlight();
            // reflashhandler.postDelayed(reflashuiRun, 20000);
            lighthandle.removeCallbacks(getlightlist);
        }
    };

    private void addspecView(Savelight lg) {
        switch (lg.lightType) {
            case 1:
                LightType1 lv = new LightType1(lightcontrollayout.getContext());
                lv.setcontant(lg);
                lv.setId(lg.light_id);
                lightcontrollayout.addView(lv);
                break;
            case 2:
                LightType2 lv2 = new LightType2(lightcontrollayout.getContext());
                lv2.setcontant(lg);
                lv2.setId(lg.light_id);
                lightcontrollayout.addView(lv2);
                break;
            case 3:
                LightType3 lv3 = new LightType3(lightcontrollayout.getContext());
                lv3.setcontant(lg);
                lv3.setId(lg.light_id);
                lightcontrollayout.addView(lv3);
                break;
            case 4:
                LightType4 lv4 = new LightType4(lightcontrollayout.getContext());
                lv4.setcontant(lg);
                lv4.setId(lg.light_id);
                lightcontrollayout.addView(lv4);
                break;
            default:
                break;
        }
    }

    /*
    * *************reflash the ui value****************** */

    Runnable reflashuiRun = new Runnable() {
        @Override
        public void run() {
            reflashroomlight();
            //reflashhandler.postDelayed(reflashuiRun, 20000);
        }
    };
    byte sub = 0, dev = 0, count = 0;
    boolean getstatefinish = false;
    int getcount = 0, getcount2 = 0, timeout = 0;
    Runnable reflashroomlightrun = new Runnable() {
        @Override
        public void run() {


            //发送读取灯光状态指令
            for (int i=0;i<roomlight.size();i++){
                if (sub!=(byte) roomlight.get(i).subnetID||dev!=(byte) roomlight.get(i).deviceID){
                    sub=(byte) roomlight.get(i).subnetID;
                    dev=(byte) roomlight.get(i).deviceID;
                    reflash.getlightstate(sub, dev, MainActivity.mydupsocket);
                }

            }
            reflash.getlightstate(sub, dev, MainActivity.mydupsocket);

//            if (count < roomlight.size()) {
//                if (roomlight.get(count).subnetID != (sub & 0xff)) {
//                    sub = (byte) roomlight.get(count).subnetID;
//                    dev = (byte) roomlight.get(count).deviceID;
//                    reflash.getlightstate(sub, dev, MainActivity.mydupsocket);
//                    getcount++;
//                } else if (roomlight.get(count).deviceID != (dev & 0xff)) {
//                    sub = (byte) roomlight.get(count).subnetID;
//                    dev = (byte) roomlight.get(count).deviceID;
//                    reflash.getlightstate(sub, dev, MainActivity.mydupsocket);
//                    getcount++;
//                }
//                count++;
//                reflashroomlight.postDelayed(reflashroomlightrun, 100);
//            } else {
//                if (getcount2 != getcount) {
//                    sub = 0;
//                    dev = 0;
//                    count = 0;
//                    getcount = 0;
//                    getcount2 = 0;
//                    timeout++;
//                    if (timeout >= 10) {
//                        reflashroomlight.removeCallbacks(reflashroomlightrun);
//                        sub = 0;
//                        dev = 0;
//                        count = 0;
//                        getcount = 0;
//                        getcount2 = 0;
//                        timeout = 0;
//                    } else {
//                        reflashroomlight();
//                    }
//                } else {
//                    reflashroomlight.removeCallbacks(reflashroomlightrun);
//                    sub = 0;
//                    dev = 0;
//                    count = 0;
//                    getcount = 0;
//                    getcount2 = 0;
//                    timeout = 0;
//                }
//
//            }

        }
    };

    public void reflashroomlight() {
        reflashroomlight.postDelayed(reflashroomlightrun, 0);
    }

    int shakecount = 0;
    Handler shakehandler = new Handler();
    int shakelightvalue = 999, shakeledvalue = 999;
    Runnable shakerun = new Runnable() {
        @Override
        public void run() {
            if (shakecount >= roomlight.size()) {
                shakelightvalue = 999;
                shakeledvalue = 999;
                shakecount = 0;
                shakehandler.removeCallbacks(shakerun);
            } else {
                if (roomlight.get(shakecount).lightType != 3) {
                    reflash.SingleChannelControl((byte) roomlight.get(shakecount).subnetID, (byte) roomlight.get(shakecount).deviceID,
                            roomlight.get(shakecount).channel, shakelightvalue, MainActivity.mydupsocket);
                } else {
                    reflash.ARGBlightcontrol((byte) roomlight.get(shakecount).subnetID,
                            (byte) roomlight.get(shakecount).deviceID, shakeledvalue, MainActivity.mydupsocket);
                }
                shakecount++;
                shakehandler.postDelayed(shakerun, 70);
            }

        }
    };

    public void shakeperform(int shaketype) {
        switch (shaketype) {
            case 1:
                shakelightvalue = 0;
                int color = Color.argb(255, 0, 0, 0);
                shakeledvalue = color;
                shakehandler.postDelayed(shakerun, 30);
                break;
            case 2:
                shakelightvalue = 100;
                int color2 = Color.argb(255, 255, 255, 255);
                shakeledvalue = color2;
                shakehandler.postDelayed(shakerun, 30);
                break;
            default:
                break;
        }

    }

    /**********************************监听listener********************************/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev = (intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if (rev.length > 25) {
                    RunReceiveData(rev);
                }
                rev = null;
            } else if (FounctionActivity.ACTION_BACKPRESS.equals(action)) {
                getActivity().finish();
                reflashroomlight.removeCallbacks(reflashroomlightrun);
            } else if (FounctionActivity.ACTION_DELETELIGHT.equals(action)) {
                lighthandle.postDelayed(getlightlist, 30);
            } else if (FounctionActivity.ACTION_SHAKE.equals(action)) {
                if (!MainActivity.islockshake) {
                    int shaketype = (intent.getIntExtra("shake_type", 0));
                    shakeperform(shaketype);
                }
            }
        }
    };

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getByteToBits(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    //0xefff 解析通道状态
    public byte[] getChannelStates(byte[] data) {
        int zoneCount = (data[25] & 0xff);//区域数量
        int channelCount = (data[25 + zoneCount + 1] & 0xff);//通道数量
        int channelStateCount = channelCount / 8 + 1;//通道状态位数量(一个状态位用8Bit表示8个通道的状态)
        byte[] channelStates = new byte[channelCount];//通道状态数组 用于保存通道状态
        //遍历通道状态 获取每个通道状态(0:OFF 1:ON) 没有百分比
        for (int j = 0; j < channelStateCount; j++) {
            byte stateByte = data[25 + zoneCount + 1 + j + 1];
            byte[] stateBits = getByteToBits(stateByte);//将byte转换为一个长度为8的byte数组，数组每个值代表bit
            for (int k = 8; k > 0; k--) {
                int channelStateNo = j * 8 + (8 - k);//通道状态数组的索引
                if (channelStateNo > channelCount - 1) {//超出范围
                    j = channelStateCount;//为了跳出外循环
                    break;//完成每个通道状态的获取 跳出内循环
                }
                if (0 == (stateBits[k - 1] & 0xff)) { //OFF
                    channelStates[channelStateNo] = (byte) 0;
                } else {//ON
                    channelStates[channelStateNo] = (byte) 100;
                }
            }
        }
        return channelStates;
    }

    public void RunReceiveData(byte[] data) {
        int x = (int) (((data[21] & 0xff) << 8)) + (int) (data[22] & 0xff);//get op code
        String str = Integer.toHexString(x) + "\n";
        int subid = ((int) (data[17]) & 0xff);
        int devid = ((int) (data[18]) & 0xff);
        //str=byte2hex(data);
        log.append(str);
        // if (ifthesubanddevmatch(data[17]&0xff,data[18]&0xff,data[25]&0xff)) //get sub&device&channel and pair to light db
        switch (x) {
            case 0xefff://继电器/调光器 每五秒广播一次 或者 由用户触发按钮广播 指令的操作码

                int zoneCount = (data[25] & 0xff);//区域数量
                int channelCount = (data[25 + zoneCount + 1] & 0xff);//通道数量
                byte[] channelStates = getChannelStates(data);//解析通道状态

                for (int i = 0; i < type1list.size(); i++) {
                    if (type1list.get(i).getsubid() == subid && type1list.get(i).getdevid() == devid) {
                        LightType1 a1 = type1list.get(i);
                        if (a1.getchannel() <= channelCount && a1.getchannel() > 0) {
                            a1.setReceiveChange(channelStates[a1.getchannel() - 1]);
                        }
                    }
                }
                for (int i = 0; i < type2list.size(); i++) {
                    if (type2list.get(i).getsubid() == subid && type2list.get(i).getdevid() == devid) {
                        LightType2 a2 = type2list.get(i);
                        if (a2.getchannel() <= channelCount && a2.getchannel() > 0) {
                            a2.setReceiveChange2(channelStates[a2.getchannel() - 1]);
                        }
                    }
                }


                break;
            case 0x0032:
                if (data[26] == (byte) 0xf8) {

                    int chan = ((int) (data[25]) & 0xff);
                    LightType1 a0 = null;
                    LightType2 a1 = null;
                    LightType3 a2 = null;
                    for (int i = 0; i < type1list.size(); i++) {
                        if (type1list.get(i).getsubid() == subid && type1list.get(i).getdevid() == devid
                                && type1list.get(i).getchannel() == chan) {
                            a0 = type1list.get(i);
                        }
                    }
                    for (int i = 0; i < type2list.size(); i++) {
                        if (type2list.get(i).getsubid() == subid && type2list.get(i).getdevid() == devid
                                && type2list.get(i).getchannel() == chan) {
                            a1 = type2list.get(i);
                        }
                    }
                    for (int i = 0; i < type3list.size(); i++) {
                        if (type3list.get(i).getsubid() == subid && type3list.get(i).getdevid() == devid) {
                            a2 = type3list.get(i);
                        }
                    }
                    if (a0 != null) {
                        a0.setReceiveChange(data[27]);
                    } else if (a1 != null) {
                        a1.setReceiveChange(data[27]);
                    } else if (a2 != null) {
                        reflash.getlightstate((byte) subid, (byte) devid, MainActivity.mydupsocket);
                    }
                }
                break;
            case 0x0034:
                getcount2++;
                try {
                    for (int i = 0; i < type1list.size(); i++) {
                        if (type1list.get(i).getsubid() == subid && type1list.get(i).getdevid() == devid && type1list.get(i).getchannel() != 0xff) {
                            try {
                                type1list.get(i).setReceiveChange(data[type1list.get(i).getchannel() + 25]);//get the channel value
                            } catch (Exception e) {
                                switch (type1list.get(i).getchannel()) {
                                    case 49:
                                        type1list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type1list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type1list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type1list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(getActivity(), "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < type2list.size(); i++) {
                        if (type2list.get(i).getsubid() == subid && type2list.get(i).getdevid() == devid && type2list.get(i).getchannel() != 0xff) {
                            try {
                                type2list.get(i).setReceiveChange(data[type2list.get(i).getchannel() + 25]);//get the channel value
                            } catch (Exception e) {
                                switch (type2list.get(i).getchannel()) {
                                    case 49:
                                        type2list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type2list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type2list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type2list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(getActivity(), "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        }
                    }

                    for (int i = 0; i < type3list.size(); i++) {
                        if (type3list.get(i).getsubid() == subid && type3list.get(i).getdevid() == devid) {
                            byte[] rgbdata = {data[26], data[27], data[28], data[29]};
                            type3list.get(i).setReceiveChange(rgbdata);//get the rgb value
                        }
                    }

                    for (int i = 0; i < type4list.size(); i++) {
                        if (type4list.get(i).getsubid() == subid && type4list.get(i).getdevid() == devid && type4list.get(i).getchannel() != 0xff) {
                            try {
                                type4list.get(i).setReceiveChange(data[type4list.get(i).getchannel() + 25]);//get the channel value
                            } catch (Exception e) {
                                switch (type4list.get(i).getchannel()) {
                                    case 49:
                                        type4list.get(i).setReceiveChange(data[26]);//get the channel value
                                        break;
                                    case 50:
                                        type4list.get(i).setReceiveChange(data[27]);//get the channel value
                                        break;
                                    case 51:
                                        type4list.get(i).setReceiveChange(data[28]);//get the channel value
                                        break;
                                    case 52:
                                        type4list.get(i).setReceiveChange(data[29]);//get the channel value
                                        break;
                                    default:
                                        Toast.makeText(getActivity(), "you had set wrong channel", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        intentFilter.addAction(FounctionActivity.ACTION_DELETELIGHT);
        intentFilter.addAction(FounctionActivity.ACTION_SHAKE);
        return intentFilter;
    }
}
