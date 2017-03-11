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

    /* Reminder Table */
    static abstract class ReminderTable implements BaseColumns {
        static final String TABLE_NAME = "reminder";

        static final TableColumn COLUMN_NAME_STATUS = new TableColumn(DataType.TEXT, "status");
        static final TableColumn COLUMN_NAME_TITLE = new TableColumn(DataType.TEXT, "title");
        static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        static final TableColumn COLUMN_NAME_CATEGORY = new TableColumn(DataType.TEXT, "category");
        static final TableColumn COLUMN_NAME_PLACE_FK = new TableColumn(DataType.TEXT, "fk_place");
        static final TableColumn COLUMN_NAME_DATE_TYPE = new TableColumn(DataType.TEXT, "datetype");
        static final TableColumn COLUMN_NAME_START_DATE = new TableColumn(DataType.INTEGER, "startdate");
        static final TableColumn COLUMN_NAME_END_DATE = new TableColumn(DataType.INTEGER, "enddate");
        static final TableColumn COLUMN_NAME_TIME_TYPE = new TableColumn(DataType.TEXT, "timetype");
        static final TableColumn COLUMN_NAME_START_TIME = new TableColumn(DataType.INTEGER, "starttime");
        static final TableColumn COLUMN_NAME_END_TIME = new TableColumn(DataType.INTEGER, "endtime");
    }

    /* ExtraText Table */
    static abstract class ExtraTable implements BaseColumns {
        static final String TABLE_NAME = "extra";

        static final TableColumn COLUMN_NAME_REMINDER_FK = new TableColumn(DataType.TEXT, "fk_reminder");
        static final TableColumn COLUMN_NAME_TYPE = new TableColumn(DataType.TEXT, "type");
        static final TableColumn COLUMN_NAME_CONTENT_TEXT = new TableColumn(DataType.TEXT, "text_content");
        static final TableColumn COLUMN_NAME_CONTENT_BLOB = new TableColumn(DataType.BLOB, "blob_content");
    }
}
