package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.jaf.jcore.Application;
import com.jaf.jcore.DemoHXSDKHelper;
import com.jaf.jcore.HXSDKHelper;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.adapter.ScrollAdapter;
import com.yunkairichu.cike.bean.BaseResponseUserChainInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseUserChainPull;
import com.yunkairichu.cike.widget.ChatListItemModel;
import com.yunkairichu.cike.widget.InfoImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class ActivityChatview extends Activity implements EMEventListener {
    //本类常量
    public static final int VISIBLE_LIST_CELL_COUNT = 7;
    public static final int REQUEST_CODE_CHAIN_TO_SCHAT = 102;

    public static final int RESULT_FORCE_REFLASH = 101;

    private Button cancelButton = null;
    private ImageView emptyView = null;

    RelativeLayout chatview = null;
    private InfoImageView detailImage = null;
    private Bitmap detailImageBitMap = null;
    private ListView listView = null;
    private LinearLayout topLayout = null;
    private RelativeLayout topLeftLayout = null;
    private List<ChatListItemModel> listOfModels = null;
    private List<ChatListItemModel> listOfModelsBack = null;
    private ResponseUserChainPull responseUserChainPull = null;
    private long listLastId = 0;
    private long listFirstId = 0;
    private ScrollAdapter scrollAdapter = null;
    private int lastPostion = 0;
    private int userChainPullTime = 0;

    private static int listViewHeight = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatview);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        chatview = (RelativeLayout) findViewById(R.id.chatview);

        cancelButton = (Button)findViewById(R.id.chatview_cancel_btn);

        topLayout = (LinearLayout)findViewById(R.id.chatview_topLayout);
        topLeftLayout = (RelativeLayout)findViewById(R.id.chatview_info_holder);

        detailImage = (InfoImageView)findViewById(R.id.detailPicture);

        emptyView = new ImageView(this);
        emptyView.setVisibility(View.INVISIBLE);
        emptyView.setImageResource(R.drawable.silly_list_empty);
        emptyView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams emptyLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//        emptyLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        emptyLayout.setMargins(20, 0, 20, 0);
        chatview.addView(emptyView, emptyLayout);

        //ui instance
        listView = new HalfCircleListView(this);
        listView.setDivider(null);
        listView.setOverScrollMode(2);//OVER_SCROLL_NEVER = 2
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        listViewHeight = MyHalfItem.ITEM_DEFAULT_HEIGHT * VISIBLE_LIST_CELL_COUNT;
        int leftHeight = dm.heightPixels - listViewHeight;
        int dividerHeight = leftHeight/(VISIBLE_LIST_CELL_COUNT - 1);
        listView.setDividerHeight(dividerHeight);
        listViewHeight = listViewHeight + dividerHeight*(VISIBLE_LIST_CELL_COUNT - 1);

        LinearLayout.LayoutParams topInfoP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topInfoP.height = listViewHeight;
        topInfoP.weight = 2;
        topInfoP.width = 2*dm.widthPixels/3;
        topLeftLayout.setLayoutParams(topInfoP);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = dm.widthPixels/3;
        layoutParams.weight = 1;
        layoutParams.height = listViewHeight;
        topLayout.addView(listView, layoutParams);

        chatview.bringChildToFront(cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        });

        //////////////////////////////////////////////////推送相关//////////////////////////////////////////////////////
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage
                , EMNotifierEvent.Event.EventNewCMDMessage, EMNotifierEvent.Event.EventDeliveryAck
                , EMNotifierEvent.Event.EventReadAck});

        listOfModels = new ArrayList<ChatListItemModel>();
        listOfModelsBack = new ArrayList<ChatListItemModel>();
        scrollAdapter = new ScrollAdapter(this, listOfModels);
        listView.setAdapter(scrollAdapter);
        scrollAdapter.notifyDataSetChanged();

        listOfModelsBack.clear();
        lastPostion = 0;
        userChainPullTime = 0;
        userChainPull(Constant.IDTYPE_GETOLD, 0, 1);
//        boolean hasChain = (scrollAdapter.getCount() > 4);
//        ActivityChatview.this.setEmptyViewHidden(hasChain);
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
                }catch (EaseMobException e) {
                    e.printStackTrace();
                }
                String key = String.valueOf(iTitleId)+toDeviceId;
                ToolPushNewMsgInfo.getInstance().putTitleNewMsgFlagValue(key, 1);

