package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2016/12/1.
 */
public class securitycontrol {

    public void setAlarm(byte byteSubnetID, byte byteDeviceID, int area,int type,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0104;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];

            arrayAddtional[0] = (byte) area;
            arrayAddtional[1] = (byte) type;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadAreaName(byte byteSubnetID, byte byteDeviceID, int area,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0248;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];

            arrayAddtional[0] = (byte) area;

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
               boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadLogSum(byte byteSubnetID, byte byteDeviceID,String oridate,String tardate,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0138;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            String[] value1=oridate.split("-");
            String[] value2=tardate.split("-");
            byte[] arrayAddtional = new byte[6];

            arrayAddtional[0] = (byte) Integer.parseInt(value1[0].substring(2,4));
            arrayAddtional[1] = (byte) Integer.parseInt(value1[1]);
            arrayAddtional[2] = (byte) Integer.parseInt(value1[2]);
            arrayAddtional[3] = (byte)Integer.parseInt(value2[0].substring(2,4));
            arrayAddtional[4] = (byte)Integer.parseInt(value2[1]);
            arrayAddtional[5] = (byte)Integer.parseInt(value2[2]);
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadLogvalue(byte byteSubnetID, byte byteDeviceID,String oridate,String tardate ,byte[] logvalue,int num,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x013a;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            String[] value1=oridate.split("-");
            String[] value2=tardate.split("-");
            byte[] arrayAddtional = new byte[10];

            arrayAddtional[0] = (byte) Integer.parseInt(value1[0].substring(2,4));
            arrayAddtional[1] = (byte) Integer.parseInt(value1[1]);
            arrayAddtional[2] = (byte) Integer.parseInt(value1[2]);
            arrayAddtional[3] = (byte)Integer.parseInt(value2[0].substring(2,4));
            arrayAddtional[4] = (byte)Integer.parseInt(value2[1]);
            arrayAddtional[5] = (byte)Integer.parseInt(value2[2]);

            arrayAddtional[6] = (byte) logvalue[0];
            arrayAddtional[7] = (byte)logvalue[1];
            arrayAddtional[8] = (byte) ((num &0xff00)>>8);
            arrayAddtional[9] = (byte) ((num ) - (num&0xff00));
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ClearLog(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x014a;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;


            byte[] arrayAddtional = new byte[7];

            arrayAddtional[0] = (byte)0xf8;
            arrayAddtional[1] = (byte)0x38;
            arrayAddtional[2] = (byte)0x38;
            arrayAddtional[3] = (byte)0x38;
            arrayAddtional[4] = (byte)0x38;
            arrayAddtional[5] = (byte)0x38;
            arrayAddtional[6] = (byte)0x38;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void WriteAreaName(byte byteSubnetID, byte byteDeviceID,int area,String name,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x024a;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] name1=name.getBytes("UnicodeLittleUnmarked");
            byte[] name1_2=new byte[name1.length];
            for(int i=0;i<name1.length;i++){
                name1_2[i]=name1[name1.length-1-i];
            }
            byte[] name2=new byte[20];
            if(name1_2.length<20){
                for(int i=0;i<20;i++){
                    if(i<name1_2.length){
                        name2[i]=name1_2[i];
                    }else{
                        name2[i]=(byte)0x20;
                    }
                }
            }
            byte[] arrayAddtional = new byte[22];

            arrayAddtional[0] = (byte)area;
            arrayAddtional[1] = (byte)1;
            for(int i=0;i<20;i++){
                arrayAddtional[i+2]=name2[i];
            }
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void setBypass(byte byteSubnetID, byte byteDeviceID, int area,int enable,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0130;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[3];

            arrayAddtional[0] = (byte) area;
            arrayAddtional[1] = (byte) 0;
            arrayAddtional[2] = (byte) enable;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadBypass(byte byteSubnetID, byte byteDeviceID, int area,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x012e;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];

            arrayAddtional[0] = (byte) area;
            arrayAddtional[1] = (byte) 0;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void ReadWatchDog(byte byteSubnetID, byte byteDeviceID, int area,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0132;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];

            arrayAddtional[0] = (byte) area;
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void WriteWatchDog(byte byteSubnetID, byte byteDeviceID, int area,int enable,byte[] indata,udp_socket mydupsocket) {
        try {
            byte[] arraybyteBufWithoutAA = null;
            byte byteI;

            int intOP = 0x0134;//操作代码,控制灯的开关亮度
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[13];

            arrayAddtional[0] = (byte) area;
            arrayAddtional[1] = (byte) 0;
            arrayAddtional[2] = (byte) indata[0];
            arrayAddtional[3] = (byte) enable;
            arrayAddtional[4] = (byte) indata[1];
            arrayAddtional[5] = (byte) indata[2];
            arrayAddtional[6] = (byte) indata[3];
            arrayAddtional[7] = (byte) indata[4];
            arrayAddtional[8] = (byte) indata[5];
            arrayAddtional[9] = (byte) indata[6];
            arrayAddtional[10] = (byte) indata[7];
            arrayAddtional[11] = (byte) indata[8];
            arrayAddtional[12] = (byte) indata[9];

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            // 发送2次--做一个重发机制
            for (byteI = 1; byteI < 2; byteI++) {
                boolean blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }
}
