package user;

// PDF Section 5: Subclass implementing the abstract method.
// Admin.can() always returns true — Admin can do everything.
// Variable type may be Person, but runtime type is Admin.
// Java calls Admin.can() at runtime — this is dynamic dispatch (polymorphism).
public class Admin extends Person {

    public Admin(String id, String fullName, String username, String password) {
        super(id, fullName, username, password);
    }

    // PDF Section 5: Concrete implementation of the abstract method.
    @Override
    public boolean can(String action) {
        return true; // Admin can do everything
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