//                ToolLog.dbg("titleId:" + String.valueOf(iTitleId) + " values:" + String.valueOf(ToolPushNewMsgInfo.getInstance().getTitleNewMsgFlagValue(String.valueOf(iTitleId))));
//                ToolLog.dbg("new chatview");
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                int i = 0;
//                ToolLog.dbg("renewiSize:"+String.valueOf(listOfModels.size()));
//                ToolLog.dbg("renewiTitleId:"+String.valueOf(iTitleId)+" renewtoDeviceId:"+toDeviceId);
                for (i = 0; i < listOfModels.size(); i++) {
                    //ToolLog.dbg("renewiTitleId2:"+String.valueOf(listOfModels.get(i).baseResponseUserChainInfo.getTitleInfo().getTitleId())+" renewtoDeviceId2:"+listOfModels.get(i).baseResponseUserChainInfo.getTitleInfo().getDvcId());
                    if(listOfModels.get(i).baseResponseUserChainInfo == null){
                        continue;
                    }
                    if (listOfModels.get(i).baseResponseUserChainInfo.getTitleInfo().getTitleId() == iTitleId
                            && listOfModels.get(i).baseResponseUserChainInfo.getTitleInfo().getDvcId().equals(toDeviceId)) {
                        break;
                    }
                }
                //新关系链
                if (i == listOfModels.size()) {
//                    ToolLog.dbg("renew:"+String.valueOf(listFirstId));
                    userChainPull(Constant.IDTYPE_GETOLD, 0, 1);
                }
