package com.maoba.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.maoba.CommonApplication;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.maoba.util.StringUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-16 上午9:24:55
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private EditText edUserName, edEmail, edPassWord;
	private Button btnRegister;
	private ImageView ivSinaLogin, ivQQLogin;

	private ProgressDialog pd;
	private int logintype;// 登陆方式标志位

	private Tencent mTencent;

	private static final String SCOPE = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		MobclickAgent.onError(this);
		MobclickAgent.onEvent(this, "register");
		findView();

		((CommonApplication) getApplication()).addActivity(this);
	}

	private void findView() {
		mTencent = Tencent.createInstance(Constants.TENCENT_APP_ID, this.getApplicationContext());

		edUserName = (EditText) this.findViewById(R.id.edUserName);
		edEmail = (EditText) this.findViewById(R.id.edEmail);
		edPassWord = (EditText) this.findViewById(R.id.edPassWord);

		btnRegister = (Button) this.findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);

		ivSinaLogin = (ImageView) this.findViewById(R.id.ivSinaLogin);
		ivSinaLogin.setOnClickListener(this);

		ivQQLogin = (ImageView) this.findViewById(R.id.ivQQLogin);
		ivQQLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegister:
			String userName = edUserName.getText().toString().trim();
			String passWord = edPassWord.getText().toString().trim();
			String eMail = edEmail.getText().toString().trim();
			if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(eMail) || TextUtils.isEmpty(passWord)) {
				showShortToast("请输入完整的信息后注册");
				return;
			}
			if (!StringUtil.isEmail(eMail)) {
				showShortToast("请输入正常的邮箱号登陆");
				return;
			}
			if (passWord.length() < 6) {
				showShortToast("密码必须大于或者等于6位数！");
				return;
			}
			if (NetUtil.checkNet(this)) {
				logintype = Constants.LOGIN_COMMON;
				new RegisterTask(logintype, userName, eMail, passWord).execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		case R.id.ivSinaLogin:// 已不再用
			if (SharedPrefUtil.checkWeiboBind(RegisterActivity.this)) {
				String openUid = SharedPrefUtil.getWeiboUid(this);
				if (NetUtil.checkNet(this)) {
					logintype = Constants.LOGIN_SINA;// 表示新浪微博登陆
					new RegisterTask(logintype, openUid, true).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}

			} else {
				if (NetUtil.checkNet(this)) {
					Intent authorizeIntent = new Intent(this, AuthorizeActivity.class);
					authorizeIntent.putExtra(Constants.EXTRA_BIND_FROM, Constants.BIND_WEIBO);
					startActivityForResult(authorizeIntent, Constants.REQUEST_CODE_BIND_WEIBO);
				} else {
					showShortToast(R.string.NoSignalException);
				}

			}
			break;
		case R.id.ivQQLogin:// 已不再用
			if (SharedPrefUtil.checkQQBind(this)) {
				String uid = SharedPrefUtil.getQQOpenid(this);
				if (NetUtil.checkNet(this)) {
					logintype = Constants.LOGIN_QQ;
					new RegisterTask(logintype, uid, true).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {
				logintype = Constants.LOGIN_QQ;
				IUiListener listener = new BaseUiListener(logintype);
				mTencent.login(this, SCOPE, listener);
			}
			break;
		default:
			break;
		}

	}

	public class RegisterTask extends AsyncTask<Void, Void, JSONObject> {
		private int logintype;
		private String userName;
		private String eMail;
		private String passWord;

		private String openId;;

		private boolean isThirdLogin = false;

		/**
		 * @param logintype
		 * @param openId
		 */
		public RegisterTask(int logintype, String openId, boolean isThirdLogin) {
			super();
			this.logintype = logintype;
			this.openId = openId;
			this.isThirdLogin = isThirdLogin;

		}

		/**
		 * @param logintype
		 * @param userName
		 * @param eMail
		 * @param passWord
		 */
		public RegisterTask(int logintype, String userName, String eMail, String passWord) {
			super();
			this.logintype = logintype;
			this.userName = userName;
			this.eMail = eMail;
			this.passWord = passWord;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(RegisterActivity.this);
			}
			pd.setMessage("正在注册中...");
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				if (isThirdLogin) {
				//	return new BusinessHelper().thirdLogin(logintype, openId);
				} else {
					return new BusinessHelper().register(logintype, userName, eMail, passWord);
				}
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
						JSONObject userJson = result.getJSONObject("user");
						showShortToast("注册成功");
						int uid = userJson.getInt("id");
						SharedPrefUtil.setUid(RegisterActivity.this, uid);
						setResult(RESULT_OK);
						finish();
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

	private class BaseUiListener implements IUiListener {
		private int logintype;

		/**
		 * @param logintype
		 */
		public BaseUiListener(int logintype) {
			super();
			this.logintype = logintype;
		}

		@Override
		public void onComplete(JSONObject response) {
			String access_token;
			String expires_in;
			String openid;
			try {
				access_token = response.getString("access_token");
				expires_in = response.getString("expires_in");
				openid = response.getString("openid");
				String currTime = System.currentTimeMillis() + "";
				SharedPrefUtil.setQQInfo(RegisterActivity.this, access_token, expires_in, openid, currTime);
				logintype = Constants.LOGIN_QQ;
				new RegisterTask(logintype, openid, true).execute();
			} catch (JSONException e) {
			}
		}

		@Override
		public void onError(UiError e) {
			showShortToast("msg:" + e.errorMessage + ", detail:" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			showShortToast("取消授权");
		}
	}

}
