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


        //Insert mock Places
        statement = "INSERT INTO " + RemindyContract.PlaceTable.TABLE_NAME + " (" +
                        RemindyContract.PlaceTable._ID + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName() + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName() + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName() + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName() + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName() + COMMA_SEP +
                        RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName() +
                        ") VALUES ('0', 'Home', 'Av. El Milagro, Edif. Los Canales', 10.6682603, -71.5940929, 500, 'false')," +
                        "('1', 'Vicky', 'Urb La Paragua, Edif. Caicara V', 10.693981, -71.623274, 250, 'false')," +
                        "('2', 'PizzaHut', 'PizzaHut address...', 10.693981, -71.633300, 2000, 'false')," +
                        "('3', 'Andromeda Galaxy', 'Galaxy far away', 11.0000000, -72.000000, 2000, 'true');";
        sqLiteDatabase.execSQL(statement);





        //Insert mock Reminders
        Calendar cal = getZeroedCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        long startDateWeekendPizza = cal.getTimeInMillis();     //startDateWeekendPizza = 10 days ago

        cal.add(Calendar.DAY_OF_MONTH, +10);
        cal.add(Calendar.MONTH, 3);
        long endDateWeekendPizza = cal.getTimeInMillis();      //endDateWeekendPizza = 3 months from now


        int weekendPizzaStartTime = new Time(18, 0).getTimeInMinutes();
        int weekendPizzaEndTime = new Time(19, 30).getTimeInMinutes();


        statement = "INSERT INTO " + RemindyContract.ReminderTable.TABLE_NAME + " (" +
                RemindyContract.ReminderTable._ID + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_STATUS.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_TITLE.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_PLACE_FK.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_DATE_TYPE.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_START_DATE.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_TIME_TYPE.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getName() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_END_TIME.getName() +
                ") VALUES ('0', 'ACTIVE', 'Weekend Pizza', 'End of week pizza for vicky and myself', 'PERSONAL', 0, 'ANYDAY', '', '','INTERVAL', '"+weekendPizzaStartTime+"', '"+weekendPizzaEndTime+"')," +
                "('1', 'ACTIVE', 'Weekend Pizza', 'End of week pizza for vicky and myself', 'PERSONAL', 0, 'ANYDAY', '', '','INTERVAL', '"+weekendPizzaStartTime+"', '"+weekendPizzaEndTime+"')," +
                "('2', 'ACTIVE', 'Weekend Pizza', 'End of week pizza for vicky and myself', 'PERSONAL', 0, 'ANYDAY', '', '','INTERVAL', '"+weekendPizzaStartTime+"', '"+weekendPizzaEndTime+"');";
        sqLiteDatabase.execSQL(statement);






        //Insert mock ReminderExtras
        statement = "INSERT INTO " + RemindyContract.ExtraTable.TABLE_NAME + " (" +
                RemindyContract.ExtraTable._ID + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK.getName() + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_TYPE.getName() + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() +
                ") VALUES ('0', '0', 'http://www.pizzahut.com', '')," +
                "('1', '0', 'http://www.pizzahut.com.ve', '')," +
                "('2', '0', 'http://www.pizzahut.ca', '');";
        sqLiteDatabase.execSQL(statement);

    }

    private Calendar getZeroedCalendar() {
        Calendar cal = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

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


        statement = "CREATE TABLE " + RemindyContract.ReminderTable.TABLE_NAME + " (" +
                RemindyContract.ReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_STATUS.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_STATUS.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_TITLE.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_TITLE.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_DESCRIPTION.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY.getDataType() + COMMA_SEP +

                RemindyContract.ReminderTable.COLUMN_NAME_PLACE_FK.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_PLACE_FK.getDataType() +
                " REFERENCES " + RemindyContract.PlaceTable.TABLE_NAME + "(" + RemindyContract.PlaceTable._ID + ") " + COMMA_SEP +

                RemindyContract.ReminderTable.COLUMN_NAME_DATE_TYPE.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_DATE_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_START_DATE.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_START_DATE.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_TIME_TYPE.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_TIME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getDataType() + COMMA_SEP +
                RemindyContract.ReminderTable.COLUMN_NAME_END_TIME.getName() + " " + RemindyContract.ReminderTable.COLUMN_NAME_END_TIME.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + RemindyContract.ExtraTable.TABLE_NAME + " (" +
                RemindyContract.ExtraTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +

                RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK.getName() + " " + RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK.getDataType() +
                " REFERENCES " + RemindyContract.ReminderTable.TABLE_NAME + "(" + RemindyContract.ReminderTable._ID + ") " + COMMA_SEP +

                RemindyContract.ExtraTable.COLUMN_NAME_TYPE.getName() + " " + RemindyContract.ExtraTable.COLUMN_NAME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + " " + RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getDataType() + COMMA_SEP +
                RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() + " " + RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);
    }

    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + RemindyContract.ExtraTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.ReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.PlaceTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

    }
}
