/**
 * 
 */
package com.maoba.activity.event;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.maoba.AsyncImageLoader;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.SystemException;
import com.maoba.activity.bar.BarDetailActivity;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.EventBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;

/**
 * 酒吧活动列表
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-31 上午10:46:15
 */
public class EventListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private ListView lvEventList;
	private EventAdapter adapter;
	private ArrayList<EventBean> eventList;
	private List<EventBean> hotList = new ArrayList<EventBean>();// 热门活动

	private LinearLayout viewEventList;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		lvEventList = (ListView) this.findViewById(R.id.lvEventList);

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

		// 今日推荐
		viewEventList = (LinearLayout) this.findViewById(R.id.viewEventList);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);

		eventList = new ArrayList<EventBean>();
		adapter = new EventAdapter();
		lvEventList.addFooterView(vFooter);
		lvEventList.setAdapter(adapter);
		lvEventList.setDividerHeight(0);
		lvEventList.setOnScrollListener(LoadListener);
		lvEventList.setOnItemClickListener(itemListener);
		lvEventList.setDivider(null);
		lvEventList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(EventListActivity.this)) {
			new GetEventListTask().execute();

		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * listview点击事件
	 */
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= eventList.size()) {
				return;
			}
			EventBean bean = eventList.get(arg2);
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
				if (NetUtil.checkNet(EventListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetEventListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/**
	 * 获取活动列表
	 * 
	 **/

	private class GetEventListTask extends AsyncTask<Void, Void, ResponseBean<EventBean>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在加载...");
		}

		@Override
		protected ResponseBean<EventBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getEventList(pageIndex);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<EventBean> result) {
			super.onPostExecute(result);
			dismissPd();
			pbFooter.setVisibility(View.GONE);
			if (isFilter) {
				eventList.clear();
			}
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				// 这里获取到十条数据
				List<EventBean> tempList = result.getObjList();
				if (pageIndex == 1) {
					hotList.addAll(result.getObjList1());
					fillTodayRecommend(result.getObjList1());
				}
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					eventList.addAll(tempList);
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
	 * 填充今日推荐数据
	 * 
	 * @param list
	 * 
	 */
	private void fillTodayRecommend(final List<EventBean> hotlist) {
		if (hotlist == null) {
			return;
		}
		for (int i = 0; i < hotlist.size(); i++) {
			final EventBean bean = hotlist.get(i);
			LinearLayout.LayoutParams paramItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			paramItem.rightMargin = 3;// 图片居上5dp
			// paramItem.topMargin =2;
			final View view = getLayoutInflater().inflate(R.layout.today_commened_item, null);
			view.setLayoutParams(paramItem);
			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

			String picUrl = bean.getRecommendPhotoUrl();
			ivPhoto.setTag(picUrl);
			if (!TextUtils.isEmpty(picUrl)) {
				Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(picUrl, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						ImageView image = (ImageView) viewEventList.findViewWithTag(imageUrl);
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
			viewEventList.addView(view);
		}
	}

	/**
	 * 适配器
	 * 
	 **/

	public class EventAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return eventList.size();
		}

		@Override
		public Object getItem(int position) {
			return eventList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			EventBean bean = eventList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.event_item, null);
				holder.tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
				holder.tvDistanceLabel = (TextView) convertView.findViewById(R.id.tvDistanceLabel);
				holder.tvStartTime = (TextView) convertView.findViewById(R.id.tvStartTime);
				holder.tvEndTime = (TextView) convertView.findViewById(R.id.tvEndTime);
				holder.tvJionNum = (TextView) convertView.findViewById(R.id.tvJionNum);
				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url = bean.getPhotoUrl();
			holder.ivImage.setTag(url);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) lvEventList.findViewWithTag(imageUrl);
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
			holder.tvEventTitle.setText(bean.getEventTitle());
			holder.tvDistanceLabel.setText(bean.getEventAddress());
			holder.tvStartTime.setText(bean.getStartTime());
			holder.tvEndTime.setText(bean.getEndTime());
			holder.tvJionNum.setText(bean.getJoinNumber());

			return convertView;
		}

	}

	class ViewHolder {
		private TextView tvEventTitle, tvDistanceLabel, tvStartTime, tvEndTime, tvJionNum;
		private ImageView ivImage;
	}

}
