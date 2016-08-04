package com.example.macos.libraries;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by macos on 6/16/16.
 */
public class StaticLinearLayout extends LinearLayout {
    public StaticLinearLayout(Context context) {
        super(context);
    }

    public StaticLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StaticLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
        getLayoutParams().height = getMeasuredHeight();
    }
}
