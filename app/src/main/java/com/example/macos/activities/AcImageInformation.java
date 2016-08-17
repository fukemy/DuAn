package com.example.macos.activities;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.TouchImageView;
import com.example.macos.utilities.AsyncTaskHelper;

import java.util.List;

/**
 * Created by admin2 on 8/15/16.
 */
public class AcImageInformation extends Activity {


    private TouchImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ac_image_infor);

        img = (TouchImageView) findViewById(R.id.imgInfo);
        setImage();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                    View view = new View(context);
                    view.setBackground(new BitmapDrawable((Bitmap) snapshot));
                    return view;
                }

                @Override
                public void onSharedElementStart(List<String> sharedElementNames,
                                                 List<View> sharedElements,
                                                 List<View> sharedElementSnapshots) {
                    ImageView sharedElement = (ImageView) findViewById(R.id.imgInfo);
                    for (int i = 0; i < sharedElements.size(); i++) {
                        if (sharedElements.get(i) == sharedElement) {
                            View snapshot = sharedElementSnapshots.get(i);
                            Drawable snapshotDrawable = snapshot.getBackground();
                            sharedElement.setBackground(snapshotDrawable);
                            sharedElement.setImageAlpha(0);
                            forceSharedElementLayout();
                            break;
                        }
                    }
                }

                private void forceSharedElementLayout() {
                    ImageView sharedElement = (ImageView) findViewById(R.id.imgInfo);
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(sharedElement.getWidth(),
                            View.MeasureSpec.EXACTLY);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(sharedElement.getHeight(),
                            View.MeasureSpec.EXACTLY);
                    int left = sharedElement.getLeft();
                    int top = sharedElement.getTop();
                    int right = sharedElement.getRight();
                    int bottom = sharedElement.getBottom();
                    sharedElement.measure(widthSpec, heightSpec);
                    sharedElement.layout(left, top, right, bottom);
                }

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames,
                                               List<View> sharedElements,
                                               List<View> sharedElementSnapshots) {
                    ImageView sharedElement = (ImageView) findViewById(R.id.imgInfo);
                    sharedElement.setBackground(null);
                    sharedElement.setImageAlpha(255);

                    //setImage();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setImage(){
        String imgRef = getIntent().getStringExtra("imgRef");
        Uri selectedImage = Uri.parse("file://" + imgRef);
        try {

            AsyncTaskHelper helper = new AsyncTaskHelper();
            helper.applyImage(this, img, selectedImage);
//            img.setOnTouchListener(new View.OnTouchListener() {
//                private int initialY;
//                private float initialTouchY;
//
//                @Override public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            initialY = (int)img.getY();
//                            initialTouchY = event.getRawY();
//                            return true;
//                        case MotionEvent.ACTION_UP:
//                            return true;
//                        case MotionEvent.ACTION_MOVE:
//                            int preventY = initialY + (int) (event.getRawY() - initialTouchY);
//                            img.setY(preventY);
//                            return true;
//                    }
//                    return false;
//                }
//            });

        }catch(Exception e){
            Logger.error("Exception get image: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
