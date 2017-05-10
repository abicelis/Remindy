package ve.com.abicelis.remindy.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.fragments.HomeListFragment;
import ve.com.abicelis.remindy.app.holders.ProgrammedLocationBasedTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.ProgrammedOneTimeTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.ProgrammedRepeatingTaskViewHolder;
import ve.com.abicelis.remindy.app.holders.TaskHeaderViewHolder;
import ve.com.abicelis.remindy.app.holders.UnprogrammedTaskViewHolder;
import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by abice on 13/3/2017.
 */

public class HomeAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    //DATA
    private List<TaskViewModel> mTasks;
    private LayoutInflater mInflater;
    private HomeListFragment mFragment;

    public HomeAdapter(HomeListFragment fragment, List<TaskViewModel> tasks) {
        mTasks = tasks;
        mFragment = fragment;
        mInflater = LayoutInflater.from(fragment.getActivity());
    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.get(position).getViewModelType().ordinal();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        TaskViewModelType viewModelType = TaskViewModelType.values()[viewType];
        switch (viewModelType) {
            case HEADER:
                return new TaskHeaderViewHolder(mInflater.inflate(R.layout.list_item_task_header, parent, false));
            case UNPROGRAMMED_REMINDER:
                return new UnprogrammedTaskViewHolder(mInflater.inflate(R.layout.list_item_task_unprogrammed, parent, false), mFragment);
            case ONE_TIME_REMINDER:
                return new ProgrammedOneTimeTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_one_time, parent, false), mFragment);
            case REPEATING_REMINDER:
                return new ProgrammedRepeatingTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_repeating, parent, false), mFragment);
            case LOCATION_BASED_REMINDER:
                return new ProgrammedLocationBasedTaskViewHolder(mInflater.inflate(R.layout.list_item_task_programmed_location_based, parent, false), mFragment);
            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in HomeAdapter");
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TaskViewModelType viewModelType = mTasks.get(position).getViewModelType();
        boolean nextItemIsATask = false;
        try {
            nextItemIsATask = mTasks.get(position+1).getViewModelType() != TaskViewModelType.HEADER;
        } catch (IndexOutOfBoundsException e) {/*Do nothing*/}

        switch (viewModelType) {
            case HEADER:
                TaskHeaderViewHolder thHolder = (TaskHeaderViewHolder) holder;
                thHolder.setData(this, mFragment, mTasks.get(position).getHeaderTitle(), mTasks.get(position).isHeaderTitleRed(), position);
                break;

            case UNPROGRAMMED_REMINDER:
                UnprogrammedTaskViewHolder uHolder = (UnprogrammedTaskViewHolder) holder;
                uHolder.setData(this, mFragment, mTasks.get(position).getTask(), position, isSelected(position), nextItemIsATask);
                uHolder.setListeners();
                break;

            case ONE_TIME_REMINDER:
                ProgrammedOneTimeTaskViewHolder poHolder = (ProgrammedOneTimeTaskViewHolder) holder;
                poHolder.setData(this, mFragment, mTasks.get(position).getTask(), position, isSelected(position), nextItemIsATask);
                poHolder.setListeners();
                break;

            case REPEATING_REMINDER:
                ProgrammedRepeatingTaskViewHolder prHolder = (ProgrammedRepeatingTaskViewHolder) holder;
                prHolder.setData(this, mFragment, mTasks.get(position).getTask(), position, isSelected(position), nextItemIsATask);
                prHolder.setListeners();
                break;

            case LOCATION_BASED_REMINDER:
                ProgrammedLocationBasedTaskViewHolder plHolder = (ProgrammedLocationBasedTaskViewHolder) holder;
                plHolder.setData(this, mFragment, mTasks.get(position).getTask(), position, isSelected(position), nextItemIsATask);
                plHolder.setListeners();
                break;

            default:
                throw new InvalidParameterException("Wrong viewType passed to onBindViewHolder in HomeAdapter");
        }

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    public void removeItem(int position) {
        mTasks.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mTasks.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
