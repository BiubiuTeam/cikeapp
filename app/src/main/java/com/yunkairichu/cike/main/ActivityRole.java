package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.jaf.jcore.Application;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.jaf.jcore.ToolGetLocationInfo;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseRegister;

import org.json.JSONObject;

/**
 * Created by liuxiaobo on 15/5/9.
 * Modified by vida on 2015/5/10
 */
public class ActivityRole extends Activity {

    private Button buttonStudent;
    private Button buttonWorker;
    private ResponseRegister responseRegister = new ResponseRegister();
    private int userConfig = 0;
    private int isException = 0;

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
        setContentView(R.layout.activity_role);

        Bundle bundle = this.getIntent().getExtras();
        final int genderPick = bundle.getInt("genderPick",0);

        buttonStudent = (Button) findViewById(R.id.student_button);
        buttonWorker = (Button) findViewById(R.id.worker_button);

        buttonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userConfig |= ( genderPick <<  0 );
                userConfig |= ( 1 << 0 );  //ѧ��Ϊ0
                ThreadHXRegist threadHXRegist = new ThreadHXRegist();
                new Thread(threadHXRegist).start();
            }
        });

        buttonWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userConfig |= (genderPick << 0);
                userConfig |= (1 << 1);  //�ϰ���Ϊ1
                ThreadHXRegist threadHXRegist = new ThreadHXRegist();
                new Thread(threadHXRegist).start();
            }
        });
    }

    public void sendRegist(int userConfig) {
        Http http = new Http();
        if(System.currentTimeMillis()- ToolGetLocationInfo.getInstance().getLastRecTime()>600000){
            ToolGetLocationInfo.getInstance().startLocation();
        }
        JSONObject jo = JsonPack.buildRegister(userConfig,
                ToolGetLocationInfo.getInstance().getLastLatitude(),ToolGetLocationInfo.getInstance().getLastLongitude(),ToolGetLocationInfo.getInstance().getLastCity());
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    Toast.makeText(ActivityRole.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }

                setResponseRegister(JacksonWrapper.json2Bean(response, ResponseRegister.class));

                if(responseRegister.getStatusCode()==0) {
                    Intent i = new Intent(ActivityRole.this, ActivityBeforeSearch.class);

                    startActivity(i);
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    finish();
                }
                else {
                    Toast.makeText(ActivityRole.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setResponseRegister(ResponseRegister responseRegister){this.responseRegister = responseRegister;}

    private class ThreadHXRegist implements Runnable {
        public void run() {
            try {
                // 调用sdk注册方法
                EMChatManager.getInstance().createAccountOnServer(ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase(), "123");
            } catch (final EaseMobException e) {
                isException = 1;
                runOnUiThread(new Runnable() {
                    public void run() {
                        int errorCode=e.getErrorCode();
                        if(errorCode==EMError.NONETWORK_ERROR){
                            Toast.makeText(getApplicationContext(), "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
                        }else if(errorCode==EMError.USER_ALREADY_EXISTS){
                            ToolLog.dbg("用户已存在");
                            sendRegist(userConfig);
                        }else if(errorCode==EMError.UNAUTHORIZED){
                            Toast.makeText(getApplicationContext(), "网络异常2, 请检查网络", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "网络异常3, 请检查网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } finally {
                if(isException != 1) {
                    ToolLog.dbg("注册成功");
                    sendRegist(userConfig);
                }
            }

//            try {
//                // 调用sdk注册方法
//                EMChatManager.getInstance().createAccountOnServer(ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase(), "123");
//            } catch (final Exception e) {
//                //注册失败
//                int errorCode=((EaseMobException)e).getErrorCode();
//                if(errorCode== EMError.NONETWORK_ERROR){
//                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
//                }else if(errorCode==EMError.USER_ALREADY_EXISTS){
//                    Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
//                }else if(errorCode==EMError.UNAUTHORIZED){
//                    Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
        }
    }
}
