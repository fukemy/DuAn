package com.example.macos.activities;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.IciTest.VirableDataGenerator;
import com.example.macos.adapter.DeviceListActivity;
import com.example.macos.database.BlueToothData;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.fragment.report.FragmentReportStatus;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.WorkaroundMapFragment;
import com.example.macos.report.GraphReport;
import com.example.macos.service.UartService;
import com.example.macos.utilities.AlertUtil;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Microsoft on 11/23/16.
 */

public class AcICIChecking extends AppCompatActivity {
    private TextView tvTotalDistance, tvProblemFound, tvTime;
    private ScrollView scrollContainer;
    private ImageButton btnBack;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private final int ENABLE_LOCATION = 1234;;
    SupportMapFragment mSupportMapFragment;
    private GoogleMap gMap;
    String USER_TOKEN;
    List<BlueToothData> blueToothDatas;
    ProgressDialog dialog;
    //UART STATUS
    private static final int REQUEST_SELECT_DEVICE = 11;
    private static final int REQUEST_ENABLE_BT = 12;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private String UUIDData;
    boolean isCreateNewBlueToothData;
    boolean isManualDisconnectBluetooth;

    //GRAPH VIEW PROPERTY
    String dataUUID;
    long[] dp;
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    SharedPreferenceManager pref;


