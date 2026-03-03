package model;
import user.IPerson;


public class Course {
    
    private String courseId;
    private String title;
    private String code;
    private int credits;
    private String department;
    private boolean available;
    private IPerson createdBy;

    public Course(String courseId, String title, String code, int credits,
                    String department, boolean available, IPerson createdBy) {
        setCourseId(courseId);
        setTitle(title);
        setCode(code);
        setCredits(credits);
        setDepartment(department);
        setAvailable(available);
        setCreatedBy(createdBy);
    }

    // ===== Getters =====
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getCode() { return code; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public boolean isAvailable() { return available; }
    public IPerson getCreatedBy() { return createdBy; }

    // ===== Setters =====
    public void setCourseId(String courseId) {
        if (isBlank(courseId)) this.courseId = "UNKNOWN";
        else this.courseId = courseId.trim();
    }

    public void setTitle(String title) {
        if (isBlank(title)) this.title = "No Title";
        else this.title = title.trim();
    }

    public void setCode(String code) {
        if (isBlank(code)) this.code = "XXX000";
        else this.code = code.trim().toUpperCase();
    }

    public void setCredits(int credits) {
        if (credits < 1 || credits > 6) this.credits = 3;
        else this.credits = credits;
    }

    public void setDepartment(String department) {
        if (isBlank(department)) this.department = "Unknown";
        else this.department = department.trim();
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCreatedBy(IPerson createdBy) {
        if (createdBy == null) {
            System.out.println("Cannot create course: Creator is required.");
        } else {
            this.createdBy = createdBy;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Course course = (Course) obj;
        
        if (!courseId.equals(course.courseId)) return false;
        if (!code.equals(course.code)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        String creator = (createdBy == null) ? "System" : createdBy.getFullName();
        return code + " - " + title + " (" + credits + " credits) " + 
                (available ? "Available" : "Unavailable") + 
                " created by " + creator;
    }
}