package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.jaf.jcore.Application;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.adapter.MessageAdapter;
import com.yunkairichu.cike.adapter.VoicePlayClickListener;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;
import com.yunkairichu.cike.bean.ResponseUserChainInsert;
import com.yunkairichu.cike.utils.CommonUtils;
import com.yunkairichu.cike.widget.PasteEditText;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuxiaobo on 15/5/12.
 */
public class ActivityChat extends Activity {
    //本类常量
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    ///////////////////////demo实例变量////////////////////////////////
    public static final String COPY_IMAGE = "EASEMOBIMG";
    private ImageView locationImgview;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
//    private EMConversation conversation;
//    public static ChatActivity activityInstance = null;
    // 给谁发送消息
//    private VoiceRecorder voiceRecorder;
//    private MessageAdapter adapter;
//    private File cameraFile;
//    static int resendPos;

//    private GroupListener groupListener;

    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    //Constant.TOCHATUSERNAME

    /////////////////////Ui变量//////////////////////////////
    private PasteEditText mEditTextContent;
    private Button btnSend;
    private Button btnPicture;
    private Button buttonSetModeKeyboard;  //语音和键盘切换
    private Button buttonSetModeVoice;   //语音和键盘切换
    private Button btnPicMenuTakePic;    //图片相关
    private Button btnPicMenuFromloc;    //图片相关
    private Button btnPicMenuCancel;     //图片相关
    private View recordingContainer;     //语音时的屏幕中间的图标
    private View buttonPressToSpeak;     //按住说话
    private TextView backTV;
    private TextView roleTV;
    private TextView regionTV;
    private TextView recordingHint;       //语音取消提示
    private TextView timeline_text;       //时间线文字
    private ListView listView;
    private ImageView big_image;           //聊天开始时那个大图
    private ImageView ivToUserStatus;     //对方用户状态图标
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private ImageView micImage;                    //语音时的屏幕中间的图标里的麦克风图标
    private Drawable[] micImages;                  //语音时的屏幕中间的图标里的麦克风图标
    private Drawable[] dToUserStatus;             //用户状态图
    private LinearLayout emojiIconContainer;
    private ProgressBar loadmorePB;
    private ProgressBar timeline;            //时间线
    protected RelativeLayout faceLayout=null;    //底下拉出的黑框
    private RelativeLayout edittext_layout;
    private PopupWindow popupWindow;
    //private LinearLayout btnContainer;
    //private ViewPager expressionViewpager;

    /////////////////////数据及逻辑类变量////////////////////
    EMConversation conversation;
    public String playMsgId;  //这个是给语音播放调用的
    // 给谁发送消息
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;         //语音动画
    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;

    private BaseResponseTitleInfo baseResponseTitleInfo;
    private ResponseSearchTitle responseSearchTitle;
    private ResponseUserChainInsert responseUserChainInsert;
    private int chatType;
    private NewMessageBroadcastReceiver receiver; //接收器
    private PowerManager.WakeLock wakeLock;       //电源管理器
    private int boardHeight = 0;  //大图标高
    private int boardWidth = 0;  //大图标宽
    private int smboardHeight = 0;  //小图标高
    private int smboardWidth = 0;  //小图标宽
    private int big_pic_flag;  //大小图标标识

    //定义大表情相关变量
    ArrayList<ImageView> pointList=null;
    ArrayList<ArrayList<HashMap<String,Object>>> listGrid=null;
    protected ViewFlipper viewFlipper=null;
    protected ImageView chatBottomLook=null;
    protected LinearLayout pagePoint=null,fillGapLinear=null;

    private boolean expanded=false;

    int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
            ,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_007,R.drawable.f_static_008,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011};
    String[] faceName={"鄙视","害羞","汗","抠鼻","哭","酷","吐舌头","笑","正直","窒息","醉了","Duang"};
    HashMap<String,Integer> faceMap=null;

