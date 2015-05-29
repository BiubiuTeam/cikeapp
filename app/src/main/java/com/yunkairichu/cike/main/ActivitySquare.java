package com.yunkairichu.cike.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;
import com.yunkairichu.cike.popupwindow.ConstantCategoryMenu;
import com.yunkairichu.cike.popupwindow.DeviceUtility;
import com.yunkairichu.cike.popupwindow.MenuUtility;
import com.yunkairichu.cike.popupwindow.PulldownMenuView;
import com.yunkairichu.cike.utils.CommonUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by vida2009 on 2015/5/11.
 */
public class ActivitySquare extends Activity{
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
//    private PopupWindow choseStatusPopupWindow;
    private Dialog chooseStatusDialog;
    private Dialog dropDownDialog;

    //popup弹窗相关声明
    // PulldownMenuView基本操作类
    private MenuUtility menuUtility = null;
    // PulldownMenuView对象
    private PulldownMenuView pullDownMenu = null;
    private RelativeLayout layoutHeader = null;
    private TextView tvTopic = null;
    private ImageView ivTopic = null;
    private ImageView ivTopic2 = null;

    //数据与逻辑变量
    private int height = 0;


    //��ݼ��߼������
    private Bitmap[] titleBitmap = new Bitmap[50]; //??????50
    private int bitmapNum;
    private int tmpCnt;
    private int searchFlag = 1;
    private int sendMsgTag = -1;
    private File cameraFile;
    private ResponseSearchTitle responseSearchTitle;
    private SquareOnTouchListener sqListener = new SquareOnTouchListener();
    private SendChoStaOnClickListener sendChoStaOnClickListener = new SendChoStaOnClickListener();

    //////////////////////////////////////////初始化//////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        squareScrollView = (HorizontalScrollView) findViewById(R.id.squereHScrollView);
        squareGridLayout = (GridLayout) findViewById(R.id.squereGridLayout);

        squareChainButton = (Button) findViewById(R.id.squareUserChainButton);
        squarePubTiButton = (Button) findViewById(R.id.bigbutton_add);
        LayoutInflater inflater = getLayoutInflater();
        selectView = inflater.inflate(R.layout.view_square_selector, null);
        view = squareGridLayout.getChildAt(0);
        squareScrollView.setOnTouchListener(sqListener);

        Bundle bundle = this.getIntent().getExtras();
        responseSearchTitle = (ResponseSearchTitle)bundle.getSerializable("resSearchTitle");

        squareChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySquare.this, ActivityTab.class);
                startActivity(i);
                finish();
            }
        });

//        squareSelectButton.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  setContentView(selectView);
//              }
//         });

//        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
//                choseStatusPopupWindow.dismiss();
//                return;
//            } else {
//                initmPopupWindowView();
//                choseStatusPopupWindow.showAsDropDown(v, 0, 5);
//            }
//            }
//        }



//
//        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (chooseStatusDialog != null&& chooseStatusDialog.isShowing()) {
//                    chooseStatusDialog.dismiss();
//                    return;
//                } else {
//                    popupChooseStatus();
//                }
//            }
//        });

        firReFlashSearchTitle();
        getTitleBitmap();

        // 初始化
        initViews();

//        /**
//         * 为状态Map添加数据
//         */
//        for (int i = 0; i < statusId.length; i++) {
//            statusMap.put(statusName[i], statusId[i]);
//        }
//
//        addStatusData();

    }


    protected void initViews(){
        ivTopic = (ImageView) findViewById(R.id.look_into);
        tvTopic = (TextView) findViewById(R.id.picker);
        ivTopic2 = (ImageView) findViewById(R.id.picker_arrow);


        squareSelectButton = (LinearLayout) findViewById(R.id.status_picker);
        squareSelectButton.setClickable(true);
        squareSelectButton.setOnClickListener(TopicOnClickListener);
        layoutHeader = (RelativeLayout) findViewById(R.id.abovepopup);
//        layoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
//        layoutBody = (FrameLayout) findViewById(R.id.layout_body);

        height = DeviceUtility.getScreenSize(this)[1] -DeviceUtility.getStatusBarHeight(this);

        menuUtility = new MenuUtility(
                ActivitySquare.this,
                ConstantCategoryMenu.newsImageRes,
                ConstantCategoryMenu.newsMenuTexts,
                height,layoutHeader);
    }

    protected void showPulldownMenu(){
        pullDownMenu = menuUtility.getPulldownMenuView((String)tvTopic.getText());
        ivTopic.setImageResource(R.drawable.look_into_white);
        ivTopic2.setImageResource(R.drawable.picker_arrow_white);
    }

    protected void hidePulldownMenu(){
        pullDownMenu.releasePopupMenuView();
        ivTopic.setImageResource(R.drawable.look_into_black);
        ivTopic2.setImageResource(R.drawable.picker_arrow_black);
    }

