package com.keju.maomao.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * SharedPreferences工具类
 * 
 * @author Zhoujun 说明：SharedPreferences的操作工具类，需要缓存到SharedPreferences中的数据在此设置。
 */
public class SharedPrefUtil {

	public static final String IS_FIRST_LOGIN = "is_first_login";// 第一次进入
	public static final String SINA_UID = "sina_uid";// 新浪微博唯一id

	public static final String WEIBO_ACCESS_TOKEN = "weibo_access_token";// 新浪微博令牌
	public static final String WEIBO_EXPIRES_IN = "weibo_expires_in";// 新浪微博令牌时间
	public static final String WEIBO_ACCESS_CURR_TIME = "weibo_sccess_curr_time";// 新浪微博授权时间

	public static final String QQ_ACCESS_TOKEN = "qq_access_token";// 新浪微博令牌
	public static final String QQ_EXPIRES_IN = "qq_expires_in";// 新浪微博令牌时间
	public static final String QQ_OPENID = "qq_openid";
	public static final String QQ_ACCESS_CURR_TIME = "qq_sccess_curr_time";// 新浪微博授权时间

	public static final String UID = "uid";// 用户id
	public static final String IS_INFO_COMPLETE = "is_info_complete";// 个人信息补全

	public static final String OPEN_ID = "open_id";// 开放的open_id
	public static final String LOGIN_TYPE = "login_type";// 登录方式 0 普通登陆 1 新浪登陆 2
													// qq登录
    public static final String PASSWORD = "password"; //密码
    
    
    public static final String CITYNAME="city_name"; //保存选择的城市名字
    public static final String PROVINCEID="province_id"; //保存选择的省得id
    public static final String IS_FIRST_CITYACTIVITY="is_first_cityactivity"; //是不是第一次进入城市切换界面
    
    
   
    
    
	/**
	 * 判断是否是第一次进入应用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFistLogin(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(IS_FIRST_LOGIN, true);
	}

	/**
	 * 如果已经进入应用，则设置第一次登录为false
	 * 
	 * @param context
	 */
	public static void setFistLogined(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putBoolean(IS_FIRST_LOGIN, false);
		e.commit();
	}

	/**
	 * 判断用户是否登录
	 */
	public static boolean isLogin(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return (sp.getInt(UID, 0) > 0);
	}

