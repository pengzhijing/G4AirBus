package com.shima.smartbushome.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

public class SystemUIUtil {

    //设置显示隐藏状态栏导航栏
    public static void setSystemUIVisible(Activity activity, boolean show) {
        try{
            if (show) {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                uiFlags |= 0x00001000;
                activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
            } else {
                int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiFlags |= 0x00001000;
                activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
