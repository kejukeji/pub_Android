/**
 * 
 */
package com.keju.maomao.activity.personalcenter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.PersonalCentreBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 我的邀约
 * 
 * @author zhuoyong
 * @data 创建时间：2013-12-16 下午5:38:25
 */
public class MyInviteActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private ListView lvInviteList;
	private Adapter adapter;
	private ArrayList<PersonalCentreBean> InviteList;

	private int pageIndex = 1;

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;


	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_invite_list);

		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText("邀约");

		lvInviteList = (ListView) this.findViewById(R.id.lvInviteList);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);

		InviteList = new ArrayList<PersonalCentreBean>();
		adapter = new Adapter();

		lvInviteList.addFooterView(vFooter); // 注意
												// 此句话必须要在setAdapter前面才可以显示上拉查看更多
		lvInviteList.setAdapter(adapter);
		lvInviteList.setOnScrollListener(LoadListener);
		lvInviteList.setDivider(null);
		lvInviteList.setFooterDividersEnabled(false);

		if (NetUtil.checkNet(MyInviteActivity.this)) {
			new GetInviteListTask().execute();
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
				if (NetUtil.checkNet(MyInviteActivity.this)) {
					if (!isLoad && !isComplete) {
						new GetInviteListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/***
	 * 
	 * 获取邀约的数据
	 */
	private class GetInviteListTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int userId = SharedPrefUtil.getUid(MyInviteActivity.this);
			try {
				return new BusinessHelper().getInviterList(userId, pageIndex);
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
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {

							if (result.has("invitation")) {
								JSONArray arr = result.getJSONArray("invitation");
								if (arr != null) {
									ArrayList<PersonalCentreBean> inviteBean = (ArrayList<PersonalCentreBean>) PersonalCentreBean.constractList(arr);
									boolean isLastPage = false;
									if (inviteBean.size() > 0) {
										InviteList.addAll(inviteBean);
										adapter.notifyDataSetChanged(); // 通知更新
										pageIndex++;
									} else {
										showShortToast("还没有人给你发邀约哦");
										isLastPage = true;
									}
									if (isLastPage) {
										pbFooter.setVisibility(View.GONE);
										tvFooterMore.setText(R.string.load_all);
										isComplete = true;
									} else {
										if (inviteBean.size() > 0 && inviteBean.size() <Constants.PAGE_SIZE) {
											pbFooter.setVisibility(View.GONE);
											tvFooterMore.setText(R.string.load_all);
											isComplete = true;
										} else {
											pbFooter.setVisibility(View.GONE);
											tvFooterMore.setText("上拉查看更多");
										}
									}
									if (pageIndex == 1 && inviteBean.size() == 0) {
										tvFooterMore.setText("");
									}
									adapter.notifyDataSetChanged();
									isLoad = false;
								}
							}
						} else {
							tvFooterMore.setText("");
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
	 * 适配器
	 * 
	 **/

	public class Adapter extends BaseAdapter {
		@Override
		public int getCount() {
			return InviteList.size();
		}

		@Override
		public Object getItem(int position) {
			return InviteList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			PersonalCentreBean bean = InviteList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.my_invite_item, null);
				holder.tvSendNickName = (TextView) convertView.findViewById(R.id.tvSendNickName);
				holder.tvIntegral = (TextView) convertView.findViewById(R.id.tvIntegral);
				holder.tvSendTiem = (TextView) convertView.findViewById(R.id.tvSendTiem);
				holder.ivSendPhoto = (ImageView) convertView.findViewById(R.id.ivSendPhoto);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url = bean.getSendPhotoUrl();
			holder.ivSendPhoto.setTag(url);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) lvInviteList.findViewWithTag(imageUrl);
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
				holder.ivSendPhoto.setImageDrawable(cacheDrawble);
			} else {
				holder.ivSendPhoto.setImageResource(R.drawable.ic_default);
			}
			holder.tvSendNickName.setText(bean.getSendName());
			holder.tvSendTiem.setText(bean.getSendTiem());
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tvSendNickName, tvIntegral, tvSendTiem;
		private ImageView ivSendPhoto;
	}

}
