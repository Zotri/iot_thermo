package com.example.thermalapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class NumberPickerWithXml extends NumberPicker {
    
    public NumberPickerWithXml(Context context) {
        super(context);
    }

    public NumberPickerWithXml(Context context, AttributeSet attrs) {
        super(context, attrs);
        processXmlAttributes(attrs, 0, 0);
    }


    public NumberPickerWithXml(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processXmlAttributes(attrs, defStyleAttr, 0);
    }

    public NumberPickerWithXml(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processXmlAttributes(attrs, defStyleAttr, defStyleRes);
    }

    private void processXmlAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPickerWithXml, defStyleAttr, defStyleRes);

        try {
            this.setMinValue(attributes.getInt(R.styleable.NumberPickerWithXml_minValue, 0));
            this.setMaxValue(attributes.getInt(R.styleable.NumberPickerWithXml_maxValue, 0));
            this.setValue(attributes.getInt(R.styleable.NumberPickerWithXml_defaultValue, 0));
        } finally {
            attributes.recycle();
        }
    }
}
