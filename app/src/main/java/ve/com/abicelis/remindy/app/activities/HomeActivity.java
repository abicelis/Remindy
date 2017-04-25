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
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.fragments.HomeListFragment;
import ve.com.abicelis.remindy.app.adapters.HomeViewPagerAdapter;
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
    private HomeViewPagerAdapter mHomeViewPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private HomeListFragment mUnprogrammedTasksListFragment;
    private HomeListFragment mLocationBasedTasksListFragment;
    private HomeListFragment mProgrammedTasksListFragment;
    private HomeListFragment mDoneTasksListFragment;

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

        mUnprogrammedTasksListFragment = new HomeListFragment();
        mLocationBasedTasksListFragment = new HomeListFragment();
        mProgrammedTasksListFragment = new HomeListFragment();
        mDoneTasksListFragment = new HomeListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.UNPROGRAMMED);
        mUnprogrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.LOCATION_BASED);
        mLocationBasedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.PROGRAMMED);
        mProgrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.DONE);
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
        mHomeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mViewpager.setAdapter(mHomeViewPagerAdapter);
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
                        mHomeViewPagerAdapter.getRegisteredFragment(0).refreshRecyclerView();
                        break;

                    case LOCATION_BASED:
                        mHomeViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();          //Location based tasks will always be in tab #1
                        break;

                    case ONE_TIME:
                    case REPEATING:
                        if (mShowLocationBasedTasksInOwnViewPagerTab)
                            mHomeViewPagerAdapter.getRegisteredFragment(2).refreshRecyclerView();      //These tasks are in tab 2.
                        else
                            mHomeViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();      //Otherwise refresh tab 1, where all programmed tasks are.
                        break;
                }
            } else {
                setupViewPagerAndTabLayout();   //Just refresh everything
            }
        }
    }
}
