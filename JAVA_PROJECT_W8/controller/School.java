package controller;

import java.util.ArrayList;

import model.Course;
import model.Enrollment;
import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;


public class School {
    
    // Action constants (same as before)
    public static final String CREATE_TEACHER = "CREATE_TEACHER";
    public static final String CREATE_STUDENT = "CREATE_STUDENT";
    public static final String CREATE_COURSE = "CREATE_COURSE";
    public static final String SET_COURSE_AVAILABILITY = "SET_COURSE_AVAILABILITY";
    public static final String CREATE_ENROLLMENT = "CREATE_ENROLLMENT";
    public static final String VIEW_TEACHERS = "VIEW_TEACHERS";
    public static final String VIEW_STUDENTS = "VIEW_STUDENTS";
    public static final String VIEW_COURSES = "VIEW_COURSES";
    public static final String VIEW_ENROLLMENTS = "VIEW_ENROLLMENTS";
    public static final String GRADE_STUDENT = "GRADE_STUDENT";
    public static final String DELETE_TEACHER = "DELETE_TEACHER";
    public static final String DELETE_STUDENT = "DELETE_STUDENT";
    public static final String DELETE_COURSE = "DELETE_COURSE";
    public static final String UPDATE_COURSE = "UPDATE_COURSE";
    public static final String VIEW_GRADES = "VIEW_GRADES";
    public static final String VIEW_OWN_GRADES = "VIEW_OWN_GRADES";
    public static final String VIEW_OWN_ENROLLMENTS = "VIEW_OWN_ENROLLMENTS";
    public static final String GENERATE_REPORT = "GENERATE_REPORT";

    // =========================
    // 1) BASIC INFO
    // =========================
    private String schoolName;
    private String address;

    // =========================
    // 2) "TABLES" (ArrayList)
    // =========================
    private ArrayList<Person> users;
    private ArrayList<Course> courses;
    private ArrayList<Enrollment> enrollments;

    // =========================
    // 3) LOGIN DEPENDENCY
    // =========================
    private Person loggedInUser;

    // =========================
    // 4) FEEDBACK MESSAGE
    // =========================
    private String lastMessage;

    // =========================
    // CONSTRUCTOR
    // =========================
    public School(String schoolName, String address) {
        setSchoolName(schoolName);
        setAddress(address);

        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.loggedInUser = null;

        // Create default admin
        seedDefaultAdmin();

        this.lastMessage = "School created. Default admin: admin / 1234";
    }

    @Override
    public String toString() {
        return "School [schoolName=" + schoolName + ", address=" + address + 
               ", users=" + users.size() + ", courses=" + courses.size() + 
               ", enrollments=" + enrollments.size() + ", loggedInUser=" + loggedInUser + "]";
    }

    // =========================
    // GETTERS / SETTERS
    // =========================
    public String getSchoolName() { return schoolName; }
    public String getAddress() { return address; }
    public String getLastMessage() { return lastMessage; }
    public boolean isUserLoggedIn() { return loggedInUser != null; }
    public Person getLoggedInUser() { return loggedInUser; }

    public void setSchoolName(String schoolName) {
        if (isBlank(schoolName)) this.schoolName = "School";
        else this.schoolName = schoolName.trim();
    }

    public void setAddress(String address) {
        if (isBlank(address)) this.address = "Unknown";
        else this.address = address.trim();
    }

    private void setLastMessage(String msg) {
        this.lastMessage = msg;
    }

    // =========================
    // DEFAULT ADMIN (BOOTSTRAP)
    // =========================
    private void seedDefaultAdmin() {
        Person admin = new Person("01234", "Default Admin", "admin", "1234");
        Admin adminUser = new Admin(admin);
        users.add(adminUser);
    }

