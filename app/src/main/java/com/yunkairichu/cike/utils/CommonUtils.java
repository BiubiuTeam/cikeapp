package com.yunkairichu.cike.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.yunkairichu.cike.main.Constant;
import com.yunkairichu.cike.main.R;

import java.util.List;

/**
 * Created by vida2009 on 2015/5/19.
 */
public class CommonUtils {

    /**
     * ��������Ƿ����
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * ���Sdcard�Ƿ����
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    /**
     * ������Ϣ���ݺ���Ϣ���ͻ�ȡ��Ϣ������ʾ
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // λ����Ϣ
//                if (message.direct == EMMessage.Direct.RECEIVE) {
//                    //��sdk���ᵽ��ui�У�ʹ�ø��򵥲�����Ļ�ȡstring����
////              digest = EasyUtils.getAppResourceString(context, "location_recv");
//                    digest = getString(context, R.string.location_recv);
//                    digest = String.format(digest, message.getFrom());
//                    return digest;
//                } else {
////              digest = EasyUtils.getAppResourceString(context, "location_prefix");
//                    digest = getString(context, R.string.location_prefix);
//                }
                break;
            case IMAGE: // ͼƬ��Ϣ
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// ������Ϣ
                digest = getString(context, R.string.voice);
                break;
            case VIDEO: // ��Ƶ��Ϣ
//                digest = getString(context, R.string.video);
                break;
            case TXT: // �ı���Ϣ
                if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }else{
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                }
                break;
            case FILE: //��ͨ�ļ���Ϣ
//                digest = getString(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }


    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

}
