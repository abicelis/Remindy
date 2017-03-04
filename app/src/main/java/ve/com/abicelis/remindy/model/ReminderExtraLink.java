package ve.com.abicelis.remindy.model;

import ve.com.abicelis.remindy.enums.ReminderExtraType;
import ve.com.abicelis.remindy.exception.MalformedLinkException;

import
/**
 * Created by abice on 3/3/2017.
 */

public class ReminderExtraLink extends ReminderExtra {

    private String link;

    ReminderExtraLink(String link) throws MalformedLinkException {
        setLink(link);
    }



    public String getLink() {
        return link;
    }
    public void setLink(String link) throws MalformedLinkException {
        if(isValid(link))
            this.link = link;
        else
            throw new MalformedLinkException("Link '" + link + "' is invalid, malformed");
    }

    @Override
    public ReminderExtraType getType() {
        return ReminderExtraType.RECORDING;
    }

    private boolean isValid(String link) {
        return false;
    }
}
