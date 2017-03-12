package ve.com.abicelis.remindy.exception;

/**
 * Created by abice on 12/3/2017.
 */

public class CouldNotUpdateDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be updated on the database.";

    public CouldNotUpdateDataException() {
        super(DEFAULT_MESSAGE);
    }
    public CouldNotUpdateDataException(String message) {
        super(message);
    }
    public CouldNotUpdateDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
