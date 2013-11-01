package com.maoba.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 评论对象
 * @author zhouyong
 * @data 创建时间：2013-10-31  上午10:40:17
 */
public class CommentBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6350027900157073370L;
	private int id;
	private String content;
	private String time;
	private String title;
	private String name;

	public CommentBean(JSONObject obj) throws JSONException{
		if(obj.has("id")){
			this.id = obj.getInt("id");
		}
		if(obj.has("content")){
			this.content = obj.getString("content");
		}
		if(obj.has("pub_date")){
			this.time = obj.getString("pub_date");
		}
		if(obj.has("title")){
			this.title = obj.getString("title");
		}
		if(obj.has("username")){
			this.name = obj.getString("username");
		}
	}
	/**
	 * 构建list
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static List<CommentBean> constractList(JSONArray array) throws JSONException{
		List<CommentBean> list = new ArrayList<CommentBean>();
		for (int i = 0; i < array.length(); i++) {
			CommentBean bean = new CommentBean(array.getJSONObject(i));
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
