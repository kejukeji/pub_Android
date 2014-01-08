/**
 * 
 */
package com.keju.maomao.activity.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.bar.BarDetailActivity;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.EventBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.imagecache.ImageCache;
import com.keju.maomao.imagecache.ImageCache.ImageCacheParams;
import com.keju.maomao.imagecache.ImageFetcher;
import com.keju.maomao.util.AndroidUtil;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.view.azzviewpager.JazzyViewPager;
import com.keju.maomao.view.azzviewpager.OutlineContainer;

/**
 * 活动详情界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-12-5 下午3:28:38
 */
public class EventDetailActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private TextView tvEventTitle, tvDistanceLabel;// 活动标题和详细地址
	private TextView tvStartTime, tvEndTime;// 活动的开始时间和结束时间
	private TextView tvJionNum, tvEventContent;// 活动的蚕参加的人数和内容

	private List<EventBean> eventList = new ArrayList<EventBean>();// 活动

	private JazzyViewPager eventViewPager;// 活动图片滚动空间
	private MyPagerAdapter eventAdapter;

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

	// private boolean isCollectingTask = false;// 是否收藏

	private String barName;
	private int eventId; // 活动id
	private Boolean isCollect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail);

		barName = getIntent().getExtras().getString("BARNAME");
		eventId = getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		isCollect = getIntent().getExtras().getBoolean("ISCOLLECT");
		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();// 获取图片宽度
		ImageCacheParams cacheParams = new ImageCacheParams(this, Constants.APP_DIR_NAME);
		cacheParams.memoryCacheEnabled = false;
		cacheParams.compressQuality = 60;
		mImageFetcher = new ImageFetcher(this, (int) ((screenWidth + 100) * AndroidUtil.getDensity(this)));
		mImageFetcher.addImageCache(cacheParams);

		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		eventViewPager = (JazzyViewPager) this.findViewById(R.id.eventViewPager);

		viewMenuList = (LinearLayout) this.findViewById(R.id.viewMenuList);

		tvEventTitle = (TextView) this.findViewById(R.id.tvEventTitle);
		tvDistanceLabel = (TextView) this.findViewById(R.id.tvDistanceLabel);
		tvStartTime = (TextView) this.findViewById(R.id.tvStartTime);
		tvEndTime = (TextView) this.findViewById(R.id.tvEndTime);
		tvJionNum = (TextView) this.findViewById(R.id.tvJionNum);
		tvEventContent = (TextView) this.findViewById(R.id.tvEventContent);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		if (isCollect) {
			btnRight.setText("已收藏");
		} else {
			btnRight.setText("收藏");
		}
		tvTitle.setText(barName);

		initMessageHandler();
		if (NetUtil.checkNet(EventDetailActivity.this)) {
			new GetEventDetailTask().execute();
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
//			showShortToast("正在执行收藏操作,请稍等...");
			if (NetUtil.checkNet(this)) {
				new CollectTask().execute();
				// refreshData();
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 收藏
	 * 
	 * @author Zhouyong
	 * 
	 */
	private class CollectTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(EventDetailActivity.this);
			try {
				return new BusinessHelper().collectEvent(eventId, uid);
			} catch (SystemException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						if (isCollect) {
							btnRight.setText("收藏");
							showShortToast("取消收藏成功");
							isCollect = false;
						} else {
							btnRight.setText("已收藏");
							showShortToast("收藏成功");
							isCollect = true;
						}
					} else {
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
			// isCollect = false;
		}

	}

	/***
	 * 获取活动的详情
	 * 
	 */
	private class GetEventDetailTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int userId = SharedPrefUtil.getUid(EventDetailActivity.this);
			try {
				return new BusinessHelper().getEventDetail(eventId, userId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {
							JSONObject objEvent = result.getJSONObject("activity");
							tvEventTitle.setText(objEvent.getString("title"));
							tvDistanceLabel.setText(objEvent.getString("address"));
							tvStartTime.setText(objEvent.getString("start_date"));
							tvEndTime.setText(objEvent.getString("end_date"));
							tvJionNum.setText(objEvent.getInt("join_people_number") + "");
							tvEventContent.setText(objEvent.getString("activity_info"));
//							isCollect = objEvent.getBoolean("is_collect");
							if (result.has("activity_picture")) {
								JSONArray showArrList = result.getJSONArray("activity_picture");
								if (showArrList != null) {
									ArrayList<EventBean> showBeans = (ArrayList<EventBean>) EventBean
											.constractList(showArrList);
									eventList.addAll(showBeans);
									fillEventRecommend(showBeans);
								}
							}

						}
					} catch (JSONException e) {
						showShortToast(R.string.json_exception);
					}
				}

			} else {
				showShortToast("服务器连接失败");
			}
		}

	}

	/**
	 * 填充活动数据
	 * 
	 * @param list
	 */
	private void fillEventRecommend(final List<EventBean> eventList) {
		if (eventList.size() <= 0) {
			return;
		}
		views.clear();
		viewMenuList.removeAllViews();
		count = eventList.size();
		for (int i = 0; i < eventList.size(); i++) {
			final EventBean bean = eventList.get(i);
			viewBanner = getLayoutInflater().inflate(R.layout.today_commened_item, null);
			ivBanner = (ImageView) viewBanner.findViewById(R.id.ivBannerImage);
			String picUrl = bean.getRecommendPhotoUrl();
			final int viewPagerHeight = eventViewPager.getHeight();

			ImageUtil.resetViewSize(ivBanner, screenWidth, viewPagerHeight);
			if (mImageFetcher != null) {
				mImageFetcher.loadImage(picUrl, ivBanner);
			}
			ivBanner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

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
		eventAdapter = new MyPagerAdapter(views);
		eventViewPager.setAdapter(eventAdapter);
		// int maxSize = 65535;

		eventViewPager.setCurrentItem(0);
		eventViewPager.setOnPageChangeListener(listener);
		startTask();

	}

	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			if (count == 1 && position == 1) {
				eventViewPager.setCurrentItem(0);
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
					eventViewPager.setCurrentItem(currPosition);
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
			((ViewPager) container).removeView(eventViewPager.findViewFromObject(position));

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
