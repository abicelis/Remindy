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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.fragments.TaskListFragment;
import ve.com.abicelis.remindy.app.adapters.TasksViewPagerAdapter;
import ve.com.abicelis.remindy.enums.TaskSortType;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.util.SnackbarUtil;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    //UI
    private ViewPager mViewpager;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;

    TaskListFragment mUnprogrammedTasksListFragment;
    TaskListFragment mProgrammedTasksListFragment;
    TaskListFragment mDoneTasksListFragment;

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

        initLists();

        TasksViewPagerAdapter adapter = new TasksViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mViewpager = (ViewPager) findViewById(R.id.activity_home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_home_tab_layout);
        mFab = (FloatingActionButton) findViewById(R.id.activity_home_fab);

        mViewpager.setAdapter(adapter);
        mViewpager.setCurrentItem(1);     //Start at page 2
        mTabLayout.setupWithViewPager(mViewpager);
        mFab.setOnClickListener(this);
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

    private void initLists() {
        titleList.add(getResources().getString(R.string.task_status_unprogrammed));
        titleList.add(getResources().getString(R.string.task_status_programmed));
        titleList.add(getResources().getString(R.string.task_status_done));

        mUnprogrammedTasksListFragment = new TaskListFragment();
        mProgrammedTasksListFragment = new TaskListFragment();
        mDoneTasksListFragment = new TaskListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.UNPROGRAMMED);
        mUnprogrammedTasksListFragment.setArguments(bundle);
        fragmentList.add(mUnprogrammedTasksListFragment);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.PROGRAMMED);
        mProgrammedTasksListFragment.setArguments(bundle);
        fragmentList.add(mProgrammedTasksListFragment);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.DONE);
        mDoneTasksListFragment.setArguments(bundle);
        fragmentList.add(mDoneTasksListFragment);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_home_fab:
                //TODO: Open newTask Activity
                //showAddReminderDialog();
                break;
        }
    }

//    private void showAddReminderDialog() {
//        FragmentManager fm = getSupportFragmentManager();
//        NewReminderDialogFragment dialog = NewReminderDialogFragment.newInstance();
//        dialog.show(fm, "NewReminderDialogFragment");
//    }
}