/**************************************事件响应׽************************************************/

    private View.OnClickListener TopicOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // ��ʼ��ʾ�����˵�
            showPulldownMenu();

            // TODO Auto-generated method stub
            pullDownMenu.setOnMenuItemClickListener(new PulldownMenuView.OnMenuItemClickListener() {
                @Override
                public void onMenuItemClick(AdapterView<?> parent, View view, int position) {
                    // TODO Auto-generated method stub
//                    tvTopic.setText(ConstantCategoryMenu.newsMenuTexts[position]);
//                    layoutBody.setBackgroundResource(ConstantCategoryMenu.newsBodyRes[position]);
                }

                @Override
                public void hideMenu() {
                    // TODO Auto-generated method stub
                    hidePulldownMenu();
                }
            });

            pullDownMenu.show();
        }
    };

//    private void popupChooseStatus() {
//        View v = getLayoutInflater().inflate(R.layout.status_picker,
//                null);
//
//        v.findViewById(R.id.status_cancel).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        chooseStatusDialog.dismiss();
//                    }
//                });
//
//        chooseStatusDialog = PopupUtil.makePopup(this, v);
//        chooseStatusDialog.show();
//    }

    //��ʼ����������
//    public void initmPopupWindowView() {
//        // // ��ȡ�Զ��岼���ļ�pop.xml����ͼ
//        View customView = getLayoutInflater().inflate(R.layout.menu_choose_status,
//                null, false);
//        // ����PopupWindowʵ��
//        choseStatusPopupWindow = new PopupWindow(customView, 800, 960);
//        // ���ö���Ч�� [R.style.AnimationFade ���Լ����ȶ���õ�]
//        //popupWindow.setAnimationStyle(R.style.anim_take_pic);
//        // �Զ���view��Ӵ����¼�
//        customView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (choseStatusPopupWindow != null && choseStatusPopupWindow.isShowing()) {
//                    choseStatusPopupWindow.dismiss();
//                    choseStatusPopupWindow = null;
//                }
//
//                return false;
//            }
//        });
//
//        /** ���������ʵ���Զ�����ͼ�Ĺ��� */
//        sendChosStaLoveButton = (ImageView) customView.findViewById(R.id.btn_send_status_love);
//        sendChosStaBoringButton = (ImageView) customView.findViewById(R.id.btn_send_status_boring);
//        sendChosStaThinkLifeButton = (ImageView) customView.findViewById(R.id.btn_send_status_thinklife);
//        sendChosStaSelfStudyButton = (ImageView) customView.findViewById(R.id.btn_send_status_selfstudy);
//        sendChosStaOnTheWayButton = (ImageView) customView.findViewById(R.id.btn_send_status_ontheway);
//        sendChosStaOnWorkButton = (ImageView) customView.findViewById(R.id.btn_send_status_onwork);
//        sendChosStaBodyBuildingButton = (ImageView) customView.findViewById(R.id.btn_send_status_bodybuilding);
//        sendChosStaBigMealButton = (ImageView) customView.findViewById(R.id.btn_send_status_bigmeal);
//        sendChosStaSelfShotButton = (ImageView) customView.findViewById(R.id.btn_send_status_selfshot);
//
//        sendChosStaBackButton = (Button) customView.findViewById(R.id.btn_send_status_back);
//
//        sendChosStaLoveButton.setTag(R.id.tag_msg_tag, Constant.LOVE);
//        sendChosStaLoveButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaBoringButton.setTag(R.id.tag_msg_tag, Constant.BORING);
//        sendChosStaBoringButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaThinkLifeButton.setTag(R.id.tag_msg_tag, Constant.THINKLIFE);
//        sendChosStaThinkLifeButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaSelfStudyButton.setTag(R.id.tag_msg_tag, Constant.SELFSTUDY);
//        sendChosStaSelfStudyButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaOnTheWayButton.setTag(R.id.tag_msg_tag, Constant.ONTHEWAY);
//        sendChosStaOnTheWayButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaOnWorkButton.setTag(R.id.tag_msg_tag, Constant.ONWORK);
//        sendChosStaOnWorkButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaBodyBuildingButton.setTag(R.id.tag_msg_tag, Constant.BODYBUILDING);
//        sendChosStaBodyBuildingButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaBigMealButton.setTag(R.id.tag_msg_tag, Constant.BIGMEAL);
//        sendChosStaBigMealButton.setOnClickListener(sendChoStaOnClickListener);
//        sendChosStaSelfShotButton.setTag(R.id.tag_msg_tag, Constant.SELFSHOT);
//        sendChosStaSelfShotButton.setOnClickListener(sendChoStaOnClickListener);
//
//        sendChosStaBackButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
//                    choseStatusPopupWindow.dismiss();
//                    return;
//                }
//            }
//        });
//    }

//以下是vida的代码，保持不动即可
/**************************************侦听函数׽************************************************/

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
                        if(searchFlag==1) {
                            searchFlag = 0;
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

    class SendChoStaOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!CommonUtils.isExitsSdcard()) {
                String st = getResources().getString(R.string.sd_card_does_not_exist);
                Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                return;
            }
