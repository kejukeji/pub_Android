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

public class ChangingPasswordActivity extends Activity implements OnClickListener{
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private EditText edPassword,edNewPassword;
	private String newPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changing_password);
		edPassword=(EditText)this.findViewById(R.id.edInputPassword);
		edNewPassword=(EditText)this.findViewById(R.id.edInputNewPassword);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setText("保存");
		btnRight.setOnClickListener(this);
		tvTitle.setText("密码修改");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			/*nickname=edNickname.getText().toString();
			Intent nicknameIntent= new Intent();
			nicknameIntent.putExtra("NICKNAMEINPUT",nickname);
			setResult(Activity.RESULT_OK, nicknameIntent);*/
			newPassword=edNewPassword.getText().toString();
			Intent changingPasswordIntent=new Intent();
			changingPasswordIntent.putExtra("NEWPASSWORD", newPassword);
			setResult(Activity.RESULT_OK, changingPasswordIntent);
			finish();
		default:
			break;
		}
	}

}
