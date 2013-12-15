/**
 * 
 */
package com.keju.maomao.activity.news;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;

/**
 * 消息界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-30 上午11:50:03
 */
public class NewsActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLift;
	private TextView tvTitle;

	private LinearLayout viewPrivateNews, viewSystemNews;
	private TextView tvSystemNews, tvPrivateNews;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		findView();
		fillData();
	}

	private void findView() {
		ibLift = (ImageButton) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		tvSystemNews = (TextView) this.findViewById(R.id.tvSystemNews);
		tvPrivateNews = (TextView) this.findViewById(R.id.tvPrivateNews);
		viewSystemNews = (LinearLayout) this.findViewById(R.id.viewSystemNews);
		viewPrivateNews = (LinearLayout) this.findViewById(R.id.viewPrivateNews);
	}

	private void fillData() {
		ibLift.setImageResource(R.drawable.ic_btn_left);
		ibLift.setOnClickListener(this);
		tvTitle.setText("消息");

		viewSystemNews.setOnClickListener(this);
		viewPrivateNews.setOnClickListener(this);

		if (NetUtil.checkNet(NewsActivity.this)) {
			new SysNewsListTask().execute();
		} else {
			showShortToast(R.string.NoSignalException);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			break;
		case R.id.viewSystemNews:
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, 0);
			openActivity(SystemNewsListActivity.class,b);
			break;
		case R.id.viewPrivateNews:
			openActivity(PrivateNewsListActivity.class);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取系统和私信信息条数
	 * 
	 * */

	private class SysNewsListTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(NewsActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int uid = SharedPrefUtil.getUid(NewsActivity.this);
			try {
				return new BusinessHelper().getSysLetter1(uid);
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				if (result.has("status")) {
					try {
						int status = result.getInt("status");
						if (status == Constants.REQUEST_SUCCESS) {
							int sysMessageNum = result.getInt("system_count");
							tvSystemNews.setText("" + sysMessageNum);
							int priMessageNum = result.getInt("direct_count");
							tvPrivateNews.setText("" + priMessageNum);
						}
					} catch (JSONException e) {
						showShortToast("服务器连接失败");
					}
				} else {
					showShortToast("Json解析错误");
				}

			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		tvSystemNews.setText("");
		tvPrivateNews.setText("");
	}

}
