package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 16-5-30.
 */
public class moodcontrol {

    public boolean PanelControl(byte byteSubnetID, byte byteDeviceID,
                                int intCMDID, int intValue, boolean blnNeedResend,
                                boolean blnNeedToWaitANS,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;
        try {
            // int intOP=0xE3D4; older
            int intOP = 0xE3D8;
            short shortLenOfAddtionalBuf;
            byte byteI;
            byte[] arraybyteBufWithoutAA = null;

            byte[] arrayAddtional = new byte[2];
            arrayAddtional[0] = (byte) intCMDID;
            arrayAddtional[1] = (byte) intValue;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            for (byteI = 1; byteI < 2; byteI++) {

                blnSent =mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent == true) {
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
}
