package com.maoba.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.maoba.CommonApplication;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.util.SharedPrefUtil;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

/**
 * 授权界面
 * 
 * @author Aizhimin
 * 
 */
public class AuthorizeActivity extends Activity {

	private final static String TAG = "AuthorizeActivity";

	// -------------------------sina_weibo
	// start-------------------------------------//
	private Oauth2AccessToken accessToken = null;
	private String mRedirectUrl;
	private static String WEIBO_APP_KEY;
	private static String WEIBO_APP_SECRET;

	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";

	private WeiboAuthListener mListener;
	/**
     * SsoHandler 仅当sdk支持sso时有效，
     */
    SsoHandler mSsoHandler;

	// -------------------------sina_weibo
	// end-------------------------------------//

	WebView mWebView;
	private String mUrl;// dialogUrl

	private View progress;

	private ProgressDialog mSpinner;

	String fromType;

	private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32973;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authorize);
		findView();
		fillData();
		// 添加到容器中
		((CommonApplication) getApplicationContext()).addActivity(this);

	}

	private void fillData() {
		fromType = getIntent().getExtras().getString(Constants.EXTRA_BIND_FROM);
		if (Constants.BIND_WEIBO.equals(fromType)) {
			authorizeWeibo(this, new AuthDialogListener());
		}
	}

	private void findView() {
		mWebView = (WebView) findViewById(R.id.weibo_webview);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);

		progress = this.findViewById(R.id.progress);
		progress.setVisibility(View.GONE);

		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("加载中...");

	}

	// --------------------------------------sina_weibo_start----------------------------//

	public void authorizeWeibo(Context context, final WeiboAuthListener listener){
		mRedirectUrl = Constants.WEIBO_REDIRECT_URL;
		WEIBO_APP_KEY = Constants.WEIBO_CONSUMER_KEY;	
        WEIBO_APP_SECRET = Constants.WEIBO_CONSUMER_SECRET;
//		startAuthDialog(context, listener);
        Weibo mWeibo = Weibo.getInstance(WEIBO_APP_KEY, mRedirectUrl);
        
		 /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        try {
            Class sso = Class.forName("com.weibo.sdk.android.sso.SsoHandler");
            mSsoHandler = new SsoHandler(this, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
        } catch (ClassNotFoundException e) {
        	startAuthDialog(context, listener);
        }
	}
	public void startAuthDialog(Context context, final WeiboAuthListener listener) {
		WeiboParameters params = new WeiboParameters();
//		CookieSyncManager.createInstance(context);
		startDialog(context, params, new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				if (null == accessToken) {
					accessToken = new Oauth2AccessToken();
				}
				accessToken.setToken(values.getString(KEY_TOKEN));
				accessToken.setExpiresIn(values.getString(KEY_EXPIRES));
				accessToken.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
				if (accessToken.isSessionValid()) {
					Log.d("Weibo-authorize",
							"Login Success! access_token=" + accessToken.getToken() + " expires="
									+ accessToken.getExpiresTime() + " refresh_token="
									+ accessToken.getRefreshToken());
					listener.onComplete(values);
				} else {
					Log.d("Weibo-authorize", "Failed to receive access token");
					listener.onWeiboException(new WeiboException("Failed to receive access token."));
				}
			}

			@Override
			public void onError(WeiboDialogError error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onError(error);
			}

			@Override
			public void onWeiboException(WeiboException error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onWeiboException(error);
			}

			@Override
			public void onCancel() {
				Log.d("Weibo-authorize", "Login canceled");
				listener.onCancel();
			}
		});
	}

	public void startDialog(Context context, WeiboParameters parameters, final WeiboAuthListener listener) {
		parameters.add("client_id", WEIBO_APP_KEY);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", mRedirectUrl);
		parameters.add("display", "mobile");

		if (accessToken != null && accessToken.isSessionValid()) {
			parameters.add(KEY_TOKEN, accessToken.getToken());
		}
		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error", "Application requires permission to access the Internet");
		} else {
			mUrl = url;
			mListener = listener;
			mWebView.setWebViewClient(new WeiboWebViewClient());
			mWebView.loadUrl(mUrl);
		}
	}

	/**
	 * 微博验证
	 * 
	 * @author Aizhimin
	 * 
	 */
	class AuthDialogListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			String sina_uid = values.getString("uid");
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String currTime = System.currentTimeMillis() + "";
			SharedPrefUtil.setWeiboInfo(AuthorizeActivity.this, sina_uid, token, expires_in, currTime);
			Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
			accessToken.setExpiresIn(expires_in);
			setResult(RESULT_OK);
			AuthorizeActivity.this.finish();
			Toast.makeText(AuthorizeActivity.this, R.string.account_bind_success, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(WeiboDialogError e) {
			Log.e(TAG, "Auth error : " + e.getMessage());
			Toast.makeText(AuthorizeActivity.this, R.string.weibo_bind_faild, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			AuthorizeActivity.this.finish();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Log.e(TAG, "Auth exception : " + e.getMessage());
		}

	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			// 待后台增加对默认重定向地址的支持后修改下面的逻辑
			if (url.startsWith(mRedirectUrl)) {
				handleRedirectUrl(view, url);
				return true;
			}
			// launch non-dialog URLs in a full browser
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode, failingUrl));
			// WeiboDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted URL: " + url);
			// google issue. shouldOverrideUrlLoading not executed
			if (url.startsWith(mRedirectUrl)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
			// progress.setVisibility(View.GONE);
			if (mSpinner != null && mSpinner.isShowing()) {
				mSpinner.dismiss();
			}

			// mBtnClose.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.VISIBLE);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			mListener.onComplete(values);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			mListener.onCancel();
		} else {
			mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
		}
	}

	// --------------------------------------sina_weibo_end----------------------------//

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);

	        /**
	         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
	         */
	        if (mSsoHandler != null) {
	            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	        }
	    }
}