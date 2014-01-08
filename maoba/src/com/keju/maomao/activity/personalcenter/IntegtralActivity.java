/**
 * 
 */
package com.keju.maomao.activity.personalcenter;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keju.maomao.R;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.util.NetUtil;

/**
 * 我的积分和等级界面
 * 
 * @author zhuoyong
 * @data 创建时间：2014-1-6 下午2:26:06
 */
public class IntegtralActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLift;
	private TextView tvTitle;

	private TextView tvIntegral, tvEmpiricalValue, tvNextEmpirical;
	private ProgressBar pbEmpiricalValu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.integral_rule);

		findView();
		fillData();
	}

	private void findView() {
		ibLift = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		tvIntegral = (TextView) this.findViewById(R.id.tvIntegral);
		tvEmpiricalValue = (TextView) this.findViewById(R.id.tvEmpiricalValue);
		tvNextEmpirical = (TextView) this.findViewById(R.id.tvNextEmpirical);

		pbEmpiricalValu = (ProgressBar) this.findViewById(R.id.pbEmpiricalValu);

	}

	private void fillData() {
		ibLift.setImageResource(R.drawable.ic_btn_left);
		ibLift.setOnClickListener(this);
		tvTitle.setText("积分规则");

		if (NetUtil.checkNet(this)) {
			new IntegralAndEmpiricalTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * 获取经验值和积分
	 * 
	 * */
	private class IntegralAndEmpiricalTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}
	}

}
