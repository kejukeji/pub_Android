/**
 * 
 */
package com.keju.maomao.activity.personalnfo;

import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keju.maomao.AsyncImageLoader;
import com.keju.maomao.AsyncImageLoader.ImageCallback;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.DateUtil;
import com.keju.maomao.util.ImageUtil;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人信息修改界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class PersonalInfoActivity extends BaseActivity implements OnClickListener {

	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;
	private TextView tvBirthday, tvSex, tvSignature, tvNickName, tvDistrict;
	private ImageView ivUserImage;
	private TextView tvModificationPassword;
     
	private LinearLayout viewImage;
	private LinearLayout viewBirthday, viewSex, viewSignature, viewNickname, viewChangingPassword, viewdistrict;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片，临时文件
	private File avatarFile;// 头像文件
	private File PHOTO_DIR;// 照相机拍照得到的图片的存储位置
	static final int DATE_DIALOG_ID = 1;

	private long userId;

	String nickName = null;
	String birthday = null;
	String sex = null;
	String signature = null;
	String address = null;
	String newPassword = null;
	private Bitmap cameraBitmap;// 头像bitmap

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_info);
		findView();
		fillData();
		createPhotoDir();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);

		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		ivUserImage = (ImageView) this.findViewById(R.id.ivUserImage);
		tvNickName = (TextView) this.findViewById(R.id.tvNickName);
		tvBirthday = (TextView) this.findViewById(R.id.tvBirthday);
		tvSex = (TextView) this.findViewById(R.id.tvSex);
		tvSignature = (TextView) this.findViewById(R.id.tvSignature);
		tvDistrict = (TextView) this.findViewById(R.id.tvDistrict);
		tvModificationPassword = (TextView) this.findViewById(R.id.tvModificationPassword);
		tvModificationPassword.setText("修改密码");
		viewBirthday = (LinearLayout) this.findViewById(R.id.viewBirthday);
		viewSex = (LinearLayout) this.findViewById(R.id.viewSex);
		viewSignature = (LinearLayout) this.findViewById(R.id.viewSignature);
		viewNickname = (LinearLayout) this.findViewById(R.id.viewNickname);
		viewChangingPassword = (LinearLayout) this.findViewById(R.id.viewChangingPassword);
		viewdistrict = (LinearLayout) this.findViewById(R.id.viewdistrict);
        
		viewImage = (LinearLayout)this.findViewById(R.id.viewImage);
		viewImage.setOnClickListener(this);
		viewdistrict.setOnClickListener(this);
		viewChangingPassword.setOnClickListener(this);
		viewNickname.setOnClickListener(this);
		viewSignature.setOnClickListener(this);
		viewBirthday.setOnClickListener(this);

		if (NetUtil.checkNet(PersonalInfoActivity.this)) {
			new GetUserInfor().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setText("提交");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		btnRight.setVisibility(View.GONE);
		tvTitle.setText("个人资料");
		ivUserImage.setOnClickListener(this);
		viewSex.setOnClickListener(this);
		tvNickName.setOnClickListener(this);
		tvSignature.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.PHOTO_PICKED_WITH_DATA:// 相册
				cameraBitmap = data.getParcelableExtra("data");
				if (cameraBitmap == null) {
					Uri dataUri = data.getData();
					Intent intent = getCropImageIntent(dataUri);
					startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
				}
				try {
					// 保存缩略图
					FileOutputStream out = null;
					File file = new File(PHOTO_DIR, ImageUtil.createAvatarFileName(String.valueOf(userId)));
					if (file != null && file.exists()) {
						file.delete();
					}
					avatarFile = new File(PHOTO_DIR, ImageUtil.createAvatarFileName(String.valueOf(userId)));
					out = new FileOutputStream(avatarFile, false);

					if (cameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
						out.flush();
						out.close();
					}
					if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists())
						mCurrentPhotoFile.delete();
					String nickname1 = "";
					String sex1 = "女";
					String signature1 = "";
					String newPassword1 = "";
					String birthday1 = "";
					if (NetUtil.checkNet(PersonalInfoActivity.this)) {
						new personInfoAddTask(nickname1, birthday1, sex1, signature1, newPassword1).execute();
					} else {
						showShortToast(R.string.NoSignalException);
					}
				} catch (Exception e) {
					MobclickAgent.reportError(PersonalInfoActivity.this, StringUtil.getExceptionInfo(e));
				}
				break;
			case Constants.CAMERA_WITH_DATA:// 拍照
				doCropPhoto(mCurrentPhotoFile);
				break;
