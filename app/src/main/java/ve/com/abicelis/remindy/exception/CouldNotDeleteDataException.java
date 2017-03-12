package ve.com.abicelis.remindy.exception;

/**
 * Created by abice on 12/3/2017.
 */

public class CouldNotDeleteDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be deleted from the database.";

    public CouldNotDeleteDataException() {
        super(DEFAULT_MESSAGE);
    }
    public CouldNotDeleteDataException(String message) {
        super(message);
    }
    public CouldNotDeleteDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
