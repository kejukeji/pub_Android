package com.keju.maomao.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * @author ZhouYongJian
 * @date 创建时间：2013-10-30
 */
public class SettingNoticeActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private LinearLayout viewShake;
	private LinearLayout viewRing;
	private LinearLayout viewChoiseRing;
	private CheckBox cbShake;
	private CheckBox cbNewMessage;
	private CheckBox cbRing;// 铃声
	private ImageView ivChoiseRing;
	private String mCustomRingtone;

	// 保存铃声的Uri的字符串形式
	private String mRingtoneUri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_notice);

		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		viewChoiseRing = (LinearLayout) this.findViewById(R.id.viewChoiseRing);
		viewRing = (LinearLayout) this.findViewById(R.id.viewRing);
		viewShake = (LinearLayout) this.findViewById(R.id.viewShake);
		cbNewMessage = (CheckBox) this.findViewById(R.id.cbNewMessage);
		cbRing = (CheckBox) this.findViewById(R.id.cbRing);
		cbShake = (CheckBox) this.findViewById(R.id.cbShake);
		ivChoiseRing = (ImageView) this.findViewById(R.id.ivChoiseRing);
		// 判断是否接受信息的铃声和振动选择
		if (SharedPrefUtil.getNewLetter(SettingNoticeActivity.this)) {
			// 判断是否振动
			if (SharedPrefUtil.getVibrate(SettingNoticeActivity.this)) {
				cbShake.setButtonDrawable(R.drawable.btn_check_on_normal);
			} else {
				cbShake.setButtonDrawable(R.drawable.btn_check_off_normal);
			}
			// 判断铃声
			if (SharedPrefUtil.getPlayRing(SettingNoticeActivity.this)) {
				viewChoiseRing.setVisibility(View.VISIBLE);
				cbRing.setButtonDrawable(R.drawable.btn_check_on_normal);
			} else {
				viewChoiseRing.setVisibility(View.GONE);
				cbRing.setButtonDrawable(R.drawable.btn_check_off_normal);
			}
			cbNewMessage.setButtonDrawable(R.drawable.btn_check_on_normal);
			viewRing.setVisibility(View.VISIBLE);
			viewShake.setVisibility(View.VISIBLE);

		} else {
			cbNewMessage.setButtonDrawable(R.drawable.btn_check_off_normal);
			viewChoiseRing.setVisibility(View.GONE);
			viewRing.setVisibility(View.GONE);
			viewShake.setVisibility(View.GONE);

		}

	}

	private void fillData() {
		tvTitle.setText("新消息提醒");
		// 接受新消息
		viewChoiseRing.setOnClickListener(this);
		cbNewMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPrefUtil.setNewLetter(SettingNoticeActivity.this, isChecked);
				if (isChecked) {
					cbNewMessage.setButtonDrawable(R.drawable.btn_check_on_normal);
					if (SharedPrefUtil.getPlayRing(SettingNoticeActivity.this)) {
						viewChoiseRing.setVisibility(View.VISIBLE);
					}
					viewRing.setVisibility(View.VISIBLE);
					viewShake.setVisibility(View.VISIBLE);

					isChecked = false;
				} else {
					cbNewMessage.setButtonDrawable(R.drawable.btn_check_off_normal);
					viewRing.setVisibility(View.GONE);
					viewShake.setVisibility(View.GONE);
					viewChoiseRing.setVisibility(View.GONE);

				}
			}
		});
		// 铃声
		cbRing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPrefUtil.setPlayRing(SettingNoticeActivity.this, isChecked);
				if (isChecked) {
					viewChoiseRing.setVisibility(View.VISIBLE);
					cbRing.setButtonDrawable(R.drawable.btn_check_on_normal);
				} else {
					viewChoiseRing.setVisibility(View.GONE);
					cbRing.setButtonDrawable(R.drawable.btn_check_off_normal);
				}
			}
		});
		// 振动
		cbShake.setOnClickListener(this);
		cbShake.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPrefUtil.setVibrate(SettingNoticeActivity.this, isChecked);
				if (isChecked) {
					cbShake.setButtonDrawable(R.drawable.btn_check_on_normal);
				} else {
					cbShake.setButtonDrawable(R.drawable.btn_check_off_normal);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.viewChoiseRing:
			doPickRingtone();
			break;
		default:
			break;
		}
	}

	/***
	 * 调用系统铃声
	 */
	private void doPickRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		// Allow user to pick 'Default'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		// Show only ringtones
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
		// Don't show 'Silent'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

		Uri ringtoneUri;
		if (mRingtoneUri != null) {
			ringtoneUri = Uri.parse(mRingtoneUri);
		} else {
			// Otherwise pick default ringtone Uri so that something is
			// selected.
			ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}

		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

		// Launch!
		// startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
		startActivityForResult(intent, Constants.REQUEST_CODE_PICK_RINGTONE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case Constants.REQUEST_CODE_PICK_RINGTONE: {
			Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			handleRingtonePicked(pickedUri);
			break;
		}
		}
	}

	private void handleRingtonePicked(Uri pickedUri) {
		if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
			mRingtoneUri = null;
		} else {
			mRingtoneUri = pickedUri.toString();
			SharedPrefUtil.setRingUrl(SettingNoticeActivity.this, mRingtoneUri);
		}
	}

}
