package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CourseService implements Searchable<Course> {
    private List<Course> courses;
    
    public CourseService() {
        this.courses = new ArrayList<>();
    }
    
    public void addCourse(Course course) {
        courses.add(course);
    }
    
    public boolean updateCourse(String code, String title, int credits, String instructor) {
        Optional<Course> courseOpt = courses.stream()
            .filter(c -> c.getCode().equals(code))
            .findFirst();
            
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            course.setTitle(title);
            course.setCredits(credits);
            course.setInstructor(instructor);
            return true;
        }
        return false;
    }
    
    public boolean deactivateCourse(String code) {
        return courses.stream()
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .map(c -> {
                c.setActive(false);
                return true;
            })
            .orElse(false);
    }
    
    // Implementation of Searchable interface
    @Override
    public List<Course> search(Predicate<Course> predicate) {
        return courses.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public Course findById(String id) {
        return findByCode(id);
    }
    
    public Course findByCode(String code) {
        return courses.stream()
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
    
    public List<Course> getCoursesByInstructor(String instructor) {
        return courses.stream()
            .filter(c -> c.getInstructor().equalsIgnoreCase(instructor))
            .collect(Collectors.toList());
    }
    
    public List<Course> getCoursesByDepartment(String department) {
        return courses.stream()
            .filter(c -> c.getDepartment().equalsIgnoreCase(department))
            .collect(Collectors.toList());
    }
    
    public List<Course> getCoursesBySemester(Semester semester) {
        return courses.stream()
            .filter(c -> c.getSemester() == semester)
            .collect(Collectors.toList());
    }
    
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    
    public List<Course> getActiveCourses() {
        return courses.stream()
            .filter(Course::isActive)
            .collect(Collectors.toList());
    }
}