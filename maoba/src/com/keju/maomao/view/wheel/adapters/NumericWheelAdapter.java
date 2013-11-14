/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.keju.maomao.view.wheel.adapters;

import android.content.Context;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {
    
    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    protected static final int DEFAULT_MIN_VALUE = 0;
    
    // Values

    protected int minValue;
    protected int maxValue;
    private int timeInterval=1;

  // format
    protected String format;
    
    
    // string
    private String afterString="",beforeString="";
    /**
     * Constructor
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {

            int value = minValue + index*timeInterval;
            return format != null ? beforeString+String.format(format, value)+afterString : beforeString+Integer.toString(value)+afterString;

        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return (maxValue - minValue + 1)/timeInterval;
    }    
    public void setTimeInterval(int timeInterval){
    	this.timeInterval=timeInterval;
    }
    public void setNumberSize(int minValue, int maxValue ){
    	this.minValue=minValue;
    	this.maxValue=maxValue;
    }
    public void setSpeicalString(String beforeString,String afterString){
    	this.beforeString=beforeString;
    	this.afterString=afterString;
    }

   

//    public void setMinValue(int value){
//    	if(value<this.maxValue)
//    		this.minValue=value;
//    }
//    public void setMaxValue(int value){
//    	if(value>this.minValue)
//    		this.maxValue=value;
//    }
}
