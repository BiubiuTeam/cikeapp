package com.jaf.jcore;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by vida2009 on 2015/6/5.
 */
public class ToolGetLocationInfo {
    private LocationClient mLocationClient = null;
    private long lastRecTime = 0;
    private double lastLongitude = 0;
    private double lastLatitude = 0;
    private String lastCity = "海外";
    private int failTime = 0;
    private int failFlag = 1;   //这里我们认为，只要有数据即可;如果app进入的方式有变，就是第一次搜索的那个形式有变，这里需要改为0

    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author xuzhaohu
     *
     */
    private static class ToolGetLocationInfoHolder {
        private static ToolGetLocationInfo instance = new ToolGetLocationInfo();
    }

    /**
     * 私有的构造函数
     */
    private ToolGetLocationInfo() {

    }

    public static ToolGetLocationInfo getInstance() {
        return ToolGetLocationInfoHolder.instance;
    }

    protected void method() {
        System.out.println("SingletonInner");
    }

    public void initLocation(){
        mLocationClient = Application.getLocationClient();

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值百度经纬度
        int span=10000;
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms，我们用10s
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public void startLocation(){
        if(!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    public void requestLocation(){
        if(!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        else {
            mLocationClient.requestLocation();
        }
    }

    public void stopLocation(){
        if(mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public boolean isLocationClient(){
        if(mLocationClient != null) return true;
        return false;
    }

    public void setLastRecTime(long lastRecTime){
        this.lastRecTime = lastRecTime;
    }

    public void setLastLongitude(double lastLongitude){
        this.lastLongitude = lastLongitude;
    }

    public void setLastLatitude(double lastLatitude){
        this.lastLatitude = lastLatitude;
    }

    public void setLastCity(String lastCity){
        this.lastCity = lastCity;
    }

    public void setFailTime(int failTime){
        this.failTime = failTime;
    }

    public void setFailFlag(int failFlag){
        this.failFlag = failFlag;
    }

    public long getLastRecTime(){
        return this.lastRecTime;
    }

    public double getLastLongitude(){
        return this.lastLongitude;
    }

    public double getLastLatitude(){
        return this.lastLatitude;
    }

    public String getLastCity(){
        return this.lastCity;
    }

    public int getFailTime(){
        return this.failTime;
    }

    public int getFailFlag(){
        return this.failFlag;
    }
}
