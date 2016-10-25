package com.example.macos.libraries;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

/**
 * Created by admin2 on 10/11/16.
 */

public class CustomVideoView extends VideoView {
    private int mForceHeight = 0;
    private int mForceWidth = 0;
    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure");
        setMeasuredDimension(mForceWidth, mForceHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getHolder().setSizeFromLayout();
    }
}
