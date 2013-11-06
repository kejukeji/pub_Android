/**
 * 
 */
package com.maoba.activity.personalnfo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.util.ImageUtil;
import com.maoba.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人信息修改界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class PersonalInfoActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;
	private TextView tvBirthday, tvSex, tvSignature, tvNickName, tvDistrict;
	private ImageView ivUserImage;
	private EditText edModificationPassword;
	public String newPassword;
	private LinearLayout viewBirthday, viewSex, viewSignature, viewNickname,
			viewChangingPassword;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片，临时文件
	private File avatarFile;
	private File PHOTO_DIR;//照相机拍照得到的图片的存储位置
	private int[] dateReceice = { 0, 0, 0 };
	private String dateSelected;
	static final int DATE_DIALOG_ID = 1;
	private int login_type;
	private String open_id;
	private String userImageName;// 用户头像图片名称
	private long userId;

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
		if (tvNickName.getText() == "") {
			tvNickName.setText("未填写");
		}
		tvBirthday = (TextView) this.findViewById(R.id.tvBirthday);
		if (tvBirthday.getText() == "") {
			tvBirthday.setText("未填写");
		}
		tvSex = (TextView) this.findViewById(R.id.tvSex);
		if (tvSex.getText() == "") {
			tvSex.setText("未填写");
		}
		tvSignature = (TextView) this.findViewById(R.id.tvSignature);
		if (tvSignature.getText() == "") {
			tvSignature.setText("未填写");
		}
		tvDistrict = (TextView) this.findViewById(R.id.tvDistrict);
		edModificationPassword = (EditText) this
				.findViewById(R.id.edModificationPassword);

		viewBirthday = (LinearLayout) this.findViewById(R.id.viewBirthday);
		viewSex = (LinearLayout) this.findViewById(R.id.viewSex);
		viewSignature = (LinearLayout) this.findViewById(R.id.viewSignature);
		viewNickname = (LinearLayout) this.findViewById(R.id.viewNickname);
		viewChangingPassword = (LinearLayout) this
				.findViewById(R.id.viewChangingPassword);
		viewChangingPassword.setOnClickListener(this);
		viewNickname.setOnClickListener(this);
		viewSignature.setOnClickListener(this);
		viewBirthday.setOnClickListener(this);
		final Calendar c = Calendar.getInstance();

	}

	private void fillData() {
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setText("提交");
		btnRight.setOnClickListener(this);
		tvTitle.setText("个人资料");
		ivUserImage.setOnClickListener(this);
		viewSex.setOnClickListener(this);
		tvNickName.setOnClickListener(this);
		tvSignature.setOnClickListener(this);

		// Intent intent = this.getIntent();
		// Bundle bundle = intent.getExtras();
		// login_type = bundle.getInt("logintype");
		// open_id = bundle.getString("openUid");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.PHOTO_PICKED_WITH_DATA://相册
				Bitmap cameraBitmap = data.getParcelableExtra("data");
				if (cameraBitmap == null) {
					Uri dataUri = data.getData();
					Intent intent = getCropImageIntent(dataUri);
					startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
				}
				ivUserImage.setImageBitmap(cameraBitmap);

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
				} 
				catch (Exception e) {
					MobclickAgent.reportError(PersonalInfoActivity.this, StringUtil.getExceptionInfo(e));
				}
				break;
			case Constants.CAMERA_WITH_DATA:// 拍照
				doCropPhoto(mCurrentPhotoFile);
				break;
			case Constants.BIRTHDAYNUM:
				dateReceice = (data.getIntArrayExtra("BRITHDAYSELECTED"));
				dateSelected = String.valueOf(dateReceice[0])
						+ "-"
						+ String.valueOf(dateReceice[1] + "-"
								+ String.valueOf(dateReceice[2]));
				tvBirthday.setText(dateSelected);
				break;
			case Constants.SIGNATURENUM:
				tvSignature.setText(data.getStringExtra("SIGNATUREINPUT"));
				break;
			case Constants.NICKNAMENUM:
				tvNickName.setText(data.getStringExtra("NICKNAMEINPUT"));
				break;
			case Constants.PASSWORDNUMBER:
				newPassword = new String(data.getStringExtra("NEWPASSWORD"));
				
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			String nickName = tvNickName.getText().toString().trim();
			String birthday = tvBirthday.getText().toString().trim();
			String sex = tvSex.getText().toString().trim();
			String signature = tvSignature.getText().toString().trim();
			String address = tvDistrict.getText().toString().trim();
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

			builder.setSingleChoiceItems(items, sexStatus,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
							case 0:
								tvSex.setText("男");
								tvSex.setTextSize(15);
								tvSex.setTextColor(0xFF5F5F5F);
								dialog.dismiss();
								break;
							case 1:
								tvSex.setText("女");
								tvSex.setTextSize(15);
								tvSex.setTextColor(0xFF5F5F5F);
								dialog.dismiss();
							default:
								break;

							}
						}
					}).show();
			// AlertDialog alert = builder.create();
			break;
		case R.id.ivUserImage:
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.user_image_changing, null); //
			TextView tvTakePhoto = (TextView) layout
					.findViewById(R.id.tvtakephoto);
			TextView tvGetPicture = (TextView) layout
					.findViewById(R.id.tvgetpicture);
			TextView tvCancel = (TextView) layout.findViewById(R.id.tvcancel);
			final Dialog dialog = new Dialog(this, R.style.dialog);
			dialog.setContentView(layout); // 将取得布局文件set进去
			dialog.show(); // 显示
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int) (display.getWidth() - 20); // 设置宽度
			lp.gravity = Gravity.BOTTOM;
			dialog.getWindow().setAttributes(lp);
			tvCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			tvTakePhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doTakePhoto();// 用户点击了从照相机获取
					} else {
						Toast.makeText(PersonalInfoActivity.this, "请检查SD卡是否正常", Toast.LENGTH_SHORT).show();
					}
				}
			});
			tvGetPicture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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
					
				}
			});
			break;
		case R.id.viewBirthday:
			startActivityForResult(new Intent(Constants.INTENT_BIRTHDAY),
					Constants.BIRTHDAYNUM);
			break;
		case R.id.viewSignature:
			startActivityForResult(new Intent(Constants.INTENT_SIGNATURE),
					Constants.SIGNATURENUM);
			break;
		case R.id.viewNickname:
			startActivityForResult(new Intent(Constants.INTENT_NICKNAME),
					Constants.NICKNAMENUM);
			break;
		case R.id.viewChangingPassword:
			startActivityForResult(new Intent(Constants.INTENT_PASSWORD),
					Constants.PASSWORDNUMBER);
			break;
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
			Toast.makeText(this, "请检查SD卡是否正常", Toast.LENGTH_SHORT).show();
		}
	}
	public void StartCamera(){
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
			Toast.makeText(this, "拍照出错", Toast.LENGTH_LONG).show();
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
		} 
		catch (Exception e) {
			MobclickAgent.reportError(PersonalInfoActivity.this, StringUtil.getExceptionInfo(e));
			Toast.makeText(this, "照片裁剪出错", Toast.LENGTH_LONG).show();
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
}
