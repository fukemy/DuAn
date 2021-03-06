package com.example.macos.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.macos.libraries.Logger;

import java.io.IOException;

/**
 * Created by devil2010 on 8/16/16.
 */
public class AsyncTaskHelper {

    public void applyImage(Context mContext, ImageView img, Uri uri){
//        new ApplyImage(mContext, img, uri).execute();
        new ApplyImage(mContext, img, uri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private class ApplyImage extends AsyncTask<Void, Void, Bitmap> {
        ImageView img;
        Uri uri;
        Context mContext;
        public ApplyImage(Context mContext, ImageView img, Uri uri){
            this.img = img;
            this.uri = uri;
            this.mContext = mContext;
            img.setImageBitmap(null);
            if(img.getScaleType() != ImageView.ScaleType.FIT_XY)
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Bitmap bitmap = FunctionUtils.decodeSampledBitmap(mContext, uri);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap b) {
            if(b != null){
                Logger.error("Done load img");
                img.setImageBitmap(b);
            }else{
                return;
            }
        }
    }
}
