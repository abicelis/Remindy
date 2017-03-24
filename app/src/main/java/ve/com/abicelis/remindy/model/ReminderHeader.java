package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderHeader extends Reminder implements Serializable {

    private String headerTitle;

    public ReminderHeader(@NonNull String headerTitle) {
        super();
        this.headerTitle = headerTitle;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.HEADER;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }
    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
}
