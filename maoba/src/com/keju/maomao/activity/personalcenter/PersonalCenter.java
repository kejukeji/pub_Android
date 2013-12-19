/**
 * 
 */
package com.keju.maomao.activity.personalcenter;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.keju.maomao.activity.my.CollectionOfEventListActivity;
import com.keju.maomao.activity.news.PrivateNewsListActivity;
import com.keju.maomao.activity.personalnfo.PersonalInfoActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;

/**
 * 我的个人中心
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-30 下午5:25:38
 */
public class PersonalCenter extends BaseActivity implements OnClickListener {

	private LinearLayout viewMycollect;
	private LinearLayout viewMyNews;
	private LinearLayout viewMyCollectEvent;
	private ImageView ivUserPhoto;
	private TextView tvAge, tvAddress,tvArea, tvNickName, tvSignature;

	private Button btnLeftMenu;

	private TextView tvExperiencePrice, tvGrade, tvLevel, tvIntegral, tvInvite, tvGift, tvConvey, tvPrivateLett,
			tvMyColBarCount, tvMyColEventCount;

	private ImageView ivPersonalSetting;// 个人资料设置或修改
	private ImageView ivSex;// 男女

	private LinearLayout viewInvite, viewGreeting, viewGift;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_center);
		findView();
		fillData();
	}

	private void findView() {

		btnLeftMenu = (Button) this.findViewById(R.id.btnLeftMenu);

		viewMycollect = (LinearLayout) this.findViewById(R.id.viewMyCollectBar);
		viewMyCollectEvent = (LinearLayout) this.findViewById(R.id.viewMyCollectEvent);
		viewMyNews = (LinearLayout) this.findViewById(R.id.viewMyNews);
		tvSignature = (TextView) this.findViewById(R.id.tvSignature);
		tvAge = (TextView) this.findViewById(R.id.tvAge);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvArea = (TextView) this.findViewById(R.id.tvArea);
		tvNickName = (TextView) this.findViewById(R.id.tvNickName);
		ivUserPhoto = (ImageView) this.findViewById(R.id.ivUserPhoto);
		ivSex = (ImageView) this.findViewById(R.id.ivSex);
		ivPersonalSetting = (ImageView) this.findViewById(R.id.ivPersonalSetting);

		// 经验值和积分等
		tvExperiencePrice = (TextView) this.findViewById(R.id.tvExperiencePrice);
		tvGrade = (TextView) this.findViewById(R.id.tvGrade);
		tvLevel = (TextView) this.findViewById(R.id.tvLevel);
		tvIntegral = (TextView) this.findViewById(R.id.tvIntegral);
		tvInvite = (TextView) this.findViewById(R.id.tvInvite);
		tvGift = (TextView) this.findViewById(R.id.tvGift);
		tvConvey = (TextView) this.findViewById(R.id.tvConvey);
		tvPrivateLett = (TextView) this.findViewById(R.id.tvPrivateLett);
		tvMyColBarCount = (TextView) this.findViewById(R.id.tvMyColBarCount);
		tvMyColEventCount = (TextView) this.findViewById(R.id.tvMyColEventCount);

		viewInvite = (LinearLayout) this.findViewById(R.id.viewInvite);
		viewGreeting = (LinearLayout) this.findViewById(R.id.viewGreeting);
		viewGift = (LinearLayout) this.findViewById(R.id.viewGift);

	}

	private void fillData() {
		btnLeftMenu.setOnClickListener(this);
		viewMycollect.setOnClickListener(this);
		viewMyCollectEvent.setOnClickListener(this);
		viewMyNews.setOnClickListener(this);

		ivPersonalSetting.setOnClickListener(this);
		viewInvite.setOnClickListener(this);
		viewGreeting.setOnClickListener(this);
		viewGift.setOnClickListener(this);

		if (NetUtil.checkNet(PersonalCenter.this)) {
			new GetUserInfor().execute();
			new GetUserBaseInforTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLeftMenu:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.viewMyCollectBar:
			int uid = SharedPrefUtil.getUid(PersonalCenter.this);
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_DATA, uid);
			openActivity(CollectionOfBarListActivity.class, b);
			break;
		case R.id.viewMyNews:
			openActivity(PrivateNewsListActivity.class);
			break;
		case R.id.ivPersonalSetting:
			openActivity(PersonalInfoActivity.class);
			break;
		case R.id.viewMyCollectEvent:
			openActivity(CollectionOfEventListActivity.class);
			break;
		case R.id.viewInvite:
			openActivity(MyInviteActivity.class);
			break;
		case R.id.viewGreeting:
			openActivity(MyGreetingActivity.class);
			break;
		case R.id.viewGift:
			openActivity(MyGiftActivity.class);
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
			int uid = SharedPrefUtil.getUid(PersonalCenter.this);
			try {
				return new BusinessHelper().getUserInfor(uid);
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
						String NickName = user.getString("nick_name");
						String address = userJson.getString("county");
						if(!userJson.getString("sex").equals("null")){
							int sex = userJson.getInt("sex");
							if (sex == 1) {
								ivSex.setBackgroundResource(R.drawable.ic_sex_man);
							} else {
								ivSex.setBackgroundResource(R.drawable.ic_sex_girl);
							}
						}
						if (signaTure.equals("null")) {
							tvSignature.setText("主人很懒未设置哦");
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
						if(userJson.has("pic_path")){
							String photoUrl = BusinessHelper.PIC_BASE_URL + userJson.getString("pic_path");
							ivUserPhoto.setTag(photoUrl);
							Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(photoUrl,
									new ImageCallback() {
										@Override
										public void imageLoaded(Drawable imageDrawable, String imageUrl) {
											ImageView image = (ImageView) ivUserPhoto.findViewWithTag(imageUrl);
											Bitmap bitmap = ImageUtil.getRoundCornerBitmapWithPic(imageDrawable, 0.5f);
											if (image != null) {
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
						}else{
							ivUserPhoto.setImageResource(R.drawable.bg_show11);
						}

					} else {
						showShortToast(result.getString("message"));

					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
					ivUserPhoto.setImageResource(R.drawable.bg_show11);
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
			int uid = SharedPrefUtil.getUid(PersonalCenter.this);
			try {
				return new BusinessHelper().GetUserBaseInfor(uid);
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
						tvMyColEventCount.setText(objUser.getInt("collect_activity_count") + "");
						tvMyColBarCount.setText(objUser.getInt("collect_pub_count") + "");
						tvInvite.setText(objUser.getInt("invitation") + "");
						tvGift.setText(objUser.getInt("gift") + "");
						tvConvey.setText(objUser.getInt("greeting_count") + "");
						tvPrivateLett.setText(objUser.getInt("private_letter_count") + "");
						tvExperiencePrice.setText(objUser.getInt("reputation") + "");
						tvGrade.setText(objUser.getString("level_description"));
						tvLevel.setText(objUser.getString("level"));
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
