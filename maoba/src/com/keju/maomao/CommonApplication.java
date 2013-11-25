package com.keju.maomao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.keju.maomao.db.DataBaseAdapter;

/**
 * 应用全局变量
 * 
 * @author Zhoujun 说明： 1、可以缓存一些应用全局变量。比如数据库操作对象
 */
public class CommonApplication extends Application {
	/**
	 * Singleton pattern
	 */
	private static CommonApplication instance;
	public String mStrKey = "82069EB19267A400E80733137A0A8A4C2B5D6F9A";
	boolean m_bKeyRight = true;	
	private BDLocation lastLocation;//位置
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	/**
	 * 数据库操作类
	 * @return
	 */
	private DataBaseAdapter dataBaseAdapter;
	
	public static CommonApplication getInstance() {
		return instance;
	}
	/**
	 *初始化地图信息
	 */
	public void initBMapInfo(){
		mLocationClient = new LocationClient( this );
		/**——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK(mStrKey);
		mLocationClient.registerLocationListener( myListener );
		LocationClientOption option = new LocationClientOption();
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPriority(LocationClientOption.NetWorkFirst);//网络优先
		option.setOpenGps(true);
		option.setScanSpan(500);//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.disableCache(true);		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			lastLocation = location;
			mLocationClient.stop();
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
			
		}
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public BDLocation getLastLocation(){
		return lastLocation;
	}
	/**
	 * 获得数据库操作对象
	 * @return
	 */
	public DataBaseAdapter getDbAdapter(){
		return this.dataBaseAdapter;
	}
	
	/**
	 * 缓存activity对象索引
	 */
	public List<Activity> activities = new ArrayList<Activity>();;
	public List<Activity> getActivities(){
		return activities;
	}
	public void addActivity(Activity mActivity) {
		activities.add(mActivity);
	}

}
