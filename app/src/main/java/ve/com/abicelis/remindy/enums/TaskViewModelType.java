package ve.com.abicelis.remindy.enums;

import java.io.Serializable;

/**
 * Created by abice on 3/3/2017.
 */

public enum TaskViewModelType implements Serializable {
    HEADER,
    UNPROGRAMMED_REMINDER,
    ONE_TIME_REMINDER,
    REPEATING_REMINDER,
    LOCATION_BASED_REMINDER
}
