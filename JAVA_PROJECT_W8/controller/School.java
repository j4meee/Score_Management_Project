package controller;

import java.util.ArrayList;

import filter.StaffFilter;
import model.Course;
import model.Enrollment;
import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;
import exception.*;

public class School {

    // ==================== ACTION CONSTANTS ====================
    // Admin Only Actions
    public static final String CREATE_TEACHER          = "CREATE_TEACHER";
    public static final String DELETE_TEACHER          = "DELETE_TEACHER";
    public static final String CREATE_STUDENT          = "CREATE_STUDENT";
    public static final String DELETE_STUDENT          = "DELETE_STUDENT";
    public static final String CREATE_COURSE           = "CREATE_COURSE";
    public static final String UPDATE_COURSE           = "UPDATE_COURSE";
    public static final String DELETE_COURSE           = "DELETE_COURSE";
    public static final String SET_COURSE_AVAILABILITY = "SET_COURSE_AVAILABILITY";
    public static final String CREATE_ENROLLMENT       = "CREATE_ENROLLMENT";
    
    // Teacher & Admin Actions
    public static final String GRADE_STUDENT           = "GRADE_STUDENT";
    public static final String VIEW_ENROLLMENTS        = "VIEW_ENROLLMENTS";
    public static final String VIEW_GRADES             = "VIEW_GRADES";
    public static final String VIEW_STUDENTS           = "VIEW_STUDENTS";
    public static final String VIEW_TEACHERS           = "VIEW_TEACHERS";
    
    // Public Actions (Everyone can view)
    public static final String VIEW_COURSES            = "VIEW_COURSES";
    
