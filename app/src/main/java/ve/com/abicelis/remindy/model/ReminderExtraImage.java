package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraImage extends ReminderExtra {

    private byte[] thumbnail;
    private String fullImagePath;

    public ReminderExtraImage(int reminderId, byte[] thumbnail, String fullImagePath) {
        super(reminderId);
        this.thumbnail = thumbnail;
        this.fullImagePath = fullImagePath;
    }
    public ReminderExtraImage(int id, int reminderId, byte[] thumbnail, String fullImagePath) {
        super(id, reminderId);
        this.thumbnail = thumbnail;
        this.fullImagePath = fullImagePath;
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.IMAGE;
    }


    public byte[] getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullImagePath() {
        return fullImagePath;
    }
    public void setFullImagePath(String fullImagePath) {
        this.fullImagePath = fullImagePath;
    }
}
