package edu.ccrm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person {
    private final String regNo; // Immutable
    private List<Enrollment> enrollments;
    
    // Static nested class for Student Builder pattern
    public static class Builder {
        private String id;
        private String regNo;
        private String fullName;
        private String email;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder regNo(String regNo) {
            this.regNo = regNo;
            return this;
        }
        
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Student build() {
            return new Student(this);
        }
    }
    
    private Student(Builder builder) {
        super(builder.id, builder.fullName, builder.email);
        this.regNo = Objects.requireNonNull(builder.regNo, "Registration number cannot be null");
        this.enrollments = new ArrayList<>();
    }
    
    @Override
    public String getProfileInfo() {
        return String.format("Student ID: %s, Reg No: %s, Name: %s, Email: %s, Active: %s",
                           id, regNo, fullName, email, active);
    }
    
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }
    
    public boolean removeEnrollment(String courseCode) {
        return enrollments.removeIf(e -> e.getCourse().getCode().equals(courseCode));
    }
    
    /**
     * Calculate GPA based on graded courses only
     * GPA = Σ(grade points × credits) / Σ(credits)
     * Only includes courses that have been graded
     */
    public double calculateGPA() {
        if (enrollments.isEmpty()) {
            return 0.0;
        }
        
        // Using Streams API to calculate weighted average
        double totalGradePoints = enrollments.stream()
            .filter(Enrollment::isGraded)  // Only include graded courses
            .mapToDouble(enrollment -> 
                enrollment.getGrade().getPoints() * enrollment.getCourse().getCredits()
            )
            .sum();
        
        int totalCredits = enrollments.stream()
            .filter(Enrollment::isGraded)  // Only include graded courses
            .mapToInt(enrollment -> enrollment.getCourse().getCredits())
            .sum();
        
        // Avoid division by zero
        if (totalCredits == 0) {
            return 0.0;
        }
        
        return totalGradePoints / totalCredits;
    }
    
    /**
     * Alternative GPA calculation that includes all enrolled courses (ungraded count as 0)
     */
    public double calculateOverallGPA() {
        if (enrollments.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = enrollments.stream()
            .mapToDouble(enrollment -> {
                if (enrollment.isGraded()) {
                    return enrollment.getGrade().getPoints() * enrollment.getCourse().getCredits();
                } else {
                    return 0.0; // Ungraded courses count as 0
                }
            })
            .sum();
        
        int totalCredits = enrollments.stream()
            .mapToInt(enrollment -> enrollment.getCourse().getCredits())
            .sum();
        
        if (totalCredits == 0) {
            return 0.0;
        }
        
        return totalGradePoints / totalCredits;
    }
    
    /**
     * Get number of graded courses
     */
    public long getGradedCoursesCount() {
        return enrollments.stream()
            .filter(Enrollment::isGraded)
            .count();
    }
    
    /**
     * Get total credits attempted (all enrolled courses)
     */
    public int getTotalCreditsAttempted() {
        return enrollments.stream()
            .mapToInt(enrollment -> enrollment.getCourse().getCredits())
            .sum();
    }
    
    /**
     * Get total credits earned (only graded courses with passing grades)
     */
    public int getTotalCreditsEarned() {
        return enrollments.stream()
            .filter(Enrollment::isGraded)
            .filter(enrollment -> enrollment.getGrade().getPoints() >= 5.0) // Passing grade threshold
            .mapToInt(enrollment -> enrollment.getCourse().getCredits())
            .sum();
    }
    
    // Getters
    public String getRegNo() { return regNo; }
    public List<Enrollment> getEnrollments() { return new ArrayList<>(enrollments); } // Defensive copy
    
    @Override
    public String toString() {
        double gpa = calculateGPA();
        return String.format("Student{id='%s', regNo='%s', name='%s', enrollments=%d, GPA=%.2f}", 
                           id, regNo, fullName, enrollments.size(), gpa);
    }
}