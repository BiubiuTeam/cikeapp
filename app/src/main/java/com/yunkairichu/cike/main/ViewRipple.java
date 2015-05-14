package com.yunkairichu.cike.main;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by liuxiaobo on 15/5/9.
 */
public class ViewRipple extends View {
    private int mScreenWidth;
    private int mScreenHeight;
    private Bitmap mRippleBitmap;
    private Paint mRipplePaint = new Paint();
    private float mBitmapWidth;
    private float mBitmapHeight;
    private boolean isStartRipple;
    private float heightPaddingTop;
    private float heightPaddingBottom;
    private float widthPaddingLeft;
    private float widthPaddingRight;
    private RectF mRect = new RectF();
    private float rippleFirstRadius = 0;
    private float rippleSecendRadius = -50;
    private float rippleThirdRadius = -100;
    private float rippleFourthRadius = -150;
    private Paint textPaint = new Paint();
//  private String mText="点击我吧";

    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            invalidate();
            if (isStartRipple) {
                rippleFirstRadius++;
                if (rippleFirstRadius > 200) {
                    rippleFirstRadius = 0;
                }
                rippleSecendRadius++;
                if (rippleSecendRadius > 200) {
                    rippleSecendRadius = 0;
                }
                rippleThirdRadius++;
                if (rippleThirdRadius > 200) {
                    rippleThirdRadius = 0;
                }
                rippleFourthRadius++;
                if (rippleFourthRadius > 200) {
                    rippleFourthRadius = 0;
                }
                sendEmptyMessageDelayed(0, 20);
            }
        }
    };
    /**
    *@param context 上下文*/
    public ViewRipple(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * @param context 上下文
     * @param attrs 参数
     */
    public ViewRipple(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }
    /**
     * @param context 上下文
     * @param attrs 参数
     * @param defStyleAttr 样式参数
     */
    public ViewRipple(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
//        setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        mRipplePaint.setColor(Color.argb(255, 255, 255, 255));
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setStrokeWidth(12);
        textPaint.setTextSize(26);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
//        mDevice = new ToolDevice();
        mRippleBitmap = BitmapFactory.decodeStream(getResources()
                .openRawResource(R.drawable.searchbackground));
//        mRippleWidth = mDevice.getDw();
//        mRippleHeight = mDevice.getDh();
        mBitmapWidth = mRippleBitmap.getWidth();
        mBitmapHeight = mRippleBitmap.getHeight();
        startRipple();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mw = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
//        setMeasuredDimension(getMeasuredWidth(),getMeasuredHeight());
        if (mBitmapWidth < 2 * mBitmapHeight) {
            mBitmapWidth = (2 * mBitmapHeight);
        }
        setMeasuredDimension((int)mBitmapWidth,(int) mBitmapHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (isStartRipple) {
            float f1 = 3 * mBitmapHeight / 10;
            mRipplePaint.setAlpha(255);
//            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight - ToolDevice.dp2px(74),
//                    ToolDevice.dp2px(50), mRipplePaint);
            int i1 = (int) (220.0F - (220.0F - 0.0F) / 200.0F * rippleFirstRadius);
            mRipplePaint.setAlpha(i1);
            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight - ToolDevice.dp2px(74), ToolDevice.dp2px(50) + f1 * rippleFirstRadius / 50.0F,
                    mRipplePaint);

            if (rippleSecendRadius >= 0) {
                int i3 = (int) (220.0F - (220.0F - 0.0F) / 200.0F
                        * rippleSecendRadius);
                mRipplePaint.setAlpha(i3);
                canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight - ToolDevice.dp2px(74),
                        ToolDevice.dp2px(50) + f1 * rippleSecendRadius
                                / 50.0F, mRipplePaint);
            }
            if (rippleThirdRadius >= 0) {
                int i2 = (int) (220.0F - (220.0F - 0.0F) / 200.0F
                        * rippleThirdRadius);
                mRipplePaint.setAlpha(i2);
                canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight - ToolDevice.dp2px(74), ToolDevice.dp2px(50) + f1 * rippleThirdRadius / 50.0F,
                        mRipplePaint);
            }

            if (rippleFourthRadius >= 0) {
                int i4 = (int) (220.0F - (220.0F - 0.0F) / 200.0F
                        * rippleFourthRadius);
                mRipplePaint.setAlpha(i4);
                canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight - ToolDevice.dp2px(74), ToolDevice.dp2px(50) + f1 * rippleFourthRadius / 50.0F,
                        mRipplePaint);
            }
        }
//        mRipplePaint.setAlpha(30);
//        canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight, mBitmapHeight,
//                mRipplePaint);
//        mRipplePaint.setAlpha(120);
//        canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight,
//                9 * mBitmapHeight / 10, mRipplePaint);
//        mRipplePaint.setAlpha(180);
//        canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight,
//                8 * mBitmapHeight / 10, mRipplePaint);
//        mRipplePaint.setAlpha(255);
//        canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight,
//                7 * mBitmapHeight / 10, mRipplePaint);
//        float length = textPaint.measureText(mText);
//        canvas.drawText(mText, (mBitmapWidth - length) / 2,
//                mBitmapHeight * 3 / 4, textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenWidth = w;
        mScreenHeight = h;
        confirmSize();
        invalidate();
    }
    private void confirmSize() {
        int minScreenSize = Math.min(mScreenWidth, mScreenHeight);
        int widthOverSize = mScreenWidth - minScreenSize;
        int heightOverSize = mScreenHeight - minScreenSize;
        heightPaddingTop = (getPaddingTop() + heightOverSize / 2);
        heightPaddingBottom = (getPaddingBottom() + heightOverSize / 2);
        widthPaddingLeft = (getPaddingLeft() + widthOverSize / 2);
        widthPaddingRight = (getPaddingRight() + widthOverSize / 2);
        int width = getWidth();
        int height = getHeight();
        mRect = new RectF(widthPaddingLeft, heightPaddingTop, width
                - widthPaddingRight, height * 2 - heightPaddingBottom);
    }
    public void startRipple() {
        isStartRipple = true;
        handler.sendEmptyMessage(0);
    }


}
