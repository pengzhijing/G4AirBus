package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.CircleView;
import com.shima.smartbushome.database.Savenio;
import com.shima.smartbushome.founction_command.niocontrol;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public class NioLayout extends RelativeLayout implements View.OnClickListener,View.OnLongClickListener{
    View view;
    Context rootcontext;
    LayoutInflater inflater;
    TextView value_lux,value_exttemp,value_ddp,value_4t,remark;
    Button bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bton,btoff,btnum;
    Savenio thisdevice=new Savenio();
    niocontrol nc=new niocontrol();
    Handler getdevicevalue=new Handler();
    CircleView niocenterview;
    public NioLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public NioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public void initview(Context context){
        view = View.inflate(context, R.layout.niolayout, this);
        rootcontext=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        remark=(TextView)view.findViewById(R.id.nioremark);
        value_lux=(TextView)view.findViewById(R.id.niolayout_luxvalue);
        value_exttemp=(TextView)view.findViewById(R.id.niolayout_etsvalue);
        value_ddp=(TextView)view.findViewById(R.id.niolayout_ddpvalue);
        value_4t=(TextView)view.findViewById(R.id.niolayout_fv);
        bt1=(Button)view.findViewById(R.id.niolayout_bt1);
        bt2=(Button)view.findViewById(R.id.niolayout_bt2);
        bt3=(Button)view.findViewById(R.id.niolayout_bt3);
        bt4=(Button)view.findViewById(R.id.niolayout_bt4);
        bt5=(Button)view.findViewById(R.id.niolayout_bt5);
        bt6=(Button)view.findViewById(R.id.niolayout_bt6);
        bt7=(Button)view.findViewById(R.id.niolayout_bt7);
        bt8=(Button)view.findViewById(R.id.niolayout_bt8);
        niocenterview=(CircleView)view.findViewById(R.id.niocenterview);
        niocenterview.setOnPressListener(centerpress);

        bton=(Button)view.findViewById(R.id.niolayout_bton);
        btoff=(Button)view.findViewById(R.id.niolayout_btoff);
        btnum=(Button)view.findViewById(R.id.niolayout_btpad);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);

        bton.setOnClickListener(this);
        btoff.setOnClickListener(this);
        btnum.setOnClickListener(this);

        bt1.setOnLongClickListener(this);
        bt2.setOnLongClickListener(this);
        bt3.setOnLongClickListener(this);
        bt4.setOnLongClickListener(this);
        bt5.setOnLongClickListener(this);
        bt6.setOnLongClickListener(this);
        bt7.setOnLongClickListener(this);
        bt8.setOnLongClickListener(this);

        bton.setOnLongClickListener(this);
        btoff.setOnLongClickListener(this);
        btnum.setOnLongClickListener(this);


        initKeyView(view);
    }

    //方向键控件
    private ImageView iv_center,iv_up,iv_down,iv_left,iv_right;
    //初始化方向键控件
    public void initKeyView(View view){
        iv_center= (ImageView) view.findViewById(R.id.iv_center);
        iv_up= (ImageView) view.findViewById(R.id.iv_up);
        iv_down= (ImageView) view.findViewById(R.id.iv_down);
        iv_left= (ImageView) view.findViewById(R.id.iv_left);
        iv_right= (ImageView) view.findViewById(R.id.iv_right);

        //绑定点击事件
        iv_center.setOnClickListener(new keyViewClickListener());
        iv_up.setOnClickListener(new keyViewClickListener());
        iv_down.setOnClickListener(new keyViewClickListener());
        iv_left.setOnClickListener(new keyViewClickListener());
        iv_right.setOnClickListener(new keyViewClickListener());
        //绑定长按事件
        iv_center.setOnLongClickListener(new keyViewLongClickListener());
        iv_up.setOnLongClickListener(new keyViewLongClickListener());
        iv_down.setOnLongClickListener(new keyViewLongClickListener());
        iv_left.setOnLongClickListener(new keyViewLongClickListener());
        iv_right.setOnLongClickListener(new keyViewLongClickListener());
    }


    //方向键点击事件
    class keyViewClickListener implements OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_center:
                    sentcommand(13);
                    break;
                case R.id.iv_up:
                    sentcommand(9);
                    break;
                case R.id.iv_down:
                    sentcommand(10);
                    break;
                case R.id.iv_left:
                    sentcommand(11);
                    break;
                case R.id.iv_right:
                    sentcommand(12);
                    break;
            }
        }
    }

    //方向键长按事件
    class keyViewLongClickListener implements OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.iv_center:
                    setbuttoninfo(13);
                    break;
                case R.id.iv_up:
                    setbuttoninfo(9);
                    break;
                case R.id.iv_down:
                    setbuttoninfo(10);
                    break;
                case R.id.iv_left:
                    setbuttoninfo(11);
                    break;
                case R.id.iv_right:
                    setbuttoninfo(12);
                    break;
            }
            return true;
        }
    }

    public CircleView.OnPressListener centerpress=new CircleView.OnPressListener() {
        @Override
        public void onPress(String direction, String presstype) {
            if(presstype.equals("press")){
                switch (direction){
                    case "center":
                        sentcommand(13);
                        break;
                    case "left":
                        sentcommand(11);
                        break;
                    case "right":
                        sentcommand(12);
                        break;
                    case "up":
                        sentcommand(9);
                        break;
                    case "down":
                        sentcommand(10);
                        break;
                }
            }else if(presstype.equals("longpress")){
                switch (direction){
                    case "center":
                        setbuttoninfo(13);
                        break;
                    case "left":
                        setbuttoninfo(11);
                        break;
                    case "right":
                        setbuttoninfo(12);
                        break;
                    case "up":
                        setbuttoninfo(9);
                        break;
                    case "down":
                        setbuttoninfo(10);
                        break;
                }
            }
        }
    };
    public void onClick(View v){
        switch (v.getId()){
            case R.id.niolayout_bt1:
                sentcommand(1);
                break;
            case R.id.niolayout_bt2:
                sentcommand(2);
                break;
            case R.id.niolayout_bt3:
                sentcommand(3);
                break;
            case R.id.niolayout_bt4:
                sentcommand(4);
                break;
            case R.id.niolayout_bt5:
                sentcommand(5);
                break;
            case R.id.niolayout_bt6:
                sentcommand(6);
                break;
            case R.id.niolayout_bt7:
                sentcommand(7);
                break;
            case R.id.niolayout_bt8:
                sentcommand(8);
                break;
            case R.id.niolayout_bton:
                sentcommand(14);
                break;
            case R.id.niolayout_btoff:
                sentcommand(15);
                break;
            case R.id.niolayout_btpad:
                numpadalter = new AlertView(null, null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                        settingclick);
                ViewGroup extView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.media_numpad, null);
                Button num1=(Button)extView.findViewById(R.id.medianumpad_1);
                Button num2=(Button)extView.findViewById(R.id.medianumpad_2);
                Button num3=(Button)extView.findViewById(R.id.medianumpad_3);
                Button num4=(Button)extView.findViewById(R.id.medianumpad_4);
                Button num5=(Button)extView.findViewById(R.id.medianumpad_5);
                Button num6=(Button)extView.findViewById(R.id.medianumpad_6);
                Button num7=(Button)extView.findViewById(R.id.medianumpad_7);
                Button num8=(Button)extView.findViewById(R.id.medianumpad_8);
                Button num9=(Button)extView.findViewById(R.id.medianumpad_9);
                Button numx=(Button)extView.findViewById(R.id.medianumpad_x);
                Button num0=(Button)extView.findViewById(R.id.medianumpad_0);
                Button numq=(Button)extView.findViewById(R.id.medianumpad_q);
                num1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(16);
                    }
                });
                num1.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(16);
                        return false;
                    }
                });
                num2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(17);
                    }
                });
                num2.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(17);
                        return false;
                    }
                });
                num3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(18);
                    }
                });
                num3.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(18);
                        return false;
                    }
                });
                num4.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(19);
                    }
                });
                num4.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(19);
                        return false;
                    }
                });
                num5.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(20);
                    }
                });
                num5.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(20);
                        return false;
                    }
                });
                num6.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(21);
                    }
                });
                num6.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(21);
                        return false;
                    }
                });
                num7.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(22);
                    }
                });
                num7.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(22);
                        return false;
                    }
                });
                num8.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(23);
                    }
                });
                num8.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(23);
                        return false;
                    }
                });
                num9.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(24);
                    }
                });
                num9.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(24);
                        return false;
                    }
                });
                numx.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(25);
                    }
                });
                numx.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(25);
                        return false;
                    }
                });
                num0.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(26);
                    }
                });
                num0.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(26);
                        return false;
                    }
                });
                numq.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(27);
                    }
                });
                numq.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(27);
                        return false;
                    }
                });
                numpadalter.addExtView(extView);
                numpadalter.setCancelable(true);
                numpadalter.show();
                break;
        }
    }

    public boolean onLongClick(View v){
        switch (v.getId()){
            case R.id.niolayout_bt1:
                setbuttoninfo(1);
                break;
            case R.id.niolayout_bt2:
                setbuttoninfo(2);
                break;
            case R.id.niolayout_bt3:
                setbuttoninfo(3);
                break;
            case R.id.niolayout_bt4:
                setbuttoninfo(4);
                break;
            case R.id.niolayout_bt5:
                setbuttoninfo(5);
                break;
            case R.id.niolayout_bt6:
                setbuttoninfo(6);
                break;
            case R.id.niolayout_bt7:
                setbuttoninfo(7);
                break;
            case R.id.niolayout_bt8:
                setbuttoninfo(8);
                break;
            case R.id.niolayout_bton:
                setbuttoninfo(14);
                break;
            case R.id.niolayout_btoff:
                setbuttoninfo(15);
                break;
        }
        return true;
    }
    public void renewdata(){
        List<Savenio> allnio= MainActivity.mgr.querynio();
        for(int i=0;i<allnio.size();i++){
            if((allnio.get(i).room_id== thisdevice.room_id)&&(allnio.get(i).nio_id==thisdevice.nio_id)){
                thisdevice=allnio.get(i);
                break;
            }
        }
        remark.setText(thisdevice.nio_remark);
        bt1.setText(thisdevice.name1);
        bt2.setText(thisdevice.name2);
        bt3.setText(thisdevice.name3);
        bt4.setText(thisdevice.name4);
        bt5.setText(thisdevice.name5);
        bt6.setText(thisdevice.name6);
        bt7.setText(thisdevice.name7);
        bt8.setText(thisdevice.name8);

        bton.setText(thisdevice.name14);
        btoff.setText(thisdevice.name15);
    }

    public void setcontent(Savenio thisroomnio){
        thisdevice=thisroomnio;
        remark.setText(thisdevice.nio_remark);
        bt1.setText(thisdevice.name1);
        bt2.setText(thisdevice.name2);
        bt3.setText(thisdevice.name3);
        bt4.setText(thisdevice.name4);
        bt5.setText(thisdevice.name5);
        bt6.setText(thisdevice.name6);
        bt7.setText(thisdevice.name7);
        bt8.setText(thisdevice.name8);

        bton.setText(thisdevice.name14);
        btoff.setText(thisdevice.name15);
        /*****set to default*****/
        getluxfinish=false;
        gettempsettingfinish=false;
        getvalue1finish=false;
        getvalue2finish=false;
        getvalue3finish=false;
        ddpsub=0;ddpdev=0;fourTsub=0;fourTdev=0;fourTloop=0;
        tempable[0]=0;tempable[1]=0;tempable[2]=0;
        tempi=0;step=0;timecount=0;
        getdevicevalue.postDelayed(getdatarun,30);
    }
    AlertView settingalter,numpadalter;
    EditText buttonremark,buttonnum;
    int updatebuttonnum=0;
    private void setbuttoninfo(int num) {
        updatebuttonnum=num;
        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                settingclick);
        ViewGroup setextView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.setting_niobuttoninfo, null);
        buttonremark = (EditText) setextView.findViewById(R.id.settingniobutton_remark);
        buttonnum = (EditText) setextView.findViewById(R.id.settingniobutton_num);
        if(num>15){
            buttonremark.setText("you can't set numpad name");
            buttonremark.setEnabled(false);
        }else{
            buttonremark.setText(getbuttonremark(num)); 
        }
        buttonnum.setText(String.valueOf(getbuttonvalue(num)));
        settingalter.addExtView(setextView);
        settingalter.show();
    }
    public void sentcommand(int buttonnum){
        nc.IRControl((byte) thisdevice.subnetID, (byte) thisdevice.deviceID, getbuttonvalue(buttonnum),MainActivity.mydupsocket);
    }

    public int getbuttonvalue(int buttonnum){
        int result=0;
        switch (buttonnum)
        {
            case 1:result=thisdevice.value1;break;
            case 2:result=thisdevice.value2;break;
            case 3:result=thisdevice.value3;break;
            case 4:result=thisdevice.value4;break;
            case 5:result=thisdevice.value5;break;
            case 6:result=thisdevice.value6;break;
            case 7:result=thisdevice.value7;break;
            case 8:result=thisdevice.value8;break;
            case 9:result=thisdevice.value9;break;
            case 10:result=thisdevice.value10;break;
            case 11:result=thisdevice.value11;break;
            case 12:result=thisdevice.value12;break;
            case 13:result=thisdevice.value13;break;
            case 14:result=thisdevice.value14;break;
            case 15:result=thisdevice.value15;break;
            case 16:result=thisdevice.value16;break;
            case 17:result=thisdevice.value17;break;
            case 18:result=thisdevice.value18;break;
            case 19:result=thisdevice.value19;break;
            case 20:result=thisdevice.value20;break;
            case 21:result=thisdevice.value21;break;
            case 22:result=thisdevice.value22;break;
            case 23:result=thisdevice.value23;break;
            case 24:result=thisdevice.value24;break;
            case 25:result=thisdevice.value25;break;
            case 26:result=thisdevice.value26;break;
            case 27:result=thisdevice.value27;break;
        }
        return result;
    }

    public String getbuttonremark(int buttonnum){
        String result="";
        switch (buttonnum)
        {
            case 1:result=thisdevice.name1;break;
            case 2:result=thisdevice.name2;break;
            case 3:result=thisdevice.name3;break;
            case 4:result=thisdevice.name4;break;
            case 5:result=thisdevice.name5;break;
            case 6:result=thisdevice.name6;break;
            case 7:result=thisdevice.name7;break;
            case 8:result=thisdevice.name8;break;
            case 9:result=thisdevice.name9;break;
            case 10:result=thisdevice.name10;break;
            case 11:result=thisdevice.name11;break;
            case 12:result=thisdevice.name12;break;
            case 13:result=thisdevice.name13;break;
            case 14:result=thisdevice.name14;break;
            case 15:result=thisdevice.name15;break;
           
        }
        return result;
    }

    public int getsubnetid(){
        return thisdevice.subnetID;
    }
    public int getdeviceid(){
        return thisdevice.deviceID;
    }
    public com.bigkoo.alertview.OnItemClickListener settingclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }
            if(o==settingalter){
                if(position==0){
                    if(updatebuttonnum>15){
                        MainActivity.mgr.updateniovalue(thisdevice.room_id,thisdevice.nio_id,"",
                                Integer.parseInt(buttonnum.getText().toString().trim()),updatebuttonnum);
                    }else{
                        MainActivity.mgr.updateniovalue(thisdevice.room_id,thisdevice.nio_id,buttonremark.getText().toString().trim(),
                                Integer.parseInt(buttonnum.getText().toString().trim()),updatebuttonnum); 
                    }
                    renewdata();
                }
            }

        }
    };

    boolean getluxfinish=false,gettempsettingfinish=false,getvalue1finish=false
            ,getvalue2finish=false,getvalue3finish=false;
    int step=0,timecount=0;
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            switch (step){
                case 0:
                    nc.Readlux((byte)thisdevice.subnetID,(byte)thisdevice.deviceID,MainActivity.mydupsocket);
                    break;
                case 1:
                    nc.ReadTempSetting((byte) thisdevice.subnetID, (byte) thisdevice.deviceID,MainActivity.mydupsocket);
                    break;
                case 2:
                    if(tempi<3){
                        if(tempable[tempi]==0){
                            switch (tempi){
                                case 0:nc.ReadTemp((byte) thisdevice.subnetID, (byte) thisdevice.deviceID,MainActivity.mydupsocket);break;
                                case 1:nc.ReadTemp((byte) ddpsub, (byte) ddpdev,MainActivity.mydupsocket);break;
                                case 2:nc.ReadTemp((byte) fourTsub, (byte) fourTdev,MainActivity.mydupsocket);break;
                            }
                        }else{
                            tempi++;
                        }
                    }else{
                        step=3;
                    }
                    break;
                case 3:
                    stoptimer();
                    break;
            }
            if(step!=3){
                getdevicevalue.postDelayed(getdatarun,500);
            }
           timecount++;
            if(timecount>30){
                if(step!=3){stoptimer();}
            }
        }
    };

    public void stoptimer(){
        getdevicevalue.removeCallbacks(getdatarun);
        getluxfinish=false;
        gettempsettingfinish=false;
        getvalue1finish=false;
        getvalue2finish=false;
        getvalue3finish=false;
        ddpsub=0;ddpdev=0;fourTsub=0;fourTdev=0;fourTloop=0;
        tempable[0]=0;tempable[1]=0;tempable[2]=0;
        tempi=0;step=0;timecount=0;
    }
    int ddpsub=0,ddpdev=0,fourTsub=0,fourTdev=0,fourTloop=0;
    int[] tempable={0,0,0};//0:enable;1:disable
    int tempi=0;
    public void receivedata(byte[] data){
        int x =  (((data[21]&0xff) << 8) + (int) (data[22] & 0xff));
            switch(x){
                case 0xe3e8://获取温度
                    switch (tempi){
                        case 0:
                            if((!getvalue1finish)&& ((data[17]&0xff) ==getsubnetid()) && ((data[18]&0xff) ==getdeviceid())){
                                if(data[34]==0){
                                    value_exttemp.setText((data[26]&0xff)+"℃");
                                }else{
                                    value_exttemp.setText("-"+(data[26]&0xff)+"℃");
                                }
                                getvalue1finish=true;
                                tempi++;
                            }
                            break;
                        case 1:
                            if((!getvalue2finish)&& ((data[17]&0xff) ==ddpsub) && ((data[18]&0xff) ==ddpdev)){
                                if(data[34]==0){
                                    value_ddp.setText((data[26]&0xff)+"℃");
                                }else{
                                    value_ddp.setText("-"+(data[26]&0xff)+"℃");
                                }
                                getvalue2finish=true;
                                tempi++;
                            }

                            break;
                        case 2:
                            if((!getvalue3finish)&& ((data[17]&0xff) ==fourTsub) && ((data[18]&0xff) ==fourTdev)){
                                String fourtv1,fourtv2,fourtv3,fourtv4;
                                if(data[34]==0){
                                    fourtv1=String.valueOf((data[26]&0xff))+"℃";
                                }else{
                                    fourtv1="-"+String.valueOf((data[26]&0xff))+"℃";
                                }
                                if(data[35]==0){
                                    fourtv2=String.valueOf((data[27]&0xff))+"℃";
                                }else{
                                    fourtv2="-"+String.valueOf((data[27]&0xff))+"℃";
                                }
                                if(data[36]==0){
                                    fourtv3=String.valueOf((data[28]&0xff))+"℃";
                                }else{
                                    fourtv3="-"+String.valueOf((data[28]&0xff))+"℃";
                                }
                                if(data[37]==0){
                                    fourtv4=String.valueOf((data[29]&0xff))+"℃";
                                }else{
                                    fourtv4="-"+String.valueOf((data[29]&0xff))+"℃";
                                }
                                value_4t.setText(fourtv1+"/"+fourtv2+"/"+fourtv3+"/"+fourtv4);
                                getvalue3finish=true;
                                tempi++;
                            }
                            break;
                    }
                    break;
                case 0xd993://获取lux value
                    if (((data[17]&0xff) ==getsubnetid()) && ((data[18]&0xff) ==getdeviceid())) {
                        if (!getluxfinish) {
                            int lightvalue = ((data[25] & 0xff) << 8) + (data[26] & 0xff);
                            value_lux.setText(lightvalue + "");
                            getluxfinish = true;
                            step = 1;
                        }
                    }
                    break;
                case 0x018d://获取温度id设置
                    if (((data[17]&0xff) ==getsubnetid()) && ((data[18]&0xff) ==getdeviceid())) {
                        if (!gettempsettingfinish) {
                            if (data[26] == 1) {//0:disable;1:enable
                                tempable[0] = 0;
                            } else {
                                tempable[0] = 1;
                                value_exttemp.setText("Extra Tempature is disable");
                            }

                            if (data[28] == 1) {
                                tempable[1] = 0;
                                ddpsub = data[29] & 0xff;
                                ddpdev = data[30] & 0xff;
                            } else {
                                tempable[1] = 1;
                                value_ddp.setText("DDP Temperature is disable");
                            }

                            if (data[32] == 1) {
                                tempable[2] = 0;
                                fourTsub = data[33] & 0xff;
                                fourTdev = data[34] & 0xff;
                                fourTloop = data[35] & 0xff;
                            } else {
                                tempable[2] = 1;
                                value_4t.setText("4T Temperature is disable");
                            }
                            gettempsettingfinish = true;
                            step = 2;
                        }
                    }
                    break;
                default:
                    break;
            }


    }
}
