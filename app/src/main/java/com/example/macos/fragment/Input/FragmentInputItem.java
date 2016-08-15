package com.example.macos.fragment.Input;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.gson.Gson;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentInputItem extends CustomFragment{
    private View rootView;
    private ImageView imgDone;
    private LinearLayout  lnlAll, container;
    private List<LinearLayout> listData;
    private EditText edtComment;
    private TextView tvCurrentLocation, tvRoadNameEntered;
    private LinearLayoutThatDetectsSoftKeyboard linearLayoutThatDetectsSoftKeyboard;
    private LinearLayout bottomView;
    private int ORDER_CAMERA_POSITION = 0;
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

    public String getCatalog(){
        return catalog;
    }
    public String getSummary(){
        return edtComment.getText().toString();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_road_surface, container, false);
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
        tvCurrentLocation.setText("Vị trí hiện tại: " + lo.getAddress());
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
        bottomView =  (LinearLayout)rootView.findViewById(R.id.bottomView);
        scroll = (NestedScrollView) rootView.findViewById(R.id.scroll);

        imgDone = (ImageView) rootView.findViewById(R.id.imgDone);
        edtComment = (EditText) rootView.findViewById(R.id.edtComment);
        FunctionUtils.setupEdittext(edtComment, getActivity());

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

        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.hideSoftInput(rootView, getActivity());
                FunctionUtils.showConfirmDialog(getResources().getString(R.string.bancochacchandanhapdayduthongtin), getActivity(), dialogAction);
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
        ImageView imgCameraRoadName = (ImageView) container.findViewById(R.id.imgCameraRoadName);
        ImageView imgVoidRoadName = (ImageView) container.findViewById(R.id.imgVoidRoadName);
        final ImageView imgEditRoadName = (ImageView) container.findViewById(R.id.imgEditRoadName);
        final ImageView imgDeleteRoadName = (ImageView) container.findViewById(R.id.imgDeleteRoadName);
        final EditText edtInformation = (EditText) container.findViewById(R.id.edtInformation);
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

        final int ORDER_CAMERA_POSITION = listData.size();

        imgAddRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContainerInput(container);

                if(spinPromtCatalog.getError() == null && spinStatus.getError() == null) {
                    imgAddRoadName.setImageResource(0);
                    imgAddRoadName.setImageDrawable(null);
                    imgAddRoadName.setTag("noMoreAdd");
                    addNewContainer();
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

        imgEditRoadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit button work only when add button disapear and change to own tag :'noMoreAdd'
                if(imgAddRoadName.getTag() != null && imgAddRoadName.getTag().equals("noMoreAdd")) {
                    if (imgEditRoadName.getTag().toString().equals("edit")) {
                        disableNestedData(listData.get(ORDER_CAMERA_POSITION), true);
                        imgEditRoadName.setTag("done");
                        imgEditRoadName.setImageResource(R.mipmap.done_black);
                        System.out.println("edit");
                    } else {
                        if (imgEditRoadName.getTag().toString().equals("done")) {
                            disableNestedData(listData.get(ORDER_CAMERA_POSITION), false);
                            imgEditRoadName.setTag("edit");
                            imgEditRoadName.setImageResource(R.mipmap.edit_black);
                            System.out.println("done");
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
                promptSpeechInput();
            }
        });
    }

    private void addNewContainer(){
        disableNestedData(((LinearLayout) listData.get(listData.size() - 1).findViewById(R.id.lnlContainerData)), false);
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

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private final int TAKE_PICTURE = 1;
    private final int REQ_CODE_SPEECH_INPUT = 2;
    private final int CHOOSEN_PICTURE = 3;
    private Uri imageUri;

    public void takePhoto(int pos) {
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
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                }
                break;
            case CHOOSEN_PICTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri>  imageUriList = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
//                    if(imageUriList.size() > GlobalParams.MAX_UPLOAD_IMAGE_AMOUNT){
//                        Toast.makeText(getActivity(), "Bạn không thể chọn nhiều hơn 3 bức ảnh!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
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
                                ((LinearLayout) listData.get(ORDER_CAMERA_POSITION).findViewById(R.id.lnlFirstPlan)).addView(lnlHorizontal);
                            }
                            getActivity().getContentResolver().notifyChange(selectedImage, null);
                            ContentResolver cr = getActivity().getContentResolver();
                            Bitmap bitmap;
                            selectedImage = Uri.parse("file://" + selectedImage);
                            try {
                                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                                //resize image
                                Bitmap b = FunctionUtils.scaleBitmap(bitmap, rootView.findViewById(R.id.viewNull).getWidth() / 3, rootView.findViewById(R.id.viewNull).getWidth() / 3);
                                final ImageView img = new ImageView(getActivity());
                                img.setImageBitmap(b);
                                img.setTag(selectedImage.toString());
                                lnlHorizontal.addView(img);
                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showImage(img);
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

    private void showImage(ImageView img){
        Intent in = new Intent(getActivity(), AcImageInformation.class);
        in.putExtra("imgRef", img.getTag().toString());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), img,
                            "viewimage");
            startActivity(in, options.toBundle());
        }else{
            startActivity(in);
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

        lnl.setClickable(b);
        lnl.setEnabled(false);
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
                    ((MaterialBetterSpinner)lnl.getChildAt(j)).setEnabled(true);
                else
                    ((MaterialBetterSpinner)lnl.getChildAt(j)).setEnabled(false);
            }

            if(lnl.getChildAt(j) instanceof ImageView){
                ImageView img = (ImageView) lnl.getChildAt(j);
                if(img.getTag() == null) {
                    img.setEnabled(b);
                    img.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            }

            if(lnl.getChildAt(j) instanceof FrameLayout) {
                FrameLayout fr = (FrameLayout) lnl.getChildAt(j);
                EditText edt = (EditText) fr.getChildAt(0);

            }

            if(lnl.getChildAt(j) instanceof RelativeLayout) {
                RelativeLayout fr = (RelativeLayout) lnl.getChildAt(j);
                EditText edt = (EditText) fr.getChildAt(1);
                edt.setEnabled(b);
                LinearLayout lnlInputIcon = (LinearLayout) fr.findViewById(R.id.lnlInputIcon);
                disableNestedData(lnlInputIcon, b);
            }

            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                disableNestedData((LinearLayout)lnl.getChildAt(j), b);
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
