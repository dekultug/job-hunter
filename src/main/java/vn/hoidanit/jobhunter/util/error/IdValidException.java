package vn.hoidanit.jobhunter.util.error;

public class IdValidException extends Exception {
    public IdValidException() {
    }

    // Constructor that accepts a message
    public IdValidException(String message) {
        super(message);
    }
}
