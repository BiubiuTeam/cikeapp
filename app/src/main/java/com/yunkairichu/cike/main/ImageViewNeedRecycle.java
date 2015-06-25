package com.yunkairichu.cike.main;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by vida2009 on 2015/6/25.
 */
public class ImageViewNeedRecycle extends ImageView {
    public ImageViewNeedRecycle (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            System.out
                    .println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }
}
