package com.keju.maomao.activity.my;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.activity.event.EventDetailActivity;
import com.keju.maomao.bean.EventBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 我的收藏的活动例表
 * 
 * @author zhouyong
 * @data 创建时间：2013-12-8 下午10:16:13
 */
public class CollectionOfEventListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;
	private ListView lvCollEventList;
	private Adapter adapter;
	private ArrayList<EventBean> list;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private int pageIndex = 1;

	private boolean isEdit = false;// 是不是点击编辑

	// private int userId;

	private boolean isLoadMore = false;
	private boolean isLoad = false;// 是否正在加载数据
	private boolean isComplete = false;// 是否加载完了；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_event_list);

		// userId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		lvCollEventList = (ListView) findViewById(R.id.lvCollEventList);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		btnRight.setText("编辑");
		tvTitle.setText("活动收藏");

		list = new ArrayList<EventBean>();
		adapter = new Adapter();
		lvCollEventList.addFooterView(vFooter);
		lvCollEventList.setAdapter(adapter);
		lvCollEventList.setOnScrollListener(LoadListener);
		lvCollEventList.setOnItemClickListener(itemListener);
		lvCollEventList.setDivider(null);
		lvCollEventList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(this)) {
			new GetCollEventListTask().execute();
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
			isEdit = !isEdit;
			if (isEdit) {
				btnRight.setText("完成");
			} else {
				btnRight.setText("编辑");
			}
			adapter.notifyDataSetChanged();
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

			EventBean bean = list.get(arg2);
			// Bundle类用来携带数据
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_DATA, bean.getEventId());
			b.putString("BARNAME", bean.getBarName());
			b.putBoolean("ISCOLLECT", bean.getIsCollect());
			openActivity(EventDetailActivity.class, b);
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
				if (NetUtil.checkNet(CollectionOfEventListActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetCollEventListTask().execute();
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
	/***
	 * 活动收藏
	 * 
	 */
	private class GetCollEventListTask extends AsyncTask<Void, Void, ResponseBean<EventBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isLoadMore) {
				isLoad = true;
				pbFooter.setVisibility(View.VISIBLE);
				tvFooterMore.setText(R.string.loading);
			} else {
				showPd(R.string.loading);
			}

		}

		@Override
		protected ResponseBean<EventBean> doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(CollectionOfEventListActivity.this);
			try {
				return new BusinessHelper().getcollectEvent(uid, pageIndex);
			} catch (SystemException e) {
			}

			return null;
		}

		protected void onPostExecute(ResponseBean<EventBean> result) {
			super.onPostExecute(result);
			dismissPd();
			pbFooter.setVisibility(View.GONE);
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				List<EventBean> tempList = result.getObjList();
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
	 * 删除活動收藏
	 * 
	 * @author Zhouyong
	 * 
	 */
	private class DelTask extends AsyncTask<Void, Void, JSONObject> {
		private int position;

		/**
		 * @param position
		 */
		public DelTask(int position) {
			super();
			this.position = position;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(CollectionOfEventListActivity.this);
			try {
				return new BusinessHelper().collectEvent(list.get(position).getEventId(),uid);
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
						showShortToast("删除成功");
						list.remove(position);
						adapter.notifyDataSetChanged();
					} else {
						showShortToast(result.getString("message"));
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
			final int clickPosition = position;
			ViewHolder holder = null;
			EventBean bean = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.collection_event_item, null);
				holder.tvCollEventName = (TextView) convertView.findViewById(R.id.tvCollEventName);
				holder.tvCollTime = (TextView) convertView.findViewById(R.id.tvCollTime);
				holder.tvEventTime = (TextView)convertView.findViewById(R.id.tvEventTime);
				holder.ivCollImage = (ImageView) convertView.findViewById(R.id.ivCollImage);
				holder.ivDel = (ImageView) convertView.findViewById(R.id.ivDel);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvCollEventName.setText(bean.getEventTitle());
			holder.tvCollTime.setText(bean.getCollectTime());
            holder.tvEventTime.setText(bean.getStartTime());
			String url = bean.getPhotoUrl();
			holder.ivCollImage.setTag(url);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) lvCollEventList.findViewWithTag(imageUrl);
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
			if (isEdit) {
				holder.ivDel.setVisibility(View.VISIBLE);
			} else {
				holder.ivDel.setVisibility(View.GONE);
			}

			holder.ivDel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder ab = new AlertDialog.Builder(CollectionOfEventListActivity.this);
					ab.setTitle("提示");
					ab.setMessage("确定删除该收藏吗？");
					ab.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (NetUtil.checkNet(CollectionOfEventListActivity.this)) {
								new DelTask(clickPosition).execute();
							} else {
								showShortToast(R.string.NoSignalException);
							}
						}
					});
					ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					AlertDialog alert = ab.create();
					alert.show();
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		private TextView tvCollEventName, tvCollTime,tvEventTime;
		private ImageView ivCollImage, ivDel;
	}

}
