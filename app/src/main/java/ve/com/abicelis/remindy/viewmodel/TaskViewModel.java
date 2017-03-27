package ve.com.abicelis.remindy.viewmodel;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;

import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.model.Task;

/**
 * Created by abice on 26/3/2017.
 */

public class TaskViewModel {

    //DATA
    private Task task = null;
    private String headerTitle = null;
    private boolean headerTitleRed;
    private TaskViewModelType viewModelType;


    public TaskViewModel(@NonNull Task task, @NonNull TaskViewModelType viewModelType) {

        if(viewModelType == TaskViewModelType.HEADER)
            throw new InvalidParameterException("TaskViewModelType cannot be of type HEADER");

        this.task = task;
        this.viewModelType = viewModelType;
    }

    public TaskViewModel(@NonNull String headerTitle, @NonNull boolean headerTitleRed) {
        this.headerTitle = headerTitle;
        this.headerTitleRed = headerTitleRed;
        viewModelType = TaskViewModelType.HEADER;
    }


    public Task getTask() {
        return task;
    }
    public String getHeaderTitle() {
        return headerTitle;
    }
    public boolean isHeaderTitleRed() {return headerTitleRed;}

    public TaskViewModelType getViewModelType() {
        return viewModelType;
    }
}
