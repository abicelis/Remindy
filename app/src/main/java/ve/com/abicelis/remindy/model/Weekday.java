package ve.com.abicelis.remindy.model;

/**
 * Created by abice on 16/3/2017.
 */

public class Weekday {

    private int value;

    public Weekday() {}
    public Weekday(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        //TODO: Convert that into binary or something, kind of like linux's permissions
        //777 = 111 111 111
    }

    public int getValue() {
        return value;
    }




    public void setMonday(boolean flag) {
        //TODO: set this.value here for monday flag
    }
    boolean getMonday() {
        return true;
    }
    //TODO: do this with all days of week


}
