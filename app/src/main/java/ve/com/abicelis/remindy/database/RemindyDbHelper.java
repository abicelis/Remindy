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
import ve.com.abicelis.remindy.util.FileUtil;


/**
 * Created by Alex on 9/3/2017.
 */
public class RemindyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Remindy.db";
    private static final int DATABASE_VERSION = 2;                               // If you change the database schema, you must increment the database version.
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
        deleteDatabase(sqLiteDatabase);
        onCreate(sqLiteDatabase);
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
            FileUtil.copyFile(new FileInputStream(appDatabase), new FileOutputStream(backupDatabase));
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
            FileUtil.copyFile(new FileInputStream(backupDatabase), new FileOutputStream(appDatabase));
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

        cal.add(Calendar.DAY_OF_MONTH, +6);
        long dateYesterday = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, +1);
        long dateToday = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, +1);
        long dateTomorrow = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, +1);
        long dateIn2Days = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, +6);
        long dateNextWeek = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, -8);
        cal.add(Calendar.MONTH, +1);
        long dateNextMonth = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, +2);
        long dateNext3Months = cal.getTimeInMillis();

        cal.add(Calendar.YEAR, +1);
        long dateNextYear = cal.getTimeInMillis();

        cal.add(Calendar.YEAR, +1);
        long dateFuture = cal.getTimeInMillis();

        //Insert mock Tasks Table
        statement = "INSERT INTO " + RemindyContract.TaskTable.TABLE_NAME + " (" +
                RemindyContract.TaskTable._ID + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_STATUS.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_TITLE.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getName() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getName() +
                ") VALUES " +
                "(0,    'UNPROGRAMMED',     'Mock Task 0',      'Task 0 - No Reminder',             'PERSONAL',    'NONE',             -1)," +
                "(1,    'UNPROGRAMMED',     'Mock Task 1',      'Task 1 - No Reminder',             'BUSINESS',    'NONE',             -1)," +
                "(2,    'UNPROGRAMMED',     'Mock Task 2',      'Task 2 - No Reminder',             'PERSONAL',    'NONE',             -1)," +
                "(3,    'UNPROGRAMMED',     'Mock Task 3',      'Task 3 - No Reminder',             'PERSONAL',    'NONE',             -1)," +


                "(4,    'DONE',             'Mock Task 4',      'Task 4 - One-time DONE',           'PERSONAL',     'ONE_TIME',         "+dateToday+")," +
                "(5,    'DONE',             'Mock Task 5',      'Task 5 - One-time DONE',           'BUSINESS',     'ONE_TIME',         "+dateYesterday+")," +
                "(6,    'DONE',             'Mock Task 6',      'Task 6 - One-time DONE',           'PERSONAL',     'ONE_TIME',         "+dateLastWeek+")," +
                "(7,    'DONE',             'Mock Task 7',      'Task 7 - One-time DONE',           'BUSINESS',     'ONE_TIME',         "+dateToday+")," +
                "(8,    'DONE',             'Mock Task 8',      'Task 8 - One-time DONE',           'PERSONAL',     'ONE_TIME',         "+dateYesterday+")," +
                "(9,    'DONE',             'Mock Task 9',      'Task 9 - One-time DONE',           'BUSINESS',     'ONE_TIME',         "+dateLastWeek+")," +

                "(10,   'PROGRAMMED',       'Mock Task 10',     'Task 10 - One-time Reminder',      'REPAIRS',     'ONE_TIME',         -1)," +
                "(11,   'PROGRAMMED',       'Mock Task 11',     'Task 11 - One-time Reminder',      'BUSINESS',     'ONE_TIME',         -1)," +
                "(12,   'PROGRAMMED',       'Mock Task 12',     'Task 12 - One-time Reminder',      'HEALTH',       'ONE_TIME',         -1)," +
                "(13,   'PROGRAMMED',       'Mock Task 13',     'Task 13 - One-time Reminder',      'SHOPPING',     'ONE_TIME',         -1)," +
                "(14,   'PROGRAMMED',       'Mock Task 14',     'Task 14 - One-time Reminder',      'PERSONAL',     'ONE_TIME',         -1)," +
                "(15,   'PROGRAMMED',       'Mock Task 15',     'Task 15 - One-time Reminder',      'HEALTH',       'ONE_TIME',         -1)," +
                "(16,   'PROGRAMMED',       'Mock Task 16',     'Task 16 - Repeating Reminder',     'BUSINESS',     'REPEATING',        -1)," +
                "(17,   'PROGRAMMED',       'Mock Task 17',     'Task 17 - Repeating Reminder',     'BUSINESS',     'REPEATING',        -1)," +
                "(18,   'PROGRAMMED',       'Mock Task 18',     'Task 18 - Repeating Reminder',     'SHOPPING',     'REPEATING',        -1)," +
                "(19,   'PROGRAMMED',       'Mock Task 19',     'Task 19 - Location Reminder',      'REPAIRS',     'LOCATION_BASED',   -1)," +
                "(20,   'PROGRAMMED',       'Mock Task 20',     'Task 20 - Location Reminder',      'HEALTH',       'LOCATION_BASED',   -1)," +
                "(21,   'PROGRAMMED',       'Mock Task 21',     'Task 21 - Location Reminder',      'SHOPPING',     'LOCATION_BASED',   -1)," +
                "(22,   'PROGRAMMED',       'Mock Task 22',     'Task 22 - Location Reminder',      'PERSONAL',     'LOCATION_BASED',   -1);";
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
                "(1, 0, 'TEXT', 'Mock text', '')," +
                "(2, 1, 'LINK', 'http://www.mocklinkTask2.com', '')," +
                "(3, 2, 'LINK', 'http://www.mocklinkTask3.com', '')," +
                "(4, 2, 'LINK', 'http://www.mocklinkTask3.com', '')," +
                "(5, 4, 'TEXT', 'Mock text task 5', '')," +
                "(6, 5, 'TEXT', 'Mock text task 6', '')," +
                "(7, 6, 'TEXT', 'Mock text task 7', '')," +
                "(8, 7, 'LINK', 'http://www.mocklinkTask8.com', '')," +
                "(9, 7, 'TEXT', 'Mock text task 8', '')," +
                "(10, 8, 'LINK', 'http://www.mocklinkTask9.com', '')," +
                "(11, 9, 'LINK', 'http://www.mocklinkTask10.com', '');";
        sqLiteDatabase.execSQL(statement);


        //Insert mock One-time reminders
        statement = "INSERT INTO " + RemindyContract.OneTimeReminderTable.TABLE_NAME + " (" +
                RemindyContract.OneTimeReminderTable._ID + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TASK_FK.getName() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getName() +
                ") VALUES " +
                "(0,    4,      "+dateToday+",           "+time0600+")," +
                "(1,    5,      "+dateTomorrow+",        "+time1259+")," +
                "(2,    6,      "+dateNextWeek+",        "+time1930+")," +
                "(3,    7,      "+dateNextMonth+",       "+time0600+")," +
                "(4,    8,      "+dateNext3Months+",     "+time1800+")," +
                "(5,    9,      "+dateNextYear+",        "+time0600+")," +

                "(6,    10,     "+dateToday+",          "+time1259+")," +
                "(7,    11,     "+dateTomorrow+",       "+time1930+")," +
                "(8,    12,     "+dateIn2Days+",       "+time0600+")," +
                "(9,    13,     "+dateNextMonth+",      "+time1930+")," +
                "(10,   14,     "+dateNext3Months+",    "+time0600+")," +
                "(11,   15,     "+dateFuture+",       "+time1800+");";
        sqLiteDatabase.execSQL(statement);



        //Insert mock Repeating reminders
        statement = "INSERT INTO " + RemindyContract.RepeatingReminderTable.TABLE_NAME + " (" +
                RemindyContract.RepeatingReminderTable._ID + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TASK_FK.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() +
                ") VALUES " +
                "(0,    16,     "+dateYesterday+",  "+time0600+", 'MONTHLY',    2,      'FOREVER',          -1,     -1)," +
                "(1,    17,     "+dateNextWeek+",   "+time1800+", 'DAILY',      2,      'UNTIL_DATE',       -1,     "+dateNextWeek+")," +
                "(2,    18,     "+dateNextYear+",   "+time1930+", 'WEEKLY',     1,      'FOR_X_EVENTS',     2,      -1);";
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
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TASK_FK.getName() + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getName() + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_IS_ENTERING.getName() +
                ") VALUES " +
                "(0, 19, 0, 'TRUE')," +
                "(1, 20, 1, 'TRUE')," +
                "(2, 21, 1, 'TRUE')," +
                "(3, 22, 3, 'FALSE');";
        sqLiteDatabase.execSQL(statement);

    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

        statement = "CREATE TABLE " + RemindyContract.OneTimeReminderTable.TABLE_NAME + " (" +
                RemindyContract.OneTimeReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.RepeatingReminderTable.TABLE_NAME + " (" +
                RemindyContract.RepeatingReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
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
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
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
