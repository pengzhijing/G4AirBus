package com.shima.smartbushome.founction_command;

import android.os.Handler;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2016/8/29.
 */
public class curtaincontrol {

    Handler doublesenthandler=new Handler();
    byte sub,dev;
    int channel2;
    udp_socket udpsocket;
    public void CurtainControl(byte byteSubnetID, byte byteDeviceID,
                                        int channel_1,int channel_2,String control_type,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;
        this.udpsocket=mydupsocket;
            if(control_type.equals("open")){
                SingleChannelControl(byteSubnetID,byteDeviceID,channel_1,100, mydupsocket);
            }else if(control_type.equals("close")){
                SingleChannelControl(byteSubnetID,byteDeviceID,channel_2,100, mydupsocket);
            }else if(control_type.equals("stop")){
                sub=byteSubnetID;
                dev=byteDeviceID;
                channel2=channel_2;
                SingleChannelControl(byteSubnetID,byteDeviceID,channel_1,0, mydupsocket);
                doublesenthandler.postDelayed(doublesentrun,90);
            }
    }


    Runnable doublesentrun=new Runnable() {
        @Override
        public void run() {
            SingleChannelControl(sub,dev,channel2,0,udpsocket);
            doublesenthandler.removeCallbacks(doublesentrun);
        }
    };
    public void SingleChannelControl(byte byteSubnetID, byte byteDeviceID,
                                        int intChns, int intBrightness,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0031;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[4];

            arrayAddtional[0] = (byte) intChns;
            arrayAddtional[1] = (byte) intBrightness;
            arrayAddtional[2] = (byte) 0;
            arrayAddtional[3] = (byte) 0;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                // 判断UDPSocket是否关闭，
               /* if (MainActivity.mydupsocket.IsSocketClose() == true) {
                    return false;
                }*/

                blnSent =mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent == true) // 如果发送成功
                {
                    blnSuccess = true;

                } else {
                    break;
                }
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void getcurtainstate(byte subnetid,byte deviceid,udp_socket mydupsocket){
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0033;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];

            arrayAddtional[0] = (byte) subnetid;
            arrayAddtional[1] = (byte) deviceid;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 0; byteI < 2; byteI++) {
                // 判断UDPSocket是否关闭，
               /* if (MainActivity.mydupsocket.IsSocketClose() == true) {
                    return false;
                }*/

                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, subnetid, deviceid, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }
}
