package com.keju.maomao.view.wheel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class YearTimeNumericWheelAdapter extends TimeNumericWheelAdapter {
		private YearMidTextInterface yearTextInterface;
	 public YearTimeNumericWheelAdapter(Context context) {
	        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	    }
	    
	    public YearTimeNumericWheelAdapter(Context context, int minValue, int maxValue) {
			super(context, minValue, maxValue, null);
			// TODO Auto-generated constructor stub
		}
	    
	    public YearTimeNumericWheelAdapter(Context context, int minValue, int maxValue,
				String format) {
			super(context, minValue, maxValue, format);
			// TODO Auto-generated constructor stub
		}
	    
	    @Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
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
	                 if(yearTextInterface!=null)
	                 	yearTextInterface.changeYearText(index,textView);
	     
	                 if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
	                     configureTextView(textView);
	                 }
	             }
	             return convertView;
	         }
	     	return null;
		}

		public YearMidTextInterface getYearTextInterface() {
			return yearTextInterface;
		}

		public void setYearTextInterface(YearMidTextInterface yearTextInterface) {
			this.yearTextInterface = yearTextInterface;
		}

		public interface YearMidTextInterface{
			public void changeYearText(int index,TextView textView);
		}
}
