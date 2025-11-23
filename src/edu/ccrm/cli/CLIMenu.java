package edu.ccrm.cli;

import edu.ccrm.domain.*;
import edu.ccrm.service.StudentService;
import edu.ccrm.service.CourseService;
import edu.ccrm.io.FileService;
import edu.ccrm.config.AppConfig;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.service.DataAccessException;
import edu.ccrm.util.ValidationUtil;
import edu.ccrm.util.RecursiveFileLister;

import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLIMenu {
    private Scanner scanner;
    private StudentService studentService;
    private CourseService courseService;
    private FileService fileService;
    private AppConfig config;
    
    public CLIMenu() {
        this.scanner = new Scanner(System.in);
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.fileService = new FileService();
        this.config = AppConfig.getInstance();
        
        // Load sample data
        loadSampleData();
    }
    
    public void start() {
        int choice;
        mainLoop: while (true) {
            printMainMenu();
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    manageStudents();
                    break;
                case 2:
                    manageCourses();
                    break;
                case 3:
                    manageEnrollments();
                    break;
                case 4:
                    manageGrades();
                    break;
                case 5:
                    importExportData();
                    break;
                case 6:
                    backupOperations();
                    break;
                case 7:
                    generateReports();
                    break;
                case 8:
                    printJavaPlatformInfo();
                    break;
                case 0:
                    System.out.println("Thank you for using CCRM. Goodbye!");
                    break mainLoop;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    private void printMainMenu() {
        System.out.println("\n=== CCRM Main Menu ===");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments");
        System.out.println("4. Manage Grades");
        System.out.println("5. Import/Export Data");
        System.out.println("6. Backup Operations");
        System.out.println("7. Generate Reports");
        System.out.println("8. Java Platform Info");
        System.out.println("0. Exit");
        System.out.println("======================");
    }
    
    // Student Management Methods
    private void manageStudents() {
        int choice;
        do {
            System.out.println("\n=== Student Management ===");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Deactivate Student");
            System.out.println("5. Search Students");
            System.out.println("6. View Student Transcript");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    listStudents();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    deactivateStudent();
                    break;
                case 5:
                    searchStudents();
                    break;
                case 6:
                    viewStudentTranscript();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void addStudent() {
        System.out.println("\n--- Add New Student ---");
        String id = getStringInput("Student ID: ");
        String regNo = getStringInput("Registration Number: ");
        String fullName = getStringInput("Full Name: ");
        String email = getStringInput("Email: ");
        
        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }
        
        if (!ValidationUtil.isValidRegNo(regNo)) {
            System.out.println("Invalid registration number format.");
            return;
        }
        
        // Check if student already exists
        if (studentService.findById(id) != null) {
            System.out.println("Student with ID " + id + " already exists.");
            return;
        }
        
        Student student = new Student.Builder()
            .id(id)
            .regNo(regNo)
            .fullName(fullName)
            .email(email)
            .build();
            
        studentService.addStudent(student);
        System.out.println("Student added successfully!");
    }
    
    private void listStudents() {
        List<Student> students = studentService.getActiveStudents();
        if (students.isEmpty()) {
            System.out.println("No active students found.");
            return;
        }
        
        System.out.println("\n--- Active Students ---");
        students.forEach(student -> {
            System.out.println(student.getProfileInfo());
        });
    }
    
    private void updateStudent() {
        System.out.println("\n--- Update Student ---");
        String id = getStringInput("Enter Student ID to update: ");
        
        Student student = studentService.findById(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("Current details: " + student.getProfileInfo());
        
        String fullName = getStringInput("New Full Name (press enter to keep current): ");
        String email = getStringInput("New Email (press enter to keep current): ");
        
        if (!fullName.isEmpty()) {
            student.setFullName(fullName);
        }
        if (!email.isEmpty()) {
            if (!ValidationUtil.isValidEmail(email)) {
                System.out.println("Invalid email format. Email not updated.");
            } else {
                student.setEmail(email);
            }
        }
        
        System.out.println("Student updated successfully!");
    }
    
    private void deactivateStudent() {
        System.out.println("\n--- Deactivate Student ---");
        String id = getStringInput("Enter Student ID to deactivate: ");
        
        if (studentService.deactivateStudent(id)) {
            System.out.println("Student deactivated successfully!");
        } else {
            System.out.println("Student not found.");
        }
    }
    
    private void searchStudents() {
        System.out.println("\n--- Search Students ---");
        String searchTerm = getStringInput("Enter name or email to search: ").toLowerCase();
        
        List<Student> results = studentService.search(student -> 
            student.getFullName().toLowerCase().contains(searchTerm) ||
            student.getEmail().toLowerCase().contains(searchTerm)
        );
        
        if (results.isEmpty()) {
            System.out.println("No students found matching: " + searchTerm);
        } else {
            System.out.println("Found " + results.size() + " student(s):");
            results.forEach(System.out::println);
        }
    }
    
    private void viewStudentTranscript() {
        System.out.println("\n--- Student Transcript ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("\n=== Transcript for " + student.getFullName() + " ===");
        System.out.println("Student ID: " + student.getId());
        System.out.println("Registration No: " + student.getRegNo());
        System.out.println("Email: " + student.getEmail());
        System.out.println("\nEnrolled Courses:");
        System.out.println("-----------------");
        
        if (student.getEnrollments().isEmpty()) {
            System.out.println("No courses enrolled.");
        } else {
            student.getEnrollments().forEach(enrollment -> {
                Course course = enrollment.getCourse();
                String status = enrollment.isGraded() ? 
                    String.format("Marks: %.2f, Grade: %s", enrollment.getMarks(), enrollment.getGrade()) : 
                    "Not Graded";
                System.out.printf("%s - %s (%d credits) - %s%n", 
                    course.getCode(), course.getTitle(), course.getCredits(), status);
            });
            
            double gpa = student.calculateGPA();
            System.out.printf("%nOverall GPA: %.2f%n", gpa);
        }
    }
    
    // Course Management Methods
    private void manageCourses() {
        int choice;
        do {
            System.out.println("\n=== Course Management ===");
            System.out.println("1. Add Course");
            System.out.println("2. List All Courses");
            System.out.println("3. Update Course");
            System.out.println("4. Deactivate Course");
            System.out.println("5. Search Courses");
            System.out.println("6. Filter Courses by Department");
            System.out.println("7. Filter Courses by Instructor");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addCourse();
                    break;
                case 2:
                    listCourses();
                    break;
                case 3:
                    updateCourse();
                    break;
                case 4:
                    deactivateCourse();
                    break;
                case 5:
                    searchCourses();
                    break;
                case 6:
                    filterCoursesByDepartment();
                    break;
                case 7:
                    filterCoursesByInstructor();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void addCourse() {
        System.out.println("\n--- Add New Course ---");
        String code = getStringInput("Course Code: ");
        String title = getStringInput("Course Title: ");
        int credits = getIntInput("Credits: ");
        String instructor = getStringInput("Instructor: ");
        String department = getStringInput("Department: ");
        
        if (!ValidationUtil.isValidCredits(credits)) {
            System.out.println("Invalid credits. Must be between 1 and 6.");
            return;
        }
        
        // Show semester options
        System.out.println("Available Semesters:");
        for (Semester semester : Semester.values()) {
            System.out.println(semester.ordinal() + 1 + ". " + semester.getDisplayName());
        }
        int semesterChoice = getIntInput("Select Semester: ") - 1;
        
        if (semesterChoice < 0 || semesterChoice >= Semester.values().length) {
            System.out.println("Invalid semester choice.");
            return;
        }
        
        Semester semester = Semester.values()[semesterChoice];
        
        Course course = new Course.Builder()
            .code(code)
            .title(title)
            .credits(credits)
            .instructor(instructor)
            .department(department)
            .semester(semester)
            .build();
            
        courseService.addCourse(course);
        System.out.println("Course added successfully!");
    }
    
    private void listCourses() {
        List<Course> courses = courseService.getActiveCourses();
        if (courses.isEmpty()) {
            System.out.println("No active courses found.");
            return;
        }
        
        System.out.println("\n--- Active Courses ---");
        courses.forEach(course -> {
            System.out.printf("%s - %s (%d credits) - %s - %s%n",
                course.getCode(), course.getTitle(), course.getCredits(),
                course.getInstructor(), course.getDepartment());
        });
    }
    
    private void updateCourse() {
        System.out.println("\n--- Update Course ---");
        String code = getStringInput("Enter Course Code to update: ");
        
        Course course = courseService.findByCode(code);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        
        System.out.println("Current details: " + course.toString());
        
        String title = getStringInput("New Title (press enter to keep current): ");
        String instructor = getStringInput("New Instructor (press enter to keep current): ");
        String department = getStringInput("New Department (press enter to keep current): ");
        String creditsStr = getStringInput("New Credits (press enter to keep current): ");
        
        if (!title.isEmpty()) course.setTitle(title);
        if (!instructor.isEmpty()) course.setInstructor(instructor);
        if (!department.isEmpty()) course.setDepartment(department);
        if (!creditsStr.isEmpty()) {
            try {
                int credits = Integer.parseInt(creditsStr);
                if (ValidationUtil.isValidCredits(credits)) {
                    course.setCredits(credits);
                } else {
                    System.out.println("Invalid credits. Not updated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Credits not updated.");
            }
        }
        
        System.out.println("Course updated successfully!");
    }
    
    private void deactivateCourse() {
        System.out.println("\n--- Deactivate Course ---");
        String code = getStringInput("Enter Course Code to deactivate: ");
        
        if (courseService.deactivateCourse(code)) {
            System.out.println("Course deactivated successfully!");
        } else {
            System.out.println("Course not found.");
        }
    }
    
    private void searchCourses() {
        System.out.println("\n--- Search Courses ---");
        String searchTerm = getStringInput("Enter course title or code to search: ").toLowerCase();
        
        List<Course> results = courseService.search(course -> 
            course.getTitle().toLowerCase().contains(searchTerm) ||
            course.getCode().toLowerCase().contains(searchTerm)
        );
        
        if (results.isEmpty()) {
            System.out.println("No courses found matching: " + searchTerm);
        } else {
            System.out.println("Found " + results.size() + " course(s):");
            results.forEach(System.out::println);
        }
    }
    
    private void filterCoursesByDepartment() {
        System.out.println("\n--- Filter Courses by Department ---");
        String department = getStringInput("Enter Department: ");
        
        List<Course> courses = courseService.getCoursesByDepartment(department);
        if (courses.isEmpty()) {
            System.out.println("No courses found in department: " + department);
        } else {
            System.out.println("Courses in " + department + " department:");
            courses.forEach(course -> 
                System.out.printf("%s - %s (%d credits)%n",
                    course.getCode(), course.getTitle(), course.getCredits())
            );
        }
    }
    
    private void filterCoursesByInstructor() {
        System.out.println("\n--- Filter Courses by Instructor ---");
        String instructor = getStringInput("Enter Instructor Name: ");
        
        List<Course> courses = courseService.getCoursesByInstructor(instructor);
        if (courses.isEmpty()) {
            System.out.println("No courses found for instructor: " + instructor);
        } else {
            System.out.println("Courses taught by " + instructor + ":");
            courses.forEach(course -> 
                System.out.printf("%s - %s (%d credits)%n",
                    course.getCode(), course.getTitle(), course.getCredits())
            );
        }
    }
    
    // Enrollment Management Methods
    private void manageEnrollments() {
        int choice;
        do {
            System.out.println("\n=== Enrollment Management ===");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Unenroll Student from Course");
            System.out.println("3. View Student Enrollments");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    enrollStudent();
                    break;
                case 2:
                    unenrollStudent();
                    break;
                case 3:
                    viewStudentEnrollments();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void enrollStudent() {
        System.out.println("\n--- Enroll Student in Course ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        String courseCode = getStringInput("Enter Course Code: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        Course course = courseService.findByCode(courseCode);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        
        try {
            studentService.enrollInCourse(student, course);
            System.out.println("Student enrolled successfully in " + course.getTitle());
        } catch (DuplicateEnrollmentException e) {
            System.out.println("Enrollment failed: " + e.getMessage());
        } catch (MaxCreditLimitExceededException e) {
            System.out.println("Enrollment failed: " + e.getMessage());
        }
    }
    
    private void unenrollStudent() {
        System.out.println("\n--- Unenroll Student from Course ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        String courseCode = getStringInput("Enter Course Code: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        if (studentService.unenrollFromCourse(student, courseCode)) {
            System.out.println("Student unenrolled successfully from course " + courseCode);
        } else {
            System.out.println("Student was not enrolled in that course.");
        }
    }
    
    private void viewStudentEnrollments() {
        System.out.println("\n--- View Student Enrollments ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        List<Enrollment> enrollments = student.getEnrollments();
        if (enrollments.isEmpty()) {
            System.out.println("Student has no enrollments.");
        } else {
            System.out.println("Enrollments for " + student.getFullName() + ":");
            enrollments.forEach(enrollment -> {
                Course course = enrollment.getCourse();
                System.out.printf("- %s: %s (Enrolled: %s)%n",
                    course.getCode(), course.getTitle(), enrollment.getEnrollmentDate());
            });
        }
    }
    
    // Grade Management Methods
    private void manageGrades() {
        int choice;
        do {
            System.out.println("\n=== Grade Management ===");
            System.out.println("1. Record Marks for Student");
            System.out.println("2. View Student Grades");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    recordMarks();
                    break;
                case 2:
                    viewStudentGrades();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void recordMarks() {
        System.out.println("\n--- Record Marks ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        String courseCode = getStringInput("Enter Course Code: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        // Find the enrollment
        Enrollment enrollment = student.getEnrollments().stream()
            .filter(e -> e.getCourse().getCode().equals(courseCode))
            .findFirst()
            .orElse(null);
            
        if (enrollment == null) {
            System.out.println("Student is not enrolled in that course.");
            return;
        }
        
        double marks = getDoubleInput("Enter marks (0-100): ");
        if (marks < 0 || marks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }
        
        enrollment.recordMarks(marks);
        System.out.printf("Marks recorded successfully! Grade: %s%n", enrollment.getGrade());
    }
    
    private void viewStudentGrades() {
        System.out.println("\n--- View Student Grades ---");
        String regNo = getStringInput("Enter Student Registration Number: ");
        
        Student student = studentService.findByRegNo(regNo);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        List<Enrollment> enrollments = student.getEnrollments();
        if (enrollments.isEmpty()) {
            System.out.println("Student has no enrollments.");
        } else {
            System.out.println("Grades for " + student.getFullName() + ":");
            System.out.println("Course Code | Course Title | Marks | Grade");
            System.out.println("------------------------------------------");
            
            enrollments.forEach(enrollment -> {
                Course course = enrollment.getCourse();
                if (enrollment.isGraded()) {
                    System.out.printf("%-11s | %-12s | %5.2f | %s%n",
                        course.getCode(), course.getTitle(), 
                        enrollment.getMarks(), enrollment.getGrade());
                } else {
                    System.out.printf("%-11s | %-12s | %5s | %s%n",
                        course.getCode(), course.getTitle(), "N/A", "Not Graded");
                }
            });
            
            double gpa = student.calculateGPA();
            System.out.printf("%nOverall GPA: %.2f%n", gpa);
        }
    }
    
    // Import/Export Methods
    private void importExportData() {
        int choice;
        do {
            System.out.println("\n=== Import/Export Data ===");
            System.out.println("1. Import Students from CSV");
            System.out.println("2. Import Courses from CSV");
            System.out.println("3. Export Students to CSV");
            System.out.println("4. Export Courses to CSV");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    importStudentsFromCSV();
                    break;
                case 2:
                    importCoursesFromCSV();
                    break;
                case 3:
                    exportStudentsToCSV();
                    break;
                case 4:
                    exportCoursesToCSV();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void importStudentsFromCSV() {
        System.out.println("\n--- Import Students from CSV ---");
        String filename = getStringInput("Enter CSV filename (in data folder): ");
        Path filePath = config.getDataDirectory().resolve(filename);
        
        try {
            List<Student> students = fileService.importStudents(filePath);
            students.forEach(studentService::addStudent);
            System.out.println("Successfully imported " + students.size() + " students.");
        } catch (DataAccessException e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }
    
    private void importCoursesFromCSV() {
        System.out.println("\n--- Import Courses from CSV ---");
        String filename = getStringInput("Enter CSV filename (in data folder): ");
        Path filePath = config.getDataDirectory().resolve(filename);
        
        try {
            List<Course> courses = fileService.importCourses(filePath);
            courses.forEach(courseService::addCourse);
            System.out.println("Successfully imported " + courses.size() + " courses.");
        } catch (DataAccessException e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }
    
    private void exportStudentsToCSV() {
        System.out.println("\n--- Export Students to CSV ---");
        String filename = getStringInput("Enter CSV filename: ");
        Path filePath = config.getDataDirectory().resolve(filename);
        
        try {
            fileService.exportStudents(studentService.getAllStudents(), filePath);
            System.out.println("Students exported successfully to: " + filePath);
        } catch (DataAccessException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
    
    private void exportCoursesToCSV() {
        System.out.println("\n--- Export Courses to CSV ---");
        String filename = getStringInput("Enter CSV filename: ");
        Path filePath = config.getDataDirectory().resolve(filename);
        
        try {
            fileService.exportCourses(courseService.getAllCourses(), filePath);
            System.out.println("Courses exported successfully to: " + filePath);
        } catch (DataAccessException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
    
    // Backup Operations
    private void backupOperations() {
        System.out.println("\n=== Backup Operations ===");
        
        try {
            // Create timestamped backup folder
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupPath = config.getBackupDirectory().resolve("backup_" + timestamp);
            Files.createDirectories(backupPath);
            
            // Export data to backup folder
            fileService.exportStudents(studentService.getAllStudents(), backupPath.resolve("students.csv"));
            fileService.exportCourses(courseService.getAllCourses(), backupPath.resolve("courses.csv"));
            
            // Calculate backup size recursively
            long size = fileService.calculateDirectorySize(backupPath);
            System.out.println("Backup created successfully at: " + backupPath);
            System.out.println("Backup size: " + size + " bytes");
            
            // List backup files recursively
            List<Path> backupFiles = RecursiveFileLister.listFilesByDepth(config.getBackupDirectory(), 2);
            System.out.println("Backup files:");
            backupFiles.forEach(file -> System.out.println("  " + file));
            
        } catch (Exception e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }
    
    // Report Generation
    private void generateReports() {
        int choice;
        do {
            System.out.println("\n=== Generate Reports ===");
            System.out.println("1. GPA Distribution Report");
            System.out.println("2. Course Enrollment Report");
            System.out.println("3. Top Performing Students");
            System.out.println("0. Back to Main Menu");
            
            choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    generateGPADistribution();
                    break;
                case 2:
                    generateCourseEnrollmentReport();
                    break;
                case 3:
                    generateTopStudentsReport();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }
    
    private void generateGPADistribution() {
        System.out.println("\n=== GPA Distribution Report ===");
        
        // Using Streams API for data processing
        var gpaDistribution = studentService.getAllStudents().stream()
            .filter(Student::isActive)
            .collect(Collectors.groupingBy(
                student -> {
                    double gpa = student.calculateGPA();
                    if (gpa >= 9.0) return "A+ (9.0-10.0)";
                    else if (gpa >= 8.0) return "A (8.0-8.9)";
                    else if (gpa >= 7.0) return "B (7.0-7.9)";
                    else if (gpa >= 6.0) return "C (6.0-6.9)";
                    else if (gpa >= 5.0) return "D (5.0-5.9)";
                    else return "F (Below 5.0)";
                },
                Collectors.counting()
            ));
        
        if (gpaDistribution.isEmpty()) {
            System.out.println("No GPA data available.");
        } else {
            gpaDistribution.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> 
                    System.out.printf("%-15s: %d students%n", entry.getKey(), entry.getValue())
                );
        }
    }
    
    private void generateCourseEnrollmentReport() {
        System.out.println("\n=== Course Enrollment Report ===");
        
        // Using Streams to aggregate enrollment data
        var enrollmentStats = studentService.getAllStudents().stream()
            .flatMap(student -> student.getEnrollments().stream())
            .collect(Collectors.groupingBy(
                enrollment -> enrollment.getCourse().getCode(),
                Collectors.counting()
            ));
        
        if (enrollmentStats.isEmpty()) {
            System.out.println("No enrollment data available.");
        } else {
            enrollmentStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> 
                    System.out.printf("Course %s: %d enrollments%n", entry.getKey(), entry.getValue())
                );
        }
    }
    
    private void generateTopStudentsReport() {
        System.out.println("\n=== Top Performing Students ===");
        
        var topStudents = studentService.getAllStudents().stream()
            .filter(Student::isActive)
            .filter(student -> !student.getEnrollments().isEmpty())
            .sorted((s1, s2) -> Double.compare(s2.calculateGPA(), s1.calculateGPA()))
            .limit(5)
            .collect(Collectors.toList());
        
        if (topStudents.isEmpty()) {
            System.out.println("No student data available.");
        } else {
            System.out.println("Top 5 Students by GPA:");
            topStudents.forEach(student -> 
                System.out.printf("%s - GPA: %.2f%n", student.getFullName(), student.calculateGPA())
            );
        }
    }
    
    private void printJavaPlatformInfo() {
        System.out.println("\n=== Java Platform Information ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("\nJava Platform Comparison:");
        System.out.println("Java SE (Standard Edition): Desktop and server applications");
        System.out.println("Java EE (Enterprise Edition): Enterprise-scale applications");
        System.out.println("Java ME (Micro Edition): Embedded and mobile devices");
        System.out.println("\nThis application uses Java SE (Standard Edition)");
    }
    
    // Utility methods
    private void loadSampleData() {
        // Sample students
        Student student1 = new Student.Builder()
            .id("S001")
            .regNo("2023001")
            .fullName("John Doe")
            .email("john.doe@university.edu")
            .build();
            
        Student student2 = new Student.Builder()
            .id("S002")
            .regNo("2023002")
            .fullName("Jane Smith")
            .email("jane.smith@university.edu")
            .build();
            
        Student student3 = new Student.Builder()
            .id("S003")
            .regNo("2023003")
            .fullName("Bob Johnson")
            .email("bob.johnson@university.edu")
            .build();
            
        studentService.addStudent(student1);
        studentService.addStudent(student2);
        studentService.addStudent(student3);
        
        // Sample courses
        Course course1 = new Course.Builder()
            .code("CS101")
            .title("Introduction to Computer Science")
            .credits(3)
            .instructor("Dr. Smith")
            .semester(Semester.FALL)
            .department("Computer Science")
            .build();
            
        Course course2 = new Course.Builder()
            .code("MATH201")
            .title("Calculus I")
            .credits(4)
            .instructor("Dr. Johnson")
            .semester(Semester.FALL)
            .department("Mathematics")
            .build();
            
        Course course3 = new Course.Builder()
            .code("PHY101")
            .title("Physics Fundamentals")
            .credits(3)
            .instructor("Dr. Brown")
            .semester(Semester.SPRING)
            .department("Physics")
            .build();
            
        courseService.addCourse(course1);
        courseService.addCourse(course2);
        courseService.addCourse(course3);
        
        // Sample enrollments and grades
        try {
            studentService.enrollInCourse(student1, course1);
            studentService.enrollInCourse(student1, course2);
            studentService.enrollInCourse(student2, course1);
            studentService.enrollInCourse(student3, course3);
            
            // Record some grades
            student1.getEnrollments().get(0).recordMarks(85.5); // CS101
            student1.getEnrollments().get(1).recordMarks(92.0); // MATH201
            student2.getEnrollments().get(0).recordMarks(78.0); // CS101
            
        } catch (Exception e) {
            System.out.println("Error setting up sample data: " + e.getMessage());
        }
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}