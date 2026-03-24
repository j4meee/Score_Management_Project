package user;

// PDF Section 3: Abstraction — defining what a system must do
// PDF Section 4: Abstract Class — cannot be instantiated
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

    @Override 
    public String getId() { 
        return id; 
    }
    
    @Override 
    public String getFullName() { 
        return fullName; 
    }
    
    @Override 
    public String getUsername() { 
        return username; 
    }
    
    @Override 
    public String getPassword() { 
        return password; 
    }
    
    @Override 
    public boolean isActive() { 
        return active; 
    }

    public void setId(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.id = id.trim();
    }

    public void setFullName(String fullName) {
        if (isBlank(fullName)) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
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
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        this.password = password;
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

    // PDF Section 4-5: Abstract method — subclasses MUST implement this differently
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