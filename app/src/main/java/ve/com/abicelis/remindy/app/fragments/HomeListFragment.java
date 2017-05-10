package ve.com.abicelis.remindy.app.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.activities.TaskDetailActivity;
import ve.com.abicelis.remindy.app.adapters.HomeAdapter;
import ve.com.abicelis.remindy.app.interfaces.ViewHolderClickListener;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.TaskSortType;
import ve.com.abicelis.remindy.enums.ViewPagerTaskDisplayType;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.ConversionUtil;
import ve.com.abicelis.remindy.util.SnackbarUtil;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeListFragment extends Fragment implements ViewHolderClickListener {

    public static final String ARGUMENT_TASK_TYPE_TO_DISPLAY = "ARGUMENT_TASK_TYPE_TO_DISPLAY";
    public static final String TAG = HomeListFragment.class.getSimpleName();

    //DATA
    private List<TaskViewModel> mTasks = new ArrayList<>();
    private ViewPagerTaskDisplayType mReminderTypeToDisplay;
    private RemindyDAO mDao;
    private TaskSortType mTaskSortType = TaskSortType.DATE;

    //UI
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HomeAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout mNoItemsContainer;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            mReminderTypeToDisplay = (ViewPagerTaskDisplayType)getArguments().getSerializable(ARGUMENT_TASK_TYPE_TO_DISPLAY);
        }catch (NullPointerException e) {
            Log.d(TAG, "Error! mReminderTypeToDisplay == null" + e.getMessage());
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_home_list_recycler);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_list_swipe_refresh);
        mNoItemsContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_home_list_no_items_container);

        setUpRecyclerView();
        setUpSwipeRefresh();

        refreshRecyclerView();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        switch (mReminderTypeToDisplay) {
            case UNPROGRAMMED:
                inflater.inflate(R.menu.menu_home_no_sort, menu);
                break;
            case PROGRAMMED:
                inflater.inflate(R.menu.menu_home_sort, menu);
                break;
            case DONE:
                inflater.inflate(R.menu.menu_home_sort, menu);
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new HomeAdapter(this, mTasks);

        //DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), mLayoutManager.getOrientation());
        //itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));
        //mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpSwipeRefresh() {
        mSwipeRefresh.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        mSwipeRefresh.setRefreshing(false);
                                                    }
                                                }
        );
    }

    public void setSortTypeAndRefresh(TaskSortType taskSortType) {
        mTaskSortType = taskSortType;
        refreshRecyclerView();
    }

    public void refreshRecyclerView() {

        if(mDao == null)
            mDao = new RemindyDAO(getActivity().getApplicationContext());

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of the reminder list
        mTasks.clear();

        try {
            switch (mReminderTypeToDisplay) {
                case UNPROGRAMMED:
                    mTasks.addAll(mDao.getUnprogrammedTasks());
                    break;

                case PROGRAMMED:
                    mTasks.addAll(mDao.getProgrammedTasks(mTaskSortType, true, getResources()));
                    break;

                case DONE:
                    mTasks.addAll(mDao.getDoneTasks(mTaskSortType, getResources()));
            }
        }catch (CouldNotGetDataException | InvalidClassException e) {
            Log.d(TAG, "Error fetching data from db for recyclerView: " + e.getMessage());
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_getting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        mAdapter.notifyDataSetChanged();

        if(mTasks.size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoItemsContainer.setVisibility(View.GONE);
        }
    }

    /* Called from HomeActivity.onActivityResult() */
    public void updateViewholderItem(int position) {
        //Task was edited, refresh task info and refresh recycler
        try {
            Task task = mDao.getTask(mTasks.get(position).getTask().getId());
            TaskViewModel taskViewModel = new TaskViewModel(task, ConversionUtil.taskReminderTypeToTaskViewmodelType(task.getReminderType()));
            mTasks.set(position, taskViewModel);
            mAdapter.notifyItemChanged(position);
        }catch (CouldNotGetDataException e) {
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_updating_task_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
        }
    }

    /* Called from HomeActivity.onActivityResult() */
    public void removeViewHolderItem(int position) {
        mTasks.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
    }






    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onItemClicked(int position, @Nullable Intent optionalIntent, @Nullable Bundle optionalBundle) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            //Open task detail activity
            getActivity().startActivityForResult(optionalIntent, TaskDetailActivity.TASK_DETAIL_REQUEST_CODE, optionalBundle);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSelection(position);

        return true;
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_home_contextual, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_contextual_delete:
                    Log.d(TAG, "menu_remove");
                    mode.finish();
                    return true;

                case R.id.home_contextual_done:
                    Log.d(TAG, "home_contextual_done");
                    mode.finish();
                    return true;

                case R.id.home_contextual_not_done:
                    Log.d(TAG, "home_contextual_not_done");
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }
}
