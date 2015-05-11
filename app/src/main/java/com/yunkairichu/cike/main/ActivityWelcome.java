package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuxiaobo on 15/5/10.
 */
public class ActivityWelcome extends Activity{
    private int[] imagesWelcome = {R.drawable.welcome2, R.drawable.welcome2,R.drawable.welcome1,R.drawable.welcome1};
    private int [] imagesArrow = {R.drawable.arrow1,R.drawable.arrow2,R.drawable.arrow3,R.drawable.arrow4};
    private int SIGN = 17;
    private int num1 = 0;
    private int num2 = 0;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private Button button;
    private ImageView arrow;
    private TextView textView;

    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        arrow = (ImageView) findViewById(R.id.imagearrow);
        button = (Button) findViewById(R.id.imagewelcome);
        textView = (TextView) findViewById(R.id.textwelcome);

        button.setBackgroundResource(imagesWelcome[0]);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SIGN) {
                    arrow.setImageResource(imagesArrow[num1++]);
                    button.setBackgroundResource(imagesWelcome[num2++]);
                    if (num1 >= imagesArrow.length) {
                        num1 = 0;
                    }
                    if (num2 >= imagesWelcome.length) {
                        num2 = 0;
                    }
                }
            }
        };

        startTimer();

        button.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int initX, initY,ll,rr,bb,tt;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();
                switch (ea) {
                    case MotionEvent.ACTION_DOWN:
                        cancelTimer();
                        lastX = (int) event.getRawX();// 获取触摸事件触摸位置的原始X坐标
                        lastY = (int) event.getRawY();
                        initX = lastX;
                        initY = lastY;
                        ll = v.getLeft();
                        bb = v.getBottom();
                        rr = v.getRight();
                        tt = v.getTop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int maxY = ((int) event.getRawY() - initY >= 200) ? initY + 200 - lastY : (int) event.getRawY() - lastY;
                        int dy = ((int) event.getRawY() <= initY) ? 0 : maxY;
                        int l = v.getLeft();
                        int b = v.getBottom() + dy;
                        int r = v.getRight();
                        int t = v.getTop() + dy;
                        v.layout(l, t, r, b);

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        if((int) event.getRawY()-initY >= 200){
                            button.setVisibility(View.INVISIBLE);
                            arrow.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.INVISIBLE);
                            Intent i = new Intent(ActivityWelcome.this, ActivityGender.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            v.layout(ll, tt, rr, bb);
                            v.postInvalidate();
                            startTimer();
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void startTimer(){
        if(timer == null){timer = new Timer();}
        if(timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = SIGN;
                    handler.sendMessage(msg);
                }
            };
        }
        //开始一个定时任务
        if(timer != null && timerTask != null){timer.schedule(timerTask, 800, 400);}
    }

    public void cancelTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        button.setBackgroundResource(imagesWelcome[0]);
        arrow.setImageResource(imagesArrow[3]);
    }
}




