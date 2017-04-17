package ve.com.abicelis.remindy.model.attachment;

import java.io.Serializable;

/**
 * Created by abice on 3/3/2017.
 */

public class ListItemAttachment implements Serializable {

    private String text;
    private boolean checked;

    public ListItemAttachment() {
    }
    public ListItemAttachment(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
