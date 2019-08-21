package com.shima.smartbushome.assist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class SelectorUtils {
    /**
     * 动态修改selector中图片的背景颜色
     *
     * @param drawable
     *            selectorDrawable
     * @param rgbColors
     *            默认以及按下状态的颜色值
     */
    public static void changeViewColor(StateListDrawable drawable, int[] rgbColors) {
        ConstantState cs = drawable.getConstantState();
        if (rgbColors.length < 2) {
            return;
        }
        try {
            Method method = cs.getClass().getMethod("getChildren", new  Class[ 0 ]);// 通过反射调用getChildren方法获取xml文件中写的drawable数组
            method.setAccessible(true);
            Object obj = method.invoke(cs, new  Object[ 0 ]);
            Drawable[] drawables = (Drawable[]) obj;

            for (int i = 0; i < drawables.length; i++) {
                // 接下来我们要通过遍历的方式对每个drawable对象进行修改颜色值
                GradientDrawable gd = (GradientDrawable) drawables[i];
                if (gd == null) {
                    break;
                }
                if (i == 0) {
                    // 我们对按下的状态做浅色处理
                    gd.setColor(rgbColors[0]);
                } else {
                    // 对默认状态做深色处理
                    gd.setColor(rgbColors[1]);
                }
            }
            // 最后总结一下，为了实现这个效果，刚开始并没有看到setColor的方法，而是通过反射获取GradientDrawable对象的属性GradientState，
            // 再通过反射调用GradientState对象的setSolidColor方法去实现，效果不太理想。
            // 最后在仔仔细细一一看GradientDrawable对象的属性，发现属性Paint
            // mFillPaint，从名字就可以看出这个对象是用来绘制drawable的背景的，
            // 于是顺着往下找，发现setColor方法，于是bingo，这个过程也是挺曲折的。

        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
