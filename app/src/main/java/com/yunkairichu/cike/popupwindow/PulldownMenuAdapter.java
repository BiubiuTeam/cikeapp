package com.yunkairichu.cike.popupwindow;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by liuxiaobo on 15/5/28.
 */
public class PulldownMenuAdapter extends BaseAdapter{
    private ArrayList<PulldownMenuItem> menuItems = null;

    public PulldownMenuAdapter() {
        // TODO Auto-generated constructor stub
    }

    public PulldownMenuAdapter(ArrayList<PulldownMenuItem> menuItems){
        this.menuItems = menuItems;
    }

    @Override
    public int getCount(){
        return menuItems.size();
    }

    @Override
    public Object getItem(int position){
        return position;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        /**
         * 由于此处Item在可见屏幕中够用，所以不必缓存。
         */
        if (null == convertView){
            convertView = menuItems.get(position).getView();
        }

        return convertView;
    }
}

