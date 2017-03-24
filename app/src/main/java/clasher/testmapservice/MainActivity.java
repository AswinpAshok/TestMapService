package clasher.testmapservice;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ///////////////////REQUEST_ALL_PERMISSIONS_IN_A_SINGLE_ALERT///////////////////////////////

            //hasPermission is a function defined at the end of this class
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////SHOW_MAP_FRAGMENT_IN_MAINACTIVITY'S_LAYOUT//////////////////
        MapFragment mapFragment=new MapFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder,mapFragment);
        ft.commit();
        ///////////////////////////////////////////////////////////////////////////////

    }

    @Override
    protected void onResume() {
        super.onResume();
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable Location")
                    .setMessage("Location services are not enabled.This application need to access location. Please enable it")
                    .setPositiveButton("enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(onGPS);
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }
        //////////////////////START_BACKGROUND_SERVICE_TO_GET_GPS_LOCATION//////////////////////

        if(hasPermissions(this, PERMISSIONS)){
                 Intent intent=new Intent(this,LocService.class);
                startService(intent);
        }

        ////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
        try {
            //DO THIS ONLY IF YOU DONT WANT SERVICE TO RUN IN BACKGROUND
            stopService(new Intent(getBaseContext(),LocService.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static final String TAG="####_MAIN";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //DO THIS ONLY IF YOU DONT WANT SERVICE TO RUN IN BACKGROUND
        stopService(new Intent(getBaseContext(),LocService.class));
        Log.d("MAIN ####", "onDestroy: Stoppoing service");
    }



    ///////////////////////PERMISSION_CHECKING_FUNCTION////////////////////////////////////////////
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


}
