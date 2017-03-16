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
                        ") VALUES " +
                        "('0', 'Home', 'Av. El Milagro, Edif. Los Canales', 10.6682603, -71.5940929, 500, 'false')," +
                        "('1', 'Vicky', 'Urb La Paragua, Edif. Caicara V', 10.693981, -71.623274, 250, 'false')," +
                        "('2', 'PizzaHut', 'PizzaHut address...', 10.693981, -71.633300, 2000, 'false')," +
                        "('3', 'Andromeda Galaxy', 'Galaxy far away', 11.0000000, -72.000000, 2000, 'true');";
        sqLiteDatabase.execSQL(statement);





        //Insert mock AdvancedReminderTable
        Calendar cal = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        //Pizza reminder
        int weekendPizzaStartTime = new Time(18, 0).getTimeInMinutes();
        int weekendPizzaEndTime = new Time(19, 30).getTimeInMinutes();

        //English test reminder
        cal.add(Calendar.DAY_OF_MONTH, -10);
        long startDateEnglishTest = cal.getTimeInMillis();     //startDateEnglishTest = 10 days ago
        cal.add(Calendar.DAY_OF_MONTH, +10);
        int englishTestStartTime = new Time(7, 0).getTimeInMinutes();
        int englishTestEndTime = new Time(7, 30).getTimeInMinutes();

        //Pay landlord reminder (OVERDUE!)
        cal.add(Calendar.DAY_OF_MONTH, -2);
        long startDateLandlord = cal.getTimeInMillis();     //startDateLandlord = 2 days ago
        cal.add(Calendar.DAY_OF_MONTH, +1);
        long endDateLandlord = cal.getTimeInMillis();       //endDateLandlord = 1 days ago
        cal.add(Calendar.DAY_OF_MONTH, +1);


        //Drink water reminder
        int drinkWaterMorningStartTime = new Time(7, 0).getTimeInMinutes();
        int drinkWaterMorningEndTime = new Time(9, 0).getTimeInMinutes();
        int drinkWaterAfternoonStartTime = new Time(16, 0).getTimeInMinutes();
        int drinkWaterAfternoonEndTime = new Time(18, 0).getTimeInMinutes();

        statement = "INSERT INTO " + RemindyContract.AdvancedReminderTable.TABLE_NAME + " (" +
                RemindyContract.AdvancedReminderTable._ID + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_TITLE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_CATEGORY.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_PLACE_FK.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_DATE_TYPE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_DATE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_TIME_TYPE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_TIME.getName() +
                ") VALUES " +
                "('0', 'ACTIVE', 'Weekend Pizza', 'End of week pizza for vicky and myself', 'PERSONAL', 0, 'ANYDAY', '', '','INTERVAL', '"+weekendPizzaStartTime+"', '"+weekendPizzaEndTime+"')," +
                "('1', 'DONE', 'Take english test', 'Take IELTS General Training test', 'BUSINESS', -1, 'SINGLE_DAY', '" + startDateEnglishTest + "', '', 'INTERVAL', '"+englishTestStartTime+"', '"+englishTestEndTime+"')," +
                "('2', 'ARCHIVED', 'Get some apples', 'Whenever at Vickys, be sure to grab some', 'PERSONAL', 1, 'ANYDAY', '', '','ANYTIME', '', '')," +
                "('3', 'ACTIVE', 'Fetch millennium falcon', 'Yeah, its in the andromeda galaxy...', 'BUSINESS', 3, 'ANYDAY', '', '', 'ANYTIME', '', '')," +
                "('4', 'ACTIVE', 'Morning water', 'Drink 1 whole glass of water', 'PERSONAL', -1, 'ANYDAY', '', '', 'INTERVAL', '"+drinkWaterMorningStartTime+"', '"+drinkWaterMorningEndTime+"')," +
                "('5', 'ACTIVE', 'Afternoon water', 'Drink 1 whole glass of water', 'PERSONAL', -1, 'ANYDAY', '', '', 'INTERVAL', '"+drinkWaterAfternoonStartTime+"', '"+drinkWaterAfternoonEndTime+"')," +
                "('6', 'ACTIVE', 'Pay landlord', '', 'PERSONAL', 0, 'INTERVAL', '" + startDateLandlord + "', '" + endDateLandlord + "','ANYTIME', '', '');";
        sqLiteDatabase.execSQL(statement);


        //Insert mock AdvancedReminderExtraTable
        statement = "INSERT INTO " + RemindyContract.AdvancedReminderExtraTable.TABLE_NAME + " (" +
                RemindyContract.AdvancedReminderExtraTable._ID + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_TYPE.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() +
                ") VALUES " +
                "('0', '0', 'LINK', 'http://www.pizzahut.com', '')," +
                "('1', '1', 'TEXT', 'There are 4 tests. Listening, Reading, Writing and finally, Speaking. The Speaking test will probably be taken on a different day as the rest of the tests', '')," +
                "('2', '6', 'TEXT', 'This months bill is xxx USD', '')," +
                "('3', '6', 'LINK', 'http://www.landlordpay.com', '');";
        sqLiteDatabase.execSQL(statement);




        //Insert mock SimpleReminderTable
        Calendar ca = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);


        long dateSimpleToday = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long dateSimpleTomorrow = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        long dateSimpleFourDaysFromToday = cal.getTimeInMillis();

        int timeSimple7Am = new Time(7, 0).getTimeInMinutes();
        int timeSimple9Am = new Time(9, 0).getTimeInMinutes();


        statement = "INSERT INTO " + RemindyContract.SimpleReminderTable.TABLE_NAME + " (" +
                RemindyContract.SimpleReminderTable._ID + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_STATUS.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_TITLE.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_CATEGORY.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_TIME.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() +
                ") VALUES " +
                "('0', 'ACTIVE', 'Simple reminder example', 'Today at 7am. No RPT', 'PERSONAL', '" + dateSimpleToday + "', '"+timeSimple7Am+"', 'DISABLED', 0, '', 0, '')," +
                "('1', 'ACTIVE', 'Simple reminder example', 'Today at 7am. RPT every 2 days, forever', 'PERSONAL', '" + dateSimpleToday + "', '"+timeSimple7Am+"', 'DAILY', 2, 'FOREVER', 0, '')," +
                "('3', 'ACTIVE', 'Simple reminder example', 'Today at 9am. RPT every 2 days, till 4 days from today', 'PERSONAL', '" + dateSimpleToday + "', '"+timeSimple9Am+"', 'DAILY', 2, 'UNTIL_DATE', 0, '" + dateSimpleFourDaysFromToday + "')," +
                "('2', 'ACTIVE', 'Simple reminder example', 'Tomorrow at 9am. No RPT', 'PERSONAL', '" + dateSimpleTomorrow + "', '"+timeSimple9Am+"', 'DISABLED', 0, '', 0, '')," +
                "('3', 'ACTIVE', 'Simple reminder example', 'Tomorrow at 7am. RPT every 2 weeks, for 2 weeks', 'PERSONAL', '" + dateSimpleTomorrow + "', '"+timeSimple7Am+"', 'WEEKLY', 2, 'FOR_X_EVENTS', 2, '')," +
                "('4', 'DONE', 'Simple reminder example', 'Simple reminder description', 'PERSONAL', '" + dateSimpleToday + "', '"+timeSimple9Am+"', 'DISABLED', 0, '', 0, '')," +
                "('5', 'ARCHIVED', 'Simple reminder example', 'Simple reminder description', 'PERSONAL', '" + dateSimpleToday + "', '"+timeSimple9Am+"', 'DISABLED', 0, '', 0, '');";
        sqLiteDatabase.execSQL(statement);


        //Insert mock SimpleReminderExtraTable
        statement = "INSERT INTO " + RemindyContract.SimpleReminderExtraTable.TABLE_NAME + " (" +
                RemindyContract.SimpleReminderExtraTable._ID + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_TYPE.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() +
                ") VALUES " +
                "('0', '0', 'LINK', 'http://www.pizzahut.com', '')," +
                "('1', '1', 'TEXT', 'There are 4 tests. Listening, Reading, Writing and finally, Speaking. The Speaking test will probably be taken on a different day as the rest of the tests', '')," +
                "('2', '3', 'TEXT', 'This months bill is xxx USD', '')," +
                "('3', '4', 'LINK', 'http://www.landlordpay.com', '');";
        sqLiteDatabase.execSQL(statement);

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


        statement = "CREATE TABLE " + RemindyContract.AdvancedReminderTable.TABLE_NAME + " (" +
                RemindyContract.AdvancedReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_TITLE.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_TITLE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_DESCRIPTION.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_CATEGORY.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_CATEGORY.getDataType() + COMMA_SEP +

                RemindyContract.AdvancedReminderTable.COLUMN_NAME_PLACE_FK.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_PLACE_FK.getDataType() +
                " REFERENCES " + RemindyContract.PlaceTable.TABLE_NAME + "(" + RemindyContract.PlaceTable._ID + ") " + COMMA_SEP +

                RemindyContract.AdvancedReminderTable.COLUMN_NAME_DATE_TYPE.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_DATE_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_DATE.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_DATE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_TIME_TYPE.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_TIME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_TIME.getName() + " " + RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_TIME.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + RemindyContract.AdvancedReminderExtraTable.TABLE_NAME + " (" +
                RemindyContract.AdvancedReminderExtraTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +

                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + " " + RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getDataType() +
                " REFERENCES " + RemindyContract.AdvancedReminderTable.TABLE_NAME + "(" + RemindyContract.AdvancedReminderTable._ID + ") " + COMMA_SEP +

                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_TYPE.getName() + " " + RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + " " + RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getDataType() + COMMA_SEP +
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() + " " + RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + RemindyContract.SimpleReminderTable.TABLE_NAME + " (" +
                RemindyContract.SimpleReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_STATUS.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_STATUS.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_TITLE.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_TITLE.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_DESCRIPTION.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_CATEGORY.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_CATEGORY.getDataType() + COMMA_SEP +

                RemindyContract.SimpleReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_TIME.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() + " " + RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_DATE.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + RemindyContract.SimpleReminderExtraTable.TABLE_NAME + " (" +
                RemindyContract.SimpleReminderExtraTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +

                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + " " + RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_REMINDER_FK.getDataType() +
                " REFERENCES " + RemindyContract.SimpleReminderTable.TABLE_NAME + "(" + RemindyContract.SimpleReminderTable._ID + ") " + COMMA_SEP +

                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_TYPE.getName() + " " + RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName() + " " + RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getDataType() + COMMA_SEP +
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName() + " " + RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);
    }

    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + RemindyContract.SimpleReminderExtraTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.SimpleReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.AdvancedReminderExtraTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.AdvancedReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.PlaceTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

    }
}
