package com.keju.maomao.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class SortModelBean implements Serializable{
	
	private static final long serialVersionUID = -6350027900157073370L;
	
	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	
	private String cityName;//城市名字
	private int provinceId;
	
	private List<SortModelBean> list = new ArrayList<SortModelBean>();
	
	public SortModelBean(int id, String cityName) {
		super();
		this.cityName = cityName;
	}
	
	public SortModelBean(JSONObject obj) throws JSONException {
		if (obj.has("province_id")) {
			this.provinceId = obj.getInt("province_id");
		}
		if (obj.has("name")) {
			this.cityName = obj.getString("name");
		}
		
		if (obj.has("city") && !TextUtils.isEmpty(obj.getString("city"))) {
			this.list.add(new SortModelBean(0, this.cityName));
			this.list.addAll(SortModelBean.constractList(obj.getJSONArray("city")));
		}
	}

	/**
	 * 构建list
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<SortModelBean> constractList(JSONArray array) throws JSONException {
		ArrayList<SortModelBean> list = new ArrayList<SortModelBean>();
		for (int i = 0; i < array.length(); i++) {
			SortModelBean bean = new SortModelBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}
	
	public SortModelBean(){
		super();
	}
	// Json解析的
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
