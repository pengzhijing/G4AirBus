package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.CircleView;
import com.shima.smartbushome.database.Savemedia;
import com.shima.smartbushome.database.Savemediabutton;
import com.shima.smartbushome.founction_command.mediacontrol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class MediaType1 extends RelativeLayout implements View.OnClickListener,View.OnLongClickListener{
    View view;
    Context rootcontext;
    mediacontrol mc=new mediacontrol();
    Button mediatype1_on,mediatype1_off,mediatype1_min,mediatype1_plus,mediatype1_mute,
            mediatype1_up,mediatype1_down,mediatype1_left,mediatype1_right,mediatype1_ok,
            mediatype1_v1,mediatype1_v2,mediatype1_v3,mediatype1_v4,mediatype1_num,mediatype1_back,
            mediatype1_home,mediatype1_setting;
    List<Savemediabutton> mediacontent=new ArrayList<>();
    Savemedia thismedia=new Savemedia();
    TextView name;
    public CircleView cv;
    public MediaType1(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public MediaType1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }
    public MediaType1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initview(context);
    }





    public void initview(Context context){
        view = View.inflate(context, R.layout.media_layout_type1, this);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootcontext=context;
        mediatype1_on=(Button)view.findViewById(R.id.mediatype1_on);
        mediatype1_on.setOnClickListener(this);
        mediatype1_on.setOnLongClickListener(this);
        mediatype1_off=(Button)view.findViewById(R.id.mediatype1_off);
        mediatype1_off.setOnClickListener(this);
        mediatype1_off.setOnLongClickListener(this);
        mediatype1_min=(Button)view.findViewById(R.id.mediatype1_min);
        mediatype1_min.setOnClickListener(this);
        mediatype1_min.setOnLongClickListener(this);
        mediatype1_plus=(Button)view.findViewById(R.id.mediatype1_plus);
        mediatype1_plus.setOnClickListener(this);
        mediatype1_plus.setOnLongClickListener(this);
        mediatype1_mute=(Button)view.findViewById(R.id.mediatype1_mute);
        mediatype1_mute.setOnClickListener(this);
        mediatype1_mute.setOnLongClickListener(this);

        cv=(CircleView)view.findViewById(R.id.mediatype_controlview);
        cv.setOnPressListener(controlpress);

        mediatype1_v1=(Button)view.findViewById(R.id.mediatype1_v1);
        mediatype1_v1.setOnClickListener(this);
        mediatype1_v1.setOnLongClickListener(this);
        mediatype1_v2=(Button)view.findViewById(R.id.mediatype1_v2);
        mediatype1_v2.setOnClickListener(this);
        mediatype1_v2.setOnLongClickListener(this);
        mediatype1_v3=(Button)view.findViewById(R.id.mediatype1_v3);
        mediatype1_v3.setOnClickListener(this);
        mediatype1_v3.setOnLongClickListener(this);
        mediatype1_v4=(Button)view.findViewById(R.id.mediatype1_v4);
        mediatype1_v4.setOnClickListener(this);
        mediatype1_v4.setOnLongClickListener(this);
        mediatype1_num=(Button)view.findViewById(R.id.mediatype1_num);
        mediatype1_num.setOnClickListener(this);
        mediatype1_back=(Button)view.findViewById(R.id.mediatype1_back);
        mediatype1_back.setOnClickListener(this);
        mediatype1_back.setOnLongClickListener(this);
        mediatype1_home=(Button)view.findViewById(R.id.mediatype1_home);
        mediatype1_home.setOnClickListener(this);
        mediatype1_home.setOnLongClickListener(this);
        mediatype1_setting=(Button)view.findViewById(R.id.mediatype1_setting);
        mediatype1_setting.setOnClickListener(this);
        mediatype1_setting.setOnLongClickListener(this);
        name=(TextView)view.findViewById(R.id.medianame);


        //初始化方向键控件
        initKeyView(view);

    }

    public void sentcommand(int buttonnum){
        if(getbuttoninfo(buttonnum).ifIRmarco==1){
            mc.IRMarcoControl((byte) thismedia.subnetID, (byte) thismedia.deviceID, getbuttoninfo(buttonnum).media_swno, getbuttoninfo(buttonnum).media_controltype,MainActivity.mydupsocket);
        }else{
            mc.IRControl((byte)thismedia.subnetID,(byte)thismedia.deviceID,getbuttoninfo(buttonnum).media_swno,getbuttoninfo(buttonnum).media_controltype,MainActivity.mydupsocket);
        }
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
        //设置透明度
       /* iv_center.setAlpha(0.9f);
        iv_up.setAlpha(0.9f);
        iv_down.setAlpha(0.9f);
        iv_left.setAlpha(0.9f);
        iv_right.setAlpha(0.9f);*/

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
        //绑定触摸事件
       /* iv_center.setOnTouchListener(new keyViewTouchListener());
        iv_up.setOnTouchListener(new keyViewTouchListener());
        iv_down.setOnTouchListener(new keyViewTouchListener());
        iv_left.setOnTouchListener(new keyViewTouchListener());
        iv_right.setOnTouchListener(new keyViewTouchListener());*/
    }

    //方向键触摸事件
    class keyViewTouchListener implements OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int action = motionEvent.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN://按下
                    //设置透明度
                    view.setAlpha(0.9f);
                    break;
                case MotionEvent.ACTION_UP://放开
                    //设置透明度
                    view.setAlpha(0.7f);
                    break;
            }
            //onTouch事件是否会传递下去  true 不会触发 click/onclick
            return true;
        }
    }

    //方向键点击事件
    class keyViewClickListener implements OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_center:
                    sentcommand(10);
                    break;
                case R.id.iv_up:
                    sentcommand(6);
                    break;
                case R.id.iv_down:
                    sentcommand(7);
                    break;
                case R.id.iv_left:
                    sentcommand(8);
                    break;
                case R.id.iv_right:
                    sentcommand(9);
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
                    setbuttoninfo(10);
                    break;
                case R.id.iv_up:
                    setbuttoninfo(6);
                    break;
                case R.id.iv_down:
                    setbuttoninfo(7);
                    break;
                case R.id.iv_left:
                    setbuttoninfo(8);
                    break;
                case R.id.iv_right:
                    setbuttoninfo(9);
                    break;
            }
            return true;
        }
    }






    //自定义控件方向键点击事件
    public CircleView.OnPressListener controlpress=new CircleView.OnPressListener() {
        @Override
        public void onPress(String direction,String presstype) {
           if(presstype.equals("press")){
               switch (direction){
                   case "center":
                       sentcommand(10);
                       break;
                   case "left":
                       sentcommand(8);
                       break;
                   case "right":
                       sentcommand(9);
                       break;
                   case "up":
                       sentcommand(6);
                       break;
                   case "down":
                       sentcommand(7);
                       break;
               }
           }else if(presstype.equals("longpress")){
               switch (direction){
                   case "center":
                       setbuttoninfo(10);
                       break;
                   case "left":
                       setbuttoninfo(8);
                       break;
                   case "right":
                       setbuttoninfo(9);
                       break;
                   case "up":
                       setbuttoninfo(6);
                       break;
                   case "down":
                       setbuttoninfo(7);
                       break;
               }
           }
        }
    };
    AlertView numpadalter;
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mediatype1_on:
                sentcommand(1);
                break;
            case R.id.mediatype1_off:
                sentcommand(2);
                break;
            case R.id.mediatype1_min:
                sentcommand(3);
                break;
            case R.id.mediatype1_plus:
                sentcommand(4);
                break;
            case R.id.mediatype1_mute:
                sentcommand(5);
                break;
            case R.id.mediatype1_v1:
                sentcommand(11);
                break;
            case R.id.mediatype1_v2:
                sentcommand(12);
                break;
            case R.id.mediatype1_v3:
                sentcommand(13);
                break;
            case R.id.mediatype1_v4:
                sentcommand(14);
                break;
            case R.id.mediatype1_num:
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
                        sentcommand(18);
                    }
                });
                num1.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(18);
                        return false;
                    }
                });
                num2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(19);
                    }
                });
                num2.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(19);
                        return false;
                    }
                });
                num3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(20);
                    }
                });
                num3.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(20);
                        return false;
                    }
                });
                num4.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(21);
                    }
                });
                num4.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(21);
                        return false;
                    }
                });
                num5.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(22);
                    }
                });
                num5.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(22);
                        return false;
                    }
                });
                num6.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(23);
                    }
                });
                num6.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(23);
                        return false;
                    }
                });
                num7.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(24);
                    }
                });
                num7.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(24);
                        return false;
                    }
                });
                num8.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(25);
                    }
                });
                num8.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(25);
                        return false;
                    }
                });
                num9.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(26);
                    }
                });
                num9.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(26);
                        return false;
                    }
                });
                numx.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(27);
                    }
                });
                numx.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(27);
                        return false;
                    }
                });
                num0.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(28);
                    }
                });
                num0.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(28);
                        return false;
                    }
                });
                numq.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentcommand(29);
                    }
                });
                numq.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbuttoninfo(29);
                        return false;
                    }
                });
                numpadalter.addExtView(extView);
                numpadalter.setCancelable(true);
                numpadalter.show();
                break;
            case R.id.mediatype1_back:
                sentcommand(15);
                break;
            case R.id.mediatype1_home:
                sentcommand(16);
                break;
            case R.id.mediatype1_setting:
                sentcommand(17);
                break;
        }
    }
    public boolean onLongClick(View v){
        if(!MainActivity.islockchangeid){
            switch (v.getId()){
                case R.id.mediatype1_on:
                    setbuttoninfo(1);
                    break;
                case R.id.mediatype1_off:
                    setbuttoninfo(2);
                    break;
                case R.id.mediatype1_min:
                    setbuttoninfo(3);
                    break;
                case R.id.mediatype1_plus:
                    setbuttoninfo(4);
                    break;
                case R.id.mediatype1_mute:
                    setbuttoninfo(5);
                    break;
                case R.id.mediatype1_v1:
                    setbuttoninfo(11);
                    break;
                case R.id.mediatype1_v2:
                    setbuttoninfo(12);
                    break;
                case R.id.mediatype1_v3:
                    setbuttoninfo(13);
                    break;
                case R.id.mediatype1_v4:
                    setbuttoninfo(14);
                    break;
                case R.id.mediatype1_num:
                    break;
                case R.id.mediatype1_back:
                    setbuttoninfo(15);
                    break;
                case R.id.mediatype1_home:
                    setbuttoninfo(16);
                    break;
                case R.id.mediatype1_setting:
                    setbuttoninfo(17);
                    break;
            }
        }

        return true;
    }
    AlertView settingalter;
    EditText IRNO;
    public static CheckBox onoroff,isIRMarco;
    int updatebuttonnum=0;
    private void setbuttoninfo(int num) {
        updatebuttonnum=num;
        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                settingclick);
        ViewGroup setextView = (ViewGroup) LayoutInflater.from(rootcontext).inflate(R.layout.setting_mediabuttoninfo, null);
        IRNO = (EditText) setextView.findViewById(R.id.setmediabutton_no);
        onoroff = (CheckBox) setextView.findViewById(R.id.setmediabutton_onoff);
        isIRMarco = (CheckBox) setextView.findViewById(R.id.setmediabutton_ismarco);

        IRNO.setText(String.valueOf(getbuttoninfo(num).media_swno));
        if(getbuttoninfo(num).media_controltype==255){
            onoroff.setChecked(true);
        }else{
            onoroff.setChecked(false);
        }

        if(getbuttoninfo(num).ifIRmarco==1){
            isIRMarco.setChecked(true);
        }else{
            isIRMarco.setChecked(false);
        }
        settingalter.addExtView(setextView);
        settingalter.show();
    }

    public com.bigkoo.alertview.OnItemClickListener settingclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==-1){

            }
            if(o==settingalter){
                if(position==0){
                   Savemediabutton updatamediabutton=new Savemediabutton();
                    updatamediabutton.room_id=thismedia.room_id;
                    updatamediabutton.media_id=thismedia.media_id;
                    updatamediabutton.button_num=updatebuttonnum;
                    updatamediabutton.media_swno=Integer.parseInt(IRNO.getText().toString().trim());
                    updatamediabutton.media_type=1;
                    if(onoroff.isChecked()){
                        updatamediabutton.media_controltype=255;
                    }else{
                        updatamediabutton.media_controltype=0;
                    }
                    if(isIRMarco.isChecked()){
                        updatamediabutton.ifIRmarco=1;
                    }else{
                        updatamediabutton.ifIRmarco=0;
                    }
                    MainActivity.mgr.updatemediabutton(updatamediabutton);
                    renewdata();
                }
            }

        }
    };

    public void renewdata(){
        List<Savemediabutton> allmediabutton= MainActivity.mgr.querymediabutton();
        if (mediacontent.size() > 0) {
            mediacontent.clear();
        }
        for(int i=0;i<allmediabutton.size();i++){
            if(allmediabutton.get(i).room_id==thismedia.room_id&&allmediabutton.get(i).media_id==thismedia.media_id){
                mediacontent.add(allmediabutton.get(i));
            }
        }
    }
    public void setcontent(Savemedia data){
        thismedia=data;
        List<Savemediabutton> allmediabutton= MainActivity.mgr.querymediabutton();
        if (mediacontent.size() > 0) {
            mediacontent.clear();
        }
        for(int i=0;i<allmediabutton.size();i++){
            if(allmediabutton.get(i).room_id==data.room_id&&allmediabutton.get(i).media_id==data.media_id){
                mediacontent.add(allmediabutton.get(i));
            }
        }
        name.setText(thismedia.media_statement);
    }

    public Savemediabutton getbuttoninfo(int buttonnum){
        Savemediabutton result=new Savemediabutton();
        for(int i=0;i<mediacontent.size();i++){
            if(mediacontent.get(i).button_num==buttonnum){
                result=mediacontent.get(i);
                break;
            }
        }
        return result;
    }
}
