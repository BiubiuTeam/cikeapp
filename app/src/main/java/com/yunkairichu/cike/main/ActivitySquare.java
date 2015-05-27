package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.easemob.util.PathUtil;
import com.jaf.jcore.Application;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponsePublishTitle;
import com.yunkairichu.cike.bean.ResponseSearchTitle;
import com.yunkairichu.cike.utils.CommonUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vida2009 on 2015/5/11.
 */
//测试
public class ActivitySquare extends Activity{
    //���ೣ��
    public static final int TITLE_TYPE_PICTURE = 3;

    public static final int REQUEST_CODE_CAMERA = 18;

    public static final int RESULT_CODE_COPY = 1;

    //ui����
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
    private PopupWindow choseStatusPopupWindow;


    //��ݼ��߼������
    private Bitmap[] titleBitmap = new Bitmap[50]; //??????50
    private int bitmapNum;
    private int tmpCnt;
    private int searchFlag = 1;
    private int sendMsgTag = -1;
    private File cameraFile;
    private ResponseSearchTitle responseSearchTitle;
    private ResponsePublishTitle responsePublishTitle;
    private SquareOnTouchListener sqListener = new SquareOnTouchListener();
    private SendChoStaOnClickListener sendChoStaOnClickListener = new SendChoStaOnClickListener();

    //////////////////////////////////////////��ʼ��//////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
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

        squareSelectButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  setContentView(selectView);
              }
         });

        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
                choseStatusPopupWindow.dismiss();
                return;
            } else {
                initmPopupWindowView();
                choseStatusPopupWindow.showAsDropDown(v, 0, 5);
            }
            }
        });

        firReFlashSearchTitle();
        getTitleBitmap();
    }

    //��ʼ����������
    public void initmPopupWindowView() {
        // // ��ȡ�Զ��岼���ļ�pop.xml����ͼ
        View customView = getLayoutInflater().inflate(R.layout.menu_choose_status,
                null, false);
        // ����PopupWindowʵ��
        choseStatusPopupWindow = new PopupWindow(customView, 800, 960);
        // ���ö���Ч�� [R.style.AnimationFade ���Լ����ȶ���õ�]
        //popupWindow.setAnimationStyle(R.style.anim_take_pic);
        // �Զ���view��Ӵ����¼�
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (choseStatusPopupWindow != null && choseStatusPopupWindow.isShowing()) {
                    choseStatusPopupWindow.dismiss();
                    choseStatusPopupWindow = null;
                }

                return false;
            }
        });

        /** ���������ʵ���Զ�����ͼ�Ĺ��� */
        sendChosStaLoveButton = (ImageView) customView.findViewById(R.id.btn_send_status_love);
        sendChosStaBoringButton = (ImageView) customView.findViewById(R.id.btn_send_status_boring);
        sendChosStaThinkLifeButton = (ImageView) customView.findViewById(R.id.btn_send_status_thinklife);
        sendChosStaSelfStudyButton = (ImageView) customView.findViewById(R.id.btn_send_status_selfstudy);
        sendChosStaOnTheWayButton = (ImageView) customView.findViewById(R.id.btn_send_status_ontheway);
        sendChosStaOnWorkButton = (ImageView) customView.findViewById(R.id.btn_send_status_onwork);
        sendChosStaBodyBuildingButton = (ImageView) customView.findViewById(R.id.btn_send_status_bodybuilding);
        sendChosStaBigMealButton = (ImageView) customView.findViewById(R.id.btn_send_status_bigmeal);
        sendChosStaSelfShotButton = (ImageView) customView.findViewById(R.id.btn_send_status_selfshot);

        sendChosStaBackButton = (Button) customView.findViewById(R.id.btn_send_status_back);

        sendChosStaLoveButton.setTag(R.id.tag_msg_tag, Constant.LOVE);
        sendChosStaLoveButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaBoringButton.setTag(R.id.tag_msg_tag, Constant.BORING);
        sendChosStaBoringButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaThinkLifeButton.setTag(R.id.tag_msg_tag, Constant.THINKLIFE);
        sendChosStaThinkLifeButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaSelfStudyButton.setTag(R.id.tag_msg_tag, Constant.SELFSTUDY);
        sendChosStaSelfStudyButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaOnTheWayButton.setTag(R.id.tag_msg_tag, Constant.ONTHEWAY);
        sendChosStaOnTheWayButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaOnWorkButton.setTag(R.id.tag_msg_tag, Constant.ONWORK);
        sendChosStaOnWorkButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaBodyBuildingButton.setTag(R.id.tag_msg_tag, Constant.BODYBUILDING);
        sendChosStaBodyBuildingButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaBigMealButton.setTag(R.id.tag_msg_tag, Constant.BIGMEAL);
        sendChosStaBigMealButton.setOnClickListener(sendChoStaOnClickListener);
        sendChosStaSelfShotButton.setTag(R.id.tag_msg_tag, Constant.SELFSHOT);
        sendChosStaSelfShotButton.setOnClickListener(sendChoStaOnClickListener);

        sendChosStaBackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
                    choseStatusPopupWindow.dismiss();
                    return;
                }
            }
        });
    }

