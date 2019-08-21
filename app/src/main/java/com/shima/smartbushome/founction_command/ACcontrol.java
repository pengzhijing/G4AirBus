package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 16-5-30.
 */
public class ACcontrol {
    public boolean ACControl(byte byteSubnetID, byte byteDeviceID, int intType,
                             int intValue,udp_socket mydupsocket) {
        boolean blnSuccess = false;
        try {
            int intOP = 0xE3D8;
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];
            arrayAddtional[0] = (byte) intType;
            arrayAddtional[1] = (byte) intValue;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            blnSuccess = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                    intOP, byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
        return blnSuccess;
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
        }
    }


    //Supported Device: HVAC, Zone Beast, 9in1/6in1 Sensor,4T
    public void ACReadTemperatureValue(byte subid,byte devid,udp_socket mydupsocket){
        try {
            int intOP = 0xE3E7;
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
}
