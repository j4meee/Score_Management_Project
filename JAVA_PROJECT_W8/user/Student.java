package user;

import controller.School;

// Student can:
// - View all courses (public information)
// - View their own enrollments only
// - View their own grades only
// - Cannot view other students' information
// - Cannot grade anyone
public class Student extends Person {

    private String major;
    private IPerson createdBy;

    public Student(String id, String fullName, String username, String password,
                   String major, IPerson createdBy) {
        super(id, fullName, username, password);
        setMajor(major);
        this.createdBy = createdBy;
    }

    public Student(String id, String fullName, String username, String password, IPerson createdBy) {
        this(id, fullName, username, password, "Undeclared", createdBy);
    }

    public String getMajor()      { return major; }
    public String getStudentId()  { return getId(); }
    public IPerson getCreatedBy() { return createdBy; }

    public void setMajor(String major) {
        this.major = isBlank(major) ? "Undeclared" : major.trim();
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;
        
        // Students can view all courses (public information)
        if (action.equals(School.VIEW_COURSES)) return true;
        
        // Students can view ONLY their own enrollments
        if (action.equals(School.VIEW_OWN_ENROLLMENTS)) return true;
        
        // Students can view ONLY their own grades
        if (action.equals(School.VIEW_OWN_GRADES)) return true;
        
        // Students CANNOT view other students' data
        if (action.equals(School.VIEW_STUDENTS)) return false;
        
        // Students CANNOT view all enrollments
        if (action.equals(School.VIEW_ENROLLMENTS)) return false;
        
        // Students CANNOT view all grades
        if (action.equals(School.VIEW_GRADES)) return false;
        
        // Students CANNOT grade anyone
        if (action.equals(School.GRADE_STUDENT)) return false;
        
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Student student = (Student) obj;
        return major.equals(student.major);
    }

    @Override
    public String toString() {
        return super.toString().replace("Person{", "Student{") +
               ", major='" + major + '\'' +
               ", createdBy='" + (createdBy == null ? "System" : createdBy.getFullName()) + "'}";
    }
}