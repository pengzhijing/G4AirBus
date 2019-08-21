package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shima.smartbushome.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8.
 */
public class CentralControlAdapter extends BaseAdapter{
    private Context context;
    private List<String> stringarray=new ArrayList<String>();

    private List<String> iconbgarray=new ArrayList<String>();
    private int size;
    private LayoutInflater inflater=null;
    private HashMap<Integer, View> mView ;
    private String[] colorarray={"ff9962cc", "ffe18d06","ff599ade","ff2ead58","ffa99950","ff396879",
            "ffBA5252","ff00dba1","ffcbe471","ffaeaeae","FF37ACBC","FFCEBBE0"};
    private String[] namearray={"MACRO", "LIGHT","HVAC","MUSIC","CURTAIN","OTHER","FAN","SECURITY",
            "ENERGY","STATUS","SCHEDULE","NFC"};
    private String[] iconarray={"marco", "light","hvac","music","curtain","other","fan","security",
            "energy","status","schedule","nfc"};
    public CentralControlAdapter(final Context context){
        this.context = context;
        size=12;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = new HashMap<Integer, View>();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return stringarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = mView.get(position);
        if(view==null)
        {
            view = inflater.inflate(R.layout.adapter_centralcontrol_grid_item, null);
            TextView txt = (TextView)view.findViewById(R.id.centraltext);
            ImageView ceb = (ImageView)view.findViewById(R.id.centralimg);
            txt.setText(namearray[(position)]);
            ceb.setImageDrawable(context.getResources().getDrawable(getResourdIdByResourdName(context, iconarray[(position)])));
            if(Build.VERSION.SDK_INT >= 16){
                setdrawable(view,position);
              //  view.setBackgroundColor(context.getResources().getColor(R.color.title_transparent_black));
                view.setBackgroundColor(context.getResources().getColor(R.color.title_transparent_black));
            }else{
                //view.setBackgroundColor(ToColor(colorarray[(position)]));
                view.setBackgroundColor(context.getResources().getColor(R.color.title_transparent_black));
            }

            mView.put(position, view);
        }
        return view;
    }

    public static int getResourdIdByResourdName(Context context, String ResName){
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
                // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
                // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
    }
    public void setdrawable(View v,int position){
        Drawable backgroundDrawable = context.getResources().getDrawable(R.drawable.roomitem);
        StateListDrawable sld = (StateListDrawable) backgroundDrawable;// 通过向下转型，转回原型，selector对应的Java类为：StateListDrawable

        Drawable.ConstantState cs = sld.getConstantState();

        try {
            Method method = cs.getClass().getMethod("getChildren", new Class[0]);// 通过反射调用getChildren方法获取xml文件中写的drawable数组
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
                    gd.setColor(context.getResources().getColor(R.color.title_transparent_black));
                } else {
                    // 对默认状态做深色处理
                    gd.setColor(context.getResources().getColor(R.color.title_transparent_black));
                }
            }
            // 最后总结一下，为了实现这个效果，刚开始并没有看到setColor的方法，而是通过反射获取GradientDrawable对象的属性GradientState，
            // 再通过反射调用GradientState对象的setSolidColor方法去实现，效果不太理想。
            // 最后在仔仔细细一一看GradientDrawable对象的属性，发现属性Paint
            // mFillPaint，从名字就可以看出这个对象是用来绘制drawable的背景的，
            // 于是顺着往下找，发现setColor方法，于是bingo，这个过程也是挺曲折的。
            v.setBackground(backgroundDrawable);
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
    public int ToColor(String data){
        int color=0;
        int rin,gin,bin,ain;
        ain=Integer.parseInt(data.substring(0,2),16);
        rin=Integer.parseInt(data.substring(2,4),16);
        gin=Integer.parseInt(data.substring(4,6),16);
        bin=Integer.parseInt(data.substring(6,8),16);
        color= Color.argb(ain, rin, gin, bin);
        return color;
    }
}
