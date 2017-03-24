package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderType;

/**
 * Created by abice on 3/3/2017.
 */

public abstract class Reminder implements Serializable {

    int id;
    ReminderStatus status;
    String title;
    String description;
    ReminderCategory category;
    ArrayList<ReminderExtra> extras;

    public Reminder() {/*Used only by ReminderHeader*/}

    public Reminder(@NonNull ReminderStatus status, @NonNull String title, @Nullable String description, @NonNull ReminderCategory category) {
        this.status = status;
        this.title = title;
        this.description = description;
        this.category = category;
        this.extras = new ArrayList<>();
    }

    public Reminder(@NonNull int id, @NonNull ReminderStatus status, @NonNull String title,
                            @Nullable String description, @NonNull ReminderCategory category) {
        this(status, title, description, category);
        this.id = id;
    }

    public abstract ReminderType getType();






    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ReminderStatus getStatus() {
        return status;
    }
    public void setStatus(boolean active) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public ReminderCategory getCategory() {
        return category;
    }
    public void setCategory(ReminderCategory category) {
        this.category = category;
    }


    public ArrayList<ReminderExtra> getExtras() {
        return extras;
    }
    public void setExtras(ArrayList<ReminderExtra> extras) {
        this.extras = extras;
    }
    public void addExtra(ReminderExtra extra) {
        this.extras.add(extra);
    }
    public void clearExtras() {
        extras.clear();
    }
}
