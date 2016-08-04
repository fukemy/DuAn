package com.example.macos.activities;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.macos.adapter.MainScreenAdapter;
import com.example.macos.database.DataTypeItem;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.Item;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.entities.EnWorkList;
import com.example.macos.entities.ImageModel;
import com.example.macos.fragment.Input.FragmentInputItem;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class AcInput extends FragmentActivity {

    private iListWork swapInterface;
    private GoogleMap gMap;
    private ViewPager viewPager;
    private EnWorkList dataViewList;
    private ImageView backButton;
    private boolean IS_FOUND_LOCATION = false;
    SupportMapFragment mSupportMapFragment;
    private EnLocationItem locationItem;
    private String ACTION_TYPE = "";
    private ProgressDialog dialog;
    private TitlePageIndicator title;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private SharedPreferenceManager pref;
    RoadInformation roadInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_input);

        pref = new SharedPreferenceManager(AcInput.this);
        Gson gson = new Gson();
        SharedPreferenceManager pref = new SharedPreferenceManager(AcInput.this);
        roadInformation = gson.fromJson(pref.getString(GlobalParams.ROAD_CHOOSEN, ""), RoadInformation.class);
        dataViewList = (EnWorkList) getIntent().getExtras().getSerializable(GlobalParams.LIST_WORKING_NAME);
        ACTION_TYPE = getIntent().getStringExtra(GlobalParams.ACTION_TYPE);
        initLayout();

        findViewById(R.id.backButton).bringToFront();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
        }

        locationItem = new EnLocationItem();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tìm vị trí hiện tại...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public String getACTION_TYPE() {
        return ACTION_TYPE;
    }

    public EnLocationItem getLocationItem(){
        return locationItem;
    }

    boolean isBack = false;
    @Override
    public void onBackPressed() {
        if(isBack) {
            Intent in = new Intent(AcInput.this, MainScreen.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(in);
        }
        else
            FunctionUtils.showConfirmDialog(getResources().getString(R.string.bancochacchanmuonhuy), AcInput.this, dialogAction);
    }

    iDialogAction dialogAction = new iDialogAction() {
        @Override
        public void showRoadNameInputDialog() {

        }

        @Override
        public void refreshViewonly() {

        }

        @Override
        public void isAcceptWarning(boolean b) {
            if(b) {
                isBack = true;
                onBackPressed();
            }
        }
    };

    Thread t;
    public void closeToolbar(final boolean isClose){
        if(t != null) {
            if (t.isAlive()) {
            }
            else
                t = null;
        }
        if(t == null) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                    if (behavior != null) {
                        final ValueAnimator valueAnimator = ValueAnimator.ofInt();
                        valueAnimator.setInterpolator(new DecelerateInterpolator());
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                                appBarLayout.requestLayout();
                            }
                        });
                        if (isClose)
                            valueAnimator.setIntValues(0, -appBarLayout.getHeight());
                        else
                            valueAnimator.setIntValues(0, appBarLayout.getHeight() * 2);

                        valueAnimator.setDuration(400);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                valueAnimator.start();
                            }
                        });

                    }
                }
            });
            t.start();
        }
    }

    private void initLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AcInput.this);
                builder.setTitle("Warning");
                builder.setMessage(getResources().getString(R.string.bancochacchanmuonthoat));

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent in = new Intent(AcInput.this, MainScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(in);
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();


        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.mapp, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        gMap = googleMap;
                        gMap.setMyLocationEnabled(true);
                        gMap.setOnMyLocationChangeListener(myLocationChangeListener);

                        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                IS_FOUND_LOCATION = false;
                                return false;
                            }
                        });
                    }

                }
            });

        }

        List<Item> list = dataViewList.getDataList();
        final MainScreenAdapter adapter = new MainScreenAdapter(getSupportFragmentManager());

        // init data

        for(int i = 0; i< list.size() ; i++){
            FragmentInputItem f = new FragmentInputItem();
            if(i == list.size() - 1)
                f.setActionDone("Done");
            else
                f.setActionDone("Next");

            f.setCatalog(list.get(i).getItemName());
            adapter.addFragment(f, list.get(i).getItemName());
        }

        // set data
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size() - 1);
        title = (TitlePageIndicator) findViewById(R.id.titles);
        title.setViewPager(viewPager);

        title.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(locationItem != null){
                    ((FragmentInputItem)adapter.getmFragmentList().get(position)).setCurrentLocation(locationItem);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if(!IS_FOUND_LOCATION) {
                title.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                dialog.dismiss();
                locationItem = FunctionUtils.getDataAboutLocation(location, AcInput.this);
                ((FragmentInputItem)((MainScreenAdapter)viewPager.getAdapter()).getmFragmentList().get(0)).setCurrentLocation(locationItem);
                System.out.println("got location : " + locationItem.toString());
                IS_FOUND_LOCATION = true;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    };

    DataTypeItem dataTypeItem;
    ImageModel imgModal;
    List<ImageModel> imgModalList;
    private void collectNestedData(LinearLayout lnl){
        for (int j = 0; j < lnl.getChildCount(); j++) {
            /**
             * BUG : Catched MaterialBetterSpinner and Edittext have the same instance, so duplicate call MaterialBetterSpinner two times
             * FIX: only call instance of EditText and check TAG
             */
            try {
                if (lnl.getChildAt(j) instanceof EditText) {
                    String tag = ((EditText) lnl.getChildAt(j)).getTag().toString();
                    String text = ((EditText) lnl.getChildAt(j)).getText().toString();
                    if (tag.equals("promptCatalog")) {   // for catalog spin
                        int selectionId = Integer.parseInt("" + text.charAt(0));
                        int dataId = (int) (long) dataTypeItem.getDataID();
                        dataTypeItem.setDataType(selectionId + FunctionUtils.getDataTypedByDataId(dataId));
                        dataTypeItem.setDataTypeName(text.replace("" + text.charAt(0), "").replace("" + text.charAt(1), ""));
                    }

                    if (tag.equals("status")) {        // for statuc spin
                        dataTypeItem.setThangDanhGia(text);
                    }
                    if (tag.equals("information")) {     // for edittext
                        dataTypeItem.setMoTaTinhTrang(text);
                    }
                }

                if (lnl.getChildAt(j) instanceof ImageView) {
                    ImageView img = (ImageView) lnl.getChildAt(j);
                    if (img.getTag() != null) {
                        if (img.getTag().toString().length() > 10) {
                            //imgData.add(img.getTag().toString());
                            imgModal = new ImageModel();
                            imgModal.setImageName(System.currentTimeMillis() + img.getTag().toString().substring(img.getTag().toString().lastIndexOf(".")));
                            imgModal.setImagePath(lnl.getChildAt(j).getTag().toString()); // set path first
                            imgModal.setImageDataByte("");
                            imgModalList.add(imgModal);
                        }
                    }
                }


                if (lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                    collectNestedData((LinearLayout) lnl.getChildAt(j));
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.error("Wrong data");
            }
        }
    }

    public void collectAllData() {
        if(locationItem.getLocation() == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(AcInput.this);
            builder.setTitle("Lưu dữ liệu thất bại!");
            builder.setMessage("Chúng tôi ko tìm được vị trí của bạn, hãy kiểm tra lại vị trí trên bản đồ!");
            builder.setNegativeButton("OK", null);
            builder.show();
            return;

        }

        Gson gson = new Gson();
        for(int k = 0; k < ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().size(); k++){
            Fragment f = ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().get(k);
            LinearLayout lnlAll = (LinearLayout) f.getView().findViewById(R.id.lnlAll);
            for (int i = 0; i < lnlAll.getChildCount(); i++) {
                dataTypeItem = new DataTypeItem();
                dataTypeItem.setLocationItem(locationItem);
                dataTypeItem.setTenDuong(roadInformation.getTenDuong());
                dataTypeItem.setDataName(dataViewList.getDataList().get(k).getItemName());
                dataTypeItem.setThoiGianNhap("" + System.currentTimeMillis());
                dataTypeItem.setAction(getResources().getString(R.string.road_test));
                dataTypeItem.setKinhDo("" + (locationItem.getLocation() != null ? locationItem.getLocation().getLongitude() : ""));
                dataTypeItem.setViDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getLatitude() : ""));
                dataTypeItem.setMaDuong(Integer.parseInt(roadInformation.getMaDuong().trim()));
                dataTypeItem.setCaoDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getAltitude() : ""));
                dataTypeItem.setTuyenSo(Integer.parseInt(roadInformation.getTuyenSo()));
                dataTypeItem.setNguoiNhap(pref.getString(GlobalParams.USERNAME,"User"));
                dataTypeItem.setDataID(dataViewList.getDataList().get(k).getItemID());

                imgModalList = new ArrayList<>();
                LinearLayout lnl = (LinearLayout) lnlAll.getChildAt(i);
                if(lnl != null) {
                    collectNestedData(lnl);
                }

                if(dataTypeItem.getDataType() == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AcInput.this);
                    builder.setTitle("Lưu dữ liệu thất bại!");
                    builder.setMessage("Bạn phải chọn hạng mục trước khi lưu!");
                    builder.setNegativeButton("OK", null);
                    builder.show();
                    return;
                }
                EnDataModel enDataModel = new EnDataModel();
                enDataModel.setDaValue(dataTypeItem);
                enDataModel.setListImageData(imgModalList);
                DatabaseHelper.insertData(gson.toJson(enDataModel));

                Logger.error("dataTypeItem:  " + enDataModel.toString());
            }
            pref.saveBoolean(GlobalParams.IS_WORKED_TODAY, true);
        }
        Intent in = new Intent(AcInput.this, MainScreen.class);
        in.putExtra(GlobalParams.IS_REINPUT_ROADNAME, true);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(in);

    }
}