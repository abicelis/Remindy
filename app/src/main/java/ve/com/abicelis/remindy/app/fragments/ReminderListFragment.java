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

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.ReminderAdapter;
import ve.com.abicelis.remindy.database.RemindyDAO;
import ve.com.abicelis.remindy.enums.ReminderSortType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.model.Reminder;

/**
 * Created by abice on 13/3/2017.
 */

public class ReminderListFragment extends Fragment {

    public static final String REMINDER_TO_DISPLAY = "REMINDER_TO_DISPLAY";

    //DATA
    private List<Reminder> reminders = new ArrayList<>();
    private ReminderStatus reminderTypeToDisplay;
    private RemindyDAO mDao;

    //UI
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ReminderAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout mNoItemsContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            reminderTypeToDisplay = (ReminderStatus)getArguments().getSerializable(REMINDER_TO_DISPLAY);
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
        mAdapter = new ReminderAdapter(getActivity(), reminders);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));

        mRecyclerView.addItemDecoration(itemDecoration);
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


    public void refreshRecyclerView() {

        if(mDao == null)
            mDao = new RemindyDAO(getActivity().getApplicationContext());

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of the reminder list
        reminders.clear();
        reminders.addAll(mDao.getRemindersByStatus(reminderTypeToDisplay, ReminderSortType.DATE));

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
