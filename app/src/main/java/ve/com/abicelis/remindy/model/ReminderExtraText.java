package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraText extends ReminderExtra {

    private String text;

    ReminderExtraText(String text) {
        this.text = text;
    }



    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.RECORDING;
    }
}
