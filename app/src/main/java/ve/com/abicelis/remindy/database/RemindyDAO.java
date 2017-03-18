package ve.com.abicelis.remindy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import ve.com.abicelis.remindy.enums.ReminderCategory;
import ve.com.abicelis.remindy.enums.ReminderDateType;
import ve.com.abicelis.remindy.enums.ReminderExtraType;
import ve.com.abicelis.remindy.enums.ReminderRepeatEndType;
import ve.com.abicelis.remindy.enums.ReminderRepeatType;
import ve.com.abicelis.remindy.enums.ReminderSortType;
import ve.com.abicelis.remindy.enums.ReminderStatus;
import ve.com.abicelis.remindy.enums.ReminderTimeType;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.exception.CouldNotDeleteDataException;
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.exception.CouldNotUpdateDataException;
import ve.com.abicelis.remindy.exception.PlaceNotFoundException;
import ve.com.abicelis.remindy.model.AdvancedReminder;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.model.Reminder;
import ve.com.abicelis.remindy.model.AdvancedReminderByPlaceComparator;
import ve.com.abicelis.remindy.model.ReminderExtra;
import ve.com.abicelis.remindy.model.ReminderExtraAudio;
import ve.com.abicelis.remindy.model.ReminderExtraImage;
import ve.com.abicelis.remindy.model.ReminderExtraLink;
import ve.com.abicelis.remindy.model.ReminderExtraText;
import ve.com.abicelis.remindy.model.SimpleReminder;
import ve.com.abicelis.remindy.model.Time;

/**
 * Created by Alex on 9/3/2017.
 */
public class RemindyDAO {

    private RemindyDbHelper mDatabaseHelper;

    public RemindyDAO(Context context) {
        mDatabaseHelper = new RemindyDbHelper(context);
    }


    /* Get data from database */

//    /**
//     * Returns a List of OVERDUE Reminders.
//     * These are reminders which have an Active status, but will never trigger because of their set dates
//     * Examples:
//     * 1. Reminder is set to end in the past:
//     * endDate < today
//     * 2. Reminder is set to end sometime in the future, but on a day of the week that have passed:
//     * Today=Tuesday, endDate=Friday but Reminder is set to trigger only Mondays.
//     */
//    public List<Reminder> getOverdueReminders() {
//        return getRemindersByStatus(ReminderStatus.OVERDUE, ReminderSortType.DATE);
//    }
//
//    /**
//     * Returns a List of ACTIVE Reminders.
//     * These are reminders which have an Active status, and will trigger sometime in the present or in the future
//     * Basically, all Reminders which are *NOT* OVERDUE (See function above)
//     *
//     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
//     */
//    public List<Reminder> getActiveReminders(@NonNull ReminderSortType sortType) {
//        return getRemindersByStatus(ReminderStatus.ACTIVE, sortType);
//    }
//
//    /**
//     * Returns a List of Reminders with a status of DONE.
//     *
//     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
//     */
//    public List<Reminder> getDoneReminders(@NonNull ReminderSortType sortType) {
//        return getRemindersByStatus(ReminderStatus.DONE, sortType);
//    }
//
//    /**
//     * Returns a List of Reminders with a status of ARCHIVED.
//     *
//     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
//     */
//    public List<Reminder> getArchivedReminders(@NonNull ReminderSortType sortType) {
//        return getRemindersByStatus(ReminderStatus.ARCHIVED, sortType);
//    }


    /**
     * Returns a List of ADVANCED AND SIMPLE Reminders given a specific ReminderStatus and ReminderSortType
     * The available ReminderStatus are:
     *   - ARCHIVED.
     *   - DONE.
     *   - ACTIVE:
     *          These are reminders which have an Active status, and will trigger sometime in the present or in the future
     *          Basically, all Reminders which are *NOT* OVERDUE (See below)
     *   - OVERDUE:
     *          These are reminders which have an Active status, but will never trigger because of their set dates
     *          Examples:
     *              1. Reminder is set to end in the past: endDate < today
     *              2. Reminder is set to end sometime in the future, but on a day of the week that have passed: Today=Tuesday, endDate=Friday but Reminder is set to trigger only Mondays.
     *
     * @param reminderStatus ReminderStatus enum value with which to filter Reminders.
     * @param sortType       ReminderSortType enum value with which to sort results. By date, by category or by Location
     */


