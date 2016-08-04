package com.example.macos.utilities;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.libraries.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by macos on 6/16/16.
 */
public class FunctionUtils {

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public static void hideMenu(Menu menu, boolean hide){
        if(menu != null && menu.size() > 0) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(hide);
            }
            menu.findItem(R.id.done).setVisible(false);
        }
    }

    public static EnLocationItem getDataAboutLocation(Location location, Context mContext){
        EnLocationItem en = new EnLocationItem();
        String address ="My Location",city = "",state,country = "",postalCode,knownName;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                en.setAddress(strReturnedAddress.toString());
            }
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            en.setCity(addresses.get(0).getLocality());
            en.setCountry(addresses.get(0).getCountryName());
            en.setKnownName(addresses.get(0).getFeatureName());
            en.setPostalCode(addresses.get(0).getPostalCode());
            en.setState(addresses.get(0).getAdminArea());
            en.setLocation(location);


        }catch (Exception e){
        }

        return en;
    }

    public static int getResId(String variableName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void hideSoftInput(View view, Context mContext){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftInput(View view, Context mContext){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public static void showErrorDialog(String error, Context mContext, final iDialogAction dialogInterface){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Warning");
        builder.setMessage(error);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogInterface.showRoadNameInputDialog();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogInterface.refreshViewonly();
            }
        });
        builder.show();
    }

    public static void setupKeyboard(final EditText edt, final Context mContext){
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(edt.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    public static void showConfirmDialog(String content, Context mContext, final iDialogAction dialogInterface){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm");
        builder.setMessage(content);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogInterface != null)
                 dialogInterface.isAcceptWarning(true);
            }
        });

        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    public static String timeStampToTime(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date;
    }

    public static String timeStampToTimeOnly(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return date;
    }

    public static int dpToPx(int dp, Context mContext) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
        return px;
    }

    public static int pxToDp(int px, Context mContext) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