    // Student Only Actions
    public static final String VIEW_OWN_ENROLLMENTS    = "VIEW_OWN_ENROLLMENTS";
    public static final String VIEW_OWN_GRADES         = "VIEW_OWN_GRADES";

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
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.loggedInUser = null;
        seedDefaultAdmin();
        this.lastMessage = "School created with a default admin account.";
    }

    // ==================== GETTERS / SETTERS ====================
    public String getSchoolName()   { return schoolName; }
    public String getAddress()      { return address; }
    public String getLastMessage()  { return lastMessage; }
    public boolean isUserLoggedIn() { return loggedInUser != null; }
    public Person getLoggedInUser() { return loggedInUser; }

    public void setSchoolName(String schoolName) {
        if (isBlank(schoolName)) {
            throw new IllegalArgumentException("School name cannot be empty");
        }
        this.schoolName = schoolName.trim();
    }

    public void setAddress(String address) {
        if (isBlank(address)) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address.trim();
    }

    private void setLastMessage(String msg) {
        this.lastMessage = msg;
    }

    // ==================== BOOTSTRAP ====================
    private void seedDefaultAdmin() {
        users.add(new Admin("01234", "Default Admin", "admin", "1234"));
    }

    // ==================== PERMISSION GUARDS ====================
    private void checkLogin() throws UnauthorizedActionException {
        if (loggedInUser == null) {
            throw new UnauthorizedActionException("Please login first.");
        }
        if (!loggedInUser.isActive()) {
            loggedInUser = null;
            throw new UnauthorizedActionException("User is inactive. Auto-logout performed.");
        }
    }

    private void checkPermission(String action) throws UnauthorizedActionException {
        checkLogin();
        if (!loggedInUser.can(action)) {
            throw new UnauthorizedActionException(action, loggedInUser.getClass().getSimpleName());
        }
    }

    // ==================== LOGIN / LOGOUT ====================
    public void login(String username, String password) {
        try {
            if (isBlank(username)) {
                throw new ValidationException("username", "cannot be empty");
            }
            if (password == null) {
                throw new ValidationException("password", "cannot be null");
            }

            for (Person user : users) {
                if (user.getUsername().equalsIgnoreCase(username.trim())) {
                    if (!user.isActive()) {
                        throw new UnauthorizedActionException("User account is inactive.");
                    }
                    if (!user.checkPassword(password)) {
                        throw new UnauthorizedActionException("Invalid password.");
                    }
                    loggedInUser = user;
                    setLastMessage("Login successful: " +
                                   user.getClass().getSimpleName() + " - " + user.getFullName());
                    return;
                }
            }
            throw new EntityNotFoundException("User", username);
        } catch (ValidationException | UnauthorizedActionException | EntityNotFoundException e) {
            setLastMessage("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        loggedInUser = null;
        setLastMessage("Logged out successfully.");
    }

    // ==================== TEACHER (Admin Only) ====================
    public void createTeacher(String teacherId, String fullName,
                               String username, String password, String department) {
        try {
            checkPermission(CREATE_TEACHER);
            
            if (isBlank(teacherId)) {
                throw new ValidationException("teacherId", "cannot be empty");
            }
            if (isBlank(username)) {
                throw new ValidationException("username", "cannot be empty");
            }

            for (Person user : users) {
                if (user.getUsername().equalsIgnoreCase(username.trim())) {
                    throw new DuplicateEntityException("Teacher", "username: " + username);
                }
            }

            users.add(new Teacher(teacherId, fullName, username, password, department));
            setLastMessage("Teacher created successfully.");
        } catch (UnauthorizedActionException | ValidationException | DuplicateEntityException e) {
            setLastMessage("Cannot create teacher: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            setLastMessage("Cannot create teacher: " + e.getMessage());
        }
    }

    public void deleteTeacher(String teacherId) {
        try {
            checkPermission(DELETE_TEACHER);
            
            for (int i = 0; i < users.size(); i++) {
                Person user = users.get(i);
                if (user instanceof Teacher && user.getId().equalsIgnoreCase(teacherId)) {
                    users.remove(i);
                    setLastMessage("Teacher deleted successfully.");
                    return;
                }
            }
            throw new EntityNotFoundException("Teacher", teacherId);
        } catch (UnauthorizedActionException | EntityNotFoundException e) {
            setLastMessage("Cannot delete teacher: " + e.getMessage());
        }
    }

    // ==================== STUDENT (Admin Only) ====================
    public void createStudent(String studentId, String fullName,
                               String username, String password, String major) {
        try {
            checkPermission(CREATE_STUDENT);
            
            if (isBlank(studentId)) {
                throw new ValidationException("studentId", "cannot be empty");
            }
            if (isBlank(username)) {
                throw new ValidationException("username", "cannot be empty");
            }

            for (Person user : users) {
                if (user.getUsername().equalsIgnoreCase(username.trim())) {
                    throw new DuplicateEntityException("Student", "username: " + username);
                }
            }

            users.add(new Student(studentId, fullName, username, password, major, loggedInUser));
            setLastMessage("Student created successfully.");
        } catch (UnauthorizedActionException | ValidationException | DuplicateEntityException e) {
            setLastMessage("Cannot create student: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            setLastMessage("Cannot create student: " + e.getMessage());
        }
    }

    public void deleteStudent(String studentId) {
        try {
            checkPermission(DELETE_STUDENT);
            
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStudent().getId().equalsIgnoreCase(studentId)) {
                    throw new ValidationException("delete", 
                        "Student has existing enrollments. Cannot delete.");
                }
            }
            
            for (int i = 0; i < users.size(); i++) {
                Person user = users.get(i);
                if (user instanceof Student && user.getId().equalsIgnoreCase(studentId)) {
                    users.remove(i);
                    setLastMessage("Student deleted successfully.");
                    return;
                }
            }
            throw new EntityNotFoundException("Student", studentId);
        } catch (UnauthorizedActionException | EntityNotFoundException | ValidationException e) {
            setLastMessage("Cannot delete student: " + e.getMessage());
        }
    }

    // ==================== COURSE (Admin Only) ====================
    public void createCourse(String courseId, String title, String code,
                              int credits, String department, boolean isAvailable) {
        try {
            checkPermission(CREATE_COURSE);
            
            if (isBlank(courseId)) {
                throw new ValidationException("courseId", "cannot be empty");
            }
            if (isBlank(code)) {
                throw new ValidationException("code", "cannot be empty");
            }
            
            if (credits < 1 || credits > 6) {
                throw new ValidationException("credits", "must be between 1 and 6");
            }

            for (Course course : courses) {
                if (course.getCourseId().equalsIgnoreCase(courseId.trim())) {
                    throw new DuplicateEntityException("Course", "ID: " + courseId);
                }
            }

            courses.add(new Course(courseId, title, code, credits, department, isAvailable));
            setLastMessage("Course created successfully.");
        } catch (UnauthorizedActionException | ValidationException | DuplicateEntityException e) {
            setLastMessage("Cannot create course: " + e.getMessage());
        }
    }

    public void updateCourse(String courseId, String title, String code,
                              int credits, String department) {
        try {
            checkPermission(UPDATE_COURSE);
            
            for (Course course : courses) {
                if (course.getCourseId().equalsIgnoreCase(courseId)) {
                    if (!isBlank(title)) {
                        course.setTitle(title);
                    }
                    if (!isBlank(code)) {
                        course.setCode(code);
                    }
                    if (credits > 0) {
                        if (credits < 1 || credits > 6) {
                            throw new ValidationException("credits", "must be between 1 and 6");
                        }
                        course.setCredits(credits);
                    }
                    if (!isBlank(department)) {
                        course.setDepartment(department);
                    }
                    setLastMessage("Course updated successfully.");
                    return;
                }
            }
            throw new EntityNotFoundException("Course", courseId);
        } catch (UnauthorizedActionException | EntityNotFoundException | ValidationException e) {
            setLastMessage("Cannot update course: " + e.getMessage());
        }
    }

    public void deleteCourse(String courseId) {
        try {
            checkPermission(DELETE_COURSE);
            
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getCourse().getCourseId().equalsIgnoreCase(courseId)) {
                    throw new ValidationException("delete", 
                        "Cannot delete course: has existing enrollments.");
                }
            }
            
            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getCourseId().equalsIgnoreCase(courseId)) {
                    courses.remove(i);
                    setLastMessage("Course deleted successfully.");
                    return;
                }
            }
            throw new EntityNotFoundException("Course", courseId);
        } catch (UnauthorizedActionException | EntityNotFoundException | ValidationException e) {
            setLastMessage("Cannot delete course: " + e.getMessage());
        }
    }

    public void setCourseAvailability(String courseId, boolean available) {
        try {
            checkPermission(SET_COURSE_AVAILABILITY);
            
            for (Course course : courses) {
                if (course.getCourseId().equalsIgnoreCase(courseId)) {
                    course.setAvailable(available);
                    setLastMessage("Course availability updated.");
                    return;
                }
            }
            throw new EntityNotFoundException("Course", courseId);
        } catch (UnauthorizedActionException | EntityNotFoundException e) {
            setLastMessage("Cannot update course availability: " + e.getMessage());
        }
    }

    // ==================== ENROLLMENT (Admin Only) ====================
    public void createEnrollment(String studentId, String courseId,
                                  String semester, int year) {
        try {
            checkPermission(CREATE_ENROLLMENT);
            
            Student student = null;
            for (Person user : users) {
                if (user instanceof Student && user.getId().equalsIgnoreCase(studentId)) {
                    student = (Student) user;
                    break;
                }
            }
            if (student == null) {
                throw new EntityNotFoundException("Student", studentId);
            }

            Course course = null;
            for (Course c : courses) {
                if (c.getCourseId().equalsIgnoreCase(courseId)) {
                    course = c;
                    break;
                }
            }
            if (course == null) {
                throw new EntityNotFoundException("Course", courseId);
            }

            if (!course.isAvailable()) {
                throw new ValidationException("enrollment", 
                    "Course is not available for enrollment.");
            }
            
            if (year < 2000 || year > 2100) {
                throw new ValidationException("year", "must be between 2000 and 2100");
            }

            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStudent().equals(student) &&
                    enrollment.getCourse().equals(course) &&
                    enrollment.getSemester().equalsIgnoreCase(semester) &&
                    enrollment.getYear() == year) {
                    throw new DuplicateEntityException("Enrollment", 
                        student.getFullName() + " in " + course.getCode());
                }
            }

            String enrollmentId = "EN" + (enrollments.size() + 1);
            enrollments.add(new Enrollment(enrollmentId, student, course, semester, year, loggedInUser));
            setLastMessage("Enrollment created successfully. ID: " + enrollmentId);
        } catch (UnauthorizedActionException | EntityNotFoundException | 
                 ValidationException | DuplicateEntityException e) {
            setLastMessage("Cannot create enrollment: " + e.getMessage());
        }
    }

    // ==================== GRADE STUDENT (Teacher & Admin) ====================
    public boolean gradeStudent(String enrollmentId, double score) {
        try {
            checkPermission(GRADE_STUDENT);
            
            if (score < 0 || score > 100) {
                throw new InvalidScoreException(score);
            }
            
            String normalizedInput = normalizeEnrollmentId(enrollmentId);
            
            for (Enrollment enrollment : enrollments) {
                boolean matches = enrollmentId != null &&
                                  enrollment.getEnrollmentId().equalsIgnoreCase(enrollmentId);
                if (!matches && !normalizedInput.isEmpty()) {
                    matches = normalizedInput.equals(
                        normalizeEnrollmentId(enrollment.getEnrollmentId()));
                }
                if (matches) {
                    enrollment.setScore(score);
                    setLastMessage("Grade recorded successfully. Grade: " + 
                                   enrollment.getLetterGrade());
                    return true;
                }
            }
            throw new EntityNotFoundException("Enrollment", enrollmentId);
        } catch (UnauthorizedActionException | EntityNotFoundException | InvalidScoreException e) {
            setLastMessage("Cannot grade student: " + e.getMessage());
        }
        return false;
    }

    // ==================== VIEW METHODS ====================
    
    // View Teachers (Admin Only)
    public void printTeachers() {
        try {
            checkPermission(VIEW_TEACHERS); // Teacher & admin access
            System.out.println("\n--- TEACHERS LIST ---");
            boolean found = false;
            for (Person user : users) {
                if (user instanceof Teacher) {
                    System.out.println(user);
                    found = true;
                }
            }
            if (!found) System.out.println("No teachers found.");
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view teachers: " + e.getMessage());
        }
    }

    // View Students (Teacher & Admin)
    public void printStudents() {
        try {
            checkPermission(VIEW_STUDENTS);
            System.out.println("\n--- STUDENTS LIST ---");
            boolean found = false;
            for (Person user : users) {
                if (user instanceof Student) {
                    System.out.println(user);
                    found = true;
                }
            }
            if (!found) System.out.println("No students found.");
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view students: " + e.getMessage());
        }
    }

    // View Courses (Public - Everyone)
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

    // View All Enrollments (Teacher & Admin)
    public void printEnrollments() {
        try {
            checkPermission(VIEW_ENROLLMENTS);
            System.out.println("\n--- ALL ENROLLMENTS ---");
            if (enrollments.isEmpty()) {
                System.out.println("No enrollments found.");
                return;
            }
            for (Enrollment enrollment : enrollments) {
                System.out.println(enrollment);
            }
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view enrollments: " + e.getMessage());
        }
    }

    // View My Enrollments (Student Only)
    public void printOwnEnrollments() {
        try {
            checkPermission(VIEW_OWN_ENROLLMENTS);
            System.out.println("\n--- MY ENROLLMENTS ---");
            boolean found = false;
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStudent().getId().equals(loggedInUser.getId())) {
                    System.out.println(formatEnrollmentSummary(enrollment, false));
                    found = true;
                }
            }
            if (!found) System.out.println("You have no enrollments.");
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view enrollments: " + e.getMessage());
        }
    }

    // View All Grades (Teacher & Admin)
    public void printGrades() {
        try {
            checkPermission(VIEW_GRADES);
            System.out.println("\n--- ALL GRADES ---");
            boolean found = false;
            for (Enrollment enrollment : enrollments) {
                if (enrollment.isGraded()) {
                    System.out.println(enrollment);
                    found = true;
                }
            }
            if (!found) System.out.println("No grades recorded yet.");
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view grades: " + e.getMessage());
        }
    }

    // View My Grades (Student Only)
    public void printOwnGrades() {
        try {
            checkPermission(VIEW_OWN_GRADES);
            System.out.println("\n--- MY GRADES ---");
            boolean found = false;
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStudent().getId().equals(loggedInUser.getId())
                        && enrollment.isGraded()) {
                    System.out.println(enrollment);
                    found = true;
                }
            }
            if (!found) System.out.println("No grades recorded yet.");
        } catch (UnauthorizedActionException e) {
            System.out.println("Cannot view grades: " + e.getMessage());
        }
    }

    // ==================== DEMO METHODS ====================
    public void demonstrateAnonymousInnerClass() {
        System.out.println("\n--- Anonymous Inner Class: filter active users ---");
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

    public void demonstrateLambdaExpression() {
        System.out.println("\n--- Lambda Expression: same filter, less code ---");
        StaffFilter activeFilter = staff -> staff != null && staff.isActive();
        System.out.println("Active users via stream + lambda:");
        users.stream()
             .filter(activeFilter::test)
             .forEach(staff -> System.out.println(
                 "  " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
             ));
    }

    // ==================== EXCEPTION HANDLING DEMO ====================
    public void demonstrateExceptionHandling() {
        System.out.println("\n=== EXCEPTION HANDLING DEMO ===");
        System.out.println("PDF Section 10: Learning try-catch-finally, multiple catch, and custom exceptions");
        
        System.out.println("\n1. NumberFormatException Demo:");
        String[] invalidNumbers = {"abc", "10", "xyz", "50"};
        for (String num : invalidNumbers) {
            try {
                int value = Integer.parseInt(num);
                System.out.println("  ✓ Parsed: " + num + " → " + value);
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Cannot parse '" + num + "': " + e.getMessage());
            } finally {
                System.out.println("    → finally block executed");
            }
        }
        
        System.out.println("\n2. NullPointerException Demo:");
        String testString = null;
        try {
            @SuppressWarnings("null")
            int length = testString.length();
            System.out.println("  Length: " + length);
        } catch (NullPointerException e) {
            System.out.println("  ✗ Caught NullPointerException: " + e.getMessage());
        } finally {
            System.out.println("  → finally block always runs");
        }
        
        System.out.println("\n3. ArrayIndexOutOfBoundsException Demo:");
        int[] numbers = {1, 2, 3};
        for (int i = 0; i <= numbers.length; i++) {
            try {
                System.out.println("  numbers[" + i + "] = " + numbers[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("  ✗ Error at index " + i);
            }
        }
        
        System.out.println("\n4. Custom Exception Demo:");
        double[] testScores = {95, -10, 75, 101, 82};
        for (double score : testScores) {
            try {
                if (score < 0 || score > 100) {
                    throw new InvalidScoreException(score);
                }
                System.out.println("  ✓ Score " + score + " is valid");
            } catch (InvalidScoreException e) {
                System.out.println("  ✗ " + e.getMessage());
            }
        }
        
        System.out.println("\n5. Multiple Catch Blocks Demo:");
        String[] testInputs = {"42", "abc", "5"};
        for (String input : testInputs) {
            try {
                int value = Integer.parseInt(input);
                int[] arr = new int[3];
                System.out.println("  arr[" + value + "] = " + arr[value]);
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Not a number: " + input);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("  ✗ Index out of bounds: " + input);
            }
        }
        
        System.out.println("\n6. throw and throws Demo:");
        try {
            validateAge(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        
        try {
            validateAge(25);
            System.out.println("  ✓ Age 25 is valid");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        
        System.out.println("\n=== END EXCEPTION DEMO ===");
    }
    
    private void validateAge(int age) throws IllegalArgumentException {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
        if (age < 18) {
            throw new IllegalArgumentException("Age must be at least 18: " + age);
        }
    }

    private String normalizeEnrollmentId(String rawId) {
        if (rawId == null) {
            return "";
        }
        String digits = rawId.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "";
        }
        try {
            return String.valueOf(Integer.parseInt(digits));
        } catch (NumberFormatException e) {
            return digits;
        }
    }

    private String formatEnrollmentSummary(Enrollment enrollment, boolean includeScore) {
        if (enrollment == null || enrollment.getCourse() == null) {
            return "Unknown enrollment";
        }
        StringBuilder summary = new StringBuilder();
        summary.append(enrollment.getEnrollmentId())
               .append(": ")
               .append(enrollment.getCourse().getCode())
               .append(" - ").append(enrollment.getCourse().getTitle())
               .append(" (").append(enrollment.getSemester())
               .append(" ").append(enrollment.getYear()).append(")");
        if (includeScore) {
            if (enrollment.isGraded()) {
                summary.append(" Score: ").append(enrollment.getScore())
                       .append(" (").append(enrollment.getLetterGrade()).append(")");
            } else {
                summary.append(" Score: N/A (not graded)");
            }
        }
        return summary.toString();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
