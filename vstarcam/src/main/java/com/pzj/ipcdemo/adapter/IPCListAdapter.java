package com.pzj.ipcdemo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pzj.ipcdemo.R;
import com.pzj.ipcdemo.entity.VStarCamera;


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

        switch (ipcStates.get(helper.getAdapterPosition())){
            case PPPP_STATUS_CONNECTING : // 连接中
                helper.setText(R.id.tv_status,"连接中");
                break;
            case PPPP_STATUS_INITIALING : // 已连接，正在初始化
                helper.setText(R.id.tv_status,"正在初始化");
                break;
            case PPPP_STATUS_ON_LINE : // 在线
                helper.setText(R.id.tv_status,"在线");
                break;
            case PPPP_STATUS_CONNECT_FAILED : // 连接失败
                helper.setText(R.id.tv_status,"连接失败");
                break;
            case PPPP_STATUS_DISCONNECT : // / 连接已关闭
                helper.setText(R.id.tv_status,"连接已关闭");
                break;
            case PPPP_STATUS_INVALID_ID :// 无效 UID
                helper.setText(R.id.tv_status,"无效 UID");
                break;
            case PPPP_STATUS_DEVICE_NOT_ON_LINE : // 不在线
                helper.setText(R.id.tv_status,"不在线");
                break;
            case PPPP_STATUS_CONNECT_TIMEOUT : // 连接超时
                helper.setText(R.id.tv_status,"连接超时");
                break;
            case PPPP_STATUS_WRONGUSER_RIGHTPWD : // 密码错误 ..
            case PPPP_STATUS_WRONGPWD_RIGHTUSER : // 密码错误. .
            case PPPP_STATUS_WRONGPWD_WRONGUSER : // 密码错误. .
                helper.setText(R.id.tv_status,"密码错误");
                break;
                default:
                    helper.setText(R.id.tv_status,"未知");

        }


        helper.addOnClickListener(R.id.iv_ipcDelete);
        helper.addOnClickListener(R.id.iv_item);
        helper.addOnLongClickListener(R.id.iv_item);
    }


    public List<Integer> getIpcStates() {
        return ipcStates;
    }

    public void setIpcStates(List<Integer> ipcStates) {
        this.ipcStates = ipcStates;
        this.notifyDataSetChanged();
    }

    public void setIpcStatesIndex(int ipcState,int index) {
        ipcStates.remove(index);
        ipcStates.add(index,ipcState);
        this.notifyDataSetChanged();
    }
}
