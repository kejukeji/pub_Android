package com.keju.maomao.activity.personalnfo;


import java.io.File;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keju.maomao.Constants;
import com.keju.maomao.R;
import com.keju.maomao.SystemException;
import com.keju.maomao.activity.base.BaseActivity;
import com.keju.maomao.helper.BusinessHelper;
import com.keju.maomao.util.NetUtil;
import com.keju.maomao.util.SharedPrefUtil;
import com.keju.maomao.view.wheel.adapters.DayTimeNumericWheelAdapter;
import com.keju.maomao.view.wheel.adapters.DayTimeNumericWheelAdapter.DayMidTextInterface;
import com.keju.maomao.view.wheel.adapters.MonthTimeNumericWheelAdapter;
import com.keju.maomao.view.wheel.adapters.MonthTimeNumericWheelAdapter.MonthMidTextInterface;
import com.keju.maomao.view.wheel.adapters.YearTimeNumericWheelAdapter;
import com.keju.maomao.view.wheel.adapters.YearTimeNumericWheelAdapter.YearMidTextInterface;
import com.keju.maomao.view.wheel.widget.OnWheelScrollListener;
import com.keju.maomao.view.wheel.widget.TimeWheelView;
import com.keju.maomao.view.wheel.widget.WheelView;

/**
 * 生日设置界面
 * 
 * @author ZhouYongJian
 * @data 创建时间：2013-10-27 18:00
 */
public class BirthdaySetActivity extends BaseActivity implements OnClickListener {
	private LinearLayout timeWheels;

	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private int yearSelected, monthSelected, daySelected;// 点保存时选择的年，月，日。
	private int[] dateSelected = new int[3];// 选择的日期 xxxx-xx-xx。

	DateModel model = new DateModel();// 实例化日期类。
	Calendar calendar;
	TimeWheelView year, month, day;// 年滚轮，月滚轮，日滚轮。
	
