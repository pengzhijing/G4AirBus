package com.shima.smartbushome.selflayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.shima.smartbushome.FounctionActivity;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savenfc;

public class NFCLayout extends LinearLayout implements View.OnLongClickListener {
    View view;
    Context rootcontext;
    LayoutInflater inflater;
    Savenfc nfccontent;
    TextView tv_detail,tv_name;
    CheckBox cb_delete;
    Switch nfc_switch;
    RelativeLayout nfc_selflayout;
    boolean deletemode=false,init=false;
    private String[] colorarray={"FF2176BC", "FF8E4E87","FFB62F32","FF7BC176","FFEB6A68","FFF08519","FFFAC65A"};
    public NFCLayout(Context context) {
        super(context);
        // 将自定义组合控件的布局渲染成View
        initview(context);
    }

    public NFCLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public void initview(Context context) {
        view = View.inflate(context, R.layout.nfc_layout, this);
        rootcontext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initUI();
    }

    public void initUI(){
        tv_detail=(TextView)view.findViewById(R.id.nfc_detail);
        tv_name=(TextView)view.findViewById(R.id.nfc_name);
        cb_delete=(CheckBox)view.findViewById(R.id.nfc_delete);
        nfc_switch=(Switch)view.findViewById(R.id.nfc_switch);
        nfc_selflayout=(RelativeLayout)view.findViewById(R.id.nfc_selflayout);
        tv_name.setOnLongClickListener(this);
        nfc_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (init) {
                    if (isChecked) {
                        nfccontent.state = 0;
                        MainActivity.mgr.updatenfc_status(nfccontent);
                    } else {
                        nfccontent.state = 1;
                        MainActivity.mgr.updatenfc_status(nfccontent);
                    }
                }
            }
        });
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //intent.putExtra(FounctionActivity.ACTION_DELETELIGHT, 2);
        FounctionActivity.fcontext.sendBroadcast(intent);
    }

    public boolean onLongClick(View v) {
        if(v.getId()==R.id.nfc_name){


        }
        return true;
    }



    public void setcontan(Savenfc sn){
        nfccontent=sn;
        setname(sn.nfc_name);
        switch (sn.action_type){
            case 1:
                setcontent("Marco:"+sn.marco_name);
                break;
            case 2:
                setcontent("Call:"+sn.call_num);
                break;
            case 3:
                setcontent("Message:To "+sn.call_num+"\n"+sn.message);
                break;
        }
        switch (sn.state){
            case 0:
                nfc_switch.setChecked(true);
                break;
            case 1:
                nfc_switch.setChecked(false);
                break;
        }
       // nfc_selflayout.setBackgroundColor(ToColor(colorarray[(nfccontent.nfc_id%7)]));
        nfc_selflayout.setBackground(getResources().getDrawable(R.drawable.control_back_10));
        init=true;
    }



    public void setname(String str){
        tv_name.setText(str);
    }
    public void setcontent(String str){
        tv_detail.setText(str);
    }
    public void setdeletevisable(boolean result){
        if(result){
            cb_delete.setVisibility(VISIBLE);
            deletemode=true;
            cb_delete.setChecked(false);
        }else{
            cb_delete.setVisibility(INVISIBLE);
            deletemode=false;
        }
    }
    public boolean getIfneedtoDelete(){
        return cb_delete.isChecked();
    }
    public int getnfcid(){
        return nfccontent.nfc_id;
    }
    public int ToColor(String data) {
        int color = 0;
        int rin, gin, bin, ain;
        ain = Integer.parseInt(data.substring(0, 2), 16);
        rin = Integer.parseInt(data.substring(2, 4), 16);
        gin = Integer.parseInt(data.substring(4, 6), 16);
        bin = Integer.parseInt(data.substring(6, 8), 16);
        color = Color.argb(ain, rin, gin, bin);
        return color;
    }
    /***************************************************************************************/

}
