package exception;

// Base custom exception for the school system
// PDF Section 10: Custom exception class
public class SchoolException extends Exception {
    
    public SchoolException(String message) {
        super(message);
    }
    
    public SchoolException(String message, Throwable cause) {
        super(message, cause);
    }
}