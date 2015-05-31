package com.yunkairichu.cike.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Created by liuxiaobo on 15/5/8.
 */
public class ActivityTab extends Activity {

    private Button button;
    private ImageView mView;
    private FrameLayout mContainer;
    private int mIndex;
    private Animator.AnimatorListener animatorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_before_search);

        button = (Button) findViewById(R.id.bigbutton);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Http http = new Http();
//                JSONObject jo = JsonPack.buildUserInfoLoad();
//                http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        super.onResponse(response);
//                        ToolLog.dbg("pack back");
//                        if (response == null) {
//                            ToolLog.dbg("server error");
//                            return;
//                        }
//                        //ToolLog.dbg("refresh title" + response);
//                        ResponseUserInfoLoad responseUserInfoLoad = JacksonWrapper.json2Bean(response,
//                                ResponseUserInfoLoad.class);
//                        ToolLog.dbg("json2Bean");
//                        Toast.makeText(ActivityTab.this, "latitue:" + String.valueOf(responseUserInfoLoad.getReturnData()
//                                .getLatitude()) + ",longitude:" + String.valueOf(responseUserInfoLoad.getReturnData()
//                                .getLongitude()), Toast.LENGTH_SHORT).show();
//                        ToolLog.dbg("toast show");
//                    }
//                });
//            }
//        });
//
//        mContainer = new FrameLayout(this);
//        mContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
//                FrameLayout.LayoutParams.FILL_PARENT));
//
        mView = createNewView();
//        mContainer.addView(mView);
//
//        setContentView(mContainer);
//        animatorListener = new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        };

//Note: in order to use the ViewPropertyAnimator like this add the following import:
//  import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
        animate(button).setDuration(2000).rotationYBy(720).x(100).y(100);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mView, "rotationX", 0, 360),
                ObjectAnimator.ofFloat(mView, "rotationY", 0, 180),
                ObjectAnimator.ofFloat(mView, "rotation", 0, -90),
                ObjectAnimator.ofFloat(mView, "translationX", 0, 90),
                ObjectAnimator.ofFloat(mView, "translationY", 0, 90),
                ObjectAnimator.ofFloat(mView, "scaleX", 1, 1.5f),
                ObjectAnimator.ofFloat(mView, "scaleY", 1, 0.5f),
                ObjectAnimator.ofFloat(mView, "alpha", 1, 0.25f, 1)
        );
        set.setDuration(5 * 1000).start();
    }

    /////////////////////////////////////////////动画相关///////////////////////////////////////////////
    private ImageView createNewView() {
        ImageView ret = new ImageView(this);
        ret.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        ret.setScaleType(ImageView.ScaleType.FIT_XY);
        ret.setImageResource(PHOTOS[mIndex]);
        mIndex = (mIndex + 1 < PHOTOS.length) ? mIndex + 1 : 0;

        return ret;
    }
//
    private static final int[] PHOTOS = new int[] { R.drawable.boring,
            R.drawable.love, R.drawable.ontheway, R.drawable.selfshot,
            R.drawable.bigmeal, R.drawable.selfstudy };
//
//    private void nextAnimation() {
//        AnimatorSet anim = new AnimatorSet();
//        final int index = new Random().nextInt(4);
//
//        switch (index) {
//            case 0:
//                anim.playTogether(
//                        ObjectAnimator.ofFloat(mView, "scaleX", 1.5f, 1f),
//                        ObjectAnimator.ofFloat(mView, "scaleY", 1.5f, 1f));
//                break;
//
//            case 1:
//                anim.playTogether(ObjectAnimator.ofFloat(mView, "scaleX", 1, 1.5f),
//                        ObjectAnimator.ofFloat(mView, "scaleY", 1, 1.5f));
//                break;
//
//            case 2:
//                AnimatorProxy.wrap(mView).setScaleX(1.5f);
//                AnimatorProxy.wrap(mView).setScaleY(1.5f);
//                anim.playTogether(ObjectAnimator.ofFloat(mView, "translationY",
//                        80f, 0f));
//                break;
//
//            case 3:
//            default:
//                AnimatorProxy.wrap(mView).setScaleX(1.5f);
//                AnimatorProxy.wrap(mView).setScaleY(1.5f);
//                anim.playTogether(ObjectAnimator.ofFloat(mView, "translationX", 0f,
//                        40f));
//                break;
//        }
//
//        anim.setDuration(3000);
//        //anim.addListener();
//        anim.start();
//    }
//
//    public void onAnimationEnd(Animator animator) {
//        mContainer.removeView(mView);
//        mView = createNewView();
//        mContainer.addView(mView);
//        nextAnimation();
//    }
//
//    @Override
//    protected void onResume(){
//        nextAnimation();
//    }
}

