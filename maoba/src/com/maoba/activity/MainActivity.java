package com.maoba.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.bar.BarListActivity;
import com.maoba.activity.base.BaseActivity;
import com.maoba.activity.my.CollectionOfBarListActivity;
import com.maoba.activity.news.NewsActivity;
import com.maoba.activity.news.PrivateNewsListActivity;
import com.maoba.activity.personalcenter.PersonalCenter;
import com.maoba.activity.setting.SettingActivity;
import com.maoba.bean.BarTypeBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.AndroidUtil;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.maoba.view.GridViewInScrollView;
import com.maoba.view.MyHorizontalScrollView;
import com.maoba.view.MyHorizontalScrollView.SizeCallback;

public class MainActivity extends BaseActivity implements OnClickListener {
	private MyHorizontalScrollView scrollView; // 水平滑动控件按钮
	private static View settingView;// 设置界面
	private static View homeView;// 主界
	private static View currentView;// 当前显示的view

	private LinearLayout rlCollect;// 收藏
	private LinearLayout rlInfromation;// 信息
	private LinearLayout rlSetting;// 设置
	private LinearLayout viewSettingTitle;

	private Button btnLeftMenu;
	private int screenWidth;
	private ImageView ivTop;
	private TextView tvTop;
	private GridViewInScrollView gvBarType;
	private List<BarTypeBean> barTypeList;
	private Adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Display display = this.getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		findView();
		fillData();
	}

	private void findView() {
		// 设置界面
		LayoutInflater inflater = LayoutInflater.from(this);
		scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.main, null);// 加载水平滑动控件按钮
		setContentView(scrollView);

		settingView = inflater.inflate(R.layout.left_menu, null);// 加载左边菜单栏界
		setContentView(scrollView);

		/************************** 侧边栏 **********************/
		settingView = inflater.inflate(R.layout.left_menu, null);// 加载左边菜单栏界

		rlCollect = (LinearLayout) settingView.findViewById(R.id.rlCollect);
		rlInfromation = (LinearLayout) settingView.findViewById(R.id.rlInfromation);
		rlSetting = (LinearLayout) settingView.findViewById(R.id.rlSetting);
		viewSettingTitle = (LinearLayout) settingView.findViewById(R.id.viewSettingTitle);

		rlCollect.setOnClickListener(this);
		rlInfromation.setOnClickListener(this);
		rlSetting.setOnClickListener(this);
		viewSettingTitle.setOnClickListener(this);
		/************************** 主页 **********************/

		homeView = inflater.inflate(R.layout.home, null);// 加载头部按钮界面
		btnLeftMenu = (Button) homeView.findViewById(R.id.btnLeftMenu);// 头部界面左边按钮控件
		btnLeftMenu.setOnClickListener(new ClickListenerForScrolling(scrollView, settingView));
		ivTop = (ImageView) homeView.findViewById(R.id.ivTop);
		gvBarType = (GridViewInScrollView) homeView.findViewById(R.id.gvBarType);
		tvTop = (TextView) homeView.findViewById(R.id.tvTop);

		final View[] children = new View[] { settingView, homeView };
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnLeftMenu));
		currentView = homeView;

	}

	private void fillData() {
		barTypeList = new ArrayList<BarTypeBean>();
		adapter = new Adapter();
		gvBarType.setAdapter(adapter);
		gvBarType.setOnItemClickListener(itemListener);
		if (NetUtil.checkNet(this)) {
			new GetHomeTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BarTypeBean bean = barTypeList.get(position);
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, bean);
			openActivity(BarListActivity.class, b);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlCollect:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						openActivity(LoginActivity.class);
					}
				}, null, null);
				return;
			}
			openActivity(CollectionOfBarListActivity.class);
			break;
		case R.id.rlInfromation:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						openActivity(LoginActivity.class);
					}
				}, null, null);
				return;
			}
			openActivity(NewsActivity.class);
			break;
		case R.id.rlSetting:
			openActivity(SettingActivity.class);
			break;
		case R.id.viewSettingTitle:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						openActivity(LoginActivity.class);
					}
				}, null, null);
				return;
			}
			openActivity(PersonalCenter.class);
			break;
		default:
			break;
		}

	}

	static int left = 0;
	static boolean leftMenuOut = false;

	/**
	 * Menu must NOT be out/shown to start with.
	 */
	public static class ClickListenerForScrolling implements OnClickListener {
		private View view;
		private HorizontalScrollView scrollView;

		/**
		 * @param scrollView
		 * @param settingView
		 * 
		 */
		public ClickListenerForScrolling(HorizontalScrollView scrollView, View settingView) {
			super();
			this.view = settingView;
			this.scrollView = scrollView;
		}

		@Override
		public void onClick(View v) {
			// Log.i(TAG, "menu---onClick");
			if (v.getId() != R.id.btnLeftMenu && left > 0) {
				return;
			}
			int viewWidth = view.getMeasuredWidth();
			// Ensure menu is visible
			view.setVisibility(View.VISIBLE);
			if (v.getId() == R.id.btnLeftMenu) {
				if (!leftMenuOut) {
					/* 根据分辨来辨�?偏移 各种分辨率偏移是不一样的 */
					int offset = 0;
					left = 0;
					if (viewWidth <= 320) {
						offset = 20;
					} else if (viewWidth <= 480) {
						offset = 30;
					} else {
						offset = 40;
					}

					scrollView.smoothScrollTo(left + offset, 0);
					currentView = settingView;// 将左侧菜单栏给当前的view
				} else {
					left = viewWidth;
					scrollView.smoothScrollTo(left, 0);
					currentView = homeView;// 将主界面给当前的view
				}
				leftMenuOut = !leftMenuOut;
			}
		}

	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnLeftMenu;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnLeftMenu = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnLeftMenu.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			// final int menuIdx = 0;
			// if (idx == menuIdx) {
			// dims[0] = w - btnWidth;
			// }
			if (idx != 1) {
				// 当视图不是中间的视图
				dims[0] = w - btnWidth;
			}
		}
	}

	/**
	 * 获取首页数据
	 * 
	 * @author Zhoujun
	 * 
	 */
	private class GetHomeTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getHomeData();
			} catch (SystemException e) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					if (Constants.REQUEST_SUCCESS == result.getInt("status")) {
						List<BarTypeBean> tempList = BarTypeBean.constractList(result.getJSONArray("list"));
						final BarTypeBean topBean = tempList.get(0);
						tvTop.setText(topBean.getName());
						ivTop.setTag(topBean.getUrl());
						Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(topBean.getUrl(),
								new ImageCallback() {

									@Override
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										if (imageDrawable != null) {
											ivTop.setImageDrawable(imageDrawable);
										}
									}
								});
						if (cacheDrawable != null) {
							ivTop.setImageDrawable(cacheDrawable);
						}
						ivTop.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Bundle b = new Bundle();
								b.putSerializable(Constants.EXTRA_DATA, topBean);
								openActivity(BarListActivity.class, b);
							}
						});
						tempList.remove(0);
						barTypeList.addAll(tempList);
						adapter.notifyDataSetChanged();
					} else {
						showShortToast(R.string.connect_server_exception);
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}
	}

	/**
	 * 酒吧类型适配
	 * 
	 * @author Zhoujun
	 * 
	 */
	private class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return barTypeList.size();
		}

		@Override
		public Object getItem(int position) {
			return barTypeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			BarTypeBean bean = barTypeList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.bar_type_item, null);
				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
				holder.tvBarType = (TextView) convertView.findViewById(R.id.tvBarType);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvBarType.setText(bean.getName());
			int itemWidth = (screenWidth - 30 - 10) / 2;
			LayoutParams params = holder.ivImage.getLayoutParams();
			params.width = itemWidth;
			params.height = itemWidth * 2 / 3;
			holder.ivImage.setLayoutParams(params);
			holder.ivImage.setTag(bean.getUrl());
			Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(bean.getUrl(), new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) gvBarType.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
						}
					}
				}
			});
			if (cacheDrawable != null) {
				holder.ivImage.setImageDrawable(cacheDrawable);
			}

			return convertView;
		}

		class ViewHolder {
			private ImageView ivImage;
			private TextView tvBarType;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showAlertDialog(R.string.msg, R.string.logout, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					AndroidUtil.exitApp(MainActivity.this);
				}
			}, null, null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