    // =========================
    // LOGIN CHECK
    // =========================
    private boolean requireLogin() {
        if (loggedInUser == null) {
            setLastMessage("Action denied: please login first.");
            return false;
        }

        if (!loggedInUser.isActive()) {
            loggedInUser = null;
            setLastMessage("Action denied: user is inactive (auto logout).");
            return false;
        }

        return true;
    }

    // FIXED: Use class name instead of getRole()
    private boolean requirePermission(String action) {
        if (loggedInUser == null) {
            setLastMessage("Please login first.");
            return false;
        }
        if (!loggedInUser.can(action)) {
            setLastMessage("Action denied: " + loggedInUser.getClass().getSimpleName() + " cannot " + action);
            return false;
        }
        return true;
    }

    // =========================
    // LOGIN / LOGOUT
    // =========================
    public void login(String username, String password) {
        if (isBlank(username) || password == null) {
            setLastMessage("Login failed: missing username/password.");
            return;
        }

        for (Person user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                
                if (!user.isActive()) {
                    setLastMessage("Login failed: user is inactive.");
                    return;
                }

                if (!user.checkPassword(password)) {
                    setLastMessage("Login failed: wrong password.");
                    return;
                }

                loggedInUser = user;
                // FIXED: Use class name instead of getRole()
                setLastMessage("Login successful: " + user.getClass().getSimpleName() + " - " + user.getFullName());
                return;
            }
        }

        setLastMessage("Login failed: username not found.");
    }

    public void logout() {
        loggedInUser = null;
        setLastMessage("Logged out successfully.");
    }

    // =========================
    // CREATE TEACHER (Admin only)
    // =========================
    public void createTeacher(String teacherId, String fullName, 
                                String username, String password, String department) {
        if (!requireLogin() || !requirePermission(CREATE_TEACHER)) return;

        if (isBlank(teacherId) || isBlank(username)) {
            setLastMessage("Cannot create teacher: teacherId/username is empty.");
            return;
        }

        // Check duplicate username
        for (Person user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                setLastMessage("Cannot create teacher: username already exists.");
                return;
            }
        }

        Teacher newTeacher = new Teacher(new Person(teacherId, fullName, username, password), department);
        users.add(newTeacher);
        setLastMessage("Teacher created successfully.");
    }

    // =========================
    // DELETE TEACHER (Admin only)
    // =========================
    public void deleteTeacher(String teacherId) {
        if (!requireLogin() || !requirePermission(DELETE_TEACHER)) return;

        for (int i = 0; i < users.size(); i++) {
            Person user = users.get(i);
            if (user instanceof Teacher && user.getId().equalsIgnoreCase(teacherId)) {
                users.remove(i);
                setLastMessage("Teacher deleted successfully.");
                return;
            }
        }
        setLastMessage("Teacher not found.");
    }

    // =========================
    // CREATE STUDENT (Admin and Teacher)
    // =========================
    public void createStudent(String studentId, String fullName, 
                                String username, String password, String major) {
        if (!requireLogin() || !requirePermission(CREATE_STUDENT)) return;

        if (isBlank(studentId) || isBlank(username)) {
            setLastMessage("Cannot create student: studentId/username is empty.");
            return;
        }

        // Check duplicate username
        for (Person user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                setLastMessage("Cannot create student: username already exists.");
                return;
            }
        }

        Student newStudent = new Student(new Person(studentId, fullName, username, password), major, loggedInUser);
        users.add(newStudent);
        setLastMessage("Student created successfully.");
    }

    // =========================
    // DELETE STUDENT (Admin only)
    // =========================
    public void deleteStudent(String studentId) {
        if (!requireLogin() || !requirePermission(DELETE_STUDENT)) return;

        for (int i = 0; i < users.size(); i++) {
            Person user = users.get(i);
            if (user instanceof Student && user.getId().equalsIgnoreCase(studentId)) {
                users.remove(i);
                setLastMessage("Student deleted successfully.");
                return;
            }
        }
        setLastMessage("Student not found.");
    }

    // Add these methods after deleteStudent() in School.java

