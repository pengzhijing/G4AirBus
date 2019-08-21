package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2016/10/20.
 */
public class fancontrol {
    public boolean FanChannelControl(byte byteSubnetID, byte byteDeviceID,
                                        int intChns, int intgear,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0031;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            if(intgear==4){intgear=100;}
            byte[] arrayAddtional = new byte[4];

            arrayAddtional[0] = (byte) intChns;
            arrayAddtional[1] = (byte) intgear;
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
            e.printStackTrace();
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
        return blnSuccess;
    }
}
