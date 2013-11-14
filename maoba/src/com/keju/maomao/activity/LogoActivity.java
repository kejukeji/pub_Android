package com.keju.maomao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.util.SharedPrefUtil;

public class LogoActivity extends BaseActivity {
	private View viewLogo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		viewLogo = findViewById(R.id.viewLogo);
		animation();
	}

	/**
	 * 跳转；
	 */
	private void animation() {
		AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
		aa.setDuration(2000);
		viewLogo.startAnimation(aa);
			aa.setAnimationListener(new AnimationListener() {
				public void onAnimationEnd(Animation arg0) {
					if(SharedPrefUtil.isLogin(LogoActivity.this)){
						startActivity(new Intent(LogoActivity.this, MainActivity.class));
					}else{
						startActivity(new Intent(LogoActivity.this, LoginActivity.class));
					}
					finish();
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
	}
}