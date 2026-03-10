package user;

import controller.School;

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

    public String getMajor() { return major; }
    public String getStudentId() { return getId(); }
    public IPerson getCreatedBy() { return createdBy; }

    public void setMajor(String major) {
        if (isBlank(major)) this.major = "Undeclared";
        else this.major = major.trim();
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;
        
            return action.equals(School.VIEW_COURSES) ||
                action.equals(School.VIEW_OWN_ENROLLMENTS) ||
                action.equals(School.VIEW_OWN_GRADES);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        
        Student student = (Student) obj;
        
        if (!major.equals(student.major)) return false;
        
        return true;
    }
    @Override
    public String toString() {
        return super.toString() + " Student{" +
                "major='" + major + '\'' +
                ", createdBy='" + ((createdBy == null) ? "System" : createdBy.getFullName()) + '\'' +
                '}';
    }

    // @Override
    // public String toString() {
    //     String creator = (createdBy == null) ? "System" : createdBy.getFullName();
    //     return "Student{" +
    //             "id='" + getId() + '\'' +
    //             ", fullName='" + getFullName() + '\'' +
    //             ", major='" + major + '\'' +
    //             ", createdBy='" + creator + '\'' +
    //             '}';
    // }
}