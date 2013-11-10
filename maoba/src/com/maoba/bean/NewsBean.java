/**
 * 
 */
package com.maoba.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.maoba.helper.BusinessHelper;

/**
 * 消息实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-30 下午3:02:38
 */
public class NewsBean implements Serializable {
	private static final long serialVersionUID = 7003978646361075720L;

	private int id;
	private int userId;
	private int friendId;
	private String age;
	private String nickName;
	private String sendTime;
	private String content;
	private String userUrl;

	public NewsBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.id = obj.getInt("id");
		}
		if (obj.has("receiver_id")) {
			this.userId = obj.getInt("receiver_id");
		}

		if (obj.has("sender_id")) {
			this.friendId = obj.getInt("sender_id");
		}
		if (obj.has("age")) {
			this.age = obj.getString("age");
		}
		if (obj.has("nick_name")) {
			this.nickName = obj.getString("nick_name");
		}
		if (obj.has("time")) {
			this.sendTime = obj.getString("time");
		}
		if (obj.has("content")) {
			this.content = obj.getString("content");
		}
		if (obj.has("receiver_path")) {
			this.userUrl = BusinessHelper.PIC_BASE_URL + obj.getString("receiver_path");
		}
	}

	public static List<NewsBean> constractList(JSONArray array) throws JSONException {
		List<NewsBean> list = new ArrayList<NewsBean>();
		for (int i = 0; i < array.length(); i++) {
			NewsBean bean = new NewsBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

}
