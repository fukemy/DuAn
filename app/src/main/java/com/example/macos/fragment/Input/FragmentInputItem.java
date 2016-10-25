package com.example.macos.fragment.Input;


import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.activities.AcImageInformation;
import com.example.macos.activities.AcInput;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.libraries.LinearLayoutThatDetectsSoftKeyboard;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.AsyncTaskHelper;
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.gson.Gson;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.wooplr.spotlight.SpotlightView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import tyrantgit.explosionfield.ExplosionField;

public class FragmentInputItem extends CustomFragment{
    private View rootView;
    private LinearLayout  lnlAll, container;
    private List<LinearLayout> listData;
    private TextView tvCurrentLocation, tvRoadNameEntered;
    private LinearLayoutThatDetectsSoftKeyboard linearLayoutThatDetectsSoftKeyboard;
    private FrameLayout bottomView;
    private int ORDER_CAMERA_POSITION = 0, ORDER_SPEAK_POSITION = 0;
    private View keyBoardView;
    public String catalog = "";
    public String ACTION_BUTTON_ITEM = "Done";
    private NestedScrollView scroll;
    private int currentDiffheight = 0;
    RoadInformation roadInformation;
    public void setCatalog(String s){
        this.catalog = s;
    }
    public void setActionDone(String s){
        ACTION_BUTTON_ITEM = s;
    }
    boolean isExpand = true;
    DisplayMetrics dm;
    private ImageView viewingImage;
    private ExplosionField mExplosionField;
    private SharedPreferenceManager pref;
    AsyncTaskHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_road_surface, container, false);
        mExplosionField = ExplosionField.attach2Window(getActivity());
        helper = new AsyncTaskHelper();
        dm = getResources().getDisplayMetrics();
        pref = new SharedPreferenceManager(getActivity());

        initLayout();

        LinearLayout.LayoutParams btmParams = (LinearLayout.LayoutParams)bottomView.getLayoutParams();
        btmParams.bottomMargin = FunctionUtils.dpToPx(216, getActivity());
        bottomView.setLayoutParams(btmParams);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scroll.setNestedScrollingEnabled(true);
        }

        return rootView;
    }

    public void setCurrentLocation(EnLocationItem lo){
        if(lo.getAddress() != null)
            tvCurrentLocation.setText("Vị trí hiện tại: " + lo.getAddress());
        else
            tvCurrentLocation.setText("Vị trí hiện tại: Chưa cập nhập");

    }

    private void initLayout() {
        listData = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferenceManager pref = new SharedPreferenceManager(getActivity());
        roadInformation = gson.fromJson(pref.getString(GlobalParams.ROAD_CHOOSEN, ""), RoadInformation.class);

        tvRoadNameEntered = (TextView) rootView.findViewById(R.id.tvRoadNameEntered);
        tvRoadNameEntered.setText("Tên đường đã chọn: " + roadInformation.getTenDuong());
        tvCurrentLocation = (TextView) rootView.findViewById(R.id.tvCurrentLocation);
        tvCurrentLocation.setText("Vị trí hiện tại: Đang cập nhập!");

        keyBoardView =  rootView.findViewById(R.id.keyBoardView);
        bottomView =  (FrameLayout)rootView.findViewById(R.id.lnlDone);
        scroll = (NestedScrollView) rootView.findViewById(R.id.scroll);

        bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.hideSoftInput(rootView, getActivity());
                FunctionUtils.showConfirmDialog(getResources().getString(R.string.bancochacchandanhapdayduthongtin), getActivity(), dialogAction);
            }
        });

        lnlAll = (LinearLayout) rootView.findViewById(R.id.lnlAll);
        container = (LinearLayout) rootView.findViewById(R.id.container);
        initContainer(container);
        listData.add(container);
        container.setTag(listData.size());

