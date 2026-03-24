package exception;

// Custom exception for duplicate entries
public class DuplicateEntityException extends SchoolException {
    
    public DuplicateEntityException(String entityType, String identifier) {
        super(entityType + " with identifier '" + identifier + "' already exists.");
    }
    
    public DuplicateEntityException(String message) {
        super(message);
    }
}
    