package ve.com.abicelis.remindy.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.app.fragments.LocationBasedReminderDetailFragment;
import ve.com.abicelis.remindy.app.fragments.OneTimeReminderDetailFragment;
import ve.com.abicelis.remindy.app.fragments.RepeatingReminderDetailFragment;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.DateFormat;
import ve.com.abicelis.remindy.exception.CouldNotDeleteDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.reminder.LocationBasedReminder;
import ve.com.abicelis.remindy.model.reminder.OneTimeReminder;
import ve.com.abicelis.remindy.model.reminder.RepeatingReminder;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 30/3/2017.
 */

public class TaskDetailActivity extends AppCompatActivity implements View.OnClickListener {

    //CONST
    public static final String TASK_TO_DISPLAY = "TASK_TO_DISPLAY";

    //DATA
    private Task mTask;
    private DateFormat mDateFormat;

    //UI
    private Toolbar mToolbar;
    private ScrollView mScrollviewContainer;
    private ImageView mCategory;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mReminderSubtitle;
    private TextView mAttachmentsSubtitle;
    private TextView mReminderTitle;
    private LinearLayout mDoneDateContainer;
    private TextView mDoneDate;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AttachmentAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //Enable Lollipop Material Design transitions
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);


        mScrollviewContainer = (ScrollView) findViewById(R.id.activity_task_detail_scrollview_container);
        if(getIntent().hasExtra(TASK_TO_DISPLAY)) {
            mTask = (Task) getIntent().getSerializableExtra(TASK_TO_DISPLAY);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            };
            SnackbarUtil.showSnackbar(mScrollviewContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_no_task, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

        //Get distance format preference
        mDateFormat = SharedPreferenceUtil.getDateFormat(getApplicationContext());

        mCategory = (ImageView) findViewById(R.id.activity_task_detail_category);
        mTitle = (TextView) findViewById(R.id.activity_task_detail_title);
        mDescription = (TextView) findViewById(R.id.activity_task_detail_description);
        mReminderSubtitle = (TextView) findViewById(R.id.activity_task_detail_reminder_subtitle);
        mAttachmentsSubtitle = (TextView) findViewById(R.id.activity_task_detail_attachments_subtitle);
        mDoneDateContainer = (LinearLayout) findViewById(R.id.activity_task_detail_done_date_container);
        mDoneDate = (TextView) findViewById(R.id.activity_task_detail_done_date);


        mCategory.setImageResource(mTask.getCategory().getIconRes());
        mTitle.setText(mTask.getTitle());
        mDescription.setText(mTask.getDescription());
        if(mTask.getDoneDate() != null) {
            mDoneDateContainer.setVisibility(View.VISIBLE);
            mDoneDate.setText(mDateFormat.formatCalendar(mTask.getDoneDate()));
        }

        setUpToolbar();

        setUpReminderViews();



        mRecyclerView = (RecyclerView) findViewById(R.id.activity_task_detail_recycler);

        setUpRecyclerView();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_task_detail_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_task_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private void setUpReminderViews() {

        Bundle bundle = new Bundle();
        Fragment fragment = null;

        switch (mTask.getReminderType()) {
            case ONE_TIME:
                mReminderSubtitle.setText(getResources().getString(mTask.getReminderType().getFriendlyNameRes()));
                fragment = new OneTimeReminderDetailFragment();
                bundle.putSerializable(OneTimeReminderDetailFragment.REMINDER_TO_DISPLAY, (OneTimeReminder) mTask.getReminder());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_task_detail_reminder_placeholder, fragment).commit();
                break;

            case REPEATING:
                fragment = new RepeatingReminderDetailFragment();
                bundle.putSerializable(RepeatingReminderDetailFragment.REMINDER_TO_DISPLAY, (RepeatingReminder) mTask.getReminder());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_task_detail_reminder_placeholder, fragment).commit();
                break;

            case LOCATION_BASED:
                fragment = new LocationBasedReminderDetailFragment();
                bundle.putSerializable(LocationBasedReminderDetailFragment.REMINDER_TO_DISPLAY, (LocationBasedReminder) mTask.getReminder());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_task_detail_reminder_placeholder, fragment).commit();
                break;
        }

        if(fragment != null) {
            mReminderSubtitle.setText(getResources().getString(mTask.getReminderType().getFriendlyNameRes()));
            mReminderSubtitle.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();     //When user backs out, transition back!
    }





    private void setUpRecyclerView() {

        if(mTask.getAttachments().size() > 0) {
            mAttachmentsSubtitle.setVisibility(View.VISIBLE);

            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mAdapter = new AttachmentAdapter(this, mTask.getAttachments());

            DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
            itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_half_line));
            mRecyclerView.addItemDecoration(itemDecoration);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
//        switch (id) {
//            case R.id.activity_place_alias_address_edit:
//                FragmentManager fm = getSupportFragmentManager();
//                EditPlaceDialogFragment dialog = EditPlaceDialogFragment.newInstance(mPlace.getAlias(), mPlace.getAddress());
//                dialog.setListener(this);
//                dialog.show(fm, "EditPlaceDialogFragment");
//        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
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

            case R.id.menu_task_edit:
                Toast.makeText(this, "edit!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_task_delete:
                handleDeleteTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void handleDeleteTask() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_task_dialog_delete_title))
                .setMessage(getResources().getString(R.string.activity_task_dialog_delete_message))
                .setPositiveButton(getResources().getString(R.string.activity_task_dialog_delete_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            new RemindyDAO(TaskDetailActivity.this).deleteTask(mTask.getId());
                            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    finish();
                                }
                            };
                            SnackbarUtil.showSnackbar(mScrollviewContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_task_snackbar_task_deleted_successfully, SnackbarUtil.SnackbarDuration.SHORT, callback);
                        }catch (CouldNotDeleteDataException e) {
                            SnackbarUtil.showSnackbar(mScrollviewContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_deleting_task, SnackbarUtil.SnackbarDuration.LONG, null);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_task_dialog_delete_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


}
