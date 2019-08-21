package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

/**
 * Created by Administrator on 2016/8/22.
 */
public class radiocontrol {
    public void SwitchtoRadio(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

        try {
            int intOP = 0x0218;
            short shortLenOfAddtionalBuf;
            // 命令
            byte[] arrayAddtional = new byte[4];
            arrayAddtional[0] = 1;// type
            arrayAddtional[1] = 4;// source
            arrayAddtional[2] = 0;// anyvalue
            arrayAddtional[3] = 0;// anyvalue

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }

    }// end

    // 播放当前处于暂停的歌曲
    public void MusicControl(byte data1,byte data2, byte data3,byte data4,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

        try {
            int intOP = 0x0218;
            short shortLenOfAddtionalBuf;
            // 命令
            byte[] arrayAddtional = new byte[4];
            arrayAddtional[0] = data1;// 固定值
            arrayAddtional[1] = data2;// 当前列表号
            arrayAddtional[2] = data3;// 固定值
            arrayAddtional[3] = data4;// 当前歌曲号ID

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }

    }// end

    public void GetChannelValue(int channel,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

        try {
            int intOP = 0xe140;
            short shortLenOfAddtionalBuf;
            // 命令
            byte[] arrayAddtional = new byte[1];
            arrayAddtional[0] = (byte)channel;// type

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }// end

    public void WriteChannelValue(int channel,int fm,byte[] remark,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

        try {
            int intOP = 0xe142;
            short shortLenOfAddtionalBuf;
            byte[] fmbyte = new byte[2];
            fmbyte[0] = (byte) ((fm &0xff00)>>8);
            fmbyte[1] = (byte) ((fm) - (fm &0xff00));
            // 命令
            byte[] arrayAddtional = new byte[43];
            arrayAddtional[0] = (byte)channel;// type
            arrayAddtional[1] = (byte)fmbyte[0];// type
            arrayAddtional[2] = (byte)fmbyte[1];// type
            for(int i=0;i<40;i++){
                arrayAddtional[i+3]=remark[i];
            }
            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), e.getMessage(),
            // Toast.LENGTH_SHORT).show();
        }
    }// end

    public void GetMusicState(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

        try {
            int intOP = 0x192e;
            short shortLenOfAddtionalBuf;
            // 命令
            byte[] arrayAddtional = new byte[11];
            arrayAddtional[0] = 0x2a;//
            arrayAddtional[1] = 0x5a;//
            arrayAddtional[2] = 0x31;//
            arrayAddtional[3] = 0x53;//
            arrayAddtional[4] = 0x54;//
            arrayAddtional[5] = 0x41;//
            arrayAddtional[6] = 0x54;//
            arrayAddtional[7] = 0x55;//
            arrayAddtional[8] = 0x53;//
            arrayAddtional[9] = 0x3f;//
            arrayAddtional[10] = 0x0d;//

            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
            mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
