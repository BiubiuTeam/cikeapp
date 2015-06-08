package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Context;
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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.yunkairichu.cike.adapter.ScrollAdapter;
import com.yunkairichu.cike.widget.ChatListItemModel;
import com.yunkairichu.cike.widget.InfoImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ActivityChatview extends Activity {

    private Button cancelButton = null;
    RelativeLayout chatview = null;
    private InfoImageView detailImage = null;
    private ListView listView = null;
    private LinearLayout topLayout = null;
    private RelativeLayout topLeftLayout = null;

    private static int listViewHeight = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);

        chatview = (RelativeLayout) findViewById(R.id.chatview);

        cancelButton = (Button)findViewById(R.id.chatview_cancel_btn);

        topLayout = (LinearLayout)findViewById(R.id.chatview_topLayout);
        topLeftLayout = (RelativeLayout)findViewById(R.id.chatview_info_holder);

        detailImage = (InfoImageView)findViewById(R.id.detailPicture);

        //ui instance
        listView = new HalfCircleListView(this);
        listView.setDivider(null);
        listView.setOverScrollMode(2);//OVER_SCROLL_NEVER = 2
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setDividerHeight((int) ToolDevice.dp2px(30));

        listViewHeight = MyHalfItem.ITEM_DEFAULT_HEIGHT * 5 + listView.getDividerHeight()*(5-1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout.LayoutParams topInfoP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topInfoP.height = listViewHeight;
        topInfoP.weight = 2;
        topInfoP.width = 2*dm.widthPixels/3;
        topLeftLayout.setLayoutParams(topInfoP);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = dm.widthPixels/3;
        layoutParams.weight = 1;
        layoutParams.height = listViewHeight;
        topLayout.addView(listView, layoutParams);

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do anything you want with datacontent
                InfoImageView iiv = (InfoImageView)v;
                ChatListItemModel model = iiv.dataContent;

                if(model.isFemale){
                    Toast.makeText(ActivityChatview.this,"Female Chatty",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ActivityChatview.this,"Male Chatty",Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public void clickAvatarAtIndex(ChatListItemModel model,int index){
        //get the model, and show up the image
        detailImage.setVisibility(View.VISIBLE);
        detailImage.dataContent = model;

        //show up the image base on data content you set
        Resources res = getResources();
        if (model.isFemale) {
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.female_avatar);
            detailImage.setImageBitmap(bmp);
        }else{
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.male_avatar);
            detailImage.setImageBitmap(bmp);
        }

        listView.setSelection(index-2);
    }
}
