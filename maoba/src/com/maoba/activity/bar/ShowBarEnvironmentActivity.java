/**
 * 
 */
package com.maoba.activity.bar;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.Constants;
import com.maoba.R;
import com.maoba.SystemException;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.maoba.bean.ResponseBean;
import com.maoba.helper.BusinessHelper;
import com.maoba.util.NetUtil;

/**
 * 显示酒吧环境
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-27 下午10:21:13
 */
public class ShowBarEnvironmentActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private ImageButton ibRight;
	private TextView tvRight;
	private TextView tvTitle;
	
	private BarBean bean;
	private ArrayList<BarBean> environmentBean = new ArrayList<BarBean>();
	private GridView gvBarEnvironmentPhoto;

	private BarEnvironmentPhotoAdapter adapter;

	private ProgressDialog pd;
	// private CommonApplication app;

	Display display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_bar_environment_list);
		display = this.getWindowManager().getDefaultDisplay();
		bean = (BarBean) getIntent().getExtras().getSerializable(Constants.EXTRA_DATA);

		findView();
		fillData();
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvRight = (TextView) this.findViewById(R.id.tvRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		gvBarEnvironmentPhoto = (GridView) findViewById(R.id.gvPhoto);

	}

	private void fillData() {

		ibLeft.setImageResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		tvRight.setOnClickListener(this);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间

		adapter = new BarEnvironmentPhotoAdapter();
		gvBarEnvironmentPhoto.setAdapter(adapter);

		tvTitle.setText("酒吧环境");
		if (NetUtil.checkNet(ShowBarEnvironmentActivity.this)) {
			new GetBarEnvironmentTask().execute();
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

	public class GetBarEnvironmentTask extends AsyncTask<Void, Void, ResponseBean<BarBean>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(ShowBarEnvironmentActivity.this);
			}
			pd.setMessage(getString(R.string.loading));
			pd.show();
		}

		@Override
		protected ResponseBean<BarBean> doInBackground(Void... params) {
			try {
				return new BusinessHelper().getenBarEnvironmentList(bean.getBar_id());
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
					List<BarBean> tempList = result.getObjList();
					environmentBean.addAll(tempList);
					if (tempList.size() > 0) {
						adapter.notifyDataSetChanged();
					}

				}
			} else {
				showShortToast(R.string.connect_server_exception);
			}
		}

	}

	/**
	 * 酒吧环境照片显示适配器
	 * 
	 * @author zhouyong
	 * 
	 */
	private class BarEnvironmentPhotoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return environmentBean.size();
		}

		@Override
		public Object getItem(int position) {
			return environmentBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final BarBean photoBean = environmentBean.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.show_bar_enviroment_item, null);
				holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ViewHolder holderUse = holder;

			int itemWidth = (display.getWidth() - 4 * 10) / 3;
			android.view.ViewGroup.LayoutParams param = holder.ivPhoto.getLayoutParams();
			param.width = itemWidth;
			param.height = itemWidth;
			holder.ivPhoto.setLayoutParams(param);
			String enviromentUrl = photoBean.getBarEnviromentPhoto();
			holder.ivPhoto.setTag(enviromentUrl);
			Drawable cacheDrawble = AsyncImageLoader.getInstance().loadDrawable(enviromentUrl, new ImageCallback() {

				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView image = (ImageView) gvBarEnvironmentPhoto.findViewWithTag(imageUrl);
					if (image != null) {
						if (imageDrawable != null) {
							image.setImageDrawable(imageDrawable);
							BarEnvironmentPhotoAdapter.this.notifyDataSetChanged();
						} else {
							image.setImageResource(R.drawable.ic_default);
						}
					}
				}
			});
			if (cacheDrawble != null) {
				holder.ivPhoto.setImageDrawable(cacheDrawble);
			} else {
				holder.ivPhoto.setImageResource(R.drawable.ic_default);
			}

			// 点击进入大图
			holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ShowBarEnvironmentActivity.this, PhotoShowActivity.class);
					intent.putExtra("photoId", photoBean.getBar_id());
					intent.putExtra("photoBeans", environmentBean);
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		private ImageView ivPhoto;
	}
}
