package com.keju.maomao.view.wheel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class SecondNumericWheelAdapter extends TimeNumericWheelAdapter {

	public SecondNumericWheelAdapter(Context context, int minValue,
			int maxValue, String format) {
		super(context, minValue, maxValue, format);
		// TODO Auto-generated constructor stub
	}

	public SecondNumericWheelAdapter(Context context, int minValue, int maxValue) {
		super(context, minValue, maxValue);
		// TODO Auto-generated constructor stub
	}

	public SecondNumericWheelAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	 @Override
	    public CharSequence getItemText(int index) {
	        if (index >= 0 && index < getItemsCount()) {
	            int value = minValue + index*5;
	            return format != null ? String.format(format, value)+getSuffix() : Integer.toString(value)+getSuffix();
	        }
	        return null;
	    }
	 @Override
	    public View getItem(int index, View convertView, ViewGroup parent) {
		 return super.getItem(index, convertView, parent);
	 }
}
