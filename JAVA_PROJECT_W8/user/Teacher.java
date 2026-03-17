package user;

import controller.School;

// PDF Section 5: Subclass implementing the abstract method differently.
// Teacher.can() returns true only for view and grade actions.
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

    // PDF Section 5: Different implementation of the abstract method.
    // Teacher can view students, courses, grades, and grade students — nothing else.
    @Override
    public boolean can(String action) {
        if (action == null) return false;
        return action.equals(School.VIEW_STUDENTS) ||
               action.equals(School.VIEW_COURSES)  ||
               action.equals(School.VIEW_GRADES)   ||
               action.equals(School.GRADE_STUDENT);
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