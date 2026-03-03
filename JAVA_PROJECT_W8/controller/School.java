package controller;
import java.util.ArrayList;

import model.Course;
import model.Enrollment;
import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;

public class School {
    
    // Action constants
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
        Admin admin = new Admin("A001", "System Admin", "admin", "1234");
        users.add(admin);
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

    private boolean requirePermission(String action) {
        if (loggedInUser == null) {
            setLastMessage("Please login first.");
            return false;
        }
        if (!loggedInUser.can(action)) {
            setLastMessage("Action denied: " + loggedInUser.getRole() + " cannot " + action);
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
                setLastMessage("Login successful: " + user.getRole() + " - " + user.getFullName());
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

        Teacher newTeacher = new Teacher(teacherId, fullName, username, password, department);
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

        Student newStudent = new Student(studentId, fullName, username, password, major, loggedInUser);
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

    // =========================
    // CREATE COURSE (Admin and Teacher)
    // =========================
    public void createCourse(String courseId, String title, String code, 
                             int credits, String department, boolean available) {
        if (!requireLogin() || !requirePermission(CREATE_COURSE)) return;

        if (isBlank(courseId)) {
            setLastMessage("Cannot create course: courseId is empty.");
            return;
        }

        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(courseId.trim())) {
                setLastMessage("Cannot create course: courseId already exists.");
                return;
            }
        }

        Course newCourse = new Course(courseId, title, code, credits, department, available, loggedInUser);
        courses.add(newCourse);
        setLastMessage("Course created successfully.");
    }

    // =========================
    // UPDATE COURSE (Admin and Teacher)
    // =========================
    public void updateCourse(String courseId, String title, String code, int credits, String department) {
        if (!requireLogin() || !requirePermission(UPDATE_COURSE)) return;

        Course course = findCourseById(courseId);
        if (course == null) {
            setLastMessage("Course not found.");
            return;
        }

        if (!isBlank(title)) course.setTitle(title);
        if (!isBlank(code)) course.setCode(code);
        if (credits > 0) course.setCredits(credits);
        if (!isBlank(department)) course.setDepartment(department);
        
        setLastMessage("Course updated successfully.");
    }

    // =========================
    // DELETE COURSE (Admin only)
    // =========================
    public void deleteCourse(String courseId) {
        if (!requireLogin() || !requirePermission(DELETE_COURSE)) return;

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equalsIgnoreCase(courseId)) {
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

        Course course = findCourseById(courseId);
        if (course == null) {
            setLastMessage("Course not found.");
            return;
        }

        course.setAvailable(available);
        String status = available ? "available" : "unavailable";
        setLastMessage("Course is now " + status);
    }

    // =========================
    // CREATE ENROLLMENT
    // =========================
    public void createEnrollment(String studentId, String courseId, 
                                 String semester, int year) {
        if (!requireLogin() || !requirePermission(CREATE_ENROLLMENT)) return;

        if (isBlank(studentId) || isBlank(courseId)) {
            setLastMessage("Cannot create enrollment: invalid input.");
            return;
        }

        Student student = findStudentById(studentId);
        if (student == null) {
            setLastMessage("Cannot create enrollment: student not found.");
            return;
        }

        Course course = findCourseById(courseId);
        if (course == null) {
            setLastMessage("Cannot create enrollment: course not found.");
            return;
        }
        if (!course.isAvailable()) {
            setLastMessage("Cannot create enrollment: course is not available.");
            return;
        }

        // Check if already enrolled
        for (Enrollment e : enrollments) {
            if (e.getStudent().getId().equals(studentId) && 
                e.getCourse().getCourseId().equals(courseId) &&
                e.getSemester().equals(semester) && e.getYear() == year) {
                setLastMessage("Cannot create enrollment: student already enrolled.");
                return;
            }
        }

        String enrollmentId = "ENR" + (enrollments.size() + 1);
        Enrollment newEnrollment = new Enrollment(enrollmentId, student, course, semester, year, loggedInUser);
        enrollments.add(newEnrollment);
        setLastMessage("Enrollment created successfully: " + enrollmentId);
    }

    // =========================
    // GRADE STUDENT
    // =========================
    public void gradeStudent(String enrollmentId, double score) {
        if (!requireLogin() || !requirePermission(GRADE_STUDENT)) return;

        Enrollment enrollment = findEnrollmentById(enrollmentId);
        if (enrollment == null) {
            setLastMessage("Enrollment not found.");
            return;
        }

        enrollment.setScore(score);
        setLastMessage("Score updated successfully for " + enrollment.getStudent().getFullName() + 
                      " in " + enrollment.getCourse().getCode() + ": " + score + 
                      " (" + enrollment.getLetterGrade() + ")");
    }

    // =========================
    // VIEW METHODS
    // =========================
    public void viewTeachers() {
        if (!requireLogin() || !requirePermission(VIEW_TEACHERS)) {
            System.out.println(getLastMessage());
            return;
        }
        System.out.println("\n--- Teachers ---");
        boolean found = false;
        for (Person user : users) {
            if (user instanceof Teacher) {
                System.out.println("  " + user.toString());
                found = true;
            }
        }
        if (!found) System.out.println("  No teachers found.");
    }

    public void viewStudents() {
        if (!requireLogin() || !requirePermission(VIEW_STUDENTS)) {
            System.out.println(getLastMessage());
            return;
        }
        System.out.println("\n--- Students ---");
        boolean found = false;
        for (Person user : users) {
            if (user instanceof Student) {
                System.out.println("  " + user.toString());
                found = true;
            }
        }
        if (!found) System.out.println("  No students found.");
    }

