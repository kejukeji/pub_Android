package com.maoba.helper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.maoba.Constants;
import com.maoba.SystemException;
import com.maoba.bean.BarBean;
import com.maoba.bean.ResponseBean;
import com.maoba.internet.HttpClient;
import com.maoba.internet.PostParameter;

/**
 * 网络访问操作
 * 
 * @author zhouyong 说明�?1、一些网络操作方�?2、访问系统业务方法，转换成json数据对象，或者业务对象�?
 */
public class BusinessHelper {

	/**
	 * 网络访问路径
	 */

	public static final String BASE_URL = "http://42.121.108.142:6001/restful/";
	public static final String PIC_BASE_URL = "http://42.121.108.142:6001/";
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
	 * 第三方登陆接�?
	 * 
	 * @param nickName
	 * 
	 * @param loginWay
	 * @param openUid
	 * @return
	 * @throws SystemException
	 */
	public JSONObject thirdLogin(String nickName, int loginWay, String openUid) throws SystemException {
		return httpClient.post(
				BASE_URL + "user/login",
				new PostParameter[] { new PostParameter("login_type", loginWay), new PostParameter("open_id", openUid),
						new PostParameter("user_name", nickName) }).asJSONObject();
	}

	/**
	 * 第三方登陆检查接�?
	 * 
	 * @param loginWay
	 * @param openUid
	 * @return
	 * @throws SystemException
	 */
	public JSONObject check(int loginWay, String openUid) throws SystemException {
		return httpClient
				.post(BASE_URL + "user/check",
						new PostParameter[] { new PostParameter("login_type", loginWay),
								new PostParameter("open_id", openUid) }).asJSONObject();
	}

	/**
	 * 获取酒吧列表接口
	 * 
	 * @param bar_id酒吧的id
	 * @param pageIndex 页数
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<BarBean> getBarList(int bar_id, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (bar_id > 0) {
			p.add(new PostParameter("type_id", bar_id));
		}
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/list/detail", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				List<BarBean> list1 = null;
				if (!TextUtils.isEmpty(obj.getString("pub_list"))) {
					list = BarBean.constractList(obj.getJSONArray("pub_list"));//酒吧列表
					list1 = BarBean.constractList(obj.getJSONArray("picture_list"));//推荐酒吧列表
				} else {
					list = new ArrayList<BarBean>();
					list1 = new ArrayList<BarBean>();
				}
				response.setObjList(list);

				response.setObjList1(list1);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取酒吧详情接口 用户登录状�?
	 * 
	 * @param bar_id 酒吧的id
	 * @param uid 用户的id
	 * @return
	 * @throws SystemException
	 */
	public ResponseBean<BarBean> getBarDetail(int bar_id, int uid) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		p.add(new PostParameter("pub_id", bar_id));
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/detail", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				List<BarBean> list1 = null;
				if (!TextUtils.isEmpty(obj.getString("picture_list"))) {
					list = BarBean.constractList(obj.getJSONArray("picture_list"));//签到列表
					list1 = BarBean.constractList(obj.getJSONArray("pub_list"));//酒吧列表
				} else {
					list = new ArrayList<BarBean>();
					list1 = new ArrayList<BarBean>();
				}
				response.setObjList(list);

				response.setObjList1(list1);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取酒吧详情接口 用户无登录状�?
	 * 
	 * @param bar_id  酒吧的id
	 * @param uid用户的id
	 * @return
	 * @throws SystemException
	 */
	public ResponseBean<BarBean> getBarDetail(int bar_id) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		p.add(new PostParameter("pub_id", bar_id));
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/detail", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				List<BarBean> list1 = null;
				if (!TextUtils.isEmpty(obj.getString("picture_list"))) {
					list = BarBean.constractList(obj.getJSONArray("picture_list"));
					list1 = BarBean.constractList(obj.getJSONArray("pub_list"));
				} else {
					list = new ArrayList<BarBean>();
					list1 = new ArrayList<BarBean>();
				}
				response.setObjList(list);

				response.setObjList1(list1);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取酒吧热门搜索面接�?
	 * 
	 * @param bar_id 酒吧的id
	 * @param uid 用户的id
	 * @return
	 * @throws SystemException
	 */
	public ResponseBean<BarBean> getBarHotSearch() throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/search/view", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("pub_list"))) {
					list = BarBean.constractList(obj.getJSONArray("pub_list"));
				} else {
					list = new ArrayList<BarBean>();
				}
				response.setObjList(list);

			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 搜索酒吧接口
	 * 
	 * @param content 关键�?       
	 * @param pageIndex 页数
	 *           
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<BarBean> getSearchBarList(String content, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		p.add(new PostParameter("page", pageIndex));
		p.add(new PostParameter("content", content));
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/search", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("pub_list"))) {
					list = BarBean.constractList(obj.getJSONArray("pub_list"));
				} else {
					list = new ArrayList<BarBean>();
				}
				response.setObjList(list);

			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}
	/**
	 * 获取酒吧环境接口
	 * 
	 * @param bar_id  酒吧的id
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<BarBean> getenBarEnvironmentList(int bar_id) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		p.add(new PostParameter("pub_id", bar_id));
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/picture", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("picture_list"))) {
					list = BarBean.constractList(obj.getJSONArray("picture_list"));
				} else {
					list = new ArrayList<BarBean>();
				}
				response.setObjList(list);

			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失�?);
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}
	/**
	 * 获取首页数据
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getHomeData() throws SystemException{
		return httpClient.get(BASE_URL + "pub/home").asJSONObject();
	}
}
