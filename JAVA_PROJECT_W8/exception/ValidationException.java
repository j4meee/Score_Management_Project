package exception;

// Custom exception for input validation failures
public class ValidationException extends SchoolException {
    
    public ValidationException(String field, String reason) {
        super("Validation failed for '" + field + "': " + reason);
    }
    
    public ValidationException(String message) {
        super(message);
    }
}