package ve.com.abicelis.remindy.model.reminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Place;

/**
 * Created by abice on 3/3/2017.
 */

public class LocationBasedReminder extends Reminder implements Serializable {

    private int placeId;
    private Place place;
    private boolean triggerEntering;
    private boolean triggerExiting;

    public LocationBasedReminder() {} //Parameter-less argument for Reminder creation

    public LocationBasedReminder(int placeId, @NonNull Place place, boolean triggerEntering, boolean triggerExiting) {
        init(placeId, place, triggerEntering, triggerExiting);
    }

    public LocationBasedReminder(int id, int taskId, int placeId, @Nullable Place place, boolean triggerEntering, boolean triggerExiting) {
        super(id, taskId);
        init(placeId, place, triggerEntering, triggerExiting);
    }

    private void init(int placeId, @Nullable Place place, boolean triggerEntering, boolean triggerExiting) {
        this.placeId = placeId;
        this.place = place;
        this.triggerEntering = triggerEntering;
        this.triggerExiting = triggerExiting;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.LOCATION_BASED;
    }

    public int getPlaceId() {
        return placeId;
    }
    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public Place getPlace() {return place;}
    public void setPlace(Place place) {this.place = place;}

    public boolean getTriggerEntering() {return triggerEntering;}
    public void setTriggerEntering(boolean triggerEntering) {this.triggerEntering = triggerEntering;}

    public boolean getTriggerExiting() {return triggerExiting;}
    public void setTriggerExiting(boolean triggerExiting) {this.triggerExiting = triggerExiting;}

    public String getEnteringExitingString(Context context) {
        if(triggerEntering && triggerExiting)
             return context.getResources().getString(R.string.fragment_detail_location_based_reminder_entering_or_exiting);
        else if(triggerEntering)
            return context.getResources().getString(R.string.fragment_detail_location_based_reminder_entering);
        else
            return context.getResources().getString(R.string.fragment_detail_location_based_reminder_exiting);
    }

    @Override
    public String toString() {
        String                      res = "Reminder ID=" + getId() + "\r\n";
                                    res += " Type=" + getType().name() + "\r\n";
                                    res += " TaskID=" + getTaskId() + "\r\n";
                                    res += " PlaceID=" + placeId + "\r\n";
        if(place != null)           res +=  " Place=" + place.toString() + "\r\n";
                                    res +=  " triggerEntering=" + triggerEntering + "\r\n";
                                    res +=  " triggerExiting=" + triggerExiting;
        return res;
    }
}
