package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHorizontalScrollView;
import com.jaf.jcore.Application;
import com.jaf.jcore.DemoHXSDKHelper;
import com.jaf.jcore.HXSDKHelper;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.jaf.jcore.ToolGetLocationInfo;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;
import com.yunkairichu.cike.bean.ResponseUserChainPull;
import com.yunkairichu.cike.utils.PopupUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vida2009 on 2015/5/11.
 */
//TEXT
public class ActivitySquare extends Activity implements EMEventListener {
    //本类常量
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_SINGLECHAT = 19;
    public static final int REQUEST_CODE_USER_CHAIN = 101;

    public static final int RESULT_FORCE_REFLASH = 101;

    public static final int RESULT_CODE_COPY = 1;

    //ui变量
    private View view;
    private View selectView;
    private LinearLayout squareSelectButton;
    private ImageView squareBigPic;
    private ImageView squareBigPicBg;
    private ImageView squareNothingAnim;
    private TextView squareBigPicText;
    private Button sendChosStaBackButton;
    private Button squareChainButton;
    private Button squarePubTiButton;
    private MyDialog chooseStatusDialog;
    private AnimationDrawable animationDrawable;
//    private MySwitchDialog dropDownDialog;
    private PopupWindow menuWindow;
    private ResponseUserChainPull responseUserChainPull;

    //数据与逻辑变量
    private int height = 0;
    private Bitmap[] titleBitmap = new Bitmap[50]; //??????50
    private int[] wpara = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] wdirect = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int bitmapNum;
    private int tmpCnt;
    private int searchFlag = 1;
    private File cameraFile;
    private ResponseSearchTitle responseSearchTitle;
    private int isOnCreated;
    //private Timer timer;
    private TimerTask timerTask;
    private Timer timer2;
    private TimerTask timerTask2;
    private int selectorStatus = 0;
    private int selectorScale = 0;
    private int selectorGender = 0;
    private int selectorFlag = 0;
    private SimpleAdapter simpleAdapter1;
    private SimpleAdapter simpleAdapter2;
    private SimpleAdapter simpleAdapter3;
    private int menuWindowStatus = 0;
    private int titleItemLongShortClickFlag = 0;

    //popup弹窗相关声明
//    private RelativeLayout layoutHeader = null;
    private TextView tvTopic = null;
    private ImageView ivTopic = null;
    private ImageView ivTopic2 = null;

    private GridView gv1 = null;GridView gv2 = null;GridView gv3 = null;

    private String[] statusName = new String[]{"全部","失恋中", "无聊", "思考人生", "上自习", "在路上", "上班ing", "健身", "吃大餐", "自拍"};
    private String[] scale = new String[]{"全球","同城","身边"};
    private String[] gender = new String[]{"所有人","男生","女生"};
    private int[] statusNameNum = {0,1,2,3,4,5,6,8,9,10};
    private int[] scaleNum = {0,1,1};
    private int[] genderNum ={0,1,2};

    HashMap<String,Integer> statusNameMap=null;
    HashMap<String,Integer> scaleMap=null;
    HashMap<String,Integer> genderMap=null;

    //////////////////////////////////////////初始化//////////////////////////////////////////////////

    //haowenliang
    private PullToRefreshHorizontalScrollView mPullRefreshScrollView;
    private HorizontalScrollView squareScrollView;
    private GridLayout squareGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        chooseStatusDialog = new MyDialog(this);
