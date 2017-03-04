package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraRecording extends ReminderExtra {

    //add recording bytes?

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.RECORDING;
    }
}
