package com.shima.smartbushome.selflayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.DeviceListAdapter;
import com.shima.smartbushome.assist.SwipeLayout;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.Savecurtain;
import com.shima.smartbushome.founction_command.curtaincontrol;

/**
 * Created by Administrator on 2016/8/26.
 */
public class CurtainType1 extends LinearLayout implements View.OnClickListener,View.OnLongClickListener{
    private TextView tv_remark,tv_state;
    private ImageView im_image;
    private Button bt_open,bt_close,bt_stop;
    private Space sp1,sp2;
    private CheckBox deleteview;
    Savecurtain curtaincontent;
    boolean deletemode=false,receiveChange=false;
    curtaincontrol cc;
    LinearLayout delete;
    public DBManager mgr;
    Dialog aupairdialog;
    Context rootcontext;
    View view,selfview;
    LayoutInflater inflater;
    LinearLayout curtainlinear;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public CurtainType1(Context context) {
        super(context);
        initview(context);
    }
    public CurtainType1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public void initview(Context context){
        view = View.inflate(context, R.layout.curtain_type1, this);
        rootcontext=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tv_remark=(TextView)view.findViewById(R.id.curtainrmark);
        curtainlinear=(LinearLayout)view.findViewById(R.id.curtainlinear);
        tv_state=(TextView)view.findViewById(R.id.curtainstate);
        im_image=(ImageView)view.findViewById(R.id.curtaintype1imageView);
        bt_open=(Button)view.findViewById(R.id.curtainon);
        bt_stop=(Button)view.findViewById(R.id.curtainstop);
        bt_close=(Button)view.findViewById(R.id.curtainoff);
        deleteview=(CheckBox)view.findViewById(R.id.curtaindelete);
        delete=(LinearLayout)view.findViewById(R.id.curtaintype1_delete);
        SwipeLayout.addSwipeView((SwipeLayout) findViewById(R.id.curtaintype1swipe));
        bt_open.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_close.setOnClickListener(this);
        im_image.setOnClickListener(this);
        delete.setOnClickListener(this);
        tv_remark.setOnLongClickListener(this);
        cc=new curtaincontrol();
        setimage(R.mipmap.curtain_icon1_off);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.curtainon:
                cc.CurtainControl((byte)curtaincontent.subnetID,(byte)curtaincontent.deviceID,curtaincontent.channel_1,
                        curtaincontent.channel_2,"open",MainActivity.mydupsocket);
                setstate("open");
                setimage(R.mipmap.curtain_icon1_on);
                setDBcurtainstate("open");
                break;
            case R.id.curtainoff:
                cc.CurtainControl((byte) curtaincontent.subnetID, (byte) curtaincontent.deviceID, curtaincontent.channel_1,
                        curtaincontent.channel_2, "close",MainActivity.mydupsocket);
                setstate("close");
                setimage(R.mipmap.curtain_icon1_off);
                setDBcurtainstate("close");
                break;
            case R.id.curtainstop:
                cc.CurtainControl((byte) curtaincontent.subnetID, (byte) curtaincontent.deviceID, curtaincontent.channel_1,
                        curtaincontent.channel_2, "stop",MainActivity.mydupsocket);
                setstate("stop");
                break;
            case R.id.curtaintype1imageView:
                String state=tv_state.getText().toString().trim();
                if((state.equals("unknown"))||(state.equals("close"))||(state.equals("stop"))){
                    cc.CurtainControl((byte)curtaincontent.subnetID,(byte)curtaincontent.deviceID,curtaincontent.channel_1,
                            curtaincontent.channel_2,"open",MainActivity.mydupsocket);
                    setstate("open");
                    setimage(R.mipmap.curtain_icon1_on);
                    setDBcurtainstate("open");
                }else{
                    cc.CurtainControl((byte) curtaincontent.subnetID, (byte) curtaincontent.deviceID, curtaincontent.channel_1,
                            curtaincontent.channel_2, "close",MainActivity.mydupsocket);
                    setstate("close");
                    setimage(R.mipmap.curtain_icon1_off);
                    setDBcurtainstate("close");
                }
                break;
            case R.id.curtaintype1_delete:
                MainActivity.mgr.deletecurtain("curtain", curtaincontent.curtain_id, curtaincontent.room_id);
                broadcastUpdate(FounctionActivity.ACTION_DELETECURTAIN);
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
        if(!MainActivity.islockchangeid){
            showPopupMenu(tv_remark);
        }

        return true;
    }
    AlertView settingalter,pairalter;
    EditText sub,dev,cha1,cha2,name;
    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(rootcontext, popview);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_open:

                        settingalter = new AlertView("Settings", null, "CANCEL",  new String[]{"SAVE"}, null, rootcontext, AlertView.Style.Alert,
                                itemclick);
                        View  selfview= inflater.inflate(R.layout.setting_curtaininfo, null);
                        sub = (EditText) selfview.findViewById(R.id.subidedit);
                        dev = (EditText) selfview.findViewById(R.id.devidedit);
                        cha1 = (EditText) selfview.findViewById(R.id.chan1edit);
                        cha2 = (EditText) selfview.findViewById(R.id.chan2edit);
                        name = (EditText) selfview.findViewById(R.id.curtainremarkedit);
                        sub.setText(String.valueOf(curtaincontent.subnetID));
                        dev.setText(String.valueOf(curtaincontent.deviceID));
                        cha1.setText(String.valueOf(curtaincontent.channel_1));
                        cha2.setText(String.valueOf(curtaincontent.channel_2));
                        name.setText(curtaincontent.curtain_remark);
                        settingalter.addExtView(selfview);
                        settingalter.show();

