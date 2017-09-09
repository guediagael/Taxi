package android.theletch.tech.taxi;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.theletch.tech.taxi.utils.LocationPermission;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        CommentDialogFragment.DialogListener{

    private GoogleMap mMap;
    private double mLat =55.7983556,mLng = 49.1064488;
    private final int LOCATION_REQUEST_CODE = 167;
    private final int REQUEST_CHECK_SETTINGS_CODE = 168;

    private  LocationPermission mLocationPermission;
    private CameraPosition mCameraPosition;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnowLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingRequest;
    private SettingsClient mClient ;


    private TextView tvComment;

//    private GoogleApiClient mGoogleApiClient;

    private static final String KEY_CAMERA_POSITION = "cameraPosition";
    private static final String KEY_LAST_KNOWN_LOCATION = "lastLocation";
    private static final float DEFAULT_ZOOM = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState !=null){
            mLastKnowLocation = savedInstanceState.getParcelable(KEY_LAST_KNOWN_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);

            mLat = mLastKnowLocation.getLatitude();
            mLng = mLastKnowLocation.getLongitude();

        }
        setContentView(R.layout.activity_map);
        tvComment = (TextView) findViewById(R.id.text_map_comment);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mClient = LocationServices.getSettingsClient(this);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        TODO: ADD TOGGLE
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mLocationPermission = new LocationPermission.Builder(this,LOCATION_REQUEST_CODE)
                    .setExplanationMessage(getString(R.string.msg_location_permission)).build();
            mLocationPermission.askForPermission();
        }



        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null){
            outState.putParcelable(KEY_CAMERA_POSITION,mCameraPosition);
            outState.putParcelable(KEY_LAST_KNOWN_LOCATION,mLastKnowLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCoordinates();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap =googleMap;
//        mGoogleApiClient.connect();
        getCoordinates();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCoordinates();
            }

            else Log.d(getClass().getSimpleName(),"Location not authorized");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void focusOnMap(){
        LatLng userLocation = new LatLng(mLat,mLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( userLocation,DEFAULT_ZOOM));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_CODE){
            switch (resultCode){
                case Activity.RESULT_OK :

                    break;
                case Activity.RESULT_CANCELED :

                    break;

            }
        }
    }

    private void getCoordinates(){
        if (mLocationPermission != null){
            mLocationPermission.askForPermission();
        }
        if(mMap == null) return;
        focusOnMap();
        try{
            mMap.setMyLocationEnabled(true);
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        mLastKnowLocation = task.getResult();
                        if (mLastKnowLocation ==null) {
                            newLocationRequest();
                            Log.d(MapActivity.class.getSimpleName()," last known location is null");
                        }
                        else {
                            mLat = mLastKnowLocation.getLatitude();
                            mLng = mLastKnowLocation.getLongitude();
                            focusOnMap();
                        }


                    }else {
                        Log.d(getClass().getSimpleName(), "location is null");
                        Log.e(getClass().getSimpleName(), "Location Exception :",task.getException());
                        //        mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    }
                }
            });

        }catch (SecurityException e){
            Log.e(getClass().getSimpleName(),"security exception");
        }

    }





    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildLocationSettingsRequest(){
        LocationSettingsRequest.Builder locationRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        mLocationSettingRequest = locationRequestBuilder.build();

    }

    @SuppressWarnings("MissingPermission")
    private void newLocationRequest(){
        Task<LocationSettingsResponse> task = mClient.checkLocationSettings(mLocationSettingRequest);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(MapActivity.class.getSimpleName(),"Location setting success :" +
                        locationSettingsResponse.getLocationSettingsStates().toString() );
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,getMainLooper());
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(MapActivity.class.getSimpleName(),"Location setting failure",e);
                ApiException apiException = (ApiException) e;
                switch (apiException.getStatusCode()){
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                        try{
                            ResolvableApiException resolvableApiException =
                                    (ResolvableApiException) apiException;
                            resolvableApiException.startResolutionForResult(
                                    MapActivity.this,REQUEST_CHECK_SETTINGS_CODE

                            );
                        }catch (IntentSender.SendIntentException e1){

                        }catch (ClassCastException e1){

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                        break;
                }
            }
        });
    }

    private void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                mLastKnowLocation = locationResult.getLastLocation();
                mLng = mLastKnowLocation.getLongitude();
                mLat = mLastKnowLocation.getLatitude();
                focusOnMap();
            }
        };
    }


    private void showFullForm(){

    }

    private void showHalfForm(){

    }

    public void openCommentDialog(View view){
//        DialogFragment dialogFragment = new CommentDialogFragment();
//        dialogFragment.show(getSupportFragmentManager(),CommentDialogFragment.TAG);
        FragmentManager fragmentManager = getSupportFragmentManager();
        CommentDialogFragment newFragment = new CommentDialogFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    @Override
    public void sendComment(String comment) {
        tvComment.setText(comment);
//        TODO : change the image close to the comment to be yellow
    }
}
