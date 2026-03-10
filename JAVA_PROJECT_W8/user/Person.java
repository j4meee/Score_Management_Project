package user;

// Person.can() returns false by default.
// Child classes (Admin, Teacher, Student) OVERRIDE can() with their own rules.
// This is polymorphism — same method signature, different behavior per role.
public class Person implements IPerson {

    private String id;
    private String fullName;
    private String username;
    private String password;
    private boolean active;

    public Person(String id, String fullName, String username, String password) {
        setId(id);
        setFullName(fullName);
        setPassword(password);
        setUsername(username);
        this.active = true;
    }

    @Override public String getId()       { return id; }
    @Override public String getFullName() { return fullName; }
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public boolean isActive()   { return active; }

    public void setId(String id) {
        this.id = isBlank(id) ? "UNKNOWN" : id.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = isBlank(fullName) ? "No Name" : fullName.trim();
    }

    public void setUsername(String username) {
        if (isBlank(username)) {
            String baseId = (this.id == null || this.id.equals("UNKNOWN")) ? "user" : this.id;
            this.username = "user_" + baseId;
        } else {
            this.username = username.trim();
        }
    }

    public void setPassword(String password) {
        String pw = (password == null) ? "" : password;
        this.password = (pw.length() < 4) ? "0000" : pw;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    protected boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Default: Person has no permissions.
    // Subclasses OVERRIDE this — that's polymorphism (override, not overload).
    @Override
    public boolean can(String action) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id.equals(person.id) && username.equals(person.username);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", active=" + active +
                '}';
    }
}