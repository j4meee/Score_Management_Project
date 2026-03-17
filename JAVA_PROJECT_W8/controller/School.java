package controller;

import java.util.ArrayList;

import filter.StaffFilter;
import model.Course;
import model.Enrollment;
import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;

public class School {

    // ==================== ACTION CONSTANTS ====================
    public static final String CREATE_TEACHER          = "CREATE_TEACHER";
    public static final String CREATE_STUDENT          = "CREATE_STUDENT";
    public static final String CREATE_COURSE           = "CREATE_COURSE";
    public static final String SET_COURSE_AVAILABILITY = "SET_COURSE_AVAILABILITY";
    public static final String CREATE_ENROLLMENT       = "CREATE_ENROLLMENT";
    public static final String VIEW_TEACHERS           = "VIEW_TEACHERS";
    public static final String VIEW_STUDENTS           = "VIEW_STUDENTS";
    public static final String VIEW_COURSES            = "VIEW_COURSES";
    public static final String VIEW_ENROLLMENTS        = "VIEW_ENROLLMENTS";
    public static final String GRADE_STUDENT           = "GRADE_STUDENT";
    public static final String DELETE_TEACHER          = "DELETE_TEACHER";
    public static final String DELETE_STUDENT          = "DELETE_STUDENT";
    public static final String DELETE_COURSE           = "DELETE_COURSE";
    public static final String UPDATE_COURSE           = "UPDATE_COURSE";
    public static final String VIEW_GRADES             = "VIEW_GRADES";
    public static final String VIEW_OWN_GRADES         = "VIEW_OWN_GRADES";
    public static final String VIEW_OWN_ENROLLMENTS    = "VIEW_OWN_ENROLLMENTS";
    public static final String GENERATE_REPORT         = "GENERATE_REPORT";

    // ==================== FIELDS ====================
    private String schoolName;
    private String address;

    private ArrayList<Person> users;
    private ArrayList<Course> courses;
    private ArrayList<Enrollment> enrollments;

    private Person loggedInUser;
    private String lastMessage;

    // ==================== CONSTRUCTOR ====================
    public School(String schoolName, String address) {
        setSchoolName(schoolName);
        setAddress(address);

        this.users       = new ArrayList<>();
        this.courses     = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.loggedInUser = null;

        seedDefaultAdmin();

        this.lastMessage = "School created. Default admin: admin / 1234";
    }

    @Override
    public String toString() {
        return "School [schoolName=" + schoolName + ", address=" + address +
               ", users=" + users.size() + ", courses=" + courses.size() +
               ", enrollments=" + enrollments.size() + ", loggedInUser=" + loggedInUser + "]";
    }

    // ==================== GETTERS / SETTERS ====================
    public String getSchoolName()   { return schoolName; }
    public String getAddress()      { return address; }
    public String getLastMessage()  { return lastMessage; }
    public boolean isUserLoggedIn() { return loggedInUser != null; }
    public Person getLoggedInUser() { return loggedInUser; }

    public void setSchoolName(String schoolName) {
        this.schoolName = isBlank(schoolName) ? "School" : schoolName.trim();
    }

    public void setAddress(String address) {
        this.address = isBlank(address) ? "Unknown" : address.trim();
    }

    private void setLastMessage(String msg) {
        this.lastMessage = msg;
    }

    // ==================== BOOTSTRAP ====================
    // FIX: Person is now abstract — we must use Admin directly, not new Person().
    private void seedDefaultAdmin() {
        users.add(new Admin("01234", "Default Admin", "admin", "1234"));
    }

    // ==================== PERMISSION GUARDS ====================
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

    // Polymorphism in action: School calls can() without knowing the real role.
    // Java dispatches to the correct overridden can() at runtime (dynamic dispatch).
    private boolean requirePermission(String action) {
        if (loggedInUser == null) {
            setLastMessage("Please login first.");
            return false;
        }
        if (!loggedInUser.can(action)) {
            setLastMessage("Action denied: " + loggedInUser.getClass().getSimpleName() +
                           " cannot " + action);
            return false;
        }
        return true;
    }

