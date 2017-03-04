package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public abstract class ReminderExtra {
    private int id;

    public abstract ReminderExtraType getType();
}
