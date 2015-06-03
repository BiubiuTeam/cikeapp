package com.yunkairichu.cike.main;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.widget.ImageView;
/**
 * Created by liuxiaobo on 15/6/1.
 */
public class MyHalfItem extends ImageView{
    public  Object itemModel;
    public static  final int ITEM_DEFAULT_HEIGHT = (int)ToolDevice.dp2px(84);
    private static final int MAX_INDENT = 220;
    private static final String TAG = MyHalfItem.class.getSimpleName();

    public MyHalfItem(Context context) {
        super(context);
        this.initItem();
    }

    public void initItem(){
        this.setAdjustViewBounds(true);
        this.setMaxHeight(ITEM_DEFAULT_HEIGHT);
    }

    public void onDraw(Canvas canvas){
        canvas.save();
        float indent = getIndent(getY());
        //Part of the magic happens here too
        canvas.translate(indent, 0);
        super.onDraw(canvas);
        canvas.restore();
    }

    public float getIndent(float distance){
        float x_vertex = MAX_INDENT;
        distance = distance + ITEM_DEFAULT_HEIGHT/2;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        float y_vertex = displayMetrics.heightPixels  / displayMetrics.density;
        double a = (0 - x_vertex) / ( Math.pow(( 0 - y_vertex), 2) ) ;
        float indent = (float)  -(a * Math.pow((distance - y_vertex), 2) + x_vertex/2)
                + displayMetrics.widthPixels / displayMetrics.density;
        return indent;
    }
}
