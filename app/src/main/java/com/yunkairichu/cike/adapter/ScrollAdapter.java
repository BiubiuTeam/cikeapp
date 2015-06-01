package com.yunkairichu.cike.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunkairichu.cike.main.MyHalfItem;
import com.yunkairichu.cike.main.R;

import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ScrollAdapter extends BaseAdapter {

    private List<String> scrollViews;
    private static final String TAG = ScrollAdapter.class.getSimpleName();

    public ScrollAdapter(List<String> scrollViews){

        this.scrollViews = scrollViews;
    }
    @Override
    public int getCount() {
        return scrollViews.size();
    }

    @Override
    public String getItem(int i) {
        return scrollViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = new MyHalfItem(viewGroup.getContext());
        }


        MyHalfItem currentView = (MyHalfItem) view;
        String itemViewType = getItem(i);
        Log.d(TAG, itemViewType);
//        ((MyView) view).setLayoutParams(new ActionBar.LayoutParams(50,50));
        ((MyHalfItem) view).setImageResource(R.drawable.male_avatar);
//        ((MyView) view).setMaxHeight(30);
//        ((MyView) view).setMinimumHeight(10);
        return currentView;
    }
}

