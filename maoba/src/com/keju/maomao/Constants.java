package com.keju.maomao;

/**
 * 常量类
 * 
 * @author Zhoujun 说明： 1、一些应用常量在此定义 2、常量包括：一些类型的定义，在其他程序中不能够出现1 2 3之类的值。
 */
public class Constants {
	/**
	 * 生日设置滚轮显示项目数
	 */
	public static final int TIME_LIST_NUMBER=3;
	/** 
	 * 用来标识请求照相功能的activity
	 */
	public static final int CAMERA_WITH_DATA = 3023;
	/** 
	 * 用来标识请求gallery的activity 
	 * */
	public static final int PHOTO_PICKED_WITH_DATA = 3021;
	/**
	 * 应用文件存放目录
	 */
	public static final String APP_DIR_NAME = "maomao";
	
	public static final int NOTI_LETTER = 3;//私信通知
	public static final int NOTI_SYSTEM=21;//系统通知
	/**
	 * 网络请求状态
	 */
	public static final int REQUEST_SUCCESS = 0;// 成功
	public static final int REQUEST_FAILD = 1;
	/**
	 * 登录类型
	 */
	public static final int LOGIN_COMMON = 0;
	public static final int LOGIN_SINA = 1;
	public static final int LOGIN_QQ = 2;
	/**
	 * intent code
	 */
	public static final String EXTRA_DATA = "extra_data";// 跳转绑定的数据；
	public static final String INTENT_BIRTHDAY = "com.maoba.intent.action.BirthdaySetActivity";
	public static final String INTENT_SIGNATURE = "com.maoba.intent.action.PersonalizedSignatureActivity";
	public static final String INTENT_NICKNAME = "com.maoba.intent.action.NickNameActivity";
	public static final String INTENT_PASSWORD="com.maoba.intent.action.ChangingPasswordActivity";
    public static final int INDEX = 0;//调转回掉 标志位
	/**
	 * 分页数据
	 */
	public static final int PAGE_SIZE = 10;
	public static final String NO_DATA = "1";
	public static final String LOAD_ALL = "2";

	/**
	 * 微博绑定类型，点击账号绑定和新浪微博
	 */
	public static final String EXTRA_BIND_FROM = "extra_bind_from";
	public static final String BIND_WEIBO = "bind_weibo";// 微博
	public static final String BIND_RENREN = "bind_renren";// 绑定人人

	/**
	 * 微博绑定的request code
	 */
	public static final int REQUEST_CODE_BIND_WEIBO = 11;
	public static final int REQUEST_CODE_BIND_RENREN = 12;

	/**
	 * 新浪微博配置
	 */
	public static final String WEIBO_CONSUMER_KEY = "3250026215";// 替换为开发者的appkey，例如"1646212960";
	public static final String WEIBO_CONSUMER_SECRET = "149d5190f01fe9d6189abfa69fb84398";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";
	public static final String WEIBO_REDIRECT_URL = "http://www.maomao.com";// 微博应用回调地址
	public static final String WEIBO_USER_UID = "1291843462";

	/**
	 * 腾讯微博配置
	 */
	public static final String TENCENT_APP_ID = "100559005";//app id  801237383
	public static final String TENCENT_APP_KEY = "0e95f60635ebe70775a49737b4fdbd7c";//app key
	
	public static final String TENCENT_REDIRECT_URL = "http://www.eemedia.cn/app_download.aspx";

	/**
	 * 人人网
	 */
	public static final String RENREN_APP_ID = "211176";// app id
	public static final String RENREN_API_KEY = "979175fc39c14a8eba6ea78f6e876c01";// api
																					// key
	public static final String RENREN_SECRET_KEY = "a113d3aa3cde431eb499f6fc37ff1e30";// secret
																						// key

	/**
	 * activity RequestCode
	 */
	public static final int REQUEST_CODE_REGISTER = 1;
	public static final int BIRTHDAYNUM = 4;
	public static final int SIGNATURENUM = 5;
	public static final int NICKNAMENUM = 6;
	public static final int PASSWORDNUMBER=7;
	public static final int DISTRICT=8;
	
	public static final int REQUEST_CODE_CHOOSE_AREA = 9;//选择地区
}
