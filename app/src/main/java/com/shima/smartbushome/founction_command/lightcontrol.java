package com.shima.smartbushome.founction_command;

import android.util.Log;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 16-5-25.
 */
public class lightcontrol {

    public boolean SingleChannelControl(byte byteSubnetID, byte byteDeviceID,
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

                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
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
        return blnSuccess;
    }

    public void ARGBlightcontrol(byte byteSubnetID, byte byteDeviceID,int color,udp_socket mydupsocket){
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xF080;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[6];

            arrayAddtional[0] = getcolor("R",color);
            arrayAddtional[1] = getcolor("G",color);
            arrayAddtional[2] = getcolor("B",color);
            arrayAddtional[3] = (byte)(0x64-getcolor("A",color));
            arrayAddtional[4] = (byte) 0;
            arrayAddtional[5] = (byte) 0;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            System.out.println("sentvalue:"+"   a:"+arrayAddtional[3]+"    r:"+arrayAddtional[0]+"    g:"+arrayAddtional[1]+"      b:"+arrayAddtional[2]);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                // 判断UDPSocket是否关闭，
               /* if (MainActivity.mydupsocket.IsSocketClose() == true) {
                    return false;
                }*/

                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);

            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }
    public byte getcolor(String argb,int color){
        byte value=0;
        if(argb.equals("A")){
            int avalue=Integer.parseInt(Integer.toHexString(color).substring(0, 2), 16);
            value=(byte)((avalue*3922)/10000);
        }else if(argb.equals("R")){
            int rvalue=Integer.parseInt(Integer.toHexString(color).substring(2, 4), 16);
            value=(byte)((rvalue*3922)/10000);
        }else if(argb.equals("G")){
            int gvalue=Integer.parseInt(Integer.toHexString(color).substring(4, 6), 16);
            value=(byte)((gvalue*3922)/10000);
        }else if(argb.equals("B")){
            int bvalue=Integer.parseInt(Integer.toHexString(color).substring(6, 8), 16);
            value=(byte)((bvalue*3922)/10000);
        }
        return value;
    }
    public void getlightstate(byte subnetid,byte deviceid,udp_socket mydupsocket){
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0033;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[0];

//            arrayAddtional[0] = (byte) subnetid;
//            arrayAddtional[1] = (byte) deviceid;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
//            for (byteI = 0; byteI < 2; byteI++) {
//
//               boolean blnSent =mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
//                        intOP, subnetid, deviceid, false);
//            }
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,intOP, subnetid, deviceid, false);

        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

}