// =========================
// CREATE COURSE
// =========================
public void createCourse(String courseId, String title, String code, 
                            int credits, String department, boolean isAvailable) {
    if (!requireLogin() || !requirePermission(CREATE_COURSE)) return;

    if (isBlank(courseId) || isBlank(code)) {
        setLastMessage("Cannot create course: courseId/code is empty.");
        return;
    }

    // Check duplicate course ID
    for (Course course : courses) {
        if (course.getCourseId().equalsIgnoreCase(courseId.trim())) {
            setLastMessage("Cannot create course: course ID already exists.");
            return;
        }
    }

    Course newCourse = new Course(courseId, title, code, credits, department, isAvailable);
    courses.add(newCourse);
    setLastMessage("Course created successfully.");
}

// =========================
// UPDATE COURSE
// =========================
public void updateCourse(String courseId, String title, String code, 
                            int credits, String department) {
    if (!requireLogin() || !requirePermission(UPDATE_COURSE)) return;

    for (Course course : courses) {
        if (course.getCourseId().equalsIgnoreCase(courseId)) {
            if (!isBlank(title)) course.setTitle(title);
            if (!isBlank(code)) course.setCode(code);
            if (credits > 0) course.setCredits(credits);
            if (!isBlank(department)) course.setDepartment(department);
            
            setLastMessage("Course updated successfully.");
            return;
        }
    }
    setLastMessage("Course not found.");
}

// =========================
// DELETE COURSE
// =========================
public void deleteCourse(String courseId) {
    if (!requireLogin() || !requirePermission(DELETE_COURSE)) return;

    for (int i = 0; i < courses.size(); i++) {
        if (courses.get(i).getCourseId().equalsIgnoreCase(courseId)) {
            // Check if course has enrollments
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getCourse().getCourseId().equalsIgnoreCase(courseId)) {
                    setLastMessage("Cannot delete course: has existing enrollments.");
                    return;
                }
            }
            courses.remove(i);
            setLastMessage("Course deleted successfully.");
            return;
        }
    }
    setLastMessage("Course not found.");
}

// =========================
// SET COURSE AVAILABILITY
// =========================
public void setCourseAvailability(String courseId, boolean available) {
    if (!requireLogin() || !requirePermission(SET_COURSE_AVAILABILITY)) return;

    for (Course course : courses) {
        if (course.getCourseId().equalsIgnoreCase(courseId)) {
            course.setAvailable(available);
            setLastMessage("Course availability updated.");
            return;
        }
    }
    setLastMessage("Course not found.");
}

// =========================
// CREATE ENROLLMENT
// =========================
public void createEnrollment(String studentId, String courseId, 
                             String semester, int year) {
    if (!requireLogin() || !requirePermission(CREATE_ENROLLMENT)) return;

    // Find student
    Student student = null;
    for (Person user : users) {
        if (user instanceof Student && user.getId().equalsIgnoreCase(studentId)) {
            student = (Student) user;
            break;
        }
    }
    if (student == null) {
        setLastMessage("Student not found.");
        return;
    }

    // Find course
    Course course = null;
    for (Course c : courses) {
        if (c.getCourseId().equalsIgnoreCase(courseId)) {
            course = c;
            break;
        }
    }
    if (course == null) {
        setLastMessage("Course not found.");
        return;
    }

    // Check if course is available
    if (!course.isAvailable()) {
        setLastMessage("Course is not available for enrollment.");
        return;
    }

    // Check for duplicate enrollment
    for (Enrollment enrollment : enrollments) {
        if (enrollment.getStudent().equals(student) && 
            enrollment.getCourse().equals(course) &&
            enrollment.getSemester().equalsIgnoreCase(semester) &&
            enrollment.getYear() == year) {
            setLastMessage("Student already enrolled in this course for this semester.");
            return;
        }
    }

    // Generate enrollment ID
    String enrollmentId = "EN" + (enrollments.size() + 1);
    
    Enrollment newEnrollment = new Enrollment(enrollmentId, student, course, 
                                              semester, year, loggedInUser);
    enrollments.add(newEnrollment);
    setLastMessage("Enrollment created successfully. ID: " + enrollmentId);
}