//////////////////////////////////////////////////初始化/////////////////////////////////////////////

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            int picNumber = msg.what%4;
            micImage.setImageDrawable(micImages[picNumber].getCurrent());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = this.getIntent().getExtras();
        baseResponseTitleInfo = (BaseResponseTitleInfo)bundle.getSerializable("titleInfo");
        responseSearchTitle = (ResponseSearchTitle)bundle.getSerializable("resSearchTitle");

        backTV = (TextView) findViewById(R.id.back);
        roleTV = (TextView) findViewById(R.id.role);
        regionTV = (TextView) findViewById(R.id.region);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        timeline_text = (TextView) findViewById(R.id.timeline_text);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        btnPicture = (Button) findViewById(R.id.btn_add_picture);
        btnSend = (Button) findViewById(R.id.btn_send);
        buttonSetModeKeyboard = (Button) findViewById(R.id.btn_set_mode_keyboard);
        buttonSetModeVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        listView = (ListView) findViewById(R.id.list);
        ivToUserStatus = (ImageView) findViewById(R.id.status);
        timeline = (ProgressBar) findViewById(R.id.timeline);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        big_image = (ImageView) findViewById(R.id.big_image);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        loadmorePB = (ProgressBar) findViewById(R.id.chat_load_more);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);  //聊天编辑框
        //btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container); //emoji表情
        recordingContainer = findViewById(R.id.recording_container);
        chatType = 1;//暂时默认是单聊

        // 动画资源文件,用于录制语音时
        micImage = (ImageView) findViewById(R.id.mic_image);
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.mic_01),
                getResources().getDrawable(R.drawable.mic_02), getResources().getDrawable(R.drawable.mic_03),
                getResources().getDrawable(R.drawable.mic_04) };

        int userConfig = baseResponseTitleInfo.getUserConfig();
        int toUserRole = (userConfig>>1)&1;
        int toUserSex = (userConfig>>0)&1;
        String roleText = (toUserRole==0)?"学生":"在职";
        String regionText = "对方在"+baseResponseTitleInfo.getCity();
        toChatUsername = baseResponseTitleInfo.getDvcId().toLowerCase();
        //进来时的大图
        Intent intent=getIntent();
        byte [] bis=intent.getByteArrayExtra("bitmap");
        Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
        big_image.setImageBitmap(bitmap);
        big_pic_flag = 0;
        //状态图标设置
        dToUserStatus = new Drawable[] { getResources().getDrawable(R.drawable.boring),
                getResources().getDrawable(R.drawable.love), getResources().getDrawable(R.drawable.boring),
                getResources().getDrawable(R.drawable.thinklife), getResources().getDrawable(R.drawable.selfstudy),
                getResources().getDrawable(R.drawable.ontheway), getResources().getDrawable(R.drawable.onwork),
                getResources().getDrawable(R.drawable.bodybuilding), getResources().getDrawable(R.drawable.bigmeal),
                getResources().getDrawable(R.drawable.selfshot)};
        int toUserStatus = baseResponseTitleInfo.getMsgTag();
        if(toUserStatus < 1 || toUserStatus > 9) toUserStatus = 0;
        ivToUserStatus.setImageDrawable(dToUserStatus[toUserStatus].getCurrent());
        //设置时间线
        long startTime = (long)baseResponseTitleInfo.getPubTime();
        long time=System.currentTimeMillis();
        ToolLog.dbg("startTime:"+String.valueOf(startTime)+"time:"+String.valueOf(time));
        int diffTime = (int) ((time/1000)-startTime);
        int lastTime = diffTime*100/86400;
        lastTime = 0 - lastTime;
        ToolLog.dbg("diffTime:" + String.valueOf(diffTime) + "lastTime:" + String.valueOf(lastTime));
        final Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(diffTime);
        int mHour=(86400-diffTime)/3600;
        int mMinuts=((86400-diffTime)%3600)/60;
        timeline.incrementProgressBy(lastTime);
        timeline_text.setText("还有" + String.valueOf(mHour) + "小时" + String.valueOf(mMinuts) + "分钟会话销毁");

        roleTV.setText(roleText);
        regionTV.setText(regionText);
        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

        backTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityChat.this, ActivitySquare.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolLog.dbg((String) mEditTextContent.getText().toString());
                sendText(mEditTextContent.getText().toString());
            }
        });

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null&& popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupWindow.showAsDropDown(v, 0, 5);
                }
            }
        });

        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnPicture.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnPicture.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                    ToolLog.dbg("hasFocus");
                } else {
                    //edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                    ToolLog.dbg("noFocus");
                }

            }
        });

        mEditTextContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                //faceLayout.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                //btnContainer.setVisibility(View.GONE);
            }
        });

        big_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int height, width;
                if(big_pic_flag == 0) {
                    if(boardHeight==0||boardHeight<v.getHeight())boardHeight = v.getHeight();
                    if(boardWidth==0||boardWidth<v.getWidth())boardWidth = v.getWidth();
                    smboardHeight = boardHeight/4;
                    smboardWidth = boardWidth*1/3;
                    height = smboardHeight;
                    width = smboardWidth;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                    layoutParams.setMargins(width * 2, 0, 0, 0);
                    v.setLayoutParams(layoutParams);
                    big_pic_flag = 1;
                }
                else{
                    height = boardHeight;
                    width = boardWidth;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                    layoutParams.setMargins(0, 0, 0, 0);
                    v.setLayoutParams(layoutParams);
                    big_pic_flag = 0;
                }
            }
        });

        setUpView();

        //////////////////////////////////////////////////////////////////////////////////////////////////

        faceMap=new HashMap<String,Integer>();
        listGrid=new ArrayList<ArrayList<HashMap<String,Object>>>();
        pointList=new ArrayList<ImageView>();

        //btnPicture =(Button)findViewById(R.id.btn_add_picture);
        //mEditTextContent= (PasteEditText) findViewById(R.id.et_sendmessage);

        chatBottomLook=(ImageView)findViewById(R.id.iv_emoticons_normal);
        faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);
        pagePoint=(LinearLayout)findViewById(R.id.pagePoint);
        fillGapLinear=(LinearLayout)findViewById(R.id.fill_the_gap);
        viewFlipper=(ViewFlipper)findViewById(R.id.faceFlipper);
        fillGapLinear=(LinearLayout)findViewById(R.id.fill_the_gap);

        chatBottomLook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (expanded) {
                    setFaceLayoutExpandState(false);
                    expanded = false;

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    /**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
                     *再次打开ViewFlipper时画面在动的结果,
                     *为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
                     *最上面添加了一个1dip高的黑色色块
                     */

                } else {

                    setFaceLayoutExpandState(true);
                    expanded = true;
                    setPointEffect(0);

                }
            }

        });

        /**mEditTextContent从未获得焦点到首次获得焦点时不会调用OnClickListener方法，所以应该改成OnTouchListener
         * 从而保证点mEditTextContent第一下就能够把表情界面关闭
         editText.setOnClickListener(new OnClickListener(){

        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub
        ViewGroup.LayoutParams params=viewFlipper.getLayoutParams();
        params.height=0;
        viewFlipper.setLayoutParams(params);
        expanded=false;
        System.out.println("WHYWHWYWHYW is Clicked");
        }

        });
         **/

        mEditTextContent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (expanded) {

                    setFaceLayoutExpandState(false);
                    expanded = false;
                }
                return false;
            }
        });

        /**
         * 为表情Map添加数据
         */
        for (int i = 0; i < faceId.length; i++) {
            faceMap.put(faceName[i], faceId[i]);
        }

        addFaceData();
        addGridView();

        //Toast.makeText(ActivityChat.this, "titleId:" + String.valueOf(baseResponseTitleInfo.getSortId()), Toast.LENGTH_SHORT).show();
    }

    private void setUpView() {
        //iv_emoticons_normal.setOnClickListener(this);
        //iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        // 把此会话的未读数置为0
        conversation.resetUnreadMsgCount();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }

        adapter = new MessageAdapter(this, toChatUsername, chatType, baseResponseTitleInfo.getSortId());
        // 显示消息
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();


        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                //faceLayout.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                //btnContainer.setVisibility(View.GONE);
                return false;
            }
        });

        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);

        // 监听当前会话的群聊解散被T事件
