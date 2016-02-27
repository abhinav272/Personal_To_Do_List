package com.abhinav.personalto_dolist.Services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by abhinavsharma on 26-02-2016.
 */
public class LatLongService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private Address mAddress = null;
    private static final String TAG = "LatLongService";
    public static final int STATUS_PASS = 1;
    protected ResultReceiver mResultReceiver;

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mAddress = getAddressFromLatLng(mLastLocation);
            Log.d(TAG, "" + mAddress);
            deliverAddressToActivity(STATUS_PASS,mAddress);
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Location connection failed " + connectionResult.getErrorCode());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mResultReceiver = intent.getParcelableExtra("receiver");
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    private LatLng getLatLongfromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        LatLng latLng;

        try {
            addressList = geocoder.getFromLocationName(address, 5);
            if (addressList.size() == 0) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address location = addressList.get(0);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        return latLng;


    }

    private Address getAddressFromLatLng(Location mLocation) {
        Geocoder geocoder;
        List<Address> addresses;
        Address address = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            address = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void deliverAddressToActivity(int resultCode, Address mAddress) {
        String addrs = "";
        Bundle bundle = new Bundle();
        if (resultCode == STATUS_PASS && mAddress!=null) {
            addrs = mAddress.getAddressLine(0)
                    + " " + mAddress.getAddressLine(1)
                    + " " + mAddress.getAddressLine(2)
                    + " " + mAddress.getAddressLine(3);
            bundle.putString("address", addrs);
        }
        mResultReceiver.send(resultCode, bundle);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service Stopped");
        super.onDestroy();
    }
}
