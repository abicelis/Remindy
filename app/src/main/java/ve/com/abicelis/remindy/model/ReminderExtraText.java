package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraText extends ReminderExtra {

    private String text;

    public ReminderExtraText(String text) {
        this.text = text;
    }
    public ReminderExtraText(int id, int reminderId, String text) {
        super(id, reminderId);
        this.text = text;
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.AUDIO;
    }


    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
