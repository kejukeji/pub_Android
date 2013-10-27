/**
 * 
 */
package com.maoba.activity.personalnfo;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;

/**
 * 个人信息修改界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-23 下午5:10:47
 */
public class PersonalInfoActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft, ibRight;
	private TextView tvRight;
	private TextView tvTitle;

	private ImageView ivUserImage;
	private EditText edNickName, edBirthday, edSex, edSignature, edAddress, edModificationPassword;

	private LinearLayout viewBirthday, viewSex;

	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 1;

	private int login_type;
	private String open_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_info);
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		tvRight = (Button) this.findViewById(R.id.tvRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		ivUserImage = (ImageView) this.findViewById(R.id.ivUserImage);
		edNickName = (EditText) this.findViewById(R.id.edNickName);
		edBirthday = (EditText) this.findViewById(R.id.edBirthday);
		edSex = (EditText) this.findViewById(R.id.edSex);
		edSignature = (EditText) this.findViewById(R.id.edSignature);
		edAddress = (EditText) this.findViewById(R.id.edAddress);
		edModificationPassword = (EditText) this.findViewById(R.id.edModificationPassword);

		viewBirthday = (LinearLayout) this.findViewById(R.id.viewBirthday);
		viewSex = (LinearLayout) this.findViewById(R.id.viewSex);
		// 生日的选择
		viewBirthday.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showDialog(DATE_DIALOG_ID);
				}
				return true;
			}
		});
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

	}

	private void fillData() {
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);
		ibRight.setVisibility(View.GONE);
		tvRight.setText("提交");
		tvRight.setOnClickListener(this);
		tvTitle.setText("个人资料");

		viewSex.setOnClickListener(this);

		// Intent intent = this.getIntent();
		// Bundle bundle = intent.getExtras();
		// login_type = bundle.getInt("logintype");
		// open_id = bundle.getString("openUid");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.tvRight:
			String nickName = edNickName.getText().toString().trim();
			String birthday = edBirthday.getText().toString().trim();
			String sex = edSex.getText().toString().trim();
			String signature = edSignature.getText().toString().trim();
			String address = edAddress.getText().toString().trim();
			break;
		case R.id.viewSex:
			final CharSequence[] items = { "男", "女", };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("请选择男女");
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
				}
			}).setNegativeButton("取消", null).show();
			// AlertDialog alert = builder.create();
			break;
		default:
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	private void updateDisplay() {
		StringBuilder sb = new StringBuilder();
		sb.append(mYear).append("-");
		if (mMonth + 1 < 10) {
			sb.append(0);
		}
		sb.append(mMonth + 1).append("-");
		if (mDay < 10) {
			sb.append(0);
		}
		sb.append(mDay);
		edBirthday.setText(sb);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);
			Calendar current = Calendar.getInstance();
			if (c.compareTo(current) > 0) {
				showShortToast("出生日期不能大于当前日期哦");
				return;
			}
			updateDisplay();
		}
	};

}
