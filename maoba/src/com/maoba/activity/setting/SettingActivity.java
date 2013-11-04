package com.maoba.activity.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoba.R;
import com.maoba.activity.LoginActivity;
import com.maoba.activity.base.BaseActivity;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * 设置界面
 * 
 * @author lhm
 * @date 创建时间：2013-10-30
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private LinearLayout rlNotice, rlAbout, rlVersionTest, rlFeedback;
	private Button btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_page);
		findView();
		fillData();

	}

	private void fillData() {
		tvTitle.setText("设置");

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		rlNotice = (LinearLayout) findViewById(R.id.rlNotice);
		rlNotice.setOnClickListener(this);
		rlAbout = (LinearLayout) findViewById(R.id.rlAbout);
		rlAbout.setOnClickListener(this);
		rlVersionTest = (LinearLayout) findViewById(R.id.rlVersionTest);
		rlVersionTest.setOnClickListener(this);
		rlFeedback = (LinearLayout) findViewById(R.id.rlFeedback);
		rlFeedback.setOnClickListener(this);
		btnLogout = (Button) this.findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.rlNotice:
			openActivity(SettingNoticeActivity.class);
			break;
		case R.id.rlVersionTest:
			if (NetUtil.checkNet(this)) {
				UmengUpdateAgent.update(this);
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

					@Override
					public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0:
							UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
							break;
						case 1: // has no update
							showShortToast("已经是最新版本");
							break;
						case 2: // none wifi
							showShortToast("没有wifi连接， 只在wifi下更新");
							break;
						case 3: // time out
							showShortToast("连接服务器超时");
							break;

						}
					}
				});
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		case R.id.rlAbout:
			openActivity(SettingAboutActivity.class);
			break;
		case R.id.rlFeedback:
			openActivity(SettingFeedbackActivity.class);
			break;
		case R.id.btnLogout:
			showAlertDialog("提示", "确定要注销登录吗？", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// MainActivity.rb_home.setChecked(true);
					// MainActivity.rb_my.setChecked(false);
					SharedPrefUtil.clearUserinfo(SettingActivity.this);
					SharedPrefUtil.setInfoComplete(SettingActivity.this, false);
					openActivity(LoginActivity.class);
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}, new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

				}
			});

			break;
		default:
			break;
		}
	}
}
