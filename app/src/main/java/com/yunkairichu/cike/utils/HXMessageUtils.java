package com.yunkairichu.cike.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.NotificationCompat;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.jaf.jcore.HXSDKHelper;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSingleTitlePull;
import com.yunkairichu.cike.main.ActivityChat;
import com.yunkairichu.cike.main.ToolLog;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by haowenliang on 15/6/18.
 */
public class HXMessageUtils implements EMEventListener {

    private final static String TAG = "Holaween";
    protected static int notifyID = 0520; // start notification id
    private ResponseSingleTitlePull responseSingleTitlePull = null;
    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        switch (emNotifierEvent.getEvent()) {
            case EventNewMessage:
            {
                //获取到message
                EMMessage message = (EMMessage) emNotifierEvent.getData();
                if (isApplicationBroughtToBackground(mContext)) {
                    //background
                    sendNotification(message);
                    HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                }
                break;
            }
            default:
                break;
        }
    }

    protected NotificationManager notificationManager = null;
    public Context mContext;
    private static HXMessageUtils mInstance = new HXMessageUtils();

    public static HXMessageUtils getInstance(){
        return mInstance;
    }

    public void setmContext(Context mContext) {
//        Toast.makeText(mContext,"MessageUtils Init",Toast.LENGTH_SHORT).show();
        this.mContext = mContext;
        notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage
                , EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventNewCMDMessage
                , EMNotifierEvent.Event.EventReadAck});
        EMChat.getInstance().setAppInited();
    }

    private void sendNotification(EMMessage message){
        try{
            // notification titile
            String contentTitle = "此刻";
            String contentText = "";
            responseSingleTitlePull = null;

            switch (message.getType()){
                case IMAGE:
                    contentText += "您收到一张新图片";
                    break;
                case TXT:
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    contentText += txtBody.getMessage();
                    break;
                case VOICE:
                    contentText += "您收到一段新语音";
                    break;
                default:
                    contentText += "您收到一条新信息";
                    break;
            }

                       // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(mContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setContentText(contentText);
            mBuilder.setTicker(contentTitle + ": " + contentText);

            int iTitleId = 0;
            String toDeviceId = "";
            String receiptId = "";
            ToolLog.dbg(message.getBody().toString());
            try {
                iTitleId = Integer.parseInt(message.getStringAttribute("broadcast"));
                toDeviceId = message.getStringAttribute("from");
                receiptId = message.getTo();
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
            getChatIntentData((long) iTitleId, mBuilder);

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "notification message utils");
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void getChatIntentData(long titleId, final NotificationCompat.Builder mBuilder) {
        Http http = new Http();
        JSONObject jo = JsonPack.buildSingleTitlePull(titleId, 0, 0, "");

        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponseSingleTitlePull(JacksonWrapper.json2Bean(response, ResponseSingleTitlePull.class));
                ToolLog.dbg("json:" + response.toString());
                ToolLog.dbg("getchat finish");
                ToolLog.dbg("dvcID:" + responseSingleTitlePull.getReturnData().getDvcId());

                Intent msgIntent = new Intent(mContext, ActivityChat.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("titleInfo", responseSingleTitlePull.getReturnData());
                bundle.putSerializable("resSearchTitle", null);
                bundle.putString("fromAct", "Background");
                msgIntent.putExtra("bitmap", (byte[]) null);
                msgIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(pendingIntent);

                Notification notification = mBuilder.build();
                notificationManager.notify(notifyID, notification);

                notifyID++;
            }
        });
    }

    public void setResponseSingleTitlePull(ResponseSingleTitlePull responseSingleTitlePull) {
        this.responseSingleTitlePull = responseSingleTitlePull;
    }
}
