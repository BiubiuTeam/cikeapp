package com.yunkairichu.cike.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by vida2009 on 2015/5/21.
 */
public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresumeʱ��ȡ��notification��ʾ
        //HXSDKHelper.getInstance().getNotifier().reset();

        // umeng ����
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng ����
        //MobclickAgent.onPause(this);
    }


    /**
     * ����
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
