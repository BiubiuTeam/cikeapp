package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by liuxiaobo on 15/5/9.
 */
public class ActivityGender extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gender);
    }
}
