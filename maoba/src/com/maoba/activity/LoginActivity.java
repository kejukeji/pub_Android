/**
 * 
 */
package com.maoba.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.bar.BarListActivity;
import com.maoba.activity.base.BaseActivity;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-16 下午1:23:52
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private EditText edUserName, edPassWord;
	private Button btnLogin;
	private TextView tvGetPasswordBack, tvRegisterMaoMao;
	private ImageView ivSinaLogin, ivQQLogin;// 第三方登陆

	private ProgressDialog pd;
	private int logintype;// 登陆方式标志位

	private Tencent mTencent;

	private static final String SCOPE = "all";

	private Button btn;

	private String nickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		MobclickAgent.onError(this);
		MobclickAgent.onEvent(this, "login");
		findView();
		fillData();
	}

	private void findView() {
		mTencent = Tencent.createInstance(Constants.TENCENT_APP_ID, this.getApplicationContext());

		edUserName = (EditText) this.findViewById(R.id.edUserName);
		edPassWord = (EditText) this.findViewById(R.id.edPassWord);

		tvGetPasswordBack = (TextView) this.findViewById(R.id.tvGetPasswordBack);
		tvGetPasswordBack.setOnClickListener(this);

		tvRegisterMaoMao = (TextView) this.findViewById(R.id.tvRegisterMaoMao);
		tvRegisterMaoMao.setOnClickListener(this);

		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);

		ivSinaLogin = (ImageView) this.findViewById(R.id.ivSinaLogin);
		ivSinaLogin.setOnClickListener(this);

		ivQQLogin = (ImageView) this.findViewById(R.id.ivQQLogin);
		ivQQLogin.setOnClickListener(this);

		btn = (Button) this.findViewById(R.id.btn);
		btn.setOnClickListener(this);
	}

	private void fillData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			String userName = edUserName.getText().toString().trim();
			String passWord = edPassWord.getText().toString().trim();
			if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
				showShortToast("请输入用户名或密码");
				return;
			}
			if (NetUtil.checkNet(LoginActivity.this)) {
				logintype = Constants.LOGIN_COMMON;// 表示普通登陆
				new LoginTask(logintype, userName, passWord).execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}

			break;
		case R.id.ivSinaLogin:
			if (SharedPrefUtil.checkWeiboBind(LoginActivity.this)) {
				String openUid = SharedPrefUtil.getWeiboUid(this);
				if (NetUtil.checkNet(this)) {
					logintype = Constants.LOGIN_SINA;// 表示新浪微博登陆
					new CheckTask(logintype, openUid).execute();

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
		case R.id.ivQQLogin:
			if (SharedPrefUtil.checkQQBind(this)) {
				String openUid = SharedPrefUtil.getQQOpenid(this);
				if (NetUtil.checkNet(this)) {
					logintype = Constants.LOGIN_QQ;
					new CheckTask(logintype, openUid).execute();

				} else {
					showShortToast(R.string.NoSignalException);
				}
			} else {
				logintype = Constants.LOGIN_QQ;
				IUiListener listener = new BaseUiListener(logintype);
				mTencent.login(this, SCOPE, listener);
			}
			break;
		case R.id.tvRegisterMaoMao:
			openActivity(RegisterActivity.class);
			break;

		case R.id.btn:
			openActivity(BarListActivity.class);
		default:
			break;
		}

	}

	/**
	 * 登陆
	 * 
	 * @author Zhouyong
	 */
	public class LoginTask extends AsyncTask<Void, Void, JSONObject> {
		private String userName;
		private String passWord;
		private int logintype;
		private String nickName;

		private String openid;
		private boolean isThirdLogin = false;

		/**
		 * @param nickName
		 * @param LoginWay
		 * @param openUid
		 */
		public LoginTask(String nickName, int logintype, String openUid, boolean isThirdLogin) {
			super();
			this.openid = openUid;
			this.logintype = logintype;
			this.isThirdLogin = isThirdLogin;
			this.nickName = nickName;
		}

		/**
		 * @param LoginWay
		 * @param commonLogin
		 * @param userName
		 * @param passWord
		 */
		public LoginTask(int logintype, String userName, String passWord) {
			super();
			this.passWord = passWord;
			this.userName = userName;
			this.logintype = logintype;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(LoginActivity.this);
			}
			pd.setMessage("正在登陆中...");
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				if (isThirdLogin) {
					return new BusinessHelper().thirdLogin(nickName, logintype, openid);

				} else {
					return new BusinessHelper().login(logintype, userName, passWord);
				}
			} catch (SystemException e) {
				e.printStackTrace();
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
						int uid = 0;
						if (userJson.has("id")) {
							uid = userJson.getInt("id");
						}
						SharedPrefUtil.setUid(LoginActivity.this, uid);
						showShortToast("登录成功");
						// finish();
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
	 * 检查接口
	 * 
	 * @author Zhouyong
	 */

	public class CheckTask extends AsyncTask<Void, Void, JSONObject> {
		private int logintype;
		private String openUid;

		/**
		 * @param logintype
		 * @param openUid
		 */
		public CheckTask(int logintype, String openUid) {
			this.logintype = logintype;
			this.openUid = openUid;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().check(logintype, openUid);
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
					if (status == Constants.REQUEST_FAILD) {
						
						LayoutInflater inflater = getLayoutInflater();
						View view = inflater.inflate(R.layout.dialog_ninkname_, null); //
						TextView tvDialogMsg = (TextView) view.findViewById(R.id.tvDialogMsg); // 取得布局文件的控件
						final EditText etNickName = (EditText) view.findViewById(R.id.etNickName); // 取得布局文件的控件
						Button btnDialogLeft = (Button) view.findViewById(R.id.btnDialogLeft);
						btnDialogLeft.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								nickName = etNickName.getText().toString().trim();
								if (TextUtils.isEmpty(nickName)) {
									showShortToast("请输入昵称");
								} else {
									new LoginTask(nickName, logintype, openUid, true).execute();
								}

							}
						});
						Button btnDialogRight = (Button) view.findViewById(R.id.btnDialogRight);
						btnDialogRight.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});
						btnDialogRight.setText("取消");

						tvDialogMsg.setText("没有昵称无法第三方登录，请输入昵称");
						final Dialog dialog = new Dialog(LoginActivity.this, R.style.dialog); // 取得style的文件

						dialog.setContentView(view); // 将取得布局文件set进去
						dialog.show(); // 显示
						WindowManager windowManager = getWindowManager();
						Display display = windowManager.getDefaultDisplay();
						WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
						lp.width = (int) (display.getWidth() - 30); // 设置宽度

						dialog.getWindow().setAttributes(lp);

					} else {
						new LoginTask(nickName, logintype, openUid, true).execute();
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
				SharedPrefUtil.setQQInfo(LoginActivity.this, access_token, expires_in, openid, currTime);
				// new LoginTask(logintype, openid, true).execute();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.REQUEST_CODE_BIND_WEIBO:
			if (resultCode == RESULT_OK) {
				String uid = SharedPrefUtil.getWeiboUid(LoginActivity.this);
				logintype = Constants.LOGIN_SINA;
				// new LoginTask(logintype, uid, true).execute();
			}
			break;
		case Constants.REQUEST_CODE_REGISTER:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		default:
			if (mTencent == null) {
				return;
			}
			if (!mTencent.onActivityResult(requestCode, resultCode, data)) {

			}
			break;
		}
	}

}
