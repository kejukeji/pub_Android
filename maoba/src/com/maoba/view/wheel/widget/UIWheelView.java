package com.maoba.view.wheel.widget;

import com.maoba.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class UIWheelView extends WheelView {

	Paint mPaint;
	static final float DEFAULT_LINE_WIDTH=3.0F;
	/**
	 * Constructor
	 */
	public UIWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	/**
	 * Constructor
	 */
	public UIWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Constructor
	 */
	public UIWheelView(Context context) {
		super(context);
		init();
	}
	

	private void init() {
		mPaint=new Paint();
		mPaint.setColor(0xFF000000);
		mPaint.setStrokeWidth(DEFAULT_LINE_WIDTH);
		centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val_with_frame);
	}
	
	public void setRightLineWidth(int width){
		mPaint.setStrokeWidth(width);
	}
	
	public void setPaint(Paint paint){
		mPaint=paint;
	}
	
	@Override
	protected void drawCenterRect(Canvas canvas) {
		if(mPaint!=null){
			int center = getHeight() / 2;
			int offset = (int) (getItemHeight() / 2 * 1.2);
			centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
			centerDrawable.draw(canvas);
			canvas.drawLine(getWidth()-mPaint.getStrokeWidth(), 0, getWidth()-mPaint.getStrokeWidth(), center - offset, mPaint);
			canvas.drawLine(getWidth()-mPaint.getStrokeWidth(), center + offset, getWidth()-mPaint.getStrokeWidth(), getHeight(), mPaint);
		}
	}
}
