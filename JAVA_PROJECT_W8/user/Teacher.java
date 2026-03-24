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
        
        // Teachers can view student information
        if (action.equals(School.VIEW_STUDENTS)) return true;
        
        // Teachers can view all courses
        if (action.equals(School.VIEW_COURSES)) return true;
        
        // Teachers can view all enrollments
        if (action.equals(School.VIEW_ENROLLMENTS)) return true;
        
        // Teachers can view all grades
        if (action.equals(School.VIEW_GRADES)) return true;
        
        // Teachers can grade students (MOST IMPORTANT)
        if (action.equals(School.GRADE_STUDENT)) return true;
        
        // Teachers cannot create, delete, or modify users/courses
        return false;
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