    //FOR SENSOR MANAGER
    private SensorManager sensor;
    private List list;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_ici);

        isManualDisconnectBluetooth = false;
        pref = new SharedPreferenceManager(this);

        isCreateNewBlueToothData = false;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initLayout();
        initGraphView();
        initGoogleMap();
        if(!FunctionUtils.checkLocationEnabled(this)){
            buildAlertMessageNoGps();
        }else{
            initBlueTooth();
        }
    }

    private void initLayout(){
        tvTotalDistance = (TextView) findViewById(R.id.tvTotalDistance);
        tvProblemFound = (TextView) findViewById(R.id.tvProblemFounded);
        tvTime = (TextView) findViewById(R.id.tvTime);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);


        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blueToothDatas.size() > 0 && isCreateNewBlueToothData) {
                    isManualDisconnectBluetooth = true;
                    mService.disconnect();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AcICIChecking.this);
                    builder.setTitle("Chú ý");
                    builder.setMessage("Thoát màn hình này sẽ mất hết dữ liệu bạn vừa đo được.\nBạn có muốn upload dữ liệu lên server ko?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    getToken();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(blueToothDatas == null)
             super.onBackPressed();
        else {
            if (blueToothDatas.size() > 0 && isCreateNewBlueToothData) {
                isManualDisconnectBluetooth = true;
                mService.disconnect();
                final AlertDialog.Builder builder = new AlertDialog.Builder(AcICIChecking.this);
                builder.setTitle("Chú ý");
                builder.setMessage("Thoát màn hình này sẽ mất hết dữ liệu bạn vừa đo được.\nBạn có muốn upload dữ liệu lên server ko?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                getToken();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                finish();
            }
        }
    }

    private void getToken(){
        dialog = new ProgressDialog(AcICIChecking.this);
        dialog.setMessage("Đang lấy token từ server!");
        dialog.show();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(GlobalParams.BASED_LOGIN_URL);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = FunctionUtils.convertStreamToString(instream).replace("\"","");
                Logger.error("login token:" + result);
                USER_TOKEN = result;
                pref.saveString(GlobalParams.USER_TOKEN, USER_TOKEN);
                instream.close();

                new UploadBlueToothData().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(dialog != null)
                if(dialog.isShowing())
                    dialog.dismiss();
            Logger.error("upload bluetooth fail");
            final AlertDialog.Builder builder = new AlertDialog.Builder(AcICIChecking.this);
            builder.setTitle("Báo cáo!");
            builder.setMessage("Upload dữ liệu thất bại! Ấn \"OK\" để tắt thông báo này.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,  final int id) {
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }
    /*
        UPLOAD VIBERATION DATA VIA BLUETOOTH
     */
    private class UploadBlueToothData extends AsyncTask<Void, Void, String> {
        private String result;
        private List<BlueToothData> blueToothData;
        String url;
        Gson gson = new Gson();

        public UploadBlueToothData(){
            this.result = "";
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_UPLOAD_BLUETOOTH_URL + USER_TOKEN);
            Logger.error("Url bluetooth to upload: " + url);
        }
        @Override
        protected void onPreExecute() {
            if(dialog != null)
                if(dialog.isShowing())
                    dialog.setMessage("Đang upload dữ liệu độ sóc!");

            Logger.error("bluetooth data to upload: " + gson.toJson(blueToothDatas));
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setHeader("Accept","application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity(
//                        "[\n" +
                                gson.toJson(blueToothDatas)
//                                + "\n]"
                        , HTTP.UTF_8);
                post.setEntity(entityData);
                response = httpclient.execute(post);
                Logger.error("status code:" + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = FunctionUtils.convertStreamToString(instream);
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("exception:  " + e.getMessage());
                return "wrong";
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            if(dialog != null)
                if(dialog.isShowing())
                    dialog.dismiss();
            Logger.error("result upload bluetooth: " + result);
            if (result.contains("Successfull") || result.contains("Image List")) {
               Logger.error("upload bluetooth success");
                final AlertDialog.Builder builder = new AlertDialog.Builder(AcICIChecking.this);
                builder.setTitle("Chú ý");
                builder.setMessage("Dữ liệu đã được upload thành công!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,  final int id) {
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                Logger.error("upload bluetooth fail");
                final AlertDialog.Builder builder = new AlertDialog.Builder(AcICIChecking.this);
                builder.setTitle("Báo cáo!");
                builder.setMessage("Upload dữ liệu thất bại!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,  final int id) {
                                dialog.dismiss();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

        }
    }
    private void initGoogleMap(){
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollContainer.requestDisallowInterceptTouchEvent(true);
                    }
                });
            }
        });
    }

    private void initGraphView() {
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(false); // enables horizontal scrolling
        graph.getViewport().setScrollableY(false); // enables vertical scrolling
        graph.getViewport().setScalable(false); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(false); // enables vertical zooming and scrollingZ

        graph.getViewport().setXAxisBoundsManual(false);
        graph.getGridLabelRenderer().setTextSize(10f);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (dp != null && dp.length > 0) {
                        try {
                            long timeVal = dp[(int) value];
                            SimpleDateFormat formatter = new SimpleDateFormat("hh.mm.ss");
                            return formatter.format(new Date(timeVal));
                        }catch(Exception e){
                            return super.formatLabel(value, isValueX);
                        }
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        series = new LineGraphSeries<>();
        series.setColor(Color.RED);
        series.setThickness(1);
        graph.addSeries(series);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    private void initBlueTooth(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        service_init();

        if (!mBtAdapter.isEnabled()) {
            Log.e(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Intent newIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        }
    }

    int i = 0;
    private void initData(){
        VirableDataGenerator dataGenerator = new VirableDataGenerator();
        blueToothDatas = dataGenerator.generateVirableData();

//        Toast.makeText(this, "size: " + blueToothDatas.size(), Toast.LENGTH_SHORT).show();
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrollingZ
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        this.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    //tao gia tri truc Z - random
    private int generatRandomPositiveNegitiveValue(int max , int min) {
        Random rand = new Random();
        int ii = rand.nextInt(max + 1 -min) + min;
        return ii;
    }

    StringBuilder BleTemp;
    int count = 0;
    long currentTime = 0;
    List<BlueToothData> problemListFound;
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        isCreateNewBlueToothData = true;
                        graph.removeAllSeries();
                        blueToothDatas = new ArrayList<BlueToothData>();
                        series = new LineGraphSeries<>();
                        series.setColor(Color.RED);
                        series.setThickness(1);
                        graph.addSeries(series);


                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMinX(0);
                        graph.getViewport().setMaxX(60);

                        isManualDisconnectBluetooth = false;
                        problemListFound = new ArrayList<>();
                        BleTemp = new StringBuilder();

                        currentTime = System.currentTimeMillis();
                        probleCount = 0;
                        totalDistance = 0;
                        //still not calculate distance. because the data get is too many in short time
                        tvTotalDistance.setVisibility(View.GONE);
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Logger.error("[" + currentDateTimeString + "] Connected to: " + mDevice.getName());
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Logger.error("[" + currentDateTimeString + "] Disconnected to: " + mDevice.getName());
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();

                        if(!isManualDisconnectBluetooth)
                            buildAlertConnectBLueToothAgain();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }

            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final byte[] txValue = mIntent.getByteArrayExtra(UartService.EXTRA_DATA);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    String text = new String(txValue, "UTF-8").replace("\n","");
                                    Logger.error("text: " + text);
                                    if(text.contains("ZZ")) {
                                        BleTemp.append(text);
                                        count++;
                                        UUIDData = UUID.randomUUID().toString();

                                        double zData;
                                        String latitude = "", longtitude = "";

                                        String[] stk = BleTemp.toString().split(",");
                                        if (stk.length > 4) {
                                            zData = Double.parseDouble(stk[1]);
                                            latitude = stk[2];
                                            longtitude = stk[4];
                                        } else {
                                            zData = Double.parseDouble(stk[1]);
                                        }

                                        BlueToothData blData = new BlueToothData();

                                        blData.setId(UUIDData);
                                        blData.setRoadId(4);
                                        blData.setDateTimeLoging("" + System.currentTimeMillis());
                                        blData.setZaxisValue(zData);
                                        blData.setLatitude(latitude);
                                        blData.setLongitude(longtitude);
                                        blData.setUserLoging("dungdv");

                                        DatabaseHelper.insertBlueToothData(blData);
                                        blueToothDatas.add(blData);

                                        /**
                                         * this method increase problem when first found zData > 15000
                                         * . Then until zData = 0 -> problem increase by 1
                                         */
                                        if (zData > 15000) {
                                            problemListFound.add(blData);
                                        } else {
                                            if (zData > -1500 && zData < 1500) {
                                                zData = 0;
                                                //Only created new list if currentList is not created
                                                if (problemListFound.size() > 0) {
                                                    probleCount++;
//                                                caculateTheMostOfProblem(problemListFound);
                                                    problemListFound = new ArrayList<>();
                                                } else {
                                                }
                                            } else {
                                                //zData < 15000 is not problem. Avoid it
                                            }
                                        }

                                        if (currentTime != 0)
                                            tvTime.setText("Tổng thời gian( ước tính): " + calculateAmountOfTime(currentTime, System.currentTimeMillis()) + " phút.");
//                                            tvTotalDistance.setText(tvTotalDistance.getText() + ": " + (int) totalDistance + " mét.");
                                        tvProblemFound.setText("Số lượng ổ gà( ước tính): " + probleCount + " .");


                                        series.appendData(new DataPoint(count, zData / 100), true, count);

                                        //refresh buffer
                                        BleTemp = new StringBuilder();
                                    }else{
                                        BleTemp.append(text); // add last data
                                    }
                                } catch (Exception e) {
                                    Logger.error(e.toString());
                                    //refresh buffer
                                    BleTemp = new StringBuilder();
                                }
                            }
                        });
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                mService.disconnect();
            }
        }
    };

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

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
                        Intent in = new Intent(AcICIChecking.this, MainScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(in);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertShowHardCodeData(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hệ thống tìm được bản ghi quá trình đo độ phẳng của đường X (Hà Nội), bạn có muốn xem chứ?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        runHardCodeData();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Intent in = new Intent(AcICIChecking.this, MainScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(in);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void buildAlertConnectBLueToothAgain(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Kết nối với mạch đã bị ngắt, bạn có muốn dò lại tín hiệu chứ?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        Intent newIntent = new Intent(AcICIChecking.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }


    double totalDistance = 0;
    int probleCount = 0;
    private void runHardCodeData(){
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(blueToothDatas.size());

        i = 0;
        dp = new long[blueToothDatas.size()];
        for (int j = 0; j < blueToothDatas.size(); j++) {
            dp[j] = Long.parseLong(blueToothDatas.get(j).getDateTimeLoging());
        }

        graph.removeAllSeries();

        series = new LineGraphSeries<>();
        series.setColor(Color.RED);
        series.setThickness(1);
        graph.addSeries(series);

        tvTotalDistance.setVisibility(View.VISIBLE);
//        AlertDialog d = AlertUtil.showSimpleAlertWithMessage(this, "Dang nap du lieu");
        while (i < blueToothDatas.size()){
            series.appendData(new DataPoint(i, blueToothDatas.get(i).getZaxisValue()), false, blueToothDatas.size());

            if(blueToothDatas.get(i).getZaxisValue() > 5000){
                LatLng latLng = new LatLng(Double.parseDouble(blueToothDatas.get(i).getLatitude()),Double.parseDouble(blueToothDatas.get(i).getLongitude()));
                addMarker(latLng);
                moveCamera(latLng);
                probleCount++;
            }

            if (i == blueToothDatas.size() - 1) {
                tvTime.setText(tvTime.getText() + ": " + (int) calculateAmountOfTime(Long.parseLong(blueToothDatas.get(0).getDateTimeLoging()),
                        Long.parseLong(blueToothDatas.get(i).getDateTimeLoging())) + " phút.");
                tvTotalDistance.setText(tvTotalDistance.getText() + ": " + (int) totalDistance + " mét.");
                tvProblemFound.setText(tvProblemFound.getText() + ": " +  probleCount + " .");
//                d.dismiss();
                break;
            }else{
                if(i > 0) {
                    totalDistance += calculateDistanceBetweenLatlng(new LatLng(Double.parseDouble(blueToothDatas.get(i - 1).getLatitude()), Double.parseDouble(blueToothDatas.get(i - 1).getLongitude()))
                            , new LatLng(Double.parseDouble(blueToothDatas.get(i).getLatitude()), Double.parseDouble(blueToothDatas.get(i).getLongitude())));

                    drawPath(new LatLng(Double.parseDouble(blueToothDatas.get(i - 1).getLatitude()), Double.parseDouble(blueToothDatas.get(i - 1).getLongitude()))
                            , new LatLng(Double.parseDouble(blueToothDatas.get(i).getLatitude()), Double.parseDouble(blueToothDatas.get(i).getLongitude())));
                }
            }
            i++;
        }

    }

    private void moveCamera(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(18)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10, null);
    }

    private int calculateAmountOfTime(long firstTime, long lastTime){
        Date d = new Date();
        d.setTime(lastTime - firstTime);

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
//        return cal.get(Calendar.MINUTE);
        return (int) (long)(lastTime - firstTime) / (60 * 1000) % 60;
    }

    private double calculateDistanceBetweenLatlng(LatLng curentLat, LatLng newLat){
        Location locationA = new Location("point A");

        locationA.setLatitude(curentLat.latitude);
        locationA.setLongitude(curentLat.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(newLat.latitude);
        locationB.setLongitude(newLat.longitude);

        return locationB.distanceTo(locationA);
    }

    private void addMarker(LatLng latLng){
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        MarkerOptions options = new MarkerOptions();
        options.snippet("Vị trí được đánh dấu là đường xấu / ổ gà.");
        options.position(latLng);
        options.draggable(false);
        options.icon(icon);
        gMap.addMarker(options);
    }

    private void drawPath(LatLng curentLat, LatLng newLat){
        gMap.addPolyline(new PolylineOptions()
                .add(curentLat, newLat)
                .width(3)
                .color(Color.GREEN));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
            Log.e(TAG, "onResume - BT not enabled yet");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

//        try{
//            if(list.size() > 0){
//                sensor.unregisterListener(sel);
//            }
//        }catch(Exception e){
//
//        }

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        try {
            this.unbindService(mServiceConnection);
            mService.stopSelf();
        }catch (Exception e){
            Logger.error("Chua dang ki service");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    mService.connect(deviceAddress);
                    graph.setVisibility(View.VISIBLE);

                }else{
                    isCreateNewBlueToothData = false;
                    /*
                        init hardcode data
                     */
                    initData();
                    buildAlertShowHardCodeData();
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Đã bật bluetooth.", Toast.LENGTH_SHORT).show();
                    Intent newIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Có sự cố xảy ra khi bật bluetooth. ", Toast.LENGTH_SHORT).show();
                }
                break;
            case ENABLE_LOCATION:
                LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    initBlueTooth();
                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Bật dịch vụ vị trí thất bại, hãy chú ý đợi từ 4 đến 6 giây ở màn hình kích hoạt vị trí để"
                            + " vị trí được kích hoạt trước khi trở lại màn hình nhập. Đồng thời phải đảm bảo đường truyền mạng của bạn là ổn định!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog,  final int id) {
                                    dialog.cancel();
                                    Intent in = new Intent(AcICIChecking.this, MainScreen.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(in);
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }

            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }
}
