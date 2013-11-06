package com.maoba.view.wheel.adapters;

import java.util.Random;

import com.maoba.view.wheel.widget.TimeWheelView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * ʱ�����������
 * @author Desmond
 *
 */ 
public class TimeNumericWheelAdapter extends NumericWheelAdapter {

	// suffix
    private String suffix;
    //���������������ʽ�ӿ�
    private MidTextInterface textInterface;
    
    public TimeNumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }
    
    public TimeNumericWheelAdapter(Context context, int minValue, int maxValue) {
		super(context, minValue, maxValue, null);
		// TODO Auto-generated constructor stub
	}
    
    public TimeNumericWheelAdapter(Context context, int minValue, int maxValue,
			String format) {
		super(context, minValue, maxValue, format);
		// TODO Auto-generated constructor stub
	}
    
    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? String.format(format, value)+getSuffix() : Integer.toString(value)+getSuffix();
        }
        return null;
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
                if(textInterface!=null)
                	textInterface.changeText(index,textView);
    
                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView);
                }
            }
            return convertView;
        }
    	return null;
    }
    
	public String getSuffix() {
		return suffix==null?"":suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	} 
	
	public MidTextInterface getTextInterface() {
		return textInterface;
	}

	public void setTextInterface(MidTextInterface textInterface) {
		this.textInterface = textInterface;
	}

	public interface MidTextInterface{
		public void changeText(int index,TextView textView);
	}
}
