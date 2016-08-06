package com.example.macos.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class SprashScreen extends AppCompatActivity {

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
    ProgressDialog progressFbDialog;

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
        showLogo();


        imgLogo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                imgLogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                CENTER_OF_SCREEN = dm.heightPixels / 2 - imgLogo.getMeasuredHeight();
            }
        });

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//           btLogin.setBackgroundColor(getResources().getColor(R.color.mainColor80));
//            lnlRegister.setBackgroundColor(getResources().getColor(R.color.mainColor80));
//        }

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


        final Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(SPRASH_TIME);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    if (!IS_LOGGED_ON) {
                        System.out.println("IS_LOGGED_ON: " + IS_LOGGED_ON);
                        showLogin();
                    } else {
                        LAST_LOGIN = pref.getLong(GlobalParams.LAST_LOGIN, 0);
                        Calendar now = Calendar.getInstance();
                        now.setTimeInMillis(System.currentTimeMillis());

                        Calendar last = Calendar.getInstance();
                        last.setTimeInMillis(LAST_LOGIN);
                        System.out.println("DIFF_LAST_LOGIN: " + (now.get(Calendar.DAY_OF_MONTH) - last.get(Calendar.DAY_OF_MONTH)));
                        if ((now.get(Calendar.DAY_OF_MONTH) - last.get(Calendar.DAY_OF_MONTH)) > 3) {
                            showLogin();
                        } else {
                            Intent in = new Intent(SprashScreen.this, MainScreen.class);
                            startActivity(in);
                            finish();
                        }
                        this.interrupt();
                    }

                }
            }
        };

        thread.start();
    }

    private void initLayout(){
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        lnlLogin = (LinearLayout)findViewById(R.id.lnlLogin);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        prLogin = (ProgressBar) findViewById(R.id.prLogin);
        btLogin = (LinearLayout) findViewById(R.id.lnlLogin);
        lnlRegister = (LinearLayout) findViewById(R.id.lnlRegister);
//        lnlRegister.setBackgroundColor(getResources().getColor(R.color.mainColor));
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

        final Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(LOGIN_TIME);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btLogin.setEnabled(false);
                            lnlRegister.setEnabled(false);
                        }
                    });
                    login(GlobalParams.BASED_LOGIN_URL);
                }

                }
            };
        thread.start();
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
            requestFocus(edtUsername);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
        }
    }

    private void showLogo(){
        AnimationSet setAnimShowLogo = new AnimationSet(true);
        setAnimShowLogo.setDuration(1000);
        setAnimShowLogo.setFillAfter(true);
        setAnimShowLogo.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        ScaleAnimation scaleShow = new ScaleAnimation(0f, 1.4f, 0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleShow.setInterpolator(new LinearInterpolator());
        scaleShow.setFillAfter(true);

        AlphaAnimation alphaShow = new AlphaAnimation(0, 1);
        alphaShow.setInterpolator(new LinearInterpolator());


        setAnimShowLogo.addAnimation(scaleShow);
        setAnimShowLogo.addAnimation(alphaShow);

        imgLogo.startAnimation(setAnimShowLogo);
    }

    private void showLogin(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * transate Logo to top and show login
                 */
                final AnimationSet setShowLogin = new AnimationSet(true);
                setShowLogin.setDuration(800);
                setShowLogin.setFillAfter(true);
                setShowLogin.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lnlLogin.setVisibility(View.VISIBLE);
                        lnlRegister.setVisibility(View.VISIBLE);
                        AnimationControl.AppearIcon(lnlLogin);
                    }
                });

                ScaleAnimation scaleHide = new ScaleAnimation(1.4f, 1f, 1.4f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleHide.setInterpolator(new LinearInterpolator());
                setShowLogin.addAnimation(scaleHide);

                TranslateAnimation translateHide = new TranslateAnimation(0, 0, 0, -CENTER_OF_SCREEN - 120);
                translateHide.setFillAfter(true);
                translateHide.setInterpolator(new LinearInterpolator());
                setShowLogin.addAnimation(translateHide);

                imgLogo.startAnimation(setShowLogin);
            }
        });

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

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private class LoadingUserFbAsynctask extends AsyncTask<String, Void, Bitmap> {
        Bundle bundle;
        String URL;

        public LoadingUserFbAsynctask(Bundle bundle) {
            this.bundle = bundle;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(final Bitmap result) {

            //imgLogo.setImageBitmap(result);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        pref.saveBoolean(GlobalParams.IS_LOGGED_ON, true);
                        pref.saveString(GlobalParams.USERNAME, edtUsername.getText().toString().trim());
                        pref.saveLong(GlobalParams.LAST_LOGIN, System.currentTimeMillis());
                    }catch(Exception e){

                    }finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent in = new Intent(SprashScreen.this, MainScreen.class);
                                startActivity(in);
                                progressFbDialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            }).start();


        }
    }


    public void login(String url)
    {
        FunctionUtils.hideSoftInput(edtPassword, SprashScreen.this);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            System.out.println("login status:" + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = FunctionUtils.convertStreamToString(instream).replace("\"","");
                System.out.println("login response:" + result);
                USER_TOKEN = result;
                instream.close();
                loadRoadName();
            }else{
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoadName(){
        new LoadRoadName().execute(FunctionUtils.encodeUrl(GlobalParams.BASED_GET_ROAD +  USER_TOKEN));
    }

    private void layTatCaDanhMuc(){
        pref.saveString(GlobalParams.USER_ONLY_TYPE, GlobalParams.TYPE_WIFI);
        new GetAllDanhMuc().execute(FunctionUtils.encodeUrl(GlobalParams.BASED_GET_DANHMUC +  USER_TOKEN));
    }
    class LoadRoadName extends AsyncTask<String, Void, String>{

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
            System.out.println("Road json response:" + result);
            Gson gson = new Gson();
            List<RoadInformation> roadInformationList = gson.fromJson(result,new TypeToken<List<RoadInformation>>(){}.getType());

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
            Logger.error("Road item response:" + result);
            Gson gson = new Gson();
            List<Item> catalogList = gson.fromJson(result,new TypeToken<List<Item>>(){}.getType());

            DatabaseHelper.insertListItem(catalogList);
            for(Item e : catalogList)
                Logger.error("catalog: " + e.toString());

            pref.saveBoolean(GlobalParams.IS_LOGGED_ON, true);
            pref.saveString(GlobalParams.USERNAME, edtUsername.getText().toString().trim());
            pref.saveLong(GlobalParams.LAST_LOGIN, System.currentTimeMillis());
            pref.saveString(GlobalParams.USER_TOKEN, USER_TOKEN);

            FunctionUtils.setAlarm(SprashScreen.this);

            Intent in = new Intent(SprashScreen.this, MainScreen.class);
            startActivity(in);
            finish();
        }
    }
}
