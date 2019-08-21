package com.shima.smartbushome.selflayout;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savefloorheat;
import com.shima.smartbushome.founction_command.FloorHeatControl;
import com.shima.smartbushome.util.SharedPreferencesHelper;

import java.util.Calendar;

/**
 * Created by zhijing on 2018/5/24.
 */

public class FloorHeatLayout extends LinearLayout {

    View rootView;
    Savefloorheat floorHeat = null;

    TextView tv_devicename;
    Button bt_on, bt_off;
    LinearLayout layout_core, layout_manual, layout_day, layout_night, layout_away,layout_curtemper;
    RadioGroup rg_group;
    RadioButton rb_manual, rb_day, rb_night, rb_away, rb_auto;
    SeekBar sb_manual, sb_day, sb_night, sb_away;
    TextView tv_manual, tv_day, tv_night, tv_away,tv_curtemper;
    Button bt_daytime, bt_nighttime;

    FloorHeatControl floorHeatControl;


    int manualTem = 23;
    int manualFault = 0;
    int dayTem = 23;
    int dayFault = 0;
    int nightTem = 23;
    int nightFault = 0;
    int awayTem = 23;
    int awayFault = 0;
    int sensorSubnetId = 0;
    int sensorDeviceId = 0;
    int sensorChannelNo = 0;


    public int curTemperType=0;//0:FloorHeat  1:zoneBeast  2:9in1/6in1 sensor 3:4T  //温度传感器获取设备的类型
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


    public FloorHeatLayout(Context context) {
        super(context);
        initView();
        floorHeatControl = new FloorHeatControl();
    }

