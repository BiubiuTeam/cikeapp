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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;
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
public class ActivitySquare extends Activity {
    //本类常量
    public static final int REQUEST_CODE_CAMERA = 18;

    public static final int RESULT_CODE_COPY = 1;

    //ui变量
    private HorizontalScrollView squareScrollView;
    private GridLayout squareGridLayout;
    private View view;
    private View selectView;
    private LinearLayout squareSelectButton;
    private ImageView sendChosStaLoveButton;
    private ImageView sendChosStaBoringButton;
    private ImageView sendChosStaThinkLifeButton;
    private ImageView sendChosStaSelfStudyButton;
    private ImageView sendChosStaOnTheWayButton;
    private ImageView sendChosStaOnWorkButton;
    private ImageView sendChosStaBodyBuildingButton;
    private ImageView sendChosStaBigMealButton;
    private ImageView sendChosStaSelfShotButton;
    private Button sendChosStaBackButton;
    private Button squareChainButton;
    private Button squarePubTiButton;
    private MyDialog chooseStatusDialog;
//    private MySwitchDialog dropDownDialog;
    private PopupWindow menuWindow;

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
    private SquareOnTouchListener sqListener = new SquareOnTouchListener();
    private int isOnCreated;
    private Timer timer = new Timer();
    private Handler handler2;

    //popup弹窗相关声明
//    private RelativeLayout layoutHeader = null;
    private TextView tvTopic = null;
    private ImageView ivTopic = null;
    private ImageView ivTopic2 = null;

    private GridView gv1 = null;GridView gv2 = null;GridView gv3 = null;

    private String[] statusName = new String[]{"全部","失恋中", "无聊", "思考人生", "上自习", "在路上", "上班ing", "健身", "吃大餐", "自拍"};
    private String[] scale = new String[]{"全球","同城","身边"};
    private String[] gender = new String[]{"所有人","男生","女生"};
    HashMap<String,Integer> statusNameMap=null;
    HashMap<String,Integer> scaleMap=null;
    HashMap<String,Integer> genderMap=null;

    //////////////////////////////////////////初始化//////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        chooseStatusDialog = new MyDialog(this);
//        dropDownDialog = new MySwitchDialog(this);
        squareScrollView = (HorizontalScrollView) findViewById(R.id.squereHScrollView);
        squareGridLayout = (GridLayout) findViewById(R.id.squereGridLayout);
        squareSelectButton = (LinearLayout) findViewById(R.id.status_picker);
        squareSelectButton.setClickable(true);
        squareChainButton = (Button) findViewById(R.id.squareUserChainButton);
        squarePubTiButton = (Button) findViewById(R.id.bigbutton_add);
        LayoutInflater inflater = getLayoutInflater();
        selectView = inflater.inflate(R.layout.view_square_selector, null);
        view = squareGridLayout.getChildAt(0);
        squareScrollView.setOnTouchListener(sqListener);

        ivTopic = (ImageView) findViewById(R.id.look_into);
        ivTopic2 = (ImageView) findViewById(R.id.picker_arrow);
        tvTopic = (TextView) findViewById(R.id.picker);

        Bundle bundle = this.getIntent().getExtras();
        responseSearchTitle = (ResponseSearchTitle) bundle.getSerializable("resSearchTitle");

        squareChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySquare.this, ActivityChatview.class);
                startActivity(i);
                finish();
            }
        });

