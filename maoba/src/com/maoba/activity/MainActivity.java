package com.maoba.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.view.MyHorizontalScrollView;
import com.maoba.view.MyHorizontalScrollView.SizeCallback;

public class MainActivity extends BaseActivity implements OnClickListener {
	private MyHorizontalScrollView scrollView; // 水平滑动控件按钮
	private static View settingView;//设置界面
	private static View homeView;//主界面
	private static View currentView;// 当前显示的view

	private LinearLayout rlCollect;// 收藏
	private LinearLayout rlInfromation;// 信息
	private LinearLayout rlSetting;// 设置

	private Button btnLeftMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findView();
	}

	private void findView() {
		// 设置界面
		LayoutInflater inflater = LayoutInflater.from(this);
		scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.main, null);// 加载水平滑动控件按钮
		setContentView(scrollView);// 动态加载view

		settingView = inflater.inflate(R.layout.left_menu, null);// 加载左边菜单栏界面
		homeView = inflater.inflate(R.layout.home, null);// 加载头部按钮界面
		btnLeftMenu = (Button) homeView.findViewById(R.id.btnLeftMenu);// 头部界面左边按钮控件
		btnLeftMenu.setOnClickListener(new ClickListenerForScrolling(scrollView, settingView));//

		final View[] children = new View[] { settingView, homeView };
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnLeftMenu));
		currentView = homeView;

		rlCollect = (LinearLayout) settingView.findViewById(R.id.rlCollect);
		rlInfromation = (LinearLayout) settingView.findViewById(R.id.rlInfromation);
		rlSetting = (LinearLayout) settingView.findViewById(R.id.rlSetting);

		rlCollect.setOnClickListener(this);
		rlInfromation.setOnClickListener(this);
		rlSetting.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlCollect:
			Toast.makeText(MainActivity.this, "你点击了收藏", 1).show();
			break;
		case R.id.rlInfromation:
			Toast.makeText(MainActivity.this, "你点击了信息", 1).show();
			break;
		case R.id.rlSetting:
			Toast.makeText(MainActivity.this, "你点击了设置", 1).show();
		default:
			break;
		}

	}

	static int left = 0;
	static boolean leftMenuOut = false;

	/**
	 * Menu must NOT be out/shown to start with.
	 */
	public static class ClickListenerForScrolling implements OnClickListener {
		private View view;
		private HorizontalScrollView scrollView;

		/**
		 * @param scrollView
		 * @param settingView
		 * 
		 */
		public ClickListenerForScrolling(HorizontalScrollView scrollView, View settingView) {
			super();
			this.view = settingView;
			this.scrollView = scrollView;
		}

		@Override
		public void onClick(View v) {
			// Log.i(TAG, "menu---onClick");
			if (v.getId() != R.id.btnLeftMenu && left > 0) {
				return;
			}
			int viewWidth = view.getMeasuredWidth();
			// Ensure menu is visible
			view.setVisibility(View.VISIBLE);
			if (v.getId() == R.id.btnLeftMenu) {
				if (!leftMenuOut) {
					/* 根据分辨来辨别 偏移 各种分辨率偏移是不一样的 */
					int offset = 0;
					left = 0;
					if (viewWidth <= 320) {
						offset = 20;
					} else if (viewWidth <= 480) {
						offset = 30;
					} else {
						offset = 40;
					}

					scrollView.smoothScrollTo(left + offset, 0);
					currentView = settingView;// 将左侧菜单栏给当前的view
				} else {
					left = viewWidth;
					scrollView.smoothScrollTo(left, 0);
					currentView = homeView;// 将主界面给当前的view
				}
				leftMenuOut = !leftMenuOut;
			}
		}

	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnLeftMenu;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnLeftMenu = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnLeftMenu.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			// final int menuIdx = 0;
			// if (idx == menuIdx) {
			// dims[0] = w - btnWidth;
			// }
			if (idx != 1) {
				// 当视图不是中间的视图
				dims[0] = w - btnWidth;
			}
		}
	}

}
