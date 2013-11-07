/**
 * 
 */
package com.maoba.activity.personalcenter;


import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.activity.my.CollectionOfBarListActivity;
import com.maoba.activity.news.PrivateLetterActivity;
import com.maoba.bean.BarBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;

/**
 * 他人的个人中心
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-3 下午10:16:13
 */
public class FriendPersonalCenter extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvSignature;// 个性签名
	private TextView tvAge, tvAddress, tvNickName, tvCollectNum;
	private TextView tvSendPrivateNews; //发私信
	
	private ImageView ivUserPhoto;
	private LinearLayout viewMyCollect;
	
	private BarBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_personal_center);

		bean = (BarBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvSignature = (TextView) this.findViewById(R.id.tvSignature);

		tvAge = (TextView) this.findViewById(R.id.tvAge);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvNickName = (TextView) this.findViewById(R.id.tvNickName);
		tvCollectNum = (TextView) this.findViewById(R.id.tvCollectNum);
		ivUserPhoto = (ImageView) this.findViewById(R.id.ivUserPhoto);
		tvSendPrivateNews = (TextView)this.findViewById(R.id.tvSendPrivateNews);

		viewMyCollect = (LinearLayout) this.findViewById(R.id.viewMyCollect);

		if (NetUtil.checkNet(FriendPersonalCenter.this)) {
			new GetUserInfor().execute();
			new GetCollectNum().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		
		tvSendPrivateNews.setOnClickListener(this);

		viewMyCollect.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.viewMyCollect:
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_DATA, bean.getUserId());
			openActivity(CollectionOfBarListActivity.class, b);
			break;
		case R.id.tvSendPrivateNews:
			Bundle b1 = new Bundle();
			b1.putInt(Constants.EXTRA_DATA, bean.getUserId());
			openActivity(PrivateLetterActivity.class,b1);
		default:
			break;
		}

	}

	/**
	 * 
	 * 获取用户个人资料信息
	 * 
	 * */
	private class GetUserInfor extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在加载...");
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getUserInfor(bean.getUserId());
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
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						JSONObject userJson = result.getJSONObject("user_info");
						String signaTure = userJson.getString("signature");
						String birthday = userJson.getString("birthday");
						String NickName = userJson.getString("upload_name");
						String address = userJson.getString("county_id");
						if (signaTure.equals("null")) {
							tvSignature.setText("未设置");
						} else {
							tvSignature.setText(signaTure);
						}
						if (NickName.equals("null")) {
							tvNickName.setText("未设置");
						} else {
							tvNickName.setText(NickName);
						}
						if (birthday == null) {
							tvAge.setText("未设置");
						} else {
							// Calendar calendar = Calendar.getInstance();
							// int year = calendar.get(Calendar.YEAR);
							// SimpleDateFormat format = new
							// SimpleDateFormat("yyyy");
							// String year1 = format.format(birthday);
							// int age = year - Integer.parseInt(year1);
							tvAge.setText("");
						}
						if (address.equals("null")) {
							tvAddress.setText("未设置");
						} else {
							tvAddress.setText(address + "");
						}
						String photoUrl = BusinessHelper.BASE_URL +userJson.getString("rel_path")+ userJson.getString("pic_name");
						ivUserPhoto.setTag(photoUrl);
						Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(photoUrl,
								new ImageCallback() {
									@Override
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										ImageView image = (ImageView) ivUserPhoto.findViewWithTag(imageUrl);
										if (image != null) {
											if (imageDrawable != null) {
												image.setImageDrawable(imageDrawable);
											} else {
												image.setImageResource(R.drawable.bg_show11);
											}
										}
									}
								});
						if (cacheDrawble != null) {
							ivUserPhoto.setImageDrawable(cacheDrawble);
						} else {
							ivUserPhoto.setImageResource(R.drawable.bg_show11);
						}

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
	 * 
	 * 获取他人的收藏条数
	 * 
	 * */
	private class GetCollectNum extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getContentNum(bean.getUserId(), 1);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				int status;
				try {
					status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
                           String collectNum =result.getString("count");
                           tvCollectNum.setText("("+collectNum+")");
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
