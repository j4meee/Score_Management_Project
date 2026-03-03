package user;
public class Admin extends Person {
    
    public Admin(String adminId, String fullName, String username, String password) {
        super(adminId, fullName, username, password, "Admin");
    }

    @Override
    public boolean can(String action) {
        // Admin can do everything
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        return true; // No additional fields to compare
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}