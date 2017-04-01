package ve.com.abicelis.remindy.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.PlaceAdapter;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 30/3/2017.
 */

public class PlaceListActivity extends AppCompatActivity implements View.OnClickListener {

    //CONST
    public static final int ADD_OR_EDIT_PLACE_REQUEST_CODE = 500;

    //DATA
    private List<Place> mPlaces = new ArrayList<>();
    private RemindyDAO mDao;

    //UI
    private LinearLayout mContainer;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PlaceAdapter mAdapter;
    private RelativeLayout mNoItemsContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);


        mContainer = (LinearLayout) findViewById(R.id.activity_place_list_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_place_list_recycler);
        mNoItemsContainer = (RelativeLayout) findViewById(R.id.activity_place_list_no_items_container);
        mFab = (FloatingActionButton) findViewById(R.id.activity_place_list_fab);
        mFab.setOnClickListener(this);

        setUpToolbar();
        setUpRecyclerView();
        refreshRecyclerView();
    }

    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.activity_place_list_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_place_list_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new PlaceAdapter(this, mPlaces);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_half_line_unprogrammed));
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void refreshRecyclerView() {

        if(mDao == null)
            mDao = new RemindyDAO(getApplicationContext());

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of the reminder list
        mPlaces.clear();
        mPlaces.addAll(mDao.getPlaces());
        mAdapter.notifyDataSetChanged();

        if(mPlaces.size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setItemViewCacheSize(View.VISIBLE);
            mNoItemsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_place_list_fab:
                Intent newPlaceIntent = new Intent(getApplicationContext(), PlaceActivity.class);
                startActivityForResult(newPlaceIntent, ADD_OR_EDIT_PLACE_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_OR_EDIT_PLACE_REQUEST_CODE) {
            refreshRecyclerView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the mToolbar's back/home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
