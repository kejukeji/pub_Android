package com.keju.maomao.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.keju.maomao.CommonApplication;
import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.bean.SortModelBean;
import com.keju.maomao.db.DataBaseAdapter;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

public class LogoActivity extends BaseActivity {
	private View viewLogo;
	/**
	 * 数据库操作对象
	 */
	private DataBaseAdapter dba;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		
		dba = ((CommonApplication) getApplicationContext()).getDbAdapter();
		
		viewLogo = findViewById(R.id.viewLogo);
		if (NetUtil.checkNet(this)) {
			new GetCityTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}
		animation();
	}

	/**
	 * 跳转；
	 */
	private void animation() {
		AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
		aa.setDuration(1000);
		viewLogo.startAnimation(aa);
			aa.setAnimationListener(new AnimationListener() {
				public void onAnimationEnd(Animation arg0) {
					if (SharedPrefUtil.isFistLogin(LogoActivity.this)) {
						startActivity(new Intent(LogoActivity.this, FirstStartActivity.class));
						SharedPrefUtil.setFistLogined(LogoActivity.this);
					}else{
						if (SharedPrefUtil.isLogin(LogoActivity.this)) {
//							if(SharedPrefUtil.getCityName(LogoActivity.this)==null){
//								startActivity(new Intent(LogoActivity.this, CityChangActivity.class));
//							}else{
//							}
							startActivity(new Intent(LogoActivity.this, MainActivity.class));
							LogoActivity.this.finish();
						} else {
							startActivity(new Intent(LogoActivity.this, LoginActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							LogoActivity.this.finish();
						}
					}
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
			}
		});
	}
	/**
	 * 获取所有城市
	 * 
	 */
	private class GetCityTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {
				return new BusinessHelper().getCity();
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
						JSONArray cityArr = result.getJSONArray("city");
						if (cityArr != null) {
							ArrayList<SortModelBean> cityBean = (ArrayList<SortModelBean>) SortModelBean
									.constractList(cityArr);
							dba.bantchCitys(cityBean);
						}
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
}