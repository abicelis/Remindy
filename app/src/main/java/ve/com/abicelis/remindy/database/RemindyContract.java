package ve.com.abicelis.remindy.database;

import android.provider.BaseColumns;

/**
 * Created by Alex on 9/3/2017.
 */
final class RemindyContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    RemindyContract() { }

    /* One-time Reminders Table */
    static abstract class OneTimeReminderTable implements BaseColumns {
        static final String TABLE_NAME = "one_time_reminder";

        static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        static final TableColumn COLUMN_NAME_TIME = new TableColumn(DataType.INTEGER, "time");
    }


    /* Repeating Reminders Table */
    static abstract class RepeatingReminderTable implements BaseColumns {
        static final String TABLE_NAME = "repeating_reminder";

        static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        static final TableColumn COLUMN_NAME_TIME = new TableColumn(DataType.INTEGER, "time");
        static final TableColumn COLUMN_NAME_REPEAT_TYPE = new TableColumn(DataType.TEXT, "repeat_type");
        static final TableColumn COLUMN_NAME_REPEAT_INTERVAL = new TableColumn(DataType.INTEGER, "repeat_interval");
        static final TableColumn COLUMN_NAME_REPEAT_END_TYPE = new TableColumn(DataType.TEXT, "repeat_end_type");
        static final TableColumn COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS = new TableColumn(DataType.INTEGER, "repeat_number_of_events");
        static final TableColumn COLUMN_NAME_REPEAT_END_DATE = new TableColumn(DataType.INTEGER, "repeat_end_date");
    }

    /* Places Table */
    static abstract class PlaceTable implements BaseColumns {
        static final String TABLE_NAME = "place";

        static final TableColumn COLUMN_NAME_ALIAS = new TableColumn(DataType.TEXT, "alias");
        static final TableColumn COLUMN_NAME_ADDRESS = new TableColumn(DataType.TEXT, "address");
        static final TableColumn COLUMN_NAME_LATITUDE = new TableColumn(DataType.TEXT, "latitude");
        static final TableColumn COLUMN_NAME_LONGITUDE = new TableColumn(DataType.TEXT, "longitude");
        static final TableColumn COLUMN_NAME_RADIUS = new TableColumn(DataType.TEXT, "radius");
        static final TableColumn COLUMN_NAME_IS_ONE_OFF = new TableColumn(DataType.TEXT, "is_one_off");
    }

    /* Location-based Reminders Table */
    static abstract class LocationBasedReminderTable implements BaseColumns {
        static final String TABLE_NAME = "location_based_reminder";

        static final TableColumn COLUMN_NAME_PLACE_FK = new TableColumn(DataType.INTEGER, "fk_place");
        static final TableColumn COLUMN_NAME_IS_ENTERING = new TableColumn(DataType.TEXT, "is_entering");
    }


//    /* ADVANCED Reminders Table */
//    static abstract class AdvancedReminderTable implements BaseColumns {
//        static final String TABLE_NAME = "advanced_reminder";
//
//        static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
//        static final TableColumn COLUMN_NAME_TIME = new TableColumn(DataType.INTEGER, "time");
//        static final TableColumn COLUMN_NAME_REPEAT_TYPE = new TableColumn(DataType.TEXT, "repeat_type");
//        static final TableColumn COLUMN_NAME_REPEAT_INTERVAL = new TableColumn(DataType.INTEGER, "repeat_interval");
//        static final TableColumn COLUMN_NAME_REPEAT_END_TYPE = new TableColumn(DataType.TEXT, "repeat_end_type");
//        static final TableColumn COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS = new TableColumn(DataType.INTEGER, "repeat_number_of_events");
//        static final TableColumn COLUMN_NAME_REPEAT_END_DATE = new TableColumn(DataType.INTEGER, "repeat_end_date");
//    }


    /* Tasks Table */
    static abstract class TaskTable implements BaseColumns {
        static final String TABLE_NAME = "task";

        static final TableColumn COLUMN_NAME_STATUS = new TableColumn(DataType.TEXT, "status");
        static final TableColumn COLUMN_NAME_TITLE = new TableColumn(DataType.TEXT, "title");
        static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        static final TableColumn COLUMN_NAME_CATEGORY = new TableColumn(DataType.TEXT, "category");
        static final TableColumn COLUMN_NAME_REMINDER_TYPE = new TableColumn(DataType.TEXT, "reminder_type");
        static final TableColumn COLUMN_NAME_REMINDER_ID = new TableColumn(DataType.INTEGER, "reminder_id");
        static final TableColumn COLUMN_NAME_DONE_DATE = new TableColumn(DataType.INTEGER, "done_date");
    }


    /* Attachments Table */
    static abstract class AttachmentTable implements BaseColumns {
        static final String TABLE_NAME = "attachment";

        static final TableColumn COLUMN_NAME_TASK_FK = new TableColumn(DataType.TEXT, "fk_task");
        static final TableColumn COLUMN_NAME_TYPE = new TableColumn(DataType.TEXT, "type");
        static final TableColumn COLUMN_NAME_CONTENT_TEXT = new TableColumn(DataType.TEXT, "text_content");
        static final TableColumn COLUMN_NAME_CONTENT_BLOB = new TableColumn(DataType.BLOB, "blob_content");
    }




}
