package com.shima.smartbushome.founction_command;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.shima.smartbushome.udp.udp_socket;

import java.util.Calendar;

/**
 * Created by Administrator on 16-5-25.
 */
public class FloorHeatControl {


    //配置各个的模式温度和温度传感器地址
    public boolean  ConfigureModeAndSensorAddressControl(
            byte byteSubnetID,
            byte byteDeviceID,
            int channelNo,
            int manualTem,
            int manualFault,
            int dayTem,
            int dayFault,
            int nightTem,
            int nightFault,
            int awayTem,
            int awayFault,
            int sensorSubnetId,
            int sensorDeviceId,
            int sensorChannelNo,
            udp_socket mydupsocket) {

        boolean blnSent = false;
        try {

            int intOP = 0x03c5;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[12];

            arrayAddtional[0] = (byte) channelNo;
            arrayAddtional[1] = (byte) manualTem;
            arrayAddtional[2] = (byte) manualFault;
            arrayAddtional[3] = (byte) dayTem;
            arrayAddtional[4] = (byte) dayFault;
            arrayAddtional[5] = (byte) nightTem;
            arrayAddtional[6] = (byte) nightFault;
            arrayAddtional[7] = (byte) awayTem;
            arrayAddtional[8] = (byte) awayFault;
            arrayAddtional[9] = (byte) sensorSubnetId;
            arrayAddtional[10] = (byte) sensorDeviceId;
            arrayAddtional[11] = (byte) sensorChannelNo;


            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }


    //读取各个工作模式的温度配置和温度传感器地址
    public boolean  ReadodeModeStateAndSensorAddressControl(
            byte byteSubnetID,
            byte byteDeviceID,
            int channelNo,
            udp_socket mydupsocket
    ) {

        boolean blnSent = false;
        try {

            int intOP = 0x03c7;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];

            arrayAddtional[0] = (byte) channelNo;


            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }



    //操作类型
    // Type:
    // 0x14 开关   Value: 0-OFF 1-ON
    // 0x15 工作模式切换  Value: 1-manual 2-day 3-night 4-away 5-timer
    // 0x17 手动温度  Value:5-32
    //feedback:
    //0xe3d9
     // 0 type: 0x14 开关 0x15 工作模式切换
    // 1 value: 0x14(0-OFF,1-ON)  0x15（1-maual,2-day,3-night,4-away,5-timer） 0x17 (5-32)
    // 2 channelNo
    public boolean  TypeValueControl(
            byte byteSubnetID,
            byte byteDeviceID,
            byte type,
            int value,
            int channelNo,
            udp_socket mydupsocket
          ) {

        boolean blnSent = false;
        try {

            int intOP = 0XE3D8;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[3];

            arrayAddtional[0] = type;
            arrayAddtional[1] = (byte) value;
            arrayAddtional[2] = (byte) channelNo;


            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }



    //读取开关和工作模式
    // Type:
    // 0x14 开关
    // 0x15 工作模式切换

    //FeedBack:
    //0xe3db
    // 0 type: 0x14 开关 0x15 工作模式切换
    // 1 value: 0x14(0-OFF,1-ON)  0x15（1-maual,2-day,3-night,4-away,5-timer）
    // 2 channelNo

    public boolean   ReadOnOrOFFAndModeControl(
            byte byteSubnetID,
            byte byteDeviceID,
            byte type,
            int channelNo,
            udp_socket mydupsocket
    ) {

        boolean blnSent = false;
        try {

            int intOP = 0xe3da;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[2];
            arrayAddtional[0] = type;
            arrayAddtional[1] = (byte) channelNo;



            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }

    //配置定时器工作模式开始&结束时间
    public boolean   ConfigureTimeControl(
            byte byteSubnetID,
            byte byteDeviceID,
            int channelNo,
            int dayH,
            int dayM,
            int nightH,
            int nightM,
            udp_socket mydupsocket
    ) {

        boolean blnSent = false;
        try {

            int intOP = 0x03c9;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[5];
            arrayAddtional[0] = (byte) channelNo;
            arrayAddtional[1] = (byte) dayH;
            arrayAddtional[2] = (byte) dayM;
            arrayAddtional[3] = (byte) nightH;
            arrayAddtional[4] = (byte) nightM;




            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }

    //读取定时器工作模式开始&结束时间
    //FeedBack:
    //0x03cc
    // 0 channelNo
    // 1 day mode H: 0-23
    // 2 day mode M: 0-59
    // 3 night mode H: 0-23
    // 4 night mode M: 0-59
    public boolean   ReadTimeControl(
            byte byteSubnetID,
            byte byteDeviceID,
            int channelNo,
            udp_socket mydupsocket
    ) {

        boolean blnSent = false;
        try {

            int intOP = 0x03cb;//操作代码
            short shortLenOfAddtionalBuf;

            byte[] arrayAddtional = new byte[1];
            arrayAddtional[0]=(byte) channelNo;




            shortLenOfAddtionalBuf = (short) (arrayAddtional.length);

            // 发送2次--做一个重发机制
            for (int i = 1; i < 2; i++) {
                blnSent = mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf,
                        intOP, byteSubnetID, byteDeviceID, false);
                if (blnSent){// 如果发送成功
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnSent;
    }



    //读取温度传感器温度
    //Supported Device: HVAC, Zone Beast, 9in1/6in1 Sensor,4T
    //feedback:
    //0XE3E8
    public void ReadTemperatureValue(byte subid,byte devid,udp_socket mydupsocket){
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
