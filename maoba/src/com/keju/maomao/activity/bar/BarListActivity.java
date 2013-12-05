/**
 * 
 */
package com.keju.maomao.activity.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.CommonApplication;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.BarBean;
import com.keju.maomao.bean.BarTypeBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.imagecache.ImageCache;
import com.keju.maomao.imagecache.ImageCache.ImageCacheParams;
import com.keju.maomao.imagecache.ImageFetcher;
import com.keju.maomao.util.AndroidUtil;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.StringUtil;
import com.keju.maomao.view.azzviewpager.JazzyViewPager;
import com.keju.maomao.view.azzviewpager.JazzyViewPager.TransitionEffect;
import com.keju.maomao.view.azzviewpager.OutlineContainer;

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

	private JazzyViewPager viewPage;// 推荐酒吧滚动控件

	private MyPagerAdapter barAdapter;
	private ArrayList<View> views = new ArrayList<View>();
	private LinearLayout viewMenuList;// 原点
	private int currPosition = 0;// 当前位置
	private View viewBanner;
	private ImageView ivBanner;

	private Handler iHandler;
	private TimerTask timerTask;
	private Timer timer;
	private static final int HANDLE_TYPE_RUN = 1;

	private ImageFetcher mImageFetcher;
	private int screenWidth;// 屏幕宽度

	private int count = 0;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;// 是否为筛选

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	private boolean isFirst = true;// 是否第一次进该界面
	private BarTypeBean bean;

	private ProgressDialog pd;
	private CommonApplication app;

	private TextView tvNearbyBar;// 附近酒啊

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_list);

		app = (CommonApplication) getApplication();
		bean = (BarTypeBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		findView();
		fillData();

		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();// 获取图片宽度
		ImageCacheParams cacheParams = new ImageCacheParams(this, Constants.APP_DIR_NAME);
		cacheParams.memoryCacheEnabled = false;
		cacheParams.compressQuality = 60;
		mImageFetcher = new ImageFetcher(this, (int) ((screenWidth + 100) * AndroidUtil.getDensity(this)));
		mImageFetcher.addImageCache(cacheParams);
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		llCommon = findViewById(R.id.llCommon);
		lvBarList = (ListView) this.findViewById(R.id.lvBarList);

		viewPage = (JazzyViewPager) this.findViewById(R.id.viewPage);
		viewPage.setTransitionEffect(TransitionEffect.FlipVertical);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(bean.getName());
		tvTitle.setOnClickListener(this);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

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
		initMessageHandler();
		if (NetUtil.checkNet(BarListActivity.this)) {
			new GetBarListTask().execute();
			app.initBMapInfo();
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
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
				getResources().getDrawable(R.drawable.ic_filter_up_arrow), null);

		displayHeight = (BarListActivity.this).getWindowManager().getDefaultDisplay().getHeight();
		int maxHeight = (int) (displayHeight * 0.8);

		View view = LayoutInflater.from(this).inflate(R.layout.popu_data_picker_list, null);
		lvScreenArea = (ListView) view.findViewById(R.id.lvScreenArea);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, maxHeight);
		view.setLayoutParams(p);
		LinearLayout layout = new LinearLayout(this);
		layout.addView(view, p);
		pw = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
			tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_filter_down_arrow), null);
			return true;
		} else {
			return false;
		}

	}

	public void onBackPressed() {
		if (!onPressBack()) {
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
				pageIndex = 1;
				if (NetUtil.checkNet(BarListActivity.this)) {
					isFilter = true;
					new GetBarListTask(0).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {
				BarBean bean1 = ScreenAreaList.get(arg2);
				pageIndex = 1;
				if (NetUtil.checkNet(BarListActivity.this)) {
					isFilter = true;
					new GetBarListTask(bean1.getCityId()).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			}

			onPressBack();
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
			} else {
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
	 * 获取酒吧列表
	 * 
	 */
	public class GetBarListTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		private int cityId = 0;

		/**
		 * @param pageIndex
		 * @param i
		 * @param cityid
		 */

		public GetBarListTask() {

		}

		public GetBarListTask(int cityId) {
			this.cityId = cityId;

		}

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
				return new BusinessHelper().getBarList(bean.getId(), cityId, pageIndex);
			} catch (SystemException e) {
				e.printStackTrace();
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
					if (isFilter) {// 根据用户的需求再定吧
					} else {
						fillTodayRecommend(result.getObjList1());
					}
					if (isFirst) {
						ScreenAreaList.addAll(result.getObjList2());
					}
				}
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					barList.addAll(tempList);
					adapter.notifyDataSetChanged(); // 通知更新
					pageIndex++;
				} else {
					showShortToast("该地区无相应的酒吧");
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
			if (isFilter) {
				isComplete = false;
			}
			adapter.notifyDataSetChanged();
			isLoad = false;
			isFilter = false;
			isFirst = false;
		}

	}

	/**
	 * 填充今日推荐数据
	 * 
	 * @param list
	 */
	private void fillTodayRecommend(final List<BarBean> hotlist) {
		if (hotlist.size() <= 0) {
			return;
		}
		views.clear();
		viewMenuList.removeAllViews();
		count = hotlist.size();
		for (int i = 0; i < hotlist.size(); i++) {
			final BarBean bean = hotlist.get(i);
			viewBanner = getLayoutInflater().inflate(R.layout.today_commened_item, null);
			ivBanner = (ImageView) viewBanner.findViewById(R.id.ivBannerImage);
			String picUrl = bean.getRecommendImageUrl();
			final int viewPagerHeight = viewPage.getHeight();

			ImageUtil.resetViewSize(ivBanner, screenWidth, viewPagerHeight);
			if (mImageFetcher != null) {
				mImageFetcher.loadImage(picUrl, ivBanner);
			}
			ivBanner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putSerializable(Constants.EXTRA_DATA, bean);
					openActivity(BarDetailActivity.class, b);

				}
			});
			views.add(viewBanner);

			ImageView iviewMenuList = new ImageView(this);// 原点设置
			LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
			params.leftMargin = 2;
			params.rightMargin = 2;
			iviewMenuList.setLayoutParams(params);
			if (i == 0) {
				iviewMenuList.setBackgroundResource(R.drawable.ic_pager_sel);
			} else {
				iviewMenuList.setBackgroundResource(R.drawable.ic_pager_nor);
			}
			viewMenuList.addView(iviewMenuList, i);
			if (count == 1) {
				viewMenuList.setVisibility(View.GONE);
			} else {
				viewMenuList.setVisibility(View.VISIBLE);
			}
		}
		barAdapter = new MyPagerAdapter(views);
		viewPage.setAdapter(barAdapter);
		// int maxSize = 65535;

		viewPage.setCurrentItem(0);
		viewPage.setOnPageChangeListener(listener);
		startTask();

	}

	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			if (count == 1 && position == 1) {
				viewPage.setCurrentItem(0);
				return;
			}
			currPosition = position;
			changeState(position);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	/**
	 * 改变原点的状态
	 * 
	 * @param position
	 */
	private void changeState(int position) {
		int pos = position % count;
		int count = viewMenuList.getChildCount();
		for (int i = 0; i < count; i++) {
			ImageView ivItem = (ImageView) viewMenuList.getChildAt(i);
			if (i == pos) {
				ivItem.setBackgroundResource(R.drawable.ic_pager_sel);
			} else {
				ivItem.setBackgroundResource(R.drawable.ic_pager_nor);
			}
		}
	}

	private void startTask() {
		if (timerTask == null) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = HANDLE_TYPE_RUN;
					iHandler.sendMessage(msg);
				}
			};
			timer = new Timer();
			timer.schedule(timerTask, 0, 5000);
		}
	}

	private void closeTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask = null;
		}
	}

	private void initMessageHandler() {
		iHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case HANDLE_TYPE_RUN:
					if (++currPosition == count) {
						currPosition = 0;
					}
					viewPage.setCurrentItem(currPosition);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void onPause() {
		super.onPause();
		closeTimer();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ImageCache mImageCache = mImageFetcher.getImageCache();
		if (mImageCache != null) {
			mImageCache.clearCache();
			mImageCache.close();
			mImageCache = null;
		}
		mImageFetcher.closeCache();
		mImageFetcher.clearCache();
		mImageFetcher = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		if (count > 1) {
			startTask();
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
			// holder.tvAddress.setText(bean.getBar_Address());
			StringTokenizer token = new StringTokenizer(bean.getBar_Address(), "$");
			String[] add = new String[3];
			int i = 0;
			while (token.hasMoreTokens()) {
				add[i] = token.nextToken();
				i++;
				String address1 = add[0];
				String string = bean.getBarStreet();
				holder.tvAddress.setText(address1 + string);// 酒吧地址
			}
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

	/**
	 * ViewPager的适配器
	 * 
	 * @author Zhoujun
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {
		private ArrayList<View> views;

		public MyPagerAdapter(ArrayList<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			if (count == 1) {
				return 1;
			}
			return Integer.MAX_VALUE;// 是否循环滚动
		}

		public void setData(ArrayList<View> views) {
			this.views = views;
		}

		private void clear() {
			if (views != null)
				views.clear();
			this.notifyDataSetChanged();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			// return arg0 == arg1;
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// ((ViewPager) container).removeView(views.get(position));
			((ViewPager) container).removeView(viewPage.findViewFromObject(position));

		}

		@Override
		public Object instantiateItem(View container, int position) {
			try {
				((ViewPager) container).addView(views.get(position % views.size()), 0);
			} catch (Exception e) {

			}
			return views.get(position % views.size());
		}

	}

}
