package com.maoba.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;

/**
 * @author lhm
 * @date 创建时间：2013-10-30
 */
public class SettingNoticeActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_notice);
		findView();
		fillData();
	}

	private void fillData() {
		// TODO Auto-generated method stub
		tvTitle.setText("消息推送通知");
	}

	private void findView() {
		// TODO Auto-generated method stub
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		
		default:
			break;
		}
	}

}
