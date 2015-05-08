package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

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
public class ActivityTab extends Activity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab);

        button = (Button) findViewById(R.id.bigbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Http http = new Http();
                JSONObject jo = JsonPack.buildUserInfoLoad();
                http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        ToolLog.dbg("pack back");
                        if (response == null) {
                            ToolLog.dbg("server error");
                            return;
                        }
                        //ToolLog.dbg("refresh title" + response);
                        ResponseUserInfoLoad responseUserInfoLoad = JacksonWrapper.json2Bean(response,
                                ResponseUserInfoLoad.class);
                        ToolLog.dbg("json2Bean");
                        Toast.makeText(ActivityTab.this, "latitue:" + String.valueOf(responseUserInfoLoad.getReturnData()
                                .getLatitude()) + ",longitude:" + String.valueOf(responseUserInfoLoad.getReturnData()
                                .getLongitude()), Toast.LENGTH_SHORT).show();
                        ToolLog.dbg("toast show");
                    }
                });
            }
        });
    }


}

