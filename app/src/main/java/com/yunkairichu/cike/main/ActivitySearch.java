package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by liuxiaobo on 15/5/9.
 */
public class ActivitySearch extends Activity {

    ViewRipple viewRipple;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewRipple = new ViewRipple(this);
        setContentView(R.layout.activity_search);
        viewRipple.startRipple();

    }
}