//        return dp;
    }

    public static int getResouceFromCatalog(Context mContext, String catalog){
        if (catalog.equals(mContext.getResources().getString(R.string.road_surface)))
            return  R.array.road_surface_list;
        else if (catalog.equals(mContext.getResources().getString(R.string.roadbed)))
            return  R.array.nenduong;
        else if (catalog.equals(mContext.getResources().getString(R.string.divider)))
            return  R.array.daiphancach;
        else if (catalog.equals(mContext.getResources().getString(R.string.underground_drain_people)))
            return  R.array.congchuidansinh;
        else if (catalog.equals(mContext.getResources().getString(R.string.drainage_box_culvert)))
            return  R.array.conghopvacongbanthoatnuoc;
        else if (catalog.equals(mContext.getResources().getString(R.string.culverts)))
            return  R.array.congtron;
        else if (catalog.equals(mContext.getResources().getString(R.string.slopes)))
            return  R.array.maidoc;
        else if (catalog.equals(mContext.getResources().getString(R.string.retaining_walls)))
            return  R.array.tuongchan;
        else if (catalog.equals(mContext.getResources().getString(R.string.protective_barrier)))
            return  R.array.hangraobaove;
        else if (catalog.equals(mContext.getResources().getString(R.string.lighting_Systems)))
            return  R.array.hethongchieusangduong;
        else if (catalog.equals(mContext.getResources().getString(R.string.notice_board)))
            return  R.array.bienbaoduong;
        else if (catalog.equals(mContext.getResources().getString(R.string.road_markings)))
            return  R.array.vachsonduong;
        else if (catalog.equals(mContext.getResources().getString(R.string.column_Km)))
            return  R.array.cotkm;
        else if (catalog.equals(mContext.getResources().getString(R.string.bridge)))
            return  R.array.cau;
        else if (catalog.equals(mContext.getResources().getString(R.string.marker)))
            return  R.array.coctieu;
        else if (catalog.equals(mContext.getResources().getString(R.string.manhole_sumps)))
            return  R.array.hogahothu;
        else if (catalog.equals(mContext.getResources().getString(R.string.longitudinal_grooves_groove_Border)))
            return  R.array.ranhdocranhbien;
        else if (catalog.equals(mContext.getResources().getString(R.string.vertical_drain)))
            return  R.array.congdoc;
        else
                return R.array.nenduong;
    }

    public static void revertCursoOfEditText(int heightDifference, ScrollView scroll, final LinearLayout lnl, Context mContext){
        for (int j = 0; j < lnl.getChildCount(); j++) {

            if(lnl.getChildAt(j) instanceof EditText){
                EditText edt = (EditText) lnl.getChildAt(j);
            }


            if(lnl.getChildAt(j) instanceof LinearLayout && ((LinearLayout) lnl.getChildAt(j)).getChildCount() > 0) {
                revertCursoOfEditText(heightDifference, scroll, (LinearLayout)lnl.getChildAt(j), mContext);
            }

            if(lnl.getChildAt(j) instanceof RelativeLayout) {
                RelativeLayout fr = (RelativeLayout) lnl.getChildAt(j);
                EditText edt = (EditText) fr.getChildAt(0);

                LinearLayout lnlInputIcon = (LinearLayout) fr.findViewById(R.id.lnlInputIcon);
                if(edt.isFocused()) {
                    ((LinearLayout) scroll.getParent()).setPadding(0,0,0,heightDifference + dpToPx(lnlInputIcon.getHeight() + 20, mContext));
                }


            }
        }
    }

    public static int getStatusListFromPrompt(String catalog, int pos, Context mContext){
        int returnId =  R.array.chuacodulieu;
        if (catalog.equals(mContext.getResources().getString(R.string.road_surface))) {
            //if(pos == 2 || pos == 4 || pos == 7) {
                returnId = R.array.road_surface_status_list;
            //}
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.roadbed))) {
            if(pos == 3)
                returnId = R.array.doondinhcuanenduong;
            if(pos == 4)
                returnId = R.array.congtrinhbaovemaidocnenduong;
            if(pos == 5)
                returnId = R.array.maitaluynenduong;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.divider))){
            if(pos == 2)
                returnId = R.array.huhongdoxevaquet;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.underground_drain_people))){
            if(pos == 4)
                returnId = R.array.huhongketcaubetongcongchuidansinh;
            if(pos == 5)
                returnId = R.array.khuyettatcongchuidansinh;
            if(pos == 6)
                returnId = R.array.xuongcapcongchuidansinh;
            if(pos == 15)
                returnId = R.array.thoatnuoccongchuidansinh;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.drainage_box_culvert))){

            if(pos == 5)
                returnId = R.array.huhongketcaubetongcongchuidansinh;
            if(pos == 6)
                returnId = R.array.khuyettatcongchuidansinh;
            if(pos == 7)
                returnId = R.array.xuongcapcongchuidansinh;
            if(pos == 16)
                returnId = R.array.thoatnuoccongchuidansinh;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.culverts))){
            if(pos == 4)
                returnId = R.array.huhongketcaubetongcongchuidansinh;
            if(pos == 5)
                returnId = R.array.khuyettatcongchuidansinh;
            if(pos == 6)
                returnId = R.array.xuongcapcongchuidansinh;
            if(pos == 15)
                returnId = R.array.thoatnuoccongchuidansinh;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.slopes))){
            if(pos == 3)
                returnId = R.array.khanangthoatnuocmaidoc;
            if(pos == 5)
                returnId = R.array.caccongtrinhbaovemaidoc;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.retaining_walls))){
            if(pos == 5)
                returnId = R.array.bemattuongchan;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.protective_barrier))){
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.lighting_Systems))){
            if(pos == 6)
                returnId = R.array.thancothethongchieusangduong;
            if(pos == 4)
                returnId = R.array.bulongvadaiochethongchieusangduong;
            if(pos == 5)
                returnId = R.array.bongdenhethongchieusangduong;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.notice_board))){
            if(pos == 1)
                returnId = R.array.huhongbuibanbienbaoduong;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.road_markings))){
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.column_Km))){}
        else if (catalog.equals(mContext.getResources().getString(R.string.bridge))){
            if(pos == 1)
                returnId = R.array.goicau;
            if(pos == 2)
                returnId = R.array.khecodancau;
            if(pos == 3)
                returnId = R.array.lancanraochancau;
            if(pos == 4)
                returnId = R.array.thoatnuoccau;
            if(pos == 5)
                returnId = R.array.ketcaucuabetongphantrencau;
            if(pos == 6)
                returnId = R.array.ketcaucuabetongphanduoicau;
            if(pos == 7)
                returnId = R.array.matduongtrencau;
            if(pos == 8)
                returnId = R.array.daiphancachgiuacau;
            if(pos == 9)
                returnId = R.array.bienbaotrencau;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.marker))){
            if(pos == 2)
                returnId = R.array.huhongbiendangcoctieu;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.manhole_sumps))){
            if(pos == 2)
                returnId = R.array.huhongbiendanghogahothu;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.longitudinal_grooves_groove_Border))){
            if(pos == 2)
                returnId = R.array.huhongbiendangranhdocranhbien;
        }
        else if (catalog.equals(mContext.getResources().getString(R.string.vertical_drain))){
            if(pos == 2) {
                returnId = R.array.huhongbiendangcongdoc;
            }else{
                returnId = R.array.chuacapnhap;
            }
        }
        return returnId;
    }

    public static int getCurrentCursorLine(EditText editText)
    {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (!(selectionStart == -1)) {
            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    public static void setupEdittext(final EditText edt, final Context mContext){

        edt.addTextChangedListener(new TextWatcher() {
            long mCurrentTime = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * The loop is in reverse for a purpose,
                 * each replace or delete call on the Editable will cause
                 * the afterTextChanged method to be called again.
                 * Hence the return statement after the first removal.
                 * http://developer.android.com/reference/android/text/TextWatcher.html#afterTextChanged(android.text.Editable)
                 */
                //if((edt.getLineCount() - 1) >= edt.getMaxLines()) {
                    for (int i = s.length() - 1; i > 0; i--) {
                        if (s.charAt(i) == '\n' && s.charAt(i - 1) == '\n') {
                            s.delete(i, i + 1);
                            FunctionUtils.hideSoftInput(edt, mContext);
                            /*
                            if (getCurrentCursorLine(edt) == (edt.getMaxLines() - 1)) {
                                FunctionUtils.hideSoftInput(edt, mContext);
                                return;
                            }

                            if((edt.getLineCount() - 1) >= edt.getMaxLines()){

                                return;
                            }

                            if (System.currentTimeMillis() - mCurrentTime < 300) {
                                FunctionUtils.hideSoftInput(edt, mContext);
                                return;
                            }
                            */
                            return;
                        }
                    }
                //}
                mCurrentTime = System.currentTimeMillis();
            }
        });
    }

    public static void setupEdittext(final ScrollView scroll, final EditText edt, final Context mContext){

        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll.scrollTo(0, scroll.getChildAt(0).getHeight());
                            //showSoftInput(edt, mContext);
                        }
                    });
            }
        });

        edt.addTextChangedListener(new TextWatcher() {
            long mCurrentTime = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * The loop is in reverse for a purpose,
                 * each replace or delete call on the Editable will cause
                 * the afterTextChanged method to be called again.
                 * Hence the return statement after the first removal.
                 * http://developer.android.com/reference/android/text/TextWatcher.html#afterTextChanged(android.text.Editable)
                 */
                //if((edt.getLineCount() - 1) >= edt.getMaxLines()) {
                for (int i = s.length() - 1; i > 0; i--) {
                    if (s.charAt(i) == '\n' && s.charAt(i - 1) == '\n') {
                        s.delete(i, i + 1);
                        FunctionUtils.hideSoftInput(edt, mContext);
                            /*
                            if (getCurrentCursorLine(edt) == (edt.getMaxLines() - 1)) {
                                FunctionUtils.hideSoftInput(edt, mContext);
                                return;
                            }

                            if((edt.getLineCount() - 1) >= edt.getMaxLines()){

                                return;
                            }

                            if (System.currentTimeMillis() - mCurrentTime < 300) {
                                FunctionUtils.hideSoftInput(edt, mContext);
                                return;
                            }
                            */
                        return;
                    }
                }
                //}
                mCurrentTime = System.currentTimeMillis();
            }
        });
    }

    public static int getDateDiffString(Date dateOne, Date dateTwo)
    {
        long timeOne = dateOne.getTime();
        long timeTwo = dateTwo.getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;

       return (int) delta;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);

