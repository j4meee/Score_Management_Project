package user;
public abstract class Person implements IPerson {
    
    private String id;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private boolean active;

    public Person(String id, String fullName, String username, String password, String role) {
        setId(id);
        setFullName(fullName);
        setUsername(username);
        setPassword(password);
        setRole(role);
        this.active = true;
    }

    // ===== Getters =====
    @Override
    public String getId() { return id; }
    @Override
    public String getFullName() { return fullName; }
    @Override
    public String getUsername() { return username; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getRole() { return role; }
    public boolean isActive() { return active; }

    // ===== Setters with validation =====
    public void setId(String id) {
        if (isBlank(id)) this.id = "UNKNOWN";
        else this.id = id.trim();
    }

    public void setFullName(String fullName) {
        if (isBlank(fullName)) this.fullName = "No Name";
        else this.fullName = fullName.trim();
    }

    public void setUsername(String username) {
        if (isBlank(username)) this.username = "user_" + this.id;
        else this.username = username.trim();
    }

    public void setPassword(String password) {
        String pw = (password == null) ? "" : password;
        if (pw.length() < 4) this.password = "0000";
        else this.password = pw;
    }

    public void setRole(String role) {
        if (isBlank(role)) this.role = "Unknown";
        else this.role = role.trim();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ===== Helper methods =====
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    protected boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ===== equals =====
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Person person = (Person) obj;
        
        if (!id.equals(person.id)) return false;
        if (!username.equals(person.username)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}