/**************************************������׽************************************************/

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

            sendMsgTag = (int)view.getTag(R.id.tag_msg_tag);
            cameraFile = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
                    + System.currentTimeMillis() + ".jpg");
            cameraFile.getParentFile().mkdirs();
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                    REQUEST_CODE_CAMERA);
        }
    }

///////////////////////////////////////���¼���Ӧ//////////////////////////////////////

    /**
     * onActivityResult �ۺ��źŴ���
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CODE_EXIT_GROUP) {
//            setResult(RESULT_OK);
//            finish();
//            return;
//        }
//        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//            switch (resultCode) {
//                case RESULT_CODE_COPY: // ������Ϣ
//                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
//                    // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
//                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
//                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
//                    break;
//                case RESULT_CODE_DELETE: // ɾ����Ϣ
//                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
//                    conversation.removeMessage(deleteMsg.getMsgId());
//                    adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
//                    break;
//
////                case RESULT_CODE_FORWARD: // ת����Ϣ
////                    EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
////                    Intent intent = new Intent(this, ForwardMessageActivity.class);
////                    intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
////                    startActivity(intent);
////
////                    break;
////
//                default:
//                    break;
//            }
//        }
        if (resultCode == RESULT_OK) { // �����Ϣ
//            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
//                // ��ջỰ
//                EMChatManager.getInstance().clearConversation(toChatUsername);
//                adapter.refresh();
//            } else
            if (requestCode == REQUEST_CODE_CAMERA) { // ������Ƭ
                ToolLog.dbg("Take Pic Finish");
                choseStatusPopupWindow.dismiss();
                if (cameraFile != null && cameraFile.exists())
                    sendPicRes(cameraFile);
            }
//            else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // ���ͱ���ѡ�����Ƶ
//
////                int duration = data.getIntExtra("dur", 0);
////                String videoPath = data.getStringExtra("path");
////                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
////                Bitmap bitmap = null;
////                FileOutputStream fos = null;
////                try {
////                    if (!file.getParentFile().exists()) {
////                        file.getParentFile().mkdirs();
////                    }
////                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
////                    if (bitmap == null) {
////                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
////                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
////                    }
////                    fos = new FileOutputStream(file);
////
////                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
////
////                } catch (Exception e) {
////                    e.printStackTrace();
////                } finally {
////                    if (fos != null) {
////                        try {
////                            fos.close();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                        fos = null;
////                    }
////                    if (bitmap != null) {
////                        bitmap.recycle();
////                        bitmap = null;
////                    }
////
////                }
////                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
//            } else if (requestCode == REQUEST_CODE_LOCAL) { // ���ͱ���ͼƬ
//                if (data != null) {
//                    popupWindow.dismiss();
//                    Uri selectedImage = data.getData();
//                    if (selectedImage != null) {
//                        sendPicByUri(selectedImage);
//                    }
//                }
//            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // ����ѡ����ļ�
////                if (data != null) {
////                    Uri uri = data.getData();
////                    if (uri != null) {
////                        sendFile(uri);
////                    }
////                }
//            } else if (requestCode == REQUEST_CODE_MAP) { // ��ͼ
////                double latitude = data.getDoubleExtra("latitude", 0);
////                double longitude = data.getDoubleExtra("longitude", 0);
////                String locationAddress = data.getStringExtra("address");
////                if (locationAddress != null && !locationAddress.equals("")) {
////                    more(more);
////                    sendLocationMsg(latitude, longitude, "", locationAddress);
////                } else {
////                    String st = getResources().getString(R.string.unable_to_get_loaction);
////                    Toast.makeText(this, st, 0).show();
////                }
//                // �ط���Ϣ
//            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
//                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
//                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
////                resendMessage();
//            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
////                // ճ��
////                if (!TextUtils.isEmpty(clipboard.getText())) {
////                    String pasteText = clipboard.getText().toString();
////                    if (pasteText.startsWith(COPY_IMAGE)) {
////                        // ��ͼƬǰ׺ȥ������ԭ�����path
////                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
////                    }
////
////                }
//            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // �������
////                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
////                addUserToBlacklist(deleteMsg.getFrom());
//            } else if (conversation.getMsgCount() > 0) {
//                adapter.refresh();
//                setResult(RESULT_OK);
//            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
//                adapter.refresh();
//            }
        }
    }

/****************************************������׽  ��*******************************************/

