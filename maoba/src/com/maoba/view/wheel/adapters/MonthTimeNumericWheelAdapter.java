package com.maoba.view.wheel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MonthTimeNumericWheelAdapter extends TimeNumericWheelAdapter {
	private MonthMidTextInterface monthTextInterface;

	public MonthTimeNumericWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	public MonthTimeNumericWheelAdapter(Context context, int minValue,
			int maxValue) {
		super(context, minValue, maxValue, null);
		// TODO Auto-generated constructor stub
	}

	public MonthTimeNumericWheelAdapter(Context context, int minValue,
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
				if (monthTextInterface != null)
					monthTextInterface.changeMonthText(index, textView);

				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					configureTextView(textView);
				}
			}
			return convertView;
		}
		return null;
	}

	public MonthMidTextInterface getMonthTextInterface() {
		return monthTextInterface;
	}

	public void setMonthTextInterface(MonthMidTextInterface monthTextInterface) {
		this.monthTextInterface = monthTextInterface;
	}

	public interface MonthMidTextInterface {
		public void changeMonthText(int index, TextView textView);
	}
}