    //TODO: This method must change
    //Get advanced reminders, and get its extras
    //Get simple reminders, and get its extras
    //Sort/Group them by ReminderSortType
    //Return them either Map<ReminderDateType, Reminder> or Map<ReminderDateType, List<Reminder>>
    //Where ReminderDateType is [Today, Tomorrow, Next Week... ]
    //Or maybe list Map<ReminderViewType, Object> where ReminderViewType is [AdvRem, SimplRem, Header)]
    //and when Map key is Header, value will be just s String with header name? so ReminderAdapter is happy...
    //Figure this part out.
    public List<Reminder> getRemindersByStatus(@NonNull ReminderStatus reminderStatus, @NonNull ReminderSortType sortType) {
        List<Reminder> reminders = new ArrayList<>();

        String orderByClause = null;
        switch (sortType) {
            case DATE:
                orderByClause = RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getName() + " DESC";
                break;
            case PLACE:
                //Cant sort here, need the Place's name, which we don't have yet.
                break;
        }

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.AdvancedReminderTable.TABLE_NAME, null, RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getName() + "=?",
                new String[]{reminderStatus.name()}, null, null, orderByClause);

        try {
            while (cursor.moveToNext()) {
                AdvancedReminder current = getAdvancedReminderFromCursor(cursor);

                //Try to get the Place, if there is one
                try {
                    int placeId = cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_PLACE_FK.getName()));
                    if(placeId != -1)
                        current.setPlace(getPlace(placeId));
                } catch (Exception e) {/*Thrown if COLUMN_NAME_PLACE_FK is null, so do nothing.*/}

                //Try to get the Extras, if there are any
                current.setExtras(getAdvancedReminderExtras(current.getId()));

                reminders.add(current);
            }
        } finally {
            cursor.close();
        }

