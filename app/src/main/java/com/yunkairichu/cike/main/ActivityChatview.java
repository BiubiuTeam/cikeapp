package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yunkairichu.cike.adapter.ScrollAdapter;

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
        List<String> listOfStrings = new ArrayList<String>();
        for(int i = 0; i < 10 ; i++){
            listOfStrings.add("Value " + i);
        }

//        listView.setBackgroundColor(Color.argb(255,255,220,50));



        ListView listView = new HalfCircleListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(30);
        listView.setAdapter(new ScrollAdapter(listOfStrings));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin=0;
        layoutParams.leftMargin=0;
        layoutParams.rightMargin=0;
        layoutParams.bottomMargin= (int) ToolDevice.dp2px(80.0f);

        chatview.addView(listView, layoutParams);
    }
}
