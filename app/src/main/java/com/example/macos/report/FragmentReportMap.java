package com.example.macos.report;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.fragment.FragmentViewFullReport;
import com.example.macos.interfaces.iListWork;
import com.example.macos.utilities.CustomFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by macos on 6/15/16.
 */
public class FragmentReportMap extends CustomFragment{
    private View rootView;
    private iListWork swapInterface;
    private GoogleMap gMap;
    private ProgressDialog dialog;
    private boolean IS_FIRST_INIT_MAP = false;
    private HashMap<Marker, EnDataModel> listMarkerData;

    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }

    private SupportMapFragment mSupportMapFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_map, container, false);
        return rootView;
    }

    private void initLayout() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Waiting for map loader!");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.show();

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapp);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapp, mSupportMapFragment).commit();
        }

        MapsInitializer.initialize(getActivity());
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        gMap = googleMap;
                        gMap.setMyLocationEnabled(true);
                        gMap.setOnMyLocationChangeListener(myLocationChangeListener);
                    }

                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(dialog != null)
            if(dialog.isShowing())
                dialog.dismiss();
        if (isVisibleToUser ) {
            initLayout();
            listMarkerData = new HashMap<>();
            summaryData();
        }else{
        }
    }
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (!IS_FIRST_INIT_MAP) {
                Location mCurrentLocation = null;
                IS_FIRST_INIT_MAP = true;
                if (enList.size() > 0) {
                    if(enList.get(enList.size() - 1).getDaValue().getLocationItem().getLocation() != null)
                        mCurrentLocation = enList.get(enList.size() - 1).getDaValue().getLocationItem().getLocation();
                        if(mCurrentLocation != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(15)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }else{
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(15)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            Toast.makeText(getActivity(), "Có lối xảy ra khi dò vị trí mới nhập dữ liệu gần đây nhất, tự động hiển thị vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(location != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(15)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            Toast.makeText(getActivity(), "Chưa có dữ liệu hạng mục nào, tự động hiển thị vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                addIcon();
            }
        }
    };


    private void summaryUserLocation(){

    }

    List<EnDataModel> enList;
    private void summaryData(){
        Gson gson = new Gson();
        enList = new ArrayList<>();
        List<Data> listData = DatabaseHelper.getData();
        if (listData != null) {
            if (listData.size() != 0) {
                for (Data d : listData) {
                    enList.add(gson.fromJson(d.getInput(), EnDataModel.class));
                }
            }
        }
    }
    private void addIcon() {

        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        if(enList.size() > 0){
            for(int i = 0 ; i < enList.size(); i++){
                EnDataModel e = enList.get(i);
                if(e.getDaValue().getLocationItem() != null && e.getDaValue().getLocationItem().getLocation() != null){
                    MarkerOptions options = new MarkerOptions();
                    options.title("Danh mục : " + e.getDaValue().getAction());
                    String promptItem;
                    String status;
                    String infor;
                    try {
                        promptItem = e.getDaValue().getDataTypeName().equals("") ? "Chưa có dữ liệu" : e.getDaValue().getDataTypeName();
                    }catch(Exception ex){
                        promptItem = "Chưa có dữ liệu";
                    }

                    try {
                        status = e.getDaValue().getMoTaTinhTrang().equals("") ? "Chưa có dữ liệu" : e.getDaValue().getThangDanhGia();
                    }catch(Exception ex){
                        status = "Chưa có dữ liệu";
                    }

                    try {
                        infor = e.getDaValue().getMoTaTinhTrang().equals("") ? "Chưa có dữ liệu" : e.getDaValue().getMoTaTinhTrang();
                    }catch(Exception ex){
                        infor = "Chưa có dữ liệu";
                    }
                    options.snippet("Hạng mục : " + promptItem
                            + "\n Mức độ: " + status
                            + "\n Đánh giá: " + infor);
                    options.position(new LatLng(e.getDaValue().getLocationItem().getLocation().getLatitude(), e.getDaValue().getLocationItem().getLocation().getLongitude()));

                    options.draggable(true);
                    options.icon(icon);
                    Marker marker = gMap.addMarker(options);
                    listMarkerData.put(marker, e);
                    System.out.println("data :" + e.getDaValue().getDataTypeName());
                }
                gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getActivity());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getActivity());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getActivity());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
            }
            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Iterator myVeryOwnIterator = listMarkerData.keySet().iterator();
                    while(myVeryOwnIterator.hasNext()) {
                        Marker markerKey = (Marker) myVeryOwnIterator.next();
                        if(markerKey.getTitle().trim().equals(marker.getTitle().trim())
                            && markerKey.getTitle().trim().equals(marker.getTitle().trim())) {
                            System.out.println("marker found: -----------");
                            EnDataModel statusData = (EnDataModel) listMarkerData.get(markerKey);
                            FragmentViewFullReport reportInformation = new FragmentViewFullReport();
                            reportInformation.setData(statusData);
                            reportInformation.show(getChildFragmentManager(), "test");
                        }
                    }
                }
            });
        }
        if(dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(dialog != null)
            dialog.dismiss();
    }
}
