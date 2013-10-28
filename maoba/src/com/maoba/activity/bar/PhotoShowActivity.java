/**
 * 
 */
package com.maoba.activity.bar;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.maoba.AsyncImageLoader;
import com.maoba.AsyncImageLoader.ImageCallback;
import com.maoba.CommonApplication;
import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.bean.BarBean;
import com.umeng.analytics.MobclickAgent;

/**
 * 点击图片看大图
 * 
 * @author zhouyong
 * @data 创建时间：2013-10-28 上午10:50:01
 */
public class PhotoShowActivity extends BaseActivity implements OnClickListener {
	private ImageButton ibLeft;
	private ImageButton ibRight;
	private TextView tvRight;
	private TextView tvTitle;

	private ViewPager mViewPager;
	private PhotoPagerAdapter photoPagerAdapter;

	private int screenWidth;// 屏幕宽度
	private File localFile;
	private ProgressDialog pd;

	private List<BarBean> photoBeans;
	private int currentPhotoPosition;// 当前选择图片索引

	private long currPhotoId;
	private boolean isSelf;

	public static Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	public static int maxSize = 3;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);

		photoBeans = (List<BarBean>) getIntent().getSerializableExtra("photoBeans");
		if (photoBeans == null) {
			photoBeans = new ArrayList<BarBean>();
		}
		isSelf = getIntent().getBooleanExtra("isSelf", false);
		currPhotoId = getIntent().getLongExtra("photoId", 0);
		currentPhotoPosition = getCurrentPhotoPosition();
		
		
		findView();
		fillData();
		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
		// 添加到容器中
		((CommonApplication) getApplicationContext()).addActivity(this);
		if (imageCache == null){
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}
	}

	private void findView() {
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		tvRight = (TextView) this.findViewById(R.id.tvRight);
		ibRight = (ImageButton) this.findViewById(R.id.ibRight);

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);

		int size = 0;
		if (photoBeans != null) {
			size = photoBeans.size();
		}
		tvTitle.setText((currentPhotoPosition + 1) + "/" + size);
		mViewPager = (ViewPager) this.findViewById(R.id.photo_detail_pager);
		photoPagerAdapter = new PhotoPagerAdapter(this, photoBeans);
		mViewPager.setAdapter(photoPagerAdapter);
		mViewPager.setCurrentItem(currentPhotoPosition);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				currentPhotoPosition = index;
				tvTitle.setText((index + 1) + "/" + photoBeans.size());
				if (index + 1 == photoBeans.size()) {
					showShortToast("这已经是最后一张了");
				}
				BarBean photoBean = photoBeans.get(currentPhotoPosition);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void fillData() {

		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		ibLeft.setOnClickListener(this);

		tvRight.setOnClickListener(this);

		ibRight.setVisibility(View.GONE);// 隐藏并且不占用布局的空间

		tvTitle.setText("酒吧环境图片");

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

	private int getCurrentPhotoPosition() {
		if (photoBeans != null) {
			int photoSize = photoBeans.size();
			for (int i = 0; i < photoSize; i++) {
				BarBean photoBean = photoBeans.get(i);
				int id = photoBean.getBar_id();
				if (id != 0) {
					if (id == currPhotoId) {
						return i;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 图片Pager容器
	 * 
	 * @author syghh
	 * 
	 */
	private class PhotoPagerAdapter extends PagerAdapter {
		private Context mContext;
		private List<BarBean> photoBeans;

		public PhotoPagerAdapter(Context mContext, List<BarBean> photoBeans) {
			this.mContext = mContext;
			this.photoBeans = photoBeans;
		}

		@Override
		public void destroyItem(ViewGroup container, int arg1, Object arg2) {
			container.removeView((View) arg2);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			BarBean photoBean = photoBeans.get(position);
			View view = LayoutInflater.from(mContext).inflate(R.layout.photo_detail, null);
			final ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
			final ImageView ivPhotoDefault = (ImageView) view.findViewById(R.id.ivPhotoDefault);
			String hightImgUrl = photoBean.getBarEnviromentPhoto();
			final View progress = view.findViewById(R.id.progress);
			Drawable cacheDrawable = AsyncImageLoader.getInstance().loadAsynSoftRefeDrawable(imageCache, maxSize, hightImgUrl, new ImageCallback() {
					
						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
							if (imageDrawable != null) {
								ivPhoto.setVisibility(View.VISIBLE);
								int oldwidth = imageDrawable.getIntrinsicWidth();
								int oldheight = imageDrawable.getIntrinsicHeight();
								LayoutParams lp = ivPhoto.getLayoutParams();
								lp.width = screenWidth;
								lp.height = (oldheight * screenWidth) / oldwidth;
								ivPhoto.setLayoutParams(lp);
								ivPhoto.setImageDrawable(imageDrawable);
								progress.setVisibility(View.GONE);
								ivPhotoDefault.setVisibility(View.GONE);
								PhotoPagerAdapter.this.notifyDataSetChanged();
							} else {
								ivPhoto.setVisibility(View.GONE);
								progress.setVisibility(View.VISIBLE);
								ivPhotoDefault.setVisibility(View.VISIBLE);
							}
						}

					});
			if (cacheDrawable != null) {
				ivPhoto.setVisibility(View.VISIBLE);
				int oldwidth = cacheDrawable.getIntrinsicWidth();
				int oldheight = cacheDrawable.getIntrinsicHeight();
				LayoutParams lp = ivPhoto.getLayoutParams();
				lp.width = screenWidth;
				lp.height = (oldheight * screenWidth) / oldwidth;
				ivPhoto.setLayoutParams(lp);
				ivPhoto.setImageDrawable(cacheDrawable);
				ivPhotoDefault.setVisibility(View.GONE);
				progress.setVisibility(View.GONE);
			} else {
				ivPhoto.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				ivPhotoDefault.setVisibility(View.VISIBLE);
			}
			((ViewPager) container).addView(view, 0);
			return view;
		}

		@Override
		public int getCount() {
			return photoBeans.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
