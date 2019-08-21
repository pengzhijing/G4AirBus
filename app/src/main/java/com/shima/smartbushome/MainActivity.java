package com.shima.smartbushome;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.shima.smartbushome.assist.AutoUpdate.updatemain;
import com.shima.smartbushome.assist.MusicNotification;
import com.shima.smartbushome.assist.MusicNotifyReceiver;
import com.shima.smartbushome.assist.nfc.NFCRun;

import com.shima.smartbushome.assist.scheduleutil.ScheduleRestartService;
import com.shima.smartbushome.assist.scheduleutil.ScheduleServer;
import com.shima.smartbushome.database.DBManager;
import com.shima.smartbushome.database.DatabaseContext;
import com.shima.smartbushome.database.Savenfc;
import com.shima.smartbushome.udp.udp_socket;
import com.shima.smartbushome.util.SystemUIUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    public static TextView locationid;
    private FragmentManager mFragmentManager;
    public static DBManager mgr;
    public static udp_socket mydupsocket;
    public static Context maincontext;
    public static boolean islockshake=true,islockchangeid=false;
    MusicNotifyReceiver mReceiver;
    public static List<HashMap<String,String>> netdeviceList=new ArrayList<HashMap<String, String>>();
    Toolbar toolbar;
    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition;
    public final static String ACTION_BACKPRESS = "com.example.main.BACKPRESS";
    private MainFragment mHomeFragment;
    private CenterFragment mcenterFragment;
    Window window;
    updatemain update;
    private List<Savenfc> thisnfclist=new ArrayList<>();
    private boolean hadnfc=false;
    private NFCRun revnfc;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(0,getStatusBarHeight(this),0,0);
        }
        //获取窗口对象
         window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        locationid=(TextView)findViewById(R.id.locationid);
        locationid.setMovementMethod(ScrollingMovementMethod.getInstance());
        bottomNavigationBar=(BottomNavigationBar)findViewById(R.id.buttonitem);//底部标签栏
        buttonitemInit();//底部标签栏初始化
        initCheckPermission();
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //拦截系统级的NFC扫描，例如扫描蓝牙
        //mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord("",
                Locale.ENGLISH, true) });


    }

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
       int statusBarHeight=0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        System.out.println("nfc newin");
        if(getIntent()!=null){
            List<Savenfc> alldata=mgr.querynfc();
            if(thisnfclist.size()>0){thisnfclist.clear();}
            Parcelable tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag!=null){
                String tagstr=dumpTagData((Tag)tag);
                for(int i=0;i<alldata.size();i++){
                    if(alldata.get(i).nfc_content.equals(tagstr)){
                        thisnfclist.add(alldata.get(i));
                    }
                }
            }
            revnfc.run(thisnfclist);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        if (mAdapter == null) {
            try{
                if (!mAdapter.isEnabled()) {

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        if (!mAdapter.isEnabled()) {

            return;
        }

        if (mAdapter != null) {
            //隐式启动
            try{
                mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
                mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }
    @Override
    public void onPause(){
        super.onPause();
      //  unregeisterReceiver();
        if (mAdapter != null) {
            //隐式启动
            try {
                mAdapter.disableForegroundDispatch(this);
                mAdapter.disableForegroundNdefPush(this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregeisterReceiver();
        Intent service=new Intent(this, ScheduleServer.class);
      //  startService(service);
        Intent service2=new Intent(this, ScheduleRestartService.class);
      //  startService(service2);
        MainActivity.mydupsocket.StopAllThread();
        //MusicNotification.manager.cancel(10);
        if(mgr!=null)
            mgr.closeDB();
       // finish();
    }
/****************************************init method**********************************************************/
public void initcreate(){

    maincontext=this;
    mReceiver = new MusicNotifyReceiver();
    mgr = new DBManager(maincontext);
    mydupsocket=new udp_socket(maincontext);
    SharedPreferences sharedPre = getSharedPreferences("ipconfig", MODE_PRIVATE);
    udp_socket.mansetip = sharedPre.getBoolean("ipset", false);
    if(udp_socket.mansetip ){
        udp_socket.manlocalip=stringtobyte(sharedPre.getString("manlocalip", ""));
        udp_socket.mantargetip=stringtobyte(sharedPre.getString("mantargetip", ""));
    }
    SharedPreferences sharedPre2 = getSharedPreferences("ddnsconfig", MODE_PRIVATE);
    udp_socket.ddnssetip = sharedPre2.getBoolean("ipset", false);
    if(udp_socket.ddnssetip){
        udp_socket.ddnstargetip=stringtobyte(sharedPre2.getString("ddnstargetip", ""));
    }
    SharedPreferences sharedPre3 = getSharedPreferences("httpconfig", MODE_PRIVATE);
    udp_socket.rsipip = sharedPre3.getBoolean("ipset", false);

    SharedPreferences sharedPre4 = getSharedPreferences("lockinfo", MODE_PRIVATE);
    islockshake = sharedPre4.getBoolean("lockshake", true);
    islockchangeid= sharedPre4.getBoolean("lockchangeid", false);
    if(udp_socket.rsipip){
        String mac=sharedPre3.getString("MacAddress", "");
        if(mac.equals("")){

        }else{
            udp_socket.MacAddress=stringtoMACbyte(mac);
        }

    }
    SharedPreferences sharedcolorPre = getSharedPreferences("pagesbgcolor", MODE_PRIVATE);
    int backgroudcolor = sharedcolorPre.getInt("mainbgcolor", 0xFF000000);
    RelativeLayout mainacbg=(RelativeLayout)findViewById(R.id.mainacbg);
    mainacbg.setBackgroundColor(backgroudcolor);

    if(MainActivity.mydupsocket!=null){
        MainActivity.mydupsocket.initprocess();
    }else{
        Toast.makeText(MainActivity.this, "init process fail", Toast.LENGTH_SHORT).show();
    }

    intiReceiver();
    //initautoupdate(); 检查更新
    revnfc=new NFCRun(this);

    if(!this.isWorked("com.shima.smartbushome.ScheduleServer")){
        Intent service=new Intent(this, ScheduleServer.class);
       // startService(service);
        Intent service2=new Intent(this, ScheduleRestartService.class);
       // startService(service2);
        System.out.println("服务没有启动！！");
    }
    else{
        System.out.println("服务已经启动了！！");
    }

}

private  void buttonitemInit(){
    bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
        @Override
        public void onTabSelected(int position) {//未选中 -> 选中


            lastSelectedPosition = position;

            //开启事务
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            hideFragment(transaction);
            switch (position) {
                case 0:
                    if (mHomeFragment == null) {
                        mHomeFragment = MainFragment.newInstance("mHomeFragment");
                        transaction.add(R.id.tb, mHomeFragment);
                    } else {
                        transaction.show(mHomeFragment);
                    }
                    toolbar.setBackgroundColor(getResources().getColor(R.color.title_transparent_black));
                    if (Build.VERSION.SDK_INT >= 21) {
                        window.setStatusBarColor(getResources().getColor(R.color.black));
                    }
                    break;
                case 1:
                    if (mcenterFragment == null) {
                        mcenterFragment = CenterFragment.newInstance("mToolFragment");
                        transaction.add(R.id.tb, mcenterFragment);
                    } else {
                        transaction.show(mcenterFragment);
                    }
                    toolbar.setBackgroundColor(getResources().getColor(R.color.title_transparent_black));
                    if (Build.VERSION.SDK_INT >= 21) {
                        window.setStatusBarColor(getResources().getColor(R.color.black));
                    }
                    break;
            }
            transaction.commit();
        }

        @Override
        public void onTabUnselected(int position) {
        }

        @Override
        public void onTabReselected(int position) {
        }
    });
    BottomNavigationItem mainpage=new BottomNavigationItem(R.mipmap.ic_home_white_24dp, "Room");
    mainpage.setActiveColor("#5f000000").setInActiveColor("#ffffff");
    BottomNavigationItem toolitem=new BottomNavigationItem(R.mipmap.ic_launch_white_24dp, "Center");
    toolitem.setActiveColor("#5f000000").setInActiveColor("#ffffff");
    bottomNavigationBar.addItem(mainpage)
            .addItem(toolitem)
            .setMode(BottomNavigationBar.MODE_DEFAULT)
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
            .initialise();//所有的设置需在调用该方法前完成
    setDefaultFragment();
}

    private void intiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicNotification.ACTION_Button);
        intentFilter.addAction(MusicNotification.ACTION_notify_close);
        intentFilter.addAction(udp_socket.ACTION_DATA_IN);
        getApplicationContext().registerReceiver(mReceiver, intentFilter);

      /*  IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Intent.ACTION_TIME_TICK);
        getApplicationContext().registerReceiver(schdueleReceiver, intentFilter2);*/

    }

    private void initautoupdate(){
        update=new updatemain(this);
        update.checkUpdate(true);
    }

/****************************************init method**********************************************************/

/****************************************assist method**********************************************************/

private boolean isWorked(String className) {
    ActivityManager myManager = (ActivityManager) MainActivity.this
            .getApplicationContext().getSystemService(
                    Context.ACTIVITY_SERVICE);
    ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
            .getRunningServices(30);
    for (int i = 0; i < runningService.size(); i++) {
        if (runningService.get(i).service.getClassName().toString()
                .equals(className)) {
            return true;
        }
    }
    return false;
}

    private void setDefaultFragment() {
    mFragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    mHomeFragment = mHomeFragment.newInstance("mHomeFragment");
    transaction.add(R.id.tb, mHomeFragment);
    transaction.commit();
  }

    private void hideFragment(FragmentTransaction transaction){
        if (mHomeFragment != null){
            transaction.hide(mHomeFragment);
        }
        if (mcenterFragment != null){
            transaction.hide(mcenterFragment);
        }
    }

    private void unregeisterReceiver() {
        if (mReceiver != null) {
            getApplicationContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        /*if (schdueleReceiver != null) {
            getApplicationContext().unregisterReceiver(schdueleReceiver);
            schdueleReceiver = null;
        }*/
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    //一般公家卡，扫描的信息
    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append(getHex(id));
        return sb.toString().trim();
    }
    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    private NdefRecord newTextRecord(String text, Locale locale,
                                     boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(
                Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
                .forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
                textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);
    }
    public byte[] stringtobyte(String ip){
        String[] x=ip.split("\\.");
        byte[] result=new byte[4];
        result[0]=(byte)Integer.parseInt(x[0]);
        result[1]=(byte)Integer.parseInt(x[1]);
        result[2]=(byte)Integer.parseInt(x[2]);
        result[3]=(byte)Integer.parseInt(x[3]);
        return result;
    }
    public byte[] stringtoMACbyte(String ip){
        String[] x=ip.split("\\.");
        byte[] result=new byte[8];
        result[0]=(byte)Integer.parseInt(x[0],16);
        result[1]=(byte)Integer.parseInt(x[1],16);
        result[2]=(byte)Integer.parseInt(x[2],16);
        result[3]=(byte)Integer.parseInt(x[3],16);
        result[4]=(byte)Integer.parseInt(x[4],16);
        result[5]=(byte)Integer.parseInt(x[5],16);
        result[6]=(byte)Integer.parseInt(x[6],16);
        result[7]=(byte)Integer.parseInt(x[7],16);
        return result;
    }
/****************************************assist method**********************************************************/

/****************************************system method**********************************************************/
    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        broadcastUpdate(ACTION_BACKPRESS);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                //   perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
           /*     if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {*/
                if ( perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    initcreate();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                    showMessageOKCancel("You can open Permission in app setting",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                                    startActivity(intent);
                                }
                            });
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Activity activity){
        String packageName = activity.getPackageName();
        PowerManager pm = (PowerManager) activity
                .getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            return true;
        }else {
            return false;
        }
    }
    /**
     * 针对N以上的Doze模式
     *
     * @param activity
     */
    public static int REQUEST_IGNORE_BATTERY_CODE=11;
    public static void isIgnoreBatteryOption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
               intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE){

            }
        }else if (resultCode == RESULT_CANCELED){
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE){
                Toast.makeText(MainActivity.this, "please confirm to ignore battery saving", Toast.LENGTH_SHORT).show();
            }
        }

    }



/****************************************system method**********************************************************/



/****************************************check sdk version**********************************************************/
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public void initCheckPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            //忽略电池优化，定时会更加准确
            if(isIgnoringBatteryOptimizations(this)){

            }else{
               // isIgnoreBatteryOption(this);
            }
            insertDummyContactWrapper();
        } else {
            initcreate();// Pre-Marshmallow
        }
    }

    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
     /*   if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Fine Location");*/
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
      /*  if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Coarse Location");*/

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }else{
            initcreate();
        }
       // insertDummyContact();
    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }






}