///////////////////////////////////////////���߳�/////////////////////////////////////////////////

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

/****************************************���߼�********************************************************/

    /////////////////////////////////////////��ҳ���ٲ���////////////////////////////////////////
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

    ////////////////////////////////////////Title�����߼�////////////////////////////////////////
    public void sendPicRes(final File mImageFile){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("file", mImageFile);
        params.put("name", mImageFile.getName());

        if(sendMsgTag <= 0){
            ToolLog.dbg("sendMsgTag err: " + String.valueOf(sendMsgTag));
            sendMsgTag = 0;
        }

        AQuery aq = new AQuery(getApplicationContext());
        aq.ajax(Constant.UPLOADRESURL, params, String.class,
                new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object,
                                         AjaxStatus status) {
                        super.callback(url, object, status);
                        ToolLog.dbg("SendPicFinish");
                        if (status.getCode() == 200) {
                            try {
                                sendTitle(sendMsgTag,(int)getFileSize(mImageFile), object);
                                sendMsgTag = 0;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.network_err, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

        setResult(RESULT_OK);
    }

    private void sendTitle(int msgTag, int picSize, String titleCont) {
        Http http = new Http();
        JSONObject jo = JsonPack.buildPublishTitle(msgTag, TITLE_TYPE_PICTURE, titleCont, 1, picSize);//������ʱд��Ϊ��ͼƬ
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponsePublishTitle(JacksonWrapper.json2Bean(response, ResponsePublishTitle.class));

                if (responsePublishTitle.getStatusCode() == 0) {
                    if (choseStatusPopupWindow != null&& choseStatusPopupWindow.isShowing()) {
                        ToolLog.dbg("SendTitleFinish");
                        choseStatusPopupWindow.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.network_err, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

/****************************************���߼�  ��********************************************************/

/****************************************������********************************************************/

//////////////////////////////////////////get set�ຯ��///////////////////////////////////////////

    public ResponseSearchTitle getResponseSearchTitle(){return responseSearchTitle;}
    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle){this.responseSearchTitle = responseSearchTitle;}
    public ResponsePublishTitle getResponsePublishTitle(){return responsePublishTitle;}
    public void setResponsePublishTitle(ResponsePublishTitle responsePublishTitle){this.responsePublishTitle = responsePublishTitle;}

/////////////////////////////////////////�����ຯ��///////////////////////////////////////////////
    //ͼƬѹ������
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

    /**
     * ��ȡָ���ļ���С
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            ToolLog.dbg("��ȡ�ļ���С", "�ļ�������!");
        }
        return size;
    }

/****************************************������  ��********************************************************/

}
