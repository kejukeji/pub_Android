package com.maoba.activity.personalnfo;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;

/**
 * 个人个性签名修改界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-3 下午10:16:13
 */
public class PersonalizedSignatureActivity extends BaseActivity implements OnClickListener {
	private EditText edSignature;
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private String Signature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalized_signature);

		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edSignature = (EditText) this.findViewById(R.id.edpersonalsignature);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("个性签名");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			Signature = edSignature.getText().toString();
			if (TextUtils.isEmpty(Signature)) {
				showShortToast("请输入个性签名");
				return;
			}
			Intent signatureIntent = new Intent();
			signatureIntent.putExtra("SIGNATUREINPUT", Signature);
			setResult(Activity.RESULT_OK, signatureIntent);
			String nickname = "";
			String birthday = "";
			String sex = "";
			String address = "";
			String newPassword = "";
			
			if (NetUtil.checkNet(PersonalizedSignatureActivity.this)) {
				new personInfoAddTask(nickname, birthday, sex, Signature, address, newPassword).execute();
			}
		default:
			break;
		}
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
		private String address;
		private String newPassword;
		private String provinceId;
		private String cityId;
		private File avatarFile = null;

		/**
		 * @param nickName
		 * @param birthday
		 * @param sex
		 * @param signature
		 * @param address
		 * @param newPassword
		 */
		public personInfoAddTask(String nickName, String birthday, String sex, String signature, String address,
				String newPassword) {

			this.nickName = nickName;
			this.birthday = birthday;
			this.sex = sex;
			this.signature = signature;
			this.address = address;
			this.newPassword = newPassword;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int loginType = SharedPrefUtil.getLoginType(PersonalizedSignatureActivity.this);
			int userId = SharedPrefUtil.getUid(PersonalizedSignatureActivity.this);
			String openId = SharedPrefUtil.getWeiboUid(PersonalizedSignatureActivity.this);
			String password = SharedPrefUtil.getPassword(PersonalizedSignatureActivity.this);
			int sex = 0;
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, sex,
							signature, address, newPassword,provinceId,cityId, avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday, sex,
							signature, address, avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
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
						showShortToast("个人资料设置成功");
						finish();
					} else {
						showShortToast("个人资料设置失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				// showShortToast(result.getString("message"));
				showShortToast("服务连接失败");
			}
		}
	}
}
