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

        //TODO: Complete this method

//        String statement;
//
//        Calendar cal = Calendar.getInstance();
//        // Set cal to be at midnight (start of day) today.
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//
//        cal.add(Calendar.DAY_OF_MONTH, -10);
//        int closingDay = cal.get(Calendar.DAY_OF_MONTH);        //closingDay 10 days ago
//        cal.add(Calendar.DAY_OF_MONTH, 20);
//        int dueDay = cal.get(Calendar.DAY_OF_MONTH);            //dueDay in 10 days
//        cal.add(Calendar.DAY_OF_MONTH, -10);
//
//
//        statement = "INSERT INTO " + RemindyContract.CreditCardTable.TABLE_NAME + " (" +
//                        RemindyContract.CreditCardTable._ID + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() + COMMA_SEP +
//                        RemindyContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName() +
//                        ") VALUES (0, 'Shopping Card', 'Bank Of America', '1234-5678-1234-5678', 'USD', 'MASTERCARD', '0', " + closingDay + ", " + dueDay + ", 'DARK_RED'); ";
//        sqLiteDatabase.execSQL(statement);
//
//        cal.add(Calendar.DAY_OF_MONTH, -9);
//        long period1StartDate = cal.getTimeInMillis();
//        cal.add(Calendar.MONTH, 1);
//        long period2StartDate = cal.getTimeInMillis();
//        cal.add(Calendar.MILLISECOND, -1);
//        long period1EndDate = cal.getTimeInMillis();
//        cal.add(Calendar.MONTH, 1);
//        long period2EndDate = cal.getTimeInMillis();
//
//        statement = "INSERT INTO " + RemindyContract.CreditPeriodTable.TABLE_NAME + " (" +
//                        RemindyContract.CreditPeriodTable._ID + COMMA_SEP +
//                        RemindyContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + COMMA_SEP +
//                        RemindyContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + COMMA_SEP +
//                        RemindyContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + COMMA_SEP +
//                        RemindyContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + COMMA_SEP +
//                        RemindyContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() +
//                        ") VALUES (0, 0, 0, '" + period1StartDate + "', '" + period1EndDate + "', '60000'), (1, 0, 0, '" + period2StartDate + "', '" + period2EndDate + "', '80000'); ";
//        sqLiteDatabase.execSQL(statement);
//
//        cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, -6);
//        long expense1period1 = cal.getTimeInMillis();
//        cal.add(Calendar.DAY_OF_MONTH, 1);
//        long expense2period1 = cal.getTimeInMillis();
//        cal.add(Calendar.DAY_OF_MONTH, 2);
//        long expense3period1 = cal.getTimeInMillis();
//        long expense4period1 = cal.getTimeInMillis();
//        cal.add(Calendar.DAY_OF_MONTH, 2);
//        long expense5period1 = cal.getTimeInMillis();
//
//
//        statement  = "INSERT INTO " + RemindyContract.ExpenseTable.TABLE_NAME + " (" +
//                        RemindyContract.ExpenseTable._ID + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName() + COMMA_SEP +
//                        RemindyContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName() +
//                        ") VALUES (0, 0, 'MockExpense 1', X'', 'mockpath', '5300', 'USD', '" + expense1period1 + "', 'FOOD', 'ORDINARY'), " +
//                                 "(1, 0, 'MockExpense 2', X'', 'mockpath', '10000', 'USD', '" + expense2period1 + "', 'ENTERTAINMENT', 'EXTRAORDINARY')," +
//                                 "(2, 0, 'MockExpense 3', X'', 'mockpath', '4500', 'USD', '" + expense3period1 + "', 'LEISURE', 'EXTRAORDINARY')," +
//                                 "(3, 0, 'MockExpense 4', X'', 'mockpath', '2000', 'USD', '" + expense4period1 + "', 'EDUCATION', 'ORDINARY')," +
//                                 "(4, 0, 'MockExpense 5', X'', 'mockpath', '12000', 'USD', '" + expense5period1 + "', 'CLOTHING', 'ORDINARY');";
//        sqLiteDatabase.execSQL(statement);
//
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
