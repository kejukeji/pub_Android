/**
 * 
 */
package com.keju.maomao.activity.friendpersonalcenter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.FriendPersonalCentreBean;
import com.keju.maomao.bean.ResponseBean;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 发送礼物界面
 * 
 * @author zhuoyong
 * @data 创建时间：2013-12-16 下午5:01:13
 */
public class SendGiftActivity extends BaseActivity implements OnClickListener {

	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private ArrayList<FriendPersonalCentreBean> giftBean = new ArrayList<FriendPersonalCentreBean>();
	private GridView gvGife;

	private GiftAdapter adapter;

	Display display;
	private int receiverId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_gift_list);
		receiverId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		display = this.getWindowManager().getDefaultDisplay();
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		gvGife = (GridView) findViewById(R.id.gvGife);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setOnClickListener(this);

		adapter = new GiftAdapter();
		gvGife.setAdapter(adapter);

		tvTitle.setText("送个小礼");
		if (NetUtil.checkNet(SendGiftActivity.this)) {
			new GetGiftTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;

		default:
			break;
		}

	}

	public class GetGiftTask extends AsyncTask<Void, Void, ResponseBean<FriendPersonalCentreBean>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected ResponseBean<FriendPersonalCentreBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getGiftList();
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<FriendPersonalCentreBean> result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				if (result.getStatus() != Constants.REQUEST_FAILD) {
					List<FriendPersonalCentreBean> tempList = result.getObjList();
					giftBean.addAll(tempList);
					if (tempList.size() > 0) {
						adapter.notifyDataSetChanged();
					}

				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

	/**
	 * 礼物图片显示适配
	 * 
	 * @author zhouyong
	 * 
	 */
	private class GiftAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return giftBean.size();
		}

		@Override
		public Object getItem(int position) {
			return giftBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendPersonalCentreBean bean = giftBean.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.send_gift_item, null);
				holder.ivGiftPhoto = (ImageView) convertView.findViewById(R.id.ivGiftPhoto);
				holder.tvGiftName = (TextView) convertView.findViewById(R.id.tvGiftName);
				holder.tvIntegral = (TextView) convertView.findViewById(R.id.tvIntegral);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// final ViewHolder holderUse = holder;
			int itemWidth = (display.getWidth() - 4 * 10) / 4;
			android.view.ViewGroup.LayoutParams param = holder.ivGiftPhoto.getLayoutParams();
			param.width = itemWidth;
			param.height = itemWidth;

			holder.tvGiftName.setText(bean.getGiftName());
			holder.tvIntegral.setText(bean.getIntegral());
			holder.ivGiftPhoto.setLayoutParams(param);
			String giftUrl = bean.getGiftphotoUrl();
			holder.ivGiftPhoto.setTag(giftUrl);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(giftUrl, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) gvGife.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
							GiftAdapter.this.notifyDataSetChanged();
						} else {
							image.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawble != null) {
				holder.ivGiftPhoto.setImageDrawable(cacheDrawble);
			} else {
				holder.ivGiftPhoto.setImageResource(R.drawable.ic_default);
			}

			holder.ivGiftPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (NetUtil.checkNet(SendGiftActivity.this)) {
						new SendGiftTask(bean.getGiftId()).execute();
					} else {
						showShortToast(R.string.NoSignalException);
					}
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		private ImageView ivGiftPhoto;
		private TextView tvGiftName, tvIntegral;
	}

	/***
	 * 
	 * 发送礼物接口
	 * 
	 */

	private class SendGiftTask extends AsyncTask<Void, Void, JSONObject> {
		private int giftId;

		/**
		 * @param giftId
		 */
		public SendGiftTask(int giftId) {
			this.giftId = giftId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int senderId = SharedPrefUtil.getUid(SendGiftActivity.this);
			try {
				return new BusinessHelper().sendGift(senderId, receiverId, giftId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					if (result.getInt("status") == Constants.REQUEST_SUCCESS) {
						showShortToast("发送成功");
					} else {
						showShortToast("发送失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

}
