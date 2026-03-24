package exception;

// Custom exception for invalid score values
// PDF Section 10 & 13: Custom exception with specific validation
public class InvalidScoreException extends SchoolException {
    
    public InvalidScoreException(String message) {
        super(message);
    }
    
    public InvalidScoreException(double score) {
        super("Invalid score: " + score + ". Score must be between 0 and 100.");
    }
}