package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraPhoto extends ReminderExtra {

    //add photo bytes?
    //maybe a thumbnail?


    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.PHOTO;
    }
}
