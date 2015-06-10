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
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.jaf.jcore.Application;
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

import org.json.JSONException;
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
    private ResponseUserChainPull responseUserChainPull = null;
    private long listLastId = 0;
    private long listFirstId = 0;
    private ScrollAdapter scrollAdapter = null;

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
        emptyLayout.setMargins(20,0,20,0);
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

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do anything you want with datacontent
                InfoImageView iiv = (InfoImageView) v;
                ChatListItemModel model = iiv.dataContent;

                if (model.isFemale) {
                    Toast.makeText(ActivityChatview.this, "Female Chatty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityChatview.this, "Male Chatty", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        scrollAdapter = new ScrollAdapter(this, listOfModels);
        listView.setAdapter(scrollAdapter);
        initList();

        //一直检测吧
        userChainPull(Constant.IDTYPE_GETOLD, 0, Application.getInstance().isFirstCheckUnread);
        //Application.getInstance().isFirstCheckUnread = 0;

        boolean hasChain = (scrollAdapter.getCount() > 4);
        ActivityChatview.this.setEmptyViewHidden(hasChain);
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
                ToolLog.dbg(message.getBody().toString());
                try {
                    iTitleId = Integer.parseInt(message.getStringAttribute("broadcast"));
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                int i = 0;
                for(i = 0;i<listOfModels.size();i++){
                    if(listOfModels.get(i).baseResponseUserChainInfo.getTitleInfo().getTitleId() == iTitleId){
                        listOfModels.get(i).unReadCnt = 1;
                        scrollAdapter.notifyDataSetChanged();
                        boolean hasChain = (scrollAdapter.getCount() > 4);
                        ActivityChatview.this.setEmptyViewHidden(hasChain);
                        break;
                    }
                }
                //新关系链
                if(i==listOfModels.size()){

                }
                ToolLog.dbg("chatview");
                HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);

                break;
            }
            default:
                break;
        }
    }

    /////////////////////////////////////////////////////事件响应//////////////////////////////
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
//            if (model.isFemale) {
//                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.female_avatar);
//                detailImage.setImageBitmap(bmp);
//            } else {
//                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.male_avatar);
//                detailImage.setImageBitmap(bmp);
//            }

            if(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleType() == 3){
                getBitmap(listOfModels.get(index).baseResponseUserChainInfo.getTitleInfo().getTitleCont(), index);
            } else {
                if (model.isFemale) {
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.female_avatar);
                    detailImage.setImageBitmap(bmp);
                } else {
                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.male_avatar);
                    detailImage.setImageBitmap(bmp);
                }
            }
        }

        listView.setSelection(index - 2);
    }

    public void centeralListViewAtIndex(int index){
        ChatListItemModel model = scrollAdapter.getItem(index+2);
        this.clickAvatarAtIndex(model,index+2);
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
        scrollAdapter.notifyDataSetChanged();
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_CHAIN_TO_SCHAT){
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
    public void initList(){
        ChatListItemModel localTmpModel = new ChatListItemModel(true, null, 0);
        listOfModels.add(localTmpModel);
        listOfModels.add(localTmpModel);

        listOfModels.add(localTmpModel);
        listOfModels.add(localTmpModel);
        scrollAdapter.notifyDataSetChanged();
    }

    public void userChainPull(final int idType, int lastId, final int isCheckUnread) {
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
                JSONObject checkUnreadJson = new JSONObject();
                if (idType == 1) {
                    int i;
                    for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                        int unReadCnt = 0;
                        BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                        if (isCheckUnread == 1) {
                            try {
                                if (checkUnreadJson.isNull(baseResponseUserChainInfo.getTitleInfo().getDvcId()) || checkUnreadJson.getInt(baseResponseUserChainInfo.getTitleInfo().getDvcId()) == 0) {
                                    checkUnreadJson.put(baseResponseUserChainInfo.getTitleInfo().getDvcId(), 1);
                                    EMConversation conversation = EMChatManager.getInstance().getConversation(baseResponseUserChainInfo.getTitleInfo().getDvcId());
                                    unReadCnt = conversation.getUnreadMsgCount();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo, unReadCnt);
                        int j = listOfModels.size();
                        listOfModels.add(j - 2, model);
                        listLastId = baseResponseUserChainInfo.getSortId();
                    }
                } else if (idType == 2) {
                    int i;
                    for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                        int unReadCnt = 0;
                        BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                        if (isCheckUnread == 1) {
                            try {
                                if (checkUnreadJson.get(baseResponseUserChainInfo.getTitleInfo().getDvcId()) == null || checkUnreadJson.getInt(baseResponseUserChainInfo.getTitleInfo().getDvcId()) == 0) {
                                    checkUnreadJson.put(baseResponseUserChainInfo.getTitleInfo().getDvcId(), 1);
                                    EMConversation conversation = EMChatManager.getInstance().getConversation(baseResponseUserChainInfo.getTitleInfo().getDvcId());
                                    unReadCnt = conversation.getUnreadMsgCount();
                                    ToolLog.dbg("unread:" + String.valueOf(unReadCnt));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ChatListItemModel model = new ChatListItemModel(false, baseResponseUserChainInfo, unReadCnt);
                        listOfModels.add(0 + 2, model);
                        listFirstId = baseResponseUserChainInfo.getSortId();
                    }
                }
                ToolLog.dbg("UserChainNum:" + responseUserChainPull.getReturnData().getContData().size());
                scrollAdapter.notifyDataSetChanged();

                if(detailImage.getVisibility() != View.VISIBLE){
                    ActivityChatview.this.centeralListViewAtIndex(0);
                }
                boolean hasChain = (scrollAdapter.getCount() > 4);
                ActivityChatview.this.setEmptyViewHidden(hasChain);
            }
        });
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
