package ve.com.abicelis.remindy.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.dialogs.RecordAudioDialogFragment;
import ve.com.abicelis.remindy.app.fragments.TaskListFragment;
import ve.com.abicelis.remindy.app.adapters.TasksViewPagerAdapter;
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
    public static final int NEW_TASK_REQUEST_CODE = 490;
    public static final String NEW_TASK_RETURN_REMINDER_TYPE = "NEW_TASK_RETURN_REMINDER_TYPE";

    //UI
    private ViewPager mViewpager;
    private TasksViewPagerAdapter mTasksViewPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private TaskListFragment mUnprogrammedTasksListFragment;
    private TaskListFragment mLocationBasedTasksListFragment;
    private TaskListFragment mProgrammedTasksListFragment;
    private TaskListFragment mDoneTasksListFragment;

    //DATA
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private TaskSortType mTaskSortType = TaskSortType.DATE;
    private boolean mShowLocationBasedTasksInOwnViewPagerTab = false;


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

        mShowLocationBasedTasksInOwnViewPagerTab = SharedPreferenceUtil.getShowLocationBasedTasksInOwnTab(getApplicationContext());
        setupViewPagerAndTabLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SharedPreferenceUtil.getShowLocationBasedTasksInOwnTab(getApplicationContext()) != mShowLocationBasedTasksInOwnViewPagerTab) {
            mShowLocationBasedTasksInOwnViewPagerTab = !mShowLocationBasedTasksInOwnViewPagerTab;
            setupViewPagerAndTabLayout();
        }
    }

    private void setupViewPagerAndTabLayout() {

        mUnprogrammedTasksListFragment = new TaskListFragment();
        mLocationBasedTasksListFragment = new TaskListFragment();
        mProgrammedTasksListFragment = new TaskListFragment();
        mDoneTasksListFragment = new TaskListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.UNPROGRAMMED);
        mUnprogrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.LOCATION_BASED);
        mLocationBasedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.PROGRAMMED);
        mProgrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.DONE);
        mDoneTasksListFragment.setArguments(bundle);


        //Rebuild lists
        titleList.clear();
        fragmentList.clear();

        titleList.add(getResources().getString(ViewPagerTaskDisplayType.UNPROGRAMMED.getFriendlyNameRes()));
        if(mShowLocationBasedTasksInOwnViewPagerTab)
            titleList.add(getResources().getString(ViewPagerTaskDisplayType.LOCATION_BASED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.PROGRAMMED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.DONE.getFriendlyNameRes()));

        fragmentList.add(mUnprogrammedTasksListFragment);
        if(mShowLocationBasedTasksInOwnViewPagerTab)
            fragmentList.add(mLocationBasedTasksListFragment);
        fragmentList.add(mProgrammedTasksListFragment);
        fragmentList.add(mDoneTasksListFragment);


        //Setup adapter, viewpager and tablayout
        mTasksViewPagerAdapter = new TasksViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mViewpager.setAdapter(mTasksViewPagerAdapter);
        mViewpager.setCurrentItem(1);     //Start at page 2
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_home_sort:
                mTaskSortType = (mTaskSortType == TaskSortType.DATE ? TaskSortType.PLACE : TaskSortType.DATE);
                mProgrammedTasksListFragment.setSortTypeAndRefresh(mTaskSortType);
                mDoneTasksListFragment.setSortTypeAndRefresh(mTaskSortType);
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
                Intent openNewTaskActivity = new Intent(this, NewTaskActivity.class);
                startActivityForResult(openNewTaskActivity, NEW_TASK_REQUEST_CODE);
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

                switch (rt) {
                    case NONE:
                        mTasksViewPagerAdapter.getRegisteredFragment(0).refreshRecyclerView();
                        break;

                    case LOCATION_BASED:
                        mTasksViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();          //Location based tasks will always be in tab #1
                        break;

                    case ONE_TIME:
                    case REPEATING:
                        if (mShowLocationBasedTasksInOwnViewPagerTab)
                            mTasksViewPagerAdapter.getRegisteredFragment(2).refreshRecyclerView();      //These tasks are in tab 2.
                        else
                            mTasksViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();      //Otherwise refresh tab 1, where all programmed tasks are.
                        break;
                }
            } else {
                setupViewPagerAndTabLayout();   //Just refresh everything
            }
        }
    }
}
