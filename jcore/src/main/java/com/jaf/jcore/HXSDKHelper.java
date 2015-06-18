package com.jaf.jcore;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import java.util.Iterator;
import java.util.List;

/**
 * Created by vida2009 on 2015/5/15.
 */
public abstract class HXSDKHelper {
    private static final String TAG = "HXSDKHelper";

    /**
     * MyConnectionListener
     */
    protected EMConnectionListener connectionListener = null;

    /**
     * the notifier
     */
    protected HXNotifier notifier = null;

    /**
     * subclass can override this api to return the customer notifier
     *
     * @return
     */
    protected HXNotifier createNotifier(){
        return new HXNotifier();
    }

    public HXNotifier getNotifier(){
        return notifier;
    }

    /**
     * application context
     */
    public Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    /**
     * the global HXSDKHelper instance
     */
    private static HXSDKHelper me;

    public HXSDKHelper(){
        me = this;
    }

    /**
     * HuanXin mode helper, which will manage the user data and user preferences
     */
    protected HXSDKModel hxModel = null;

    /**
     * the subclass must override this class to provide its own model or directly use {@link DefaultHXSDKModel}
     * @return
     */
    abstract protected HXSDKModel createModel();

    public HXSDKModel getModel(){
        return hxModel;
    }

    /**
     * this function will initialize the HuanXin SDK
     *
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     *
     * ���ų�ʼ��SDK������
     * ����true�����ȷ��ʼ��������false������Ϊfalse�����ں���ĵ����в�Ҫ�����κκͻ�����صĴ���
     *
     * for example:
     * ���ӣ�
     *
     * public class DemoHXSDKHelper extends HXSDKHelper
     *
     * HXHelper = new DemoHXSDKHelper();
     * if(HXHelper.onInit(context)){
     *     // do HuanXin related work
     * }
     */
    public synchronized boolean onInit(Context context){
        if(sdkInited){
            return true;
        }

        appContext = context;

        // create HX SDK model
        hxModel = createModel();

        // create a defalut HX SDK model in case subclass did not provide the model
        if(hxModel == null){
            hxModel = new DefaultHXSDKModel(appContext);
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        Log.d(TAG, "process app name : " + processAppName);

        // ���app������Զ�̵�service����application:onCreate�ᱻ����2��
        // Ϊ�˷�ֹ����SDK����ʼ��2�Σ��Ӵ��жϻᱣ֤SDK����ʼ��1��
        // Ĭ�ϵ�app�����԰���ΪĬ�ϵ�process name�����У����鵽��process name����app��process name����������
        if (processAppName == null || !processAppName.equalsIgnoreCase(hxModel.getAppProcessName())) {
            Log.e(TAG, "enter the service process!");

            // ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
            return false;
        }

        // ��ʼ������SDK,һ��Ҫ�ȵ���init()
        EMChat.getInstance().init(context);

        // ����sandbox���Ի���
        // ���鿪���߿���ʱ���ô�ģʽ
        if(hxModel.isSandboxMode()){
            EMChat.getInstance().setEnv(EMChatConfig.EMEnvMode.EMSandboxMode);
        }

        if(hxModel.isDebugMode()){
            // set debug mode in development process
            EMChat.getInstance().setDebugMode(true);
        }

        Log.d(TAG, "initialize EMChat SDK");

        initHXOptions();
        initListener();
        sdkInited = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    protected void initHXOptions(){
        Log.d(TAG, "init HuanXin Options");

        // ��ȡ��EMChatOptions����
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
        options.setAcceptInvitationAlways(hxModel.getAcceptInvitationAlways());
        // Ĭ�ϻ����ǲ�ά�����ѹ�ϵ�б�ģ����app�������ŵĺ��ѹ�ϵ���������������Ϊtrue
        options.setUseRoster(hxModel.getUseHXRoster());
        // �����Ƿ���Ҫ�Ѷ���ִ
        options.setRequireAck(hxModel.getRequireReadAck());
        // �����Ƿ���Ҫ���ʹ��ִ
        options.setRequireDeliveryAck(hxModel.getRequireDeliveryAck());
        // ���ô�db��ʼ������ʱ, ÿ��conversation��Ҫ����msg�ĸ���
        options.setNumberOfMessagesLoaded(1);

        options.setNotifyBySoundAndVibrate(true); //默认为true 开启新消息提醒
        options.setNoticeBySound(true); //默认为true 开启声音提醒
        options.setNoticedByVibrate(true); //默认为true 开启震动提醒
        options.setUseSpeaker(true); //默认为true 开启扬声器播放
        options.setShowNotificationInBackgroud(true); //默认为true
        options.setAcceptInvitationAlways(true); //默认添加好友时为true，是不需要验证的，改成需要验证为false)
        //设置自定义的文字提示
//        options.setNotifyText(new OnMessageNotifyListener() {
//
//            @Override
//            public String onNewMessageNotify(EMMessage message) {
//                //可以根据message的类型提示不同文字，这里为一个简单的示例
//                return "你的好基友" + message.getFrom() + "发来了一条消息哦";
//            }
//
//            @Override
//            public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
//                return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//            }
//
//            @Override
//            public String onSetNotificationTitle(EMMessage emMessage) {
//                return null;
//            }
//
//            @Override
//            public int onSetSmallIcon(EMMessage emMessage) {
//                return 0;
//            }
//        });

        notifier = createNotifier();
        notifier.init(appContext);

        notifier.setNotificationInfoProvider(getNotificationListener());
    }

    public static HXSDKHelper getInstance(){
        return me;
    }

    /**
     * 检查是否已经登录过
     * @return
     */
    public boolean isLogined(){
        return EMChat.getInstance().isLoggedIn();
    }

    protected HXNotifier.HXNotificationInfoProvider getNotificationListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    protected void initListener(){
        Log.d(TAG, "init listener");

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }else{
                    onConnectionDisconnected(error);
                }
            }

            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };

        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }

    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}


    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}


    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}

    /**
     * handle the connection disconnect
     * @param error see {@link EMError}
     */
    protected void onConnectionDisconnected(int error){}
}
