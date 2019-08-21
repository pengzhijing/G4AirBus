package com.shima.smartbushome;

import android.app.Application;

import com.shima.smartbushome.assist.CrashHandle.CrashHandler;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/1/17.
 */
public class SmartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
      //  CrashHandler.getInstance().init(this);
        //BugLy bug监控
        CrashReport.initCrashReport(getApplicationContext(), "ac724b7810", false);
    }

}