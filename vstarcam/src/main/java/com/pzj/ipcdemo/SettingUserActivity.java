package com.pzj.ipcdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.ContentCommon;

import vstc2.nativecaller.NativeCaller;

public class SettingUserActivity extends Activity implements OnCheckedChangeListener, OnClickListener,BridgeService.UserInterface {
private boolean successFlag=false;
private int CAMERAPARAM=0xffffffff;//״̬
private final int TIMEOUT=3000;
private final int FAILED=0;
private final int SUCCESS=1;
private final int PARAMS=3;
private String strDID;//camera id
private String cameraName;
private String operatorName="";
private String operatorPwd="";
private String visitorName="";
private String visitorPwd="";
private String adminName="";
private String adminPwd="";
private EditText editName;
private EditText editPwd;
private CheckBox cbxShowPwd;
private Button btnOk;
private Button btnCancel;
private ProgressDialog progressDialog;

    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case FAILED://set failed
                showToast(R.string.user_set_failed);
                break;
            case SUCCESS://set success
                showToast(R.string.user_set_success);
                NativeCaller.PPPPRebootDevice(strDID);

                Log.d("info","user:"+ adminName+" pwd:"+adminPwd);
                final Intent intent=new Intent(ContentCommon.STR_CAMERA_INFO_RECEIVER);
                intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
                intent.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
                intent.putExtra(ContentCommon.STR_CAMERA_USER, adminName);
                intent.putExtra(ContentCommon.STR_CAMERA_PWD, adminPwd);
                intent.putExtra(ContentCommon.STR_CAMERA_OLD_ID, strDID);
                intent.putExtra(ContentCommon.CAMERA_OPTION, ContentCommon.CHANGE_CAMERA_USER);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        sendBroadcast(intent);
                    }
                }, 3000);
                finish();

                break;
            case PARAMS://get user params
                successFlag=true;
                if(progressDialog.isShowing()){
                    progressDialog.cancel();
                }
                editName.setText(adminName);
                editPwd.setText(adminPwd);
                break;

            default:
                break;
            }
        }
    };

    private Runnable runnable=new Runnable() {

        @Override
        public void run() {
            if(!successFlag){
                successFlag=false;
                progressDialog.dismiss();
//					showToast(R.string.user_getparams_failed);
            }
        }
    };
    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getDataFromOther();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.settinguser);
    progressDialog = new ProgressDialog(this);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage(getString(R.string.user_getparams));
    progressDialog.show();
    mHandler.postDelayed(runnable,TIMEOUT);
    findView();
    setLisetener();
    BridgeService.setUserInterface(this);
    NativeCaller.PPPPGetSystemParams(strDID,ContentCommon.MSG_TYPE_GET_PARAMS);
    tvCameraName.setText(cameraName+"  "+getResources().getString(R.string.setting_user));
}

private void setLisetener() {
    cbxShowPwd.setOnCheckedChangeListener(this);
    btnOk.setOnClickListener(this);
    btnCancel.setOnClickListener(this);
    MyTextWatch  myNameTextWatch=new MyTextWatch(R.id.edit_name);
    editName.addTextChangedListener(myNameTextWatch);
    MyTextWatch  myPwdTextWatch=new MyTextWatch(R.id.edit_pwd);
    editPwd.addTextChangedListener(myPwdTextWatch);
    }

private void getDataFromOther() {
        Intent intent=getIntent();
        strDID=intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        cameraName=intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
        adminName ="admin";
    }
@Override
protected void onPause() {
    // TODO Auto-generated method stub
    overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
    super.onPause();
}
private void findView() {
    editName = (EditText)findViewById(R.id.edit_name);
    editPwd = (EditText)findViewById(R.id.edit_pwd);
    cbxShowPwd = (CheckBox)findViewById(R.id.cbox_show_pwd);
    btnOk = (Button)findViewById(R.id.user_ok);
    btnCancel = (Button)findViewById(R.id.user_cancel);

    tvCameraName = (TextView)findViewById(R.id.tv_camera_setting);
    RelativeLayout layout=(RelativeLayout)findViewById(R.id.top);
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.top_bg);
    BitmapDrawable drawable = new BitmapDrawable(bitmap);
    drawable.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );
    drawable.setDither(true);
    layout.setBackgroundDrawable(drawable);
}

@Override
public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.user_ok) {
        setUser();


    } else if (i == R.id.user_cancel) {
        finish();

    } else {
    }
}
private void setUser(){
    if(successFlag){
        /*if(TextUtils.isEmpty(adminName)){
            showToast(R.string.user_name_no_empty);
            return;
        }*/

//			if(TextUtils.isEmpty(pwd)){
//				showToast(R.string.pwd_no_empty);
//				return;
//			}

        Log.d("info","adminName:"+adminName+" adminPwd:"+adminPwd);
        NativeCaller.PPPPUserSetting(strDID, visitorName, visitorPwd, operatorName, operatorPwd, adminName, adminPwd);
    }else{
        showToast(R.string.user_set_failed);
    }
}

 private Runnable settingRunnable=new Runnable() {

        @Override
        public void run() {
            if(!successFlag){
                showToast(R.string.user_set_failed);
            }
        }
    };
private TextView tvCameraName;
    /**
     * BridgeService Feedback execute
     * **/
public void CallBack_UserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3){
    Log.d("info"," did:"+did+" user1:"+user1+" pwd1:"+pwd1+" user2:"+user2+" pwd2:"+pwd2+" user3:"+user3+" pwd3:"+pwd3);
    adminName=user3;
    adminPwd=pwd3;
    mHandler.sendEmptyMessage(PARAMS);
}
/**
 * BridgeService Feedback execute
 * **/
public void CallBack_SetSystemParamsResult(String did, int paramType, int result){
    Log.d("info","result:"+result+" paramType:"+paramType);
    mHandler.sendEmptyMessage(result);
}
/**
 * BridgeService Feedback execute
 * **/
public void setPPPPMsgNotifyData(String did, int type, int param){
    if(strDID.equals(did)){
        if(ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS==type){
            CAMERAPARAM=param;
        }
    }
}

private class MyTextWatch implements TextWatcher {
private int id;
public MyTextWatch(int id){
        this.id=id;
}
    @Override
    public void afterTextChanged(Editable s) {
        String result = s.toString();
        if (id == R.id.edit_name) {
            adminName = result;

        } else if (id == R.id.edit_pwd) {
            Log.i("info", "result:" + result);
            adminPwd = result;

        } else {
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {

    }

}
@Override
    protected void onDestroy() {
        super.onDestroy();
    }

@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if(isChecked){
           editPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
       }else{
           editPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
       }
}
public void showToast(String content){
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
}
public void showToast(int rid){
    Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG).show();
}

@Override
public void callBackUserParams(String did, String user1, String pwd1,
                               String user2, String pwd2, String user3, String pwd3) {
    // TODO Auto-generated method stub
    Log.e("用户信息","管理员名称"+adminName+"管理员密码"+adminPwd);
    adminName = user3;
    adminPwd = pwd3;
    operatorName = user2;
    operatorPwd = pwd2;
    mHandler.sendEmptyMessage(PARAMS);
}

@Override
public void callBackSetSystemParamsResult(String did, int paramType,
                                          int result) {
    // TODO Auto-generated method stub
    mHandler.sendEmptyMessage(result);
}

@Override
public void callBackPPPPMsgNotifyData(String did, int type, int param) {
    // TODO Auto-generated method stub

}

}
