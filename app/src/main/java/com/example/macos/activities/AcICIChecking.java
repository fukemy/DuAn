package com.example.macos.activities;

import android.animation.LayoutTransition;
import android.app.Activity;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.adapter.DeviceListActivity;
import com.example.macos.database.BlueToothData;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.service.UartService;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Microsoft on 11/23/16.
 */

public class AcICIChecking extends AppCompatActivity {
    private TextView tvSpeed, tvProblemFound, tvTime;
    private Button btnBack;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private final int ENABLE_LOCATION = 1234;;
    SupportMapFragment mSupportMapFragment;
    private GoogleMap gMap;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_ici);

        initLayout();
        initGraphView();
        initGoogleMap();

        if(!FunctionUtils.checkLocationEnabled(this)){
            buildAlertMessageNoGps();
        }else{
            initData();
        }


    }

    private void initLayout(){
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvProblemFound = (TextView) findViewById(R.id.tvProblemFounded);
        tvTime = (TextView) findViewById(R.id.tvTime);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initGoogleMap(){
        mSupportMapFragment = new SupportMapFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.mapp, mSupportMapFragment).commit();
        getFragmentManager().beginTransaction();
        gMap = mSupportMapFragment.getMap();

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        gMap = googleMap;
                    }
                }
            });
        }
    }
    private void initGraphView() {
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(false); // enables horizontal scrolling
        graph.getViewport().setScrollableY(false); // enables vertical scrolling
        graph.getViewport().setScalable(false); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(false); // enables vertical zooming and scrollingZ

        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);

        graph.getGridLabelRenderer().setTextSize(12f);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return DateFormat.getTimeInstance().format(new Date());
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

    private void initData(){
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

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
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
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                        } catch (Exception e) {
                            Logger.error(e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                //showMessage("Device doesn't support UART. Disconnecting");
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
            ////     mService.disconnect(mDevice);
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


    @Override
    protected void onResume() {
        super.onResume();
        if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
            Log.e(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

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
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                    initData();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }
}
