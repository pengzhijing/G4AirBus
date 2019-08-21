package com.shima.smartbushome.assist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.shima.smartbushome.R;
import com.shima.smartbushome.database.SaveArea;

import java.util.List;

public class AreaAdapter extends BaseAdapter {

    private Context context;
    private List<SaveArea> areaList;

    public AreaAdapter(Context context, List<SaveArea> areaList) {
        this.context = context;
        this.areaList = areaList;
    }

    @Override
    public int getCount() {
        return areaList.size();
    }

    @Override
    public Object getItem(int position) {
        return areaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView)
        {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.adapter_draggable_grid_item3, null);

            viewHolder.tv_areaName = (TextView) convertView.findViewById(R.id.textView12);
            viewHolder.iv_areaBackground = (ImageView) convertView.findViewById(R.id.iv_roomBackground);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SaveArea area=areaList.get(position);
        viewHolder.tv_areaName.setText(area.getArea_name());
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(10);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(500, 500);
        //加载区域背景图片
        Glide.with(context)
                .load(area.getArea_bg())
                .apply(options)
                .into(viewHolder.iv_areaBackground);


        return convertView;
    }

    private static class ViewHolder
    {
        TextView tv_areaName;
        ImageView iv_areaBackground;

    }

}
