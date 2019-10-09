package com.pzj.ipcdemo.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pzj.ipcdemo.R;
import com.pzj.ipcdemo.adapter.SearchListAdapter;
import com.pzj.ipcdemo.entity.VStarCamera;
import com.pzj.ipcdemo.service.BridgeService;

import java.util.ArrayList;
import java.util.List;

import per.goweii.anylayer.AnimHelper;
import per.goweii.anylayer.AnyLayer;
import vstc2.nativecaller.NativeCaller;

//IPC 局域网搜索 对话框
public class SearchAnyLayerView implements BridgeService.AddCameraInterface {

    public final static String TAG="SearchAnyLayerView";

    private Context context;

    private RecyclerView rv_searchList;
    private ProgressBar pb_search;

    private RelativeLayout layout_dialog;
    private AnyLayer anyLayer=null;

    private OnSelectSearchListener onSelectSearchListener;

    //局域网内搜索到的设备列表
    private List<VStarCamera> searchList=new ArrayList<>();

    private SearchListAdapter searchListAdapter;


    public SearchAnyLayerView(Context context){
      this.context=context;
    }

    public void initView(AnyLayer anyLayer){
        rv_searchList=anyLayer.getView(R.id.rv_searchList);
        pb_search=anyLayer.getView(R.id.pb_search);
        layout_dialog=anyLayer.getView(R.id.layout_dialog);

        //初始化列表布局
        RecyclerView.LayoutManager layout = new GridLayoutManager(context, 1);//网格布局，每行为1
        rv_searchList.setLayoutManager(layout);
        rv_searchList.setHasFixedSize(true);//适配器内容改变，不会改变RecyclerView的大小
    }

    public void dismiss(){
        try {
            anyLayer.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //显示对话框
    public void show(){
        if (anyLayer!=null){
            if (!anyLayer.isShow()){
                anyLayer.show();
            }
        }else {
            anyLayer = AnyLayer.with(context)
                    .contentView(R.layout.dialog_search_camera_list)
                    .backgroundBlurPercent(0.015F)//背景高斯模糊
                    .gravity(Gravity.TOP | Gravity.CENTER)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .contentAnim(new AnyLayer.IAnim() {
                        @Override
                        public long inAnim(View content) {
                            AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                            return 350;
                        }

                        @Override
                        public long outAnim(View content) {
                            AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                            return 350;
                        }
                    });

            initView(anyLayer);

            //绑定搜索回调
            BridgeService.setAddCameraInterface(this);

            //开始搜索
            startSearch();


            //绑定数据适配器
            searchListAdapter=new SearchListAdapter(context,searchList);
            rv_searchList.setAdapter(searchListAdapter);

            searchListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    onSelectSearchListener.onSelectSearchListener(searchList.get(position));
                }
            });

            layout_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (anyLayer.isShow()){
                            anyLayer.dismiss();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            anyLayer.show();
        }
    }

    int count=0;

    //处理搜索到的设备后回调
    Handler searchHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            VStarCamera vStarCamera= (VStarCamera) msg.getData().getSerializable("search");

            for (VStarCamera item: searchList) {
                if (item.getId().equals(vStarCamera.getId())){
                    count++;
                    if (count==2){
                        //停止搜索
                        NativeCaller.StopSearch();
                        //隐藏进度条
                        pb_search.setVisibility(View.INVISIBLE);

                    }
                    return;
                }
            }

            //添加到搜索列表 并刷新
            List<VStarCamera> searchs=new ArrayList<>();
            searchs.addAll(searchList);
            searchs.add(vStarCamera);

            searchList.clear();
            searchList.addAll(searchs);
            searchListAdapter.notifyDataSetChanged();






        }
    };


    //搜索设备回调
    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
            VStarCamera vStarCamera=new VStarCamera(strName, strDeviceID, "admin", "");

            Message message=new Message();
            Bundle bundle=new Bundle();
            bundle.putSerializable("search",vStarCamera);
            message.setData(bundle);

            searchHandler.sendMessage(message);

            Log.d(TAG,vStarCamera.toString());
    }


    //开始搜索
    public void startSearch(){
        searchList.clear();
        new Thread(new SearchThread()).start();
    }

    //局域网内搜索设备线程
    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "startSearch");
            NativeCaller.StartSearch();
        }
    }


    //定义接口回调
    public interface OnSelectSearchListener{
        void onSelectSearchListener(VStarCamera vStarCamera);
    }

    public void setOnSelectSearchListener(OnSelectSearchListener onSelectSearchListener){
        this.onSelectSearchListener=onSelectSearchListener;
    }





}
