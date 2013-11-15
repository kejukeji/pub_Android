package com.keju.maomao.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.keju.maomao.R;


/**
 * 引导页
 * 
 * @author Zhoujun
 * @version 创建时间：2013-3-1 下午5:42:58
 * 
 */
public class FirstStartActivity extends Activity {

	private ViewPager vpPager;
	private ArrayList<View> alPages;
	private ImageView ivFlag;
	private ImageView[] ivPages;
	// 包裹滑动图片LinearLayout
	private ViewGroup vgWelcomepage;
	// 包裹小圆点的LinearLayout
	private ViewGroup vgWelcomeflag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
	}

	// 指引页面数据适配器
	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return alPages.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(alPages.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(alPages.get(arg1));
			return alPages.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i <= alPages.size() - 1; i++) {
				if (arg0 == i) {
					ivPages[arg0].setBackgroundResource(R.drawable.ic_slide_point_sel);
				} else {
					ivPages[i].setBackgroundResource(R.drawable.ic_slide_point_nor);
				}
			}
		}
	}

	private void init() {
		LayoutInflater inflater = getLayoutInflater();
		alPages = new ArrayList<View>();
		alPages.add(inflater.inflate(R.layout.firststartpage1, null));
		alPages.add(inflater.inflate(R.layout.firststartpage2, null));
		alPages.add(inflater.inflate(R.layout.firststartpage3, null));

		ivPages = new ImageView[alPages.size()]; // 减掉空的view就是点的数目
		
		vgWelcomepage = (ViewGroup) inflater.inflate(R.layout.firststart, null);

		vgWelcomeflag = (ViewGroup) vgWelcomepage.findViewById(R.id.viewGroup);
		vpPager = (ViewPager) vgWelcomepage.findViewById(R.id.guidePages);

		for (int i = 0; i < ivPages.length; i++) {
			// 设置最下面标识图片
			ivFlag = new ImageView(FirstStartActivity.this);
			if (i == 0) {
				// 默认选中第一张图片
				ivFlag.setBackgroundResource(R.drawable.ic_slide_point_sel);
			} else {
				ivFlag.setBackgroundResource(R.drawable.ic_slide_point_nor);
			}
			ivPages[i] = ivFlag;
			vgWelcomeflag.addView(ivPages[i]);
		}

		setContentView(vgWelcomepage);

		vpPager.setAdapter(new GuidePageAdapter());
		vpPager.setOnPageChangeListener(new GuidePageChangeListener());
		vpPager.setCurrentItem(0);
		View view = alPages.get(2);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(FirstStartActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
			}
		});
	}
}
