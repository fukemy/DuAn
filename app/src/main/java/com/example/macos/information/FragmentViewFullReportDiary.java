package com.example.macos.information;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by devil2010 on 7/9/16.
 */
public class FragmentViewFullReportDiary extends DialogFragment {
    private TextView tvCalalog, tvRoadName, tvCurrentLocation, tvTime, tvSummary;
    LinearLayout lnlInput;
    private View rootView;
    private EnDataModel data;
    private GoogleMap gMap;
    SupportMapFragment mSupportMapFragment;

    public void setData(EnDataModel en) {
        data = en;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.report_status_content, container, false);
        initLayout();
        initData();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getDialog().getWindow().setEnterTransition(new Explode().setDuration(600));
        return rootView;
    }

    private void initLayout() {
        tvCalalog = (TextView) rootView.findViewById(R.id.tvCatalog);
        tvRoadName = (TextView) rootView.findViewById(R.id.tvRoadName);
        tvCurrentLocation = (TextView) rootView.findViewById(R.id.tvCurrentLocation);
        tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        tvSummary = (TextView) rootView.findViewById(R.id.tvSummary);


        /*
        mSupportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapp);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mapp, mSupportMapFragment).commit();
        }
        */
        mSupportMapFragment = new SupportMapFragment();
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.add(R.id.mapp, mSupportMapFragment).commit();
        getFragmentManager().beginTransaction();
        gMap = mSupportMapFragment.getMap();


    }

    private void initData() {
        System.out.println("data: " + data.toString());
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
                            Toast.makeText(getActivity(), "Có lỗi xảy ra khi hiển thị vị trí!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        tvCalalog.setText(tvCalalog.getText().toString() + " : " + (data.getDaValue().getDataName().equals("") ? "Chưa cập nhập!" :data.getDaValue().getDataName()));
        tvRoadName.setText(tvRoadName.getText().toString() + " : " + data.getDaValue().getTenDuong());
//        tvSummary.setText(tvSummary.getText().toString() + " : " + (data.getDaValue().getSummary().equals("") ? "Chưa cập nhập!" :data.getSummary()));
        tvSummary.setText("");
        if (data.getDaValue().getLocationItem() != null)
            if(data.getDaValue().getLocationItem().getLocation() != null)
                tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : " + data.getDaValue().getLocationItem().getAddress());
        else
            tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : Chưa cập nhập!");

        tvTime.setText(tvTime.getText().toString() + ": " + FunctionUtils.timeStampToTime(Long.parseLong(data.getDaValue().getThoiGianNhap())));

        lnlInput = (LinearLayout) rootView.findViewById(R.id.lnlInput);

        LinearLayout container = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.report_status_content_layout_include, null, false);

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
            tvStatus.setText(tvStatus.getText().toString() + " : " + (data.getDaValue().getDanhGia().equals("") ? "Chưa cập nhập!" : data.getDaValue().getDanhGia()));
        } catch (Exception e) {
            tvStatus.setText(tvStatus.getText().toString() + " : " + "Chưa cập nhập!");
        }
        try {
            tvComment.setText(tvComment.getText().toString() + " : " + (data.getDaValue().getMoTaTinhTrang().equals("") ? "Chưa cập nhập!" : data.getDaValue().getMoTaTinhTrang()));
        } catch (Exception e) {
            tvComment.setText(tvComment.getText().toString() + " : " + "Chưa cập nhập!");
        }


        ContentResolver cr = getActivity().getContentResolver();
        DisplayMetrics dm = getResources().getDisplayMetrics();


        for (ImageModel source : data.getListImageData()) {
            Uri uri = Uri.parse(source.getImagePath());
            try {
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
                //resize image
                Bitmap b = FunctionUtils.scaleBitmap(bitmap, dm.widthPixels / 2, dm.widthPixels / 2);
                ImageView img = new ImageView(getActivity());
                img.setImageBitmap(b);
                imgContainer.addView(img);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        lnlInput.addView(container);
    }
}