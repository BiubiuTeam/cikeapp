package com.yunkairichu.cike.adapter;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunkairichu.cike.main.ActivityChatview;
import com.yunkairichu.cike.main.MyHalfItem;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolLog;
import com.yunkairichu.cike.main.ToolPushNewMsgInfo;
import com.yunkairichu.cike.widget.ChatListItemModel;

import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ScrollAdapter extends BaseAdapter {

    private List<ChatListItemModel> modelList;
    private static final String TAG = ScrollAdapter.class.getSimpleName();
    private ActivityChatview activity;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;

    public ScrollAdapter(ActivityChatview activity, List<ChatListItemModel> modelList){
        this.activity = activity;
        this.modelList = modelList;
    }

    Handler handler = new Handler() {
        private void refreshList() {
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                default:
                    break;
            }
        }
    };

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    @Override
    public int getCount() {
        //ToolLog.dbg("modelListLen:"+String.valueOf(modelList.size()));
        if(modelList!=null){
            return modelList.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatListItemModel getItem(int i) {
        //ToolLog.dbg("modelListLen:" + String.valueOf(modelList.size()));
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private int mChildCount = 0;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ToolLog.dbg("userChain itemnum:"+String.valueOf(i));
        if (view == null) {
            ToolLog.dbg("bbc");
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
                    ToolLog.dbg("before clickAvatarAtIndex");
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
        if (isLocal) {
            //transparent image source
            ((MyHalfItem) view).setImageResource(R.drawable.transparent_avatar);
        } else{
            if (isFemale)
                ((MyHalfItem) view).setImageResource(R.drawable.female_avatar);
            else
                ((MyHalfItem) view).setImageResource(R.drawable.male_avatar);
        }
        if(itemModel.baseResponseUserChainInfo!=null) {
//            ToolLog.dbg("baseResponseUserChainInfo not null");
            String key = String.valueOf(itemModel.baseResponseUserChainInfo.getTitleInfo().getTitleId()) + itemModel.baseResponseUserChainInfo.getTitleInfo().getDvcId();
            ToolLog.dbg("titleId:" + String.valueOf(key) + " values:" + String.valueOf(ToolPushNewMsgInfo.getInstance().getTitleNewMsgFlagValue(key)));
            if (ToolPushNewMsgInfo.getInstance().getTitleNewMsgFlagValue(key) > 0) {
                currentView.setModelWithGendarAndHeartbeat(isLocal, isFemale, true);;
            } else {
                currentView.setModelWithGendarAndHeartbeat(isLocal, isFemale, false);
            }
        } else {
//            ToolLog.dbg("baseResponseUserChainInfo is null");
            currentView.setModelWithGendarAndHeartbeat(isLocal, isFemale, false);
        }

        return currentView;
    }
}