//        squareSelectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dropDownDialog != null&& dropDownDialog.isShowing()) {
//                    dropDownDialog.dismiss();
//                    return;
//                }
//                dropDownSelector();
//                changeImage();
//            }
//        });

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



        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupChooseStatus();
            }
        });

        isOnCreated = 0;

        for (int i = 0; i < 50; i++) {
            if (titleBitmap[i] != null) {
                if (!titleBitmap[i].isRecycled()) {
                    titleBitmap[i].recycle();   //回收图片所占的内存
                }
            }
        }

        handler2 = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    for(int i=0;i<50;i++){
                        if(titleBitmap[i]!=null){
                            ImageViewSquareItem ivsi = (ImageViewSquareItem) squareGridLayout.getChildAt(i);
                            if(ivsi.getWidth() >= 1 && ivsi.getHeight() >= 1) {
                                if (titleBitmap[i].getWidth() - ivsi.getWidth() <= 0) {
                                    ivsi.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    Bitmap bitmapTmp = Bitmap.createBitmap(titleBitmap[i], 0, wpara[i], titleBitmap[i].getWidth(), ivsi.getHeight());
                                    ivsi.setImageBitmap(bitmapTmp);
                                    if (titleBitmap[i].getHeight() - ivsi.getHeight() > 0) {
                                        if (wdirect[i] == 0) wpara[i]++;
                                        else wpara[i]--;
                                        if (wpara[i] == 0) wdirect[i] = 0;
                                        if (wpara[i] + ivsi.getHeight() >= titleBitmap[i].getHeight())
                                            wdirect[i] = 1;
                                    } else {
                                        wpara[i] = 0;
                                        wdirect[i] = 0;
                                    }
                                } else {
                                    ivsi.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    int bitMapHeightStart = titleBitmap[i].getHeight() / 2 - ivsi.getHeight() / 2;
                                    Bitmap bitmapTmp = Bitmap.createBitmap(titleBitmap[i], wpara[i], bitMapHeightStart, ivsi.getWidth(), ivsi.getHeight());
                                    ivsi.setImageBitmap(bitmapTmp);
                                    if (titleBitmap[i].getWidth() - ivsi.getWidth() > 0) {
                                        if (wdirect[i] == 0) wpara[i]++;
                                        else wpara[i]--;
                                        if (wpara[i] == 0) wdirect[i] = 0;
                                        if (wpara[i] + ivsi.getWidth() >= titleBitmap[i].getWidth())
                                            wdirect[i] = 1;
                                    } else {
                                        wpara[i] = 0;
                                        wdirect[i] = 0;
                                    }
                                }
                            }
                        }
                    }
                }
                super.handleMessage(msg);
            };
        };
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = 1;
                handler2.sendMessage(message);
            }
        };

        timer.schedule(task,0,80); // 1s后执行task,经过1s再次执行

        firReFlashSearchTitle();
        getTitleBitmap();

        isOnCreated = 1;
    }


    /**
     * ***********************************事件响应׽***********************************************
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
//
//
//    private void dropDownSelector() {
//        View v = getLayoutInflater().inflate(R.layout.status_selector,
//                null);
//
////        v.findViewById(R.id.status_cancel_btn).setOnClickListener(
////                new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        chooseStatusDialog.dismiss();
////                    }
////                });
//        dropDownDialog = PopupUtil.makeMySwitchPopup(this, v);
//        dropDownDialog.show();
//    }
//
    private void changeImage() {
        if (menuWindow != null&& menuWindow.isShowing()) {
            ivTopic.setImageResource(R.drawable.look_into_white);
            ivTopic2.setImageResource(R.drawable.picker_arrow_white);
            tvTopic.setTextColor(Color.argb(255,255,255,255));
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
        SimpleAdapter simpleAdapter1 = new SimpleAdapter(this, scaleList,
                R.layout.scale_item, new String[] { "scale" }, new int[] {
                R.id.scale_item });
        gv1.setAdapter(simpleAdapter1);

        SimpleAdapter simpleAdapter2 = new SimpleAdapter(this, statusNameList,
                R.layout.status_selector_item, new String[] { "status" }, new int[] {
                R.id.status_text });
        gv2.setAdapter(simpleAdapter2);

        SimpleAdapter simpleAdapter3 = new SimpleAdapter(this, genderList,
                R.layout.gender_item, new String[] { "gender" }, new int[] {
                R.id.gender_item });
        gv3.setAdapter(simpleAdapter3);

        gv1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv3.setSelector(new ColorDrawable(Color.TRANSPARENT));

       //给查看区域的选项设置点击事件
        gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;

                }
            }
        });

        //给查看状态的选项设置点击事件
        gv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:

                        break;
                    case 8:

                        break;
                    case 9:

                        break;
                }
            }
        });

        //给查看性别的选项设置点击事件
        gv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        });
        menuWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        menuWindow.setBackgroundDrawable(getDrawable());
//        menuWindow.setAnimationStyle(R.style.pulldown_in_out);
        menuWindow.showAtLocation(findViewById(R.id.square),
                Gravity.NO_GRAVITY, 0, (int) ToolDevice.dp2px(100.0f));
        menuWindow.setFocusable(true);
        menuWindow.setTouchable(true);
        menuWindow.setOutsideTouchable(true);
        menuWindow.update();

    }

    /**
     * 给menWindow生成一个 透明的背景图片
     * @return
     */
    private Drawable getDrawable(){
        ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(getResources().getColor(android.R.color.transparent));
        return   bgdrawable;
    }


