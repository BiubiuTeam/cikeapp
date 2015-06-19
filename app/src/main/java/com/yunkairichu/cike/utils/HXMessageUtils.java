package com.yunkairichu.cike.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.NotificationCompat;
import com.jaf.jcore.HXSDKHelper;
import com.yunkairichu.cike.main.ActivityChatview;

import java.util.List;

/**
 * Created by haowenliang on 15/6/18.
 */
public class HXMessageUtils implements EMEventListener {

    private final static String TAG = "Holaween";
    protected static int notifyID = 0520; // start notification id
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
        Toast.makeText(mContext,"MessageUtils Init",Toast.LENGTH_SHORT).show();
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
            String contentTitle = "Holaween";
            String contentText = "You have a new message!!!";

                       // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(mContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            Intent msgIntent = new Intent(mContext,ActivityChatview.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setContentText(contentText);
            mBuilder.setContentIntent(pendingIntent);

            Notification notification = mBuilder.build();
            notificationManager.notify(notifyID, notification);

            notifyID++;
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG,"notification message utils");
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
}
