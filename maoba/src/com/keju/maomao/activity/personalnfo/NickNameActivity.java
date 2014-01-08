package com.keju.maomao.activity.personalnfo;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.util.StringUtil;

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
        nickname = getIntent().getExtras().getString(Constants.EXTRA_DATA);
		findView();
		fillData();

	}

	private void findView() {
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		edNickname = (EditText) this.findViewById(R.id.ednickname);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
     
		edNickname.setText(nickname);
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
			if(StringUtil.isBlank(nickname)){
				showShortToast("输入新昵称才可以修改哦");
				return;
			}
			Intent nicknameIntent = new Intent();
			nicknameIntent.putExtra("NICKNAMEINPUT", nickname);
			setResult(Activity.RESULT_OK, nicknameIntent);
			
			String sex = "";
			String signature = "";
			String newPassword = "";
			String birthday = "";
			if (NetUtil.checkNet(NickNameActivity.this)) {
				new personInfoAddTask(nickname, birthday, sex, signature,newPassword).execute();
			}
			
		default:
			break;
		}
	}
	
	
	/**
	 * 用户修改或添加个人资料
	 * 
	 * */
	private class personInfoAddTask extends AsyncTask<Void, Void, JSONObject> {
		private String nickName;
		private String birthday;
		private String sex;
		private String signature;
		private String newPassword;
		private String provinceId;
		private String cityId;
		private File avatarFile = null;

		/**
		 * @param nickName
		 * @param birthday
		 * @param sex
		 * @param signature
		 * @param address
		 * @param newPassword
		 */
		public personInfoAddTask(String nickName, String birthday, String sex, String signature,
				String newPassword) {

			this.nickName = nickName;
			this.birthday = birthday;
			this.sex = sex;
			this.signature = signature;
			this.newPassword = newPassword;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String openId = null;
			int loginType = SharedPrefUtil.getLoginType(NickNameActivity.this);
			if (loginType == Constants.LOGIN_QQ) {
				openId = SharedPrefUtil.getQQOpenid(NickNameActivity.this);
			} else if (loginType == Constants.LOGIN_SINA) {
				openId = SharedPrefUtil.getWeiboUid(NickNameActivity.this);
			} else {
			}
			int userId = SharedPrefUtil.getUid(NickNameActivity.this);
			String password = SharedPrefUtil.getPassword(NickNameActivity.this);
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, "",
							signature,newPassword,"","","",avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday, "",
							signature,"","","", avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			if (result != null) {
				try {
					int status = result.getInt("status");
					if (status == Constants.REQUEST_SUCCESS) {
						showShortToast("昵称设置成功");
						finish();
					} else {
						showShortToast("昵称设置失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				// showShortToast(result.getString("message"));
				showShortToast("服务连接失败");
			}
		}
	}
	
}
