package com.example.macos.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.example.macos.activities.AcInput;
import com.example.macos.adapter.MainScreenAdapter;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.fragment.Input.FragmentInputItem;
import com.example.macos.interfaces.iLocationUpdate;
import com.example.macos.libraries.Logger;

import java.util.List;
import java.util.Locale;

/**
 * Created by Microsoft on 12/6/16.
 */

public class LocationHelper {

    public void getLocationDetail(Context context, Location mLocation, iLocationUpdate callback){
        new GeoDecodeLotaionAsynctask(context, mLocation, callback).execute();
    }

    private class GeoDecodeLotaionAsynctask extends AsyncTask<Void, Void, EnLocationItem> {
        Geocoder geocoder;
        EnLocationItem en;
        Location location;
        iLocationUpdate callback;

        public GeoDecodeLotaionAsynctask(Context context, Location mLocation, iLocationUpdate callback){
            Logger.error("GeoDecodeLotaion:" + mLocation);
            en = new EnLocationItem();
            geocoder = new Geocoder(context, Locale.getDefault());
            location = mLocation;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected EnLocationItem doInBackground(Void... voids) {
            List<Address> addresses;
            try {
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if(addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(".");
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

                return en;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(EnLocationItem locationItemData) {
            if(locationItemData == null ||locationItemData.getAddress() == null){
                callback.onFailGetLocation();
            }else {
                Logger.error("GeoDecodeLotaion:" + "locationItemData: " + locationItemData.getAddress());
                callback.updateLocation(locationItemData);
            }
        }
    }
}
