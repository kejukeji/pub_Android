/**
 * 
 */
package com.maoba.activity.bar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.CommonApplication;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.maoba.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 酒吧详情
 * 
 * @author zhuoyong
 * @data 创建时间：2013-10-22 下午4:05:12
 */
public class BarDetailActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private ImageButton ibRight;
	private Button btnRight;
	private TextView tvTitle;

	private TextView tvName, tvDistanceLabel, tvAddress, tvBarType, tvIntro, tvHot;
	private ImageView ivImage;

	private ProgressDialog pd;
	private CommonApplication app;

	private BarBean bean;

	private String type_name;
	private String intro;
	private String view_number;

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
		btnRight = (Button) this.findViewById(R.id.btnRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		tvName = (TextView) this.findViewById(R.id.tvName);
		tvDistanceLabel = (TextView) this.findViewById(R.id.tvDistanceLabel);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvBarType = (TextView) this.findViewById(R.id.tvBarType);
		tvIntro = (TextView) this.findViewById(R.id.tvIntro);
		tvHot = (TextView) this.findViewById(R.id.tvHot);
		ivImage = (ImageView) this.findViewById(R.id.ivImage);

	}

	private void fillData() {
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setText("收藏");
		btnRight.setOnClickListener(this);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间

		tvTitle.setText(bean.getBar_Name());

		tvName.setText(bean.getBar_Name());
		tvAddress.setText(bean.getBar_Address());

		ivImage.setOnClickListener(this);
		double latitude;
		try {
			latitude = Double.parseDouble(bean.getLatitude());
		} catch (NumberFormatException e) {
			latitude = 0;
		}
		// 距离
		double longitude;
		try {
			longitude = Double.parseDouble(bean.getLongitude());
		} catch (NumberFormatException e) {
			longitude = 0;
		}
		try {
			if (app.getLastLocation() != null) {
				double distance = StringUtil.getDistance(app.getLastLocation().getLatitude(), app.getLastLocation()
						.getLongitude(), latitude, longitude);
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

		if (NetUtil.checkNet(this)) {
			new GetBarDetailTask().execute();
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
	 * 
	 * 获取酒吧详情
	 * 
	 * */

	public class GetBarDetailTask extends AsyncTask<Void, Void, JSONObject> {

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
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(BarDetailActivity.this);
			try {
				return new BusinessHelper().getBarDetail(bean.getBar_id(), uid);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						JSONArray PubList = result.getJSONArray("pub_list");
						JSONObject PubListJson = PubList.getJSONObject(0);
						view_number = PubListJson.getString("view_number");
						tvHot.setText(view_number);

						intro = PubListJson.getString("intro");
						tvIntro.setText(intro);

						type_name = PubListJson.getString("type_name");
						tvBarType.setText(type_name);
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

}
