package com.shima.smartbushome.mainsetting;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.XMLParser;
import com.shima.smartbushome.udp.udp_socket;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.data.GsonImpl;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.impl.huc.HttpUrlClient;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ru.alexbykov.nopermission.PermissionHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class Net_settingFragment extends Fragment implements View.OnClickListener{
    Button bt_login,bt_logout,bt_select,bt_save,ms_ns_savewifiname,ms_ns_deletewifiname,bt_modifypassword,bt_g4_server_save;
    EditText et_username,et_password,et_realm;
    CheckBox cb_usehttp;
    TextView ms_ns_wifinametext;
    View view;
    ProgressDialog postProgress;
    List<String> devicelistname= new ArrayList<>();
    List<HashMap<String,String>> devicelist=new ArrayList<>();
    protected static LiteHttp liteHttp;
    public final int OP_LOGIN = 1, OP_LOGOUT = 2, OP_IP_LIST = 3,
            OP_SERVER_LIST = 4;
    //远程服务器域名
    public  String g4Realm="smartbuscloud.com";
    public String IP = "http://"+g4Realm; // "179.186.3.85"; //http://smartbuscloud.com
    //http://www.g4cloud.ir
    public final byte TERMINAL_TYPE = 0x03;
    // RequetType
    public static final byte EGetURLListSH = 0x00, ELoginInSH = 0x01,
            ELoginOutSH = 0x02, EGetIPLListSH = 0x03, EResetPassword = 0x04,
            EModifyPassword = 0x05, EgetCaptcha = 0x06,
            EModifyAliasOfRSIP = 0x07;

    // LogiResult
    public static final byte ELoginSuccess_Same = 0x00,
            ELoginSuccess_PC = 0x01,// pc
            ELoginSuccess_IOS = 0x02,// IOS
            ELoginSuccess_Andriod = 0x03,// Andriod
            ELoginFail_NoSuchUsrName = 0x04, ELoginFail_PasswordIsWrong = 0x05,
            ELoginFail_CanNotUseUser = 0x06,
            ELoginFailUsrHadLoginOnOtherPhone = 0x07,
            ELoginFailSystemError = 0x08;

    private String wifiname="";
    public static Net_settingFragment newInstance() {
        Net_settingFragment pageFragment = new Net_settingFragment();
        return pageFragment;
    }
    public Net_settingFragment() {


    }

    //权限申请对象
    private PermissionHelper permissionHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_net_setting, container, false);
        initView();
        inithttp();
        permissionHelper = new PermissionHelper(this);
        getWifiSSidPermissions();
        return view;
    }

    public void initView(){
        bt_login=(Button)view.findViewById(R.id.ms_ns_login);
        bt_logout=(Button)view.findViewById(R.id.ms_ns_logout);
        bt_modifypassword=(Button)view.findViewById(R.id.bt_modifypassword);
        bt_select=(Button)view.findViewById(R.id.ms_ns_select);
        bt_save=(Button)view.findViewById(R.id.ms_ns_save);
        bt_g4_server_save=view.findViewById(R.id.bt_g4_server_save);
        ms_ns_savewifiname=(Button)view.findViewById(R.id.ms_ns_savewifiname);
        ms_ns_deletewifiname=(Button)view.findViewById(R.id.ms_ns_deletewifiname);
        ms_ns_wifinametext=(TextView)view.findViewById(R.id.ms_ns_wifinametext);
        bt_login.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        bt_modifypassword.setOnClickListener(this);
        bt_select.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        bt_g4_server_save.setOnClickListener(this);
        ms_ns_deletewifiname.setOnClickListener(this);
        ms_ns_savewifiname.setOnClickListener(this);
        et_username=(EditText)view.findViewById(R.id.ms_ns_etname);
        et_password=(EditText)view.findViewById(R.id.ms_ns_etpassword);
        et_realm=(EditText)view.findViewById(R.id.et_realm);
        cb_usehttp=(CheckBox)view.findViewById(R.id.ms_ns_cb);
        postProgress=new ProgressDialog(getActivity());
        postProgress.setMessage("logining");
        postProgress.setCancelable(true);
        postProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(udp_socket.rsipip){
            cb_usehttp.setChecked(true);
        }
        SharedPreferences sharedPre3 = getActivity().getSharedPreferences("httpconfig", getActivity().MODE_PRIVATE);
        et_username.setText(sharedPre3.getString("username", ""));
        et_password.setText(sharedPre3.getString("password", ""));
        bt_select.setText(sharedPre3.getString("MacAddress", ""));
        SharedPreferences sharedPre4 = getActivity().getSharedPreferences("wifiinfo", getActivity().MODE_PRIVATE);
        wifiname=sharedPre4.getString("wifiname", "not set wifiname");
        ms_ns_wifinametext.setText("Remember your current wifi: "+wifiname);



        //远程服务器域名
        SharedPreferences sharedPre5 = getActivity().getSharedPreferences("remoteinfo", getActivity().MODE_PRIVATE);
        g4Realm=sharedPre5.getString("g4Realm", "smartbuscloud.com");
        et_realm.setText(g4Realm);
        IP = "http://"+g4Realm; // "179.186.3.85"; //http://smartbuscloud.com

        et_realm.setText(""+g4Realm);

    }

    public void inithttp(){
        if (liteHttp == null) {
            liteHttp = LiteHttp.build(getActivity())
                    .setHttpClient(new HttpUrlClient())       // http client
                    .setJsonConvertor(new GsonImpl())        // json convertor
                    .setDebugged(true)                     // log output when debugged
                    .setDoStatistics(true)                // statistics of time and traffic
                    .setDetectNetwork(true)              // detect network before connect
                    .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                    .setSocketTimeout(10000)           // socket timeout: 10s
                    .setConnectTimeout(10000)         // connect timeout: 10s
                    .create();
        } else {
            liteHttp.getConfig()                   // configuration directly
                    .setSocketTimeout(5000)       // socket timeout: 5s
                    .setConnectTimeout(5000);    // connect timeout: 5s
        }
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ms_ns_login:
                if (et_username.getText().toString() != null
                        && !et_username.getText().toString().equals("")) {
                    if (et_password.getText().toString() != null
                            && !et_password.getText().toString().equals("")) {
                        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                        pList.add(new NameValuePair("userName", et_username.getText().toString()));
                        pList.add(new NameValuePair("password", et_password.getText().toString() ));
                        pList.add(new NameValuePair("terminalType",TERMINAL_TYPE + ""));
                        postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                        liteHttp.getConfig().setBaseUrl(loginUrl);
                        liteHttp.executeAsync(postRequest);
                    }
                }
                break;
            case R.id.ms_ns_logout:
                if (et_username.getText().toString() != null
                        && !et_username.getText().toString().equals("")) {
                    if (et_password.getText().toString() != null
                            && !et_password.getText().toString().equals("")) {
                        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                        pList.add(new NameValuePair("userName", et_username.getText().toString()));
                        pList.add(new NameValuePair("password", et_password.getText().toString() ));
                        pList.add(new NameValuePair("terminalType",TERMINAL_TYPE + ""));
                        logoutRequest.setHttpBody(new UrlEncodedFormBody(pList));
                        liteHttp.getConfig().setBaseUrl(logoutUrl);
                        liteHttp.executeAsync(logoutRequest);
                    }
                }

                //清除保存的密码
                try{
                    et_username.setText("");
                    et_password.setText("");

                    savevalueInfo(getActivity(), "httpconfig", "MacAddress", "");
                    savevalueInfo(getActivity(), "httpconfig", "username","");
                    savevalueInfo(getActivity(), "httpconfig", "password","");

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ms_ns_select:
                if(devicelistalter!=null){
                    devicelistalter.show();
                }
                break;
            case R.id.ms_ns_save:
                if(cb_usehttp.isChecked()){
                    saveLoginInfo(getActivity(),"ipconfig","ipset",false);
                    saveLoginInfo(getActivity(), "ddnsconfig", "ipset", false);
                    if(choosedevice.equals("")){
                        choosedevice=bt_select.getText().toString().trim();
                    }
                   savevalueInfo(getActivity(), "httpconfig", "MacAddress", choosedevice);
                    savevalueInfo(getActivity(), "httpconfig", "username",et_username.getText().toString() );
                    savevalueInfo(getActivity(), "httpconfig", "password", et_password.getText().toString());
                    udp_socket.MacAddress=stringtoMACbyte(choosedevice);
                    udp_socket.mansetip = false;
                    udp_socket.ddnssetip = false;
                   // MainActivity.mydupsocket.reflash();
                }
                udp_socket.rsipip = cb_usehttp.isChecked();
                saveLoginInfo(getActivity(), "httpconfig", "ipset", cb_usehttp.isChecked());
                Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                if(MainActivity.mydupsocket!=null){
                    MainActivity.mydupsocket.StopAllThread();
                    MainActivity.mydupsocket.initprocess();
                }
                break;
            case R.id.bt_g4_server_save:
                if (et_realm.getText().toString().trim().length()>0){
                    savevalueInfo(getActivity(), "remoteinfo", "g4Realm", et_realm.getText().toString().trim());
                    Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                    g4Realm=et_realm.getText().toString().trim();
                    IP = "http://"+g4Realm; // "179.186.3.85"; //http://smartbuscloud.com
                }else{
                    et_realm.setText("");
                    Toast.makeText(getActivity(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.ms_ns_savewifiname:
//                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().WIFI_SERVICE);
//                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                wifiname=getWIFISSID(this.getActivity());
                if(wifiname!=null&&!wifiname.equals("")){
                    deletealter = new AlertView("Save WifiName", "Your Current Wifi is "+wifiname+" right?", "CANCEL",
                            new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener(){
                        public void onItemClick(Object o,int position) {
                            if(position==-1){

                            }else if(position==0){
                                ms_ns_wifinametext.setText("Remember your current wifi: "+wifiname);
                                savevalueInfo(getActivity(), "wifiinfo", "wifiname", wifiname);
                            }
                        }
                    });
                    deletealter .setCancelable(true);
                    deletealter .show();
                }else{
                    Toast.makeText(getActivity(), "you are not connecting to a wifi", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ms_ns_deletewifiname:
                deletewifiname = new AlertView("Delete WifiName", "You want to delete your wifiname "+wifiname+" right?", "CANCEL",
                        new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener(){
                    public void onItemClick(Object o,int position) {
                        if(position==-1){

                        }else if(position==0){
                            ms_ns_wifinametext.setText("Remember your current wifi: "+"");
                            savevalueInfo(getActivity(), "wifiinfo", "wifiname", "");
                        }
                    }
                });
                deletewifiname .setCancelable(true);
                deletewifiname .show();
                break;
            case R.id.bt_modifypassword:
                showModifyPasswordView();
                break;
        }
    }
    AlertView deletealter,deletewifiname;
    byte[] data ={(byte)0xa2, (byte)0x90, 0x42, (byte)0x83, 0x53, 0x4d, 0x41, 0x52, 0x54, 0x43,
            0x4c, 0x4f, 0x55, 0x44, (byte)0xaa, (byte)0xaa, 0x18,
            (byte)0xbb, (byte)0xbb, (byte)0xdd, (byte)0xdd, 0x00, 0x31, 0x01, (byte)0xd5,
            0x01, 0x50, 0x00, 0x00, 0x02, 0x53, 0x03, 0x00, 0x00, 0x00, 0x00, 0x7a, 0x57,0x00,0x00};
    byte[] tarip={(byte)0xa2,(byte)0x90,0x42,(byte)0x83};


    String target=IP+"";
    String getdevicelist=IP+":8888/DDNSServerService.asmx/GetDeviceList";
    String loginUrl=IP+":8888/DDNSServerService.asmx/UserLogin";
    String logoutUrl=IP+":8888/DDNSServerService.asmx/UserLogout";
    String modifyPasswordUrl=IP+":8888/DDNSServerService.asmx/ModifyPassword";

    final StringRequest postRequest = new StringRequest(loginUrl)
            .setMethod(HttpMethods.Post)
            .setHttpListener(new HttpListener<String>(true, false, true) {
                @Override
                public void onStart(AbstractRequest<String> request) {
                    super.onStart(request);
                    postProgress.show();
                }
                @Override
                public void onSuccess(String data, Response<String> response) {
                   // HttpUtil.showTips(getActivity(), "result", response.getResult() + "");
                    //response.printInfo();
                    XMLParser parser = new XMLParser();
                    Document doc = parser.getDomElement(data);
                    NodeList nodeList = doc
                            .getElementsByTagName("unsignedByte").item(0)
                            .getChildNodes();
                    Node node = (Node) nodeList.item(0);
                    String val = node.getNodeValue();
                    if (val != null) {
                        if (!val.equals("")) {
                            byte value = Byte.parseByte(val);
                            String Message = "";
                            if (value == ELoginSuccess_Same) {
                               // Message = "Login Success Same";
                                Message = "Login Success";
                            } else if (value == ELoginSuccess_PC) {
                               // Message = "Login Success PC";
                                Message = "Login Success";
                            } else if (value == ELoginSuccess_IOS) {
                               // Message = "Login Success IOS ";
                                Message = "Login Success";
                            } else if (value == ELoginSuccess_Andriod) {
                                //Message = "Login Success Android";
                                Message = "Login Success";
                            } else if (value == ELoginFail_NoSuchUsrName) {
                                postProgress.dismiss();
                                Message = "Login Fail User Name Error ";
                            } else if (value == ELoginFail_PasswordIsWrong) {
                                postProgress.dismiss();
                                Message = "Login Fail Password Error ";
                            } else if (value == ELoginFail_CanNotUseUser) {
                                postProgress.dismiss();
                                Message = "Login Fail Can not use This User  ";
                            } else if (value == ELoginFailUsrHadLoginOnOtherPhone) {
                                postProgress.dismiss();
                                Message = "Login Fail User Had Login On Other Phone  ";
                            } else if (value == ELoginFailSystemError) {
                                postProgress.dismiss();
                                Message = "Login Fail System Error";
                            } else {
                                postProgress.dismiss();
                                Message = " Error";
                            }
                            Toast.makeText(getActivity(),
                                    Message, Toast.LENGTH_LONG).show();
                            if (value == ELoginSuccess_Same
                                    || value == ELoginSuccess_PC
                                    || value == ELoginSuccess_IOS
                                    || value == ELoginSuccess_Andriod) {
                                LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                                pList.add(new NameValuePair("userName", et_username.getText().toString()));
                                pList.add(new NameValuePair("password", et_password.getText().toString() ));
                                pList.add(new NameValuePair("terminalType",TERMINAL_TYPE + ""));
                                devicelistRequest.setHttpBody(new UrlEncodedFormBody(pList));
                                liteHttp.getConfig().setBaseUrl(getdevicelist);
                                liteHttp.executeAsync(devicelistRequest);
                            }
                        }
                    }
                }
            });
    final StringRequest logoutRequest = new StringRequest(logoutUrl)
            .setMethod(HttpMethods.Post)
            .setHttpListener(new HttpListener<String>(true, false, true) {
                @Override
                public void onStart(AbstractRequest<String> request) {
                    super.onStart(request);
                }
                @Override
                public void onSuccess(String data, Response<String> response) {
                    // HttpUtil.showTips(getActivity(), "result", response.getResult() + "");
                    //response.printInfo();
                    XMLParser parser = new XMLParser();
                    Document doc = parser.getDomElement(data);
                    NodeList nodeList = doc
                            .getElementsByTagName("unsignedByte").item(0)
                            .getChildNodes();
                    Node node = (Node) nodeList.item(0);
                    String val = node.getNodeValue();
                    if (val != null) {
                        if (!val.equals("")) {
                            byte value = Byte.parseByte(val);
                            String Message = "";
                            if (value == ELoginSuccess_Same) {
                               // Message = "LogOut Success Same";
                                Message = "LogOut Success";
                            } else if (value == ELoginSuccess_PC) {
                              //  Message = "LogOut Success PC";
                                Message = "LogOut Success";
                            } else if (value == ELoginSuccess_IOS) {
                              //  Message = "LogOut Success IOS ";
                                Message = "LogOut Success";
                            } else if (value == ELoginSuccess_Andriod) {
                              //  Message = "LogOut Success Android";
                                Message = "LogOut Success";
                            } else if (value == ELoginFail_NoSuchUsrName) {
                                Message = "LogOut Fail User Name Error ";
                            } else if (value == ELoginFail_PasswordIsWrong) {
                                Message = "LogOut Fail Password Error ";
                            } else if (value == ELoginFail_CanNotUseUser) {
                                Message = "LogOut Fail Can not use This User  ";
                            } else if (value == ELoginFailUsrHadLoginOnOtherPhone) {
                                Message = "LogOut Fail User Had Login On Other Phone  ";
                            } else if (value == ELoginFailSystemError) {
                                Message = "LogOut Fail System Error";
                            } else {
                                Message = " Error";
                            }
                            Toast.makeText(getActivity(),
                                    Message, Toast.LENGTH_LONG).show();
                        }
                    }}
            });

    AlertView devicelistalter;
    String choosedevice="";
    final StringRequest devicelistRequest = new StringRequest(getdevicelist)
            .setMethod(HttpMethods.Post)
            .setHttpListener(new HttpListener<String>(true, false, true) {
                @Override
                public void onSuccess(String data, Response<String> response) {
                    postProgress.dismiss();
                   // HttpUtil.showTips(getActivity(), "result", response.getResult() + "");
                   // response.printInfo();
                    XMLParser parser = new XMLParser();
                    Document doc = parser.getDomElement(data);
                    NodeList nodeList1 = doc
                            .getElementsByTagName("DeviceList");
                    devicelist.clear();
                    devicelistname.clear();
                    for (int i = 0; i < nodeList1.getLength(); i++) {
                        Element e = (Element) nodeList1.item(i);
                        HashMap<String,String> item=new HashMap<String, String>();
                        item.put("MACAddress", parser.getValue(e, "MACAddress"));
                        item.put("Alias", parser.getValue(e, "Alias"));
                        devicelistname.add(parser.getValue(e, "Alias"));
                        devicelist.add(item);
                    }
                    devicelistalter = new AlertView("RSIP List", null, "CANCEL",  null, null, getActivity(), AlertView.Style.Alert,
                            null);
                    ViewGroup extView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.listdialog, null);
                    ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
                    extView.setLayoutParams(lp);
                    deletelist.setAdapter(new ArrayAdapter(getActivity(),
                            R.layout.simplelistitem, devicelistname));
                    deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            bt_select.setText(devicelistname.get(position));
                            choosedevice=devicelist.get(position).get("MACAddress");
                            devicelistalter.dismiss();
                        }
                    });
                    devicelistalter.addExtView(extView);
                    devicelistalter.show();
                }
            });

    final StringRequest sentdataRequest = new StringRequest(target)
            .setMethod(HttpMethods.Post)
            .setHttpListener(new HttpListener<String>(true, false, true) {
                @Override
                public void onSuccess(String data, Response<String> response) {
                     HttpUtil.showTips(getActivity(), "result", response.getResult() + "");
                     response.printInfo();

                }
            });
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

    public byte[] stringtoMACbyte(String ip){
        String[] x=ip.split("\\.");
        byte[] result=new byte[8];
        result[0]=(byte)Integer.parseInt(x[0],16);
        result[1]=(byte)Integer.parseInt(x[1],16);
        result[2]=(byte)Integer.parseInt(x[2],16);
        result[3]=(byte)Integer.parseInt(x[3],16);
        result[4]=(byte)Integer.parseInt(x[4],16);
        result[5]=(byte)Integer.parseInt(x[5],16);
        result[6]=(byte)Integer.parseInt(x[6],16);
        result[7]=(byte)Integer.parseInt(x[7],16);
        return result;
    }


    EditText et_Username;
    EditText et_Oldpassword;
    EditText et_Newpassword;
    AlertView settingalter;

    //修改密码弹框
    private void showModifyPasswordView() {
        settingalter = new AlertView("Modify Password", null, "CANCEL",  new String[]{"SUBMIT"}, null, getContext(), AlertView.Style.Alert,
                saveClick);
        settingalter.setShoulddismiss(false);
        ViewGroup settingView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.setting_modifpassword, null);
        et_Username= (EditText) settingView.findViewById(R.id.et_username);
        et_Oldpassword= (EditText) settingView.findViewById(R.id.et_oldpassword);
        et_Newpassword= (EditText) settingView.findViewById(R.id.et_newpassword);

        settingalter.addExtView(settingView);
        settingalter.show();
    }

    //确认修改密码
    public com.bigkoo.alertview.OnItemClickListener saveClick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(position==0){

                String username=et_Username.getText().toString();
                String oldpassword=et_Oldpassword.getText().toString().trim();
                String newpassword=et_Newpassword.getText().toString().trim();


                LinkedList<NameValuePair> modifyPasswordList = new LinkedList<NameValuePair>();
                modifyPasswordList.add(new NameValuePair("userName",username));
                modifyPasswordList.add(new NameValuePair("oldPassword", oldpassword));
                modifyPasswordList.add(new NameValuePair("newPassword",newpassword ));
                modifyPasswordList.add(new NameValuePair("terminalType",TERMINAL_TYPE + ""));
                modifyPasswordRequest.setHttpBody(new UrlEncodedFormBody(modifyPasswordList));
                liteHttp.getConfig().setBaseUrl(modifyPasswordUrl);
                liteHttp.executeAsync(modifyPasswordRequest);
            }else{
                settingalter.dismiss();
            }
        }
    };

    final StringRequest modifyPasswordRequest = new StringRequest(modifyPasswordUrl)
            .setMethod(HttpMethods.Post)
            .setHttpListener(new HttpListener<String>(true, false, true) {
                @Override
                public void onSuccess(String data, Response<String> response) {
                    XMLParser parser = new XMLParser();
                    Document doc = parser.getDomElement(data);
                    //通过doc找到根标签root
                    Element root= doc.getDocumentElement();
                    String isSuccess=root.getTextContent();
                    if ("true".equals(isSuccess)){
                        Toast.makeText(getContext(), "Modify Password Success", Toast.LENGTH_SHORT).show();
                        settingalter.dismiss();
                    }else{
                        Toast.makeText(getContext(), "Modify Password Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    /**
     * 获取SSID
     * @param activity 上下文
     * @return  WIFI 的SSID
     */
    public String getWIFISSID(Activity activity) {
        String ssid="unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT==Build.VERSION_CODES.P) {

            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1){

            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo()!=null){
                    return networkInfo.getExtraInfo().replace("\"","");
                }
            }
        }
        return ssid;
    }
    //申请Wifi权限
    private void getWifiSSidPermissions() {
        permissionHelper
                .check(Manifest.permission.ACCESS_FINE_LOCATION)
                .onSuccess(new Runnable() {
            @Override
            public void run() {
                //申请成功
                Log.d("getWifiSSidPermissions","Android9.0申请Wifi权限成功");
            }
        })
                .onDenied(new Runnable() {
                    @Override
                    public void run() {
                        //权限被拒绝，9.0系统无法获取SSID
                    }
                })
                .onNeverAskAgain(new Runnable() {
                    @Override
                    public void run() {
                        //权限被拒绝，9.0系统无法获取SSID,下次不会在询问了
                    }
                }).run();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
