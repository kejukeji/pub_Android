package com.keju.maomao.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.keju.maomao.helper.BusinessHelper;

/**
 * 酒吧类型
 * @author Zhoujun
 * @version 创建时间：2013-10-28 上午10:45:49
 */
public class BarTypeBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6731766703892468980L;
	private int id;
	private String name;
	private String url;
	public BarTypeBean (JSONObject obj) throws JSONException{
		if(obj.has("id")){
			this.id = obj.getInt("id");
		}
		if(obj.has("name")){
			this.name = obj.getString("name");
		}
		if(obj.has("pic_path")){
			this.url = BusinessHelper.PIC_BASE_URL + obj.getString("pic_path");
		}
	}
	/**
	 * 构建一个list
	 * @param array
	 * @return
	 * @throws JSONException 
	 */
	public static final List<BarTypeBean> constractList(JSONArray array) throws JSONException{
		List<BarTypeBean> list = new ArrayList<BarTypeBean>();
		for (int i = 0; i < array.length(); i++) {
			BarTypeBean bean = new BarTypeBean(array.getJSONObject(i));
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
