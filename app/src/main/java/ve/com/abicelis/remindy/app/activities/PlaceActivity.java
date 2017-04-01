package ve.com.abicelis.remindy.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.dialogs.EditPlaceDialogFragment;
import ve.com.abicelis.remindy.app.services.AddressResultReceiver;
import ve.com.abicelis.remindy.app.services.FetchAddressIntentService;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.DistanceFormat;
import ve.com.abicelis.remindy.exception.CouldNotDeleteDataException;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.exception.CouldNotUpdateDataException;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.DpToPxUtil;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 30/3/2017.
 */

public class PlaceActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        AddressResultReceiver.AddressReceiverListener,
        EditPlaceDialogFragment.EditPlaceDialogDismissListener {

    //CONST
    public static final String PLACE_TO_EDIT = "PLACE_TO_EDIT";
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP = 40;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION = 41;

    //DATA
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    private Place mPlaceToEdit;
    private Place mPlace;
    private Marker mPlaceMarker;
    private Circle mPlaceCircle;
    private boolean mAliasAddressAlreadySet;
    private RemindyDAO mDao;
    private DistanceFormat mDistanceFormat;

    //UI
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private RelativeLayout mMapContainer;
    private EditText mSearch;
    private ImageView mSearchButton;
    private SeekBar mRadius;
    private TextView mRadiusDisplay;
    private TextView mAlias;
    private TextView mAddress;
    private ImageView mAliasAddressEdit;
    private LinearLayout mAliasAddressContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

//        //Enable Lollipop Material Design transitions
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        if(getIntent().hasExtra(PLACE_TO_EDIT)) {
            mPlaceToEdit = (Place) getIntent().getSerializableExtra(PLACE_TO_EDIT);
            mPlace = new Place(mPlaceToEdit);
        } else {
            mPlace = new Place();
        }

        //Get distance format preference
        mDistanceFormat = SharedPreferenceUtil.getDistanceFormat(getApplicationContext());


        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();



        mMapContainer = (RelativeLayout) findViewById(R.id.activity_place_map_container);
        mSearch = (EditText) findViewById(R.id.activity_place_search);
        mSearchButton = (ImageView) findViewById(R.id.activity_place_search_button);
        mRadius = (SeekBar) findViewById(R.id.activity_place_radius_seekbar);
        mRadius.setMax(14);
        mRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlace.setRadius((progress+1) * 100);
                mRadiusDisplay.setText(String.valueOf(mPlace.getRadius()) + " m");                          //TODO: mDistanceFormat
                if(mPlaceCircle != null)
                    mPlaceCircle.setRadius(mPlace.getRadius());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mRadiusDisplay = (TextView) findViewById(R.id.activity_place_radius_display);
        mAlias = (TextView) findViewById(R.id.activity_place_alias);
        mAddress = (TextView) findViewById(R.id.activity_place_address);
        mAliasAddressEdit = (ImageView) findViewById(R.id.activity_place_alias_address_edit);
        mAliasAddressEdit.setOnClickListener(this);
        mAliasAddressContainer = (LinearLayout) findViewById(R.id.activity_place_alias_address_container);

        setUpToolbar();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_place_toolbar);
        mToolbar.setTitle(getResources().getString( (mPlaceToEdit != null ? R.string.activity_place_title_edit : R.string.activity_place_title_new) ));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_place_map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onConnectionSuspended(int i) {Toast.makeText(this, "onConnectionSuspended() " + i, Toast.LENGTH_SHORT).show();}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {Toast.makeText(this, "OnConnectionFailed()" + connectionResult.toString(), Toast.LENGTH_SHORT).show();}



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //Save lat and long
                mPlace.setLatitude(latLng.latitude);
                mPlace.setLongitude(latLng.longitude);

                //Add/Update marker and circle
                if(mPlaceMarker == null)
                    drawMarkerWithCircle(latLng, mPlace.getRadius());
                else
                    updateMarkerWithCircle(latLng);


                //Request its location
                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLatitude(mPlaceMarker.getPosition().latitude);
                loc.setLongitude(mPlaceMarker.getPosition().longitude);

                Toast.makeText(PlaceActivity.this, "Fetching address...", Toast.LENGTH_SHORT).show();
                fetchAddressFromLocation(loc);
            }
        });

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  {
            setUpMap();
        }
        else
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP);
    }

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, DpToPxUtil.dpToPx(52, getResources()), 0, 0);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(mPlaceToEdit == null) {
            moveCameraToLastKnownLocation();        //If creating a new place, go to user current location
            mRadius.setProgress(1);
            mRadiusDisplay.setText("100 m");        //TODO: mDistanceFormat
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.activity_place_snackbar_help, SnackbarUtil.SnackbarDuration.SHORT, null);

        } else {
            drawMarkerWithCircle(new LatLng(mPlace.getLatitude(), mPlace.getLongitude()), mPlace.getRadius());        //If editing a place, go to that place and add a marker, circle

            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(mPlace.getLatitude());
            loc.setLongitude(mPlace.getLongitude());
            moveCameraToLocation(loc);

            TransitionManager.beginDelayedTransition(mMapContainer, new Slide(Gravity.BOTTOM));
            mAlias.setText(mPlace.getAlias());
            mAddress.setText(mPlace.getAddress());
            mAliasAddressContainer.setVisibility(View.VISIBLE);
            mAliasAddressAlreadySet = true;

            mRadius.setProgress((int) (mPlace.getRadius()/100)-1 );
            mRadiusDisplay.setText(String.valueOf(mPlace.getRadius()) + " m");      //TODO: mDistanceFormat

        }
    }


    /* Map Camera, Marker and Circle helper methods */
    private void drawMarkerWithCircle(LatLng position, double circleRadiusInMeters){
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(circleRadiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        mPlaceCircle = mMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position).title("My Location");
        mPlaceMarker = mMap.addMarker(markerOptions);
        mPlaceCircle.setZIndex(100);
    }

    private void updateMarkerWithCircle(LatLng position) {
        mPlaceCircle.setCenter(position);
        mPlaceMarker.setPosition(position);
    }

    private void moveCameraToLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                moveCameraToLocation(lastLocation);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION);
        }
    }

    private void moveCameraToLocation(Location location) {
        if (location != null) {
            //Point map camera to location
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15), 1000, null);  //Zoom level 15 = Streets, 1000ms animation
            CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(latlng).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);
        }
    }



    /* Address helper methods */

    protected void fetchAddressFromLocation(Location location) {
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiverListener(this);

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onReceiveAddressResult(int resultCode, Bundle resultData) {
        String alias = resultData.getString(FetchAddressIntentService.RESULT_ALIAS_KEY);
        String address = resultData.getString(FetchAddressIntentService.RESULT_ADDRESS_KEY);

        if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {

            //Save alias and address
            mPlace.setAlias(alias);
            mPlace.setAddress(address);

            if(!mAliasAddressAlreadySet) {
                TransitionManager.beginDelayedTransition(mMapContainer, new Slide(Gravity.BOTTOM));
                mAlias.setText(alias);
                mAddress.setText(address);
                mAliasAddressContainer.setVisibility(View.VISIBLE);
                mAliasAddressAlreadySet = true;

            } else {
                //TransitionManager.beginDelayedTransition(mMapContainer, new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
                mAlias.setText(alias);
                mAddress.setText(address);
            }


        } else {
            Toast.makeText(this, "There was an error fetching the address...", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                }
                break;
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (lastLocation != null) {
                        moveCameraToLocation(lastLocation);
                    }
                }
        }

    }




    /* Edit Alias Address Dialog */

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_place_alias_address_edit:
                FragmentManager fm = getSupportFragmentManager();
                EditPlaceDialogFragment dialog = EditPlaceDialogFragment.newInstance(mPlace.getAlias(), mPlace.getAddress());
                dialog.setListener(this);
                dialog.show(fm, "EditPlaceDialogFragment");
        }
    }

    @Override
    public void onFinishEditPlaceDialog(String alias, String address) {
        mPlace.setAlias(alias);
        mPlace.setAddress(address);

        mAlias.setText(alias);
        mAddress.setText(address);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place, menu);

        if(mPlaceToEdit == null)   //If creating a Place, hide delete button.
            menu.findItem(R.id.menu_place_delete).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            // Respond to the mToolbar's back/home button
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_place_save:
                handleSaveOrUpdatePlace();
                break;

            case R.id.menu_place_delete:
                handleDeletePlace();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSaveOrUpdatePlace() {
        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                setResult(RESULT_OK);
                finish();
            }
        };

        mDao = new RemindyDAO(getApplicationContext());

        //Verify mPlace is set
        if(mPlace.getLongitude() == 0) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_no_place, SnackbarUtil.SnackbarDuration.LONG, null);
            return;
        }
        if(mPlace.getAlias() == null || mPlace.getAlias().isEmpty()) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.activity_place_snackbar_error_no_alias, SnackbarUtil.SnackbarDuration.LONG, null);
            return;
        }

        if(mPlaceToEdit != null) {
            try {
                mDao.updatePlace(mPlace);
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_edit_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
            } catch (CouldNotUpdateDataException e ) {
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_saving, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        } else {
            try {
                mDao.insertPlace(mPlace);
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_save_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
            } catch (CouldNotInsertDataException e ) {
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_saving, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        }
    }

    private void handleDeletePlace() {
        mDao = new RemindyDAO(getApplicationContext());

        final BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                setResult(RESULT_OK);
                finish();
            }
        };

        //Check if Place is associated with at least one location-based reminder
        List<Task> locationBasedTasks = new ArrayList<>();
        try {
            locationBasedTasks = mDao.getLocationBasedTasksAssociatedWithPlace(mPlace.getId());
        }catch (CouldNotGetDataException e) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_deleting, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        //if there are tasks that use this place, warn user
        @StringRes int title = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_title : R.string.activity_place_dialog_delete_title);
        @StringRes int message = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_message : R.string.activity_place_dialog_delete_message);
        @StringRes int positive = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_positive : R.string.activity_place_dialog_delete_positive);
        @StringRes int negative = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_negative : R.string.activity_place_dialog_delete_negative);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(title))
                .setMessage(getResources().getString(message))
                .setPositiveButton(getResources().getString(positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mDao.deletePlace(mPlace.getId());
                            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_delete_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
                            dialog.dismiss();
                        } catch (CouldNotDeleteDataException e) {
                            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_deleting, SnackbarUtil.SnackbarDuration.LONG, callback);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

    }





    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_place_dialog_exit_title))
                .setMessage(getResources().getString(R.string.activity_place_dialog_exit_message))
                .setPositiveButton(getResources().getString(R.string.activity_place_dialog_exit_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_place_dialog_exit_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


}
