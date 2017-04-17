package ve.com.abicelis.remindy.model.attachment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.enums.AttachmentType;

/**
 * Created by abice on 3/3/2017.
 */

public class ListAttachment extends Attachment implements Serializable {

    private List<ListItemAttachment> items;

    public ListAttachment() {
        this.items = new ArrayList<>();
    }
    public ListAttachment(int id, int reminderId, String itemsJsonText) {
        super(id, reminderId);

        setItemsJson(itemsJsonText);
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.LIST;
    }


    public List<ListItemAttachment> getItems() {
        return items;
    }

    public void removeItem(int position) {

    }

    public String getItemsJson() {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    public void setItemsJson(String itemsJsonText) {
        Type listType = new TypeToken<List<ListItemAttachment>>() {}.getType();
        Gson gson = new Gson();
        items = gson.fromJson(itemsJsonText, listType);
    }

}
