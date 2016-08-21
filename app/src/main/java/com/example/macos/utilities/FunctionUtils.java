package com.example.macos.utilities;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;

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
        if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.road_surface).toLowerCase()))
            return  R.array.road_surface_list;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.roadbed).toLowerCase()))
            return  R.array.nenduong;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.divider).toLowerCase()))
            return  R.array.daiphancach;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.underground_drain_people).toLowerCase()))
            return  R.array.congchuidansinh;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.drainage_box_culvert).toLowerCase()))
            return  R.array.conghopvacongbanthoatnuoc;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.culverts).toLowerCase()))
            return  R.array.congtron;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.slopes).toLowerCase()))
            return  R.array.maidoc;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.retaining_walls).toLowerCase()))
            return  R.array.tuongchan;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.protective_barrier).toLowerCase()))
            return  R.array.hangraobaove;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.lighting_Systems).toLowerCase()))
            return  R.array.hethongchieusangduong;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.notice_board).toLowerCase()))
            return  R.array.bienbaoduong;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.road_markings).toLowerCase()))
            return  R.array.vachsonduong;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.column_Km).toLowerCase()))
            return  R.array.cotkm;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.bridge).toLowerCase()))
            return  R.array.cau;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.marker).toLowerCase()))
            return  R.array.coctieu;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.manhole_sumps).toLowerCase()))
            return  R.array.hogahothu;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.longitudinal_grooves_groove_Border).toLowerCase()))
            return  R.array.ranhdocranhbien;
        else if (catalog.toLowerCase().equals(mContext.getResources().getString(R.string.vertical_drain).toLowerCase()))
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
        int returnId = R.array.chuacapnhap;
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
        immage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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

    public static void calcelAlarm(Context context) {
        Intent i = new Intent("com.dungdv4.alarmreceiver");
        //check alarm is exist
        boolean alarmUp = (PendingIntent.getBroadcast(context, GlobalParams.NOTIFICATION_ID,
                i, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Logger.error("Alarm is already active");
            PendingIntent pi = PendingIntent.getBroadcast(context, GlobalParams.NOTIFICATION_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        } else {
            Logger.error("no alarm to cancel!");
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

    public static String convertBitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try{
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            Logger.error("exception bitmap: " + e.getMessage());
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos = new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,30, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Logger.error("EWN", "Out of memory error catched");
        }finally {
        }
        return temp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888 ;
        options.inDither = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }

    private static final int MAX_HEIGHT = 1024;
    private static final int MAX_WIDTH = 1024;
    private static Bitmap rotateImageIfRequired(Context context,Bitmap bitmap, Uri selectedImage){
        int rotation = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(selectedImage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

        // Detect rotation
//        rotation=getRotation(context, selectedImage);
//        if(rotation!=0){
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotation);
//            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
//            img.recycle();
//            return rotatedImg;
//        }else{
//            return img;
//        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Get the rotation of the last image added.
     * @param context
     * @param selectedImage
     * @return
     */
    private static int getRotation(Context context,Uri selectedImage) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();


        Cursor mediaCursor = content.query(selectedImage,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        try {
            img = rotateImageIfRequired(context, img, selectedImage);
        }catch (Exception e){
            Toast.makeText(context, "Hệ thống không thể xoay ảnh được theo đúng trạng thái hiện thị!", Toast.LENGTH_SHORT).show();
        }
        return img;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     *
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     *
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     *
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     *
     * If you are using this algorithm in your code please add
     * the following line:
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */

    public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Logger.error(w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}
