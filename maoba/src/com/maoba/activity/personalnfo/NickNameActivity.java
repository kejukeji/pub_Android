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

/**
 * 昵称界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class NickNameActivity extends BaseActivity implements OnClickListener {
	private EditText edNickname;
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private String nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nick_name);

		findView();
		fillData();

	}

	private void findView() {
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edNickname = (EditText) this.findViewById(R.id.ednickname);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("更改名字");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			nickname = edNickname.getText().toString();
			Intent nicknameIntent = new Intent();
			nicknameIntent.putExtra("NICKNAMEINPUT", nickname);
			setResult(Activity.RESULT_OK, nicknameIntent);
			finish();
		default:
			break;
		}
	}

}
