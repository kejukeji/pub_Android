package com.keju.maomao.activity.setting;

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

import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;

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
	private CheckBox cbRing;
	private ImageView ivChoiseRing;
	private boolean isReceive = true;
	private boolean isRing = false;
	private String mCustomRingtone;

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
	}

	private void fillData() {
		tvTitle.setText("新消息提醒");
		viewChoiseRing.setOnClickListener(this);
		cbNewMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isReceive = true;
					viewRing.setVisibility(View.VISIBLE);
					viewShake.setVisibility(View.VISIBLE);
					if (isReceive && isRing) {
						viewChoiseRing.setVisibility(View.VISIBLE);
					}
				} else {
					isReceive = false;
					viewRing.setVisibility(View.GONE);
					viewShake.setVisibility(View.GONE);
					viewChoiseRing.setVisibility(View.GONE);

				}
			}
		});
		cbRing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isRing = true;
					if (isReceive && isRing) {
						viewChoiseRing.setVisibility(View.VISIBLE);
					}
				} else {
					isRing = false;
					viewChoiseRing.setVisibility(View.GONE);
				}
			}
		});
		cbShake.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.viewChoiseRing:

			break;
		default:
			break;
		}
	}

}
