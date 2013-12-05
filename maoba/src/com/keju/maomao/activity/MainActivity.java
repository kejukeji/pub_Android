package com.keju.maomao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.CommonApplication;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.bar.BarListActivity;
import com.keju.maomao.activity.base.BaseSlidingFragmentActivity;
import com.keju.maomao.activity.event.EventListActivity;
import com.keju.maomao.activity.my.CollectionOfBarListActivity;
import com.keju.maomao.activity.news.NewsActivity;
import com.keju.maomao.activity.personalcenter.PersonalCenter;
import com.keju.maomao.activity.setting.SettingActivity;
import com.keju.maomao.bean.BarTypeBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.AndroidUtil;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.view.GridViewInScrollView;
import com.keju.maomao.view.slidingmenu.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-15 下午2:52:49
 */

public class MainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener {
	private SlidingMenu sm;
	// 侧边栏
	private LinearLayout rlCollect;// 收藏
	private LinearLayout rlInfromation;// 信息
	private LinearLayout rlSetting;// 设置
	private LinearLayout rlMain;// 首页
	private LinearLayout viewSettingTitle;
	private ImageView ivSettingUserPhoto;
	private TextView tvsignaTure;
	private TextView tvNewMessagePoint;
	// 主页
	private Button btnLeftMenu;
	private int screenWidth;
	private ImageView ivTop;
	private TextView tvTop;
	private GridViewInScrollView gvBarType;
	private List<BarTypeBean> barTypeList;
	private Adapter adapter;
	private ImageView ivBanner;//广告
	