    public FloorHeatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        floorHeatControl = new FloorHeatControl();
    }

    //设置没有获取到设备状态
    public void setNullStateUI(){
        tv_curtemper.setText("N/A");
        bt_on.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        bt_off.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        layout_core.setVisibility(GONE);

    }

    public void setFH(final Savefloorheat floorHeat) {
        this.floorHeat = floorHeat;
        tv_devicename.setText("FH:" + floorHeat.floorheat_remark);
        setNullStateUI();
        initCurTemperData();

        //开启子线程发送读取状态指令
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                //读取开关
                                floorHeatControl.ReadOnOrOFFAndModeControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x14, floorHeat.channel, MainActivity.mydupsocket);
                                break;
                            case 1:
                                //读取工作模式
                                floorHeatControl.ReadOnOrOFFAndModeControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, floorHeat.channel, MainActivity.mydupsocket);
                                break;
                            case 2:
                                //读取各个工作模式的温度配置和温度传感器地址
                                floorHeatControl.ReadodeModeStateAndSensorAddressControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, floorHeat.channel, MainActivity.mydupsocket);
                                break;
                            case 3:
                                //读取时间
                                floorHeatControl.ReadTimeControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, floorHeat.channel,MainActivity.mydupsocket);
                                break;
                            case 4:
                                //读取温度传感器温度
                                readCurTemperByType(curTemperType);
                                break;
                        }
                        Thread.sleep(300);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    //初始化视图
    public void initView() {
        rootView = View.inflate(getContext(), R.layout.floorheat_layout, this);

        tv_devicename = (TextView) rootView.findViewById(R.id.tv_devicename);
        tv_curtemper = (TextView) rootView.findViewById(R.id.tv_curtemper);


        bt_on = (Button) rootView.findViewById(R.id.bt_on);
        bt_off = (Button) rootView.findViewById(R.id.bt_off);

        layout_core = (LinearLayout) rootView.findViewById(R.id.layout_core);
        layout_manual = (LinearLayout) rootView.findViewById(R.id.layout_manual);
        layout_day = (LinearLayout) rootView.findViewById(R.id.layout_day);
        layout_night = (LinearLayout) rootView.findViewById(R.id.layout_night);
        layout_away = (LinearLayout) rootView.findViewById(R.id.layout_away);
        layout_curtemper=(LinearLayout) rootView.findViewById(R.id.layout_curtemper);

        rg_group = (RadioGroup) rootView.findViewById(R.id.rg_group);

        rb_manual = (RadioButton) rootView.findViewById(R.id.rb_manual);
        rb_day = (RadioButton) rootView.findViewById(R.id.rb_day);
        rb_night = (RadioButton) rootView.findViewById(R.id.rb_night);
        rb_away = (RadioButton) rootView.findViewById(R.id.rb_away);
        rb_auto = (RadioButton) rootView.findViewById(R.id.rb_auto);

        sb_manual = (SeekBar) rootView.findViewById(R.id.sb_manual);
        sb_day = (SeekBar) rootView.findViewById(R.id.sb_day);
        sb_night = (SeekBar) rootView.findViewById(R.id.sb_night);
        sb_away = (SeekBar) rootView.findViewById(R.id.sb_away);

        tv_manual = (TextView) rootView.findViewById(R.id.tv_manual);
        tv_day = (TextView) rootView.findViewById(R.id.tv_day);
        tv_night = (TextView) rootView.findViewById(R.id.tv_night);
        tv_away = (TextView) rootView.findViewById(R.id.tv_away);


        bt_daytime = (Button) rootView.findViewById(R.id.bt_daytime);
        bt_nighttime = (Button) rootView.findViewById(R.id.bt_nighttime);

        sb_manual.setProgress(5);
        sb_day.setProgress(5);
        sb_night.setProgress(5);
        sb_away.setProgress(5);

        setListener();

    }


    //初始化温度获取设置相关数据
    public void initCurTemperData(){
        CurTemperType="floorHeatCurTemperType"+floorHeat._id;
        ZoneBeastSubID="floorHeatZoneBeastSubID"+floorHeat._id;
        ZoneBeastDevID="floorHeatZoneBeastDevID"+floorHeat._id;
        SensorSubID="floorHeatSensorSubID"+floorHeat._id;
        SensorDevID="floorHeatSensorDevID"+floorHeat._id;
        FTSubID="floorHeatFTSubID"+floorHeat._id;
        FTDevID="floorHeatFTDevID"+floorHeat._id;

        SharedPreferencesHelper.init(getContext());
        curTemperType=(Integer) SharedPreferencesHelper.getInstance().getData(CurTemperType,0);
        zoneBeastSubID=(Integer) SharedPreferencesHelper.getInstance().getData(ZoneBeastSubID,0);
        zoneBeastDevID=(Integer) SharedPreferencesHelper.getInstance().getData(ZoneBeastDevID,0);
        sensorSubID=(Integer) SharedPreferencesHelper.getInstance().getData(SensorSubID,0);
        sensorDevID=(Integer) SharedPreferencesHelper.getInstance().getData(SensorDevID,0);
        fTSubID=(Integer) SharedPreferencesHelper.getInstance().getData(FTSubID,0);
        fTDevID=(Integer) SharedPreferencesHelper.getInstance().getData(FTDevID,0);
    }

    RadioGroup rg_Temgroup;
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
        rg_Temgroup= (RadioGroup) settingView.findViewById(R.id.rg_group);
        rb_hvac= (RadioButton) settingView.findViewById(R.id.rb_hvac);
        rb_hvac.setText("Floor Heat");
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



        et_hvacsubid.setText(""+sensorSubnetId);
        et_zbsubid.setText(""+SharedPreferencesHelper.getInstance().getData(ZoneBeastSubID,0));
        et_sensorsubid.setText(""+SharedPreferencesHelper.getInstance().getData(SensorSubID,0));
        et_4tsubid.setText(""+SharedPreferencesHelper.getInstance().getData(FTSubID,0));

        et_hvacdevid.setText(""+sensorDeviceId);
        et_zbdevid.setText(""+SharedPreferencesHelper.getInstance().getData(ZoneBeastDevID,0));
        et_sensordevid.setText(""+SharedPreferencesHelper.getInstance().getData(SensorDevID,0));
        et_4tdevid.setText(""+SharedPreferencesHelper.getInstance().getData(FTDevID,0));


        rg_Temgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

                    tv_curtemper.setText("N/A");
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
                floorHeatControl.ReadTemperatureValue((byte) sensorSubnetId, (byte) sensorDeviceId, MainActivity.mydupsocket);
                break;
            case 1://zonebeast
                floorHeatControl.ReadTemperatureValue((byte)zoneBeastSubID,(byte)zoneBeastDevID,MainActivity.mydupsocket);
                break;
            case 2://9 in 1 sensor
                floorHeatControl.ReadTemperatureValue((byte)sensorSubID,(byte)sensorDevID,MainActivity.mydupsocket);
                break;
            case 3://4T
                floorHeatControl.ReadTemperatureValue((byte)fTSubID,(byte)fTDevID,MainActivity.mydupsocket);
                break;
        }
    }




    //设置事件监听
    public void setListener() {

        //控制开关
        bt_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setONStyle();
                layout_core.setVisibility(VISIBLE);
                floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x14, 1, floorHeat.channel, MainActivity.mydupsocket);
            }
        });

        bt_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setOFFStyle();
                layout_core.setVisibility(GONE);
                floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x14, 0, floorHeat.channel, MainActivity.mydupsocket);
            }
        });

        //控制工作模式
        rg_group.setOnCheckedChangeListener(rgCheckedChangeListener);

        bt_daytime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayTimeDialog();


            }
        });
        bt_nighttime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNightTimeDialog();

            }
        });


        setSeekBarChangeListener(sb_manual, tv_manual);
        setSeekBarChangeListener(sb_day, tv_day);
        setSeekBarChangeListener(sb_night, tv_night);
        setSeekBarChangeListener(sb_away, tv_away);

        layout_curtemper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurTemperSettingView();
            }
        });
    }

    RadioGroup.OnCheckedChangeListener rgCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_manual:
                    floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, 1, floorHeat.channel, MainActivity.mydupsocket);
                    layout_manual.setVisibility(VISIBLE);
                    layout_day.setVisibility(GONE);
                    layout_night.setVisibility(GONE);
                    layout_away.setVisibility(GONE);
                    break;
                case R.id.rb_day:
                    floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, 2, floorHeat.channel, MainActivity.mydupsocket);
                    layout_manual.setVisibility(GONE);
                    layout_day.setVisibility(VISIBLE);
                    layout_night.setVisibility(GONE);
                    layout_away.setVisibility(GONE);
                    break;
                case R.id.rb_night:
                    floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, 3, floorHeat.channel, MainActivity.mydupsocket);
                    layout_manual.setVisibility(GONE);
                    layout_day.setVisibility(GONE);
                    layout_night.setVisibility(VISIBLE);
                    layout_away.setVisibility(GONE);
                    break;
                case R.id.rb_away:
                    floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, 4, floorHeat.channel, MainActivity.mydupsocket);
                    layout_manual.setVisibility(GONE);
                    layout_day.setVisibility(GONE);
                    layout_night.setVisibility(GONE);
                    layout_away.setVisibility(VISIBLE);
                    break;
                case R.id.rb_auto:
                    floorHeatControl.TypeValueControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, (byte) 0x15, 5, floorHeat.channel, MainActivity.mydupsocket);
                    layout_manual.setVisibility(GONE);
                    layout_day.setVisibility(VISIBLE);
                    layout_night.setVisibility(VISIBLE);
                    layout_away.setVisibility(GONE);
                    break;
            }
        }
    };


    //设置开的样式
    public void setONStyle() {
        bt_on.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
        bt_off.setBackground(getResources().getDrawable(R.drawable.control_back_10));
    }


    //设置关的样式
    public void setOFFStyle() {
        bt_on.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        bt_off.setBackground(getResources().getDrawable(R.drawable.control_blue_10));
    }


    //设置控制条控件拖动事件
    public void setSeekBarChangeListener(SeekBar seekBar, final TextView textView) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 5) {
                    seekBar.setProgress(5);
                } else {
                    textView.setText(progress + "°C");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar == sb_manual) {
                    //配置指令
                    floorHeatControl.ConfigureModeAndSensorAddressControl(
                            (byte) floorHeat.subnetID
                            , (byte) floorHeat.deviceID
                            , floorHeat.channel
                            , seekBar.getProgress()
                            , manualFault
                            , dayTem
                            , dayFault
                            , nightTem
                            , nightFault
                            , awayTem
                            , awayFault
                            , sensorSubnetId
                            , sensorDeviceId
                            , sensorChannelNo
                            , MainActivity.mydupsocket);

                    //控制指令
                    floorHeatControl.TypeValueControl(
                            (byte) floorHeat.subnetID,
                            (byte) floorHeat.deviceID,
                            (byte) 0x17,
                            seekBar.getProgress(),
                            floorHeat.channel,
                            MainActivity.mydupsocket);
                }
                if (seekBar == sb_day) {
                    floorHeatControl.ConfigureModeAndSensorAddressControl(
                            (byte) floorHeat.subnetID
                            , (byte) floorHeat.deviceID
                            , floorHeat.channel
                            , manualTem
                            , manualFault
                            , seekBar.getProgress()
                            , dayFault
                            , nightTem
                            , nightFault
                            , awayTem
                            , awayFault
                            , sensorSubnetId
                            , sensorDeviceId
                            , sensorChannelNo
                            , MainActivity.mydupsocket);
                }
                if (seekBar == sb_night) {
                    floorHeatControl.ConfigureModeAndSensorAddressControl(
                            (byte) floorHeat.subnetID
                            , (byte) floorHeat.deviceID
                            , floorHeat.channel
                            , manualTem
                            , manualFault
                            , dayTem
                            , dayFault
                            , seekBar.getProgress()
                            , nightFault
                            , awayTem
                            , awayFault
                            , sensorSubnetId
                            , sensorDeviceId
                            , sensorChannelNo
                            , MainActivity.mydupsocket);
                }
                if (seekBar == sb_away) {
                    floorHeatControl.ConfigureModeAndSensorAddressControl(
                            (byte) floorHeat.subnetID
                            , (byte) floorHeat.deviceID
                            , floorHeat.channel
                            , manualTem
                            , manualFault
                            , dayTem
                            , dayFault
                            , nightTem
                            , nightFault
                            , seekBar.getProgress()
                            , awayFault
                            , sensorSubnetId
                            , sensorDeviceId
                            , sensorChannelNo
                            , MainActivity.mydupsocket);
                }


            }
        });
    }


    //设置白天时间对话框
    public void showDayTimeDialog() {
        //获取当前系统时间
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //弹出时间对话框
        TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if (hourOfDay <= 9 && minute > 9) {
                    bt_daytime.setText("DAY:0" + hourOfDay + ":" + minute);
                } else if (hourOfDay > 9 && minute <= 9) {
                    bt_daytime.setText("DAY:" + hourOfDay + ":0" + minute);
                } else if (hourOfDay <= 9 && minute <= 9) {
                    bt_daytime.setText("DAY:0" + hourOfDay + ":0" + minute);
                } else {
                    bt_daytime.setText("DAY:" + hourOfDay + ":" + minute);
                }

                //截取字符串获取时间并发送设置时间指令
                String strDayTime = bt_daytime.getText().toString();
                int positionDay = strDayTime.indexOf(":", 4);
                String strDayH = strDayTime.substring(positionDay - 2, positionDay);
                String strDayM = strDayTime.substring(positionDay + 1, positionDay + 3);

                String strNightTime = bt_nighttime.getText().toString();
                int positionNight = strNightTime.indexOf(":", 6);
                String strNightH = strNightTime.substring(positionNight - 2, positionNight);
                String strNightM = strNightTime.substring(positionNight + 1, positionNight + 3);

                int timeDayH = 0;
                int timeDayM = 0;
                int timeNightH = 0;
                int timeNightM = 0;
                try {
                    timeDayH = Integer.parseInt(strDayH);
                    timeDayM = Integer.parseInt(strDayM);
                    timeNightH = Integer.parseInt(strNightH);
                    timeNightM = Integer.parseInt(strNightM);

                    floorHeatControl.ConfigureTimeControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, floorHeat.channel,timeDayH, timeDayM, timeNightH, timeNightM, MainActivity.mydupsocket);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }

            }
        }, hour, minute, true);
        tpd.show();
    }

    //设置白天N时间对话框
    public void showNightTimeDialog() {
        //获取当前系统时间
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //弹出时间对话框
        TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                if (hourOfDay <= 9 && minute > 9) {
                    bt_nighttime.setText("NIGHT:0" + hourOfDay + ":" + minute);
                } else if (hourOfDay > 9 && minute <= 9) {
                    bt_nighttime.setText("NIGHT:" + hourOfDay + ":0" + minute);
                } else if (hourOfDay <= 9 && minute <= 9) {
                    bt_nighttime.setText("NIGHT:0" + hourOfDay + ":0" + minute);
                } else {
                    bt_nighttime.setText("NIGHT:" + hourOfDay + ":" + minute);
                }

                //截取字符串获取时间并发送设置时间指令
                String strDayTime = bt_daytime.getText().toString();
                int positionDay = strDayTime.indexOf(":", 4);
                String strDayH = strDayTime.substring(positionDay - 2, positionDay);
                String strDayM = strDayTime.substring(positionDay + 1, positionDay + 3);

                String strNightTime = bt_nighttime.getText().toString();
                int positionNight = strNightTime.indexOf(":", 6);
                String strNightH = strNightTime.substring(positionNight - 2, positionNight);
                String strNightM = strNightTime.substring(positionNight + 1, positionNight + 3);

                int timeDayH = 0;
                int timeDayM = 0;
                int timeNightH = 0;
                int timeNightM = 0;
                try {
                    timeDayH = Integer.parseInt(strDayH);
                    timeDayM = Integer.parseInt(strDayM);
                    timeNightH = Integer.parseInt(strNightH);
                    timeNightM = Integer.parseInt(strNightM);

                    floorHeatControl.ConfigureTimeControl((byte) floorHeat.subnetID, (byte) floorHeat.deviceID, floorHeat.channel,timeDayH, timeDayM, timeNightH, timeNightM, MainActivity.mydupsocket);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }


            }
        }, hour, minute, true);
        tpd.show();
    }

    //解析反馈的数据
    public void setFeedBackData(byte[] backData) {
        if (floorHeat == null) {
            return;
        }

        int backCode = (((backData[21] & 0xff) << 8) + (int) (backData[22] & 0xff));//反馈的操作代码
        int subnetID = ((int) (backData[17]) & 0xff);
        int deviceID = ((int) (backData[18]) & 0xff);
        int channelNo = -1;
        switch (backCode) {
            case 0xe3d9://操作反馈
            case 0xe3db: //读取开关和工作模式

                channelNo = ((int) (backData[27]) & 0xff);
                if (floorHeat.subnetID == subnetID && floorHeat.deviceID == deviceID && floorHeat.channel == channelNo) {
                    byte backType = backData[25];
                    int backValue = ((int) (backData[26]) & 0xff);

                    if (backType == (byte) 0x14) {//开关
                        switch (backValue) {
                            case 0://OFF
                                setOFFStyle();
                                layout_core.setVisibility(GONE);
                                break;
                            case 1://ON
                                setONStyle();
                                layout_core.setVisibility(VISIBLE);
                                break;
                        }
                    } else if (backType == (byte) 0x15) {//工作模式
                        rg_group.setOnCheckedChangeListener(null);
                        switch (backValue) {
                            case 1://manual
                                rb_manual.setChecked(true);
                                layout_manual.setVisibility(VISIBLE);
                                layout_day.setVisibility(GONE);
                                layout_night.setVisibility(GONE);
                                layout_away.setVisibility(GONE);
                                break;
                            case 2://day
                                rb_day.setChecked(true);
                                layout_manual.setVisibility(GONE);
                                layout_day.setVisibility(VISIBLE);
                                layout_night.setVisibility(GONE);
                                layout_away.setVisibility(GONE);
                                break;
                            case 3://night
                                rb_night.setChecked(true);
                                layout_manual.setVisibility(GONE);
                                layout_day.setVisibility(GONE);
                                layout_night.setVisibility(VISIBLE);
                                layout_away.setVisibility(GONE);
                                break;
                            case 4://away
                                rb_away.setChecked(true);
                                layout_manual.setVisibility(GONE);
                                layout_day.setVisibility(GONE);
                                layout_night.setVisibility(GONE);
                                layout_away.setVisibility(VISIBLE);
                                break;
                            case 5://timer
                                rb_auto.setChecked(true);
                                layout_manual.setVisibility(GONE);
                                layout_day.setVisibility(VISIBLE);
                                layout_night.setVisibility(VISIBLE);
                                layout_away.setVisibility(GONE);
                                break;
                        }
                        rg_group.setOnCheckedChangeListener(rgCheckedChangeListener);
                    }else if(backType == (byte) 0x17){//手动控制温度
                        manualTem=backValue;

                        sb_manual.setOnSeekBarChangeListener(null);
                        sb_manual.setProgress(manualTem);
                        tv_manual.setText(manualTem + "°C");
                        setSeekBarChangeListener(sb_manual, tv_manual);
                    }
                }

                break;
            case 0x03c8://读取各个工作模式的温度配置和温度传感器地址
                channelNo = ((int) (backData[25]) & 0xff);
                if (floorHeat.subnetID == subnetID && floorHeat.deviceID == deviceID && floorHeat.channel == channelNo) {
                    manualTem = ((int) (backData[26]) & 0xff);
                    manualFault = ((int) (backData[27]) & 0xff);
                    dayTem = ((int) (backData[28]) & 0xff);
                    dayFault = ((int) (backData[29]) & 0xff);
                    nightTem = ((int) (backData[30]) & 0xff);
                    nightFault = ((int) (backData[31]) & 0xff);
                    awayTem = ((int) (backData[32]) & 0xff);
                    awayFault = ((int) (backData[33]) & 0xff);
                    sensorSubnetId = ((int) (backData[34]) & 0xff);
                    sensorDeviceId = ((int) (backData[35]) & 0xff);
                    sensorChannelNo = ((int) (backData[36]) & 0xff);


                    sb_manual.setOnSeekBarChangeListener(null);
                    sb_day.setOnSeekBarChangeListener(null);
                    sb_night.setOnSeekBarChangeListener(null);
                    sb_away.setOnSeekBarChangeListener(null);

                    sb_manual.setProgress(manualTem);
                    sb_day.setProgress(dayTem);
                    sb_night.setProgress(nightTem);
                    sb_away.setProgress(awayTem);

                    tv_manual.setText(manualTem + "°C");
                    tv_day.setText(dayTem + "°C");
                    tv_night.setText(nightTem + "°C");
                    tv_away.setText(awayTem + "°C");

                    setSeekBarChangeListener(sb_manual, tv_manual);
                    setSeekBarChangeListener(sb_day, tv_day);
                    setSeekBarChangeListener(sb_night, tv_night);
                    setSeekBarChangeListener(sb_away, tv_away);
                }
                break;
            case  0x03cc:
                channelNo = ((int) (backData[25]) & 0xff);
                if (floorHeat.subnetID == subnetID && floorHeat.deviceID == deviceID && floorHeat.channel == channelNo) {

                    int dayH=((int) (backData[26]) & 0xff);
                    int dayM=((int) (backData[27]) & 0xff);
                    int nightH=((int) (backData[28]) & 0xff);
                    int nightM=((int) (backData[29]) & 0xff);


                    if (dayH <= 9 && dayM > 9) {
                        bt_daytime.setText("DAY:0" + dayH + ":" + dayM);
                    } else if (dayH > 9 && dayM <= 9) {
                        bt_daytime.setText("DAY:" + dayH + ":0" + dayM);
                    } else if (dayH <= 9 && dayM <= 9) {
                        bt_daytime.setText("DAY:0" + dayH + ":0" + dayM);
                    } else {
                        bt_daytime.setText("DAY:" + dayH + ":" + dayM);
                    }


                    if (nightH <= 9 && nightM > 9) {
                        bt_nighttime.setText("NIGHT:0" + nightH + ":" + nightM);
                    } else if (nightH > 9 && nightM <= 9) {
                        bt_nighttime.setText("NIGHT:" + nightH + ":0" + nightM);
                    } else if (nightH <= 9 && nightM <= 9) {
                        bt_nighttime.setText("NIGHT:0" + nightH + ":0" + nightM);
                    } else {
                        bt_nighttime.setText("NIGHT:" + nightH + ":" + nightM);
                    }

                }
                break;
            case 0xe3e8://温度传感器温度



                switch (curTemperType){
                    case 0://floorheat
                        if (sensorSubnetId == subnetID && sensorDeviceId == deviceID ) {
                            int tmperType=backData[25];//1 摄氏度  0 华氏度 ;华氏度=32＋摄氏度x1.8 ; 摄氏度=(华氏度－32)／1.8
                            if ((backData.length-26)>=sensorChannelNo){
                                int tmper=backData[26];
                                if (sensorChannelNo>1){
                                    tmper=backData[25+sensorChannelNo];
                                }
                                if (tmperType==0){
                                    tmper=(int) ((tmper-32)/1.8);
                                }
                                tv_curtemper.setText(tmper+"°C");
                            }
                        }
                        break;
                    case 1://zonebeast
                        if (zoneBeastSubID == subnetID && zoneBeastDevID == deviceID ) {
                            int tmperType=backData[25];//1 摄氏度  0 华氏度 ;华氏度=32＋摄氏度x1.8 ; 摄氏度=(华氏度－32)／1.8
                            int tmper=backData[26];
                            if (tmperType==0){
                                tmper=(int) ((tmper-32)/1.8);
                            }
                            tv_curtemper.setText(tmper+"°C");
                        }
                        break;
                    case 2://9 in 1 sensor

                        if (sensorSubID == subnetID && sensorDevID == deviceID ) {
                            int tmperType=backData[25];//1 摄氏度  0 华氏度 ;华氏度=32＋摄氏度x1.8 ; 摄氏度=(华氏度－32)／1.8
                            int tmper=backData[26];
                            if (tmperType==0){
                                tmper=(int) ((tmper-32)/1.8);
                            }
                            tv_curtemper.setText(tmper+"°C");
                        }
                        break;
                    case 3://4T

                        if (fTSubID == subnetID && fTDevID == deviceID ) {
                            int tmperType=backData[25];//1 摄氏度  0 华氏度 ;华氏度=32＋摄氏度x1.8 ; 摄氏度=(华氏度－32)／1.8
                            int tmper=backData[26];
                            if (tmperType==0){
                                tmper=(int) ((tmper-32)/1.8);
                            }
                            tv_curtemper.setText(tmper+"°C");
                        }
                        break;
                }

                break;
        }

    }

}
