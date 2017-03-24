package clasher.testmapservice;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;


public class GPSbroadcastReceiver extends BroadcastReceiver {
    public GPSbroadcastReceiver() {

    }
    String locationMode;
    public static final String TAG="## GPSbroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Broadcast receiver");
        // an Intent broadcast.
        ContentResolver contentResolver = context.getContentResolver();
        // Find out what the settings say about which providers are enabled
        int mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

        if (mode == Settings.Secure.LOCATION_MODE_OFF) {
            Log.d(TAG, "onReceive: Location is off");
            // Location is turned OFF!
        } else {
            // Location is turned ON!
            Log.d(TAG, "onReceive: location on");
            // Get the Mode value from Location system setting
            LocationManager locationManager = (LocationManager) context.
                    getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                Log.d(TAG, "onReceive: "+locationMode);
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationMode = "Device only. Uses GPS to determine location";
                Log.d(TAG, "onReceive: "+locationMode);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                Log.d(TAG, "onReceive: "+locationMode);
            }
        }
    }
}
