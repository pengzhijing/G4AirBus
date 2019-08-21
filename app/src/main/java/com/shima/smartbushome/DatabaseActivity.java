package com.shima.smartbushome;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.shima.smartbushome.andserver.CoreService;
import com.shima.smartbushome.andserver.LoadingDialog;
import com.shima.smartbushome.andserver.ServerStatusReceiver;
import com.shima.smartbushome.database.DatabaseContext;
import com.shima.smartbushome.util.SystemUIUtil;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zhijing on 2017/9/27.
 */

public class DatabaseActivity extends AppCompatActivity {

    private Switch sw_server;
    private TextView tv_url;

    private Intent mService;
    /**
     * Accept and server status.
     */
    private ServerStatusReceiver mReceiver;


    private LoadingDialog loadingDialog;


    public  static  File databaseFile;

    Window window;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_database);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Upload and Download");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseActivity.this.finish();
            }
        });
        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {

            toolbar.setPadding(0,getStatusBarHeight(this),0,0);
        }
        //获取窗口对象
        window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        initView();


        // AndServer run in the service.
        mService = new Intent(this, CoreService.class);
        mReceiver = new ServerStatusReceiver(this);
        mReceiver.register();


        sw_server.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //开启服务
                    showDiolog();
                    startService(mService);
                } else {
                    //关闭服务
                    stopService(mService);
                    tv_url.setText("");
                }
            }
        });


        //复制数据库文件到SD根目录
       // Log.d("数据库路径", this.getDatabasePath("g4image.db").toString());
        databaseFile= new File(DatabaseContext.dbPath);
       // FileUtil.copyFile(this.getDatabasePath("g4image.db").toString(), Environment.getExternalStorageDirectory().getPath()+"/g4image.db");

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

    public void initView() {
        sw_server = (Switch) this.findViewById(R.id.sw_server);
        tv_url = (TextView) this.findViewById(R.id.tv_url);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiver.unRegister();
    }

    /**
     * Start notify.
     */
    public void serverStart() {


        String ip = getHostIP();
        if (!TextUtils.isEmpty(ip)) {
            //开启服务成功
            closeDiolog();
            tv_url.setText("http://" + ip + ":8899");
        }

    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("DatabaseActivity", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }


    /**
     * Started notify.
     */
    public void serverHasStarted() {

        closeDiolog();
        Toast.makeText(this, "server is runing", Toast.LENGTH_SHORT).show();
        String ip = getHostIP();
        if (!TextUtils.isEmpty(ip)) {
            tv_url.setText("http://" + ip + ":8899");
        }

    }


    /**
     * Stop notify.
     */
    public void serverStop() {
        try {
            tv_url.setText("");
            Toast.makeText(this, "server stop", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void showDiolog() {
        try {
            loadingDialog.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadingDialog = new LoadingDialog(this, "opening server");
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void closeDiolog() {
        try {
            loadingDialog.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
    }


}
