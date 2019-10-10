package com.pzj.ipcdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pzj.ipcdemo.R;
import com.pzj.ipcdemo.entity.VStarCamera;
import com.pzj.ipcdemo.utils.ImageDispose;


import java.util.ArrayList;
import java.util.List;


/**
 * 摄像头列表适配器
 */

public class IPCListAdapter extends BaseItemDraggableAdapter {

    private Context context;
    private List<Integer> ipcStates;//标记设备状态

    public static final int PPPP_STATUS_CONNECTING = 0; // 连接中
    public static final int PPPP_STATUS_INITIALING = 1; // 已连接，正在初始化
    public static final int PPPP_STATUS_ON_LINE = 2; // 在线
    public static final int PPPP_STATUS_CONNECT_FAILED = 3; // 连接失败
    public static final int PPPP_STATUS_DISCONNECT = 4; // / 连接已关闭
    public static final int PPPP_STATUS_INVALID_ID = 5; // 无效 UID
    public static final int PPPP_STATUS_DEVICE_NOT_ON_LINE = 6; // 不在线
    public static final int PPPP_STATUS_CONNECT_TIMEOUT = 7; // 连接超时
    public static final int PPPP_STATUS_WRONGUSER_RIGHTPWD = 8; // 密码错误 ..
    public static final int PPPP_STATUS_WRONGPWD_RIGHTUSER = 9; // 密码错误. .
    public static final int PPPP_STATUS_WRONGPWD_WRONGUSER = 10; // 密码错误. .

    public IPCListAdapter(Context context, List<VStarCamera> vStarCameraList) {
        super(R.layout.view_ipc_item, vStarCameraList);
        this.context=context;
        ipcStates=new ArrayList<>();
        for (int i=0;i<vStarCameraList.size();i++){
            ipcStates.add(-1);
        }
    }


    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        VStarCamera vStarCamera=(VStarCamera) item;
        String name=vStarCamera.getName();

        helper.setText(R.id.tv_ipcName,name);

        //设置状态
        switch (ipcStates.get(helper.getAdapterPosition())){
            case PPPP_STATUS_CONNECTING : // 连接中
                helper.setText(R.id.tv_status,"connecting");
                break;
            case PPPP_STATUS_INITIALING : // 已连接，正在初始化
                helper.setText(R.id.tv_status,"initializing");
                break;
            case PPPP_STATUS_ON_LINE : // 在线
                helper.setText(R.id.tv_status,"On-line");
                break;
            case PPPP_STATUS_CONNECT_FAILED : // 连接失败
                helper.setText(R.id.tv_status,"connection failed");
                break;
            case PPPP_STATUS_DISCONNECT : // / 连接已关闭
                helper.setText(R.id.tv_status,"connection closed");
                break;
            case PPPP_STATUS_INVALID_ID :// 无效 UID
                helper.setText(R.id.tv_status,"invalid uid");
                break;
            case PPPP_STATUS_DEVICE_NOT_ON_LINE : // 不在线
                helper.setText(R.id.tv_status,"not online");
                break;
            case PPPP_STATUS_CONNECT_TIMEOUT : // 连接超时
                helper.setText(R.id.tv_status,"connection timed out");
                break;
            case PPPP_STATUS_WRONGUSER_RIGHTPWD : // 密码错误 ..
            case PPPP_STATUS_WRONGPWD_RIGHTUSER : // 密码错误. .
            case PPPP_STATUS_WRONGPWD_WRONGUSER : // 密码错误. .
                helper.setText(R.id.tv_status,"password error");
                break;
                default:
                    helper.setText(R.id.tv_status,"unknown");

        }

        //设置预览图
        if (vStarCamera.getbImage()!=null){
            try {
                ImageView iv_ipc=helper.getView(R.id.iv_ipc);

                //将字节数组转换为ImageView可调用的Bitmap对象
                Bitmap bitmap=ImageDispose.getPicFromBytes(vStarCamera.getbImage(),new BitmapFactory.Options());

                //设置图片圆角角度
                RoundedCorners roundedCorners= new RoundedCorners(10);
                //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
               // RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(500, 500);
                RequestOptions options=RequestOptions.bitmapTransform(roundedCorners);

                //加载区域背景图片
                Glide.with(context)
                        .load(bitmap)
                        .apply(options)
                        .into(iv_ipc);

            }catch (Exception e){
                e.printStackTrace();
            }
        }



        helper.addOnClickListener(R.id.iv_ipcSetting);
        helper.addOnClickListener(R.id.iv_ipcUpdate);
        helper.addOnClickListener(R.id.iv_ipcDelete);
        helper.addOnClickListener(R.id.iv_item);

        helper.addOnLongClickListener(R.id.iv_ipcSetting);
        helper.addOnLongClickListener(R.id.iv_ipcUpdate);
        helper.addOnLongClickListener(R.id.iv_ipcDelete);
        helper.addOnLongClickListener(R.id.iv_item);
    }


    public List<Integer> getIpcStates() {
        return ipcStates;
    }

    public void setIpcStates(List<Integer> ipcStates) {
        this.ipcStates = ipcStates;
        this.notifyDataSetChanged();
    }

    //根据index设置设备状态
    public void setIpcStatesIndex(int ipcState,int index) {
        ipcStates.remove(index);
        ipcStates.add(index,ipcState);
        this.notifyDataSetChanged();
    }




}
