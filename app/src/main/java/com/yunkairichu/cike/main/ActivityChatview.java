package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.yunkairichu.cike.adapter.ScrollAdapter;
import com.yunkairichu.cike.widget.ChatListItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ActivityChatview extends Activity {

    private Button cancelButton = null;
    RelativeLayout chatview = null;
    private ImageView detailImage = null;
    private ListView listView = null;

    private static int listViewHeight = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);

        chatview = (RelativeLayout) findViewById(R.id.chatview);
        cancelButton = (Button)findViewById(R.id.chatview_cancel_btn);

        //ui instance
        listView = new HalfCircleListView(this);
        listView.setDivider(null);
        listView.setOverScrollMode(2);//OVER_SCROLL_NEVER = 2

        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setDividerHeight((int) ToolDevice.dp2px(30));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = 0;
        layoutParams.leftMargin = dm.widthPixels/2;

        layoutParams.width = dm.widthPixels/2;

        listViewHeight = MyHalfItem.ITEM_DEFAULT_HEIGHT * 5 + listView.getDividerHeight()*(5-1);
        layoutParams.height = listViewHeight;
        chatview.addView(listView, layoutParams);

        detailImage = (ImageView)findViewById(R.id.detailPicture);
        RelativeLayout.LayoutParams infoLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoLP.leftMargin = (dm.widthPixels/2 - detailImage.getWidth())/2;
        infoLP.topMargin = listViewHeight/2 - detailImage.getHeight();

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityChatview.this,"Chat",Toast.LENGTH_SHORT).show();
            }
        });
        detailImage.setLayoutParams(infoLP);

        chatview.bringChildToFront(cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //data source
        List<Object> listOfData = new ArrayList<Object>();
        for(int i = 0; i < 6; i++){
            listOfData.add(new Object());
        }
        this.updateListViewWithDataList(listOfData);
    }

    public void updateListViewWithDataList(List<Object> dataList){
        List<ChatListItemModel> listOfModels = new ArrayList<ChatListItemModel>();
        int dataSize = dataList.size();
        if (dataSize > 0) {
            ChatListItemModel localTmpModel = new ChatListItemModel(null, false, true);
            listOfModels.add(localTmpModel);
            listOfModels.add(localTmpModel);

            for (int i = 0; i < dataSize; i++) {
                Object obj = dataList.get(i);
                ChatListItemModel model = new ChatListItemModel(obj, i % 2 == 1, false);
                listOfModels.add(model);
            }

            listOfModels.add(localTmpModel);
            listOfModels.add(localTmpModel);
        }

        listView.setAdapter(new ScrollAdapter(this,listOfModels));
    }

    public void clickAvatarAtIndex(ChatListItemModel model){
        //get the model, and show up

        Toast.makeText(this,"Nice",Toast.LENGTH_SHORT).show();

        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.female_avatar);
        detailImage.setImageBitmap(bmp);
        detailImage.setVisibility(View.VISIBLE);


    }
}
