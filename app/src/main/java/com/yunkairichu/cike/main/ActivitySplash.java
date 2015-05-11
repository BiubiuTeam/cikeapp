package com.yunkairichu.cike.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jaf.jcore.BindableActivity;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseUserInfoLoad;

import org.json.JSONObject;


/**
 * Created by liuxiaobo on 15/5/8.
 */
public class ActivitySplash extends BindableActivity {
    private static final long SPLASH_DELAY = 1000;
    private final Handler mHandler = new Handler();
    private Runnable mDelayStart;
    private ResponseUserInfoLoad responseUserInfoLoad = new ResponseUserInfoLoad();

    @Override
    protected int onLoadViewResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewDidLoad(Bundle savedInstanceState) {
        mDelayStart = new Runnable() {

            @Override
            public void run() {
                start();
            }
        };

        splash();
    }

    private void splash() {
        mHandler.postDelayed(mDelayStart, SPLASH_DELAY);
    }

    private void start() {
        mHandler.removeCallbacks(mDelayStart);
        checkRegist();
//        overridePendingTransition(android.R.anim.fade_in,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(this);
    }

    public void setResponseUserInfoLoad(ResponseUserInfoLoad responseUserInfoLoad){this.responseUserInfoLoad = responseUserInfoLoad;}


    public void checkRegist() {
        Http http = new Http();
        JSONObject jo = JsonPack.buildUserInfoLoad();
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }
                //ToolLog.dbg("refresh title" + response);
                setResponseUserInfoLoad(JacksonWrapper.json2Bean(response, ResponseUserInfoLoad.class));

                Intent i;
                if(responseUserInfoLoad.getStatusCode() == 1){
                    i = new Intent(ActivitySplash.this, ActivityWelcome.class);
                }
                else if(responseUserInfoLoad.getStatusCode() == 0){
                    i = new Intent(ActivitySplash.this, ActivityBeforeSearch.class);
                }
                else{
                    i = new Intent(ActivitySplash.this, ActivityWelcome.class);    //其他情况暂时也重新注册
                }
                startActivity(i);
                finish();
            }
        });
    }
}
