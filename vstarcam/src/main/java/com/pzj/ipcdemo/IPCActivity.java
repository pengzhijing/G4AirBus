package com.pzj.ipcdemo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pzj.ipcdemo.entity.VStarCamera;
import com.pzj.ipcdemo.service.BridgeService;
import com.pzj.ipcdemo.utils.JSONUtil;
import com.pzj.ipcdemo.utils.SystemUIUtil;

import java.util.ArrayList;
import java.util.List;

import per.goweii.anylayer.AnimHelper;
import per.goweii.anylayer.AnyLayer;
import vstc2.nativecaller.NativeCaller;

/**
 * 视频连接流程:
 * 1. 启动服务 BridgeService；
 * 2. 初始化服务器 NativeCaller. PPPPInitial
 * 3. 初始化回调 NativeCaller. Init ();
 * 4. 开启 p2p 连接 StartPPPP；
 * 5. p2p 返回在线之后 开启视频流(视频画面) StartPPPPLivestream
 * 6 关闭视频流 StopPPPPLivestream
 * 7 断开 p2p 连接 StopPPPP
 * 8 释放 p2p 资源 NativeCaller. Free ();
 */

public class IPCActivity extends AppCompatActivity implements BridgeService.IpcamClientInterface, View.OnClickListener {

    public static String TAG = "IPCActivity";

    private FloatingActionButton fb_addCamera;
    private AnyLayer editDialog;//手动添加设备对话框


    public static String deviceName = "admin";//设备账号
    public static String devicePass = "12345678";//设备密码
    public static String deviceId = "VSTA899484MXNCJ";//设备唯一标识

    public static final int PPPP_STATUS_CONNECTING = 0; // 连接中
    public static final int PPPP_STATUS_INITIALING = 1; // 已连接，正在初始化
    public static final int PPPP_STATUS_ON_LINE = 2; // 在线
    public static final int PPPP_STATUS_CONNECT_FAILED = 3; // 连接失败
    public static final int PPPP_STATUS_DISCONNECT = 4; // / 连接已关闭
    public static final int PPPP_STATUS_INVALID_ID = 5; // 无效 UID
    public static final int PPPP_STATUS_DEVICE_NOT_ON_LINE = 6; // 不在线
    public static final int PPPP_STATUS_CONNECT_TIMEOUT = 7; // 连接超时
    public static final int PPPP_STATUS_WRONGUSER_RIGHTPWD = 8; // 密码错误 ..
    public static final int PPPP_STATUS_WRONGPWD_RIGHTUSER = 9; // 密码错误. .
    public static final int PPPP_STATUS_WRONGPWD_WRONGUSER = 10; // 密码错误. .

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_ipc);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Camera");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(0, getStatusBarHeight(this), 0, 0);
        }
        //获取窗口对象
        Window window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //初始化控件
        initView();


//        //启动服务
//        Intent intent = new Intent();
//        intent.setClass(IPCActivity.this, BridgeService.class);
//        startService(intent);
//        Log.d(TAG,"启动服务....");
//
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转到配网页面
//                Intent intent=new Intent();
//                intent.setClass(IPCActivity.this, com.mediatek.demo.smartconnection.MainActivity.class);
//                IPCActivity.this.startActivity(intent);
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (button.getText().toString().equals("在线")){
//                    //跳转到实时画面
//                    Intent intent=new Intent("com.pzj.play");
//                    Bundle bd=new Bundle();
//                    bd.putString("name",deviceName);
//                    bd.putString("pass",devicePass);
//                    bd.putString("id",deviceId);
//                    intent.putExtras(bd);
//                    IPCActivity.this.startActivity(intent);
//
//                }else {
//                    //开启线程 准备连接工作
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//
//                                //初始化服务器
//                                NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
//                                Log.d(TAG,"初始化服务器...");
//                                Thread.sleep(3000);
//                                // NativeCaller.SetAPPDataPath(getApplicationContext().getFilesDir().getAbsolutePath());
//                                //初始化回调
//                                NativeCaller.Init();
//                                Log.d(TAG,"初始化回调...");
//                                Thread.sleep(200);
//                                //开启 p2p 连接 (账号，密码，设备ID)
//                                startCameraPPPP(deviceName,devicePass,deviceId);
//                                Log.d(TAG,"开启 p2p 连接 (账号:"+deviceName+"，密码:"+devicePass+"，设备ID:"+deviceId);
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }).start();
//                }
//            }
//        });

        //绑定回调
        BridgeService.setIpcamClientInterface(this);

        //绑定事件
        fb_addCamera.setOnClickListener(this);
    }


    public void initView() {
        fb_addCamera = (FloatingActionButton) this.findViewById(R.id.fb_addCamera);
    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    //页面菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 开启p2p连接
     *
     * @param deviceName//账号
     * @param devicePass//密码
     * @param deviceId//设备ID
     */
    private void startCameraPPPP(String deviceName, String devicePass, String deviceId) {

        if (deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0);
        } else if (deviceId.toLowerCase().startsWith("vstd")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1);
        } else if (deviceId.toLowerCase().startsWith("vstf")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1);
        } else if (deviceId.toLowerCase().startsWith("vste")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0);
        } else if (deviceId.toLowerCase().startsWith("pisr")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0);
        } else if (deviceId.toLowerCase().startsWith("vstg")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0);
        } else if (deviceId.toLowerCase().startsWith("vsth")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "EEGDFHBLKGJIGEJLEKGOFMEDHAMHHJNAGGFABMCOBGJOLHLJDFAFCPPHGILKIKLMANNHKEDKOINIBNCPJOMK:vstarcam2018", 0);
        } else if (deviceId.toLowerCase().startsWith("vstb") || deviceId.toLowerCase().startsWith("vstc")) {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0);
        } else {
            NativeCaller.StartPPPPExt(deviceId, deviceName,
                    devicePass, 1, "", "", 0);
        }
    }

    /**
     * 断开p2p连接
     *
     * @param deviceId//设备ID
     */
    private void stopCameraPPPP(String deviceId) {
        NativeCaller.StopPPPP(deviceId);
    }


    //设备状态信息及视频连接流程相关参数回调
    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d(TAG, "设备状态信息及视频连接流程相关参数回调");
        Log.d(TAG, "did:" + did);
        Log.d(TAG, "type:" + type);
        Log.d(TAG, "param:" + param);

