package com.shima.smartbushome.centercontrol;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savenfc;
import com.shima.smartbushome.selflayout.NFCLayout;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NFCActivity extends AppCompatActivity {
    private static final DateFormat TIME_FORMAT = SimpleDateFormat
            .getDateTimeInstance();
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private AlertDialog mDialog;
    private RelativeLayout nonfclayout;
    private ScrollView nfcview;
    private LinearLayout nfclinearlayout;
    Handler getdatahandler=new Handler();
    List<Savenfc> allnfcdata=new ArrayList<>();
    List<NFCLayout> layoutlist=new ArrayList<>();
    MenuItem  add,delete;
    boolean deletemode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.nfc_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("NFC");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置4.4及以上的状态栏上内边距
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {

            toolbar.setPadding(0,getStatusBarHeight(this),0,0);
        }
        //获取窗口对象
        Window window = this.getWindow();
        //设置透明状态栏,使 ContentView 内容覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        nonfclayout=(RelativeLayout)findViewById(R.id.nonfclayout);
        nfcview=(ScrollView)findViewById(R.id.nfcview);
        nfclinearlayout=(LinearLayout)findViewById(R.id.nfclinearlayout);

       // resolveIntent(getIntent());


        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //拦截系统级的NFC扫描，例如扫描蓝牙
        //mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord("",
                Locale.ENGLISH, true) });
        getdatahandler.postDelayed(getdatarun,20);
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
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
        if (mAdapter == null) {
            try{
                if (!mAdapter.isEnabled()) {
                    showWirelessSettingsDialog();
                }
            }catch (Exception e){
                showMessage("error", "No NFC found on this device");
            }
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(NFCActivity.this, "please open the NFC funcion", Toast.LENGTH_SHORT).show();
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
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            //隐式启动
            try{
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
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.light_setting_menu, menu);
        add= menu.findItem(R.id.light_add);
        delete= menu.findItem(R.id.light_remove);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case R.id.light_add:
                if(deletemode){
                    add.setTitle("ADD");
                    delete.setTitle("DELETE");
                    deletemode=false;
                    for(int i=0;i<layoutlist.size();i++){
                        layoutlist.get(i).setdeletevisable(false);
                    }
                }else{
                    startActivity(new Intent(this, NFCAddActivity.class));
                }
                break;
            case R.id.light_remove:
                deletemode=!deletemode;
                if(deletemode){
                    add.setTitle("CANCLE DELETE");
                    delete.setTitle("DELETE");
                    for(int i=0;i<layoutlist.size();i++){
                        layoutlist.get(i).setdeletevisable(true);
                    }
                }else{
                    for(int i=0;i<layoutlist.size();i++){
                        if(layoutlist.get(i).getIfneedtoDelete()){
                            MainActivity.mgr.deletenfc(layoutlist.get(i).getnfcid());
                        }
                    }
                    getdatahandler.postDelayed(getdatarun,20);
                    add.setTitle("ADD");
                    delete.setTitle("DELETE");
                    deletemode=false;
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_nfc.equals(action)) {
                getdatahandler.postDelayed(getdatarun, 20);
            }
        }
    };
    Runnable getdatarun=new Runnable() {
        @Override
        public void run() {
            if(allnfcdata.size()>0){allnfcdata.clear();}
            if(layoutlist.size()>0){layoutlist.clear();}
            nfclinearlayout.removeAllViews();
            allnfcdata= MainActivity.mgr.querynfc();
            if(allnfcdata.size()>0){
                nonfclayout.setVisibility(View.GONE);
                nfcview.setVisibility(View.VISIBLE);
                for(int i=0;i<allnfcdata.size();i++){
                    addspecView(allnfcdata.get(i));
                }
            }else{
                nonfclayout.setVisibility(View.VISIBLE);
                nfcview.setVisibility(View.GONE);
            }
            getdatahandler.removeCallbacks(getdatarun);
        }
    };

    private void addspecView(Savenfc sn) {
        NFCLayout lv=new NFCLayout(this);
        lv.setcontan(sn);
        nfclinearlayout.addView(lv);
        layoutlist.add(lv);
    }

    public void addnfc(View v){
        startActivity(new Intent(this,NFCAddActivity.class));
    }

    //16进制字符串转换为String
    private String hexString = "0123456789ABCDEF";
    public String decode(String bytes) {
        if (bytes.length() != 30) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    // 字符序列转换为16进制字符串
    private static String bytesToHexString(byte[] src, boolean isPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isPrefix == true) {
            stringBuilder.append("0x");
        }
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.toUpperCase(Character.forDigit(
                    (src[i] >>> 4) & 0x0F, 16));
            buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
                    16));
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    private void showMessage(String title, String message) {
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.show();
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

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("NFC is not enabled. Please go to the wireless settings to enable it.");
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        builder.create().show();
        return;
    }



    public static String ACTION_UPDATE_nfc="com.smarthome.updatenfc";

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(ACTION_UPDATE_nfc);
        return intentFilter;
    }
}
