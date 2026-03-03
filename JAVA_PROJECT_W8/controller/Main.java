package controller;
import java.util.Scanner;

import user.Person;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static School school = new School("CADT University", "Phnom Penh");
    
    public static void main(String[] args) {
        
        // Add sample data
        setupSampleData();
        
        int choice = 0;
        
        do {
            if (!school.isUserLoggedIn()) {
                showMainMenu();
                choice = getIntInput("Choose: ");
                
                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        school.viewCourses();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } else {
                showMainLoggedInMenu();
                choice = getIntInput("Choose: ");
                
                switch (choice) {
                    case 1: showManagementMenu(); break;
                    case 2: showViewMenu(); break;
                    case 3: 
                        handleLogout();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
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
        System.out.println("\n+----------------------------------------+");
        System.out.println("|         CADT UNIVERSITY SYSTEM        |");
        System.out.println("+----------------------------------------+");
        System.out.println("| Logged In: " + padRight(user.getRole() + " - " + user.getFullName(), 30) + " |");
        System.out.println("+----------------------------------------+");
        System.out.println("|              MAIN MENU                 |");
        System.out.println("+----------------------------------------+");
        
        // Show Management Menu only if user has any management permissions
        if (hasAnyManagementPermission()) {
            System.out.println("| 1) Management Menu                     |");
        }
        
        // Show View Menu only if user has any view permissions
        if (hasAnyViewPermission()) {
            System.out.println("| 2) View Menu                           |");
        }
        
        System.out.println("| 3) Logout                              |");
        System.out.println("| 0) Exit                                |");
        System.out.println("+----------------------------------------+");
    }
    
    // ==================== PERMISSION CHECKERS ====================
    
    private static boolean hasAnyManagementPermission() {
        Person user = school.getLoggedInUser();
        
        // Check if user can do any management actions
            return user.can(School.CREATE_TEACHER) ||
                user.can(School.DELETE_TEACHER) ||
                user.can(School.CREATE_STUDENT) ||
                user.can(School.DELETE_STUDENT) ||
                user.can(School.CREATE_COURSE) ||
                user.can(School.UPDATE_COURSE) ||
                user.can(School.DELETE_COURSE) ||
                user.can(School.SET_COURSE_AVAILABILITY) ||
                user.can(School.CREATE_ENROLLMENT) ||
                user.can(School.GRADE_STUDENT);
    }
    
    private static boolean hasAnyViewPermission() {
        Person user = school.getLoggedInUser();
        
        // Check if user can do any view actions
            return user.can(School.VIEW_TEACHERS) ||
                user.can(School.VIEW_STUDENTS) ||
                user.can(School.VIEW_COURSES) ||
                user.can(School.VIEW_ENROLLMENTS) ||
                user.can(School.VIEW_OWN_ENROLLMENTS) ||
                user.can(School.VIEW_GRADES) ||
                user.can(School.VIEW_OWN_GRADES);
    }
    
    // ==================== SUB MENUS ====================
    
    private static void showManagementMenu() {
        int choice;
        Person user = school.getLoggedInUser();
        
        do {
            System.out.println("\n+----------------------------------------+");
            System.out.println("|           MANAGEMENT MENU              |");
            System.out.println("+----------------------------------------+");
            
            int menuNumber = 1;
            
            if (user.can(School.CREATE_TEACHER)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Create Teacher", 38) + " |");
            }
            if (user.can(School.DELETE_TEACHER)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Delete Teacher", 38) + " |");
            }
            if (user.can(School.CREATE_STUDENT)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Create Student", 38) + " |");
            }
            if (user.can(School.DELETE_STUDENT)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Delete Student", 38) + " |");
            }
            if (user.can(School.CREATE_COURSE)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Create Course", 38) + " |");
            }
            if (user.can(School.UPDATE_COURSE)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Update Course", 38) + " |");
            }
            if (user.can(School.DELETE_COURSE)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Delete Course", 38) + " |");
            }
            if (user.can(School.SET_COURSE_AVAILABILITY)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Set Course Availability", 38) + " |");
            }
            if (user.can(School.CREATE_ENROLLMENT)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Create Enrollment", 38) + " |");
            }
            if (user.can(School.GRADE_STUDENT)) {
                System.out.println("| " + padLeft(menuNumber++ + ") Grade Student", 38) + " |");
            }
            
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");
            
            choice = getIntInput("Choose: ");
            
            // Map the choice back to the actual action
            if (choice > 0 && choice <= menuNumber - 1) {
                int actionIndex = 1;
                
                if (user.can(School.CREATE_TEACHER) && actionIndex++ == choice) {
                    handleCreateTeacher();
                } else if (user.can(School.DELETE_TEACHER) && actionIndex++ == choice) {
                    handleDeleteTeacher();
                } else if (user.can(School.CREATE_STUDENT) && actionIndex++ == choice) {
                    handleCreateStudent();
                } else if (user.can(School.DELETE_STUDENT) && actionIndex++ == choice) {
                    handleDeleteStudent();
                } else if (user.can(School.CREATE_COURSE) && actionIndex++ == choice) {
                    handleCreateCourse();
                } else if (user.can(School.UPDATE_COURSE) && actionIndex++ == choice) {
                    handleUpdateCourse();
                } else if (user.can(School.DELETE_COURSE) && actionIndex++ == choice) {
                    handleDeleteCourse();
                } else if (user.can(School.SET_COURSE_AVAILABILITY) && actionIndex++ == choice) {
                    handleSetCourseAvailability();
                } else if (user.can(School.CREATE_ENROLLMENT) && actionIndex++ == choice) {
                    handleCreateEnrollment();
                } else if (user.can(School.GRADE_STUDENT) && actionIndex++ == choice) {
                    handleGradeStudent();
                }
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
            
        } while (choice != 0);
        
        System.out.println("Returning to Main Menu...");
    }
    
    private static void showViewMenu() {
        int choice;
        Person user = school.getLoggedInUser();
        
        do {
            System.out.println("\n+----------------------------------------+");
            System.out.println("|              VIEW MENU                 |");
            System.out.println("+----------------------------------------+");
            
            int menuNumber = 11; // Start from 11 to match original numbering
            
            if (user.can(School.VIEW_TEACHERS)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View Teachers", 38) + " |");
            }
            if (user.can(School.VIEW_STUDENTS)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View Students", 38) + " |");
            }
            if (user.can(School.VIEW_COURSES)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View Courses", 38) + " |");
            }
            if (user.can(School.VIEW_ENROLLMENTS)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View All Enrollments", 38) + " |");
            }
            if (user.can(School.VIEW_OWN_ENROLLMENTS)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View My Enrollments", 38) + " |");
            }
            if (user.can(School.VIEW_GRADES)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View All Grades", 38) + " |");
            }
            if (user.can(School.VIEW_OWN_GRADES)) {
                System.out.println("| " + padLeft(menuNumber++ + ") View My Grades", 38) + " |");
            }
            
            System.out.println("| " + padLeft("0) Back to Main Menu", 38) + " |");
            System.out.println("+----------------------------------------+");
            
            choice = getIntInput("Choose: ");
            
            // Handle view actions based on choice
            switch (choice) {
                case 11: if (user.can(School.VIEW_TEACHERS)) school.viewTeachers(); break;
                case 12: if (user.can(School.VIEW_STUDENTS)) school.viewStudents(); break;
                case 13: if (user.can(School.VIEW_COURSES)) school.viewCourses(); break;
                case 14: if (user.can(School.VIEW_ENROLLMENTS)) school.viewEnrollments(); break;
                case 15: if (user.can(School.VIEW_OWN_ENROLLMENTS)) school.viewOwnEnrollments(); break;
                case 16: if (user.can(School.VIEW_GRADES)) school.viewGrades(); break;
                case 17: if (user.can(School.VIEW_OWN_GRADES)) school.viewOwnGrades(); break;
                case 0: 
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    if (choice >= 11 && choice <= 17) {
                        System.out.println("You don't have permission for this action.");
                    } else {
                        System.out.println("Invalid choice.");
                    }
            }
        } while (choice != 0);
    }
    
    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
    
    private static String padLeft(String text, int length) {
        return String.format("%" + length + "s", text);
    }
    
    // ==================== SAMPLE DATA SETUP ====================
    
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
        sc.nextLine(); // consume newline
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
        sc.nextLine(); // consume newline
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
        String id = getStringInput("Teacher ID: ");
        String name = getStringInput("Full Name: ");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        String dept = getStringInput("Department: ");
        
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
        String id = getStringInput("Student ID: ");
        String name = getStringInput("Full Name: ");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");
        String major = getStringInput("Major: ");
        
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
        String id = getStringInput("Course ID: ");
        String title = getStringInput("Title: ");
        String code = getStringInput("Code: ");
        int credits = getIntInput("Credits: ");
        String dept = getStringInput("Department: ");
        boolean available = getBooleanInput("Available?");
        
        school.createCourse(id, title, code, credits, dept, available);
        System.out.println(school.getLastMessage());
    }
    
    private static void handleUpdateCourse() {
        System.out.println("\n--- Update Course ---");
        String id = getStringInput("Course ID: ");
        String title = getStringInput("New Title (press Enter to skip): ");
        String code = getStringInput("New Code (press Enter to skip): ");
        
        int credits = -1;
        System.out.print("New Credits (enter 0 to skip): ");
        if (sc.hasNextInt()) {
            credits = sc.nextInt();
            sc.nextLine();
        } else {
            sc.nextLine(); // consume invalid input
        }
        
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
        String id = getStringInput("Course ID: ");
        boolean available = getBooleanInput("Available?");
        
        school.setCourseAvailability(id, available);
        System.out.println(school.getLastMessage());
    }
    
    private static void handleCreateEnrollment() {
        System.out.println("\n--- Create New Enrollment ---");
        String studentId = getStringInput("Student ID: ");
        String courseId = getStringInput("Course ID: ");
        String semester = getStringInput("Semester (Fall/Spring/Summer): ");
        int year = getIntInput("Year: ");
        
        school.createEnrollment(studentId, courseId, semester, year);
        System.out.println(school.getLastMessage());
    }
    
    private static void handleGradeStudent() {
        System.out.println("\n--- Grade Student ---");
        String enrollId = getStringInput("Enrollment ID: ");
        double score = getDoubleInput("Score (0-100): ");
        
        school.gradeStudent(enrollId, score);
        System.out.println(school.getLastMessage());
    }
}