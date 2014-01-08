package com.keju.maomao.activity.personalnfo;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.R.string;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;

/**
 * 密码重置界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class ChangingPasswordActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private EditText edPassword, edNewPassword;
	private String newPassword;
	private String passWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changing_password);
		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edPassword = (EditText) this.findViewById(R.id.edInputPassword);
		edNewPassword = (EditText) this.findViewById(R.id.edInputNewPassword);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("密码修改");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			passWord = edPassword.getText().toString().trim();
			newPassword = edNewPassword.getText().toString().trim();
			if (StringUtil.isBlank(passWord) || StringUtil.isBlank(newPassword)) {
				showShortToast("密码不能为空");
				return;
			}
			if (newPassword.length() < 6) {
				showShortToast("新密码请输入大于六位");
				return;
			} else {
				String userPassWord = SharedPrefUtil.getPassword(ChangingPasswordActivity.this);
				int loginType = SharedPrefUtil.getLoginType(ChangingPasswordActivity.this);
				{
					if (loginType == 0) {
						if (passWord.equals(userPassWord)) {
							String nickname = "";
							String sex = "";
							String signature = "";
							String birthday = "";
							if (NetUtil.checkNet(ChangingPasswordActivity.this)) {
								new personInfoAddTask(nickname, birthday, sex, signature, newPassword).execute();
							}
						} else {
							showShortToast("你输入的旧密码不正确，请重新输入");
						}
					} else {
						showShortToast("您非普通登录不可修改密码");
					}
				}

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
			int loginType = SharedPrefUtil.getLoginType(ChangingPasswordActivity.this);
			int userId = SharedPrefUtil.getUid(ChangingPasswordActivity.this);
			String openId = SharedPrefUtil.getWeiboUid(ChangingPasswordActivity.this);
			String password = SharedPrefUtil.getPassword(ChangingPasswordActivity.this);
			int sex = 0;
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, "",
							signature, newPassword, "", "", "", avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday, "",
							signature, "", "", "", avatarFile);
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
						showShortToast("修改密码成功");
						SharedPrefUtil.setPassword(ChangingPasswordActivity.this, newPassword);
						finish();
					} else {
						showShortToast("修改密码失败");
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
