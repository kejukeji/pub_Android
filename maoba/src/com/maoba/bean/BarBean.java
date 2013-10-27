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

/**
 * 酒吧实体类
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-21 下午8:19:02
 */
public class BarBean implements Serializable {

	private static final long serialVersionUID = 4617043918315208981L;
	public static final String BASE_URL = "http://42.121.108.142:6001/";
	private int bar_id;
	private String bar_Name;
	private String bar_Address;
	private String imageUrl;
	private String bar_Intro;
	private String bar_Type;
	private String recommendImageUrl;
	private String showPhotoUrl;
	private String hot;
	private String barType;

	private String latitude;// 纬度（跳转地图时，纬度放在前面）
	private String longitude;// 经度

	/**
	 * @param obj
	 * @throws JSONException
	 */
	public BarBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.bar_id = obj.getInt("id");
		}

		if (obj.has("name")) {
			this.bar_Name = obj.getString("name");

		}
		if (obj.has("city_county")) {
			this.bar_Address = obj.getString("city_county");
		}
		if (obj.has("pic_path")) {
			this.imageUrl = BASE_URL + obj.getString("pic_path");
		}
		if (obj.has("intro")) {
			this.bar_Intro = obj.getString("intro");
		}
		if (obj.has("type")) {
			this.bar_Type = obj.getString("type");

		}
		if (obj.has("latitude")) {
			this.latitude = obj.getString("latitude");
		}
		if (obj.has("longitude")) {
			this.longitude = obj.getString("longitude");
		}
		if (obj.has("pic_path")) {
			this.recommendImageUrl = BASE_URL + obj.getString("pic_path");
		}
		if (obj.has("view_number")) {
			this.hot = obj.getString("view_number");
		}
		if (obj.has("type_name")) {
			this.barType = obj.getString("type_name");
		}
		if (obj.has("pic_path")) {
			this.showPhotoUrl = obj.getString("pic_path");
		}
	}

	/**
	 * 构建list list 最后得到的数据是:解析出的有所有的数据 使用 for 循环加载所有的 Array
	 **/
	public static List<BarBean> constractList(JSONArray array) throws JSONException {
		List<BarBean> list = new ArrayList<BarBean>();
		for (int i = 0; i < array.length(); i++) {
			BarBean bean = new BarBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getBar_id() {
		return bar_id;
	}

	public void setBar_id(int bar_id) {
		this.bar_id = bar_id;
	}

	public String getBar_Name() {
		return bar_Name;
	}

	public void setBar_Name(String bar_Name) {
		this.bar_Name = bar_Name;
	}

	public String getBar_Address() {
		return bar_Address;
	}

	public void setBar_Address(String bar_Address) {
		this.bar_Address = bar_Address;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBar_Intro() {
		return bar_Intro;
	}

	public void setBar_Intro(String bar_Intro) {
		this.bar_Intro = bar_Intro;
	}

	public String getBar_Type() {
		return bar_Type;
	}

	public void setBar_Type(String bar_Type) {
		this.bar_Type = bar_Type;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRecommendImageUrl() {
		return recommendImageUrl;
	}

	public void setRecommendImageUrl(String recommendImageUrl) {
		this.recommendImageUrl = recommendImageUrl;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getBarType() {
		return barType;
	}

	public void setBarType(String barType) {
		this.barType = barType;
	}

	public String getShowPhotoUrl() {
		return showPhotoUrl;
	}

	public void setShowPhotoUrl(String showPhotoUrl) {
		this.showPhotoUrl = showPhotoUrl;
	}

}
