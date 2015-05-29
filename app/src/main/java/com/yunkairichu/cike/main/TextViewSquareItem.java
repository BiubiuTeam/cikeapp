package com.yunkairichu.cike.main;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * Created by vida2009 on 2015/5/29.
 */
public class TextViewSquareItem extends TextView {
    public TextViewSquareItem(Context context, int parentHeight, int parentWidth, int BlockLen, int rowSpec, int colSpec, int lineNum) {
        super(context);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(rowSpec);
        params.columnSpec = GridLayout.spec(colSpec, BlockLen);
        params.height = parentHeight/lineNum-12;
        params.width = parentWidth/lineNum-12;
        params.bottomMargin = 6;
        params.topMargin = 6;
        params.leftMargin = 6;
        params.rightMargin = 6;
        this.setLayoutParams(params);
        this.setGravity(Gravity.CENTER);
        this.setTextColor(0xFFFFFFFF);
    }
}
