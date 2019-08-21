package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2016/11/1.
 */
public class mediacontrol {
    public boolean IRControl(byte byteSubnetID, byte byteDeviceID,
                                        int UniversalSwitchNo, int ControlType,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xE01C;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];

            arrayAddtional[0] = (byte) UniversalSwitchNo;
            arrayAddtional[1] = (byte) ControlType;

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
    public boolean IRMarcoControl(byte byteSubnetID, byte byteDeviceID,
                             int UniversalSwitchNo, int ControlType,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0031;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[4];

            arrayAddtional[0] = (byte) UniversalSwitchNo;
            arrayAddtional[1] = (byte) ControlType;
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
    public boolean ReadRemarkOfIR(byte byteSubnetID, byte byteDeviceID,
                             int IRNO,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xD90C;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];

            arrayAddtional[0] = (byte) IRNO;

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

    public boolean ReadQTYofIR(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xD914;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[0];


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

    public boolean ReadIRMarcoRemark(byte byteSubnetID, byte byteDeviceID,
                             int MarcoNo,udp_socket mydupsocket) {
        boolean blnSuccess = false, blnSent = false;

        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0xDC3A;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];

            arrayAddtional[0] = (byte) MarcoNo;

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
            e.printStackTrace();
        }
        return blnSuccess;
    }
}
