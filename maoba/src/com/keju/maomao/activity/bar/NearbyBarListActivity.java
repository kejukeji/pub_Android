/**
 * 
 */
package com.keju.maomao.activity.bar;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.StringUtil;

/**
 * 附近酒吧列表
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-4 下午8:32:15
 */
public class NearbyBarListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;

	private ListView lvNearbyBarList;
	private Adapter adapter;
	private ArrayList<BarBean> barList;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	private CommonApplication app;
	private boolean gpsIsOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_bar_list);

		findView();
		fillData();
		app = (CommonApplication) getApplication();
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText("附近酒吧");

		lvNearbyBarList = (ListView) this.findViewById(R.id.lvNearbyBar);

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);
		if (NetUtil.checkNet(NearbyBarListActivity.this)) {
			gpsIsOpen = NetUtil.isOPen(NearbyBarListActivity.this);
			if (gpsIsOpen) {
				app.initBMapInfo();
				new GetNearbyBarListTask().execute();
			} else {
				showShortToast("请先打开定位服务");
			}
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);

		barList = new ArrayList<BarBean>();
		adapter = new Adapter();
		lvNearbyBarList.setAdapter(adapter);
		lvNearbyBarList.addFooterView(vFooter);

		lvNearbyBarList.setOnScrollListener(LoadListener);
		lvNearbyBarList.setOnItemClickListener(itemListener);
		lvNearbyBarList.setDivider(null);
		lvNearbyBarList.setFooterDividersEnabled(false);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
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
				if (NetUtil.checkNet(NearbyBarListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetNearbyBarListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/**
	 * 获取附近酒吧列表
	 * 
	 */
	public class GetNearbyBarListTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isLoadMore) {
				isLoad = true;
				pbFooter.setVisibility(View.VISIBLE);
				tvFooterMore.setText(R.string.loading);
			} else {
				showPd("正在加载...");
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				double longitude = app.getLastLocation().getLongitude();
				double latitude = app.getLastLocation().getLatitude();
				return new BusinessHelper().getNearbyBarList(longitude, latitude, pageIndex);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			pbFooter.setVisibility(View.GONE);
			if (isFilter) {
				barList.clear();
			}
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {

							if (result.has("pub_list")) {
								JSONArray arr = result.getJSONArray("pub_list");
								if (arr != null) {
									ArrayList<BarBean> nearBean = (ArrayList<BarBean>) BarBean.constractList(arr);
									boolean isLastPage = false;
									if (nearBean.size() > 0) {
										barList.addAll(nearBean);
										adapter.notifyDataSetChanged(); // 通知更新
										pageIndex++;
									} else {
										showShortToast("您附近没有酒吧");
										isLastPage = true;
									}
									if (isLastPage) {
										pbFooter.setVisibility(View.GONE);
										tvFooterMore.setText(R.string.load_all);
										isComplete = true;
									} else {
										if (nearBean.size() > 0 && nearBean.size() < Constants.PAGE_SIZE) {
											pbFooter.setVisibility(View.GONE);
											tvFooterMore.setText(R.string.load_all);
											isComplete = true;
										} else {
											pbFooter.setVisibility(View.GONE);
											tvFooterMore.setText("上拉查看更多");
										}
									}
									if ((pageIndex == 1 || pageIndex == 2) && nearBean.size() < Constants.PAGE_SIZE) {
										tvFooterMore.setText("");
									}
									adapter.notifyDataSetChanged();
									isLoad = false;
									isFilter = false;
								}
							}
						} else {
							tvFooterMore.setText("");
						}
					} catch (JSONException e) {
						showShortToast("您附近没有酒吧");
					}
				}
			} else {
				showShortToast("服务器连接失败");
			}
		}

	}

	/**
	 * 适配器
	 * 
	 **/

	public class Adapter extends BaseAdapter {
		// private String address;
		//
		// /**
		// * @param address
		// */
		// public Adapter(String address) {
		// this.address = address;
		// }

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
					ImageView image = (ImageView) lvNearbyBarList.findViewWithTag(imageUrl);
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
