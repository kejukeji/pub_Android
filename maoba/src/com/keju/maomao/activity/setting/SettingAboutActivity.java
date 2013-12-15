package com.keju.maomao.activity.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;

/**
 * 关于冒冒
 * 
 * @author lhm
 * @date 创建时间：2013-10-30
 */
public class SettingAboutActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle,tvUrl;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_about);

		findView();
		fillData();
	}

	@SuppressLint("ResourceAsColor")
	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		tvTitle.setText("关于冒冒");
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibLeft.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);
		tvUrl=(TextView) findViewById(R.id.tvUrl);
		tvUrl.setOnClickListener(this);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.tvUrl:
			//tvUrl.setTextColor(R.color.url_down);
			Uri uri =Uri.parse("http://www.maobake.com/"); 

			 Intent it = new Intent(Intent.ACTION_VIEW,uri); 

			 startActivity(it); 
			break;
		default:
			break;
		}
	}

}
