package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuxiaobo on 15/5/10.
 */
public class ActivityWelcome extends Activity{
    int[] imagesWelcome = {R.drawable.welcome2, R.drawable.welcome2,R.drawable.welcome1,R.drawable.welcome1};
    int [] imagesArrow = {R.drawable.arrow1,R.drawable.arrow2,R.drawable.arrow3,R.drawable.arrow4};
    int SIGN = 17;
    int num1 = 0;
    int num2 = 0;

    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final ImageView arrow = (ImageView) findViewById(R.id.imagearrow);
        final Button button = (Button) findViewById(R.id.imagewelcome);

//        button.setBackgroundResource(imagesWelcome[0]);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
// TODO Auto-generated method stub
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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
// TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = SIGN;
                handler.sendMessage(msg);
            }
        }, 800, 400);
    }


}




