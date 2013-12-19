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
 * 个人中心实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 上午9:45:41
 */
public class PersonalCentreBean implements Serializable {
	private static final long serialVersionUID = -6731766703892468980L;

	private int receiverId;
	private int sendId;
	private String sendName;
	private String sendPhotoUrl;
	private String sendTiem;
	private String integral;
	
	private int giftId;
	private String giftName;
	private String giftPhotoUrl;
	

	public PersonalCentreBean(JSONObject obj) throws JSONException {
		if (obj.has("receiver_id")) {
			this.receiverId = obj.getInt("receiver_id");
		}
		if (obj.has("sender_id")) {
			this.sendId = obj.getInt("sender_id");
		}
		if (obj.has("gift_id")) {
			this.giftId = obj.getInt("gift_id");
		}
		
		if (obj.has("nick_name")) {
			this.sendName = obj.getString("nick_name");
		}
		if (obj.has("pic_path")) {
			this.sendPhotoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("pic_path");
		}
		if (obj.has("time")) {
			this.sendTiem = obj.getString("time");
		}
		if (obj.has("integral")) {
			this.integral = obj.getString("integral");
		}
		
		if (obj.has("words")) {
			this.giftName = obj.getString("words");
		}
		if (obj.has("gift_pic_path")) {
			this.giftPhotoUrl =BusinessHelper.PIC_BASE_URL + obj.getString("gift_pic_path");
		}
		
	}

	/**
	 * 构建一个list
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static final List<PersonalCentreBean> constractList(JSONArray array) throws JSONException {
		List<PersonalCentreBean> list = new ArrayList<PersonalCentreBean>();
		for (int i = 0; i < array.length(); i++) {
			PersonalCentreBean bean = new PersonalCentreBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}

	public int getSendId() {
		return sendId;
	}

	public void setSendId(int sendId) {
		this.sendId = sendId;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getSendPhotoUrl() {
		return sendPhotoUrl;
	}

	public void setSendPhotoUrl(String sendPhotoUrl) {
		this.sendPhotoUrl = sendPhotoUrl;
	}

	public String getSendTiem() {
		return sendTiem;
	}

	public void setSendTiem(String sendTiem) {
		this.sendTiem = sendTiem;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public int getGiftId() {
		return giftId;
	}

	public void setGiftId(int giftId) {
		this.giftId = giftId;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public String getGiftPhotoUrl() {
		return giftPhotoUrl;
	}

	public void setGiftPhotoUrl(String giftPhotoUrl) {
		this.giftPhotoUrl = giftPhotoUrl;
	}
	
	

}
