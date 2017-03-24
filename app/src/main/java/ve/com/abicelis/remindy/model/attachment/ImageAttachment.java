package ve.com.abicelis.remindy.model.attachment;

import ve.com.abicelis.remindy.enums.AttachmentType;

/**
 * Created by abice on 3/3/2017.
 */

public class ImageAttachment extends Attachment {

    private byte[] thumbnail;
    private String fullImagePath;

    public ImageAttachment(byte[] thumbnail, String fullImagePath) {
        this.thumbnail = thumbnail;
        this.fullImagePath = fullImagePath;
    }
    public ImageAttachment(int id, int reminderId, byte[] thumbnail, String fullImagePath) {
        super(id, reminderId);
        this.thumbnail = thumbnail;
        this.fullImagePath = fullImagePath;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.IMAGE;
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