//       final int states=param;
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                switch (states){
//                    case PPPP_STATUS_CONNECTING : // 连接中
//                        button.setText("连接中");
//                        break;
//                    case PPPP_STATUS_INITIALING : // 已连接，正在初始化
//                        button.setText("正在初始化");
//                        break;
//                    case PPPP_STATUS_ON_LINE : // 在线
//                        button.setText("在线");
//                        break;
//                    case PPPP_STATUS_CONNECT_FAILED : // 连接失败
//                        button.setText("连接失败");
//                        break;
//                    case PPPP_STATUS_DISCONNECT : // / 连接已关闭
//                        button.setText("连接已关闭");
//                        break;
//                    case PPPP_STATUS_INVALID_ID :// 无效 UID
//                        button.setText("无效 UID");
//                        break;
//                    case PPPP_STATUS_DEVICE_NOT_ON_LINE : // 不在线
//                        button.setText("不在线");
//                        break;
//                    case PPPP_STATUS_CONNECT_TIMEOUT : // 连接超时
//                        button.setText("连接超时");
//                        break;
//                    case PPPP_STATUS_WRONGUSER_RIGHTPWD : // 密码错误 ..
//                    case PPPP_STATUS_WRONGPWD_RIGHTUSER : // 密码错误. .
//                    case PPPP_STATUS_WRONGPWD_WRONGUSER : // 密码错误. .
//                        button.setText("密码错误");
//                        break;
//
//                }
//            }
//        });

    }

    /**
     * 返回预览图
     *
     * @param did
     * @param bImage
     * @param len
     */
    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        Log.i(TAG, "BSSnapshotNotify---len" + len);

//        //将字节数组转换为ImageView可调用的Bitmap对象
//       final Bitmap bitmap=ImageDispose.getPicFromBytes(bImage,new BitmapFactory.Options());
//
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                iv_bimage.setImageBitmap(bitmap);
//            }
//        });
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

    }

    @Override
    public void CameraStatus(String did, int status) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
//        try {
//            NativeCaller.StopPPPPLivestream(deviceId);
//            NativeCaller.StopPPPP(deviceId);
//            NativeCaller.Free();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.fb_addCamera) {
            showAddCameraTypeDialog();
        }
    }

    //显示添加摄像头方式对话框
    public void showAddCameraTypeDialog() {
        AnyLayer.with(this)
                .contentView(R.layout.dialog_add_camera_type)
                .backgroundBlurPercent(0.015F)//背景高斯模糊
                .gravity(Gravity.TOP | Gravity.CENTER)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick(R.id.iv_scan, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //扫描添加

                        if (anyLayer.isShow()) {
                            anyLayer.dismiss();
                        }
                    }
                })
                .onClick(R.id.iv_search, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //搜索添加

                        if (anyLayer.isShow()) {
                            anyLayer.dismiss();
                        }
                    }
                })
                .onClick(R.id.iv_edit, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {

                        if (anyLayer.isShow()) {
                            anyLayer.dismiss();
                        }
                        //手动添加0
                        showAddCameraEditDialog();

                    }
                })
                .contentAnim(new AnyLayer.IAnim() {
                    @Override
                    public long inAnim(View content) {
                        AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                        return 350;
                    }

                    @Override
                    public long outAnim(View content) {
                        AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                        return 0;
                    }
                })
                .show();
    }


    //显示手动添加摄像头对话框
    public void showAddCameraEditDialog() {

        editDialog = AnyLayer.with(this)
                .contentView(R.layout.dialog_add_camera_edit)
                .backgroundBlurPercent(0.015F)//背景高斯模糊
                .gravity(Gravity.TOP | Gravity.CENTER)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick(R.id.fl_dialog_no, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //取消
                        if (anyLayer.isShow()) {
                            anyLayer.dismiss();
                        }
                    }
                })

                .contentAnim(new AnyLayer.IAnim() {
                    @Override
                    public long inAnim(View content) {
                        AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                        return 350;
                    }

                    @Override
                    public long outAnim(View content) {
                        AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                        return 350;
                    }
                });

        final EditText et_name = editDialog.getView(R.id.et_name);
        final EditText et_id = editDialog.getView(R.id.et_id);
        final EditText et_username = editDialog.getView(R.id.et_username);
        final EditText et_password = editDialog.getView(R.id.et_password);
        FrameLayout fl_dialog_yes = editDialog.getView(R.id.fl_dialog_yes);

        fl_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认添加
                String name=et_name.getText().toString();
                String id=et_id.getText().toString();
                String username=et_username.getText().toString();
                String password=et_password.getText().toString();

                VStarCamera vStarCamera=new VStarCamera(name,id,username,password);
                List<VStarCamera> vStarCameraList=new ArrayList<>();
                vStarCameraList.add(vStarCamera);
                String str=JSONUtil.listToJson(vStarCameraList);
                Toast.makeText(IPCActivity.this, ""+str, Toast.LENGTH_LONG).show();
            }
        });

        editDialog.show();
    }
}
