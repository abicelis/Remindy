package ve.com.abicelis.remindy.app.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.AddReminderActivity;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;

/**
 * Created by abice on 20/4/2017.
 */

public class EditLocationBasedReminderFragment extends Fragment implements AddReminderActivity.ReminderValueUpdater, OnMapReadyCallback {

    //CONST
    public static final String REMINDER_ARGUMENT = "REMINDER_ARGUMENT";
    //public static final String INSTANCE_STATE_REMINDER_KEY = "INSTANCE_STATE_REMINDER_KEY";


    //DATA
    private RemindyDAO mDao;
    private List<String> mPlaceTypes = new ArrayList<>();
    private List<Place> mPlaces;
    private LocationBasedReminder mReminder;

    //UI
    private Spinner mPlace;
    private MapView mMapView;
    private TextView mAddress;
    private RadioGroup mRadioGroup;
    private RadioButton mEntering;
    private RadioButton mExiting;

    private GoogleMap mMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        //If a state was saved (such as when rotating the device), restore the state!
//        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_REMINDER_KEY)) {
//            mReminder = (RepeatingReminder) savedInstanceState.getSerializable(INSTANCE_STATE_REMINDER_KEY);
//        }


        //If fragment was just called, expect a reminder at REMINDER_ARGUMENT
        if(getArguments().containsKey(REMINDER_ARGUMENT))
            mReminder = (LocationBasedReminder) getArguments().getSerializable(REMINDER_ARGUMENT);
        else
            Toast.makeText(getActivity(), getResources().getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();

        mDao = new RemindyDAO(getActivity());
        mPlaces = mDao.getPlaces();

        //Set mReminder to the first place
        mReminder.setPlace(mPlaces.get(0));

        for (Place p : mPlaces)
            mPlaceTypes.add(p.getAlias());
        Log.d("POOP", "OnCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_location_based_reminder, container, false);

        mPlace = (Spinner) rootView.findViewById(R.id.fragment_edit_location_based_reminder_place);
        mMapView = (MapView) rootView.findViewById(R.id.fragment_edit_location_based_reminder_map);
        mAddress = (TextView) rootView.findViewById(R.id.fragment_edit_location_based_reminder_address);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.fragment_edit_location_based_reminder_radio_group);
        mEntering = (RadioButton) rootView.findViewById(R.id.fragment_edit_location_based_reminder_entering);
        mExiting = (RadioButton) rootView.findViewById(R.id.fragment_edit_location_based_reminder_exiting);

        mMapView.setClickable(false);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.fragment_edit_location_based_reminder_entering:
                        mReminder.setEntering(true);
                        break;
                    case R.id.fragment_edit_location_based_reminder_exiting:
                        mReminder.setEntering(false);
                        break;
                }
            }
        });
        setupSpinners();


        // Initialise the MapView
        mMapView.onCreate(null);
        // Set the map ready callback to receive the GoogleMap object
        mMapView.getMapAsync(this);
        Log.d("POOP", "OnCreateView");

        return rootView;

    }


    private void setupSpinners() {
        Log.d("POOP", "setupSpinners");

        ArrayAdapter placeTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, mPlaceTypes);
        placeTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mPlace.setAdapter(placeTypeAdapter);
        mPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handlePlaceTypeSelected(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void handlePlaceTypeSelected(int position) {
        Log.d("POOP", "handlePlaceTypeSelected");

        Place selectedPlace = mPlaces.get(position);

        mReminder.setPlace(selectedPlace);
        mReminder.setPlaceId(selectedPlace.getId());
        mReminder.setEntering(true);

        updateMapView();
        mAddress.setText(selectedPlace.getAddress());
        mEntering.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("POOP", "onMapReady");

        mMap = googleMap;

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        updateMapView();
    }

    private void updateMapView() {
        Log.d("POOP", "updateMapView");

        if (mMap != null && mReminder != null) {
            Log.d("POOP", "updateMapView GOOD");

            mMap.clear();

            // Add a marker for this item and set the camera
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);

            LatLng loc = new LatLng(mReminder.getPlace().getLatitude(), mReminder.getPlace().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
            mMap.addMarker(new MarkerOptions().position(loc).icon(icon));
        }
    }




    @Override
    public void updateReminderValues() {
        //Place, PlaceId and isEntering already set
    }

}
