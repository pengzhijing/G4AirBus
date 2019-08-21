package com.shima.smartbushome.udp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by Administrator on 2016/11/22.
 */

public class GetInfoThread extends Thread {
    private Handler handler;
    private DatagramSocket socket;
    private int msgType=11;

   // private final String IP = "255.255.255.255";//广播地址
    private int PORT = 6000;

    /**
     * 6000端口：C32x系列的端口，用户可以用AT指令更改
     * 6000端口：除C32x系列，其他WIFI模块的端口
     * 1902端口：有人掌控宝系列产品的端口
     */
    //private int targetPort = 6000 ;

    private boolean receive = true;
    private Context mcontext;

    /**
     *
     * @param handler 传入监听此线程的Handler
     *
     */
    public GetInfoThread(Context context,Handler handler) {
        this.handler = handler;
        mcontext=context;
        //close();
        init();
    }

    public void init(){
        try {
            socket = new DatagramSocket(null);
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            if(udp_socket.rsipip){
                WifiManager wifiManager = (WifiManager) mcontext.getSystemService(mcontext.WIFI_SERVICE);
                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                SharedPreferences sharedPre4 = mcontext.getSharedPreferences("wifiinfo", mcontext.MODE_PRIVATE);
                    if((wifiInfo.getSSID()).equals(sharedPre4.getString("wifiname", "not set wifiname"))){//是否是可用的局域网
                        socket.bind(new InetSocketAddress(PORT));
                        System.out.println("------------------rev-1-------------------");
                    }else{
                        socket.bind(new InetSocketAddress(8888));
                        System.out.println("------------------rev-2-------------------");
                    }
            }else{
                socket.bind(new InetSocketAddress(PORT));
                System.out.println("------------------rev-4-------------------");
            }

        } catch (SocketException e) {
            e.printStackTrace();
            sendErrorMsg("Search Thread init fail");
            return;
        }
    }


    public void run() {
        if (socket == null) {
            return;
        }

        try {

            byte[] data = new byte[2048];
            //创建一个空的DatagramPacket对象
            DatagramPacket revPacket = new DatagramPacket(data, data.length);
            while (receive) {
                //服务端接收数据
                socket.receive(revPacket);
                if(null!=handler){
                    byte[] realData = new byte[revPacket.getLength()];
                    System.arraycopy(data, 0, realData, 0, realData.length);
                    Message msg =handler.obtainMessage(msgType,realData);
                    handler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            socket.close();
        }
    }

    public void close() {
        if (socket == null)
            return;
        socket.close();
    }


    private void sendErrorMsg(String info){

    }

    /**
     * 发送数据
     * @param packet
     */
    public void sendMsg(DatagramPacket packet) {
        if (socket != null) {
            try {
                socket.send(packet);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d("tag", "发送失败");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("tag", "发送失败");
            }

        }
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

   /* public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }*/
    public void setMsgType(int msgType){
        this.msgType=msgType;
    }
}
