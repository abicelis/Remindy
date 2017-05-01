package ve.com.abicelis.remindy.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.HomeViewPagerAdapter;
import ve.com.abicelis.remindy.app.fragments.HomeListFragment;
import ve.com.abicelis.remindy.app.services.NotificationIntentService;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.enums.TaskSortType;
import ve.com.abicelis.remindy.enums.ViewPagerTaskDisplayType;
import ve.com.abicelis.remindy.util.SharedPreferenceUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    //CONST
    public static final String TAG = HomeActivity.class.getSimpleName();
    public static final int NEW_TASK_REQUEST_CODE = 490;
    public static final String NEW_TASK_RETURN_REMINDER_TYPE = "NEW_TASK_RETURN_REMINDER_TYPE";

    //UI
    private ViewPager mViewpager;
    private HomeViewPagerAdapter mHomeViewPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private HomeListFragment mUnprogrammedTasksListFragment;
    private HomeListFragment mProgrammedTasksListFragment;
    private HomeListFragment mDoneTasksListFragment;

    //DATA
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private TaskSortType mTaskSortType = TaskSortType.DATE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_home_toolbar_title);

        mViewpager = (ViewPager) findViewById(R.id.activity_home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_home_tab_layout);
        mFab = (FloatingActionButton) findViewById(R.id.activity_home_fab);
        mFab.setOnClickListener(this);

        setupViewPagerAndTabLayout();
        startNotificationService();
    }


    private void setupViewPagerAndTabLayout() {

        mUnprogrammedTasksListFragment = new HomeListFragment();
        mProgrammedTasksListFragment = new HomeListFragment();
        mDoneTasksListFragment = new HomeListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.UNPROGRAMMED);
        mUnprogrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.PROGRAMMED);
        mProgrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.DONE);
        mDoneTasksListFragment.setArguments(bundle);


        //Rebuild lists
        titleList.clear();
        fragmentList.clear();

        titleList.add(getResources().getString(ViewPagerTaskDisplayType.UNPROGRAMMED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.PROGRAMMED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.DONE.getFriendlyNameRes()));

        fragmentList.add(mUnprogrammedTasksListFragment);
        fragmentList.add(mProgrammedTasksListFragment);
        fragmentList.add(mDoneTasksListFragment);


        //Setup adapter, viewpager and tablayout
        mHomeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mViewpager.setAdapter(mHomeViewPagerAdapter);
        mViewpager.setCurrentItem(1);     //Start at page 2
        mTabLayout.setupWithViewPager(mViewpager);
    }


    private void startNotificationService() {
        Intent startNotificationServiceIntent = new Intent(getApplicationContext(), NotificationIntentService.class);
        startService(startNotificationServiceIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_home_sort:
                mTaskSortType = (mTaskSortType == TaskSortType.DATE ? TaskSortType.PLACE : TaskSortType.DATE);
                mHomeViewPagerAdapter.getRegisteredFragment(1).setSortTypeAndRefresh(mTaskSortType);
                mHomeViewPagerAdapter.getRegisteredFragment(2).setSortTypeAndRefresh(mTaskSortType);
                SnackbarUtil.showSnackbar(mViewpager, SnackbarUtil.SnackbarType.NOTICE, mTaskSortType.getFriendlyMessageRes(), SnackbarUtil.SnackbarDuration.SHORT, null);
                return true;

            case R.id.menu_home_settings:
                Intent goToSettingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(goToSettingsActivity);
                return true;

            case R.id.menu_home_about:
                Intent goToAboutActivity = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(goToAboutActivity);
                return true;

            case R.id.menu_home_rate:
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_home_fab:
                Intent openTaskActivity = new Intent(this, TaskActivity.class);
                startActivityForResult(openTaskActivity, NEW_TASK_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            //Try to get NEW_TASK_RETURN_REMINDER_TYPE
            ReminderType rt;
            if (data.hasExtra(NEW_TASK_RETURN_REMINDER_TYPE)) {
                rt = (ReminderType) data.getSerializableExtra(NEW_TASK_RETURN_REMINDER_TYPE);

                try {
                    if(rt.equals(ReminderType.NONE))
                        mHomeViewPagerAdapter.getRegisteredFragment(0).refreshRecyclerView();   //Unprogrammed tasks will always be in tab #1
                    else
                        mHomeViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();   //Programmed tasks will always be in tab #1
                }catch (NullPointerException e) {/* Do nothing, the recycler will be refreshed upon creation */}
            } else {
                setupViewPagerAndTabLayout();   //Just refresh everything
            }
        }

        if (requestCode == TaskDetailActivity.TASK_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {     //Task has been deleted or edited

            //Try to get TASK_DETAIL_RETURN_TASK_POSITION and TASK_DETAIL_RETURN_ACTION_TYPE
            if (data.hasExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_POSITION) &&
                    data.hasExtra(TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_TYPE) &&
                    data.hasExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX)) {

                int position = data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_POSITION, -1);
                int viewPagerIndex = data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX, -1);

                switch (data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_TYPE, -1)) {
                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_DELETED:
                        try {
                            mHomeViewPagerAdapter.getRegisteredFragment(viewPagerIndex).removeViewHolderItem(position);
                            } catch (NullPointerException e) {/* Do nothing, the recycler will be refreshed upon creation */}
                        break;
                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_EDITED:
                        try {
                            mHomeViewPagerAdapter.getRegisteredFragment(viewPagerIndex).updateViewholderItem(position);
                        } catch (NullPointerException e) {/* Do nothing, the recycler will be refreshed upon creation */}
                        break;

                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_EDITED_REMINDER:
                        setupViewPagerAndTabLayout();   //Just refresh everything
                        break;
                }
            } else {
                Log.d(TAG, "Error! TASK_DETAIL_RETURN_TASK_POSITION or TASK_DETAIL_RETURN_ACTION_TYPE or TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX == null");
                SnackbarUtil.showSnackbar(mViewpager, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        }
    }



}