//        groupListener = new GroupListener();
//        EMGroupManager.getInstance().addGroupChangeListener(groupListener);

        // show forward message if the message is not null
//        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
//        if (forward_msg_id != null) {
//            // 显示发送要转发的消息
//            forwardMessage(forward_msg_id);
//        }
    }

    //初始化弹出窗口
    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.menu_take_picture,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,600,350);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        //popupWindow.setAnimationStyle(R.style.anim_take_pic);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        btnPicMenuTakePic = (Button) customView.findViewById(R.id.btn_picmenu_takepic);
        btnPicMenuFromloc = (Button) customView.findViewById(R.id.btn_picmenu_fromloc);
        btnPicMenuCancel = (Button) customView.findViewById(R.id.btn_picmenu_cancel);

        btnPicMenuTakePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!CommonUtils.isExitsSdcard()) {
                    String st = getResources().getString(R.string.sd_card_does_not_exist);
                    Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                    return;
                }

                cameraFile = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
                        + System.currentTimeMillis() + ".jpg");
                cameraFile.getParentFile().mkdirs();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                        REQUEST_CODE_CAMERA);
            }
        });

        btnPicMenuFromloc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, REQUEST_CODE_LOCAL);
            }
        });

        btnPicMenuCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow != null&& popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return;
                }
            }
        });
    }

