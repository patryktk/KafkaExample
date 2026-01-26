package pl.tkaczyk.emailnotificationservice.error;

public class RetryableException extends RuntimeException {

    //Custom error message to throw
    public RetryableException(String message) {
        super(message);
    }
    //Akceptuje obiekt, który spowodował błąd i przekazuje go do parent klass
    public RetryableException(Throwable cause) {
        super(cause);
    }
}
