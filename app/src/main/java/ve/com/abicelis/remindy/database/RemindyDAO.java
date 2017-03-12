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
import ve.com.abicelis.remindy.exception.CouldNotInsertDataException;
import ve.com.abicelis.remindy.exception.CouldNotUpdateDataException;
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
     * endDate < today
     * 2. Reminder is set to end sometime in the future, but on a day of the week that have passed:
     * Today=Tuesday, endDate=Friday but Reminder is set to trigger only Mondays.
     */
    public List<Reminder> getOverdueReminders() {
        return getRemindersByStatus(ReminderStatus.OVERDUE, ReminderSortType.DATE);
    }

    /**
     * Returns a List of ACTIVE Reminders.
     * These are reminders which have an Active status, and will trigger sometime in the present or in the future
     * Basically, all Reminders which are *NOT* OVERDUE (See function above)
     *
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getActiveReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.ACTIVE, sortType);
    }

    /**
     * Returns a List of Reminders with a status of DONE.
     *
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getDoneReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.DONE, sortType);
    }

    /**
     * Returns a List of Reminders with a status of ARCHIVED.
     *
     * @param sortType ReminderSortType enum value with which to sort results. By date, by category or by Location
     */
    public List<Reminder> getArchivedReminders(@NonNull ReminderSortType sortType) {
        return getRemindersByStatus(ReminderStatus.ARCHIVED, sortType);
    }


    /**
     * Returns a List of Reminders given a specific ReminderStatus and ReminderSortType
     *
     * @param reminderStatus ReminderStatus enum value with which to filter Reminders.
     * @param sortType       ReminderSortType enum value with which to sort results. By date, by category or by Location
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
        Cursor cursor = db.query(RemindyContract.ReminderTable.TABLE_NAME, null, RemindyContract.ReminderTable.COLUMN_NAME_STATUS + "=?",
                new String[]{reminderStatus.name()}, null, null, orderByClause);

        try {
            while (cursor.moveToNext()) {
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
        if (sortType == ReminderSortType.PLACE) {
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
        } finally {
            cursor.close();
        }

        return places;
    }

    /**
     * Returns a Place given a placeId.
     *
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
     * Returns a List of Extras associated to a Reminder.
     *
     * @param reminderId The id of the reminder, fk in ExtraTable
     */
    public List<ReminderExtra> getReminderExtras(int reminderId) {
        List<ReminderExtra> extras = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RemindyContract.ExtraTable.TABLE_NAME, null, RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK + "=?",
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
     *
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
     *
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
     *
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
     *
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

    /**
     * Updates the information stored about a Place
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
     * Updates the information stored about an Extra
     */
    public long updateReminderExtra(ReminderExtra extra) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromExtra(extra);

        //Which row to update
        String selection = RemindyContract.ExtraTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(extra.getId())};

        int count = db.update(
                RemindyContract.ExtraTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    /**
     * Updates the information stored about a Reminder and its Extras.
     */
    public long updateReminder(Reminder reminder) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Delete the extras
        try {
            deleteExtrasFromReminder(reminder.getId());
        } catch (CouldNotDeleteDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to delete Extras associated with Reminder ID= " + reminder.getId(), e);
        }
        //Insert new Extras
        try {
            insertReminderExtras(reminder.getExtras());
        } catch (CouldNotInsertDataException e) {
            throw new CouldNotUpdateDataException("Failed trying to insert Extras associated with Reminder ID= " + reminder.getId(), e);
        }


        //Set reminder values
        ContentValues values = getValuesFromReminder(reminder);

        //Which row to update
        String selection = RemindyContract.ReminderTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(reminder.getId())};

        int count = db.update(
                RemindyContract.ReminderTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }


    /* Insert data into database */

    public long insertPlace(Place place) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = getValuesFromPlace(place);

        long newRowId;
        newRowId = db.insert(RemindyContract.PlaceTable.TABLE_NAME, null, values);

        if (newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Place: " + place.toString());

        return newRowId;
    }

    public long[] insertReminderExtras(List<ReminderExtra> extras) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long[] newRowIds = new long[extras.size()];

        for (int i = 0; i < extras.size(); i++) {

            ContentValues values = getValuesFromExtra(extras.get(i));

            newRowIds[i] = db.insert(RemindyContract.ExtraTable.TABLE_NAME, null, values);

            if (newRowIds[i] == -1)
                throw new CouldNotInsertDataException("There was a problem inserting the Extra: " + extras.toString());
        }

        return newRowIds;
    }

    public long insetReminder(Reminder reminder) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Insert Extras
        if (reminder.getExtras() != null && reminder.getExtras().size() > 0) {
            try {
                insertReminderExtras(reminder.getExtras());
            } catch (CouldNotInsertDataException e) {
                throw new CouldNotInsertDataException("There was a problem inserting the Extras while inserting the Reminder: " + reminder.toString(), e);
            }
        }

        ContentValues values = getValuesFromReminder(reminder);

        long newRowId;
        newRowId = db.insert(RemindyContract.ReminderTable.TABLE_NAME, null, values);

        if (newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Reminder: " + reminder.toString());

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
        values.put(RemindyContract.ExtraTable.COLUMN_NAME_REMINDER_FK.getName(), extra.getReminderId());
        values.put(RemindyContract.ExtraTable.COLUMN_NAME_TYPE.getName(), extra.getType().name());

        switch (extra.getType()) {
            case AUDIO:
                values.put(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getName(), ((ReminderExtraAudio) extra).getAudio());
                break;
            case IMAGE:
                values.put(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_BLOB.getName(), ((ReminderExtraImage) extra).getThumbnail());
                values.put(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraImage) extra).getFullImagePath());
                break;
            case TEXT:
                values.put(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraText) extra).getText());
                break;
            case LINK:
                values.put(RemindyContract.ExtraTable.COLUMN_NAME_CONTENT_TEXT.getName(), ((ReminderExtraLink) extra).getLink());
                break;
            default:
                throw new InvalidParameterException("ReminderExtraType is invalid. Value = " + extra.getType());
        }
        return values;
    }

    private ContentValues getValuesFromReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_STATUS.getName(), reminder.getStatus().name());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_TITLE.getName(), reminder.getTitle());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_DESCRIPTION.getName(), reminder.getDescription());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_CATEGORY.getName(), reminder.getCategory().name());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_PLACE_FK.getName(), (reminder.getPlace() != null ? String.valueOf(reminder.getPlace().getId()) : "null"));
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_DATE_TYPE.getName(), reminder.getDateType().name());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_START_DATE.getName(), reminder.getStartDate().getTimeInMillis());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getName(), reminder.getEndDate().getTimeInMillis());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_TIME_TYPE.getName(), reminder.getTimeType().name());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_START_TIME.getName(), reminder.getStartTime().getTimeInMinutes());
        values.put(RemindyContract.ReminderTable.COLUMN_NAME_END_TIME.getName(), reminder.getEndTime().getTimeInMinutes());
        return values;
    }








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
        if (!cursor.getString(startDateIndex).isEmpty()) {
            startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(startDateIndex));
        }

        Calendar endDate = null;
        int endDateIndex = cursor.getColumnIndex(RemindyContract.ReminderTable.COLUMN_NAME_END_DATE.getName());
        if (!cursor.getString(endDateIndex).isEmpty()) {
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

}