package com.yunkairichu.cike.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.widget.ChatEmoji;
import com.yunkairichu.cike.widget.FaceConversionUtil;

import java.util.List;

/**
 * Created by haowenliang on 15/6/9.
 * @文件描述	: 表情填充器
 */

public class FaceAdapter extends BaseAdapter {

    private List<ChatEmoji> data;

    private LayoutInflater inflater;

    private int size=0;

    public FaceAdapter(Context context, List<ChatEmoji> list) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEmoji emoji=data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.chat_grid_item, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.gridImage);
            viewHolder.iv_name = (TextView)convertView.findViewById(R.id.gridText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        if(TextUtils.isEmpty(emoji.getFaceName()) ) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
            viewHolder.iv_name.setText(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
            viewHolder.iv_name.setText(emoji.getFaceName());
        }

        return convertView;
    }

    class ViewHolder
    {
        public ImageView iv_face = null;
        public TextView iv_name = null;
    }
}
