package com.yunkairichu.cike.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunkairichu.cike.main.ActivityChatview;
import com.yunkairichu.cike.main.MyHalfItem;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolLog;
import com.yunkairichu.cike.widget.ChatListItemModel;

import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ScrollAdapter extends BaseAdapter {

    private List<ChatListItemModel> modelList;
    private static final String TAG = ScrollAdapter.class.getSimpleName();
    private ActivityChatview activity;

    public ScrollAdapter(ActivityChatview activity, List<ChatListItemModel> modelList){
        this.activity = activity;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        ToolLog.dbg("modelListLen:"+String.valueOf(modelList.size()));
        if(modelList!=null){
            return modelList.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatListItemModel getItem(int i) {
        ToolLog.dbg("modelListLen:"+String.valueOf(modelList.size()));
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ToolLog.dbg("userChain itemnum:"+String.valueOf(i));
        if (view == null) {
            view = new MyHalfItem(viewGroup.getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send object link with this view
                    MyHalfItem clickView = (MyHalfItem)v;
                    ChatListItemModel model = (ChatListItemModel)clickView.itemModel;
                    if (model.isLocalTmp){
                        return;
                    }
                    activity.clickAvatarAtIndex(model,clickView.position);
                }
            });
        }

        MyHalfItem currentView = (MyHalfItem) view;
        ChatListItemModel itemModel = getItem(i);
        currentView.itemModel = itemModel;
        currentView.position = i;

        boolean isLocal = itemModel.isLocalTmp;
        boolean isFemale = itemModel.isFemale;
        currentView.setModelWithGendarAndHeartbeat(isLocal,isFemale,true);

        return currentView;
    }
}

