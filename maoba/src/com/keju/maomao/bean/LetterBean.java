/**
 * 
 */
package com.keju.maomao.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.keju.maomao.helper.BusinessHelper;

/**
 * 私信实体
 * 
 * @author zhouyong
 * 
 */
public class LetterBean implements Serializable {

	private static final long serialVersionUID = -6190204637500449066L;
	private int id;
	private int senderId;// 发送者
	private int receiverId;// 接受者
	private int sender;
	private int receiver;
	private String content;// 内容
	private int status;// 状态NewsBean
	private String sendTime;// 发送时间

	private String friendUrl;//接收者图片的rul
	private String sendUrl;//发送者的图片url

	public LetterBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.id = obj.getInt("id");
		}
		if (obj.has("sender_id")) {
			this.senderId = obj.getInt("sender_id");
		}
		if (obj.has("receiver_id")) {
			this.receiverId = obj.getInt("receiver_id");
		}
		// if (obj.has("sender")) {
		// this.sender = obj.getInt("sender");
		// }
		// if (obj.has("receiver")) {
		// this.receiver = obj.getInt("receiver");
		// }
		if (obj.has("content")) {
			this.content = obj.getString("content");
		}
		if (obj.has("status")) {
			this.status = obj.getInt("status");
		}
		if (obj.has("time")) {
			this.sendTime = obj.getString("time");
		}
		if (obj.has("sender_pic_path")) {
			this.sendUrl = BusinessHelper.PIC_BASE_URL + obj.getString("sender_pic_path");
		}
		if (obj.has("receiver_path")) {
			this.friendUrl = BusinessHelper.PIC_BASE_URL + obj.getString("receiver_path");
		}
	}

	public static List<LetterBean> constractList(JSONArray arr) throws JSONException {
		List<LetterBean> letterBeans = new ArrayList<LetterBean>();
		if (arr != null) {
			int length = arr.length();
			for (int i = 0; i < length; i++) {
				JSONObject subObj = arr.getJSONObject(i);
				if (subObj != null) {
					letterBeans.add(new LetterBean(subObj));
				}
			}
		}
		return letterBeans;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getreceiverId() {
		return receiverId;
	}

	public void setreceiverId(int receiverId) {
		this.receiverId = receiverId;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getFriendUrl() {
		return friendUrl;
	}

	public void setFriendUrl(String friendUrl) {
		this.friendUrl = friendUrl;
	}

	public String getSendUrl() {
		return sendUrl;
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}

}