    // ==================== LOGIN / LOGOUT ====================
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
                setLastMessage("Login successful: " +
                               user.getClass().getSimpleName() + " - " + user.getFullName());
                return;
            }
        }

        setLastMessage("Login failed: username not found.");
    }

    public void logout() {
        loggedInUser = null;
        setLastMessage("Logged out successfully.");
    }

    // ==================== TEACHER ====================
    public void createTeacher(String teacherId, String fullName,
                               String username, String password, String department) {
        if (!requireLogin() || !requirePermission(CREATE_TEACHER)) return;

        if (isBlank(teacherId) || isBlank(username)) {
            setLastMessage("Cannot create teacher: teacherId/username is empty.");
            return;
        }

        for (Person user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                setLastMessage("Cannot create teacher: username already exists.");
                return;
            }
        }

        // FIX: Teacher now takes plain fields, not a Person wrapper.
        users.add(new Teacher(teacherId, fullName, username, password, department));
        setLastMessage("Teacher created successfully.");
    }

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

    // ==================== STUDENT ====================
    public void createStudent(String studentId, String fullName,
                               String username, String password, String major) {
        if (!requireLogin() || !requirePermission(CREATE_STUDENT)) return;

        if (isBlank(studentId) || isBlank(username)) {
            setLastMessage("Cannot create student: studentId/username is empty.");
            return;
        }

        for (Person user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                setLastMessage("Cannot create student: username already exists.");
                return;
            }
        }

        // FIX: Student now takes plain fields, not a Person wrapper.
        users.add(new Student(studentId, fullName, username, password, major, loggedInUser));
        setLastMessage("Student created successfully.");
    }

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

    // ==================== COURSE ====================
    public void createCourse(String courseId, String title, String code,
                              int credits, String department, boolean isAvailable) {
        if (!requireLogin() || !requirePermission(CREATE_COURSE)) return;

        if (isBlank(courseId) || isBlank(code)) {
            setLastMessage("Cannot create course: courseId/code is empty.");
            return;
        }

        for (Course course : courses) {
            if (course.getCourseId().equalsIgnoreCase(courseId.trim())) {
                setLastMessage("Cannot create course: course ID already exists.");
                return;
            }
        }

        courses.add(new Course(courseId, title, code, credits, department, isAvailable));
        setLastMessage("Course created successfully.");
    }

    public void updateCourse(String courseId, String title, String code,
                              int credits, String department) {
        if (!requireLogin() || !requirePermission(UPDATE_COURSE)) return;

        for (Course course : courses) {
            if (course.getCourseId().equalsIgnoreCase(courseId)) {
                if (!isBlank(title))      course.setTitle(title);
                if (!isBlank(code))       course.setCode(code);
                if (credits > 0)          course.setCredits(credits);
                if (!isBlank(department)) course.setDepartment(department);
                setLastMessage("Course updated successfully.");
                return;
            }
        }
        setLastMessage("Course not found.");
    }

    public void deleteCourse(String courseId) {
        if (!requireLogin() || !requirePermission(DELETE_COURSE)) return;

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equalsIgnoreCase(courseId)) {
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

    // ==================== ENROLLMENT ====================
    public void createEnrollment(String studentId, String courseId,
                                  String semester, int year) {
        if (!requireLogin() || !requirePermission(CREATE_ENROLLMENT)) return;

        Student student = null;
        for (Person user : users) {
            if (user instanceof Student && user.getId().equalsIgnoreCase(studentId)) {
                student = (Student) user;
                break;
            }
        }
        if (student == null) { setLastMessage("Student not found."); return; }

        Course course = null;
        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(courseId)) {
                course = c;
                break;
            }
        }
        if (course == null) { setLastMessage("Course not found."); return; }

        if (!course.isAvailable()) {
            setLastMessage("Course is not available for enrollment.");
            return;
        }

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().equals(student) &&
                enrollment.getCourse().equals(course)   &&
                enrollment.getSemester().equalsIgnoreCase(semester) &&
                enrollment.getYear() == year) {
                setLastMessage("Student already enrolled in this course for this semester.");
                return;
            }
        }

        String enrollmentId = "EN" + (enrollments.size() + 1);
        enrollments.add(new Enrollment(enrollmentId, student, course, semester, year, loggedInUser));
        setLastMessage("Enrollment created successfully. ID: " + enrollmentId);
    }

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

    // ==================== PRINT / VIEW METHODS ====================

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

    // No instanceof cast needed — match by ID only.
    // can(VIEW_OWN_ENROLLMENTS) already ensures only Students reach this point.
    public void printOwnEnrollments() {
        if (!requireLogin() || !requirePermission(VIEW_OWN_ENROLLMENTS)) return;

        System.out.println("\n--- MY ENROLLMENTS ---");
        boolean found = false;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().getId().equals(loggedInUser.getId())) {
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

    // No instanceof cast needed — match by ID only.
    public void printOwnGrades() {
        if (!requireLogin() || !requirePermission(VIEW_OWN_GRADES)) return;

        System.out.println("\n--- MY GRADES ---");
        boolean found = false;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().getId().equals(loggedInUser.getId())
                    && enrollment.getScore() > 0) {
                System.out.println(enrollment);
                found = true;
            }
        }
        if (!found) System.out.println("No grades recorded yet.");
    }

    // ==================== PDF SECTION 9-10: ANONYMOUS INNER CLASS DEMO ====================
    // Demonstrates filtering without a separate named class file.
    public void demonstrateAnonymousInnerClass() {
        System.out.println("\n--- Anonymous Inner Class: filter active users ---");

        // PDF Section 9: Anonymous inner class — no class name, created at the point of use.
        StaffFilter activeFilter = new StaffFilter() {
            @Override
            public boolean test(Person staff) {
                return staff != null && staff.isActive();
            }
        };

        for (Person user : users) {
            if (activeFilter.test(user)) {
                System.out.println("  Active: " + user.getFullName() +
                                   " (" + user.getClass().getSimpleName() + ")");
            }
        }
    }

    // ==================== PDF SECTION 13-15: LAMBDA EXPRESSION DEMO ====================
    // Converts the anonymous class above into a lambda — much shorter.
    public void demonstrateLambdaExpression() {
        System.out.println("\n--- Lambda Expression: same filter, less code ---");

        // PDF Section 13: Lambda replaces the entire anonymous class.
        // Syntax: (parameter) -> expression
        StaffFilter activeFilter = staff -> staff != null && staff.isActive();

        // PDF Section 15: Lambdas work naturally with streams.
        // activeFilter::test wraps our StaffFilter into what stream().filter() expects.
        System.out.println("Active users via stream + lambda:");
        users.stream()
             .filter(activeFilter::test)
             .forEach(staff -> System.out.println(
                 "  " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
             ));
    }

    // ==================== HELPER ====================
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}