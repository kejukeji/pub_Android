package com.maoba.activity.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;
import com.maoba.util.StringUtil;

/**
 * 意见反馈
 * 
 * @author lhm
 * @date 创建时间：2013-10-31
 */
public class SettingFeedbackActivity extends BaseActivity implements OnClickListener, TextWatcher {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private EditText etFeedback;
	private TextView tvNum;
	TextView hasnum;// 用来显示剩余字数
	int num = 140;// 限制的最大字数　

	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_feedback);
		findView();
		fillData();
	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		tvTitle.setText("意见反馈");
		tvNum.setText(0 + "");
		btnRight.setText("发送");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);

	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);

		ibLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		etFeedback = (EditText) findViewById(R.id.etFeedback);
		etFeedback.addTextChangedListener(this);
		tvNum = (TextView) findViewById(R.id.tvNum);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		default:
		case R.id.btnRight:
			String feedBackContent = etFeedback.getText().toString().trim();
			if (StringUtil.isBlank(feedBackContent)) {
				showShortToast("请输入你要反馈的内容");
				return;
			} else {
				if (NetUtil.checkNet(SettingFeedbackActivity.this)) {
					new FeedBack(feedBackContent).execute();
				} else {
					showShortToast(R.string.NoSignalException);
				}
			}
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {
		int number = editable.length();
		tvNum.setText("" + number);
		// selectionStart = tvNum.getSelectionStart();
		selectionEnd = etFeedback.getSelectionEnd();
		if (temp.length() > num) {
			editable.delete(num, number);
			int tempSelection = selectionEnd;
			etFeedback.setText(editable);
			etFeedback.setSelection(tempSelection);// 设置光标在最后

			showShortToast("最多可输入140个字..");

		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;
	}

	/**
	 * 
	 * 反馈接口
	 * 
	 * */

	private class FeedBack extends AsyncTask<Void, Void, JSONObject> {
		private String feedBackContent;

		/**
		 * @param feedBackContent
		 */
		public FeedBack(String feedBackContent) {
			this.feedBackContent = feedBackContent;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在提交...");
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(SettingFeedbackActivity.this);
			try {
				return new BusinessHelper().getFeedBack(uid, feedBackContent);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						showShortToast("感谢你为我们提交了宝贵意见");
						finish();
					} else {
						showShortToast("意见反馈失败");
					}
				} catch (JSONException e) {
					showShortToast(R.string.json_exception);
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

}
