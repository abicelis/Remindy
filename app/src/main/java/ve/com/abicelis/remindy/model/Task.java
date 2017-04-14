package ve.com.abicelis.remindy.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.enums.TaskCategory;
import ve.com.abicelis.remindy.enums.TaskStatus;
import ve.com.abicelis.remindy.model.attachment.Attachment;
import ve.com.abicelis.remindy.model.reminder.Reminder;

/**
 * Created by abice on 3/3/2017.
 */

public class Task implements Serializable {

    private int id;
    private TaskStatus status;
    private String title;
    private String description;
    private TaskCategory category;
    private ReminderType reminderType;
    private Reminder reminder;
    private Calendar doneDate;
    private ArrayList<Attachment> attachments;

    public Task() {     //Empty constructor for creating new tasks
        this.attachments = new ArrayList<>();
    }

    public Task(@NonNull TaskStatus status, @NonNull String title, @NonNull String description, @NonNull TaskCategory category, @NonNull ReminderType reminderType, @Nullable Reminder reminder, @Nullable Calendar doneDate) {
        this.status = status;
        this.title = title;
        this.description = description;
        this.category = category;
        this.reminderType = reminderType;
        this.reminder = reminder;
        this.doneDate = doneDate;
        this.attachments = new ArrayList<>();
    }

    public Task(int id, @NonNull TaskStatus status, @NonNull String title, @NonNull String description, @NonNull TaskCategory category, @NonNull ReminderType reminderType, @Nullable Reminder reminder, @Nullable Calendar doneDate) {
        this(status, title, description, category, reminderType, reminder, doneDate);
        this.id = id;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
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

    public TaskCategory getCategory() {
        return category;
    }
    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public ReminderType getReminderType() {
        return reminderType;
    }
    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    public Reminder getReminder() {
        return reminder;
    }
    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public Calendar getDoneDate() {
        return doneDate;
    }
    public void setDoneDate(Calendar doneDate) {
        this.doneDate = doneDate;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }
    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }
    public void clearAttachments() {
        attachments.clear();
    }



    @Override
    public String toString() {
        String                      res = "Task ID=" + id + "\r\n Status= " + status.name() + "\r\n Title=" + title + "\r\n Description=" + description + "\r\n Category=" + category.name() + "\r\n";
                                    res += " ReminderType=" + reminderType.name();
        if(reminder != null)        res += " Reminder=" + reminder.toString() + "\r\n";
        if(doneDate != null)        res += " DoneDate=" + doneDate.toString();
                                    res += " Attachments="  + attachments.size();
        return res;
    }
}
