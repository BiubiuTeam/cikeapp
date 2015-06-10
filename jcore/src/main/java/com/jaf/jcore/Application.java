package com.jaf.jcore;

import android.content.Context;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;

/**
 * Created by vida2009 on 2015/5/19.
 */
public class Application extends android.app.Application {
    private static final String TAG = "TEST";
    private static Application INSTANCE;
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public AppExtraInfo mAppExtraInfo;
    private static Context context;
    public static LocationClient MLOCATIONCLIENT;
    public MyLocationListener mMyLocationListener;

    public int isFirstCheckUnread = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (Application.class) {
            INSTANCE = this;
        }
        mAppExtraInfo = new AppExtraInfo();
        mRequestQueue = Volley.newRequestQueue(this);

        Context applicationContext = getApplicationContext();

//		int pid = android.os.Process.myPid();
//		String processAppName =  getAppName(pid);
//		// ���app������Զ�̵�service����application:onCreate�ᱻ����2��
//		// Ϊ�˷�ֹ����SDK����ʼ��2�Σ��Ӵ��жϻᱣ֤SDK����ʼ��1��
//		// Ĭ�ϵ�app�����԰���ΪĬ�ϵ�process name�����У����鵽��process name����app��process name����������
//
//		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.easemob.chatuidemo")) {
//			Log.e(TAG, "enter the service process!");
//			//"com.easemob.chatuidemo"Ϊdemo�İ������Լ���Ŀ��Ҫ�ĳ��Լ�����
//
//			// ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
//			return;
//		}

        //环信相关
        EMChat.getInstance().setAutoLogin(true);
        EMChat.getInstance().init(applicationContext);

        /**
         * debugMode == true ʱΪ�򿪣�sdk ����log�����������Ϣ
         * @param debugMode
         * ������������ʱ����Ҫ���ó�false
         */
        EMChat.getInstance().setDebugMode(true);//����������ʱ��Ҫ�ر�debugģʽ�����δ���رգ������ֳ����޷���������
        //JPushInterface.setDebugMode(Constant.Debug.DEBUG); 	// ���ÿ�����־,����ʱ��ر���־
        //JPushInterface.init(this);

        hxSDKHelper.onInit(applicationContext);

        EMChatOptions chatOptions = EMChatManager.getInstance().getChatOptions();
//        chatOptions.setNotifyBySoundAndVibrate(true); //默认为true 开启新消息提醒
//        chatOptions.setNoticeBySound(true); //默认为true 开启声音提醒
//        chatOptions.setNoticedByVibrate(true); //默认为true 开启震动提醒
//        chatOptions.setUseSpeaker(true); //默认为true 开启扬声器播放
//        chatOptions.setShowNotificationInBackgroud(true); //默认为true
//        chatOptions.setAcceptInvitationAlways(true); //默认添加好友时为true，是不需要验证的，改成需要验证为false)
//        //设置自定义的文字提示
//        chatOptions.setNotifyText(new OnMessageNotifyListener() {
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

        //百度地图相关
        MLOCATIONCLIENT = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        MLOCATIONCLIENT.registerLocationListener(mMyLocationListener);

        ToolGetLocationInfo.getInstance().initLocation();
    }

    public static final Application getInstance() {
        Application ret;
        synchronized (Application.class) {
            ret = INSTANCE;
        }
        return ret;
    }

    public static final LocationClient getLocationClient() {
        LocationClient ret;
        synchronized (Application.class) {
            ret = MLOCATIONCLIENT;
        }
        return ret;
    }

    /**
     * �����û���
     *
     * @param user
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * ��ȡ��ǰ��½�û���
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void cancelRequest(String tag) {
        mRequestQueue.cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    Cache.getInstance());
        }
        return this.mImageLoader;
    }


    public static class AppExtraInfo {
        public double lat;
        public double lon;
        public String dvcId;
        public String city;
        public String addrStr;
        public String school;
    }

    public void setAppExtraInfo(String dvcId, double lat, double lon) {
        mAppExtraInfo = getAppExtraInfo();
        mAppExtraInfo.dvcId = dvcId;
        mAppExtraInfo.lat = lat;
        mAppExtraInfo.lon = lon;
    }


    private AQuery aQuery;
    public AQuery getAQuery() {
        if(aQuery == null) {
            aQuery = new AQuery(this);
        }
        return aQuery;
    }

    public AppExtraInfo getAppExtraInfo() {
        return mAppExtraInfo;
    }

//	private String getAppName(int pID) {
//		String processName = null;
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		List l = am.getRunningAppProcesses();
//		Iterator i = l.iterator();
//		PackageManager pm = context.getPackageManager();
//		while (i.hasNext()) {
//			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
//			try {
//				if (info.pid == pID) {
//					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
//					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
//					// info.processName +"  Label: "+c.toString());
//					// processName = c.toString();
//					processName = info.processName;
//					return processName;
//				}
//			} catch (Exception e) {
//				// Log.d("Process", "Error>> :"+ e.toString());
//			}
//		}
//		return processName;
//	}

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

//            //Receive Location
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation){
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\ndirection : ");
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append(location.getDirection());
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//            }
//            Log.i("LocationApi", sb.toString());
            int ret = location.getLocType();
            if (ret==61||ret==65||ret==161) {
                ToolGetLocationInfo.getInstance().setLastLatitude(location.getLatitude());
                ToolGetLocationInfo.getInstance().setLastLongitude(location.getLongitude());
                if (location.getCity().equals("")) {
                    ToolGetLocationInfo.getInstance().setLastCity("海外");
                } else {
                    ToolGetLocationInfo.getInstance().setLastCity(location.getCity());
                }
                ToolGetLocationInfo.getInstance().setLastRecTime(System.currentTimeMillis());
                ToolGetLocationInfo.getInstance().stopLocation();
                ToolGetLocationInfo.getInstance().setFailFlag(0);
                ToolGetLocationInfo.getInstance().setFailTime(0);
            }
            else if (ToolGetLocationInfo.getInstance().getFailTime() < 10){
                ToolGetLocationInfo.getInstance().requestLocation();
                int failTime = ToolGetLocationInfo.getInstance().getFailTime() + 1;
                ToolGetLocationInfo.getInstance().setFailTime(failTime);
            }
            else {
                ToolGetLocationInfo.getInstance().stopLocation();
                ToolGetLocationInfo.getInstance().setFailTime(0);
            }
        }
    }
}
