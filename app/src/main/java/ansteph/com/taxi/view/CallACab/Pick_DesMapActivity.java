package ansteph.com.taxi.view.CallACab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ansteph.com.taxi.R;
import ansteph.com.taxi.helper.PermissionUtils;
import ansteph.com.taxi.service.Constants;
import ansteph.com.taxi.service.FetchAddressIntentService;

public class Pick_DesMapActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraChangeListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    protected static String TAG = Pick_DesMapActivity.class.getSimpleName();

    private GoogleMap mMap;
    private TextView mTapTxt;
    private TextView mCameraTxt;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private Marker mPE;

    public static final CameraPosition PE =
            new CameraPosition.Builder().target(new LatLng(-33.7139, 25.5207))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();


    /*******Geocoder********/
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation; // this the geographical location

    protected boolean mAddressRequested;

    protected String mAddressOutput;// the formatted location address

   private AddressResultReceiver mResultReceiver; //Receiver registered with this activity to get the response from FetchAddressIntentService

    protected TextView mLocationAddressTextview;

    protected EditText txtFormatAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_des_map);

        mTapTxt = (TextView) findViewById(R.id.tap_text);
        mCameraTxt = (TextView) findViewById(R.id.camera_text);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //Geocoder
        mResultReceiver = new AddressResultReceiver(new Handler());
        txtFormatAddress = (EditText) findViewById(R.id.txtFormatAddress);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        //updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build() ;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Port Elizabeth, South Africa.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setPadding(40,250,40,40);

        mMap.getUiSettings().setZoomControlsEnabled(true);


        // Add a marker in Sydney and move the camera
        LatLng pe = new LatLng(-33.9736, 25.5983);

        mPE = mMap.addMarker(new MarkerOptions().position(pe)
        .title("Here")
        .snippet("Drag to desire location")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        .draggable(true));

      //  mMap.addMarker(new MarkerOptions().position(pe).title("Marker in PE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pe,14.5f));
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mTapTxt.setText("Tapped, point="+latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mTapTxt.setText("Long pressed, point="+latLng);

        mLastLocation = new Location("");
        mLastLocation.setLatitude(latLng.latitude);
        mLastLocation.setLongitude(latLng.longitude);
       mGoogleApiClient.connect();

       /*if(mGoogleApiClient.isConnected() && mLastLocation!=null)
        {startIntentService();
             }*/
        if( mLastLocation!=null)
        {startIntentService();
        }
        mAddressRequested=true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mCameraTxt.setText(cameraPosition.toString());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(mPermissionDenied)
        {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.



        // It is possible that the user presses the button to get the address before the
        // GoogleApiClient object successfully connects. In such a case, mAddressRequested
        // is set to true, but no attempt is made to fetch the address (see
        // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
        // user has requested an address, since we now have a connection to GoogleApiClient.
      //  if (mAddressRequested) {
           // startIntentService();
       // }

    }

    @Override
    public void onConnectionSuspended(int i) {
// The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    protected void startIntentService()
    {
        Intent intent = new Intent (this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
       startService(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    protected void displayAddressOutput(int resultCode){

        Toast.makeText(this, "Looking",Toast.LENGTH_SHORT).show();
        if (resultCode==Constants.SUCCESS_RESULT)
        {
            Toast.makeText(this, "Found",Toast.LENGTH_SHORT).show();
            txtFormatAddress.setText(mAddressOutput);
        }else{
            Toast.makeText(this, "Not Found",Toast.LENGTH_SHORT).show();
            txtFormatAddress.setText("Invalid address");
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver{

        public AddressResultReceiver (Handler handler) {super(handler);}
        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            displayAddressOutput(resultCode);

            mAddressRequested = false;
        }
    }


}
