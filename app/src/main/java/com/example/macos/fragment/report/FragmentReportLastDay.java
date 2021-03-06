package com.example.macos.fragment.report;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.macos.activities.AcImageInformation;
import com.example.macos.activities.MainScreen;
import com.example.macos.database.DataTypeItem;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.gson.Gson;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by devil2010 on 7/23/16.
 */
public class FragmentReportLastDay extends CustomFragment {
    private View rootView;
    private LinearLayout lnlALl;
    private iListWork swapInterface;
    private Button btnDone;
    private NestedScrollView scroll;
    private List<LinearLayout> listData;
    private int ORDER_CAMERA_POSITION = 0, ORDER_SPEAK_POSITION = 0;
    private View keyBoardView;
    List<String> uriStringList;
    int currentHeightDiff = 0;
    int currentEditorChild = 0;
    int containerSize = 0;
    private DisplayMetrics dm;
    private ImageView viewingImage;
    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_last_day, container, false);
        uriStringList = new ArrayList<>();
        listData = new ArrayList<>();
        dm = getResources().getDisplayMetrics();
        initLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scroll.setNestedScrollingEnabled(true);
        }

        return rootView;
    }

    private void initLayout(){
        lnlALl = (LinearLayout) rootView.findViewById(R.id.lnlAll);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        keyBoardView =  rootView.findViewById(R.id.keyBoardView);
        scroll = (NestedScrollView) rootView.findViewById(R.id.scroll);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.hideSoftInput(rootView, getActivity());
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
        final ImageView imgSpeak = (ImageView) container.findViewById(R.id.imgVoidRoadName);
        final MaterialBetterSpinner spinPromtCatalog = (MaterialBetterSpinner) container.findViewById(R.id.spinPromtCatalog);
        final ImageView imgCamera = (ImageView) container.findViewById(R.id.imgCameraRoadName);
        final ImageView imgGallery = (ImageView) container.findViewById(R.id.imgGaleryRoadName);
        final ImageView imgAdd = (ImageView) container.findViewById(R.id.imgAddRoadName);
        final ImageView imgEdit = (ImageView) container.findViewById(R.id.imgEditRoadName);
        final ImageView imgDelete = (ImageView) container.findViewById(R.id.imgDeleteRoadName);

        spinPromtCatalog.setVisibility(View.GONE);
        FunctionUtils.setupEdittext(edtInput, getActivity());
        final int ORDER_CAMERA_POSITION = listData.size();
        final int ORDER_SPEAK_POSITION = listData.size();
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
        imgSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(ORDER_SPEAK_POSITION);
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtInput.getText().toString().trim().equals("")) {
                    imgAdd.setImageResource(0);
                    imgAdd.setImageDrawable(null);
                    imgAdd.setTag(null);
                    imgSpeak.setVisibility(View.GONE);
                    imgCamera.setVisibility(View.GONE);
                    imgGallery.setVisibility(View.GONE);
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
                        imgSpeak.setVisibility(View.VISIBLE);
                        imgCamera.setVisibility(View.VISIBLE);
                        imgGallery.setVisibility(View.VISIBLE);
                        System.out.println("edit");
                    } else {
                        if (imgEdit.getTag().toString().equals("done")) {
                            FunctionUtils.hideSoftInput(edtInput, getActivity());
                            currentEditorChild = listData.size();
                            disableNestedData(container, false);
                            imgEdit.setTag("edit");
                            imgEdit.setImageResource(R.mipmap.edit_black);
                            imgSpeak.setVisibility(View.GONE);
                            imgCamera.setVisibility(View.GONE);
                            imgGallery.setVisibility(View.GONE);
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

    private final int REQ_CODE_SPEECH_INPUT = 2;
    private final int SHOW_IMAGE = 5;
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
                    if(img.getTag().toString().length() > 15) {
                        img.setEnabled(b);
                        img.setAlpha(b ? 1f : 0.6f);
                    }
                }
            }

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

    DataTypeItem dataTypeItem;
    ImageModel imgModal;
    List<ImageModel> imgModalList;

    private void collectData(){
        Gson gson = new Gson();
        SharedPreferenceManager pref = new SharedPreferenceManager(getActivity());
        String ROAD_NAME = pref.getString(GlobalParams.ROAD_CHOOSEN, "");
        if(ROAD_NAME != "") {
            ROAD_NAME =  gson.fromJson(ROAD_NAME, RoadInformation.class).getTenDuong();
        }


        LinearLayout lnlAll = (LinearLayout) rootView.findViewById(R.id.lnlAll);
        for (int i = 0; i < lnlAll.getChildCount(); i++) {
            isAcceptCollectData = true;
            dataTypeItem = new DataTypeItem();
            dataTypeItem.setAction(getResources().getString(R.string.lastday));
            dataTypeItem.setLocationItem(null);
            dataTypeItem.setTenDuong(ROAD_NAME);
            dataTypeItem.setDataName(getResources().getString(R.string.report));
            dataTypeItem.setDataTypeName(getResources().getString(R.string.lastday));
            dataTypeItem.setThoiGianNhap("" + System.currentTimeMillis());
            dataTypeItem.setKinhDo(null);
            dataTypeItem.setViDo(null);
            dataTypeItem.setCaoDo(null);
            dataTypeItem.setNguoiNhap(pref.getString(GlobalParams.USERNAME,"User"));
            dataTypeItem.setMaDuong(99);
            dataTypeItem.setDataType(99);
            dataTypeItem.setTuyenSo(99);
            dataTypeItem.setDataID(FunctionUtils.generateUUID());

            imgModalList = new ArrayList<>();
            LinearLayout lnl = (LinearLayout) lnlAll.getChildAt(i);
            collectNestedData(lnl);

            EnDataModel enDataModel = new EnDataModel();
            enDataModel.setDaValue(dataTypeItem);
            enDataModel.setListImageData(imgModalList);
            if(isAcceptCollectData) {
                DatabaseHelper.insertData(gson.toJson(enDataModel));
                Logger.error("accident saved: " + enDataModel.toString());
            }
        }


        ((MainScreen) getActivity()).initLayoutAndData();
    }


    boolean isAcceptCollectData = false;
    private void collectNestedData(LinearLayout lnl){
        if(isAcceptCollectData)
            for (int j = 0; j < lnl.getChildCount(); j++) {
                try {
                    if (lnl.getChildAt(j) instanceof EditText) {
                        String tag = (lnl.getChildAt(j)).getTag().toString();
                        String text = ((EditText) lnl.getChildAt(j)).getText().toString();
                        if (tag.equals("information")) {
                            if (((EditText) lnl.getChildAt(j)).getText() != null && text.toString().length() > 0) {
                                dataTypeItem.setMoTaTinhTrang(text);

                            } else {
                                Logger.error("cac");
                                isAcceptCollectData = false;
                            }
                        }
                    }

                    if (lnl.getChildAt(j) instanceof ImageView) {
                        Logger.error("find img: "+ lnl.getChildAt(j).getTag().toString());
                        ImageView img = (ImageView) lnl.getChildAt(j);
                        if (img.getTag() != null) {
                            if (img.getTag().toString().length() > 10) {
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

                    if(lnl.getChildAt(j) instanceof HorizontalScrollView) {
                        HorizontalScrollView scroll = (HorizontalScrollView) lnl.getChildAt(j);
                        collectNestedData((LinearLayout) scroll.getChildAt(0));
                        Logger.error("found scroll");
                    }
                }catch (Exception e){
                    Logger.error("Wrong data");
                    e.printStackTrace();
                    isAcceptCollectData = false;
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

    private void moveImageToCurrent(final ImageView img, final int currentY){
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
                        if(img.getAlpha() < 0.2f || Math.abs(temp) > 250){
                            ((ViewGroup) img.getParent()).removeView(img);
                        }else {
                            Logger.error("temp: " + temp + " current: " + currentPosition);
                            if (img.getAlpha() == 1f) {
                                mHandler.removeCallbacks(myRunnable);
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
                            img.setY(temp);
                            float alpha = (1 - (float)Math.abs(temp) / 200);
                            if(alpha <= 1 && alpha >= 0)
                                img.setAlpha(alpha);
                            else if(alpha > 1)
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
                        shinkImage();
                        mHandler.removeCallbacks(myRunnable);
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
                    int size =  rootView.findViewById(R.id.edtInput).getWidth();
                    Bitmap decodedBitmap = Bitmap.createScaledBitmap(b, size /3, size / 3, true);
//                    Bitmap decodedBitmap = FunctionUtils.decodeSampledBitmapFromFile(uri.getPath(),  size / 3, size / 3);
                    final ImageView img = new ImageView(getActivity());
                    img.setImageBitmap(decodedBitmap);
                    img.setTag(uri.toString());
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    LinearLayout lnlFirstPlan = (LinearLayout) listData.get(ORDER_CAMERA_POSITION).findViewById(R.id.container);
                    HorizontalScrollView scroll = (HorizontalScrollView) lnlFirstPlan.findViewById(R.id.scrImage);
                    ((LinearLayout) scroll.getChildAt(0)).addView(img);

                    addTouchListenerImage(img);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result != null && result.size() > 0){
                        LinearLayout lnlTemp = ((LinearLayout) listData.get(ORDER_SPEAK_POSITION).findViewById(R.id.container));
                        EditText edtInformation = (EditText) lnlTemp.findViewById(R.id.edtInput);
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
                            ((ViewGroup) viewingImage.getParent()).removeView(viewingImage);
                            viewingImage = null;
                        }
                    }
                }
                break;
        }
    }
}
