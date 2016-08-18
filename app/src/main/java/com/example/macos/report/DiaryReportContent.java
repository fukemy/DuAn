package com.example.macos.report;

import android.app.SharedElementCallback;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.activities.AcImageInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.utilities.FunctionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;

public class DiaryReportContent extends AppCompatActivity {
    private TextView tvCalalog, tvRoadName, tvCurrentLocation, tvTime, tvSummary;
    LinearLayout lnlInput;
    private EnDataModel data;
    private GoogleMap gMap;
    private DisplayMetrics dm;
    SupportMapFragment mSupportMapFragment;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.report_status_content);
        dm = getResources().getDisplayMetrics();
        initLayout();
        initData();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                    int bitmapWidth = Math.round(screenBounds.width());
                    int bitmapHeight = Math.round(screenBounds.height());
                    Bitmap bitmap = null;
                    if (bitmapWidth > 0 && bitmapHeight > 0) {
                        Matrix matrix = new Matrix();
                        matrix.set(viewToGlobalMatrix);
                        matrix.postTranslate(-screenBounds.left, -screenBounds.top);
                        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.concat(matrix);
                        sharedElement.draw(canvas);
                    }
                    return bitmap;
                }
            });


            Transition transition = new Explode().setDuration(600);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    try {
                        initMap();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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
            getWindow().setEnterTransition(transition);
            getWindow().setSharedElementExitTransition(new Fade().setDuration(300));
            getWindow().setExitTransition(new Fade().setDuration(100));
        }else{
            try {
                initMap();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initLayout() {
        btnBack = (Button) findViewById(R.id.btnBack);
        tvCalalog = (TextView) findViewById(R.id.tvCatalog);
        tvRoadName = (TextView) findViewById(R.id.tvRoadName);
        tvCurrentLocation = (TextView) findViewById(R.id.tvCurrentLocation);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSummary = (TextView) findViewById(R.id.tvSummary);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void initMap(){
        mSupportMapFragment = new SupportMapFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.mapp, mSupportMapFragment).commit();
        getFragmentManager().beginTransaction();
        gMap = mSupportMapFragment.getMap();

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        gMap = googleMap;
                        if(data.getDaValue().getLocationItem() != null && data.getDaValue().getLocationItem().getLocation() != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(data.getDaValue().getLocationItem().getLocation().getLatitude(), data.getDaValue().getLocationItem().getLocation().getLongitude()))
                                    .zoom(17)                   // Sets the zoom
                                    .bearing(90)                // Sets the orientation of the camera to east
                                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                            MarkerOptions options = new MarkerOptions();
                            options.position(new LatLng(data.getDaValue().getLocationItem().getLocation().getLatitude(), data.getDaValue().getLocationItem().getLocation().getLongitude()));
                            options.draggable(true);
                            options.icon(icon);
                            gMap.addMarker(options);
                        }else{
                            Toast.makeText(DiaryReportContent.this, "Có lỗi xảy ra khi hiển thị vị trí!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void initData() {
        Gson gson = new Gson();
        data = gson.fromJson(getIntent().getStringExtra("data"), EnDataModel.class);
        System.out.println("data: " + data.toString());

        tvCalalog.setText(tvCalalog.getText().toString() + " : " + (data.getDaValue().getDataName().equals("") ? "Chưa cập nhập!" :data.getDaValue().getDataName()));
        tvRoadName.setText(tvRoadName.getText().toString() + " : " + data.getDaValue().getTenDuong());
        tvSummary.setVisibility(View.GONE);

        if (data.getDaValue().getLocationItem() != null)
            if(data.getDaValue().getLocationItem().getLocation() != null)
                tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : " + data.getDaValue().getLocationItem().getAddress().replace("\n",". "));
            else
                tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : Chưa cập nhập!");

        tvTime.setText(tvTime.getText().toString() + ": " + FunctionUtils.timeStampToTime(Long.parseLong(data.getDaValue().getThoiGianNhap())));

        lnlInput = (LinearLayout) findViewById(R.id.lnlInput);

        LinearLayout container = (LinearLayout) LayoutInflater.from(DiaryReportContent.this).inflate(R.layout.report_status_content_layout_include, null, false);

        TextView tvPromptItem = (TextView) container.findViewById(R.id.tvPromptItem);
        TextView tvStatus = (TextView) container.findViewById(R.id.tvStatus);
        TextView tvComment = (TextView) container.findViewById(R.id.tvComment);
        LinearLayout imgContainer = (LinearLayout) container.findViewById(R.id.imgContainer);

        try {
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + (data.getDaValue().getDataTypeName().equals("") ? "Chưa cập nhập!" : data.getDaValue().getDataTypeName()));
        } catch (Exception e) {
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + "Chưa cập nhập!");
        }
        try {
            tvStatus.setText(tvStatus.getText().toString() + " : " + (data.getDaValue().getThangDanhGia().equals("") ? "Chưa cập nhập!" : data.getDaValue().getThangDanhGia()));
        } catch (Exception e) {
            tvStatus.setText(tvStatus.getText().toString() + " : " + "Chưa cập nhập!");
        }
        try {
            tvComment.setText(tvComment.getText().toString() + " : " + (data.getDaValue().getMoTaTinhTrang().equals("") ? "Chưa cập nhập!" : data.getDaValue().getMoTaTinhTrang()));
        } catch (Exception e) {
            tvComment.setText(tvComment.getText().toString() + " : " + "Chưa cập nhập!");
        }


        ContentResolver cr = DiaryReportContent.this.getContentResolver();
        DisplayMetrics dm = getResources().getDisplayMetrics();

        LinearLayout lnlHorizontal = null;
        int i = 0;
        for (ImageModel source : data.getListImageData()) {
            Uri uri = Uri.parse(source.getImagePath());
            i++;
            if(i == 1){
                lnlHorizontal = new LinearLayout(DiaryReportContent.this);
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(dm.widthPixels, dm.widthPixels / 2);
                lParams.gravity = Gravity.CENTER;
                lnlHorizontal.setLayoutParams(lParams);
                imgContainer.addView(lnlHorizontal);
            }

            final ImageView img = new ImageView(DiaryReportContent.this);
            img.setTag(uri.toString());

            lnlHorizontal.addView(img);
            new ApplyImage(img, uri).execute();
            if(i == 2)
                i = 0;
        }
        lnlInput.addView(container);
    }

    private class ApplyImage extends AsyncTask<Void, Void, Bitmap>{
        ImageView img;
        Uri uri;
        public ApplyImage(ImageView img, Uri uri){
            this.img = img;
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Bitmap b = FunctionUtils.decodeSampledBitmap(DiaryReportContent.this, uri);
                Bitmap decodedBitmap = Bitmap.createScaledBitmap(b, dm.widthPixels / 2, dm.widthPixels / 2, true);
                return decodedBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                img.setImageBitmap(bitmap);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(img);
                    }
                });
            }
        }
    }

    private void showImage(ImageView img){
        Intent in = new Intent(DiaryReportContent.this, AcImageInformation.class);
        in.putExtra("imgRef", img.getTag().toString());
        in.putExtra("isAcceptDelete", false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(DiaryReportContent.this, img,
                            "viewimage");
            startActivity(in, options.toBundle());
        }else{
            startActivity(in);
        }
    }
}
