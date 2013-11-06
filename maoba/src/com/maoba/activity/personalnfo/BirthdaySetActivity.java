package com.maoba.activity.personalnfo;

/**
 * 生日设置界面
 * 
 * @author ZhouYongJian
 * @data 创建时间：2013-10-27 18:00
 */
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maoba.Constants;
import com.maoba.R;
import com.maoba.activity.base.BaseActivity;
import com.maoba.view.wheel.adapters.TimeNumericWheelAdapter;
import com.maoba.view.wheel.widget.OnWheelScrollListener;
import com.maoba.view.wheel.widget.TimeWheelView;
import com.maoba.view.wheel.widget.WheelView;

public class BirthdaySetActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout timeWheels;
	
	private ImageButton ibLeft;
	private TextView tvTitle;
	private Button btnRight;
	private int yearSelected, monthSelected, daySelected;// 点保存时选择的年，月，日。
	private int []dateSelected=new int[3];// 选择的日期 xxxx-xx-xx。
	
	DateModel model = new DateModel();// 实例化日期类。
	Calendar calendar;
	TimeWheelView year, month, day;// 年滚轮，月滚轮，日滚轮。
	TimeNumericWheelAdapter yearAdapter, monthAdapter, dayAdapter;// 年月日滚轮适配器。
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birthday_set);
		ibLeft = (ImageButton) this.findViewById(R.id.ibLeft);
		
		btnRight = (Button) this.findViewById(R.id.btnRight);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		ibLeft.setBackgroundResource(R.drawable.ic_btn_left);
		btnRight.setText("保存");
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
		yearAdapter = new TimeNumericWheelAdapter(this, model.year - 120,
				model.year + 120);
		yearAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		yearAdapter.setItemTextResource(R.id.numeric_text);
		yearAdapter.setSuffix("  年");
		year.setViewAdapter(yearAdapter);
		year.setVisibleItems(Constants.TIME_LIST_NUMBER);
		year.setCurrentItem(120);
		System.out.println(year.getLinearLayout());/*getChildAt(0).setBackgroundColor(0xFFFFFFFF);*/
		year.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.year = calendar.get(Calendar.YEAR)
						+ wheel.getCurrentItem() - 120;
				freshDayWheel();

			}
		});
		// 实现月滚轮。
		month = (TimeWheelView) this.findViewById(R.id.monthwheel);
		monthAdapter = new TimeNumericWheelAdapter(this, 1, 12);
		monthAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		monthAdapter.setItemTextResource(R.id.numeric_text);
		monthAdapter.setSuffix("  月");
		month.setVisibleItems(Constants.TIME_LIST_NUMBER);
		month.setViewAdapter(monthAdapter);
		month.setCurrentItem(model.month);
		month.setCyclic(true);
		month.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.month = wheel.getCurrentItem()+1;
				freshDayWheel();

			}
		});
		// 实现日滚轮。
		day = (TimeWheelView) this.findViewById(R.id.daywheel);
		dayAdapter = new TimeNumericWheelAdapter(this, 1,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		dayAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		dayAdapter.setItemTextResource(R.id.numeric_text);
		dayAdapter.setSuffix("  日");
		day.setVisibleItems(Constants.TIME_LIST_NUMBER);
		day.setViewAdapter(dayAdapter);
		day.setCyclic(true);
		day.setCurrentItem(model.day - 1);
		day.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				model.day = wheel.getCurrentItem() + 1;

			}
		});
		
	}
	

	/* 切换年，月时，刷新日滚轮的最大天数 */
	private void freshDayWheel() {
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.YEAR, model.year);
		calendar.set(Calendar.MONTH, model.month-1);
		int maxDays = calendar.getMaximum(Calendar.DATE);
		dayAdapter = new TimeNumericWheelAdapter(BirthdaySetActivity.this, 1,
				maxDays);
		dayAdapter.setItemResource(R.layout.wheel_nemeric_text_item);
		dayAdapter.setItemTextResource(R.id.numeric_text);
		dayAdapter.setSuffix("  日");
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibLeft:
			finish();
			break;
		case R.id.btnRight:

			yearSelected = model.year;
			monthSelected = month.getCurrentItem() + 1;
			daySelected = day.getCurrentItem() + 1;
			dateSelected[0] =yearSelected;
			dateSelected[1] =monthSelected;
			dateSelected[2] =daySelected;
			Calendar finalDate=Calendar.getInstance();
			finalDate.set(yearSelected, monthSelected-1, daySelected);
			Log.i("", dateSelected.toString());
			if(finalDate.after(calendar)){//最后选择的要在当前时间之前。
					Toast.makeText(BirthdaySetActivity.this, "出生日期不能大于等于当前日期哦",
					Toast.LENGTH_SHORT).show();
			}
			else{
				//把年，月，日，传递给上层界面。
				Intent birthdayintent = new Intent();
				birthdayintent.putExtra("BRITHDAYSELECTED", dateSelected);
				setResult(Activity.RESULT_OK, birthdayintent);
				finish();
			}
			break;
		default:
			break;
		}
	}
/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.PERSONALINFONUM:
				
			}
		}
	}*/

}
