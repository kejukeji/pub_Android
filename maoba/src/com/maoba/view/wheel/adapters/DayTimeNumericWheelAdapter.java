package com.maoba.view.wheel.adapters;

import com.maoba.view.wheel.adapters.MonthTimeNumericWheelAdapter.MonthMidTextInterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DayTimeNumericWheelAdapter extends TimeNumericWheelAdapter{
	private DayMidTextInterface dayTextInterface;
	public DayTimeNumericWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	public DayTimeNumericWheelAdapter(Context context, int minValue,
			int maxValue) {
		super(context, minValue, maxValue, null);
		// TODO Auto-generated constructor stub
	}

	public DayTimeNumericWheelAdapter(Context context, int minValue,
			int maxValue, String format) {
		super(context, minValue, maxValue, format);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		if (index >= 0 && index < getItemsCount()) {
			if (convertView == null) {
				convertView = getView(itemResourceId, parent);
			}
			TextView textView = getTextView(convertView, itemTextResourceId);
			if (textView != null) {
				CharSequence text = getItemText(index);
				if (text == null) {
					text = "";
				}
				textView.setText(text);
				if (dayTextInterface != null)
					dayTextInterface.changeDayText(index, textView);

				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					configureTextView(textView);
				}
			}
			return convertView;
		}
		return null;
	}

	public DayMidTextInterface getDayTextInterface() {
		return dayTextInterface;
	}

	public void setDayTextInterface(DayMidTextInterface dayTextInterface) {
		this.dayTextInterface = dayTextInterface;
	}

	public interface DayMidTextInterface {
		public void changeDayText(int index, TextView textView);
	}
}
