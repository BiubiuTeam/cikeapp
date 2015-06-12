package com.jaf.jcore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

//import com.yunkairichu.cike.utils.User;

/**
 * Created by vida2009 on 2015/5/19.
 */
public class DemoHXSDKHelper extends HXSDKHelper{
    private static final String TAG = "DemoHXSDKHelper";

    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    /**
     * HuanXin ID in cache
     */
    protected String hxId = null;

    /**
     * contact list in cache
     */
    //private Map<String, User> contactList;
    //private CallReceiver callReceiver;

    /**
     * ������¼foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity);
        }
    }

    public void popActivity(Activity activity){
        activityList.remove(activity);
    }

//    @Override
//    protected void initHXOptions(){
//        super.initHXOptions();
//
//        // you can also get EMChatOptions to set related SDK options
//        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
//
//    }

    @Override
    protected void initListener(){
        super.initListener();
        //注册消息事件监听
        //initEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {

            @Override
            public void onEvent(EMNotifierEvent event) {

                switch (event.getEvent()) {
                    case EventNewMessage:
                    {
                        EMMessage message = (EMMessage)event.getData();
                        Log.e(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());

                        //应用在后台，不需要刷新UI,通知栏提示新消息
                        if(activityList.size() <= 0){
                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        }
                        //HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);

                        break;
                    }
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage:
                    {
                        EMMessage message = (EMMessage)event.getData();
                        EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
//
//                        EMLog.d(TAG, "收到透传消息");
//                        //获取消息body
//                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
//                        final String action = cmdMsgBody.action;//获取自定义action
//
//                        //获取扩展属性 此处省略
//                        //message.getStringAttribute("");
//                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
//                        final String str = "Receive the passthrough:action";
//
//                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
//                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
//
//                        //注册通话广播接收者
//                        appContext.registerReceiver(new BroadcastReceiver(){
//
//                            @Override
//                            public void onReceive(Context context, Intent intent) {
//                                // TODO Auto-generated method stub
//                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
//                            }
//
//                        }, cmdFilter);
//
//
//                        Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
//                        broadcastIntent.putExtra("cmd_value", str+action);
//                        appContext.sendBroadcast(broadcastIntent, null);

                        break;
                    }
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);
    }

    /**
     * 自定义通知栏提示内容
     * @return
     */
    @Override
    protected HXNotifier.HXNotificationInfoProvider getNotificationListener() {
        //���Ը���Ĭ�ϵ�����
        return new HXNotifier.HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
//                // ����״̬������Ϣ��ʾ�����Ը��message����������Ӧ��ʾ
//                String ticker = CommonUtils.getMessageDigest(message, appContext);
//                if(message.getType() == EMMessage.Type.TXT){
//                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[����]");
//                }
//
//                return message.getFrom() + ": " + ticker;
                //return message.getFrom() + ": ";
                return "您收到一条新消息";
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "�����ѣ�������" + messageNum + "����Ϣ";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
//                //���õ��֪ͨ����ת�¼�
                Intent intent = new Intent(appContext, Activity.class);
//                EMMessage.ChatType chatType = message.getChatType();
//                if (chatType == EMMessage.ChatType.Chat) { // ������Ϣ
//                    intent.putExtra("userId", message.getFrom());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//                } else { // Ⱥ����Ϣ
//                    // message.getTo()ΪȺ��id
//                    intent.putExtra("groupId", message.getTo());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
//                }
                return intent;
            }
        };
    }



//    @Override
//    protected void onConnectionConflict(){
//        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("conflict", true);
//        appContext.startActivity(intent);
//    }

//    @Override
//    protected void onCurrentAccountRemoved(){
//        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
//        appContext.startActivity(intent);
//    }


//    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier(){
        return new HXNotifier(){
            public synchronized void onNewMsg(final EMMessage message) {
                Log.e(TAG, "hasmsg");
                if(EMChatManager.getInstance().isSlientMessage(message)){
                    return;
                }
                Log.e(TAG, "hasmsg");
                String chatUsename = null;
                List<String> notNotifyIds = null;
//                // 获取设置的不提示新消息的用户或者群组ids
//                if (message.getChatType() == EMMessage.ChatType.Chat) {
//                    chatUsename = message.getFrom();
//                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledGroups();
//                } else {
//                    chatUsename = message.getTo();
//                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
//                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        Log.e(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        Log.e(TAG, "app is running in foreroud");
                        sendNotification(message, true);

                    }

                    //viberateAndPlayTone(message);
                }
            }
        };
    }

    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel(){
        return (DemoHXSDKModel) hxModel;
    }

    public String getHXId(){
        if(hxId == null){
            hxId = hxModel.getHXId();
        }
        return hxId;
    }

    public void setHXId(String hxId){
        if (hxId != null) {
            if(hxModel.saveHXId(hxId)){
                this.hxId = hxId;
            }
        }
    }

    /**
     * ��ȡ�ڴ��к���user list
     *
     * @return
     */
//    public Map<String, User> getContactList() {
//        if (getHXId() != null && contactList == null) {
//            contactList = ((DemoHXSDKModel) getModel()).getContactList();
//        }
//
//        return contactList;
//    }

    /**
     * ���ú���user list���ڴ���
     *
     * @param contactList
     */
//    public void setContactList(Map<String, User> contactList) {
//        this.contactList = contactList;
//    }
//
//    @Override
//    public void logout(final EMCallBack callback){
//        endCall();
//        super.logout(new EMCallBack(){
//
//            @Override
//            public void onSuccess() {
//                // TODO Auto-generated method stub
//                setContactList(null);
//                getModel().closeDB();
//                if(callback != null){
//                    callback.onSuccess();
//                }
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//                // TODO Auto-generated method stub
//                if(callback != null){
//                    callback.onProgress(progress, status);
//                }
//            }
//
//        });
//    }

    void endCall(){
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