	YearTimeNumericWheelAdapter yearAdapter;
	MonthTimeNumericWheelAdapter monthAdapter;
	DayTimeNumericWheelAdapter dayAdapter;// 年月日滚轮适配器。
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birthday_set);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);

		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		ibLeft.setImageResource(R.drawable.ic_btn_left);
		btnRight.setText("保存");
		btnRight.setBackgroundResource(R.drawable.bg_btn_collection);
		tvTitle.setText("生日设置");
		btnRight.setOnClickListener(this);
		ibLeft.setOnClickListener(this);
		timeWheels = (LinearLayout) this.findViewById(R.id.wheelgroup);
		calendar = Calendar.getInstance();
		model.year = calendar.get(Calendar.YEAR);
		model.month = calendar.get(Calendar.MONTH);
		model.day = calendar.get(Calendar.DAY_OF_MONTH);// 获取当前日期。

		// 实现年滚轮
		year = (TimeWheelView) this.findViewById(R.id.yearwheel);
		yearAdapter = new YearTimeNumericWheelAdapter(this, model.year - 120, model.year + 120);
		yearAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		yearAdapter.setItemTextResource(R.id.numeric_text);
		yearAdapter.setSuffix("  年");
		yearAdapter.setYearTextInterface(yearMidTextInterface);
		year.setViewAdapter(yearAdapter);
		year.setVisibleItems(Constants.TIME_LIST_NUMBER);
		year.setCurrentItem(120);
		System.out.println(year.getLinearLayout());/*
													 * getChildAt(0).
													 * setBackgroundColor
													 * (0xFFFFFFFF);
													 */
		year.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.year = calendar.get(Calendar.YEAR) + wheel.getCurrentItem() - 120;
				year.invalidateWheel(true);
				freshDayWheel();

			}
		});
		// 实现月滚轮。
		month = (TimeWheelView) this.findViewById(R.id.monthwheel);
		monthAdapter = new MonthTimeNumericWheelAdapter(this, 1, 12);
		monthAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		monthAdapter.setItemTextResource(R.id.numeric_text);
		monthAdapter.setSuffix("  月");
		monthAdapter.setMonthTextInterface(monthMidTextInterface);
		month.setVisibleItems(Constants.TIME_LIST_NUMBER);
		month.setViewAdapter(monthAdapter);
		month.setCurrentItem(model.month);
		month.setCyclic(true);
		month.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.month = wheel.getCurrentItem() + 1;
				month.invalidateWheel(true);
				freshDayWheel();

			}
		});
		// 实现日滚轮。
		day = (TimeWheelView) this.findViewById(R.id.daywheel);
		dayAdapter = new DayTimeNumericWheelAdapter(this, 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		dayAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		dayAdapter.setItemTextResource(R.id.numeric_text);
		dayAdapter.setSuffix("  日");
		dayAdapter.setDayTextInterface(dayMidTextInterface);
		day.setVisibleItems(Constants.TIME_LIST_NUMBER);
		day.setViewAdapter(dayAdapter);
		day.setCyclic(true);
		day.setCurrentItem(model.day - 1);
		day.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.day = wheel.getCurrentItem() + 1;
				day.invalidateWheel(true);
			}
		});

	}

	/* 切换年，月时，刷新日滚轮的最大天数 */
	private void freshDayWheel() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, model.year);
		calendar.set(Calendar.MONTH, model.month - 1);
		int maxDays = calendar.getActualMaximum(Calendar.DATE);
		dayAdapter = new DayTimeNumericWheelAdapter(BirthdaySetActivity.this, 1, maxDays);
		dayAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		dayAdapter.setItemTextResource(R.id.numeric_text);
		dayAdapter.setSuffix("  日");
		dayAdapter.setDayTextInterface(dayMidTextInterface);
		day.setViewAdapter(dayAdapter);
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
		model.day = curDay;
	}

	// 日期内部类
	class DateModel {
		int year, month, day;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:

			yearSelected = model.year;
			monthSelected = month.getCurrentItem() + 1;
			daySelected = day.getCurrentItem() + 1;
			dateSelected[0] = yearSelected;
			dateSelected[1] = monthSelected;
			dateSelected[2] = daySelected;
			Calendar finalDate = Calendar.getInstance();
			finalDate.set(yearSelected, monthSelected - 1, daySelected);
			Log.i("", dateSelected.toString());
			if (finalDate.after(calendar)) {// 最后选择的要在当前时间之前。
				Toast.makeText(BirthdaySetActivity.this, "出生日期不能大于等于当前日期哦", Toast.LENGTH_SHORT).show();
			} else {
				// 把年，月，日，传递给上层界面。
				Intent birthdayintent = new Intent();
				birthdayintent.putExtra("BRITHDAYSELECTED", dateSelected);
				// 将日期转换为string类型
				String birthday = String.valueOf(dateSelected[0]) + "-"
						+ String.valueOf(dateSelected[1] + "-" + String.valueOf(dateSelected[2]));
				setResult(Activity.RESULT_OK, birthdayintent);
				String nickName = "";
				String sex = "";
				String signature = "";
				String newPassword = "";
				if (NetUtil.checkNet(BirthdaySetActivity.this)) {
					new personInfoAddTask(nickName, birthday, sex, signature, newPassword).execute();
				}
				
			}
			break;
		default:
			break;
		}
		
	}
	YearMidTextInterface yearMidTextInterface=new YearMidTextInterface() {
		
		@Override
		public void changeYearText(int index, TextView textView) {
			if (index == year.getCurrentItem()) {

				textView.setTextColor(0xFF3A788B);
				

			} else {
				textView.setTextColor(0xFF000000);
				
			}
		}
	};
	MonthMidTextInterface monthMidTextInterface=new MonthMidTextInterface() {
		
		@Override
		public void changeMonthText(int index, TextView textView) {
			if (index == month.getCurrentItem()) {

				textView.setTextColor(0xFF3A788B);
				

			} else {
				textView.setTextColor(0xFF000000);
				
			}
		}
	};
	DayMidTextInterface dayMidTextInterface=new DayMidTextInterface() {
		
		@Override
		public void changeDayText(int index, TextView textView) {
			if (index == day.getCurrentItem()) {

				textView.setTextColor(0xFF3A788B);
			

			} else {
				textView.setTextColor(0xFF000000);
				
			}
			
		}
	};
	
	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (resultCode == RESULT_OK) { switch
	 * (requestCode) { case Constants.PERSONALINFONUM:

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
			int loginType = SharedPrefUtil.getLoginType(BirthdaySetActivity.this);
			int userId = SharedPrefUtil.getUid(BirthdaySetActivity.this);
			String openId = SharedPrefUtil.getWeiboUid(BirthdaySetActivity.this);
			String password = SharedPrefUtil.getPassword(BirthdaySetActivity.this);
			int sex = 0;
			if (loginType == 0) {
				try {
					return new BusinessHelper().addUserInfor(userId, loginType, password, nickName, birthday, sex,
							signature, newPassword,"","","", avatarFile);
				} catch (SystemException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new BusinessHelper().thirdAddUserInfor(userId, loginType, openId, nickName, birthday, sex,
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
						showShortToast("生日设置成功");
						finish();
					} else {
						showShortToast("生日设置失败");
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
