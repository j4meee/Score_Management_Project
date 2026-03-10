package user;

// Admin OVERRIDES can() — returns true for everything.
// Variable type may be Person, but runtime type is Admin.
// Java calls Admin.can() at runtime — dynamic dispatch.
public class Admin extends Person {

    public Admin(Person person) {
        super(person.getId(), person.getFullName(), person.getUsername(), person.getPassword());
    }

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
        return super.toString() + "}";
    }
}