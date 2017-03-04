package ve.com.abicelis.remindy.exception;



/**
 * Created by abice on 3/3/2017.
 */

public class MalformedLinkException extends Exception {

    private static final String DEFAULT_MESSAGE = "Link is invalid, malformed.";

    public MalformedLinkException() {
        super(DEFAULT_MESSAGE);
    }


    public MalformedLinkException(String message) {
        super(message);
    }

    public MalformedLinkException(String message, Throwable cause) {
        super(message, cause);
    }

}
