package com.yunkairichu.cike.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.yunkairichu.cike.main.ActivityChatview;
import com.yunkairichu.cike.main.R;

/**
 * Created by haowenliang on 15/6/6.
 */
public class InfoImageView extends ImageView {

    public ChatListItemModel dataContent;

    public InfoImageView(Context context) {
        super(context);
    }

    public InfoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InfoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDataContent(ChatListItemModel dataContent) {
        this.dataContent = dataContent;
    }
}
