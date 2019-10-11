package com.pzj.ipcdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by MK on 2017/10/18.
 */

public class Tools {
    private static float memorySizeTotal =0;
    public static void savePicToSDcard(Context mContext, String strDID, final Bitmap bmp) {
        if (bmp == null) {
            return;
        }
        DatabaseUtil dbUtil = new DatabaseUtil(mContext);
        String strDate = getStrDate();
        dbUtil.open();
        Cursor cursor = dbUtil.queryVideoOrPictureByDate(strDID, strDate, DatabaseUtil.TYPE_PICTURE);
        int seri = cursor.getCount() + 1;
        dbUtil.close();
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
            if (!div.exists()) {
                div.mkdirs();
            }
            File file = new File(div, strDate + "_" + seri + strDID + ".jpg");
            fos = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos)) {
                fos.flush();
                //Log.d("tag", "takepicture success");
                dbUtil.open();
                dbUtil.createVideoOrPic(strDID, file.getAbsolutePath(),
                        DatabaseUtil.TYPE_PICTURE, strDate);
                dbUtil.close();
                String filePath = file.getAbsolutePath();
                String s1 = filePath
                        .substring(filePath.lastIndexOf("/") + 1);
                String date = s1.substring(0, 10);
                // 最后通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                if (!bmp.isRecycled() && bmp != null) {
                    //Log.i("info", "拍照回收");
                    bmp.recycle();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String savePresetPicToSDcard(Context mContext, String strDID, int presetIndex, final Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(), "/eye4/preset");
            if (!div.exists()) {
                div.mkdirs();
            }
            File file = new File(div, strDID + "_" + presetIndex + ".jpg");
            fos = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos)) {
                fos.flush();
                if (!bmp.isRecycled() && bmp != null) {
                    //Log.i("info", "拍照回收");
                    bmp.recycle();
                }
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getStrDate() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String strDate = f.format(d);
        return strDate;
    }


    public static int getPhoneSDKIntForPlayBack(){
        int  a =0;
        if(Build.BRAND.toLowerCase().contains("xiaomi")) a =24;
          else a =Integer.parseInt(android.os.Build.VERSION.SDK);
        return a;
    }

    public static int getPhoneMemoryForPlayBack(){
        if(Tools.getPhoneTotalMemory()>=2.8&&Integer.parseInt(android.os.Build.VERSION.SDK)>=23)
            return 1;
        else return 0;
    }

    public static float getPhoneTotalMemory() {
        if(memorySizeTotal!=0)return memorySizeTotal;
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            //LogTools.e("memory","getTotalMemory---str2="+str2);

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                //LogTools.e("memory","getTotalMemory---num="+num);
            }

            //LogTools.e("memory","getTotalMemory---KB---arrayOfString[1]="+Integer.valueOf(arrayOfString[1]).intValue());
            double length = ((double)Integer.valueOf(arrayOfString[1]).intValue()) / ((double)1000);// MB
            length = length / ((double)1000);// GB
            memorySizeTotal = (float) length;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return memorySizeTotal;
    }

}
