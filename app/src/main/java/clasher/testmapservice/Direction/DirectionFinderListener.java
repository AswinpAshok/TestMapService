package clasher.testmapservice.Direction;

/**
 * Created by PC4 on 3/25/2017.
 */
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import clasher.testmapservice.Direction.Route;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}