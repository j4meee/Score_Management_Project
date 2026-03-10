package user;

public interface IPerson {
    String getId();
    String getUsername();
    String getPassword();
    String getFullName();
    boolean isActive();
    boolean checkPassword(String input);
    boolean can(String action);
}