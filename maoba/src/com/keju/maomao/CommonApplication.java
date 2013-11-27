package com.keju.maomao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
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
	public BMapManager mBMapManager = null;// 地图管理类；
	public static final String mStrKey = "x2mcje0YDuQOzk7c2GYGrwZw";
	boolean m_bKeyRight = true;
	private BDLocation lastLocation;// 位置
	LocationData locData = null;
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	/**
	 * 数据库操作类
	 * 
	 * @return
	 */
	private DataBaseAdapter dataBaseAdapter;

	public static CommonApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initBMapInfo();
		dataBaseAdapter = new DataBaseAdapter(this);
		dataBaseAdapter.open();
	}

	/**
	 * 初始化地图信息
	 */
	public void initBMapInfo() {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(this);
		}

		if (!mBMapManager.init(mStrKey, new MyGeneralListener())) {
			Toast.makeText(this, "BMapManager  初始化错误!", Toast.LENGTH_SHORT).show();
		}
		mLocationClient = new LocationClient(getApplicationContext());
		/**
		 * ——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		locData = new LocationData();
		//mLocationClient.setAK(mStrKey);
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setServiceName("com.baidu.location.f");
		option.setPriority(LocationClientOption.NetWorkFirst);// 网络优先
		option.setOpenGps(true);
		option.setScanSpan(500);// 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.disableCache(true);
		option.setCoorType("bd09ll"); // 设置坐标类型
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}

			lastLocation = location;
			mLocationClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}

		}
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	private class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				// Toast.makeText(CommonApplication.getInstance(), "您的网络出错啦！",
				// Toast.LENGTH_SHORT).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				// Toast.makeText(CommonApplication.getInstance(), "输入正确的检索条件！",
				// Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				// Toast.makeText(CommonApplication.getInstance(),
				// "请输入正确的授权Key！", Toast.LENGTH_SHORT).show();
				CommonApplication.getInstance().m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public BDLocation getLastLocation() {
		return lastLocation;
	}

	/**
	 * 获得数据库操作对象
	 * 
	 * @return
	 */
	public DataBaseAdapter getDbAdapter() {
		return this.dataBaseAdapter;
	}

	/**
	 * 缓存activity对象索引
	 */
	public List<Activity> activities = new ArrayList<Activity>();;

	public List<Activity> getActivities() {
		return activities;
	}

	public void addActivity(Activity mActivity) {
		activities.add(mActivity);
	}

}

