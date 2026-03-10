package user;

import controller.School;

// Teacher OVERRIDES can() — can view and grade, not manage users.
public class Teacher extends Person {

    private String department;

    public Teacher(Person person, String department) {
        super(person.getId(), person.getFullName(), person.getUsername(), person.getPassword());
        setDepartment(department);
    }

    public Teacher(Person person) {
        this(person, "Unknown");
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) {
        this.department = isBlank(department) ? "Unknown" : department.trim();
    }

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
        return super.toString() + ", dept='" + this.department + "'}";
    }
}