/**
 * 
 */
package com.maoba.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 城市实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-11 下午8:22:40
 */
public class CityBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6350027900157073370L;
	private int id;
	private String name;

	public CityBean(int id, String cityName) {
		super();
		this.name = cityName;
	}

	public CityBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.id = obj.getInt("id");
		}
		if (obj.has("name")) {
			this.name = obj.getString("name");
		}
	}

	/**
	 * 构建list
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<CityBean> constractList(JSONArray array) throws JSONException {
		ArrayList<CityBean> list = new ArrayList<CityBean>();
		for (int i = 0; i < array.length(); i++) {
			CityBean bean = new CityBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getCityId() {
		return id;
	}

	public void setCityId(int cityId) {
		this.id = cityId;
	}

	public String getCityName() {
		return name;
	}

	public void setCityName(String cityName) {
		this.name = cityName;
	}

}
