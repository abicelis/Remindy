package ve.com.abicelis.remindy.exception;

/**
 * Created by abice on 12/3/2017.
 */

public class CouldNotInsertDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Could not insert data into the database.";

    public CouldNotInsertDataException() {
        super(DEFAULT_MESSAGE);
    }
    public CouldNotInsertDataException(String message) {
        super(message);
    }
    public CouldNotInsertDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
