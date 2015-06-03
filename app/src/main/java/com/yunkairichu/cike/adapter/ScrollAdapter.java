package com.yunkairichu.cike.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaf.jcore.Application;
import com.yunkairichu.cike.main.ActivityChatview;
import com.yunkairichu.cike.main.MyHalfItem;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.widget.ChatListItemModel;

import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ScrollAdapter extends BaseAdapter {

    private List<ChatListItemModel> scrollViews;
    private static final String TAG = ScrollAdapter.class.getSimpleName();

    public ScrollAdapter(List<ChatListItemModel> scrollViews){

        this.scrollViews = scrollViews;
    }
    @Override
    public int getCount() {
        return scrollViews.size();
    }

    @Override
    public ChatListItemModel getItem(int i) {
        return scrollViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new MyHalfItem(viewGroup.getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send object link with this view
                    MyHalfItem clickView = (MyHalfItem)v;
                    ChatListItemModel model = (ChatListItemModel)clickView.itemModel;

                    //how?
                    
                }
            });
        }

        MyHalfItem currentView = (MyHalfItem) view;
        ChatListItemModel itemModel = getItem(i);
        currentView.itemModel = itemModel;

        boolean isLocal = itemModel.isLocalTmp;
        if (isLocal) {
            //transparent image source
            ((MyHalfItem) view).setImageResource(R.drawable.female_avatar);
        } else{
            boolean isFemale = itemModel.isFemale;
            if (isFemale)
                ((MyHalfItem) view).setImageResource(R.drawable.female_avatar);
            else
                ((MyHalfItem) view).setImageResource(R.drawable.male_avatar);
        }
        return currentView;
    }
}

