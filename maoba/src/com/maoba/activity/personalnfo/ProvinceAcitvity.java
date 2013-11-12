/**
 * 
 */
package com.maoba.activity.personalnfo;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.ProvinceBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;

/**
 * 省选择界面
 * 
 * @author zhouyong
 * @data 创建时间：2013-11-11 下午8:21:15
 */
public class ProvinceAcitvity extends BaseActivity implements OnClickListener {

	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;

	private ListView listview;
	private Adapter adapter;
	private ArrayList<ProvinceBean> provinceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.province_list);
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

		provinceList = new ArrayList<ProvinceBean>();
		adapter = new Adapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(itemListener);
		
		if (NetUtil.checkNet(this)) {
			new GetProvinceTask().execute();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CHOOSE_AREA){
			finish();
		}
	};
	/**
	 * listview点击事件
	 */
	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= provinceList.size()) {
				return;
			}
			ProvinceBean bean = provinceList.get(arg2);
			Bundle b = new Bundle();
			b.putSerializable(Constants.EXTRA_DATA, bean.getList());
			b.putInt("province", bean.getProvinceId());
			Intent intent = new Intent(ProvinceAcitvity.this,CityActivity.class);
			intent.putExtras(b);
			startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_AREA);
		}
	};

	/**
	 * 
	 * 获取地区选择
	 * 
	 * */

	private class GetProvinceTask extends AsyncTask<Void, Void, ResponseBean<ProvinceBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showPd(R.string.loading);
		}

		@Override
		protected ResponseBean<ProvinceBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getArea();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<ProvinceBean> result) {
			super.onPostExecute(result);
			dismissPd();
			if (result.getStatus() != Constants.REQUEST_FAILD) {
				List<ProvinceBean> tempList = result.getObjList();
				if (tempList.size() > 0) {
					provinceList.addAll(tempList);
					adapter.notifyDataSetChanged(); // 通知更新
				}
			}
		}

	}

	/**
	 * 
	 * 筛选地区适配器
	 * 
	 * */
	private class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return provinceList.size();
		}

		@Override
		public Object getItem(int position) {
			return provinceList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProvinceBean bean = provinceList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.popu_data_picker_item, null);
				holder.tvScreenArea = (TextView) convertView.findViewById(R.id.tvScreenArea);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvScreenArea.setText(bean.getProvinceName());
			return convertView;
		}

		class ViewHolder {
			private TextView tvScreenArea;
		}

	}

}
