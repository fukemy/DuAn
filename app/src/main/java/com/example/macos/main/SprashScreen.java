package com.example.macos.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.activities.MainScreen;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.Item;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.interfaces.iRequestNetwork;
import com.example.macos.libraries.Logger;
import com.example.macos.network.NetworkHelper;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.NetworkUtil;
import com.example.macos.utilities.SharedPreferenceManager;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SprashScreen extends Activity {

    private final int SPRASH_TIME = 3000;
    private final int LOGIN_TIME = 1000;
    private ImageView imgLogo;
    private LinearLayout lnlLogin;
    private ProgressBar progressBar, prLogin;
    private TextInputLayout inputUsername, inputPassword;
    private EditText edtUsername,edtPassword;
    private SharedPreferenceManager pref;
    private RelativeLayout rootView;
    private LinearLayout btLogin,lnlRegister;
    boolean IS_LOGGED_ON = false;
    long LAST_LOGIN = 0;
    int currentDiffheight = 0;
    String USER_TOKEN = "";
    int CENTER_OF_SCREEN = 0;
    DisplayMetrics dm;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sprash_screen);
        pref = new SharedPreferenceManager(SprashScreen.this);

        initLayout();

        dm = getResources().getDisplayMetrics();
        imgLogo.requestLayout();
        imgLogo.getLayoutParams().height = dm.heightPixels / 4;
        imgLogo.getLayoutParams().width = dm.heightPixels / 4;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("");
                builder.setMessage("");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        showLogo();



        imgLogo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                imgLogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                CENTER_OF_SCREEN = dm.heightPixels / 2 - imgLogo.getMeasuredHeight();
            }
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                final int heightDifference = screenHeight - (r.bottom - r.top);
                if(heightDifference != currentDiffheight) {
                    if (heightDifference > 150) {
//                        rootView.setPadding(0, 0, 0, heightDifference);
                    } else {
//                        rootView.setPadding(0, 0, 0, 0);
                        edtUsername.clearFocus();
                        edtPassword.clearFocus();
                        inputUsername.clearFocus();
                        inputPassword.clearFocus();
                    }
                }
                currentDiffheight = heightDifference;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                IS_LOGGED_ON = pref.getBoolean(GlobalParams.IS_LOGGED_ON, false);
                if (!IS_LOGGED_ON) {
                    Logger.error("IS_LOGGED_ON: " + IS_LOGGED_ON);
                    showLogin();
                } else {
                    LAST_LOGIN = pref.getLong(GlobalParams.LAST_LOGIN, 0);
                    Calendar now = Calendar.getInstance();
                    now.setTimeInMillis(System.currentTimeMillis());

                    Calendar last = Calendar.getInstance();
                    last.setTimeInMillis(LAST_LOGIN);
                    Logger.error("DIFF_LAST_LOGIN: " + (now.get(Calendar.DAY_OF_MONTH) - last.get(Calendar.DAY_OF_MONTH)));
                    int MAX_LOGIN_TIME = pref.getInt(GlobalParams.MAX_LOGIN_TIME, 9999);
                    if (Math.abs(now.get(Calendar.DAY_OF_MONTH) - last.get(Calendar.DAY_OF_MONTH)) > MAX_LOGIN_TIME) {
                        showLogin();
                    } else {
                        final Intent in = new Intent(SprashScreen.this, MainScreen.class);
                        startActivity(in);
                        finish();
                    }
                }
            }
        }, SPRASH_TIME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.error("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void initLayout(){
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        lnlLogin = (LinearLayout)findViewById(R.id.lnlLogin);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        prLogin = (ProgressBar) findViewById(R.id.prLogin);
        btLogin = (LinearLayout) findViewById(R.id.lnlLoginButton);
        lnlRegister = (LinearLayout) findViewById(R.id.lnlRegister);
        inputUsername = (TextInputLayout) findViewById(R.id.inputUsername);
        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        lnlRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SprashScreen.this, "Chưa cập nhập chức năng đăng ký!", Toast.LENGTH_SHORT).show();
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.hideSoftInput(rootView, getApplicationContext());
            }
        });

        edtUsername.addTextChangedListener(new MyTextWatcher(edtUsername));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword));

        edtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    edtUsername.clearFocus();
                    edtPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    FunctionUtils.hideSoftInput(edtPassword, SprashScreen.this);
                    btLogin.performClick();
                    return true;
                }
                return false;
            }
        });

    }

    private void submitForm() {

        edtUsername.clearFocus();
        edtPassword.clearFocus();
        if (!validateUserName()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        prLogin.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FunctionUtils.hideSoftInput(edtPassword, SprashScreen.this);
                login();
            }
        }, LOGIN_TIME);
    }

    private boolean validateUserName(){
        if (edtUsername.getText().toString().trim().isEmpty()) {
            inputUsername.setError(getString(R.string.error_blank_username));
            requestFocus(edtUsername);
            return false;
        } else {
            inputUsername.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(){
        if (edtPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.error_blank_password));
            requestFocus(edtPassword);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showLogo(){
        ViewAnimator.animate(imgLogo)
                .scale(0f, 1.4f)
                .alpha(0f, 1f)
                .duration(1000)
                .accelerate()
                .start();
    }

    private void showLogin(){
        ViewAnimator.animate(imgLogo)
                .scale(1.4f, 1f)
                .translationY(0 , - lnlLogin.getTop() - imgLogo.getHeight() - 40)
                .duration(800)
                .accelerate()
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        ViewAnimator.animate(lnlLogin)
                                .scale(0f, 1.2f, 1f)
                                .duration(500)
                                .accelerate()
                                .onStart(new AnimationListener.Start() {
                                    @Override
                                    public void onStart() {
                                        lnlLogin.setVisibility(View.VISIBLE);
                                    }
                                })
                                .start();
                    }
                })
                .thenAnimate(lnlRegister)
                .waitForHeight()
                .translationY(lnlRegister.getHeight(), 0)
                .accelerate()
                .duration(800)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        lnlRegister.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtUsername:
                    validateUserName();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;
            }
        }
    }


    public void login()
    {
        if(NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Điện thoại đang không ở chế độ trực tuyến");
            builder.setMessage("Xin vui lòng mở mạng để đăng nhập.");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", null);
            builder.show();

            prLogin.setVisibility(View.GONE);
            return;
        }

        btLogin.setEnabled(false);

        new NetworkHelper().getToken(SprashScreen.this, new iRequestNetwork() {
            @Override
            public void onSuccess(Object result) {
                USER_TOKEN = (String) result;
                loadRoadName();
            }

            @Override
            public void onFailRequest() {
                btLogin.setEnabled(true);
                Toast.makeText(SprashScreen.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                prLogin.setVisibility(View.GONE);
                return;
            }
        });
    }

    private void loadRoadName(){
        new LoadRoadName().execute(FunctionUtils.encodeUrl(GlobalParams.BASED_GET_ROAD +  USER_TOKEN));
    }

    private void layTatCaDanhMuc(){
        pref.saveString(GlobalParams.USER_ONLY_TYPE, GlobalParams.TYPE_WIFI);
        new GetAllDanhMuc().execute(FunctionUtils.encodeUrl(GlobalParams.BASED_GET_DANHMUC +  USER_TOKEN));
    }
    class LoadRoadName extends AsyncTask<String, Void, String>{

        List<RoadInformation> roadInformationList;
        @Override
        protected String doInBackground(String... urls) {
            String responseResult = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urls[0]);
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    responseResult = FunctionUtils.convertStreamToString(instream);
                    instream.close();
                }


            } catch (Exception e) {
                return "";
            }
            return responseResult;
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            roadInformationList = new ArrayList<>();
            roadInformationList = gson.fromJson(result,new TypeToken<List<RoadInformation>>(){}.getType());

            Logger.error("roadInformationList: " + roadInformationList);
            DatabaseHelper.insertListRoadInformation(roadInformationList);
            for(RoadInformation e : roadInformationList)
                Logger.error("Road: " + e.toString());

            layTatCaDanhMuc();
        }
    }

    class GetAllDanhMuc extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String responseResult = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urls[0]);
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    responseResult = FunctionUtils.convertStreamToString(instream);
                    instream.close();
                }


            } catch (Exception e) {
                return "";
            }
            return responseResult;
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            List<Item> catalogList = gson.fromJson(result,new TypeToken<List<Item>>(){}.getType());

            DatabaseHelper.insertListItem(catalogList);
            for(Item e : catalogList)
                Logger.error("catalog: " + e.toString());

            pref.saveBoolean(GlobalParams.IS_LOGGED_ON, true);
            pref.saveString(GlobalParams.USERNAME, "dungdv");
            pref.saveLong(GlobalParams.LAST_LOGIN, System.currentTimeMillis());
            pref.saveString(GlobalParams.USER_TOKEN, USER_TOKEN);


            FunctionUtils.setAlarm(SprashScreen.this);

            Intent in = new Intent(SprashScreen.this, MainScreen.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish();
        }
    }
}
