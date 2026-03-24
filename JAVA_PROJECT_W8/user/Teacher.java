package user;

import controller.School;

// Teacher can:
// - View all students in their courses
// - View all courses
// - View all enrollments
// - View and assign grades
// - Cannot create/delete users or courses (only Admin can)
public class Teacher extends Person {

    private String department;

    public Teacher(String id, String fullName, String username, String password, String department) {
        super(id, fullName, username, password);
        setDepartment(department);
    }

    public Teacher(String id, String fullName, String username, String password) {
        this(id, fullName, username, password, "Unknown");
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) {
        this.department = isBlank(department) ? "Unknown" : department.trim();
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;

        switch (action) {
            case School.VIEW_STUDENTS:
            case School.VIEW_TEACHERS:
            case School.VIEW_COURSES:
            case School.VIEW_ENROLLMENTS:
            case School.VIEW_GRADES:
            case School.GRADE_STUDENT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Teacher teacher = (Teacher) obj;
        return department.equals(teacher.department);
    }

    @Override
    public String toString() {
        return super.toString().replace("Person{", "Teacher{") +
               ", dept='" + department + "'}";
    }
}
