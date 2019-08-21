/*
 * Copyright (C) 2014 Francesco Azzola
 *  Surviving with Android (http://www.survivingwithandroid.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.shima.smartbushome.assist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class ShakeEventManager implements SensorEventListener {

  private SensorManager sManager;
  private Sensor s;


  private static final int MOV_COUNTS = 2;
  private static final int MOV_COUNTSDATA = MOV_COUNTS+1;
  private static final int MOV_THRESHOLD = 4;
  private static final float ALPHA = 0.8F;
  private static final int SHAKE_WINDOW_TIME_INTERVAL = 350; // milliseconds

  // Gravity force on x,y,z axis
  private float gravity[] = new float[3];

  private int counter;
  private long firstMovTime;
  private ShakeListener listener;

  public ShakeEventManager() {
  }

  public void setListener(ShakeListener listener) {
    this.listener = listener;
  }

  public void init(Context ctx) {
    sManager = (SensorManager)  ctx.getSystemService(Context.SENSOR_SERVICE);
    s = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    register();
  }

  public void register() {
    sManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
  }

  int delayremark=0,getingdata=0;
  float[] olddirection=new float[3];
  Handler delay=new Handler();
  Runnable delayrun=new Runnable() {
    @Override
    public void run() {
      delayremark=0;
      delay.removeCallbacks(delayrun);
    }
  };
  float[] maxnum=new float[MOV_COUNTSDATA];
  float[] maxnumdir=new float[MOV_COUNTSDATA];
  @Override//&&(direction!=(int)maxAcc[1])(maxAcc[0] !=0)&&
  public void onSensorChanged(SensorEvent sensorEvent) {
    float[] maxAcc = calcMaxAcceleration(sensorEvent);
    long now = System.currentTimeMillis();

   /* if((int)maxAcc[0]!=0||(int)maxAcc[1]!=0||(int)maxAcc[2]!=0){
      Log.d("SwA", "AccX :" + (int)maxAcc[0] + "");
      Log.d("SwA", "AccY :" + (int)maxAcc[1] + "");
      Log.d("SwA", "AccZ :" + (int)maxAcc[2] + "");

      if (counter == 0) {
        counter++;
        firstMovTime = System.currentTimeMillis();
        for(int i=0;i<3;i++){
          olddirection[i]=maxAcc[i];
        }
      }else{
        counter++;
        if (counter >= MOV_COUNTS)
          if (listener != null)
            shakedirection(olddirection,maxAcc);
      }
    }*/
    if((int)maxAcc[1]!=0) {
      if(getingdata==0){
        delayremark=1;
        getingdata=1;
      }else{

      }
    }
    if(delayremark==1){
      if(counter<MOV_COUNTSDATA){
        maxnum[counter]=maxAcc[0];
        maxnumdir[counter]=maxAcc[1];
      }
      if (counter == 0) {
        counter++;
        firstMovTime = System.currentTimeMillis();

      }else{
        counter++;
        if (counter >= MOV_COUNTS)
          if (listener != null){
            if ((now - firstMovTime) > SHAKE_WINDOW_TIME_INTERVAL){
              listener.onShake(getmaxdir());
              resetAllData();
              return;
            }
          }

      }
    }


  }

  public int getmaxdir(){
    int dir=0,max=0,dir2=0,max2=0;
    int left=0,right=0,updown=0;
    for(int i=0;i<MOV_COUNTSDATA;i++){
      if(max>(int)maxnum[i]){

      }else{
        max=(int)maxnum[i];
        dir=(int)maxnumdir[i];
      }
    }

    for(int i=0;i<MOV_COUNTSDATA;i++){
      if((int)maxnumdir[i]==1){
        left++;
      }
      if((int)maxnumdir[i]==2){
        right++;
      }
      if((int)maxnumdir[i]==5||(int)maxnumdir[i]==6){
        updown++;
      }
    }
    max2=Math.max(left,right);
    max2=Math.max(max2,updown);

    if(max2==left){
      dir2=2;
    }
    if(max2==right){
      dir2=1;
    }
    if(max2==updown){
      dir2=5;
    }

    if((dir==dir2)||(dir==5&&dir2==6)||(dir==6&&dir2==5)){
      return dir;
    }else{
      if(max>10){
        return dir;
      }else if(max2>5){
        return dir2;
      }else{
        return dir;
      }
    }

  }
  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {}

  public void deregister()  {
    sManager.unregisterListener(this);
  }


  private float[] calcMaxAcceleration(SensorEvent event) {

    float[] result=new float[2];
    gravity[0] = calcGravityForce(event.values[0], 0);
    gravity[1] = calcGravityForce(event.values[1], 1);
    gravity[2] = calcGravityForce(event.values[2], 2);

    float accX = event.values[0] - gravity[0];
    float accY = event.values[1] - gravity[1];
    float accZ = event.values[2] - gravity[2];


    float xr,yr,zr;

    float max=Math.max(Math.abs(accX),Math.abs(accY));
    max=Math.max(max,Math.abs(accZ));
  /*  if((int)accX>MOV_THRESHOLD){
      xr=1;//+
    }else if((int)accX<-MOV_THRESHOLD){
      xr=2;//-
    }else{
      xr=0;//nothing
    }

    if((int)accY>MOV_THRESHOLD){
      yr=1;//+
    }else if((int)accY<-MOV_THRESHOLD){
      yr=2;//-
    }else{
      yr=0;//nothing
    }

    if((int)accZ>MOV_THRESHOLD){
      zr=1;//+
    }else if((int)accZ<-MOV_THRESHOLD){
      zr=2;//-
    }else{
      zr=0;//nothing
    }
    result[0]=xr;
    result[1]=yr;
    result[2]=zr;*/

    if((max==Math.abs(accX))&&(max>MOV_THRESHOLD))
    {
      if(accX>0){
        result[0]=max;result[1]=1;
      }else{
        result[0]=max;result[1]=2;
    }

    }
    else if((max==Math.abs(accY))&&(max>MOV_THRESHOLD))
    {
      if(accY>0){
        result[0]=max;result[1]=3;
      }else{
        result[0]=max;result[1]=4;
      }
    }
    else if((max==Math.abs(accZ))&&(max>MOV_THRESHOLD))
    {
      if(accZ>0){
        result[0]=max;result[1]=6;
      }else{
        result[0]=max;result[1]=5;
      }
    }
    return result;
  }

  // Low pass filter
  private float calcGravityForce(float currentVal, int index) {
    return  ALPHA * gravity[index] + (1 - ALPHA) * currentVal;
  }


  private void resetAllData() {
    Log.d("SwA", "Reset all data");
    counter = 0;
    delayremark=0;
    getingdata=0;
    firstMovTime = System.currentTimeMillis();
    for(int i=0;i<MOV_COUNTSDATA;i++){
      maxnum[i]=0;
      maxnumdir[i]=0;
    }
  }

  public static interface ShakeListener {
    public void onShake(int type);
  }

  public static final int UP=1,DOWM=2,LEFT=3,RIGHT=4;
  public static final int SHAKE_UP=5,SHAKE_DOWM=6,SHAKE_LEFT=1,SHAKE_RIGHT=2;
  public void shakedirection(float[] oldmove,float[] newmove){
    int oldD=100*(int)oldmove[0]+10*(int)oldmove[1]+1*(int)oldmove[2];
    int newD=100*(int)newmove[0]+10*(int)newmove[1]+1*(int)newmove[2];
    int resulte1=0;//上下左右，1,2,3,4
    switch (oldD){
      case	1	:	//	下
        resulte1=DOWM;
       break;
      case	2	:	//	上
        resulte1=UP;
       break;
      case	10	:	//	后
       break;
      case	11	:	//	后下
        resulte1=DOWM;
       break;
      case	12	:	//	后上
        resulte1=UP;
       	break;
      case	20	:	//	前

       break;
      case	21	:	//	前下
        resulte1=DOWM;
       	break;
      case	22	:	//	前上
        resulte1=UP;
       break;
      case	100	:	//	左
        resulte1=LEFT;
       break;
      case	101	:	//	左下
        resulte1=LEFT;
       break;
      case	102	:	//	左上
        resulte1=LEFT;
       	break;
      case	110	:	//	左后
        resulte1=LEFT;
       	break;
      case	111	:	//	左后下
        resulte1=LEFT;
       break;
      case	112	:	//	左后上
        resulte1=LEFT;
       break;
      case	120	:	//	左前
        resulte1=LEFT;
       	break;
      case	121	:	//	左前下
        resulte1=LEFT;
       break;
      case	122	:	//	左前上
        resulte1=LEFT;
       break;
      case	200	:	//	右
        resulte1=RIGHT;
       break;
      case	201	:	//	右下
        resulte1=RIGHT;
      	break;
      case	202	:	//	右上
        resulte1=RIGHT;
       	break;
      case	210	:	//	右后
        resulte1=RIGHT;
      	break;
      case	211	:	//	右后下
        resulte1=RIGHT;
       break;
      case	212	:	//	右后上
        resulte1=RIGHT;
       break;
      case	220	:	//	右前
        resulte1=RIGHT;
       	break;
      case	221	:	//	右前下
        resulte1=RIGHT;
       break;
      case	222	:	//	右前上
        resulte1=RIGHT;
       break;
      default:break;
    }

    switch (newD){
      case	1	:	//	下
        switch (resulte1){
          case UP:listener.onShake(SHAKE_UP);break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	2	:	//	上
        switch (resulte1){
          case UP:break;
          case DOWM:listener.onShake(SHAKE_DOWM);break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	10	:	//	后
        break;
      case	11	:	//	后下
        switch (resulte1){
          case UP:listener.onShake(SHAKE_UP);break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	12	:	//	后上
        switch (resulte1){
          case UP:break;
          case DOWM:listener.onShake(SHAKE_DOWM);break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	20	:	//	前
        break;
      case	21	:	//	前下
        switch (resulte1){
          case UP:listener.onShake(SHAKE_UP);break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	22	:	//	前上
        switch (resulte1){
          case UP:break;
          case DOWM:listener.onShake(SHAKE_DOWM);break;
          case LEFT:break;
          case RIGHT:break;
        }
        break;
      case	100	:	//	左
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	101	:	//	左下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	102	:	//	左上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	110	:	//	左后
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	111	:	//	左后下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	112	:	//	左后上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	120	:	//	左前
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	121	:	//	左前下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	122	:	//	左前上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:break;
          case RIGHT:listener.onShake(SHAKE_RIGHT);break;
        }
        break;
      case	200	:	//	右
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	201	:	//	右下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	202	:	//	右上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	210	:	//	右后
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	211	:	//	右后下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	212	:	//	右后上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	220	:	//	右前
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	221	:	//	右前下
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      case	222	:	//	右前上
        switch (resulte1){
          case UP:break;
          case DOWM:break;
          case LEFT:listener.onShake(SHAKE_LEFT);break;
          case RIGHT:break;
        }
        break;
      default:break;
    }
    resetAllData();
    Log.d("SwA", "////old:"+oldD+"------"+"new:"+newD+"//////////////////////////////////");
  }

}