package clasher.testmapservice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ///////////////////REQUEST_ALL_PERMISSIONS_IN_A_SINGLE_ALERT///////////////////////////////
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

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



        //////////////////////START_BACKGROUND_SERVICE_TO_GET_GPS_LOCATION//////////////////////
        Intent intent=new Intent(this,LocService.class);
        startService(intent);
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
