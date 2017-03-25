package clasher.testmapservice;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import clasher.testmapservice.Direction.DirectionFinder;
import clasher.testmapservice.Direction.DirectionFinderListener;
import clasher.testmapservice.Direction.Route;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements DirectionFinderListener {

    EditText dest;
    Button pathfinder;
    MapView mapView;
    GoogleMap googlemap;
    View view;
    Location origin;
    public static double latitude;
    public static double longitude;
    CameraPosition cameraPosition;
    private static int flag=0;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    public MapFragment() {
        // Required empty public constructor
    }


    /////This broadcast receiver will get location broadcasts from service
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           Location location=intent.getParcelableExtra("location");
            origin=location;
            Log.d("MAP####", "onReceive: Recieving loaction");

            LatLng myLoc=new LatLng(location.getLatitude(),location.getLongitude());
            try {
                googlemap.clear();
                googlemap.addMarker(new MarkerOptions().position(myLoc).title("You").snippet("This is your current position").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_marker)));
                if (flag == 0) {
                    cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(18).tilt(67).build();
                    googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    flag = 1;
                } else {
                    if (location.hasBearing()) {
                        CameraPosition cameraPos = new CameraPosition.Builder()
                                .target(myLoc)             // Sets the center of the map to current location
                                .zoom(googlemap.getCameraPosition().zoom)                   // Sets the zoom
                                .bearing(location.getBearing())
                                .tilt(67)                   // Sets the tilt of the camera to 0 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                    } else {
                        cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(googlemap.getCameraPosition().zoom).tilt(67).bearing(location.getBearing()).build();
                        googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }

                }catch(Exception e){
                    e.printStackTrace();
                }


        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_map, container, false);

        dest=(EditText) view.findViewById(R.id.destination);
        pathfinder=(Button) view.findViewById(R.id.button2);
        pathfinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dest.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Please enter destination",Toast.LENGTH_LONG).show();
                }else {
                    String destination=dest.getText().toString();
                    Double originLat=origin.getLatitude();
                    Double originLon=origin.getLongitude();
                    String sourceLocation=originLat+","+originLon;
                    sendrequest(sourceLocation,destination);

                }
            }
        });

        //////////////////////TO_VIEW_MAP_INSIDE//////////////////////////////////////
        mapView=(MapView) view.findViewById(R.id.mapFragment);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getContext());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googlemap=googleMap;
                googleMap.setBuildingsEnabled(true);

            }
        });
        ///////////////////////////////////////////////////////////////////////////////

        return view;
    }

    private void sendrequest(String origin,String destination){
        try{
            new DirectionFinder(this,origin,destination).execute();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        flag=0;
        Log.d(TAG, "onResume: called");

        //////Register broadcast reciever, to recieve location updates
        getActivity().registerReceiver(receiver, new IntentFilter("LOC_SERVICE_LOCATION"));
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
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getContext(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(googlemap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(googlemap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(googlemap.addPolyline(polylineOptions));
        }
    }
}