//        dropDownDialog = new MySwitchDialog(this);

        //haowenliang
        mPullRefreshScrollView = (PullToRefreshHorizontalScrollView) findViewById(R.id.pull_refresh_horizontalscrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<HorizontalScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<HorizontalScrollView> refreshView) {
                new GetSquareDataTask().execute();
            }
        });
        squareScrollView  = mPullRefreshScrollView.getRefreshableView();
        squareGridLayout = (GridLayout) findViewById(R.id.squereGridLayout);

        squareSelectButton = (LinearLayout) findViewById(R.id.status_picker);
        squareSelectButton.setClickable(true);
        squareChainButton = (Button) findViewById(R.id.squareUserChainButton);
        squarePubTiButton = (Button) findViewById(R.id.bigbutton_add);
        squareBigPic = (ImageView) findViewById(R.id.square_big_picture);
        squareBigPicBg = (ImageView) findViewById(R.id.square_big_picture_bg);
        squareBigPicText = (TextView) findViewById(R.id.square_big_picture_text);
        LayoutInflater inflater = getLayoutInflater();
        selectView = inflater.inflate(R.layout.view_square_selector, null);
        view = squareGridLayout.getChildAt(0);
        squareNothingAnim = (ImageView) findViewById(R.id.square_nothing_anim);
        animationDrawable=(AnimationDrawable) squareNothingAnim.getBackground();

        ivTopic = (ImageView) findViewById(R.id.look_into);
        ivTopic2 = (ImageView) findViewById(R.id.picker_arrow);
        tvTopic = (TextView) findViewById(R.id.picker);

        ///////////////////////////////////////popupwindow相关//////////////////////////////////
        ArrayList<HashMap<String, Object>> statusNameList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <statusName.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("status", statusName[i]);
            statusNameList.add(map);
        }

        ArrayList<HashMap<String, Object>> scaleList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <scale.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("scale", scale[i]);
            scaleList.add(map);
        }

        ArrayList<HashMap<String, Object>> genderList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <gender.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gender", gender[i]);
            genderList.add(map);
        }

        // 设置GridView的数据源
        simpleAdapter1 = new SimpleAdapter(this, scaleList,
                R.layout.scale_item, new String[] { "scale" }, new int[] {
                R.id.scale_item });

        simpleAdapter2 = new SimpleAdapter(this, statusNameList,
                R.layout.status_selector_item, new String[] { "status" }, new int[] {
                R.id.status_text });

        simpleAdapter3 = new SimpleAdapter(this, genderList,
                R.layout.gender_item, new String[] { "gender" }, new int[] {
                R.id.gender_item });

        Bundle bundle = this.getIntent().getExtras();
        responseSearchTitle = (ResponseSearchTitle) bundle.getSerializable("resSearchTitle");

        squareNothingDisplay();

        squareChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySquare.this, ActivityChatview.class);
                clearTitleBitmap();
                //squareScrollView.removeAllViews();
                squareScrollView.setVisibility(View.GONE);
//                cancelTimer2();
                startActivityForResult(i, REQUEST_CODE_USER_CHAIN);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                ActivitySquare.this.updateChatChainButtonBeating(false);
            }
        });

        squareSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuWindow != null && menuWindow.isShowing()) {
                    menuWindow.dismiss();
                    changeImage();
                    return;
                }
                menuWindowShow();
                changeImage();
            }
        });

        //////////////////////////////////////////////////推送相关//////////////////////////////////////////////////////
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage
                , EMNotifierEvent.Event.EventNewCMDMessage, EMNotifierEvent.Event.EventDeliveryAck
                , EMNotifierEvent.Event.EventReadAck});

        if(ToolPushNewMsgInfo.getInstance().getTitleHasNewMsgCnt() > 0){
            ActivitySquare.this.updateChatChainButtonBeating(true);
        }

///////////////////////////////////////popupwindow相关 完//////////////////////////////////

        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupChooseStatus();
            }
        });
        isOnCreated = 0;

        ////////////////////////////////////// popupwindow的监控 //////////////////////////////////