//                ToolLog.dbg("chatview");
                scrollAdapter.refresh();
                boolean hasChain = (scrollAdapter.getCount() > 4);
                ActivityChatview.this.setEmptyViewHidden(hasChain);
                break;
            }
            default:
                break;
        }
    }

    /////////////////////////////////////////////////////事件响应 及 逻辑//////////////////////////////
    public void clickAvatarAtIndex(ChatListItemModel model, final int index){
//        ToolLog.dbg("clickAvatarAtIndex");
        if (model == null || model.isLocalTmp){
//            ToolLog.dbg("111");
            return;
        }
        //get the model, and show up the image
        detailImage.setVisibility(View.VISIBLE);
        detailImage.dataContent = model;
        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.getInstance().chatViewCentralItemIndex = index;
                Intent i = new Intent(ActivityChatview.this, ActivityChat.class);
                Bundle bundle = new Bundle();
                detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo().setSortId(detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo().getTitleId());
                bundle.putSerializable("titleInfo", detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo());
                bundle.putSerializable("resSearchTitle", null);
                bundle.putString("fromAct","ActivityChatview");
                if(detailImageBitMap!=null) {
                    ToolLog.dbg("byteCnt:" + String.valueOf(detailImageBitMap.getByteCount()));
                    ByteArrayOutputStream baos = compressImage(detailImageBitMap);
                    ToolLog.dbg("toByteArray:" + String.valueOf(baos.toByteArray().length));
                    i.putExtra("bitmap", baos.toByteArray());
                    i.putExtras(bundle);

                    detailImageBitMap.recycle();
                    detailImageBitMap = null;
                    startActivityForResult(i, REQUEST_CODE_CHAIN_TO_SCHAT);
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                }
            }
        });

        //show up the image base on data content you set
        //Resources res = getResources();
        if(model.isLocalTmp == false) {
            if(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleType() == 3){
                getBitmap(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleCont(), index);
            }
        }

        listView.setSelection(index - VISIBLE_LIST_CELL_COUNT/2);
    }

    public void centeralListViewAtIndex(int index){
        ChatListItemModel model = scrollAdapter.getItem(index + VISIBLE_LIST_CELL_COUNT / 2);
        ToolLog.dbg("VISIBLE_LIST_CELL_COUNT:"+String.valueOf(index + VISIBLE_LIST_CELL_COUNT / 2));
        this.clickAvatarAtIndex(model, index + VISIBLE_LIST_CELL_COUNT / 2);
    }

    public void setEmptyViewHidden(boolean hidden){
        if (hidden){
            emptyView.setVisibility(View.INVISIBLE);
            chatview.bringChildToFront(listView);
            detailImage.setVisibility(View.VISIBLE);
            chatview.bringChildToFront(detailImage);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            detailImage.setVisibility(View.INVISIBLE);
            chatview.bringChildToFront(emptyView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }

        return true;
    }

    ///////////////////////////////////////大事件响应/////////////////////////////////////

    /**
     * onActivityResult ACT结果回传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_CHAIN_TO_SCHAT){
                listOfModelsBack.clear();
                lastPostion = 0;
                userChainPullTime = 0;
                userChainPull(Constant.IDTYPE_GETOLD, 0,0);
                ToolLog.dbg("chatViewCentralItemIndex:" + String.valueOf(Application.getInstance().chatViewCentralItemIndex));
//                centeralListViewAtIndex(Application.getInstance().chatViewCentralItemIndex - 3);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        centeralListViewAtIndex(Application.getInstance().chatViewCentralItemIndex-3);
//                    }
//                }, 500);
            }
        } else if (resultCode == RESULT_FORCE_REFLASH) {
            if(requestCode == REQUEST_CODE_CHAIN_TO_SCHAT){
                listOfModelsBack.clear();
                lastPostion = 0;
                userChainPullTime = 0;
                userChainPull(Constant.IDTYPE_GETOLD, 0,1);
                ToolLog.dbg("chatViewCentralItemIndex:" + String.valueOf(Application.getInstance().chatViewCentralItemIndex));
//                centeralListViewAtIndex(Application.getInstance().chatViewCentralItemIndex - 3);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        centeralListViewAtIndex(Application.getInstance().chatViewCentralItemIndex-3);
//                    }
//                }, 500);
            }
        }
    }

    ////////////////////////////////////////////////////////子线程//////////////////////////////
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            Bitmap bitmap = (Bitmap) data.getParcelable("bitmap");
            int index = data.getInt("index");
            if(bitmap != null) {
                ToolLog.dbg("OrigCnt" + String.valueOf(bitmap.getByteCount()));
                //titleBitmap[index] = compressImage(bitmap);
                detailImageBitMap = bitmap;
                detailImage.setImageBitmap(bitmap);
            }
        }
    };

    private class ThreadGetOneUserChainBitmap implements Runnable {
        private String myUrl;
        private int index;

        public ThreadGetOneUserChainBitmap(String myUrl, int index) {
            this.myUrl = myUrl;
            this.index = index;
        }

        public void run() {
            Bitmap bitmap = null;
            //先从文件里找
            String picName = myUrl.replace("http://biubiu.co/upload_src/", "");
            //ToolLog.dbg("BITMAP NAME:" + picName);
            bitmap = ToolFileRW.getInstance().loadBitmapFromFile(picName);
            //如果bitmap为空，再发网络请求获取
            if(bitmap == null) {
                URL url = null;
                //ToolLog.dbg("BITMAP URL:" + myUrl);
                try {
                    url = new URL(myUrl);
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

            ToolLog.dbg("INDEX:" + index);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putParcelable("bitmap", bitmap);
            data.putInt("index", index);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    /////////////////////////////////////////////////拉取关系链列表/////////////////////////////////////////
    public void userChainPull(final int idType, final long lastId, final int flag) {
        Http http = new Http();
        JSONObject jo = JsonPack.buildUserChainPull(idType, lastId);
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                //入列表
                setResponseUserChainPull(JacksonWrapper.json2Bean(response, ResponseUserChainPull.class));
                if (idType == 1) {
                    if (lastId == 0) {
                        ToolFileRW.getInstance().saveUserChainToFile(response, Constant.CIKEAPPUSERCHAINDATA);
                    }

                    int i;
                    if (lastPostion == 0) {
                        int tmpCount = VISIBLE_LIST_CELL_COUNT / 2;
                        ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                        for (int k = 0; k < tmpCount; k++) {
                            listOfModelsBack.add(k + lastPostion, localTmpModel);
                        }
                        lastPostion = tmpCount;
                    }
                    for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                        BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                        ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo);
                        listOfModelsBack.add(lastPostion, model);
                        lastPostion++;
                        listLastId = baseResponseUserChainInfo.getSortId();
                        if (i == 0 && lastId == 0) {
                            listFirstId = baseResponseUserChainInfo.getSortId();
                        }
                    }
                    userChainPullTime++;
                    if (responseUserChainPull.getReturnData().getContData().size() > 0 && userChainPullTime < Constant.MAX_CHAIN_PULL_TIME) {
                        userChainPull(Constant.IDTYPE_GETOLD, listLastId, flag);
                    } else {
                        int tmpCount = VISIBLE_LIST_CELL_COUNT / 2;
                        ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                        for (int tk = 0; tk < tmpCount; tk++) {
                            listOfModelsBack.add(lastPostion + tk, localTmpModel);
                        }

                        listOfModelsReflash(flag);
                        if (detailImage.getVisibility() != View.VISIBLE) {
                            ActivityChatview.this.centeralListViewAtIndex(0);
                        }
                        boolean hasChain = (scrollAdapter.getCount() > 2 * tmpCount);
                        ActivityChatview.this.setEmptyViewHidden(hasChain);
                    }
                } else if (idType == 2) {
                    int i;
                    //ToolLog.dbg("listFirstId:" + String.valueOf(listFirstId));
                    List<ChatListItemModel> listOfModelsTmp = new ArrayList<ChatListItemModel>();
                    listOfModelsTmp.clear();
                    int tmpCount = VISIBLE_LIST_CELL_COUNT / 2;
                    for (int j = tmpCount; j < listOfModels.size() - tmpCount; j++) {
                        listOfModelsTmp.add(j - tmpCount, listOfModels.get(j));
                    }
                    ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                    listOfModels.clear();
                    for (int tk = 0; tk < tmpCount; tk++) {
                        listOfModelsBack.add(tk, localTmpModel);
                    }

                    int j = tmpCount;
                    for (i = responseUserChainPull.getReturnData().getContData().size() - 1; i >= 0; i--) {
                        BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                        ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo);
                        listOfModels.add(j, model);
                        if (i == responseUserChainPull.getReturnData().getContData().size() - 1) {
                            listFirstId = baseResponseUserChainInfo.getSortId();
                        }
                        j++;
                    }
                    for (i = 0; i < listOfModelsTmp.size(); i++) {
                        listOfModels.add(j, listOfModelsTmp.get(i));
                        j++;
                    }
                    listOfModelsTmp.clear();
                    for (int bk = 0; bk < tmpCount; bk++) {
                        listOfModelsBack.add(bk + j, localTmpModel);
                    }

                    scrollAdapter.refresh();
                    if (detailImage.getVisibility() != View.VISIBLE) {
                        ActivityChatview.this.centeralListViewAtIndex(0);
                    }
                    boolean hasChain = (scrollAdapter.getCount() > 2 * tmpCount);
                    ActivityChatview.this.setEmptyViewHidden(hasChain);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                userChainPullTime = 0;
                lastPostion = 0;
                listOfModelsBack.clear();

                Toast.makeText(Application.getInstance().getApplicationContext(), "网络不给力，请稍后再试", Toast.LENGTH_SHORT).show();
                ToolLog.dbg("BAD NETWORK:" + error.toString());
                if (idType == 1) {
                    JSONObject jo = null;
                    jo = ToolFileRW.getInstance().loadUserChainFromFile(Constant.CIKEAPPUSERCHAINDATA);
                    if (jo != null) {
                        int i;
                        setResponseUserChainPull(JacksonWrapper.json2Bean(jo, ResponseUserChainPull.class));
                        int tmpCount = VISIBLE_LIST_CELL_COUNT / 2;
                        ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                        for (int k = 0; k < tmpCount; k++) {
                            listOfModelsBack.add(k + lastPostion, localTmpModel);
                        }
                        lastPostion = tmpCount;

                        for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                            BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                            ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo);
                            listOfModelsBack.add(lastPostion, model);
                            lastPostion++;
                            listLastId = baseResponseUserChainInfo.getSortId();
                            if (i == 0 && lastId == 0) {
                                listFirstId = baseResponseUserChainInfo.getSortId();
                            }
                        }
                        for (int bk = 0; bk < tmpCount; bk++) {
                            listOfModelsBack.add(bk + lastPostion, localTmpModel);
                        }
                        listOfModelsReflash(flag);
                        if (detailImage.getVisibility() != View.VISIBLE) {
                            ActivityChatview.this.centeralListViewAtIndex(0);
                        }
                        boolean hasChain = (scrollAdapter.getCount() > 4);
                        ActivityChatview.this.setEmptyViewHidden(hasChain);
                    }
                }
            }
        });
    }

    public void listOfModelsReflash(int flag){
        listOfModels.clear();
        for(int i=0;i < listOfModelsBack.size();i++){
            listOfModels.add(i,listOfModelsBack.get(i));
        }
        userChainPullTime = 0;
        lastPostion = 0;
        listOfModelsBack.clear();
        ToolLog.dbg("notifyDataSetChanged");
        scrollAdapter.notifyDataSetChanged();
        if(flag == 1) {
            centeralListViewAtIndex(0);
        } else {
            centeralListViewAtIndex(Application.getInstance().chatViewCentralItemIndex - 3);
        }
    }

    public void getBitmap(String url, int index) {
        ThreadGetOneUserChainBitmap threadGetOneUserChainBitmap = new ThreadGetOneUserChainBitmap(url, index);
        new Thread(threadGetOneUserChainBitmap).start();
    }

    //////////////////////////////////////////get set 类////////////////////////////////////////////
    public ResponseUserChainPull getResponseUserChainPull() {
        return responseUserChainPull;
    }

    public void setResponseUserChainPull(ResponseUserChainPull responseUserChainPull) {
        this.responseUserChainPull = responseUserChainPull;
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

    /////////////////////////////////////////辅助函数/////////////////////////////////////////////
    @Override
    protected void onResume() {
        Log.i("ChatActivity", "onResume");
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
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
        MobclickAgent.onPageEnd(getClass().getName());
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
}
