package com.example.macos.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;
import com.example.macos.interfaces.iListWork;

/**
 * Created by macos on 6/13/16.
 */
public class AnimationControl {
    public static Animator mCurrentAnimatorEffect;
    public static int mShortAnimationDurationEffect = 200;

    public static void AppearIcon(View v){
        ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f
                                                , Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(mShortAnimationDurationEffect);
        scale.setFillAfter(true);
        scale.setInterpolator(new LinearInterpolator());
//        v.startAnimation(scale);

        AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        scale.setDuration(mShortAnimationDurationEffect);
        v.startAnimation(alpha);
    }

    public static void hideIcon(View v){
        ScaleAnimation scale = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(mShortAnimationDurationEffect);
        scale.setFillAfter(true);
        scale.setInterpolator(new LinearInterpolator());
        v.startAnimation(scale);
    }

    public static void translateView(View v, int fromX, int toX, int fromY, int toY, boolean isFillAfter,
                                     int time, Context mCtx){
        TranslateAnimation trans = new TranslateAnimation(fromX, toX, fromY, toY);
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(time == 0 ? mShortAnimationDurationEffect : time);
        trans.setFillAfter(isFillAfter);
        v.startAnimation(trans);
    }
    public static void zoomImageFromThumb(final View thumbView, int imageResId, final View parent,
                                          final iListWork swap,
                                          final EnMainCatalogItem en) {
        if (mCurrentAnimatorEffect != null) {
            mCurrentAnimatorEffect.cancel();
        }

        //((ImageView)thumbView.findViewById(R.id.imgCategory)).setImageResource(0);

        final ImageView expandedImageView = (ImageView) parent.findViewById(R.id.expanded_image);
        expandedImageView.setImageResource(imageResId);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        parent.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        //thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).
                with(ObjectAnimator.ofFloat(expandedImageView,
                    View.SCALE_Y, startScale, 1f)).
                with(ObjectAnimator.ofFloat(thumbView, "alpha", 1f, 0f));
        set.setDuration(mShortAnimationDurationEffect);
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimatorEffect = null;
                //expandedImageView.setVisibility(View.GONE);
                parent.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Thread thread = new Thread(){
                    public void run(){
                        try{
                            Thread.sleep(600);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }finally {
                            {
                                swap.doListWork(en);
                            }
                        }
                    }
                };
                thread.start();;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimatorEffect = null;
            }
        });
        set.start();
        mCurrentAnimatorEffect = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimatorEffect != null) {
                    mCurrentAnimatorEffect.cancel();
                }

                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDurationEffect);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimatorEffect = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimatorEffect = null;
                    }
                });
                set.start();
                mCurrentAnimatorEffect = set;
            }
        });
    }
}