package com.keju.maomao.activity.my;

import java.util.ArrayList;
import java.util.List;

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
import com.keju.maomao.activity.bar.BarDetailActivity;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.BarBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;

/**
 * 我的收藏的酒吧例表
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-27 下午10:16:13
 */
public class CollectionOfBarListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private ListView lvCollBarList;
	private Adapter adapter;
	private ArrayList<BarBean> list;
	private CommonApplication app;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private int pageIndex = 1;
	private ProgressDialog pd;

	private int userId;

	private boolean isLoadMore = false;
	private boolean isLoad = false;// 是否正在加载数据
	private boolean isComplete = false;// 是否加载完了；
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_bar_list);
		
		userId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		findView();
		fillData();
		app = (CommonApplication) getApplication();
		app.addActivity(this);
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		lvCollBarList = (ListView) findViewById(R.id.lvCollBarList);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		tvTitle.setText("酒吧收藏");

		list = new ArrayList<BarBean>();
		adapter = new Adapter();
		lvCollBarList.addFooterView(vFooter);
		lvCollBarList.setAdapter(adapter);
		lvCollBarList.setOnScrollListener(LoadListener);
		lvCollBarList.setOnItemClickListener(itemListener);
		lvCollBarList.setDivider(null);
		lvCollBarList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(this)) {
			new GetCollBarListTask().execute();
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
			// Bundle类用来携带数据
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
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 滚动到最后，默认加载下一页
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isLoadMore) {
				if (NetUtil.checkNet(CollectionOfBarListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetCollBarListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount) {
				isLoadMore = true;
			} else {
				isLoadMore = false;
			}
		}
	};

	private class GetCollBarListTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isLoadMore) {
				isLoad = true;
				pbFooter.setVisibility(View.VISIBLE);
				tvFooterMore.setText(R.string.loading);
			} else {
				if (pd == null) {
					pd = new ProgressDialog(CollectionOfBarListActivity.this);
				}
				pd.setMessage(getString(R.string.loading));
				pd.show();
			}

		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(CollectionOfBarListActivity.this);
			try {
				if (uid == userId) {
					return new BusinessHelper().getcollectBar(uid, pageIndex);
				} else {
					return new BusinessHelper().getcollectBar(userId, pageIndex);
				}
			} catch (SystemException e) {
			}

			return null;
		}

		protected void onPostExecute(ResponseBean<BarBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			pbFooter.setVisibility(View.GONE);
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				List<BarBean> tempList = result.getObjList();
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					list.addAll(tempList);
					// 通知ListView刷新界面
					adapter.notifyDataSetChanged();
					pageIndex++;
				} else {
					showShortToast("还没有收藏哦！");
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
			}
			isLoad = false;
		}
	}

	/**
	 * 收藏列表适配器
	 * 
	 * */
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
				convertView = getLayoutInflater().inflate(R.layout.collection_bar_item, null);
				holder.tvCollBarName = (TextView) convertView.findViewById(R.id.tvCollBarName);
				holder.tvCollDistanceLabel = (TextView) convertView.findViewById(R.id.tvCollDistanceLabel);
				holder.tvCollTime = (TextView) convertView.findViewById(R.id.tvCollTime);
				holder.ivCollImage = (ImageView) convertView.findViewById(R.id.ivCollImage);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvCollBarName.setText(bean.getBar_Name());
			holder.tvCollTime.setText(bean.getCollectTime());

			String url = bean.getImageUrl();
			holder.ivCollImage.setTag(url);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) lvCollBarList.findViewWithTag(imageUrl);
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
				holder.ivCollImage.setImageDrawable(cacheDrawble);
			} else {
				holder.ivCollImage.setImageResource(R.drawable.ic_default);
			}

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
					holder.tvCollDistanceLabel.setText(String.format("%.1f", distance) + "km");
				} else {
					holder.tvCollDistanceLabel.setText(String.format("%.0f", distance) + "m");
				}
			} else {
				holder.tvCollDistanceLabel.setText("");
			}
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tvCollBarName, tvCollDistanceLabel, tvCollTime;
		private ImageView ivCollImage;
	}

}
