package com.example.macos.fragment.report;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.activities.MainScreen;
import com.example.macos.database.DataTypeItem;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.entities.ImageModel;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devil2010 on 7/6/16.
 */
public class FragmentProblem  extends CustomFragment {
    private View rootView;
    private LinearLayout lnlALl;
    private iListWork swapInterface;
    private TextView tvCurrentLocation;
    private Button btnDone;
    private NestedScrollView scroll;
    private List<LinearLayout> listData;
    private int ORDER_CAMERA_POSITION = 0;
    private View keyBoardView;
    List<String> uriStringList;

    SupportMapFragment mSupportMapFragment;
    EnLocationItem locationItem;
    int currentHeightDiff = 0;
    int currentEditorChild = 0;
    int containerSize = 0;
    private DisplayMetrics dm;
    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }
    RoadInformation roadInformation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_accident, container, false);
        uriStringList = new ArrayList<>();
        listData = new ArrayList<>();
        dm = getResources().getDisplayMetrics();
        initLayout();
        initData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           scroll.setNestedScrollingEnabled(true);
        }

        return rootView;
    }


    private void initLayout(){
        lnlALl = (LinearLayout) rootView.findViewById(R.id.lnlAll);
        tvCurrentLocation = (TextView) rootView.findViewById(R.id.tvCurrentLocation);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        keyBoardView =  rootView.findViewById(R.id.keyBoardView);
        scroll = (NestedScrollView) rootView.findViewById(R.id.scroll);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.showConfirmDialog(getResources().getString(R.string.bancochacchandanhapdayduthongtin), getActivity(), dialogAction);
            }
        });

        //LinearLayout container = (LinearLayout) rootView.findViewById(R.id.container);
        //initContainer(container);
        //listData.add(container);
        addnewContainer((LinearLayout) scroll.getChildAt(0), null);
        currentEditorChild = 1;


        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout(){
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                final int heightDifference = screenHeight - (r.bottom - r.top);

                if(heightDifference != currentHeightDiff) {
                    if (heightDifference > 150) {
                        rootView.setPadding(0, 0, 0, heightDifference);
                        scroll.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scroll.smoothScrollBy(0, FunctionUtils.dpToPx(100, getActivity()));
                            }
                        }, 100);
                    } else {
                        rootView.setPadding(0, 0, 0, 0);
                    }
                }
                currentHeightDiff = heightDifference;
                //rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        scroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.hideSoftInput(rootView, getActivity());
            }
        });
    }

    private void addnewContainer(LinearLayout parent, LinearLayout mCurrentContainer){
        if(mCurrentContainer != null)
            disableNestedData(mCurrentContainer, false);
        LinearLayout temp = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.accident_include_layout, parent, false);
        initContainer(temp);
        lnlALl.addView(temp);
        listData.add(temp);
        currentEditorChild = listData.size();
    }

    private void initContainer(final LinearLayout container){
        final EditText edtInput = (EditText) container.findViewById(R.id.edtInput);
        final ImageView imgCamera = (ImageView) container.findViewById(R.id.imgCamera);
        final ImageView imgAdd = (ImageView) container.findViewById(R.id.imgAddRoadName);
        final ImageView imgEdit = (ImageView) container.findViewById(R.id.imgEditRoadName);
        final ImageView imgDelete = (ImageView) container.findViewById(R.id.imgDeleteRoadName);

        FunctionUtils.setupEdittext(edtInput, getActivity());
        final int ORDER_CAMERA_POSITION = listData.size();
        edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(container.getTag() != null)
                    currentEditorChild = Integer.parseInt(container.getTag().toString());
                else
                    currentEditorChild = listData.size();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(ORDER_CAMERA_POSITION);
            }
        });



        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtInput.getText().toString().trim().equals("")) {
                    imgAdd.setImageResource(0);
                    imgAdd.setImageDrawable(null);
                    imgAdd.setTag(null);
                    container.setTag(listData.size());
                    addnewContainer((LinearLayout)scroll.getChildAt(0), container);
                }else{
                    edtInput.setError("Trường báo cáo không được để trống!");
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit button work only when add button disapear
                if(imgAdd.getTag() == null) {
                    if (imgEdit.getTag().toString().equals("edit")) {
                        currentEditorChild = Integer.parseInt(container.getTag().toString());
                        disableNestedData(container, true);
                        imgEdit.setTag("done");
                        imgEdit.setImageResource(R.mipmap.done_black);
                        System.out.println("edit");
                    } else {
                        if (imgEdit.getTag().toString().equals("done")) {
                            FunctionUtils.hideSoftInput(edtInput, getActivity());
                            currentEditorChild = listData.size();
                            disableNestedData(container, false);
                            imgEdit.setTag("edit");
                            imgEdit.setImageResource(R.mipmap.edit_black);
                            System.out.println("done");
                        }
                    }
                }
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listData.size() > 1 && container.getTag() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Warning");
                    builder.setMessage(getResources().getString(R.string.bancochacchanmuonxoa));

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((LinearLayout)container.getParent()).removeView(container);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.show();

                }else{
                    Toast.makeText(getActivity(), "Không thể xoá bản ghi này!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void disableNestedData(LinearLayout lnl, boolean b){
        for (int j = 0; j < lnl.getChildCount(); j++) {
            if(lnl.getChildAt(j) instanceof EditText){
                lnl.getChildAt(j).setEnabled(b); // for edittext
                if(!b)
                    lnl.getChildAt(j).setAlpha((float)0.5);
                else
                    lnl.getChildAt(j).setAlpha(1);
            }

            if(lnl.getChildAt(j) instanceof ImageView){
                ImageView img = (ImageView) lnl.getChildAt(j);
                if(img.getTag() != null) {
                    if(img.getTag().toString().equals("camera")) {
                        img.setEnabled(b);
                        img.setVisibility(b ? View.VISIBLE : View.GONE);
                    }
                }
            }

            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                disableNestedData((LinearLayout)lnl.getChildAt(j), b);
            }
        }
    }

    public void setUpMap(){
        MapsInitializer.initialize(getActivity());
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
                                IS_FIRST_INIT_MAP = false;
                                return false;
                            }
                        });
                    }

                }
            });
        }
    }

    private void initData(){
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapp);

        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapp, mSupportMapFragment).commit();
        }