//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            public void onGlobalLayout(){
//                Rect r = new Rect();
//                rootView.getWindowVisibleDisplayFrame(r);
//                int screenHeight = rootView.getRootView().getHeight();
//                final int heightDifference = screenHeight - (r.bottom - r.top);
//                if(heightDifference != currentDiffheight) {
//                    if (heightDifference > 150) {
//                        rootView.setPadding(0, 0, 0, heightDifference);
//                    } else {
//                        rootView.setPadding(0, 0, 0, 0);
//                        scroll.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                            }
//                        }, 200);
//                    }
//                }
//                currentDiffheight = heightDifference;
//            }
//        });

        scroll.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("scroll click");
                        isExpand = !isExpand;
                        FunctionUtils.hideSoftInput(rootView, getActivity());
                        break;
                }
                return false;
            }
        });

        tvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpand = !isExpand;
                FunctionUtils.hideSoftInput(rootView, getActivity());
            }
        });
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
                ((AcInput) FragmentInputItem.this.getActivity()).collectAllData();
            }
        }
    };

    private void initContainer(final LinearLayout container){
        final ImageView imgAddRoadName = (ImageView) container.findViewById(R.id.imgAddRoadName);
        final ImageView imgCameraRoadName = (ImageView) container.findViewById(R.id.imgCameraRoadName);
        final ImageView imgGaleryRoadName = (ImageView) container.findViewById(R.id.imgGaleryRoadName);
        final ImageView imgVoidRoadName = (ImageView) container.findViewById(R.id.imgVoidRoadName);
        final ShineButton imgEditRoadName = (ShineButton) container.findViewById(R.id.imgEditRoadName);
        final ImageView imgDeleteRoadName = (ImageView) container.findViewById(R.id.imgDeleteRoadName);
        final EditText edtInformation = (EditText) container.findViewById(R.id.edtInformation);
        final EditText edtOtherStatus = (EditText) container.findViewById(R.id.edtOtherStatus);
        final EditText edtJusticeProcess = (EditText) container.findViewById(R.id.edtJusticeProcess);
        final TextView tvRoadName = (TextView) container.findViewById(R.id.tvRoadName);
        tvRoadName.setVisibility(View.GONE);
        edtInformation.clearFocus();
        FunctionUtils.setupEdittext(edtInformation, getActivity());
        edtInformation.clearFocus();
        String[] list;
        ArrayAdapter<String> arrayAdapter;

        final MaterialBetterSpinner spinPromtCatalog = (MaterialBetterSpinner) container.findViewById(R.id.spinPromtCatalog);
        final MaterialBetterSpinner spinStatus = (MaterialBetterSpinner) container.findViewById(R.id.spinStatus);

        spinPromtCatalog.setHint(getResources().getString(R.string.prompt_item));
        list = getResources().getStringArray(FunctionUtils.getResouceFromCatalog(getActivity(), catalog));
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        spinPromtCatalog.setAdapter(arrayAdapter);


        edtInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(container.getTag() != null)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    if(isExpand) {
                        isExpand = !isExpand;
                        //((AcInput) getActivity()).closeToolbar(false);
                    }
            }
        });
        spinStatus.setHint(getResources().getString(R.string.Status));
        list = getResources().getStringArray(R.array.chuacapnhap);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        spinStatus.setAdapter(arrayAdapter);

        spinPromtCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    if(isExpand) {
                        isExpand = !isExpand;
//                        ((AcInput) getActivity()).closeToolbar(false);
                    }
                }

                String selectedData = spinPromtCatalog.getText().toString();
                spinStatus.setText("");
                if((selectedData.charAt(selectedData.length() - 1)) != ' '){
                    String[] list = getResources().getStringArray(R.array.chuacapnhap);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
                    spinStatus.setAdapter(null);
                    spinStatus.setAdapter(arrayAdapter);
                    spinStatus.setSelection(0);
                }else{
                    int spinItemMatched = FunctionUtils.getStatusListFromPrompt(catalog,Integer.parseInt("" + selectedData.charAt(0)), getActivity());
                    String[] list = getResources().getStringArray(spinItemMatched);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
                    spinStatus.setAdapter(null);
                    spinStatus.setAdapter(arrayAdapter);
                    spinStatus.setSelection(0);
                }
            }
        });

        spinStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedData = spinStatus.getText().toString();
                if(selectedData.toLowerCase().equals(getResources().getString(R.string.other).toLowerCase())){
                    edtOtherStatus.setVisibility(View.VISIBLE);
                }else{
                    edtOtherStatus.setVisibility(View.GONE);
                }
            }
        });

        final int ORDER_CAMERA_POSITION = listData.size();
        final int ORDER_SPEAK_POSITION = listData.size();

        imgAddRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContainerInput(container);

                if(spinPromtCatalog.getError() == null && spinStatus.getError() == null) {
                    imgAddRoadName.setImageResource(0);
                    imgAddRoadName.setImageDrawable(null);
                    imgAddRoadName.setTag("noMoreAdd");
                    addNewContainer();
                    imgVoidRoadName.setVisibility(View.GONE);
                    imgCameraRoadName.setVisibility(View.GONE);
                    imgGaleryRoadName.setVisibility(View.GONE);
                    imgEditRoadName.setVisibility(View.VISIBLE);
                    imgDeleteRoadName.setVisibility(View.VISIBLE);
                    imgAddRoadName.setVisibility(View.GONE);

                    if(listData.size() == 2){
                        LinearLayout.LayoutParams btmParams = (LinearLayout.LayoutParams)bottomView.getLayoutParams();
                        btmParams.bottomMargin = 0;
                        bottomView.setLayoutParams(btmParams);
                    }
                }
            }
        });

        imgCameraRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(ORDER_CAMERA_POSITION);
            }
        });

        imgGaleryRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeMultiPhoto(ORDER_CAMERA_POSITION);
            }
        });

        imgEditRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit button work only when add button disapear and change to own tag :'noMoreAdd'
                if(imgAddRoadName.getTag() != null && imgAddRoadName.getTag().equals("noMoreAdd")) {
                    if (imgEditRoadName.getTag().toString().equals("edit")) {
                        disableNestedData(listData.get(ORDER_CAMERA_POSITION), true);
                        imgEditRoadName.setTag("done");
                        imgEditRoadName.setImageResource(R.mipmap.done_black);
                        imgVoidRoadName.setVisibility(View.VISIBLE);
                        imgCameraRoadName.setVisibility(View.VISIBLE);
                        imgGaleryRoadName.setVisibility(View.VISIBLE);
                    } else {
                        if (imgEditRoadName.getTag().toString().equals("done")) {
                            disableNestedData(listData.get(ORDER_CAMERA_POSITION), false);
                            imgEditRoadName.setTag("edit");
                            imgEditRoadName.setImageResource(R.mipmap.edit_black);
                            imgVoidRoadName.setVisibility(View.GONE);
                            imgCameraRoadName.setVisibility(View.GONE);
                            imgGaleryRoadName.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        imgDeleteRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listData.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Warning");
                    builder.setMessage(getResources().getString(R.string.bancochacchanmuonxoa));

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mExplosionField.explode(listData.get(ORDER_CAMERA_POSITION));
                            ((LinearLayout)listData.get(ORDER_CAMERA_POSITION).getParent()).removeView(listData.get(ORDER_CAMERA_POSITION));
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

        imgVoidRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(ORDER_SPEAK_POSITION);
            }
        });
    }

    private void addNewContainer(){
        disableNestedData(((LinearLayout) listData.get(listData.size() - 1).findViewById(R.id.lnlFirstPlan)), false);
        LinearLayout temp;
        temp = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.road_surface_include, null, false);
        initContainer(temp);
        lnlAll.addView(temp);
        listData.add(temp);
        temp.setTag(listData.size());
        scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }

    private void promptSpeechInput(int pos) {
        ORDER_SPEAK_POSITION = pos;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi_VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Nói và chờ ít giây đề nhập mô tả tình trạng!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private final int REQ_CODE_SPEECH_INPUT = 2;
    private final int CHOOSEN_PICTURE = 3;
    private final int SHOW_IMAGE = 5;

    private void takeMultiPhoto(int pos){
        ORDER_CAMERA_POSITION = pos;

        Config config = new Config();
        config.setSelectionLimit(4);
        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(getContext(), ImagePickerActivity.class);
        try {
            Toast.makeText(getActivity(), "Đang khởi động camera, xin chờ 1 chút cho đến khi thông báo này tắt đi!", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, CHOOSEN_PICTURE);
        }catch (Exception e){
            Toast.makeText(getActivity() , "Mở camera thất bại, có thể do hệ thống không hỗ trợ camera của phần mềm!", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePhoto(int pos) {
        ORDER_CAMERA_POSITION = pos;

        RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                Logger.error("uri: " + uri);
                uri = Uri.parse("file://" + FunctionUtils.getRealPathFromUri(getActivity(), uri));
                Logger.error("uri realpath: " + uri.getPath());
                try {
                    Bitmap b = FunctionUtils.decodeSampledBitmap(getActivity(), uri);
                    int size = rootView.findViewById(R.id.viewNull).getWidth();
//                    Bitmap decodedBitmap = Bitmap.createScaledBitmap(b, size /3, size / 3, true);
//                    Bitmap decodedBitmap = FunctionUtils.decodeSampledBitmapFromFile(uri.getPath(),  size / 3, size / 3);
                    final ImageView img = new ImageView(getActivity());
                    img.setLayoutParams(new ViewGroup.LayoutParams(size /3, size / 3));
//                    img.setImageBitmap(decodedBitmap);
                    img.setTag(uri.toString());
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                    LinearLayout lnlFirstPlan = (LinearLayout) listData.get(ORDER_CAMERA_POSITION).findViewById(R.id.lnlFirstPlan);
                    HorizontalScrollView scroll = (HorizontalScrollView) lnlFirstPlan.findViewById(R.id.scrImage);
                    ((LinearLayout) scroll.getChildAt(0)).addView(img);

                    Picasso.with(getActivity())
                            .load(uri) // Uri of the picture
                            .fit()
                            .into(img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    addTouchListenerImage(img);
                                    addImageShowcase(img);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public void addImageShowcase(ImageView img){
        boolean addedShowcase = pref.getBoolean(GlobalParams.IMAGE_SWIPE_TO_DELETE_SHOWCASE, false);
        if(!addedShowcase){
            try {
                new SpotlightView.Builder(getActivity())
                        .introAnimationDuration(400)
                        .enableRevalAnimation(true)
                        .performClick(true)
                        .fadeinTextDuration(400)
                        .headingTvColor(Color.parseColor("#eb273f"))
                        .headingTvSize(32)
                        .headingTvText("Xoá ảnh")
                        .subHeadingTvColor(Color.parseColor("#ffffff"))
                        .subHeadingTvSize(16)
                        .subHeadingTvText("Bạn có thể xoá ảnh đã chọn bằng cách nhấn vào ảnh và giữ nguyên 1 giây cho đến khi ảnh thu nhỏ lại"
                                + " ,và vuốt lên/xuống để xoá!")
                        .maskColor(Color.parseColor("#dc000000"))
                        .target(img)
                        .lineAnimDuration(400)
                        .lineAndArcColor(Color.parseColor("#eb273f"))
                        .dismissOnTouch(false)
                        .enableDismissAfterShown(true)
                        .usageId(img.getTag().toString())
                        .show();
                pref.saveBoolean(GlobalParams.IMAGE_SWIPE_TO_DELETE_SHOWCASE, true);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result != null && result.size() > 0){
                        LinearLayout lnlTemp = ((LinearLayout) listData.get(ORDER_SPEAK_POSITION).findViewById(R.id.lnlFirstPlan));
                        LinearLayout lnlInputInformation = ((LinearLayout) lnlTemp.findViewById(R.id.lnlInputInformation));
                        EditText edtInformation = (EditText) lnlInputInformation.findViewById(R.id.edtInformation);
                        edtInformation.setText(result.get(0));
                    }
                }
                break;
            case SHOW_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    boolean isDelete = data.getBooleanExtra("isDelete", false);
                    Logger.error("Show image result: " + isDelete);
                    if(isDelete){
                        if(viewingImage != null){
                            viewingImage.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mExplosionField.explode(viewingImage);
                                    ((ViewGroup) viewingImage.getParent()).removeView(viewingImage);
                                    viewingImage = null;
                                }
                            }, 100);
                        }
                    }
                }
                break;
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
                                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(rootView.findViewById(R.id.viewNull).getWidth(),
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lParams.gravity = Gravity.CENTER;
                                lnlHorizontal.setLayoutParams(lParams);
                                lnlHorizontal.setLayoutTransition(new LayoutTransition());
                                ((LinearLayout) listData.get(ORDER_CAMERA_POSITION).findViewById(R.id.lnlFirstPlan)).addView(lnlHorizontal);
                            }

                            selectedImage = Uri.parse("file://" + selectedImage);
                            try {

                                int size = rootView.findViewById(R.id.viewNull).getWidth();
//                                Bitmap b = FunctionUtils.decodeSampledBitmap(getActivity(), selectedImage);
//                                Bitmap decodedBitmap = Bitmap.createScaledBitmap(b, size /3, size / 3, true);

                                final ImageView img = new ImageView(getActivity());
                                img.setLayoutParams(new ViewGroup.LayoutParams(size /3, size / 3));
                                img.setScaleType(ImageView.ScaleType.FIT_XY);
//                                img.setImageBitmap(decodedBitmap);
                                img.setTag(selectedImage.toString());
                                lnlHorizontal.addView(img);

//                                helper.applyImage(getActivity(), img, selectedImage);
                                Picasso.with(getActivity())
                                        .load(selectedImage) // Uri of the picture
                                        .fit()
                                        .into(img, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                addTouchListenerImage(img);
                                                addImageShowcase(img);
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });

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
    private void moveImageToCurrent(final ImageView img, final int currentY){
        //img.setOnTouchListener(null);
        Logger.error("move image to current");
        img.animate().y(currentY).withEndAction(new Runnable() {
            @Override
            public void run() {
                isRunningAnimation = false;
                img.setY(currentY);
                img.setEnabled(true);
            }
        });
    }

    private void addTouchListenerImage(final ImageView img){
        img.setOnTouchListener(new View.OnTouchListener() {
            private int initialY;
            private float initialTouchY;
            int currentPosition = 0;
            int temp = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // enlarge animation
                enlargeHeight = ObjectAnimator.ofFloat(img,"scaleY", 1f, 0.7f);
                enlargeHeight.setDuration(200);
                enlargeWidth = ObjectAnimator.ofFloat(img,"scaleX", 1f, 0.7f);
                enlargeWidth.setDuration(200);


                // shink animation
                shrinkHeight = ObjectAnimator.ofFloat(img,"scaleY", 0.7f, 1f);
                shrinkHeight.setDuration(200);
                shrinkWidth = ObjectAnimator.ofFloat(img,"scaleX", 0.7f, 1f);
                shrinkWidth.setDuration(200);
                shrinkWidth.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        img.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        img.setAlpha(1f);
                        moveImageToCurrent(img, initialY);
//                        isRunningAnimation = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        temp = 0;
                        currentPosition = 0;
                        initialY = (int) img.getY();
                        initialTouchY = event.getRawY();
                        mHandler.postDelayed(myRunnable, TIME_ALPHA_LONGPRESS);
                        return true;
                    case MotionEvent.ACTION_UP:
                        scroll.requestDisallowInterceptTouchEvent(false);
                        isRunningAnimation = false;
                        if(img.getAlpha() < 0.1f || Math.abs(temp) > 200){
                            mExplosionField.explode(img);
                            ((ViewGroup) img.getParent()).removeView(img);
                        }else {
                            Logger.error("temp: " + temp + " current: " + currentPosition);
                            if (img.getAlpha() == 1f) {
                                mHandler.removeCallbacks(myRunnable);
                                img.setScaleX(1f);
                                img.setScaleY(1f);
                                showImage(img);
                            } else {
                                shinkImage();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        temp = initialY + (int) (event.getRawY() - initialTouchY);
                        if (isRunningAnimation) {
                            Logger.error("temp: " + temp);
                            float alpha = (1 - (float)Math.abs(temp) / 250);
                            if(alpha <= 1 && alpha >= 0) {
                                img.setAlpha(alpha);
                                img.setY(temp);
                            }else if(alpha > 1)
                                img.setAlpha(1f);
                            else
                                img.setAlpha(0f);
                        } else {
                            if (Math.abs(temp - currentPosition) > 15) {
                                Logger.error("removeCallbacks: temp: " + temp + " - current: " + currentPosition);
                                mHandler.removeCallbacks(myRunnable);
                            }
                        }
                        currentPosition = temp;
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.error("ACTION_CANCEL");
                        if(isRunningAnimation && ((ViewGroup) img.getParent()).getTag() != null &&
                                ((ViewGroup) img.getParent()).getTag().toString().equals("fromCamera")){
                            Logger.error("fromCamera");
                            if(img.getAlpha() < 0.1f){
                                mExplosionField.explode(img);
                                ((ViewGroup) img.getParent()).removeView(img);
                            }else{
                                shinkImage();
                                mHandler.removeCallbacks(myRunnable);
                            }
                        }else{
                            shinkImage();
                            mHandler.removeCallbacks(myRunnable);
                        }
                        return true;
                    case MotionEvent.ACTION_OUTSIDE:
                        Logger.error("ACTION_OUTSIDE");
                        mHandler.removeCallbacks(myRunnable);
                        return true;
                }
                return false;
            }
        });

    }
    boolean isRunningAnimation = false;
    private final int TIME_ALPHA_LONGPRESS = 1000;

    private void enlargeImage() {
        Logger.error("enlargeImage!");
        isRunningAnimation = true;
        enlargeWidth.start();
        enlargeHeight.start();
        scroll.requestDisallowInterceptTouchEvent(true);
    }

    private void shinkImage() {
        mHandler.removeCallbacks(myRunnable);
        shrinkWidth.start();
        shrinkHeight.start();
    }

    ObjectAnimator enlargeWidth, enlargeHeight, shrinkWidth, shrinkHeight;
    Handler mHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            Logger.error("my RUnnable!");
            enlargeImage();
        }
    };

    private void showImage(ImageView img){
        viewingImage = img;
        Intent in = new Intent(getActivity(), AcImageInformation.class);
        in.putExtra("imgRef", img.getTag().toString());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            img.setTransitionName("viewimage");
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), img,
                            "viewimage");
            startActivityForResult(in, SHOW_IMAGE, options.toBundle());
        }else{
            startActivityForResult(in, SHOW_IMAGE);
        }
    }

    private void checkContainerInput(LinearLayout lnl){
        for (int j = 0; j < lnl.getChildCount(); j++) {
            if (lnl.getChildAt(j) instanceof MaterialBetterSpinner) {
                MaterialBetterSpinner spin = (MaterialBetterSpinner) lnl.getChildAt(j);
                String value = spin.getText().toString();
                if(value.equals("")){
                    if(spin.getTag().toString().equals("promptCatalog")) {
                        spin.setError("Bạn cần chọn mục cần nhập");
                        break;
                    }

                    if(spin.getTag().toString().equals("status")) {
                        spin.setError("Bạn cần chọn tình trạng cho mục cần nhập");
                        break;
                    }

                }
            }

            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                checkContainerInput((LinearLayout)lnl.getChildAt(j));
            }
        }
    }

    private void disableNestedData(LinearLayout lnl, boolean b){

        if(lnl.getId() == R.id.lnlFirstPlan) {
//            if (!b)
//                lnl.setBackground(getResources().getDrawable(R.drawable.border_disable));
//            else
//                lnl.setBackground(getResources().getDrawable(R.drawable.border));
        }

        for (int j = 0; j < lnl.getChildCount(); j++) {
            if(lnl.getChildAt(j) instanceof EditText){
                lnl.getChildAt(j).setEnabled(b); // for edittext
            }

            if(lnl.getChildAt(j) instanceof MaterialBetterSpinner){
                if(b)
                    lnl.getChildAt(j).setEnabled(true);
                else
                    lnl.getChildAt(j).setEnabled(false);
            }

            if(lnl.getChildAt(j) instanceof ImageView){
                ImageView img = (ImageView) lnl.getChildAt(j);
                if(img.getTag() != null) {
                    if(img.getTag().toString().length() > 15) {
                        Logger.error("disable img tag: " + img.getTag().toString());
                        img.setEnabled(b);
                        img.setAlpha(b ? 1f : 0.6f);
                    }
                }
            }

//            if(lnl.getChildAt(j) instanceof RelativeLayout) {
//                RelativeLayout fr = (RelativeLayout) lnl.getChildAt(j);
//                EditText edt = (EditText) fr.getChildAt(1);
//                edt.setEnabled(b);
//                LinearLayout lnlInputIcon = (LinearLayout) fr.findViewById(R.id.lnlInputIcon);
//                disableNestedData(lnlInputIcon, b);
//            }

            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                disableNestedData((LinearLayout)lnl.getChildAt(j), b);
            }

            if(lnl.getChildAt(j) instanceof HorizontalScrollView) {
                Logger.error("found scrollview");
                HorizontalScrollView scroll = (HorizontalScrollView) lnl.getChildAt(j);
                disableNestedData((LinearLayout) scroll.getChildAt(0), b);
            }
        }
    }

    /*
    EnInputItem en;
    List<String> imgData;
    private void collectNestedData(LinearLayout lnl){
        for (int j = 0; j < lnl.getChildCount(); j++) {

            if(lnl.getChildAt(j) instanceof EditText){
                String tag = ((EditText) lnl.getChildAt(j)).getTag().toString();
                String text = ((EditText) lnl.getChildAt(j)).getText().toString();
                if(tag.equals("promptCatalog")) {   // for catalog spin
                    System.out.println("" + ((EditText) lnl.getChildAt(j)).getText());
                    en.setPromptItem(text);
                }
                if(tag.equals("status")) {        // for statuc spin
                    System.out.println("" + ((EditText) lnl.getChildAt(j)).getText());
                    en.setStatus(text);
                }
                if(tag.equals("information")) {     // for edittext
                    System.out.println("" + ((EditText) lnl.getChildAt(j)).getText());
                    en.setInformation(text);
                }
            }

            if(lnl.getChildAt(j) instanceof ImageView){
                if(lnl.getChildAt(j).getTag() != null) {
                    System.out.println("" + lnl.getChildAt(j).getTag());
                    imgData.add(lnl.getChildAt(j).getTag().toString());
                }
            }

            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                collectNestedData((LinearLayout)lnl.getChildAt(j));
            }
        }
    }

    private List<EnInputItem> collectAllData() {
        List<EnInputItem> finalData = new ArrayList<>();
        for (int i = 0; i < lnlAll.getChildCount(); i++) {
            imgData = new ArrayList<>();
            en = new EnInputItem();

            LinearLayout lnl = (LinearLayout) lnlAll.getChildAt(i);
            collectNestedData(lnl);

            en.setImgUri(imgData);
            finalData.add(en);
        }

        return finalData;
    }
    */
}
