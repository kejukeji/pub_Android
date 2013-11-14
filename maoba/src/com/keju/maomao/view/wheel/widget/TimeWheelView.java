package com.keju.maomao.view.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class TimeWheelView extends WheelView {
	
	public TimeWheelView(Context context) {
		super(context);
		init();
	}
	
	public TimeWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public TimeWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		
	
	}
	
	
	@Override
	protected void drawCenterRect(Canvas canvas) {
		super.drawCenterRect(canvas);
	}
	
}
