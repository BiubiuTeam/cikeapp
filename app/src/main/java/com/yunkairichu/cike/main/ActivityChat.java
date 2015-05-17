package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.yunkairichu.cike.widget.PasteEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuxiaobo on 15/5/12.
 */
public class ActivityChat extends Activity {

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

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
    //    private EMConversation conversation;
//    public static ChatActivity activityInstance = null;
    // 给谁发送消息
    private String toChatUsername;
    //    private VoiceRecorder voiceRecorder;
//    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;

//    private GroupListener groupListener;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;

    //定义大表情相关变量
    ArrayList<ImageView> pointList=null;
    ArrayList<ArrayList<HashMap<String,Object>>> listGrid=null;
    protected ViewFlipper viewFlipper=null;
    protected RelativeLayout faceLayout=null;
    protected ImageView chatBottomLook=null;
    protected LinearLayout pagePoint=null,fillGapLinear=null;
    protected Button chatSendButton=null;

    private boolean expanded=false;

    int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
            ,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_007,R.drawable.f_static_008,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011};
    String[] faceName={"鄙视","害羞","汗","抠鼻","哭","酷","吐舌头","笑","正直","窒息","醉了","Duang"};
    HashMap<String,Integer> faceMap=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        faceMap=new HashMap<String,Integer>();
        listGrid=new ArrayList<ArrayList<HashMap<String,Object>>>();
        pointList=new ArrayList<ImageView>();

        chatSendButton=(Button)findViewById(R.id.btn_add_picture);
        mEditTextContent= (PasteEditText) findViewById(R.id.et_sendmessage);

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
    }

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
            MyGridAdapter adapter = new MyGridAdapter(this, listGrid.get(i), R.layout.chat_grid_item, new String[]{"image"}, new int[]{R.id.gridImage});
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
        int[] to;


        public MyGridAdapter(Context context,
                             ArrayList<HashMap<String, Object>> list, int layout,
                             String[] from, int[] to) {
            super();
            this.context = context;
            this.list = list;
            this.layout = layout;
            this.from = from;
            this.to = to;
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
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(layout, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(to[0]);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setImageResource((Integer) list.get(position).get(from[0]));
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


            TextView mGridText =  (TextView) convertView.findViewById(R.id.gridText);
            mGridText.setText(faceName[position]);
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
                        if (event.getX() - startX > 60) {
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
                        } else if (event.getX() - startX < -60) {
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
            params.height = (int) ToolDevice.dp2px(275);
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