//        startTimer2();

        ////////////////////////////////////// popupwindow的监控  完//////////////////////////////////

        if(responseSearchTitle != null) {
            squareNothingDisplay();
            firReFlashSearchTitle();
            getTitleBitmap();
        } else{
            if(searchFlag==1) {
                responseSearchTitle = null;
                searchFlag = 0;
                clearTitleBitmap();
                squareScrollView.scrollTo(10, 10);
                doTitleSearch();
            }
        }

        isOnCreated = 1;
    }

    /**
     * ***********************************事件响应**********************************************
     */
    private void popupChooseStatus() {
        View v = getLayoutInflater().inflate(R.layout.status_picker,
                null);

        v.findViewById(R.id.status_cancel_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseStatusDialog.dismiss();
                    }
                });
        chooseStatusDialog = PopupUtil.makeMyPopup(this, v);
        chooseStatusDialog.show();
    }

    private void changeImage() {
        if (menuWindow != null&& menuWindow.isShowing()) {
            ivTopic.setImageResource(R.drawable.look_into_white);
            ivTopic2.setImageResource(R.drawable.picker_arrow_white);
            tvTopic.setTextColor(Color.argb(255, 255, 255, 255));
            menuWindowStatus = 1;
        }
        else {
            ivTopic.setImageResource(R.drawable.look_into_black);
            ivTopic2.setImageResource(R.drawable.picker_arrow_black);
            tvTopic.setTextColor(Color.argb(255, 0, 0, 0));
        }
    }

    /**
     * 显示下拉菜单
     */
    private void menuWindowShow()
    {
        LayoutInflater layout = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) layout.inflate(
                R.layout.status_selector, null);
        gv1 = (GridView) view.findViewById(R.id.statusscale);
        gv2 = (GridView) view.findViewById(R.id.statustype);
        gv3 = (GridView) view.findViewById(R.id.usergender);

        gv1.setAdapter(simpleAdapter1);
        gv2.setAdapter(simpleAdapter2);
        gv3.setAdapter(simpleAdapter3);

        gv1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv3.setSelector(new ColorDrawable(Color.TRANSPARENT));

       //给查看区域的选项设置点击事件
        gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv1.getChildAt(selectorScale).findViewById(R.id.scale_item)).setTextColor(0xffffffff);
                ToolLog.dbg("selectorScale:" + String.valueOf(selectorScale) + " position" + String.valueOf(position));
                if(selectorScale != position){
                    selectorFlag = 1;
                }
                selectorScale = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.scale_item)).setTextColor(0xff000000);
            }
        });

        //给查看状态的选项设置点击事件
        gv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv2.getChildAt(selectorStatus).findViewById(R.id.status_text)).setTextColor(0xffffffff);
                ToolLog.dbg("selectorStatus:"+String.valueOf(selectorStatus)+" position"+String.valueOf(position));
                if(selectorStatus != position){
                    selectorFlag = 1;
                }
                selectorStatus = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.status_text)).setTextColor(0xff000000);
            }
        });

        //给查看性别的选项设置点击事件
        gv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv3.getChildAt(selectorGender).findViewById(R.id.gender_item)).setTextColor(0xffffffff);
                ToolLog.dbg("selectorGender:" + String.valueOf(selectorGender) + " position" + String.valueOf(position));
                if(selectorGender != position){
                    selectorFlag = 1;
                }
                selectorGender = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.gender_item)).setTextColor(0xff000000);
            }
        });

        menuWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        menuWindow.setBackgroundDrawable(getDrawable());
        menuWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.default_image));
//        menuWindow.setAnimationStyle(R.style.pulldown_in_out);
        menuWindow.showAtLocation(findViewById(R.id.square),
                Gravity.NO_GRAVITY, 0, (int) ToolDevice.dp2px(100.0f));
        menuWindow.setFocusable(true);
        menuWindow.setTouchable(true);
        menuWindow.setOutsideTouchable(true);
        menuWindow.update();

        menuWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /**** 如果点击了popupwindow的外部，popupwindow也会消失 ****/
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    menuWindow.dismiss();
                    changeImage();
                    //ToolLog.dbg("outside");
                    return true;
                }
                //ToolLog.dbg("not-outside");
                return false;
            }
        });
    }

    /**
     * 给menWindow生成一个 透明的背景图片
     * @return
     */
    private Drawable getDrawable(){
        ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(getResources().getColor(android.R.color.transparent));
//        bgdrawable.getPaint().setARGB(0xFF, 0xFF,0xFF,0xFF);
        return   bgdrawable;
    }

