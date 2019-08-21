package com.shima.smartbushome.assist.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class smartBeaconManager {
    private BluetoothAdapter mBluetoothAdapter;
    private long SCAN_PERIOD = 500;
    private Handler mHandler;
    private Context rootcontext;
    private boolean mScanning;
    private IbeaconDetectedListener listener;
    public smartBeaconManager(Context context){
        rootcontext=context;
        mHandler = new Handler();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(rootcontext,"error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(rootcontext, "ble_not_supported", Toast.LENGTH_SHORT).show();

        }
        mBluetoothAdapter.enable();
    }
    public void setIbeaconDetectedListener(IbeaconDetectedListener l) {
        listener = l;
    }
    public interface IbeaconDetectedListener {

        void onBeaconDetect(iBeaconClass.iBeacon ibeacon, double Distence);

    }
    public void start(){
        startDetect();
    }
    public void stop(){
        mHandler.removeCallbacks(stop);
        stopDetect();
    }
    private void stopDetect(){
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private void startDetect(){
        mHandler.postDelayed(stop, SCAN_PERIOD);
        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    public void setSavePowerTime(int time){
        SCAN_PERIOD=time;
    }
    boolean stopb=false;
    Runnable stop=new Runnable() {
        @Override
        public void run() {
            if(stopb){
                stopb=false;
                startDetect();
            }else{
                stopDetect();
                stopb=true;
                mHandler.postDelayed(stop, SCAN_PERIOD);
                receivelist.clear();
            }
        }
    };
    public int limitrssi=10;
    List<iBeaconClass.iBeacon> receivelist=new ArrayList<>();
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
                    if(ibeacon!=null){
                        int bro=0;
                        for(int i=0;i<receivelist.size();i++){
                            if(ibeacon==receivelist.get(i)){
                                bro=1;
                                if((Math.abs(receivelist.get(i).rssi))-(Math.abs(ibeacon.rssi))<=limitrssi){
                                    listener.onBeaconDetect(ibeacon,calculateAccuracy(ibeacon.txPower,ibeacon.rssi));
                                    receivelist.remove(i);
                                    receivelist.add(ibeacon);
                                }
                                break;
                            }
                        }
                        if(bro==0){
                            receivelist.add(ibeacon);
                        }

                    }
                }
            };
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }
}
