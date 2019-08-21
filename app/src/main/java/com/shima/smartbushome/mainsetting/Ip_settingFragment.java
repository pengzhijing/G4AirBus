package com.shima.smartbushome.mainsetting;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.udp.udp_socket;

import java.net.InetAddress;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Ip_settingFragment extends Fragment implements View.OnClickListener{
    EditText local,target,ddnstarget;
    CheckBox cha,ddnscha;
    Button save,ams_getddnsip;
    TextView ams_ddnsipvalue;
    View view;
    public Ip_settingFragment() {
        // Required empty public constructor
    }

    public static Ip_settingFragment newInstance() {
        Ip_settingFragment pageFragment = new Ip_settingFragment();
        return pageFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_ip_setting, container, false);
         local=(EditText)view.findViewById(R.id.ams_localip_ed);
        target=(EditText)view.findViewById(R.id.ams_targetip_ed);
        ams_ddnsipvalue=(TextView)view.findViewById(R.id.ams_ddnsipvalue);
        cha=(CheckBox)view.findViewById(R.id.ams_cb);
        ddnscha=(CheckBox)view.findViewById(R.id.ams_useddns);
        ddnstarget=(EditText)view.findViewById(R.id.ams_ddnssite);
        cha.setOnCheckedChangeListener(usetarip);
        ddnscha.setOnCheckedChangeListener(useddns);
        try{
            if(udp_socket.mansetip){
                local.setText(bytetostring(udp_socket.manlocalip));
                target.setText(bytetostring(udp_socket.mantargetip));
                cha.setChecked(true);
            }else{
                local.setText(bytetostring(MainActivity.mydupsocket.GetLocalIP()));
                target.setText(bytetostring(MainActivity.mydupsocket.GetTargetIP(MainActivity.mydupsocket.GetLocalIP())));
                cha.setChecked(false);
            }
            if(udp_socket.ddnssetip){
                SharedPreferences sharedPre2 = getActivity().getSharedPreferences("ddnsconfig", getActivity().MODE_PRIVATE);
                ddnstarget.setText(sharedPre2.getString("ddnswebsite", ""));;
                ams_ddnsipvalue.setText(bytetostring(udp_socket.ddnstargetip));
                ddnscha.setChecked(true);
            }else{
                ddnscha.setChecked(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        save=(Button)view.findViewById(R.id.ams_save);
        ams_getddnsip=(Button)view.findViewById(R.id.ams_getddnsip);
        save.setOnClickListener(this);
        ams_getddnsip.setOnClickListener(this);
        return view;
    }
    int c1=0,c2=0;
    public CompoundButton.OnCheckedChangeListener usetarip=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(c1==1){
                c1=0;
            }else{
                if(ddnscha.isChecked()){
                    c2=1;
                    ddnscha.setChecked(false);
                }
            }

        }
    };
    public CompoundButton.OnCheckedChangeListener useddns=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(c2==1){
                c2=0;
            }else{
                if(cha.isChecked()){
                    c1=1;
                    cha.setChecked(false);
                }
            }

        }
    };
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ams_save:
                if (cha.isChecked()) {
                    udp_socket.manlocalip=stringtobyte(local.getText().toString().trim());
                    udp_socket.mantargetip=stringtobyte(target.getText().toString().trim());
                    savevalueInfo(getActivity(), "ipconfig","manlocalip", local.getText().toString().trim());
                    savevalueInfo(getActivity(), "ipconfig","mantargetip", target.getText().toString().trim());
                }

                if(ddnscha.isChecked()){
                    udp_socket.ddnstargetip=stringtobyte(ams_ddnsipvalue.getText().toString().trim());
                    savevalueInfo(getActivity(), "ddnsconfig","ddnswebsite", ddnstarget.getText().toString().trim());
                    savevalueInfo(getActivity(), "ddnsconfig","ddnstargetip", ams_ddnsipvalue.getText().toString().trim());
                }

                if(cha.isChecked()||ddnscha.isChecked()){
                    saveLoginInfo(getActivity(),"httpconfig","ipset",false);
                    udp_socket.rsipip = false;
                }
                saveLoginInfo(getActivity(),"ipconfig","ipset",cha.isChecked());
                saveLoginInfo(getActivity(),"ddnsconfig","ipset",ddnscha.isChecked());
                Toast.makeText(getActivity(), "save succeed", Toast.LENGTH_SHORT).show();
                udp_socket.mansetip = cha.isChecked();
                udp_socket.ddnssetip = ddnscha.isChecked();
                break;
            case R.id.ams_getddnsip:
                String site=ddnstarget.getText().toString().trim();
                if(site.length()<1){
                    Toast.makeText(getActivity(), "please enter website name", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new getDDNSIP()).start();
                }

                break;
        }
    }


    public void saveLoginInfo(Context context, String filename,String savename,boolean value){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences(filename, context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putBoolean(savename, value);
        //提交
        editor.commit();
    }
    public void savevalueInfo(Context context, String filename,String savename,String value){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences(filename, context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString(savename, value);
        //提交
        editor.commit();
    }
    public String bytetostring(byte[] ip){
        String x="";
        x=String.valueOf((int)(ip[0])&0xff) + "." + String.valueOf((int)(ip[1])&0xff)  + "."
                + String.valueOf((int)(ip[2])&0xff)  + "." + String.valueOf((int)(ip[3])&0xff) ;
        return x;
    }
    public byte[] stringtobyte(String ip){
        String[] x=ip.split("\\.");
        byte[] result=new byte[4];
        result[0]=(byte)Integer.parseInt(x[0]);
        result[1]=(byte)Integer.parseInt(x[1]);
        result[2]=(byte)Integer.parseInt(x[2]);
        result[3]=(byte)Integer.parseInt(x[3]);
        return result;
    }
    public class getDDNSIP implements Runnable {
        @Override
        public void run() {
            try {

                InetAddress address = InetAddress.getByName(new URL("http://"+ddnstarget
                        .getText().toString()).getHost());
                ams_ddnsipvalue.setText(address.getHostAddress());// 197.164.95.40

            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
                // Toast.makeText(DNSNetworkActivity.this, "formed URL  Error ",
                // Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                // Toast.makeText(DNSNetworkActivity.this, "Error getting ip ",
                // Toast.LENGTH_LONG).show();
            }

        }
    }

}
