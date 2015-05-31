package com.yunkairichu.cike.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;

/**
 * Created by vida2009 on 2015/5/29.
 */
public class ImageViewSquareItem extends ImageView {
    private Paint paint;
    private int roundWidth = 5;
    private int roundHeight = 5;
    private Paint paint2;

    public ImageViewSquareItem(Context context, int parentHeight, int parentWidth, int BlockLen, int rowSpec, int colSpec, int lineNum) {
        super(context);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(rowSpec);
        params.columnSpec = GridLayout.spec(colSpec, BlockLen);
        params.setGravity(Gravity.FILL);
        params.height = parentHeight/lineNum-12;
        params.width = parentWidth/lineNum*BlockLen-12;
        params.bottomMargin = 6;
        params.topMargin = 6;
        params.leftMargin = 6;
        params.rightMargin = 6;
        this.setLayoutParams(params);
        //this.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        Animation animation = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.picture_move_2);
//        this.startAnimation(animation);

        //this.setAnimation(animation);
        //animation.start();
        //this.setBackgroundResource(R.drawable.circle_corner);

        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

//        if(attrs != null) {
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
//            roundWidth= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth);
//            roundHeight= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight);
//        }else {
            float density = context.getResources().getDisplayMetrics().density;
            roundWidth = (int) (roundWidth*density);
            roundHeight = (int) (roundHeight*density);
//        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawLiftUp(canvas2);
        drawRightUp(canvas2);
        drawLiftDown(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        //bitmap.recycle();
    }

    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(
                        0,
                        0,
                        roundWidth * 2,
                        roundHeight * 2),
                -90,
                -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight()-roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(
                        0,
                        getHeight()-roundHeight*2,
                        0+roundWidth*2,
                        getHeight()),
                90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth()-roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight()-roundHeight);
        path.arcTo(new RectF(
                getWidth()-roundWidth*2,
                getHeight()-roundHeight*2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth()-roundWidth, 0);
        path.arcTo(new RectF(
                        getWidth()-roundWidth*2,
                        0,
                        getWidth(),
                        0+roundHeight*2),
                -90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }
}
