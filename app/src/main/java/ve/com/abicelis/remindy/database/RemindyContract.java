package ve.com.abicelis.remindy.database;

import android.provider.BaseColumns;

/**
 * Created by Alex on 9/3/2017.
 */
final class RemindyContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    RemindyContract() { }

    /* Place Table */
    static abstract class PlaceTable implements BaseColumns {
        static final String TABLE_NAME = "place";

        static final TableColumn COLUMN_NAME_ALIAS = new TableColumn(DataType.TEXT, "alias");
        static final TableColumn COLUMN_NAME_ADDRESS = new TableColumn(DataType.TEXT, "address");
        static final TableColumn COLUMN_NAME_LATITUDE = new TableColumn(DataType.TEXT, "latitude");
        static final TableColumn COLUMN_NAME_LONGITUDE = new TableColumn(DataType.TEXT, "longitude");
        static final TableColumn COLUMN_NAME_RADIUS = new TableColumn(DataType.TEXT, "radius");
        static final TableColumn COLUMN_NAME_IS_ONE_OFF = new TableColumn(DataType.TEXT, "is_one_off");
    }



    /* Advanced Reminder Table */
    static abstract class AdvancedReminderTable implements BaseColumns {
        static final String TABLE_NAME = "reminder_advanced";

        static final TableColumn COLUMN_NAME_STATUS = new TableColumn(DataType.TEXT, "status");
        static final TableColumn COLUMN_NAME_TITLE = new TableColumn(DataType.TEXT, "title");
        static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        static final TableColumn COLUMN_NAME_CATEGORY = new TableColumn(DataType.TEXT, "category");

        static final TableColumn COLUMN_NAME_PLACE_FK = new TableColumn(DataType.TEXT, "fk_place");
        static final TableColumn COLUMN_NAME_DATE_TYPE = new TableColumn(DataType.TEXT, "date_type");
        static final TableColumn COLUMN_NAME_START_DATE = new TableColumn(DataType.INTEGER, "start_date");
        static final TableColumn COLUMN_NAME_END_DATE = new TableColumn(DataType.INTEGER, "end_date");
        static final TableColumn COLUMN_NAME_TIME_TYPE = new TableColumn(DataType.TEXT, "time_type");
        static final TableColumn COLUMN_NAME_START_TIME = new TableColumn(DataType.INTEGER, "start_time");
        static final TableColumn COLUMN_NAME_END_TIME = new TableColumn(DataType.INTEGER, "end_time");
        static final TableColumn COLUMN_NAME_WEEKDAYS = new TableColumn(DataType.INTEGER, "weekdays");
    }

    /* Extra Table for Advanced Reminders */
    static abstract class AdvancedReminderExtraTable implements BaseColumns {
        static final String TABLE_NAME = "reminder_advanced_extra";

        static final TableColumn COLUMN_NAME_REMINDER_FK = new TableColumn(DataType.TEXT, "fk_reminder");
        static final TableColumn COLUMN_NAME_TYPE = new TableColumn(DataType.TEXT, "type");
        static final TableColumn COLUMN_NAME_CONTENT_TEXT = new TableColumn(DataType.TEXT, "text_content");
        static final TableColumn COLUMN_NAME_CONTENT_BLOB = new TableColumn(DataType.BLOB, "blob_content");
    }



    /* Simple Reminder Table */
    static abstract class SimpleReminderTable implements BaseColumns {
        static final String TABLE_NAME = "reminder_simple";

        static final TableColumn COLUMN_NAME_STATUS = new TableColumn(DataType.TEXT, "status");
        static final TableColumn COLUMN_NAME_TITLE = new TableColumn(DataType.TEXT, "title");
        static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        static final TableColumn COLUMN_NAME_CATEGORY = new TableColumn(DataType.TEXT, "category");

        static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        static final TableColumn COLUMN_NAME_TIME = new TableColumn(DataType.INTEGER, "time");
        static final TableColumn COLUMN_NAME_REPEAT_TYPE = new TableColumn(DataType.TEXT, "repeat_type");
        static final TableColumn COLUMN_NAME_REPEAT_INTERVAL = new TableColumn(DataType.INTEGER, "repeat_interval");
        static final TableColumn COLUMN_NAME_REPEAT_END_TYPE = new TableColumn(DataType.TEXT, "repeat_end_type");
        static final TableColumn COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS = new TableColumn(DataType.INTEGER, "repeat_number_of_events");
        static final TableColumn COLUMN_NAME_REPEAT_END_DATE = new TableColumn(DataType.INTEGER, "repeat_end_date");
    }

    /* Extra Table for Advanced Reminders */
    static abstract class SimpleReminderExtraTable implements BaseColumns {
        static final String TABLE_NAME = "reminder_simple_extra";

        static final TableColumn COLUMN_NAME_REMINDER_FK = new TableColumn(DataType.TEXT, "fk_reminder");
        static final TableColumn COLUMN_NAME_TYPE = new TableColumn(DataType.TEXT, "type");
        static final TableColumn COLUMN_NAME_CONTENT_TEXT = new TableColumn(DataType.TEXT, "text_content");
        static final TableColumn COLUMN_NAME_CONTENT_BLOB = new TableColumn(DataType.BLOB, "blob_content");
    }

}
