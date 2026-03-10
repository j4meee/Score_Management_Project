package user;

public class Admin extends Person {
    
    // CHANGED: Now takes a Person object instead of individual parameters
    public Admin(Person person) {
        super(person.getId(), person.getFullName(), person.getUsername(), person.getPassword());
    }

    @Override
    public boolean can(String action) {
        // Admin can do everything
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", username='" + getUsername() + '\'' +
                '}';
    }
}