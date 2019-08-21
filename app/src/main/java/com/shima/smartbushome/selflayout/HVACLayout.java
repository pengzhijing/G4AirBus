package com.shima.smartbushome.selflayout;

/**
 * Created by Administrator on 16-5-25.
 */
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savehvac;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.niocontrol;
import com.shima.smartbushome.util.SharedPreferencesHelper;

public class HVACLayout extends RelativeLayout implements View.OnClickListener{

    private Button on,off,autofan,highfan,mediumfan,
            lowfan,automode,coolmode,fanmode,heatmode,
            tempup,tempdowm;
    TextView fanstate,tempstate,modestate,uint,name,fanremark,moderemark;

    TextView tv_curtemper;
    private LinearLayout layout_curtemper;

    //ImageView fanimage,modeimage;
    private Savehvac thishvac=new Savehvac();
    Handler reflashuihandler=new Handler();
    ACcontrol acc;
    niocontrol niocl;
    boolean powerstate=false;
    public byte CurrentMode=3,CoolTemp=20,HeatTemp=20,AutoTemp=20,CurrentFanMode=2,CurrentTem=-1,
    CurrentUnit=0;
    private byte[] fanArray,modeArray;
    //AC command type define
    private static final byte const_ac_cmd_type_onoff=3;
    private static final byte const_ac_cmd_type_set_cold_tmp=4;
    private static final byte const_ac_cmd_type_set_fan=5;
    private static final byte const_ac_cmd_type_set_mode=6;
    private static final byte const_ac_cmd_type_set_heat_tmp=7;
    private static final byte const_ac_cmd_type_set_auto_tmp=8;

    //fan speed
    private static final byte const_fan_speed_anto=0;
    private static final byte const_fan_speed_high=1;
    private static final byte const_fan_speed_medium=2;
    private static final byte const_fan_speed_low=3;

    //ac mode
    private static final byte const_mode_cool=0;
    private static final byte const_mode_heat=1;
    private static final byte const_mode_fan=2;
    private static final byte const_mode_auto=3;

    //ac temp min/max value
    private  byte const_cool_temp_min=0;
    private  byte const_cool_temp_max=30;
    private  byte const_heat_temp_min=20;
    private  byte const_heat_temp_max=30;
    private  byte const_auto_temp_min=0;
    private  byte const_auto_temp_max=30;

    public HVACLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public HVACLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public void initview(Context context){
        View view = View.inflate(context, R.layout.hvac_layout, this);
        on=(Button)view.findViewById(R.id.on);
        off=(Button)view.findViewById(R.id.off);
        autofan=(Button)view.findViewById(R.id.autofan);
        highfan=(Button)view.findViewById(R.id.highfan);
        mediumfan=(Button)view.findViewById(R.id.mediumfan);
        lowfan=(Button)view.findViewById(R.id.lowfan);
        automode=(Button)view.findViewById(R.id.automode);
        coolmode=(Button)view.findViewById(R.id.cool);
        fanmode=(Button)view.findViewById(R.id.fan);
        heatmode=(Button)view.findViewById(R.id.heat);
        tempup=(Button)view.findViewById(R.id.up);
        tempdowm=(Button)view.findViewById(R.id.down);


       // onoffstate=(TextView)view.findViewById(R.id.state);
        tempstate=(TextView)view.findViewById(R.id.temp);
        fanstate=(TextView)view.findViewById(R.id.fanstate);
        modestate=(TextView)view.findViewById(R.id.mode);
        uint=(TextView)view.findViewById(R.id.uint);
        name=(TextView)view.findViewById(R.id.hvacremark);
        fanremark=(TextView)view.findViewById(R.id.textView66);
        moderemark=(TextView)view.findViewById(R.id.textView68);

        tv_curtemper=(TextView)view.findViewById(R.id.tv_curtemper);
        layout_curtemper=(LinearLayout) view.findViewById(R.id.layout_curtemper);
        layout_curtemper.setOnClickListener(this);

       // fanimage=(ImageView)view.findViewById(R.id.fanimg);
       // modeimage=(ImageView)view.findViewById(R.id.modeimg);
        on.setOnClickListener(this);
        off.setOnClickListener(this);
        autofan.setOnClickListener(this);
        highfan.setOnClickListener(this);
        mediumfan.setOnClickListener(this);
        lowfan.setOnClickListener(this);
        automode.setOnClickListener(this);
        coolmode.setOnClickListener(this);
        fanmode.setOnClickListener(this);
        heatmode.setOnClickListener(this);
        tempup.setOnClickListener(this);
        tempdowm.setOnClickListener(this);
        acc=new ACcontrol();
        niocl=new niocontrol();

    }

