package ve.com.abicelis.remindy.app.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;
import ve.com.abicelis.remindy.util.ConversionUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class LocationBasedReminderDetailFragment extends Fragment implements OnMapReadyCallback {

    //CONST
    public static final String REMINDER_TO_DISPLAY = "REMINDER_TO_DISPLAY";
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP = 49;

    //DATA
    private LocationBasedReminder mReminder;

    //UI
    private GoogleMap mMap;
    private LinearLayout mContainer;
    private TextView mAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments().containsKey(REMINDER_TO_DISPLAY)) {
            mReminder = (LocationBasedReminder) getArguments().getSerializable(REMINDER_TO_DISPLAY);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.fragment_location_based_reminder_detail_snackbar_error_no_reminder, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder_location_based, container, false);

        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_location_based_container);
        mAddress = (TextView) rootView.findViewById(R.id.fragment_reminder_location_based_address);
        mAddress.setText(mReminder.getPlace().getAddress());

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_reminder_location_based_map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            setUpMap();
        else
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP);
    }

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        //mMap.setPadding(0, ConversionUtil.dpToPx(68, getResources()), 0, 0);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        //Add circle and marker
        int strokeColor = ContextCompat.getColor(getActivity(), R.color.map_circle_stroke);
        int shadeColor = ContextCompat.getColor(getActivity(), R.color.map_circle_shade);
        LatLng latLng = ConversionUtil.placeToLatLng(mReminder.getPlace());
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(mReminder.getPlace().getRadius())
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(2));
        mMap.addMarker(new MarkerOptions().position(latLng));

        //Move camera
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15), 1000, null);  //Zoom level 15 = Streets, 1000ms animation
        CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);
    }



    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setUpMap();
                //TODO: Else Error message no permissions!
                break;
        }

    }





















}
