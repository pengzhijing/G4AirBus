package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MoodIconAdapter;
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.database.Savemood;
import com.shima.smartbushome.database.Savemoodbutton;
import com.shima.smartbushome.founction_command.ACcontrol;
import com.shima.smartbushome.founction_command.audio_incontrol;
import com.shima.smartbushome.founction_command.curtaincontrol;
import com.shima.smartbushome.founction_command.lightcontrol;
import com.shima.smartbushome.founction_command.moodcontrol;
import com.shima.smartbushome.founction_command.musiccontrol;
import com.shima.smartbushome.founction_command.radiocontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MoodLayout extends LinearLayout implements View.OnLongClickListener,View.OnClickListener{
    View view;
    TextView tv_moodremark;
    ImageView iv_moodicon;
    CheckBox cb_mood;
    LinearLayout item,moodlinear;
    Context rootcontext;
    String moodremark,iconrount;
    View selfview;
    LinearLayout delete;
    LayoutInflater inflater;
    List<Savemood> thismood=new ArrayList<Savemood>();
    Savemoodbutton thismoodbutton;
    Handler getcommandhandler=new Handler();
    Handler senthandler=new Handler();
    boolean deletemode=false;
    moodcontrol mc;
    lightcontrol lc;
    ACcontrol acc;
    musiccontrol muc;
    radiocontrol rc;
    audio_incontrol aic;
    curtaincontrol ctc;
    private List<String> iconarray=new ArrayList<String>(){
        {
            add("mood_icon1");add("mood_icon2");add("mood_icon3");add("mood_icon4");
            add("mood_icon5");add("mood_icon6");add("mood_icon7");add("mood_icon8");
            add("mood_icon9");add("mood_icon10");
        }
    };
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
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public int Music_Source=1,PlayModeChange=2,AlbumorRadioControl=3,
            PlayControl=4,VoluneControl=5,ControlSpecSong=6;
    public MoodLayout(Context context) {
        super(context);
        rootcontext=context;
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }
    public MoodLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        rootcontext=context;
        initview(context);
    }

    public void initview(Context context){
        view = View.inflate(context, R.layout.mood_layout, this);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_moodremark=(TextView)view.findViewById(R.id.mood_text);
        iv_moodicon=(ImageView)view.findViewById(R.id.mood_image);
        cb_mood=(CheckBox)view.findViewById(R.id.mooddeletecb);
        delete=(LinearLayout)view.findViewById(R.id.mood_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.moodswipe));
        //item=(LinearLayout)view.findViewById(R.id.mooditem);
        moodlinear=(LinearLayout)view.findViewById(R.id.moodlinear);
        moodlinear.setOnClickListener(this);
        moodlinear.setOnLongClickListener(this);
        moodlinear.setOnTouchListener(touchcolor);
        delete.setOnClickListener(this);
        mc=new moodcontrol();
        lc=new lightcontrol();
        acc=new ACcontrol();
        muc=new musiccontrol();
        rc=new radiocontrol();
        aic=new audio_incontrol();
        ctc=new curtaincontrol();
        //cb_mood.setOnClickListener(this);
        getcommandhandler.postDelayed(getmoodcommandrun,30);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.moodlinear:
                if(deletemode){
                    cb_mood.setChecked(!cb_mood.isChecked());
                }else{
                    if(thismood.size()==0){
                        getcommandhandler.postDelayed(getmoodcommandrun,30);
                    }
                    senthandler.postDelayed(sentingcommandrun,50);
                }
                break;
            case R.id.mood_delete:
                MainActivity.mgr.deletemoodbutton("moodbutton", thismoodbutton.mood_id, thismoodbutton.room_id);
                MainActivity.mgr.deletemood("mood",thismoodbutton.mood_id,thismoodbutton.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETEMOOD);
                Toast.makeText(rootcontext, "delete succeed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }
    public boolean onLongClick(View v){
        switch(v.getId()){
            case R.id.moodlinear:
                if(deletemode){

                }else{
                    showPopupMenu(moodlinear);
                }
                break;
        }
        return true;
    }

    public OnTouchListener touchcolor=new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()== KeyEvent.ACTION_DOWN){
               // moodlinear.setBackgroundColor(ToColor("9CA7A7A7"));
                //moodlinear.setBackgroundColor(getResources().getColor(R.color.title_transparent_black));
            }else if(event.getAction()==KeyEvent.ACTION_UP){
               // moodlinear.setBackgroundColor(ToColor(colorarray[thismoodbutton.mood_id%7]));
            }
            return false;
        }
    };
    AlertView settingalter,iconalter;
    EditText name;
    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.mood_popup_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                        settingclick);
                selfview= inflater.inflate(R.layout.setting_moodinfo, null);
                final ImageView iconchoose = (ImageView) selfview.findViewById(R.id.imageView7);
                name = (EditText) selfview.findViewById(R.id.changemoodname);
                name.setText(thismoodbutton.mood_name);
                iconrount=thismoodbutton.mood_icon;
                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(rootcontext, thismoodbutton.mood_icon)));
                iconchoose.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iconalter = new AlertView("Icon Selection", null, "CANCEL", null, null, rootcontext, AlertView.Style.Alert,
                                settingclick);
                        View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                        GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                        icongrid.setAdapter(new MoodIconAdapter(rootcontext));
                        icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                iconrount = iconarray.get(position);
                                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(rootcontext, iconarray.get(position))));
                                iconalter.dismiss();
                            }
                        });
                        iconalter.addExtView(selfviewx);
                        iconalter.show();
                    }
                });
                settingalter.addExtView(selfview);
                settingalter.show();
                return false;

            }
        });

        popupMenu.show();

    }
    public com.bigkoo.alertview.OnItemClickListener settingclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==settingalter){
                if(position==0){
                    Savemoodbutton moodbuttoninfo = new Savemoodbutton();
                    moodbuttoninfo.room_id=thismoodbutton.room_id;
                    moodbuttoninfo.mood_id=thismoodbutton.mood_id;
                    moodbuttoninfo.mood_name=name.getText().toString().trim();
                    moodbuttoninfo.mood_icon=iconrount;
                    MainActivity.mgr.updatemoodbutton(moodbuttoninfo);
                    thismoodbutton.mood_name=name.getText().toString().trim();
                    thismoodbutton.mood_icon=iconrount;
                    setcontent(thismoodbutton);
                }
            }

        }
    };

    public void setDeletemode(boolean x){
        deletemode=x;
    }

    public void seticon(Drawable x){
        iv_moodicon.setImageDrawable(x);
    }
    public void setitile(String x){
        tv_moodremark.setText(x);
    }

    public void setcontent(Savemoodbutton sm){
        thismoodbutton=sm;
        setitile(thismoodbutton.mood_name);
       // moodlinear.setBackgroundColor(ToColor(colorarray[thismoodbutton.mood_id%7]));
        moodlinear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        seticon(getResources().getDrawable(getResourdIdByResourdName(rootcontext, thismoodbutton.mood_icon)));
    }

    public void setcheck(boolean x){
        cb_mood.setChecked(x);
    }
    public int getMoodID(){
        return thismoodbutton.mood_id;
    }
    public int getRoomid(){
        return thismoodbutton.room_id;
    }
    public void setdeleteable(boolean x){
        if(x){
            cb_mood.setVisibility(VISIBLE);
        }else{
            cb_mood.setVisibility(INVISIBLE);
        }
    }
    public boolean getchoosestate(){
        return cb_mood.isChecked();
    }


    /*************************************************************************************/
    public static int getResourdIdByResourdName(Context context, String ResName){
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
                // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
                // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
    }
    public void setDialogdismiss(boolean result,DialogInterface dialog){
        try
        {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing");
            field.setAccessible( true );
// 将mShowing变量设为false，表示对话框已关闭
            field.set(dialog, result );
            dialog.dismiss();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public int ToColor(String data){
        int color=0;
        int rin,gin,bin,ain;
        ain=Integer.parseInt(data.substring(0,2),16);
        rin=Integer.parseInt(data.substring(2,4),16);
        gin=Integer.parseInt(data.substring(4,6),16);
        bin=Integer.parseInt(data.substring(6,8),16);
        color= Color.argb(ain, rin, gin, bin);
        return color;
    }
    /*************************************************************************************/
    Runnable getmoodcommandrun=new Runnable() {
        @Override
        public void run() {
            List<Savemood> alldata=MainActivity.mgr.querymood();
            for(int i=0;i<alldata.size();i++){
                if(alldata.get(i).room_id==thismoodbutton.room_id&&alldata.get(i).mood_id==thismoodbutton.mood_id){
                    thismood.add(alldata.get(i));
                }
            }

        }
    };

    Runnable sentingcommandrun=new Runnable() {
        @Override
        public void run() {
            if(senting){
                sentcount++;
                senting=false;
                if(sentcount>=thismood.size()){
                    senthandler.removeCallbacks(sentingcommandrun);
                    sentcount=0;
                }else{
                    senthandler.postDelayed(sentingcommandrun,75);
                }
            }else{
                if(thismood.size()>0){
                    sentcommand();
                }
                senthandler.postDelayed(sentingcommandrun,450);//450
                senting=true;
            }
        }
    };
    /*************************************************************************************/
    int sentcount=0;boolean senting=false;
    public void sentcommand(){
        Savemood sentmood=thismood.get(sentcount);
        switch(sentmood.control_type){
            case 1:
                if(sentmood.value_6==0){
                    lc.SingleChannelControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,sentmood.value_1,sentmood.value_2,MainActivity.mydupsocket);
                }else{
                    byte[] v={(byte)sentmood.value_1,(byte)sentmood.value_2,(byte)sentmood.value_3,(byte)sentmood.value_4};
                  lc.ARGBlightcontrol((byte)sentmood.subnetID,(byte)sentmood.deviceID,ToColor(v),MainActivity.mydupsocket);
                }
                break;
            case 2:
                switch (sentmood.value_1){
                    case 1://on/off
                        acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_onoff,sentmood.value_2,MainActivity.mydupsocket);
                        break;
                    case 2://mode
                        acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_set_mode,sentmood.value_2,MainActivity.mydupsocket);
                        break;
                    case 3://temp
                        switch(sentmood.value_2){
                            case const_mode_cool:
                                acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_set_cold_tmp,sentmood.value_3,MainActivity.mydupsocket);
                                break;
                            case const_mode_heat:
                                acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_set_heat_tmp,sentmood.value_3,MainActivity.mydupsocket);
                                break;
                            case const_mode_auto:
                                acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_set_auto_tmp,sentmood.value_3,MainActivity.mydupsocket);
                                break;
                        }
                        break;
                    case 4://fan speed
                        acc.ACControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,const_ac_cmd_type_set_fan,sentmood.value_2,MainActivity.mydupsocket);
                        break;
                        default:break;
                }
                break;
            case 3:
                switch (sentmood.value_1){
                    case 1:
                        if(sentmood.value_6==1){
                            muc.SwitchtoMusicSD((byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==2){
                            muc.MusicControl((byte) VoluneControl,(byte) 1, (byte) 3,(byte)sentmood.value_5,(byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==3){
                            byte[] songbyte = new byte[2];
                            songbyte[0] = (byte) ((sentmood.value_3 &0xff00)>>8);
                            songbyte[1] = (byte) ((sentmood.value_3 ) - (sentmood.value_3&0xff00));
                            muc.MusicControl((byte) ControlSpecSong, (byte) (sentmood.value_2), songbyte[0], songbyte[1], (byte) sentmood.subnetID, (byte) sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==99){
                            muc.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }
                        break;
                    case 5:
                        if(sentmood.value_6==1){
                            rc.SwitchtoRadio((byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==2){
                            rc.MusicControl((byte) VoluneControl,(byte) 1, (byte) 3,(byte)sentmood.value_5,(byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==3){
                            rc.MusicControl((byte) AlbumorRadioControl, (byte) 6, (byte) (sentmood.value_4), (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==4){
                            rc.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==99){
                            rc.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }
                        break;
                    case 6:
                        if(sentmood.value_6==1){
                            aic.SwitchtoAudioIn((byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==2){
                            aic.MusicControl((byte) VoluneControl,(byte) 1, (byte) 3,(byte)sentmood.value_5,(byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==3){
                            aic.MusicControl((byte) PlayControl, (byte) 3, (byte) 0, (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }else if(sentmood.value_6==99){
                            aic.MusicControl((byte) PlayControl, (byte) 4, (byte) 0, (byte) 0, (byte)sentmood.subnetID,(byte)sentmood.deviceID,MainActivity.mydupsocket);
                        }
                        break;
                }
                break;
            case 4:
                lc.SingleChannelControl((byte)sentmood.subnetID,(byte)sentmood.deviceID,sentmood.value_1,sentmood.value_2,MainActivity.mydupsocket);
                break;
                default:break;
        }
    }

    public int ToColor(byte[] data){
        int color=0;
        int rin,gin,bin;
        rin=(255*(data[0]&0xff))/100;
        gin=(255*(data[1]&0xff))/100;
        bin=(255*(data[2]&0xff))/100;
        String r=Integer.toHexString(rin);
        switch (r.length()){
            case 0:r="00";break;
            case 1:r="0"+r;break;
        }
        String g=Integer.toHexString(gin);
        switch (g.length()){
            case 0:g="00";break;
            case 1:g="0"+g;break;
        }
        String b=Integer.toHexString(bin);
        switch (b.length()){
            case 0:b="00";break;
            case 1:b="0"+b;break;
        }
        color=Color.argb(255,Integer.parseInt(r,16),Integer.parseInt(g,16),Integer.parseInt(b,16));
        return color;
    }
}
