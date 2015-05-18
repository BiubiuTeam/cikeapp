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
                int userConfig = 0;
                userConfig |= ( genderPick <<  0 );
                userConfig |= ( 1 << 0 );  //ѧ��Ϊ0
                sendRegist(userConfig);

                Intent i = new Intent(ActivityRole.this, ActivityBeforeSearch.class);

                startActivity(i);
                finish();
            }
        });

        buttonWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userConfig = 0;
                userConfig |= ( genderPick <<  0 );
                userConfig |= ( 1 << 1 );  //�ϰ���Ϊ1
                sendRegist(userConfig);

                Intent i = new Intent(ActivityRole.this, ActivityBeforeSearch.class);

                startActivity(i);
                finish();
            }
        });
    }

    public void sendRegist(int userConfig) {
        Http http = new Http();
        JSONObject jo = JsonPack.buildRegister(userConfig);
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponseRegister(JacksonWrapper.json2Bean(response, ResponseRegister.class));

                if(responseRegister.getStatusCode()==0) {
                    ThreadHXRegist threadHXRegist = new ThreadHXRegist();
                    new Thread(threadHXRegist).start();
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
            } catch (final Exception e) {
                //注册失败
                int errorCode=((EaseMobException)e).getErrorCode();
                if(errorCode== EMError.NONETWORK_ERROR){
                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                }else if(errorCode==EMError.USER_ALREADY_EXISTS){
                    Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
                }else if(errorCode==EMError.UNAUTHORIZED){
                    Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