    int reflashstate=0,uicount=0;
    boolean readCF=false,readTempRange=false,readCountfanAndMode=false,readcstate=false;
    Runnable reflashrun=new Runnable() {
        @Override
        public void run() {
            uicount++;
            if(uicount>50){
                reflashstate=0;
                uicount=0;
                readCF=false;
                readTempRange=false;
                readCountfanAndMode=false;
                readcstate=false;
                reflashuihandler.removeCallbacks(reflashrun);
            }else{
                switch(reflashstate){
                    case 0:
                        acc.ACReadCFFlag((byte) thishvac.subnetID, (byte) thishvac.deviceID, MainActivity.mydupsocket);
                        // reflashstate=1;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 1:
                        acc.ACReadTempRange((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        //reflashstate=2;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 2:
                        acc.ACReadCountFanAndMode((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        //reflashstate=3;
                        reflashuihandler.postDelayed(reflashrun,70);
                        break;
                    case 3:
                        acc.ACReadCState((byte) thishvac.subnetID, (byte) thishvac.deviceID,MainActivity.mydupsocket);
                        // reflashstate=4;
                        reflashuihandler.postDelayed(reflashrun, 70);
                        break;
                    case 4:
                        reflashstate=0;
                        uicount=0;
                        readCF=false;
                        readTempRange=false;
                        readCountfanAndMode=false;
                        readcstate=false;
                        reflashuihandler.removeCallbacks(reflashrun);
                        break;
                }
            }

        }
    };

    public void removetimer(){
        reflashuihandler.removeCallbacks(reflashrun);
    }
    public void ReflashUI(){
        reflashuihandler.postDelayed(reflashrun,0);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.on:
                setvisable(VISIBLE);
                powerstate=true;
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 因9in1增加 - 发送 0xE01C 1 - 开 2 - 关
                            niocl.IRControl((byte)thishvac.subnetID, (byte)thishvac.deviceID, 1,MainActivity.mydupsocket);
                            //间隔200毫秒
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            SendCommandOfAC(const_ac_cmd_type_onoff, 1);
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
                //ReflashUI();
                //setOnoffstate("ON");
                setONStyle();
                break;
            case R.id.off:
                setvisable(GONE);
                powerstate=false;
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 因9in1增加 - 发送 0xE01C 1 - 开 2 - 关
                            niocl.IRControl((byte)thishvac.subnetID, (byte)thishvac.deviceID, 2,MainActivity.mydupsocket);
                            //间隔200毫秒
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            SendCommandOfAC(const_ac_cmd_type_onoff, 0);
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //setOnoffstate("OFF");
                setOFFStyle();
                break;
            case R.id.up:
                switch(CurrentMode){
                    case const_mode_cool:
                        CoolTemp++;
                        if(CoolTemp>=const_cool_temp_max){
                            CoolTemp=const_cool_temp_max;
                        }else if(CoolTemp<const_cool_temp_min){
                            CoolTemp=const_cool_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_cold_tmp,CoolTemp);
                        setTempstate(String.valueOf(CoolTemp));
                        break;
                    case const_mode_heat:
                        HeatTemp++;
                        if(HeatTemp>=const_heat_temp_max){
                            HeatTemp=const_heat_temp_max;
                        }else if(HeatTemp<const_heat_temp_min){
                            HeatTemp=const_heat_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_heat_tmp,HeatTemp);
                        setTempstate(String.valueOf(HeatTemp));
                        break;
                    case const_mode_auto:
                        AutoTemp++;
                        if(AutoTemp>=const_auto_temp_max){
                            AutoTemp=const_auto_temp_max;
                        }else if(AutoTemp<const_auto_temp_min){
                            AutoTemp=const_auto_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_auto_tmp,AutoTemp);
                        setTempstate(String.valueOf(AutoTemp));
                        break;
                }
                break;
            case R.id.down:
                switch(CurrentMode){
                    case const_mode_cool:
                        CoolTemp--;
                        if(CoolTemp>=const_cool_temp_max){
                            CoolTemp=const_cool_temp_max;
                        }else if(CoolTemp<const_cool_temp_min){
                            CoolTemp=const_cool_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_cold_tmp,CoolTemp);
                        setTempstate(String.valueOf(CoolTemp));
                        break;
                    case const_mode_heat:
                        HeatTemp--;
                        if(HeatTemp>=const_heat_temp_max){
                            HeatTemp=const_heat_temp_max;
                        }else if(HeatTemp<const_heat_temp_min){
                            HeatTemp=const_heat_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_heat_tmp,HeatTemp);
                        setTempstate(String.valueOf(HeatTemp));
                        break;
                    case const_mode_auto:
                        AutoTemp--;
                        if(AutoTemp>=const_auto_temp_max){
                            AutoTemp=const_auto_temp_max;
                        }else if(AutoTemp<const_auto_temp_min){
                            AutoTemp=const_auto_temp_min;
                        }
                        SendCommandOfAC(const_ac_cmd_type_set_auto_tmp,AutoTemp);
                        setTempstate(String.valueOf(AutoTemp));
                        break;
                }
                break;
            case R.id.autofan:
                CurrentFanMode=const_fan_speed_anto;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_anto);
                setFanstate("AUTO");
                setFANStyle(autofan);
                break;
            case R.id.highfan:
                CurrentFanMode=const_fan_speed_high;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_high);
                setFanstate("HIGH");
                setFANStyle(highfan);
                break;
            case R.id.mediumfan:
                CurrentFanMode=const_fan_speed_medium;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_medium);
                setFanstate("MEDIUM");
                setFANStyle(mediumfan);
                break;
            case R.id.lowfan:
                CurrentFanMode=const_fan_speed_low;
                SendCommandOfAC(const_ac_cmd_type_set_fan,const_fan_speed_low);
                setFanstate("LOW");
                setFANStyle(lowfan);
                break;
            case R.id.automode:
                CurrentMode=const_mode_auto;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_auto);
                setModestate("AUTO");
                setMODEStyle(automode);
                break;
            case R.id.cool:
                CurrentMode=const_mode_cool;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_cool);
                setModestate("COOL");
                setMODEStyle(coolmode);
                break;
            case R.id.fan:
                CurrentMode=const_mode_fan;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_fan);
                setModestate("FAN");
                setMODEStyle(fanmode);
                break;
            case R.id.heat:
                CurrentMode=const_mode_heat;
                SendCommandOfAC(const_ac_cmd_type_set_mode,const_mode_heat);
                setModestate("HEAT");
                setMODEStyle(heatmode);
                break;
            case R.id.layout_curtemper:
                showCurTemperSettingView();
                break;
            default:break;
        }
    }
    public void setvisable(int vis){
        autofan.setVisibility(vis);
        highfan.setVisibility(vis);
        mediumfan.setVisibility(vis);
        lowfan.setVisibility(vis);
        automode.setVisibility(vis);
        coolmode.setVisibility(vis);
        fanmode.setVisibility(vis);
        tempup.setVisibility(vis);
        tempdowm.setVisibility(vis);
        heatmode.setVisibility(vis);
        tempstate.setVisibility(vis);
        fanstate.setVisibility(vis);
        modestate.setVisibility(vis);
        fanremark.setVisibility(vis);
        moderemark.setVisibility(vis);
       // fanimage.setVisibility(vis);
       // modeimage.setVisibility(vis);
    }
    /*public void setOnoffstate(String x){
        onoffstate.setText(x);
    }*/
    public void setTempstate(String x){
        if(CurrentUnit==1){
            tempstate.setText(x+"°F");
        }else{
            tempstate.setText(x+"°C");
        }

    }
    public void setFanstate(String x){
        fanstate.setText(x);
        //fanimage.setImageDrawable(img);
    }
    public void setModestate(String x){
        modestate.setText(x);
       // modeimage.setImageDrawable(img);
    }
    //初始化数据
    public void setcontant(Savehvac sh){
        thishvac=sh;
        name.setText("AC:  "+thishvac.hvac_remark);
        initCurTemperData();

        readCurTemperByType(curTemperType);
        //Toast.makeText(getContext(), ""+curTemperType, Toast.LENGTH_SHORT).show();
        ReflashUI();
    }
    public int getsubnetid(){return thishvac.subnetID;}
    public int getdeviceid(){return thishvac.deviceID;}






    public void setReceiveChange(int command,byte value){
        switch (command){
            case 0:
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setONStyle();
                    powerstate=true;
                break;
            case 1:
                    setvisable(GONE);
                    //setOnoffstate("OFF");
                    powerstate=false;
                    setOFFStyle();
                break;
            case 2:
                if(powerstate){
                    setFanstate("AUTO");
                    setFANStyle(autofan);
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("AUTO");
                    setFANStyle(autofan);
                }
                break;
            case 3:
                if(powerstate){
                    setFanstate("HIGH");
                    setFANStyle(highfan);
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("HIGH");
                    setFANStyle(highfan);
                }
                break;
            case 4:
                if(powerstate){
                    setFanstate("MEDIUM");
                    setFANStyle(mediumfan);
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFANStyle(mediumfan);
                }
                break;
            case 5:
                if(powerstate){
                    setFanstate("LOW");
                    setFANStyle(lowfan);
                }else{
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setFanstate("LOW");
                    setFANStyle(lowfan);
                }
                break;
            case 6:
                if(powerstate){
                    CurrentMode=const_mode_auto;
                    setTempstate(String.valueOf((AutoTemp&0xff)));
                    setModestate("AUTO");
                    setMODEStyle(automode);
                }else{
                    CurrentMode=const_mode_auto;
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setTempstate(String.valueOf(AutoTemp&0xff));
                    setModestate("AUTO");
                    setMODEStyle(automode);
                }
                break;
            case 7:
                if(powerstate){
                    CurrentMode=const_mode_cool;
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                    setMODEStyle(coolmode);
                }else{
                    CurrentMode=const_mode_cool;
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                    setMODEStyle(coolmode);
                }
                break;
            case 8:
                if(powerstate){
                    CurrentMode=const_mode_heat;
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                    setMODEStyle(heatmode);
                }else{
                    CurrentMode=const_mode_heat;
                    setvisable(VISIBLE);
                    //setOnoffstate("ON");
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                    setMODEStyle(heatmode);
                }
                break;
            case 9:
                if(powerstate){
                    CurrentMode=const_mode_fan;
                    setModestate("FAN");
                    setMODEStyle(fanmode);
                }else{
                    CurrentMode=const_mode_fan;
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    setModestate("FAN");
                    setMODEStyle(fanmode);
                }
                break;
            case 10:
                if(powerstate){
                    switch(CurrentMode){
                        case const_mode_cool:
                            CoolTemp=value;
                            setTempstate(String.valueOf((int)(CoolTemp)&0xff));
                            break;
                        case const_mode_heat:
                            HeatTemp=value;
                            setTempstate(String.valueOf((int)(HeatTemp)&0xff));
                            break;
                        case const_mode_auto:
                            AutoTemp=value;
                            setTempstate(String.valueOf((int)(AutoTemp)&0xff));
                            break;
                    }
                }else{
                    setvisable(VISIBLE);
                   // setOnoffstate("ON");
                    switch(CurrentMode){
                        case const_mode_cool:
                            CoolTemp=value;
                            setTempstate(String.valueOf((int)(CoolTemp)&0xff));
                            break;
                        case const_mode_heat:
                            HeatTemp=value;
                            setTempstate(String.valueOf((int)(HeatTemp)&0xff));
                            break;
                        case const_mode_auto:
                            AutoTemp=value;
                            setTempstate(String.valueOf((int)(AutoTemp)&0xff));
                            break;
                    }
                }
                break;
                default:break;
        }
    }
    public void setCandF(byte value){
        if(!readCF){
            readCF=true;
            if(value==0){
                uint.setText("℃");
                CurrentUnit=0;
            }else if((value&0xff)==1){
                uint.setText("℉");
                CurrentUnit=1;
            }
            reflashstate=1;
        }
    }
    public void setACTempRange(byte[] value){
        if(!readTempRange){
            readTempRange=true;
            const_cool_temp_min=value[0];
            const_cool_temp_max=value[1];
            const_heat_temp_min=value[2];
            const_heat_temp_max=value[3];
            const_auto_temp_min=value[4];
            const_auto_temp_max=value[5];
            reflashstate=2;
        }
    }

    public void setFanSpeedAndModeCount(byte[] value){
        if(!readCountfanAndMode){
            readCountfanAndMode=true;
            fanArray=new byte[value[25]];
            for(int i=0;i<fanArray.length;i++){
                fanArray[i]=value[26+i];
            }
            int startmode=26+fanArray.length;
            for(int i=0;i<10;i++){
                if((value[startmode]>(byte)0x20)||(value[startmode]==(byte)0x00)){
                    startmode++;
                }else if(value[startmode]<(byte)0x06){
                    break;
                }
            }
            modeArray=new byte[value[startmode]];
            for(int i=0;i<modeArray.length;i++){
                modeArray[i]=value[startmode+1+i];
            }
            reflashstate=3;
        }

    }

    public void setACCurrentState(byte[] value){
        if(!readcstate){
            readcstate=true;
            if(value[25]==0){
                setvisable(GONE);
                //setOnoffstate("OFF");
                powerstate=false;
                setOFFStyle();
            }else{
                setvisable(VISIBLE);
                //setOnoffstate("ON");
                powerstate=true;
                setONStyle();
            }
            CurrentTem=value[29];
            CoolTemp=value[26];
            HeatTemp=value[30];
            AutoTemp=value[32];
            if((value[27]&0x0f)==fanArray.length){
                CurrentFanMode=fanArray[(value[27]&0x0f)-1];
            }else{
                CurrentFanMode=fanArray[(value[27]&0x0f)];
            }
            if (curTemperType==0){
               // tv_curtemper.setText(CurrentTem+"°C");
            }

            switch(CurrentFanMode){
                case const_fan_speed_anto:
                    setFanstate("AUTO");
                    setFANStyle(autofan);
                    break;
                case const_fan_speed_high:
                    setFanstate("HIGH");
                    setFANStyle(highfan);
                    break;
                case const_fan_speed_medium:
                    setFanstate("MEDIUM");
                    setFANStyle(mediumfan);
                    break;
                case const_fan_speed_low:
                    setFanstate("LOW");
                    setFANStyle(lowfan);
                    break;
                default:break;
            }
            if((value[27]>>4)==modeArray.length){
                CurrentMode=modeArray[(value[27]>>4)-1];
            }else{
                CurrentMode=modeArray[(value[27]>>4)];
            }

            switch(CurrentMode){
                case const_mode_auto:
                    setTempstate(String.valueOf(AutoTemp&0xff));
                    setModestate("AUTO");
                    setMODEStyle(automode);
                    break;
                case const_mode_cool:
                    setTempstate(String.valueOf(CoolTemp&0xff));
                    setModestate("COOL");
                    setMODEStyle(coolmode);
                    break;
                case const_mode_fan:
                    setModestate("FAN");
                    setMODEStyle(fanmode);
                    break;
                case const_mode_heat:
                    setTempstate(String.valueOf(HeatTemp&0xff));
                    setModestate("HEAT");
                    setMODEStyle(heatmode);
                    break;
                default:break;
            }
            //CurrentTemp=value[29];
            reflashstate=4;
        }

    }

    private boolean SendCommandOfAC(int intType,int intValue)
    {
        boolean blnSuccess=false;
        try
        {
            blnSuccess=acc.ACControl((byte)thishvac.subnetID, (byte)thishvac.deviceID, intType, intValue,MainActivity.mydupsocket);

        }catch(Exception e)
        {e.printStackTrace();}

        return blnSuccess;
    }



    //设置开的样式
    public void setONStyle(){
        on.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
        off.setBackground(getResources().getDrawable(R.drawable.control_back_10));
    }


    //设置关的样式
    public void setOFFStyle(){
        on.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        off.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
    }


    //设置风的样式
    public void setFANStyle(View view){
        autofan.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        highfan.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        mediumfan.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        lowfan.setBackground(getResources().getDrawable(R.drawable.control_back_10));

        view.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
    }


    //设置模式的样式
    public void setMODEStyle(View view){

        automode.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        coolmode.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        fanmode.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        heatmode.setBackground(getResources().getDrawable(R.drawable.control_back_10));

        view.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
    }


    //初始化HVAC页面当前温度的相关数据
    //由于需求有所改动 当前温度获取方式需要可选的4种方式 为了兼容旧版本数据库采用SharedPreference保存数据

    public int curTemperType=0;//0:HVAC  1:zoneBeast  2:9in1/6in1 sensor 3:4T  //温度传感器获取设备的类型
    public int zoneBeastSubID=0;
    public int zoneBeastDevID=0;
    public int sensorSubID=0;
    public int sensorDevID=0;
    public int fTSubID=0;
    public int fTDevID=0;

    public String CurTemperType="";
    public String ZoneBeastSubID="";
    public String ZoneBeastDevID="";
    public String SensorSubID="";
    public String SensorDevID="";
    public String FTSubID="";
    public String FTDevID="";
    public void initCurTemperData(){
        CurTemperType="CurTemperType"+thishvac._id;
        ZoneBeastSubID="ZoneBeastSubID"+thishvac._id;
        ZoneBeastDevID="ZoneBeastDevID"+thishvac._id;
        SensorSubID="SensorSubID"+thishvac._id;
        SensorDevID="SensorDevID"+thishvac._id;
        FTSubID="FTSubID"+thishvac._id;
        FTDevID="FTDevID"+thishvac._id;

        SharedPreferencesHelper.init(getContext());
        curTemperType=(Integer) SharedPreferencesHelper.getInstance().getData(CurTemperType,0);
        zoneBeastSubID=(Integer) SharedPreferencesHelper.getInstance().getData(ZoneBeastSubID,0);
        zoneBeastDevID=(Integer) SharedPreferencesHelper.getInstance().getData(ZoneBeastDevID,0);
        sensorSubID=(Integer) SharedPreferencesHelper.getInstance().getData(SensorSubID,0);
        sensorDevID=(Integer) SharedPreferencesHelper.getInstance().getData(SensorDevID,0);
        fTSubID=(Integer) SharedPreferencesHelper.getInstance().getData(FTSubID,0);
        fTDevID=(Integer) SharedPreferencesHelper.getInstance().getData(FTDevID,0);
    }


    RadioGroup rg_group;
    RadioButton rb_hvac;
    RadioButton rb_zb;
    RadioButton rb_sensor;
    RadioButton rb_4t;

    EditText et_hvacsubid;
    EditText et_zbsubid;
    EditText et_sensorsubid;
    EditText et_4tsubid;

    EditText et_hvacdevid;
    EditText et_zbdevid;
    EditText et_sensordevid;
    EditText et_4tdevid;

    //当前温度设置弹框
    private void showCurTemperSettingView() {
        AlertView settingalter = new AlertView("Read temperature settings", null, "CANCEL",  new String[]{"SAVE"}, null, getContext(), AlertView.Style.Alert,
                saveClick);
        ViewGroup settingView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.setting_hvaccurtemper, null);
         rg_group= (RadioGroup) settingView.findViewById(R.id.rg_group);
         rb_hvac= (RadioButton) settingView.findViewById(R.id.rb_hvac);
         rb_zb= (RadioButton) settingView.findViewById(R.id.rb_zb);
         rb_sensor= (RadioButton) settingView.findViewById(R.id.rb_sensor);
         rb_4t= (RadioButton) settingView.findViewById(R.id.rb_4t);

         et_hvacsubid= (EditText) settingView.findViewById(R.id.et_hvacsubid);
         et_zbsubid= (EditText) settingView.findViewById(R.id.et_zbsubid);
         et_sensorsubid= (EditText) settingView.findViewById(R.id.et_sensorsubid);
         et_4tsubid= (EditText) settingView.findViewById(R.id.et_4tsubid);

         et_hvacdevid= (EditText) settingView.findViewById(R.id.et_hvacdevid);
         et_zbdevid= (EditText) settingView.findViewById(R.id.et_zbdevid);
         et_sensordevid= (EditText) settingView.findViewById(R.id.et_sensordevid);
         et_4tdevid= (EditText) settingView.findViewById(R.id.et_4tdevid);

        //初始化SharedPreferences工具类
        SharedPreferencesHelper.init(getContext());

        switch ((Integer) SharedPreferencesHelper.getInstance().getData(CurTemperType,0)){
            case 0:
                rb_hvac.setChecked(true);
                break;
            case 1:
                rb_zb.setChecked(true);
                break;
            case 2:
                rb_sensor.setChecked(true);
                break;
            case 3:
                rb_4t.setChecked(true);
                break;
        }



        et_hvacsubid.setText(""+thishvac.subnetID);
        et_zbsubid.setText(""+SharedPreferencesHelper.getInstance().getData(ZoneBeastSubID,0));
        et_sensorsubid.setText(""+SharedPreferencesHelper.getInstance().getData(SensorSubID,0));
        et_4tsubid.setText(""+SharedPreferencesHelper.getInstance().getData(FTSubID,0));

        et_hvacdevid.setText(""+thishvac.deviceID);
        et_zbdevid.setText(""+SharedPreferencesHelper.getInstance().getData(ZoneBeastDevID,0));
        et_sensordevid.setText(""+SharedPreferencesHelper.getInstance().getData(SensorDevID,0));
        et_4tdevid.setText(""+SharedPreferencesHelper.getInstance().getData(FTDevID,0));


        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_hvac:
                      curTemperType=0;
                        break;
                    case  R.id.rb_zb:
                        curTemperType=1;
                        break;
                    case  R.id.rb_sensor:
                        curTemperType=2;
                        break;
                    case  R.id.rb_4t:
                        curTemperType=3;
                        break;
                }
               // Toast.makeText(getContext(), ""+curTemperType, Toast.LENGTH_SHORT).show();
            }
        });




        settingalter.addExtView(settingView);
        settingalter.show();

    }

    //保存读取温度设置
    public com.bigkoo.alertview.OnItemClickListener saveClick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==0){
                //SAVE
                boolean isSuccess=true;
                SharedPreferencesHelper.init(getContext());
                try {
                    zoneBeastSubID=Integer.parseInt(et_zbsubid.getText().toString().trim());
                    sensorSubID=Integer.parseInt(et_sensorsubid.getText().toString().trim());
                    fTSubID=Integer.parseInt(et_4tsubid.getText().toString().trim());

                    zoneBeastDevID=Integer.parseInt(et_zbdevid.getText().toString().trim());
                    sensorDevID=Integer.parseInt(et_sensordevid.getText().toString().trim());
                    fTDevID=Integer.parseInt(et_4tdevid.getText().toString().trim());

                    SharedPreferencesHelper.getInstance().saveData(CurTemperType,curTemperType);

                    SharedPreferencesHelper.getInstance().saveData(ZoneBeastSubID,zoneBeastSubID);
                    SharedPreferencesHelper.getInstance().saveData(SensorSubID,sensorSubID);
                    SharedPreferencesHelper.getInstance().saveData(FTSubID,fTSubID);

                    SharedPreferencesHelper.getInstance().saveData(ZoneBeastDevID,zoneBeastDevID);
                    SharedPreferencesHelper.getInstance().saveData(SensorDevID,sensorDevID);
                    SharedPreferencesHelper.getInstance().saveData(FTDevID,fTDevID);

                }catch (Exception e){
                    e.printStackTrace();
                    isSuccess=false;
                }

                if (isSuccess){
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    curTemperType=(int) SharedPreferencesHelper.getInstance().getData(CurTemperType,0);
                    zoneBeastSubID=(int) SharedPreferencesHelper.getInstance().getData(ZoneBeastSubID,0);
                    zoneBeastDevID=(int) SharedPreferencesHelper.getInstance().getData(ZoneBeastDevID,0);
                    sensorSubID=(int) SharedPreferencesHelper.getInstance().getData(SensorSubID,0);
                    sensorDevID=(int) SharedPreferencesHelper.getInstance().getData(SensorDevID,0);
                    fTSubID=(int) SharedPreferencesHelper.getInstance().getData(FTSubID,0);
                    fTDevID=(int) SharedPreferencesHelper.getInstance().getData(FTDevID,0);

                    readCurTemperByType(curTemperType);
                }else{
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    //根据设置的方式发送读取当前温度命令
    public void readCurTemperByType(int curtempertype){
        switch (curtempertype){
            case 0://hvac
                acc.ACReadTemperatureValue((byte)thishvac.subnetID,(byte)thishvac.deviceID,MainActivity.mydupsocket);
                break;
            case 1://zonebeast
                acc.ACReadTemperatureValue((byte)zoneBeastSubID,(byte)zoneBeastDevID,MainActivity.mydupsocket);
                break;
            case 2://9 in 1 sensor
                acc.ACReadTemperatureValue((byte)sensorSubID,(byte)sensorDevID,MainActivity.mydupsocket);
                break;
            case 3://4T
                acc.ACReadTemperatureValue((byte)fTSubID,(byte)fTDevID,MainActivity.mydupsocket);
                break;
        }
    }

    public void setCurTemperByData(byte[] data){
        int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
        int subid=0;
        int devid=0;
        switch (curTemperType){
            case 0:
                subid=thishvac.subnetID;
                devid=thishvac.deviceID;
                break;
            case 1:
                subid=zoneBeastSubID;
                devid=zoneBeastDevID;
                break;
            case 2:
                subid=sensorSubID;
                devid=sensorDevID;
                break;
            case 3:
                subid=fTSubID;
                devid=fTDevID;
                break;
        }

        if (((data[17]&0xff) == subid) && ((data[18]&0xff) == devid)) {
            switch (x) {
                case 0xe3e8:

                    int tmperType=data[25];//1 摄氏度  0 华氏度 ;华氏度=32＋摄氏度x1.8 ; 摄氏度=(华氏度－32)／1.8

                    int tmper=data[26];
                    if (tmperType==0){
                        tmper=(int) ((tmper-32)/1.8);
                    }
                    tv_curtemper.setText(tmper+"°C");
                    break;
            }
        }

    }
}
