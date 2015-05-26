package com.jaf.jcore;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.easemob.chat.EMChat;

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
//		// Ĭ�ϵ�app�����԰���ΪĬ�ϵ�process name�����У�����鵽��process name����app��process name����������
//
//		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.easemob.chatuidemo")) {
//			Log.e(TAG, "enter the service process!");
//			//"com.easemob.chatuidemo"Ϊdemo�İ����������Լ���Ŀ��Ҫ�ĳ��Լ�����
//
//			// ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
//			return;
//		}

        EMChat.getInstance().setAutoLogin(true);
        EMChat.getInstance().init(applicationContext);

        /**
         * debugMode == true ʱΪ�򿪣�sdk ����log�����������Ϣ
         * @param debugMode
         * �������������ʱ����Ҫ���ó�false
         */
        EMChat.getInstance().setDebugMode(true);//�����������ʱ��Ҫ�ر�debugģʽ�����δ���رգ������ֳ����޷���������
        //JPushInterface.setDebugMode(Constant.Debug.DEBUG); 	// ���ÿ�����־,����ʱ��ر���־
        //JPushInterface.init(this);

        hxSDKHelper.onInit(applicationContext);
    }

    public static final Application getInstance() {
        Application ret;
        synchronized (Application.class) {
            ret = INSTANCE;
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
}
