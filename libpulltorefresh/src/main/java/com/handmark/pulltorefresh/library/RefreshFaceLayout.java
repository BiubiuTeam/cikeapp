package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by haowenliang on 15/6/15.
 */
public class RefreshFaceLayout extends RelativeLayout {

    static final int faceCount = 4;
    private ImageView[] faceArray;
    private TextView refreshInfo;

    static String RefreshMessage = "松\n手\n换\n一\n批";
    static String LoadingMessage = "正\n在\n加\n载";

    static int faceAnimateIndex = 0;
    static boolean faceAnimating = false;

    public RefreshFaceLayout(Context context) {
        super(context);
    }

    public RefreshFaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshFaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RefreshFaceLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onCreate();
    }

    private void onCreate(){
        Init_Face();
        Init_TextView();
    }

    private void Init_Face(){
        faceArray = new ImageView[faceCount];

        faceArray[0] = (ImageView)findViewById(R.id.refreshFace0);
        faceArray[1] = (ImageView)findViewById(R.id.refreshFace1);
        faceArray[2] = (ImageView)findViewById(R.id.refreshFace2);
        faceArray[3] = (ImageView)findViewById(R.id.refreshFace3);

        for(int i=0; i<faceCount; i++){
            faceArray[i].setBackgroundResource(R.drawable.smile_face_normal);
        }
    }

    private void Init_TextView(){
        refreshInfo = (TextView)findViewById(R.id.refreshInfoLabel);
    }

    public void startFaceAnimate(boolean resetIndex){
        if (resetIndex)
            faceAnimateIndex = 0;
        faceAnimating = true;
        refreshInfo.setText(LoadingMessage);
        animteHandler.post(animateTask);
    }

    public void stopFaceAnimate(boolean resetIndex){
        if (resetIndex)faceAnimateIndex = 0;
        refreshInfo.setText(RefreshMessage);
        faceAnimating = false;
    }

    private Handler animteHandler = new Handler(Looper.getMainLooper());

    private Runnable animateTask = new Runnable() {

        public void run() {
            for(int i=0; i<faceCount; i++){
                faceArray[i].setBackgroundResource(R.drawable.smile_face_normal);
            }
            if (faceAnimating == false)
                return;

            int index = faceAnimateIndex%faceCount;
            faceArray[index].setBackgroundResource(R.drawable.smile_face_lighted);

            if (faceAnimating) {
                animteHandler.postDelayed(this, 250);
                faceAnimateIndex++;
            }
        }
    };
}
