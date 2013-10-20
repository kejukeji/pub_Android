package com.maoba.helper;

import org.json.JSONObject;

import com.maoba.SystemException;
import com.maoba.internet.HttpClient;
import com.maoba.internet.PostParameter;

/**
 * 网络访问操作
 * 
 * @author Zhoujun 说明： 1、一些网络操作方法 2、访问系统业务方法，转换成json数据对象，或者业务对象。
 */
public class BusinessHelper {

	/**
	 * 网络访问路径
	 */

	public static final String BASE_URL = "http://www.maobake.com/restful/";
	HttpClient httpClient = new HttpClient();

	/**
	 * 注册接口
	 * 
	 * @param flagLoginWay
	 * @param userName
	 * @param eMail
	 * @param passWord
	 * @return
	 * @throws SystemException
	 */
	public JSONObject register(int flagLoginWay, String userName, String eMail, String passWord) throws SystemException {

		return httpClient.post(
				BASE_URL + "user/register",
				new PostParameter[] { new PostParameter("login_type", flagLoginWay),
						new PostParameter("nick_name", userName), new PostParameter("login_name", eMail),
						new PostParameter("password", passWord) }).asJSONObject();
	  
	}

	/**
	 * 登录接口
	 * 
	 * @param loginWay
	 * @param loginName
	 * @param passWord
	 * @return
	 * @throws SystemException
	 */
	public JSONObject login(int loginWay, String loginName, String passWord) throws SystemException {
		return httpClient.post(
				BASE_URL + "user/login",
				new PostParameter[] { new PostParameter("login_type", loginWay),
						new PostParameter("user_name", loginName), new PostParameter("password", passWord) })
				.asJSONObject();
	}

	/**
	 * 第三方登陆接口
	 * 
	 * @param loginWay
	 * @param openUid
	 * @return
	 * @throws SystemException
	 */
	public JSONObject thirdLogin(int loginWay, String openUid) throws SystemException {
		return httpClient
				.post(BASE_URL + "user/login",
						new PostParameter[] { new PostParameter("login_type", loginWay),
								new PostParameter("open_id", openUid) }).asJSONObject();
	}

}
