package clasher.testmapservice;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView mapView;
    GoogleMap googlemap;
    View view;
    public static double latitude;
    public static double longitude;
    CameraPosition cameraPosition;
    private static int flag=0;
    public MapFragment() {
        // Required empty public constructor
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            latitude=intent.getDoubleExtra("latitude",0);
            longitude=intent.getDoubleExtra("longitude",0);
            Log.d("MAP####", "onReceive: Recieving loaction");

            LatLng myLoc=new LatLng(latitude,longitude);
            try {
                googlemap.addMarker(new MarkerOptions().position(myLoc).title("myLoc"));
//                if (flag==0) {
                    cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(18).build();
//                    googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    flag=1;
//                }else {
//                    if (location.hasBearing()) {
//                        CameraPosition cameraPos = new CameraPosition.Builder()
//                                .target(myLoc)             // Sets the center of the map to current location
//                                .zoom(15)                   // Sets the zoom
//                                .bearing(location.getBearing()) // Sets the orientation of the camera to east
//                                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
//                                .build();                   // Creates a CameraPosition from the builder
//                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
//                    } else {
//                        cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(googleMap.getCameraPosition().zoom).build();
                        googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    }

            }catch (Exception e){
                e.printStackTrace();
            }


            //or
            //exercises = ParseJSON.ChallengeParseJSON(intent.getStringExtra(MY_KEY));

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_map, container, false);

        mapView=(MapView) view.findViewById(R.id.mapFragment);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getContext());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googlemap=googleMap;

            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        getActivity().registerReceiver(receiver, new IntentFilter("LocServiceLocation"));
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(receiver);
            Log.d(TAG, "onStop: called");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
public static final String TAG="####_MAP_FRAG";
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
        try {
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
        try {
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}