package com.shima.smartbushome.centercontrol;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.MainActivity;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.Savemarcobutton;
import com.shima.smartbushome.database.Savenfc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NFCAddActivity extends AppCompatActivity implements View.OnClickListener{
    private static final DateFormat TIME_FORMAT = SimpleDateFormat
            .getDateTimeInstance();
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private AlertDialog mDialog;
    private TextView nfcadd_actiondetail;
    private EditText nfcadd_tagid,nfcadd_detail,nfcadd_name;
    private Spinner nfcadd_spinner;
    private Button nfcadd_save;
    Savenfc thissavenfc=new Savenfc();
    Handler initdata=new Handler();
    List<String> spinnerdata=new ArrayList<>();
    ArrayAdapter<String> spinneradapter;
    int marcoID=99999999;
    boolean writesucceed=false;
    String writeTagId="",newwriteTagId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcadd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.nfcadd_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("NFC Add");
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

        resolveIntent(getIntent());
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
                .create();
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //拦截系统级的NFC扫描，例如扫描蓝牙
        //mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord("",
                Locale.ENGLISH, true) });

        spinnerdata.clear();
        spinnerdata.add("Marco");spinnerdata.add("Calling");//spinnerdata.add("Message");
        spinneradapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerdata);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initView();
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

    private void initView() {
        nfcadd_tagid= (EditText) findViewById(R.id.nfcadd_tagid);
        nfcadd_actiondetail = (TextView) findViewById(R.id.nfcadd_actiondetail);
        nfcadd_save=(Button)findViewById(R.id.nfcadd_save);
        nfcadd_save.setOnClickListener(this);
        nfcadd_detail = (EditText) findViewById(R.id.nfcadd_detail);
        nfcadd_detail.setOnClickListener(this);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nfcadd_detail.getWindowToken(), 0);
        nfcadd_name = (EditText) findViewById(R.id.nfcadd_name);
        nfcadd_spinner = (Spinner) findViewById(R.id.nfcadd_spinner);
        nfcadd_spinner.setAdapter(spinneradapter);
        thissavenfc.action_type = 0;
        nfcadd_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nfcadd_actiondetail.setText("Marco:");
                        thissavenfc.action_type = 1;
                        break;
                    case 1:
                        nfcadd_actiondetail.setText("Phone Num:");
                        thissavenfc.action_type = 2;
                        break;
                    case 2:
                        nfcadd_actiondetail.setText("Message:");
                        thissavenfc.action_type = 3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter == null) {
            try{
                if (!mAdapter.isEnabled()) {
                    showWirelessSettingsDialog();
                }
            }catch (Exception e){
                showMessage("error", "No NFC found on this device");
                nfcadd_tagid.setText("Your device did not support NFC！");
            }
            return;
        }
        if (!mAdapter.isEnabled()) {
            nfcadd_tagid.setText("Please open your NFC function！");
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
                mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
                mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**********************************************************************************************/
    AlertView marcolistalert;
    List<Savemarcobutton> listmarco=new ArrayList<>();
    List<String> stringlist=new ArrayList<>();

    public void onClick(View v){
        switch (v.getId()){
            case R.id.nfcadd_detail:
                if(thissavenfc.action_type==1){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nfcadd_detail.getWindowToken(),0);
                    if(stringlist.size()>0){stringlist.clear();}
                    listmarco=MainActivity.mgr.querymarcobutton();
                    for(int i=0;i<listmarco.size();i++){
                        stringlist.add(listmarco.get(i).marco_remark);
                    }
                    marcolistalert = new AlertView("Select Marco to Act", null, "CANCEL",  null, null, this, AlertView.Style.Alert,
                            null);
                    ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.listdialog, null);
                    ListView deletelist=(ListView)extView.findViewById(R.id.listView6);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,400);
                    extView.setLayoutParams(lp);
                    deletelist.setAdapter(new ArrayAdapter(this, R.layout.simplelistitem, stringlist));
                    deletelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            nfcadd_detail.setText(listmarco.get(position).marco_remark);
                            marcoID = listmarco.get(position).marco_id;
                            marcolistalert.dismiss();
                        }
                    });
                    marcolistalert.addExtView(extView);
                    marcolistalert.show();

                    //Toast.makeText(NFCAddActivity.this, "marco", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nfcadd_save:
                String name=nfcadd_name.getText().toString().trim();
                int nfcid=0;
                List<Savenfc> allnfc= MainActivity.mgr.querynfc();
                if(allnfc.size()>0){
                    nfcid=allnfc.get(allnfc.size()-1).nfc_id+1;
                }else{
                    nfcid=0;
                }
                if(name.length()>0){
                    thissavenfc.nfc_name=name;
                    String content=nfcadd_tagid.getText().toString().trim();
                    if(content.length()>0){
                        thissavenfc.nfc_content=content;
                        switch (thissavenfc.action_type){
                            case 0:
                                Toast.makeText(NFCAddActivity.this, "please select 1 control type", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                if(marcoID!=99999999){
                                    thissavenfc.nfc_id=nfcid;
                                    thissavenfc.nfc_icon="";
                                    thissavenfc.state=0;
                                    thissavenfc.call_num="";
                                    thissavenfc.message="";
                                    thissavenfc.resume1=0;
                                    thissavenfc.resume2="";
                                    thissavenfc.delaytime=0;
                                    thissavenfc.marco_ID=marcoID;
                                    thissavenfc.marco_name=nfcadd_detail.getText().toString().trim();
                                    MainActivity.mgr.addnfc(thissavenfc);
                                    broadcastUpdate(NFCActivity.ACTION_UPDATE_nfc);
                                    finish();
                                }else{
                                    Toast.makeText(NFCAddActivity.this, "please select 1 marco", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                String num=nfcadd_detail.getText().toString().trim();
                                if(num.length()>0){
                                    thissavenfc.call_num=num;
                                    thissavenfc.nfc_id=nfcid;
                                    thissavenfc.nfc_icon="";
                                    thissavenfc.state=0;
                                    thissavenfc.message="";
                                    thissavenfc.resume1=0;
                                    thissavenfc.resume2="";
                                    thissavenfc.delaytime=0;
                                    thissavenfc.marco_ID=0;
                                    thissavenfc.marco_name="";
                                    MainActivity.mgr.addnfc(thissavenfc);
                                    broadcastUpdate(NFCActivity.ACTION_UPDATE_nfc);
                                    finish();
                                }else{
                                    Toast.makeText(NFCAddActivity.this, "please input your phone num", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3:
                                String message=nfcadd_detail.getText().toString().trim();
                                if(message.length()>0){

                                }else{
                                    Toast.makeText(NFCAddActivity.this, "please input your message", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }else{
                        Toast.makeText(NFCAddActivity.this, "please put your NFC card on you phone", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NFCAddActivity.this, "please input your name", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**********************************************************************************************/
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
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

    //初步判断是什么类型NFC卡
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                nfcadd_tagid.setText(dumpTagData(tag));
            }
            // Setup the view
        }
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

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        //resolveIntent(intent);
        Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        newwriteTagId=dumpTagData(tag);
        if(newwriteTagId.equals(writeTagId)){
            if(writesucceed){

            }else{
                onWriteNFC((Tag) tag);
            }
        }else{
            writeTagId=newwriteTagId;
            onWriteNFC((Tag) tag);
        }
        nfcadd_tagid.setText(dumpTagData(tag));
    }

    private NdefMessage ndefMessage;
    public void onWriteNFC(final Tag tag){
        if(tag==null){
            Toast.makeText(this, "did not find TAG", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "tag had value", Toast.LENGTH_SHORT).show();
        ndefMessage=new NdefMessage(new NdefRecord[]{NdefRecord.createApplicationRecord("com.dave.smartbushome")});
        //写入一个网址
        //ndefMessage=new NdefMessage(new NdefRecord[]{NdefRecord.createUri(Uri.parse("http://www.baidu.com"))});

        int size=ndefMessage.toByteArray().length;
        Toast.makeText(this, "length need to write"+size, Toast.LENGTH_SHORT).show();
        Ndef ndef=Ndef.get(tag);

        if(ndef!=null){
            try {
                ndef.connect();
                if(!ndef.isWritable()){
                    Toast.makeText(this, "NFC did not support write data", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ndef.getMaxSize()<size){
                    Toast.makeText(this, "NFC card did not have enough space", Toast.LENGTH_SHORT).show();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                Toast.makeText(this, "NFC card write succeed", Toast.LENGTH_SHORT).show();
                writesucceed=true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "this card is not ndef format", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this).setTitle("do you want to format to ndef and write the package_name?")
                    .setPositiveButton("YES", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            NdefFormatable formatable= NdefFormatable.get(tag);
                            if(formatable==null){
                                Toast.makeText(NFCAddActivity.this, "this card can not format into ndef",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                formatable.connect();
                                formatable.format(ndefMessage);
                                Toast.makeText(NFCAddActivity. this, "NFC car write succeed!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (FormatException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("NO", null).create().show();
        }
    }

    /***********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.marco_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
