package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 16-5-30.
 */
public class niocontrol {
    public boolean IRControl(byte byteSubnetID, byte byteDeviceID,
                             int UniversalSwitchNo,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xE01C;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];

            arrayAddtional[0] = (byte) UniversalSwitchNo;
            arrayAddtional[1] = (byte) 255;

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

    public void ReadTempSetting(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0x018c;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[0];

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            boolean blnSuccess =mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, subid, devid, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void Readlux(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xd992;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[0];

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            boolean blnSuccess = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, subid, devid, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadTemp(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xe3e7;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];
            arrayAddtional[0]=1;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            boolean blnSuccess = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, subid, devid, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ACReadCState(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xE0EC;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];
            arrayAddtional[0]=0;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            boolean blnSuccess = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, subid, devid, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
