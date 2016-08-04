/**
 * Name: $RCSfile: ResizeAnimation.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Nov 27, 2015 2:51:02 PM $
 *
 * Copyright (c) 2013 FPT Software, Inc. All rights reserved.
 */
package com.example.macos.utilities;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author macos
 *
 */

public class ResizeAnimation extends Animation {
	int mode;
	final int targetSize;
	View view;
	int startSize;
	boolean isIncrease, hasAnimation;
	
	public ResizeAnimation(View view, int targetSize, int startSize, boolean isIncrease, int mode) {
		this.view = view;
		this.targetSize = targetSize;
		this.startSize = startSize;
		this.isIncrease = isIncrease;
		this.mode = mode;
		hasAnimation = false; 
	}

	public void setHasAnimation(boolean hasAnimation){
		this.hasAnimation = hasAnimation;
	}
	public void startAniamtionView() {
		view.startAnimation(this);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		
		if (isIncrease) {
			int newSize = (int) (startSize + targetSize * interpolatedTime);
			if(hasAnimation)
				view.setAlpha(interpolatedTime);
			if (mode == 0) {
				view.requestLayout();
				view.getLayoutParams().width = newSize;
			} else if (mode == 1) {
				view.requestLayout();
				view.getLayoutParams().height = newSize;
			}
		} else {
			if(hasAnimation)
				view.setAlpha(1 - interpolatedTime);
			int newSize = (int) (targetSize - targetSize * interpolatedTime);
			if (mode == 0) {
				view.requestLayout();
				view.getLayoutParams().width = newSize;
			} else if (mode == 1) {
				view.requestLayout();
				view.getLayoutParams().height = newSize;
			}

		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}
