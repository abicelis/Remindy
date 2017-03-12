package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraAudio extends ReminderExtra {

    private byte[] audio;

    public ReminderExtraAudio(int reminderId, byte[] audio) {
        super(reminderId);
        this.audio = audio;
    }
    public ReminderExtraAudio(int id, int reminderId, byte[] audio) {
        super(id, reminderId);
        this.audio = audio;
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.AUDIO;
    }

    public byte[] getAudio() {
        return audio;
    }
    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
