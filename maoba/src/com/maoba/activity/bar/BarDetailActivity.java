/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.maoba.util.SharedPrefUtil;
import com.maoba.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 酒吧详情
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-22 下午4:05:12
 */
public class BarDetailActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private ImageButton ibRight;
	private TextView tvRight;
	private TextView tvTitle;

	private TextView tvName, tvDistanceLabel, tvAddress, tvBarType, tvIntro, tvHot;
	private ImageView ivImage;

	private LinearLayout viewShowList;

	private ProgressDialog pd;
	private CommonApplication app;

	private BarBean bean;
	private List<BarBean> barDetailList = new ArrayList<BarBean>();// 酒吧详情
	private List<BarBean> showList = new ArrayList<BarBean>();// 签到

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_details);
		MobclickAgent.onError(this);
		bean = (BarBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		MobclickAgent.onEvent(this, "bar_details");

		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvRight = (TextView) this.findViewById(R.id.tvRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		tvName = (TextView) this.findViewById(R.id.tvName);
		tvDistanceLabel = (TextView) this.findViewById(R.id.tvDistanceLabel);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvBarType = (TextView) this.findViewById(R.id.tvBarType);
		tvIntro = (TextView) this.findViewById(R.id.tvIntro);
		tvHot = (TextView) this.findViewById(R.id.tvHot);
		ivImage = (ImageView) this.findViewById(R.id.ivImage);

		viewShowList = (LinearLayout) this.findViewById(R.id.viewShowList);// 签到

	}

	private void fillData() {

		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		tvRight.setText("收藏");
		tvRight.setOnClickListener(this);
		ivImage.setOnClickListener(this);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间

		tvName.setText(bean.getBar_Name());

		if (NetUtil.checkNet(BarDetailActivity.this)) {
			new GetBarDetailTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

		String imageUrl = bean.getImageUrl();
		ivImage.setTag(imageUrl);
		Drawable cacheDrawable = AsyncImageLoader.getInstance().loadDrawable(imageUrl, new ImageCallback() {

			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView image = (ImageView) ivImage.findViewWithTag(imageUrl);
				if (image != null) {
					if (imageDrawable != null) {
						ivImage.setImageDrawable(imageDrawable);
					} else {
						ivImage.setImageResource(R.drawable.ic_default);
					}
				}
			}
		});
		if (cacheDrawable != null) {
			ivImage.setImageDrawable(cacheDrawable);
		} else {
			ivImage.setImageResource(R.drawable.ic_default);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.tvRight:

		default:
			break;
		}
	}

	/**
	 * 
	 * 获取酒吧详情
	 * 
	 * */

	public class GetBarDetailTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(BarDetailActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			Integer uid = SharedPrefUtil.getUid(BarDetailActivity.this);
			if (uid == 0) {
				try {
					return new BusinessHelper().getBarDetail(bean.getBar_id());
				} catch (SystemException e) {
				}
			} else {
				try {
					return new BusinessHelper().getBarDetail(bean.getBar_id(), uid);
				} catch (SystemException e) {
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<BarBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				if (result.getStatus() != Constants.REQUEST_FAILD) {
					barDetailList.addAll(result.getObjList1());

					if (barDetailList != null && barDetailList.size() > 0) {
						BarBean barDetail = barDetailList.get(0);
						tvTitle.setText(barDetail.getBar_Name());
						tvIntro.setText(barDetail.getBar_Intro());
						tvHot.setText(barDetail.getHot());
						tvBarType.setText(barDetail.getBarType());
						tvAddress.setText(barDetail.getBar_Address());
						double latitude;
						try {
							latitude = Double.parseDouble(barDetail.getLatitude());
						} catch (NumberFormatException e) {
							latitude = 0;
						}
						// 距离
						double longitude;
						try {
							longitude = Double.parseDouble(barDetail.getLongitude());
						} catch (NumberFormatException e) {
							longitude = 0;
						}
						try {
							if (app.getLastLocation() != null) {
								double distance = StringUtil.getDistance(app.getLastLocation().getLatitude(), app
										.getLastLocation().getLongitude(), latitude, longitude);
								if (distance > 1000) {
									distance = distance / 1000;
									tvDistanceLabel.setText(String.format("%.1f", distance) + "km");
								} else {
									tvDistanceLabel.setText(String.format("%.0f", distance) + "m");
								}
							} else {
								tvDistanceLabel.setText("");
							}
						} catch (Exception e) {
						}
					}
					showList.addAll(result.getObjList());

					fillShowList(result.getObjList());

				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}
	}

	/**
	 * 填充签到数据
	 * 
	 * @param list
	 * 
	 */
	private void fillShowList(final List<BarBean> showlist) {
		if (showlist == null) {
			return;
		}
		for (int i = 0; i < showlist.size(); i++) {
			final BarBean showBean = showlist.get(i);
			LinearLayout.LayoutParams paramItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			paramItem.rightMargin = 10;
			final View view = getLayoutInflater().inflate(R.layout.show_item, null);
			view.setLayoutParams(paramItem);
			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

			String picUrl = showBean.getShowPhotoUrl();
			ivPhoto.setTag(picUrl);
			if (!TextUtils.isEmpty(picUrl)) {
				Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(picUrl, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						ImageView image = (ImageView) viewShowList.findViewWithTag(imageUrl);
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
			viewShowList.addView(view);
		}

	}

}