    public void viewCourses() {
        if (!requireLogin() || !requirePermission(VIEW_COURSES)) {
            System.out.println(getLastMessage());
            return;
        }
        System.out.println("\n--- Courses (" + courses.size() + ") ---");
        if (courses.size() == 0) {
            System.out.println("  No courses.");
        } else {
            for (int i = 0; i < courses.size(); i++) {
                System.out.println("  " + (i + 1) + ") " + courses.get(i).toString());
            }
        }
    }

    public void viewEnrollments() {
        if (!requireLogin() || !requirePermission(VIEW_ENROLLMENTS)) {
            System.out.println(getLastMessage());
            return;
        }
        System.out.println("\n--- All Enrollments (" + enrollments.size() + ") ---");
        if (enrollments.size() == 0) {
            System.out.println("  No enrollments.");
        } else {
            for (int i = 0; i < enrollments.size(); i++) {
                System.out.println("  " + (i + 1) + ") " + enrollments.get(i).toString());
            }
        }
    }

    public void viewOwnEnrollments() {
        if (!requireLogin() || !requirePermission(VIEW_OWN_ENROLLMENTS)) {
            System.out.println(getLastMessage());
            return;
        }
        
        if (!(loggedInUser instanceof Student)) {
            System.out.println("Only students can view their own enrollments.");
            return;
        }
        
        Student currentStudent = (Student) loggedInUser;
        System.out.println("\n--- My Enrollments ---");
        boolean found = false;
        for (Enrollment e : enrollments) {
            if (e.getStudent().getId().equals(currentStudent.getId())) {
                System.out.println("  " + e.toString());
                found = true;
            }
        }
        if (!found) System.out.println("  No enrollments found.");
    }

    public void viewGrades() {
        if (!requireLogin() || !requirePermission(VIEW_GRADES)) {
            System.out.println(getLastMessage());
            return;
        }
        
        System.out.println("\n--- All Grades ---");
        if (enrollments.size() == 0) {
            System.out.println("  No grades available.");
            return;
        }
        for (Enrollment e : enrollments) {
            System.out.println("  " + e.getStudent().getFullName() + " - " + 
                              e.getCourse().getCode() + ": " + 
                              e.getScore() + " (" + e.getLetterGrade() + ")");
        }
    }

    public void viewOwnGrades() {
        if (!requireLogin() || !requirePermission(VIEW_OWN_GRADES)) {
            System.out.println(getLastMessage());
            return;
        }
        
        if (!(loggedInUser instanceof Student)) {
            System.out.println("Only students can view their own grades.");
            return;
        }
        
        Student currentStudent = (Student) loggedInUser;
        System.out.println("\n--- My Grades ---");
        boolean found = false;
        for (Enrollment e : enrollments) {
            if (e.getStudent().getId().equals(currentStudent.getId())) {
                System.out.println("  " + e.getCourse().getCode() + " - " + 
                                  e.getCourse().getTitle() + ": " + 
                                  e.getScore() + " (" + e.getLetterGrade() + ")");
                found = true;
            }
        }
        if (!found) System.out.println("  No grades found.");
    }

    // =========================
    // GENERATE REPORT (Admin only)
    // =========================
    public void generateReport() {
        if (!requireLogin() || !requirePermission(GENERATE_REPORT)) return;
        
        System.out.println("\n=== SCHOOL SYSTEM REPORT ===");
        System.out.println("School: " + schoolName);
        System.out.println("Address: " + address);
        System.out.println("Total Users: " + users.size());
        
        int adminCount = 0, teacherCount = 0, studentCount = 0;
        for (Person user : users) {
            if (user instanceof Admin) adminCount++;
            else if (user instanceof Teacher) teacherCount++;
            else if (user instanceof Student) studentCount++;
        }
        
        System.out.println("  - Admins: " + adminCount);
        System.out.println("  - Teachers: " + teacherCount);
        System.out.println("  - Students: " + studentCount);
        System.out.println("Total Courses: " + courses.size());
        System.out.println("Total Enrollments: " + enrollments.size());
        
        double totalScore = 0;
        int gradedCount = 0;
        for (Enrollment e : enrollments) {
            if (e.getScore() > 0) {
                totalScore += e.getScore();
                gradedCount++;
            }
        }
        if (gradedCount > 0) {
            System.out.println("Average Grade: " + (totalScore / gradedCount));
        } else {
            System.out.println("Average Grade: N/A (no grades recorded)");
        }
    }

    // =========================
    // FIND HELPERS
    // =========================
    private Student findStudentById(String studentId) {
        if (isBlank(studentId)) return null;
        for (Person user : users) {
            if (user instanceof Student) {
                Student s = (Student) user;
                if (s.getId().equalsIgnoreCase(studentId.trim())) {
                    return s;
                }
            }
        }
        return null;
    }

    private Course findCourseById(String courseId) {
        if (isBlank(courseId)) return null;
        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(courseId.trim())) {
                return c;
            }
        }
        return null;
    }

    private Enrollment findEnrollmentById(String enrollmentId) {
        if (isBlank(enrollmentId)) return null;
        for (Enrollment e : enrollments) {
            if (e.getEnrollmentId().equalsIgnoreCase(enrollmentId.trim())) {
                return e;
            }
        }
        return null;
    }

    // =========================
    // HELPER
    // =========================
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}