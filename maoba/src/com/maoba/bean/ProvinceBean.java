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

/**
 * 省份实体类
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-11 下午8:22:40
 */
public class ProvinceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6350027900157073370L;
	private int provinceId;
	private String provinceName;
	private ArrayList<CityBean> list;

	public ProvinceBean(JSONObject obj) throws JSONException {
		if (obj.has("id")) {
			this.provinceId = obj.getInt("id");
		}
		if (obj.has("name")) {
			this.provinceName = obj.getString("name");
		}
		if(obj.has("city_list") && !TextUtils.isEmpty(obj.getString("city_list"))){
			this.list = CityBean.constractList(obj.getJSONArray("city_list"));
		}
	}

	/**
	 * 构建list
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static List<ProvinceBean> constractList(JSONArray array) throws JSONException {
		List<ProvinceBean> list = new ArrayList<ProvinceBean>();
		for (int i = 0; i < array.length(); i++) {
			ProvinceBean bean = new ProvinceBean(array.getJSONObject(i));
			list.add(bean);
		}
		return list;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public ArrayList<CityBean> getList() {
		return list;
	}

	public void setList(ArrayList<CityBean> list) {
		this.list = list;
	}

}
