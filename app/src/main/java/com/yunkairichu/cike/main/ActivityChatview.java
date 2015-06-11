package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.jaf.jcore.DemoHXSDKHelper;
import com.jaf.jcore.HXSDKHelper;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
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
    public static final int REQUEST_CODE_CHAIN_TO_SCHAT = 102;

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
        listView.setDividerHeight((int) ToolDevice.dp2px(30));

        listViewHeight = MyHalfItem.ITEM_DEFAULT_HEIGHT * 5 + listView.getDividerHeight()*(5-1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

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
                , EMNotifierEvent.Event.EventDeliveryAck
                , EMNotifierEvent.Event.EventReadAck});

        listOfModels = new ArrayList<ChatListItemModel>();
        listOfModelsBack = new ArrayList<ChatListItemModel>();
        scrollAdapter = new ScrollAdapter(this, listOfModels);
        listView.setAdapter(scrollAdapter);
        scrollAdapter.notifyDataSetChanged();

        listOfModelsBack.clear();
        lastPostion = 0;
        userChainPullTime = 0;
        userChainPull(Constant.IDTYPE_GETOLD, 0);
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
                    userChainPull(Constant.IDTYPE_GETOLD, 0);
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
    public void clickAvatarAtIndex(ChatListItemModel model,int index){
        if (model == null || model.isLocalTmp){
            return;
        }
        //get the model, and show up the image
        detailImage.setVisibility(View.VISIBLE);
        detailImage.dataContent = model;
        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityChatview.this, ActivityChat.class);
                Bundle bundle = new Bundle();
                detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo().setSortId(detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo().getTitleId());
                bundle.putSerializable("titleInfo", detailImage.dataContent.baseResponseUserChainInfo.getTitleInfo());
                bundle.putSerializable("resSearchTitle", null);
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
        Resources res = getResources();
        if(model.isLocalTmp == false) {
            if(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleType() == 3){
                getBitmap(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleCont(), index);
            }
        }

        listView.setSelection(index - 2);
    }

    public void centeralListViewAtIndex(int index){
        ChatListItemModel model = scrollAdapter.getItem(index + 2);
        this.clickAvatarAtIndex(model, index + 2);
    }

    public void setEmptyViewHidden(boolean hidden){
        if (hidden){
            emptyView.setVisibility(View.INVISIBLE);
            chatview.bringChildToFront(listView);
            chatview.bringChildToFront(detailImage);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            chatview.bringChildToFront(emptyView);
        }
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
                userChainPull(Constant.IDTYPE_GETOLD, 0);
                ToolLog.dbg("back chain");
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
            ToolLog.dbg("OrigCnt" + String.valueOf(bitmap.getByteCount()));
            //titleBitmap[index] = compressImage(bitmap);
            detailImageBitMap = bitmap;
            detailImage.setImageBitmap(bitmap);
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
            URL url = null;
            ToolLog.dbg("BITMAP URL:"+myUrl);
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
            Bitmap bitmap = null;
            try {
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ToolLog.dbg("INDEX:"+index);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putParcelable("bitmap", bitmap);
            data.putInt("index", index);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    /////////////////////////////////////////////////拉取关系链列表/////////////////////////////////////////
    public void userChainPull(final int idType, final long lastId) {
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
                    int i;
                    if (lastPostion == 0) {
                        ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                        listOfModelsBack.add(lastPostion, localTmpModel);
                        listOfModelsBack.add(lastPostion + 1, localTmpModel);
                        lastPostion = 2;
                    }
                    for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                        BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                        ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo);
                        listOfModelsBack.add(lastPostion, model);
                        lastPostion++;
                        listLastId = baseResponseUserChainInfo.getSortId();
                        if(i==0&&lastId==0){
                            listFirstId = baseResponseUserChainInfo.getSortId();
                        }
                    }
                    userChainPullTime++;
//                    ToolLog.dbg("listLastId:" + String.valueOf(listLastId));
//                    ToolLog.dbg("responseUserChainPullSize:" + String.valueOf(responseUserChainPull.getReturnData().getContData().size()));
//                    ToolLog.dbg("userChainPullTime:" + String.valueOf(userChainPullTime));
//                    ToolLog.dbg("lastPostion:" + String.valueOf(lastPostion));
                    if (responseUserChainPull.getReturnData().getContData().size() > 0 && userChainPullTime < Constant.MAX_CHAIN_PULL_TIME) {
                        userChainPull(Constant.IDTYPE_GETOLD, listLastId);
                    } else {
                        ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                        listOfModelsBack.add(lastPostion, localTmpModel);
                        listOfModelsBack.add(lastPostion + 1, localTmpModel);
                        listOfModelsReflash();
                        if(detailImage.getVisibility() != View.VISIBLE){
                            ActivityChatview.this.centeralListViewAtIndex(0);
                        }
                        boolean hasChain = (scrollAdapter.getCount() > 4);
                        ActivityChatview.this.setEmptyViewHidden(hasChain);
                    }
                } else if (idType == 2) {
                    int i;
                    //ToolLog.dbg("listFirstId:" + String.valueOf(listFirstId));
                    List<ChatListItemModel> listOfModelsTmp = new ArrayList<ChatListItemModel>();
                    listOfModelsTmp.clear();
                    for(int j=2; j<listOfModels.size()-2; j++){
                        listOfModelsTmp.add(j - 2, listOfModels.get(j));
                    }
                    ChatListItemModel localTmpModel = new ChatListItemModel(true, null);
                    listOfModels.clear();
                    listOfModels.add(0, localTmpModel);
                    listOfModels.add(1, localTmpModel);
                    int j = 2;
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
                    listOfModels.add(j, localTmpModel);
                    listOfModels.add(j + 1, localTmpModel);
                    scrollAdapter.refresh();
                    if(detailImage.getVisibility() != View.VISIBLE){
                        ActivityChatview.this.centeralListViewAtIndex(0);
                    }
                    boolean hasChain = (scrollAdapter.getCount() > 4);
                    ActivityChatview.this.setEmptyViewHidden(hasChain);
                }
            }
        });
    }

    public void listOfModelsReflash(){
        listOfModels.clear();
        for(int i=0;i < listOfModelsBack.size();i++){
            listOfModels.add(i,listOfModelsBack.get(i));
        }
        userChainPullTime = 0;
        lastPostion = 0;
        listOfModelsBack.clear();
        scrollAdapter.notifyDataSetChanged();
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

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage
                , EMNotifierEvent.Event.EventDeliveryAck
                , EMNotifierEvent.Event.EventReadAck});
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
