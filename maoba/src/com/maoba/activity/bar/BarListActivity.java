/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.StringUtil;

/**
 * 酒吧列表
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-21 下午10:47:28
 */
public class BarListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private ImageButton ibRight;

	private ListView lvBarList;
	private Adapter adapter;
	private ArrayList<BarBean> list;

	private LinearLayout viewMenuList;

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

	private static final int REFRESH = 1;
	private static final int ADD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_list);

		findView();
		fillData();
		app = (CommonApplication) getApplication();
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		lvBarList = (ListView) this.findViewById(R.id.lvBarList);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

		// 今日推荐
		viewMenuList = (LinearLayout) this.findViewById(R.id.viewMenuList);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间
		btnRight.setOnClickListener(this);
		btnRight.setText("搜索");

		list = new ArrayList<BarBean>();
		adapter = new Adapter();
		lvBarList.addFooterView(vFooter);
		lvBarList.setAdapter(adapter);
		lvBarList.setDividerHeight(0);
		lvBarList.setOnScrollListener(LoadListener);
		lvBarList.setOnItemClickListener(itemListener);
		lvBarList.setDivider(null);
		lvBarList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(BarListActivity.this)) {
			new GetBarListTask().execute();
			// fillMenuList(list, REFRESH);
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
			if (arg2 >= list.size()) {
				return;
			}
			BarBean bean = list.get(arg2);
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
				return new BusinessHelper().getBarList(3, pageIndex);
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
				list.clear();
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				// 这里获取到十条数据
				List<BarBean> tempList = result.getObjList();
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					list.addAll(tempList);
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

	private int lastPosition = -1;
	private Vector<Boolean> menuChecked = new Vector<Boolean>();

	/**
	 * 填充今日推荐数据
	 * 
	 * @param list
	 * 
	 *            private void fillMenuList(final List<BarBean> list, int type)
	 *            { if (list == null) { return; } if (type == ADD) { for (int i
	 *            = 0; i < list.size(); i++) { menuChecked.add(false); } } else
	 *            { viewMenuList.removeAllViews(); } for (int i = 0; i <
	 *            list.size(); i++) { final BarBean bean = list.get(i); final
	 *            int position = i; LinearLayout.LayoutParams paramItem = new
	 *            LinearLayout
	 *            .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
	 *            LinearLayout.LayoutParams.WRAP_CONTENT); paramItem.rightMargin
	 *            = 10; final View view =
	 *            getLayoutInflater().inflate(R.layout.today_recommend_item,
	 *            null); view.setLayoutParams(paramItem); ImageView ivPhoto =
	 *            (ImageView) view.findViewById(R.id.ivPhoto);
	 * 
	 *            String picUrl = bean.getRecommendImageUrl(); if
	 *            (!TextUtils.isEmpty(picUrl)) { ivPhoto.setTag(picUrl);
	 *            Drawable cacheDrawable =
	 *            AsyncImageLoader.getInstance().loadDrawable(picUrl, new
	 *            ImageCallback() {
	 * @Override public void imageLoaded(Drawable imageDrawable, String
	 *           imageUrl) { ImageView image = (ImageView)
	 *           viewMenuList.findViewWithTag(imageUrl); if (image != null) { if
	 *           (imageDrawable != null) {
	 *           image.setImageDrawable(imageDrawable); } } } }); if
	 *           (cacheDrawable != null) {
	 *           ivPhoto.setImageDrawable(cacheDrawable); } } if
	 *           (menuChecked.get(i)) { } else { }
	 * 
	 *           view.setOnClickListener(new View.OnClickListener() {
	 * @Override public void onClick(View v) {
	 * 
	 *           if (lastPosition != -1) { // 取消上一次的选中状态
	 *           menuChecked.setElementAt(false, lastPosition); } // 直接取反即可
	 *           menuChecked.setElementAt(!menuChecked.elementAt(position),
	 *           position); lastPosition = position; // 记录本次选中的位置
	 *           fillMenuList(list, REFRESH); } });
	 *           view.setOnLongClickListener(new View.OnLongClickListener() {
	 * @Override public boolean onLongClick(View v) { return true; } });
	 *           viewMenuList.addView(view); } }
	 */
	/**
	 * 适配器
	 * 
	 **/

	public class Adapter extends BaseAdapter {

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
			ViewHolder holder = null;
			BarBean bean = list.get(position);
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
