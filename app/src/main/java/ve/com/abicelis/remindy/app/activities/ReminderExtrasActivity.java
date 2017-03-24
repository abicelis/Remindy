package ve.com.abicelis.remindy.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderExtraAdapter;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderSortType;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.model.Reminder;
import ve.com.abicelis.remindy.model.ReminderExtra;
import ve.com.abicelis.remindy.model.ReminderExtraAudio;
import ve.com.abicelis.remindy.model.ReminderExtraLink;
import ve.com.abicelis.remindy.model.ReminderExtraText;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 19/3/2017.
 */

public class ReminderExtrasActivity extends AppCompatActivity implements View.OnClickListener {

    //CONST
    public static final String ARG_EXTRAS = "ARG_EXTRAS";
    private static final String TAG = ReminderExtrasActivity.class.getSimpleName();

    //DATA
    private ArrayList<ReminderExtra> mExtras;
    private ReminderExtraAdapter mAdapter;

    //UI
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mNoItemsContainer;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_extras);

        mToolbar = (Toolbar) findViewById(R.id.activity_reminder_extras_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_reminder_extras_recycler);
        mNoItemsContainer = (RelativeLayout) findViewById(R.id.activity_reminder_extras_no_items_container);
//        mFab = (FloatingActionButton) findViewById(R.id.activity_reminder_extras_fab);

        mToolbar.setTitle(R.string.activity_reminder_extras_toolbar_title);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        setSupportActionBar(mToolbar);

//        mFab.setOnClickListener(this);

        setUpRecyclerView();

        if(getIntent().hasExtra(ARG_EXTRAS)) {
            mExtras = (ArrayList<ReminderExtra>) getIntent().getSerializableExtra(ARG_EXTRAS);
        } else {
            mExtras = new ArrayList<>();
        }

        setUpRecyclerView();

        //TODO: Remove this eventually
        mExtras.add(new ReminderExtraLink("http://www.alejandrobicelis.com.ve"));
        mExtras.add(new ReminderExtraText("Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text "));
        mAdapter.notifyDataSetChanged();
    }


    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReminderExtraAdapter(this, mExtras);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_half_line));

        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Toast.makeText(ReminderExtrasActivity.this, "Swiped position " + position + " into direction=" + swipeDir, Toast.LENGTH_SHORT).show();
                mExtras.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }


    private void addExtraAndRefreshRecyclerView(ReminderExtra extra) {
        mExtras.add(extra);
        mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        switch (id) {
////            case R.id.activity_reminder_extras_fab:
////                //TODO: Use a fab menu, switch for 4 types of ReminderExtras...
////                addExtraAndRefreshRecyclerView(new ReminderExtraText("Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text "));
////                break;
//
//        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(ARG_EXTRAS, mExtras);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        };
        SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_reminder_on_back_saving_extras, SnackbarUtil.SnackbarDuration.SHORT, callback);
    }
}