///////////////////////////////////////大事件响应/////////////////////////////////////

    /**
     * onActivityResult ACT结果回传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ToolPushNewMsgInfo.getInstance().getTitleHasNewMsgCnt() > 0){
            ActivitySquare.this.updateChatChainButtonBeating(true);
        }
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_SINGLECHAT || requestCode == REQUEST_CODE_USER_CHAIN || requestCode == REQUEST_CODE_CAMERA){
//                startTimer2();
                chooseStatusDialog.dismiss();
                squareScrollView.setVisibility(View.VISIBLE);

                if(responseSearchTitle != null) {
                    ToolLog.dbg("111");
                    squareNothingDisplay();
                    firReFlashSearchTitle();
                    getTitleBitmap();
                } else{
                    ToolLog.dbg("222");
                    if(searchFlag==1) {
                        responseSearchTitle = null;
                        searchFlag = 0;
                        clearTitleBitmap();
                        squareScrollView.scrollTo(10, 10);
                        doTitleSearch();
                    }
                }
                ToolLog.dbg("start2");
            }
        } else if(resultCode == RESULT_FORCE_REFLASH){
            if(requestCode == REQUEST_CODE_SINGLECHAT || requestCode == REQUEST_CODE_CAMERA){
//                startTimer2();
                chooseStatusDialog.dismiss();
                squareScrollView.setVisibility(View.VISIBLE);
                if(searchFlag==1) {
                    responseSearchTitle = null;
                    searchFlag = 0;
                    clearTitleBitmap();
                    squareScrollView.scrollTo(10, 10);
                    doTitleSearch();
                }
                ToolLog.dbg("start3");
            }
        }
    }

    /**
     * 事件监听（第二种接收方式）
     *
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                String username = null;
                //单聊消息
                username = message.getFrom();

                int iTitleId = 0;
                String toDeviceId = "";
                ToolLog.dbg(message.getBody().toString());
                try {
                    iTitleId = Integer.parseInt(message.getStringAttribute("broadcast"));
                    toDeviceId = message.getStringAttribute("from");
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                String key = String.valueOf(iTitleId)+toDeviceId;
                ToolPushNewMsgInfo.getInstance().putTitleNewMsgFlagValue(key, 1);

                ToolLog.dbg("square shake");
                ActivitySquare.this.refreshUIWithNewMessage(true);
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                break;
            }
            default:
                break;
        }
    }

    //新消息提醒
    public void updateChatChainButtonBeating(boolean isBeating){
        if (isBeating){
            squareChainButton.setBackgroundResource(R.drawable.oval_button);
            AnimationDrawable animationDrawable = (AnimationDrawable) squareChainButton.getBackground();
            animationDrawable.start();
        }else{
            squareChainButton.setBackgroundResource(R.drawable.btn_chatlist_selector);
        }
    }

    /**
     * *************************************子线程******************************************
     */

