package com.maoba.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.maoba.CommonApplication;
import com.maoba.R;

public class LogoActivity extends Activity {
	private View viewLogo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		viewLogo = findViewById(R.id.viewLogo);
		animation();
		((CommonApplication) getApplication()).addActivity(this);
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
				startActivity(new Intent(LogoActivity.this, LoginActivity.class));
				finish();
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
	}
}