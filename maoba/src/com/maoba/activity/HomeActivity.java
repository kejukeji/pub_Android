package com.maoba.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.util.AndroidUtil;

public class HomeActivity extends BaseActivity {
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showAlertDialog(R.string.msg, R.string.logout, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					AndroidUtil.exitApp(HomeActivity.this);
				}
			}, null, null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