///////////////////////////////////////////Handler/////////////////////////////////////////////////

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            Bitmap bitmap = (Bitmap) data.getParcelable("bitmap");
            int index = data.getInt("index");
            //ToolLog.dbg("OrigCnt" + String.valueOf(bitmap.getByteCount()));
            //titleBitmap[index] = compressImage(bitmap);
            titleBitmap[index] = bitmap;
            secReFlashSearchTitle(index);
        }
    };

    private class ThreadGetSquareTitleBitmap implements Runnable {
        private ResponseSearchTitle responseSearchTitle;

        public ThreadGetSquareTitleBitmap(ResponseSearchTitle responseSearchTitle) {
            this.responseSearchTitle = responseSearchTitle;
        }

        public void run() {
            bitmapNum = 0;
            tmpCnt = 0;

            for (int i = 0; i < responseSearchTitle.getReturnData().getContData().size(); i++) {
                if (responseSearchTitle.getReturnData().getContData().get(i).getTitleType() == 2 || responseSearchTitle.getReturnData().getContData().get(i).getTitleType() == 3) {
                    bitmapNum++;
                }
            }
            for (int i = 0; i < responseSearchTitle.getReturnData().getContData().size(); i++) {
                BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
                if (baseResponseTitleInfo.getTitleType() != 2 && baseResponseTitleInfo.getTitleType() != 3) {
                    continue;
                }

                Bitmap bitmap = null;
                //先从文件里找
                String picName = baseResponseTitleInfo.getTitleCont().replace("http://biubiu.co/upload_src/", "");
                bitmap = ToolFileRW.getInstance().loadBitmapFromFile(picName);
                //如果bitmap为空，再发网络请求获取
                if(bitmap == null) {
                    ToolLog.dbg("图片不在，网络请求");
                    URL url = null;
                    try {
                        url = new URL(baseResponseTitleInfo.getTitleCont());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.setConnectTimeout(5000 * 10);
                    try {
                        conn.setRequestMethod("GET");
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (conn.getResponseCode() == 200) {
                            InputStream inputStream = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            //数据存文件
                            ToolFileRW.getInstance().saveBitmapToFile(bitmap, picName);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putParcelable("bitmap", bitmap);
                data.putInt("index", i);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }
    }

    /**
     * *************************************主逻辑*******************************************************
     */

    /////////////////////////////////////////广场瀑布流////////////////////////////////////////
    public void doTitleSearch() {
        Http http = new Http();
        int filter = 0;
        filter |= (scaleNum[selectorScale]<<0);
        filter |= (genderNum[selectorGender]<<1);
        ToolLog.dbg("filter:"+String.valueOf(filter)+" tag:"+String.valueOf(statusNameNum[selectorStatus]));
        if(System.currentTimeMillis()- ToolGetLocationInfo.getInstance().getLastRecTime()>600000){
            ToolGetLocationInfo.getInstance().startLocation();
        }
        if(ToolGetLocationInfo.getInstance().getFailFlag()==1){
            updatePullRefreshViewOnUiThread(false);
            Toast.makeText(ActivitySquare.this,"网络不太好，请稍后再试",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jo = JsonPack.buildSearchTitle(filter, statusNameNum[selectorStatus],
                ToolGetLocationInfo.getInstance().getLastLatitude(),ToolGetLocationInfo.getInstance().getLastLongitude(),ToolGetLocationInfo.getInstance().getLastCity());
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                updatePullRefreshViewOnUiThread(true);

                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponseSearchTitle(JacksonWrapper.json2Bean(response, ResponseSearchTitle.class));
                if (responseSearchTitle.getReturnData().getContData().size() == 0) {
                    searchFlag = 1;
                }
                squareNothingDisplay();
                firReFlashSearchTitle();
                getTitleBitmap();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                updatePullRefreshViewOnUiThread(false);

                Toast.makeText(Application.getInstance().getApplicationContext(), "网络不给力，请稍后再试", Toast.LENGTH_SHORT).show();
                searchFlag = 1;
            }
        });
    }

    public void getTitleBitmap() {
        ThreadGetSquareTitleBitmap threadGetSquareTitleBitmap = new ThreadGetSquareTitleBitmap(responseSearchTitle);
        new Thread(threadGetSquareTitleBitmap).start();
    }

    public void firReFlashSearchTitle() {
        if (isOnCreated == 0) {
            ViewTreeObserver vto2 = squareScrollView.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    squareScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    firPosReFlashSearchTitle(squareScrollView.getWidth(), squareScrollView.getHeight());
                }
            });
        } else {
            firPosReFlashSearchTitle(squareScrollView.getWidth(), squareScrollView.getHeight());
        }
    }

    public void firPosReFlashSearchTitle(int width, int height) {
        int lineNum = responseSearchTitle.getReturnData().getLineNum();
        int[] linePos = new int[lineNum];
        for (int i = 0; i < lineNum; i++) {
            linePos[i] = 0;
        }
        clearTitleBitmap();
        squareGridLayout.removeAllViews();
        for (int i = 0; i < responseSearchTitle.getReturnData().getContData().size(); i++) {
            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
            int k = 0;
            if(lineNum>1) {
                for (int j = 1; j < lineNum; j++) {
                    if(linePos[j] < linePos[k]) k = j;
                }
            }
            //ToolLog.dbg("AllHeight:"+String.valueOf(height)+" AllWidth:"+String.valueOf(width));
            //TODO haowenliang here
            ImageViewSquareItem iv = new ImageViewSquareItem(this, height, width, baseResponseTitleInfo.getBlockLen(), k, linePos[k], lineNum);
            iv.setTag(i);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(titleItemLongShortClickFlag==0) {
                        Intent i = new Intent(ActivitySquare.this, ActivityChat.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("titleInfo", responseSearchTitle.getReturnData().getContData().get((int) v.getTag()));
                        bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                        int iTag = (int) v.getTag();
                        if(titleBitmap[iTag]!=null) {
                            ToolLog.dbg("byteCnt:" + String.valueOf(titleBitmap[iTag].getByteCount()));
                            ByteArrayOutputStream baos = compressImage(titleBitmap[iTag]);
                            ToolLog.dbg("toByteArray:" + String.valueOf(baos.toByteArray().length));
                            i.putExtra("bitmap", baos.toByteArray());
                            i.putExtras(bundle);

                            clearTitleBitmap();
                            //squareGridLayout.removeAllViews();
                            squareScrollView.setVisibility(View.GONE);
//                            cancelTimer2();
                            startActivityForResult(i, REQUEST_CODE_SINGLECHAT);
                            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                        }
//                        finish();
                    } else{
                        titleItemLongShortClickFlag = 0;
                    }
                }
            });
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int iTag = (int) view.getTag();
                    if (titleBitmap[iTag] == null) return false;
                    BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(iTag);
                    squareBigPicBg.setVisibility(View.VISIBLE);
                    squareBigPic.setVisibility(View.VISIBLE);
                    squareBigPicText.setVisibility(View.VISIBLE);
                    squareBigPic.setImageBitmap(titleBitmap[iTag]);
                    squareBigPicText.setText(baseResponseTitleInfo.getExtension().getText());
                    titleItemLongShortClickFlag = 1;
                    return false;
                }
            });
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                            motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                            motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        squareBigPicBg.setVisibility(View.GONE);
                        squareBigPic.setVisibility(View.GONE);
                        squareBigPicText.setVisibility(View.GONE);
                        return false;
                    }
                    return false;
                }
            });
            squareGridLayout.addView(iv, 2 * i);

            TextViewSquareItem tv = new TextViewSquareItem(this,height, width, baseResponseTitleInfo.getBlockLen(), k, linePos[k], lineNum);
            tv.setText(baseResponseTitleInfo.getExtension().getText());
            squareGridLayout.addView(tv, 2 * i + 1);

            linePos[k] += baseResponseTitleInfo.getBlockLen();
        }
    }

    public void secReFlashSearchTitle(int index) {
        tmpCnt++;
        if (tmpCnt == bitmapNum) {
            searchFlag = 1;
        }

        Bitmap tmpBit = titleBitmap[index];//.copy(titleBitmap[index].getConfig(),true);
        if(((ImageViewSquareItem) squareGridLayout.getChildAt(2*index))!=null && tmpBit!=null)((ImageViewSquareItem) squareGridLayout.getChildAt(2*index)).setImageBitmap(tmpBit);
        if(((TextViewSquareItem) squareGridLayout.getChildAt(2*index+1))!=null)((TextViewSquareItem) squareGridLayout.getChildAt(2*index+1)).setVisibility(View.VISIBLE);
    }

