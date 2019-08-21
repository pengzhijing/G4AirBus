package com.shima.smartbushome.assist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;

/**
 * Created by Administrator on 2016/9/21.
 */
public class MusicNotification {
    public static final String NOTICE_ID_KEY = "NOTICE_ID";
    public static final String ACTION_Button = "com.dave.smartbushome.button";
    public static final String ACTION_notify_close = "com.dave.smartbushome.close";
    public static final int NOTICE_ID_TYPE_0 = R.string.app_name;
    public static boolean playcheck=false;
    public static RemoteViews remoteViews;
    public static NotificationManager manager;
    public static Notification notification;
    @TargetApi(16)
    public static void sendResidentNoticeType0(Context context,String songname) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_notify);
        //remoteViews.setTextViewText(R.id.title_tv, title);
       // remoteViews.setTextViewText(R.id.content_tv, content);
        remoteViews.setTextViewText(R.id.music_notify_songname, songname);
      //  remoteViews.setImageViewResource(R.id.icon_iv, R.drawable.logo);


        if(playcheck){
            remoteViews.setImageViewResource(R.id.music_notify_play, R.drawable.notify_play);
        }else{
            remoteViews.setImageViewResource(R.id.music_notify_play, R.drawable.notify_pause);
        }
        remoteViews.setInt(R.id.close_iv, "setColorFilter", getIconColor());



        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTICE_ID_KEY, NOTICE_ID_TYPE_0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notice_view_type_0, pendingIntent);

        int requestCode1 = (int) SystemClock.uptimeMillis();
        Intent intent1 = new Intent(ACTION_notify_close);
        intent1.putExtra(NOTICE_ID_KEY, NOTICE_ID_TYPE_0);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, requestCode1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.close_iv, pendingIntent1);

        Intent intent2 = new Intent(ACTION_Button);
        intent2.putExtra("button_type", 0);
        PendingIntent intentpi1 = PendingIntent.getBroadcast(context, 111, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notify_back, intentpi1);

        //Intent intent3 = new Intent(ACTION_Button);
        intent2.putExtra("button_type", 1);
        PendingIntent intentpi2 = PendingIntent.getBroadcast(context, 222, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notify_next, intentpi2);

       // Intent intent4 = new Intent(ACTION_Button);
        intent2.putExtra("button_type", 2);
        PendingIntent intentpi3 = PendingIntent.getBroadcast(context, 333, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_notify_play, intentpi3);

        builder.setSmallIcon(R.mipmap.ic_launcher);

       /* notification = builder.build();
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
            notification.bigContentView = remoteViews;
        }
        notification.contentView = remoteViews;
        notification.contentIntent=pendingIntent;
        //notification.flags = Notification.FLAG_NO_CLEAR ;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTICE_ID_TYPE_0, notification);*/
    }

    public static void sendDefaultNotice(Context context, String title, String content, @DrawableRes int res) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);



        Notification notification = builder
                .setContentTitle("Campus")
                .setContentText("It's a default notification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTICE_ID_TYPE_0, notification);
    }
    public static int getIconColor(){
        return Color.parseColor("#999999");

    }

    public static void clearNotification(Context context, int noticeId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(noticeId);
    }

}