/*************************************主逻辑******************************************/

/////////////////////////与本地后台对接的逻辑/////////////////////////////////////
    public void addUserChain()
    {
        Http http = new Http();
        JSONObject jo = JsonPack.buildUserChainInsert(baseResponseTitleInfo.getDvcId(), baseResponseTitleInfo.getSortId());
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }
                setResponseUserChainInsert(JacksonWrapper.json2Bean(response, ResponseUserChainInsert.class));

                if(responseUserChainInsert.getStatusCode() != 0){
                    ToolLog.dbg("Add chain faild!");
                }
            }
        });
    }


/////////////////////////////////////文本 图片 语音发送///////////////////////////

    /**
     * 发送文本消息
     *
     * @param content
     *            message content
     * @param isResend
     *            boolean resend
     */
    private void sendText(String content) {
        Toast.makeText(ActivityChat.this, "listViewCnt:" + String.valueOf(listView.getCount()), Toast.LENGTH_SHORT);
        ToolLog.dbg("listViewCnt:"+ String.valueOf(listView.getCount()));
        int flag = 1;
        for(int i=0;i<listView.getCount();i++){
            if(listView.getChildAt(i).getVisibility()==View.VISIBLE){
                flag = 0;
                break;
            }
        }
        if(flag==1){
            ToolLog.dbg("addUserChain");
            addUserChain();
        }

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            //增加会话标识，用titleId区分
            message.setAttribute("broadcast", String.valueOf(baseResponseTitleInfo.getSortId()));
            message.setAttribute("from", ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase());
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);

            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
            mEditTextContent.setText("");

            setResult(RESULT_OK);

