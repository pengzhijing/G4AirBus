package com.shima.smartbushome;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.SmartG4DeviceType;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SystemUIUtil;

import java.util.HashMap;

public class NetDaviceListActivity extends AppCompatActivity {
    private DeviceListAdapter mLeDeviceListAdapter;
    SmartG4DeviceType sdt;
    ListView lv;
    ProgressDialog progress;
    Handler runtime=new Handler();
    int time=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("DeviceList");
        setContentView(R.layout.activity_net_davice_list);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        lv=(ListView)findViewById(R.id.listView3);
        progress = new ProgressDialog(this);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Getting Device...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.show();
        if(MainActivity.netdeviceList.size()!=0){
            MainActivity.netdeviceList.clear();
        }
        /*if(MainActivity.mydupsocket!=null){
            MainActivity.mydupsocket.initprocess();
        }else{
            finish();
        }*/
        mLeDeviceListAdapter = new DeviceListAdapter(this,MainActivity.netdeviceList);
        lv.setAdapter(mLeDeviceListAdapter);
        getdevice();
        sdt=new SmartG4DeviceType();
        sdt.initdevicetype();
        runtime.postDelayed(sentrun, 2000);
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
    }
    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    Runnable sentrun=new Runnable() {
        @Override
        public void run() {
            time++;
            progress.setProgress(time*20);
            if(time==5){
                progress.dismiss();
                runtime.removeCallbacks(sentrun);
            }else{
                getdevice();
                runtime.postDelayed(sentrun,1000);
            }

        }
    };
    public void save(View v){
        Toast.makeText(NetDaviceListActivity.this, "save succeed", Toast.LENGTH_SHORT).show();
        finish();
    }
    public void exit(View v){
        MainActivity.netdeviceList.clear();
        finish();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if(rev.length>20){
                    RunReceiveData(rev);
                }

                rev=null;
            }
        }
    };

    public void RunReceiveData(byte[] data){
        if(data[21]==0x00&&data[22]==0x0f){
            if(pairifexist(data[17],data[18])){

            }else{
                int dt=(((data[19]&0xff)<<8))+(data[20]&0xff);
                HashMap<String,String> device=new HashMap<String,String>();
                device.put("subnetID",String.valueOf((data[17]&0xff)));
                device.put("deviceID",String.valueOf((data[18]&0xff)));
                device.put("devicename",sdt.getDeviceType(dt));
                MainActivity.netdeviceList.add(device);
                mLeDeviceListAdapter.addDevice(device);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }

        }
    }
    public boolean pairifexist(byte subnet,byte device){
        boolean result=false;
        int sub=subnet&0xff;
        int dev=device&0xff;
        for(int i = 0; i<MainActivity.netdeviceList.size(); i++){
            if((String.valueOf(sub).equals(MainActivity.netdeviceList.get(i).get("subnetID")))&&(String.valueOf(dev).equals(MainActivity.netdeviceList.get(i).get("deviceID")))){
                result=true;
                break;
            }
        }
        return result;
    }
    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        intentFilter.addAction(FounctionActivity.ACTION_BACKPRESS);
        return intentFilter;
    }


    public void getdevice() {

        try {
            int intOP = 0x000E;
            short shortLenOfAddtionalBuf;
            // 命令
            byte[] arrayAddtional = new byte[0];
            byte byteSubnetID=(byte)0xff;
            byte byteDeviceID=(byte)0xff;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            MainActivity.mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }// end
}
