package ve.com.abicelis.remindy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import ve.com.abicelis.remindy.model.Time;
import ve.com.abicelis.remindy.util.FileUtils;


/**
 * Created by Alex on 9/3/2017.
 */
public class RemindyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Remindy.db";
    private static final int DATABASE_VERSION = 1;                               // If you change the database schema, you must increment the database version.
    private static final String COMMA_SEP = ", ";

    private String mAppDbFilepath;
    private String mDbExternalBackupFilepath;
    private Context mContext;


    public RemindyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mAppDbFilepath =  context.getDatabasePath(DATABASE_NAME).getPath();
        mDbExternalBackupFilepath = Environment.getExternalStorageDirectory().getPath() + "/" + DATABASE_NAME;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
        insertMockData(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: FIGURE THIS PART OUT!
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //deleteDatabase(sqLiteDatabase);
        //onCreate(sqLiteDatabase);
    }


    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean exportDatabase() throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (appDatabase.exists()) {
            FileUtils.copyFile(new FileInputStream(appDatabase), new FileOutputStream(backupDatabase));
            return true;
        }
        return false;
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean importDatabase() throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (backupDatabase.exists()) {
            FileUtils.copyFile(new FileInputStream(backupDatabase), new FileOutputStream(appDatabase));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    private void insertMockData(SQLiteDatabase sqLiteDatabase) {
        String statement;

        //Mock times
        int time0600 = new Time(6, 0).getTimeInMinutes();
        int time1259 = new Time(12, 59).getTimeInMinutes();
        int time1800 = new Time(18, 0).getTimeInMinutes();
        int time1930 = new Time(19, 30).getTimeInMinutes();

        //Mock dates
        Calendar cal = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -7);
        long dateLastWeek = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, +9);
        long dateYesterday = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, +5);
        long dateToday = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, +1);
        long dateTomorrow = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, +8);
        long dateNextWeek = cal.getTimeInMillis();




        //Insert mock Tasks Table
        statement = "INSERT INTO " + RemindyContract.TaskTable.TABLE_NAME + " (" +
                RemindyContract.TaskTable._ID + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_STATUS.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_TITLE.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_ID.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getName() +
                ") VALUES " +
                "(0,    'ACTIVE',   'Mock Task 2',      'Task 2 - No Reminder',          'PERSONAL', '',                -1, -1)," +
                "(1,    'ACTIVE',   'Mock Task 3',      'Task 3 - No Reminder',          'BUSINESS', '',                -1, -1)," +
                "(2,    'ACTIVE',   'Mock Task 1',      'Task 1 - No Reminder',          'PERSONAL', '',                -1, -1)," +
                "(3,    'ACTIVE',   'Mock Task 4',      'Task 4 - No Reminder',          'PERSONAL', '',                -1, -1)," +
                "(4,    'ACTIVE',   'Mock Task 5',      'Task 5 - One-time Reminder',    'PERSONAL', 'ONE_TIME',         0, -1)," +
                "(5,    'ACTIVE',   'Mock Task 6',      'Task 6 - One-time Reminder',    'BUSINESS', 'ONE_TIME',         1, -1)," +
                "(6,    'ACTIVE',   'Mock Task 7',      'Task 7 - Repeating Reminder',   'PERSONAL', 'REPEATING',        0, -1)," +
                "(7,    'ACTIVE',   'Mock Task 8',      'Task 8 - Location Reminder',    'BUSINESS', 'LOCATION_BASED',   0, -1)," +
                "(8,    'ACTIVE',   'Mock Task 9',      'Task 9 - Location Reminder',    'PERSONAL', 'LOCATION_BASED',   1, -1)," +
                "(9,    'ACTIVE',   'Mock Task 10',     'Task 10 - Repeating Reminder',  'BUSINESS', 'REPEATING',        1, -1)," +
                "(10,   'ACTIVE',   'Mock Task 11',     'Task 11 - Repeating Reminder',  'BUSINESS', 'REPEATING',        2, -1)," +
                "(11,   'DONE',     'Mock Task 12',     'Task 12 - One-time DONE',       'PERSONAL', 'ONE_TIME',         2, "+dateToday+")," +
                "(12,   'DONE',     'Mock Task 13',     'Task 13 - One-time DONE',       'BUSINESS', 'ONE_TIME',         3, "+dateYesterday+")," +
                "(13,   'DONE',     'Mock Task 14',     'Task 14 - One-time DONE',       'PERSONAL', 'ONE_TIME',         4, "+dateLastWeek+");";
        sqLiteDatabase.execSQL(statement);


        //Insert mock Attachments
        statement = "INSERT INTO " + RemindyContract.AttachmentTable.TABLE_NAME + " (" +
                RemindyContract.AttachmentTable._ID + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TASK_FK.getName() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TYPE.getName() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_TEXT.getName() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_BLOB.getName() +
                ") VALUES " +
                "(0, 0, 'LINK', 'http://www.mocklinkTask1.com', '')," +
                "(0, 0, 'TEXT', 'Mock text', '')," +
                "(0, 1, 'LINK', 'http://www.mocklinkTask2.com', '')," +
                "(0, 2, 'LIST', 'Mock item #1|Mock item #2|Mock item #3', '')," +
                "(0, 2, 'LINK', 'http://www.mocklinkTask3.com', '')," +
                "(0, 4, 'TEXT', 'Mock text task 5', '')," +
                "(0, 5, 'TEXT', 'Mock text task 6', '')," +
                "(0, 6, 'TEXT', 'Mock text task 7', '')," +
                "(0, 7, 'LINK', 'http://www.mocklinkTask8.com', '')," +
                "(0, 7, 'TEXT', 'Mock text task 8', '')," +
                "(0, 8, 'LINK', 'http://www.mocklinkTask9.com', '')," +
                "(0, 9, 'LINK', 'http://www.mocklinkTask10.com', '');";
        sqLiteDatabase.execSQL(statement);


        //Insert mock One-time reminders
        statement = "INSERT INTO " + RemindyContract.OneTimeReminderTable.TABLE_NAME + " (" +
                RemindyContract.OneTimeReminderTable._ID + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getName() +
                ") VALUES " +
                "(0, "+dateToday+", "+time1930+")," +
                "(1, "+dateTomorrow+", "+time0600+")," +
                "(2, "+dateLastWeek+", "+time1259+")," +
                "(3, "+dateYesterday+", "+time0600+")," +
                "(4, "+dateYesterday+", "+time1800+");";
        sqLiteDatabase.execSQL(statement);



        //Insert mock Repeating reminders
        statement = "INSERT INTO " + RemindyContract.RepeatingReminderTable.TABLE_NAME + " (" +
                RemindyContract.RepeatingReminderTable._ID + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() +
                ") VALUES " +
                "(0, "+dateToday+", "+time0600+", 'DAILY', 2, 'FOREVER', -1, -1)," +
                "(1, "+dateLastWeek+", "+time1800+", 'MONTHLY', 2, 'UNTIL_DATE', -1, "+dateNextWeek+")," +
                "(2, "+dateYesterday+", "+time1930+", 'WEEKLY', 1, 'FOR_X_EVENTS', 2, -1);";
        sqLiteDatabase.execSQL(statement);




        //Insert mock Places
        statement = "INSERT INTO " + RemindyContract.PlaceTable.TABLE_NAME + " (" +
                RemindyContract.PlaceTable._ID + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName() +
                ") VALUES " +
                "('0', 'Home', 'Av. El Milagro, Edif. Los Canales', 10.6682603, -71.5940929, 500, 'false')," +
                "('1', 'Vicky', 'Urb La Paragua, Edif. Caicara V', 10.693981, -71.623274, 250, 'false')," +
                "('2', 'PizzaHut', 'PizzaHut address...', 10.693981, -71.633300, 2000, 'false')," +
                "('3', 'Andromeda Galaxy', 'Galaxy far away', 11.0000000, -72.000000, 2000, 'true');";
        sqLiteDatabase.execSQL(statement);


        //Insert mock Location-based reminders
        statement = "INSERT INTO " + RemindyContract.LocationBasedReminderTable.TABLE_NAME + " (" +
                RemindyContract.LocationBasedReminderTable._ID + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getName() + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_IS_ENTERING.getName() +
                ") VALUES " +
                "(0, 0, TRUE)," +
                "(0, 2, FALSE);";
        sqLiteDatabase.execSQL(statement);

    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

        statement = "CREATE TABLE " + RemindyContract.OneTimeReminderTable.TABLE_NAME + " (" +
                RemindyContract.OneTimeReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.RepeatingReminderTable.TABLE_NAME + " (" +
                RemindyContract.RepeatingReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.PlaceTable.TABLE_NAME + " (" +
                RemindyContract.PlaceTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.LocationBasedReminderTable.TABLE_NAME + " (" +
                RemindyContract.LocationBasedReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getDataType() +
                " REFERENCES " + RemindyContract.PlaceTable.TABLE_NAME + "(" + RemindyContract.PlaceTable._ID + ") " + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_IS_ENTERING.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_IS_ENTERING.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.TaskTable.TABLE_NAME + " (" +
                RemindyContract.TaskTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_STATUS.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_STATUS.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_TITLE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_TITLE.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_ID.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_REMINDER_ID.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);



        statement = "CREATE TABLE " + RemindyContract.AttachmentTable.TABLE_NAME + " (" +
                RemindyContract.AttachmentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TYPE.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_TEXT.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_TEXT.getDataType() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_BLOB.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_BLOB.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);
    }

    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + RemindyContract.AttachmentTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.TaskTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.LocationBasedReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.PlaceTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.RepeatingReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.OneTimeReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

    }
}