/****************************************主逻辑 完********************************************************/

    /**
     * *************************************辅助函数*******************************************************
     */

    ////////////////////////////////////////////////ACT生存周期类/////////////////////////////////
    @Override
    protected void onResume() {
        Log.i("ChatActivity", "onResume");
        super.onResume();
        MobclickAgent.onResume(this);

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage
                , EMNotifierEvent.Event.EventDeliveryAck
                , EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop(){
        Log.i("ChatActivity", "onStop");

        // unregister this event listener when this activity enters the background
        EMChatManager.getInstance().unregisterEventListener(this);

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);

        super.onStop();
    }

    private void refreshUIWithNewMessage(final boolean isBeating){
        runOnUiThread(new Runnable() {
            public void run() {
                ActivitySquare.this.updateChatChainButtonBeating(isBeating);
            }
        });
    }

    ///////////////////////////////////////内存清理//////////////////////////////////////////
    public void clearTitleBitmap(){
        for(int i=0; i<50;i++) {
            if(titleBitmap[i]!=null) {
                if (!titleBitmap[i].isRecycled()) {
                    titleBitmap[i].recycle();   //回收图片所占的内存
                    titleBitmap[i] = null;
                }
            }
            wpara[i] = 0;
            wdirect[i] = 0;
        }
    }

    public void squareNothingDisplay(){
        if(responseSearchTitle.getReturnData().getContData() == null || responseSearchTitle.getReturnData().getContData().size() <= 0){
            squareNothingAnim.setVisibility(View.VISIBLE);
            animationDrawable.setOneShot(false);
            animationDrawable.start();
        } else {
            animationDrawable.stop();
            squareNothingAnim.setVisibility(View.GONE);
        }
    }

    //////////////////////////////////////定时器///////////////////////////////////////////
    public void startTimer2(){
        if(timer2 == null){timer2 = new Timer();}
        if(timerTask2 == null) {
            timerTask2 = new TimerTask() {
                @Override
                public void run() {
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 2;
                }
            };
        }
        //��ʼһ����ʱ����
        if(timer2 != null && timerTask2 != null){timer2.schedule(timerTask2, 2000, 500);}// 1s后执行task,经过1s再次执行)
    }

    public void cancelTimer2(){
        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }

        if (timerTask2 != null) {
            timerTask2.cancel();
            timerTask2 = null;
        }
    }

