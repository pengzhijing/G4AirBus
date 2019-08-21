package com.shima.smartbushome.assist.AutoUpdate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/6.
 */
public class updatemain {
    private Context thiscontext;
    public updatemain(Context context){
        thiscontext=context;
    }
    /**
     * 检测软件更新
     */
    Handler handler;
    public void checkUpdate(final boolean mainpage)
    {
        // 此方法在主线程中调用，可以更新UI
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // 处理消息时需要知道是成功的消息还是失败的消息
                switch (msg.what) {
                    case 1:
                        // 显示提示对话框
                        showNoticeDialog();
                        break;
                    case 0:
                        if(!mainpage){
                            Toast.makeText(thiscontext, "already the newest", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        if(!mainpage) {
                            Toast.makeText(thiscontext, "timeout", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }

            }
        };
        // 把version.xml放到网络上，然后获取文件信息
        new Thread(new getXMLThread()).start();

    }
    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;


    public class getXMLThread extends Thread {

        @Override
        public void run() {
            try {
                ParseXmlService service = new ParseXmlService();
                // 获取当前软件版本
                int versionCode = getVersionCode(thiscontext);
                HttpURLConnection conn=(HttpURLConnection)new
                        URL("http://www.smartg4.cn/EasyControl/ecupdate.xml").openConnection();
                conn.setConnectTimeout(5000);//设置连接超时
                conn.setRequestMethod("POST");
                if (conn.getResponseCode()==200) {
                    InputStream inStream=conn.getInputStream();
                    mHashMap = service.parseXml(inStream);
                    if (null != mHashMap)
                    {
                        int serviceCode = Integer.valueOf(mHashMap.get("version"));
                        // 版本判断
                        if (serviceCode > versionCode)
                        {
                            Message msg = new Message();
                            // 消息对象可以携带数据
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            // 消息对象可以携带数据
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                Message msg = new Message();
                // 消息对象可以携带数据
                msg.what = 3;
                handler.sendMessage(msg);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }//和服务器建立连接
        }

    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context)
    {
        int versionCode = 0;
        try
        {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.shima.smartbushome", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog()
    {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
        builder.setTitle("Software Update");
        builder.setMessage("New EasyControl is available, Download now ?");
        // 更新
        builder.setPositiveButton("Download Now!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 显示下载对话框
                String URL=mHashMap.get("url");
               String dbDir = "/Android/data/com.dave.smartbushome/update";
                File dirFile = new File(dbDir);
                if(!dirFile.exists()){
                    dirFile.mkdirs();
                }
                UpdateService.Builder.create(URL)
                .setStoreDir(dbDir)
                .build(thiscontext);

            }
        });
        // 稍后更新
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }


}