//            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
//
//                @Override
//                public void onSuccess() {
//                    ToolLog.dbg("OK");
//                    //updateSendedView(message, holder);
//                }
//
//                @Override
//                public void onError(int code, String error) {
//                    ToolLog.dbg("bad");
//                    //updateSendedView(message, holder);
//                }
//
//                @Override
//                public void onProgress(int progress, String status) {
//                }
//
//            });
        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        int flag = 1;
        for(int i=0;i<listView.getCount();i++){
            if(listView.getChildAt(i).getVisibility()==View.VISIBLE){
                flag = 0;
                break;
            }
        }
        if(flag==1){
            addUserChain();
        }

        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(EMMessage.ChatType.GroupChat);
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);
            //增加会话标识，用titleId区分
            message.setAttribute("broadcast", String.valueOf(baseResponseTitleInfo.getSortId()));
            message.setAttribute("from", ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase());

            conversation.addMessage(message);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        int flag = 1;
        for(int i=0;i<listView.getCount();i++){
            if(listView.getChildAt(i).getVisibility()==View.VISIBLE){
                flag = 0;
                break;
            }
        }
        if(flag==1){
            addUserChain();
        }

        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        //增加会话标识，用titleId区分
        message.setAttribute("broadcast", String.valueOf(baseResponseTitleInfo.getSortId()));
        message.setAttribute("from", ToolDevice.getId(Application.getInstance().getApplicationContext()).toLowerCase());
        conversation.addMessage(message);

        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
        // more(more);
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(file.getAbsolutePath());
        }
    }

/////////////////////////////////////接收侦听相关///////////////////////////////////////////

    /**
     * 消息广播接收者
     *
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(toChatUsername)) {
                // 消息不是发给当前会话，return
                return;
            }
            // conversation =
            // EMChatManager.getInstance().getConversation(toChatUsername);
            // 通知adapter有新消息，更新ui

            adapter.refresh();
            listView.setSelection(listView.getCount() - 1);

//            unregisterReceiver(this); // 这句话必须要写要不会报错，不写虽然能关闭，会报一堆错
//            ((Activity) context).finish();
        }

        public void onResume() {
            // 注册接收消息广播
            receiver = new NewMessageBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
            // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
            intentFilter.setPriority(5);
            registerReceiver(receiver, intentFilter);
        }

        public void close() {
            Intent intent = new Intent();
            sendBroadcast(intent);// 该函数用于发送广播
            finish();
        }
    }

/////////////////////////////////////主题内容显示///////////////////////////////////////////

    /**
     * listview滑动监听listener
     *
     */
    private class ListScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                        isloading = true;
                        loadmorePB.setVisibility(View.VISIBLE);
                        // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
                        List<EMMessage> messages;
                        EMMessage firstMsg = conversation.getAllMessages().get(0);
                        try {
                            // 获取更多messges，调用此方法的时候从db获取的messages
                            // sdk会自动存入到此conversation中
                            if (chatType == CHATTYPE_SINGLE)
                                messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
                            else
                                messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
                        } catch (Exception e1) {
                            loadmorePB.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                        }
                        if (messages.size() != 0) {
                            // 刷新ui
                            if (messages.size() > 0) {
                                adapter.refreshSeekTo(messages.size() - 1);
                            }
                            if (messages.size() != pagesize)
                                haveMoreData = false;
                        } else {
                            haveMoreData = false;
                        }
                        loadmorePB.setVisibility(View.GONE);
                        isloading = false;

                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

/*************************************主逻辑 完****************************************************/

/************************************事件响应****************************************************/

////////////////////////////////////////小事件响应//////////////////////////////////////////////

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        //faceLayout.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.GONE);
        //btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        //btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);

    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        //faceLayout.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            //btnMore.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        } else {
            //btnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        }

    }

