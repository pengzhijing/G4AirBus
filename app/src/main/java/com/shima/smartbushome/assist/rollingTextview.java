package com.shima.smartbushome.assist;

/**
 * Created by Administrator on 2017/4/1.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
public class rollingTextview extends TextView{

    public rollingTextview(Context context) {
        super(context);
    }

    public rollingTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public rollingTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
