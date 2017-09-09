package android.theletch.tech.taxi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by guedi on 9/5/2017.
 */

public class LocationPermission extends Permission{


    private LocationPermission(Activity activity, int requestCode) {
        super(activity, requestCode);
    }

    @Override
    protected boolean isPermissionGranted(){
        return ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED));
    }


    @Override
    public void askForPermission() {
        if (!isPermissionGranted()){
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) showExplanationMessage();

            ActivityCompat.requestPermissions(mActivity,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},mRequestCode);

        }
    }




    public static class Builder{
        private LocationPermission mLocationPermission;

        public Builder(Activity activity, int requestCode) {
           mLocationPermission = new LocationPermission(activity,requestCode);
        }

        public Builder setExplanationMessage(String message){
            setMessage(message);
            return this;
        }

        public LocationPermission build(){
           return mLocationPermission;
        }


    }





}
