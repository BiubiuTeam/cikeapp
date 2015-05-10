package com.yunkairichu.cike.main;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by liuxiaobo on 15/5/9.
 */
public class ActivitySearch extends Activity {

    RippleView rippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rippleView = new RippleView(this);
        setContentView(R.layout.activity_search);
        rippleView.startRipple();

    }
}