//			case Constants.BIRTHDAYNUM:
//				dateReceice = (data.getIntArrayExtra("BRITHDAYSELECTED"));
//				dateSelected = String.valueOf(dateReceice[0]) + "-"
//						+ String.valueOf(dateReceice[1] + "-" + String.valueOf(dateReceice[2]));
//				tvBirthday.setText(dateSelected);
//				break;
//			case Constants.SIGNATURENUM:
//				tvSignature.setText(data.getStringExtra("SIGNATUREINPUT"));
//				break;
//			case Constants.NICKNAMENUM:
//				tvNickName.setText(data.getStringExtra("NICKNAMEINPUT"));
//				break;
//			case Constants.PASSWORDNUMBER:
//				tvModificationPassword.setText(data.getStringExtra("NEWPASSWORD"));
//				// .(data.getStringExtra("NEWPASSWORD"));

			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.btnRight:

			break;
		case R.id.viewSex:
			final CharSequence[] items = { "男", "女", };
			int sexStatus = 0;
			if (tvSex.getText() == "女") {
				sexStatus = 1;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("性别");
			builder.setSingleChoiceItems(items, sexStatus, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					case 1:
						tvSex.setText("女");
						tvSex.setTextSize(15);
						tvSex.setTextColor(0xFF5F5F5F);
						String nickname = "";
						String sex = "女";
						String signature = "";
						String newPassword = "";
						String birthday = "";
						if (NetUtil.checkNet(PersonalInfoActivity.this)) {
							new personInfoAddTask(nickname, birthday, sex, signature, newPassword).execute();
						}
						dialog.dismiss();
						break;
					case 0:
						tvSex.setText("男");
						tvSex.setTextSize(15);
						tvSex.setTextColor(0xFF5F5F5F);
						String nickname1 = "";
						String sex1 = "男";
						String signature1 = "";
						String newPassword1 = "";
						String birthday1 = "";
						if (NetUtil.checkNet(PersonalInfoActivity.this)) {
							new personInfoAddTask(nickname1, birthday1, sex1, signature1, newPassword1).execute();
						} else {
							showShortToast(R.string.NoSignalException);
						}
						dialog.dismiss();
					default:
						break;

					}
				}
			}).show();
			// AlertDialog alert = builder.create();
			break;
		case R.id.viewImage:
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.user_image_changing, null); //
			TextView tvTakePhoto = (TextView) layout.findViewById(R.id.tvtakephoto);
			TextView tvGetPicture = (TextView) layout.findViewById(R.id.tvgetpicture);
			TextView tvCancel = (TextView) layout.findViewById(R.id.tvcancel);
			final Dialog dialog = new Dialog(this, R.style.dialog);
			dialog.setContentView(layout); // 将取得布局文件set进去
			dialog.show(); // 显示
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int) (display.getWidth()); // 设置宽度
			lp.gravity = Gravity.BOTTOM;
			dialog.getWindow().setAttributes(lp);
			tvCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			tvTakePhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doTakePhoto();// 用户点击了从照相机获取
					} else {
						showShortToast("请检查SD卡是否正常");
					}
					dialog.dismiss();
				}
			});
			tvGetPicture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", 200);
					intent.putExtra("outputY", 200);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
					dialog.dismiss();

				}
			});
			break;
		case R.id.viewBirthday:
		   openActivity(BirthdaySetActivity.class);
		   //动画效果
		   overridePendingTransition(R.anim.roll_up, R.anim.roll);
			break;
		case R.id.viewSignature:
			openActivity(PersonalizedSignatureActivity.class);		
			overridePendingTransition(R.anim.roll_up, R.anim.roll);
			break;
		case R.id.viewNickname:
			openActivity(NickNameActivity.class);	
			overridePendingTransition(R.anim.roll_up, R.anim.roll);
			break;
		case R.id.viewChangingPassword:
			openActivity(ChangingPasswordActivity.class);	
			overridePendingTransition(R.anim.roll_up, R.anim.roll);
			break;
		case R.id.viewdistrict:
			openActivity(ProvinceAcitvity.class);
			overridePendingTransition(R.anim.roll_up, R.anim.roll);
		default:
			break;
		}

	}

	private void createPhotoDir() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_DIR_NAME + "/");
			if (!PHOTO_DIR.exists()) {
				// 创建照片的存储目录
				PHOTO_DIR.mkdirs();
			}
		} else {
			showShortToast("请检查SD卡是否正常");
		}
	}

	public void StartCamera() {
		try {
			mCurrentPhotoFile = new File(PHOTO_DIR, ImageUtil.getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, Constants.CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "拍照出错", Toast.LENGTH_LONG).show();
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			mCurrentPhotoFile = new File(PHOTO_DIR, ImageUtil.getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, Constants.CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			showShortToast("拍照出错");
		}
	}

	/**
	 * 调用图片剪辑程序去剪裁图片
	 * 
	 * @param f
	 */
	protected void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);

		} catch (Exception e) {
			MobclickAgent.reportError(PersonalInfoActivity.this, StringUtil.getExceptionInfo(e));
			showShortToast("照片裁剪出错");
		}
	}

	/**
	 * 调用图片剪辑程序
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * 用户修改或添加个人资料
	 * 
	 * */
	private class personInfoAddTask extends AsyncTask<Void, Void, JSONObject> {
		private String nickName;
		private String birthday;
		private String sex;
		private String signature;
		private String provinceId;
		private String cityId;
		private String newPassword;

		/**
		 * @param nickName昵称
		 * @param birthday生日
		 * @param sex性别
		 * @param signature
		 *            个性签名
		 * @param address地址
		 * @param newPassword密码
		 * 
		 * 
		 */
		public personInfoAddTask(String nickName, String birthday, String sex, String signature, String newPassword) {
			this.nickName = nickName;
			this.birthday = birthday;
			this.sex = sex;
			this.signature = signature;
			this.newPassword = newPassword;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int loginType = SharedPrefUtil.getLoginType(PersonalInfoActivity.this);
			int userId = SharedPrefUtil.getUid(PersonalInfoActivity.this);
			String openId = SharedPrefUtil.getWeiboUid(PersonalInfoActivity.this);
			String password = SharedPrefUtil.getPassword(PersonalInfoActivity.this);
			int gender = sex.equals("男") ? 1 : 0;
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, gender,
							signature, newPassword, "", "", "", avatarFile);
				} catch (SystemException e) {

				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday,
							gender, signature, "", "", "", avatarFile);
				} catch (SystemException e) {

				}
			}
			return null;

		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						if (avatarFile != null) {
							ivUserImage.setImageBitmap(cameraBitmap);
							showShortToast("修改头像成功");
						} else {
							showShortToast("个人资料设置成功");
						}
					} else {
						showShortToast("个人资料设置失败");
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}

			} else {
				showShortToast("服务连接失败");
			}
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
			int userId = SharedPrefUtil.getUid(PersonalInfoActivity.this);
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
						nickName = user.getString("nick_name");
						signature = userJson.getString("signature");
						birthday = userJson.getString("birthday");
						address = userJson.getString("county");
						String sex = userJson.getString("sex");
						if (nickName.equals("null")) {
							tvNickName.setText("未填写");
						} else {
							tvNickName.setText(nickName);
						}

						if (birthday.equals("null")) {
							tvBirthday.setText("未填写");
						} else {
							try {
								tvBirthday.setText(DateUtil.dateToString("yyyy-MM-dd",
										DateUtil.stringToDate("yyyy-MM-dd HH:mm:ss", birthday)));
							} catch (Exception e) {
							}

						}
						if (sex.equals("null")) {
							tvSex.setText("未填写");
						} else {
							if (Integer.parseInt(sex) == 1) {
								tvSex.setText("男");
							} else if (Integer.parseInt(sex) == 0) {
								tvSex.setText("女");
							}
						}
						if (signature.equals("null")) {
							tvSignature.setText("未填写");
						} else {
							tvSignature.setText(signature);
						}
						if (address.equals("$$")) {
							tvDistrict.setText("未设置");
						} else {
							String[] address1 = StringUtil.stringCut(address);
							tvDistrict.setText(address1[0]+address1[1]);
						}

						String photoUrl = BusinessHelper.PIC_BASE_URL + userJson.getString("pic_path");
						ivUserImage.setTag(photoUrl);
						Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(photoUrl,
								new ImageCallback() {
									@Override
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										ImageView image = (ImageView) ivUserImage.findViewWithTag(imageUrl);
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
							ivUserImage.setImageDrawable(cacheDrawble);
						} else {
							ivUserImage.setImageResource(R.drawable.bg_show11);
						}

					} else {
						showShortToast(result.getString("message"));

					}
				} catch (JSONException e) {
					// showShortToast(R.string.json_exception);
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

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if (NetUtil.checkNet(this)) {
	// new GetUserInfor().execute();
	// } else {
	// showShortToast(R.string.NoSignalException);
	// }
	// }

}
