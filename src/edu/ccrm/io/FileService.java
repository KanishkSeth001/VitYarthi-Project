package edu.ccrm.io;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Semester;
import edu.ccrm.service.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileService {
    
    public void exportStudents(List<Student> students, Path filePath) throws DataAccessException {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,regNo,fullName,email,active,createdDate");
            
            students.forEach(student -> {
                String line = String.format("%s,%s,%s,%s,%s,%s",
                    student.getId(), student.getRegNo(), student.getFullName(),
                    student.getEmail(), student.isActive(), student.getCreatedDate());
                lines.add(line);
            });
            
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new DataAccessException("Failed to export students: " + e.getMessage(), e);
        }
    }
    
    public List<Student> importStudents(Path filePath) throws DataAccessException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1) // Skip header
                .map(this::parseStudentFromCSV)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DataAccessException("Failed to import students: " + e.getMessage(), e);
        }
    }
    
    public void exportCourses(List<Course> courses, Path filePath) throws DataAccessException {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("code,title,credits,instructor,semester,department,active");
            
            courses.forEach(course -> {
                String line = String.format("%s,%s,%d,%s,%s,%s,%s",
                    course.getCode(), course.getTitle(), course.getCredits(),
                    course.getInstructor(), course.getSemester().name(),
                    course.getDepartment(), course.isActive());
                lines.add(line);
            });
            
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new DataAccessException("Failed to export courses: " + e.getMessage(), e);
        }
    }
    
    public List<Course> importCourses(Path filePath) throws DataAccessException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1) // Skip header
                .map(this::parseCourseFromCSV)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DataAccessException("Failed to import courses: " + e.getMessage(), e);
        }
    }
    
    private Student parseStudentFromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 6) {
            return new Student.Builder()
                .id(parts[0])
                .regNo(parts[1])
                .fullName(parts[2])
                .email(parts[3])
                .build();
        }
        throw new IllegalArgumentException("Invalid student CSV line: " + line);
    }
    
    private Course parseCourseFromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            return new Course.Builder()
                .code(parts[0])
                .title(parts[1])
                .credits(Integer.parseInt(parts[2]))
                .instructor(parts[3])
                .semester(Semester.valueOf(parts[4]))
                .department(parts[5])
                .build();
        }
        throw new IllegalArgumentException("Invalid course CSV line: " + line);
    }
    
    // Recursive directory size calculation
    public long calculateDirectorySize(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return 0;
        }
        
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
                .filter(Files::isRegularFile)
                .mapToLong(this::getFileSize)
                .sum();
        }
    }
    
    private long getFileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
    }
}