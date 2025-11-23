package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentService implements Searchable<Student> {
    private List<Student> students;
    private static final int MAX_CREDITS_PER_SEMESTER = 18;
    
    public StudentService() {
        this.students = new ArrayList<>();
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }
    
    public boolean updateStudent(String id, String fullName, String email) {
        Optional<Student> studentOpt = students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
            
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setFullName(fullName);
            student.setEmail(email);
            return true;
        }
        return false;
    }
    
    public boolean deactivateStudent(String id) {
        return students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .map(s -> {
                s.setActive(false);
                return true;
            })
            .orElse(false);
    }
    
    public void enrollInCourse(Student student, Course course) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        
        // Check for duplicate enrollment
        boolean alreadyEnrolled = student.getEnrollments().stream()
            .anyMatch(e -> e.getCourse().getCode().equals(course.getCode()));
            
        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(
                "Student " + student.getRegNo() + " is already enrolled in course " + course.getCode());
        }
        
        // Check credit limit
        int currentCredits = student.getEnrollments().stream()
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
            
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                "Credit limit exceeded. Current: " + currentCredits + ", Attempting: " + 
                course.getCredits() + ", Max: " + MAX_CREDITS_PER_SEMESTER);
        }
        
        Enrollment enrollment = new Enrollment(student, course);
        student.addEnrollment(enrollment);
    }
    
    public boolean unenrollFromCourse(Student student, String courseCode) {
        return student.removeEnrollment(courseCode);
    }
    
    // Implementation of Searchable interface
    @Override
    public List<Student> search(Predicate<Student> predicate) {
        return students.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public Student findById(String id) {
        return students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public Student findByRegNo(String regNo) {
        return students.stream()
            .filter(s -> s.getRegNo().equals(regNo))
            .findFirst()
            .orElse(null);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }
    
    public List<Student> getActiveStudents() {
        return students.stream()
            .filter(Student::isActive)
            .collect(Collectors.toList());
    }
}