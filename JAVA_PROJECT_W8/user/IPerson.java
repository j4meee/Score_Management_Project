package user;
public interface IPerson {
    String getId();
    String getUsername();
    String getPassword();
    String getFullName();
    String getRole();
    boolean can(String action);
}