//        Logger.error("1:" + imageEncoded);
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static String encodeUrl(String link){
        URL url = null;
        URI uri = null;

        try {
            url = new URL(link);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }

        try{
            uri = new URI(url.toString());
        } catch(URISyntaxException e) {
            try {
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
                        url.getPort(), url.getPath(), url.getQuery(),
                        url.getRef());
            } catch(URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
        try {
            url = uri.toURL();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }

       return url.toString();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void setAlarm(Context context) {
        Intent i = new Intent("com.dungdv4.alarmreceiver");
        //check alarm is exist
        boolean alarmUp = (PendingIntent.getBroadcast(context, GlobalParams.NOTIFICATION_ID,
                i, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Logger.error("Alarm is already active");
        } else {
            Logger.error("setalarm functionutil");

            PendingIntent pi = PendingIntent.getBroadcast(context, GlobalParams.NOTIFICATION_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 22);  //HOUR
            calendar.set(Calendar.MINUTE, 0);       //MIN

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4000, pi);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public static int getDataTypedByDataId(int dataId){
        int id = 0;
        switch (dataId){
            case 1:
                return 0;
            case 2:
                return 8;
            case 3:
                return 13;
            case 4:
                return 16;
            case 5:
                return 31;
            case 6:
                return 47;
            case 7:
                return 62;
            case 8:
                return 67;
            case 9:
                return 75;
            case 10:
                return 79;
            case 11:
                return 86;
            case 12:
                return 89;
            case 13:
                return 92;
            case 14:
                return 95;
            case 15:
                return 104;
            case 16:
                return 108;
            case 17:
                return 115;
            case 18:
                return 120;
        }
        return id;
    }
}