package com.yunkairichu.cike.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

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

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
//            ToolLog.dbg("trying to use a recycled bitmap chatview");
        }
    }
}
