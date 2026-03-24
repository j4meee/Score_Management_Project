package user;

// Admin has full system access - can do everything
public class Admin extends Person {

    public Admin(String id, String fullName, String username, String password) {
        super(id, fullName, username, password);
    }

    @Override
    public boolean can(String action) {
        // Admin can do EVERYTHING in the system
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString().replace("Person{", "Admin{");
    }
}