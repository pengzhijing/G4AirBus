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

public class SearchListAdapter extends BaseItemDraggableAdapter {

    private Context context;

    public SearchListAdapter(Context context, List<VStarCamera> searchList) {
        super(R.layout.view_search_camera_item, searchList);
        this.context=context;
    }


    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        VStarCamera vStarCamera=(VStarCamera) item;
        String name=vStarCamera.getName();
        String id=vStarCamera.getId();

        helper.setText(R.id.tv_searchName,name);
        helper.setText(R.id.tv_searchId,id);


        helper.addOnClickListener(R.id.layout_ipc);
    }






}