//////////////////////////////////////////get set类///////////////////////////////////////////
    public ResponseSearchTitle getResponseSearchTitle() {
        return responseSearchTitle;
    }

    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle) {
        this.responseSearchTitle = responseSearchTitle;
    }

    public ResponseUserChainPull getResponseUserChainPull() {
        return responseUserChainPull;
    }

    public void setResponseUserChainPull(ResponseUserChainPull responseUserChainPull) {
        this.responseUserChainPull = responseUserChainPull;
    }

    public HorizontalScrollView getSquareScrollView(){
        return squareScrollView;
    }

    /////////////////////////////////////////工具类///////////////////////////////////////////////
    //图像压缩
    private /*Bitmap*/ ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ������������100��ʾ��ѹ������ѹ�������ݴ�ŵ�baos��
        ToolLog.dbg("compressCnt:" + String.valueOf(baos.toByteArray().length));
        int options = 40;
        while (baos.toByteArray().length / 1024 > 35) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����35kb,���ڼ���ѹ��
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ�������ݴ�ŵ�baos��
            options -= 10;//ÿ�ζ�����10
        }
        //ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ��������baos��ŵ�ByteArrayInputStream��
        //Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream������ͼƬ
        //ToolLog.dbg("fiCompressCnt:" + String.valueOf(bitmap.getByteCount()));
        //return bitmap;
        return baos;
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

/****************************************工具类 完********************************************************/

    private class GetSquareDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here
            doTitleSearch();
            super.onPostExecute(result);
        }
    }

    private static boolean squareDataScrollToTop = false;
    public void updatePullRefreshViewOnUiThread(boolean scrollToTop) {
        squareDataScrollToTop = scrollToTop;
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                //更新UI
                if(squareDataScrollToTop)
                    squareScrollView.smoothScrollTo(0,0);
                mPullRefreshScrollView.onRefreshComplete();
            }
        });
    }
}
