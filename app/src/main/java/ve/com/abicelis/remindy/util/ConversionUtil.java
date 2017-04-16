package ve.com.abicelis.remindy.util;

import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;

import ve.com.abicelis.remindy.enums.ReminderType;
import ve.com.abicelis.remindy.enums.TaskViewModelType;
import ve.com.abicelis.remindy.model.Place;
import ve.com.abicelis.remindy.viewmodel.TaskViewModel;

/**
 * Created by Alex on 9/3/2017.
 */

public class ConversionUtil {
    /**
     * Converts dp to px
     */
    public static int dpToPx(int dp, Resources resources) {
        final float scale = resources.getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static LatLng placeToLatLng(Place place) {
        return new LatLng(place.getLatitude(), place.getLongitude());
    }

    public static Location placeToLocation(Place place) {
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLatitude(place.getLatitude());
        loc.setLongitude(place.getLongitude());
        return loc;
    }


    public static TaskViewModelType taskReminderTypeToTaskViewmodelType(ReminderType reminderType) {
        switch (reminderType) {
            case NONE:
                return TaskViewModelType.UNPROGRAMMED_REMINDER;
            case ONE_TIME:
                return TaskViewModelType.ONE_TIME_REMINDER;
            case REPEATING:
                return TaskViewModelType.REPEATING_REMINDER;
            case LOCATION_BASED:
                return TaskViewModelType.LOCATION_BASED_REMINDER;
            default:
                throw new InvalidParameterException("Unhandled ReminderType passed into ConversionUtil.taskReminderTypeToTaskViewmodelType()");
        }
    }

}