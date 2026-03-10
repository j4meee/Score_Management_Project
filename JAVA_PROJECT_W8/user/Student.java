package user;

import controller.School;

// Student OVERRIDES can() — can only view own data.
public class Student extends Person {

    private String major;
    private Person createdBy;

    public Student(Person person, String major, Person createdBy) {
        super(person.getId(), person.getFullName(), person.getUsername(), person.getPassword());
        setMajor(major);
        this.createdBy = createdBy;
    }

    public Student(Person person, Person createdBy) {
        this(person, "Undeclared", createdBy);
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
        return super.toString() + " Student{" +
                "major='" + major + '\'' +
                ", createdBy='" + ((createdBy == null) ? "System" : createdBy.getFullName()) + '\'' +
                '}';
    }
}