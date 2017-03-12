package ve.com.abicelis.remindy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderDateType;
import ve.com.abicelis.remindy.enums.ReminderExtraType;
import ve.com.abicelis.remindy.enums.ReminderSortType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderTimeType;
import ve.com.abicelis.remindy.exception.CouldNotDeleteDataException;
import ve.com.abicelis.remindy.exception.MalformedLinkException;
import ve.com.abicelis.remindy.exception.PlaceNotFoundException;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.Reminder;
import ve.com.abicelis.remindy.model.ReminderByPlaceComparator;
import ve.com.abicelis.remindy.model.ReminderExtra;
import ve.com.abicelis.remindy.model.ReminderExtraAudio;
import ve.com.abicelis.remindy.model.ReminderExtraImage;
import ve.com.abicelis.remindy.model.ReminderExtraLink;
import ve.com.abicelis.remindy.model.ReminderExtraText;
import ve.com.abicelis.remindy.model.Time;

/**
 * Created by Alex on 9/3/2017.
 */
public class RemindyDAO {

    private RemindyDbHelper mDatabaseHelper;

    public RemindyDAO(Context context) {
        mDatabaseHelper = new RemindyDbHelper(context);
    }

    /**
     * Returns a List of OVERDUE Reminders.
     * These are reminders which have an Active status, but will never trigger because of their set dates
     * Examples:
     * 1. Reminder is set to end in the past:
     *        endDate < today
     * 2. Reminder is set to end sometime in the future, but on a day of the week that have passed:
     *        Today=Tuesday, endDate=Friday but Reminder is set to trigger only Mondays.
     */
    public List<Reminder> getOverdueReminders() {
        return getRemindersByStatus(ReminderStatus.OVERDUE, ReminderSortType.DATE);
    }