//
//            sendMsgTag = (int)view.getTag(R.id.tag_msg_tag);
//            cameraFile = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
//                    + System.currentTimeMillis() + ".jpg");
//            cameraFile.getParentFile().mkdirs();
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                    REQUEST_CODE_CAMERA);

            sendMsgTag = (int)view.getTag(R.id.tag_msg_tag);
            Intent i = new Intent(ActivitySquare.this, ActivityTakePhoto.class);
            Bundle bundle = new Bundle();
            bundle.putInt("msgTag", sendMsgTag);
            i.putExtras(bundle);
            startActivityForResult(i, REQUEST_CODE_CAMERA);
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
                sendMsgTag = -1;
//                if (cameraFile != null && cameraFile.exists())
//                    sendPicRes(cameraFile);
            }
        }
    }

/****************************************子线程*******************************************/

///////////////////////////////////////////Handler/////////////////////////////////////////////////

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            Bundle data = msg.getData();
            Bitmap bitmap = (Bitmap)data.getParcelable("bitmap");
            int index = data.getInt("index");
            ToolLog.dbg("OrigCnt"+String.valueOf(bitmap.getByteCount()));
            //titleBitmap[index] = compressImage(bitmap);
            titleBitmap[index] = bitmap;
            secReFlashSearchTitle(index);
        }
    };

    private class ThreadGetSquareTitleBitmap implements Runnable {
        private ResponseSearchTitle responseSearchTitle;

        public ThreadGetSquareTitleBitmap(ResponseSearchTitle responseSearchTitle){
            this.responseSearchTitle = responseSearchTitle;
        }

        public void run() {
            bitmapNum = 0;
            tmpCnt=0;
            for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
                if(responseSearchTitle.getReturnData().getContData().get(i).getTitleType()==2||responseSearchTitle.getReturnData().getContData().get(i).getTitleType()==3){bitmapNum++;}
            }
            for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++) {
                BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
                if(baseResponseTitleInfo.getTitleType()!=2&&baseResponseTitleInfo.getTitleType()!=3){continue;}
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

/****************************************主逻辑********************************************************/

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

    public void getTitleBitmap(){
        ThreadGetSquareTitleBitmap threadGetSquareTitleBitmap =new ThreadGetSquareTitleBitmap(responseSearchTitle);
        new Thread(threadGetSquareTitleBitmap).start();
    }

    public void firReFlashSearchTitle(){
        int lineNum = responseSearchTitle.getReturnData().getLineNum();
        int[] linePos = new int[lineNum];
        for(int i=0;i<lineNum;i++){linePos[i]=0;}
        squareGridLayout.removeAllViews();
        for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
            ImageView tv = new ImageView(this);
            if(baseResponseTitleInfo.getTitleType()==3){tv.setBackgroundColor(123);}
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int k = i%lineNum;
            params.rowSpec = GridLayout.spec(k);
            params.columnSpec = GridLayout.spec(linePos[k], baseResponseTitleInfo.getBlockLen());
            linePos[k] += baseResponseTitleInfo.getBlockLen();
            params.setGravity(Gravity.FILL);
            tv.setLayoutParams(params);

            tv.setTag(i);
            tv.setOnClickListener(new View.OnClickListener() {
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
            squareGridLayout.addView(tv, i);
        }
    }

    public void secReFlashSearchTitle(int index){
//        int lineNum = responseSearchTitle.getReturnData().getLineNum();
//        int[] linePos = new int[lineNum];
//        for(int i=0;i<lineNum;i++){linePos[i]=0;}
//        squareGridLayout.removeAllViews();
//        for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
//            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
//            ImageView tv = new ImageView(this);
//            if(baseResponseTitleInfo.getTitleType()==3){tv.setImageBitmap(titleBitmap[i]);}
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            int k = i%lineNum;
//            params.rowSpec = GridLayout.spec(k);
//            params.columnSpec = GridLayout.spec(linePos[k], baseResponseTitleInfo.getBlockLen());
//            linePos[k] += baseResponseTitleInfo.getBlockLen();
//            params.setGravity(Gravity.FILL);
//            tv.setLayoutParams(params);
//            squareGridLayout.addView(tv,i);
//        }
        tmpCnt++;
        //ToolLog.dbg("bitmapNum:"+String.valueOf(bitmapNum)+"tmpCnt:" + String.valueOf(tmpCnt));
        if(tmpCnt==bitmapNum){searchFlag = 1;}
        ((ImageView)squareGridLayout.getChildAt(index)).setImageBitmap(titleBitmap[index]);

    }

/****************************************主逻辑 完********************************************************/

/****************************************辅助函数********************************************************/

//////////////////////////////////////////get set类///////////////////////////////////////////

    public ResponseSearchTitle getResponseSearchTitle(){return responseSearchTitle;}
    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle){this.responseSearchTitle = responseSearchTitle;}

/////////////////////////////////////////工具类///////////////////////////////////////////////
    //图像压缩
    private /*Bitmap*/ ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ������������100��ʾ��ѹ������ѹ�������ݴ�ŵ�baos��
        ToolLog.dbg("compressCnt:" + String.valueOf(baos.toByteArray().length));
        int options = 40;
        while ( baos.toByteArray().length / 1024>35) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����35kb,���ڼ���ѹ��
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

/****************************************工具类 完********************************************************/

}