	/**
	 * 保存uid
	 * 
	 * @param context
	 * @param uid
	 */
	public static void setUid(Context context, int uid) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putInt(UID, uid);
		e.commit();
	}

	public static int getUid(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(UID, 0);
	}

	/**
	 * 保存open_id
	 * 
	 * @param context
	 * @param uid
	 */
	public static void setOpenId(Context context, String openid) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putString(OPEN_ID, openid);
		e.commit();
	}

	public static String getOpenId(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(OPEN_ID, null);
	}
	
	/**
	 * 保存用户选择的城市
	 * 
	 * @param context
	 * @param cityName
	 */
	public static void setCityName(Context context, String cityName) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putString(CITYNAME, cityName);
		e.commit();
	}

	public static String getCityName(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(CITYNAME, null);
	}
	/**
	 * 保存用户选择省的id
	 * 
	 * @param context
	 * @param provinceId
	 */
	public static void setProvinceId(Context context, int provinceId) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putInt(PROVINCEID, provinceId);
		e.commit();
	}

	public static int getProvinceId(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(PROVINCEID, 0);
	}
	
	/**
	 * 判断是否是第一次进入城市切换界面
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFistCityActivity(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(IS_FIRST_CITYACTIVITY, true);
	}

	/**
	 * 如果已经进入城市切换界面，则设置第一次为false
	 * 
	 * @param context
	 */
	public static void setFistCityActivity(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putBoolean(IS_FIRST_CITYACTIVITY, false);
		e.commit();
	}


	/**
	 * 保存登陆的方式
	 * 
	 * @param context
	 * @param uid
	 */
	public static void setLoginType(Context context, int logintype) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putInt(LOGIN_TYPE, logintype);
		e.commit();
	}

	public static int getLoginType(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(LOGIN_TYPE, 0);
	}
	
	/**
	 * 保存登陆的方式
	 * 
	 * @param context
	 * @param uid
	 */
	public static void setPassword(Context context, String passWord) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putString(PASSWORD, passWord);
		e.commit();
	}

	public static String getPassword(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(PASSWORD, null);
	}

	// -----------------------------新浪微博验证信息-----------------
	/**
	 * 获取新浪微博openid
	 * 
	 * @param context
	 * @return
	 */
	public static String getWeiboUid(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(SINA_UID, null);
	}

	/**
	 * 设置微博绑定信息
	 * 
	 * @param context
	 * @param access_token
	 * @param expires_in
	 */
	public static void setWeiboInfo(Context context, String sina_uid, String access_token, String expires_in,
			String access_curr_time) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putString(SINA_UID, sina_uid);
		e.putString(WEIBO_ACCESS_TOKEN, access_token);
		e.putString(WEIBO_EXPIRES_IN, expires_in);
		e.putString(WEIBO_ACCESS_CURR_TIME, access_curr_time);
		e.commit();
	}

	/**
	 * 清除微博绑定
	 * 
	 * @param context
	 * @return
	 */
	public static void clearWeiboBind(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().remove(WEIBO_ACCESS_TOKEN).remove(WEIBO_EXPIRES_IN).commit();
	}

	public static String getWeiboAccessToken(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(WEIBO_ACCESS_TOKEN, null);
	}

	public static String getWeiboExpiresIn(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(WEIBO_EXPIRES_IN, null);
	}

	public static String getWeiboAccessCurrTime(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(WEIBO_ACCESS_CURR_TIME, null);
	}

	/**
	 * 检测新浪微博是否绑定
	 */
	public static boolean checkWeiboBind(Context context) {
		String WeiboAccessToken = getWeiboAccessToken(context);
		String WeiboExpiresIn = getWeiboExpiresIn(context);
		String weiboAccessCurrTime = getWeiboAccessCurrTime(context);
		if (WeiboAccessToken == null || WeiboExpiresIn == null || weiboAccessCurrTime == null) {
			return false;
		} else {
			long currTime = System.currentTimeMillis();
			long accessCurrTime = Long.parseLong(weiboAccessCurrTime);
			long expiresIn = Long.parseLong(WeiboExpiresIn);
			if ((currTime - accessCurrTime) / 1000 > expiresIn) {
				return false;
			} else {
				return true;
			}
		}
	}

	// -----------------------------腾讯微博验证信息-----------------
	/**
	 * 设置腾讯微博信息
	 * 
	 * @param context
	 * @param access_token
	 * @param expires_in
	 * @param access_curr_time
	 */
	public static void setQQInfo(Context context, String access_token, String expires_in, String openid,
			String access_curr_time) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putString(QQ_ACCESS_TOKEN, access_token);
		e.putString(QQ_EXPIRES_IN, expires_in);
		e.putString(QQ_OPENID, openid);
		e.putString(QQ_ACCESS_CURR_TIME, access_curr_time);
		e.commit();
	}

	public static String getQQAccessToken(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(QQ_ACCESS_TOKEN, null);
	}

	public static String getQQExpiresIn(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(QQ_EXPIRES_IN, null);
	}

	public static String getQQOpenid(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(QQ_OPENID, null);
	}

	public static String getQQAccessCurrTime(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(QQ_ACCESS_CURR_TIME, null);
	}

	public static void clearQQBind(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().remove(QQ_ACCESS_TOKEN).remove(QQ_EXPIRES_IN).remove(QQ_OPENID).remove(QQ_ACCESS_CURR_TIME).commit();
	}

	/**
	 * 检查腾讯微博是否绑定
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkQQBind(Context context) {
		String qqAccessToken = getQQAccessToken(context);
		String qqExpiresIn = getQQExpiresIn(context);
		String qqAccessCurrTime = getQQAccessCurrTime(context);
		if (qqAccessToken == null || qqExpiresIn == null || qqAccessCurrTime == null) {
			return false;
		} else {
			long currTime = System.currentTimeMillis();
			long accessCurrTime = Long.parseLong(qqAccessCurrTime);
			long expiresIn = Long.parseLong(qqExpiresIn);
			if ((currTime - accessCurrTime) / 1000 > expiresIn) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 获得检测间隔
	 * 
	 * @param con
	 * @return
	 */
	public static final String CHECK_UPDATE_TIME_KEY = "check_update_time_key";// 轮询时间

	public static long getUpdateInterval(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getLong(CHECK_UPDATE_TIME_KEY, 5 * 60 * 1000);
	}

	/**
	 * 清除用户信息
	 * 
	 * @param context
	 */
	public static void clearUserinfo(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.remove(UID).remove(WEIBO_ACCESS_TOKEN).remove(WEIBO_EXPIRES_IN).remove(SINA_UID);
		e.remove(QQ_ACCESS_TOKEN).remove(QQ_EXPIRES_IN).remove(QQ_OPENID).remove(QQ_ACCESS_CURR_TIME);
		e.commit();
	}

	/**
	 * 设置补充信息完整
	 * 
	 * @param context
	 */
	public static void setInfoComplete(Context context, boolean isComplete) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = sp.edit();
		e.putBoolean(IS_INFO_COMPLETE, isComplete);
		e.commit();
	}


}
