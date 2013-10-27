/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.maoba.AsyncImageLoader;
import com.maoba.CommonApplication;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-25 下午9:22:21
 */
public class SearchListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvRight;
	private ImageButton ibRight;

	private ListView lvSearchBarList;
	private Adapter adapter;
	private ArrayList<BarBean> searchBarList;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	private ProgressDialog pd;
	private CommonApplication app;

	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list);
		MobclickAgent.onError(this);
		Intent intent = this.getIntent();
		content = intent.getExtras().getString(Constants.EXTRA_DATA);
		MobclickAgent.onEvent(this, "search_list");

		findView();
		fillData();
		app = (CommonApplication) getApplication();
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvRight = (TextView) this.findViewById(R.id.tvRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		lvSearchBarList = (ListView) this.findViewById(R.id.lvSearchBarList);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间
		// tvRight.setOnClickListener(this);
		// tvRight.setText("搜索");

		searchBarList = new ArrayList<BarBean>();
		adapter = new Adapter();
		lvSearchBarList.addFooterView(vFooter);
		lvSearchBarList.setAdapter(adapter);
		lvSearchBarList.setDividerHeight(0);
		lvSearchBarList.setOnScrollListener(LoadListener);
		lvSearchBarList.setOnItemClickListener(itemListener);
		lvSearchBarList.setDivider(null);
		lvSearchBarList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(SearchListActivity.this)) {
			new GetSearchBarListTask().execute();

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
			if (arg2 >= searchBarList.size()) {
				return;
			}
			BarBean bean = searchBarList.get(arg2);
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
				if (NetUtil.checkNet(SearchListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetSearchBarListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/**
	 * 获取搜索酒吧列表
	 * 
	 */
	public class GetSearchBarListTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isLoadMore) {
				isLoad = true;
				pbFooter.setVisibility(View.VISIBLE);
				tvFooterMore.setText(R.string.loading);
			} else {
				if (pd == null) {
					pd = new ProgressDialog(SearchListActivity.this);
				}
				pd.setMessage(getString(R.string.loading));
				pd.show();
			}
		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... arg0) {
			try {
				return new BusinessHelper().getSearchBarList(content, pageIndex);
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
				searchBarList.clear();
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				// 这里获取到十条数据
				List<BarBean> tempList = result.getObjList();
				if (tempList.size() <= 0) {
                   showShortToast("没有你要查询的酒吧,请重新查询");
                  return;
				}
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					searchBarList.addAll(tempList);
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
	 * 适配器
	 * 
	 **/

	public class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return searchBarList.size();
		}

		@Override
		public Object getItem(int position) {
			return searchBarList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			BarBean bean = searchBarList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.bar_item, null);
				holder.tvBarName = (TextView) convertView.findViewById(R.id.tvBarName);
				holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
				holder.tvDistanceLabel = (TextView) convertView.findViewById(R.id.tvDistanceLabel);
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
					ImageView image = (ImageView) lvSearchBarList.findViewWithTag(imageUrl);
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
		private TextView tvBarName, tvDistanceLabel, tvAddress;
		private ImageView ivImage;
	}

}
