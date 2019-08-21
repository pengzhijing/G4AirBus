package com.shima.smartbushome.about;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.shima.smartbushome.R;
import com.shima.smartbushome.util.SystemUIUtil;

public class HelpActivity extends AppCompatActivity {
    private WebView webView;
    String url="http://projects-beta.com/smartg4/wp-content/uploads/2017/01/EasyControlusermanual.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.helptoolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.tab_bgcolor));
        toolbar.setTitle("Help");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.tab_bgcolor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

      /*  Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);*/
        init();
    }
    private void init(){
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
        webView.getSettings().setJavaScriptEnabled(true);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
         // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
         // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
       //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
       /* webView.loadUrl("http://docs.google.com/gview?embedded=true&url="
                + "http://smarthomebus.com/dealers/Programming%20Manual/Programming%20Manual-%20Security%20Module%20v.2.2.pdf");*/
        webView.loadUrl(url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.room_menu, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏状态栏导航栏
        SystemUIUtil.setSystemUIVisible(this,false);
    }
}
