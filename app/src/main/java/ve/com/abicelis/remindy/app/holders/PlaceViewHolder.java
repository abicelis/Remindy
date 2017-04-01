package ve.com.abicelis.remindy.app.holders;

import android.app.Activity;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.PlaceActivity;
import ve.com.abicelis.remindy.app.activities.PlaceListActivity;
import ve.com.abicelis.remindy.app.adapters.PlaceAdapter;
import ve.com.abicelis.remindy.model.Place;

/**
 * Created by abice on 13/3/2017.
 */

public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {

    private PlaceAdapter mAdapter;
    private Activity mActivity;

    //UI
    private RelativeLayout mContainer;
    private TextView mAlias;
    private TextView mAddress;

    private MapView mMapView;
    private GoogleMap mMap;


    //DATA
    private Place mCurrent;
    private int mPlacePosition;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_place_container);
        mAlias = (TextView) itemView.findViewById(R.id.item_place_alias);
        mAddress = (TextView) itemView.findViewById(R.id.item_place_address);
        mMapView = (MapView) itemView.findViewById(R.id.item_place_map);
        mMapView.setClickable(false);

        // Initialise the MapView
        mMapView.onCreate(null);
        // Set the map ready callback to receive the GoogleMap object
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapsInitializer.initialize(mActivity.getApplicationContext());
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        updateMapView();
    }


    public void setData(PlaceAdapter adapter, Activity activity, Place current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPlacePosition = position;

        mAlias.setText(mCurrent.getAlias());
        mAddress.setText(mCurrent.getAddress());

        // If the map is ready, update its content.
        if (mMap != null)
            updateMapView();

    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }


    private void updateMapView() {

        if (mCurrent != null) {
            mMap.clear();

            // Add a marker for this item and set the camera
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);

            LatLng loc = new LatLng(mCurrent.getLatitude(), mCurrent.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
            mMap.addMarker(new MarkerOptions().position(loc).icon(icon));

//            // Set the map type back to normal.
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }




    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_place_container:
                //Toast.makeText(mActivity, "Place'" + mCurrent.getAlias() + "' clicked! Pos=" + mPlacePosition, Toast.LENGTH_SHORT).show();
                //Pair[] pairs = new Pair[1];
                //pairs[0] = new Pair<View, String>(mMapView, "map");
                //pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
                //pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));

                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairs);
                Intent editPlaceIntent = new Intent(mActivity, PlaceActivity.class);
                editPlaceIntent.putExtra(PlaceActivity.PLACE_TO_EDIT, mCurrent);
                //mActivity.startActivityForResult(editPlaceIntent, PlaceListActivity.ADD_OR_EDIT_PLACE_REQUEST_CODE, options.toBundle());
                mActivity.startActivityForResult(editPlaceIntent, PlaceListActivity.ADD_OR_EDIT_PLACE_REQUEST_CODE);
                break;
        }
    }


}
