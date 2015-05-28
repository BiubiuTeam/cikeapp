package com.yunkairichu.cike.adapter;

/**
 * Created by liuxiaobo on 15/5/27.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 为放置表情的GridView设置适配器
 */
public class MyGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String, Object>> list;
    int layout;
    String[] from;
    String[] fromName;
    int[] to;
    int[] toName;
//    Activity activity;


    public MyGridAdapter(Context context,
                         ArrayList<HashMap<String, Object>> list, int layout,
                         String[] from,String[] fromName, int[] to, int[] toName ) {
        super();
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.from = from;
        this.fromName = fromName;
        this.to = to;
        this.toName = toName;
//        this.activity = (Activity) context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    class ViewHolder {
        ImageView image = null;
        TextView name = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(to[0]);
            holder.name = (TextView) convertView.findViewById(toName[0]);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageResource((Integer) list.get(position).get(from[0]));
        holder.name.setText((CharSequence) list.get(position).get(fromName[0]));
        class MyGridImageClickListener implements View.OnClickListener {

            int position;

            public MyGridImageClickListener(int position) {
                super();
                this.position = position;
            }



            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /**
                 * 这里的点击函数要重写，点击后表情从右往左加载
                 */
//                    mEditTextContent.append((String)list.get(position).get("faceName"));
            }

        }
        //这里创建了一个方法内部类
        holder.image.setOnClickListener(new MyGridImageClickListener(position));


//            TextView mGridText = (TextView) convertView.findViewById(R.id.gridText);
//            mGridText.setText(faceName[position]);




        return convertView;
    }

}
