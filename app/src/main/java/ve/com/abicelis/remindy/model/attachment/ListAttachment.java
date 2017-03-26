package ve.com.abicelis.remindy.model.attachment;

import ve.com.abicelis.remindy.enums.AttachmentType;

/**
 * Created by abice on 3/3/2017.
 */

public class ListAttachment extends Attachment {

    private String text;

    public ListAttachment(String text) {
        this.text = text;
    }
    public ListAttachment(int id, int reminderId, String text) {
        super(id, reminderId);
        this.text = text;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.TEXT;
    }


    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
