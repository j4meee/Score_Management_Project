package controller;

import java.util.ArrayList;
import java.util.Scanner;

import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static School school = new School("CADT University", "Phnom Penh");

    public static void main(String[] args) {

        setupSampleData();

        int choice = 0;

        do {
            if (!school.isUserLoggedIn()) {
                showMainMenu();
                choice = getIntInput("Choose: ");

                switch (choice) {
                    case 1: handleLogin();          break;
                    case 2: school.printCourses();  break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: System.out.println("Invalid choice.");
                }
            } else {
                showMainLoggedInMenu();
                choice = getIntInput("Choose: ");

                switch (choice) {
                    case 1: showManagementMenu(); break;
                    case 2: showViewMenu();       break;
                    case 3: handleLogout();       break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: System.out.println("Invalid choice.");
                }
            }
        } while (choice != 0);

        sc.close();
    }

    // ==================== MAIN MENUS ====================

    private static void showMainMenu() {
        System.out.println("\n+----------------------------------------+");
        System.out.println("|         CADT UNIVERSITY SYSTEM        |");
        System.out.println("+----------------------------------------+");
        System.out.println("|           MAIN MENU (Not Logged In)   |");
        System.out.println("+----------------------------------------+");
        System.out.println("| 1) Login                               |");
        System.out.println("| 2) View Courses (Public)               |");
        System.out.println("| 0) Exit                                |");
        System.out.println("+----------------------------------------+");
    }

    private static void showMainLoggedInMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { System.out.println("Error: Not logged in."); return; }

        System.out.println("\n+----------------------------------------+");
        System.out.println("|         CADT UNIVERSITY SYSTEM        |");
        System.out.println("+----------------------------------------+");
        System.out.println("| Logged In: " + padRight(user.getClass().getSimpleName() + " - " + user.getFullName(), 30) + " |");
        System.out.println("+----------------------------------------+");
        System.out.println("|              MAIN MENU                 |");
        System.out.println("+----------------------------------------+");
        if (hasAnyManagementPermission(user)) System.out.println("| 1) Management Menu                     |");
        if (hasAnyViewPermission(user))       System.out.println("| 2) View Menu                           |");
        System.out.println("| 3) Logout                              |");
        System.out.println("| 0) Exit                                |");
        System.out.println("+----------------------------------------+");
    }

    // ==================== PERMISSION HELPERS ====================
    // Polymorphism: user.can(action) dispatches to the correct role's can() at runtime.
    // Main never checks instanceof — each role decides its own permissions.

    private static boolean hasAnyManagementPermission(Person user) {
        return user.can(School.CREATE_TEACHER)          ||
               user.can(School.DELETE_TEACHER)          ||
               user.can(School.CREATE_STUDENT)          ||
               user.can(School.DELETE_STUDENT)          ||
               user.can(School.CREATE_COURSE)           ||
               user.can(School.UPDATE_COURSE)           ||
               user.can(School.DELETE_COURSE)           ||
               user.can(School.SET_COURSE_AVAILABILITY) ||
               user.can(School.CREATE_ENROLLMENT)       ||
               user.can(School.GRADE_STUDENT);
    }

    private static boolean hasAnyViewPermission(Person user) {
        return user.can(School.VIEW_TEACHERS)        ||
               user.can(School.VIEW_STUDENTS)        ||
               user.can(School.VIEW_COURSES)         ||
               user.can(School.VIEW_ENROLLMENTS)     ||
               user.can(School.VIEW_OWN_ENROLLMENTS) ||
               user.can(School.VIEW_GRADES)          ||
               user.can(School.VIEW_OWN_GRADES);
    }

    // ==================== MANAGEMENT MENU ====================
    // Build allowed actions list ONCE using can() — then use that same list
    // to print the menu AND to dispatch the chosen action. No duplicate can() calls.

    private static void showManagementMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { System.out.println("You are not logged in."); return; }

        int choice;

        do {
            // Build allowed actions ONCE — can() called here only
            ArrayList<String> allowedActions = new ArrayList<>();
            if (user.can(School.CREATE_TEACHER))          allowedActions.add(School.CREATE_TEACHER);
            if (user.can(School.DELETE_TEACHER))          allowedActions.add(School.DELETE_TEACHER);
            if (user.can(School.CREATE_STUDENT))          allowedActions.add(School.CREATE_STUDENT);
            if (user.can(School.DELETE_STUDENT))          allowedActions.add(School.DELETE_STUDENT);
            if (user.can(School.CREATE_COURSE))           allowedActions.add(School.CREATE_COURSE);
            if (user.can(School.UPDATE_COURSE))           allowedActions.add(School.UPDATE_COURSE);
            if (user.can(School.DELETE_COURSE))           allowedActions.add(School.DELETE_COURSE);
            if (user.can(School.SET_COURSE_AVAILABILITY)) allowedActions.add(School.SET_COURSE_AVAILABILITY);
            if (user.can(School.CREATE_ENROLLMENT))       allowedActions.add(School.CREATE_ENROLLMENT);
            if (user.can(School.GRADE_STUDENT))           allowedActions.add(School.GRADE_STUDENT);

            // Print menu from list — no second can() call
            System.out.println("\n+----------------------------------------+");
            System.out.println("|           MANAGEMENT MENU              |");
            System.out.println("+----------------------------------------+");
            for (int i = 0; i < allowedActions.size(); i++) {
                System.out.println("| " + padLeft((i + 1) + ") " + formatAction(allowedActions.get(i)), 38) + " |");
            }
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");

            choice = getIntInput("Choose: ");

            // Dispatch from list — no second can() call
            if (choice >= 1 && choice <= allowedActions.size()) {
                handleManagementAction(allowedActions.get(choice - 1));
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }

            user = school.getLoggedInUser();

        } while (choice != 0 && user != null);

        if (user == null) System.out.println("You have been logged out.");
        else              System.out.println("Returning to Main Menu...");
    }

    private static void handleManagementAction(String action) {
        switch (action) {
            case School.CREATE_TEACHER:          handleCreateTeacher();         break;
            case School.DELETE_TEACHER:          handleDeleteTeacher();         break;
            case School.CREATE_STUDENT:          handleCreateStudent();         break;
            case School.DELETE_STUDENT:          handleDeleteStudent();         break;
            case School.CREATE_COURSE:           handleCreateCourse();          break;
            case School.UPDATE_COURSE:           handleUpdateCourse();          break;
            case School.DELETE_COURSE:           handleDeleteCourse();          break;
            case School.SET_COURSE_AVAILABILITY: handleSetCourseAvailability(); break;
            case School.CREATE_ENROLLMENT:       handleCreateEnrollment();      break;
            case School.GRADE_STUDENT:           handleGradeStudent();          break;
        }
    }

    // ==================== VIEW MENU ====================

    private static void showViewMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { System.out.println("You are not logged in."); return; }

        int choice;

        do {
            // Build allowed actions ONCE — can() called here only
            ArrayList<String> allowedActions = new ArrayList<>();
            if (user.can(School.VIEW_TEACHERS))        allowedActions.add(School.VIEW_TEACHERS);
            if (user.can(School.VIEW_STUDENTS))        allowedActions.add(School.VIEW_STUDENTS);
            if (user.can(School.VIEW_COURSES))         allowedActions.add(School.VIEW_COURSES);
            if (user.can(School.VIEW_ENROLLMENTS))     allowedActions.add(School.VIEW_ENROLLMENTS);
            if (user.can(School.VIEW_OWN_ENROLLMENTS)) allowedActions.add(School.VIEW_OWN_ENROLLMENTS);
            if (user.can(School.VIEW_GRADES))          allowedActions.add(School.VIEW_GRADES);
            if (user.can(School.VIEW_OWN_GRADES))      allowedActions.add(School.VIEW_OWN_GRADES);

            // Print menu from list — no second can() call
            System.out.println("\n+----------------------------------------+");
            System.out.println("|              VIEW MENU                 |");
            System.out.println("+----------------------------------------+");
            for (int i = 0; i < allowedActions.size(); i++) {
                System.out.println("| " + padLeft((i + 1) + ") " + formatAction(allowedActions.get(i)), 38) + " |");
            }
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");

            choice = getIntInput("Choose: ");

            // Dispatch from list — no second can() call
            if (choice >= 1 && choice <= allowedActions.size()) {
                handleViewAction(allowedActions.get(choice - 1));
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }

            user = school.getLoggedInUser();

        } while (choice != 0 && user != null);

        if (user == null) System.out.println("You have been logged out.");
    }

    private static void handleViewAction(String action) {
        switch (action) {
            case School.VIEW_TEACHERS:        school.printTeachers();        break;
            case School.VIEW_STUDENTS:        school.printStudents();        break;
            case School.VIEW_COURSES:         school.printCourses();         break;
            case School.VIEW_ENROLLMENTS:     school.printEnrollments();     break;
            case School.VIEW_OWN_ENROLLMENTS: school.printOwnEnrollments();  break;
            case School.VIEW_GRADES:          school.printGrades();          break;
            case School.VIEW_OWN_GRADES:      school.printOwnGrades();       break;
        }
    }

    // ==================== FORMAT ACTION NAME FOR DISPLAY ====================

    private static String formatAction(String action) {
        switch (action) {
            case School.CREATE_TEACHER:          return "Create Teacher";
            case School.DELETE_TEACHER:          return "Delete Teacher";
            case School.CREATE_STUDENT:          return "Create Student";
            case School.DELETE_STUDENT:          return "Delete Student";
            case School.CREATE_COURSE:           return "Create Course";
            case School.UPDATE_COURSE:           return "Update Course";
            case School.DELETE_COURSE:           return "Delete Course";
            case School.SET_COURSE_AVAILABILITY: return "Set Course Availability";
            case School.CREATE_ENROLLMENT:       return "Create Enrollment";
            case School.GRADE_STUDENT:           return "Grade Student";
            case School.VIEW_TEACHERS:           return "View Teachers";
            case School.VIEW_STUDENTS:           return "View Students";
            case School.VIEW_COURSES:            return "View Courses";
            case School.VIEW_ENROLLMENTS:        return "View All Enrollments";
            case School.VIEW_OWN_ENROLLMENTS:    return "View My Enrollments";
            case School.VIEW_GRADES:             return "View All Grades";
            case School.VIEW_OWN_GRADES:         return "View My Grades";
            default:                             return action;
        }
    }

    // ==================== PADDING HELPERS ====================

    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    private static String padLeft(String text, int length) {
        return String.format("%" + length + "s", text);
    }

    // ==================== SAMPLE DATA + POLYMORPHISM DEMO ====================

    private static void setupSampleData() {
        System.out.println("Setting up sample data...");
        school.login("admin", "1234");
        school.createTeacher("T001", "Dr. Smith", "smith", "pass123", "Math");
        school.createStudent("S001", "John Doe", "john", "student123", "CS");
        school.createCourse("C001", "Linear Algebra", "MATH101", 4, "Math", true);
        school.createEnrollment("S001", "C001", "Fall", 2024);
        school.logout();

        System.out.println("\nSample data created successfully!");
        System.out.println("Default admin: admin / 1234\n");

        // ===================================================
        // POLYMORPHISM DEMO (PDF Section 5)
        // Step A: one list, different roles
        // Step B: same can() call, different results
        // ===================================================
        System.out.println("========================================");
        System.out.println("   POLYMORPHISM DEMO");
        System.out.println("   Same can() call, different results");
        System.out.println("========================================");

        ArrayList<Person> staffs = new ArrayList<>();
        staffs.add(new Admin(new Person("A01", "Admin User",   "adminDemo",   "1234")));
        staffs.add(new Teacher(new Person("T02", "Teacher User", "teacherDemo", "1234"), "Science"));
        staffs.add(new Student(new Person("S03", "Student User", "studentDemo", "1234"), "CS", null));

        String[] actions = {
            School.CREATE_TEACHER,
            School.GRADE_STUDENT,
            School.VIEW_OWN_GRADES
        };

        // Same line s.can(action) — Java picks the right can() at runtime (dynamic dispatch)
        for (Person s : staffs) {
            System.out.println("\n[ " + s.getClass().getSimpleName() + " ] " + s.getUsername());
            for (String action : actions) {
                System.out.println("  " + s.getUsername() + " can " + action + "? " + s.can(action));
            }
        }

        System.out.println("\n=> Java chose the right can() at runtime (dynamic dispatch).");
        System.out.println("=> School never checked instanceof — each role decided its own permissions.");
        System.out.println("========================================\n");
    }

    // ==================== INPUT HANDLING ====================

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next();
            System.out.print(prompt);
        }
        int input = sc.nextInt();
        sc.nextLine();
        return input;
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next();
            System.out.print(prompt);
        }
        double input = sc.nextDouble();
        sc.nextLine();
        return input;
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static boolean getBooleanInput(String prompt) {
        System.out.print(prompt + " (1=Yes, 0=No): ");
        int input = getIntInput("");
        return input == 1;
    }

    // ==================== HANDLER METHODS ====================

    private static void handleLogin() {
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        school.login(username, password);
        System.out.println(school.getLastMessage());
    }

    private static void handleLogout() {
        school.logout();
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateTeacher() {
        System.out.println("\n--- Create New Teacher ---");
        String id       = getStringInput("Teacher ID: ");
        String name     = getStringInput("Full Name: ");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        String dept     = getStringInput("Department: ");
        school.createTeacher(id, name, username, password, dept);
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteTeacher() {
        System.out.println("\n--- Delete Teacher ---");
        String id = getStringInput("Teacher ID to delete: ");
        school.deleteTeacher(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateStudent() {
        System.out.println("\n--- Create New Student ---");
        String id       = getStringInput("Student ID: ");
        String name     = getStringInput("Full Name: ");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        String major    = getStringInput("Major: ");
        school.createStudent(id, name, username, password, major);
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteStudent() {
        System.out.println("\n--- Delete Student ---");
        String id = getStringInput("Student ID to delete: ");
        school.deleteStudent(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateCourse() {
        System.out.println("\n--- Create New Course ---");
        String id         = getStringInput("Course ID: ");
        String title      = getStringInput("Title: ");
        String code       = getStringInput("Code: ");
        int credits       = getIntInput("Credits: ");
        String dept       = getStringInput("Department: ");
        boolean available = getBooleanInput("Available?");
        school.createCourse(id, title, code, credits, dept, available);
        System.out.println(school.getLastMessage());
    }

    private static void handleUpdateCourse() {
        System.out.println("\n--- Update Course ---");
        String id    = getStringInput("Course ID: ");
        String title = getStringInput("New Title (press Enter to skip): ");
        String code  = getStringInput("New Code (press Enter to skip): ");

        int credits = -1;
        System.out.print("New Credits (enter 0 to skip): ");
        if (sc.hasNextInt()) { credits = sc.nextInt(); sc.nextLine(); }
        else                 { sc.nextLine(); }

        String dept = getStringInput("New Department (press Enter to skip): ");
        school.updateCourse(id, title, code, credits, dept);
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteCourse() {
        System.out.println("\n--- Delete Course ---");
        String id = getStringInput("Course ID to delete: ");
        school.deleteCourse(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleSetCourseAvailability() {
        System.out.println("\n--- Set Course Availability ---");
        String id         = getStringInput("Course ID: ");
        boolean available = getBooleanInput("Available?");
        school.setCourseAvailability(id, available);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateEnrollment() {
        System.out.println("\n--- Create New Enrollment ---");
        String studentId = getStringInput("Student ID: ");
        String courseId  = getStringInput("Course ID: ");
        String semester  = getStringInput("Semester (Fall/Spring/Summer): ");
        int year         = getIntInput("Year: ");
        school.createEnrollment(studentId, courseId, semester, year);
        System.out.println(school.getLastMessage());
    }

    private static void handleGradeStudent() {
        System.out.println("\n--- Grade Student ---");
        String enrollId = getStringInput("Enrollment ID: ");
        double score    = getDoubleInput("Score (0-100): ");
        school.gradeStudent(enrollId, score);
        System.out.println(school.getLastMessage());
    }
}