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
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 上午10:26:17
 */
public class EventBean implements Serializable {
	private static final long serialVersionUID = 4617043918315208981L;

	private int id;
	private String title;
	private String address;
	private String startTime;// 活动开始时间
	private String endTime; // 活动结束时间
	private int joinNumber;
	private String photoUrl;
	private String recommendPhotoUrl;// 推荐图片

	private String barName;
	private String barAddress;
	private String eventContent;

	/**
	 * @param obj
	 * @throws JSONException
	 */
	public EventBean(JSONObject obj) throws JSONException {

		if (obj.has("id")) {
			this.id = obj.getInt("id");
		}

		if (obj.has("name")) {
			this.title = obj.getString("name");

		}
		if (obj.has("city_county")) {
			this.address = obj.getString("city_county");
		}

		if (obj.has("time")) {
			this.startTime = obj.getString("time");
		}
		if (obj.has("endTime")) {
			this.endTime = obj.getString("endTime");

		}

		if (obj.has("joinNumber")) {
			this.joinNumber = obj.getInt("joinNumber");
		}
		if (obj.has("pic_path")) {
			this.photoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("pic_path");
		}
		if (obj.has("recommendPhotoUrl")) {
			this.recommendPhotoUrl = BusinessHelper.PIC_BASE_URL + obj.getString("recommendPhotoUrl");
		}

		if (obj.has("barName")) {
			this.barName = obj.getString("barName");
		}
		if (obj.has("barAddress")) {
			this.barAddress = obj.getString("barAddress");
		}

		if (obj.has("eventContent")) {
			this.eventContent = obj.getString("eventContent");
		}

	}

	/**
	 * 构建list list 最后得到的数据是:解析出的有所有的数据 使用 for 循环加载所有的 Array
	 **/
	public static List<EventBean> constractList(JSONArray array) throws JSONException {
		List<EventBean> list = new ArrayList<EventBean>();
		for (int i = 0; i < array.length(); i++) {
			EventBean bean = new EventBean(array.getJSONObject(i));
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getJoinNumber() {
		return joinNumber;
	}

	public void setJoinNumber(int joinNumber) {
		this.joinNumber = joinNumber;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getRecommendPhotoUrl() {
		return recommendPhotoUrl;
	}

	public void setRecommendPhotoUrl(String recommendPhotoUrl) {
		this.recommendPhotoUrl = recommendPhotoUrl;
	}

	public String getBarName() {
		return barName;
	}

	public void setBarName(String barName) {
		this.barName = barName;
	}

	public String getBarAddress() {
		return barAddress;
	}

	public void setBarAddress(String barAddress) {
		this.barAddress = barAddress;
	}

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

}
