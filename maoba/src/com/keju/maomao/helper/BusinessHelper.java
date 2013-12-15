package com.keju.maomao.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.keju.maomao.Constants;
import com.keju.maomao.SystemException;
import com.keju.maomao.bean.BarBean;
import com.keju.maomao.bean.EventBean;
import com.keju.maomao.bean.LetterBean;
import com.keju.maomao.bean.NewsBean;
import com.keju.maomao.bean.ProvinceBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.internet.HttpClient;
import com.keju.maomao.internet.PostParameter;

/**
 * 网络访问操作
 * 
 * @author zhouyong 说明 1、一些网络操作方法2、访问系统业务方法，转换成json数据对象，或者业务对象方法
 */
public class BusinessHelper {

	/**
	 * 网络访问路径
	 */
	//测试服务器
	public static final String BASE_URL = "http://42.121.108.142:6001/restful/";
	public static final String PIC_BASE_URL = "http://42.121.108.142:6001";
	//本地服务器
//	public static final String BASE_URL1 = "http://192.168.0.126:5000/restful/";
//	public static final String PIC_BASE_URL = "http://192.168.0.126:5000";
	//生产服务器
//	public static final String BASE_URL = "http://61.188.37.228:8081/restful/";
//	public static final String PIC_BASE_URL = "http://61.188.37.228:8081";
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
	 * 注册接口
	 * 
	 * @param flagLoginWay
	 * @param userName
	 * @param eMail
	 * @param passWord
	 * @return
	 * @throws SystemException
	 */
	public JSONObject register(String nickName, int logintype, String openUid) throws SystemException {

		return httpClient.post(
				BASE_URL + "user/register",
				new PostParameter[] { new PostParameter("nick_name", nickName),
						new PostParameter("login_type", logintype), new PostParameter("open_id", openUid) })
				.asJSONObject();

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
	public JSONObject thirdLogin(int loginWay, String openUid) throws SystemException {
		return httpClient
				.post(BASE_URL + "user/login",
						new PostParameter[] { new PostParameter("login_type", loginWay),
								new PostParameter("open_id", openUid) }).asJSONObject();

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
	 * @param bar_id 酒吧的id
	 * @param pageIndex页数
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<BarBean> getBarList(int barTypeId, int provinceId,int cityId,int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (barTypeId > 0) {
			p.add(new PostParameter("type_id", barTypeId));
		}if(provinceId >=0){
		  p.add(new PostParameter("province_id", provinceId));
		}
		p.add(new PostParameter("city_id", cityId));
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<BarBean> response = null;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "pub/list/detail", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
//				Context context = ;
//				SharedPrefUtil.setPubCount(context, obj.getInt("pub_count"));
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				List<BarBean> list1 = null;
			    List<BarBean> list2 = null;
				if (!TextUtils.isEmpty(obj.getString("pub_list"))) {
					list = BarBean.constractList(obj.getJSONArray("pub_list"));// 酒吧列表
					list1 = BarBean.constractList(obj.getJSONArray("picture_list"));// 推荐酒吧列表
					list2 = BarBean.constractList(obj.getJSONArray("county")); //筛选的地区
				} else {
					list = new ArrayList<BarBean>();
					list1 = new ArrayList<BarBean>();
					list2 = new ArrayList<BarBean>();
				}
				response.setObjList(list);
				response.setObjList1(list1);
				response.setObjList2(list2);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			e1.printStackTrace();
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			e.printStackTrace();
		//	response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	

	/**
	 * 获取酒吧详情接口 
	 * 
	 * @param bar_id 酒吧的id
	 * @param uid用户的id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getBarDetail(int bar_id, int uid) throws SystemException {
		return httpClient.get(
				BASE_URL + "pub/detail",
				new PostParameter[] { new PostParameter("pub_id", bar_id),
						new PostParameter("user_id", uid),})
				.asJSONObject();

	}

	/**
	 * 获取酒吧热门搜索面接�?
	 * 
	 * @param bar_id
	 *            酒吧的id
	 * @param uid
	 *            用户的id
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
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 搜索酒吧接口
	 * 
	 * @param content关键
	 * @param pageIndex
	 *            页数
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
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取酒吧环境接口
	 * 
	 * @param bar_id酒吧的id
	 * 
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
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取用户收藏的酒吧列表接口
	 * 
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws SystemException
	 * */
	public ResponseBean<BarBean> getcollectBar(int uid, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<BarBean> response;
		JSONObject obj;
		try {
			obj = httpClient.get(BASE_URL + "user/collect", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("list"))) {
					list = BarBean.constractList(obj.getJSONArray("list"));

				} else {
					list = new ArrayList<BarBean>();
				}
				response.setObjList(list);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString(""));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e2) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析失败");
		}

		return response;

	}

