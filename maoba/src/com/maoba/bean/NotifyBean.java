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

import android.text.TextUtils;

import com.maoba.util.StringUtil;


/**
 * 系统通知(实体)
 * 
 * @author Arvin
 * 
 */
public class NotifyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7299753145293640344L;
	private Long id;
	private Long userId;
	private String title;
	private String content;
	private String type;// 通知类型
	private String uri;
	private int status;
	private String sendTime;
	private long wasId;// 活动通知对方的id
	private long activityId;// 活动的id

	public NotifyBean(JSONObject obj) throws JSONException {
		if (obj != null) {
			//nitiId
			if (obj.has("id")) {
				if (!TextUtils.isEmpty(obj.getString("id"))) {
					this.id = obj.getLong("id");
				} else {
					id = 0L;
				}
			}
			if (obj.has("userId")) {
				String userIdL = obj.getString("userId");
				if (!TextUtils.isEmpty(userIdL)) {
					this.userId = Long.parseLong(userIdL);
				}
			}
			if (obj.has("title")) {
				this.title = obj.getString("title");
			}
			if (obj.has("content")) {
				this.content = obj.getString("content");
			}
			if (obj.has("type")) {
				this.type = obj.getString("type");
			}
			// if (obj.has("uri")) {
			// this.uri = obj.getString("uri");
			// }
			 if (obj.has("status") &&!StringUtil.isBlank(String.valueOf(obj.get("status")))) {
				 this.status = obj.getInt("status");
			 }
			// if (obj.has("sendTime")) {
			// this.sendTime = obj.getString("sendTime");
			// }
			if (obj.has("wasId")) {
				String wasIdL = obj.getString("wasId");
				if (!TextUtils.isEmpty(wasIdL)) {
					this.wasId = Long.parseLong(wasIdL);
				}
			}

			if (obj.has("activityId")) {
				String activityIdL = obj.getString("activityId");
				if (!TextUtils.isEmpty(activityIdL)) {
					this.activityId = Long.parseLong(activityIdL);
				}
			}
		}
	}

	public static List<NotifyBean> constractList(JSONArray array) throws JSONException {
		List<NotifyBean> notifyBeans = null;
		if (array != null) {
			notifyBeans = new ArrayList<NotifyBean>();
			int length = array.length();
			for (int i = 0; i < length; i++) {
				JSONObject subObj = (JSONObject) array.get(i);
				notifyBeans.add(new NotifyBean(subObj));
			}
		}
		return notifyBeans;
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the sendTime
	 */
	public String getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime
	 *            the sendTime to set
	 */
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public long getWasId() {
		return wasId;
	}

	public void setWasId(long wasId) {
		this.wasId = wasId;
	}

	public long getActivityId() {
		return activityId;
	}

	public void setActivityId(long activityId) {
		this.activityId = activityId;
	}

}
