package com.keju.maomao.activity;

import android.content.DialogInterface;
import android.view.KeyEvent;

import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.util.AndroidUtil;

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
