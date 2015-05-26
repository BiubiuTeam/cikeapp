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
//		// 如果app启用了远程的service，此application:onCreate会被调用2次
//		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
//		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
//
//		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.easemob.chatuidemo")) {
//			Log.e(TAG, "enter the service process!");
//			//"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
//
//			// 则此application::onCreate 是被service 调用的，直接返回
//			return;
//		}

        EMChat.getInstance().setAutoLogin(true);
        EMChat.getInstance().init(applicationContext);

        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
        //JPushInterface.setDebugMode(Constant.Debug.DEBUG); 	// 设置开启日志,发布时请关闭日志
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
     * 设置用户名
     *
     * @param user
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 获取当前登陆用户名
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
