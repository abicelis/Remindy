package ve.com.abicelis.remindy.model.reminder;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.model.Place;

/**
 * Created by abice on 3/3/2017.
 */

public class LocationBasedReminder extends Reminder implements Serializable {

    private Place place;
    private boolean isEntering;

    public LocationBasedReminder(@NonNull Place place, boolean entering) {
        init(place, entering);
    }

    public LocationBasedReminder(int id, @NonNull Place place, boolean entering) {
        super(id);
        init(place, entering);
    }

    private void init(@NonNull Place place, boolean isEntering) {
        this.place = place;
        this.isEntering = isEntering;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.LOCATION_BASED;
    }

    public Place getPlace() {return place;}
    public void setPlace(Place place) {this.place = place;}

    public boolean isEntering() {return isEntering;}
    public void setEntering(boolean entering) {isEntering = entering;}


    @Override
    public String toString() {
        String                      res = "Reminder ID=" + getId() + "\r\n";
                                    res += " Type=" + getType().name() + "\r\n";
                                    res +=  " Place=" + place.toString() + "\r\n";
                                    res +=  " isEntering=" + isEntering;
        return res;
    }
}
