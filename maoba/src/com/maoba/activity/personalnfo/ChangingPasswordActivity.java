package com.maoba.activity.personalnfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.util.SharedPrefUtil;

/**
 * 密码重置界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class ChangingPasswordActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private EditText edPassword, edNewPassword;
	private String newPassword;
	private String passWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changing_password);
		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edPassword = (EditText) this.findViewById(R.id.edInputPassword);
		edNewPassword = (EditText) this.findViewById(R.id.edInputNewPassword);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("密码修改");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			/*
			 * nickname=edNickname.getText().toString(); Intent nicknameIntent=
			 * new Intent(); nicknameIntent.putExtra("NICKNAMEINPUT",nickname);
			 * setResult(Activity.RESULT_OK, nicknameIntent);
			 */
			passWord = edPassword.getText().toString().trim();
			newPassword = edNewPassword.getText().toString().trim();
			String userPassWord = SharedPrefUtil.getPassword(ChangingPasswordActivity.this);
			if (passWord.equals(userPassWord)) {
				Intent changingPasswordIntent = new Intent();
				changingPasswordIntent.putExtra("NEWPASSWORD", newPassword);
				setResult(Activity.RESULT_OK, changingPasswordIntent);
				finish();
			} else {
				showShortToast("你输入的旧密码不正确，请重新输入");
			}
		default:
			break;
		}
	}

}
