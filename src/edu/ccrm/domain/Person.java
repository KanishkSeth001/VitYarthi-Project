package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Person {
    protected final String id; // Immutable
    protected String fullName;
    protected String email;
    protected LocalDate createdDate;
    protected boolean active;
    
    public Person(String id, String fullName, String email) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.createdDate = LocalDate.now();
        this.active = true;
    }
    
    // Abstract method demonstrating abstraction
    public abstract String getProfileInfo();
    
    // Getters and setters demonstrating encapsulation
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getCreatedDate() { return createdDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Person{id='%s', name='%s', email='%s', active=%s}", 
                           id, fullName, email, active);
    }
}