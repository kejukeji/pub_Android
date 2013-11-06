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
 * 个人昵称修改界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-3 下午10:16:13
 */
public class PersonalizedSignatureActivity extends BaseActivity implements OnClickListener {
	private EditText edSignature;
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private String Signature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalized_signature);

		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edSignature = (EditText) this.findViewById(R.id.edpersonalsignature);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		btnRight.setOnClickListener(this);
		tvTitle.setText("个性签名");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:
			Signature = edSignature.getText().toString();
			Intent signatureIntent = new Intent();
			signatureIntent.putExtra("SIGNATUREINPUT", Signature);
			setResult(Activity.RESULT_OK, signatureIntent);
			finish();
		default:
			break;
		}
	}

}
