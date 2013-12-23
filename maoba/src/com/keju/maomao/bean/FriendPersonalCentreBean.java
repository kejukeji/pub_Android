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
 * 朋友中心实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 上午9:45:41
 */
public class FriendPersonalCentreBean implements Serializable {
	private static final long serialVersionUID = -6731766703892468980L;

	private int giftId;
	private String giftName;
	private String giftphotoUrl;
	
	private String integral;//积分

	public FriendPersonalCentreBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.giftId = obj.getInt("id");
		}
		if (obj.has("words")) {
			this.giftName = obj.getString("words");
		}
		if (obj.has("gift_pic_path")) {
			this.giftphotoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("gift_pic_path");
		}
		if (obj.has("cost")) {
			this.integral = obj.getString("cost");
		}
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	/**
	 * 构建一个list
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static final List<FriendPersonalCentreBean> constractList(JSONArray array) throws JSONException {
		List<FriendPersonalCentreBean> list = new ArrayList<FriendPersonalCentreBean>();
		for (int i = 0; i < array.length(); i++) {
			FriendPersonalCentreBean bean = new FriendPersonalCentreBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
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

	public String getGiftphotoUrl() {
		return giftphotoUrl;
	}

	public void setGiftphotoUrl(String giftphotoUrl) {
		this.giftphotoUrl = giftphotoUrl;
	}

}
