package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Enrollment {
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private Double marks;
    private Grade grade;
    
    public Enrollment(Student student, Course course) {
        this.student = Objects.requireNonNull(student, "Student cannot be null");
        this.course = Objects.requireNonNull(course, "Course cannot be null");
        this.enrollmentDate = LocalDate.now();
    }
    
    public void recordMarks(double marks) {
        this.marks = marks;
        this.grade = Grade.fromMarks(marks);
    }
    
    // Getters
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public Double getMarks() { return marks; }
    public Grade getGrade() { return grade; }
    public boolean isGraded() { return marks != null; }
    
    @Override
    public String toString() {
        return String.format("Enrollment{student=%s, course=%s, date=%s, marks=%s, grade=%s}",
                           student.getRegNo(), course.getCode(), enrollmentDate, marks, grade);
    }
}