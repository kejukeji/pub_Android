/**
 * 
 */
package com.keju.maomao.activity.friendpersonalcenter;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.activity.my.CollectionOfBarListActivity;
import com.keju.maomao.activity.news.PrivateLetterActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;
import com.keju.maomao.view.toast.TipsToast;

/**
 * 他人的个人中心
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-3 下午10:16:13
 */
public class FriendPersonalCenter extends BaseActivity implements OnClickListener {
	private Button btnLeftMenu;
	private TextView tvSignature;// 个性签名
	private TextView tvAge, tvAddress, tvNickName, tvCollectNum;

	private LinearLayout viewDrink, viewGiveOneTheEye, viewSendGift, viewSendNews; // 喝一杯/眉目传情/送礼物/发私信/

	private ImageView ivUserPhoto, ivSex;
	private LinearLayout viewCollectBar, viewFriendGift;

	private int userId;
	private String NickName;

	private TextView tvDistance, tvGrade, tvArea, tvIntegral, tvGiftCount;// 距离/等级/积分/礼物个数
	
	private static TipsToast tipsToast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_personal_center);

		userId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		findView();
		fillData();
	}

	private void findView() {
		btnLeftMenu = (Button) this.findViewById(R.id.btnLeftMenu);
		tvSignature = (TextView) this.findViewById(R.id.tvSignature);

		tvAge = (TextView) this.findViewById(R.id.tvAge);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvArea = (TextView) this.findViewById(R.id.tvArea);
		tvNickName = (TextView) this.findViewById(R.id.tvNickName);
		tvGrade =(TextView) this.findViewById(R.id.tvGrade);
		tvIntegral=(TextView) this.findViewById(R.id.tvIntegral);
		tvCollectNum = (TextView) this.findViewById(R.id.tvCollectNum);
		tvGiftCount = (TextView) this.findViewById(R.id.tvGiftCount);
		ivUserPhoto = (ImageView) this.findViewById(R.id.ivUserPhoto);
		ivSex = (ImageView) this.findViewById(R.id.ivSex);

		viewDrink = (LinearLayout) this.findViewById(R.id.viewDrink);
		viewGiveOneTheEye = (LinearLayout) this.findViewById(R.id.viewGiveOneTheEye);
		viewSendGift = (LinearLayout) this.findViewById(R.id.viewSendGift);
		viewSendNews = (LinearLayout) this.findViewById(R.id.viewSendNews);

		viewFriendGift = (LinearLayout) this.findViewById(R.id.viewFriendGift);
		viewCollectBar = (LinearLayout) this.findViewById(R.id.viewCollectBar);

		if (NetUtil.checkNet(FriendPersonalCenter.this)) {
			new GetUserInfor().execute();
			new GetCollectNum().execute();
			new GetUserBaseInforTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	private void fillData() {
		btnLeftMenu.setOnClickListener(this);

		viewDrink.setOnClickListener(this);
		viewGiveOneTheEye.setOnClickListener(this);
		viewSendGift.setOnClickListener(this);
		viewSendNews.setOnClickListener(this);

		viewFriendGift.setOnClickListener(this);
		viewCollectBar.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLeftMenu:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.viewCollectBar:
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_DATA, userId);
			openActivity(CollectionOfBarListActivity.class, b);
			break;
		case R.id.viewSendNews:
			Bundle b1 = new Bundle();
			b1.putInt(Constants.EXTRA_DATA, userId);
			b1.putString("NICK_NAME", NickName);
			openActivity(PrivateLetterActivity.class, b1);
			break;
		case R.id.viewFriendGift:
			Bundle b2 = new Bundle();
			b2.putInt(Constants.EXTRA_DATA, userId);
			openActivity(GetGiftActivity.class, b2);
			break;
		case R.id.viewDrink:
			Bundle b3 = new Bundle();
			b3.putInt(Constants.EXTRA_DATA, userId);
			openActivity(SendInviteActivity.class, b3);
			break;
		case R.id.viewSendGift:
			Bundle b4 = new Bundle();
			b4.putInt(Constants.EXTRA_DATA, userId);
			openActivity(SendGiftActivity.class, b4);
			break;
		case R.id.viewGiveOneTheEye:
			if (NetUtil.checkNet(FriendPersonalCenter.this)) {
				new SendGiveOneTheEyeTask().execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
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
						int sex = userJson.getInt("sex");
						NickName = user.getString("nick_name");
						if (sex == 1) {
							ivSex.setBackgroundResource(R.drawable.ic_sex_man);
						} else {
							ivSex.setBackgroundResource(R.drawable.ic_sex_girl);
						}
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
							tvAge.setText(ageString + "岁");

						}
						if (address.equals("$$")) {
							tvAddress.setText("未设置");
						} else {
							try {
								String[] address1 = StringUtil.stringCut(address);
								tvAddress.setText(address1[0]);
								tvArea.setText(address1[1]);
							} catch (Exception e) {
							}
						}
						String photoUrl = BusinessHelper.PIC_BASE_URL + userJson.getString("pic_path");
						ivUserPhoto.setTag(photoUrl);
						Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(photoUrl,
								new ImageCallback() {
									@Override
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										ImageView image = (ImageView) ivUserPhoto.findViewWithTag(imageUrl);
										if (image != null) {
											Bitmap bitmap = ImageUtil.getRoundCornerBitmapWithPic(imageDrawable, 0.5f);
											if (imageDrawable != null) {
												image.setImageBitmap(bitmap);
											} else {
												image.setImageResource(R.drawable.bg_show11);
											}
										}
									}
								});
						if (cacheDrawble != null) {
							Bitmap bitmap = ImageUtil.getRoundCornerBitmapWithPic(cacheDrawble, 0.5f);
							ivUserPhoto.setImageBitmap(bitmap);
						} else {
							ivUserPhoto.setImageResource(R.drawable.bg_show11);
						}

					} else {
						showShortToast(result.getString("message"));

					}
				} catch (JSONException e) {
					ivUserPhoto.setImageResource(R.drawable.bg_show11);
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
						tvCollectNum.setText(collectNum);
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}

			} else {
				 showShortToast(R.string.connect_server_exception);
			}
		}

	}

	/***
	 * 
	 * 获取用户在猫吧的详细信息
	 * 
	 */
	private class GetUserBaseInforTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().GetUserBaseInfor(userId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					if (result.getInt("status") == Constants.REQUEST_SUCCESS) {
						JSONObject objUser = result.getJSONObject("user");
						// tvMyColBarCount.setText(objUser.getInt("collect_pub_count")+"");
						// tvDistance.setText(objUser.getInt("reputation")+"");
						tvGiftCount.setText(objUser.getInt("gift")+"件");
						tvGrade.setText(objUser.getString("level_description"));
						tvIntegral.setText(objUser.getInt("credit") + "");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showShortToast(R.string.json_exception);
				}

			} else {
				showShortToast(R.string.connect_server_exception);
			}

		}

	}

	/***
	 * 
	 * 给人抛媚眼接口
	 */
	private class SendGiveOneTheEyeTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在抛媚眼");
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int senderId = SharedPrefUtil.getUid(FriendPersonalCenter.this);
			try {
				return new BusinessHelper().sendGiveOneTheEye(senderId, userId);
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
						showTips(R.drawable.ic_send_one_eye,R.string.send_eye);
					} else {
						showShortToast("抛媚眼失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}
	/***
	 * 自定义toast
	 * @param iconResId
	 * 
	 */
	private void showTips(int iconResId,int msgResId) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(), msgResId, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(msgResId);
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
