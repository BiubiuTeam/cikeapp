package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.yunkairichu.cike.adapter.ScrollAdapter;
import com.yunkairichu.cike.widget.ChatListItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ActivityChatview extends Activity {

    RelativeLayout chatview = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);

        chatview = (RelativeLayout) findViewById(R.id.chatview);



//        listView.setLayoutParams(new ActionBar.LayoutParams(100, 500));
        List<ChatListItemModel> listOfModels = new ArrayList<ChatListItemModel>();

        ChatListItemModel localTmpModel = new ChatListItemModel(null,false,true);
        listOfModels.add(localTmpModel);
        listOfModels.add(localTmpModel);
        for(int i = 0; i < 50 ; i++){
            ChatListItemModel model = new ChatListItemModel(null,i%2 == 1,false);
            listOfModels.add(model);
        }
        listOfModels.add(localTmpModel);
        listOfModels.add(localTmpModel);

        final ListView listView = new HalfCircleListView(this);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setDividerHeight((int)ToolDevice.dp2px(20));
        listView.setAdapter(new ScrollAdapter(listOfModels));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin=0;
        layoutParams.leftMargin=0;
        layoutParams.rightMargin=0;
//        layoutParams.bottomMargin= (int) ToolDevice.dp2px(80.0f);
//
        int listHeight = MyHalfItem.ITEM_DEFAULT_HEIGHT * 5 + listView.getDividerHeight()*(5-1);
        layoutParams.height = listHeight;

        chatview.addView(listView, layoutParams);
    }
}
