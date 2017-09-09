package android.theletch.tech.taxi.utils;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;

/**
 * Created by guedi on 9/5/2017.
 */

public abstract class Permission {

    protected final Activity mActivity;
    protected final int mRequestCode;
    protected static String explanationMessage;
    protected boolean stronglyTurnedDown =true;

    protected Permission(Activity activity,int requestCode){
        mActivity =activity;
        mRequestCode = requestCode;

    }

    protected static void setMessage(String message){
        explanationMessage = message;
    }

    protected abstract boolean isPermissionGranted();

    public abstract void askForPermission();

    protected void showExplanationMessage(){
        if (explanationMessage!=null)
            Toast.makeText(mActivity,explanationMessage,Toast.LENGTH_LONG).show();
        stronglyTurnedDown =false;
    }

    public boolean isStronglyTurnedDown(){
        return stronglyTurnedDown;
    }





}
