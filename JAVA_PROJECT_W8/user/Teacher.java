package user;
import controller.School;

public class Teacher extends Person {
    
    private String department;

    public Teacher(String teacherId, String fullName, String username, 
                    String password, String department) {
        super(teacherId, fullName, username, password, "Teacher");
        setDepartment(department);
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) {
        if (isBlank(department)) this.department = "Unknown";
        else this.department = department.trim();
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;
        
            return action.equals(School.UPDATE_COURSE) ||
                    action.equals(School.VIEW_STUDENTS) ||
                    action.equals(School.VIEW_COURSES) ||
                    action.equals(School.VIEW_GRADES) ||
                    action.equals(School.GRADE_STUDENT);        
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        
        Teacher teacher = (Teacher) obj;
        
        if (!department.equals(teacher.department)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", dept='" + department + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}