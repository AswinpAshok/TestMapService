package clasher.testmapservice;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public LocService() {
    }
    public static final String TAG="SERVICE ####";

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    public static final long BACKGROUND_INTERVAL = 1000 * 5;

    @Override
    public void onCreate() {
        super.onCreate();
        if (isGooglePlayServicesAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(BACKGROUND_INTERVAL);
            mLocationRequest.setFastestInterval(BACKGROUND_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(10.0f);  /* min dist for location change, here it is 10 meter */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
        }
        Log.d(TAG, "onCreate: Service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        return ConnectionResult.SUCCESS == status;
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(getBaseContext(),"Loc changed from service",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onLocationChanged: Location changed");
        Intent intent=new Intent("LocServiceLocation");
        intent.putExtra("latitude",location.getLatitude());
        intent.putExtra("longitude",location.getLongitude());

        sendBroadcast(intent);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();

    }

    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d(TAG, "startLocationUpdates: Starting Location Updates");
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (IllegalStateException e) {}
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ondestroy ..............");
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }
}
