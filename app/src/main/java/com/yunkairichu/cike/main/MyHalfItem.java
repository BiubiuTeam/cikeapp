package com.yunkairichu.cike.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.widget.ImageView;
/**
 * Created by liuxiaobo on 15/6/1.
 */
public class MyHalfItem extends ImageView{
    public int position;
    public Object itemModel;
    public static  final int ITEM_DEFAULT_HEIGHT = (int)ToolDevice.dp2px(60);

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
        float x_vertex = maxIndent();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//        distance = distance - ITEM_DEFAULT_HEIGHT;
        float y_vertex = displayMetrics.heightPixels  / displayMetrics.density;
        double a = (0 - x_vertex) / ( Math.pow(( 0 - y_vertex), 2) ) ;
        float indent = (float)  -(a * Math.pow((distance - y_vertex), 2) + x_vertex/2);
        return indent;
    }

    public float maxIndent()
    {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels/(3*displayMetrics.density);
    }

    public void setModelWithGendarAndHeartbeat(boolean isLocal, boolean isFemale ,boolean heartbeat){
        if (isLocal) {
            //transparent image source
            this.setImageResource(R.drawable.transparent_avatar);
        } else{
            if (isFemale) {
                if (heartbeat) {
                    this.setImageResource(R.drawable.animate_female);

                    AnimationDrawable animationDrawable = (AnimationDrawable) this.getDrawable();
                    animationDrawable.start();
                }else{
                    this.setImageResource(R.drawable.female_avatar);
                }
            }else {
                if (heartbeat){
                    this.setImageResource(R.drawable.animate_male);

                    AnimationDrawable animationDrawable = (AnimationDrawable) this.getDrawable();
                    animationDrawable.start();
                }else {
                    this.setImageResource(R.drawable.male_avatar);
                }
            }
        }

        this.invalidate();
    }
}