    /**
     * Returns a List of ACTIVE Reminders.
     * These are reminders which have an Active status, and will trigger sometime in the present or in the future
     * Basically, all Reminders which are *NOT* OVERDUE (See function above)
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getActiveReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.ACTIVE, sortType);
    }

    /**
     * Returns a List of Reminders with a status of DONE.
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getDoneReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.DONE, sortType);
    }

    /**
     * Returns a List of Reminders with a status of ARCHIVED.
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getArchivedReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.ARCHIVED, sortType);
    }



    /**
     * Returns a List of Reminders given a specific ReminderStatus and ReminderSortType
     * @param reminderStatus ReminderStatus enum value with which to filter Reminders.
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    private List<Reminder> getRemindersByStatus(@NonNull ReminderStatus reminderStatus, @NonNull ReminderSortType sortType) {
        List<Reminder> reminders = new ArrayList<>();

        String orderByClause = null;
        switch (sortType) {
            case DATE:
                orderByClause = RemindyContract.ReminderTable.COLUMN_NAME_END_DATE + " DESC";
                break;
            case PLACE:
                //Cant sort here, need the Place's name, dont have it.
                break;
            case CATEGORY:
                orderByClause = RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY + " DESC";
        }

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.ReminderTable.TABLE_NAME, null, RemindyContract.ReminderTable.COLUMN_NAME_STATUS+"=?",
                new String[] {reminderStatus.name()}, null, null, orderByClause);

        try {
            while(cursor.moveToNext()) {
                Reminder current = getReminderFromCursor(cursor);

                //Try to get the Place, if there is one
                try {
                    int placeId = cursor.getInt(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_PLACE_FK.getName()));
                    current.setPlace(getPlace(placeId));
                } catch (Exception e) {/*Thrown if COLUMN_NAME_PLACE_FK is null, so do nothing.*/}

                //Try to get the Extras, if there are any
                current.setExtras(getReminderExtras(current.getId()));

                reminders.add(current);
            }
        } finally {
            cursor.close();
        }

        //Do sort by place if needed
        if(sortType == ReminderSortType.PLACE) {
            Collections.sort(reminders, new ReminderByPlaceComparator());
        }

        return reminders;
    }

    /**
     * Returns a List of Places.
     */
    public List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.PlaceTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                places.add(getPlaceFromCursor(cursor));
            }
        }finally {
            cursor.close();
        }

        return places;
    }

    /**
     * Returns a Place given a placeId.
     * @param placeId The id of the place
     */
    public Place getPlace(int placeId) throws PlaceNotFoundException, SQLiteConstraintException{
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.PlaceTable.TABLE_NAME, null, RemindyContract.PlaceTable._ID+"=?",
                new String[] {String.valueOf(placeId)}, null, null, null);

        if(cursor.getCount() == 0)
            throw new PlaceNotFoundException("Specified Place not found in the database. Passed id=" + placeId);
        if(cursor.getCount() > 1)
            throw new SQLiteConstraintException("Database UNIQUE constraint failure, more than one record found. Passed value=" + placeId);

        cursor.moveToNext();
        return getPlaceFromCursor(cursor);
    }


    /**
     * Returns a List of Extras associated to a Reminder.
     * @param reminderId The id of the reminder, fk in ExtraTable
     */
    public List<ReminderExtra> getReminderExtras(int reminderId) {
        List<ReminderExtra> extras = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.ExtraTable.TABLE_NAME, null, RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK+"=?",
                new String[] {String.valueOf(reminderId)}, null, null, null);

        try {
            while (cursor.moveToNext()) {
                extras.add(getReminderExtraFromCursor(cursor));
            }
        }finally {
            cursor.close();
        }

        return extras;
    }




    /* Delete data from database */
    /**
     * Deletes a single Place, given its ID
     * @param placeId The ID of the place to delete
     */
    public boolean deletePlace(int placeId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.PlaceTable.TABLE_NAME,
                RemindyContract.PlaceTable._ID + " =?",
                new String[]{String.valueOf(placeId)}) > 0;
    }

    /**
     * Deletes a single Extra, given its ID
     * @param extraId The ID of the extra to delete
     */
    public boolean deleteExtra(int extraId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.ExtraTable.TABLE_NAME,
                RemindyContract.ExtraTable._ID + " =?",
                new String[]{String.valueOf(extraId)}) > 0;
    }

    /**
     * Deletes all Extras linked to a Reminder, given the reminder's ID
     * @param reminderId The ID of the reminder whos extras will be deleted
     */
    public boolean deleteExtrasFromReminder(int reminderId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.ExtraTable.TABLE_NAME,
                RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }

    /**
     * Deletes a Reminder with its associated Extras, given the reminder's ID
     * @param reminderId The ID of the reminder o delete
     */
    public boolean deleteReminder(int reminderId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        deleteExtrasFromReminder(reminderId);

        return db.delete(RemindyContract.ReminderTable.TABLE_NAME,
                RemindyContract.ReminderTable._ID + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }



    /* Update data on database */
    //TODO: Update Reminder with extras
    //TODO: Update Place info


    /* Insert data into database */
    //TODO: Insert a Reminder with Extras
    //TODO Insert a Place


    /* Model to ContentValues */







    /* Cursor to Model */

    private Reminder getReminderFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.ReminderTable._ID));
        ReminderStatus status = ReminderStatus.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_STATUS.getName())));
        String title = cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_TITLE.getName()));
        String description = cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_DESCRIPTION.getName()));
        ReminderCategory category = ReminderCategory.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY.getName())));
        ReminderDateType dateType = ReminderDateType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_DATE_TYPE.getName())));

        Calendar startDate = null;
        int startDateIndex = cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getName());
        if(!cursor.getString(startDateIndex).isEmpty()) {
            startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(startDateIndex));
        }

        Calendar endDate = null;
        int endDateIndex = cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getName());
        if(!cursor.getString(endDateIndex).isEmpty()) {
            endDate = Calendar.getInstance();
            endDate.setTimeInMillis(cursor.getLong(endDateIndex));
        }

        ReminderTimeType timeType = ReminderTimeType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_TIME_TYPE.getName())));

        Time startTime = new Time(cursor.getInt(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getName())));
        Time endTime = new Time(cursor.getInt(cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_END_TIME.getName())));


        return new Reminder(id, status, title, description, category, null, dateType, startDate, endDate, timeType, startTime, endTime);
    }


    private Place getPlaceFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.PlaceTable._ID));
        String alias = cursor.getString(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName()));
        String address = cursor.getString(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName()));
        double latitude = cursor.getDouble(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName()));
        double longitude = cursor.getDouble(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName()));
        float radius = cursor.getFloat(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName()));
        boolean isOneOff = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName())));

        return new Place(id, alias, address, latitude, longitude, radius, isOneOff);
    }

    private ReminderExtra getReminderExtraFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.ExtraTable._ID));
        int reminderId = cursor.getInt(cursor.getColumnIndex(RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK.getName()));
        ReminderExtraType extraType = ReminderExtraType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.ExtraTable.COLUMN_NAME_TYPE.getName())));
        String textContent = cursor.getString(cursor.getColumnIndex(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName()));
        byte[] blobContent = cursor.getBlob(cursor.getColumnIndex(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getName()));

        switch (extraType) {
            case AUDIO:
                return new ReminderExtraAudio(id, reminderId, blobContent);
            case IMAGE:
                return new ReminderExtraImage(id, reminderId, blobContent, textContent);
            case TEXT:
                return new ReminderExtraText(id, reminderId, textContent);
            case LINK:
                return new ReminderExtraLink(id, reminderId, textContent);
            default:
                throw new InvalidParameterException("ReminderExtraType is invalid. Value = " + extraType);
        }
    }





















