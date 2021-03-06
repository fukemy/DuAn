package com.example.macos.activities;

import android.app.ProgressDialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.macos.adapter.MainScreenAdapter;
import com.example.macos.database.BlueToothData;
import com.example.macos.database.DataTypeItem;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.Item;
import com.example.macos.database.PositionData;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.entities.EnWorkList;
import com.example.macos.entities.ImageModel;
import com.example.macos.fragment.Input.FragmentInputItem;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.interfaces.iLocationUpdate;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.WorkaroundMapFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.LocationHelper;
import com.example.macos.utilities.SharedPreferenceManager;
import com.example.macos.youtube.AcVideoList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.viewpagerindicator.TitlePageIndicator;
import com.wooplr.spotlight.SpotlightView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class AcInput extends AppCompatActivity {
    private final int ENABLE_LOCATION = 1234;
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
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_input);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        pref = new SharedPreferenceManager(AcInput.this);
        gson = new Gson();
        Gson gson = new Gson();
        SharedPreferenceManager pref = new SharedPreferenceManager(AcInput.this);
        roadInformation = gson.fromJson(pref.getString(GlobalParams.ROAD_CHOOSEN, ""), RoadInformation.class);
        dataViewList = (EnWorkList) getIntent().getExtras().getSerializable(GlobalParams.LIST_WORKING_NAME);
        ACTION_TYPE = getIntent().getStringExtra(GlobalParams.ACTION_TYPE);


        if(checkLocationEnabled()) {
            initLayout();
            locationItem = new EnLocationItem();
            dialog = new ProgressDialog(AcInput.this);
            dialog.setMessage("Đang tìm vị trí hiện tại...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        }

    }


    private boolean checkLocationEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }else{
            buildAlertMessageNoGps();
            return false;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dịch vụ định vị vị trí chưa được bật, bạn có muốn bật lên?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ENABLE_LOCATION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Intent in = new Intent(AcInput.this, MainScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(in);

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENABLE_LOCATION){
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                initLayout();
                locationItem = new EnLocationItem();
                dialog = new ProgressDialog(AcInput.this);
                dialog.setMessage("Đang tìm vị trí hiện tại...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Bật dịch vụ vị trí thất bại, hãy chú ý đợi từ 4 đến 6 giây ở màn hình kích hoạt vị trí để"
                        + " vị trí được kích hoạt trước khi trở lại màn hình nhập. Đồng thời phải đảm bảo đường truyền mạng của bạn là ổn định!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,  final int id) {
                                dialog.cancel();
                                Intent in = new Intent(AcInput.this, MainScreen.class);
                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(in);
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }
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
            FunctionUtils.showConfirmDialog(getResources().getString(R.string.bancochacchanmuonhuy), AcInput.this, new iDialogAction() {
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
            });
    }

    private void initLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                            @Override
                            public void onTouch() {
                                viewPager.requestDisallowInterceptTouchEvent(true);
                            }
                        });

                        gMap = googleMap;
                        gMap.setMyLocationEnabled(true);
                        gMap.setOnMyLocationChangeListener(myLocationChangeListener);

                        FunctionUtils.modifyMapLayout(googleMap, AcInput.this);

                        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                if(dialog != null && !dialog.isShowing())
                                    dialog.show();
                                IS_FOUND_LOCATION = false;
                                return false;
                            }
                        });
                    }

                }
            });

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
        }

        List<Item> list = dataViewList.getDataList();
        getSupportActionBar().setTitle("" + list.get(0).getItemName().toUpperCase());
        final MainScreenAdapter adapter = new MainScreenAdapter(getSupportFragmentManager());

        for(int i = 0; i< list.size() ; i++){
            FragmentInputItem f = new FragmentInputItem();
            f.setCatalog(list.get(i).getItemName());
            adapter.addFragment(f, list.get(i).getItemName());
        }

        // set data
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size() - 1);
    }

    private void addGoogleMapShowcase(){
        boolean addedShowcase = pref.getBoolean(GlobalParams.GOOGLE_MAP_CLICK_SHOWCASE, false);
        if(!addedShowcase){
            try {
                View mapView = mSupportMapFragment.getView();
                if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                    View mapButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    new SpotlightView.Builder(this)
                            .introAnimationDuration(400)
                            .enableRevalAnimation(true)
                            .performClick(true)
                            .fadeinTextDuration(400)
                            .headingTvColor(Color.parseColor("#eb273f"))
                            .headingTvSize(32)
                            .headingTvText("Định vị vị trí thất bại!")
                            .subHeadingTvColor(Color.parseColor("#ffffff"))
                            .subHeadingTvSize(16)
                            .subHeadingTvText("Hệ thống đôi lúc ko lấy được vị trí của bạn trên máy chủ google, thử lại bằng cách nhấn vào nút \""
                                    + "Vị trí của tôi\" để tìm kiếm lại!")
                            .maskColor(Color.parseColor("#dc000000"))
                            .target(mapButton)
                            .lineAnimDuration(400)
                            .lineAndArcColor(Color.parseColor("#eb273f"))
                            .dismissOnTouch(false)
                            .enableDismissAfterShown(true)
                            .usageId("mapButton")
                            .show();
                    pref.saveBoolean(GlobalParams.GOOGLE_MAP_CLICK_SHOWCASE, true);
                }
            }catch (Exception e){

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
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
        return super.onOptionsItemSelected(menuItem);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(final Location location) {
            if(!IS_FOUND_LOCATION) {
                Logger.error("found location :" + location.toString());

                LocationHelper lh = new LocationHelper();
                lh.getLocationDetail(AcInput.this, location, new iLocationUpdate() {
                    @Override
                    public void updateLocation(EnLocationItem lo) {
                        locationItem = lo;
                        ((FragmentInputItem) ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().get(0)).setCurrentLocation(locationItem);
                    }

                    @Override
                    public void onFailGetLocation() {
                        addGoogleMapShowcase();
                    }
                });
                //TODO VERY IMPORTAIN, still now I only can change location in all fragment, so hard.

//                System.out.println("got location : " + locationItem.toString());
                IS_FOUND_LOCATION = true;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(18)                   // Sets the zoom
                        .build();
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        try {
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });
            }else {

                if (location != null) {

                    //TODO VERY IMPORTAIN, still now I only can get first fragment UUID, so hard.
//                    Fragment f = ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().get(0);

                    PositionData positionData = new PositionData();
                    positionData.setId(UUID.randomUUID().toString());
                    positionData.setLattitude("" + location.getLatitude());
                    positionData.setLongitude("" + location.getLongitude());
                    positionData.setLogTime("" + System.currentTimeMillis());
                    positionData.setUserName(pref.getString(GlobalParams.USERNAME, "dungdv"));

                    Logger.error("save position update: " + positionData);

                    //MOVE CAMERA
                    DatabaseHelper.insertPositionData(positionData);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(18)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
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
                    String tag = (lnl.getChildAt(j)).getTag().toString();
                    String text = ((EditText) lnl.getChildAt(j)).getText().toString();
                    if (tag.equals("promptCatalog")) {   // for catalog spin
                        int selectionId = Integer.parseInt("" + text.charAt(0));
                        int dataId = (int) (long) dataTypeItem.getDataTypeID();
                        dataTypeItem.setDataType(selectionId + FunctionUtils.getDataTypedByDataId(dataId));
                        dataTypeItem.setDataTypeName(text.replace("" + text.charAt(0), "").replace("" + text.charAt(1), ""));
                    }

                    if (tag.equals("status")) {        // for statuc spin
                        dataTypeItem.setDanhGia(text);
                    }
                    if (tag.equals("information")) {     // for edittext
                        dataTypeItem.setMoTaTinhTrang(text);
                    }

                    if (tag.equals("justiceProcess")) {     // for edittext
                        dataTypeItem.setLyTrinh(text);
                    }

                    if (tag.equals("otherStatus")) {     // for edittext
                        if(text != null && !text.equals(""))
                            dataTypeItem.setDanhGia(text);
                    }
                }

                if (lnl.getChildAt(j) instanceof ImageView) {
                    ImageView img = (ImageView) lnl.getChildAt(j);
                    if (img.getTag() != null) {
                        if (img.getTag().toString().length() > 10) {
                            String temp = "" +  new Random().nextInt(20);
                            imgModal = new ImageModel();
                            String type = img.getTag().toString().substring(img.getTag().toString().lastIndexOf("."));
                            String name = img.getTag().toString().replace(type, "").replace("-","").replace(".","").replace("_","").replace("/","");
                            imgModal.setImageName("" + System.currentTimeMillis()
                                    + temp
                                    + name.substring(name.length() - 8, name.length() - 1)
                                    + type);
                            imgModal.setImagePath(lnl.getChildAt(j).getTag().toString()); // set path first
                            imgModal.setImageDataByte("");
                            imgModalList.add(imgModal);
                        }
                    }
                }


                if (lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                    collectNestedData((LinearLayout) lnl.getChildAt(j));
                }

                if (lnl.getChildAt(j) instanceof CardView && ((CardView) lnl.getChildAt(j)).getChildCount() > 0) {
                    collectNestedData((LinearLayout) ((CardView) lnl.getChildAt(j)).getChildAt(0));
                }

                if(lnl.getChildAt(j) instanceof HorizontalScrollView) {
                    HorizontalScrollView scroll = (HorizontalScrollView) lnl.getChildAt(j);
                    collectNestedData((LinearLayout) scroll.getChildAt(0));
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
            builder.setMessage("Hệ thống không định vị được vị trí của bạn, hãy nhấn vị trí của tôi trên bản đồ để dò lại!");
            builder.setNegativeButton("OK", null);
            builder.show();
            return;
        }

        for(int k = 0; k < ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().size(); k++){
            Fragment f = ((MainScreenAdapter) viewPager.getAdapter()).getmFragmentList().get(k);
            LinearLayout lnlAll = (LinearLayout) f.getView().findViewById(R.id.lnlAll);
            for (int i = 0; i < lnlAll.getChildCount(); i++) {
                dataTypeItem = new DataTypeItem();
                dataTypeItem.setTenDuong(roadInformation.getTenDuong());
                dataTypeItem.setLocationItem(locationItem);
                dataTypeItem.setDataName(dataViewList.getDataList().get(k).getItemName());
                dataTypeItem.setThoiGianNhap("" + System.currentTimeMillis());
                dataTypeItem.setKinhDo("" + (locationItem.getLocation() != null ? locationItem.getLocation().getLongitude() : ""));
                dataTypeItem.setViDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getLatitude() : ""));
                dataTypeItem.setMaDuong(Integer.parseInt(roadInformation.getMaDuong().trim()));
                dataTypeItem.setCaoDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getAltitude() : ""));
                dataTypeItem.setTuyenSo(Integer.parseInt(roadInformation.getTuyenSo()));
                dataTypeItem.setNguoiNhap("dungdv");
                dataTypeItem.setDataTypeID((int) (long)dataViewList.getDataList().get(k).getItemID());
                dataTypeItem.setDataID(((FragmentInputItem) f).getUUID());
                dataTypeItem.setAction(getResources().getString(R.string.road_test));

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

                Logger.error("save data success: " + enDataModel);

            }

            pref.saveBoolean(GlobalParams.IS_WORKED_TODAY, true);

            Intent in = new Intent(AcInput.this, MainScreen.class);
            in.putExtra(GlobalParams.IS_REINPUT_ROADNAME, true);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(in);
        }

    }

}