///////////////////////////////////////大事件响应//////////////////////////////////////
    /**
     * 按住说话listener
     *
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(ActivityChat.this, st4, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ActivityChat.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        //recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                        recordingHint.setBackgroundColor(-65536);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ActivityChat.this, st3, Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * onActivityResult 综合信号传递
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                    // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                case RESULT_CODE_DELETE: // 删除消息
                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                    conversation.removeMessage(deleteMsg.getMsgId());
                    adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
                    break;

//                case RESULT_CODE_FORWARD: // 转发消息
//                    EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
//                    Intent intent = new Intent(this, ForwardMessageActivity.class);
//                    intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//                    startActivity(intent);
//
//                    break;
//
                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                popupWindow.dismiss();
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

//                int duration = data.getIntExtra("dur", 0);
//                String videoPath = data.getStringExtra("path");
//                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
//                Bitmap bitmap = null;
//                FileOutputStream fos = null;
//                try {
//                    if (!file.getParentFile().exists()) {
//                        file.getParentFile().mkdirs();
//                    }
//                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
//                    if (bitmap == null) {
//                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
//                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
//                    }
//                    fos = new FileOutputStream(file);
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (fos != null) {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        fos = null;
//                    }
//                    if (bitmap != null) {
//                        bitmap.recycle();
//                        bitmap = null;
//                    }
//
//                }
//                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    popupWindow.dismiss();
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
//                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        sendFile(uri);
//                    }
//                }
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
//                double latitude = data.getDoubleExtra("latitude", 0);
//                double longitude = data.getDoubleExtra("longitude", 0);
//                String locationAddress = data.getStringExtra("address");
//                if (locationAddress != null && !locationAddress.equals("")) {
//                    more(more);
//                    sendLocationMsg(latitude, longitude, "", locationAddress);
//                } else {
//                    String st = getResources().getString(R.string.unable_to_get_loaction);
//                    Toast.makeText(this, st, 0).show();
//                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
//                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
//                // 粘贴
//                if (!TextUtils.isEmpty(clipboard.getText())) {
//                    String pasteText = clipboard.getText().toString();
//                    if (pasteText.startsWith(COPY_IMAGE)) {
//                        // 把图片前缀去掉，还原成正常的path
//                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
//                    }
//
//                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
//                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
//                addUserToBlacklist(deleteMsg.getFrom());
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

/************************************事件响应 完*************************************************/

///////////////////////////////////////get set类函数/////////////////////////////////////

    public ResponseSearchTitle getResponseSearchTitle(){return responseSearchTitle;}
    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle){this.responseSearchTitle = responseSearchTitle;}
    public ResponseUserChainInsert getResponseUserChainInsert(){return responseUserChainInsert;}
    public void setResponseUserChainInsert(ResponseUserChainInsert responseUserChainInsert){this.responseUserChainInsert = responseUserChainInsert;}
    public ListView getListView() {
        return listView;
    }
    public MessageAdapter getAdapter(){
        return adapter;
    }

