package ve.com.abicelis.remindy.model;

import java.util.regex.Pattern;

import ve.com.abicelis.remindy.enums.ReminderExtraType;
import ve.com.abicelis.remindy.exception.MalformedLinkException;

/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraLink extends ReminderExtra {

    private String link;

    public ReminderExtraLink(String link) {
        this.link = link;
    }
    public ReminderExtraLink(int id, int reminderId, String link) {
        super(id, reminderId);
        this.link = link;
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.LINK;
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
        //TODO: LINK REGEX HEREEE
        Pattern mDomainPattern = Pattern.compile("LINK REGEX");
        return (mDomainPattern.matcher((link)).matches());
    }
}
