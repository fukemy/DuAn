package com.example.macos.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.TouchImageView;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.AsyncTaskHelper;

import java.util.List;

/**
 * Created by admin2 on 8/15/16.
 */
public class AcImageInformation extends Activity {

    private TouchImageView img;
    private LinearLayout lnlAction, lnlDelete, lnlBack;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ac_image_infor);

        initLayout();

        setImage();
        try{
            boolean acceptDelete = getIntent().getBooleanExtra("isAcceptDelete", true);
            if(!acceptDelete){
                lnlDelete.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition trans = TransitionInflater.from(this).inflateTransition(R.transition.shared_element);
            getWindow().setSharedElementEnterTransition(trans);
            Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
            sharedElementEnterTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    Logger.error("onTransitionEnd");
                    lnlAction.setVisibility(View.VISIBLE);
                    AnimationControl.translateView(lnlAction, 0, 0, 200, 0 , true, 300);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });

            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                    View view = new View(context);
                    try {
                        view.setBackground(new BitmapDrawable((Bitmap) snapshot));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    Logger.error("end element");
                }
            });
        }else{
            lnlAction.setVisibility(View.VISIBLE);
            AnimationControl.translateView(lnlAction, 0, 0, 200, 0 , true, 300);
        }

    }

    private void initLayout(){

        lnlDelete = (LinearLayout) findViewById(R.id.lnlDelete);
        lnlBack = (LinearLayout) findViewById(R.id.lnlBack);
        lnlAction = (LinearLayout) findViewById(R.id.lnlAction);
        img = (TouchImageView) findViewById(R.id.imgInfo);


        lnlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lnlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AcImageInformation.this);
                builder.setTitle("Chú ý");
                builder.setMessage("Bạn có chắc chắn muốn xoá ảnh này chứ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        data.putExtra("isDelete", true);
                        setResult(RESULT_OK, data);
//                        onBackPressed();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setImage(){
        String imgRef = getIntent().getStringExtra("imgRef");
        Uri selectedImage = Uri.parse("file://" + imgRef);
        AsyncTaskHelper helper = new AsyncTaskHelper();
        helper.applyImage(this, img, selectedImage);
    }

}