////////////////////////////////////表情相关函数/////////////////////////////////////////

    private void addFaceData() {
        ArrayList<HashMap<String, Object>> list = null;
        for (int i = 0; i < faceId.length; i++) {
            if (i % 6 == 0) {
                list = new ArrayList<HashMap<String, Object>>();
                listGrid.add(list);
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", faceId[i]);
            map.put("faceName", faceName[i]);

            /**
             * 这里把表情对应的名字也添加进数据对象中，便于在点击时获得表情对应的名称
             */
            listGrid.get(i / 6).add(map);
        }
        System.out.println("listGrid size is " + listGrid.size());
    }

    private void addGridView() {
        for (int i = 0; i < listGrid.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_item, null);
            GridView gv = (GridView) view.findViewById(R.id.myGridView);
            gv.setNumColumns(3);
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
            MyGridAdapter adapter = new MyGridAdapter(this, listGrid.get(i), R.layout.chat_grid_item, new String[]{"image"}, new String[]{"faceName"}, new int[]{R.id.gridImage},new int[]{R.id.gridText});
            gv.setAdapter(adapter);
            gv.setOnTouchListener(new MyTouchListener(viewFlipper));
            viewFlipper.addView(view);
            /**
             * 这里不喜欢用Java代码设置Image的边框大小等，所以单独配置了一个Imageview的布局文件
             */
            View pointView = LayoutInflater.from(this).inflate(R.layout.point_image_layout, null);
            ImageView image = (ImageView) pointView.findViewById(R.id.pointImageView);
            image.setBackgroundResource(R.drawable.qian_point);
            pagePoint.addView(pointView);
            /**
             * 这里验证了LinearLayout属于ViewGroup类型，可以采用addView 动态添加view
             */

            pointList.add(image);
            /**
             * 将image放入pointList，便于修改点的颜色
             */



        }

    }

    /**
     为放置表情的GridView设置适配器
     */
    class MyGridAdapter extends BaseAdapter {

        Context context;
        ArrayList<HashMap<String, Object>> list;
        int layout;
        String[] from;
        String[] fromName;
        int[] to;
        int[] toName;


        public MyGridAdapter(Context context,
                             ArrayList<HashMap<String, Object>> list, int layout,
                             String[] from,String[] fromName, int[] to, int[] toName ) {
            super();
            this.context = context;
            this.list = list;
            this.layout = layout;
            this.from = from;
            this.fromName = fromName;
            this.to = to;
            this.toName = toName;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            ImageView image = null;
            TextView name = null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(layout, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(to[0]);
                holder.name = (TextView) convertView.findViewById(toName[0]);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setImageResource((Integer) list.get(position).get(from[0]));
            holder.name.setText((CharSequence) list.get(position).get(fromName[0]));
            class MyGridImageClickListener implements View.OnClickListener {

                int position;

                public MyGridImageClickListener(int position) {
                    super();
                    this.position = position;
                }



                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    /**
                     * 这里的点击函数要重写，点击后表情从右往左加载
                     */
//                    mEditTextContent.append((String)list.get(position).get("faceName"));
                }

            }
            //这里创建了一个方法内部类
            holder.image.setOnClickListener(new MyGridImageClickListener(position));


//            TextView mGridText = (TextView) convertView.findViewById(R.id.gridText);
//            mGridText.setText(faceName[position]);




            return convertView;
        }

    }

    private boolean moveable = true;
    private float startX = 0;

    class MyTouchListener implements View.OnTouchListener {

        ViewFlipper viewFlipper = null;


        public MyTouchListener(ViewFlipper viewFlipper) {
            super();
            this.viewFlipper = viewFlipper;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    moveable = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (moveable) {
                        if (event.getX() - startX > 40) {
                            moveable = false;
                            int childIndex = viewFlipper.getDisplayedChild();
                            /**
                             * 这里的这个if检测是防止表情列表循环滑动
                             */
                            if (childIndex > 0) {
                                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ActivityChat.this, R.anim.left_in));
                                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ActivityChat.this, R.anim.right_out));
                                viewFlipper.showPrevious();
                                setPointEffect(childIndex - 1);
                            }
                        } else if (event.getX() - startX < -40) {
                            moveable = false;
                            int childIndex = viewFlipper.getDisplayedChild();
                            /**
                             * 这里的这个if检测是防止表情列表循环滑动
                             */
                            if (childIndex < listGrid.size() - 1) {
                                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ActivityChat.this, R.anim.right_in));
                                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ActivityChat.this, R.anim.left_out));
                                viewFlipper.showNext();
                                setPointEffect(childIndex + 1);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    moveable = true;
                    break;
                default:
                    break;
            }

            return false;
        }

    }

    private void setSoftInputState() {
        ((InputMethodManager) ActivityChat.this.getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setFaceLayoutExpandState(boolean isexpand) {
        if (isexpand == false) {

            viewFlipper.setDisplayedChild(0);
            ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
            params.height = 1;
            faceLayout.setLayoutParams(params);
            /**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
             *再次打开ViewFlipper时画面在动的结果,
             *为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout中ViewFlipper
             *上层添加了一个1dip高的黑色色块
             *
             *viewFlipper必须在屏幕中有像素才能执行setDisplayedChild()操作
             */
            chatBottomLook.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);


        } else {
            /**
             * 让软键盘消失
             */
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                    (ActivityChat.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
            params.height = (int) ToolDevice.dp2px(265);
            faceLayout.setLayoutParams(params);
            chatBottomLook.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn);

        }
    }

    /**
     翻页表情时的小圆点效果
     */
    private void setPointEffect(int darkPointNum){
        for(int i=0; i<pointList.size(); i++){
            pointList.get(i).setBackgroundResource(R.drawable.qian_point);
        }
        pointList.get(darkPointNum).setBackgroundResource(R.drawable.shen_point);
    }


}
