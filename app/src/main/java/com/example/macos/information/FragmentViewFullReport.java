package com.example.macos.information;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.libraries.Logger;
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
 * Created by devil2010 on 7/6/16.
 */
public class FragmentViewFullReport extends DialogFragment {
    private TextView tvCalalog,tvRoadName,tvCurrentLocation,tvTime,tvSummary,tvJusticeProcess;
    LinearLayout lnlInput;
    private View rootView;
    private EnDataModel data;
    int inputOrder;
    private GoogleMap gMap;
    SupportMapFragment mSupportMapFragment;
    private Button btnBack;

    public void setData(EnDataModel en){
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

        return rootView;
    }

    private void initLayout(){
        tvCalalog = (TextView) rootView.findViewById(R.id.tvCatalog);
        tvRoadName = (TextView) rootView.findViewById(R.id.tvRoadName);
        tvCurrentLocation = (TextView) rootView.findViewById(R.id.tvCurrentLocation);
        tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        tvSummary = (TextView) rootView.findViewById(R.id.tvSummary);
        tvJusticeProcess = (TextView) rootView.findViewById(R.id.tvJusticeProcess);
        btnBack = (Button) rootView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        mSupportMapFragment = new SupportMapFragment();
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.add(R.id.mapp, mSupportMapFragment).commit();
        getFragmentManager().beginTransaction();
        gMap = mSupportMapFragment.getMap();


    }

    private void initData() {
        lnlInput = (LinearLayout) rootView.findViewById(R.id.lnlInput);

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
        LinearLayout container = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.report_status_content_layout_include, null, false);
        tvCalalog.setText(tvCalalog.getText().toString() + " : " + data.getDaValue().getAction());
        tvRoadName.setText(tvRoadName.getText().toString() + " : " + data.getDaValue().getTenDuong());

        try {
            tvJusticeProcess.setText(tvJusticeProcess.getText().toString() + " : " + (data.getDaValue().getLyTrinh() == null || data.getDaValue().getLyTrinh().equals("")
                    || data.getDaValue().getLyTrinh().equals("null") ? "Chưa cập nhập!" : data.getDaValue().getLyTrinh()));
        }catch (Exception e){
            tvJusticeProcess.setText(tvJusticeProcess.getText().toString() + " : " + "Chưa cập nhập!");
        }

        tvSummary.setText("");
        if (data.getDaValue().getLocationItem().getLocation() != null) {
            tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : " + data.getDaValue().getLocationItem().getAddress());
        }
        else
            tvCurrentLocation.setText(tvCurrentLocation.getText().toString() + " : Chưa cập nhập!");

        tvTime.setText(tvTime.getText().toString() + ": " + FunctionUtils.timeStampToTime(Long.parseLong(data.getDaValue().getThoiGianNhap())));
        TextView tvPromptItem = (TextView) container.findViewById(R.id.tvPromptItem);
        TextView tvStatus = (TextView) container.findViewById(R.id.tvStatus);
        TextView tvComment = (TextView) container.findViewById(R.id.tvComment);

        try {
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + ((data.getDaValue().getDataTypeName() == null || data.getDaValue().getDataTypeName().equals("")) ?
                    "Chưa có dữ liệu!" : data.getDaValue().getDataTypeName()));
        }catch (Exception e){
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + "Chưa có dữ liệu!");
        }
        try{
            tvStatus.setText(tvStatus.getText().toString() + " : " + ((data.getDaValue().getThangDanhGia() == null || data.getDaValue().getThangDanhGia().equals("null") || data.getDaValue().getThangDanhGia().equals(""))?
                    "Chưa có dữ liệu!" : data.getDaValue().getThangDanhGia()));
        }catch (Exception e){
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + "Chưa có dữ liệu!");
        }
        try{
            tvComment.setText(tvComment.getText().toString() + " : " + ((data.getDaValue().getMoTaTinhTrang() == null || data.getDaValue().getMoTaTinhTrang().equals(""))?
                    "Chưa có dữ liệu!" : data.getDaValue().getMoTaTinhTrang()));
        }catch (Exception e){
            tvPromptItem.setText(tvPromptItem.getText().toString() + " : " + "Chưa có dữ liệu!");
        }
        ContentResolver cr = getActivity().getContentResolver();
        DisplayMetrics dm = getResources().getDisplayMetrics();


        if(data.getListImageData() != null && data.getListImageData().size() > 0)
            for (ImageModel source : data.getListImageData()) {
                Uri uri = Uri.parse(source.getImagePath());
                try {
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
                    //resize image
                    Bitmap b = FunctionUtils.scaleBitmap(bitmap, dm.widthPixels / 2, dm.widthPixels / 2);
                    ImageView img = new ImageView(getActivity());
                    img.setImageBitmap(b);
                    container.addView(img);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        lnlInput.addView(container);
    }
}
