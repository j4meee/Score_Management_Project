package model;

import user.IPerson;
import user.Student;

public class Enrollment {

    private String enrollmentId;
    private Student student;
    private Course course;
    private double score;
    private IPerson enrolledBy;
    private String semester;
    private int year;

    public Enrollment(String enrollmentId, Student student, Course course,
                      String semester, int year, IPerson enrolledBy) {
        setEnrollmentId(enrollmentId);
        setStudent(student);
        setCourse(course);
        setSemester(semester);
        setYear(year);
        setEnrolledBy(enrolledBy);
        this.score = -1.0; // -1 means "not graded yet"
    }

    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent()     { return student; }
    public Course getCourse()       { return course; }
    public double getScore()        { return score; }
    public boolean isGraded()       { return score >= 0; }
    public IPerson getEnrolledBy()  { return enrolledBy; }
    public String getSemester()     { return semester; }
    public int getYear()            { return year; }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = isBlank(enrollmentId) ? "UNKNOWN" : enrollmentId.trim();
    }

    public void setStudent(Student student)  { this.student    = student; }
    public void setCourse(Course course)     { this.course     = course; }
    public void setEnrolledBy(IPerson e)     { this.enrolledBy = e; }

    public void setSemester(String semester) {
        this.semester = isBlank(semester) ? "Fall" : semester.trim();
    }

    public void setYear(int year) {
        this.year = (year < 2000 || year > 2100) ? 2024 : year;
    }

    public void setScore(double score) {
        if (score >= 0 && score <= 100) this.score = score;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String getLetterGrade() {
        if (score < 0) return "N/A";
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return enrollmentId.equals(((Enrollment) obj).enrollmentId);
    }

    @Override
    public String toString() {
        String studentName    = (student    == null) ? "UNKNOWN" : student.getFullName();
        String courseCode     = (course     == null) ? "UNKNOWN" : course.getCode();
        String enrolledByName = (enrolledBy == null) ? "System"  : enrolledBy.getFullName();

        String scoreText = isGraded() ? String.valueOf(score) : "N/A";
        return enrollmentId + ": " + studentName + " enrolled in " + courseCode +
               " (" + semester + " " + year + ") Score: " + scoreText +
               " (" + getLetterGrade() + ") Enrolled by: " + enrolledByName;
    }
}
