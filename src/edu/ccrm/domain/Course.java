package edu.ccrm.domain;

import java.util.Objects;

public class Course {
    private final String code; // Immutable
    private String title;
    private int credits;
    private String instructor;
    private Semester semester;
    private String department;
    private boolean active;
    
    // Builder pattern for Course
    public static class Builder {
        private String code;
        private String title;
        private int credits = 3;
        private String instructor = "TBA";
        private Semester semester = Semester.FALL;
        private String department = "General";
        
        public Builder code(String code) {
            this.code = code;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder credits(int credits) {
            this.credits = credits;
            return this;
        }
        
        public Builder instructor(String instructor) {
            this.instructor = instructor;
            return this;
        }
        
        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }
        
        public Builder department(String department) {
            this.department = department;
            return this;
        }
        
        public Course build() {
            return new Course(this);
        }
    }
    
    private Course(Builder builder) {
        this.code = Objects.requireNonNull(builder.code, "Course code cannot be null");
        this.title = Objects.requireNonNull(builder.title, "Course title cannot be null");
        this.credits = builder.credits;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.department = builder.department;
        this.active = true;
        
        // Assertion for credit bounds
        assert credits > 0 && credits <= 6 : "Credits must be between 1 and 6";
    }
    
    // Getters and setters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return code.equals(course.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("Course{code='%s', title='%s', credits=%d, instructor='%s'}", 
                           code, title, credits, instructor);
    }
}