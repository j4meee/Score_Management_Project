package user;

// PDF Section 3: Abstraction — defining what a system must do without specifying how.
// PDF Section 4: Abstract Class — cannot be instantiated, serves as base class.
// "new Person(...)" is now IMPOSSIBLE — Person is only a concept, not a real role.
public abstract class Person implements IPerson {

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
        this.password = (pw.length() <= 4) ? "0000" : pw;
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

    // PDF Section 4-5: Abstract method — subclasses MUST implement this differently.
    // Admin, Teacher, Student each decide their own permissions.
    // This is abstraction: we define WHAT must happen, not HOW.
    @Override
    public abstract boolean can(String action);

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