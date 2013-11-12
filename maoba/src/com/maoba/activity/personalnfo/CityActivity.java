/**
 * 
 */
package com.maoba.activity.personalnfo;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.CityBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;
import com.maoba.util.SharedPrefUtil;

/**
 * 城市选择界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-11 下午8:19:30
 */
public class CityActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;

	private ListView listview;
	private CityAdapter adapter;
	private ArrayList<CityBean> cityListBean;
	private int provinceId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.province_list);
		provinceId = getIntent().getExtras().getInt("province");
		cityListBean = (ArrayList<CityBean>) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);
		findView();
		fillData();

	}

	private void findView() {

		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		listview = (ListView) this.findViewById(R.id.provinceList);

	}

	private void fillData() {
		ibLeft.setOnClickListener(this);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		btnRight.setOnClickListener(this);
		tvTitle.setText("地区选择");

		adapter = new CityAdapter(cityListBean);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(itemListener);
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
	 * listview点击事件
	 */
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String nickname = "";
			String sex = "";
			String signature = "";
			String address = "";
			String newPassword = "";
			String birthday = "";

			CityBean bean = cityListBean.get(arg2);

			if (NetUtil.checkNet(CityActivity.this)) {
				new personInfoAddTask(nickname, birthday, sex, signature, address, newPassword, provinceId,
						bean.getCityId()).execute();
			}

		}
	};

	/**
	 * 
	 * 地区适配器
	 * 
	 * */
	private class CityAdapter extends BaseAdapter {
		private ArrayList<CityBean> cityListBean;

		/**
		 * @param cityListBean
		 */
		public CityAdapter(ArrayList<CityBean> cityListBean) {
			this.cityListBean = cityListBean;
		}

		@Override
		public int getCount() {
			return cityListBean.size();
		}

		@Override
		public Object getItem(int position) {
			return cityListBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CityBean bean = cityListBean.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.popu_data_picker_item, null);
				holder.tvScreenArea = (TextView) convertView.findViewById(R.id.tvScreenArea);
				holder.creenArealine = (View) convertView.findViewById(R.id.creenArealine);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvScreenArea.setText(bean.getCityName());
			return convertView;
		}

		class ViewHolder {
			private TextView tvScreenArea;
			private View creenArealine;
		}

	}

	/**
	 * 更新个人地区信息
	 * 
	 * @author zhuoyong
	 * @data 创建时间：2013-11-12 下午3:28:18
	 */
	private class personInfoAddTask extends AsyncTask<Void, Void, JSONObject> {
		private String nickName;
		private String birthday;
		private String sex;
		private String signature;
		private String address;
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
		 * @param cityId
		 * @param provinceId
		 */
		public personInfoAddTask(String nickName, String birthday, String sex, String signature, String address,
				String newPassword, int provinceId, int cityId) {

			this.nickName = nickName;
			this.birthday = birthday;
			this.sex = sex;
			this.signature = signature;
			this.address = address;
			this.newPassword = newPassword;
			this.provinceId = String.valueOf(provinceId);
			this.cityId = String.valueOf(cityId);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			int loginType = SharedPrefUtil.getLoginType(CityActivity.this);
			int userId = SharedPrefUtil.getUid(CityActivity.this);
			String openId = SharedPrefUtil.getWeiboUid(CityActivity.this);
			String password = SharedPrefUtil.getPassword(CityActivity.this);
			int sex = 0;
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, sex,
							signature, address, newPassword, provinceId, cityId, avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday, sex,
							signature, address, avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			dismissPd();
			try {
				if (result.getInt("status") == Constants.REQUEST_SUCCESS) {
					setResult(RESULT_OK);
					showShortToast("个人资料设置成功");
					finish();
				} else {
					showShortToast("个人资料设置失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
