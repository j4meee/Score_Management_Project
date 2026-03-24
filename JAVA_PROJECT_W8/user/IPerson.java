package user;

// PDF Section 6: Interface — defines behavior contract only
public interface IPerson {
    String getId();
    String getUsername();
    String getPassword();
    String getFullName();
    boolean isActive();
    boolean checkPassword(String input);
    
    // PDF Section 6: behavior rule — every person must be able to answer can()
    boolean can(String action);
}