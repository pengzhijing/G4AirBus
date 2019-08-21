package com.shima.smartbushome.assist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 自定义圆形的方向布局
 *
 * @author 樊列龙
 * @since 2014-06-07
 */
public class CircleView extends View {

    private int circleWidth = 5; // 圆环宽
    private int circleColor = 0xff57b9c2;//圆环颜色
    private int innerCircleColor = 0xff4d7d8e;//里面圆圈的颜色
    private int innerpressCircleColor = 0x404d7d8e;//里面圆圈的颜色
    private int backgroundColor = 0x000000;
    private Paint paint = new Paint();
    int center=0,centerx = 0,centery=0;
    int innerRadius = 0;
    private float innerCircleRadius = 0;
    private float smallCircle = 10;
    public Dir dir = Dir.UP;
    Context mcontext;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext=context;
    }

    public CircleView(Context context) {
        super(context);
        mcontext=context;
        // paint = new Paint();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext=context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);

        int cen=Math.min(measuredWidth, measuredHeight);
        center = cen/ 2;
        centerx=measuredWidth/2;
        centery=measuredHeight/2;
        innerCircleRadius = (float) (center / 2);
        innerRadius = center-2;//(center - circleWidth / 2 - 10);// 圆环

        this.setOnTouchListener(onTouchListener);
    }

    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    public Canvas maincanvas;
    /**
     * 开始绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        maincanvas=canvas;
        initBackGround(canvas);
        drawDirTriangle(canvas, dir);

    }

    /**
     * 绘制方向小箭头
     *
     * @param canvas
     */
    private void drawDirTriangle(Canvas canvas, Dir dir) {
        paint.setColor(innerCircleColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(backgroundColor);

        //canvas.drawCircle(center, center, smallCircle, paint);
        // canvas.drawText(text, center, center+40, paint);

    }


    /**
     * 点击的时候绘制黑色的扇形
     *
     * @param canvas
     * @param dir
     */
    private void drawOnclikColor(Canvas canvas, Dir dir) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(100);
        switch (dir) {
            case UP:
                canvas.drawArc(new RectF(centerx - innerRadius, centery - innerRadius, centerx + innerRadius, centery
                        + innerRadius), 225, 90, false, paint);
                break;
            case DOWN:
                canvas.drawArc(new RectF(centerx - innerRadius, centery - innerRadius, centerx + innerRadius, centery
                        + innerRadius), 45, 90, false, paint);
                break;
            case LEFT:
                canvas.drawArc(new RectF(centerx - innerRadius, centery - innerRadius, centerx + innerRadius, centery
                        + innerRadius), 135, 90, false, paint);
                break;
            case RIGHT:
                canvas.drawArc(new RectF(centerx - innerRadius, centery - innerRadius, centerx + innerRadius, centery
                        + innerRadius), -45, 90, false, paint);
                break;

            default:
                break;
        }

        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * 绘制像向上的箭头
     *
     * @param canvas
     */
    private void drawUpTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);

        path.lineTo((float) (center - sqrt2), (float) (center - sqrt2));
        path.lineTo(center, (float) (center - pow05));
        path.lineTo((float) (center + sqrt2), (float) (center - sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center, center - innerCircleRadius, paint);

        drawOnclikColor(canvas, Dir.UP);
    }

    /**
     * 绘制基本的背景， 这包括了三个步骤：1.清空画布 2.绘制外圈的圆 3.绘制内圈的圆
     *
     * @param canvas
     */
    private void initBackGround(Canvas canvas) {
        clearCanvas(canvas);
        drawBackCircle(canvas);
        drawInnerCircle(canvas);

    }

    /**
     * 绘制中心白色小圆
     *
     * @param canvas
     */
    private void drawInnerCircle(Canvas canvas) {
        switch (press){
            case 1:
                paint.setColor(innerCircleColor);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(2);
                canvas.drawCircle(centerx, centery, innerCircleRadius, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("OK", centerx, centery + 20, paint);
                break;
            default:
                paint.setColor(innerCircleColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawCircle(centerx, centery, innerCircleRadius, paint);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("OK", centerx, centery + 20, paint);
                break;
        }

    }

    /**
     * 绘制背景的圆圈和隔线
     *
     * @param canvas
     */
    private void drawBackCircle(Canvas canvas) {
        paint.setColor(circleColor);
        paint.setStrokeWidth(circleWidth);
        //paint.setAntiAlias(true);
        switch (press){
            case 0:
            case 1:
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerx, centery, innerRadius, paint); // 绘制圆圈

                break;
            case 2://上
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerx, centery, innerRadius, paint); // 绘制圆圈
                float r2=innerCircleRadius+((innerRadius-innerCircleRadius)/2);
                paint.setStrokeWidth(2*(r2-innerCircleRadius));
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLUE);

                float startAngle01 = 225;//开始角度
                float sweepAngle01 = 90;//从开始角度开始多少度

                RectF rect = new RectF(centerx -r2 , centery -r2, centerx
                        +r2, centery + r2);
                canvas.drawArc(rect, startAngle01, sweepAngle01, false, paint);

                break;
            case 3://右
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerx, centery, innerRadius, paint); // 绘制圆圈
                float r3=innerCircleRadius+((innerRadius-innerCircleRadius)/2);
                paint.setStrokeWidth(2*(r3-innerCircleRadius));
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLUE);

                float startAngle03 = 315;//开始角度
                float sweepAngle03 = 90;//从开始角度开始多少度

                RectF rect3 = new RectF(centerx -r3 , centery -r3, centerx
                        +r3, centery + r3);
                canvas.drawArc(rect3, startAngle03, sweepAngle03, false, paint);
                break;
            case 4://左
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerx, centery, innerRadius, paint); // 绘制圆圈
                float r4=innerCircleRadius+((innerRadius-innerCircleRadius)/2);
                paint.setStrokeWidth(2*(r4-innerCircleRadius));
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLUE);

                float startAngle04 = 135;//开始角度
                float sweepAngle04 = 90;//从开始角度开始多少度

                RectF rect4 = new RectF(centerx -r4 , centery -r4, centerx
                        +r4, centery + r4);
                canvas.drawArc(rect4, startAngle04, sweepAngle04, false, paint);
                break;
            case 5://下
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerx, centery, innerRadius, paint); // 绘制圆圈
                float r5=innerCircleRadius+((innerRadius-innerCircleRadius)/2);
                paint.setStrokeWidth(2*(r5-innerCircleRadius));
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLUE);

                float startAngle05 = 45;//开始角度
                float sweepAngle05 = 90;//从开始角度开始多少度

                RectF rect5 = new RectF(centerx -r5 , centery -r5, centerx
                        +r5, centery + r5);
                canvas.drawArc(rect5, startAngle05, sweepAngle05, false, paint);
                break;

        }
       // paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("^", centerx, centery - ((innerRadius-innerCircleRadius)/5) - innerCircleRadius, paint);
        canvas.drawText("v", centerx, centery + ((innerRadius-innerCircleRadius)/2) + innerCircleRadius, paint);
        canvas.drawText("<", centerx - ((innerRadius-innerCircleRadius)/2)- innerCircleRadius, centery+25 , paint);
        canvas.drawText(">", centerx+ ((innerRadius-innerCircleRadius)/2)  + innerCircleRadius, centery+25, paint);

        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        /*  math */
        float upleftx=(float)(innerCircleRadius/(Math.sqrt(2)));
        float uplefty=(float)(innerRadius/(Math.sqrt(2)));
        canvas.drawLine(centerx-upleftx, centery-upleftx, centerx-uplefty, centery-uplefty, paint);
        canvas.drawLine(centerx+upleftx, centery-upleftx,centerx+uplefty, centery-uplefty, paint);
        canvas.drawLine(centerx-upleftx, centery+upleftx,centerx-uplefty, centery+uplefty, paint);
        canvas.drawLine(centerx+upleftx, centery+upleftx,centerx+uplefty, centery+uplefty, paint);

    }

    /**
     * 清空画布
     *
     * @param canvas
     */
    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(backgroundColor);
    }
    private OnPressListener listener;
    public void setOnPressListener(OnPressListener l) {
        listener = l;
    }
    public interface OnPressListener {

        void onPress(String direction,String presstype);

    }
    int press=0,returnresult=0;
    boolean longpress=false;
    OnTouchListener onTouchListener = new OnTouchListener() {
    long downTime;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Dir tmp = Dir.UNDEFINE;
            MotionEvent touchevent=event;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downTime=event.getDownTime();
                    touchdown(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    long time2=event.getEventTime();
                    if(!longpress){
                        if(time2-downTime>700){
                            longpress=true;
                            touchuplongpress(event.getX(), event.getY());
                            if(returnresult==1){
                                press=0;
                                invalidate();
                            }else{

                            }
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    long time=event.getEventTime();
                    press=0;
                    if(returnresult==1){

                    }else{
                        touchup(event.getX(), event.getY());
                    }
                    returnresult=0;
                    longpress=false;
                    break;
                default:
                    break;
            }

            return true;
        }
    };
    public void touchuplongpress(float x,float y){
        if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
           // System.out.println("----松开中央");
            if(listener!=null)
            if(press==1){
                listener.onPress("center","longpress");
                returnresult=1;
            }else{
               // listener.onPress("center","press");
            }
        } else if (getangle(x,y)>45&&getangle(x,y)<135) {
            dir = Dir.UP;
           // System.out.println("----松开向上");
            if(listener!=null)
            if(press==2){
                listener.onPress("up","longpress");
                returnresult=1;
            }else{
                //listener.onPress("up","press");
            }
        } else if ((getangle(x,y)>=0&&getangle(x,y)<45)||((getangle(x,y)>315&&getangle(x,y)<=360))) {
            dir = Dir.RIGHT;
            //System.out.println("----松开向右");
            if(listener!=null)
            if(press==3){
                listener.onPress("right","longpress");
                returnresult=1;
            }else{
               // listener.onPress("right","press");
            }
        } else if (getangle(x,y)>135&&getangle(x,y)<225) {
            dir = Dir.LEFT;
           // System.out.println("----松开向左");
            if(listener!=null)
            if(press==4){
                listener.onPress("left","longpress");
                returnresult=1;
            }else{
               // listener.onPress("left","press");
            }

        } else if (getangle(x,y)>225&&getangle(x,y)<315) {
            dir = Dir.DOWN;
            //System.out.println("----松开向下");
            if(listener!=null)
            if(press==5){
                listener.onPress("down","longpress");
                returnresult=1;
            }else{
              //  listener.onPress("down","press");
            }
        }
    }
    public void touchup(float x,float y){
       /* if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
            //System.out.println("----松开中央");
            if(listener!=null)
            listener.onPress("center","press");
        } else if (y < x && y + x < 2 * center) {
            dir = Dir.UP;
           // System.out.println("----松开向上");
            if(listener!=null)
            listener.onPress("up","press");
        } else if (y < x && y + x > 2 * center) {
            dir = Dir.RIGHT;
           // System.out.println("----松开向右");
            if(listener!=null)
            listener.onPress("right","press");
        } else if (y > x && y + x < 2 * center) {
            dir = Dir.LEFT;
           // System.out.println("----松开向左");
            if(listener!=null)
            listener.onPress("left","press");
        } else if (y > x && y + x > 2 * center) {
            dir = Dir.DOWN;
           // System.out.println("----松开向下");
            if(listener!=null)
            listener.onPress("down","press");
        }*/
        if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
            //System.out.println("----松开中央");
            if(listener!=null)
                listener.onPress("center","press");
        } else if (getangle(x,y)>45&&getangle(x,y)<135) {
            dir = Dir.UP;
            // System.out.println("----松开向上");
            if(listener!=null)
                listener.onPress("up","press");
        } else if ((getangle(x,y)>=0&&getangle(x,y)<45)||((getangle(x,y)>315&&getangle(x,y)<=360))) {
            dir = Dir.RIGHT;
            // System.out.println("----松开向右");
            if(listener!=null)
                listener.onPress("right","press");
        } else if ((getangle(x,y)>135&&getangle(x,y)<225)) {
            dir = Dir.LEFT;
            // System.out.println("----松开向左");
            if(listener!=null)
                listener.onPress("left","press");
        } else if ((getangle(x,y)>225&&getangle(x,y)<315)) {
            dir = Dir.DOWN;
            // System.out.println("----松开向下");
            if(listener!=null)
                listener.onPress("down","press");
        }
        invalidate();

    }

    public void touchdown(float x,float y){
        /*if (Math.sqrt(Math.pow(y - centery, 2) + Math.pow(x - centerx, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
            //System.out.println("----按下中央");
            press=1;
        } else if (y < x && y + x < 2 * center) {
            dir = Dir.UP;
           // System.out.println("----按下向上");
            press=2;
        } else if (y < x && y + x > 2 * center) {
            dir = Dir.RIGHT;
           // System.out.println("----按下向右");
            press=3;
        } else if (y > x && y + x < 2 * center) {
            dir = Dir.LEFT;
           // System.out.println("----按下向左");
            press=4;
        } else if (y > x && y + x > 2 * center) {
            dir = Dir.DOWN;
           // System.out.println("----按下向下");
            press=5;
        }*/
        if (Math.sqrt(Math.pow(y - centery, 2) + Math.pow(x - centerx, 2)) < innerCircleRadius) {// 判断在中心圆圈内
            dir = Dir.CENTER;
            //System.out.println("----按下中央");
            press=1;
        } else if (getangle(x,y)>45&&getangle(x,y)<135) {
            dir = Dir.UP;
            // System.out.println("----按下向上");
            press=2;
        } else if ((getangle(x,y)>=0&&getangle(x,y)<45)||((getangle(x,y)>315&&getangle(x,y)<=360))) {
            dir = Dir.RIGHT;
            // System.out.println("----按下向右");
            press=3;
        } else if ((getangle(x,y)>135&&getangle(x,y)<225)) {
            dir = Dir.LEFT;
            // System.out.println("----按下向左");
            press=4;
        } else if ((getangle(x,y)>225&&getangle(x,y)<315)) {
            dir = Dir.DOWN;
            // System.out.println("----按下向下");
            press=5;
        }
        invalidate();
    }
    /**
     * 关于方向的枚举
     *
     * @author Administrator
     *
     */
    public enum Dir {
        UP, DOWN, LEFT, RIGHT, CENTER, UNDEFINE
    }
    public double getangle(float x,float y){
        int a = (int)x-centerx;
        int b = (int)y-centery;
        int c =(int) Math.sqrt(a*a+b*b);
        // 计算弧度表示的角
        double B = Math.acos((a*a + c*c - b*b)/(2.0*a*c));
        B = Math.toDegrees(B);
        if(y>centery){
            B=360-B;
        }
        return B;
    }
}