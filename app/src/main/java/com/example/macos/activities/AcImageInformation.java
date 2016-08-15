package com.example.macos.activities;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;

/**
 * Created by admin2 on 8/15/16.
 */
public class AcImageInformation extends AppCompatActivity {

    private ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_infor);

        setImage();

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    private void setImage(){
        img = (ImageView) findViewById(R.id.imgInfo);
        String imgRef = getIntent().getStringExtra("imgRef");
        ContentResolver cr = getContentResolver();
        Uri selectedImage = Uri.parse("file://" + imgRef);
        try {
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
            img.setImageBitmap(bitmap);

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