//以下是vida的代码，保持不动即可

    /**
     * ***********************************侦听函数׽***********************************************
     */

    class SquareOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    // ��������¼������������ݣ�����ScrollView�Ѿ������ײ�������һ�����
                    if (/*sqListener != null
                            && view != null
                            &&*/ squareScrollView.getChildAt(0).getMeasuredWidth() <= view.getWidth() + view.getScrollX()) {
                        if (searchFlag == 1) {
                            searchFlag = 0;
                            for (int i = 0; i < 50; i++) {
                                if (titleBitmap[i] != null) {
                                    if (!titleBitmap[i].isRecycled()) {
                                        titleBitmap[i].recycle();   //回收图片所占的内存
                                        titleBitmap[i] = null;
                                    }
                                }
                                wpara[i] = 0;
                                wdirect[i] = 0;
                            }
                            squareScrollView.scrollTo(10, 10);
                            doTitleSearch();
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

///////////////////////////////////////大事件响应/////////////////////////////////////

    /**
     * onActivityResult ACT结果回传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // �����Ϣ
            if (requestCode == REQUEST_CODE_CAMERA) { // ������Ƭ
                ToolLog.dbg("Take Pic Finish");
//                if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
//                    choseStatusPopupWindow.dismiss();
//                    return;
//                }
//                if (cameraFile != null && cameraFile.exists())
//                    sendPicRes(cameraFile);
                chooseStatusDialog.dismiss();

                if(searchFlag==1) {
                    searchFlag = 0;
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
                    squareScrollView.scrollTo(10, 10);
                    doTitleSearch();
                }
            }
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
            ToolLog.dbg("OrigCnt" + String.valueOf(bitmap.getByteCount()));
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
                Bitmap bitmap = null;
                try {
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
        JSONObject jo = JsonPack.buildSearchTitle(0, 0);
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponseSearchTitle(JacksonWrapper.json2Bean(response, ResponseSearchTitle.class));
                firReFlashSearchTitle();
                getTitleBitmap();
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
            linePos[i] = 1;
        }
        squareGridLayout.removeAllViews();
        for (int i = 0; i < responseSearchTitle.getReturnData().getContData().size(); i++) {
            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
            int k = i % lineNum;
            ToolLog.dbg("bLen:" + String.valueOf(baseResponseTitleInfo.getBlockLen()));
            ImageViewSquareItem iv = new ImageViewSquareItem(this, height, width, baseResponseTitleInfo.getBlockLen(), k + 1, linePos[k], lineNum);
            iv.setTag(i);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ActivitySquare.this, ActivityChat.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("titleInfo", responseSearchTitle.getReturnData().getContData().get((int) v.getTag()));
                    bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                    int iTag = (int) v.getTag();
                    ToolLog.dbg("byteCnt:" + String.valueOf(titleBitmap[iTag].getByteCount()));
                    ByteArrayOutputStream baos = compressImage(titleBitmap[iTag]);
                    i.putExtra("bitmap", baos.toByteArray());
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            });
            squareGridLayout.addView(iv, i);

            TextViewSquareItem tv = new TextViewSquareItem(this,height, width, baseResponseTitleInfo.getBlockLen(), k+1, linePos[k], lineNum);
            tv.setText(baseResponseTitleInfo.getExtension().getText());
            //tv.setText("hehe");
            squareGridLayout.addView(tv);

            linePos[k] += baseResponseTitleInfo.getBlockLen();
        }
    }

    public void secReFlashSearchTitle(int index) {
        tmpCnt++;
        if (tmpCnt == bitmapNum) {
            searchFlag = 1;
        }

        ((ImageViewSquareItem) squareGridLayout.getChildAt(index)).setImageBitmap(titleBitmap[index]);
    }

/****************************************主逻辑 完********************************************************/

    /**
     * *************************************辅助函数*******************************************************
     */

//////////////////////////////////////////get set类///////////////////////////////////////////
    public ResponseSearchTitle getResponseSearchTitle() {
        return responseSearchTitle;
    }

    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle) {
        this.responseSearchTitle = responseSearchTitle;
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

}
