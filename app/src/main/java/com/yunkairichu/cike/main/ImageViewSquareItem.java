package com.yunkairichu.cike.main;

import android.content.Context;
import android.graphics.Canvas;
import android.view.Gravity;
import android.widget.GridLayout;

import com.flaviofaria.kenburnsview.KenBurnsView;

/**
 * Created by vida2009 on 2015/5/29.
 */
public class ImageViewSquareItem extends KenBurnsView {

    public ImageViewSquareItem(Context context, int parentHeight, int parentWidth, int BlockLen, int rowSpec, int colSpec, int lineNum) {
        super(context);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(rowSpec);
        params.columnSpec = GridLayout.spec(colSpec, BlockLen);
        params.setGravity(Gravity.FILL);
        params.height = parentHeight/lineNum-12;
        params.width = parentWidth/lineNum*BlockLen-12;

        ToolLog.dbg("pheight:"+String.valueOf(params.height)+" pWidth:"+String.valueOf(params.width));

        params.bottomMargin = 6;
        params.topMargin = 6;
        params.leftMargin = 6;
        params.rightMargin = 6;
        this.setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
//            ToolLog.dbg("trying to use a recycled bitmap");
        }
    }
}
