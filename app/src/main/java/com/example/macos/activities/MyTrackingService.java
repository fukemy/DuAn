package com.example.macos.activities;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by devil2010 on 7/8/16.
 */
public class MyTrackingService extends Service {
    private static final String TAG = "LocationService";
    LocationRequest mLocationRequest;
    LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!isGooglePlayServicesAvailable()) {
//            stopSelf();
//            Toast.makeText(getApplicationContext(), "Chua cai dat google play service!", Toast.LENGTH_SHORT).show();
//        }
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                System.out.println("new location in " + FunctionUtils.timeStampToTimeOnly(System.currentTimeMillis()));
//                DatabaseHelper.insertUserLocation(FunctionUtils.getDataAboutLocation(location, getApplicationContext()).toString());
//            }
//        };
//        createLocationRequest();

        return super.onStartCommand(intent, flags, startId);
    }

    public void destroyService(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                locationListener);
        stopSelf();
    }

    protected void createLocationRequest() {
        try {
            mLocationRequest = LocationRequest.create()
                    .setInterval(30000)
                    .setSmallestDisplacement(10) // 100 meter
                    .setPriority(LocationRequest.PRIORITY_LOW_POWER);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    mGoogleApiClient,
                                    mLocationRequest,
                                    locationListener);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            System.out.println("connect map failed!");
                        }
                    })
                    .build();

            mGoogleApiClient.connect();
        }catch (Exception e){
            e.printStackTrace();
            stopSelf();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
