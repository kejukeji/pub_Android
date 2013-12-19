/**
 * 
 */
package com.keju.maomao.activity.friendpersonalcenter;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 喝一杯界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-12-16 下午3:40:38
 */
public class SendInviteActivity extends BaseActivity implements OnClickListener {

	private ImageButton ibLeft;
	private Button btnRight;
	private TextView tvTitle;

	private Button btnSendInvite;
	
	private int friendId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_drink);
		
		friendId = (int) getIntent().getExtras().getInt(Constants.EXTRA_DATA);
		
		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		btnSendInvite = (Button) this.findViewById(R.id.btnSendInvite);
		btnSendInvite.setOnClickListener(this);

	}

	private void fillData() {
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		btnRight.setOnClickListener(this);

		tvTitle.setText("喝一杯");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.btnSendInvite:
			if (NetUtil.checkNet(SendInviteActivity.this)) {
				new SendInviteTask().execute();
			} else {
				showShortToast(R.string.NoSignalException);
			}
			break;
		default:
			break;
		}

	}

	/***
	 * 
	 * 发送邀请接口
	 * 
	 */
	private class SendInviteTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd("正在邀请..");
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int userId = SharedPrefUtil.getUid(SendInviteActivity.this);
			try {
				return new BusinessHelper().sendInvite(userId,friendId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if(result!=null){
				try {
					int status = result.getInt("status");
					if(status==Constants.REQUEST_SUCCESS){
						showShortToast("邀约成功");
					}else{
						showShortToast("邀约失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

}
