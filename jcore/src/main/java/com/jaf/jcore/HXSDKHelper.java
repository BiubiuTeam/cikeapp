package com.jaf.jcore;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

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
     * the notifier
     */
//    protected HXNotifier notifier = null;

    /**
     * subclass can override this api to return the customer notifier
     *
     * @return
     */
//    protected HXNotifier createNotifier(){
//        return new HXNotifier();
//    }
//
//    public HXNotifier getNotifier(){
//        return notifier;
//    }

    /**
     * application context
     */
    protected Context appContext = null;

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
     * ���ų�ʼ��SDK��������
     * ����true�����ȷ��ʼ��������false���������Ϊfalse�����ں����ĵ����в�Ҫ�����κκͻ�����صĴ���
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
        // Ĭ�ϵ�app�����԰���ΪĬ�ϵ�process name�����У�����鵽��process name����app��process name����������
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
        //initListener();
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

//        notifier = createNotifier();
//        notifier.init(appContext);
//
//        notifier.setNotificationInfoProvider(getNotificationListener());
    }

    public static HXSDKHelper getInstance(){
        return me;
    }
}
