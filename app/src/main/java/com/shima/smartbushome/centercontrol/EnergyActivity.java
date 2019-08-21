package com.shima.smartbushome.centercontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.Adapter.EnergyAdapter;
import com.shima.smartbushome.database.Saveenergy;
import com.shima.smartbushome.founction_command.energycontrol;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SystemUIUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class EnergyActivity extends AppCompatActivity {
    private LineChartView lineChart;
    private GridView deviceview;
    EnergyAdapter madapter;
    RelativeLayout noenergyinfo,detaillayout;
    Handler getdata=new Handler();
    Handler getct24data=new Handler();
    energycontrol ec=new energycontrol();
    boolean deletemode=false;
    Saveenergy thisenergy=new Saveenergy();
    List<String> timelist=new ArrayList<>();
    RadioGroup channelgroup;
    RadioButton rb_channel[]=new RadioButton[25];
    int step=0;
    TextView powerresult,current;
    EditText voltagevalue;
    boolean touchChart=false,startcatchdata=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.energytoolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Energy");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {

            toolbar.setPadding(0,getStatusBarHeight(this),0,0);
        }
        //获取窗口对象
        Window window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        voltagevalue=(EditText)findViewById(R.id.voltagevalue);
        powerresult=(TextView)findViewById(R.id.powerresult);
        current=(TextView)findViewById(R.id.current);
        lineChart = (LineChartView)findViewById(R.id.line_chart);
        deviceview=(GridView)findViewById(R.id.ct24device_view);
        noenergyinfo=(RelativeLayout)findViewById(R.id.noenergyinfo);
        detaillayout=(RelativeLayout)findViewById(R.id.detaillayout);
        channelgroup=(RadioGroup)findViewById(R.id.radioGroup3);
        channelgroup.setOnCheckedChangeListener(channel_change);
        rb_channel[0]=(RadioButton)findViewById(R.id.radioButton);
        rb_channel[1]=(RadioButton)findViewById(R.id.radioButton2);
        rb_channel[2]=(RadioButton)findViewById(R.id.radioButton3);
        rb_channel[3]=(RadioButton)findViewById(R.id.radioButton4);
        rb_channel[4]=(RadioButton)findViewById(R.id.radioButton5);
        rb_channel[5]=(RadioButton)findViewById(R.id.radioButton6);
        rb_channel[6]=(RadioButton)findViewById(R.id.radioButton7);
        rb_channel[7]=(RadioButton)findViewById(R.id.radioButton8);
        rb_channel[8]=(RadioButton)findViewById(R.id.radioButton9);
        rb_channel[9]=(RadioButton)findViewById(R.id.radioButton10);
        rb_channel[10]=(RadioButton)findViewById(R.id.radioButton11);
        rb_channel[11]=(RadioButton)findViewById(R.id.radioButton12);
        rb_channel[12]=(RadioButton)findViewById(R.id.radioButton13);
        rb_channel[13]=(RadioButton)findViewById(R.id.radioButton14);
        rb_channel[14]=(RadioButton)findViewById(R.id.radioButton15);
        rb_channel[15]=(RadioButton)findViewById(R.id.radioButton16);
        rb_channel[16]=(RadioButton)findViewById(R.id.radioButton17);
        rb_channel[17]=(RadioButton)findViewById(R.id.radioButton18);
        rb_channel[18]=(RadioButton)findViewById(R.id.radioButton19);
        rb_channel[19]=(RadioButton)findViewById(R.id.radioButton20);
        rb_channel[20]=(RadioButton)findViewById(R.id.radioButton21);
        rb_channel[21]=(RadioButton)findViewById(R.id.radioButton22);
        rb_channel[22]=(RadioButton)findViewById(R.id.radioButton23);
        rb_channel[23]=(RadioButton)findViewById(R.id.radioButton24);
        rb_channel[24]=(RadioButton)findViewById(R.id.radioButtonall);
       // generateValues();

       // generateData();
     getdata.postDelayed(getdatarun,20);
    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int statusBarHeight=0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }
    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //MainActivity.mydupsocket.StopAllThread();
        getct24data.removeCallbacks(getct24datarun);
    }
    int selectChannel=24;
    public RadioGroup.OnCheckedChangeListener channel_change=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for(int i=0;i<25;i++){
                if(checkedId==rb_channel[i].getId()){
                    selectChannel=i;
                    break;
                }
            }
            if(timelist.size()>0){timelist.clear();}
            if(mAxisXValues.size()>0){mAxisXValues.clear();}
            numberOfPoints=1;
            generateValues();
            SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            timelist.add(date);
            mAxisXValues.add(new AxisValue(numberOfPoints).setLabel(date));
            generateData();
           // getct24data.postDelayed(getct24datarun,20);
        }
    };
    AlertView setIDdialog;
    EditText sub,dev,name;
    List<Saveenergy> allenergy=new ArrayList<>();
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            if(allenergy.size()>0){allenergy.clear();}
            if(timelist.size()>0){timelist.clear();}
            if(mAxisXValues.size()>0){mAxisXValues.clear();}
            allenergy= MainActivity.mgr.queryenergy();

            switch (allenergy.size()){
                case 0:
                    noenergyinfo.setVisibility(View.VISIBLE);
                    detaillayout.setVisibility(View.GONE);
                    deviceview.setVisibility(View.GONE);
                    break;
                case 1:
                    noenergyinfo.setVisibility(View.GONE);
                    detaillayout.setVisibility(View.VISIBLE);
                    deviceview.setVisibility(View.GONE);
                    thisenergy=allenergy.get(0);
                    madapter=new EnergyAdapter(EnergyActivity.this,allenergy);
                    deviceview.setAdapter(madapter);
                    generateValues();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());
                    timelist.add(date);
                    mAxisXValues.add(new AxisValue(numberOfPoints).setLabel(date));
                    generateData();
                    startcatchdata=true;
                    getct24data.postDelayed(getct24datarun, 0);
                    break;
                default:
                    noenergyinfo.setVisibility(View.GONE);
                    detaillayout.setVisibility(View.GONE);
                    deviceview.setVisibility(View.VISIBLE);
                    madapter=new EnergyAdapter(EnergyActivity.this,allenergy);
                    deviceview.setAdapter(madapter);
                    deviceview.setOnItemClickListener(deviceclick);
                    thisenergy=allenergy.get(0);
                    break;
            }

        }
    };

    Runnable getct24datarun=new Runnable() {
        @Override
        public void run() {
            ec.ReadChannelValue((byte)thisenergy.subnetID,(byte)thisenergy.deviceID,MainActivity.mydupsocket);
            getct24data.postDelayed(getct24datarun,3000);
        }
    };
    public AdapterView.OnItemClickListener deviceclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            thisenergy=madapter.getselected(position);
            noenergyinfo.setVisibility(View.GONE);
            detaillayout.setVisibility(View.VISIBLE);
            deviceview.setVisibility(View.GONE);
            step=1;
        }
    };
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==setIDdialog){
                switch (position){
                    case -1:setIDdialog.dismiss();break;
                    case 0:
                        Saveenergy energy=new Saveenergy();
                        energy.energy_id=getenergyID();
                        energy.subnetID=Integer.parseInt(sub.getText().toString());
                        energy.deviceID=Integer.parseInt(dev.getText().toString());
                        energy.energyname=name.getText().toString();
                        energy.channel1_name="1";
                        energy.channel2_name="2";
                        energy.channel3_name="3";
                        energy.channel4_name="4";
                        energy.channel5_name="5";
                        energy.channel6_name="6";
                        energy.channel7_name="7";
                        energy.channel8_name="8";
                        energy.channel9_name="9";
                        energy.channel10_name="10";
                        energy.channel11_name="11";
                        energy.channel12_name="12";
                        energy.channel13_name="13";
                        energy.channel14_name="14";
                        energy.channel15_name="15";
                        energy.channel16_name="16";
                        energy.channel17_name="17";
                        energy.channel18_name="18";
                        energy.channel19_name="19";
                        energy.channel20_name="20";
                        energy.channel21_name="21";
                        energy.channel22_name="22";
                        energy.channel23_name="23";
                        energy.channel24_name="24";
                        MainActivity.mgr.addenergy(energy);
                        getdata.postDelayed(getdatarun, 20);
                        break;
                }
            }else if(o==settingDialog){
                switch (position){
                    case -1:settingDialog.dismiss();break;
                    case 0:
                        Saveenergy energy=new Saveenergy();
                        energy.energy_id=thisenergy.energy_id;
                        energy.subnetID=Integer.parseInt(sub.getText().toString());
                        energy.deviceID=Integer.parseInt(dev.getText().toString());
                        energy.energyname=name.getText().toString();
                        MainActivity.mgr.updateenergy(energy);
                        getdata.postDelayed(getdatarun, 20);
                        break;
                }
            }
        }
    };

    public int getenergyID(){
        int result=0;
        if(allenergy.size()>0){
            result=allenergy.get(allenergy.size()-1).energy_id+1;
        }
        return result;
    }
    public void addenergy(View v){
        setIDdialog=new AlertView("Setting", null, "CANCEL",  new String[]{"SAVE"}, null, EnergyActivity.this, AlertView.Style.Alert,
                itemclick);
        View selfview= getLayoutInflater().inflate(R.layout.setting_acinfo, null);
        sub = (EditText) selfview.findViewById(R.id.acsubedit);
        dev = (EditText) selfview.findViewById(R.id.acdevedit);
        name= (EditText) selfview.findViewById(R.id.acremark);
        sub.setText("0");
        dev.setText("0");
        name.setText("CT24:NO"+getenergyID());
        setIDdialog.addExtView(selfview);
        setIDdialog.setCancelable(false);
        setIDdialog.show();
    }

    public void startgetdata(View v){
        if(startcatchdata){
            Toast.makeText(EnergyActivity.this, "already started", Toast.LENGTH_SHORT).show();
        }else{
            startcatchdata=true;
            getct24data.postDelayed(getct24datarun, 3000);
            Toast.makeText(EnergyActivity.this, "start getting data", Toast.LENGTH_SHORT).show();
        }

    }
    public void stopgetdata(View v){
        if(startcatchdata){
            startcatchdata=false;
            getct24data.removeCallbacks(getct24datarun);
            Toast.makeText(EnergyActivity.this, "stop getting data", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(EnergyActivity.this, "already stop", Toast.LENGTH_SHORT).show();
        }

    }
    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 1;
    float[][] randomNumbersTab ,olddata;
    private void generateValues() {
        randomNumbersTab= new float[maxNumberOfLines][numberOfPoints];
        for (int i = 0; i < numberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) 0;
            }
        }
        olddata= new float[maxNumberOfLines][numberOfPoints];
        olddata=randomNumbersTab;
    }
    private void addnewvalue(int value){
        randomNumbersTab= new float[maxNumberOfLines][numberOfPoints];
        for (int i = 0; i < olddata.length; ++i) {
            for (int j = 0; j < olddata[i].length; ++j) {
                randomNumbersTab[i][j] = olddata[i][j];
            }
            randomNumbersTab[i][numberOfPoints-1]=(float) value;
        }
        olddata= new float[maxNumberOfLines][numberOfPoints];
        olddata=randomNumbersTab;

    }
    private LineChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = true;
    private boolean pointsHaveDifferentColor;
    private void generateData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Time/s");
                axisY.setName("I/mA");
                axisX.setHasTiltedLabels(true);
                axisX.setValues(mAxisXValues);
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChart.setLineChartData(data);

    }
    /**
     * 设置X 轴的显示
     */
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (udp_socket.ACTION_DATA_IN.equals(action)) {
                byte[] rev=(intent.getByteArrayExtra(udp_socket.ACTION_DATA_IN));

                if(rev.length>25){
                    RunReceiveData(rev);
                }
                rev=null;
            }
        }
    };
    boolean doing=false;
    public void RunReceiveData(byte[] data){
        int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
        if(doing){

        }else {
            doing = true;
            if (((data[17]&0xff) == thisenergy.subnetID) && ((data[18]&0xff) == thisenergy.deviceID)){
                switch(x){
                    case 0x0151:

                            int channel_value[]=new int[24];
                            for(int i=0;i<24;i++){
                                channel_value[i]=(data[25+2*i]<<8)+(data[26+2*i]&0xff);
                            }
                            numberOfPoints++;
                            int value=0;
                            if(selectChannel!=24){
                                value=channel_value[selectChannel];
                            }else{
                                for(int i=0;i<24;i++){
                                    value=value+ channel_value[i];
                                }
                            }
                            addnewvalue(value);
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
                            String date = sDateFormat.format(new java.util.Date());
                            timelist.add(date);
                            mAxisXValues.add(new AxisValue(numberOfPoints).setLabel(date));
                            try{
                                current.setText("current: "+value+"mA");
                                powerresult.setText("instantaneous power: "+((double)((Integer.parseInt(voltagevalue.getText().toString()))*
                                        value)/1000)+"W");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            generateData();
                        break;
                }
            }

            doing=false;
        }
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.energy_menu, menu);
        return true;
    }

    AlertView deletealter,settingDialog,pairalter;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] mItems=new String[allenergy.size()] ;
        for(int i=0;i<allenergy.size();i++){
            mItems[i]=allenergy.get(i).energyname;
        }
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                switch (step){
                    case 0:
                        finish();
                        break;
                    case 1:
                        noenergyinfo.setVisibility(View.GONE);
                        detaillayout.setVisibility(View.GONE);
                        deviceview.setVisibility(View.VISIBLE);
                        step=0;
                        break;
                }
                break;
            case R.id.energy_add:
                setIDdialog=new AlertView("Setting", null, "CANCEL",  new String[]{"SAVE"}, null, EnergyActivity.this, AlertView.Style.Alert,
                        itemclick);
                View selfview= getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                sub = (EditText) selfview.findViewById(R.id.acsubedit);
                dev = (EditText) selfview.findViewById(R.id.acdevedit);
                name = (EditText) selfview.findViewById(R.id.acremark);
                sub.setText("0");
                dev.setText("0");
                name.setText("CT24:NO"+getenergyID());
                setIDdialog.addExtView(selfview);
                setIDdialog.setCancelable(false);
                setIDdialog.show();
                getdata.postDelayed(getdatarun, 20);
                break;
            case R.id.energy_delete:
                deletealter = new AlertView("Select CT24 to Delete", null, "CANCEL",  null, null, this, AlertView.Style.Alert,
                        itemclick);
                ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.listdialog, null);
                ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
                extView.setLayoutParams(lp);
                deletelist.setAdapter(new ArrayAdapter(this, R.layout.simplelistitem, mItems));
                deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MainActivity.mgr.deleteenergy("energy",madapter.getselected(position));
                        getdata.postDelayed(getdatarun, 20);
                        Toast.makeText(EnergyActivity.this, "Delete Succeed", Toast.LENGTH_SHORT).show();
                        deletealter.dismiss();
                    }
                });
                deletealter.addExtView(extView);
                deletealter.show();
                break;
            case R.id.energysetting:
                settingDialog=new AlertView("Setting", null, "CANCEL",  new String[]{"SAVE"}, null, EnergyActivity.this, AlertView.Style.Alert,
                        itemclick);
                View selfviewx= getLayoutInflater().inflate(R.layout.setting_acinfo, null);
                sub = (EditText) selfviewx.findViewById(R.id.acsubedit);
                dev = (EditText) selfviewx.findViewById(R.id.acdevedit);
                name = (EditText) selfviewx.findViewById(R.id.acremark);
                sub.setText(String.valueOf(thisenergy.subnetID));
                dev.setText(String.valueOf(thisenergy.deviceID));
                name.setText(thisenergy.energyname);
                settingDialog.addExtView(selfviewx);
                settingDialog.setCancelable(false);
                settingDialog.show();
                break;
            case R.id.energypair:
                pairalter = new AlertView("Select Device", null, "CANCEL",  null, null, this, AlertView.Style.Alert,
                        itemclick);
                View selfviewx2= getLayoutInflater().inflate(R.layout.auto_pair_dialog, null);
                ListView test=(ListView)selfviewx2.findViewById(R.id.listView4);
                DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(this,MainActivity.netdeviceList);
                test.setAdapter(mLeDeviceListAdapter);
                test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Saveenergy energy=new Saveenergy();
                        energy.energy_id=thisenergy.energy_id;
                        energy.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                        energy.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                        energy.energyname=thisenergy.energyname;
                        MainActivity.mgr.updateenergy(energy);
                        getdata.postDelayed(getdatarun, 20);
                        Toast.makeText(EnergyActivity.this, "apir " + MainActivity.netdeviceList.get(position).get("devicename") + " succeed", Toast.LENGTH_SHORT).show();
                        pairalter.dismiss();
                    }
                });
                pairalter.addExtView(selfviewx2);
                pairalter.show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        return intentFilter;
    }

    @Override
    public void onBackPressed(){
        switch (step){
            case 0:
                finish();
                break;
            case 1:
                noenergyinfo.setVisibility(View.GONE);
                detaillayout.setVisibility(View.GONE);
                deviceview.setVisibility(View.VISIBLE);
                step=0;
                break;
        }
    }
}