	private CommonApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		app = (CommonApplication) getApplication();
		Display display = this.getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		initSlidingMenu();
		findView();
		fillData();
		app.addActivity(this);
	}

	/**
	 * 初始化SlidingMenu
	 */
	private void initSlidingMenu() {
		setBehindContentView(R.layout.left_menu);
		// customize the SlidingMenu
		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// sm.setShadowWidth(20);
		sm.setBehindScrollScale(0);
	}

	private void findView() {

		btnLeftMenu = (Button) findViewById(R.id.btnLeftMenu);// 头部界面左边按钮控件
		btnLeftMenu.setOnClickListener(this);
		ivTop = (ImageView) findViewById(R.id.ivTop);
		ivBanner = (ImageView) findViewById(R.id.ivBanner);
		gvBarType = (GridViewInScrollView) findViewById(R.id.gvBarType);
		tvTop = (TextView) findViewById(R.id.tvTop);

		ivSettingUserPhoto = (ImageView) findViewById(R.id.ivSettingUserPhoto);
		tvsignaTure = (TextView) findViewById(R.id.tvsignaTure);
		rlCollect = (LinearLayout) findViewById(R.id.rlCollect);
		rlInfromation = (LinearLayout) findViewById(R.id.rlInfromation);
		rlSetting = (LinearLayout) findViewById(R.id.rlSetting);
		rlMain = (LinearLayout) findViewById(R.id.rlMain);
		viewSettingTitle = (LinearLayout) findViewById(R.id.viewSettingTitle);
		tvNewMessagePoint = (TextView) findViewById(R.id.tv_new_message_point);

		rlCollect.setOnClickListener(this);
		rlInfromation.setOnClickListener(this);
		rlSetting.setOnClickListener(this);
		rlMain.setOnClickListener(this);
		viewSettingTitle.setOnClickListener(this);

	}

	private void fillData() {
		barTypeList = new ArrayList<BarTypeBean>();
		adapter = new Adapter();
		gvBarType.setAdapter(adapter);
		gvBarType.setOnItemClickListener(itemListener);
		if (NetUtil.checkNet(this)) {
			new GetHomeTask().execute();
			new GetNewMessage().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BarTypeBean bean = barTypeList.get(position);
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, bean);
			openActivity(BarListActivity.class, b);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLeftMenu:
			showMenu();
			break;
		case R.id.rlCollect:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								openActivity(LoginActivity.class);
							}
						}, null, null);
				return;
			}
			int uid = SharedPrefUtil.getUid(MainActivity.this);
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_DATA, uid);
			openActivity(CollectionOfBarListActivity.class, b);
			break;
		case R.id.rlInfromation:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								openActivity(LoginActivity.class);
							}
						}, null, null);
				return;
			}
			openActivity(NewsActivity.class);
			break;
		case R.id.rlSetting:
			if (!SharedPrefUtil.isLogin(this)) {
				showAlertDialog(R.string.msg, R.string.no_login,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								openActivity(LoginActivity.class);
							}
						}, null, null);
				return;
			}
			openActivity(SettingActivity.class);
			break;
		case R.id.rlMain:
			 openActivity(MainActivity.class);
			break;
		case R.id.viewSettingTitle:
			openActivity(PersonalCenter.class);
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * 获取用户个人资料信息
	 * 
	 * */
	private class GetUserInfor extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(MainActivity.this);
			try {
				return new BusinessHelper().getUserInfor(uid);
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
						JSONObject userJson = result.getJSONObject("user_info");
						String signaTure = userJson.getString("signature");
						if (signaTure.equals("null")) {
							tvsignaTure.setText("未设置");
						} else {
							tvsignaTure.setText(signaTure);
						}
						String photoUrl = BusinessHelper.PIC_BASE_URL
								+ userJson.getString("pic_path");
						ivSettingUserPhoto.setTag(photoUrl);
						Drawable cacheDrawble = AsyncImageLoader.getInstance()
								.loadDrawable(photoUrl, new ImageCallback() {
									@Override
									public void imageLoaded(
											Drawable imageDrawable,
											String imageUrl) {
										ImageView image = (ImageView) ivSettingUserPhoto
												.findViewWithTag(imageUrl);
										Bitmap bitmap = ImageUtil
												.getRoundCornerBitmapWithPic(
														imageDrawable, 0.5f);

										if (image != null) {
											if (imageDrawable != null) {
												image.setImageBitmap(bitmap);
											} else {
												image.setImageResource(R.drawable.left_menu_userimage);
											}
										}
									}
								});
						if (cacheDrawble != null) {
							Bitmap bitmap = ImageUtil
									.getRoundCornerBitmapWithPic(cacheDrawble,
											0.5f);
							ivSettingUserPhoto.setImageBitmap(bitmap);
						} else {
							ivSettingUserPhoto
									.setImageResource(R.drawable.left_menu_userimage);
						}

					} else {
						showShortToast(result.getString("message"));

					}
				} catch (JSONException e) {
					ivSettingUserPhoto
							.setImageResource(R.drawable.left_menu_userimage);
				}
			} else {
				ivSettingUserPhoto
						.setImageResource(R.drawable.left_menu_userimage);
				tvsignaTure.setText("未设置");
			}
		}

	}

	private class GetNewMessage extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(MainActivity.this);
			try {
				return new BusinessHelper().getSysLetter1(uid);
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
					if (status == Constants.REQUEST_FAILD) {
						tvNewMessagePoint.setVisibility(View.GONE);
					} else {
						int systemMessageCount = result.getInt("system_count");
						int privateMessageCount = result.getInt("direct_count");
						int finalCount = systemMessageCount
								+ privateMessageCount;
						if (finalCount == 0) {
							tvNewMessagePoint.setVisibility(View.GONE);
						} else {
							tvNewMessagePoint.setVisibility(View.VISIBLE);
						}
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
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
						String bannerUrl = result.getString("advertising_picture");
						if(bannerUrl == null){
							return;
						}
						ivBanner.setTag(BusinessHelper.PIC_BASE_URL+bannerUrl);
						Drawable cacheDrawable1 = AsyncImageLoader.getInstance()
								.loadDrawable(BusinessHelper.PIC_BASE_URL+bannerUrl,new ImageCallback() {
											@Override
											public void imageLoaded(
													Drawable imageDrawable,
													String imageUrl) {
												if (imageDrawable != null) {
													ivBanner.setImageDrawable(imageDrawable);
												}
											}
										});
						if (cacheDrawable1 != null) {
							ivBanner.setImageDrawable(cacheDrawable1);
						}
						ivBanner.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
//								MobclickAgent.onEvent(HomeActivity.this, " banner_click", bean.getTitle() + bean.getLink());
								Bundle b = new Bundle();
								b.putString(Constants.EXTRA_DATA, "http://www.tmall.com/");
								b.putString("name", "百度");
								openActivity(WebviewActivity.class, b);
							}
						});
						List<BarTypeBean> tempList = BarTypeBean
								.constractList(result.getJSONArray("list"));
						final BarTypeBean topBean = tempList.get(0);
						tvTop.setText(topBean.getName());
						ivTop.setTag(topBean.getUrl());
						Drawable cacheDrawable = AsyncImageLoader.getInstance()
								.loadDrawable(topBean.getUrl(),
										new ImageCallback() {

											@Override
											public void imageLoaded(
													Drawable imageDrawable,
													String imageUrl) {
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
				convertView = getLayoutInflater().inflate(
						R.layout.bar_type_item, null);
				holder.ivImage = (ImageView) convertView
						.findViewById(R.id.ivImage);
				holder.tvBarType = (TextView) convertView
						.findViewById(R.id.tvBarType);
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
			Drawable cacheDrawable = AsyncImageLoader.getInstance()
					.loadDrawable(bean.getUrl(), new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView image = (ImageView) gvBarType
									.findViewWithTag(imageUrl);
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

	/**
	 * 连续按两次返回键就退出
	 */
	private int keyBackClickCount = 0;

	@Override
	protected void onResume() {
		super.onResume();
		if (SharedPrefUtil.isLogin(this)) {
			if (NetUtil.checkNet(this)) {
				new GetUserInfor().execute();
				new GetNewMessage().execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
		}
		keyBackClickCount = 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (keyBackClickCount++) {
			case 0:
				showShortToast("请再按次返回键退出");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 3000);
				break;
			case 1:
				defaultFinish();
				AndroidUtil.exitApp(MainActivity.this);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
