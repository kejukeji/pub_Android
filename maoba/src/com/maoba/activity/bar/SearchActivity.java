/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;

/**
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-25 下午1:10:06
 */
public class SearchActivity extends BaseActivity implements OnClickListener {
	private ImageView ibLeft;
	private TextView tvRight;
	private TextView tvTitle;

	private LinearLayout viewHotKeywords;
	private EditText edSerarch;
	private ImageView ivSerarch;

	private ProgressDialog pd;
	private ArrayList<BarBean> hotList = new ArrayList<BarBean>();

	private int barId;// 酒吧的id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		findView();
		fillData();

	}

	private void findView() {
		ibLeft = (ImageView) this.findViewById(R.id.ibLeft);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		ivSerarch = (ImageView) this.findViewById(R.id.ivserarch);

		edSerarch = (EditText) this.findViewById(R.id.edsearch);
		viewHotKeywords = (LinearLayout) this.findViewById(R.id.viewhotkeywords);

	}

	private void fillData() {
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		ivSerarch.setOnClickListener(this);

		tvTitle.setText("酒吧搜索");

		if (NetUtil.checkNet(SearchActivity.this)) {
			new GetBarHotKeywordsTask().execute();
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
		case R.id.ivserarch:
			String content = edSerarch.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				showShortToast("请输入你要查询的酒吧关键字");
				return;
			}
			Bundle b = new Bundle();
			b.putString(Constants.EXTRA_DATA, content);
			openActivity(SearchListActivity.class, b);

			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 获取热点酒吧
	 * 
	 * */
	public class GetBarHotKeywordsTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(SearchActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getBarHotSearch();
			} catch (SystemException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseBean<BarBean> result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result != null) {
				if (result.getStatus() != Constants.REQUEST_FAILD) {
					hotList.addAll(result.getObjList());
					fillHotBarList(result.getObjList());
				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

	/**
	 * 填充热点酒吧数据
	 * 
	 * @param list
	 * 
	 */
	private void fillHotBarList(final List<BarBean> hotlist) {
		if (hotlist == null) {
			return;
		}
		for (int i = 0; i < hotlist.size(); i++) {
			final BarBean bean = hotlist.get(i);
			LinearLayout.LayoutParams paramItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			paramItem.rightMargin = 10;
			final View view = getLayoutInflater().inflate(R.layout.hotbar_item, null);
			view.setLayoutParams(paramItem);
			TextView tvHotBar = (TextView) view.findViewById(R.id.tvhotbar);

			tvHotBar.setText(bean.getBar_Name());
			barId = bean.getBar_id();

			tvHotBar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Bundle b = new Bundle();
					b.putSerializable(Constants.EXTRA_DATA, bean);
					openActivity(BarDetailActivity.class, b);
					// fillMenuList(hotlist);
				}
			});
			viewHotKeywords.addView(view);
		}
	}

}