// =========================
// GRADE STUDENT
// =========================
public void gradeStudent(String enrollmentId, double score) {
    if (!requireLogin() || !requirePermission(GRADE_STUDENT)) return;

    for (Enrollment enrollment : enrollments) {
        if (enrollment.getEnrollmentId().equalsIgnoreCase(enrollmentId)) {
            enrollment.setScore(score);
            setLastMessage("Grade recorded successfully.");
            return;
        }
    }
    setLastMessage("Enrollment not found.");
}

// =========================
// PRINT METHODS (VIEW)
// =========================

public void printTeachers() {
    if (!requireLogin() || !requirePermission(VIEW_TEACHERS)) return;
    
    System.out.println("\n--- TEACHERS LIST ---");
    boolean found = false;
    for (Person user : users) {
        if (user instanceof Teacher) {
            System.out.println(user);
            found = true;
        }
    }
    if (!found) System.out.println("No teachers found.");
}

public void printStudents() {
    if (!requireLogin() || !requirePermission(VIEW_STUDENTS)) return;
    
    System.out.println("\n--- STUDENTS LIST ---");
    boolean found = false;
    for (Person user : users) {
        if (user instanceof Student) {
            System.out.println(user);
            found = true;
        }
    }
    if (!found) System.out.println("No students found.");
}

public void printCourses() {
    System.out.println("\n--- COURSES LIST ---");
    if (courses.isEmpty()) {
        System.out.println("No courses available.");
        return;
    }
    for (Course course : courses) {
        System.out.println(course);
    }
}

public void printEnrollments() {
    if (!requireLogin() || !requirePermission(VIEW_ENROLLMENTS)) return;
    
    System.out.println("\n--- ALL ENROLLMENTS ---");
    if (enrollments.isEmpty()) {
        System.out.println("No enrollments found.");
        return;
    }
    for (Enrollment enrollment : enrollments) {
        System.out.println(enrollment);
    }
}

public void printOwnEnrollments() {
    if (!requireLogin() || !requirePermission(VIEW_OWN_ENROLLMENTS)) return;
    
    if (!(loggedInUser instanceof Student)) {
        setLastMessage("Only students can view their own enrollments.");
        return;
    }
    
    Student student = (Student) loggedInUser;
    System.out.println("\n--- MY ENROLLMENTS ---");
    boolean found = false;
    for (Enrollment enrollment : enrollments) {
        if (enrollment.getStudent().equals(student)) {
            System.out.println(enrollment);
            found = true;
        }
    }
    if (!found) System.out.println("You have no enrollments.");
}

public void printGrades() {
    if (!requireLogin() || !requirePermission(VIEW_GRADES)) return;
    
    System.out.println("\n--- ALL GRADES ---");
    boolean found = false;
    for (Enrollment enrollment : enrollments) {
        if (enrollment.getScore() > 0) {
            System.out.println(enrollment);
            found = true;
        }
    }
    if (!found) System.out.println("No grades recorded yet.");
}

public void printOwnGrades() {
    if (!requireLogin() || !requirePermission(VIEW_OWN_GRADES)) return;
    
    if (!(loggedInUser instanceof Student)) {
        setLastMessage("Only students can view their own grades.");
        return;
    }
    
    Student student = (Student) loggedInUser;
    System.out.println("\n--- MY GRADES ---");
    boolean found = false;
    for (Enrollment enrollment : enrollments) {
        if (enrollment.getStudent().equals(student) && enrollment.getScore() > 0) {
            System.out.println(enrollment);
            found = true;
        }
    }
    if (!found) System.out.println("No grades recorded yet.");
}
    
    // =========================
    // HELPER
    // =========================
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}