package ve.com.abicelis.remindy.exception;

/**
 * Created by abice on 12/3/2017.
 */

public class CouldNotGetDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be fetched from the database.";

    public CouldNotGetDataException() {
        super(DEFAULT_MESSAGE);
    }
    public CouldNotGetDataException(String message) {
        super(message);
    }
    public CouldNotGetDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
