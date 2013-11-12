/**
 * 
 */
package com.maoba.activity.personalcenter;

import java.util.Calendar;

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
	private TextView tvSendPrivateNews; // 发私信

	private ImageView ivUserPhoto;
	private LinearLayout viewMyCollect;

	private int userId;
	private String NickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_personal_center);

		userId =(int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
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
		tvSendPrivateNews = (TextView) this.findViewById(R.id.tvSendPrivateNews);

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
			b.putInt(Constants.EXTRA_DATA, userId);
			openActivity(CollectionOfBarListActivity.class, b);
			break;
		case R.id.tvSendPrivateNews:
			Bundle b1 = new Bundle();
			b1.putInt(Constants.EXTRA_DATA, userId);
			b1.putString("NICK_NAME", NickName);
			openActivity(PrivateLetterActivity.class, b1);
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
				return new BusinessHelper().getUserInfor(userId);
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
						JSONObject user = result.getJSONObject("user");
						String signaTure = userJson.getString("signature");
						String birthday = userJson.getString("birthday");
						NickName = user.getString("nick_name");
						String address = userJson.getString("county");
						if (signaTure.equals("null")) {
							tvSignature.setText("主人很懒还未设置哦");
						} else {
							tvSignature.setText(signaTure);
						}
						if (NickName.equals("null")) {
							tvNickName.setText("未设置");
						} else {
							tvNickName.setText(NickName);
						}
						if (birthday.equals("null")) {
							tvAge.setText("未设置");
						} else {
							Calendar calendar = Calendar.getInstance();
							int currentYear = calendar.get(Calendar.YEAR);// 当前年份
							String[] Num = birthday.split("-");// 去掉“——”
							int birthYear = Integer.valueOf(Num[0]);// 取出年份值
							int age = currentYear - birthYear;// 算出年龄
							String ageString = String.valueOf(age);// 转换
							tvAge.setText(ageString);

						}
						if (address.equals("null")) {
							tvAddress.setText("未设置");
						} else {
							tvAddress.setText(address + "");
						}
						String photoUrl = BusinessHelper.PIC_BASE_URL + userJson.getString("pic_path");
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
				//	showShortToast(R.string.json_exception);
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
				return new BusinessHelper().getContentNum(userId, 1);
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
						String collectNum = result.getString("count");
						tvCollectNum.setText("(" + collectNum + ")");
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}

			} else {
				//showShortToast(R.string.connect_server_exception);
			}
		}

	}

	// Activity从后台重新回到前台时被调用
	@Override
	protected void onRestart() {
		super.onRestart();
		if (NetUtil.checkNet(this)) {
			new GetUserInfor().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetUtil.checkNet(this)) {
			new GetUserInfor().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

}
