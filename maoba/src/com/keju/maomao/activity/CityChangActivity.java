/**
 * 
 */
package com.keju.maomao.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.SortModelBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.CharacterParser;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.PinyinComparator;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.view.citychangeview.ClearEditText;
import com.keju.maomao.view.citychangeview.SideBar;
import com.keju.maomao.view.citychangeview.SideBar.OnTouchingLetterChangedListener;

/**
 * 城市切换界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-12-12 下午2:08:45
 */
public class CityChangActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;

	private ListView sortListView;
	private SideBar sideBar;// 自定义字母排序控件
	private TextView dialog; // 显示手触摸字母时放大显示控件
	private SortAdapter adapter;
	private ClearEditText mClearEditText; // 重写editText控件

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModelBean> SourceDateList;

	private List<SortModelBean> cityList = new ArrayList<SortModelBean>();// 城市list

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_change_list);
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		sortListView = (ListView) findViewById(R.id.countryList);

		sortListView.setOnItemClickListener(itemListener);
		mClearEditText = (ClearEditText) findViewById(R.id.edFilterSearch);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				} else {
					sortListView.setSelection(0);
				}

			}
		});
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		tvTitle.setText("城市切换");

		if (NetUtil.checkNet(this)) {
			new GetCityTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		default:
			break;
		}

	}

	/**
	 * listview点击事件
	 */
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position >= cityList.size()) {
				return;
			}
			//这里要利用adapter.getItem(position)来获取当前position所对应的对象 否则筛选选择的不能对应
			
		    String cityName	=((SortModelBean)adapter.getItem(position)).getCityName();
		    int provinceId = ((SortModelBean)adapter.getItem(position)).getProvinceId();
			Intent intent = new Intent();
			intent.putExtra("PROVINCEID", cityName);
			intent.putExtra("CITYNAME", provinceId);
            
			SharedPrefUtil.setCityName(CityChangActivity.this, cityName);
			SharedPrefUtil.setProvinceId(CityChangActivity.this, provinceId);
			if (SharedPrefUtil.isFistCityActivity(CityChangActivity.this)) {
				intent.setClass(CityChangActivity.this, MainActivity.class);
				SharedPrefUtil.setFistLogined(CityChangActivity.this);
                startActivity(intent);
			} else {
				setResult(RESULT_OK, intent);
			}
			finish();
		}

	};

	/**
	 * 获取所有城市
	 * 
	 */
	private class GetCityTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getCity();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					if (result.getInt("status") == Constants.REQUEST_SUCCESS) {
						JSONArray cityArr = result.getJSONArray("city");
						if (cityArr != null) {
							ArrayList<SortModelBean> cityBean = (ArrayList<SortModelBean>) SortModelBean
									.constractList(cityArr);
							cityList.addAll(cityBean);
							filledData(cityBean);
							SourceDateList = cityList;

							// 根据a-z进行排序源数据
							Collections.sort(SourceDateList, pinyinComparator);
							adapter = new SortAdapter(SourceDateList);
							sortListView.setAdapter(adapter);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showShortToast(R.string.json_exception);
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param cityBean
	 * @return
	 */
	private List<SortModelBean> filledData(ArrayList<SortModelBean> cityBean) {
		List<SortModelBean> mSortList = new ArrayList<SortModelBean>();

		for (int i = 0; i < cityBean.size(); i++) {
			SortModelBean sortModel = cityBean.get(i);
			// sortModel.setName(cityBean.);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(sortModel.getCityName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("热门");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModelBean> filterDateList = new ArrayList<SortModelBean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModelBean sortModel : SourceDateList) {
				String name = sortModel.getCityName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	/**
	 * 城市切换适配器
	 * 
	 */
	private class SortAdapter extends BaseAdapter implements SectionIndexer {
		private List<SortModelBean> list = null;

		/**
		 * @param sourceDateList2
		 */
		public SortAdapter(List<SortModelBean> sourceDateList2) {
			this.list = sourceDateList2;
		}

		/**
		 * 当ListView数据发生变化时,调用此方法来更新ListView
		 * 
		 * @param list
		 */
		public void updateListView(List<SortModelBean> SourceDateList) {
			this.list = SourceDateList;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			SortModelBean mContent = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.city_change_item, null);
				viewHolder.tvCityName = (TextView) convertView.findViewById(R.id.tvCityName);
				viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getSortLetters());
			} else {
				viewHolder.tvLetter.setVisibility(View.GONE);
			}

			viewHolder.tvCityName.setText(mContent.getCityName());

			return convertView;
		}

		class ViewHolder {
			TextView tvLetter;
			TextView tvCityName;
		}

		@Override
		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			String sortStr = str.trim().substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "热门";
			}
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

}
