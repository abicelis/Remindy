package ve.com.abicelis.remindy.model.attachment;

import android.util.Patterns;

import java.util.regex.Pattern;

import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.exception.MalformedLinkException;

/**
 * Created by abice on 3/3/2017.
 */

public class LinkAttachment extends Attachment {

    private String link;

    public LinkAttachment(String link) {
        this.link = link;
    }
    public LinkAttachment(int id, int reminderId, String link) {
        super(id, reminderId);
        this.link = link;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.LINK;
    }


    public String getLink() {
        return link;
    }
    public void setLink(String link) throws MalformedLinkException {
        if(isValid(link))
            this.link = link;
        else
            throw new MalformedLinkException("Link '" + link + "' is invalid");
    }
    private boolean isValid(String link) {
        return Patterns.WEB_URL.matcher(link).matches();
    }
}
