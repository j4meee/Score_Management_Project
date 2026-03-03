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
        this.score = 0.0;
    }

    // ===== Getters =====
    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public double getScore() { return score; }
    public IPerson getEnrolledBy() { return enrolledBy; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }

    // ===== Setters =====
    public void setEnrollmentId(String enrollmentId) {
        if (isBlank(enrollmentId)) this.enrollmentId = "UNKNOWN";
        else this.enrollmentId = enrollmentId.trim();
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setSemester(String semester) {
        if (isBlank(semester)) this.semester = "Fall";
        else this.semester = semester.trim();
    }

    public void setYear(int year) {
        if (year < 2000 || year > 2100) this.year = 2024;
        else this.year = year;
    }

    public void setEnrolledBy(IPerson enrolledBy) {
        this.enrolledBy = enrolledBy;
    }

    public void setScore(double score) {
        if (score >= 0 && score <= 100) {
            this.score = score;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String getLetterGrade() {
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
        
        Enrollment enrollment = (Enrollment) obj;
        
        if (!enrollmentId.equals(enrollment.enrollmentId)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        String studentName = (student == null) ? "UNKNOWN" : student.getFullName();
        String courseCode = (course == null) ? "UNKNOWN" : course.getCode();
        String enrolledByName = (enrolledBy == null) ? "System" : enrolledBy.getFullName();

        return enrollmentId + ": " + studentName + " enrolled in " + courseCode +
               " (" + semester + " " + year + ") Score: " + score + " (" + getLetterGrade() + ")" +
               " Enrolled by: " + enrolledByName;
    }
}