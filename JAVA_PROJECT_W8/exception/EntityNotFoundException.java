package exception;

// Custom exception for when an entity is not found
public class EntityNotFoundException extends SchoolException {
    
    public EntityNotFoundException(String entityType, String id) {
        super(entityType + " with ID '" + id + "' not found.");
    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}