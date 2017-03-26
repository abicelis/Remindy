package ve.com.abicelis.remindy.model.reminder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Place;

/**
 * Created by abice on 3/3/2017.
 */

public class LocationBasedReminder extends Reminder implements Serializable {

    private int placeId;
    private Place place;
    private boolean isEntering;

    public LocationBasedReminder(int placeId, @NonNull Place place, boolean entering) {
        init(placeId, place, entering);
    }

    public LocationBasedReminder(int id, int taskId, int placeId, @Nullable Place place, boolean entering) {
        super(id, taskId);
        init(placeId, place, entering);
    }

    private void init(int placeId, @Nullable Place place, boolean isEntering) {
        this.placeId = placeId;
        this.place = place;
        this.isEntering = isEntering;
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

    public boolean isEntering() {return isEntering;}
    public void setEntering(boolean entering) {isEntering = entering;}


    @Override
    public String toString() {
        String                      res = "Reminder ID=" + getId() + "\r\n";
                                    res += " Type=" + getType().name() + "\r\n";
                                    res += " TaskID=" + getTaskId() + "\r\n";
                                    res += " PlaceID=" + placeId + "\r\n";
        if(place != null)           res +=  " Place=" + place.toString() + "\r\n";
                                    res +=  " isEntering=" + isEntering;
        return res;
    }
}
