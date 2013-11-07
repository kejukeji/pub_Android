/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.CommonApplication;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.maoba.bean.BarTypeBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.StringUtil;

/**
 * 酒吧列表
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-21 下午10:47:28
 */
public class BarListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;
	private View llCommon;

	private ListView lvBarList;
	private Adapter adapter;
	private ArrayList<BarBean> barList;
	private List<BarBean> hotList = new ArrayList<BarBean>();// 热门推荐酒吧
	private List<BarBean> ScreenAreaList;// 地区

	private LinearLayout viewMenuList;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	private BarTypeBean bean;

	private ProgressDialog pd;
	private CommonApplication app;

	private TextView tvNearbyBar;// 附近酒啊

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_list);

		bean = (BarTypeBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		findView();
		fillData();
		app = (CommonApplication) getApplication();
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		llCommon = findViewById(R.id.llCommon);
		lvBarList = (ListView) this.findViewById(R.id.lvBarList);

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(bean.getName());
		tvTitle.setOnClickListener(this);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

		// 今日推荐
		viewMenuList = (LinearLayout) this.findViewById(R.id.viewMenuList);

		tvNearbyBar = (TextView) this.findViewById(R.id.tvNearbyBar);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);

		btnRight.setOnClickListener(this);
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setText("搜索");

		tvNearbyBar.setOnClickListener(this);

		barList = new ArrayList<BarBean>();
		adapter = new Adapter();
		lvBarList.addFooterView(vFooter);
		lvBarList.setAdapter(adapter);
		lvBarList.setOnScrollListener(LoadListener);
		lvBarList.setOnItemClickListener(itemListener);
		lvBarList.setDivider(null);
		lvBarList.setFooterDividersEnabled(false);

		ScreenAreaList = new ArrayList<BarBean>();// 地区
		ScreenAreaList.add(new BarBean(0, "全部地区"));

		if (NetUtil.checkNet(BarListActivity.this)) {
			new GetBarListTask().execute();

		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			openActivity(SearchActivity.class);
			break;
		case R.id.tvTitle:
			showScreenAreaPopuWindow();
			break;
		case R.id.tvNearbyBar:
			openActivity(NearbyBarListActivity.class);
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
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= barList.size()) {
				return;
			}
			BarBean bean = barList.get(arg2);
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, bean);
			openActivity(BarDetailActivity.class, b);
		}
	};
	/**
	 * 滚动监听器
	 */
	OnScrollListener LoadListener = new OnScrollListener() {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount) {
				isLoadMore = true;
			} else {
				isLoadMore = false;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 滚动到最后，默认加载下一页
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isLoadMore) {
				if (NetUtil.checkNet(BarListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetBarListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/**
	 * 显示筛选地区 使用的方法是PopuWindow
	 * 
	 */
	private PopupWindow pw;
	private int displayHeight;
	private ListView lvScreenArea;
	private ScreenAreaAdapter screenAdapter;

	private void showScreenAreaPopuWindow() {
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_filter_up_arrow), null);

		displayHeight = (BarListActivity.this).getWindowManager().getDefaultDisplay().getHeight();
		int maxHeight = (int) (displayHeight * 0.8);

		View view =  LayoutInflater.from(this).inflate(R.layout.popu_data_picker_list, null);
		lvScreenArea = (ListView) view.findViewById(R.id.lvScreenArea);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, maxHeight);
		view.setLayoutParams(p);
		LinearLayout layout = new LinearLayout(this);
		layout.addView(view, p);
		pw = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
		pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_date_picker));
		pw.setAnimationStyle(R.style.PopupWindowAnimation);
		pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_lightransparent));
		// 设置焦点，必须设置，否则listView无法响应
		pw.setFocusable(true);
		// 设置点击其他地方 popupWindow消失
		pw.setOutsideTouchable(true);
		// pw.showAsDropDown(btnLeft);
		pw.showAsDropDown(llCommon);
		screenAdapter = new ScreenAreaAdapter();
		lvScreenArea.setAdapter(screenAdapter);
		lvScreenArea.addFooterView(vFooter);
		lvScreenArea.setDivider(null);
		lvScreenArea.setOnItemClickListener(ItemListener1);
		layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPressBack();
			}
		});
	}
	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (pw != null && pw.isShowing()) {
			pw.dismiss();
			tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_filter_down_arrow), null);
			return true;
		} else {
			return false;
		}

	}
	public void onBackPressed() {
		if(!onPressBack()){
			finish();
		}
	};
	/**
	 * 筛选地区点击 listview点击事件
	 */
	OnItemClickListener ItemListener1 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= ScreenAreaList.size()) {
				return;
			}
			if (arg2 == 0) {
				if (NetUtil.checkNet(BarListActivity.this)) {
					isFilter = true;
					new GetScreenAreaTask(0, bean.getId()).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {
				BarBean bean1 = ScreenAreaList.get(arg2);
				if (NetUtil.checkNet(BarListActivity.this)) {
					isFilter = true;
					new GetScreenAreaTask(bean1.getCityId(), bean.getId()).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			}

			pw.dismiss();
		}
	};

	/**
	 * 
	 * 筛选地区适配器
	 * 
	 * */
	private class ScreenAreaAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return ScreenAreaList.size();
		}

		@Override
		public Object getItem(int position) {
			return screenAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BarBean bean = ScreenAreaList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.popu_data_picker_item, null);
				holder.tvScreenArea = (TextView) convertView.findViewById(R.id.tvScreenArea);
				holder.creenArealine = (View) convertView.findViewById(R.id.creenArealine);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.tvScreenArea.setTextColor(getResources().getColor(R.color.blue));
				holder.creenArealine.setBackgroundResource(R.drawable.bg_bar_details_line1);
			} else{
				holder.tvScreenArea.setTextColor(getResources().getColor(R.color.black));
				holder.creenArealine.setBackgroundResource(R.drawable.bg_bar_details_line);
			}
			holder.tvScreenArea.setText(bean.getScreenAreaName());
			return convertView;
		}

		class ViewHolder {
			private TextView tvScreenArea;
			private View creenArealine;
		}

	}

	/**
	 * 地区酒吧筛选
	 * 
	 */
	private class GetScreenAreaTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		private int cityId, barId;
		private int pageIndex1 = 1;

		/**
		 * @param cityId
		 * @param barId
		 */
		public GetScreenAreaTask(int cityId, int barId) {
			this.cityId = cityId;
			this.barId = barId;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);

		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getScreenArea(cityId, pageIndex1, barId);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<BarBean> result) {
			super.onPostExecute(result);
			dismissPd();
			pbFooter.setVisibility(View.GONE);
			if (isFilter) {
				barList.clear();
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				List<BarBean> tempList = result.getObjList();
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					barList.addAll(tempList);
					adapter.notifyDataSetChanged(); // 通知更新
					pageIndex++;
				} else {
					showShortToast("该地区没有相应的酒吧...");
					isLastPage = true;
				}
				if (isLastPage) {
					pbFooter.setVisibility(View.GONE);
					tvFooterMore.setText(R.string.load_all);
					isComplete = true;
				} else {
					if (tempList.size() > 0 && tempList.size() < Constants.PAGE_SIZE) {
						pbFooter.setVisibility(View.GONE);
						tvFooterMore.setText(R.string.load_all);
						isComplete = true;
					} else {
						pbFooter.setVisibility(View.GONE);
						tvFooterMore.setText("上拉查看更多");
					}
				}
				if ((pageIndex == 1 || pageIndex == 2) && tempList.size() < Constants.PAGE_SIZE) {
					tvFooterMore.setText("");
				}

			} else {
				showShortToast(result.getError());
				tvFooterMore.setText("");
			}
			adapter.notifyDataSetChanged();
			isLoad = false;
			isFilter = false;
		}

	}

	/**
	 * 获取酒吧列表
	 * 
	 */
	public class GetBarListTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isLoadMore) {
				isLoad = true;
				pbFooter.setVisibility(View.VISIBLE);
				tvFooterMore.setText(R.string.loading);
			} else {
				if (pd == null) {
					pd = new ProgressDialog(BarListActivity.this);
				}
				pd.setMessage(getString(R.string.loading));
				pd.show();
			}
		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getBarList(bean.getId(), pageIndex);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<BarBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			pbFooter.setVisibility(View.GONE);
			if (isFilter) {
				barList.clear(); // 就是如果是筛选 就清除listview数据
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				// 这里获取到十条数据
				List<BarBean> tempList = result.getObjList();
				if (pageIndex == 1) {
					hotList.addAll(result.getObjList1());
					fillTodayRecommend(result.getObjList1());
					ScreenAreaList.addAll(result.getObjList2());
				}
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					barList.addAll(tempList);
					adapter.notifyDataSetChanged(); // 通知更新
					pageIndex++;
				} else {
					isLastPage = true;
				}
				if (isLastPage) {
					pbFooter.setVisibility(View.GONE);
					tvFooterMore.setText(R.string.load_all);
					isComplete = true;
				} else {
					if (tempList.size() > 0 && tempList.size() < Constants.PAGE_SIZE) {
						pbFooter.setVisibility(View.GONE);
						tvFooterMore.setText(R.string.load_all);
						isComplete = true;
					} else {
						pbFooter.setVisibility(View.GONE);
						tvFooterMore.setText("上拉查看更多");
					}
				}
				if (pageIndex == 1 && tempList.size() == 0) {
					tvFooterMore.setText("");
				}

			} else {
				showShortToast(result.getError());
				tvFooterMore.setText("");
			}
			adapter.notifyDataSetChanged();
			isLoad = false;
			isFilter = false;
		}

	}

	/**
	 * 填充今日推荐数据
	 * 
	 * @param list
	 * 
	 */
	private void fillTodayRecommend(final List<BarBean> hotlist) {
		if (hotlist == null) {
			return;
		}
		for (int i = 0; i < hotlist.size(); i++) {
			final BarBean bean = hotlist.get(i);
			LinearLayout.LayoutParams paramItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			paramItem.rightMargin = 3;// 图片居上5dp
			// paramItem.topMargin =2;
			final View view = getLayoutInflater().inflate(R.layout.today_commened_item, null);
			view.setLayoutParams(paramItem);
			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

			String picUrl = bean.getRecommendImageUrl();
			ivPhoto.setTag(picUrl);
			if (!TextUtils.isEmpty(picUrl)) {
				Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(picUrl, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						ImageView image = (ImageView) viewMenuList.findViewWithTag(imageUrl);
						if (image != null) {
							if (imageDrawable != null) {
								image.setImageDrawable(imageDrawable);
							} else {
								image.setImageResource(R.drawable.ic_default);
							}
						}
					}
				});
				if (cacheDrawble != null) {
					ivPhoto.setImageDrawable(cacheDrawble);
				} else {
					ivPhoto.setImageResource(R.drawable.ic_default);
				}
			}
			ivPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putSerializable(Constants.EXTRA_DATA, bean);
					openActivity(BarDetailActivity.class, b);

				}
			});
			viewMenuList.addView(view);
		}
	}

	/**
	 * 适配器
	 * 
	 **/

	public class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return barList.size();
		}

		@Override
		public Object getItem(int position) {
			return barList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			BarBean bean = barList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.bar_item, null);
				holder.tvBarName = (TextView) convertView.findViewById(R.id.tvBarName);
				holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
				holder.tvDistanceLabel = (TextView) convertView.findViewById(R.id.tvDistanceLabel);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tvcontent);
				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url = bean.getImageUrl();
			holder.ivImage.setTag(url);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) lvBarList.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
						} else {
							image.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawble != null) {
				holder.ivImage.setImageDrawable(cacheDrawble);
			} else {
				holder.ivImage.setImageResource(R.drawable.ic_default);
			}
			holder.tvBarName.setText(bean.getBar_Name());
			holder.tvAddress.setText(bean.getBar_Address());
			holder.tvContent.setText(bean.getBar_Intro());

			double latitude;
			try {
				latitude = Double.parseDouble(bean.getLatitude());
			} catch (NumberFormatException e) {
				latitude = 0;
			}
			double longitude;
			try {
				longitude = Double.parseDouble(bean.getLongitude());
			} catch (NumberFormatException e) {
				longitude = 0;
			}
			if (app.getLastLocation() != null) {
				double distance = StringUtil.getDistance(app.getLastLocation().getLatitude(), app.getLastLocation()
						.getLongitude(), latitude, longitude);
				if (distance > 1000) {
					distance = distance / 1000;
					holder.tvDistanceLabel.setText(String.format("%.1f", distance) + "km");
				} else {
					holder.tvDistanceLabel.setText(String.format("%.0f", distance) + "m");
				}
			} else {
				holder.tvDistanceLabel.setText("");
			}
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tvBarName, tvDistanceLabel, tvAddress, tvContent;
		private ImageView ivImage;
	}

}
