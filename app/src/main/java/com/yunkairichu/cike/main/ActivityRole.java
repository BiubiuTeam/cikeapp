package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;

import org.json.JSONObject;

/**
 * Created by liuxiaobo on 15/5/9.
 * Modified by vida on 2015/5/10
 */
public class ActivityRole extends Activity {

    private Button buttonStudent;
    private Button buttonWorker;

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
                userConfig |= ( 1 << 0 );  //学生为0
                sendRegist(userConfig);

                Intent i = new Intent(ActivityRole.this, ActivityTab.class);

                startActivity(i);
                finish();
            }
        });

        buttonWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userConfig = 0;
                userConfig |= ( genderPick <<  0 );
                userConfig |= ( 1 << 1 );  //上班族为1
                sendRegist(userConfig);

                Intent i = new Intent(ActivityRole.this, ActivityTab.class);

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
            }
        });
    }
}