                        break;
                    case R.id.action_pair:
                        pairalter = new AlertView("Select Device", null, "CANCEL",  null, null, rootcontext, AlertView.Style.Alert,
                                itemclick);
                        View selfviewx= inflater.inflate(R.layout.auto_pair_dialog, null);
                        ListView test=(ListView)selfviewx.findViewById(R.id.listView4);
                        DeviceListAdapter mLeDeviceListAdapter= new DeviceListAdapter(rootcontext,MainActivity.netdeviceList);
                        test.setAdapter(mLeDeviceListAdapter);
                        test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Savecurtain curtaininfo = new Savecurtain();
                                curtaininfo.room_id=curtaincontent.room_id;
                                curtaininfo.curtain_id=curtaincontent.curtain_id;
                                curtaininfo.channel_1 = curtaincontent.channel_1;
                                curtaininfo.channel_2 = curtaincontent.channel_2;
                                curtaininfo.curtain_remark=curtaincontent.curtain_remark;
                                curtaininfo.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                curtaininfo.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                MainActivity.mgr.updatecurtain(curtaininfo);
                                Toast.makeText(rootcontext, "apir "+MainActivity.netdeviceList.get(position).get("devicename")+" succeed", Toast.LENGTH_SHORT).show();
                                curtaincontent.subnetID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("subnetID"));
                                curtaincontent.deviceID=Integer.parseInt(MainActivity.netdeviceList.get(position).get("deviceID"));
                                aupairdialog.dismiss();
                            }
                        });
                        pairalter.addExtView(selfviewx);
                        pairalter.show();
                        break;
                }


                return false;

            }
        });

        popupMenu.show();

    }
    public com.bigkoo.alertview.OnItemClickListener itemclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==settingalter){
                if(position==0){
                    Savecurtain curtaininfo = new Savecurtain();
                    curtaininfo.room_id=curtaincontent.room_id;
                    curtaininfo.curtain_id=curtaincontent.curtain_id;
                    curtaininfo.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    curtaininfo.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    curtaininfo.channel_1 = Integer.parseInt(cha1.getText().toString().trim());
                    curtaininfo.channel_2 = Integer.parseInt(cha2.getText().toString().trim());
                    curtaininfo.curtain_remark=name.getText().toString().trim();
                    MainActivity.mgr.updatecurtain(curtaininfo);
                    curtaincontent.subnetID=Integer.parseInt(sub.getText().toString().trim());
                    curtaincontent.deviceID=Integer.parseInt(dev.getText().toString().trim());
                    curtaincontent.channel_1 = Integer.parseInt(cha1.getText().toString().trim());
                    curtaincontent.channel_2 = Integer.parseInt(cha2.getText().toString().trim());
                    curtaincontent.curtain_remark=name.getText().toString().trim();
                    setremark(curtaincontent.curtain_remark);
                }
            }else if(o==pairalter){

            }

        }
    };
    public void setdeletevisable(boolean vs){
        if(vs){
            deleteview.setVisibility(VISIBLE);
            deletemode=true;
            deleteview.setChecked(false);
        }else{
            deleteview.setVisibility(INVISIBLE);
            deletemode=false;
        }
    }
    public void setimage(int background) {
        im_image.setImageResource(background);
    }
    public void setremark(String remark){
        tv_remark.setText(remark);
    }
    public void setstate(String state){
        tv_state.setText(state);
    }
    public void setcontan(Savecurtain lg){
        curtaincontent=lg;
        tv_remark.setText(lg.curtain_remark);
        setstate(lg.current_state);
        //curtainlinear.setBackgroundColor(ToColor(colorarray[(curtaincontent.curtain_id % 7)]));
        curtainlinear.setBackground(getResources().getDrawable(R.drawable.control_back_10));
    }
    public int getType1curtainid(){
        return curtaincontent.curtain_id;
    }

    public boolean getIfneedtoDelete(){
        return deleteview.isChecked();
    }
    public int getsubid(){
        return curtaincontent.subnetID;
    }
    public int getdevid(){
        return curtaincontent.deviceID;
    }
    public int[] getchannel(){
        int[] result=new int[2];
        result[0]=curtaincontent.channel_1;
        result[1]=curtaincontent.channel_2;
        return result;
    }
    public void setDBcurtainstate(String str){
        Savecurtain curtaininfo = new Savecurtain();
        curtaininfo.room_id=curtaincontent.room_id;
        curtaininfo.curtain_id=curtaincontent.curtain_id;
        curtaininfo.current_state=str;
        MainActivity.mgr.updatecurtainstate(curtaininfo);
    }
    public void setReceiveChange(int state)//0:open;1:close
    {
        switch (state){
            case 0:
                setstate("open");
                setimage(R.mipmap.curtain_icon1_on);
                setDBcurtainstate("open");
                break;
            case 1:
                setstate("close");
                setimage(R.mipmap.curtain_icon1_off);
                setDBcurtainstate("close");
                break;
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
}
