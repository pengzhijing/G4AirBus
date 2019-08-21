package com.shima.smartbushome.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 文件管理类
 */

public class FileUtil {


    //判断sdcard是否被挂载
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    //根据文件路径复制单个文件
    public  static  boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true ;
            }
            return false ;
        }
        catch (Exception e) {
            System.out.println("copyFile error");
            e.printStackTrace();
            return false ;

        }
    }

    //根据文件复制单个文件
    public  static  boolean copyFile(File file, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;

            if (file!=null) { //文件存在时
                InputStream inStream = new FileInputStream(file); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true ;
            }
            return false ;
        }
        catch (Exception e) {
            System.out.println("copyFile error");
            e.printStackTrace();
            return false ;

        }
    }

    // 复制整个文件夹内容
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("copyFolder error");
            e.printStackTrace();
        }
    }

    // 删除方法
    public static boolean deleteSDFile(String absolutePath) {

        boolean isDelete=false;

        try {
            // file目标文件夹绝对路径
            File file=new File(absolutePath);
            if (file.exists()) { // 指定文件是否存在
                if (file.isFile()) { // 该路径名表示的文件是否是一个标准文件
                    file.delete(); // 删除该文件
                    isDelete=true;
                } else if (file.isDirectory()) { // 该路径名表示的文件是否是一个目录
                    File[] files = file.listFiles(); // 列出当前文件夹下的所有文件
                    for (File f : files) {
                        deleteSDFile(f.getAbsolutePath()); // 递归删除
                    }
                }
                file.delete();// 删除文件夹
                isDelete=true;
            }

        }catch (Exception e){
            e.printStackTrace();
            isDelete=false;
        }
        return isDelete;
    }

    // 获取文件大小方法
    public static String GetFileSize(File ff) {
        float changdu = ff.length();

        System.out.println("-------changdu:" + changdu);
        String changdu2 = "";

        int kb = 1024;
        int mb = 1024 * kb;
        int gb = 1024 * mb;
        if (changdu < kb) {
            changdu2 = String.format("%d byte", (int) changdu);
        } else if (changdu < mb) {
            changdu2 = String.format("%.2f kb", changdu / kb);
        } else if (changdu < gb) {
            changdu2 = String.format("%.2f mb", changdu / mb);
        }
        return changdu2;
    }

    // 根据文件路径获取文件类型方法
    public static String GetFileType(String filetype) {

        if ((filetype != null) && (filetype.length() > 0)) {
            int dot = filetype.lastIndexOf('.');
            if ((dot > -1) && (dot < (filetype.length() - 1))) {
                return filetype.substring(dot + 1);
            }
        }
        return filetype;

    }

    //uri转文件绝对路径
    public static String UriToFilePath(Activity activity,Uri uri){
        String filepath="";

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, proj, null,null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        filepath = cursor.getString(column_index);

        return  filepath;
    }

    // 解决小米手机上获取图片路径为null的情况
    public static Uri getPictureUri(Context context, android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}