	/**
	 * 获取用户收藏的酒吧条数接口
	 * 
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws SystemException
	 * */
	public JSONObject getContentNum(int uid, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		p.add(new PostParameter("page", pageIndex));
		JSONObject obj;
		obj = httpClient.get(BASE_URL + "user/collect", p.toArray(new PostParameter[p.size()])).asJSONObject();
		return obj;
	}

	/**
	 * 收藏酒吧
	 * 
	 * @param uid
	 * @param id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject collectBar(int uid, int bar_id) throws SystemException {
		return httpClient.get(BASE_URL + "pub/collect",
				new PostParameter[] { new PostParameter("user_id", uid), new PostParameter("pub_id", bar_id) })
				.asJSONObject();
	}
	/**
	 * 删除酒吧
	 * 
	 * @param uid
	 * @param id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject DelBar(int uid, int bar_id) throws SystemException {
		return httpClient.get(BASE_URL + "cancel/collect/pub",
				new PostParameter[] { new PostParameter("user_id", uid), new PostParameter("pub_id", bar_id) })
				.asJSONObject();
	}
	/**
	 * 获取首页数据
	 * 
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getHomeData() throws SystemException {
		return httpClient.get(BASE_URL + "pub/home").asJSONObject();
	}

	/**
	 * 获取私信会话列表
	 * 
	 * @param uid
	 * @param pageIndex
	 * @param newType 
	 * @return
	 * @throws SystemException
	 * */
	public ResponseBean<NewsBean> getPrivateNews(int uid, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<NewsBean> response;
		JSONObject obj;
		try {
			obj = httpClient.get(BASE_URL + "user/direct/message", p.toArray(new PostParameter[p.size()]))
					.asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<NewsBean>(obj);
				List<NewsBean> list;
					if (!TextUtils.isEmpty(obj.getString("list"))) {
						list = NewsBean.constractList(obj.getJSONArray("list"));

					} else {
						list = new ArrayList<NewsBean>();
					}
					
				response.setObjList(list);
			} else {
				response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, obj.getString(""));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e2) {
			response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, "json解析失败");
		}

		return response;

	}

	/**
	 * 获取某个私信会话列表
	 * 
	 * @param uid
	 * @param friendId
	 * @return
	 * @throws SystemException
	 * */
	public ResponseBean<LetterBean> getLetterList(int friendId,int uid) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (friendId > 0) {
			p.add(new PostParameter("sender_id", friendId));
		} else {
			p.add(new PostParameter("sender_id", ""));
		}
		if (uid > 0) {
			p.add(new PostParameter("receiver_id", uid));
		} else {
			p.add(new PostParameter("receiver_id", ""));
		}
		ResponseBean<LetterBean> response;
		JSONObject obj;
		try {
			obj = httpClient.get(BASE_URL + "user/message/info", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<LetterBean>(obj);
				List<LetterBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("list"))) {
					list = LetterBean.constractList(obj.getJSONArray("list"));
				} else {
					list = new ArrayList<LetterBean>();
				}
				response.setObjList(list);

			} else {
				response = new ResponseBean<LetterBean>(Constants.REQUEST_FAILD, obj.getString(""));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<LetterBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e2) {
			response = new ResponseBean<LetterBean>(Constants.REQUEST_FAILD, "json解析失败");
		}

		return response;

	}

	/**
	 * 发送私信
	 * 
	 * @param content
	 * @param sender 发送者
	 * @param receiver接受者
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getSendLetter(long senderId, long receiverId, String content) throws SystemException {

		return httpClient.get(
				BASE_URL + "user/sender/message",
				new PostParameter[] { new PostParameter("sender_id", senderId),
						new PostParameter("receiver_id", receiverId), new PostParameter("content", content), })
				.asJSONObject();

	}

	/**
	 * 系统信息
	 * @param newsType 
	 * 
	 * @param user_id
	 * @return
	 * @throws SystemException
	 */
	public ResponseBean<NewsBean> getSysLetter(int uid, int newsType) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		p.add(new PostParameter("types", newsType));
		ResponseBean<NewsBean> response;
		JSONObject obj;
		try {
			obj = httpClient.get(BASE_URL + "message/by/type/info", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<NewsBean>(obj);
				List<NewsBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("system_message_list"))) {
					list = NewsBean.constractList(obj.getJSONArray("system_message_list"));
				} else {
					list = new ArrayList<NewsBean>();
				}
				response.setObjList(list);

			} else {
				response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, obj.getString(""));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e2) {
			response = new ResponseBean<NewsBean>(Constants.REQUEST_FAILD, "json解析失败");
		}

		return response;

	}

	/**
	 * 获取系统信息和私信信息的条数
	 * @param newsType 
	 * 
	 * @param user_id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getSysLetter1(int uid) throws SystemException {
		return httpClient.get(BASE_URL + "user/message", new PostParameter[] { 
				new PostParameter("user_id", uid),})
				.asJSONObject();

	}

	/**
	 * 清除私信聊天信息
	 * 
	 * @param user_id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getClear(int uid) throws SystemException {

		return httpClient.get(BASE_URL + "user/clear/message",
				new PostParameter[] { new PostParameter("user_id", uid), }).asJSONObject();

	}

	/**
	 * 
	 * @param user_id提交意见反馈接口
	 * @param content反馈的内容
	 *            
	 * @return
	 * @throws SystemException
	 */

	public JSONObject getFeedBack(int uid, String content) throws SystemException {

		return httpClient.get(BASE_URL + "feed/back",
				new PostParameter[] { new PostParameter("user_id", uid), new PostParameter("content", content) })
				.asJSONObject();
	}

	/**
	 * 获取酒吧活动列表接口
	 * 
	 * @param bar_id 酒吧的id
	 * @param pageIndex 页数
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<EventBean> getEventList(int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<EventBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "activity/list", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<EventBean>(obj);
				List<EventBean> list = null;
				List<EventBean> list1 = null;
				if (!TextUtils.isEmpty(obj.getString("activity_list"))) {
					list = EventBean.constractList(obj.getJSONArray("activity_list"));// 活动列表
					list1 = EventBean.constractList(obj.getJSONArray("hot_list"));// 推荐活动列表
				} else {
					list = new ArrayList<EventBean>();
					list1 = new ArrayList<EventBean>();
				}
				response.setObjList(list);

				response.setObjList1(list1);
			} else {
				response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}

	/**
	 * 获取用户资料
	 * 
	 * @param loginWay
	 * @param openId
	 * @param uid
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getUserInfor(int uid) throws SystemException {
		return httpClient.post(BASE_URL + "user/user_info/" + uid).asJSONObject();
	}
	
	/**
	 * 获取用户在貓吧的信息
	 * 
	 * @param loginWay
	 * @param openId
	 * @param uid
	 * @return
	 * @throws SystemException
	 */
	public JSONObject GetUserBaseInfor(int uid) throws SystemException {
		return httpClient.get(BASE_URL + "person/center", new PostParameter[] { 
				new PostParameter("user_id", uid),})
				.asJSONObject();
	}
	
	
	/**
	 * 修改用户资料  图片上传
	 * 此方案即可上传文件  又可以不传
	 * @param loginWay
	 * @param openId
	 * @param uid
	 * @param avatarFile 
	 * @param address 
	 * @param signature 
	 * @param sex 
	 * @param birthday 
	 * @param nickName 
	 * @param avatarFile 
	 * @param openId 
	 * @return
	 * @throws SystemException
	 */
	public JSONObject addUserInfor(int uid,int loginWay, String passWord, String nickName, String birthday, int sex, 
			String signature,String newPassword,String provinceId,String cityId,String countyId,File avatarFile) throws SystemException {
		List<PostParameter> params = new ArrayList<PostParameter>();
		params.add(new PostParameter("login_type", loginWay));
		params.add(new PostParameter("password", passWord));
		params.add(new PostParameter("nick_name", nickName));
		params.add(new PostParameter("birthday", birthday));
		params.add(new PostParameter("sex", sex));
		params.add(new PostParameter("signature", signature));
		params.add(new PostParameter("new_password", newPassword));
		params.add(new PostParameter("province_id", provinceId));
		params.add(new PostParameter("city_id", cityId));
        params.add(new PostParameter("county_id", countyId));
		if(avatarFile!=null){
			return httpClient.multPartURL("head_picture",
					BASE_URL + "user/user_info/" + uid,
					params.toArray(new PostParameter[params.size()]), avatarFile)
					.asJSONObject();
		}else{
			return httpClient.post(
					BASE_URL + "user/user_info/"+ uid,params.toArray(new PostParameter[params.size()])).asJSONObject();
		}
	}
	/**
	 * 第三方登陆的用户设置资料
	 * @param userId
	 * @param loginType
	 * @param openId
	 * @param nickName
	 * @param birthday
	 * @param sex
	 * @param signature
	 * @param address
	 * @param newPassword
	 * @param avatarFile 
	 * @return
	 */
	public JSONObject thirdAddUserInfor(int userId, int loginType, String openId, String nickName, String birthday,
			int sex, String signature,String provinceId,String cityId,String countyId,File avatarFile)throws SystemException {
		  List<PostParameter> params = new ArrayList<PostParameter>();
	  		params.add(new PostParameter("login_type", loginType));
	  		params.add(new PostParameter("open_id", openId));
	  		params.add(new PostParameter("nick_name", nickName));
	  		params.add(new PostParameter("birthday", birthday));
	  		params.add(new PostParameter("sex", sex));
	  		params.add(new PostParameter("signature", signature));
	  		params.add(new PostParameter("province_id", provinceId));
			params.add(new PostParameter("city_id", cityId));
	        params.add(new PostParameter("county_id", countyId));
		if(avatarFile!=null){
	  		return httpClient.multPartURL("head_picture",
	  				BASE_URL + "user/user_info/" + userId,
	  				params.toArray(new PostParameter[params.size()]), avatarFile)
	  				.asJSONObject();
	      }else{
	    	  return httpClient.post(
						BASE_URL + "user/user_info/"+ userId,
						params.toArray(new PostParameter[params.size()])).asJSONObject();
	      }
	}
	/**
	 * 获取附近酒吧
	 * 
	 * @param latitude   经度
	 * @param longitude  纬度
	 * @param pageIndex 
	 * @param page   页数
	 * @return
	 * @throws SystemException
	 */

	public JSONObject getNearbyBarList( double longitude,double latitude, int pageIndex) throws SystemException{
		return httpClient.get(BASE_URL + "near/pub",
				new PostParameter[] { new PostParameter("longitude", longitude), 
				new PostParameter("latitude", latitude),
				new PostParameter("page", pageIndex)})
				.asJSONObject();
	}
	
	
	/**
	 * 地区筛子接口
	 *  
	 * @param bar_id 酒吧的id
	 * @param pageIndex页数
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<BarBean> getScreenArea(int countyId,int pageIndex,int barId) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
			p.add(new PostParameter("county_id", countyId));
	     	p.add(new PostParameter("page", pageIndex));
		if (barId > 0) {
			p.add(new PostParameter("type_id", barId));
		}
		ResponseBean<BarBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "screening/county", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<BarBean>(obj);
				List<BarBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("pub_list"))) {
					list = BarBean.constractList(obj.getJSONArray("pub_list"));//筛选 酒吧列表
				} else {
					list = new ArrayList<BarBean>();
				}
				response.setObjList(list);
			} else {
				response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<BarBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}



	/**
	 * 省份选择接口
	 *  
	 * @param 
	 * @param 
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<ProvinceBean> getProvince() throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		ResponseBean<ProvinceBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "area", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<ProvinceBean>(obj);
				List<ProvinceBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("list"))) {
					list = ProvinceBean.constractList(obj.getJSONArray("list"));//省
				} else {
					list = new ArrayList<ProvinceBean>();
				}
				response.setObjList(list);
			} else {
				response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;

	}
	
	/**
	 * 获取地区信息
	 *  
	 * @param 
	 * @param 
	 * @return
	 * @throws SystemException
	 */

	public ResponseBean<ProvinceBean> getArea() throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		ResponseBean<ProvinceBean> response;
		try {
			JSONObject obj;
			obj = httpClient.get(BASE_URL + "area", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<ProvinceBean>(obj);
				List<ProvinceBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("list"))) {
					list = ProvinceBean.constractList(obj.getJSONArray("list"));//省
				} else {
					list = new ArrayList<ProvinceBean>();
				}
				response.setObjList(list);
			} else {
				response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, obj.getString("message"));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e) {
			response = new ResponseBean<ProvinceBean>(Constants.REQUEST_FAILD, "json解析错误");
		}
		return response;
	}
	
	/**
	 * 获取活动详情接口 
	 * 
	 * @param bar_id 酒吧的id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getEventDetail(int eventId,int userId) throws SystemException {
		return httpClient.get(
				BASE_URL + "activity/info",
				new PostParameter[] { new PostParameter("activity_id", eventId),
						new PostParameter("user_id", userId)}).asJSONObject();
	}
	
	/**
	 * 活动收藏和取消接口 
	 * 
	 * @param bar_id 酒吧的id
	 * @return
	 * @throws SystemException
	 */
	public JSONObject collectEvent(int eventId,int userId) throws SystemException {
		return httpClient.get(
				BASE_URL + "cancel/collect/activity",
				new PostParameter[] { new PostParameter("activity_id", eventId),
				new PostParameter("user_id", userId)}).asJSONObject();
	}
	
	/**
	 * 获取用户收藏的活动列表接口
	 * 
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws SystemException
	 * */
	public ResponseBean<EventBean> getcollectEvent(int uid, int pageIndex) throws SystemException {
		List<PostParameter> p = new ArrayList<PostParameter>();
		if (uid > 0) {
			p.add(new PostParameter("user_id", uid));
		} else {
			p.add(new PostParameter("user_id", ""));
		}
		p.add(new PostParameter("page", pageIndex));
		ResponseBean<EventBean> response;
		JSONObject obj;
		try {
			obj = httpClient.get(BASE_URL + "collect/activity/list", p.toArray(new PostParameter[p.size()])).asJSONObject();
			int status = obj.getInt("status");
			if (status == Constants.REQUEST_SUCCESS) {
				response = new ResponseBean<EventBean>(obj);
				List<EventBean> list = null;
				if (!TextUtils.isEmpty(obj.getString("activity_collect"))) {
					list = EventBean.constractList(obj.getJSONArray("activity_collect"));

				} else {
					list = new ArrayList<EventBean>();
				}
				response.setObjList(list);
			} else {
				response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, obj.getString(""));
			}
		} catch (SystemException e1) {
			response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, "服务器连接失败");
		} catch (JSONException e2) {
			response = new ResponseBean<EventBean>(Constants.REQUEST_FAILD, "json解析失败");
		}

		return response;

	}
	/**
	 * 获取城市切换接口 
	 * 
	 * @return
	 * @throws SystemException
	 */
	public JSONObject getCity() throws SystemException {
		return httpClient.get(
				BASE_URL + "city/data").asJSONObject();
	}
	

}
