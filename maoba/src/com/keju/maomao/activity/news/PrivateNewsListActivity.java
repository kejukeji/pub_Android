/**
 * 
 */
package com.keju.maomao.activity.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
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
import com.keju.maomao.bean.NewsBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;

/**
 * 私信列表
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-30 下午2:52:49
 */
public class PrivateNewsListActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLift;
	private Button btnRight;
	private TextView tvTitle;

	private int pageIndex = 1;// 页数

	private View vFooter;
	private ProgressBar pbFooter;
	private TextView tvFooterMore;

	private boolean isFilter = false;

	private boolean isLoad = false;// 是否正在加载数据
	private boolean isLoadMore = false;
	private boolean isComplete = false;// 是否加载完了；

	private ListView ivPrivateList;
	private List<NewsBean> newsListBean = new ArrayList<NewsBean>();
	private NewsListAdapter newsAdapter;

	private String friendUrl;// 好友的图片的Url

	private Boolean isCliclClear = false; // 是否点击了清除按钮 私信聊天界面的数据回调

	private Map<String, Integer> faceMap = new HashMap<String, Integer>();
	private int[] faceRes = new int[] { R.drawable.ic_face_001, R.drawable.ic_face_002, R.drawable.ic_face_003,
			R.drawable.ic_face_004, R.drawable.ic_face_005, R.drawable.ic_face_006, R.drawable.ic_face_007,
			R.drawable.ic_face_008, R.drawable.ic_face_009, R.drawable.ic_face_010, R.drawable.ic_face_011,
			R.drawable.ic_face_012, R.drawable.ic_face_013, R.drawable.ic_face_014, R.drawable.ic_face_015,
			R.drawable.ic_face_016 };

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piavate_news_list);
		for (int i = 0; i < faceRes.length; i++) {
			String j;
			int k = i + 1;
			if (k < 10) {
				j = "00" + k;
			} else if (k < 100) {
				j = "0" + k;
			} else {
				j = "" + k;
			}
			String key = "[edu" + j + "]";
			faceMap.put(key, faceRes[i]);
		}
		findView();
		fillData();
	}

	private void findView() {
		ibLift = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		btnRight = (Button) this.findViewById(R.id.btnRight);

		// 加载更多footer
		vFooter = getLayoutInflater().inflate(R.layout.footer, null);
		pbFooter = (ProgressBar) vFooter.findViewById(R.id.progressBar);
		tvFooterMore = (TextView) vFooter.findViewById(R.id.tvMore);

		ivPrivateList = (ListView) this.findViewById(R.id.ivPrivatelist);
	}

	private void fillData() {
		ibLift.setImageResource(R.drawable.ic_btn_left);
		ibLift.setOnClickListener(this);
		btnRight.setText("清空");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("我的私信");

		newsAdapter = new NewsListAdapter();
		ivPrivateList.addFooterView(vFooter);
		ivPrivateList.setAdapter(newsAdapter);
		ivPrivateList.setDividerHeight(0);
		ivPrivateList.setOnScrollListener(LoadListener);
		ivPrivateList.setOnItemClickListener(itemListener);

		if (NetUtil.checkNet(PrivateNewsListActivity.this)) {
			new PrivateNewsListTask().execute();
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
			if (newsListBean.size() > 0) {
				if (NetUtil.checkNet(PrivateNewsListActivity.this)) {
					isFilter = true;
					new ClearTask().execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {
				showShortToast("无数据无需清空哦");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.INDEX:
				isCliclClear = data.getBooleanExtra("iscliclclear", false);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * listview点击事件
	 */
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= newsListBean.size()) {
				return;
			}
			NewsBean bean = newsListBean.get(arg2);
			friendUrl = bean.getUserUrl();
			Intent intent = new Intent();
			intent.setClass(PrivateNewsListActivity.this, PrivateLetterActivity.class);
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, bean.getFriendId());
			b.putSerializable("NICK_NAME", bean.getNickName());
			b.putSerializable("FREIND_URL", friendUrl);
			intent.putExtras(b);
			startActivityForResult(intent, Constants.INDEX);
			// openActivity(PrivateLetterActivity.class, b);
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
				if (NetUtil.checkNet(PrivateNewsListActivity.this)) {
					if (!isLoad && !isComplete) {
						new PrivateNewsListTask().execute();
					}
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {

			}
		}
	};

	/**
	 * 获取私信会话
	 * 
	 */

	public class PrivateNewsListTask extends AsyncTask<Void, Void, ResponseBean<NewsBean>> {
		private int page;
		public PrivateNewsListTask() {
			
		}

		/**
		 * @param pageIndex
		 */
		public PrivateNewsListTask(int pageIndex) {
			this.page = pageIndex;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(PrivateNewsListActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected ResponseBean<NewsBean> doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(PrivateNewsListActivity.this);
			try {
				if(isCliclClear){
					return new BusinessHelper().getPrivateNews(uid, page);
				}else{
					return new BusinessHelper().getPrivateNews(uid, pageIndex);
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<NewsBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			pbFooter.setVisibility(View.GONE);

			if (result.getStatus() != Constants.REQUEST_FAILD) {
				// 这里获取到十条数据
				List<NewsBean> tempList = result.getObjList();
				boolean isLastPage = false;
				if (tempList.size() > 0) {
					if(isCliclClear){
						newsListBean.clear();
						newsListBean.addAll(tempList);
						newsAdapter.notifyDataSetChanged(); // 通知更新
						pageIndex++;
					}else{
						newsListBean.addAll(tempList);
						newsAdapter.notifyDataSetChanged(); 
						pageIndex++;
					}
				} else {
					newsListBean.clear();
					newsAdapter.notifyDataSetChanged(); 
					showShortToast("你没有私信会话列表,快找好友聊天去吧");
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
				tvFooterMore.setText("");
			}
			newsAdapter.notifyDataSetChanged();
			isLoad = false;
			isFilter = false;
		}

	}

	/**
	 * 私信会话适配器
	 * 
	 */
	private class NewsListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return newsListBean.size();
		}

		@Override
		public Object getItem(int position) {
			return newsListBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.private_news_item, null);
				holder.ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
				holder.tvNickName = (TextView) convertView.findViewById(R.id.tvNickName);
				holder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
				holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tvTime);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tvcontent);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			fillData(convertView, position, holder);
			return convertView;
		}

		private void fillData(View convertView, int position, ViewHolder viewHolder) {
			NewsBean bean = newsListBean.get(position);
			// friendUrl = bean.getUserUrl();
			setImageByUrl(viewHolder.ivUserPhoto, BusinessHelper.PIC_BASE_URL + bean.getUserUrl());

			if (position % 2 == 0) {
				// convertView.setBackgroundResource(R.drawable.bg_repeat);
			} else {
				// convertView.setBackgroundResource(R.drawable.repeat_blod_slant);
			}

			String contentStr = bean.getContent();
			SpannableString spannableString = null;
			if (!StringUtil.isBlank(contentStr)) {
				boolean isHaveFacePic = contentStr.contains("[edu");
				if (isHaveFacePic == true) {
					if (faceMap != null) {
						spannableString = ImageUtil.changeTextToEmotions(faceMap, contentStr,
								PrivateNewsListActivity.this);
					}
				}
			}

			if (spannableString != null) {
				viewHolder.tvContent.setText(spannableString);
			} else {
				viewHolder.tvContent.setText(contentStr);
			}

			// String sendTime =
			// DateUtil.getConversationTime(bean.getSendTime());
			viewHolder.tvCreateTime.setText(bean.getSendTime());
			viewHolder.tvNickName.setText(bean.getNickName());
			viewHolder.tvAge.setText(bean.getAge() + "岁");
			viewHolder.tvContent.setText(bean.getContent());
		}

		private class ViewHolder {
			ImageView ivUserPhoto;
			TextView tvNickName, tvCreateTime, tvAge, tvContent;
		}

		private void setImageByUrl(ImageView imageView, String url) {
			if (null == url) {
				imageView.setImageResource(R.drawable.ic_default);
				return;
			}
			imageView.setTag(url);
			Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView ivPhoto = (ImageView) ivPrivateList.findViewWithTag(imageUrl);
					if (ivPhoto != null) {
						if (imageDrawable != null) {
							ivPhoto.setImageDrawable(imageDrawable);
							NewsListAdapter.this.notifyDataSetChanged();
						} else {
							ivPhoto.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawable != null) {
				imageView.setImageDrawable(cacheDrawable);
			} else {
				imageView.setImageResource(R.drawable.ic_default);
			}
		}
	}

	/**
	 * 清除聊天列表信息
	 * 
	 */
	private class ClearTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在删除...");
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(PrivateNewsListActivity.this);
			try {
				return new BusinessHelper().getClear(uid);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {
							showShortToast("清除成功");
							if (isFilter) {
								newsListBean.clear();
							}
							newsAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						showShortToast("服务器连接失败");
					}
				} else {
					showShortToast("Json解析错误");
				}

			} else {
				showShortToast("没有信息可删除哦");
			}

		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (isCliclClear) {
			if (NetUtil.checkNet(PrivateNewsListActivity.this)) {
				pageIndex = 1;
				new PrivateNewsListTask(pageIndex).execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
		}
	}

}
