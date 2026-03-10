package model;

public class Course {
    private String courseId;
    private String title;
    private String code;
    private int credits;
    private String department;
    private boolean isAvailable;

    public Course(String courseId, String title, String code, 
                  int credits, String department, boolean isAvailable) {
        setCourseId(courseId);
        setTitle(title);
        setCode(code);
        setCredits(credits);
        setDepartment(department);
        setAvailable(isAvailable);
    }

    // Getters
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getCode() { return code; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public boolean isAvailable() { return isAvailable; }

    // Setters
    public void setCourseId(String courseId) {
        this.courseId = isBlank(courseId) ? "UNKNOWN" : courseId.trim();
    }

    public void setTitle(String title) {
        this.title = isBlank(title) ? "No Title" : title.trim();
    }

    public void setCode(String code) {
        this.code = isBlank(code) ? "UNKNOWN" : code.trim();
    }

    public void setCredits(int credits) {
        this.credits = (credits < 1 || credits > 6) ? 3 : credits;
    }

    public void setDepartment(String department) {
        this.department = isBlank(department) ? "General" : department.trim();
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseId.equals(course.courseId);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + courseId + '\'' +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", credits=" + credits +
                ", dept='" + department + '\'' +
                ", available=" + isAvailable +
                '}';
    }
}