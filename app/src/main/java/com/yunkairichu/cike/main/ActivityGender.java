package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by liuxiaobo on 15/5/9.
 * Modified by vida on 2015/5/10
 */
public class ActivityGender extends Activity {

    private Button buttonMale;
    private Button buttonFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gender);
        buttonMale = (Button) findViewById(R.id.male_button);
        buttonFemale = (Button) findViewById(R.id.female_button);

        buttonMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityGender.this, ActivityRole.class);

                Bundle bundle = new Bundle();
                bundle.putInt("genderPick", 0);  //男性为0
                i.putExtras(bundle);

                startActivity(i);
                finish();
            }
        });

        buttonFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityGender.this, ActivityRole.class);

                Bundle bundle = new Bundle();
                bundle.putInt("genderPick", 1);  //女性为1
                i.putExtras(bundle);

                startActivity(i);
                finish();
            }
        });
    }
}
