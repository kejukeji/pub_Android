package com.maoba;

/**
 * 常量类
 * 
 * @author Zhoujun 说明： 1、一些应用常量在此定义 2、常量包括：一些类型的定义，在其他程序中不能够出现1 2 3之类的值。
 */
public class Constants {
	/**
	 * 应用文件存放目录
	 */
	public static final String APP_DIR_NAME = "maomao";

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
	public static final String WEIBO_CONSUMER_KEY = "3955024569";// 替换为开发者的appkey，例如"1646212960";
	public static final String WEIBO_CONSUMER_SECRET = "b2071565ef1f2514504ccefb16866b25";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";
	public static final String WEIBO_REDIRECT_URL = "http://www.eemedia.cn/";// 微博应用回调地址
	public static final String WEIBO_USER_UID = "1291843462";

	/**
	 * 腾讯微博配置
	 */
	public static final String TENCENT_APP_ID = "100492496";// app id
	public static final String TENCENT_APP_KEY = "65a1355f898df7f19a6ccd47bdb45d92";// app
																					// key
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

}