//        //Do sort by place if needed
//        if (sortType == ReminderSortType.PLACE) {
//            Collections.sort(reminders, new AdvancedReminderByPlaceComparator());
//        }

        return reminders;
    }

    /**
     * Returns a List of all the Places in the database.
     */
    public List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.PlaceTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                places.add(getPlaceFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return places;
    }

    /**
     * Returns a Place given a placeId.
     * @param placeId The id of the place
     */
    public Place getPlace(int placeId) throws PlaceNotFoundException, SQLiteConstraintException {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.PlaceTable.TABLE_NAME, null, RemindyContract.PlaceTable._ID + "=?",
                new String[]{String.valueOf(placeId)}, null, null, null);

        if (cursor.getCount() == 0)
            throw new PlaceNotFoundException("Specified Place not found in the database. Passed id=" + placeId);
        if (cursor.getCount() > 1)
            throw new SQLiteConstraintException("Database UNIQUE constraint failure, more than one record found. Passed value=" + placeId);

        cursor.moveToNext();
        return getPlaceFromCursor(cursor);
    }


    /**
     * Returns a List of Extras associated to an Advanced Reminder.
     * @param reminderId The id of the Reminder, fk in AdvancedReminderExtraTable
     */
    public List<ReminderExtra> getAdvancedReminderExtras(int reminderId) {
        List<ReminderExtra> extras = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.AdvancedReminderExtraTable.TABLE_NAME, null, RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + "=?",
                        new String[]{String.valueOf(reminderId)}, null, null, null);

        try {
            while (cursor.moveToNext()) {
                extras.add(getReminderExtraFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return extras;
    }

    /**
     * Returns a List of Extras associated to a Simple Reminder.
     * @param reminderId The id of the Reminder, fk in SimpleReminderExtraTable
     */
    public List<ReminderExtra> getSimpleReminderExtras(int reminderId) {
        List<ReminderExtra> extras = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.SimpleReminderExtraTable.TABLE_NAME, null, RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + "=?",
                        new String[]{String.valueOf(reminderId)}, null, null, null);

        try {
            while (cursor.moveToNext()) {
                extras.add(getReminderExtraFromCursor(cursor));
            }
        } finally {
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
     * Deletes a single AdvancedExtra, given its ID
     * @param extraId The ID of the extra to delete
     */
    public boolean deleteAdvancedExtra(int extraId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.AdvancedReminderExtraTable.TABLE_NAME,
                RemindyContract.AdvancedReminderExtraTable._ID + " =?",
                new String[]{String.valueOf(extraId)}) > 0;
    }

    /**
     * Deletes a single SimpleExtra, given its ID
     * @param extraId The ID of the extra to delete
     */
    public boolean deleteSimpleExtra(int extraId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.SimpleReminderExtraTable.TABLE_NAME,
                RemindyContract.SimpleReminderExtraTable._ID + " =?",
                new String[]{String.valueOf(extraId)}) > 0;
    }

    /**
     * Deletes all Extras linked to an Advanced Reminder, given the reminder's ID
     * @param reminderId The ID of the reminder whose extras will be deleted
     */
    public boolean deleteExtrasFromAdvancedReminder(int reminderId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.AdvancedReminderExtraTable.TABLE_NAME,
                RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }

    /**
     * Deletes all Extras linked to a Simple Reminder, given the reminder's ID
     * @param reminderId The ID of the reminder whose extras will be deleted
     */
    public boolean deleteExtrasFromSimpleReminder(int reminderId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.delete(RemindyContract.SimpleReminderExtraTable.TABLE_NAME,
                RemindyContract.SimpleReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName() + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }

    /**
     * Deletes an Advanced Reminder with its associated Extras, given the reminder's ID
     * @param reminderId The ID of the reminder o delete
     */
    public boolean deleteAdvancedReminder(int reminderId, ReminderType reminderType) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        deleteExtrasFromAdvancedReminder(reminderId);

        return db.delete(RemindyContract.AdvancedReminderTable.TABLE_NAME,
                RemindyContract.AdvancedReminderTable._ID + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }

    /**
     * Deletes a Simple Reminder with its associated Extras, given the reminder's ID
     * @param reminderId The ID of the reminder o delete
     */
    public boolean deleteReminder(int reminderId, ReminderType reminderType) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        deleteExtrasFromSimpleReminder(reminderId);

        return db.delete(RemindyContract.SimpleReminderTable.TABLE_NAME,
                RemindyContract.SimpleReminderTable._ID + " =?",
                new String[]{String.valueOf(reminderId)}) > 0;
    }









    /* Update data on database */

    /**
     * Updates the information stored about a Place
     * @param place The Place to update
     */
    public long updatePlace(Place place) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromPlace(place);

        //Which row to update
        String selection = RemindyContract.PlaceTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(place.getId())};

        int count = db.update(
                RemindyContract.PlaceTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    /**
     * Updates the information stored about an Extra, associated to an Advanced Reminder
     * @param extra The ReminderExtra to update
     */
    public long updateAdvancedReminderExtra(ReminderExtra extra) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.update(RemindyContract.AdvancedReminderExtraTable.TABLE_NAME,
                        getValuesFromExtra(extra),
                        RemindyContract.AdvancedReminderExtraTable._ID + " =? ",
                        new String[] {String.valueOf(extra.getId())});
    }

    /**
     * Updates the information stored about an Extra, associated to a Simple Reminder
     * @param extra The ReminderExtra to update
     */
    public long updateSimpleReminderExtra(ReminderExtra extra) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        return db.update(RemindyContract.SimpleReminderExtraTable.TABLE_NAME,
                getValuesFromExtra(extra),
                RemindyContract.SimpleReminderExtraTable._ID + " =? ",
                new String[] {String.valueOf(extra.getId())});
    }

    /**
     * Updates the information stored about an Advanced Reminder and its Extras.
     * @param advancedReminder The Advanced Reminder (and associated Extras) to update
     */
    public long updateAdvancedReminder(AdvancedReminder advancedReminder) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        try {
            deleteExtrasFromAdvancedReminder(advancedReminder.getId());
        } catch (CouldNotDeleteDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to delete Extras associated with Reminder ID= " + advancedReminder.getId(), e);
        }
        //Insert new Extras
        try {
            insertAdvancedReminderExtras(advancedReminder.getExtras());
        } catch (CouldNotInsertDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to insert Extras associated with Reminder ID= " + advancedReminder.getId(), e);
        }


        //Set advancedReminder values
        ContentValues values = getValuesFromAdvancedReminder(advancedReminder);

        //Which row to update
        String selection = RemindyContract.AdvancedReminderTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(advancedReminder.getId())};

        return db.update(
                RemindyContract.AdvancedReminderTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    /**
     * Updates the information stored about a Simple Reminder and its Extras.
     * @param simpleReminder The Simple Reminder (and associated Extras) to update
     */
    public long updateSimpleReminder(SimpleReminder simpleReminder) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        try {
            deleteExtrasFromSimpleReminder(simpleReminder.getId());
        } catch (CouldNotDeleteDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to delete Extras associated with Reminder ID= " + simpleReminder.getId(), e);
        }
        //Insert new Extras
        try {
            insertSimpleReminderExtras(simpleReminder.getExtras());
        } catch (CouldNotInsertDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to insert Extras associated with Reminder ID= " + simpleReminder.getId(), e);
        }


        //Set advancedReminder values
        ContentValues values = getValuesFromSimpleReminder(simpleReminder);

        //Which row to update
        String selection = RemindyContract.SimpleReminderTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(simpleReminder.getId())};

        return db.update(
                RemindyContract.SimpleReminderTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }







    /* Insert data into database */

    /**
     * Inserts a new Place into the database.
     * @param place The Place to be inserted
     */
    public long insertPlace(Place place) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = getValuesFromPlace(place);

        long newRowId;
        newRowId = db.insert(RemindyContract.PlaceTable.TABLE_NAME, null, values);

        if (newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Place: " + place.toString());

        return newRowId;
    }

    /**
     * Inserts a List of Extras associated to an AdvancedReminder, into the database.
     * @param extras The List of Extras to be inserted
     */
    public long[] insertAdvancedReminderExtras(List<ReminderExtra> extras) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long[] newRowIds = new long[extras.size()];

        for (int i = 0; i < extras.size(); i++) {

            ContentValues values = getValuesFromExtra(extras.get(i));

            newRowIds[i] = db.insert(RemindyContract.AdvancedReminderExtraTable.TABLE_NAME, null, values);

            if (newRowIds[i] == -1)
                throw new CouldNotInsertDataException("There was a problem inserting the Extra: " + extras.toString());
        }

        return newRowIds;
    }

    /**
     * Inserts a List of Extras associated to an AdvancedReminder, into the database.
     * @param extras The List of Extras to be inserted
     */
    public long[] insertSimpleReminderExtras(List<ReminderExtra> extras) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long[] newRowIds = new long[extras.size()];

        for (int i = 0; i < extras.size(); i++) {

            ContentValues values = getValuesFromExtra(extras.get(i));

            newRowIds[i] = db.insert(RemindyContract.SimpleReminderExtraTable.TABLE_NAME, null, values);

            if (newRowIds[i] == -1)
                throw new CouldNotInsertDataException("There was a problem inserting the Extra: " + extras.toString());
        }

        return newRowIds;
    }

    /**
     * Inserts a new Advanced Reminder and its associated Extras into the database.
     * @param advancedReminder The Reminder (and associated Extras) to insert
     */
    public long insertAdvancedReminder(AdvancedReminder advancedReminder) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Insert Extras
        if (advancedReminder.getExtras() != null && advancedReminder.getExtras().size() > 0) {
            try {
                insertAdvancedReminderExtras(advancedReminder.getExtras());
            } catch (CouldNotInsertDataException e) {
                throw new CouldNotInsertDataException("There was a problem inserting the Extras while inserting the Reminder: " + advancedReminder.toString(), e);
            }
        }

        ContentValues values = getValuesFromAdvancedReminder(advancedReminder);

        long newRowId;
        newRowId = db.insert(RemindyContract.AdvancedReminderTable.TABLE_NAME, null, values);

        if (newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Reminder: " + advancedReminder.toString());

        return newRowId;
    }

    /**
     * Inserts a new Simple Reminder and its associated Extras into the database.
     * @param simpleReminder The Reminder (and associated Extras) to insert
     */
    public long insertSimpleReminder(SimpleReminder simpleReminder) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Insert Extras
        if (simpleReminder.getExtras() != null && simpleReminder.getExtras().size() > 0) {
            try {
                insertSimpleReminderExtras(simpleReminder.getExtras());
            } catch (CouldNotInsertDataException e) {
                throw new CouldNotInsertDataException("There was a problem inserting the Extras while inserting the Reminder: " + simpleReminder.toString(), e);
            }
        }

        ContentValues values = getValuesFromSimpleReminder(simpleReminder);

        long newRowId;
        newRowId = db.insert(RemindyContract.SimpleReminderTable.TABLE_NAME, null, values);

        if (newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Reminder: " + simpleReminder.toString());

        return newRowId;
    }






    /* Model to ContentValues */

    private ContentValues getValuesFromPlace(Place place) {
        ContentValues values = new ContentValues();
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName(), place.getAlias());
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName(), place.getAddress());
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName(), place.getLatitude());
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName(), place.getLongitude());
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName(), place.getRadius());
        values.put(RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName(), place.isOneOff());
        return values;
    }

    private ContentValues getValuesFromExtra(ReminderExtra extra) {
        ContentValues values = new ContentValues();
        values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName(), extra.getReminderId());
        values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_TYPE.getName(), extra.getType().name());

        switch (extra.getType()) {
            case AUDIO:
                values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName(), ((ReminderExtraAudio) extra).getAudio());
                break;
            case IMAGE:
                values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName(), ((ReminderExtraImage) extra).getThumbnail());
                values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraImage) extra).getFullImagePath());
                break;
            case TEXT:
                values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraText) extra).getText());
                break;
            case LINK:
                values.put(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraLink) extra).getLink());
                break;
            default:
                throw new InvalidParameterException("ReminderExtraType is invalid. Value = " + extra.getType());
        }
        return values;
    }

    private ContentValues getValuesFromAdvancedReminder(AdvancedReminder reminder) {
        ContentValues values = new ContentValues();
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getName(), reminder.getStatus().name());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_TITLE.getName(), reminder.getTitle());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_DESCRIPTION.getName(), reminder.getDescription());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_CATEGORY.getName(), reminder.getCategory().name());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_PLACE_FK.getName(), (reminder.getPlace() != null ? String.valueOf(reminder.getPlace().getId()) : "-1"));
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_DATE_TYPE.getName(), reminder.getDateType().name());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_DATE.getName(), reminder.getStartDate().getTimeInMillis());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getName(), reminder.getEndDate().getTimeInMillis());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_TIME_TYPE.getName(), reminder.getTimeType().name());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getName(), reminder.getStartTime().getTimeInMinutes());
        values.put(RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_TIME.getName(), reminder.getEndTime().getTimeInMinutes());
        return values;
    }

    private ContentValues getValuesFromSimpleReminder(SimpleReminder reminder) {
        ContentValues values = new ContentValues();
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_STATUS.getName(), reminder.getStatus().name());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_TITLE.getName(), reminder.getTitle());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_DESCRIPTION.getName(), reminder.getDescription());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_CATEGORY.getName(), reminder.getCategory().name());

        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_DATE.getName(), reminder.getDate().getTimeInMillis());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_TIME.getName(), reminder.getTime().getTimeInMinutes());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_TYPE.getName(), reminder.getRepeatType().name());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName(), reminder.getRepeatInterval());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName(), (reminder.getRepeatEndType() != null ? reminder.getRepeatEndType().name() : null));
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName(), reminder.getRepeatEndNumberOfEvents());
        values.put(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName(), (reminder.getRepeatEndDate() != null ? reminder.getRepeatEndDate().getTimeInMillis() : 0));
        return values;
    }







    /* Cursor to Model */

    private AdvancedReminder getAdvancedReminderFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable._ID));
        ReminderStatus status = ReminderStatus.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_STATUS.getName())));
        String title = cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_TITLE.getName()));
        String description = cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_DESCRIPTION.getName()));
        ReminderCategory category = ReminderCategory.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_CATEGORY.getName())));
        ReminderDateType dateType = ReminderDateType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_DATE_TYPE.getName())));

        Calendar startDate = null;
        int startDateIndex = cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getName());
        if (!cursor.getString(startDateIndex).isEmpty()) {
            startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(startDateIndex));
        }

        Calendar endDate = null;
        int endDateIndex = cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_DATE.getName());
        if (!cursor.getString(endDateIndex).isEmpty()) {
            endDate = Calendar.getInstance();
            endDate.setTimeInMillis(cursor.getLong(endDateIndex));
        }

        ReminderTimeType timeType = ReminderTimeType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_TIME_TYPE.getName())));

        Time startTime = new Time(cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_START_TIME.getName())));
        Time endTime = new Time(cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderTable.COLUMN_NAME_END_TIME.getName())));


        return new AdvancedReminder(id, status, title, description, category, null, dateType, startDate, endDate, timeType, startTime, endTime);
    }


    private SimpleReminder getSimpleReminderFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.SimpleReminderTable._ID));
        ReminderStatus status = ReminderStatus.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_STATUS.getName())));
        String title = cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_TITLE.getName()));
        String description = cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_DESCRIPTION.getName()));
        ReminderCategory category = ReminderCategory.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_CATEGORY.getName())));

        Calendar date = null;
        int dateIndex = cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_DATE.getName());
        if (!cursor.getString(dateIndex).isEmpty()) {
            date = Calendar.getInstance();
            date.setTimeInMillis(cursor.getLong(dateIndex));
        }

        Time time = new Time(cursor.getInt(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_TIME.getName())));
        ReminderRepeatType repeatType = ReminderRepeatType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_TYPE.getName())));
        int repeatInterval = cursor.getInt(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName()));
        ReminderRepeatEndType repeatEndType = ReminderRepeatEndType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName())));
        int repeatEndNumberOfEvents = cursor.getInt(cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName()));

        Calendar repeatEndDate = null;
        int repeatEndDateIndex = cursor.getColumnIndex(RemindyContract.SimpleReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName());
        if (!cursor.getString(repeatEndDateIndex).isEmpty()) {
            repeatEndDate = Calendar.getInstance();
            repeatEndDate.setTimeInMillis(cursor.getLong(repeatEndDateIndex));
        }

        return new SimpleReminder(id, status, title, description, category, date, time, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, repeatEndDate);
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
        int id = cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderExtraTable._ID));
        int reminderId = cursor.getInt(cursor.getColumnIndex(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_REMINDER_FK.getName()));
        ReminderExtraType extraType = ReminderExtraType.valueOf(cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_TYPE.getName())));
        String textContent = cursor.getString(cursor.getColumnIndex(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_TEXT.getName()));
        byte[] blobContent = cursor.getBlob(cursor.getColumnIndex(RemindyContract.AdvancedReminderExtraTable.COLUMN_NAME_CONTENT_BLOB.getName()));

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

}