package controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.Scanner;

import user.Admin;
import user.Person;
import user.Student;
import user.Teacher;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static School school = new School("CADT University", "Phnom Penh");
    private static Console console = System.console();

    public static void main(String[] args) {
        setupSampleData();
        
        // PDF Section 10: Demonstrate exception handling at startup
        // school.demonstrateExceptionHandling();

        int choice = -1;  // Initialize to a non-zero value

        do {
            if (!school.isUserLoggedIn()) {
                showMainMenu();
                choice = getIntInput("Choose: ");

                switch (choice) {
                    case 1: handleLogin();         break;
                    case 2: school.printCourses(); break;
                    case 3: demonstrateExceptionExamples(); break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: 
                        if (choice != -1) {  // Don't show error for initial state
                            System.out.println("Invalid choice. Please enter 0-3.");
                        }
                }
            } else {
                showMainLoggedInMenu();
                choice = getIntInput("Choose: ");

                switch (choice) {
                    case 1: 
                        if (hasAnyManagementPermission(school.getLoggedInUser())) {
                            showManagementMenu();
                        } else {
                            System.out.println("You don't have permission for Management Menu.");
                            waitForEnter();
                        }
                        break;
                    case 2: 
                        if (hasAnyViewPermission(school.getLoggedInUser())) {
                            showViewMenu();
                        } else {
                            System.out.println("You don't have permission for View Menu.");
                            waitForEnter();
                        }
                        break;
                    case 3: handleLogout();       break;
                    case 0: System.out.println("Goodbye!"); break;
                    default: 
                        System.out.println("Invalid choice. Please enter 0-3.");
                }
            }
        } while (choice != 0);

        sc.close();
    }
    
    // PDF Section 10: Exception examples with try-catch-finally
    private static void demonstrateExceptionExamples() {
        System.out.println("\n=== EXCEPTION HANDLING EXAMPLES ===");
        System.out.println("(Press Enter to skip each example)");
        
        // Example 1: NumberFormatException
        System.out.println("\n1. NumberFormatException Demo:");
        System.out.print("   Enter a number (or 'abc' to see exception, Enter to skip): ");
        String input = sc.nextLine();
        if (!input.isEmpty()) {
            try {
                int number = Integer.parseInt(input);
                System.out.println("   ✓ You entered: " + number);
            } catch (NumberFormatException e) {
                System.out.println("   ✗ Error: '" + input + "' is not a valid number!");
                System.out.println("   Exception message: " + e.getMessage());
            }
        } else {
            System.out.println("   Skipped.");
        }
        
        // Example 2: Multiple catch blocks
        System.out.println("\n2. Multiple Catch Blocks Demo:");
        System.out.print("   Enter an array index (0-4) or Enter to skip: ");
        input = sc.nextLine();
        if (!input.isEmpty()) {
            try {
                int index = Integer.parseInt(input);
                int[] array = {10, 20, 30, 40, 50};
                System.out.println("   Value at index " + index + ": " + array[index]);
            } catch (NumberFormatException e) {
                System.out.println("   ✗ Error: That's not a valid number!");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("   ✗ Error: Index out of range! Valid indices: 0-4");
            } catch (Exception e) {
                System.out.println("   ✗ Unexpected error: " + e.getMessage());
            }
        } else {
            System.out.println("   Skipped.");
        }
        
        // Example 3: throw and throws demo
        System.out.println("\n3. Custom Validation Demo:");
        System.out.print("   Enter a score (0-100) or Enter to skip: ");
        input = sc.nextLine();
        if (!input.isEmpty()) {
            try {
                double score = Double.parseDouble(input);
                validateScore(score);
                System.out.println("   ✓ Valid score: " + score);
            } catch (NumberFormatException e) {
                System.out.println("   ✗ Error: Please enter a valid number!");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("   Skipped.");
        }
        
        System.out.println("\n=== END EXCEPTION EXAMPLES ===");
        waitForEnter();
    }
    
    private static void validateScore(double score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative!");
        }
        if (score > 100) {
            throw new IllegalArgumentException("Score cannot exceed 100!");
        }
    }

    // ==================== MAIN MENUS ====================
    private static void showMainMenu() {
        System.out.println("\n+----------------------------------------+");
        System.out.println("|         CADT UNIVERSITY SYSTEM         |");
        System.out.println("+----------------------------------------+");
        System.out.println("|        MAIN MENU (Not Logged In)       |");
        System.out.println("+----------------------------------------+");
        System.out.println("| 1) Login                               |");
        System.out.println("| 2) View Courses (Public)               |");
        System.out.println("| 3) Exception Handling Examples        |");
        System.out.println("| 0) Exit                                |");
        System.out.println("+----------------------------------------+");
    }

    private static void showMainLoggedInMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { 
            System.out.println("Error: Not logged in."); 
            return; 
        }

        System.out.println("\n+----------------------------------------+");
        System.out.println("|         CADT UNIVERSITY SYSTEM         |");
        System.out.println("+----------------------------------------+");
        System.out.println("| Logged In: " + padRight(
            user.getClass().getSimpleName() + " - " + user.getFullName(), 30) + " |");
        System.out.println("+----------------------------------------+");
        System.out.println("|              MAIN MENU                 |");
        System.out.println("+----------------------------------------+");
        
        if (hasAnyManagementPermission(user)) {
            System.out.println("| 1) Management Menu (Create/Edit/Delete)     |");
        }
        
        if (hasAnyViewPermission(user)) {
            System.out.println("| 2) View Menu (View Information)             |");
        }
        
        System.out.println("| 3) Logout                              |");
        System.out.println("| 0) Exit                                |");
        System.out.println("+----------------------------------------+");
    }

    // ==================== PERMISSION HELPERS ====================
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
    private static void showManagementMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { 
            System.out.println("You are not logged in."); 
            return; 
        }

        int choice;

        do {
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

            if (allowedActions.isEmpty()) {
                System.out.println("\nYou don't have permission to access Management Menu.");
                return;
            }

            System.out.println("\n+----------------------------------------+");
            System.out.println("|           MANAGEMENT MENU              |");
            System.out.println("+----------------------------------------+");
            System.out.println("| Role: " + padRight(user.getClass().getSimpleName(), 32) + " |");
            System.out.println("+----------------------------------------+");
            
            for (int i = 0; i < allowedActions.size(); i++) {
                System.out.println("| " + padLeft((i + 1) + ") " +
                    formatAction(allowedActions.get(i)), 38) + " |");
            }
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");

            choice = getIntInput("Choose: ");

            if (choice >= 1 && choice <= allowedActions.size()) {
                handleManagementAction(allowedActions.get(choice - 1));
                waitForEnter();  // Wait for user to see the result
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
            default: System.out.println("Unknown action.");
        }
    }

    // ==================== VIEW MENU ====================
    private static void showViewMenu() {
        Person user = school.getLoggedInUser();
        if (user == null) { 
            System.out.println("You are not logged in."); 
            return; 
        }

        int choice;

        do {
            ArrayList<String> allowedActions = new ArrayList<>();
            
            if (user.can(School.VIEW_TEACHERS))        allowedActions.add(School.VIEW_TEACHERS);
            if (user.can(School.VIEW_STUDENTS))        allowedActions.add(School.VIEW_STUDENTS);
            if (user.can(School.VIEW_COURSES))         allowedActions.add(School.VIEW_COURSES);
            if (user.can(School.VIEW_ENROLLMENTS))     allowedActions.add(School.VIEW_ENROLLMENTS);
            if (user.can(School.VIEW_GRADES))          allowedActions.add(School.VIEW_GRADES);
            if (user.can(School.VIEW_OWN_ENROLLMENTS)) allowedActions.add(School.VIEW_OWN_ENROLLMENTS);
            if (user.can(School.VIEW_OWN_GRADES))      allowedActions.add(School.VIEW_OWN_GRADES);

            if (allowedActions.isEmpty()) {
                System.out.println("\nYou don't have permission to access View Menu.");
                return;
            }

            System.out.println("\n+----------------------------------------+");
            System.out.println("|              VIEW MENU                 |");
            System.out.println("+----------------------------------------+");
            System.out.println("| Role: " + padRight(user.getClass().getSimpleName(), 32) + " |");
            System.out.println("+----------------------------------------+");
            
            for (int i = 0; i < allowedActions.size(); i++) {
                System.out.println("| " + padLeft((i + 1) + ") " +
                    formatAction(allowedActions.get(i)), 38) + " |");
            }
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");

            choice = getIntInput("Choose: ");

            if (choice >= 1 && choice <= allowedActions.size()) {
                handleViewAction(allowedActions.get(choice - 1));
                waitForEnter();  // Wait for user to see the result
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }

            user = school.getLoggedInUser();

        } while (choice != 0 && user != null);

        if (user == null) System.out.println("You have been logged out.");
    }

    private static void handleViewAction(String action) {
        switch (action) {
            case School.VIEW_TEACHERS:        school.printTeachers();       break;
            case School.VIEW_STUDENTS:        school.printStudents();       break;
            case School.VIEW_COURSES:         school.printCourses();        break;
            case School.VIEW_ENROLLMENTS:     school.printEnrollments();    break;
            case School.VIEW_OWN_ENROLLMENTS: school.printOwnEnrollments(); break;
            case School.VIEW_GRADES:          school.printGrades();         break;
            case School.VIEW_OWN_GRADES:      school.printOwnGrades();      break;
            default: System.out.println("Unknown view action.");
        }
    }

    // ==================== FORMAT ACTION NAME ====================
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
            case School.VIEW_TEACHERS:           return "View All Teachers";
            case School.VIEW_STUDENTS:           return "View All Students";
            case School.VIEW_COURSES:            return "View All Courses";
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

    // ==================== SAMPLE DATA ====================
    // ==================== SAMPLE DATA ====================
    private static void setupSampleData() {
        school.login("admin", "1234");
        school.createTeacher("T001", "Dr. Smith", "smith", "pass123", "Mathematics");
        school.createTeacher("T002", "Prof. Johnson", "johnson", "pass123", "Computer Science");
        school.createStudent("S001", "John Doe", "john", "student123", "Computer Science");
        school.createStudent("S002", "Jane Smith", "jane", "student123", "Mathematics");
        school.createCourse("C001", "Linear Algebra", "MATH101", 4, "Mathematics", true);
        school.createCourse("C002", "Data Structures", "CS201", 4, "Computer Science", true);
        school.createCourse("C003", "Calculus I", "MATH102", 3, "Mathematics", true);
        school.createEnrollment("S001", "C001", "Fall", 2024);
        school.createEnrollment("S001", "C002", "Fall", 2024);
        school.createEnrollment("S002", "C001", "Fall", 2024);
        school.logout();
    }

        // ==================== POLYMORPHISM DEMO (COMMENTED OUT) ====================
        /*
        System.out.println("========================================");
        System.out.println("   POLYMORPHISM DEMO (PDF Section 1-5)");
        System.out.println("   Same can() call, different results");
        System.out.println("========================================");

        ArrayList<Person> staffs = new ArrayList<>();
        staffs.add(new Admin("A01", "Admin User", "adminDemo", "pass1234"));
        staffs.add(new Teacher("T02", "Teacher User", "teacherDemo", "pass1234", "Science"));
        staffs.add(new Student("S03", "Student User", "studentDemo", "pass1234", "CS", null));

        String[] actions = {
            School.CREATE_TEACHER,
            School.GRADE_STUDENT,
            School.VIEW_OWN_GRADES,
            School.VIEW_STUDENTS
        };

        for (Person s : staffs) {
            System.out.println("\n[ " + s.getClass().getSimpleName() + " ] " + s.getUsername());
            for (String action : actions) {
                System.out.println("  can " + action + "? " + s.can(action));
            }
        }

        System.out.println("\n=> Java chose the right can() at runtime.");
        System.out.println("=> No instanceof check — each role decided its own permissions.");
        System.out.println("========================================\n");
        */

        // ==================== LAMBDA DEMO (COMMENTED OUT) ====================
        /*
        school.login("admin", "1234");
        school.demonstrateAnonymousInnerClass();
        school.demonstrateLambdaExpression();
        school.logout();
        */
        
        // waitForEnter();  // Commented out to remove the pause

    // ==================== FIXED INPUT HANDLING ====================
    /**
     * FIXED: Properly handles integer input without multiple prompts
     */
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = sc.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.print("Please enter a number: ");
                    continue;
                }
                
                return Integer.parseInt(input);
                
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = sc.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Please enter a number: ");
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    /**
     * FIXED: Properly handles string input without automatically using default
     * Returns null if input is empty to allow caller to handle it
     */
    private static String getStringInput(String prompt, boolean allowEmpty) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty() && !allowEmpty) {
            System.out.println("Input cannot be empty. Please try again.");
            return getStringInput(prompt, allowEmpty);  // Recursive retry
        }
        return input.isEmpty() ? null : input;
    }
    
    /**
     * Convenience method - doesn't allow empty input
     */
    private static String getRequiredStringInput(String prompt) {
        return getStringInput(prompt, false);
    }
    
    /**
     * Convenience method - allows empty input (returns null if empty)
     */
    private static String getOptionalStringInput(String prompt) {
        return getStringInput(prompt, true);
    }

    private static boolean getBooleanInput(String prompt) {
        while (true) {
            try {
                int choice = getIntInput(prompt + " (1=Yes, 0=No): ");
                if (choice == 1) return true;
                if (choice == 0) return false;
                System.out.println("Please enter 1 for Yes or 0 for No.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter 1 or 0.");
            }
        }
    }

    /**
     * Gets password input with masking (invisible characters)
     * Falls back to regular input if Console is not available (e.g., in IDEs)
     */
    private static String getMaskedPassword(String prompt) {
        System.out.print(prompt);
        
        if (console != null) {
            char[] passwordChars = console.readPassword();
            if (passwordChars == null) {
                return null;
            }
            return new String(passwordChars);
        } else {
            // Fallback for IDEs where Console is not available
            System.out.println("(Password will be visible - Console not available)");
            return sc.nextLine();
        }
    }
    
    /**
     * Gets password with confirmation (invisible input)
     * @param prompt The prompt to show for password
     * @param confirmPrompt The prompt to show for confirmation
     * @return The confirmed password, or null if confirmation failed
     */
    private static String getPasswordWithConfirmation(String prompt, String confirmPrompt) {
        String password = null;
        String confirmPassword = null;
        
        while (true) {
            // Get first password
            while (password == null || password.isEmpty()) {
                password = getMaskedPassword(prompt);
                if (password == null || password.isEmpty()) {
                    System.out.println("Password cannot be empty. Please try again.");
                }
            }
            
            // Get confirmation password
            while (confirmPassword == null || confirmPassword.isEmpty()) {
                confirmPassword = getMaskedPassword(confirmPrompt);
                if (confirmPassword == null || confirmPassword.isEmpty()) {
                    System.out.println("Confirmation password cannot be empty. Please try again.");
                }
            }
            
            // Check if passwords match
            if (password.equals(confirmPassword)) {
                return password;
            } else {
                System.out.println("Error: Passwords do not match. Please try again.");
                password = null;
                confirmPassword = null;
            }
        }
    }
    
    /**
     * Helper method to wait for user to press Enter
     */
    private static void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    // ==================== NAME VALIDATION ====================
    /**
     * Validates that a name contains only letters, spaces, hyphens, apostrophes, and periods
     */
    private static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Pattern: letters (any language), spaces, hyphens, apostrophes, periods
        return name.trim().matches("^[\\p{L}\\s\\-'.]+$");
    }
    
    /**
     * Gets and validates a full name with retry loop
     */
    private static String getValidatedFullName(String prompt) {
        while (true) {
            String name = getStringInput(prompt, false);
            if (name != null && !name.trim().isEmpty()) {
                if (isValidName(name.trim())) {
                    return name.trim();
                } else {
                    System.out.println("Error: Full name can only contain letters, spaces, hyphens, apostrophes, and periods.");
                    System.out.println("Invalid name: '" + name.trim() + "'");
                    System.out.println("Please try again.");
                }
            }
        }
    }

    // ==================== FIXED HANDLER METHODS ====================
    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        
        // Get username - cannot be empty
        String username = null;
        while (username == null || username.isEmpty()) {
            username = getStringInput("Username: ", false);
            if (username == null || username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
            }
        }
        
        // Get password with masking
        String password = null;
        while (password == null || password.isEmpty()) {
            password = getMaskedPassword("Password: ");
            if (password == null || password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
            }
        }
        
        school.login(username, password);
        System.out.println(school.getLastMessage());
        
        Person user = school.getLoggedInUser();
        if (user != null) {
            System.out.println("\nWelcome " + user.getFullName() + "!");
            System.out.println("Your role: " + user.getClass().getSimpleName());
            
            if (user instanceof Admin) {
                System.out.println("You have full system access.");
            } else if (user instanceof Teacher) {
                System.out.println("You can view students and grade assignments.");
            } else if (user instanceof Student) {
                System.out.println("You can view courses and your grades.");
            }
            waitForEnter();
        } else {
            // Login failed, give user chance to try again or go back
            System.out.println("\nPress Enter to return to main menu...");
            sc.nextLine();
        }
    }

    private static void handleLogout() {
        school.logout();
        System.out.println(school.getLastMessage());
        waitForEnter();
    }

    private static void handleCreateTeacher() {
        System.out.println("\n--- Create New Teacher ---");
        String id = getRequiredStringInput("Teacher ID: ");
        
        // Validate full name with immediate feedback
        String name = getValidatedFullName("Full Name: ");
        
        String username = getRequiredStringInput("Username: ");
        
        // Get password with confirmation
        String password = getPasswordWithConfirmation("Password: ", "Confirm Password: ");
        
        String dept = getOptionalStringInput("Department (press Enter for 'Unknown'): ");
        if (dept == null) dept = "Unknown";
        
        school.createTeacher(id, name, username, password, dept);
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteTeacher() {
        System.out.println("\n--- Delete Teacher ---");
        String id = getRequiredStringInput("Teacher ID to delete: ");
        school.deleteTeacher(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateStudent() {
        System.out.println("\n--- Create New Student ---");
        String id = getRequiredStringInput("Student ID: ");
        
        // Validate full name with immediate feedback
        String name = getValidatedFullName("Full Name: ");
        
        String username = getRequiredStringInput("Username: ");
        
        // Get password with confirmation
        String password = getPasswordWithConfirmation("Password: ", "Confirm Password: ");
        
        String major = getOptionalStringInput("Major (press Enter for 'Undeclared'): ");
        if (major == null) major = "Undeclared";
        
        school.createStudent(id, name, username, password, major);
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteStudent() {
        System.out.println("\n--- Delete Student ---");
        String id = getRequiredStringInput("Student ID to delete: ");
        school.deleteStudent(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateCourse() {
        System.out.println("\n--- Create New Course ---");
        String id         = getRequiredStringInput("Course ID: ");
        String title      = getRequiredStringInput("Title: ");
        String code       = getRequiredStringInput("Code: ");
        int credits       = getIntInput("Credits (1-6): ");
        String dept       = getOptionalStringInput("Department (press Enter for 'General'): ");
        if (dept == null) dept = "General";
        boolean available = getBooleanInput("Available for enrollment?");
        school.createCourse(id, title, code, credits, dept, available);
        System.out.println(school.getLastMessage());
    }

    private static void handleUpdateCourse() {
        System.out.println("\n--- Update Course ---");
        String id      = getRequiredStringInput("Course ID: ");
        String title   = getOptionalStringInput("New Title (press Enter to skip): ");
        String code    = getOptionalStringInput("New Code (press Enter to skip): ");
        int credits    = getIntInput("New Credits (0 to skip): ");
        String dept    = getOptionalStringInput("New Department (press Enter to skip): ");
        school.updateCourse(id, title != null ? title : "", 
                           code != null ? code : "", credits, 
                           dept != null ? dept : "");
        System.out.println(school.getLastMessage());
    }

    private static void handleDeleteCourse() {
        System.out.println("\n--- Delete Course ---");
        String id = getRequiredStringInput("Course ID to delete: ");
        school.deleteCourse(id);
        System.out.println(school.getLastMessage());
    }

    private static void handleSetCourseAvailability() {
        System.out.println("\n--- Set Course Availability ---");
        String id         = getRequiredStringInput("Course ID: ");
        boolean available = getBooleanInput("Available for enrollment?");
        school.setCourseAvailability(id, available);
        System.out.println(school.getLastMessage());
    }

    private static void handleCreateEnrollment() {
        System.out.println("\n--- Create New Enrollment ---");
        String studentId = getRequiredStringInput("Student ID: ");
        String courseId  = getRequiredStringInput("Course ID: ");
        String semester  = getOptionalStringInput("Semester (Fall/Spring/Summer, Enter for 'Fall'): ");
        if (semester == null) semester = "Fall";
        int year         = getIntInput("Year: ");
        school.createEnrollment(studentId, courseId, semester, year);
        System.out.println(school.getLastMessage());
    }

    private static void handleGradeStudent() {
        System.out.println("\n--- Grade Student ---");
        System.out.println("Note: You can only grade students who are enrolled in your courses.");
        String enrollId = getRequiredStringInput("Enrollment ID: ");
        boolean gradeRecorded;
        do {
            double score = getDoubleInput("Score (0-100): ");
            gradeRecorded = school.gradeStudent(enrollId, score);
            System.out.println(school.getLastMessage());
        } while (!gradeRecorded && getBooleanInput("Try again with a different score?"));
    }
}