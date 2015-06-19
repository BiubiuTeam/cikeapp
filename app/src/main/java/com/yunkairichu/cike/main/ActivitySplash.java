package com.yunkairichu.cike.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.jaf.jcore.Application;
import com.jaf.jcore.BindableActivity;
import com.jaf.jcore.DemoHXSDKHelper;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseUserInfoLoad;
import com.yunkairichu.cike.utils.HXMessageUtils;

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
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        checkRegist();
//        overridePendingTransition(android.R.anim.fade_in,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.updateOnlineConfig(this);
//        MobclickAgent.setDebugMode(true);


        HXMessageUtils msgUtils = (HXMessageUtils)HXMessageUtils.getInstance();
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper)DemoHXSDKHelper.getInstance();
        msgUtils.setmContext(sdkHelper.appContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
//        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(this);
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
                Application.getInstance().splashConectTime = 0;
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
                    i = new Intent(ActivitySplash.this, ActivityWelcome.class);    //其他也暂定是注册
                }
                startActivity(i);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                Application.getInstance().splashConectTime++;
                ToolLog.dbg("BAD NETWORK:" + error.toString());
                if(Application.getInstance().splashConectTime<=10){
                    if(Application.getInstance().splashConectTime % 3 == 1) {
                        Toast.makeText(Application.getInstance().getApplicationContext(), "网络不给力，请稍等", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Application.getInstance().getApplicationContext(), "无网络，请稍后再启动", Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(Application.getInstance().splashConectTime<=10) {
                            Intent i = new Intent(ActivitySplash.this, ActivitySplash.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                            finish();
                        }
                    }
                }, 2000);
            }
        });
    }
}
