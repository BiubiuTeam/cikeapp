package com.jaf.jcore;

import android.app.Activity;

import com.easemob.EMEventListener;
import com.easemob.chat.EMChatManager;
//import com.yunkairichu.cike.utils.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

//    @Override
//    protected void initListener(){
//        super.initListener();
//        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
//        if(callReceiver == null){
//            callReceiver = new CallReceiver();
//        }
//
//        //ע��ͨ���㲥������
//        appContext.registerReceiver(callReceiver, callFilter);
//        //ע����Ϣ�¼�����
//        initEventListener();
//    }

    /**
     * ȫ���¼�����
     * ��Ϊ���ܻ���UIҳ���ȴ��������Ϣ������һ�����UIҳ���Ѿ���������Ͳ���Ҫ�ٴδ���
     * activityList.size() <= 0 ��ζ������ҳ�涼�Ѿ��ں�̨���У������Ѿ��뿪Activity Stack
     */
//    protected void initEventListener() {
//        eventListener = new EMEventListener() {
//
//            @Override
//            public void onEvent(EMNotifierEvent event) {
//
//                switch (event.getEvent()) {
//                    case EventNewMessage:
//                    {
//                        EMMessage message = (EMMessage)event.getData();
//                        EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
//
//                        //Ӧ���ں�̨������Ҫˢ��UI,֪ͨ����ʾ����Ϣ
//                        if(activityList.size() <= 0){
//                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
//                        }
//
//                        break;
//                    }
//                    // below is just giving a example to show a cmd toast, the app should not follow this
//                    // so be careful of this
//                    case EventNewCMDMessage:
//                    {
//                        EMMessage message = (EMMessage)event.getData();
//                        EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
//
//                        EMLog.d(TAG, "�յ�͸����Ϣ");
//                        //��ȡ��Ϣbody
//                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
//                        final String action = cmdMsgBody.action;//��ȡ�Զ���action
//
//                        //��ȡ��չ���� �˴�ʡ��
//                        //message.getStringAttribute("");
//                        EMLog.d(TAG, String.format("͸����Ϣ��action:%s,message:%s", action,message.toString()));
//                        final String str = appContext.getString(R.string.receive_the_passthrough);
//
//                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
//                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
//
//                        //ע��ͨ���㲥������
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
//
//                        break;
//                    }
//                    // add other events in case you are interested in
//                    default:
//                        break;
//                }
//
//            }
//        };
//
//        EMChatManager.getInstance().registerEventListener(eventListener);
//    }

    /**
     * �Զ���֪ͨ����ʾ����
     * @return
     */
//    @Override
//    protected HXNotificationInfoProvider getNotificationListener() {
//        //���Ը���Ĭ�ϵ�����
//        return new HXNotificationInfoProvider() {
//
//            @Override
//            public String getTitle(EMMessage message) {
//                //�޸ı���,����ʹ��Ĭ��
//                return null;
//            }
//
//            @Override
//            public int getSmallIcon(EMMessage message) {
//                //����Сͼ�꣬����ΪĬ��
//                return 0;
//            }
//
//            @Override
//            public String getDisplayedText(EMMessage message) {
//                // ����״̬������Ϣ��ʾ�����Ը���message����������Ӧ��ʾ
//                String ticker = CommonUtils.getMessageDigest(message, appContext);
//                if(message.getType() == EMMessage.Type.TXT){
//                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[����]");
//                }
//
//                return message.getFrom() + ": " + ticker;
//            }
//
//            @Override
//            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                return null;
//                // return fromUsersNum + "�����ѣ�������" + messageNum + "����Ϣ";
//            }
//
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
//                //���õ��֪ͨ����ת�¼�
//                Intent intent = new Intent(appContext, ChatActivity.class);
//                EMMessage.ChatType chatType = message.getChatType();
//                if (chatType == EMMessage.ChatType.Chat) { // ������Ϣ
//                    intent.putExtra("userId", message.getFrom());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//                } else { // Ⱥ����Ϣ
//                    // message.getTo()ΪȺ��id
//                    intent.putExtra("groupId", message.getTo());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
//                }
//                return intent;
//            }
//        };
//    }



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
//    public HXNotifier createNotifier(){
//        return new HXNotifier(){
//            public synchronized void onNewMsg(final EMMessage message) {
//                if(EMChatManager.getInstance().isSlientMessage(message)){
//                    return;
//                }
//
//                String chatUsename = null;
//                List<String> notNotifyIds = null;
//                // ��ȡ���õĲ���ʾ����Ϣ���û�����Ⱥ��ids
//                if (message.getChatType() == EMMessage.ChatType.Chat) {
//                    chatUsename = message.getFrom();
//                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledGroups();
//                } else {
//                    chatUsename = message.getTo();
//                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
//                }
//
//                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
//                    // �ж�app�Ƿ��ں�̨
//                    if (!EasyUtils.isAppRunningForeground(appContext)) {
//                        EMLog.d(TAG, "app is running in backgroud");
//                        sendNotification(message, false);
//                    } else {
//                        sendNotification(message, true);
//
//                    }
//
//                    viberateAndPlayTone(message);
//                }
//            }
//        };
//    }

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
