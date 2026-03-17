package user;

import controller.School;

// PDF Section 5: Subclass implementing the abstract method differently.
// Student.can() returns true only for viewing their own data.
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

    // PDF Section 5: Different implementation of the abstract method.
    // Student can only view courses and their own enrollments/grades.
    @Override
    public boolean can(String action) {
        if (action == null) return false;
        return action.equals(School.VIEW_COURSES)         ||
               action.equals(School.VIEW_OWN_ENROLLMENTS) ||
               action.equals(School.VIEW_OWN_GRADES);
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