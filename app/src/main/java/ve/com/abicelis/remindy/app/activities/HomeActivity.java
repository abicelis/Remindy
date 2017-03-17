package ve.com.abicelis.remindy.app.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.dialogs.NewReminderDialogFragment;
import ve.com.abicelis.remindy.app.fragments.ReminderListFragment;
import ve.com.abicelis.remindy.app.adapters.RemindersViewPagerAdapter;
import ve.com.abicelis.remindy.enums.ReminderStatus;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    //UI
    private ViewPager mGraphsViewpager;
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

        initLists();

        RemindersViewPagerAdapter adapter = new RemindersViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mGraphsViewpager = (ViewPager) findViewById(R.id.activity_home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_home_tab_layout);
        mFab = (FloatingActionButton) findViewById(R.id.activity_home_fab);

        mGraphsViewpager.setAdapter(adapter);
        mGraphsViewpager.setCurrentItem(1);     //Start at page 2
        mTabLayout.setupWithViewPager(mGraphsViewpager);
        mFab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLists() {
        titleList.add(getResources().getString(R.string.reminder_status_archived));
        titleList.add(getResources().getString(R.string.reminder_status_current));
        titleList.add(getResources().getString(R.string.reminder_status_done));

        Fragment archivedReminderListFragment = new ReminderListFragment();
        Fragment activeReminderListFragment = new ReminderListFragment();
        Fragment doneReminderListFragment = new ReminderListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ReminderListFragment.REMINDER_TO_DISPLAY, ReminderStatus.ARCHIVED);
        archivedReminderListFragment.setArguments(bundle);
        fragmentList.add(archivedReminderListFragment);

        bundle = new Bundle();
        bundle.putSerializable(ReminderListFragment.REMINDER_TO_DISPLAY, ReminderStatus.ACTIVE);
        activeReminderListFragment.setArguments(bundle);
        fragmentList.add(activeReminderListFragment);

        bundle = new Bundle();
        bundle.putSerializable(ReminderListFragment.REMINDER_TO_DISPLAY, ReminderStatus.DONE);
        doneReminderListFragment.setArguments(bundle);
        fragmentList.add(doneReminderListFragment);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_home_fab:
                showAddReminderDialog();
                break;

        }
    }

    private void showAddReminderDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewReminderDialogFragment dialog = NewReminderDialogFragment.newInstance();
        dialog.show(fm, "NewReminderDialogFragment");
    }
}
