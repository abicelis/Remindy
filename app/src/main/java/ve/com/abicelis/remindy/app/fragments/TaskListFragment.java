package ve.com.abicelis.remindy.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.TaskAdapter;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.TaskSortType;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.exception.CouldNotGetDataException;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.util.SnackbarUtil;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 13/3/2017.
 */

public class TaskListFragment extends Fragment {

    public static final String TASK_TYPE_TO_DISPLAY = "TASK_TYPE_TO_DISPLAY";

    //DATA
    private List<TaskViewModel> tasks = new ArrayList<>();
    private TaskStatus reminderTypeToDisplay;
    private RemindyDAO mDao;
    private TaskSortType mTaskSortType = TaskSortType.DATE;

    //UI
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TaskAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout mNoItemsContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            reminderTypeToDisplay = (TaskStatus)getArguments().getSerializable(TASK_TYPE_TO_DISPLAY);
        }catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Error! reminderTypeToDisplay == null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        refreshRecyclerView();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_reminder_list_recycler);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_reminder_list_swipe_refresh);
        mNoItemsContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_reminder_list_no_items_container);

        setUpRecyclerView();
        setUpSwipeRefresh();
        return rootView;
    }


    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new TaskAdapter(getActivity(), tasks);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));

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

    private void refreshRecyclerView() {

        if(mDao == null)
            mDao = new RemindyDAO(getActivity().getApplicationContext());

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of the reminder list
        tasks.clear();

        try {
            switch (reminderTypeToDisplay) {
                case UNPROGRAMMED:
                    tasks.addAll(mDao.getUnprogrammedTasks());
                    break;
                case PROGRAMMED:
                    //TODO: Sorting by date by default, change this
                    tasks.addAll(mDao.getProgrammedTasks(mTaskSortType, getResources()));
                    break;
                case DONE:
                    //TODO: Sorting by date by default, change this
                    tasks.addAll(mDao.getDoneTasks(mTaskSortType, getResources()));
            }
        }catch (CouldNotGetDataException | InvalidClassException e) {
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_getting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        //tasks.addAll(mDao.getRemindersByStatus(reminderTypeToDisplay, TaskSortType.DATE));

//            //If a new expense was added
//            if(newExpensesCount == oldExpensesCount+1) {
//                mAdapter.notifyItemInserted(0);
//                mAdapter.notifyItemRangeChanged(1, activeCreditCard.getCreditPeriods().get(0).getExpenses().size()-1);
//                mLayoutManager.scrollToPosition(0);
//            } else {
                mAdapter.notifyDataSetChanged();
//            }


    }

}
