package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2017/2/10.
 */
public class statuscontrol {
    public void getlightstate(byte subnetid,byte deviceid,udp_socket mydupsocket){
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

    public void ReadTemp(byte subid,byte devid,byte unit,udp_socket mydupsocket){
        try {
            int intOP = 0xe3e7;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];
            switch (unit){
                case 1:arrayAddtional[0]=1;break;
                case 2:arrayAddtional[0]=0;break;
            }
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            boolean blnSuccess = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, subid, devid, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ACReadCFFlag(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xE120;
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

    public void ACReadTempRange(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0x1900;
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

    public void ACReadCountFanAndMode(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xE124;
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
