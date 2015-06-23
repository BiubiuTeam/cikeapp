package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.jaf.jcore.Application;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.jaf.jcore.ToolGetLocationInfo;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;

import org.json.JSONObject;

/**
 * Created by liuxiaobo on 15/5/8.
 */
public class ActivityBeforeSearch extends Activity {

    private Button button;
    private View searching;
    private ResponseSearchTitle responseSearchTitle = new ResponseSearchTitle();
    private ImageView cikeLabel;
    private AnimationDrawable animationDrawable;

    private int isLogin=1; //不校验了，每次都发一次登录，但不管是否成功

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = getLayoutInflater();
        searching = inflater.inflate(R.layout.activity_search, null);

        setContentView(R.layout.activity_before_search);

        button = (Button) findViewById(R.id.bigbutton);
        cikeLabel = (ImageView) findViewById(R.id.boringing);
        animationDrawable=(AnimationDrawable) cikeLabel.getBackground();
        animationDrawable.start();

        ToolPushNewMsgInfo.getInstance().initTitleNewMsg();
        ToolFileRW.getInstance().initTitleNewMsg();
        ToolFileRW.getInstance().mkdirBitmapFile();
        ThreadDelBitmap threadDelBitmap = new ThreadDelBitmap();
        new Thread(threadDelBitmap).start();
        HXLogin();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin!=1){
                    HXLogin();
                    Toast.makeText(ActivityBeforeSearch.this, "网络不太好，请稍后再点击", Toast.LENGTH_SHORT).show();
                } else{
                    ((ViewRipple)searching.findViewById(R.id.search_view_ripple)).startRipple();

                    Application.getInstance().setUserName(ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase());
                    setContentView(searching);
                    Http http = new Http();
                    if(System.currentTimeMillis()- ToolGetLocationInfo.getInstance().getLastRecTime()>600000){
                        ToolGetLocationInfo.getInstance().startLocation();
                    }
                    JSONObject jo = JsonPack.buildSearchTitle(0, 0,
                            ToolGetLocationInfo.getInstance().getLastLatitude(),ToolGetLocationInfo.getInstance().getLastLongitude(),ToolGetLocationInfo.getInstance().getLastCity());
                    http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
                        @Override
                        public void onResponse(JSONObject response) {
                            super.onResponse(response);
                            if (response == null) {
                                ToolLog.dbg("server error");
                                return;
                            }

                            setResponseSearchTitle(JacksonWrapper.json2Bean(response, ResponseSearchTitle.class));
                            //写一份到缓存文件里
                            ToolFileRW.getInstance().saveSquareToFile(response, Constant.CIKEAPPSQUAREDATA);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((ViewRipple)searching.findViewById(R.id.search_view_ripple)).stopRipple();
                                    Intent i = new Intent(ActivityBeforeSearch.this, ActivitySquare.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                                    i.putExtras(bundle);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                                    finish();
                                }
                            }, 2000);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);
                            ToolLog.dbg("BAD NETWORK:" + error.toString());
                            JSONObject jo = null;
                            jo = ToolFileRW.getInstance().loadSquareFromFile(Constant.CIKEAPPSQUAREDATA);
                            if(jo != null){
                                setResponseSearchTitle(JacksonWrapper.json2Bean(jo, ResponseSearchTitle.class));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ViewRipple)searching.findViewById(R.id.search_view_ripple)).stopRipple();
                                        Intent i = new Intent(ActivityBeforeSearch.this, ActivitySquare.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                                        i.putExtras(bundle);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                                        finish();
                                    }
                                }, 2000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ViewRipple) searching.findViewById(R.id.search_view_ripple)).stopRipple();
                                        //searching.setVisibility(View.GONE);
                                        searching = null;
                                        Intent i = new Intent(ActivityBeforeSearch.this, ActivityBeforeSearch.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                    });
                }
            }
        });
    }

    private class ThreadDelBitmap implements Runnable {
        public void run() {
            ToolFileRW.getInstance().delBitmapFromFile();
        }
    }

    public void HXLogin(){
        ToolLog.dbg("stLogin");
        EMChatManager.getInstance().login(ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase(), "123",new EMCallBack() {//�ص�
            @Override
            public void onSuccess() {
                isLogin = 1;
                runOnUiThread(new Runnable() {
                    public void run() {
                        //EMGroupManager.getInstance().loadAllGroups();
                        //EMChatManager.getInstance().loadAllConversations();
                        ToolLog.dbg("login success");

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                ToolLog.dbg("loging");
            }

            @Override
            public void onError(int code, String message) {
                ToolLog.dbg("login fail");
            }
        });
    }

    public ResponseSearchTitle getResponseSearchTitle(){return responseSearchTitle;}

    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle){this.responseSearchTitle = responseSearchTitle;}
}

