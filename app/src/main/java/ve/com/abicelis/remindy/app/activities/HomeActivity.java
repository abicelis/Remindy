package ve.com.abicelis.remindy.app.activities;

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
import ve.com.abicelis.remindy.enums.TaskStatus;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    //UI
    private ViewPager mViewpager;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;

    //DATA
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_home_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLists() {
        titleList.add(getResources().getString(R.string.task_status_unprogrammed));
        titleList.add(getResources().getString(R.string.task_status_programmed));
        titleList.add(getResources().getString(R.string.task_status_done));

        Fragment unprogrammedTasksListFragment = new TaskListFragment();
        Fragment programmedTasksListFragment = new TaskListFragment();
        Fragment doneTasksListFragment = new TaskListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.UNPROGRAMMED);
        unprogrammedTasksListFragment.setArguments(bundle);
        fragmentList.add(unprogrammedTasksListFragment);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.PROGRAMMED);
        programmedTasksListFragment.setArguments(bundle);
        fragmentList.add(programmedTasksListFragment);

        bundle = new Bundle();
        bundle.putSerializable(TaskListFragment.TASK_TYPE_TO_DISPLAY, TaskStatus.DONE);
        doneTasksListFragment.setArguments(bundle);
        fragmentList.add(doneTasksListFragment);

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