//
//
//    /**
//         * Returns a List of CreditPeriods associated with ccardId.
//         * Note: The periods will not contain Expenses or Payments.
//         * @param cardId The ID of the Credit Card
//         */
//    public List<CreditPeriod> getCreditPeriodListFromCard(int cardId) {
//        List<CreditPeriod> creditPeriods = new ArrayList<>();
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//
//        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null,
//                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " == " + cardId, null, null, null,
//                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " DESC");
//
//        try {
//            while (cursor.moveToNext()) {
//                creditPeriods.add(getCreditPeriodFromCursor(cursor));
//            }
//        } finally {
//            cursor.close();
//        }
//
//        return creditPeriods;
//    }
//
//
//
//    /**
//     * Finds a CreditCard with the supplied cardId, will not return creditPeriods associated with
//     * the card
//     * @param cardId The ID of the Credit Card
//     */
//    public CreditCard getCreditCard(int cardId) throws CreditCardNotFoundException {
//        String[] selectionArgs = new String[]{String.valueOf(cardId)};
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//
//        Cursor cursor =  db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null,
//                ExpenseManagerContract.CreditCardTable._ID + " =? ", selectionArgs, null, null, null);
//
//        if(cursor.getCount() == 0)
//            throw new CreditCardNotFoundException("Specified Credit Card not found in the database. Passed value=" + cardId);
//        if(cursor.getCount() > 1)
//            throw new CreditCardNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed value=" + cardId);
//
//        cursor.moveToNext();
//        return getCreditCardFromCursor(cursor);
//    }
//
//    /**
//     * Returns a Credit Card with the supplied cardId and the specified periodIndex.
//     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
//     * negative value (-1 returns previous credit period, and so on)
//     * @param cardId The ID of the Credit Card
//     * @param periodIndex An integer <= 0
//     */
//    public CreditCard getCreditCardWithCreditPeriod(int cardId, int periodIndex) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException{
//        if(periodIndex > 0)
//            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);
//
//        CreditCard creditCard = getCreditCard(cardId);
//
//        //Get ordered (DESC) list of credit periods
//        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);
//
//        //Find the creditPeriod according to periodIndex
//        CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, periodIndex, creditCard.getClosingDay());
//
//        //Create Map and set it on the Credit Card
//        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();
//        creditPeriodMap.put(periodIndex, cp);
//        creditCard.setCreditPeriods(creditPeriodMap);
//
//        return creditCard;
//    }
//
//
//    /**
//     * Returns a Credit Card with the supplied cardId and as many credit periods as numPeriods
//     * indicates. Counting backwards starting on periodIndex.
//     * Note: PeriodIndex must be either zero (current creditPriod) or a
//     * negative value (-1 refers to the previous credit period, and so on)
//     * Note2: Passing periodIndex=-1, numPeriods=3 will return three periods (Period -1, -2 and -3)
//     * @param cardId The ID of the Credit Card
//     * @param periodIndex An integer <= 0
//     * @param numPeriods An integer >= 1
//     */
//    public CreditCard getCreditCardWithCreditPeriodRange(int cardId, int periodIndex, int numPeriods) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException {
//        if (periodIndex > 0)
//            throw new InvalidParameterException("PeriodIndex must be less or equal than 0. Passed value=" + periodIndex);
//        if (numPeriods <= 0)
//            throw new InvalidParameterException("NumPeriods must be more or equal than 1. Passed value=" + periodIndex);
//
//        CreditCard creditCard = getCreditCard(cardId);
//
//        //Get ordered (DESC) list of credit periods
//        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);
//
//        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();
//
//        //Find the creditPeriod according to periodIndex
//        for (int i = periodIndex; i > (periodIndex-numPeriods); --i) {
//            CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, i, creditCard.getClosingDay());
//            creditPeriodMap.put(i, cp);
//        }
//
//        //Set creditPeriods in creditCard and return
//        creditCard.setCreditPeriods(creditPeriodMap);
//        return creditCard;
//    }
//
//
//    /**
//     * Returns a Credit Period from a List of creditPeriods which complies with the supplied
//     * periodIndex and closingDay.
//     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
//     * negative value (-1 returns previous credit period, and so on)
//     * Note2: closingDay must be between 1 and 28.
//     * @param creditPeriods List of Credit Periods to search into
//     * @param periodIndex An integer <= 0
//     * @param closingDay An integer between 1 and 28
//     */
//    private CreditPeriod getCreditPeriodFromPeriodIndex(List<CreditPeriod> creditPeriods, int periodIndex, int closingDay) throws CreditPeriodNotFoundException, InvalidParameterException {
//        if(closingDay < 1 || closingDay > 28)
//            throw new InvalidParameterException("ClosingDay must be between 1 and 28. Passed value=" + closingDay);
//        if(periodIndex > 0)
//            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);
//        if(creditPeriods == null || creditPeriods.size() == 0)
//            throw new InvalidParameterException("creditPeriods is null or empty");
//
//        //Get a day which will be between the starting and ending dates of the wanted creditPeriod
//        Calendar day = Calendar.getInstance();
//        if(periodIndex != 0)
//            day.add(Calendar.MONTH, periodIndex);
//
//        for (CreditPeriod cp : creditPeriods) {
//            if(cp.getStartDate().compareTo(day) <=0 && cp.getEndDate().compareTo(day) >=0) {
//                cp.setExpenses(getExpensesFromCreditPeriod(cp.getId()));
//                cp.setPayments(getPaymentsFromCreditPeriod(cp.getId()));
//                return cp;
//
//            }
//        }
//
//        throw new CreditPeriodNotFoundException("Credit period (index " + periodIndex + ") not found in passed creditPeriods List");
//    }
//
//    /**
//     * Returns a Credit Period with the supplied periodId, containing its Expenses and Payments
//     * @param periodId The ID of the Credit Period
//     */
//    public CreditPeriod getCreditPeriodFromPeriodId(int periodId) throws CreditPeriodNotFoundException {
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//        CreditPeriod creditPeriod;
//
//        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, ExpenseManagerContract.CreditPeriodTable._ID + " == " + periodId, null, null, null, null);
//
//        if(cursor.getCount() == 0)
//            throw new CreditPeriodNotFoundException("Specified Credit Period not found in the database. Passed value=" + periodId);
//        if(cursor.getCount() > 1)
//            throw new CreditPeriodNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed Value=" + periodId);
//
//        try{
//            cursor.moveToNext();
//            creditPeriod = getCreditPeriodFromCursor(cursor);
//            creditPeriod.setExpenses(getExpensesFromCreditPeriod(periodId));
//            creditPeriod.setPayments(getPaymentsFromCreditPeriod(periodId));
//        } finally {
//            cursor.close();
//        }
//
//        return creditPeriod;
//    }
//
//
//    /**
//     * Returns a List of Expenses, given a creditPeriodId
//     * @param periodId the Id of the Credit period
//     */
//    public List<Expense> getExpensesFromCreditPeriod(int periodId) {
//        List<Expense> expenses = new ArrayList<>();
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//
//        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
//                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
//                null, null, null, ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + " DESC");
//
//
//        try {
//            while (cursor.moveToNext()) {
//                expenses.add(getExpenseFromCursor(cursor));
//            }
//        } finally {
//            cursor.close();
//        }
//
//        return expenses;
//    }
//
//    /**
//     * Returns a List of Payments, given a creditPeriodId
//     * @param periodId the Id of the Credit period
//     */
//    public List<Payment> getPaymentsFromCreditPeriod(int periodId) {
//        List<Payment> payments = new ArrayList<>();
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//
//        Cursor cursor =  db.query(ExpenseManagerContract.PaymentTable.TABLE_NAME, null,
//                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
//                null, null, null, ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " DESC");
//
//        try {
//            while (cursor.moveToNext()) {
//                payments.add(getPaymentFromCursor(cursor));
//            }
//        } finally {
//            cursor.close();
//        }
//
//        return payments;
//    }
//
//
//    /**
//     * Returns an Expenses, given an Expense ID
//     * @param expenseId the Id of the Expense
//     */
//    public Expense getExpense(int expenseId) throws CouldNotGetDataException {
//        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
//        String[] selectionArgs = new String[]{String.valueOf(expenseId)};
//
//
//        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
//                ExpenseManagerContract.ExpenseTable._ID + " =? ",
//                selectionArgs, null, null, null);
//
//
//        if(cursor.getCount() == 0)
//            throw new CouldNotGetDataException("Expense not found for ID = " + expenseId);
//
//
//        cursor.moveToNext();
//        return getExpenseFromCursor(cursor);
//    }
//
//
//
//    /* Delete data from database */
//
//    /**
//     * Deletes a credit card, with its associated credit periods, expenses and payments
//     */
//    public boolean deleteCreditCard(int creditCardId) throws CouldNotDeleteDataException {
//
//        //Fetch all credit periods and delete associated expenses and payments
//        for( CreditPeriod cp : getCreditPeriodListFromCard(creditCardId)) {
//            deleteExpensesFromCreditPeriod(cp.getId());
//            deletePaymentsFromCreditPeriod(cp.getId());
//            deleteCreditPeriod(cp.getId());
//        }
//
//        //Finally, delete the credit card
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(creditCardId)};
//
//        return db.delete(ExpenseManagerContract.CreditCardTable.TABLE_NAME,
//                ExpenseManagerContract.CreditCardTable._ID + " =?",
//                whereArgs) > 0;
//    }
//
//    public boolean deleteCreditPeriod(int creditPeriodId) throws CouldNotDeleteDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(creditPeriodId)};
//
//        return db.delete(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME,
//                ExpenseManagerContract.CreditPeriodTable._ID + " =?",
//                whereArgs) > 0;
//    }
//
//
//    public boolean deleteExpensesFromCreditPeriod(int creditPeriodId) throws CouldNotDeleteDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(creditPeriodId)};
//
//        return db.delete(ExpenseManagerContract.ExpenseTable.TABLE_NAME,
//                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " =?",
//                whereArgs) > 0;
//    }
//
//    public boolean deleteExpense(int expenseId) throws CouldNotDeleteDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(expenseId)};
//
//        return db.delete(ExpenseManagerContract.ExpenseTable.TABLE_NAME,
//                ExpenseManagerContract.ExpenseTable._ID + " =?",
//                whereArgs) > 0;
//    }
//
//
//
//    public boolean deletePaymentsFromCreditPeriod(int creditPeriodId) throws CouldNotDeleteDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(creditPeriodId)};
//
//        return db.delete(ExpenseManagerContract.PaymentTable.TABLE_NAME,
//                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " =?",
//                whereArgs) > 0;
//    }
//
//    public boolean deletePayment(int paymentId) throws CouldNotDeleteDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        String[] whereArgs = new String[]{String.valueOf(paymentId)};
//
//        return db.delete(ExpenseManagerContract.PaymentTable.TABLE_NAME,
//                ExpenseManagerContract.PaymentTable._ID + " =?",
//                whereArgs) > 0;
//    }
//
//
//
//
//    /* Update data on database */
//    public long updateCreditCard(CreditCard creditCard) throws CouldNotUpdateDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        //Set values
//        ContentValues values = getValuesFromCreditCard(creditCard);
//
//        //Which row to update
//        String selection = ExpenseManagerContract.CreditCardTable._ID + " =? ";
//        String[] selectionArgs = { String.valueOf(creditCard.getId()) };
//
//        int count = db.update(
//                ExpenseManagerContract.CreditCardTable.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
//
//        return count;
//    }
//
//    public long updateExpense(Expense expense) throws CouldNotUpdateDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        //Set values
//        ContentValues values = getValuesFromExpense(expense);
//
//        //Which row to update
//        String selection = ExpenseManagerContract.ExpenseTable._ID + " =? ";
//        String[] selectionArgs = { String.valueOf(expense.getId()) };
//
//        int count = db.update(
//                ExpenseManagerContract.ExpenseTable.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
//
//        return count;
//    }
//
//    public long updateCreditPeriod(int creditCardId, CreditPeriod creditPeriod) throws CouldNotUpdateDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        //Set values
//        ContentValues values = getValuesFromCreditPeriod(creditCardId, creditPeriod);
//
//        //Which row to update
//        String selection = ExpenseManagerContract.CreditPeriodTable._ID + " =? ";
//        String[] selectionArgs = { String.valueOf(creditPeriod.getId()) };
//
//        int count = db.update(
//                ExpenseManagerContract.CreditPeriodTable.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
//
//        return count;
//    }
//
//
//
//    /* Insert data into database */
//
//    public long insertCreditCard(CreditCard creditcard, BigDecimal firstCreditPeriodLimit) throws CouldNotInsertDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditcard.getCardAlias());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditcard.getBankName());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditcard.getCardNumber());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditcard.getCurrency().getCode());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditcard.getCardType().getCode());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditcard.getClosingDay());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditcard.getDueDay());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName(), creditcard.getCreditCardBackground().getCode());
//
//        if(creditcard.getCardExpiration() != null)
//            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditcard.getCardExpiration().getTimeInMillis());
//        else
//            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), "");
//
//        long newRowId;
//        newRowId = db.insert(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, values);
//
//        if(newRowId == -1)
//            throw new CouldNotInsertDataException("There was a problem inserting the Credit Card: " + creditcard.toString());
//        else {
//            //Insert first creditPeriod
//            insertCurrentCreditPeriod(newRowId, creditcard.getClosingDay(), firstCreditPeriodLimit);
//        }
//
//        return newRowId;
//
//    }
//
//    /**
//     * Inserts a creditPeriod associated to a creditCard, which engulfs the current date (today), given a closing date
//     * @param creditCardId the Id of the CreditCard to associate the CreditPeriod
//     * @param  closingDay the credit card's closing day
//     * @param creditPeriodLimit BigDecimal value of the currency limit of the period
//     */
//    public long insertCurrentCreditPeriod(long creditCardId, int closingDay, BigDecimal creditPeriodLimit) throws CouldNotInsertDataException {
//        //Insert first creditPeriod
//
//        // Set dates to be at midnight (start of day) today.
//        Calendar startDate = Calendar.getInstance();
//        startDate.set(Calendar.HOUR_OF_DAY, 0);
//        startDate.set(Calendar.MINUTE, 0);
//        startDate.set(Calendar.SECOND, 0);
//        startDate.set(Calendar.MILLISECOND, 0);
//
//        Calendar endDate = Calendar.getInstance();
//        endDate.setTimeInMillis(startDate.getTimeInMillis());
//
//        //Set start date's DAY_OF_MONTH to closingDay and endDate's DAY_OF_MONTH to closingDay-1ms
//        startDate.set(Calendar.DAY_OF_MONTH, closingDay);
//        endDate.set(Calendar.DAY_OF_MONTH, closingDay);
//        endDate.add(Calendar.MILLISECOND, -1);
//
//
//        if(closingDay <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
//            endDate.add(Calendar.MONTH, 1);
//        else
//            startDate.add(Calendar.MONTH, -1);
//
//        CreditPeriod creditPeriod = new CreditPeriod(CreditPeriod.PERIOD_NAME_COMPLETE, startDate, endDate, creditPeriodLimit);
//        return insertCreditPeriod(creditCardId, creditPeriod);
//    }
//
//    public long insertCreditPeriod(long creditCardId, CreditPeriod creditPeriod) throws CouldNotInsertDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName(), creditCardId);
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName(), creditPeriod.getPeriodNameStyle());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName(), creditPeriod.getStartDate().getTimeInMillis());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName(), creditPeriod.getEndDate().getTimeInMillis());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName(), creditPeriod.getCreditLimit().toPlainString());
//
//
//        long newRowId = -1;
//        newRowId = db.insert(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, values);
//
//        if(newRowId == -1)
//            throw new CouldNotInsertDataException("There was a problem inserting the Credit Period: " + creditPeriod.toString());
//
//        return newRowId;
//
//    }
//
//    public long insertExpense(int creditPeriodId, Expense expense) throws CouldNotInsertDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        ContentValues values = getValuesFromExpense(expense);
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), creditPeriodId);
//
//        long newRowId;
//        newRowId = db.insert(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null, values);
//
//        if(newRowId == -1)
//            throw new CouldNotInsertDataException("There was a problem inserting the Expense: " + expense.toString());
//
//        return newRowId;
//    }
//
//
//    public long insertPayment(int creditPeriodId, Payment payment) throws CouldNotInsertDataException {
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), creditPeriodId);
//        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName(), payment.getDescription());
//        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName(), payment.getAmount().toPlainString());
//        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName(), payment.getCurrency().getCode());
//        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName(), payment.getDate().getTimeInMillis());
//
//
//        long newRowId;
//        newRowId = db.insert(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null, values);
//
//        if(newRowId == -1)
//            throw new CouldNotInsertDataException("There was a problem inserting the Payment: " + payment.toString());
//
//        return newRowId;
//    }
//
//
//
//    /* Model to ContentValues */
//    private ContentValues getValuesFromExpense(Expense expense) {
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName(), expense.getDescription());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName(), expense.getThumbnail());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName(), expense.getFullImagePath());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName(), expense.getAmount().toPlainString());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName(), expense.getCurrency().getCode());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName(), expense.getDate().getTimeInMillis());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName(), expense.getExpenseCategory().getCode());
//        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName(), expense.getExpenseType().getCode());
//        return values;
//    }
//
//    private ContentValues getValuesFromCreditCard(CreditCard creditCard) {
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditCard.getCardAlias());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditCard.getBankName());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditCard.getCardNumber());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditCard.getCurrency().getCode());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditCard.getCardType().getCode());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditCard.getCardExpiration().getTimeInMillis());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditCard.getClosingDay());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditCard.getDueDay());
//        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName(), creditCard.getCreditCardBackground().getCode());
//        return values;
//    }
//
//    private ContentValues getValuesFromCreditPeriod(int creditCardId, CreditPeriod creditPeriod) {
//        ContentValues values = new ContentValues();
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName(), creditCardId);
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName(), creditPeriod.getPeriodNameStyle());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName(), creditPeriod.getStartDate().getTimeInMillis());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName(), creditPeriod.getEndDate().getTimeInMillis());
//        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName(), creditPeriod.getCreditLimit().toPlainString());
//        return values;
//    }
//
//
//    /* Cursor to Model */
//
//    private CreditCard getCreditCardFromCursor(Cursor cursor) {
//        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable._ID));
//        String cardAlias = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName()));
//        String bankName = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName()));
//        String cardNumber = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName()));
//        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName())));
//        CreditCardType cardType = CreditCardType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName())));
//        int closingDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName()));
//        int dueDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName()));
//        CreditCardBackground creditCardBackground = CreditCardBackground.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName())));
//
//
//        Calendar cardExpiration = null;
//        if(!cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())).isEmpty()) {
//            cardExpiration = Calendar.getInstance();
//            cardExpiration.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())));
//        }
//
//        return new CreditCard(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay, creditCardBackground);
//    }
//
//
//    private CreditPeriod getCreditPeriodFromCursor(Cursor cursor) {
//        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable._ID));
//        int periodNameStyle = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName()));
//        Calendar startDate = Calendar.getInstance();
//        startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName())));
//        Calendar endDate = Calendar.getInstance();
//        endDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName())));
//        BigDecimal creditLimit =  new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName())));
//
//        return new CreditPeriod(id, periodNameStyle, startDate, endDate, creditLimit);
//    }
//
//
//    private Expense getExpenseFromCursor(Cursor cursor) {
//
//        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable._ID));
//        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName()));
//        byte[] image = cursor.getBlob(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName()));
//        String fullImagePath = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName()));
//        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName())));
//        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName())));
//        Calendar date = Calendar.getInstance();
//        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName())));
//        ExpenseCategory expenseCategory = ExpenseCategory.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName())));
//        ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName())));
//
//        return new Expense(id, description, image, fullImagePath, amount, currency, date, expenseCategory, expenseType);
//    }
//
//
//    private Payment getPaymentFromCursor(Cursor cursor) {
//
//        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable._ID));
//        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName()));
//        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName())));
//        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName())));
//        Calendar date = Calendar.getInstance();
//        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName())));
//
//        return new Payment(id, description, amount, currency, date);
//    }
}