//        MapsInitializer.initialize(getActivity());
//        if (mSupportMapFragment != null) {
//            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap googleMap) {
//                    if (googleMap != null) {
//                        gMap = googleMap;
//                        gMap.setMyLocationEnabled(true);
//                        gMap.setOnMyLocationChangeListener(myLocationChangeListener);
//                    }
//
//                }
//            });
//        }
    }

    private GoogleMap gMap;
    private boolean IS_FIRST_INIT_MAP = false;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (!IS_FIRST_INIT_MAP) {
                IS_FIRST_INIT_MAP = true;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 100 , null);

                locationItem = FunctionUtils.getDataAboutLocation(location, getActivity());
                tvCurrentLocation.setText("Vị trí hiện tại: " + locationItem.getAddress());
            }
        }
    };

    DataTypeItem dataTypeItem;
    ImageModel imgModal;
    List<ImageModel> imgModalList;

    private void collectData(){
        Gson gson = new Gson();
        if(locationItem == null)
            locationItem = new EnLocationItem();
        SharedPreferenceManager pref = new SharedPreferenceManager(getActivity());
        String ROAD_NAME = pref.getString(GlobalParams.ROAD_CHOOSEN, "");
        if(ROAD_NAME != "") {
            ROAD_NAME =  gson.fromJson(ROAD_NAME, RoadInformation.class).getTenDuong();
        }


        LinearLayout lnlAll = (LinearLayout) rootView.findViewById(R.id.lnlAll);
        for (int i = 0; i < lnlAll.getChildCount(); i++) {
            dataTypeItem = new DataTypeItem();
            dataTypeItem.setAction(getResources().getString(R.string.problem));
            dataTypeItem.setLocationItem(locationItem);
            dataTypeItem.setTenDuong(ROAD_NAME);
            dataTypeItem.setDataName(getResources().getString(R.string.problem));
            dataTypeItem.setThoiGianNhap("" + System.currentTimeMillis());
            dataTypeItem.setKinhDo("" + (locationItem.getLocation() != null ? locationItem.getLocation().getLongitude() : ""));
            dataTypeItem.setViDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getLatitude() : ""));
            dataTypeItem.setMaDuong(99);
            dataTypeItem.setDataType(99);
            dataTypeItem.setCaoDo("" +  (locationItem.getLocation() != null ? locationItem.getLocation().getAltitude() : ""));
            dataTypeItem.setTuyenSo(99);
            dataTypeItem.setNguoiNhap(pref.getString(GlobalParams.USERNAME,"User"));
            dataTypeItem.setDataID((long)99);

            imgModalList = new ArrayList<>();
            LinearLayout lnl = (LinearLayout) lnlAll.getChildAt(i);
            collectNestedData(lnl);

            EnDataModel enDataModel = new EnDataModel();
            enDataModel.setDaValue(dataTypeItem);
            enDataModel.setListImageData(imgModalList);
            Logger.error("problem saved: " + enDataModel.toString());
            DatabaseHelper.insertData(gson.toJson(enDataModel));
        }



        ((MainScreen) getActivity()).initLayoutAndData();
    }



    private void collectNestedData(LinearLayout lnl){
        for (int j = 0; j < lnl.getChildCount(); j++) {
            try {
                if (lnl.getChildAt(j) instanceof EditText) {
                    String tag = ((EditText) lnl.getChildAt(j)).getTag().toString();
                    String text = ((EditText) lnl.getChildAt(j)).getText().toString();
                    if (tag.equals("information")) {     // for edittext
                        System.out.println("information" + ((EditText) lnl.getChildAt(j)).getText());
                        dataTypeItem.setMoTaTinhTrang(text);
                    }
                }

                if (lnl.getChildAt(j) instanceof ImageView) {
                    ImageView img = (ImageView) lnl.getChildAt(j);
                    if (img.getTag() != null) {
                        if (img.getTag().toString().length() > 10) {
                            Logger.error("found image: " + img.getTag().toString());
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
                System.out.println("Wrong data");
            }
        }
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
            if(b){
                collectData();
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser ) {

        }else{
        }
    }

    private final int CHOOSEN_PICTURE = 3;

    private void takePhoto(int pos){
        ORDER_CAMERA_POSITION = pos;
        Config config = new Config();
        //config.setTabBackgroundColor(R.color.colorAccent);
        //config.setTabSelectionIndicatorColor(android.R.color.holo_orange_dark);
        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(getContext(), ImagePickerActivity.class);
        startActivityForResult(intent,CHOOSEN_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSEN_PICTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri>  imageUriList = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                    LinearLayout lnlHorizontal = null;
                    int i = 0;
                    if(imageUriList.size() > 0)
                        for (Uri selectedImage : imageUriList) {
                            i++;
                            if(i == 1){
                                lnlHorizontal = new LinearLayout(getActivity());
                                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(rootView.findViewById(R.id.mapp).getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
                                lParams.gravity = Gravity.CENTER_HORIZONTAL;
                                lnlHorizontal.setLayoutParams(lParams);
                                ((LinearLayout) listData.get(ORDER_CAMERA_POSITION).findViewById(R.id.imagelist)).addView(lnlHorizontal);
                            }
                            getActivity().getContentResolver().notifyChange(selectedImage, null);
                            ContentResolver cr = getActivity().getContentResolver();
                            Bitmap bitmap;
                            selectedImage = Uri.parse("file://" + selectedImage);
                            try {
                                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                                //resize image
                                Bitmap b = FunctionUtils.scaleBitmap(bitmap, rootView.findViewById(R.id.mapp).getWidth() / 3, rootView.findViewById(R.id.mapp).getWidth() / 3);
                                ImageView img = new ImageView(getActivity());
                                img.setImageBitmap(b);
                                img.setTag(selectedImage.toString());
                                lnlHorizontal.addView(img);
                                uriStringList.add(selectedImage.toString());
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            if(i == 3)
                                i = 0;
                        }
                }
                break;
        }
    }
}