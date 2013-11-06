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

public class NickNameActivity extends BaseActivity implements OnClickListener{
	private EditText edNickname;
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private String nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nick_name);
		edNickname=(EditText)this.findViewById(R.id.ednickname);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		
		btnRight.setText("保存");
		btnRight.setOnClickListener(this);
		tvTitle.setText("更改名字");
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.nick_name, menu);
//		return true;
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			nickname=edNickname.getText().toString();
			Intent nicknameIntent= new Intent();
			nicknameIntent.putExtra("NICKNAMEINPUT",nickname);
			setResult(Activity.RESULT_OK, nicknameIntent);
			finish();
		default:
			break;
		}
	}

}
