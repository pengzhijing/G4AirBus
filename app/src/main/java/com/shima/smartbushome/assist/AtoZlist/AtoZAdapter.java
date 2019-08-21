package com.shima.smartbushome.assist.AtoZlist;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.shima.smartbushome.R;

public class AtoZAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;
	private int  selectItem=-1;
	private int size;
	public static final int select   = 0x7d02A3E9;
	private String[] colorarray={"5f000000", "5f000000"};
	public AtoZAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
		size=list.size();
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.atoz_listitem, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		viewHolder.tvTitle.setText(this.list.get(position).getName());
		if (position == selectItem) {
			viewHolder.tvTitle.setBackgroundColor(select);
		}
		else {
			//view.setBackgroundColor(Color.TRANSPARENT);
			viewHolder.tvTitle.setBackgroundColor(ToColor(colorarray[(position%2)]));
		}
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}
	public  void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}
	public int getSelectItem(){
		return selectItem;
	}
	public void setNextorBack(String str){
		if(str.equals("next")){
			this.selectItem++;
			if(selectItem>size-1){
				selectItem=0;
			}
		}else if(str.equals("back")){
			this.selectItem--;
			if(selectItem<0){
				selectItem=size-1;
			}
		}
	}
	public int ToColor(String data){
		int color=0;
		int rin,gin,bin,ain;
		ain=Integer.parseInt(data.substring(0,2),16);
		rin=Integer.parseInt(data.substring(2,4),16);
		gin=Integer.parseInt(data.substring(4,6),16);
		bin=Integer.parseInt(data.substring(6,8),16);
		color= Color.argb(ain, rin, gin, bin);
		return color;
	}
	public String getselectSongname(){
		return list.get(selectItem).getName();
	}
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}