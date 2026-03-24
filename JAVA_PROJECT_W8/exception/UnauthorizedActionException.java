package exception;

// Custom exception for permission violations
public class UnauthorizedActionException extends SchoolException {
    
    public UnauthorizedActionException(String action, String role) {
        super("Unauthorized: " + role + " cannot perform action: " + action);
    }
    
    public UnauthorizedActionException(String message) {
        super(message);
    }
}   