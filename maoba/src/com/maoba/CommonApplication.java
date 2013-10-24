package com.maoba;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.maoba.bean.LocationChangedListener;
import com.maoba.db.DataBaseAdapter;
/**
 * 应用全局变量
 * @author Zhoujun
 * 说明：	1、可以缓存一些应用全局变量。比如数据库操作对象
 */
public class CommonApplication extends Application {
	/**
	 * Singleton pattern
	 */
	private static CommonApplication instance;
	public BMapManager mBMapMan = null;//地图管理类；
	public String mStrKey = "82069EB19267A400E80733137A0A8A4C2B5D6F9A";
	boolean m_bKeyRight = true;	
	private Location lastLocation;//位置
	private LocationListener mLocationListener;//定位事件
	private LocationChangedListener changedListener;
	/**
	 * 数据库操作类
	 * @return
	 */
	private DataBaseAdapter dataBaseAdapter;
	
	public static CommonApplication getInstance() {
		return instance;
	}
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				CommonApplication.instance.m_bKeyRight = false;
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		dataBaseAdapter = new DataBaseAdapter(this);
		dataBaseAdapter.open();
	}
	/**
	 *初始化地图信息
	 */
	private void initBMapInfo(){
		mBMapMan = new BMapManager(this);
		mBMapMan.init(this.mStrKey, new MyGeneralListener());
		mBMapMan.getLocationManager().setNotifyInternal(10, 5);
		// 注册定位事件
	    mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				//Log.e("onLocationChanged-----", ""+location.getLatitude());
				if(location != null){
					lastLocation = location;
					if(changedListener!=null){
						changedListener.onLocationChanged(location);
					}
					
				}
				mBMapMan.getLocationManager().removeUpdates(mLocationListener);
				mBMapMan.getLocationManager().disableProvider(MKLocationManager.MK_GPS_PROVIDER);
				mBMapMan.stop();
			}
	    };
	    startLocation(changedListener);
	}
	/**
	 * 开始定位
	 * @param changedListener 如果为空.就不需要实时请求
	 */
	public void startLocation(LocationChangedListener changedListener){
		if(mBMapMan == null) {
			mBMapMan = new BMapManager(this);
			mBMapMan.init(mStrKey, new CommonApplication.MyGeneralListener());
		}
		this.changedListener = changedListener;
	    mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
	    mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_GPS_PROVIDER);
	    mBMapMan.start();
	}
	@Override
	public void onTerminate() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
	
	public Location getLastLocation(){
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
